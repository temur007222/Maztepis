package com.uz.maztepis

import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment

class MainActivity : AppCompatActivity() {

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			window.statusBarColor = Color.TRANSPARENT
			window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
		}

		val navHostFragment =
			supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as? NavHostFragment
		if (navHostFragment != null) {
		} else {
			throw IllegalStateException("NavHostFragment not found. Check your activity_main.xml.")
		}
	}

	override fun onBackPressed() {
		val navController = findNavController(R.id.nav_host_fragment)
		if (navController.currentDestination?.id == R.id.homeFragment) {
			finish()
		} else {
			super.onBackPressed()
		}
	}
}