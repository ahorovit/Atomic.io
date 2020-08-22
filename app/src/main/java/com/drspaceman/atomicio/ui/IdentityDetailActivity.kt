package com.drspaceman.atomicio.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.drspaceman.atomicio.R
import com.drspaceman.atomicio.viewmodel.IdentityViewModel
import com.drspaceman.atomicio.viewmodel.IdentityViewModel.IdentityView
import kotlinx.android.synthetic.main.activity_identity_detail.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class IdentityDetailActivity : AppCompatActivity() {
    private val identityViewModel by viewModels<IdentityViewModel>()
    private var identityView: IdentityView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_identity_detail)

        setSupportActionBar(toolbar)
        getIntentData()

        saveIdentityButton.setOnClickListener {
            saveIdentityDetails()
        }
    }

    private fun getIntentData() {
        val identityId = intent.getLongExtra(MainActivity.EXTRA_IDENTITY_ID, 0)

        if (identityId != 0L) {
            observeIdentity(identityId)
            //                    populateFields()
            //                    populateTypeList()
        }
    }

    private fun observeIdentity(identityId: Long) {
        identityViewModel.getIdentity(identityId)?.observe(
            this,
            Observer<IdentityView> {
                it?.let {
                    identityView = it
                }
            }
        )
    }

    private fun newIdentity() {
        GlobalScope.launch {
            val identityId = identityViewModel.addIdentity()

            identityId?.let {
                val targetIdentityView = identityViewModel.getIdentity(identityId)
                writeInputValues(targetIdentityView)

            }


            identityId?.let {
//                observeIdentity(identityId)
            }
        }
    }

    private fun populateTypeList() {
        TODO("Not yet implemented")
    }

    private fun writeInputValues(targetIdentityView: IdentityView?) {

        targetIdentityView?.let {
            it.name = editTextName.text.toString()
//            it.type = spinnerCategory.selectedItem as String

            identityViewModel.updateIdentity(it)
        }
    }

    private fun saveIdentityDetails() {
        val name = editTextName.text.toString()
        if (name.isEmpty()) {
            return
        }

        if (identityView != null) {
            writeInputValues(identityView)
        } else {
            newIdentity()
        }
    }
}
