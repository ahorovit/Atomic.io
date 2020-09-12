package com.drspaceman.atomicio.model

import androidx.room.*
import java.util.*

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
    var hour: Int? = null,
    var minute: Int? = null,
    var duration: Int? = null
)