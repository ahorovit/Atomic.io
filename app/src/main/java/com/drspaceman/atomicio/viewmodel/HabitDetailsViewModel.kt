package com.drspaceman.atomicio.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.drspaceman.atomicio.repository.AtomicIoRepository
import com.drspaceman.atomicio.viewstate.HabitLoaded
import com.drspaceman.atomicio.viewstate.HabitViewState
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class HabitDetailsViewModel
@ViewModelInject
constructor(
    atomicIoRepo: AtomicIoRepository,
    private val identitiesDelegate: IdentitiesDelegate,
    private val spinnerDelegate: SpinnerDelegate
) : BaseViewModel(atomicIoRepo),
    IdentitiesViewModelInterface by identitiesDelegate,
    SpinnerViewModelInterface by spinnerDelegate {

    private val _habit = MutableLiveData(HabitPageViewModel.HabitViewData())

    private val _newHabitId = MutableLiveData<Long?>()

    private val _viewState = MediatorLiveData<HabitViewState>().apply {
        addSource(_habit) {
            value = HabitLoaded(it)
        }
    }

    val viewState: LiveData<HabitViewState>
        get() = _viewState

    /**
     * Habit Spinner is a list of parent Identities, so we need to observe with
     * LiveData
     */
    fun getSpinnerItems(): LiveData<List<IdentityPageViewModel.IdentityViewData>> {
        return identitiesDelegate.identities
    }

    fun getHabit(habitId: Long?, identityId: Long?): LiveData<HabitPageViewModel.HabitViewData> {
        if (habitId != null) {
            viewModelScope.launch {
                _habit.value = HabitPageViewModel.HabitViewData.of(atomicIoRepo.getHabit(habitId))
            }
        } else if (identityId != null) {
            _habit.value = HabitPageViewModel.HabitViewData(identityId = identityId)
        }

        return _habit
    }

    override fun deleteItem(itemViewData: BaseViewData) {
        GlobalScope.launch {
            val habit = (itemViewData as HabitPageViewModel.HabitViewData).toModel()
            atomicIoRepo.deleteHabit(habit)
        }
    }

    override fun clearContext() {
        _habit.value = HabitPageViewModel.HabitViewData()
        _newHabitId.value = null
    }

    private fun updateHabit(habitViewData: HabitPageViewModel.HabitViewData) {
        GlobalScope.launch {
            val habit = habitViewData.toModel()
            atomicIoRepo.updateHabit(habit)
        }
    }

    fun saveHabitForReturnId(habitViewData: HabitPageViewModel.HabitViewData): LiveData<Long?> {
        viewModelScope.launch {
            _newHabitId.value = atomicIoRepo.addHabit(habitViewData.toModel())
        }

        return _newHabitId
    }

    private fun insertHabit(habitViewData: HabitPageViewModel.HabitViewData) {
        GlobalScope.launch {
            atomicIoRepo.addHabit(habitViewData.toModel())
        }
    }

    fun saveHabit(habitViewData: HabitPageViewModel.HabitViewData) {
        if (habitViewData.id != null) {
            updateHabit(habitViewData)
        } else {
            insertHabit(habitViewData)
        }
    }
}