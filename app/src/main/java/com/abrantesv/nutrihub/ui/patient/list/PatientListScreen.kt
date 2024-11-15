package com.abrantesv.nutrihub.ui.patient.list

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ExposedDropdownMenuBox
import androidx.compose.material.ExposedDropdownMenuDefaults
import androidx.compose.material.ExposedDropdownMenuDefaults.TrailingIcon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.abrantesv.nutrihub.R
import com.abrantesv.nutrihub.ui.ScreenTitle
import com.abrantesv.nutrihub.util.UiEvent

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun PatientListScreen(
    onNavigate: (UiEvent.Navigate) -> Unit, viewModel: PatientListViewModel = hiltViewModel()
) {
    LaunchedEffect(key1 = true) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is UiEvent.Navigate -> onNavigate(event)
                else -> {}
            }
        }
    }

    Scaffold(
        scaffoldState = rememberScaffoldState(),
        floatingActionButton = {
            FloatingActionButton(onClick = {
                viewModel.onEvent(PatientListEvent.OnClickAddPatient)
            }) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(id = R.string.add_patient_description)
                )
            }
        },
    ) { paddingValues ->
        val templates by viewModel.templatePlans.collectAsState(initial = emptyList())
        if (viewModel.shouldShowTemplateDialog) {
            var selectedItem by remember { mutableStateOf(templates[0]) }
            AlertDialog(onDismissRequest = { viewModel.onEvent(PatientListEvent.OnClickDismissDialog) },
                text = {
                    var expanded by remember { mutableStateOf(false) }
                    Column(
                        modifier = Modifier
                            .fillMaxWidth(0.9f)
                            .fillMaxHeight(0.5f)
                            .background(
                                color = Color.White, shape = RoundedCornerShape(size = 10.dp)
                            )
                            .padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = stringResource(R.string.create_from_template_description))
                        ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = {
                            expanded = !expanded
                        }) {
                            TextField(
                                value = selectedItem.name,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text(text = "Select a template plan") },
                                trailingIcon = {
                                    TrailingIcon(
                                        expanded = expanded
                                    )
                                },
                                colors = ExposedDropdownMenuDefaults.textFieldColors()
                            )
                            // menu
                            ExposedDropdownMenu(expanded = expanded,
                                onDismissRequest = { expanded = false }) {
                                // this is a column scope
                                // all the items are added vertically
                                templates.forEach { template ->
                                    // menu item
                                    DropdownMenuItem(onClick = {
                                        selectedItem = template
                                        expanded = false
                                    }) {
                                        Text(text = template.name)
                                    }
                                }
                            }
                        }
                        Button(onClick = {
                            viewModel.onEvent(PatientListEvent.OnSelectTemplatePlan(selectedItem))
                        }) {
                            Text(text = stringResource(id = R.string.create_from_template_plan_button))
                        }

                        Text(text = stringResource(R.string.create_new_plan_description))
                        Button(onClick = {
                            viewModel.onEvent(PatientListEvent.OnSelectNewPlan)
                        }) {
                            Text(text = stringResource(id = R.string.create_new_plan_button))
                        }
                    }
                },
                buttons = {
                })
        }

        Column(modifier = Modifier.fillMaxSize()) {
            ScreenTitle()
            val searchName by viewModel.searchName.collectAsState(initial = "")
            val patients by viewModel.patients.collectAsState(initial = emptyList())
            if (patients.isEmpty() && searchName.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp),
                        text = stringResource(id = R.string.no_patients_registered),
                        textAlign = TextAlign.Center
                    )

                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    TextField(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(size = 10.dp),
                        trailingIcon = {
                            Icon(Icons.Filled.Search, "")
                        },
                        colors = TextFieldDefaults.textFieldColors(
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        value = searchName,
                        onValueChange = {
                            viewModel.onEvent(PatientListEvent.OnSearchPatient(it))
                        },
                        placeholder = {
                            Text(
                                text = stringResource(id = R.string.search_patients)
                            )
                        },
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Words
                        ),
                        singleLine = true
                    )

                    if (patients.isEmpty()) {
                        Text(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 10.dp),
                            text = stringResource(id = R.string.no_patients_found),
                            textAlign = TextAlign.Center
                        )
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(patients) { patient ->
                                PatientItem(
                                    patient = patient,
                                    onEvent = viewModel::onEvent,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}