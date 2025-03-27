package com.uz.maztepis.viewmodel



import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.uz.maztepis.logic.AIAlgorithm
import com.uz.maztepis.logic.GameLogic
import com.uz.maztepis.logic.GameMode
import com.uz.maztepis.logic.GameState
import com.uz.maztepis.logic.PlayerType

// GameViewModel.kt
class GameViewModel : ViewModel() {

	private val gameLogic = GameLogic()
	private val _gameState = MutableLiveData<GameState>()
	val gameState: LiveData<GameState> get() = _gameState

	private val _winner = MutableLiveData<PlayerType?>()
	val winner: LiveData<PlayerType?> get() = _winner

	private var gameMode: GameMode = GameMode.HUMAN_VS_COMPUTER
	private var startingPlayer: PlayerType = PlayerType.HUMAN
	private var aiAlgorithm: AIAlgorithm = AIAlgorithm.MINIMAX

	fun startGame(startNumber: Int, mode: GameMode, aiAlgorithm: AIAlgorithm, startingPlayer: PlayerType) {
		this.gameMode = mode
		this.startingPlayer = startingPlayer
		this.aiAlgorithm = aiAlgorithm

		gameLogic.startGame(startNumber, mode, aiAlgorithm, startingPlayer)
		updateGameState()
	}

	fun makeMove(multiplier: Int) {
		val current = gameLogic.getGameState().currentPlayer

		if (gameMode == GameMode.HUMAN_VS_COMPUTER) {
			if (current == PlayerType.HUMAN) {
				Log.d("GameViewModel", "✅ Human is making a move: $multiplier")
				gameLogic.makeMove(multiplier)
			} else if (current == PlayerType.COMPUTER) {
				Log.d("GameViewModel", "✅ Computer is making a move: $multiplier")
				gameLogic.makeMove(multiplier)
			} else {
				Log.d("GameViewModel", "❌ Invalid move! Not this player's turn")
				return
			}
		} else {
			// Human vs Human or other mode
			gameLogic.makeMove(multiplier)
		}

		updateGameState()
	}

	fun getGameSteps(): List<String> = gameLogic.getGameSteps()

	fun resetGame() {
		gameLogic.resetGame()
		_winner.value = null
		updateGameState()
	}

	private fun updateGameState() {
		val newState = gameLogic.getGameState()
		_gameState.value = newState

		if (newState.isGameOver) {
			_winner.value = gameLogic.determineWinner()
		}
	}

	fun getGameLogic(): GameLogic = gameLogic
	fun getGameMode(): GameMode = gameMode
	fun getAlgorithm(): AIAlgorithm = aiAlgorithm
	fun getStartingPlayer(): PlayerType = startingPlayer
}
