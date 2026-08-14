package com.cityair


import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import com.cityair.data.local.*
//import com.cityair.ui.AirQualityViewModel
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.cityair.data.AirQualityViewModel

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn

import androidx.compose.runtime.collectAsState

import com.cityair.data.local.*
//import com.cityair.ui.AirQualityViewModel
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.foundation.lazy.items

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportPage(
	viewModel: AirQualityViewModel,
	onBackClick:() -> Unit
) {
	val cities by viewModel.cities.collectAsState()
	Scaffold(
		topBar = {
			TopAppBar(
				title = { Text("Saved Cities Report")}
			)
			
		}
	){ padding ->
		Column(
			modifier = Modifier
			.padding(padding)
			.padding(16.dp)
			.fillMaxSize()
		){
			Button(onClick = onBackClick){
				Text("Back")
			}
			Spacer(modifier = Modifier.height(16.dp))
			Text("Total Saved Cities: ${cities.size}")
			Spacer(modifier = Modifier.height(16.dp))
			LazyColumn{
				items(cities) { city ->
					Card(
						modifier =Modifier
							.padding(vertical = 6.dp)
							.fillMaxWidth()
					){
						//Text("City ID: ${city.id}")
						Text("City Name: ${city.name}")
					}
				}
			}
		}
	
	}
}