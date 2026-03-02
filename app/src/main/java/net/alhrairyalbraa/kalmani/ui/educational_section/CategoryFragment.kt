package net.alhrairyalbraa.kalmani.ui.educational_section

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import net.alhrairyalbraa.kalmani.R
import net.alhrairyalbraa.kalmani.databinding.FragmentCategoryBinding
import net.alhrairyalbraa.kalmani.ui.MainActivity
import net.alhrairyalbraa.kalmani.utils.ClickListener
import net.alhrairyalbraa.kalmani.utils.Constants
import net.alhrairyalbraa.kalmani.utils.SharedPrefs

class CategoryFragment : Fragment(), ClickListener {

    private lateinit var binding: FragmentCategoryBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentCategoryBinding.inflate(inflater, container, false)
        (activity as MainActivity).hideBottomNavigation()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
    }

    private fun initViews() {
        // Letters
        val lettersList = listOf(
            getString(R.string.alf),
            getString(R.string.baa),
            getString(R.string.taa),
            getString(R.string.thaa),
            getString(R.string.geem),
            getString(R.string.haa),
            getString(R.string.khaa),
            getString(R.string.dal),
            getString(R.string.zal),
            getString(R.string.raa),
            getString(R.string.zay),
            getString(R.string.seen),
            getString(R.string.sheen),
            getString(R.string.sad),
            getString(R.string.ddad),
            getString(R.string.ttaa),
            getString(R.string.zaa),
            getString(R.string.ein),
            getString(R.string.ghin),
            getString(R.string.faa),
            getString(R.string.quaf),
            getString(R.string.kaf),
            getString(R.string.lam),
            getString(R.string.mim),
            getString(R.string.noon),
            getString(R.string.haaa),
            getString(R.string.waw),
            getString(R.string.yaa)
        )
        // Family
        val familyList = listOf(
            getString(R.string.father),
            getString(R.string.mother),
            getString(R.string.grandpa),
            getString(R.string.grandma),
            getString(R.string.boy),
            getString(R.string.girl),
            getString(R.string.brother),
            getString(R.string.sister),
            getString(R.string.baba),
            getString(R.string.mama),
            getString(R.string.woman),
            getString(R.string.uncle),
            getString(R.string.aunt),
            getString(R.string.family_osra),
            getString(R.string.brothers),
            getString(R.string.infant)
        )
        // WeekDays
        val weekDaysList = listOf(
            getString(R.string.st),
            getString(R.string.su),
            getString(R.string.mo),
            getString(R.string.tu),
            getString(R.string.we),
            getString(R.string.th),
            getString(R.string.fr)
        )
        // Numbers
        val numbersList = listOf(
            getString(R.string.zero),
            getString(R.string.one),
            getString(R.string.two),
            getString(R.string.three),
            getString(R.string.four),
            getString(R.string.five),
            getString(R.string.six),
            getString(R.string.seven),
            getString(R.string.eight),
            getString(R.string.nine),
            getString(R.string.t10),
            getString(R.string.e11),
            getString(R.string.t12),
            getString(R.string.t13),
            getString(R.string.f14),
            getString(R.string.f15),
            getString(R.string.s16),
            getString(R.string.s17),
            getString(R.string.e18),
            getString(R.string.n19),
            getString(R.string.t20),
        )
        // Pronouns
        val pronounsList = listOf(
            getString(R.string.i),
            getString(R.string.you),
            getString(R.string.nhno),
            getString(R.string.hoala),
            getString(R.string.hatan),
            getString(R.string.hazan),
            getString(R.string.haza),
            getString(R.string.hazahi),
            getString(R.string.hwma),
            getString(R.string.he),
            getString(R.string.she)
        )

        val rv = binding.rv
        rv.setHasFixedSize(true)
        when (arguments?.getString("Category").toString()) {
            "Letters" -> rv.adapter =
                RvAdapter(lettersList, arguments?.getString("Category").toString(), this)

            "Family" -> rv.adapter =
                RvAdapter(familyList, arguments?.getString("Category").toString(), this)

            "WeekDays" -> rv.adapter =
                RvAdapter(weekDaysList, arguments?.getString("Category").toString(), this)

            "Numbers" -> rv.adapter =
                RvAdapter(numbersList, arguments?.getString("Category").toString(), this)

            "Pronouns" -> rv.adapter =
                RvAdapter(pronounsList, arguments?.getString("Category").toString(), this)
        }

        binding.backArrow.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    override fun show(sentence: String) {
        val args = Bundle()
        args.putString("Sentence", sentence)
        findNavController().navigate(R.id.action_navigation_category_to_navigation_video, args)
    }

    override fun test(sentence: String, category: String) {
        if (arguments?.getString("Category").toString() == "Pronouns") {
            Toast.makeText(requireContext(), "غير متاح الاختبار على هذا الجزء ربما يُضاف قؤيبًا", Toast.LENGTH_SHORT).show()
        } else {
            val prefs = SharedPrefs(requireContext(), Constants.TEST_PREFS)
            prefs.setTest(sentence, category)
            findNavController().navigate(R.id.action_navigation_category_to_navigation_test)
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