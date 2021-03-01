package com.drspaceman.atomicio.repository

import androidx.lifecycle.LiveData
import com.drspaceman.atomicio.R
import com.drspaceman.atomicio.adapter.DaySelection
import com.drspaceman.atomicio.db.AtomicIoDao
import com.drspaceman.atomicio.model.Habit
import com.drspaceman.atomicio.model.Identity
import com.drspaceman.atomicio.model.Task
import com.drspaceman.atomicio.viewmodel.BaseViewModel
import com.drspaceman.atomicio.viewmodel.BaseViewModel.ViewDataStub.Companion.VIEWDATA_STUB_IMAGE
import com.drspaceman.atomicio.viewmodel.BaseViewModel.ViewDataStub.Companion.VIEWDATA_STUB_TYPE
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.threeten.bp.DayOfWeek
import org.threeten.bp.LocalDate
import javax.inject.Inject

@ActivityRetainedScoped
class AtomicIoRepository
@Inject
constructor(
    private val dao: AtomicIoDao
) {
    // @todo: standardize
    val allIdentities: LiveData<List<Identity>>
        get() {
            return dao.loadAllIdentities()
        }

    val allHabits: LiveData<List<Habit>>
        get() {
            return dao.loadAllHabits()
        }

    fun addIdentity(identity: Identity): Long? {
        val newId = dao.insertIdentity(identity)
        identity.id = newId

        return newId
    }

    fun updateIdentity(identity: Identity) {
        dao.updateIdentity(identity)
    }

    suspend fun getIdentity(identityId: Long) = withContext(Dispatchers.IO) {
        dao.loadIdentity(identityId)
    }

    fun deleteIdentity(identity: Identity) {
        dao.deleteIdentity(identity)
    }

    suspend fun updateHabit(habit: Habit) = withContext(Dispatchers.IO) {
        dao.updateHabit(habit)
    }

    suspend fun getHabit(habitId: Long) = withContext(Dispatchers.IO) { dao.loadHabit(habitId) }

    suspend fun addHabit(habit: Habit): Long? = withContext(Dispatchers.IO) {
        dao.insertHabit(habit)
    }

    fun deleteHabit(habit: Habit) {
        dao.deleteHabit(habit)
    }

    fun addTask(task: Task): Long? {
        val newId = dao.insertTask(task)
        task.id = newId

        return newId
    }

    suspend fun updateTask(task: Task) = withContext(Dispatchers.IO) { dao.updateTask(task) }

    suspend fun getTask(taskId: Long) = withContext(Dispatchers.IO) { dao.loadTask(taskId) }

    suspend fun deleteTask(task: Task) = withContext(Dispatchers.IO) { dao.deleteTask(task) }

    suspend fun loadTasksForDay(day: DayOfWeek) = withContext(Dispatchers.IO) {
        dao.loadLiveTasksForDay(DaySelection.getDayMask(day))
    }

    suspend fun loadIdentityHabits() = withContext(Dispatchers.IO) {
        dao.loadIdentitiesWithHabits()
    }

    suspend fun loadOrphanHabits() = withContext(Dispatchers.IO) {
        dao.loadOrphanHabits()
    }

    suspend fun deleteHabitsForIdentity(identityId: Long) = withContext(Dispatchers.IO) {
        dao.deleteHabitsForIdentity(identityId)
    }

    suspend fun loadTasksAndResults(day: DayOfWeek) = withContext(Dispatchers.IO) {
        dao.loadTaskResultsForDay(DaySelection.getDayMask(day))
    }

    suspend fun loadTasksForHabit(habitId: Long) = withContext(Dispatchers.IO) {
        dao.loadTasksForHabit(habitId)
    }

    suspend fun upsertTasks(tasksToSave: List<Task>) = withContext(Dispatchers.IO) {
        dao.upsertTasks(tasksToSave)
    }

    companion object {
        private val allTypes = hashMapOf(
            "Academic" to R.drawable.ic_academic,
            "Artistic" to R.drawable.ic_artistic,
            "Family" to R.drawable.ic_family,
            "Financial" to R.drawable.ic_financial,
            "Friendship" to R.drawable.ic_friendship,
            "Wellness" to R.drawable.ic_health,
            "Mindset" to R.drawable.ic_mindset,
            VIEWDATA_STUB_TYPE to VIEWDATA_STUB_IMAGE,
            "Productivity" to R.drawable.ic_productivity,
            "Professional" to R.drawable.ic_professional,
            "Romantic" to R.drawable.ic_romantic
        )

        val identityTypes: List<String>
            get() = ArrayList(allTypes.keys.sorted())

        fun getTypeResourceId(type: String?): Int {
            return type?.let { allTypes[type] } ?: VIEWDATA_STUB_IMAGE
        }
    }
}