package com.uz.maztepis.logic

// GameState.kt
/**
 * Represents the current state of the game at any given time.
 */
data class GameState(
	val currentNumber: Int,      // The current number in the game
	val totalPoints: Int,        // Player's total accumulated points
	val gameBank: Int,           // Bonus bank that can affect final score
	val currentPlayer: PlayerType, // The player who has the current move
	val isGameOver: Boolean = false, // Indicates if the game has ended
	val lastMultiplier: Int? = null   // The last multiplier used (3, 4, or 5)
)

/**
 * Defines the types of players in the game.
 */
enum class PlayerType {
	HUMAN,      // First human player
	HUMAN_2,    // Second human player (for Human vs Human mode)
	COMPUTER    // AI-controlled player
}

/**
 * Defines the available game modes.
 */
enum class GameMode {
	HUMAN_VS_HUMAN,      // Two humans play against each other
	HUMAN_VS_COMPUTER    // Human plays against the computer (AI)
}

/**
 * Defines the algorithms available for AI moves.
 */
enum class AIAlgorithm {
	MINIMAX,     // Standard Minimax decision-making algorithm
	ALPHA_BETA   // Minimax with Alpha-Beta pruning optimization
}
