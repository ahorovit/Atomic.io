package com.drspaceman.atomicio.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.drspaceman.atomicio.db.AtomicIoDao.IdentityHabit
import com.drspaceman.atomicio.model.Habit
import com.drspaceman.atomicio.model.Identity
import com.drspaceman.atomicio.repository.AtomicIoRepository
import com.drspaceman.atomicio.ui.BaseDialogFragment.SpinnerItemViewData
import com.drspaceman.atomicio.ui.IdentityPageFragment
import com.drspaceman.atomicio.viewmodel.BaseViewModel.ViewDataStub.Companion.VIEWDATA_STUB_IMAGE
import com.drspaceman.atomicio.viewmodel.BaseViewModel.ViewDataStub.Companion.VIEWDATA_STUB_TYPE
import com.drspaceman.atomicio.viewmodel.HabitPageViewModel.HabitViewData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class IdentityPageViewModel
@ViewModelInject
constructor(
    atomicIoRepo: AtomicIoRepository,
    val identitiesDelegate: IdentitiesDelegate,
    val spinnerDelegate: SpinnerDelegate
) : BaseViewModel(atomicIoRepo),
    IdentitiesViewModelInterface by identitiesDelegate,
    SpinnerViewModelInterface by spinnerDelegate {

    /**
     * Tracks DB: first = Identities + Child Habits, second: Habits with no parent Identity
     */
    private val _identityHabits = MediatorLiveData<Pair<List<IdentityHabit>,List<Habit>>>()

    /**
     * Translates _identityHabits into one usable list for expandable list of identites
     */
    val identityHabits = _identityHabits.switchMap {
        liveData(context = viewModelScope.coroutineContext + Dispatchers.Default) {
            val allIdentityHabits = mutableListOf<IdentityWithHabitsViewData>()

            allIdentityHabits.addAll(toIdentityWithHabitViews(it.first))

            if(it.second.isNotEmpty()) {
                allIdentityHabits.add(toMiscIdentityWithHabitViews(it.second))
            }

            emit(allIdentityHabits)
        }
    }

    // TODO: move into apply{} block(s)
    init {
        _identityHabits.value = Pair(listOf(),listOf())

        viewModelScope.launch {
            _identityHabits.addSource(atomicIoRepo.loadIdentityHabits()) {
                _identityHabits.value = _identityHabits.value?.copy(first = it)
            }

            _identityHabits.addSource(atomicIoRepo.loadOrphanHabits()) {
                _identityHabits.value = _identityHabits.value?.copy(second = it)
            }
        }
    }

    // @todo: remove
    private val _identity = MutableLiveData<IdentityViewData>()
    val identity: LiveData<IdentityViewData>
        get() = _identity

    /**
     * Helps view track which Identity is expanded
     */
    private val _expandedIdentityId = MutableLiveData<Long?>()
    val expandedIdentityId: LiveData<Long?>
        get() = _expandedIdentityId

    fun setExpandedIdentityId(expandedId: Long?) {
        _expandedIdentityId.value = expandedId
    }

    fun clearExpandedIdentityId() {
        _expandedIdentityId.value = null
    }

    /**
     * translates list of Identity/Habit JOIN rows into Object holding IdentityViewData+List<HabitViewData>
     */
    private suspend fun toIdentityWithHabitViews(identityHabits: List<IdentityHabit>) =
        withContext(Dispatchers.Default) {
            val result = mutableListOf<IdentityWithHabitsViewData>()

            var identityViewData: IdentityViewData? = null
            var habitList = mutableListOf<HabitViewData>()

            identityHabits.forEach { identityHabit ->
                if (identityHabit.identityId != identityViewData?.id) {
                    // Store previous item
                    identityViewData?.let { result.add(IdentityWithHabitsViewData(it, habitList)) }

                    identityViewData = IdentityViewData(
                        identityHabit.identityId,
                        identityHabit.identityName,
                        null,
                        identityHabit.identityType,
                        AtomicIoRepository.getTypeResourceId(identityHabit.identityType)
                    )

                    habitList = mutableListOf()
                }

                if (identityHabit.habitId != null) {
                    habitList.add(
                        HabitViewData(
                            identityHabit.habitId,
                            identityHabit.identityId,
                            identityHabit.habitName,
                            identityHabit.identityType,
                            AtomicIoRepository.getTypeResourceId(identityHabit.identityType)
                        )
                    )
                }
            }

            // store last item
            identityViewData?.let { result.add(IdentityWithHabitsViewData(it, habitList)) }

            result
        }

    /**
     * translates list of Habits with no parent Identity into "Misc Habits" IdentityViewData+List<HabitViewData>
     */
    private suspend fun toMiscIdentityWithHabitViews(orphanHabits: List<Habit>) =
        withContext(Dispatchers.Default) {
                IdentityWithHabitsViewData(
                    IdentityViewData(
                        IdentityPageFragment.MISC_HABITS_ID,
                        "Misc Habits",
                        null,
                        VIEWDATA_STUB_TYPE
                    ),
                    orphanHabits.map { HabitViewData.of(it) }
                )
        }

    /**
     * Supports Expandable List of Identity (Level1) with child Habits (Level2)
     */
    data class IdentityWithHabitsViewData(
        val identity: IdentityViewData,
        val habits: List<HabitViewData>
    )

    data class IdentityViewData(
        override var id: Long? = null,
        var name: String? = "",
        var description: String? = "",
        override var type: String? = VIEWDATA_STUB_TYPE,
        override var typeResourceId: Int = VIEWDATA_STUB_IMAGE
    ) : BaseViewData(), SpinnerItemViewData {

        override fun toString(): String {
            return name ?: ""
        }

        override fun toModel() = Identity(
            id,
            name,
            type,
            description
        )

        companion object {
            fun of(identity: Identity) = IdentityViewData(
                identity.id,
                identity.name,
                identity.description,
                identity.type,
                AtomicIoRepository.getTypeResourceId(identity.type)
            )
        }
    }
}