package net.compoza.deactivator.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.lifecycleScope
import com.jetbrains.handson.mpp.mobile.R
import com.jetbrains.handson.mpp.mobile.databinding.PluginListItemBinding
import net.compoza.deactivator.activities.MainViewActivity
import net.compoza.deactivator.adapters.PluginsAdapter
import kotlinx.android.synthetic.main.fragment_plugin.*
import kotlinx.android.synthetic.main.fragment_plugin.view.*
import kotlinx.android.synthetic.main.plugin_list_item.view.*
import kotlinx.coroutines.launch
import net.compoza.deactivator.db.Server
import net.compoza.deactivator.mpp.base.myApp
import net.compoza.deactivator.mpp.model.PluginModel
import net.compoza.deactivator.mpp.model.PluginViewModel
import net.compoza.deactivator.mpp.repositories.ServersRepository
import org.kodein.di.instance
import kotlin.properties.Delegates

class PluginFragment : Fragment() {

    lateinit var currentServer: Server
    private var viewModel = PluginViewModel()
    private val repository: ServersRepository by myApp.kodein.instance()

    lateinit var adapter: PluginsAdapter
    private var serverId: Long = 0

    var status by Delegates.notNull<Boolean>()

    @SuppressLint("ShowToast")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        viewModel = ViewModelProviders.of(this).get(PluginViewModel::class.java)

        if (arguments?.getLong("ID") != null) {
            serverId = arguments?.getLong("ID")!!
        }

        // Get server by ID
        lifecycleScope.launch {
            repository.get(serverId).let { server ->
                currentServer = server
//                supportActionBar?.title = server.title
            }
        }.also {
            getPlugins()
            adapter = PluginsAdapter(currentServer, viewModel.pluginList)
        }


        // Set title
        if (activity is AppCompatActivity) {
            (activity as AppCompatActivity).supportActionBar?.title = currentServer.title
        }

        val view = inflater.inflate(R.layout.fragment_plugin, container, false)

        // Refresh data
        view.swipeContainer.setOnRefreshListener {
            viewModel.getInitList().addObserver {
                adapter.refreshList(viewModel.pluginList)
            }

            if (swipeContainer.isRefreshing) {
                swipeContainer.isRefreshing = false
            }
        }

        // Back press assistant
        activity?.onBackPressedDispatcher?.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                requireActivity().run {
                    startActivity(Intent(this, MainViewActivity::class.java))
                    finish()
                }
            }
        })

        viewModel.getStatus().addObserver {
            view.plugins.adapter = adapter
            view.progressBar.visibility = View.GONE
            if (viewModel._status.value == "Error") {
                toMain()
                Toast.makeText(context, getString(R.string.server_error), Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }

    private fun getPlugins() {
        try {
            viewModel.launchAsyncRequest(currentServer)
        } catch (e: Exception) {
            Toast.makeText(context, e.message.toString(), Toast.LENGTH_SHORT).show()
            toMain()
        }
    }

    private fun toMain() {
        requireActivity().run {
            startActivity(Intent(this, MainViewActivity::class.java))
            finish()
        }
    }
}