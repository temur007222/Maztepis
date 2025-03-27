package com.uz.maztepis.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class HomeViewModel(application: Application) : AndroidViewModel(application) {

	// ========== Enums ==========
	enum class GameMode { HUMAN_VS_HUMAN, HUMAN_VS_COMPUTER }
	enum class AIAlgorithm { MINIMAX, ALPHA_BETA }
	enum class PlayerType { HUMAN, HUMAN_2, COMPUTER }

	// ========== Preferences ==========
	private val sharedPrefs = application.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

	// ========== Settings LiveData ==========
	private val _isFirstLaunch = MutableLiveData(loadFirstLaunchPreference())
	val isFirstLaunch: LiveData<Boolean> = _isFirstLaunch

	private val _isMusicEnabled = MutableLiveData(loadMusicPreference())
	val isMusicEnabled: LiveData<Boolean> = _isMusicEnabled

	// ========== Game Configuration LiveData ==========
	private val _gameMode = MutableLiveData(loadGameModePreference())
	val gameMode: LiveData<GameMode> = _gameMode

	private val _aiAlgorithm = MutableLiveData(loadAIAlgorithmPreference())
	val aiAlgorithm: LiveData<AIAlgorithm> = _aiAlgorithm

	private val _startingPlayer = MutableLiveData(loadStartingPlayerPreference())
	val startingPlayer: LiveData<PlayerType> = _startingPlayer

	private val _navigateToGame = MutableLiveData<Boolean>(false)
	val navigateToGame: LiveData<Boolean> = _navigateToGame

	// ========== Preference Loaders ==========
	private fun loadFirstLaunchPreference(): Boolean {
		return sharedPrefs.getBoolean(PREF_FIRST_LAUNCH, true)
	}

	private fun loadMusicPreference(): Boolean {
		return sharedPrefs.getBoolean(PREF_MUSIC_ENABLED, true)
	}

	private fun loadGameModePreference(): GameMode {
		return try {
			GameMode.valueOf(
				sharedPrefs.getString(PREF_GAME_MODE, GameMode.HUMAN_VS_COMPUTER.name)
					?: GameMode.HUMAN_VS_COMPUTER.name
			)
		} catch (e: IllegalArgumentException) {
			GameMode.HUMAN_VS_COMPUTER
		}
	}

	private fun loadAIAlgorithmPreference(): AIAlgorithm {
		return try {
			AIAlgorithm.valueOf(
				sharedPrefs.getString(PREF_AI_ALGORITHM, AIAlgorithm.MINIMAX.name)
					?: AIAlgorithm.MINIMAX.name
			)
		} catch (e: IllegalArgumentException) {
			AIAlgorithm.MINIMAX
		}
	}

	private fun loadStartingPlayerPreference(): PlayerType {
		return try {
			PlayerType.valueOf(
				sharedPrefs.getString(PREF_STARTING_PLAYER, PlayerType.HUMAN.name)
					?: PlayerType.HUMAN.name
			)
		} catch (e: IllegalArgumentException) {
			PlayerType.HUMAN
		}
	}

	// ========== Public Methods ==========
	fun markFirstLaunch() {
		sharedPrefs.edit().putBoolean(PREF_FIRST_LAUNCH, false).apply()
		_isFirstLaunch.value = false
	}

	fun setMusicEnabled(enabled: Boolean) {
		sharedPrefs.edit().putBoolean(PREF_MUSIC_ENABLED, enabled).apply()
		_isMusicEnabled.value = enabled
	}

	fun setGameMode(mode: GameMode) {
		sharedPrefs.edit().putString(PREF_GAME_MODE, mode.name).apply()
		_gameMode.value = mode
		validateStartingPlayer()
	}

	fun setAIAlgorithm(algorithm: AIAlgorithm) {
		sharedPrefs.edit().putString(PREF_AI_ALGORITHM, algorithm.name).apply()
		_aiAlgorithm.value = algorithm
	}

	fun setStartingPlayer(player: PlayerType) {
		val validatedPlayer = when (_gameMode.value) {
			GameMode.HUMAN_VS_COMPUTER ->
				if (player == PlayerType.HUMAN_2) PlayerType.HUMAN else player
			GameMode.HUMAN_VS_HUMAN ->
				// Here, convert COMPUTER (i.e. the second option in the UI) to HUMAN_2.
				if (player == PlayerType.COMPUTER) PlayerType.HUMAN_2 else player
			else -> PlayerType.HUMAN
		}
		sharedPrefs.edit().putString(PREF_STARTING_PLAYER, validatedPlayer.name).apply()
		_startingPlayer.value = validatedPlayer
	}

	fun startGame() {
		_navigateToGame.value = true
	}

	fun completeNavigation() {
		_navigateToGame.value = false
	}

	// ========== Private Helpers ==========
	private fun validateStartingPlayer() {
		_gameMode.value?.let { currentMode ->
			when (currentMode) {
				GameMode.HUMAN_VS_HUMAN -> {
					if (_startingPlayer.value == PlayerType.COMPUTER) {
						setStartingPlayer(PlayerType.HUMAN)
					}
				}
				GameMode.HUMAN_VS_COMPUTER -> {
					if (_startingPlayer.value == PlayerType.HUMAN_2) {
						setStartingPlayer(PlayerType.HUMAN)
					}
				}
			}
		}
	}

	companion object {
		// Preference keys
		private const val PREF_FIRST_LAUNCH = "pref_first_launch"
		private const val PREF_MUSIC_ENABLED = "pref_music_enabled"
		private const val PREF_GAME_MODE = "pref_game_mode"
		private const val PREF_AI_ALGORITHM = "pref_ai_algorithm"
		private const val PREF_STARTING_PLAYER = "pref_starting_player"
	}
}