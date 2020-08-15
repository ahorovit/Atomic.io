package com.drspaceman.atomicio.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class HabitSequence(
    @PrimaryKey(autoGenerate = true) var id: Long? = null,
    var name: String? = "",
    var startTime: String? = "",
    var deadLine: String? = ""
)