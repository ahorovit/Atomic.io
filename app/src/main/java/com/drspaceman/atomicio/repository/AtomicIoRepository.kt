package com.drspaceman.atomicio.repository

import android.content.Context
import androidx.lifecycle.LiveData
import com.drspaceman.atomicio.db.AtomicIoDao
import com.drspaceman.atomicio.db.AtomicIoDatabase
import com.drspaceman.atomicio.model.Identity

class AtomicIoRepository(context: Context) {
    private var db = AtomicIoDatabase.getInstance(context)
    private var dao = db.atomicIoDao()

    fun createIdentity(): Identity {
        return Identity()
    }

    fun addIdentity(identity: Identity): Long? {
        val newId = dao.insertIdentity(identity)
        identity.id = newId

        return newId
    }

    fun getLiveIdentity(identityId: Long): LiveData<Identity> {
        return dao.loadLiveIdentity(identityId)
    }

    fun updateIdentity(identity: Identity) {
        dao.updateIdentity(identity)
    }

    fun getIdentity(identityId: Long): Identity {
        return dao.getIdentity(identityId)
    }


}