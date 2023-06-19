package ua.androbene.tripsandfuel.user_interface.trips

import android.icu.util.Calendar
import android.os.Bundle
import android.view.View
import ua.androbene.tripsandfuel.R
import ua.androbene.tripsandfuel.database.Trip

/**
 * A fragment to put data of trip made.
 */
class AddTripFragment : BaseTripFragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpUi()
    }

    private fun setUpUi() = with(binding) {
        uniqueID = getIdDependOnDate()
        selectDay(tvDate)
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
                    tripViewModel.addTrip(
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
                }
            }
        }
    }

}