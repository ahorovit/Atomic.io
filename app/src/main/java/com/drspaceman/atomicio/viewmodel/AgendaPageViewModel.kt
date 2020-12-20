package com.drspaceman.atomicio.viewmodel

import android.text.Editable
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.drspaceman.atomicio.db.AtomicIoDao.TaskAndResult
import com.drspaceman.atomicio.model.Task
import com.drspaceman.atomicio.model.TaskResultViewData
import com.drspaceman.atomicio.repository.AtomicIoRepository
import com.drspaceman.atomicio.viewstate.AgendaViewState
import com.drspaceman.atomicio.viewstate.ChecklistViewLoaded
import com.drspaceman.atomicio.viewstate.DayViewLoaded
import com.drspaceman.atomicio.viewstate.AgendaLoading
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalTime
import org.threeten.bp.format.DateTimeFormatter

class AgendaPageViewModel
@ViewModelInject
constructor(
    atomicIoRepo: AtomicIoRepository,
    private val habitsDelegate: HabitsDelegate,
) : BaseViewModel(atomicIoRepo), HabitsViewModelInterface by habitsDelegate {

    private val displayedDate = MutableLiveData<LocalDate>(LocalDate.now()) // TODO push date from UI when selecting tab

    private val agendaItems = MediatorLiveData<List<TaskResultViewData>>().apply {
        value = listOf()

        addSource(displayedDate) {
            viewModelScope.launch {
                val tasksAndResults = atomicIoRepo.loadTasksAndResults(it.dayOfWeek)
                val items = getAgendaItems(tasksAndResults)
                value = items
            }
        }
    }

    private suspend fun getAgendaItems(tasksAndResults: List<TaskAndResult>) =
        withContext(Dispatchers.Default) {
            val items = mutableListOf<TaskResultViewData>()

            tasksAndResults.forEach {

            }

            items
        }


    private lateinit var tasks: LiveData<List<Task>>

    private val _viewState = MediatorLiveData<AgendaViewState>().apply {
        value = AgendaLoading

        viewModelScope.launch {
            tasks = atomicIoRepo.loadTasksForDay(LocalDate.now().dayOfWeek)

            addSource(tasks) { repoTasks ->
                refreshViewState(repoTasks.map { TaskViewData.of(it) })
            }
        }
    }

    val viewState: LiveData<AgendaViewState>
        get() = _viewState

    private fun refreshViewState(tasks: List<TaskViewData>) {
        val state = _viewState.value!!

        _viewState.value = when (state) {
            AgendaLoading -> getDefaultViewState(tasks)
            is DayViewLoaded -> state.copy(tasks = tasks)
            is ChecklistViewLoaded -> state.copy(tasks = tasks)
        }
    }

    // TODO: access Settings for preferred view
    private fun getDefaultViewState(tasks: List<TaskViewData>): AgendaViewState {
        return DayViewLoaded(tasks)
    }

    fun toggleAgendaView() {
        val state = _viewState.value!!

        _viewState.value = when (state) {
            AgendaLoading -> AgendaLoading
            is DayViewLoaded -> ChecklistViewLoaded(state.tasks)
            is ChecklistViewLoaded -> DayViewLoaded(state.tasks)
        }
    }

    // @todo: move to TaskDetailsViewModel
    data class TaskViewData(
        override var id: Long? = null,
        var habitId: Long? = null,
        var title: String? = "",
        var startTime: LocalTime? = null,
        var duration: Int? = null
    ) : BaseViewData() {
        override fun toString() = title ?: ""

        override fun toModel() = Task(
            id,
            habitId,
            title,
            startTime,
            duration
        )

        fun setStartTime(pickedTime: String?) {
            pickedTime?.let {
                startTime = LocalTime.parse(it)
            }
        }

        fun getStartTime(): String {
            var timeString = ""

            startTime?.let {
                timeString = it.format(DateTimeFormatter.ofPattern("hh:mm a"))
                if (timeString[0] == '0') {
                    timeString = timeString.substring(1)
                }
            }

            return timeString
        }

        fun getDuration(): String {
            return duration?.toString() ?: ""
        }

        fun setDuration(durationString: String) {
            duration = durationString.toIntOrNull()
        }

        companion object {
            fun of(task: Task) = TaskViewData(
                task.id,
                task.habitId,
                task.name,
                task.startTime,
                task.duration
            )
        }
    }
}
