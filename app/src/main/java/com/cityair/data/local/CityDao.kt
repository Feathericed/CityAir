package com.cityair.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Delete
import kotlinx.coroutines.flow.Flow

@Dao
interface CityDao{
	@Query("SELECT * FROM cities ORDER BY name ASC")
	fun getAllCities(): Flow<List<CityEntity>>
	@Insert(onConflict = OnConflictStrategy.IGNORE)
	suspend fun addCity(city: CityEntity)
	//@Update
	@Delete
	suspend fun deleteCity(city: CityEntity)
	@Query("DELETE FROM cities WHERE name =:cityName")
	suspend fun deleteCity(cityName: String)
}