package net.compoza.deactivator.fragments

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.lifecycleScope
import com.jetbrains.handson.mpp.mobile.R
import net.compoza.deactivator.adapters.ServersAdapter
import kotlinx.android.synthetic.main.fragment_list.view.*
import kotlinx.coroutines.launch
import net.compoza.deactivator.db.Server
import net.compoza.deactivator.mpp.model.ListViewModel

open class ListFragment : Fragment() {
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
        viewModel = ViewModelProviders.of(this).get(ListViewModel::class.java)
        val view = inflater.inflate(R.layout.fragment_list, container, false)

        lifecycleScope.launch {
            view.progressBar.visibility = View.VISIBLE
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
                        view.servers_item.isVisible = false
                        view.fab.isVisible = false
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
                        view.servers_item.isVisible = false
                        view.fab.isVisible = false
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
                                Toast.makeText(context, getString(R.string.deleted), Toast.LENGTH_LONG).show()
                            }
                            .setNegativeButton(R.string.no) { _, _ ->
                                // nothing to do
                            }
                            .show()
                    }
                }
            )
            view.servers_item.adapter = adapter
            view.progressBar.visibility = View.GONE
        }

        viewModel.getInitList().addObserver {
            adapter.refreshList(it)
        }

        view.swipeContainer.setOnRefreshListener {
            viewModel.reload()
            adapter.items = viewModel.list
            adapter.notifyDataSetChanged()
            if (view.swipeContainer.isRefreshing) {
                view.swipeContainer.isRefreshing = false
            }
        }

        view.fab.setOnClickListener {
            transaction = activity!!.supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_list_view, EditServerFragment())
            transaction.disallowAddToBackStack()
            transaction.commit()
            view.servers_item.isVisible = false
            view.fab.isVisible = false
        }

        return view
    }
}