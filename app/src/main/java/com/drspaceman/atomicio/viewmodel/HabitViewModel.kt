package com.drspaceman.atomicio.viewmodel

class HabitViewModel {


    data class HabitViewData(
        var name: String? = "",
        var type: String? = "",
        var sequencePosition: Int? = null
    )
}