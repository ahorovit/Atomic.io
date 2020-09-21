package com.drspaceman.atomicio.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.drspaceman.atomicio.R
import com.drspaceman.atomicio.model.Identity
import com.drspaceman.atomicio.repository.AtomicIoRepository
import com.drspaceman.atomicio.ui.BaseDialogFragment.SpinnerItemViewData
import com.drspaceman.atomicio.ui.BaseDialogFragment.SpinnerViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class IdentityPageViewModel(
    application: Application
) : BaseViewModel(application), SpinnerViewModel {

    private val _identities = MediatorLiveData<List<IdentityViewData>>()
    val identities: LiveData<List<IdentityViewData>>
        get() = _identities

    private val _identity = MutableLiveData<IdentityViewData>()
    val identity: LiveData<IdentityViewData>
        get() = _identity

    init {
        viewModelScope.launch {
            _identities.addSource(Transformations.map(atomicIoRepo.allIdentities) { repoIdentities ->
                repoIdentities.map { identity ->
                    IdentityViewData.of(identity)
                }
            }) { identityViewData ->
                _identities.value = identityViewData
            }
        }
    }

    fun loadIdentity(identityId: Long) {
        viewModelScope.launch {
            _identity.value = IdentityViewData.of(atomicIoRepo.getIdentity(identityId))
        }
    }

    fun getNewIdentityView(): IdentityViewData {
        val newIdentityView = IdentityViewData()
        newIdentityView.type = "Other"
        return newIdentityView
    }

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
     */
    fun getSpinnerItems(): List<String> {
        return AtomicIoRepository.identityTypes
    }

    override fun getSpinnerItemResourceId(type: String?): Int? {
        return AtomicIoRepository.getTypeResourceId(type)
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