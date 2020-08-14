package com.drspaceman.atomicio.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.drspaceman.atomicio.R
import com.drspaceman.atomicio.viewmodel.HabitViewModel
import kotlinx.android.synthetic.main.habit_view_holder.view.*

class HabitRecyclerViewAdapter(
    val habitSequence: MutableList<HabitViewModel.Habit>
) : RecyclerView.Adapter<HabitRecyclerViewAdapter.HabitViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HabitViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.habit_view_holder,
            parent,
            false
        )

        return HabitViewHolder(view)
    }

    override fun getItemCount(): Int {
        return habitSequence.count()
    }

    override fun onBindViewHolder(holder: HabitViewHolder, position: Int) {
        holder.habitLabelTextView.text = habitSequence[position].name

    }

    class HabitViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val habitLabelTextView = itemView.habitLabelTextView
        val habitTypeImageView = itemView.habitTypeImageView
    }
}