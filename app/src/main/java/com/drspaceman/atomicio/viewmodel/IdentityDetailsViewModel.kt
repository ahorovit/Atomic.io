package com.drspaceman.atomicio.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.drspaceman.atomicio.repository.AtomicIoRepository
import com.drspaceman.atomicio.viewmodel.IdentityPageViewModel.IdentityViewData
import com.drspaceman.atomicio.viewstate.IdentityLoaded
import com.drspaceman.atomicio.viewstate.IdentityLoading
import com.drspaceman.atomicio.viewstate.IdentityViewState
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class IdentityDetailsViewModel
@ViewModelInject
constructor(
    atomicIoRepo: AtomicIoRepository,
    private val spinnerDelegate: SpinnerDelegate
) : BaseViewModel(atomicIoRepo),
    SpinnerViewModelInterface by spinnerDelegate {

    private val identity = MutableLiveData(IdentityViewData())

    // TODO ViewState is a bit overkill for this dialog
    private val _viewState = MediatorLiveData<IdentityViewState>().apply {
        addSource(identity) {
            value = IdentityLoaded(it)
        }
    }

    val viewState: LiveData<IdentityViewState>
        get() = _viewState


    fun loadIdentity(identityId: Long) {
        viewModelScope.launch {
            identity.value = IdentityViewData.of(atomicIoRepo.getIdentity(identityId))
        }
    }

    override fun clearContext() {
        identity.value = getNewIdentityView()
    }

    fun getNewIdentityView() =
        IdentityViewData(type = ViewDataStub.VIEWDATA_STUB_TYPE)


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
        itemViewData.id?.let {
            GlobalScope.launch {
                atomicIoRepo.deleteIdentity((itemViewData as IdentityViewData).toModel())
            }
        }
    }

    /**
     * Identity spinner is a simple list of identity types (strings)
     * @todo: move to Delegate?
     */
    fun getSpinnerItems(): List<String> {
        return AtomicIoRepository.identityTypes
    }

    fun deleteIdentityAndHabits(identityViewData: IdentityViewData) {
        identityViewData.id?.let {
            GlobalScope.launch {
                atomicIoRepo.deleteHabitsForIdentity(it)
                deleteItem(identityViewData)
            }
        }
    }

}