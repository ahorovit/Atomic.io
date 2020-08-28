package com.drspaceman.atomicio.db

import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.room.OnConflictStrategy.IGNORE
import androidx.room.OnConflictStrategy.REPLACE
import com.drspaceman.atomicio.model.Habit
import com.drspaceman.atomicio.model.HabitSequence
import com.drspaceman.atomicio.model.Identity

@Dao
interface AtomicIoDao {
    @Query("SELECT * FROM HabitSequence")
    fun loadAllHabitSequences(): LiveData<List<HabitSequence>>

    @Query("SELECT * FROM HabitSequence WHERE id = :sequenceId")
    fun loadHabitSequence(sequenceId: Long): HabitSequence

    @Query("SELECT * FROM HabitSequence WHERE id = :sequenceId")
    fun loadLiveHabitSequence(sequenceId: Long): LiveData<HabitSequence>

    @Insert(onConflict = IGNORE)
    fun insertHabitSequence(sequence: HabitSequence)

    @Update(onConflict = REPLACE)
    fun updateHabitSequence(sequence: HabitSequence)

    @Delete
    fun deleteHabitSequence(sequence: HabitSequence)


    // @TODO: Break up DAOs?


    @Query("SELECT * FROM Identity ORDER BY name")
    fun loadAllIdentities(): LiveData<List<Identity>>

    @Query("SELECT * FROM Identity WHERE id = :identityId")
    fun loadIdentity(identityId: Long): Identity

    @Query("SELECT * FROM Identity WHERE id = :identityId")
    fun loadLiveIdentity(identityId: Long): LiveData<Identity>

    @Insert(onConflict = IGNORE)
    fun insertIdentity(identity: Identity): Long?

    @Update(onConflict = REPLACE)
    fun updateIdentity(identity: Identity)

    @Delete
    fun deleteIdentity(identity: Identity)


    // @TODO: Break up DAOs?


    @Query("SELECT * FROM Habit")
    fun loadAllHabits(): LiveData<List<Habit>>

    @Query("SELECT * FROM Habit WHERE id = :habitId")
    fun loadHabit(habitId: Long): Habit

    @Query("SELECT * FROM Habit WHERE id = :habitId")
    fun loadLiveHabit(habitId: Long): LiveData<Habit>

    @Insert(onConflict = IGNORE)
    fun insertHabit(habit: Habit): Long?

    @Update(onConflict = REPLACE)
    fun updateHabit(habit: Habit)

    @Delete
    fun deleteHabit(habit: Habit)
}