package com.drspaceman.atomicio.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.drspaceman.atomicio.R
import com.drspaceman.atomicio.db.AtomicIoDao
import com.drspaceman.atomicio.db.AtomicIoDao.IdentityHabit
import com.drspaceman.atomicio.model.Identity
import com.drspaceman.atomicio.repository.AtomicIoRepository
import com.drspaceman.atomicio.ui.BaseDialogFragment.SpinnerItemViewData
import com.drspaceman.atomicio.viewmodel.HabitPageViewModel.HabitViewData
import com.xwray.groupie.ExpandableGroup
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class IdentityPageViewModel
@ViewModelInject
constructor(
    atomicIoRepo: AtomicIoRepository,
    private val identitiesDelegate: IdentitiesDelegate,
    private val spinnerDelegate: SpinnerDelegate
) : BaseViewModel(atomicIoRepo),
    IdentitiesViewModelInterface by identitiesDelegate,
    SpinnerViewModelInterface by spinnerDelegate {

    private val _identityHabits = MediatorLiveData<List<IdentityHabit>>()
    val identityHabits = _identityHabits.switchMap {
        liveData(context = viewModelScope.coroutineContext + Dispatchers.Default) {
            emit(toIdentityWithHabitViews(it))
        }
    }

    private val _identity = MutableLiveData<IdentityViewData>()
    val identity: LiveData<IdentityViewData>
        get() = _identity

    init {
        viewModelScope.launch {

            _identityHabits.addSource(atomicIoRepo.loadIdentityHabits()) {
                _identityHabits.value = it
            }

//
//            val identityHabits = liveData(context = viewModelScope.coroutineContext) {
//
//
//
//                val live = atomicIoRepo.loadIdentityHabits().distinctUntilChanged()
//                emit(toIdentityWithHabitViews(live))
//            }


//            val identityHabitViewData: LiveData<List<IdentityWithHabitsViewData>> = liveData {
//                val result = toIdentityWithHabitViews(identityHabits)
//
//                emit()
//            }


//            val live = Transformations.map(atomicIoRepo.loadIdentityHabits()) {
//                toIdentityWithHabitViews(it)
//            }
//
//
//            _identityHabits.addSource(live) { _identityHabits.value = it }
        }
    }

    private suspend fun toIdentityWithHabitViews(identityHabits: List<IdentityHabit>) =
        withContext(Dispatchers.IO) {
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

                if (identityHabit.habitId != null)
                {
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

            result as List<IdentityWithHabitsViewData>
        }


    fun loadIdentity(identityId: Long) {
        viewModelScope.launch {
            _identity.value = IdentityViewData.of(atomicIoRepo.getIdentity(identityId))
        }
    }

    override fun clearItem() {
        _identity.value = getNewIdentityView()
    }

    fun getNewIdentityView() = IdentityViewData(type = "Other")

    fun insertIdentity(newIdentityView: IdentityViewData) {
        GlobalScope.launch {
            atomicIoRepo.addIdentity(newIdentityView.toModel())
        }
    }

    fun updateIdentity(identityViewData: IdentityViewData) {
        GlobalScope.launch {
            atomicIoRepo.updateIdentity(identityViewData.toModel())
        }
    }

    override fun deleteItem(itemViewData: BaseViewData) {
        GlobalScope.launch {
            atomicIoRepo.deleteIdentity((itemViewData as IdentityViewData).toModel())
        }
    }

    /**
     * Identity spinner is a simple list of identity types (strings)
     * @todo: move to Delegate?
     */
    fun getSpinnerItems(): List<String> {
        return AtomicIoRepository.identityTypes
    }

    data class IdentityWithHabitsViewData(
        val identity: IdentityViewData,
        val habits: List<HabitViewData>
    )

    data class IdentityViewData(
        override var id: Long? = null,
        var name: String? = "",
        var description: String? = "",
        override var type: String? = "",
        override var typeResourceId: Int = R.drawable.ic_other
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