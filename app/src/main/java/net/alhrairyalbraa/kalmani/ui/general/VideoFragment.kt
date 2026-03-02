package net.alhrairyalbraa.kalmani.ui.general

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.MediaController
import androidx.navigation.fragment.findNavController
import net.alhrairyalbraa.kalmani.R
import net.alhrairyalbraa.kalmani.databinding.FragmentVideoBinding
import net.alhrairyalbraa.kalmani.ui.MainActivity

class VideoFragment : Fragment() {
    private lateinit var binding: FragmentVideoBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentVideoBinding.inflate(inflater, container, false)
        (activity as MainActivity).hideBottomNavigation()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.backArrow.setOnClickListener {
            findNavController().navigateUp()
        }
        initViews()
    }

    private fun initViews() {
        val videoMapping = mapOf(
            // Dictionary
            getString(R.string.your_name) to R.raw.your_name,
            getString(R.string.fine) to R.raw.fine,
            getString(R.string.welcome) to R.raw.welcome,
            getString(R.string.birthdate) to R.raw.birthdate,
            getString(R.string.thanks) to R.raw.thanks,
            getString(R.string.how_are_you) to R.raw.how_are,
            getString(R.string.address) to R.raw.address,
            getString(R.string.HBD) to R.raw.hbd,
            getString(R.string.ur_age) to R.raw.ur_age,
            getString(R.string.ur_job) to R.raw.ur_job,
            getString(R.string.sorry) to R.raw.sorry,
            getString(R.string.congrats) to R.raw.congruts,
            // Letters
            getString(R.string.alf) to R.raw.alef,
            getString(R.string.baa) to R.raw.baa,
            getString(R.string.taa) to R.raw.taa,
            getString(R.string.thaa) to R.raw.thaa,
            getString(R.string.geem) to R.raw.geem,
            getString(R.string.haa) to R.raw.haa,
            getString(R.string.khaa) to R.raw.khaa,
            getString(R.string.dal) to R.raw.dal,
            getString(R.string.zal) to R.raw.zal,
            getString(R.string.raa) to R.raw.raa,
            getString(R.string.zay) to R.raw.zay,
            getString(R.string.seen) to R.raw.seen,
            getString(R.string.sheen) to R.raw.sheen,
            getString(R.string.sad) to R.raw.sad,
            getString(R.string.ddad) to R.raw.ddad,
            getString(R.string.ttaa) to R.raw.ttaa,
            getString(R.string.zaa) to R.raw.zaa,
            getString(R.string.ein) to R.raw.ein,
            getString(R.string.ghin) to R.raw.ghin,
            getString(R.string.faa) to R.raw.faa,
            getString(R.string.quaf) to R.raw.quaf,
            getString(R.string.kaf) to R.raw.kaf,
            getString(R.string.lam) to R.raw.lam,
            getString(R.string.mim) to R.raw.mim,
            getString(R.string.noon) to R.raw.noon,
            getString(R.string.haaa) to R.raw.haaa,
            getString(R.string.waw) to R.raw.waw,
            getString(R.string.yaa) to R.raw.yaa,
            // Family
            getString(R.string.father) to R.raw.father,
            getString(R.string.mother) to R.raw.mother,
            getString(R.string.grandpa) to R.raw.grandpa,
            getString(R.string.grandma) to R.raw.grandma,
            getString(R.string.boy) to R.raw.boy,
            getString(R.string.girl) to R.raw.girl,
            getString(R.string.brother) to R.raw.brother,
            getString(R.string.sister) to R.raw.sister,
            getString(R.string.baba) to R.raw.baba,
            getString(R.string.mama) to R.raw.mama,
            getString(R.string.woman) to R.raw.woman,
            getString(R.string.uncle) to R.raw.uncle,
            getString(R.string.aunt) to R.raw.aunt,
            getString(R.string.family_osra) to R.raw.family,
            getString(R.string.brothers) to R.raw.brothers,
            getString(R.string.infant) to R.raw.infant,
            // Numbers
            getString(R.string.zero) to R.raw.zero,
            getString(R.string.one) to R.raw.one,
            getString(R.string.two) to R.raw.two,
            getString(R.string.three) to R.raw.three,
            getString(R.string.four) to R.raw.four,
            getString(R.string.five) to R.raw.five,
            getString(R.string.six) to R.raw.six,
            getString(R.string.seven) to R.raw.seven,
            getString(R.string.eight) to R.raw.eight,
            getString(R.string.nine) to R.raw.nine,
            getString(R.string.t10) to R.raw.ten,
            getString(R.string.e11) to R.raw.eleven,
            getString(R.string.t12) to R.raw.twelve,
            getString(R.string.t13) to R.raw.th13,
            getString(R.string.f14) to R.raw.f14,
            getString(R.string.f15) to R.raw.f15,
            getString(R.string.s16) to R.raw.s16,
            getString(R.string.s17) to R.raw.s17,
            getString(R.string.e18) to R.raw.e18,
            getString(R.string.n19) to R.raw.n19,
            getString(R.string.t20) to R.raw.t20,
            // Weekdays
            getString(R.string.st) to R.raw.st,
            getString(R.string.su) to R.raw.su,
            getString(R.string.mo) to R.raw.mo,
            getString(R.string.tu) to R.raw.tu,
            getString(R.string.we) to R.raw.we,
            getString(R.string.th) to R.raw.th,
            getString(R.string.fr) to R.raw.fr,
            // Pronouns
            getString(R.string.i) to R.raw.i,
            getString(R.string.you) to R.raw.you,
            getString(R.string.nhno) to R.raw.nhno,
            getString(R.string.hoala) to R.raw.hoala,
            getString(R.string.hatan) to R.raw.hatan,
            getString(R.string.hazan) to R.raw.hazan,
            getString(R.string.haza) to R.raw.haza,
            getString(R.string.hazahi) to R.raw.hazahi,
            getString(R.string.hwma) to R.raw.hwma,
            getString(R.string.he) to R.raw.he,
            getString(R.string.she) to R.raw.she
        )

        initPlayer(videoMapping[arguments?.getString("Sentence").toString()]!!)
    }

    private fun initPlayer(videoName: Int) {
        val videoView = binding.videoView
        val mediaController = MediaController(requireContext())

        val videoUri = Uri.parse("android.resource://net.alhrairyalbraa.kalmani/$videoName")
        videoView.setVideoURI(videoUri)

        mediaController.setAnchorView(videoView)
        videoView.setMediaController(mediaController)

        videoView.setOnPreparedListener {i->
            var speed = binding.videoSpeed.text.toString()
            i.playbackParams = i.playbackParams.setSpeed(speed.toFloat())
            binding.videoSpeed.setOnClickListener {
                when (speed) {
                    "0.5" -> speed = "1.0"
                    "1.0" -> speed = "1.5"
                    "1.5" -> speed = "2.0"
                    "2.0" -> speed = "0.5"
                }
                binding.videoSpeed.text = speed
                i.playbackParams = i.playbackParams.setSpeed(speed.toFloat())
            }
        }

        videoView.start()
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