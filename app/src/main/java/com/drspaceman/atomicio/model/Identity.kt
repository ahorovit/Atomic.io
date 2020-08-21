package com.drspaceman.atomicio.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Identity(
    @PrimaryKey(autoGenerate = true) var id: Long? = null,
    var name: String? = "",
    var description: String? = "",
    var type: String? = ""
)