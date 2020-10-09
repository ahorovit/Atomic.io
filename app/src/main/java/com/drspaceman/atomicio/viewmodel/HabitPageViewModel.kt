package com.drspaceman.atomicio.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.drspaceman.atomicio.R
import com.drspaceman.atomicio.model.Habit
import com.drspaceman.atomicio.repository.AtomicIoRepository
import com.drspaceman.atomicio.ui.BaseDialogFragment.SpinnerItemViewData
import com.drspaceman.atomicio.viewmodel.IdentityPageViewModel.IdentityViewData
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class HabitPageViewModel
@ViewModelInject
constructor(
    atomicIoRepo: AtomicIoRepository,
    private val identitiesDelegate: IdentitiesDelegate,
    private val habitsDelegate: HabitsDelegate,
    private val spinnerDelegate: SpinnerDelegate
) : BaseViewModel(atomicIoRepo),
    IdentitiesViewModelInterface by identitiesDelegate,
    HabitsViewModelInterface by habitsDelegate,
    SpinnerViewModelInterface by spinnerDelegate
{
    private val _habit = MutableLiveData(HabitViewData())

    fun getHabit(habitId: Long?, identityId: Long?): LiveData<HabitViewData> {
        if (habitId != null) {
            viewModelScope.launch {
                _habit.value = HabitViewData.of(atomicIoRepo.getHabit(habitId))
            }
        } else if (identityId != null) {
            _habit.value = HabitViewData(identityId = identityId)
        }

        return _habit
    }

    private val _newHabitId = MutableLiveData<Long?>()

    /**
     * Habit Spinner is a list of parent Identities, so we need to observe with
     * LiveData
     */
    fun getSpinnerItems(): LiveData<List<IdentityViewData>> {
        return identitiesDelegate.identities
    }

    // @todo: remove or move to SpinnerDelegate
    override fun getSpinnerItemResourceId(type: String?): Int? {
        TODO("Not yet implemented")
    }

    override fun clearItem() {
        _habit.value = HabitViewData()
        _newHabitId.value = null
    }

    override fun deleteItem(itemViewData: BaseViewData) {
        GlobalScope.launch {
            val habit = (itemViewData as HabitViewData).toModel()
            atomicIoRepo.deleteHabit(habit)
        }
    }

    fun saveHabit(habitViewData: HabitViewData) {
        if (habitViewData.id != null) {
            updateHabit(habitViewData)
        } else {
            insertHabit(habitViewData)
        }
    }

    fun saveHabitForReturnId(habitViewData: HabitViewData): LiveData<Long?> {
        viewModelScope.launch {
            _newHabitId.value = atomicIoRepo.addHabit(habitViewData.toModel())
        }

        return _newHabitId
    }

    private fun updateHabit(habitViewData: HabitViewData) {
        GlobalScope.launch {
            val habit = habitViewData.toModel()
            atomicIoRepo.updateHabit(habit)
        }
    }

    private fun insertHabit(habitViewData: HabitViewData) {
        GlobalScope.launch {
            atomicIoRepo.addHabit(habitViewData.toModel())
        }
    }

    data class HabitViewData(
        override var id: Long? = null,
        var identityId: Long? = null,
        var name: String? = "",
        override var type: String? = "Other",
        override var typeResourceId: Int = R.drawable.ic_other
    ) : BaseViewData(), SpinnerItemViewData {
        override fun toString(): String {
            return name ?: ""
        }

        override fun toModel() = Habit(
            id,
            identityId,
            name,
            type
        )

        companion object {
            fun of(habit: Habit) = HabitViewData(
                habit.id,
                habit.identityId,
                habit.name,
                habit.type,
                AtomicIoRepository.getTypeResourceId(habit.type)
            )
        }
    }
}