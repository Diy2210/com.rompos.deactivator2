package net.compoza.deactivator.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jetbrains.handson.mpp.mobile.databinding.PluginListItemBinding
import dev.icerock.moko.mvvm.livedata.MutableLiveData
import net.compoza.deactivator.db.Server
import net.compoza.deactivator.mpp.model.PluginModel
import net.compoza.deactivator.mpp.model.PluginViewModel

class PluginsAdapter(
    var server: Server,
    var items: MutableLiveData<List<PluginModel>>
) : RecyclerView.Adapter<PluginsAdapter.ItemTableViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ItemTableViewHolder {
        return ItemTableViewHolder(PluginListItemBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun getItemCount() = items.value.size

    fun refreshList(items: MutableLiveData<List<PluginModel>>) {
        this.items = items
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ItemTableViewHolder, position: Int) {
        holder.bind(items.value[position])
    }

    inner class ItemTableViewHolder(private val binding: PluginListItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: PluginModel) {
            binding.item = item
            binding.server = server
            binding.handler = PluginViewModel()
            binding.executePendingBindings()
        }
    }
}