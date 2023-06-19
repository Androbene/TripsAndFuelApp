package ua.androbene.tripsandfuel.user_interface.trips

import android.app.DatePickerDialog
import android.icu.text.DateFormat
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import ua.androbene.tripsandfuel.databinding.FragmentAdTripBinding
import ua.androbene.tripsandfuel.user_interface.base.BaseFragment
import java.util.*

abstract class BaseTripFragment : BaseFragment<FragmentAdTripBinding>() {

    companion object {
        const val DISTANCE_LIMIT = 10000f
        const val CONSUMPTION_LIMIT = 100f
        const val PRICE_LIMIT = 100f
    }

    override fun inflateFrag(inflater: LayoutInflater) = FragmentAdTripBinding .inflate(inflater)

    protected val tripViewModel: TripsViewModel by activityViewModels()
    protected val calendar: Calendar = Calendar.getInstance() // temporary init with current date
    protected var prefsFuelRatio: Float = 1.0f
    protected var uniqueID = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val packageName = requireActivity().applicationInfo.packageName
        prefsFuelRatio =
            requireActivity().getSharedPreferences(packageName + "_preferences", 0)
                .getString("fuel_ratio", "1.0")!!.toFloat()
    }

    protected fun getIdDependOnDate(): Long {
        return SimpleDateFormat("yyyyMMddHHmmssSSS", Locale.getDefault()).format(calendar).toLong()
    }

    protected fun selectDay(textView: TextView) {
        textView.text = DateFormat.getDateInstance(DateFormat.MEDIUM).format(calendar.time)
        textView.setOnClickListener {
            DatePickerDialog(
                requireContext(),
                { _, _year, _month, _day ->
                    calendar.set(_year, _month, _day) // выбор даты из диалога
                    textView.text =
                        DateFormat.getDateInstance(DateFormat.MEDIUM).format(calendar.time)
                    uniqueID = getIdDependOnDate()
                },
                calendar[Calendar.YEAR],
                calendar[Calendar.MONTH],
                calendar[Calendar.DAY_OF_MONTH]
            ).show()
        }
    }
}
