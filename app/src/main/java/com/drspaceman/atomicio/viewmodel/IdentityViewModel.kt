package com.drspaceman.atomicio.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.drspaceman.atomicio.model.Identity
import com.drspaceman.atomicio.repository.AtomicIoRepository
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class IdentityViewModel(application: Application) : AndroidViewModel(application) {

    private var atomicIoRepo = AtomicIoRepository(getApplication())
    private var identityView: LiveData<IdentityView>? = null

    fun addIdentity(): Long? {
        val identity: Identity = atomicIoRepo.createIdentity()
        identity.type = "Other"
        return atomicIoRepo.addIdentity(identity)
    }

    fun getIdentity(identityId: Long): LiveData<IdentityView>? {
        if (identityView == null) {
            mapIdentityToIdentityView(identityId)
        }

        return identityView
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
            identity.type
        )
    }

    fun updateIdentity(identityView: IdentityView) {
        GlobalScope.launch {
            val identity = identityViewToIdentity(identityView)
            identity?.let {
                atomicIoRepo.updateIdentity(it)
            }
        }
    }

    private fun identityViewToIdentity(identityView: IdentityView): Identity? {
        val identity = identityView.id?.let {
            atomicIoRepo.getIdentity(it)
        }

        identity?.let {
            it.id = identityView.id
            it.name = identityView.name
            it.type = identityView.type
            it.description = identityView.description
        }

        return identity
    }


    data class IdentityView(
        var id: Long? = null,
        var name: String? = "",
        var description: String? = "",
        var type: String? = ""
    )
}