package net.alhrairyalbraa.kalmani.ui.on_boarding

import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import net.alhrairyalbraa.kalmani.R
import net.alhrairyalbraa.kalmani.databinding.ActivityBoardingBinding
import net.alhrairyalbraa.kalmani.ui.MainActivity

class BoardingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBoardingBinding
    private lateinit var mDotLayout: LinearLayout
    private lateinit var nextBtn: AppCompatButton
    private lateinit var skipBtn: AppCompatButton
    private lateinit var viewPagerAdapter: ViewPagerAdapter
    private lateinit var mSLideViewPager: ViewPager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityBoardingBinding.inflate(layoutInflater)
        nextBtn = binding.nextBtn
        skipBtn = binding.skipBtn
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initViews()
    }

    private fun initViews() {

        mSLideViewPager = binding.slideViewPager
        mDotLayout = binding.indicatorLayout
        viewPagerAdapter = ViewPagerAdapter(this)
        mSLideViewPager.adapter = viewPagerAdapter
        setUpIndicator(0)
        mSLideViewPager.addOnPageChangeListener(viewListener)

        nextBtn.setOnClickListener {
            if (getItem(0) < 2) mSLideViewPager.setCurrentItem(getItem(1), true)
            else {
                val i = Intent(this, MainActivity::class.java)
                startActivity(i)
                finish()
            }
        }

        skipBtn.setOnClickListener {
            val i = Intent(this, MainActivity::class.java)
            startActivity(i)
            finish()
        }
    }

    private fun setUpIndicator(position: Int) {
        val dots = arrayOfNulls<TextView>(3)
        mDotLayout.removeAllViews()

        for (i in dots.indices) {
            dots[i] = TextView(this)
            dots[i]?.text = Html.fromHtml("&#8226;")
            dots[i]?.textSize = 35f
            dots[i]?.setTextColor(getColor(R.color.inactive))
            mDotLayout.addView(dots[i])
        }

        dots[position]?.setTextColor(getColor(R.color.active))
    }

    private var viewListener: OnPageChangeListener = object : OnPageChangeListener {
        override fun onPageScrolled(
            position: Int,
            positionOffset: Float,
            positionOffsetPixels: Int
        ) {
            // nothing to do
        }

        override fun onPageSelected(position: Int) {
            setUpIndicator(position)
        }

        override fun onPageScrollStateChanged(state: Int) {
            // nothing to do
        }
    }

    private fun getItem(i: Int): Int {
        return mSLideViewPager.currentItem + i
    }
}