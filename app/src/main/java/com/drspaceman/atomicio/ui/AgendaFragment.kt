package com.drspaceman.atomicio.ui

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.drspaceman.atomicio.R
import com.drspaceman.atomicio.adapter.HabitRecyclerViewAdapter
import com.drspaceman.atomicio.viewmodel.HabitViewModel

import kotlinx.android.synthetic.main.fragment_agenda.*
import java.util.*

class AgendaFragment : Fragment() {
    private var habitSequence: MutableList<HabitViewModel.HabitViewData>? = null
    private var habitRecyclerViewAdapter: HabitRecyclerViewAdapter? = null

    // @todo: KAE isn't working!! Why?
    private lateinit var habitSequenceRecyclerView: RecyclerView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // @todo: required if adding to the options menu
//        setHasOptionsMenu(true)
    }

    // @todo: add options to menu
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
    }

    // @todo handle menu option selection
    // NOTE: Activity will run onOptionsItemSelected first, and must not handle items specific to this fragment
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return super.onOptionsItemSelected(item)
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_agenda, container, false)

        // @todo: Move into onCreate()?
        habitSequenceRecyclerView = view.findViewById(R.id.habitSequenceRecyclerView)

        initializeData()
        initializeHabitSequenceRecyclerView(view)

        return view
    }

    override fun onPause() {
        super.onPause()
    }

    // @todo: implement DB and LiveData for habitSequence
    private fun initializeData() {
        habitSequence = mutableListOf(
            HabitViewModel.HabitViewData("wake up", "sleep", 0),
            HabitViewModel.HabitViewData("eat breakfast", "diet", 1),
            HabitViewModel.HabitViewData("walk dog", "exercise", 2)
        )
    }

    private fun initializeHabitSequenceRecyclerView(view: View) {
        val sequence = habitSequence ?: return

        habitSequenceRecyclerView.layoutManager = LinearLayoutManager(this.context)
        habitRecyclerViewAdapter = HabitRecyclerViewAdapter(sequence)
        habitSequenceRecyclerView.adapter = habitRecyclerViewAdapter

//        attachItemTouchHelper()
    }

    /**
     * WIP: Make habitSequence items draggable for reordering
     */
    private fun attachItemTouchHelper() {


        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            0
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                dragged: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {

                habitSequence?.let {
                    val positionDragged = dragged.adapterPosition
                    val positionTarget = target.adapterPosition

                    Collections.swap(it, positionDragged, positionTarget)

                    habitRecyclerViewAdapter?.notifyItemMoved(positionDragged, positionTarget)
                }

                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                TODO("Not yet implemented")
            }

        })

        itemTouchHelper.attachToRecyclerView(habitSequenceRecyclerView)
    }

    companion object {
        fun createInstance(): AgendaFragment {
            return AgendaFragment()
        }
    }

}