package com.example.cityair

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
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AirQualityDetailPage(
	cityName: String,
	viewModel: AirQualityViewModel,
	onBackClick: ()-> Unit
){
	val state = viewModel.airQuality.value
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
				}
				state.result != null ->{
					val data = state.result.data
					Card(
						modifier = Modifier.height(8.dp)
					) {
						Column(
							modifier = Modifier.height(16.dp)
						){
							Text("Status: ${state.result.status}")
							Text("Station: ${data?.city?.name ?: "Unknow"}")
							Text(text = "AQI: ${data?.aqi ?: "N/A"}",
								color = aqiColor(data?.aqi))
							Text("Status ID : ${data?.idx ?: "N/A"}")
							Text("Update Time: ${data?.time?.s ?: "N/A"}")
							Text(text = getAqiDescription(data?.aqi))
						}
						
					}
					
				}
			}
		}
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
fun aqiColor(aqi: Int?): Color{
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
