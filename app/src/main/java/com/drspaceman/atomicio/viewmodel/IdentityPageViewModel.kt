package com.drspaceman.atomicio.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.drspaceman.atomicio.R
import com.drspaceman.atomicio.model.Identity
import com.drspaceman.atomicio.repository.AtomicIoRepository
import com.drspaceman.atomicio.ui.BaseDialogFragment
import com.drspaceman.atomicio.ui.BaseDialogFragment.SpinnerItemViewData
import com.drspaceman.atomicio.ui.BaseDialogFragment.SpinnerViewModel
import kotlinx.android.synthetic.main.spinner_layout.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class IdentityPageViewModel(application: Application) : BaseViewModel(application), SpinnerViewModel {

    // @TODO: insert as Explicit Dependency
    private var atomicIoRepo = AtomicIoRepository(getApplication())
    private var identityView: LiveData<IdentityViewData>? = null
    private var identities: LiveData<List<IdentityViewData>>? = null

    // @TODO: observe?
    fun getNewIdentityView(): IdentityViewData {
        val newIdentityView = IdentityViewData()
        newIdentityView.type = "Other"
        return newIdentityView
    }

    fun insertIdentity(newIdentityView: IdentityViewData) {
        val identity = identityViewToIdentity(newIdentityView)

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
            repoIdentities.map { identityToIdentityView(it) }
        }
    }

    fun updateIdentity(identityView: IdentityViewData) {
        GlobalScope.launch {
            val identity = identityViewToIdentity(identityView)
            atomicIoRepo.updateIdentity(identity)
        }
    }

    override fun deleteItem(itemViewData: BaseViewData) {
        GlobalScope.launch {
            val identity = identityViewToIdentity(itemViewData as IdentityViewData)
            atomicIoRepo.deleteIdentity(identity)
        }
    }

    private fun mapIdentityToIdentityView(identityId: Long) {
        val identity = atomicIoRepo.getLiveIdentity(identityId)
        identityView = Transformations.map(identity) { repoIdentity ->
            repoIdentity?.let {
                identityToIdentityView(repoIdentity)
            }
        }
    }

    private fun identityToIdentityView(identity: Identity): IdentityViewData {
        return IdentityViewData(
            identity.id,
            identity.name,
            identity.description,
            identity.type,
            atomicIoRepo.getTypeResourceId(identity.type)
        )
    }

    private fun identityViewToIdentity(identityView: IdentityViewData): Identity {
        val identity = identityView.id?.let {
            atomicIoRepo.getIdentity(it)
        } ?: atomicIoRepo.createIdentity()

        identity.id = identityView.id
        identity.name = identityView.name
        identity.type = identityView.type
        identity.description = identityView.description

        return identity
    }

    /**
     * Identity spinner is a simple list of identity types (strings)
     */
    fun getSpinnerItems(): List<String> {
        return atomicIoRepo.identityTypes
    }

    override fun getSpinnerItemResourceId(type: String?): Int? {
        return atomicIoRepo.getTypeResourceId(type)
    }

    data class IdentityViewData(
        override var id: Long? = null,
        var name: String? = "",
        var description: String? = "",
        override var type: String? = "",
        override var typeResourceId: Int = R.drawable.ic_other
    ): BaseViewData(), SpinnerItemViewData {

        // Required to support Spinner display of Identity
        override fun toString(): String {
            return name ?: ""
        }
    }
}