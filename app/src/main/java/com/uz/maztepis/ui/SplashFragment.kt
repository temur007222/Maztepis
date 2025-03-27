package com.uz.maztepis.ui

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.uz.maztepis.R
import com.uz.maztepis.databinding.FragmentSplashBinding

class SplashFragment : Fragment() {

	private lateinit var binding: FragmentSplashBinding

	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		binding = FragmentSplashBinding.inflate(inflater, container, false)
		return binding.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		Handler(Looper.getMainLooper()).postDelayed({
			val navOptions = NavOptions.Builder()
				.setEnterAnim(R.anim.fade_in)
				.setExitAnim(R.anim.fade_out)
				.setPopEnterAnim(R.anim.fade_in)
				.build()

			findNavController().navigate(R.id.action_splashFragment_to_playerSelectionFragment, null, navOptions)
		}, 3000)
	}
}
