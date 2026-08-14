package com.cityair.ui
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier

@Composable
fun AutoCompleteTextField(modifier: Modifier = Modifier) {
    // 1. Define the complete list of available suggestions
    val citySuggestions = listOf("Toronto", "Tokyo", "Toulouse", "New York", "London", "Paris")

    // 2. Track what the user types and whether the dropdown is open
    var typedText by remember { mutableStateOf("") }
    var isExpanded by remember { mutableStateOf(false) }

    // 3. Filter the list dynamically based on what the user types
    val filteredSuggestions = remember(typedText) {
        if (typedText.isBlank()) {
            emptyList() // Hide suggestions if input is completely empty
        } else {
            citySuggestions.filter { city ->
                city.contains(typedText, ignoreCase = true)
            }
        }
    }

    Column(modifier = modifier) {
        // 4. The main input text box
        OutlinedTextField(
            value = typedText,
            onValueChange = { newValue ->
                typedText = newValue
                // Open the dropdown menu only if there are matching suggestions
                isExpanded = filteredSuggestions.isNotEmpty()
            },
            label = { Text("Search City") },
            modifier = Modifier.fillMaxWidth()
        )

        // 5. The floating suggestions menu positioned directly underneath the input box
        DropdownMenu(
            expanded = isExpanded && filteredSuggestions.isNotEmpty(),
            onDismissRequest = { isExpanded = false }, // Closes if user clicks outside
            modifier = Modifier.fillMaxWidth(0.9f)     // Match the approximate width of the textfield
        ) {
            filteredSuggestions.forEach { suggestion ->
                DropdownMenuItem(
                    text = { Text(suggestion) },
                    onClick = {
                        typedText = suggestion // Set the text box to the chosen item
                        isExpanded = false     // Close the dropdown
                    }
                )
            }
        }
    }
}
