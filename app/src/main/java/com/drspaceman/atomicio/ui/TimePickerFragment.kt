package com.drspaceman.atomicio.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.drspaceman.atomicio.R

import kotlinx.android.synthetic.main.time_picker.*
import org.threeten.bp.LocalTime
import org.threeten.bp.format.DateTimeFormatter

class TimePickerFragment(initialTime: LocalTime?): DialogFragment() {

    var selectedTime: LocalTime = initialTime ?: LocalTime.now()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.time_picker, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            timePicker.hour = selectedTime.hour
            timePicker.minute = selectedTime.minute
        } else {
            timePicker.currentHour = selectedTime.hour
            timePicker.currentMinute = selectedTime.minute
        }

        okButton.setOnClickListener {
            returnPickedTime()
        }

        cancelButton.setOnClickListener {
            dismiss()
        }

        onClickTime()
    }

    private fun returnPickedTime() {
        val bundle = Bundle()
        bundle.putString(TIME_PICKER_RESULT, selectedTime.format(DateTimeFormatter.ISO_LOCAL_TIME))

        val intent = Intent().putExtras(bundle)
        targetFragment?.onActivityResult(targetRequestCode, Activity.RESULT_OK, intent)

        dismiss()
    }

    private fun onClickTime() {
        timePicker.setOnTimeChangedListener { _, hour, minute ->
            selectedTime = LocalTime.of(hour, minute)
        }
    }

    companion object {
        const val TIME_PICKER_RESULT = "time_picker_result"
        const val START_TIME_REQUEST = 1
        const val END_TIME_REQUEST = 2

        fun newInstance(initialTime: LocalTime?): TimePickerFragment {
            return TimePickerFragment(initialTime)
        }
    }
}