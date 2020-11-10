package com.drspaceman.atomicio.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.drspaceman.atomicio.repository.AtomicIoRepository
import com.drspaceman.atomicio.viewmodel.AgendaPageViewModel.TaskViewData
import com.drspaceman.atomicio.viewmodel.BaseViewModel.ViewDataStub.Companion.VIEWDATA_STUB_ID
import com.drspaceman.atomicio.viewmodel.HabitPageViewModel.HabitViewData
import com.drspaceman.atomicio.viewstate.TaskLoaded
import com.drspaceman.atomicio.viewstate.TaskLoading
import com.drspaceman.atomicio.viewstate.TaskViewState
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class TaskDetailsViewModel
@ViewModelInject
constructor(
    atomicIoRepo: AtomicIoRepository,
    private val identitiesDelegate: IdentitiesDelegate,
    private val habitsDelegate: HabitsDelegate
) : BaseViewModel(atomicIoRepo),
    HabitsViewModelInterface by habitsDelegate,
    IdentitiesViewModelInterface by identitiesDelegate {

    private val _viewState = MediatorLiveData<TaskViewState>()
    val viewState: LiveData<TaskViewState>
        get() = _viewState

    private val task = MutableLiveData(TaskViewData())

    private var selectedIdentity = MutableLiveData<Long?>(null)
    private var selectedHabit: Long? = null

    private val visibleHabits = MediatorLiveData<List<HabitViewData>>()

    init {
        _viewState.value = TaskLoading
        visibleHabits.value = listOf()

        visibleHabits.addSource(habits) {
            refreshVisibleHabits()
        }

        visibleHabits.addSource(selectedIdentity) {
            refreshVisibleHabits()
        }

        _viewState.addSource(task) {
            setSelectedHabit(it.habitId)
            refreshViewState()
        }

        _viewState.addSource(visibleHabits) {
            refreshViewState()
        }

        _viewState.addSource(identities) {
            refreshViewState()
        }
    }

    private fun refreshViewState() {
        if (!habitsDelegate.isLoaded || !identitiesDelegate.isLoaded) {
            return
        }

        _viewState.value = TaskLoaded(
            task.value ?: TaskViewData(),
            identities.value ?: listOf(),
            selectedIdentity.value ?: VIEWDATA_STUB_ID,
            visibleHabits.value ?: listOf(),
            selectedHabit ?: VIEWDATA_STUB_ID,
        )
    }

    private fun refreshVisibleHabits() {
        visibleHabits.value = if (selectedIdentity.value == null) {
            habits.value
        } else {
            habits.value!!.filter { it.identityId == selectedIdentity.value }
        }
    }

    fun loadTask(taskId: Long) = viewModelScope.launch {
        task.value = TaskViewData.of(atomicIoRepo.getTask(taskId))
    }

    override fun clearItem() {
        task.value = TaskViewData()
    }

    fun updateTask(writeTaskView: TaskViewData) {
        GlobalScope.launch {
            atomicIoRepo.updateTask(writeTaskView.toModel())
        }
    }

    fun insertTask(newTaskViewData: TaskViewData) {
        val task = newTaskViewData.toModel()
//        task.agendaId = agenda.id

        GlobalScope.launch {
            atomicIoRepo.addTask(task)
        }
    }

    override fun deleteItem(itemViewData: BaseViewData) {
        GlobalScope.launch {
            val task = (itemViewData as TaskViewData).toModel()
            atomicIoRepo.deleteTask(task)
        }
    }

    fun setParentHabit(habitId: Long) {
        task.value = task.value?.copy(habitId = habitId)
    }

    fun setSelectedIdentity(identityId: Long?) {
        when (identityId) {
            selectedIdentity.value -> return
            VIEWDATA_STUB_ID -> {
                selectedHabit = null
                selectedIdentity.value = null
            }
            else -> {
                selectedHabit = null
                selectedIdentity.value = identityId
            }
        }
    }

    fun setSelectedHabit(habitId: Long?) {
        when (habitId) {
            selectedHabit -> return
            null -> {
                selectedHabit = null
            }
            VIEWDATA_STUB_ID -> {
                selectedHabit = null
            }
            else -> {
                val identityId = habits.value?.first { it.id == habitId }?.identityId
                selectedHabit = habitId
                setSelectedIdentity(identityId)
            }
        }
    }

    fun getNewTaskView(): TaskViewData {
        return TaskViewData()
    }
}