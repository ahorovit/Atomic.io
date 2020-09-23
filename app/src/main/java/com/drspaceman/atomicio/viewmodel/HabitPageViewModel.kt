package com.drspaceman.atomicio.viewmodel

import android.app.Application
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
    private val spinnerDelegate: SpinnerDelegate
) : BaseViewModel(atomicIoRepo),
    IdentitiesViewModelInterface by identitiesDelegate,
    SpinnerViewModelInterface by spinnerDelegate
{
    private val _habits = MediatorLiveData<List<HabitViewData>>()
    val habits: LiveData<List<HabitViewData>>
        get() = _habits

    private val _habit = MutableLiveData<HabitViewData>()
    val habit
        get() = _habit

    init {

        _habit.value = HabitViewData()

        viewModelScope.launch {
            _habits.addSource(Transformations.map(atomicIoRepo.allHabits) { repoHabits ->
                repoHabits.map { habit ->
                    HabitViewData.of(habit)
                }
            }) { habitViewData -> _habits.value = habitViewData }
        }
    }


//    private var allIdentities: LiveData<List<IdentityViewData>>? = null


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
//        if (allIdentities == null) {
//            mapIdentitiesToIdentityViews()
//        }

        return identitiesDelegate.identities
    }
//
//    // @todo: this is duplicated from IdentityPageViewModel
//    private fun mapIdentitiesToIdentityViews() {
//        allIdentities = Transformations.map(atomicIoRepo.allIdentities) { repoIdentities ->
//            repoIdentities.map { IdentityViewData.of(it) }
//        }
//    }

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