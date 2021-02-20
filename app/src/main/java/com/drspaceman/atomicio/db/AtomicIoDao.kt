package com.drspaceman.atomicio.db

import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.room.OnConflictStrategy.IGNORE
import androidx.room.OnConflictStrategy.REPLACE
import com.drspaceman.atomicio.model.*
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalTime

@Dao
interface AtomicIoDao {
    // @TODO: Break up DAOs?

    @Query("SELECT * FROM Identity ORDER BY name")
    fun loadAllIdentities(): LiveData<List<Identity>>

    @Query("SELECT * FROM Identity WHERE id = :identityId")
    fun loadIdentity(identityId: Long): Identity

    @Insert(onConflict = IGNORE)
    fun insertIdentity(identity: Identity): Long?

    @Update(onConflict = REPLACE)
    fun updateIdentity(identity: Identity)

    @Delete
    fun deleteIdentity(identity: Identity)

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

    @Insert(onConflict = IGNORE)
    fun insertHabit(habit: Habit): Long?

    @Update(onConflict = REPLACE)
    fun updateHabit(habit: Habit)

    @Delete
    fun deleteHabit(habit: Habit)


    // @TODO: Break up DAOs?

    @Query("SELECT * FROM Task WHERE dayFlags & :dayMask != 0")
    fun loadLiveTasksForDay(dayMask: Int): LiveData<List<Task>>

    @Query("SELECT * FROM Task WHERE habitId = :habitId")
    fun loadTasksForHabit(habitId: Long): List<Task>

    @Insert(onConflict = IGNORE)
    fun insertTask(task: Task): Long?

    @Query("SELECT * FROM Task WHERE id = :taskId")
    fun loadTask(taskId: Long): Task

    @Update(onConflict = REPLACE)
    fun updateTask(task: Task)

    @Delete
    fun deleteTask(task: Task)


    // @TODO: Break up DAOs?

    @Query(
        "SELECT " +
                "task.*, " +
                "result.id as resultId " +
                "FROM Task task " +
                "LEFT JOIN TaskResult result ON task.id = result.taskId " +
                "WHERE dayFlags & :dayMask != 0 " +
                "ORDER BY task.startTime ASC"
    )
    fun loadTaskResultsForDay(dayMask: Int): List<TaskAndResult>

    data class TaskAndResult(
        var id: Long?,
        var habitId: Long?,
        var name: String?,
        var startTime: LocalTime?,
        var duration: Int?,
        var maxVal: Int?,
        var dayFlags: Int?,
        var resultId: Long?,
    )

    @Insert(onConflict = REPLACE)
    fun saveTaskResults(results: List<TaskResult>)

    @Insert(onConflict = REPLACE)
    fun upsertTasks(tasksToSave: List<Task>)

}