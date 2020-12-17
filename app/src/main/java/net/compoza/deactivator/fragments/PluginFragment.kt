package net.compoza.deactivator.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.jetbrains.handson.mpp.mobile.R
import net.compoza.deactivator.activities.MainViewActivity
import net.compoza.deactivator.adapters.PluginsAdapter
import com.jetbrains.handson.mpp.mobile.databinding.FragmentPluginBinding
import kotlinx.coroutines.launch
import net.compoza.deactivator.Utils
import net.compoza.deactivator.db.Server
import net.compoza.deactivator.mpp.base.myApp
import net.compoza.deactivator.mpp.model.PluginViewModel
import net.compoza.deactivator.mpp.repositories.ServersRepository
import org.kodein.di.instance

class PluginFragment : Fragment() {
    private var _viewBinding: FragmentPluginBinding? = null
    private val viewBinding get() = _viewBinding!!

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
        _viewBinding = FragmentPluginBinding.inflate(inflater, container, false)
        val view = viewBinding.root

        viewBinding.progressBar.visibility = View.VISIBLE

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
        viewBinding.swipeContainer.setOnRefreshListener {
            viewModel.getInitList().addObserver {
                adapter.refreshList(viewModel.pluginList)
            }

            if (viewBinding.swipeContainer.isRefreshing) {
                viewBinding.swipeContainer.isRefreshing = false
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
            viewBinding.plugins.adapter = adapter
            if (viewModel._status.value == "Error") {
                toMain()
                Utils.snackMsg(view, getString(R.string.server_error))
            } else if (viewModel._status.value == "Success") {
                viewBinding.progressBar.visibility = View.GONE
            }
        }

        return view
    }

    private fun getPlugins() {
        try {
            viewModel.launchAsyncRequest(currentServer)
        } catch (e: Exception) {
            view?.let { Utils.snackMsg(it, e.message.toString()) }
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