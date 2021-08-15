package ua.androbene.tripsandfuel.trips

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ua.androbene.tripsandfuel.database.AppDB
import ua.androbene.tripsandfuel.database.Trip

class TripsViewModel(val app: Application) : AndroidViewModel(app) {

    private val tripDao = AppDB.getInstance(app).tripDao()

    val isLoading = MutableLiveData(false)
    val beingEditedTrip = MutableLiveData(Trip())

    private val mTrips = MutableLiveData<MutableList<Trip>>()
    val trips: LiveData<MutableList<Trip>>
        get() = mTrips

    init {
        viewModelScope.launch(Dispatchers.IO) {
            if (!isLoading.value!!)
                isLoading.postValue(true)
            mTrips.postValue(tripDao.getAll())
            isLoading.postValue(false)
        }
    }

    fun delTripByID(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            tripDao.deleteById(id)
            // it would be more correct to request updated data from the database in accordance with MVVM,
            // but for performance, a new mutable list is created,
            // the element is removed from it and the list is sent to livedata
            val updatedList = mTrips.value?.toMutableList() ?: mutableListOf()
            updatedList.removeIf { it.longID == id }
            mTrips.postValue(updatedList)
        }
    }

    fun addTrip(trip: Trip) {
        viewModelScope.launch(Dispatchers.IO) {
            tripDao.insert(trip)
            mTrips.postValue(tripDao.getAll())
        }
    }

    fun delAll() {
        viewModelScope.launch(Dispatchers.IO) {
            tripDao.clear()
            mTrips.postValue(emptyList<Trip>().toMutableList())
        }
    }

    fun editTrip(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            beingEditedTrip.postValue(tripDao.getByID(id))
        }
    }

    fun replace(oldId: Long, trip: Trip) {
        viewModelScope.launch(Dispatchers.IO) {
            if (oldId == trip.longID) {
                tripDao.update(trip)
            } else {
                tripDao.deleteById(oldId)
                tripDao.insert(trip)
            }
            mTrips.postValue(tripDao.getAll())
        }
    }

    override fun onCleared() {
        super.onCleared()
        AppDB.getInstance(app).close()
        //Log.d("mylog", "Database closed")
    }

    fun clearEditedTrip() {
        beingEditedTrip.value = Trip()
    }
}