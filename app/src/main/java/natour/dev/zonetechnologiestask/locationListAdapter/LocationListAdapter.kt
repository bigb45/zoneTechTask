package natour.dev.zonetechnologiestask.locationListAdapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import natour.dev.zonetechnologiestask.databinding.LocationUpdateTileBinding
import natour.dev.zonetechnologiestask.domain.model.LocationModel

class LocationModelDiffUtil : DiffUtil.ItemCallback<LocationModel>() {
    override fun areItemsTheSame(
        oldItem: LocationModel, newItem: LocationModel
    ): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(
        oldItem: LocationModel, newItem: LocationModel
    ): Boolean {
        return oldItem.lat == newItem.lat && oldItem.lon == newItem.lon
    }

}

class LocationUpdateTileViewHolder(private val binding: LocationUpdateTileBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(item: LocationModel) {
        with(binding) {
            lon.text = item.lon
            lat.text = item.lat
            updateTime.text = item.timestamp

        }
    }

}

class LocationListAdapter :
    ListAdapter<LocationModel, LocationUpdateTileViewHolder>(
        LocationModelDiffUtil()
    ) {
    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): LocationUpdateTileViewHolder {
        val binding =
            LocationUpdateTileBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LocationUpdateTileViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LocationUpdateTileViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

}