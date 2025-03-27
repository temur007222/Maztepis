package com.uz.maztepis.logic

// GameState.kt
data class GameState(
	val currentNumber: Int,
	val totalPoints: Int,
	val gameBank: Int,
	val currentPlayer: PlayerType,
	val isGameOver: Boolean = false,
	val lastMultiplier: Int? = null
)

enum class PlayerType { HUMAN, HUMAN_2, COMPUTER }
enum class GameMode { HUMAN_VS_HUMAN, HUMAN_VS_COMPUTER }
enum class AIAlgorithm { MINIMAX, ALPHA_BETA }
