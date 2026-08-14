package com.example.cityair

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import com.cityair.data.AirQualityViewModel
import androidx.compose.ui.unit.dp
//import androidx.compose.ui.graphics.color
import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.cityair.data.local.*
import com.cityair.data.remote.*
import com.cityair.data.*
import com.cityair.ui.*

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import com.cityair.ApiService
import com.cityair.BuildConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.json.JSONObject
import androidx.compose.ui.text.withStyle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AirQualityDetailPage(
	cityName: String,
	viewModel: AirQualityViewModel,
	onBackClick: ()-> Unit
){
	val state = viewModel.airQuality.value
	val showWarning by viewModel.showWarningDialog.collectAsStateWithLifecycle()
	//val errorMessageText by viewModel.warningText.collectAsStateWithLifecycle()

	// Render layout content here...

	// Automatically display the UI element whenever state updates to true

	LaunchedEffect(cityName){
		viewModel.loadAirQuality(cityName)
	}
	Scaffold(
		topBar ={
			TopAppBar(
				title = {Text(text = "Air Quality Detail")}
			)
		}
	){padding ->
		Column (
			modifier = Modifier
				.padding(padding)
				.padding(16.dp)
				.fillMaxSize()
		){
			Button(
				onClick = onBackClick
			){
				Text("Back")
			}
			Spacer(modifier = Modifier.height(16.dp))
			Text("City: $cityName")
			Spacer(modifier = Modifier.height(16.dp))


			when{
				state.isLoading ->{
					CircularProgressIndicator()
				}

				state.errorMessage != null ->{
					Text("Error: ${state.errorMessage}")
					Spacer(modifier = Modifier.height(16.dp))
				}
				state.result != null ->{
					val data = state.result.data
					Card(
						modifier = Modifier.height(8.dp)

					) {
						Column(
							modifier = Modifier.height(16.dp)
						){
							Text("Status: ${state.result.status}",modifier = Modifier.height(16.dp))
							Text("Station: ${data?.city?.name ?: "Unknow"}",modifier = Modifier.height(16.dp))
							Text(text = "AQI: ${data?.aqi ?: "N/A"}",
								color = aqiColor(data?.aqi))
							Text("Status ID : ${data?.idx ?: "N/A"}")
							Spacer(modifier = Modifier.height(16.dp))
							Text("Update Time: ${data?.time?.s ?: "N/A"}")
							Spacer(modifier = Modifier.height(12.dp))
							Text(text = getAqiDescription(data?.aqi))
						}

					}

				}
			}
			LoadCityAirAuqlityDetail(cityName)
		}
	}

}
@Composable
fun LoadCityAirAuqlityDetail(name: String,  modifier: Modifier = Modifier) {
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
	var post by remember {mutableStateOf(value= WaqiResponse("ok",null)) }
	CoroutineScope(Dispatchers.IO).launch {
		try {
			//val posts = //RetrofitClient.apiService.getAirQuality("toronto",temp)
			 post = apiService.getPost(name,temp)
			Log.d("MainActivity", " called")
			println("Post Title: ${post.status}")
			Log.d("API_SUCCESS", "Title: ${post.data?.aqi?: "N/A"}")
			resultText =  withContext(Dispatchers.Main) {"${post.data?: "N/A"}"}

			//val sortedNames = remember { rawNames.sorted() }

			/*for (post in posts) {
            Log.d("API_SUCCESS", "Title: ${post.title}")
        }*/
		} catch (e: Exception) {
			Log.e("API_ERROR", "er: ${e.message}")
			//name = e.message.toString()
		}
	}
	if ("ok".equals(post.status)){
		Text(text = "Status: ${post.status}", modifier = modifier)

		val multiColorString = buildAnnotatedString {
			withStyle(style = SpanStyle(color = Color.Blue)) {
				append("AQI: ${post.data?.aqi} ${getAqiDescription(post.data?.aqi)}")
			}
			//append(" | ")
			withStyle(style = SpanStyle(color =  aqiColor(post.data?.aqi))) {
				append(" \u2588")
			}
			/*
			// 1. First colored section (Green)
			withStyle(style = SpanStyle(color = Color(0xFF4CAF50))) {
				append("Good \\u2588")
			}

			append(" | ") // Plain separator text

			// 2. Second colored section (Yellow/Orange)
			withStyle(style = SpanStyle(color = Color(0xFFFFB300))) {
				append("Moderate \\u2588")
			}

			append(" | ") // Plain separator text

			// 3. Third colored section (Red)
			withStyle(style = SpanStyle(color = Color(0xFFF44336))) {
				append("Unhealthy \\u2588")
			}*/
		}
		Text(
			text = multiColorString,
			modifier = modifier
		)
		Text(text="Station: ${post.data?.city?.name ?: "Unknow"}", modifier = modifier)

		Text("Update Time: ${post.data?.time?.s ?: "N/A"}")
		Text("Data provider: ${post.data?.attributions?.getOrNull(0)?.name ?: "N/A"}")
		Text("Idx : ${post.data?.idx}", modifier = modifier)
		Text("Dominat Pollutant : ${post.data?.dominatPollutant}", modifier = modifier)
		Text("CO: ${post.data?.iaqi?.co?.v ?: "N/A"}")
		Text("O3: ${post.data?.iaqi?.o3?.v ?: "N/A"}")
		Text("PM 25: ${post.data?.iaqi?.pm25?.v ?: "N/A"}")
		Text("So2: ${post.data?.iaqi?.so2?.v ?: "N/A"}")

		/*Text("${resultText}",
			modifier = modifier)
		Text("Status: ${post.status}")
		Spacer(modifier = Modifier.height(16.dp))
		Text("Station: ${post.data?.city?.name ?: "Unknow"}")
		Spacer(modifier = Modifier.height(16.dp))
		Text(text = "AQI: ${post.data?.aqi ?: "N/A"}",
			color = aqiColor(post.data?.aqi))
		Text("Status ID : ${post.data?.idx ?: "N/A"}")
		Text("Update Time: ${post.data?.time?.s ?: "N/A"}")
		Spacer(modifier = Modifier.height(12.dp))
		Text(text = getAqiDescription(data?.aqi))*/
	}
}
fun getAqiDescription(aqi: Int?): String{
	return when {
		aqi == null ->"No AQI value available"
		aqi <= 50 -> "Good air quality"
		aqi <= 100 -> "Moderate air quality"
		aqi <= 150 -> "Unhealthy for sensitive groups"
		aqi <= 200 -> "Unhealthy air quality"
		aqi <= 300 -> "Very unhealthy air quality"
		else -> "Hazardous air quality"
	}
}
// add json response reading
fun aqiColor(aqi: Int?): Color {
	return when {
		aqi == null -> Color.Gray
		aqi <= 50 -> Color.Green
		aqi <= 100 -> Color.Yellow
		aqi <= 150 -> Color((0xFF9C27B0.toInt()))
		aqi <= 200 -> Color.Red
		aqi <= 300 -> Color(0xFF9C27B0.toInt())
		else -> Color(0xFF6D4C41.toInt())
	}
}
@Composable
fun ForecastSection(
	title: String,
	items: List<WaqiForecastItem>?
){
	Card(
		modifier = Modifier.padding(8.dp)
	){
		Column{
			Spacer(modifier = Modifier.padding(16.dp))
			Text(title)
			Spacer(modifier = Modifier.height(8.dp))
			if(items.isNullOrEmpty()){
				Text("No forecast data available")
			}else{
				items.forEach{ item ->
					ForecastRow(item)
					Spacer(modifier = Modifier.height(6.dp))
				}
			}
		}
	}

}
@Composable
fun ForecastRow(item: WaqiForecastItem){
	Column{
		Text("Date: ${item.day ?: "N/A"}")
		Text("Averange: ${item.avg ?: "N/A"}")
		Text("Min: ${item.min ?: "N/A"}")
		Text("Max: ${item.max ?: "N/A"}")
	}
}

@Composable
fun AttributionSection(
	attributions: List<WaqiAttribution>?
){
	Card(
		modifier = Modifier.padding(8.dp)
	){
		Column(modifier = Modifier.padding(16.dp)
		){
			Text("Data Sources")
			Spacer(modifier = Modifier.height(8.dp))
			if(attributions.isNullOrEmpty()){
				Text("No attribution data available")
			}else{
				attributions.forEach{ attribution ->
					Text("Name: ${attribution.name ?: "N/A"}")
					Text("URL: ${attribution.url ?: "N/A"}")
					Spacer(modifier = Modifier.height(8.dp))
				}
			}
		}
	}
}

fun formatValue(value: Double?): String {
	return value?.toString() ?: "N/A"
}
fun responseReader(json: String): WaqiResponse {
	val jsonObject = JSONObject(json)
	val status = jsonObject.getString("status")
	val data = jsonObject.getJSONObject("data")
	val aqi = data.getInt("aqi")
	val idx = data.getInt("idx")
	val attributions = data.getJSONArray("attributions")
	val p= WaqiResponse("ok",null)
	return p
	//val WaqiResponse  = data.getJSONArray("attributions")
}