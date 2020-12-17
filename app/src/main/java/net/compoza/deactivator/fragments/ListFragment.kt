package net.compoza.deactivator.fragments

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.lifecycleScope
import com.jetbrains.handson.mpp.mobile.R
import com.jetbrains.handson.mpp.mobile.databinding.FragmentListBinding
import net.compoza.deactivator.adapters.ServersAdapter
import kotlinx.coroutines.launch
import net.compoza.deactivator.Utils
import net.compoza.deactivator.db.Server
import net.compoza.deactivator.mpp.model.ListViewModel

open class ListFragment : Fragment() {
    private var _viewBinding: FragmentListBinding? = null
    private val viewBinding get() = _viewBinding!!

    lateinit var transaction: FragmentTransaction
    lateinit var adapter: ServersAdapter

    val bundle = Bundle()
    var viewModel = ListViewModel()
    val editServerFragment = EditServerFragment()
    val pluginFragment = PluginFragment()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _viewBinding = FragmentListBinding.inflate(inflater, container, false)
        val view = viewBinding.root

        lifecycleScope.launch {
            viewBinding.progressBar.visibility = View.VISIBLE
            viewModel.getInitList()
        }.also {
            adapter = ServersAdapter(
                viewModel.list,
                object : ServersAdapter.ClickCallback {
                    override fun onItemClicked(item: Server) {
                        bundle.putLong("ID", item.ID)
                        pluginFragment.arguments = bundle
                        transaction = activity!!.supportFragmentManager.beginTransaction()
                        transaction.replace(R.id.fragment_list_view, pluginFragment)
                        transaction.disallowAddToBackStack()
                        transaction.commit()
                        viewBinding.serversItem.visibility = View.GONE
                        viewBinding.fab.visibility = View.GONE
                    }
                },
                object : ServersAdapter.EditClickCallback {
                    override fun onEditItemClicked(item: Server) {
                        bundle.putLong("ID", item.ID)
                        editServerFragment.arguments = bundle
                        transaction = activity!!.supportFragmentManager.beginTransaction()
                        transaction.replace(R.id.fragment_list_view, editServerFragment)
                        transaction.disallowAddToBackStack()
                        transaction.commit()
                        viewBinding.serversItem.visibility = View.GONE
                        viewBinding.fab.visibility = View.GONE
                    }
                },
                object :
                    ServersAdapter.DeleteClickCallback {
                    override fun onDeleteItemClicked(item: Server) {
                        AlertDialog.Builder(context)
                            .setTitle(getString(R.string.confirm_delete, item.title))
                            .setCancelable(true)
                            .setPositiveButton(R.string.yes) { _, _ ->
                                viewModel.delete(item)
                                viewModel.reload()
                                adapter.notifyDataSetChanged()
                                Utils.snackMsg(view, getString(R.string.deleted))
                            }
                            .setNegativeButton(R.string.no) { _, _ ->
                                // nothing to do
                            }
                            .show()
                    }
                })

            viewBinding.serversItem.adapter = adapter
            viewBinding.progressBar.visibility = View.GONE
        }

        viewModel.getInitList().addObserver {
            adapter.refreshList(it)
        }

        viewBinding.swipeContainer.setOnRefreshListener {
            viewModel.reload()
            adapter.items = viewModel.list
            adapter.notifyDataSetChanged()
            if (viewBinding.swipeContainer.isRefreshing) {
                viewBinding.swipeContainer.isRefreshing = false
            }
        }

        viewBinding.fab.setOnClickListener {
            transaction = activity!!.supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_list_view, EditServerFragment())
            transaction.disallowAddToBackStack()
            transaction.commit()
            viewBinding.serversItem.visibility = View.GONE
            viewBinding.fab.visibility = View.GONE
        }
        return view
    }

    // Clear View Binding
    override fun onDestroyView() {
        super.onDestroyView()
        _viewBinding = null
    }
}