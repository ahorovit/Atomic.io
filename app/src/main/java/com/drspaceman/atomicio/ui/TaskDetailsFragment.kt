package com.drspaceman.atomicio.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.Spinner
import androidx.fragment.app.activityViewModels
import com.drspaceman.atomicio.R
import com.drspaceman.atomicio.adapter.ViewDataSpinnerAdapter
import com.drspaceman.atomicio.ui.HabitDetailsFragment.Companion.NEW_HABIT_REQUEST
import com.drspaceman.atomicio.ui.HabitDetailsFragment.Companion.NEW_HABIT_RESULT
import com.drspaceman.atomicio.ui.TimePickerFragment.Companion.END_TIME_REQUEST
import com.drspaceman.atomicio.ui.TimePickerFragment.Companion.START_TIME_REQUEST
import com.drspaceman.atomicio.ui.TimePickerFragment.Companion.TIME_PICKER_RESULT
import com.drspaceman.atomicio.viewmodel.AgendaPageViewModel.TaskViewData
import com.drspaceman.atomicio.viewmodel.TaskDetailsViewModel
import com.drspaceman.atomicio.viewstate.TaskLoaded
import com.drspaceman.atomicio.viewstate.TaskLoading
import kotlinx.android.synthetic.main.details_dialog.*

import kotlinx.android.synthetic.main.edit_task_form.*
import org.threeten.bp.LocalTime
import org.threeten.bp.format.DateTimeFormatter

class TaskDetailsFragment : BaseDialogFragment() {
    override val layoutId = R.layout.edit_task_form

    override val itemId: Long? by lazy {
        arguments?.getLong(ARG_TASK_ID, 0)
    }

    override val viewModel by activityViewModels<TaskDetailsViewModel>()

    override lateinit var itemViewData: TaskViewData

    // If title has been manually edited, don't autofill habit name
    // TODO: handle with viewModel
    private var isTitleEdited = false


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        habitSpinnerLabel.text = getString(R.string.habit_label)

        newHabit.setOnClickListener {
            showNewHabitDialog()
        }

        // TODO: default to NOW
        editStartTime.setOnClickListener {
            showTimePickerDialog(START_TIME_REQUEST)
        }

        // TODO: change to duration picker
        editEndTime.setOnClickListener {
            showTimePickerDialog(END_TIME_REQUEST)
        }

        // @todo: move to ViewModel
        editTextTaskName.setOnKeyListener { _, _, _ ->
            isTitleEdited = true
            false
        }
    }

    override fun setObservers() {
        viewModel.viewState.observe(
            viewLifecycleOwner,
            {
                viewFlipper.displayedChild = when (it) {
                    is TaskLoading -> {
                        LOADING
                    }
                    is TaskLoaded -> {
                        renderForm(it)
                        DETAILS_FORM
                    }
                }
            }
        )
    }

    private fun renderForm(state: TaskLoaded) {
        itemViewData = state.task

        (identitySpinner.adapter as ViewDataSpinnerAdapter).setSpinnerItems(state.identities)
        (habitSpinner.adapter as ViewDataSpinnerAdapter).setSpinnerItems(state.habits)

        val identity = updateSpinner(identitySpinner, state.selectedIdentityId)
        spinnerImage.setImageResource(identity.typeResourceId)

        updateSpinner(habitSpinner, state.selectedHabitId)

        editTextTaskName.setText(state.task.title)
        editStartTime.setText(formatTime(state.task.startTime))
        editEndTime.setText(formatTime(state.task.endTime))
    }

    private fun showNewHabitDialog() {
        val fragmentManager = activity?.supportFragmentManager ?: return
        val newHabitFragment = HabitDetailsFragment.newInstance(null)
        newHabitFragment.setTargetFragment(this, NEW_HABIT_REQUEST)
        newHabitFragment.show(fragmentManager, "${newHabitFragment::class}_tag")
    }

    private fun showTimePickerDialog(requestCode: Int) {
        val fragmentManager = activity?.supportFragmentManager ?: return
        val timePicker = TimePickerFragment.newInstance()
        timePicker.setTargetFragment(this, requestCode)
        timePicker.show(fragmentManager, "${timePicker::class}_tag")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode != Activity.RESULT_OK) {
            return
        }

        // TODO: move conversion into LocalTime into viewmodel?
        var pickedTime: LocalTime? = null
        var newHabitId: Long? = null
        if (data?.extras?.containsKey(TIME_PICKER_RESULT) == true) {
            pickedTime = LocalTime.parse(data.extras?.getString(TIME_PICKER_RESULT))
        } else if (data?.extras?.containsKey(NEW_HABIT_RESULT) == true) {
            newHabitId = data.extras?.getLong(NEW_HABIT_RESULT)
        }

        when (requestCode) {
            START_TIME_REQUEST -> {
                pickedTime?.let {
                    editStartTime.setText(formatTime(pickedTime))
                    itemViewData.startTime = pickedTime
                }
            }
            END_TIME_REQUEST -> {
                pickedTime?.let {
                    editEndTime.setText(formatTime(pickedTime))
                    itemViewData.endTime = pickedTime
                }
            }
            NEW_HABIT_REQUEST -> {
                newHabitId?.let {
                    viewModel.setParentHabit(it)
                }
            }
        }
    }

    private fun formatTime(time: LocalTime?): String {
        var displayTime = ""

        time?.let {
            displayTime = it.format(DateTimeFormatter.ofPattern("hh:mm a"))
            if (displayTime[0] == '0') {
                displayTime = displayTime.substring(1)
            }
        }

        return displayTime
    }

    override fun loadExistingItem(id: Long) {
        viewModel.loadTask(id)
    }


    // TODO: remove
    override fun populateExistingValues() {}

    override fun saveItemDetails() {
        itemViewData.title = editTextTaskName.text.toString()

        if (!viewModel.isValid()) {
            // TODO: show snackbar/toast
            return
        }

        viewModel.saveItem()
        dismiss()
    }

    override fun getNewItem() {
        itemViewData = viewModel.getNewTaskView()
    }

    /**
     * Push change to spinner from ViewState --> We need to avoid triggering additional change
     * to viewState because of the change (use tag)
     */
    private fun updateSpinner(spinner: Spinner, itemId: Long): SpinnerItemViewData {
        val adapter = spinner.adapter as ViewDataSpinnerAdapter
        val position = adapter.getPosition(itemId)
        spinner.tag = position // avoid triggering onItemSelected() logic
        spinner.setSelection(position)

        return adapter.getItem(position) as SpinnerItemViewData
    }

    // @TODO: (mostly) duplicated from HabitDetailsFragment
    override fun setSpinnerSelection() {}

    override fun populateTypeSpinner() {
        val itemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                if (parent.tag == position) return
                val item = parent.getItemAtPosition(position) as SpinnerItemViewData

                when (parent.id) {
                    R.id.identitySpinner -> viewModel.setSelectedIdentity(item.id)
                    R.id.habitSpinner -> viewModel.setSelectedHabit(item.id)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // @todo: provide option to add identity if none exist yet
            }
        }

        identitySpinner.onItemSelectedListener = itemSelectedListener
        identitySpinner.adapter = ViewDataSpinnerAdapter(
            parentActivity,
            android.R.layout.simple_spinner_item,
            "Identity"
        )

        habitSpinner.onItemSelectedListener = itemSelectedListener
        habitSpinner.adapter = ViewDataSpinnerAdapter(
            parentActivity,
            android.R.layout.simple_spinner_item,
            "Habit"
        )
    }

    companion object {
        private const val LOADING = 0
        private const val DETAILS_FORM = 1

        // @todo: could be communicated simply through the shared viewModel?
        private const val ARG_TASK_ID = "extra_task_id"

        fun newInstance(itemId: Long?): TaskDetailsFragment {
            val instance = TaskDetailsFragment()

            itemId?.let {
                val args = Bundle()
                args.putLong(ARG_TASK_ID, itemId)
                instance.arguments = args
            }

            return instance
        }
    }
}