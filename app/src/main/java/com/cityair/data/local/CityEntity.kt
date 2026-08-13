package com.cityair.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
	tableName = "cities",
	indices = [Index(value = ["name"], unique = true)]
)
data class CityEntity(
	@PrimaryKey(autoGenerate = true)
	val id: Int = 0,
	val name: String,
	val time: String,
	val airQuality: String
)
