package com.uz.maztepis.ui

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.uz.maztepis.R
import com.uz.maztepis.databinding.FragmentHomeFragmentBinding
import com.uz.maztepis.logic.GameMode
import com.uz.maztepis.viewmodel.HomeViewModel
import com.uz.maztepis.logic.AIAlgorithm

class HomeFragment : Fragment() {

	private lateinit var binding: FragmentHomeFragmentBinding
	private lateinit var viewModel: HomeViewModel
	private lateinit var mediaPlayer: MediaPlayer
	private lateinit var audioManager: AudioManager
	private var isMusicInitialized = false

	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		binding = FragmentHomeFragmentBinding.inflate(inflater, container, false)
		return binding.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		viewModel = ViewModelProvider(this)[HomeViewModel::class.java]
		audioManager = requireContext().getSystemService(Context.AUDIO_SERVICE) as AudioManager
		initializeMediaPlayer()
		observeViewModel()
		setupListeners()
		setupStartingPlayerSelector()
	}

	private fun initializeMediaPlayer() {
		val sharedPrefs = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
		val isMusicEnabled = sharedPrefs.getBoolean("pref_music_enabled", true)
		if (isMusicEnabled) {
			val result = audioManager.requestAudioFocus(null, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN)
			if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
				mediaPlayer = MediaPlayer.create(requireContext(), R.raw.home_and_result_page_song).apply {
					setAudioStreamType(AudioManager.STREAM_MUSIC)
					isLooping = true
					start()
				}
				isMusicInitialized = true
			}
		}
	}

	private fun observeViewModel() {
		viewModel.isMusicEnabled.observe(viewLifecycleOwner) { isEnabled ->
			if (::mediaPlayer.isInitialized) {
				if (isEnabled) {
					if (!mediaPlayer.isPlaying) mediaPlayer.start()
				} else {
					if (mediaPlayer.isPlaying) mediaPlayer.pause()
				}
			}
		}
		viewModel.navigateToGame.observe(viewLifecycleOwner) {
			if (it) {
				navigateToGameScreen()
				viewModel.completeNavigation()
			}
		}
	}

	private fun navigateToGameScreen() {
		val args = Bundle().apply {
			putString("gameMode", viewModel.gameMode.value?.name)
			putString("aiAlgorithm", viewModel.aiAlgorithm.value?.name)
			putString("startingPlayer", viewModel.startingPlayer.value?.name)
		}

		findNavController().navigate(
			R.id.gameFragment,
			args,
			NavOptions.Builder()
				.setEnterAnim(R.anim.fade_in)
				.setExitAnim(R.anim.fade_out)
				.setPopEnterAnim(R.anim.fade_in)
				.setPopExitAnim(R.anim.fade_out)
				.build()
		)
	}

	private fun setupListeners() {
		binding.settingsIc.setOnClickListener {
			showSettingsDialog()
		}
		binding.gameRuleIc.setOnClickListener {
			showGameRulesDialog()
		}
		binding.computerButton.setOnClickListener {
			val selectedPlayerId = binding.startingPlayerRadioGroup.checkedRadioButtonId
			val startingPlayer = when (selectedPlayerId) {
				R.id.radio_human -> HomeViewModel.PlayerType.HUMAN
				R.id.radio_computer -> HomeViewModel.PlayerType.COMPUTER
				else -> HomeViewModel.PlayerType.HUMAN
			}
			viewModel.setStartingPlayer(startingPlayer)

			val gameMode = when (binding.gameModeRadioGroup.checkedRadioButtonId) {
				R.id.humanVsHuman -> HomeViewModel.GameMode.HUMAN_VS_HUMAN
				R.id.humanVsComputer -> HomeViewModel.GameMode.HUMAN_VS_COMPUTER
				else -> HomeViewModel.GameMode.HUMAN_VS_COMPUTER
			}
			viewModel.setGameMode(gameMode)

			if (gameMode == HomeViewModel.GameMode.HUMAN_VS_COMPUTER) {
				val aiAlgorithm = when (binding.aiAlgorithmRadioGroup.checkedRadioButtonId) {
					R.id.minimaxAlgorithm -> HomeViewModel.AIAlgorithm.MINIMAX
					R.id.alphaBetaAlgorithm -> HomeViewModel.AIAlgorithm.ALPHA_BETA
					else -> HomeViewModel.AIAlgorithm.MINIMAX
				}
				viewModel.setAIAlgorithm(aiAlgorithm)
			}

			viewModel.startGame()
		}

		binding.gameModeRadioGroup.setOnCheckedChangeListener { _, _ ->
			val isVsComputer = binding.gameModeRadioGroup.checkedRadioButtonId == R.id.humanVsComputer
			binding.aiAlgorithmRadioGroup.visibility = if (isVsComputer) View.VISIBLE else View.GONE
			binding.startingPlayerRadioGroup.visibility = if (isVsComputer) View.VISIBLE else View.GONE
		}
	}

	private fun setupStartingPlayerSelector() {
		binding.startingPlayerRadioGroup.setOnCheckedChangeListener { _, checkedId ->
			val playerType = when (checkedId) {
				R.id.radio_human -> HomeViewModel.PlayerType.HUMAN
				R.id.radio_computer -> HomeViewModel.PlayerType.COMPUTER
				else -> HomeViewModel.PlayerType.HUMAN
			}
			viewModel.setStartingPlayer(playerType)
		}
	}

	private fun showSettingsDialog() {
		val sharedPrefs = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
		val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.settings_dialog, null)
		val dialog = AlertDialog.Builder(requireContext())
			.setView(dialogView)
			.create()
		dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
		dialogView.findViewById<SwitchCompat>(R.id.switchMusic).apply {
			isChecked = sharedPrefs.getBoolean("pref_music_enabled", true)
			setOnCheckedChangeListener { _, isChecked ->
				sharedPrefs.edit().putBoolean("pref_music_enabled", isChecked).apply()
				viewModel.setMusicEnabled(isChecked)
			}
		}
		dialogView.findViewById<Button>(R.id.closeSettingsButton).setOnClickListener {
			dialog.dismiss()
		}
		dialog.show()
	}

	private fun showGameRulesDialog() {
		val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.game_rules_dialog, null)
		val dialog = AlertDialog.Builder(requireContext())
			.setView(dialogView)
			.create()
		dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
		dialogView.findViewById<Button>(R.id.closeButton).setOnClickListener {
			dialog.dismiss()
		}
		dialog.show()
	}

	override fun onDestroyView() {
		super.onDestroyView()
		if (::mediaPlayer.isInitialized) {
			mediaPlayer.release()
		}
	}
}

