package com.cityair.data

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.cityair.data.remote.WaqiResponse
import com.cityair.data.local.CityEntity
import com.cityair.data.local.CityDao
import com.cityair.data.local.AppDatabase
import com.cityair.data.remote.RetrofitClient
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.Flow
//import CoroutineScope.viewModelScope
import androidx.lifecycle.viewModelScope

class AirQualityRepository(context: Context){
    private val cityDao: CityDao = AppDatabase.getDatabase(context).cityDao()

/*
    val allCities: StateFlow<List<CityEntity>> = cityDao.getAllCities()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )*/
    fun getAllCities(): Flow<List<CityEntity>> {
        return cityDao.getAllCities()
    }
    suspend fun addCity(cityName: String) {
        val cleanedName = cityName.trim()
        if(cleanedName.isNotEmpty()) {
            cityDao.addCity(CityEntity(name = cleanedName))
        }
    }
    suspend fun deleteCity(city: CityEntity) {
        cityDao.deleteCity(city)

    }
    suspend fun deleteCity(cityName: String) {
        val cleanedName = cityName.trim()
        if(cleanedName.isNotEmpty()) {
            cityDao.deleteCity(CityEntity(name = cleanedName))
        }
    }/*
    suspend fun getAllCities(cityName: String): WaqiResponse {
        return RetrofitClient.apiService.getAirQuality(city = cityName, token  = "88503d06b3f0d48fab82af922227d1d1741ff694")
    }*/
    suspend fun getAirQuality(cityName: String): WaqiResponse {
        return RetrofitClient.apiService.getAirQuality(city = cityName, token  = "88503d06b3f0d48fab82af922227d1d1741ff694")
    }
}
data class AirQualityUiState(
    val isLoading: Boolean = false,
    val result: WaqiResponse?= null,
    val errorMessage: String?= null
)
class AirQualityViewModel(
    private val respository: AirQualityRepository
): ViewModel() {
    var cities: StateFlow<List<CityEntity>> =
        respository.getAllCities()
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                emptyList()
            )
    var airQuality = androidx.compose.runtime.mutableStateOf(AirQualityUiState())
        private set
    fun addCity(cityName: String) {
        viewModelScope.launch {
            respository.addCity(cityName)
        }
    }
    fun deleteCity(cityName: String) {
        viewModelScope.launch {
            respository.deleteCity(cityName)
        }
    }
    fun loadAirQuality(cityName: String) {
        viewModelScope.launch() {
            airQuality.value = AirQualityUiState(isLoading = true)
            try {
                val response = respository.getAirQuality(cityName)
                if(response.status == "ok") {
                    airQuality.value = AirQualityUiState(result = response)
                }else{
                    airQuality.value = AirQualityUiState(errorMessage = "No air quality data found for $cityName")
                }
            }catch (e: Exception) {
                airQuality.value = AirQualityUiState(errorMessage = e.message ?:"Failed to load air quality date")
            }
        }
    }
}
class AirQualityViewModelFactory(
    private val respository: AirQualityRepository
): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AirQualityViewModel(respository) as T

    }
}
