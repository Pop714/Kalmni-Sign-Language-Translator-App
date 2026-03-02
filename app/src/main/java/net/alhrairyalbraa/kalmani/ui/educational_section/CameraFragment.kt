package net.alhrairyalbraa.kalmani.ui.educational_section

import android.Manifest
import android.content.ContentValues
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.MediaStoreOutputOptions
import androidx.camera.video.Quality
import androidx.camera.video.QualitySelector
import androidx.camera.video.Recorder
import androidx.camera.video.Recording
import androidx.camera.video.VideoCapture
import androidx.camera.video.VideoRecordEvent
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import net.alhrairyalbraa.kalmani.R
import net.alhrairyalbraa.kalmani.databinding.FragCameraBinding
import net.alhrairyalbraa.kalmani.ui.MainActivity
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.ExecutionException
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CameraFragment : Fragment() {

    private lateinit var binding: FragCameraBinding
    private val handler = Handler(Looper.getMainLooper())
    private val stopRecordingDelayMillis = 3000L
    private var manualStop = false
    private lateinit var capture: ImageButton
    private lateinit var toggleFlash: ImageButton
    private lateinit var flipCamera: ImageButton
    private lateinit var service: ExecutorService
    private var recording: Recording? = null
    private var videoCapture: VideoCapture<Recorder>? = null
    private lateinit var previewView: PreviewView
    private var cameraFacing = CameraSelector.LENS_FACING_BACK
    private val activityResultLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { _: Boolean? ->
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)
            startCamera(cameraFacing)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragCameraBinding.inflate(inflater, container, false)
        (activity as MainActivity).hideBottomNavigation()
        (activity as MainActivity).convertScreenToLandscape()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (savedInstanceState == null) {
            initViews()
        }
    }

    private fun initViews() {
        capture = binding.capture
        toggleFlash = binding.toggleFlash
        flipCamera = binding.flipCamera
        previewView = binding.preview

        capture.setOnClickListener {
            when {
                ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED -> {
                    activityResultLauncher.launch(Manifest.permission.CAMERA)
                }
                ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED -> {
                    activityResultLauncher.launch(Manifest.permission.RECORD_AUDIO)
                }
                Build.VERSION.SDK_INT <= Build.VERSION_CODES.P && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED -> {
                    activityResultLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
                else -> {
                    binding.capture.setImageResource(R.drawable.ic_stop)
                    captureVideo()
                }
            }

        }

        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            activityResultLauncher.launch(Manifest.permission.CAMERA)
        } else {
            startCamera(cameraFacing)
        }

        binding.flipCamera.setOnClickListener {
            cameraFacing = if (cameraFacing == CameraSelector.LENS_FACING_BACK) {
                CameraSelector.LENS_FACING_FRONT
            } else {
                CameraSelector.LENS_FACING_BACK
            }
            startCamera(cameraFacing)
        }

        service = Executors.newSingleThreadExecutor()
    }

    private fun captureVideo() {
        val recording1 = recording
        if (recording1 != null) {
            manualStop = true
            recording1.stop()
            binding.capture.setImageResource(R.drawable.ic_record)
            recording = null
            return
        }

        val name: String = SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS", Locale.getDefault()).format(System.currentTimeMillis())
        val contentValues = ContentValues()
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, name)
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "video/mp4")
        contentValues.put(MediaStore.Video.Media.RELATIVE_PATH, "Movies/CameraX-Video")

        val options = MediaStoreOutputOptions.Builder(requireContext().contentResolver, MediaStore.Video.Media.EXTERNAL_CONTENT_URI).setContentValues(contentValues).build()

        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            return
        }

        manualStop = false

        recording =
            videoCapture?.output?.prepareRecording(requireContext(), options)?.withAudioEnabled()
                ?.start(
                    ContextCompat.getMainExecutor(requireContext())
                ) { videoRecordEvent: VideoRecordEvent? ->
                    if (videoRecordEvent is VideoRecordEvent.Start) {
                        capture.isEnabled = true

                        // Schedule a task to stop recording after 5 seconds
                        handler.postDelayed({
                            if (!manualStop && recording != null) {
                                recording?.stop()
                                binding.capture.setImageResource(R.drawable.ic_record)
                                recording = null
                            }
                        }, stopRecordingDelayMillis)
                    } else if (videoRecordEvent is VideoRecordEvent.Finalize) {
                        if (!videoRecordEvent.hasError()) {
                            val recordedVideoUri = videoRecordEvent.outputResults.outputUri
                            val args = Bundle()
                            args.putString("VIDEO_URI", recordedVideoUri.toString())
                            args.putBoolean("SHOW_VIDEO", true)
                            findNavController().navigate(R.id.action_cameraFragment_to_navigation_test, args)
                        } else {
                            recording?.close()
                            recording = null
                            val msg = "Error: " + videoRecordEvent.error
                            Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
    }

    private fun startCamera(cameraFacing: Int) {
        val processCameraProvider = ProcessCameraProvider.getInstance(requireContext())
        processCameraProvider.addListener({
            try {
                val cameraProvider = processCameraProvider.get()
                val preview =
                    Preview.Builder().build()
                preview.setSurfaceProvider(previewView.surfaceProvider)
                val recorder = Recorder.Builder()
                    .setQualitySelector(QualitySelector.from(Quality.LOWEST))
                    .build()
                videoCapture = VideoCapture.withOutput(recorder)
                cameraProvider.unbindAll()
                val cameraSelector = CameraSelector.Builder()
                    .requireLensFacing(cameraFacing).build()
                val camera: Camera = cameraProvider.bindToLifecycle(this, cameraSelector, preview, videoCapture)
                toggleFlash.setOnClickListener {
                    toggleFlash(
                        camera
                    )
                }
            } catch (e: ExecutionException) {
                e.printStackTrace()
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun toggleFlash(camera: Camera) {
        if (camera.cameraInfo.hasFlashUnit()) {
            if (camera.cameraInfo.torchState.value == 0) {
                toggleFlash.setImageResource(R.drawable.ic_flash_on)
                camera.cameraControl.enableTorch(true)
            } else {
                toggleFlash.setImageResource(R.drawable.ic_flash_off)
                camera.cameraControl.enableTorch(false)
            }
        } else {
            requireActivity().runOnUiThread {
                Toast.makeText(
                    requireContext(),
                    "Flash is not available currently",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        (activity as MainActivity).hideBottomNavigation()
        (activity as MainActivity).convertScreenToLandscape()
    }

    override fun onPause() {
        super.onPause()
        (activity as MainActivity).convertScreenToPortrait()
    }

    override fun onDestroy() {
        super.onDestroy()
        service.shutdown()
        (activity as MainActivity).showBottomNavigation()
        (activity as MainActivity).convertScreenToPortrait()
    }

}