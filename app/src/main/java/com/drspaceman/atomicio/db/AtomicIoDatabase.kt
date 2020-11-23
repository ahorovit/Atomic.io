package com.drspaceman.atomicio.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.drspaceman.atomicio.model.*

@Database(
    entities = [
        Identity::class,
        Habit::class,
        Task::class,
        TaskResult::class
    ],
    version = 1
)
@TypeConverters(Converters::class)
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