package ua.androbene.tripsandfuel.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "trips")
data class Trip(
    @PrimaryKey
    val longID: Long = 0L,
    val destination: String = "",
    val distance: Float = 0f,
    val consumption: Float = 0f,
    val fuelRatio: Float = 0f,
    val fuelPrice: Float = 0f,
    val comments: String = "",
    val year: Int = 2020,
    val month: Int = 1,
    val day: Int = 1
)