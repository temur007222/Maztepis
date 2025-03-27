package com.uz.maztepis.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ResultViewModel : ViewModel() {

	private val _gameSteps = MutableLiveData<List<String>>()
	val gameSteps: LiveData<List<String>> get() = _gameSteps

	private val _winner = MutableLiveData<String>()
	val winner: LiveData<String> get() = _winner

	fun setGameResult(steps: List<String>, winner: String) {
		_gameSteps.value = steps
		_winner.value = winner
	}
}

