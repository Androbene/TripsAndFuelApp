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
 * A fragment to put data of trip made.
 */
class AddTripFragment : BaseTripFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = AddTripFragmentBinding.inflate(layoutInflater, container, false)
        uniqueID = getIdDependOnDate()
        selectDay(bind.tvDate)
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
                    tripViewModel.addTrip(
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
                }
            }
        }
        return bind.root
    }

}