package com.uz.maztepis.logic

// Handles core logic of the game, including moves, AI, and game state tracking
class GameLogic {

	// Current state of the game
	private var gameState: GameState = GameState(0, 0, 0, PlayerType.HUMAN)

	// List to keep track of all game steps as text descriptions
	private val gameSteps = mutableListOf<String>()

	// Selected AI algorithm (Minimax or Alpha-Beta pruning)
	private var aiAlgorithm: AIAlgorithm = AIAlgorithm.MINIMAX

	// Selected game mode (Human vs Human or Human vs Computer)
	private var gameMode: GameMode = GameMode.HUMAN_VS_COMPUTER

	// Root node of the game tree for AI decisions
	private var gameTree: GameTreeNode? = null

	/**
	 * Starts a new game with provided settings.
	 */
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

		// Build initial game tree for AI moves
		gameTree = buildGameTree(gameState, depth = 3)
	}

	/**
	 * Player makes a move with the given multiplier.
	 */
	fun makeMove(multiplier: Int) {
		require(multiplier in listOf(3, 4, 5)) { "Multiplier must be 3, 4, or 5." }
		val currentPlayer = gameState.currentPlayer
		gameState = applyMove(multiplier, currentPlayer)
		updateGameSteps(currentPlayer, multiplier)

		// Rebuild game tree from new state after move
		gameTree = buildGameTree(gameState, depth = 3)
	}

	/**
	 * Applies the move logic and returns updated game state.
	 */
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

	/**
	 * Updates the list of game steps with latest move details.
	 */
	private fun updateGameSteps(player: PlayerType, multiplier: Int) {
		gameSteps.add("Player $player multiplied by $multiplier, resulting in ${gameState.currentNumber}. Points: ${gameState.totalPoints}, Bank: ${gameState.gameBank}")
	}

	/**
	 * Returns the best move decided by the selected AI algorithm.
	 */
	fun getAIMove(): Int {
		return when (aiAlgorithm) {
			AIAlgorithm.MINIMAX -> minimax(gameTree!!, depth = 3, isMaximizing = true).first
			AIAlgorithm.ALPHA_BETA -> alphaBeta(gameTree!!, Int.MIN_VALUE, Int.MAX_VALUE, depth = 3, isMaximizing = true).first
		}
	}

	/**
	 * Resets the game to initial state.
	 */
	fun resetGame() {
		gameState = GameState(0, 0, 0, PlayerType.HUMAN)
		gameSteps.clear()
		gameTree = null
	}

	/**
	 * Returns current game state.
	 */
	fun getGameState(): GameState = gameState

	/**
	 * Determines the winner based on points and bank adjustments.
	 */
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

	/**
	 * Returns the list of game steps.
	 */
	fun getGameSteps(): List<String> = gameSteps

	/**
	 * Node structure representing a state in the game tree.
	 */
	data class GameTreeNode(
		val state: GameState,
		val children: MutableList<GameTreeNode> = mutableListOf(),
		var move: Int? = null
	)

	/**
	 * Recursively builds the game tree up to a given depth.
	 */
	private fun buildGameTree(state: GameState, depth: Int): GameTreeNode {
		val root = GameTreeNode(state)
		if (depth == 0 || state.isGameOver) return root

		val possibleMoves = listOf(3, 4, 5)
		for (move in possibleMoves) {
			val newState = simulateMove(state, move)
			val childNode = buildGameTree(newState, depth - 1)
			// Remember which move led to this child
			childNode.move = move
			root.children.add(childNode)
		}
		return root
	}

	/**
	 * Standard Minimax algorithm for decision making.
	 */
	private fun minimax(node: GameTreeNode, depth: Int, isMaximizing: Boolean): Pair<Int, Int> {
		if (depth == 0 || node.state.isGameOver) return Pair(evaluateState(node.state), node.move ?: 3)

		if (isMaximizing) {
			var bestValue = Int.MIN_VALUE
			var bestMove = 3
			for (child in node.children) {
				val (value, _) = minimax(child, depth - 1, false)
				if (value > bestValue) {
					bestValue = value
					bestMove = child.move ?: 3
				}
			}
			return Pair(bestValue, bestMove)
		} else {
			var bestValue = Int.MAX_VALUE
			for (child in node.children) {
				val (value, _) = minimax(child, depth - 1, true)
				bestValue = minOf(bestValue, value)
			}
			return Pair(bestValue, node.move ?: 3)
		}
	}

	/**
	 * Optimized Minimax using Alpha-Beta pruning.
	 */
	private fun alphaBeta(node: GameTreeNode, alpha: Int, beta: Int, depth: Int, isMaximizing: Boolean): Pair<Int, Int> {
		var alphaVar = alpha
		var betaVar = beta
		if (depth == 0 || node.state.isGameOver) return Pair(evaluateState(node.state), node.move ?: 3)

		if (isMaximizing) {
			var bestValue = Int.MIN_VALUE
			var bestMove = 3
			for (child in node.children) {
				val (value, _) = alphaBeta(child, alphaVar, betaVar, depth - 1, false)
				if (value > bestValue) {
					bestValue = value
					bestMove = child.move ?: 3
				}
				alphaVar = maxOf(alphaVar, value)
				if (betaVar <= alphaVar) break // Cut-off
			}
			return Pair(bestValue, bestMove)
		} else {
			var bestValue = Int.MAX_VALUE
			for (child in node.children) {
				val (value, _) = alphaBeta(child, alphaVar, betaVar, depth - 1, true)
				bestValue = minOf(bestValue, value)
				betaVar = minOf(betaVar, value)
				if (betaVar <= alphaVar) break // Cut-off
			}
			return Pair(bestValue, node.move ?: 3)
		}
	}

	/**
	 * Simulates a move without changing the real game state.
	 */
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

	/**
	 * Evaluates a game state to assign a score for AI decisions.
	 */
	private fun evaluateState(state: GameState): Int {
		val pointValue = state.totalPoints
		val bankValue = state.gameBank * 2
		val slowDown = -(state.currentNumber / 100)
		val parityBonus = if (state.totalPoints % 2 == 0) 3 else -3
		val lastDigitBonus = if (state.currentNumber % 10 == 0 || state.currentNumber % 10 == 5) 2 else 0
		val nearEndPenalty = if (state.currentNumber >= 2500 && state.totalPoints < 0) -10 else 0

		return pointValue + bankValue + slowDown + parityBonus + lastDigitBonus + nearEndPenalty
	}
}
