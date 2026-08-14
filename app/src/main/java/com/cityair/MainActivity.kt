package com.cityair

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.cityair.ui.theme.CityAirTheme
//import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.CoroutineScope
//import kotlinx.coroutines.Dispatchers.
import kotlinx.coroutines.launch
import com.cityair.data.remote.RetrofitClient
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
//import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import retrofit2.http.Path
import retrofit2.http.Query
import com.cityair.data.remote.WaqiResponse
import kotlinx.coroutines.withContext
import kotlinx.coroutines.async
import kotlinx.coroutines.*
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
//import kotlinx.coroutines.launch
import com.cityair.BuildConfig
import com.cityair.data.local.AppDatabase
import com.cityair.data.local.CityDao
import com.cityair.data.local.CityEntity
import com.cityair.data.remote.WaqiApi
import com.cityair.data.remote.WaqiData
import com.cityair.data.AirQualityRepository
import com.cityair.data.AirQualityViewModelFactory
import androidx.room.RoomDatabase
interface ApiService {
    @GET("feed/{city}/")
    suspend fun getPost(
        @Path("city") city: String,
        @Query("token") token: String
    ):  WaqiResponse
}
//import androidx.lifecycle.viewmodel.compose.viewModel
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val database = AppDatabase.getDatabase(this)
        Log.d("MainActivity", "Greeting: 1")
        val repository = AirQualityRepository(this)
        Log.d("MainActivity", "Greeting: 2")
        val factory = AirQualityViewModelFactory(repository)
        Log.d("MainActivity", "Greeting: 3")

        enableEdgeToEdge()
        setContent {
            CityAirTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String,  modifier: Modifier = Modifier) {
    var resultText by remember {mutableStateOf("") }
        Log.d("MainActivity", "Greeting: $name")
    val temp = BuildConfig.API_KEY

    Log.d("MainActivity", "bla : $temp")
    //setContentView(R.layout.activity_main)
    // 1. Initialize Retrofit
    val retrofit = Retrofit.Builder()
        .baseUrl("https://api.waqi.info/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    Log.d("MainActivity", "Greeting: retrofit")
    val apiService = retrofit.create(ApiService::class.java)
    Log.d("MainActivity", "Greeting: apiService")
    //
    // Launch API call in a coroutine scope tied to the Activity lifecycle
    //kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.IO)
        //.launch(   ) {

    CoroutineScope(Dispatchers.IO).launch {
            try {
            //val posts = //RetrofitClient.apiService.getAirQuality("toronto",temp)
                val post = apiService.getPost("vancouver",temp)
                Log.d("MainActivity", " called")
            println("Post Title: ${post.status}")
            Log.d("API_SUCCESS", "Title: ${post.data?.aqi?: "N/A"}")
                resultText =  withContext(Dispatchers.Main) {"${post.data?.aqi?: "N/A"}"}

                //val sortedNames = remember { rawNames.sorted() }

                /*for (post in posts) {
                Log.d("API_SUCCESS", "Title: ${post.title}")
            }*/
        } catch (e: Exception) {
            Log.e("API_ERROR", "er: ${e.message}")
                //name = e.message.toString()
        }
    }

    Text(
        text = "Hello $name $resultText!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {

    CityAirTheme {
        Greeting("Android" )
    }
}