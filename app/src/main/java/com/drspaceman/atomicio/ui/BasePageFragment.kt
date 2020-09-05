package com.drspaceman.atomicio.ui

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import com.drspaceman.atomicio.adapter.BaseRecyclerViewAdapter
import com.drspaceman.atomicio.viewmodel.BaseViewModel

import kotlinx.android.synthetic.main.fragment_identities.*

abstract class BasePageFragment : Fragment(), BaseRecyclerViewAdapter.EditItemListener {

    protected abstract val layoutId: Int

    protected lateinit var recyclerViewAdapter: BaseRecyclerViewAdapter

    protected abstract val viewModel: BaseViewModel

    protected abstract fun getEditDialogFragment(id: Long?): BaseDialogFragment

    protected abstract fun initializeRecyclerView()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(layoutId, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeRecyclerView()

        fab.setOnClickListener {
            showEditDetailsDialog(null)
        }
    }

    override fun onPause() {
        super.onPause()
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


    protected fun showEditDetailsDialog(itemId: Long?) {
        val fragmentManager = activity?.supportFragmentManager ?: return
        val editDetailsFragment = getEditDialogFragment(itemId)
        editDetailsFragment.show(fragmentManager, IdentityDetailsFragment.TAG)
    }

    override fun editItemDetails(identityId: Long?) {
        showEditDetailsDialog(identityId)
    }
}