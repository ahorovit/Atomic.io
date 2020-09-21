package com.drspaceman.atomicio.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.drspaceman.atomicio.R
import com.drspaceman.atomicio.model.Habit
import com.drspaceman.atomicio.repository.AtomicIoRepository
import com.drspaceman.atomicio.ui.BaseDialogFragment.SpinnerItemViewData
import com.drspaceman.atomicio.ui.BaseDialogFragment.SpinnerViewModel
import com.drspaceman.atomicio.viewmodel.IdentityPageViewModel.IdentityViewData
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class HabitPageViewModel(application: Application)
    : BaseViewModel(application), SpinnerViewModel {

    // @TODO: insert as Explicit Dependency
    private var oneHabit: LiveData<HabitViewData>? = null
    private var allHabits: LiveData<List<HabitViewData>>? = null

    private var allIdentities: LiveData<List<IdentityViewData>>? = null

    /**
     * Habit Spinner is a list of parent Identities, so we need to observe with
     * LiveData
     */
    fun getSpinnerItems(): LiveData<List<IdentityViewData>>?
    {
        if (allIdentities == null) {
            mapIdentitiesToIdentityViews()
        }

        return allIdentities
    }

    // @todo: this is duplicated from IdentityPageViewModel
    private fun mapIdentitiesToIdentityViews() {
        allIdentities = Transformations.map(atomicIoRepo.allIdentities) { repoIdentities ->
            repoIdentities.map { IdentityViewData.of(it) }
        }
    }

    override fun getSpinnerItemResourceId(type: String?): Int? {
        TODO("Not yet implemented")
    }

    fun getHabit(habitId: Long): LiveData<HabitViewData>? {
        if (oneHabit == null) {
            mapHabitToHabitViewData(habitId)
        }

        return oneHabit
    }

    private fun mapHabitToHabitViewData(habitId: Long) {
        val habit = atomicIoRepo.getLiveHabit(habitId)
        oneHabit = Transformations.map(habit) { repoHabit ->
            repoHabit?.let {
                HabitViewData(
                    repoHabit.id,
                    repoHabit.identityId,
                    repoHabit.name,
                    repoHabit.type
                )
            }
        }
    }

    fun getHabits(): LiveData<List<HabitViewData>>? {
        if (allHabits == null) {
            mapAllHabitsToHabitViewData()
        }

        return allHabits
    }

    private fun mapAllHabitsToHabitViewData() {
        allHabits = Transformations.map(atomicIoRepo.allHabits) { repoHabits ->
            repoHabits.map { HabitViewData.of(it) }
        }
    }

    fun getNewHabitView(): HabitViewData {
        return HabitViewData()
    }

    fun updateHabit(habitViewData: HabitViewData) {
        GlobalScope.launch {
            val habit = habitViewData.toModel()
            atomicIoRepo.updateHabit(habit)
        }
    }

    fun insertHabit(habitViewData: HabitViewData) {
        val habit = habitViewData.toModel()

        GlobalScope.launch {
            val habitId = atomicIoRepo.addHabit(habit)

            habitId?.let {
                mapHabitToHabitViewData(it)
            }
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