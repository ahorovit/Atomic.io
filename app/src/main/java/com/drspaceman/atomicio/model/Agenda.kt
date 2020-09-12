package com.drspaceman.atomicio.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.*

@Entity(
    indices = [Index("date", unique = true)]
)
data class Agenda(
    @PrimaryKey(autoGenerate = true) var id: Long? = null,
    var date: Date? = null,
    var dayOfWeek: String? = ""
)