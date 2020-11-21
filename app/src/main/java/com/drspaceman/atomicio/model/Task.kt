package com.drspaceman.atomicio.model

import androidx.room.*
import com.drspaceman.atomicio.adapter.DaySelection
import org.threeten.bp.LocalTime

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = Habit::class,
            parentColumns = ["id"],
            childColumns = ["habitId"],
            onDelete = ForeignKey.SET_NULL // Parent Habit is not required
        )
    ],
    indices = [
        Index("habitId"),
        Index("dayFlags")
    ]
)
data class Task(
    @PrimaryKey(autoGenerate = true) var id: Long? = null,
    var habitId: Long? = null,
    var name: String? = "",
    var startTime: LocalTime? = null,
    var duration: Int? = null,
    var maxVal: Int? = null,
    var dayFlags: Int? = 0  // Bits represent days active. EG 1000101 (monday, wed, sun are active)
)