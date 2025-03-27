package com.uz.maztepis.logic

class GameLogic {

	private var gameState: GameState = GameState(0, 0, 0, PlayerType.HUMAN)
	private val gameSteps = mutableListOf<String>()
	private var aiAlgorithm: AIAlgorithm = AIAlgorithm.MINIMAX
	private var gameMode: GameMode = GameMode.HUMAN_VS_COMPUTER

	fun startGame(
		startNumber: Int,
		mode: GameMode,
		selectedAIAlgorithm: AIAlgorithm,
		startingPlayer: PlayerType
	) {
		require(startNumber in 20..30) { "Starting number must be between 20 and 30." }
		gameMode = mode
		aiAlgorithm = selectedAIAlgorithm
		gameState = GameState(startNumber, 0, 0, startingPlayer)
		gameSteps.clear()
		gameSteps.add("Game started with number: $startNumber by $startingPlayer")
	}

	fun makeMove(multiplier: Int) {
		require(multiplier in listOf(3, 4, 5)) { "Multiplier must be 3, 4, or 5." }
		val currentPlayer = gameState.currentPlayer
		gameState = applyMove(multiplier, currentPlayer)
		updateGameSteps(currentPlayer, multiplier)
	}

	private fun applyMove(multiplier: Int, currentPlayer: PlayerType): GameState {
		val newNumber = gameState.currentNumber * multiplier
		val newPoints = if (newNumber % 2 == 0) gameState.totalPoints + 1 else gameState.totalPoints - 1
		val newBank = if (newNumber % 10 == 0 || newNumber % 10 == 5) gameState.gameBank + 1 else gameState.gameBank
		val isGameOver = newNumber >= 3000
		val nextPlayer = when (gameMode) {
			GameMode.HUMAN_VS_HUMAN -> if (currentPlayer == PlayerType.HUMAN) PlayerType.HUMAN_2 else PlayerType.HUMAN
			GameMode.HUMAN_VS_COMPUTER -> if (currentPlayer == PlayerType.HUMAN) PlayerType.COMPUTER else PlayerType.HUMAN
		}
		return GameState(newNumber, newPoints, newBank, nextPlayer, isGameOver, multiplier)
	}

	private fun updateGameSteps(player: PlayerType, multiplier: Int) {
		gameSteps.add("Player $player multiplied by $multiplier, resulting in ${gameState.currentNumber}. Points: ${gameState.totalPoints}, Bank: ${gameState.gameBank}")
	}

	fun getAIMove(): Int {
		return when (aiAlgorithm) {
			AIAlgorithm.MINIMAX -> minimax(gameState, depth = 3)
			AIAlgorithm.ALPHA_BETA -> alphaBeta(gameState, Int.MIN_VALUE, Int.MAX_VALUE, depth = 3)
		}
	}

	fun resetGame() {
		gameState = GameState(0, 0, 0, PlayerType.HUMAN)
		gameSteps.clear()
	}

	fun getGameState(): GameState = gameState

	fun determineWinner(): PlayerType {
		val adjustedPoints = if (gameState.totalPoints % 2 == 0) {
			gameState.totalPoints - gameState.gameBank
		} else {
			gameState.totalPoints + gameState.gameBank
		}
		return when (gameMode) {
			GameMode.HUMAN_VS_HUMAN -> {
				if (adjustedPoints % 2 == 0) PlayerType.HUMAN else PlayerType.HUMAN_2
			}
			GameMode.HUMAN_VS_COMPUTER -> {
				if (adjustedPoints % 2 == 0) PlayerType.HUMAN else PlayerType.COMPUTER
			}
		}
	}

	fun getGameSteps(): List<String> = gameSteps

	private fun minimax(state: GameState, depth: Int = 3, isMaximizing: Boolean = true): Int {
		if (depth == 0 || state.isGameOver) return evaluateState(state)
		val possibleMoves = listOf(3, 4, 5)
		return if (isMaximizing) {
			var bestValue = Int.MIN_VALUE
			var bestMove = 3
			for (move in possibleMoves) {
				val newState = simulateMove(state, move)
				val value = minimax(newState, depth - 1, false)
				if (value > bestValue) {
					bestValue = value
					bestMove = move
				}
			}
			bestMove
		} else {
			var bestValue = Int.MAX_VALUE
			for (move in possibleMoves) {
				val newState = simulateMove(state, move)
				val value = minimax(newState, depth - 1, true)
				bestValue = minOf(bestValue, value)
			}
			bestValue
		}
	}

	private fun alphaBeta(state: GameState, alpha: Int, beta: Int, depth: Int = 3, isMaximizing: Boolean = true): Int {
		var alphaVar = alpha
		var betaVar = beta
		if (depth == 0 || state.isGameOver) return evaluateState(state)
		val possibleMoves = listOf(3, 4, 5)
		return if (isMaximizing) {
			var bestValue = Int.MIN_VALUE
			var bestMove = 3
			for (move in possibleMoves) {
				val newState = simulateMove(state, move)
				val value = alphaBeta(newState, alphaVar, betaVar, depth - 1, false)
				if (value > bestValue) {
					bestValue = value
					bestMove = move
				}
				alphaVar = maxOf(alphaVar, value)
				if (betaVar <= alphaVar) break
			}
			bestMove
		} else {
			var bestValue = Int.MAX_VALUE
			for (move in possibleMoves) {
				val newState = simulateMove(state, move)
				val value = alphaBeta(newState, alphaVar, betaVar, depth - 1, true)
				bestValue = minOf(bestValue, value)
				betaVar = minOf(betaVar, value)
				if (betaVar <= alphaVar) break
			}
			bestValue
		}
	}

	private fun simulateMove(state: GameState, multiplier: Int): GameState {
		val newNumber = state.currentNumber * multiplier
		val newPoints = if (newNumber % 2 == 0) state.totalPoints + 1 else state.totalPoints - 1
		val newBank = if (newNumber % 10 == 0 || newNumber % 10 == 5) state.gameBank + 1 else state.gameBank
		val isGameOver = newNumber >= 3000
		val nextPlayer = when (gameMode) {
			GameMode.HUMAN_VS_HUMAN -> if (state.currentPlayer == PlayerType.HUMAN) PlayerType.HUMAN_2 else PlayerType.HUMAN
			GameMode.HUMAN_VS_COMPUTER -> if (state.currentPlayer == PlayerType.HUMAN) PlayerType.COMPUTER else PlayerType.HUMAN
		}
		return GameState(newNumber, newPoints, newBank, nextPlayer, isGameOver, multiplier)
	}

	private fun evaluateState(state: GameState): Int {
		val adjustedPoints = if (state.totalPoints % 2 == 0) {
			state.totalPoints - state.gameBank
		} else {
			state.totalPoints + state.gameBank
		}

		return -adjustedPoints * 10 + state.gameBank * 15 - (state.currentNumber / 40)
	}
}

