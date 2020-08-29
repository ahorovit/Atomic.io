package com.drspaceman.atomicio.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Spinner
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.drspaceman.atomicio.R
import com.drspaceman.atomicio.viewmodel.IdentityViewModel
import com.drspaceman.atomicio.viewmodel.IdentityViewModel.IdentityView
import kotlinx.android.synthetic.main.activity_identity_detail.*


// @todo: use DialogFragment?
class IdentityDetailActivity : AppCompatActivity() {
    private val identityViewModel by viewModels<IdentityViewModel>()
    private var identityView: IdentityView? = null

    // @todo: KAE isn't working for these Views for some reason
    private lateinit var imageViewType: ImageView
    private lateinit var spinnerTypes: Spinner
    private lateinit var spinnerAdapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_identity_detail)
        populateTypeSpinner()

        setSupportActionBar(appToolbar)
        getIntentData()

        saveIdentityButton.setOnClickListener {
            saveIdentityDetails()
        }
    }

    private fun getIntentData() {
        val identityId = intent.getLongExtra(MainActivity.EXTRA_IDENTITY_ID, 0)

        if (identityId != 0L) {
            observeIdentity(identityId)
        } else {
            identityView = identityViewModel.getNewIdentityView()
            setSpinnerSelection()
        }
    }

    private fun observeIdentity(identityId: Long) {
        identityViewModel.getIdentity(identityId)?.observe(
            this,
            Observer<IdentityView> {
                it?.let {
                    identityView = it
                    populateExistingValues()
                }
            }
        )
    }

    private fun populateExistingValues() {
        identityView?.let { identityView ->
            editTextName.setText(identityView.name)
            setSpinnerSelection()
        }
    }

    private fun populateTypeSpinner() {
        // @TODO: Figure out why KAE isn't working
        spinnerTypes = findViewById(R.id.spinnerTypes)
        imageViewType = findViewById(R.id.imageViewType)

        spinnerAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            identityViewModel.getTypes()
        )

        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerTypes.adapter = spinnerAdapter

        spinnerTypes.post {
            spinnerTypes.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val type = parent.getItemAtPosition(position) as String
                    val resourceId = identityViewModel.getTypeResourceId(type)
                    resourceId?.let {
                        imageViewType.setImageResource(it)
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    // Required by OnItemSelectedListener interface, but not needed
                }
            }
        }
    }

    private fun setSpinnerSelection() {
        identityView?.let {
            val type = it.type ?: return
            spinnerTypes.setSelection(spinnerAdapter.getPosition(type))
            imageViewType.setImageResource(it.typeResourceId)
        }
    }

    private fun saveIdentityDetails() {
        val writeIdentityView = identityView?: return

        val name = editTextName.text.toString()
        if (name.isEmpty()) {
            return
        }

        writeIdentityView.let {
            it.name = name
            it.type = spinnerTypes.selectedItem as String
        }

        writeIdentityView.id?.let {
            identityViewModel.updateIdentity(writeIdentityView)
        } ?: run {
            identityViewModel.insertIdentity(writeIdentityView)
        }

        finish()
    }
}
