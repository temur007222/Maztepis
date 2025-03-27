package com.uz.maztepis.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.uz.maztepis.R

class StepsAdapter(private val steps: List<String>) : RecyclerView.Adapter<StepsAdapter.StepViewHolder>() {

	inner class StepViewHolder(view: View) : RecyclerView.ViewHolder(view) {
		val stepText: TextView = view.findViewById(R.id.stepText)
	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StepViewHolder {
		val view = LayoutInflater.from(parent.context).inflate(R.layout.item_step, parent, false)
		return StepViewHolder(view)
	}

	override fun onBindViewHolder(holder: StepViewHolder, position: Int) {
		holder.stepText.text = steps[position]
	}

	override fun getItemCount(): Int = steps.size
}

