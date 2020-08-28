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
import com.drspaceman.atomicio.viewmodel.HabitViewModel
import com.drspaceman.atomicio.viewmodel.IdentityViewModel

import kotlinx.android.synthetic.main.activity_habit_detail.*

class HabitDetailActivity : AppCompatActivity() {
    private val habitViewModel by viewModels<HabitViewModel>()
    private var habitView: HabitViewModel.HabitView? = null

    // @todo: multiple ViewModels? Better than duplicating IdentityViewModel functions...
    private val identityViewModel by viewModels<IdentityViewModel>()
    private var identityViews: List<IdentityViewModel.IdentityView>? = null

    // @todo: KAE isn't working for these Views for some reason
    private lateinit var imageViewType: ImageView
    private lateinit var spinnerTypes: Spinner
    private lateinit var spinnerAdapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_habit_detail)
//        populateTypeSpinner()

        setSupportActionBar(toolbar)
        getIntentData()

        saveHabitButton.setOnClickListener {
            saveHabitDetails()
        }
    }

    private fun getIntentData() {
        val habitId = intent.getLongExtra(MainActivity.EXTRA_HABIT_ID, 0)

        if (habitId != 0L) {
            observeHabit(habitId)
        } else {
            habitView = habitViewModel.getNewHabitView()
//            setSpinnerSelection()
        }
    }

    private fun observeHabit(habitId: Long) {
        habitViewModel.getHabit(habitId)?.observe(
            this,
            Observer<HabitViewModel.HabitView> {
                it?.let {
                    habitView = it
                    populateExistingValues()
                }
            }
        )
    }

    private fun populateExistingValues() {
        TODO("not yet implemented")
//        habitView?.let { habitView ->
//            editTextName.setText(habitView.name)
//            setSpinnerSelection()
//        }
    }

    private fun populateTypeSpinner() {
        // @TODO: Figure out why KAE isn't working
        spinnerTypes = findViewById(R.id.spinnerIdentities)
        imageViewType = findViewById(R.id.imageViewHabitType)

        spinnerAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
//            habitViewModel.getIdentities()
            arrayOf() // @todo: get Identities for spinner
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
                    TODO("Not yet implemented")
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    // Required by OnItemSelectedListener interface, but not needed
                }
            }
        }
    }

    private fun setSpinnerSelection(position: Int) {
        val parentIdentity = identityViews?.get(position) ?: return

        parentIdentity.let {
            val type = it.type ?: return
            spinnerTypes.setSelection(position)
            imageViewType.setImageResource(it.typeResourceId)
        }
    }

    private fun saveHabitDetails() {
        val writeHabitView = habitView?: return

        val name = editTextHabitName.text.toString()
        if (name.isEmpty()) {
            return
        }

        writeHabitView.let {
            it.name = name
            it.type = spinnerTypes.selectedItem as String
        }

        writeHabitView.id?.let {
            habitViewModel.updateHabit(writeHabitView)
        } ?: run {
            habitViewModel.insertHabit(writeHabitView)
        }

        finish()
    }
}