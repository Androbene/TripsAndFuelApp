package ua.androbene.tripsandfuel.user_interface.report

import android.app.Application
import android.icu.text.DateFormat
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ua.androbene.tripsandfuel.database.AppDB
import ua.androbene.tripsandfuel.database.Trip
import java.util.*

class ReportViewModel(val app: Application) : AndroidViewModel(app) {

    private val tripDao = AppDB.getInstance(app).tripDao()

    val reportedTrips = MutableLiveData<MutableList<Trip>>(mutableListOf())
    val calendarFrom = MutableLiveData(Calendar.getInstance())
    val calendarTill = MutableLiveData(Calendar.getInstance())

    init {
        val defaultFrom = calendarFrom.value!!
        defaultFrom[Calendar.DAY_OF_MONTH] = 1 // default lower limit
        calendarFrom.value = defaultFrom

        viewModelScope.launch(Dispatchers.IO) {
            val queryByPeriod = tripDao.getByPeriod(
                makeIdFromCalendar(defaultFrom),
                makeIdFromCalendar(calendarTill.value!!)
            )
            reportedTrips.postValue(queryByPeriod)
        }
    }

    fun refreshReportedTrips() {
        val increasedByOneDay = Calendar.getInstance()
        increasedByOneDay[Calendar.DAY_OF_MONTH] =
            calendarTill.value!![Calendar.DAY_OF_MONTH] + 1 // increase to include last day
        increasedByOneDay[Calendar.MONTH] =
            calendarTill.value!![Calendar.MONTH]
        increasedByOneDay[Calendar.YEAR] =
            calendarTill.value!![Calendar.YEAR]

        viewModelScope.launch(Dispatchers.IO) {
            val queryByPeriod = tripDao.getByPeriod(
                makeIdFromCalendar(calendarFrom.value!!),
                makeIdFromCalendar(increasedByOneDay)
            )
            reportedTrips.postValue(queryByPeriod)
        }
    }

    fun getReportAsText(): String {
        val textDetails = StringBuilder()
        var fuelCost = 0f
        var depreciationCost = 0f
        for (t in reportedTrips.value!!) {
            val tripFuelCost = t.distance / 100f * t.consumption * t.fuelPrice * 1.2f
            fuelCost += tripFuelCost
            depreciationCost += t.distance * 1.5f // formula for depreciation //////////////////////
            textDetails.append("\n ").append(getTripDateAsString(t)).append("\n ")
                .append(t.destination)
                .append("\n ")
                .append(t.distance).append("км | ").append(t.consumption).append("л/100км | ")
                .append(t.fuelPrice).append("грн/л\n Топливо: ").append(
                    String.format(
                        "%.2f",
                        tripFuelCost
                    )
                ).append("грн + Амортизация: ").append(t.distance * 1.5f).append("грн") ///////////////////////
                .append("\n ----------------------------------------")
        }
        val fromDate =
            DateFormat.getDateInstance(DateFormat.MEDIUM).format(calendarFrom.value!!.time)
        val tillDate =
            DateFormat.getDateInstance(DateFormat.MEDIUM).format(calendarTill.value!!.time)
        val textFinal = StringBuilder()
        textFinal.append("\n Итого за период: $fromDate - $tillDate\n        Топливо - ")
            .append(String.format("%.2f", fuelCost))
            .append("грн\n        Амортизация: ").append(String.format("%.2f", depreciationCost)) ///////////////
            .append("грн")
            .append("\n ******************************\n").append(textDetails.toString())
        return textFinal.toString()
    }

    private fun getTripDateAsString(t: Trip): String {
        val currCalendar = Calendar.getInstance()
        currCalendar.set(t.year, t.month, t.day)
        return DateFormat.getDateInstance(DateFormat.MEDIUM).format(currCalendar.time)
    }

    // forming coarse ID for database request limits
    private fun makeIdFromCalendar(cal: Calendar): Long { // create
        return SimpleDateFormat("yyyyMMdd000000000", Locale.getDefault()).format(cal).toLong()

    }

}