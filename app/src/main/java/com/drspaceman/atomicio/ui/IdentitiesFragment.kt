package com.drspaceman.atomicio.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.drspaceman.atomicio.R
import com.drspaceman.atomicio.adapter.IdentityRecyclerViewAdapter
import com.drspaceman.atomicio.adapter.IdentityRecyclerViewAdapter.EditIdentityListener
import com.drspaceman.atomicio.viewmodel.IdentityViewModel
import kotlinx.android.synthetic.main.fragment_identities.*

class IdentitiesFragment : Fragment(), EditIdentityListener {
    private lateinit var identityRecyclerViewAdapter: IdentityRecyclerViewAdapter
    private val identityViewModel by viewModels<IdentityViewModel>()

    // @todo: KAE isn't working!! Why?
    private lateinit var identityRecyclerView: RecyclerView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_identities, container, false)

        identityRecyclerView = view.findViewById(R.id.identityRecyclerView)
        initializeIdentityRecyclerView()

        return view
    }


    private fun initializeIdentityRecyclerView() {
        identityRecyclerView.layoutManager = LinearLayoutManager(context)
        identityRecyclerViewAdapter = IdentityRecyclerViewAdapter(null, this)
        identityRecyclerView.adapter = identityRecyclerViewAdapter

        identityViewModel.getIdentityViews()?.observe(
            viewLifecycleOwner,
            Observer<List<IdentityViewModel.IdentityView>> {
                it?.let {
                    identityRecyclerViewAdapter.setIdentityData(it)
                }
            }
        )
    }

    override fun editIdentityDetails(identityId: Long?) {
        showIdentityDetailsFragment(identityId)
    }


    private fun showIdentityDetailsFragment(identityId: Long?) {
        val fragmentManager = activity?.supportFragmentManager ?: return
        val identityDetailsFragment = IdentityDetailsFragment.newInstance(identityId)
        identityDetailsFragment.show(fragmentManager, IdentityDetailsFragment.TAG)
    }


    companion object {
        fun newInstance() = IdentitiesFragment()
    }
}