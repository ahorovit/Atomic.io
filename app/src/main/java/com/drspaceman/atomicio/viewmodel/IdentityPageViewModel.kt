package com.drspaceman.atomicio.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.drspaceman.atomicio.R
import com.drspaceman.atomicio.model.Identity
import com.drspaceman.atomicio.repository.AtomicIoRepository
import com.drspaceman.atomicio.ui.BaseDialogFragment.SpinnerItemViewData
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class IdentityPageViewModel
    @ViewModelInject
    constructor(
        atomicIoRepo: AtomicIoRepository,
        private val identitiesDelegate: IdentitiesDelegate,
        private val spinnerDelegate: SpinnerDelegate
    ) : BaseViewModel(atomicIoRepo),
    IdentitiesViewModelInterface by identitiesDelegate,
    SpinnerViewModelInterface by spinnerDelegate
{
    private val _identity = MutableLiveData<IdentityViewData>()
    val identity: LiveData<IdentityViewData>
        get() = _identity

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