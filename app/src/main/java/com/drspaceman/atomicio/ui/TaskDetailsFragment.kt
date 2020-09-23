package com.drspaceman.atomicio.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.drspaceman.atomicio.R
import com.drspaceman.atomicio.adapter.ViewDataSpinnerAdapter
import com.drspaceman.atomicio.viewmodel.AgendaPageViewModel
import com.drspaceman.atomicio.viewmodel.AgendaPageViewModel.TaskViewData
import com.drspaceman.atomicio.viewmodel.HabitPageViewModel.HabitViewData

import kotlinx.android.synthetic.main.fragment_task_details.*
import kotlinx.android.synthetic.main.spinner_layout.*
import org.threeten.bp.LocalTime
import org.threeten.bp.format.DateTimeFormatter

class TaskDetailsFragment : BaseDialogFragment() {
    override val layoutId = R.layout.fragment_task_details

    override val itemId: Long? by lazy {
        arguments?.getLong(ARG_TASK_ID, 0)
    }

    override val viewModel by activityViewModels<AgendaPageViewModel>()

    override lateinit var itemViewData: TaskViewData


    override fun setObservers() {
        TODO("Not yet implemented")
    }

    private lateinit var spinnerAdapter: ViewDataSpinnerAdapter

    // If title has been manually edited, don't autofill habit name
    private var isTitleEdited = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        editStartTime.setOnClickListener {
            showTimePickerDialog(TimePickerFragment.START_TIME_CODE)
        }

        editEndTime.setOnClickListener {
            showTimePickerDialog(TimePickerFragment.END_TIME_CODE)
        }

        editTextTaskName.setOnKeyListener { _, _, _ ->
            isTitleEdited = true
            false
        }
    }

    private fun showTimePickerDialog(requestCode: Int) {
        val fragmentManager = activity?.supportFragmentManager ?: return
        val timePicker = TimePickerFragment.newInstance()
        timePicker.setTargetFragment(this, requestCode)
        timePicker.show(fragmentManager, "${timePicker::class}_tag")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode != Activity.RESULT_OK
            || data?.extras?.containsKey(TimePickerFragment.TIME_PICKER_RESULT) != true
        ) {
            return
        }

        val pickedTime = LocalTime.parse(data.extras?.getString(TimePickerFragment.TIME_PICKER_RESULT))

        when (requestCode) {
            TimePickerFragment.START_TIME_CODE -> {
                editStartTime.setText(formatTime(pickedTime))
                itemViewData?.startTime = pickedTime
            }
            TimePickerFragment.END_TIME_CODE -> {
                editEndTime.setText(formatTime(pickedTime))
                itemViewData?.endTime = pickedTime
            }
        }
    }

    private fun formatTime(time: LocalTime?): String {
        var displayTime: String = ""

        time?.let{
            displayTime = it.format(DateTimeFormatter.ofPattern("hh:mm a"))
            if (displayTime[0] == '0'){
                displayTime = displayTime.substring(1)
            }
        }

        return displayTime
    }

    override fun loadExistingItem(id: Long) {
        viewModel.getTask(id)?.observe(
            this,
            Observer<TaskViewData> {
                it?.let {
                    itemViewData = it
                    populateExistingValues()
                }
            }
        )
    }

    override fun populateExistingValues() {
        itemViewData?.let {
            editTextTaskName.setText(it.title)
            editStartTime.setText(formatTime(it.startTime))
            editEndTime.setText(formatTime(it.endTime))
            setSpinnerSelection()
        }
    }

    override fun saveItemDetails() {
        val writeTaskView = itemViewData ?: return

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
        val task = itemViewData ?: return

        task.habitId?.let { parentHabitId ->
            val position = spinnerAdapter.getViewDatumPosition(parentHabitId)

            // @todo: figure out why occasionally image is [NA] null while selection text is correct
            position?.let {
                val habit = spinnerAdapter.getItem(it) as HabitViewData

                spinner.setSelection(it)
                spinnerImage.setImageResource(habit.typeResourceId)
                itemViewData?.let { task ->
                    task.habitId = habit.id
                }

                if(!isTitleEdited){
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
                    if(!isTitleEdited){
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

        viewModel.getSpinnerItems()?.observe(
            viewLifecycleOwner,
            Observer<List<HabitViewData>> {
                it?.let {
                    spinnerAdapter.setSpinnerItems(it)

                    // This needs to run again in case the Fragment loaded before identities
                    // were loaded
                    setSpinnerSelection()
                }
            }
        )
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