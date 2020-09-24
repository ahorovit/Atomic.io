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
    private val _habit = MutableLiveData<HabitViewData>()
    val habit
        get() = _habit

    init {
        _habit.value = getNewHabitViewData()
    }

    fun loadHabit(habitId: Long) {
        viewModelScope.launch {
            _habit.value = HabitViewData.of(atomicIoRepo.getHabit(habitId))
        }
    }

    /**
     * Habit Spinner is a list of parent Identities, so we need to observe with
     * LiveData
     */
    fun getSpinnerItems(): LiveData<List<IdentityViewData>> {
        return identitiesDelegate.identities
    }

    override fun getSpinnerItemResourceId(type: String?): Int? {
        TODO("Not yet implemented")
    }

    fun getNewHabitViewData() = HabitViewData()

    fun updateHabit(habitViewData: HabitViewData) {
        GlobalScope.launch {
            val habit = habitViewData.toModel()
            atomicIoRepo.updateHabit(habit)
        }
    }

    fun insertHabit(habitViewData: HabitViewData) {
        GlobalScope.launch {
            atomicIoRepo.addHabit(habitViewData.toModel())
        }
    }

    override fun deleteItem(itemViewData: BaseViewData) {
        GlobalScope.launch {
            val habit = (itemViewData as HabitViewData).toModel()
            atomicIoRepo.deleteHabit(habit)
        }
    }

    data class HabitViewData(
        override var id: Long? = null,
        var identityId: Long? = null,
        var name: String? = "",
        override var type: String? = "",
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