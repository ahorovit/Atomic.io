package com.drspaceman.atomicio.repository

import android.content.Context
import androidx.lifecycle.LiveData
import com.drspaceman.atomicio.R
import com.drspaceman.atomicio.db.AtomicIoDatabase
import com.drspaceman.atomicio.model.Agenda
import com.drspaceman.atomicio.model.Habit
import com.drspaceman.atomicio.model.Identity
import com.drspaceman.atomicio.model.Task
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.threeten.bp.LocalDate

class AtomicIoRepository(context: Context) {

    private var db = AtomicIoDatabase.getInstance(context)
    private var dao = db.atomicIoDao()

    val allIdentities: LiveData<List<Identity>>
        get() {
            return dao.loadAllIdentities()
        }

    val allHabits: LiveData<List<Habit>>
        get() {
            return dao.loadAllHabits()
        }

    private val allTypes = buildTypes()

    val identityTypes: List<String>
        get() {
            return ArrayList(allTypes.keys.sorted())
        }

    fun createIdentity(): Identity {
        return Identity()
    }

    fun addIdentity(identity: Identity): Long? {
        val newId = dao.insertIdentity(identity)
        identity.id = newId

        return newId
    }

    fun getLiveIdentity(identityId: Long): LiveData<Identity> {
        return dao.loadLiveIdentity(identityId)
    }

    fun updateIdentity(identity: Identity) {
        dao.updateIdentity(identity)
    }

    fun getIdentity(identityId: Long): Identity {
        return dao.loadIdentity(identityId)
    }

    fun deleteIdentity(identity: Identity) {
        dao.deleteIdentity(identity)
    }

    private fun buildTypes(): HashMap<String, Int> {
        return hashMapOf(
            "Academic" to R.drawable.ic_academic,
            "Artistic" to R.drawable.ic_artistic,
            "Family" to R.drawable.ic_family,
            "Financial" to R.drawable.ic_financial,
            "Friendship" to R.drawable.ic_friendship,
            "Wellness" to R.drawable.ic_health,
            "Mindset" to R.drawable.ic_mindset,
            "Other" to R.drawable.ic_other,
            "Productivity" to R.drawable.ic_productivity,
            "Professional" to R.drawable.ic_professional,
            "Romantic" to R.drawable.ic_romantic
        )
    }

    fun getTypeResourceId(type: String?): Int {
        return type?.let { allTypes[type] } ?: allTypes["Other"]!!
    }


    fun getLiveHabit(habitId: Long): LiveData<Habit> {
        return dao.loadLiveHabit(habitId)
    }

    fun updateHabit(habit: Habit) {
        dao.updateHabit(habit)
    }

    fun getHabit(habitId: Long): Habit {
        return dao.loadHabit(habitId)
    }

    fun createHabit(): Habit {
        return Habit()
    }

    fun addHabit(habit: Habit): Long? {
        val newId = dao.insertHabit(habit)
        habit.id = newId

        return newId
    }

    fun deleteHabit(habit: Habit) {
        dao.deleteHabit(habit)
    }


    fun createTask(): Task {
        return Task()
    }

    fun addTask(task: Task): Long? {
        val newId = dao.insertTask(task)
        task.id = newId

        return newId
    }

    fun getLiveTask(taskId: Long): LiveData<Task> {
        return dao.loadLiveTask(taskId)
    }

    suspend fun updateTask(task: Task) = withContext(Dispatchers.IO) { dao.updateTask(task) }

    suspend fun getTask(taskId: Long) = withContext(Dispatchers.IO) { dao.loadTask(taskId) }

    suspend fun deleteTask(task: Task) = withContext(Dispatchers.IO) { dao.deleteTask(task) }

    suspend fun getAgendaForDate(date: LocalDate) = withContext(Dispatchers.IO) {
        dao.getAgenda(date) ?: createAgendaForDate(date)
    }

    private suspend fun createAgendaForDate(date: LocalDate): Agenda {
        val agenda = Agenda(date = date)
        agenda.id = dao.insertAgenda(agenda)
        return agenda
    }

    suspend fun getTasksForAgenda(agendaId: Long) = withContext(Dispatchers.IO) {
        dao.getTasksForAgenda(agendaId)
    }
}