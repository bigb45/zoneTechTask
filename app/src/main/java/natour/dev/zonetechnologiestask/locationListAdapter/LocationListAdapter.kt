package natour.dev.zonetechnologiestask.locationListAdapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import natour.dev.zonetechnologiestask.databinding.LocationUpdateTileBinding
import natour.dev.zonetechnologiestask.model.LocationUpdateTileData

class LocationTileDiffUtil : DiffUtil.ItemCallback<LocationUpdateTileData>() {
    override fun areItemsTheSame(
        oldItem: LocationUpdateTileData, newItem: LocationUpdateTileData
    ): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(
        oldItem: LocationUpdateTileData, newItem: LocationUpdateTileData
    ): Boolean {
        return oldItem.lat == newItem.lat && oldItem.lon == newItem.lon
    }

}

class LocationUpdateTileViewHolder(private val binding: LocationUpdateTileBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(item: LocationUpdateTileData) {
        with(binding) {
            lon.text = item.lon
            lat.text = item.lat
            updateTime.text = item.pushDate

        }
    }

}

class LocationListAdapter :
    ListAdapter<LocationUpdateTileData, LocationUpdateTileViewHolder>(LocationTileDiffUtil()) {
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