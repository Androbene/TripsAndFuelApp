package ua.androbene.tripsandfuel.user_interface.trips

import android.icu.util.Calendar
import android.os.Bundle
import android.view.View
import ua.androbene.tripsandfuel.R
import ua.androbene.tripsandfuel.database.Trip

/**
 * A fragment to correct data of trip made.
 */
class EditTripFragment : BaseTripFragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tvTitle.text = requireActivity().resources.getString(R.string.edit_trip_title)
        tripViewModel.beingEditedTrip.observe(viewLifecycleOwner) {
            placingDataToViews(it ?: Trip())
        }
    }

    private fun placingDataToViews(eTrip: Trip) = with(binding) {
        calendar.set(eTrip.year, eTrip.month, eTrip.day) // get date from edited trip
        uniqueID = getIdDependOnDate()
        selectDay(tvDate)
        etDestination.setText(eTrip.destination)
        etDistance.setText(eTrip.distance.toString())
        etConsumption.setText(eTrip.consumption.toString())
        etFuelPrice.setText(eTrip.fuelPrice.toString())
        etComments.setText(eTrip.comments)
        btnSave.setOnClickListener {
            val distance = if (etDistance.text.toString().isEmpty()
            ) 0f else etDistance.text.toString().toFloat()
            val consumption = if (etConsumption.text.toString().isEmpty()
            ) 0f else etConsumption.text.toString().toFloat()
            val fuelPrice = if (etFuelPrice.text.toString().isEmpty()
            ) 0f else etFuelPrice.text.toString().toFloat()
            when {
                distance > DISTANCE_LIMIT -> toast(R.string.big_trip)
                consumption > CONSUMPTION_LIMIT -> toast(R.string.big_fuel)
                fuelPrice > PRICE_LIMIT -> toast(R.string.big_price)
                else -> {
                    tripViewModel.replace(
                        eTrip.longID,
                        Trip(
                            uniqueID,
                            etDestination.text.toString(),
                            distance,
                            consumption,
                            prefsFuelRatio,
                            fuelPrice,
                            etComments.text.toString(),
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