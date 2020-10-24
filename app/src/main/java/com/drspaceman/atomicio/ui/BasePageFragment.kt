package com.drspaceman.atomicio.ui

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import com.drspaceman.atomicio.R
import com.drspaceman.atomicio.adapter.BaseRecyclerViewAdapter
import com.drspaceman.atomicio.adapter.BaseRecyclerViewAdapter.EditItemListener
import com.drspaceman.atomicio.viewmodel.BaseViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext

abstract class BasePageFragment : Fragment(), CoroutineScope, EditItemListener {

    protected abstract val fragmentTitle: String

    protected abstract val layoutId: Int

    protected abstract val viewModel: BaseViewModel

    protected lateinit var recyclerViewAdapter: BaseRecyclerViewAdapter

    protected lateinit var fab: FloatingActionButton

    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main

    protected abstract fun getEditDialogFragment(id: Long?): BaseDialogFragment

    protected abstract fun loadPageData()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(layoutId, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activity?.title = fragmentTitle

        loadPageData()

        fab = view.findViewById(R.id.fab)

        fab.setOnClickListener {
            showEditDetailsDialog(null)
        }
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
        editDetailsFragment.show(fragmentManager, "${editDetailsFragment::class}_tag")
    }

    override fun editItemDetails(itemId: Long?) {
        showEditDetailsDialog(itemId)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        job.cancel()
    }
}