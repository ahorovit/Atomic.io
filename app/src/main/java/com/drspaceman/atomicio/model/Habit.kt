package com.drspaceman.atomicio.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = HabitSequence::class,
            parentColumns = ["id"],
            childColumns = ["sequenceId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("sequenceId")]
)
data class Habit(
    @PrimaryKey(autoGenerate = true) val id: Long? = null,
    var sequenceId: Long? = null,
    var name: String? = "",
    var type: String? = "",
    var sequencePosition: Int? = null,
    var duration: Int? = null
)