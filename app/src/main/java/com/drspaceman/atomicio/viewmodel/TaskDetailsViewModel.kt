package com.drspaceman.atomicio.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.drspaceman.atomicio.isNull
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

    private val task = MutableLiveData(TaskViewData())

    private var selectedIdentity = MutableLiveData<Long?>(null)
    private var selectedHabit: Long? = null

    private val visibleHabits = MediatorLiveData<List<HabitViewData>>().apply {
        value = listOf()

        addSource(habits) {
            refreshVisibleHabits()
        }

        addSource(selectedIdentity) {
            refreshVisibleHabits()
        }
    }

    private val _viewState = MediatorLiveData<TaskViewState>().apply {
        value = TaskLoading

//        addSource(task) {
//            setSelectedHabit(it.habitId)
//            refreshViewState()
//        }

        addSource(visibleHabits) {
            refreshViewState()
        }

        addSource(identities) {
            refreshViewState()
        }
    }

    val viewState: LiveData<TaskViewState>
        get() = _viewState

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
            habits.value?.filter { it.identityId == selectedIdentity.value }
        }
    }

    fun loadTask(taskId: Long) = viewModelScope.launch {
        val loadedTask = TaskViewData.of(atomicIoRepo.getTask(taskId))
        task.value = loadedTask
        setSelectedHabit(loadedTask.habitId)
    }

    override fun clearContext() {
        selectedHabit = null
        selectedIdentity.value = null
        task.value = TaskViewData()
    }

    // TODO: bind isValid to activate/deactivate CRUD buttons
    fun isValid(): Boolean {
        task.value?.let{
            return !(it.title.isNullOrEmpty()
                    || it.startTime.isNull()
                    || it.duration.isNull())
        }

        return false
    }

    fun saveItem() {
        task.value?.let {
            GlobalScope.launch {
                if (it.id == null) {
                    atomicIoRepo.addTask(it.toModel())
                } else {
                    atomicIoRepo.updateTask(it.toModel())
                }
            }
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

    fun setSelectedIdentity(identityId: Long?, propagate: Boolean = true) {
        when (identityId) {
            selectedIdentity.value -> return
            VIEWDATA_STUB_ID -> {
                if (propagate) setSelectedHabit(null, false)
                selectedIdentity.value = null
            }
            else -> {
                if (propagate) setSelectedHabit(null, false)
                selectedIdentity.value = identityId
            }
        }
    }

    fun setSelectedHabit(habitId: Long?, propagate: Boolean = true) {
        when (habitId) {
            selectedHabit -> return
            null -> { selectedHabit = null }
            VIEWDATA_STUB_ID -> { selectedHabit = null }
            else -> {
                val identityId = habits.value?.first { it.id == habitId }?.identityId
                selectedHabit = habitId

                if (propagate) {
                    setSelectedIdentity(identityId, false)
                }
            }
        }
    }

    fun getNewTaskView(): TaskViewData {
        return TaskViewData()
    }
}