package net.alhrairyalbraa.kalmani.ui

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import net.alhrairyalbraa.kalmani.R
import net.alhrairyalbraa.kalmani.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initViews()
    }

    private fun initViews() {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment
        navController = navHostFragment.navController
        setupBottomNavBar()
    }

    private fun setupBottomNavBar() {
        binding.navHome.setOnClickListener {
            navController.navigate(R.id.navigation_home)
        }
        binding.navDictionary.setOnClickListener {
            navController.navigate(R.id.navigation_dictionary)
        }
        binding.navEducation.setOnClickListener {
            navController.navigate(R.id.navigation_educate)
        }
    }

    @SuppressLint("SourceLockedOrientationActivity")
    fun convertScreenToPortrait() {
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }

    fun convertScreenToLandscape() {
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
    }

    fun hideBottomNavigation() {
        binding.navView.visibility = View.GONE
    }

    fun showBottomNavigation() {
        binding.navView.visibility = View.VISIBLE
    }
}