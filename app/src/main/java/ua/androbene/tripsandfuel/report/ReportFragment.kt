package ua.androbene.tripsandfuel.report

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Intent
import android.icu.text.DateFormat
import android.icu.util.Calendar
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import ua.androbene.tripsandfuel.R
import ua.androbene.tripsandfuel.databinding.ReportingFragmentBinding
import ua.androbene.tripsandfuel.trips.TripsViewModel

/**
 * A fragment for generating a report for a certain period in text form.
 */
class ReportFragment : Fragment() {

    private var _binding: ReportingFragmentBinding? = null
    private val bind get() = _binding!!

    private val reportViewModel by viewModels<ReportViewModel>()
    private val tripViewModel by activityViewModels<TripsViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = ReportingFragmentBinding.inflate(inflater, container, false)
        val view = bind.root
        bind.tvFromDate.setOnClickListener { chooseDate(reportViewModel.calendarFrom) }
        bind.tvTillDate.setOnClickListener { chooseDate(reportViewModel.calendarTill) }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        reportViewModel.reportedTrips.observe(viewLifecycleOwner, {
            bind.tvReport.text = reportViewModel.getReportAsText()
        })
        tripViewModel.trips.observe(viewLifecycleOwner, {
            reportViewModel.refreshReportedTrips()
        })
        reportViewModel.calendarFrom.observe(viewLifecycleOwner, {
            reportViewModel.refreshReportedTrips()
            bind.tvFromDate.text = DateFormat.getDateInstance(DateFormat.MEDIUM).format(it.time)
        })
        reportViewModel.calendarTill.observe(viewLifecycleOwner, {
            reportViewModel.refreshReportedTrips()
            bind.tvTillDate.text = DateFormat.getDateInstance(DateFormat.MEDIUM).format(it.time)
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_reporting, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item_share -> shareReport()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun chooseDate(calendar: MutableLiveData<Calendar>) {
        DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                val newDate = Calendar.getInstance()
                newDate.set(year, month, dayOfMonth)
                calendar.value = newDate
            },
            calendar.value!![Calendar.YEAR],
            calendar.value!![Calendar.MONTH],
            calendar.value!![Calendar.DAY_OF_MONTH]
        ).show()
    }

    @SuppressLint("QueryPermissionsNeeded")
    private fun shareReport() {
        try {
            val intent = Intent()
            intent.action = Intent.ACTION_SEND
            intent.putExtra(
                Intent.EXTRA_TEXT,
                reportViewModel.getReportAsText()
            )
            intent.type = "text/plain"
            // is there an activity ready to process our intent
            if (intent.resolveActivity(requireActivity().packageManager) != null) {
                startActivity(intent)
            }
        } catch (e: Exception) {
            Toast.makeText(activity, getString(R.string.report_exception), Toast.LENGTH_SHORT)
                .show()
        }
    }
}