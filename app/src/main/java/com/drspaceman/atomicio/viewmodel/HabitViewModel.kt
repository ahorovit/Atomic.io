package com.drspaceman.atomicio.viewmodel

class HabitViewModel {


    data class Habit(
        val name: String? = "",
        val type: String? = "",
        val sequencePosition: Int? = null
    )
}