package com.drspaceman.atomicio.ui

import android.os.Bundle
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer

import com.drspaceman.atomicio.R
import com.drspaceman.atomicio.viewmodel.HabitPageViewModel
import com.drspaceman.atomicio.viewmodel.HabitPageViewModel.HabitViewData
import com.drspaceman.atomicio.viewmodel.IdentityPageViewModel

import kotlinx.android.synthetic.main.fragment_habit_details.*

class HabitDetailsFragment : BaseDialogFragment() {

    override val layoutId: Int = R.layout.fragment_habit_details

    override val itemId: Long? by lazy {
        arguments?.getLong(ARG_HABIT_ID, 0)
    }

    override val viewModel by viewModels<HabitPageViewModel>()

    override var itemViewData: HabitViewData? = null


    // @todo: multiple ViewModels? Better than duplicating IdentityViewModel functions...
    private val identityViewModel by viewModels<IdentityPageViewModel>()
    private var identityViews: List<IdentityPageViewModel.IdentityViewData>? = null


    override fun observeItem(id: Long) {
        viewModel.getHabit(id)?.observe(
            this,
            Observer<HabitViewData> {
                it?.let {
                    itemViewData = it
                    populateExistingValues()
                }
            }
        )
    }


//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.fragment_habit_details)
////        populateTypeSpinner()
//
//        setSupportActionBar(toolbar)
//        getIntentData()
//
//        saveHabitButton.setOnClickListener {
//            saveHabitDetails()
//        }
//    }

//    private fun getIntentData() {
//        val itemId = intent.getLongExtra(MainActivity.EXTRA_HABIT_ID, 0)
//
//        if (itemId != 0L) {
//            observeHabit(itemId)
//        } else {
//            habitView = habitViewModel.getNewHabitView()
////            setSpinnerSelection()
//        }
//    }


     override fun populateExistingValues() {
        TODO("not yet implemented")
//        habitView?.let { habitView ->
//            editTextName.setText(habitView.name)
//            setSpinnerSelection()
//        }
    }


    override fun getNewItem() {
        itemViewData = viewModel.getNewHabitView()
    }

//    override fun setSpinnerSelection(position: Int) {
//        val parentIdentity = identityViews?.get(position) ?: return
//
//        parentIdentity.let {
//            val type = it.type ?: return
//            spinnerIdentities.setSelection(position)
//            imageViewHabitType.setImageResource(it.typeResourceId)
//        }
//    }

    override fun saveItemDetails() {
        val writeHabitView = itemViewData ?: return

        val name = editTextHabitName.text.toString()
        if (name.isEmpty()) {
            return
        }

        writeHabitView.let {
            it.name = name
            it.type = spinnerIdentities.selectedItem as String
        }

        writeHabitView.id?.let {
            viewModel.updateHabit(writeHabitView)
        } ?: run {
            viewModel.insertHabit(writeHabitView)
        }

        dismiss()
    }

    companion object {
        private const val ARG_HABIT_ID = "extra_habit_id"

        fun newInstance(itemId: Long?): HabitDetailsFragment {
            val instance = HabitDetailsFragment()

            itemId?.let {
                val args = Bundle()
                args.putLong(ARG_HABIT_ID, itemId)
                instance.arguments = args
            }

            return instance
        }
    }
}