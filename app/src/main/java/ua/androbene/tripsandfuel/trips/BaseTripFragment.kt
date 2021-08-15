package ua.androbene.tripsandfuel.trips

import android.app.DatePickerDialog
import android.icu.text.DateFormat
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import ua.androbene.tripsandfuel.databinding.AddTripFragmentBinding
import java.util.*

abstract class BaseTripFragment : Fragment() {

    companion object {
        const val DISTANCE_LIMIT = 10000f
        const val CONSUMPTION_LIMIT = 100f
        const val PRICE_LIMIT = 100f
    }

    protected var _binding: AddTripFragmentBinding? = null
    protected val bind get() = _binding!!  // This property is only valid between onCreateView and onDestroyView

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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    protected fun toastMessage(resID: Int) {
        Toast.makeText(activity, getString(resID), Toast.LENGTH_SHORT).show()
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
