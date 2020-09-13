package com.drspaceman.atomicio.model

import androidx.room.*
import java.time.LocalTime

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = Habit::class,
            parentColumns = ["id"],
            childColumns = ["habitId"],
            onDelete = ForeignKey.SET_NULL // Parent Habit is not required
        )
    ],
    indices = [Index("habitId")]
)
data class Task(
    @PrimaryKey(autoGenerate = true) var id: Long? = null,
    var habitId: Long? = null,
    var title: String? = "",
    var location: String? = "",
    var startTime: LocalTime? = null,
    var duration: Int? = null
)