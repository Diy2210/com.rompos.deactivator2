package net.compoza.deactivator.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.jetbrains.handson.mpp.mobile.R
import net.compoza.deactivator.activities.MainViewActivity
import com.jetbrains.handson.mpp.mobile.databinding.FragmentEditServerBinding
import kotlinx.android.synthetic.main.fragment_edit_server.*
import kotlinx.coroutines.launch
import net.compoza.deactivator.mpp.base.myApp
import net.compoza.deactivator.mpp.model.ServerFormViewModel
import net.compoza.deactivator.mpp.repositories.ServersRepository
import org.kodein.di.erased.instance

class EditServerFragment : Fragment() {

    private val repository: ServersRepository by myApp.kodein.instance()
    private var serverFormViewModel = ServerFormViewModel()
    private var serverId: Long = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
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
                Toast.makeText(context, getString(R.string.error_empty_field), Toast.LENGTH_SHORT).show()
            }
        }

        return binding.root
    }

    private fun saveRecord(viewModel: ServerFormViewModel) {
        progressBar.visibility = View.VISIBLE
        try {
            repository.save(serverId, viewModel.getModel(serverId))
            toMain()
        } catch (e: Exception) {
//            Utils.snackMsg(editView, e.message.toString())
            println(e.message.toString())
            Toast.makeText(context, e.message.toString(), Toast.LENGTH_SHORT).show()
        } finally {
            progressBar.visibility = View.GONE
        }
    }

    private fun toMain() {
        requireActivity().run {
            startActivity(Intent(this, MainViewActivity::class.java))
            finish()
        }
    }
}