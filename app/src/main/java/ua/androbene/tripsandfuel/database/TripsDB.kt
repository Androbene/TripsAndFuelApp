package ua.androbene.tripsandfuel.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Trip::class], version = 1)
abstract class AppDB : RoomDatabase() {
    abstract fun tripDao(): TripDao

    companion object {

        private const val DATABASE_NAME = "trips_db"

        @Volatile
        private var instance: AppDB? = null

        fun getInstance(context: Context): AppDB {
            return instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(context, AppDB::class.java, DATABASE_NAME)
                    .fallbackToDestructiveMigration()
                    .build()
            }
        }
    }
}