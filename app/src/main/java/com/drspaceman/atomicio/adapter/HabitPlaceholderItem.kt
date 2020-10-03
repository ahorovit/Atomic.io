package com.drspaceman.atomicio.adapter

import com.drspaceman.atomicio.R
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item

class HabitPlaceholderItem() : Item() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        // Placeholder needs no binding (attributes set in layout
    }

    override fun getLayout() = R.layout.habit_placeholder
}