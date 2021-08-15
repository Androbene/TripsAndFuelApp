package ua.androbene.tripsandfuel.trips

import android.icu.util.Calendar
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ua.androbene.tripsandfuel.R
import ua.androbene.tripsandfuel.database.Trip
import ua.androbene.tripsandfuel.databinding.AddTripFragmentBinding

/**
 * A fragment to correct data of trip made.
 */
class EditTripFragment : BaseTripFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = AddTripFragmentBinding.inflate(layoutInflater, container, false)
        bind.tvTitle.text = requireActivity().resources.getString(R.string.edit_trip_title)
        return bind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tripViewModel.beingEditedTrip.observe(viewLifecycleOwner) {
            placingDataToViews(it ?: Trip())
        }
    }

    private fun placingDataToViews(eTrip: Trip) {
        calendar.set(eTrip.year, eTrip.month, eTrip.day) // get date from edited trip
        uniqueID = getIdDependOnDate()
        selectDay(bind.tvDate)
        bind.etDestination.setText(eTrip.destination)
        bind.etDistance.setText(eTrip.distance.toString())
        bind.etConsumption.setText(eTrip.consumption.toString())
        bind.etFuelPrice.setText(eTrip.fuelPrice.toString())
        bind.etComments.setText(eTrip.comments)
        bind.btnSave.setOnClickListener {
            val distance = if (bind.etDistance.text.toString().isEmpty()
            ) 0f else bind.etDistance.text.toString().toFloat()
            val consumption = if (bind.etConsumption.text.toString().isEmpty()
            ) 0f else bind.etConsumption.text.toString().toFloat()
            val fuelPrice = if (bind.etFuelPrice.text.toString().isEmpty()
            ) 0f else bind.etFuelPrice.text.toString().toFloat()
            when {
                distance > DISTANCE_LIMIT -> toastMessage(R.string.big_trip)
                consumption > CONSUMPTION_LIMIT -> toastMessage(R.string.big_fuel)
                fuelPrice > PRICE_LIMIT -> toastMessage(R.string.big_price)
                else -> {
                    tripViewModel.replace(
                        eTrip.longID,
                        Trip(
                            uniqueID,
                            bind.etDestination.text.toString(),
                            distance,
                            consumption,
                            prefsFuelRatio,
                            fuelPrice,
                            bind.etComments.text.toString(),
                            calendar[Calendar.YEAR],
                            calendar[Calendar.MONTH],
                            calendar[Calendar.DAY_OF_MONTH]
                        )
                    )
                    requireActivity().supportFragmentManager.popBackStack()
                    tripViewModel.clearEditedTrip()
                }
            }
        }
    }
}