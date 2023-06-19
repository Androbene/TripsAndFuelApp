package ua.androbene.tripsandfuel.user_interface.report

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Intent
import android.icu.text.DateFormat
import android.icu.util.Calendar
import android.os.Bundle
import android.view.*
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import ua.androbene.tripsandfuel.R
import ua.androbene.tripsandfuel.databinding.FragmentReportBinding
import ua.androbene.tripsandfuel.user_interface.base.BaseFragment
import ua.androbene.tripsandfuel.user_interface.base.paintTextGradient
import ua.androbene.tripsandfuel.user_interface.trips.TripsViewModel

/**
 * A fragment for generating a report for a certain period in text form.
 */
@Suppress("DEPRECATION")
class ReportFragment : BaseFragment<FragmentReportBinding>() {
    override fun inflateFrag(inflater: LayoutInflater) = FragmentReportBinding .inflate(inflater)

    private val reportViewModel by viewModels<ReportViewModel>()
    private val tripViewModel by activityViewModels<TripsViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpUi()
        setUpObservers()
    }

    private fun setUpUi() = with(binding) {
        tvFromDate.setOnClickListener { chooseDate(reportViewModel.calendarFrom) }
        tvTillDate.setOnClickListener { chooseDate(reportViewModel.calendarTill) }
        tvFromDate.paintTextGradient()
        tvTillDate.paintTextGradient()
    }

    private fun setUpObservers() = with(binding) {
        reportViewModel.reportedTrips.observe(viewLifecycleOwner) {
            tvReport.text = reportViewModel.getReportAsText()
        }
        tripViewModel.trips.observe(viewLifecycleOwner) {
            reportViewModel.refreshReportedTrips()
        }
        reportViewModel.calendarFrom.observe(viewLifecycleOwner) {
            reportViewModel.refreshReportedTrips()
            tvFromDate.text = DateFormat.getDateInstance(DateFormat.MEDIUM).format(it.time)
        }
        reportViewModel.calendarTill.observe(viewLifecycleOwner) {
            reportViewModel.refreshReportedTrips()
            tvTillDate.text = DateFormat.getDateInstance(DateFormat.MEDIUM).format(it.time)
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_reporting, menu)
    }

    @Deprecated("Deprecated in Java")
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
            toast(R.string.report_exception)
        }
    }
}