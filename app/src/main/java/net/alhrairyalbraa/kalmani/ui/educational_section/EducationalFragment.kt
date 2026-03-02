package net.alhrairyalbraa.kalmani.ui.educational_section

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import net.alhrairyalbraa.kalmani.R
import net.alhrairyalbraa.kalmani.databinding.FragmentEducationalBinding

class EducationalFragment : Fragment() {

    private lateinit var binding: FragmentEducationalBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentEducationalBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
    }

    private fun initViews() {
        binding.lettersBtn.setOnClickListener { startFragment("Letters") }
        binding.familyBtn.setOnClickListener { startFragment("Family") }
        binding.weekdaysBtn.setOnClickListener { startFragment("WeekDays") }
        binding.pronounsBtn.setOnClickListener { startFragment("Pronouns") }
        binding.numbersBtn.setOnClickListener { startFragment("Numbers") }
    }

    private fun startFragment(category: String) {
        val args = Bundle()
        args.putString("Category", category)
        findNavController().navigate(R.id.action_navigation_educate_to_navigation_category, args)
    }

}