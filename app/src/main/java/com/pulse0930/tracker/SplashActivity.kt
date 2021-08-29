package com.pulse0930.tracker

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.pulse0930.tracker.databinding.ActivitySplashBinding
import com.pulse0930.tracker.util.getVersion

const val SPLASH_SCREEN_DURATION:Long = 3000
class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        try {
            binding.version.text = String.format(
                "%s %s",
                getString(R.string.version),
                getVersion(packageManager, applicationContext)
            )
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        Handler().postDelayed(this::run, SPLASH_SCREEN_DURATION);
    }
    private fun run() {
        val intent = Intent(this@SplashActivity, SignInActivity::class.java)
        startActivity(intent)
        finish()
    }
}