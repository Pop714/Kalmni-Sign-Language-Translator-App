package net.alhrairyalbraa.kalmani.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import net.alhrairyalbraa.kalmani.databinding.ActivitySplashBinding
import net.alhrairyalbraa.kalmani.ui.on_boarding.BoardingActivity
import net.alhrairyalbraa.kalmani.utils.Constants
import net.alhrairyalbraa.kalmani.utils.SharedPrefs

class Splash : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        Handler(Looper.getMainLooper()).postDelayed({
            val prefs = SharedPrefs(this, Constants.BOARDING_PREFS)
            if (prefs.getBoarding()) {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            } else {
                prefs.setBoarding(true)
                startActivity(Intent(this, BoardingActivity::class.java))
                finish()
            }
        }, 2000)
    }
}