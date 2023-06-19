package ua.androbene.tripsandfuel.user_interface.trips

import android.icu.text.DateFormat
import android.icu.util.Calendar
import android.view.ContextMenu
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ua.androbene.tripsandfuel.R
import ua.androbene.tripsandfuel.database.Trip
import ua.androbene.tripsandfuel.databinding.ItemTripBinding
import java.util.*

class TripAdapter : RecyclerView.Adapter<TripAdapter.TripViewHolder>(), Filterable {

    companion object {
        const val MENU_DEL_ID = 1000
        const val MENU_EDIT_ID = 1001
        const val MENU_CLEAR_ID = 1002
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Trip>() {
            override fun areItemsTheSame(oldTrip: Trip, newTrip: Trip): Boolean {
                return oldTrip.longID == newTrip.longID
            }

            override fun areContentsTheSame(oldTrip: Trip, newTrip: Trip): Boolean {
                return oldTrip == newTrip
            }
        }
    }

    private var data: List<Trip> = emptyList()
    private var dataFiltered: List<Trip> = emptyList()
    private val differ = object : AsyncListDiffer<Trip>(this, DIFF_CALLBACK) {}
    var contextSelectedItemID = 0L

    fun setData(_data: List<Trip>?) {
        data = _data ?: emptyList()
        dataFiltered = data
        differ.submitList(data)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TripViewHolder {
        val binding = ItemTripBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TripViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TripViewHolder, position: Int) {
        val trip = differ.currentList[position]
        holder.bindTrip(trip)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val key = constraint.toString().trim().toLowerCase(Locale.ROOT)
                dataFiltered = if (key.isEmpty()) {
                    data
                } else {
                    val listFiltered = ArrayList<Trip>()
                    for (trip in data)
                        if (trip.destination.toLowerCase(Locale.ROOT).contains(key)
                            || trip.comments.toLowerCase(Locale.ROOT).contains(key)
                        ) listFiltered.add(trip)
                    listFiltered
                }
                val filterResults = FilterResults()
                filterResults.values = dataFiltered
                return filterResults
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                val result = results?.values as List<Trip>
                differ.submitList(result)
            }
        }
    }

    /////////////////////////////////////  HOLDER   ////////////////////////////////////////////////
    inner class TripViewHolder(private val bind: ItemTripBinding) :
        RecyclerView.ViewHolder(bind.root), View.OnCreateContextMenuListener {

        init {
            bind.containerCard.setOnCreateContextMenuListener(this)
        }

        fun bindTrip(trip: Trip) {
            bind.image.animation =
                AnimationUtils.loadAnimation(itemView.context, R.anim.fade_transition_animation)
            bind.image.setImageResource(R.drawable.fuel)
            bind.tvDestination.text = trip.destination
            bind.tvDate.text = getTripDateAsString(trip)
            bind.tvDistance.text = String.format("%.1f", trip.distance)
            bind.tvConsumption.text = String.format("%.1f", trip.consumption)
            bind.tvFuelPrice.text = String.format("%.2f", trip.fuelPrice)
            val tripCost =
                trip.distance / 100 * trip.consumption * trip.fuelPrice * trip.fuelRatio // formula
            bind.tvCost.text = String.format("%.2f грн", tripCost)
            if (trip.comments.isEmpty())
                bind.tvComments.visibility = View.GONE
            else {
                bind.tvComments.visibility = View.VISIBLE
                bind.tvComments.text = trip.comments
            }

        }

        override fun onCreateContextMenu(
            menu: ContextMenu?, v: View?, menuInfo: ContextMenu.ContextMenuInfo?
        ) {
            if (this.adapterPosition != RecyclerView.NO_POSITION) {
                menu?.add(
                    0, MENU_EDIT_ID, 0, itemView.resources.getString(R.string.context_item_edit)
                )
                menu?.add(
                    0, MENU_DEL_ID, 0, itemView.resources.getString(R.string.context_item_delete)
                )
//                menu?.add(0, MENU_CLEAR_ID, 0, "Delete all !")
            }
            contextSelectedItemID = dataFiltered[adapterPosition].longID
        }

        private fun getTripDateAsString(t: Trip): String {
            val currCalendar = Calendar.getInstance()
            currCalendar.set(t.year, t.month, t.day)
            return DateFormat.getDateInstance(DateFormat.MEDIUM).format(currCalendar.time)
        }
    }
}
