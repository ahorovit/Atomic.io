package com.drspaceman.atomicio.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import com.drspaceman.atomicio.model.Habit
import com.drspaceman.atomicio.repository.AtomicIoRepository
import com.drspaceman.atomicio.ui.BaseDialogFragment.SpinnerItemViewData
import com.drspaceman.atomicio.viewmodel.BaseViewModel.ViewDataStub.Companion.VIEWDATA_STUB_IMAGE
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

// TODO: remove HAbit Page entirely
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
    // @todo: remove or move to SpinnerDelegate
    override fun getSpinnerItemResourceId(type: String?): Int? {
        TODO("Not yet implemented")
    }

    override fun clearContext() {
    }

    // TODO remove
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
        override var type: String? = ViewDataStub.VIEWDATA_STUB_TYPE,
        override var typeResourceId: Int = VIEWDATA_STUB_IMAGE
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