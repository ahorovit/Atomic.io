package com.drspaceman.atomicio.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = Identity::class,
            parentColumns = ["id"],
            childColumns = ["identityId"],
            onDelete = ForeignKey.SET_NULL // Parent Identity is not required
        )
    ],
    indices = [Index("identityId")]
)
data class Habit(
    @PrimaryKey(autoGenerate = true) var id: Long? = null,
    var identityId: Long? = null,
    var name: String? = "",
    var type: String? = "",
    var duration: Int? = null // @todo: remove
)