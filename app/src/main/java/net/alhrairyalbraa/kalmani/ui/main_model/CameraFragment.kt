package net.alhrairyalbraa.kalmani.ui.main_model

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.AspectRatio
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import com.google.mediapipe.tasks.components.containers.NormalizedLandmark
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.google.mediapipe.tasks.vision.handlandmarker.HandLandmarkerResult
import net.alhrairyalbraa.kalmani.R
import net.alhrairyalbraa.kalmani.databinding.FragmentCameraBinding
import net.alhrairyalbraa.kalmani.ml.LettersModel
import net.alhrairyalbraa.kalmani.ml.WordModel
import net.alhrairyalbraa.kalmani.ui.MainActivity
import net.alhrairyalbraa.kalmani.utils.Mapping
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.util.Locale
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

@Suppress("DEPRECATION")
class CameraFragment : Fragment(), HandLandmarkerHelper.LandmarkerListener,
    TextToSpeech.OnInitListener {

    companion object {
        private const val TAG = "Hand Landmarker"
        private const val NONE = "nothing"
    }

    private var lock = false
    private var r = ""
    private var modelType = "Letters"
    private var cnt = 1
    private lateinit var textToSpeech: TextToSpeech
    private val wastageResList = mutableListOf<String>()
    private val resList = mutableListOf<String>()
    private val sequence = mutableListOf<List<Float>>()
    private var _fragmentCameraBinding: FragmentCameraBinding? = null
    private val fragmentCameraBinding
        get() = _fragmentCameraBinding!!
    private lateinit var handLandmarkerHelper: HandLandmarkerHelper
    private val viewModel: MainViewModel by activityViewModels()
    private var preview: Preview? = null
    private var imageAnalyzer: ImageAnalysis? = null
    private var camera: Camera? = null
    private var cameraProvider: ProcessCameraProvider? = null
    private var cameraFacing = CameraSelector.LENS_FACING_FRONT
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                Toast.makeText(context, "Permission request granted", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(context, "Permission request denied", Toast.LENGTH_LONG).show()
            }
        }

    private lateinit var backgroundExecutor: ExecutorService

    override fun onResume() {
        super.onResume()
        (activity as MainActivity).hideBottomNavigation()

        if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            )
        ) {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }

        backgroundExecutor.execute {
            if (handLandmarkerHelper.isClose()) {
                handLandmarkerHelper.setupHandLandmarker()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        if (this::handLandmarkerHelper.isInitialized) {
            viewModel.setMaxHands(handLandmarkerHelper.maxNumHands)
            viewModel.setMinHandDetectionConfidence(handLandmarkerHelper.minHandDetectionConfidence)
            viewModel.setMinHandTrackingConfidence(handLandmarkerHelper.minHandTrackingConfidence)
            viewModel.setMinHandPresenceConfidence(handLandmarkerHelper.minHandPresenceConfidence)
            viewModel.setDelegate(handLandmarkerHelper.currentDelegate)

            backgroundExecutor.execute { handLandmarkerHelper.clearHandLandmarker() }
        }
    }

    override fun onDestroyView() {
        _fragmentCameraBinding = null
        super.onDestroyView()

        (activity as MainActivity).showBottomNavigation()
        textToSpeech.stop()
        textToSpeech.shutdown()

        backgroundExecutor.shutdown()
        backgroundExecutor.awaitTermination(
            Long.MAX_VALUE, TimeUnit.NANOSECONDS
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _fragmentCameraBinding =
            FragmentCameraBinding.inflate(inflater, container, false)
        textToSpeech = TextToSpeech(context, this)
        return fragmentCameraBinding.root
    }

    @SuppressLint("MissingPermission")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fragmentCameraBinding.speakerBtn.setOnClickListener {
            textToSpeech.speak(
                fragmentCameraBinding.result.text,
                TextToSpeech.QUEUE_FLUSH,
                null,
                null
            )
        }

        fragmentCameraBinding.modelType.setOnClickListener {
            modelSwitching()
        }

        val switchCameraButton: ImageView? = _fragmentCameraBinding?.imgSwitchCamera
        switchCameraButton?.setOnClickListener {
            switchCamera()
        }

        backgroundExecutor = Executors.newSingleThreadExecutor()

        fragmentCameraBinding.viewFinder.post {
            setUpCamera()
        }

        backgroundExecutor.execute {
            handLandmarkerHelper = HandLandmarkerHelper(
                context = requireContext(),
                runningMode = RunningMode.LIVE_STREAM,
                minHandDetectionConfidence = viewModel.currentMinHandDetectionConfidence,
                minHandTrackingConfidence = viewModel.currentMinHandTrackingConfidence,
                minHandPresenceConfidence = viewModel.currentMinHandPresenceConfidence,
                maxNumHands = viewModel.currentMaxHands,
                currentDelegate = viewModel.currentDelegate,
                handLandmarkerHelperListener = this
            )
        }
    }

    private fun modelSwitching() {
        r += " "
        when (fragmentCameraBinding.modelType.text.toString()) {
            getString(R.string.letters1) -> {
                fragmentCameraBinding.modelType.text = getString(R.string.words)
                modelType = "Words"
            }

            getString(R.string.words) -> {
                fragmentCameraBinding.modelType.text = getString(R.string.letters1)
                modelType = "Letters"
            }
        }
    }

    private fun switchCamera() {
        cameraFacing = if (cameraFacing == CameraSelector.LENS_FACING_FRONT) {
            CameraSelector.LENS_FACING_BACK
        } else {
            CameraSelector.LENS_FACING_FRONT
        }
        fragmentCameraBinding.overlay.updateCameraFrontFacing(cameraFacing == CameraSelector.LENS_FACING_FRONT)
        bindCameraUseCases()
    }

    private fun setUpCamera() {
        val cameraProviderFuture =
            ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener(
            {
                cameraProvider = cameraProviderFuture.get()
                bindCameraUseCases()
            }, ContextCompat.getMainExecutor(requireContext())
        )
    }

    @SuppressLint("UnsafeOptInUsageError")
    private fun bindCameraUseCases() {
        val cameraProvider = cameraProvider
            ?: throw IllegalStateException("Camera initialization failed.")

        val cameraSelector =
            CameraSelector.Builder().requireLensFacing(cameraFacing).build()

        preview = Preview.Builder().setTargetAspectRatio(AspectRatio.RATIO_4_3)
            .setTargetRotation(fragmentCameraBinding.viewFinder.display.rotation)
            .build()

        imageAnalyzer =
            ImageAnalysis.Builder().setTargetAspectRatio(AspectRatio.RATIO_4_3)
                .setTargetRotation(fragmentCameraBinding.viewFinder.display.rotation)
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
                .build()
                .also {
                    it.setAnalyzer(backgroundExecutor) { image ->
                        detectHand(image)
                    }
                }

        cameraProvider.unbindAll()

        try {
            camera = cameraProvider.bindToLifecycle(
                this, cameraSelector, preview, imageAnalyzer
            )
            preview?.setSurfaceProvider(fragmentCameraBinding.viewFinder.surfaceProvider)
        } catch (exc: Exception) {
            Log.e(TAG, "Use case binding failed", exc)
        }
    }

    private fun detectHand(imageProxy: ImageProxy) {
        handLandmarkerHelper.detectLiveStream(
            imageProxy = imageProxy,
            isFrontCamera = cameraFacing == CameraSelector.LENS_FACING_FRONT
        )
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        imageAnalyzer?.targetRotation =
            fragmentCameraBinding.viewFinder.display.rotation
    }

    @SuppressLint("DefaultLocale")
    override fun onResults(
        resultBundle: HandLandmarkerHelper.ResultBundle
    ) {
        activity?.runOnUiThread {
            if (_fragmentCameraBinding != null) {
                invokeOverlay(resultBundle)

                // the result ---->>>> resultBundle.results.first()
                val extractedFeatures: MutableList<Float>
                when (modelType) {
                    "Letters" -> {
                        extractedFeatures = extractionLetter(resultBundle.results.first())
                        invokeLettersModel(extractedFeatures)
                    }
                    "Words" -> {
                        extractedFeatures = extractionWord(resultBundle.results.first())
                        invokeWordModel(extractedFeatures)
                    }
                }

            }
        }
    }

    @SuppressLint("DefaultLocale")
    private fun invokeWordModel(extractedFeatures: List<Float>) {
        val notAllZero = extractedFeatures.any { it != 0.0f }
        if (!notAllZero) {
            sequence.clear()
        } else {
            sequence.add(extractedFeatures)
            if (sequence.size == 15) {
                val output =
                    callModel(floatListToByteBufferForWords(sequence), modelType)
                sequence.clear()
                val maxIdx = output.indices.maxByOrNull { output[it] } ?: -1
                // mapping the class number to its franco word
                val francoWord = Mapping().wordsClassMap[maxIdx].toString()
                fragmentCameraBinding.resCh.text = francoWord
                val formattedOutput = String.format("%.2f", output[maxIdx])
                fragmentCameraBinding.resAcc.text = formattedOutput
                if (output[maxIdx] > 0.85) {
                    showResultedSentenceForWords(francoWord)
                }
            }
        }
    }

    @SuppressLint("DefaultLocale")
    private fun invokeLettersModel(extractedFeatures: List<Float>) {
        val notAllZero = extractedFeatures.any { it != 0.0f }
        if (notAllZero) {
            val output = callModel(
                floatListToByteBufferForLetters(extractedFeatures),
                modelType
            )
            val maxIdx = output.indices.maxByOrNull { output[it] } ?: -1
            // mapping the class number to its franco letter
            val francoLetter = Mapping().lettersMap2[maxIdx].toString()
            fragmentCameraBinding.resCh.text = francoLetter
            val formattedOutput = String.format("%.2f", output[maxIdx])
            fragmentCameraBinding.resAcc.text = formattedOutput
            if (output[maxIdx] > 0.50) {
                showResultedSentence(francoLetter)
            }
        }
    }

    private fun invokeOverlay(resultBundle: HandLandmarkerHelper.ResultBundle) {
        fragmentCameraBinding.overlay.setResults(
            resultBundle.results.first(),
            resultBundle.inputImageHeight,
            resultBundle.inputImageWidth,
            RunningMode.LIVE_STREAM
        )
        fragmentCameraBinding.overlay.invalidate()
    }

    private fun extractionLetter(results: HandLandmarkerResult): MutableList<Float> {
        val dataAux = mutableListOf<Float>()

        if (results.landmarks().isEmpty()) {
            fillWithZeros(dataAux, 84)
        } else {
            processLandmarksLetter(results.landmarks(), dataAux)
        }

        return dataAux
    }

    private fun processLandmarksLetter(
        landmarks: List<MutableList<NormalizedLandmark>>,
        dataAux: MutableList<Float>
    ) {
        for (landmark in landmarks) {
            for (normalizedLandmark in landmark) {
                val x = normalizedLandmark.x()
                val y = normalizedLandmark.y()
//                val z = normalizedLandmark.z()  // with z
                dataAux.add(x)
                dataAux.add(y)
//                dataAux.add(z)  // with z
            }
        }

        if (landmarks.size == 1) {
            fillWithZeros(dataAux, 42)
        }
    }

    override fun onError(error: String, errorCode: Int) {
        activity?.runOnUiThread {
            Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
        }
    }

    private fun extractionWord(results: HandLandmarkerResult): MutableList<Float> {
        val dataAux = mutableListOf<Float>()

        if (results.landmarks().isEmpty()) {
            fillWithZeros(dataAux, 84)
        } else {
            processLandmarksWord(results.landmarks(), dataAux)
        }

        return dataAux
    }

    private fun processLandmarksWord(
        landmarks: List<List<NormalizedLandmark>>,
        dataAux: MutableList<Float>
    ) {
        val xs = mutableListOf<Float>()
        val ys = mutableListOf<Float>()

        for (landmark in landmarks) {
            for (normalizedLandmark in landmark) {
                val x = normalizedLandmark.x()
                val y = normalizedLandmark.y()
                xs.add(x)
                ys.add(y)
            }

            for (normalizedLandmark in landmark) {
                val x = normalizedLandmark.x()
                val y = normalizedLandmark.y()
                dataAux.add(x - xs.min())
                dataAux.add(y - ys.min())
            }
        }

        if (landmarks.size == 1) {
            fillWithZeros(dataAux, 42)
        }
    }

    private fun fillWithZeros(list: MutableList<Float>, count: Int) {
        repeat(count) {
            list.add(0.0f)
        }
    }

    private fun floatListToByteBufferForLetters(floatList: List<Float>): ByteBuffer {
        val floatBuffer = FloatBuffer.allocate(floatList.size)
        floatBuffer.put(floatList.toFloatArray())
        floatBuffer.flip()

        val byteBuffer = ByteBuffer.allocateDirect(floatList.size * 4) // Each float is 4 bytes
        byteBuffer.order(ByteOrder.nativeOrder())

        while (floatBuffer.hasRemaining()) {
            byteBuffer.putFloat(floatBuffer.get())
        }
        byteBuffer.flip()

        return byteBuffer
    }

    private fun floatListToByteBufferForWords(floatList: List<List<Float>>): ByteBuffer {
        val byteBuffer = ByteBuffer.allocateDirect(15 * 84 * 4) // Each float is 4 bytes
        byteBuffer.order(ByteOrder.nativeOrder())

        for (i in 0..14) {
            for (j in 0..83) {
                byteBuffer.putFloat(floatList[i][j])
            }
        }

        return byteBuffer
    }

    private fun callModel(byteBuffer: ByteBuffer, modelT: String): FloatArray {
        var outputFeature0: TensorBuffer? = null
        when (modelT) {
            "Letters" -> {
                val model = LettersModel.newInstance(requireContext())
                val inputFeature0 =
                    TensorBuffer.createFixedSize(intArrayOf(1, 84), DataType.FLOAT32)
                inputFeature0.loadBuffer(byteBuffer)
                val outputs = model.process(inputFeature0)
                outputFeature0 = outputs.outputFeature0AsTensorBuffer
                model.close()
            }

            "Words" -> {
                val model = WordModel.newInstance(requireContext())
                val inputFeature0 =
                    TensorBuffer.createFixedSize(intArrayOf(1, 15, 84), DataType.FLOAT32)
                inputFeature0.loadBuffer(byteBuffer)
                val outputs = model.process(inputFeature0)
                outputFeature0 = outputs.outputFeature0AsTensorBuffer
                model.close()
            }
        }

        return outputFeature0!!.floatArray
    }

    private fun showResultedSentence(res: String) {
        cnt++
        if (res != NONE) {
            wastageResList.add(res)
        }

        if (cnt == 20) {  // could be edited
            cnt = 1
            if (wastageResList.isNotEmpty()) {
                val result = mostFrequentWord(wastageResList)
                checkResult(result)
            }
        }
    }

    private fun checkResult(result: Pair<String?, Int>) {
        if (result.first == "Delete" && result.second > 3 && !lock) {
            if (resList.isNotEmpty()) {
                resList.removeLast()
                r = r.dropLast(1)
                fragmentCameraBinding.result.text = r
                lock = true
            }
        } else {
            if (resList.isNotEmpty() && result.first == resList[resList.size - 1] && result.first != "Delete") {
                wastageResList.clear()
                lock = false
            }

            if (result.first != "Delete" && result.second > 3 && (resList.isEmpty() || result.first != resList[resList.size - 1])) {   // could be edited
                lock = false
                showResult(result.first)
            }
        }
    }

    private fun showResultedSentenceForWords(res: String) {
        cnt = 1
        wastageResList.clear()
        if (resList.isNotEmpty() && res != resList[resList.size - 1]) {
            showResult(res)
        } else if (resList.isEmpty()) {
            showResult(res)
        }
    }

    private fun showResult(result: String?) {
        // map the franco letter to its arabic letter
        var mappedResult = ""
        when (modelType) {
            "Letters" -> mappedResult = Mapping().lettersMap3[result].toString()
            "Words" -> mappedResult = result.toString() // will be edited to map to arabic word
        }

        wastageResList.clear()

        if (mappedResult.isNotEmpty()) {
            resList.add(result!!)
            r = if (modelType == "Letters") "$r$mappedResult" else "$r $mappedResult"
            fragmentCameraBinding.result.text = r
        }
    }

    private fun mostFrequentWord(words: List<String>): Pair<String?, Int> {
        val wordCount = mutableMapOf<String, Int>()

        for (word in words) {
            val count = wordCount[word] ?: 0
            wordCount[word] = count + 1
        }

        var maxFrequency = 0
        var mostFrequentWord: String? = null
        for ((word, frequency) in wordCount) {
            if (frequency > maxFrequency) {
                maxFrequency = frequency
                mostFrequentWord = word
            }
        }

        return Pair(mostFrequentWord, maxFrequency)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = textToSpeech.setLanguage(Locale("ar"))
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Toast.makeText(context, "Missing Your Language", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(context, "Connection Failed", Toast.LENGTH_SHORT).show()
        }
    }

}