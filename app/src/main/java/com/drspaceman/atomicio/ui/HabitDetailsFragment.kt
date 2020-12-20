package com.drspaceman.atomicio.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager

import com.drspaceman.atomicio.R
import com.drspaceman.atomicio.adapter.TaskListItem
import com.drspaceman.atomicio.adapter.ViewDataSpinnerAdapter
import com.drspaceman.atomicio.ui.TimePickerFragment.Companion.START_TIME_REQUEST
import com.drspaceman.atomicio.viewmodel.AgendaPageViewModel
import com.drspaceman.atomicio.viewmodel.AgendaPageViewModel.TaskViewData
import com.drspaceman.atomicio.viewmodel.HabitDetailsViewModel
import com.drspaceman.atomicio.viewmodel.HabitPageViewModel.HabitViewData
import com.drspaceman.atomicio.viewmodel.IdentityPageViewModel.IdentityViewData
import com.drspaceman.atomicio.viewstate.HabitLoaded
import com.drspaceman.atomicio.viewstate.HabitLoading
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.details_dialog.*
import kotlinx.android.synthetic.main.details_dialog.viewFlipper

import kotlinx.android.synthetic.main.edit_habit_form.*
import kotlinx.android.synthetic.main.edit_habit_form.spinnerImage
import kotlinx.android.synthetic.main.edit_task_form.*
import kotlinx.android.synthetic.main.fragment_agenda.*
import org.threeten.bp.LocalTime

@AndroidEntryPoint
class HabitDetailsFragment : BaseDialogFragment(), EditTaskListener {
    override val layoutId: Int = R.layout.edit_habit_form

    override val viewModel by activityViewModels<HabitDetailsViewModel>()

    override lateinit var itemViewData: HabitViewData

    private lateinit var spinnerAdapter: ViewDataSpinnerAdapter

    private var groupAdapter = GroupAdapter<GroupieViewHolder>()

    override var itemId: Long? = null
    var identityId: Long? = null

    var taskAwaiting: AwaitingTask? = null

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
        viewModel.viewState.observe(
            viewLifecycleOwner,
            { viewState ->
                viewFlipper.displayedChild = when (viewState) {
                    HabitLoading -> LOADING
                    is HabitLoaded -> {
                        itemViewData = viewState.habit
                        populateExistingValues()

                        groupAdapter.apply {
                            clear()
                            addAll(viewState.tasks.map {
                                TaskListItem(it, this@HabitDetailsFragment)
                            })
                        }

                        DETAILS_FORM
                    }
                }
            }
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        newTaskButton.setOnClickListener {
            viewModel.addNewTask()
        }

        taskRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = groupAdapter
        }
    }

    override fun populateExistingValues() {
        editTextHabitName.setText(itemViewData.name)
        setSpinnerSelection()
    }

    override fun setSpinnerSelection() {
        val identityId = itemViewData.identityId ?: identityId

        identityId?.let { parentIdentityId ->
            val position = spinnerAdapter.getPosition(parentIdentityId)

            // @todo: figure out why occasionally image is [NA] null while selection text is correct
            position.let {
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
                    identityId = identity.id
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
            requireActivity(),
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

        if (targetFragment == null) {
            viewModel.saveHabit(itemViewData)
            dismiss()
        } else {
            returnNewHabitId()
        }
    }

    /**
     * TaskDetailFragment is waiting for the saved habit ID
     * --> We need to wait for the habit to be saved before we can dismiss the dialog
     */
    private fun returnNewHabitId() {
        viewModel.saveHabitForReturnId(itemViewData).observe(
            viewLifecycleOwner,
            {
                it?.let {
                    val bundle = Bundle()
                    bundle.putLong(NEW_HABIT_RESULT, it)

                    val intent = Intent().putExtras(bundle)
                    targetFragment?.onActivityResult(targetRequestCode, Activity.RESULT_OK, intent)
                    dismiss()
                }
            }
        )
    }

    override fun pickTimeForTask(targetTask: TaskViewData, targetPosition: Int) {
        taskAwaiting = AwaitingTask(targetTask, targetPosition)
        (activity as MainActivity).showTimePickerDialog(this, START_TIME_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode != Activity.RESULT_OK) {
            return
        }

        when (requestCode) {
            START_TIME_REQUEST -> {
                taskAwaiting?.let {
                    it.task.setStartTime(
                        data?.extras?.getString(TimePickerFragment.TIME_PICKER_RESULT)
                    )

                    groupAdapter.notifyItemChanged(it.position)
                }
            }
        }
    }

    companion object {

        private const val LOADING = 0
        private const val DETAILS_FORM = 1

        const val NEW_HABIT_RESULT = "new_habit_result"
        const val NEW_HABIT_REQUEST = 234

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

data class AwaitingTask(val task: TaskViewData, val position: Int)

interface EditTaskListener {
    fun pickTimeForTask(targetTask: TaskViewData, targetPosition: Int)
}