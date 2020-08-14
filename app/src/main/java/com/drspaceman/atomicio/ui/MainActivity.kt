package com.drspaceman.atomicio.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.drspaceman.atomicio.R
import com.drspaceman.atomicio.adapter.HabitRecyclerViewAdapter
import com.drspaceman.atomicio.viewmodel.HabitViewModel

import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {
    private var habitSequence: MutableList<HabitViewModel.Habit>? = null
    private var habitRecyclerViewAdapter: HabitRecyclerViewAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

//        fab.setOnClickListener { view ->
//
//        }

        initializeData()
        initializeRecyclerView()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    // @todo: implement DB and LiveData for habitSequence
    private fun initializeData() {
        habitSequence = mutableListOf(
            HabitViewModel.Habit("wake up", "sleep", 0),
            HabitViewModel.Habit("eat breakfast", "diet", 1),
            HabitViewModel.Habit("walk dog", "exercise", 2)
        )
    }

    private fun initializeRecyclerView() {
        val sequence = habitSequence ?: return

        habitSequenceRecyclerView.layoutManager = LinearLayoutManager(this)

        habitRecyclerViewAdapter = HabitRecyclerViewAdapter(sequence)
        habitSequenceRecyclerView.adapter = habitRecyclerViewAdapter
    }

    private fun moveHabitItem() {
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
                }

                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                TODO("Not yet implemented")
            }

        })
    }
}
