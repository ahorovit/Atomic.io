package com.drspaceman.atomicio.repository

import android.content.Context
import androidx.lifecycle.LiveData
import com.drspaceman.atomicio.R
import com.drspaceman.atomicio.db.AtomicIoDao
import com.drspaceman.atomicio.db.AtomicIoDatabase
import com.drspaceman.atomicio.model.Identity

class AtomicIoRepository(context: Context) {

    private var db = AtomicIoDatabase.getInstance(context)
    private var dao = db.atomicIoDao()

    val allIdentities: LiveData<List<Identity>>
        get() {
            return dao.loadAllIdentities()
        }

    private val allTypes = buildTypes()

    val identityTypes: List<String>
        get() {
            return ArrayList(allTypes.keys.sorted())
        }

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

    private fun buildTypes(): HashMap<String, Int> {
        return hashMapOf(
            "Academic" to R.drawable.ic_academic,
            "Artistic" to R.drawable.ic_artistic,
            "Family" to R.drawable.ic_family,
            "Financial" to R.drawable.ic_financial,
            "Friendship" to R.drawable.ic_friendship,
            "Wellness" to R.drawable.ic_health,
            "Mindset" to R.drawable.ic_mindset,
            "Other" to R.drawable.ic_other,
            "Productivity" to R.drawable.ic_productivity,
            "Professional" to R.drawable.ic_professional,
            "Romantic" to R.drawable.ic_romantic
        )
    }

    fun getTypeResourceId(type: String?): Int {
        return type?.let { allTypes[type] } ?: allTypes["Other"]!!
    }
}