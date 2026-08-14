package com.cityair

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CityListPage(viewModel: AirQualityViewModel,
                 onCityCLick:(String) -> Unit,
                 onReportClick:() -> Unit){
    val cities by viewModel.cities.collectAsState()
    val cityInput = remember { mutableStateOf("") }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "City Air Quality")
                }
            )

        }

    ){padding ->
        Column(modifier = Modifier.padding(padding)
            .padding(16.dp)
            .fillMaxSize()
        ) {
            OutlinedTextField(
                value = cityInput.value,
                onValueChange = { cityInput.value = it },
                label = { Text("Enter City Name") },
                modifier = Modifier.fillMaxWidth()
            )
			Spacer(modifier = Modifier.height(12.dp))
			Row(
				horizontalArrangement = Arrangement.spacedBy(8.dp)
			){
				Button(
					onClick = {
						viewModel.addCity(cityInput.value)
						cityInput.value = ""
					}
				){
					Text("Add City")
				}
				Button(
					onClick = onReportClick
				){
					Text("Report")
				}
			}
			Spacer(modifier = Modifier.height(16.dp))
			Text("Saved Cities")
			Spacer(modifier = Modifier.height(8.dp))
			LazyColumn{
				items(cities){ city ->
					CityRow (city = city,
						onClick={
							onCityCLick(city.name)
						},
						onDelete = {
							viewModel.deleteCity(city.name)
						}
					)
				}
			}
        }
    }

}

@Composable
fun CityRow(
	city: CityEntity,
	onClick:() -> Unit,
	onDelete:() -> Unit
){
	Card(
		modifier = Modifier
			.padding(vertical = 6.dp)
			.fillMaxWidth()
			.clickable {onClick()}
	){
		Row(
			modifier = Modifier
			.padding(12.dp)
			.fillMaxWidth(),
			horizontalArrangement = Arrangement.SpaceBetween
		){
			Text(city.name)
			TextButton(onClick = onDelete){
				Text("Delete")
			}
		
		}
	}
}