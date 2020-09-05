package com.drspaceman.atomicio.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.drspaceman.atomicio.R
import com.drspaceman.atomicio.viewmodel.IdentityViewModel

import kotlinx.android.synthetic.main.fragment_identity_details.*

class IdentityDetailsFragment : DialogFragment() {
    private val identityId: Long? by lazy {
        arguments?.getLong(ARG_IDENTITY_ID, 0)
    }

    private val identityViewModel by viewModels<IdentityViewModel>()
    private var identityView: IdentityViewModel.IdentityViewData? = null

    private lateinit var parentActivity: AppCompatActivity

    private lateinit var spinnerAdapter: ArrayAdapter<String>

    // @todo: KAE isn't working for these Views for some reason
    private lateinit var imageViewType: ImageView
    private lateinit var spinnerTypes: Spinner
    private lateinit var saveIdentityButton: Button


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_identity_details, container, false)

//        // @TODO: Figure out why KAE isn't working
        spinnerTypes = view.findViewById(R.id.spinnerTypes)
        imageViewType = view.findViewById(R.id.imageViewType)
        saveIdentityButton = view.findViewById(R.id.saveIdentityButton)


//        setSupportActionBar(appToolbar)

        populateTypeSpinner()

        loadIdentity()

        saveIdentityButton.setOnClickListener {
            saveIdentityDetails()
        }

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        parentActivity = context as AppCompatActivity
    }

    override fun onStart() {
        super.onStart()

        dialog?.let {
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.MATCH_PARENT
            it.window?.setLayout(width, height)
        }
    }

    private fun populateTypeSpinner() {
        spinnerAdapter = ArrayAdapter(
            parentActivity,
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

    private fun loadIdentity() {

        identityId?.let {
            observeIdentity(it)
        } ?: run {
            identityView = identityViewModel.getNewIdentityView()
            setSpinnerSelection()
        }
    }

    private fun observeIdentity(identityId: Long) {
        identityViewModel.getIdentity(identityId)?.observe(
            this,
            Observer<IdentityViewModel.IdentityViewData> {
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

        dismiss()
    }

    companion object {
        const val TAG = "identity_details_fragment"
        private const val ARG_IDENTITY_ID = "extra_identity_id"

        fun newInstance(identityId: Long?): IdentityDetailsFragment {
            val instance = IdentityDetailsFragment()

            identityId?.let {
                val args = Bundle()
                args.putLong(ARG_IDENTITY_ID, identityId)
                instance.arguments = args
            }

            return instance
        }
    }
}