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
import kotlinx.coroutines.launch
import com.cityair.data.remote.RetrofitClient
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
//import androidx.lifecycle.viewmodel.compose.viewModel
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Log.d("MainActivity", "Greeting: $name")
    val temp = BuildConfig.API_KEY
    Log.d("MainActivity", "Greeting: $temp")
    //setContentView(R.layout.activity_main)

    // Launch API call in a coroutine scope tied to the Activity lifecycle
    lifecycleScope.launch {
        try {
            val posts = RetrofitClient.apiService.getAirQuality("toronto",temp)
            Log.d("API_SUCCESS", "Title: ${posts}")
            /*for (post in posts) {
                Log.d("API_SUCCESS", "Title: ${post.title}")
            }*/
        } catch (e: Exception) {
            Log.e("API_ERROR", "Failed to fetch data: ${e.message}")
        }
    }
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    CityAirTheme {
        Greeting("Android")
    }
}