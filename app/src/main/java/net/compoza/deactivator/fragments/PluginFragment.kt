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
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.lifecycleScope
import com.jetbrains.handson.mpp.mobile.R
import net.compoza.deactivator.activities.MainViewActivity
import net.compoza.deactivator.adapters.PluginsAdapter
import kotlinx.android.synthetic.main.fragment_plugin.*
import kotlinx.android.synthetic.main.fragment_plugin.view.*
import kotlinx.coroutines.launch
import net.compoza.deactivator.db.Server
import net.compoza.deactivator.mpp.base.myApp
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

    @SuppressLint("ShowToast")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_plugin, container, false)
        viewModel = ViewModelProviders.of(this).get(PluginViewModel::class.java)

        view.progressBar.visibility = View.VISIBLE

        if (arguments?.getLong("ID") != null) {
            serverId = arguments?.getLong("ID")!!
        }

        // Get server by ID
        lifecycleScope.launch {
            repository.get(serverId).let { server ->
                currentServer = server
            }
        }.also {
            getPlugins()
            adapter = PluginsAdapter(currentServer, viewModel.pluginList)
        }

        // Set title
        if (activity is AppCompatActivity) {
            (activity as AppCompatActivity).supportActionBar?.title = currentServer.title
        }

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
            if (viewModel._status.value == "Error") {
                toMain()
                Toast.makeText(context, getString(R.string.server_error), Toast.LENGTH_SHORT).show()
            } else if (viewModel._status.value == "Success") {
                view.progressBar.visibility = View.GONE
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