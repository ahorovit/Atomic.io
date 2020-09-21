package com.drspaceman.atomicio.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.drspaceman.atomicio.R
import com.drspaceman.atomicio.model.Identity
import com.drspaceman.atomicio.repository.AtomicIoRepository
import com.drspaceman.atomicio.ui.BaseDialogFragment.SpinnerItemViewData
import com.drspaceman.atomicio.ui.BaseDialogFragment.SpinnerViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class IdentityPageViewModel(application: Application) : BaseViewModel(application),
    SpinnerViewModel {

    // @TODO: insert as Explicit Dependency
    private var identityView: LiveData<IdentityViewData>? = null
    private var identities: LiveData<List<IdentityViewData>>? = null

    // @TODO: observe?
    fun getNewIdentityView(): IdentityViewData {
        val newIdentityView = IdentityViewData()
        newIdentityView.type = "Other"
        return newIdentityView
    }

    fun insertIdentity(newIdentityView: IdentityViewData) {
        val identity = newIdentityView.toModel()

        GlobalScope.launch {
            val identityId = atomicIoRepo.addIdentity(identity)

            // @TODO: this might be unnecessary, because when we save a new Identity, we also finis() the
            // IdentityDetailActivity --> So we're synching UI unnecessarily here maybe?
            identityId?.let {
                mapIdentityToIdentityView(identityId)
            }
        }
    }

    fun getIdentity(identityId: Long): LiveData<IdentityViewData>? {
        if (identityView == null) {
            mapIdentityToIdentityView(identityId)
        }

        return identityView
    }

    fun getIdentities(): LiveData<List<IdentityViewData>>? {
        if (identities == null) {
            mapIdentitiesToIdentityViews()
        }

        return identities
    }

    private fun mapIdentitiesToIdentityViews() {
        identities = Transformations.map(atomicIoRepo.allIdentities) { repoIdentities ->
            repoIdentities.map { IdentityViewData.of(it) }
        }
    }

    fun updateIdentity(identityViewData: IdentityViewData) {
        GlobalScope.launch {
            val identity = identityViewData.toModel()
            atomicIoRepo.updateIdentity(identity)
        }
    }

    override fun deleteItem(itemViewData: BaseViewData) {
        GlobalScope.launch {
            val identity = (itemViewData as IdentityViewData).toModel()
            atomicIoRepo.deleteIdentity(identity)
        }
    }

    private fun mapIdentityToIdentityView(identityId: Long) {
        val identity = atomicIoRepo.getLiveIdentity(identityId)
        identityView = Transformations.map(identity) { repoIdentity ->
            repoIdentity?.let {
                IdentityViewData.of(repoIdentity)
            }
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
        // Required to support Spinner display of Identity
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