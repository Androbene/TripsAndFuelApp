package ua.androbene.tripsandfuel.database

import androidx.room.*

@Dao()
interface TripDao {
    @Query("select * from trips order by longID DESC")
    fun getAll(): MutableList<Trip>

    @Query("select * from trips where longID = :id")
    fun getByID(id: Long): Trip

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(trip: Trip)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(trip: Trip)

    @Query("delete from trips where longID = :id")
    fun deleteById(id: Long)

    @Query("delete from trips")
    fun clear()

    @Query("select * from trips where longID between :idFrom and :idTo order by longID DESC")
    fun getByPeriod(idFrom:Long, idTo: Long): MutableList<Trip>
}