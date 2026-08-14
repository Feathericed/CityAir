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
import androidx.lifecycle.viewmodel.compose.rememberViewModelStoreProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cityair.data.AirQualityViewModel
import com.cityair.data.AirQualityRepository
import com.cityair.data.AirQualityViewModelFactory
import androidx.lifecycle.ViewModelProvider
//import androidx.appcompat.app.AlertDialog
import androidx.compose.material3.AlertDialog
import androidx.compose.runtime.setValue
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CityListPage(viewModel: AirQualityViewModel= androidx.lifecycle.viewmodel.compose.viewModel() ,
                 onCityClick:(String) -> Unit,
                 onReportClick:() -> Unit){
	//val viewModel= //AirQualityViewModel(AirQualityRepository(this))
	//val viewModel by AirQualityViewModel(AirQualityRepository(this))
    val cities by viewModel.cities.collectAsState()
    val cityInput = remember { mutableStateOf("") }
	val isDialogOpen = remember { mutableStateOf(false) }
	var showDeleteDialog by remember { mutableStateOf(false) }
	var cn = ""
	Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "City Air Quality")
                }
            )

        }

    ){padding ->
        Column(modifier = Modifier
	        .padding(padding)
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
				}/*
				if (!viewModel.warningText.trim().isNullOrEmpty()) {
						isDialogOpen.value = true
						AlertDialog(
							onDismissRequest = { isDialogOpen.value = false
								},
							title = { Text(text = "Invalid Input") },
							text = { Text(text = viewModel.warningText) }, // Displays the precise error message
							confirmButton = {
								Button(onClick = {
									isDialogOpen.value = false
								}) {
									Text(text = "OK")
								}
							}
						)
					viewModel.clearWarning()

				}*/
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
							onCityClick(city.name)
						},
						onDelete = {
							cn = city.name
							viewModel.deleteCity(city)
						}
					)
				}
			}
        }
		if(showDeleteDialog && !cn.isNullOrEmpty()){
			AlertDialog(onDismissRequest = {showDeleteDialog=false},
				title= { Text(cn) },
				text={Text("Are you sure to delete this city? ${cn}")},
				confirmButton = {Button(onClick={showDeleteDialog=false
					viewModel.deleteCity(cn)
				}){Text("Delete")}

				}, dismissButton = {Button(onClick={showDeleteDialog=false}) { Text("Cancel")}})
		}
    }

}

@Composable
fun CityRow(
	city: CityEntity,
	onClick:() -> Unit,
	onDelete:() -> Unit
){
	//var showDeleteDialog by remember { mutableStateOf(false) }
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
			TextButton(onClick =onDelete  ){
				Text("Delete")
			}

		
		}
	}
}