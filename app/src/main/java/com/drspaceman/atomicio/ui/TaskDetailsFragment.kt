package com.drspaceman.atomicio.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import androidx.fragment.app.activityViewModels
import com.drspaceman.atomicio.R
import com.drspaceman.atomicio.adapter.ViewDataSpinnerAdapter
import com.drspaceman.atomicio.ui.HabitDetailsFragment.Companion.NEW_HABIT_REQUEST
import com.drspaceman.atomicio.ui.HabitDetailsFragment.Companion.NEW_HABIT_RESULT
import com.drspaceman.atomicio.ui.TimePickerFragment.Companion.END_TIME_REQUEST
import com.drspaceman.atomicio.ui.TimePickerFragment.Companion.START_TIME_REQUEST
import com.drspaceman.atomicio.ui.TimePickerFragment.Companion.TIME_PICKER_RESULT
import com.drspaceman.atomicio.viewmodel.AgendaPageViewModel
import com.drspaceman.atomicio.viewmodel.AgendaPageViewModel.TaskViewData
import com.drspaceman.atomicio.viewmodel.HabitPageViewModel.HabitViewData

import kotlinx.android.synthetic.main.fragment_task_details.*
import kotlinx.android.synthetic.main.edit_task_form.*
import org.threeten.bp.LocalTime
import org.threeten.bp.format.DateTimeFormatter

class TaskDetailsFragment : BaseDialogFragment() {
    override val layoutId = R.layout.fragment_task_details

    override val itemId: Long? by lazy {
        arguments?.getLong(ARG_TASK_ID, 0)
    }

    override val viewModel by activityViewModels<AgendaPageViewModel>()

    override lateinit var itemViewData: TaskViewData

    private var spinnerAdapter: ViewDataSpinnerAdapter? = null

    // If title has been manually edited, don't autofill habit name
    private var isTitleEdited = false

    override fun setObservers() {
        viewModel.task.observe(
            this,
            {
                itemViewData = it
                populateExistingValues()
            }
        )

        viewModel.habits.observe(
            viewLifecycleOwner,
            { habitViewData ->
                spinnerAdapter?.let {
                    it.setSpinnerItems(habitViewData)

                    // This needs to run again in case the Fragment loaded before identities
                    // were loaded
                    setSpinnerSelection()
                }
            }
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        spinnerLabel.text = getString(R.string.habit_label)

        newHabit.setOnClickListener {
            showNewHabitDialog()
        }

        editStartTime.setOnClickListener {
            showTimePickerDialog(START_TIME_REQUEST)
        }

        editEndTime.setOnClickListener {
            showTimePickerDialog(END_TIME_REQUEST)
        }

        // @todo: this isn't working very well
        editTextTaskName.setOnKeyListener { _, _, _ ->
            isTitleEdited = true
            false
        }
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
        if ( resultCode != Activity.RESULT_OK) {
            return
        }

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

    override fun populateExistingValues() {
        itemViewData.let {
            editTextTaskName.setText(it.title)
            editStartTime.setText(formatTime(it.startTime))
            editEndTime.setText(formatTime(it.endTime))
            setSpinnerSelection()
        }
    }

    override fun saveItemDetails() {
        val writeTaskView = itemViewData

        // @todo: validate end > start
        if (
            editTextTaskName.text.isEmpty()
            || editStartTime.text.isEmpty()
            || editEndTime.text.isEmpty()
        ) {
            // @todo alert user of issue
            return
        }


        writeTaskView.title = editTextTaskName.text.toString()

        writeTaskView.id?.let {
            viewModel.updateTask(writeTaskView)
        } ?: run {
            viewModel.insertTask(writeTaskView)
        }

        dismiss()
    }

    override fun getNewItem() {
        itemViewData = viewModel.getNewTaskView()
    }

    // @TODO: (mostly) duplicated from HabitDetailsFragment
    override fun setSpinnerSelection() {
        val task = itemViewData

        task.habitId?.let { parentHabitId ->
            val position = spinnerAdapter?.getViewDatumPosition(parentHabitId)

            // @todo: figure out why occasionally image is [NA] null while selection text is correct
            position?.let {
                val habit = spinnerAdapter?.getItem(it) as HabitViewData

                spinner.setSelection(it)
                spinnerImage.setImageResource(habit.typeResourceId)
                itemViewData.habitId = habit.id

                if (!isTitleEdited) {
                    editTextTaskName.setText(habit.name)
                }

            }
        }
    }

    // @TODO: (mostly) duplicated from HabitDetailsFragment
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
                    val habit = parent.getItemAtPosition(position) as HabitViewData
                    spinnerImage.setImageResource(habit.typeResourceId)
                    if (!isTitleEdited) {
                        editTextTaskName.setText(habit.name)
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    // @todo: provide option to add identity if none exist yet
                }
            }
        }
    }

    // @TODO: duplicated from HabitDetailsFragment
    private fun initializeSpinnerAdapter() {
        spinnerAdapter = ViewDataSpinnerAdapter(
            parentActivity,
            android.R.layout.simple_spinner_item
        )

        spinner.adapter = spinnerAdapter
    }

    companion object {
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