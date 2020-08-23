package com.drspaceman.atomicio.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.drspaceman.atomicio.R
import com.drspaceman.atomicio.model.Identity
import com.drspaceman.atomicio.repository.AtomicIoRepository
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class IdentityViewModel(application: Application) : AndroidViewModel(application) {

    private var atomicIoRepo = AtomicIoRepository(getApplication()) // @TODO: insert as Explicity Dependency
    private var identityView: LiveData<IdentityView>? = null
    private var identities: LiveData<List<IdentityView>>? = null

    // @TODO: observe?
    fun getNewIdentityView(): IdentityView {
        val newIdentityView = IdentityView()
        newIdentityView.type = "Other"
        return newIdentityView
    }

    fun insertIdentity(newIdentityView: IdentityView) {
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

    fun getIdentity(identityId: Long): LiveData<IdentityView>? {
        if (identityView == null) {
            mapIdentityToIdentityView(identityId)
        }

        return identityView
    }

    fun getIdentityViews(): LiveData<List<IdentityView>>? {
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

    fun updateIdentity(identityView: IdentityView) {
        GlobalScope.launch {
            val identity = identityViewToIdentity(identityView)
            atomicIoRepo.updateIdentity(identity)
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

    private fun identityToIdentityView(identity: Identity): IdentityView {
        return IdentityView(
            identity.id,
            identity.name,
            identity.description,
            identity.type,
            atomicIoRepo.getTypeResourceId(identity.type)
        )
    }

    private fun identityViewToIdentity(identityView: IdentityView): Identity {
        val identity = identityView.id?.let {
            atomicIoRepo.getIdentity(it)
        } ?: atomicIoRepo.createIdentity()

        identity.id = identityView.id
        identity.name = identityView.name
        identity.type = identityView.type
        identity.description = identityView.description

        return identity
    }

    fun getTypes(): List<String> {
        return atomicIoRepo.identityTypes
    }

    fun getTypeResourceId(type: String?): Int? {
        return atomicIoRepo.getTypeResourceId(type)
    }

    data class IdentityView(
        var id: Long? = null,
        var name: String? = "",
        var description: String? = "",
        var type: String? = "",
        var typeResourceId: Int = R.drawable.ic_other
    )
}