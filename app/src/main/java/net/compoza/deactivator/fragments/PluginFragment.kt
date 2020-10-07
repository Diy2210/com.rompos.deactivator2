package net.compoza.deactivator.fragments

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
import net.compoza.deactivator.mpp.model.PluginViewModel

class PluginFragment : Fragment() {
    var model = PluginViewModel()
    lateinit var title: String
    lateinit var url: String
    lateinit var token: String
    lateinit var adapter: PluginsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        model = ViewModelProviders.of(this).get(PluginViewModel::class.java)
        adapter = PluginsAdapter(model.pluginList)
        val view = inflater.inflate(R.layout.fragment_plugin, container, false)

        if (arguments?.getString("TITLE") != null && arguments?.getString("URL") != null && arguments?.getString("TOKEN") != null) {
            title = arguments?.getString("TITLE")!!
            url = arguments?.getString("URL")!!
            token = arguments?.getString("TOKEN")!!
        }

        if (activity is AppCompatActivity) {
            (activity as AppCompatActivity).supportActionBar?.title = title
        }

        view.swipeContainer.setOnRefreshListener {
            model.getInitList().addObserver {
                adapter.refreshList(model.pluginList)
            }

            if (swipeContainer.isRefreshing) {
                swipeContainer.isRefreshing = false
            }
        }

        activity?.onBackPressedDispatcher?.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                requireActivity().run {
                    startActivity(Intent(this, MainViewActivity::class.java))
                    finish()
                }
            }
        })

        lifecycleScope.launch {
            view.progressBar.visibility = View.VISIBLE
            try {
                model.launchAsyncRequest(url, token)
            } catch (e: Exception) {
                Toast.makeText(context, e.message.toString(), Toast.LENGTH_SHORT).show()
                toMain()
            } finally {
                view.progressBar.visibility = View.GONE
                view.plugins.adapter = adapter
            }
        }

        return view
    }

    private fun toMain() {
        requireActivity().run {
            startActivity(Intent(this, MainViewActivity::class.java))
            finish()
        }
    }
}