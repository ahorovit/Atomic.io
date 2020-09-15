package com.drspaceman.atomicio.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import org.threeten.bp.LocalDate

@Entity(
    indices = [Index("date", unique = true)]
)
data class Agenda(
    @PrimaryKey(autoGenerate = true) var id: Long? = null,
    var date: LocalDate? = null
)