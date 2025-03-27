package com.uz.maztepis.ui

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.NumberPicker
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.uz.maztepis.R
import com.uz.maztepis.databinding.FragmentGameBinding
import com.uz.maztepis.logic.AIAlgorithm
import com.uz.maztepis.logic.GameMode
import com.uz.maztepis.logic.GameState
import com.uz.maztepis.logic.PlayerType
import com.uz.maztepis.viewmodel.GameViewModel

class GameFragment : Fragment() {

	private lateinit var binding: FragmentGameBinding
	private lateinit var viewModel: GameViewModel
	private var mediaPlayer: MediaPlayer? = null
	private lateinit var audioManager: AudioManager

	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		binding = FragmentGameBinding.inflate(inflater, container, false)
		return binding.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		initAudio()
		setupViewModel()
		setupUI()
		observeViewModel()
	}

	private fun initAudio() {
		audioManager = requireContext().getSystemService(Context.AUDIO_SERVICE) as AudioManager
		val sharedPrefs = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
		if (sharedPrefs.getBoolean("pref_music_enabled", true)) {
			mediaPlayer = MediaPlayer.create(requireContext(), R.raw.game_started_song).apply {
				isLooping = true
				start()
			}
		}
	}

	private fun setupViewModel() {
		val args = arguments
		val gameMode = GameMode.valueOf(args?.getString("gameMode") ?: "HUMAN_VS_COMPUTER")
		val aiAlgorithm = AIAlgorithm.valueOf(args?.getString("aiAlgorithm") ?: "MINIMAX")
		val startingPlayer = PlayerType.valueOf(args?.getString("startingPlayer") ?: "HUMAN")

		viewModel = ViewModelProvider(this)[GameViewModel::class.java]

		// Delay setting up the game until number is picked
		showNumberSelectionDialog(gameMode, aiAlgorithm, startingPlayer)
	}

	private fun setupUI() {
		binding.multiplyBy3.setOnClickListener { onMultiplyClick(3) }
		binding.multiplyBy4.setOnClickListener { onMultiplyClick(4) }
		binding.multiplyBy5.setOnClickListener { onMultiplyClick(5) }
	}

	private fun observeViewModel() {
		viewModel.gameState.observe(viewLifecycleOwner) { gameState ->
			updateUI(gameState)

			val isVsComputer = viewModel.getGameMode() == GameMode.HUMAN_VS_COMPUTER

			if (isVsComputer && gameState.currentPlayer == PlayerType.COMPUTER && !gameState.isGameOver) {
				Log.d("GameFragment", "🤖 Triggering computer AI move")

				Handler(Looper.getMainLooper()).postDelayed({
					// Extra check to prevent misfiring
					if (viewModel.gameState.value?.currentPlayer == PlayerType.COMPUTER) {
						viewModel.makeMove(viewModel.getGameLogic().getAIMove())
					} else {
						Log.d("GameFragment", "❌ Skipping AI move, not computer's turn anymore")
					}
				}, 1500)
			}
		}

		viewModel.winner.observe(viewLifecycleOwner) { winner ->
			winner?.let {
				val action = GameFragmentDirections.actionGameFragmentToResultFragment(
					gameSteps = viewModel.getGameSteps().toTypedArray(),
					winner = it.name,
					gameMode = viewModel.getGameMode().name,
					aiAlgorithm = viewModel.getAlgorithm().name,
					startingPlayer = viewModel.getStartingPlayer().name
				)

				findNavController().navigate(action)
			}
		}
	}

	private fun updateUI(gameState: GameState) {
		with(binding) {
			currentNumber.text = "Current Number: ${gameState.currentNumber}"
			totalPoints.text = "Total Points: ${gameState.totalPoints}"
			gameBank.text = "Game Bank: ${gameState.gameBank}"
			val currentPlayerLabel = when (viewModel.getGameMode()) {
				GameMode.HUMAN_VS_HUMAN -> when (gameState.currentPlayer) {
					PlayerType.HUMAN -> getString(R.string.player_1)
					PlayerType.HUMAN_2 -> getString(R.string.player_2)
					else -> "-"
				}
				GameMode.HUMAN_VS_COMPUTER -> when (gameState.currentPlayer) {
					PlayerType.HUMAN -> getString(R.string.player_you)
					PlayerType.COMPUTER -> getString(R.string.player_computer)
					else -> "-"
				}
			}

			binding.currentPlayer.text = getString(R.string.current_player_label, currentPlayerLabel)

		}

		Log.d("GameFragment", "Current Player Turn: ${gameState.currentPlayer}")
		Log.d("GameFragment", "Is Game Over: ${gameState.isGameOver}")

		if (gameState.currentPlayer == PlayerType.COMPUTER || gameState.isGameOver) {
			Log.d("GameFragment", "Disabling buttons — not user's turn or game over")
			disableButtons()
		} else {
			Log.d("GameFragment", "Enabling buttons — it's human's turn")
			enableButtons()
		}
	}

	private fun onMultiplyClick(multiplier: Int) {
		playButtonClickSound()
		viewModel.makeMove(multiplier)
	}

	private fun showNumberSelectionDialog(
		gameMode: GameMode,
		aiAlgorithm: AIAlgorithm,
		startingPlayer: PlayerType
	) {
		val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_number_picker, null)
		val numberPicker = dialogView.findViewById<NumberPicker>(R.id.numberPicker).apply {
			minValue = 20
			maxValue = 30
			value = 20
		}

		val dialog = AlertDialog.Builder(requireContext())
			.setView(dialogView)
			.setCancelable(false)
			.create()

		dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
		dialog.show()

		dialogView.animate().alpha(1f).setDuration(500).start()
		dialogView.findViewById<Button>(R.id.confirmButton).setOnClickListener {
			binding.selectedNumber.text = "${numberPicker.value}"
			dialogView.animate().alpha(0f).setDuration(500).withEndAction {
				dialog.dismiss()

				viewModel.startGame(
					numberPicker.value,
					gameMode,
					aiAlgorithm,
					startingPlayer
				)

				// 🚫 Removed: viewModel.triggerComputerFirstMoveIfNeeded()
			}.start()
		}
	}

	private fun enableButtons() {
		binding.multiplyBy3.isEnabled = true
		binding.multiplyBy4.isEnabled = true
		binding.multiplyBy5.isEnabled = true
	}

	private fun disableButtons() {
		binding.multiplyBy3.isEnabled = false
		binding.multiplyBy4.isEnabled = false
		binding.multiplyBy5.isEnabled = false
	}

	private fun playButtonClickSound() {
		MediaPlayer.create(requireContext(), R.raw.button_cliked_effect).apply {
			start()
			setOnCompletionListener { release() }
		}
	}

	override fun onDestroyView() {
		super.onDestroyView()
		mediaPlayer?.release()
	}

	override fun onResume() {
		super.onResume()
		val sharedPrefs = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
		if (sharedPrefs.getBoolean("pref_music_enabled", true)) {
			if (mediaPlayer?.isPlaying == false) mediaPlayer?.start()
		}
	}

	override fun onPause() {
		super.onPause()
		if (mediaPlayer?.isPlaying == true) mediaPlayer?.pause()
	}
}
