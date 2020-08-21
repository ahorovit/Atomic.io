package com.drspaceman.atomicio.db

import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.room.OnConflictStrategy.IGNORE
import androidx.room.OnConflictStrategy.REPLACE
import com.drspaceman.atomicio.model.HabitSequence
import com.drspaceman.atomicio.model.Identity

@Dao
interface AtomicIoDao {
    @Query("SELECT * FROM HabitSequence")
    fun loadAll(): LiveData<List<HabitSequence>>

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

    @Insert(onConflict = IGNORE)
    fun insertIdentity(identity: Identity): Long?

    @Query("SELECT * FROM Identity WHERE id = :identityId")
    fun loadLiveIdentity(identityId: Long): LiveData<Identity>

    @Update(onConflict = REPLACE)
    fun updateIdentity(identity: Identity)

    @Query("SELECT * FROM Identity WHERE id = :identityId")
    fun getIdentity(identityId: Long): Identity
}