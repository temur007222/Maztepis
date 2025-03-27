package com.uz.maztepis.ui


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.uz.maztepis.R
import com.uz.maztepis.adapter.StepsAdapter
import com.uz.maztepis.databinding.FragmentResultBinding
import com.uz.maztepis.viewmodel.ResultViewModel

class ResultFragment : Fragment() {

	private lateinit var binding: FragmentResultBinding
	private lateinit var viewModel: ResultViewModel

	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		binding = FragmentResultBinding.inflate(inflater, container, false)
		return binding.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		viewModel = ViewModelProvider(this)[ResultViewModel::class.java]

		requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
			override fun handleOnBackPressed() {
				navigateToHome()
			}
		})

		val gameSteps = arguments?.getStringArray("gameSteps")?.toList() ?: listOf()
		val rawWinner = arguments?.getString("winner") ?: "UNKNOWN"
		val rawAlgorithm = arguments?.getString("aiAlgorithm") ?: "-"
		val rawGameMode = arguments?.getString("gameMode") ?: "-"
		val rawStartingPlayer = arguments?.getString("startingPlayer") ?: "-"

		// 💬 Convert raw enum values to display-friendly strings
		val winnerLabel = when (rawWinner) {
			"COMPUTER" -> getString(R.string.player_computer)
			"HUMAN" -> getString(R.string.player_human)
			"HUMAN_2" -> getString(R.string.player_human_2)
			else -> getString(R.string.unknown_winner)
		}

		val algorithmLabel = when (rawAlgorithm) {
			"MINIMAX" -> getString(R.string.algorithm_minimax)
			"ALPHA_BETA" -> getString(R.string.algorithm_alpha_beta)
			else -> "-"
		}

		val modeLabel = when (rawGameMode) {
			"HUMAN_VS_HUMAN" -> getString(R.string.mode_human_vs_human)
			"HUMAN_VS_COMPUTER" -> getString(R.string.mode_human_vs_computer)
			else -> "-"
		}

		val starterLabel = when (rawStartingPlayer) {
			"COMPUTER" -> getString(R.string.player_computer)
			"HUMAN" -> getString(R.string.player_human)
			"HUMAN_2" -> getString(R.string.player_human_2)
			else -> "-"
		}

		// ✅ Update UI
		viewModel.setGameResult(gameSteps, rawWinner)

		binding.winnerText.text = getString(R.string.winner_label, winnerLabel)
		binding.algorithmText.text = algorithmLabel
		binding.gameModeText.text = modeLabel
		binding.startingPlayerText.text = starterLabel

		binding.stepsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
		binding.stepsRecyclerView.adapter = StepsAdapter(gameSteps)

		binding.restartButton.setOnClickListener { navigateToGame() }
		binding.homeButton.setOnClickListener { navigateToHome() }
	}

	private fun navigateToGame() {
		findNavController().navigate(R.id.gameFragment)
	}

	private fun navigateToHome() {
		findNavController().navigate(R.id.homeFragment)
	}
}
