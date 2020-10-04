package com.drspaceman.atomicio.db

import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.room.OnConflictStrategy.IGNORE
import androidx.room.OnConflictStrategy.REPLACE
import com.drspaceman.atomicio.model.*
import org.threeten.bp.LocalDate

@Dao
interface AtomicIoDao {
    @Query("SELECT * FROM TaskChain")
    fun loadAllTaskChains(): LiveData<List<TaskChain>>

    @Query("SELECT * FROM TaskChain WHERE id = :sequenceId")
    fun loadTaskChain(sequenceId: Long): TaskChain

    @Query("SELECT * FROM TaskChain WHERE id = :sequenceId")
    fun loadLiveTaskChain(sequenceId: Long): LiveData<TaskChain>

    @Insert(onConflict = IGNORE)
    fun insertTaskChain(sequence: TaskChain)

    @Update(onConflict = REPLACE)
    fun updateTaskChain(sequence: TaskChain)

    @Delete
    fun deleteTaskChain(sequence: TaskChain)


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

    @Query("DELETE FROM Identity WHERE id = :identityId")
    fun deleteIdentityById(identityId: Long)

    @Query(
        "SELECT " +
                "identity.id as identityId, " +
                "identity.name as identityName, " +
                "identity.type as identityType, " +
                "habit.id as habitId, " +
                "habit.name as habitName " +
                "FROM Identity identity " +
                "LEFT JOIN Habit habit ON identity.id = habit.identityId " +
                "ORDER BY identityName ASC, habitName ASC"
    )
    fun loadIdentitiesWithHabits(): LiveData<List<IdentityHabit>>

    data class IdentityHabit(
        val identityId: Long?,
        val identityName: String?,
        val identityType: String?,
        val habitId: Long?,
        val habitName: String?
    )

    @Query("SELECT * FROM Habit WHERE identityId IS NULL")
    fun loadOrphanHabits() : LiveData<List<Habit>>

    @Query("DELETE FROM Habit WHERE identityId = :identityId")
    suspend fun deleteHabitsForIdentity(identityId: Long)


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


    // @TODO: Break up DAOs?


    @Insert(onConflict = IGNORE)
    fun insertTask(task: Task): Long?

    @Query("SELECT * FROM Task WHERE id = :taskId")
    fun loadLiveTask(taskId: Long): LiveData<Task>

    @Query("SELECT * FROM Task WHERE id = :taskId")
    fun loadTask(taskId: Long): Task

    @Query("SELECT * FROM Task WHERE agendaId = :agendaId")
    fun getTasksForAgenda(agendaId: Long?): LiveData<List<Task>>

    @Update(onConflict = REPLACE)
    fun updateTask(task: Task)

    @Delete
    fun deleteTask(task: Task)


    // @TODO: Break up DAOs?


    @Query("SELECT * from Agenda WHERE date = :date")
    suspend fun getAgenda(date: LocalDate): Agenda?

    @Insert(onConflict = IGNORE)
    suspend fun insertAgenda(agenda: Agenda): Long?
}