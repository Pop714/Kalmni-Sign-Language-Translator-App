package net.alhrairyalbraa.kalmani.ui.dictionary

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import net.alhrairyalbraa.kalmani.R
import net.alhrairyalbraa.kalmani.databinding.FragmentDictionaryBinding
import net.alhrairyalbraa.kalmani.utils.ClickListener

class DictionaryFragment : Fragment(), ClickListener {

    private lateinit var binding: FragmentDictionaryBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentDictionaryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
    }

    private fun initViews() {
        val sentencesList = listOf(
            getString(R.string.your_name),
            getString(R.string.fine),
            getString(R.string.welcome),
            getString(R.string.birthdate),
            getString(R.string.thanks),
            getString(R.string.how_are_you),
            getString(R.string.address),
            getString(R.string.HBD),
            getString(R.string.ur_job),
            getString(R.string.ur_age),
            getString(R.string.sorry),
            getString(R.string.congrats),
        )
        val rv = binding.rv
        rv.setHasFixedSize(true)
        rv.adapter = RvAdapter(sentencesList, this)
    }

    override fun show(sentence: String) {
        val args = Bundle()
        args.putString("Sentence", sentence)
        findNavController().navigate(R.id.action_navigation_dictionary_to_navigation_video, args)
    }

    override fun test(sentence: String, category: String) {
        // Nothing to do
    }
}