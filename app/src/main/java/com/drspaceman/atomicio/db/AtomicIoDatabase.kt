package com.drspaceman.atomicio.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.drspaceman.atomicio.model.Habit
import com.drspaceman.atomicio.model.HabitSequence
import com.drspaceman.atomicio.model.Identity

@Database(
    entities = [
        Identity::class,
        HabitSequence::class,
        Habit::class
    ],
    version = 1
)
abstract class AtomicIoDatabase : RoomDatabase() {

    abstract fun atomicIoDao(): AtomicIoDao

    companion object {
        private var instance: AtomicIoDatabase? = null

        fun getInstance(context: Context): AtomicIoDatabase {
            if (instance == null) {
                instance = Room.databaseBuilder(
                    context.applicationContext,
                    AtomicIoDatabase::class.java,
                    "AtomicIo"
                ).build()
            }

            return instance as AtomicIoDatabase
        }
    }
}