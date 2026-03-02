package net.alhrairyalbraa.kalmani.ui.educational_section

import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.MediaController
import android.widget.ProgressBar
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.gson.Gson
import net.alhrairyalbraa.kalmani.R
import net.alhrairyalbraa.kalmani.databinding.FragmentTestBinding
import net.alhrairyalbraa.kalmani.ui.MainActivity
import net.alhrairyalbraa.kalmani.utils.Constants
import net.alhrairyalbraa.kalmani.utils.Mapping
import net.alhrairyalbraa.kalmani.utils.ModelResponse
import net.alhrairyalbraa.kalmani.utils.SharedPrefs
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.Response
import java.io.File
import java.io.IOException
import java.net.URLConnection
import java.util.concurrent.TimeUnit

class TestFragment : Fragment() {

    private lateinit var binding: FragmentTestBinding
    private lateinit var apiURL: String
    private var uri: Uri? = null
    private lateinit var word: String
    private lateinit var progress: ProgressBar
    private lateinit var category: String
    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentTestBinding.inflate(inflater, container, false)
        (activity as MainActivity).hideBottomNavigation()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
    }

    private fun initViews() {
        binding.backArrow.setOnClickListener { findNavController().navigateUp() }
        progress = binding.progress
        progress.visibility = View.GONE
        val prefs = SharedPrefs(requireContext(), Constants.TEST_PREFS)
        val list = prefs.getTest()
        category = list[1]
        val urlScheme = Constants.BASE_URL
        when(category) {
            "Letters" -> {
                apiURL = "$urlScheme/upload_for_char"
                word = list[0]
            }
            "Family" -> {
                apiURL = "$urlScheme/upload_for_familia"
                word = list[0]
            }
            "WeekDays" -> {
                apiURL = "$urlScheme/upload_for_week"
                word = list[0]
            }
            "Numbers" -> {
                apiURL = "$urlScheme/upload_for_num"
                word = list[0]
            }
        }
        binding.cameraBtn.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_test_to_cameraFragment)
        }
        binding.sendBtn.setOnClickListener {
            if (uri == null)
                Toast.makeText(requireContext(), getString(R.string.no_video), Toast.LENGTH_SHORT).show()
            else
                sendRecordedVideoToApi()
        }
        val showVideoVar = arguments?.getBoolean("SHOW_VIDEO", false)
        if (showVideoVar == true)
            showVideo()
    }

    private fun showVideo() {
        binding.characterImg.visibility = View.INVISIBLE
        binding.recordedVideo.visibility = View.VISIBLE

        val mediaController = MediaController(requireContext())
        mediaController.setAnchorView(binding.recordedVideo)
        binding.recordedVideo.setMediaController(mediaController)

        val vUri = arguments?.getString("VIDEO_URI").toString()
        vUri.let {
            uri = Uri.parse(it)
            binding.recordedVideo.setVideoURI(uri)
            binding.recordedVideo.start()
        }
    }

    private fun sendRecordedVideoToApi() {
        val videoUri = uri
        binding.recordedVideo.visibility = View.GONE
        binding.sendBtn.visibility = View.GONE
        binding.cameraBtn.visibility = View.GONE
        progress.visibility = View.VISIBLE
        progress.invalidate()
        sendVideoToApi(videoUri!!)
    }

    private fun sendVideoToApi(videoUri: Uri) {
        val file = File(getRealPathFromUri(videoUri))
        buildMultiPartRequest(file)
    }

    private fun buildMultiPartRequest(file: File) {
        val mimeType = URLConnection.guessContentTypeFromName(file.name)
        val videoRequestBody = file.asRequestBody(mimeType.toMediaTypeOrNull())

        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("video", file.name, videoRequestBody)
            .build()

        val request = Request.Builder()
            .url(apiURL)
            .post(requestBody)
            .build()

        okHttpClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                requireActivity().runOnUiThread {
                    progress.visibility = View.GONE
                    binding.recordedVideo.visibility = View.VISIBLE
                    binding.sendBtn.visibility = View.VISIBLE
                    binding.cameraBtn.visibility = View.VISIBLE
                    Toast.makeText(requireContext(), e.message.toString(), Toast.LENGTH_LONG).show()
                }
            }

            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {
                response.body?.string()?.let {
                    val result = Gson().fromJson(it, ModelResponse::class.java)
                    requireActivity().runOnUiThread {
                        progress.visibility = View.GONE
                        when(category) {
                            "Letters" -> {
                                check(Mapping().lettersMap[result.word].toString(), word, result.accuracy.toString())
                            }
                            "Family" -> {
                                check(Mapping().familyMap[result.word].toString(), word, result.accuracy.toString())
                            }
                            "WeekDays" -> {
                                check(Mapping().weekMap[result.word].toString(), word, result.accuracy.toString())
                            }
                            "Numbers" -> {
                                check(Mapping().numbersMap[result.word].toString(), word, result.accuracy.toString())
                            }
                        }
                    }
                }
            }
        })
    }

    private fun getRealPathFromUri(uri: Uri): String {
        val cursor = requireContext().contentResolver.query(uri, null, null, null, null)
        cursor?.moveToFirst()
        val columnIndex = cursor?.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
        val filePath = cursor?.getString(columnIndex!!)
        cursor?.close()
        return filePath ?: ""
    }

    private fun check(response: String, word: String, acc: String) {
        if (response == word)
            showMark("done", acc)
        else
            showMark("try_again", acc, response)
        Handler(Looper.getMainLooper()).postDelayed({
            binding.markAnim.visibility = View.INVISIBLE
            binding.sendBtn.visibility = View.VISIBLE
            binding.cameraBtn.visibility = View.VISIBLE
            binding.recordedVideo.visibility = View.VISIBLE
        }, 5000)
    }

    private fun showMark(mark: String, acc: String, response: String = "") {
        binding.markAnim.visibility = View.VISIBLE
        binding.sendBtn.visibility = View.INVISIBLE
        binding.cameraBtn.visibility = View.INVISIBLE
        binding.recordedVideo.visibility = View.INVISIBLE
        when (mark) {
            "done" -> {
                binding.markAnim.setAnimation(R.raw.done)
                Toast.makeText(requireContext(), "${getString(R.string.right_guess)} $acc", Toast.LENGTH_SHORT).show()
            }
            "try_again" -> {
                binding.markAnim.setAnimation(R.raw.try_again)
                if (response == "null" || response == "None")
                    Toast.makeText(requireContext(), getString(R.string.try_again), Toast.LENGTH_SHORT).show()
                else
                    Toast.makeText(requireContext(), "${getString(R.string.wrong_guess)} $response", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        (activity as MainActivity).hideBottomNavigation()
    }

    override fun onDestroy() {
        super.onDestroy()
        (activity as MainActivity).showBottomNavigation()
    }

}