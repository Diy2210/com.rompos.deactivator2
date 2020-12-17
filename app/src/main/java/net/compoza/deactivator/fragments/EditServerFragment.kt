package net.compoza.deactivator.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.jetbrains.handson.mpp.mobile.R
import net.compoza.deactivator.activities.MainViewActivity
import com.jetbrains.handson.mpp.mobile.databinding.FragmentEditServerBinding
import kotlinx.coroutines.launch
import net.compoza.deactivator.Utils
import net.compoza.deactivator.mpp.base.myApp
import net.compoza.deactivator.mpp.model.ServerFormViewModel
import net.compoza.deactivator.mpp.repositories.ServersRepository
import org.kodein.di.instance

const val EDIT_MODEL = "editModel"
const val EDIT_MODEL_ID = "editModelId"

open class EditServerFragment : Fragment() {
    private var _viewBinding: FragmentEditServerBinding? = null
    private val viewBinding get() = _viewBinding!!

    private val repository: ServersRepository by myApp.kodein.instance()
    private var serverFormViewModel = ServerFormViewModel()
    private var serverId: Long = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _viewBinding = FragmentEditServerBinding.inflate(inflater, container, false)
        val view = viewBinding.root

        val binding : FragmentEditServerBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_edit_server, container, false)
        binding.item = serverFormViewModel

        // Dispatcher Back Step to Main
        activity?.onBackPressedDispatcher?.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                toMain()
            }
        })

        if (arguments?.getLong("ID") != null) {
            serverId = arguments?.getLong("ID")!!
        }

        // Set title
        if (activity is AppCompatActivity) {
            (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.edit_server)
        }

        if (serverId > 0) {
            lifecycleScope.launch {
                binding.progressBar.visibility = View.VISIBLE
                repository.get(serverId).let { server ->
                    serverFormViewModel.setForm(server)
                }
            }.also {
                binding.progressBar.visibility = View.GONE
            }
        }

        binding.item = serverFormViewModel

        binding.cancelBtn.setOnClickListener {
            toMain()
        }

        binding.saveBtn.setOnClickListener {
            if (serverFormViewModel.isFormValid()) {
                lifecycleScope.launch {
                    saveRecord(serverFormViewModel)
                    requireActivity().run {
                        startActivity(Intent(this, MainViewActivity::class.java))
                        finish()
                    }
                }.also {
                    toMain()
                }
            } else {
                Utils.snackMsg(view, getString(R.string.error_empty_field))
            }
        }

        return binding.root
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putLong(EDIT_MODEL_ID, serverId)
        outState.putParcelable(EDIT_MODEL, serverFormViewModel.getModel())
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        if (savedInstanceState != null) {
            serverId = savedInstanceState.getLong(EDIT_MODEL_ID)
            serverFormViewModel.setForm(savedInstanceState.getParcelable(EDIT_MODEL)!!)
        }
    }

    private fun saveRecord(viewModel: ServerFormViewModel) {
        viewBinding.progressBar.visibility = View.VISIBLE
        try {
            repository.save(serverId, viewModel.getModel(serverId))
            toMain()
        } catch (e: Exception) {
            view?.let { Utils.snackMsg(it, e.message.toString()) }
        } finally {
            viewBinding.progressBar.visibility = View.GONE
        }
    }

    private fun toMain() {
        requireActivity().run {
            startActivity(Intent(this, MainViewActivity::class.java))
            finish()
        }
    }
}