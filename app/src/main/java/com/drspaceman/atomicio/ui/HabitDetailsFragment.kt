package com.drspaceman.atomicio.ui

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import androidx.fragment.app.activityViewModels

import com.drspaceman.atomicio.R
import com.drspaceman.atomicio.adapter.ViewDataSpinnerAdapter
import com.drspaceman.atomicio.viewmodel.HabitPageViewModel
import com.drspaceman.atomicio.viewmodel.HabitPageViewModel.HabitViewData
import com.drspaceman.atomicio.viewmodel.IdentityPageViewModel.IdentityViewData
import dagger.hilt.android.AndroidEntryPoint

import kotlinx.android.synthetic.main.fragment_habit_details.*
import kotlinx.android.synthetic.main.spinner_layout.*

@AndroidEntryPoint
class HabitDetailsFragment : BaseDialogFragment() {
    override val layoutId: Int = R.layout.fragment_habit_details

    override val viewModel by activityViewModels<HabitPageViewModel>()

    override lateinit var itemViewData: HabitViewData

    private lateinit var spinnerAdapter: ViewDataSpinnerAdapter

    override var itemId: Long? = null
    var identityId: Long? = null

    override fun setArguments(args: Bundle?) {
        args?.let {
            val default = -1L
            val bundleItemId = it.getLong(ARG_HABIT_ID, default)
            val bundleIdentityId = it.getLong(ARG_IDENTITY_ID, default)

            itemId = if (bundleItemId == default) null else bundleItemId
            identityId = if (bundleIdentityId == default) null else bundleIdentityId
        }

        super.setArguments(args)
    }

    override fun setObservers() {
        viewModel.getHabit(itemId, identityId).observe(
            viewLifecycleOwner,
            {
                itemViewData = it
                populateExistingValues()
            }
        )
    }

    // @todo: remove
    override fun loadDataItem() {}

    // @todo: remove
    override fun loadExistingItem(id: Long) {}

    override fun populateExistingValues() {
        editTextHabitName.setText(itemViewData.name)
        setSpinnerSelection()
    }

    // @todo: remove
    override fun getNewItem() {}

    override fun setSpinnerSelection() {
        itemViewData.identityId?.let { parentIdentityId ->
            val position = spinnerAdapter.getViewDatumPosition(parentIdentityId)

            // @todo: figure out why occasionally image is [NA] null while selection text is correct
            position?.let {
                spinner.setSelection(it)
                spinnerImage.setImageResource(itemViewData.typeResourceId)
            }
        }
    }

    override fun populateTypeSpinner() {
        initializeSpinnerAdapter()

        spinner.post {
            spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val identity = parent.getItemAtPosition(position) as IdentityViewData
                    spinnerImage.setImageResource(identity.typeResourceId)
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    // @todo: provide option to add identity if none exist yet
                }
            }
        }
    }

    /**
     * Habit Spinner is a list of Identity names, so we must observe any changes to the
     * saved Identities in the DB
     */
    private fun initializeSpinnerAdapter() {
        spinnerAdapter = ViewDataSpinnerAdapter(
            parentActivity,
            android.R.layout.simple_spinner_item
        )

        spinner.adapter = spinnerAdapter

        viewModel.getSpinnerItems().observe(
            viewLifecycleOwner,
            {
                spinnerAdapter.setSpinnerItems(it)

                // This needs to run again in case the Fragment loaded before identities
                // were loaded
                setSpinnerSelection()
            }
        )
    }

    override fun saveItemDetails() {
        val writeHabitView = itemViewData

        val name = editTextHabitName.text.toString()
        if (name.isEmpty()) {
            return
        }

        val parentIdentity = spinner.selectedItem as IdentityViewData
        writeHabitView.name = name
        writeHabitView.identityId = parentIdentity.id
        writeHabitView.type = parentIdentity.type

        writeHabitView.id?.let {
            viewModel.updateHabit(writeHabitView)
        } ?: run {
            viewModel.insertHabit(writeHabitView)
        }

        dismiss()
    }

    companion object {
        private const val ARG_HABIT_ID = "extra_habit_id"
        private const val ARG_IDENTITY_ID = "extra_identity_id"

        fun newInstance(itemId: Long?, identityId: Long? = null): HabitDetailsFragment {
            val instance = HabitDetailsFragment()
            val args = Bundle()

            itemId?.let {
                args.putLong(ARG_HABIT_ID, it)
            }

            identityId?.let {
                args.putLong(ARG_IDENTITY_ID, it)
            }

            instance.arguments = args
            return instance
        }
    }
}