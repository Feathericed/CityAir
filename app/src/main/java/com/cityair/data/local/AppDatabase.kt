package com.cityair.data.local

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


@Database(
	entities = [CityEntity::class],
	version = 1,
	exportSchema =false
)
abstract class AppDatabase : RoomDatabase(){
	abstract fun cityDao(): CityDao
	companion object{
		@Volatile
		private var INSTANCE: AppDatabase? =null
		
		fun getDatabase(context: Context): AppDatabase{
			return INSTANCE ?: synchronized(this){
				val db = Room.databaseBuilder(
					context.applicationContext,
					AppDatabase::class.java,
					"air_quality_db"
				).build()
				INSTANCE = db
				db
			}
		}
	
	}

}

