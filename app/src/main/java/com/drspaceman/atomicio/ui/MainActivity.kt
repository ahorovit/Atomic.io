package com.drspaceman.atomicio.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.drspaceman.atomicio.R
import com.drspaceman.atomicio.adapter.IdentityRecyclerViewAdapter
import com.drspaceman.atomicio.viewmodel.IdentityViewModel
import com.drspaceman.atomicio.viewmodel.IdentityViewModel.IdentityView

import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.drawer_view_main.*

class MainActivity : AppCompatActivity() {
    private lateinit var identityRecyclerViewAdapter: IdentityRecyclerViewAdapter
    private val identityViewModel by viewModels<IdentityViewModel>()

    // @todo: KAE isn't working!! Why?
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var identityRecyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        drawerLayout = findViewById(R.id.drawerLayout)
        identityRecyclerView = findViewById(R.id.identityRecyclerView)

        setupToolbar()

//        fab.setOnClickListener { view ->
//
//        }

        initializeIdentityRecyclerView()
    }

    private fun setupToolbar() {
        setSupportActionBar(appToolbar)
        val toggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            appToolbar,
            R.string.open_drawer,
            R.string.close_drawer
        )
        toggle.syncState()

        addIdentityButton.setOnClickListener {
            drawerLayout.closeDrawer(drawerView)
            startIdentityDetails(null)
        }
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

    private fun initializeIdentityRecyclerView() {
        identityRecyclerView.layoutManager = LinearLayoutManager(this)
        identityRecyclerViewAdapter = IdentityRecyclerViewAdapter(null, this)
        identityRecyclerView.adapter = identityRecyclerViewAdapter
        createIdentityObserver()
    }

    // @todo: move into initializeIndenityRecyclerView
    private fun createIdentityObserver() {
        identityViewModel.getIdentityViews()?.observe(
            this,
            Observer<List<IdentityView>> {
                it?.let {
                    identityRecyclerViewAdapter.setIdentityData(it)
                }
            }
        )
    }


//    private fun addAgendaFragment() {
//        val fragmentManager = supportFragmentManager
//        val fragmentTransaction = fragmentManager.beginTransaction()
//        val agendaFragment = AgendaFragment.createInstance()
//
//        fragmentTransaction.add(R.id.mainFragmentContainer, agendaFragment)
//        fragmentTransaction.commit()
//    }



    fun editIdentityDetails(identityId: Long) {
        drawerLayout.closeDrawer(drawerView)
        startIdentityDetails(identityId)
    }

    private fun startIdentityDetails(identityId: Long?) {
        val intent = Intent(this, IdentityDetailActivity::class.java)

        if (identityId != null) {
            intent.putExtra(EXTRA_IDENTITY_ID, identityId)
        }

        startActivity(intent)
    }

    companion object {
        const val EXTRA_IDENTITY_ID = "com.drspaceman.atomicio.EXTRA_IDENTITY_ID"
    }
}
