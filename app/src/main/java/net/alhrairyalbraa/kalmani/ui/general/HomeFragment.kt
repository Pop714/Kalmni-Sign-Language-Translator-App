package net.alhrairyalbraa.kalmani.ui.general

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import net.alhrairyalbraa.kalmani.R
import net.alhrairyalbraa.kalmani.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private enum class ActivityType { BOTH }
    private var isUnityLoaded = false
    private var mActivityType = ActivityType.BOTH
    private var isGameActivity = false
    private lateinit var mShowUnityButton: AppCompatButton
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        mShowUnityButton = binding.toSignBtn
        isUnityLoaded = false
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                isUnityLoaded = false
                enableShowUnityButtons()
            }
        }

        mShowUnityButton.setOnClickListener {
            onClickShowUnity(binding.toSignBtn)
        }
        binding.toTextBtn.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_home_to_cameraFragment2)
        }
        binding.infoBtn.setOnClickListener {
            binding.infoLayout.visibility = View.VISIBLE
            binding.infoLayout.alpha = 0f
            binding.infoLayout.animate()
                .alpha(1f)
                .start()
        }
        binding.root.setOnClickListener{
            if (binding.infoLayout.visibility == View.VISIBLE) {
                binding.infoLayout.animate()
                    .alpha(0f)
                    .withEndAction { binding.infoLayout.visibility = View.GONE }
                    .start()
            }
        }
    }

    private fun onClickShowUnity(v: View) {
        isUnityLoaded = true
        isGameActivity = v.id != R.id.to_sign_btn
        disableShowUnityButtons()
        val intent = Intent(requireActivity(), getMainUnityActivityClass())
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
        activityResultLauncher.launch(intent)
    }

    private fun disableShowUnityButtons() {
        if (mActivityType != ActivityType.BOTH) return
        mShowUnityButton.isEnabled = !isGameActivity
    }

    private fun enableShowUnityButtons() {
        if (mActivityType != ActivityType.BOTH) return
        mShowUnityButton.isEnabled = true
    }

    private fun findClassUsingReflection(): Class<*>? {
        try {
            return Class.forName("net.alhrairyalbraa.kalmani.utils.unity.MainUnityActivity")
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
        }
        return null
    }

    private fun getMainUnityActivityClass(): Class<*>? {
        return findClassUsingReflection()
    }

}