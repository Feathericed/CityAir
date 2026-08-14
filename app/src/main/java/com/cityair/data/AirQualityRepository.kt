package com.cityair.data

import android.content.Context
import android.widget.Toast
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
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
import com.cityair.BuildConfig
//import android.app.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import com.cityair.data.AirQualityViewModel
import androidx.appcompat.app.AlertDialog
class AirQualityRepository(context: Context){
    private val cityDao: CityDao = AppDatabase.getDatabase(context).cityDao()
    suspend fun getAirQuality1(cityName: String) :AirQualityResult {

        return try {
            val rseponse = RetrofitClient.apiService.getAirQuality(city = cityName, token  = "88503d06b3f0d48fab82af922227d1d1741ff694")
            if(rseponse.status == "ok") {
                AirQualityResult.Success(rseponse)
            }else{
                AirQualityResult.Error("No air quality data found for $cityName")
            }
        } catch (e: Exception) {
            AirQualityResult.Error(e.message ?: "Unknown error")
        }
    }



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
    suspend fun addCity(cityName: String): Int {
        val cleanedName = cityName.trim()
        if(cleanedName.isNotEmpty() && cityDao.findCity(cleanedName) == 0) {
            cityDao.addCity(CityEntity(name = cleanedName))
            return 1
        }else{
            if(cleanedName.isNotEmpty() )
                return 0
            else if((cityDao.findCity(cleanedName)?:0) > 0){
                return -1
            }else {
                return -2
            }
        }
    }
    suspend fun deleteCity(city: CityEntity) {
        cityDao.deleteCity(city)

    }
    suspend fun deleteCity(cityName: String) {
        val cleanedName = cityName.trim()
        if(cleanedName.isNotEmpty() && (cityDao.findCity(cleanedName) ?: 0)>0) {
            cityDao.deleteCity(CityEntity(name = cleanedName))
        }
    }/*
    suspend fun getAllCities(cityName: String): WaqiResponse {
        return RetrofitClient.apiService.getAirQuality(city = cityName, token  = "88503d06b3f0d48fab82af922227d1d1741ff694")
    }*/
    suspend fun getAirQuality(cityName: String): WaqiResponse {
        return RetrofitClient.apiService.getAirQuality(city = cityName, token  = BuildConfig.API_KEY)
    }
}
sealed class AirQualityResult {
    data object Loading: AirQualityResult()
    data class Success(val data: WaqiResponse): AirQualityResult()
    data class Error(val message: String): AirQualityResult()
    data object Empty: AirQualityResult()
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
    // 1. Internal state tracker (starts as false)
    private val _showWarningDialog = MutableStateFlow(false)
    var warningText: String = ""
    // 2. Publicly read-only state flow exposed to your UI Composable
    val showWarningDialog: StateFlow<Boolean> = _showWarningDialog.asStateFlow()

    // 3. YOUR FUNCTION: Changes state to false, closing the warning box
    public final fun clearWarning() {
        _showWarningDialog.value = false
        warningText = ""
    }
    //val showDialog by viewModel.showWarningDialog.collectAsStateWithLifecycle()
    fun addCity(cityName: String) {
        viewModelScope.launch {
            val rtn = respository.addCity(cityName)
            if (rtn!=1){
                _showWarningDialog.value = true
                if (rtn == 0)
                    warningText = "City name cannot be duplicate"
                else if (rtn == -1){
                    warningText = ""}
                else
                    warningText = "City name cannot be add something is wrong"
            }
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
                //Toast.makeText(context, "Error: Connection timed out!", Toast.LENGTH_LONG).show()
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
