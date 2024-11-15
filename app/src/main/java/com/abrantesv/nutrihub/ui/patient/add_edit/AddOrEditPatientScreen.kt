package com.abrantesv.nutrihub.ui.patient.add_edit

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Scaffold
import androidx.compose.material.Slider
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.abrantesv.nutrihub.R
import com.abrantesv.nutrihub.ui.ScreenTitle
import com.abrantesv.nutrihub.ui.patient.add_edit.AddOrEditPatientViewModel.Companion.INVALID_AGE
import com.abrantesv.nutrihub.ui.patient.add_edit.AddOrEditPatientViewModel.Companion.MAX_HEIGHT
import com.abrantesv.nutrihub.ui.patient.add_edit.AddOrEditPatientViewModel.Companion.MAX_WEIGHT
import com.abrantesv.nutrihub.ui.patient.add_edit.AddOrEditPatientViewModel.Companion.MIN_HEIGHT
import com.abrantesv.nutrihub.ui.patient.add_edit.AddOrEditPatientViewModel.Companion.MIN_WEIGHT
import com.abrantesv.nutrihub.ui.patient.add_edit.AddOrEditPatientViewModel.Companion.SEX_FEMALE_TEXT
import com.abrantesv.nutrihub.ui.patient.add_edit.AddOrEditPatientViewModel.Companion.SEX_MALE_TEXT
import com.abrantesv.nutrihub.ui.patient.add_edit.AddOrEditPatientViewModel.Companion.SEX_OTHER_TEXT
import com.abrantesv.nutrihub.util.UiEvent

@Composable
fun AddOrEditPatientScreen(
    onPopBackStack: () -> Unit, viewModel: AddOrEditPatientViewModel = hiltViewModel()
) {
    val scaffoldState = rememberScaffoldState()

    LaunchedEffect(key1 = true) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is UiEvent.PopBackStack -> onPopBackStack()
                is UiEvent.ShowSnackBar -> {
                    scaffoldState.snackbarHostState.showSnackbar(
                        message = event.message,
                        actionLabel = event.action,
                    )
                }

                else -> {}
            }
        }
    }

    Scaffold(
        scaffoldState = scaffoldState,
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize()) {
            ScreenTitle(stringResource(id = R.string.patient_info_screen_title))
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .padding(16.dp)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    TextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = viewModel.name,
                        onValueChange = {
                            viewModel.onEvent(AddOrEditPatientEvent.OnChangeName(it))
                        },
                        placeholder = {
                            Text(
                                text = stringResource(id = R.string.patient_name_placeholder)
                            )
                        },
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Words
                        ),
                        singleLine = true
                    )

                    TextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = viewModel.email,
                        onValueChange = {
                            viewModel.onEvent(AddOrEditPatientEvent.OnChangeEmail(it))
                        },
                        placeholder = {
                            Text(
                                text = stringResource(id = R.string.patient_email_placeholder)
                            )
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email
                        ),
                        singleLine = true
                    )

                    TextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = if (viewModel.age == INVALID_AGE) "" else viewModel.age.toString(),
                        onValueChange = {
                            it.toIntOrNull()?.let { age ->
                                viewModel.onEvent(AddOrEditPatientEvent.OnChangeAge(age))
                            } ?: run {
                                viewModel.onEvent(AddOrEditPatientEvent.OnChangeAge(INVALID_AGE))
                            }
                        },
                        placeholder = {
                            Text(
                                text = stringResource(id = R.string.patient_age_placeholder)
                            )
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number
                        ),
                        singleLine = true
                    )

                    Column(modifier = Modifier.fillMaxWidth()) {
                        var heightValue = viewModel.height
                        Text(
                            text = stringResource(id = R.string.patient_height, heightValue),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                        )
                        Slider(
                            value = heightValue, onValueChange = {
                                heightValue = it
                                viewModel.onEvent(AddOrEditPatientEvent.OnChangeHeight(it))
                            }, valueRange = MIN_HEIGHT..MAX_HEIGHT
                        )
                    }

                    Column(modifier = Modifier.fillMaxWidth()) {
                        var weightValue = viewModel.weight
                        Text(
                            text = stringResource(id = R.string.patient_weight, weightValue),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                        )
                        Slider(
                            value = weightValue, onValueChange = {
                                weightValue = it
                                viewModel.onEvent(AddOrEditPatientEvent.OnChangeWeight(it))
                            }, valueRange = MIN_WEIGHT..MAX_WEIGHT
                        )
                    }

                    Column(modifier = Modifier.fillMaxWidth()) {
                        var expanded by remember { mutableStateOf(false) }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            val sexText = viewModel.getSexText()
                            Text(
                                text = stringResource(id = R.string.patient_sex),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(end = 5.dp)
                            )

                            Row(
                                Modifier.clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null
                                ) { expanded = !expanded },
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = sexText,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                )
                                Image(
                                    imageVector = Icons.Default.KeyboardArrowDown,
                                    contentDescription = null
                                )
                            }
                        }
                        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                            listOf(SEX_MALE_TEXT, SEX_FEMALE_TEXT, SEX_OTHER_TEXT).forEach { text ->
                                DropdownMenuItem(onClick = {
                                    expanded = false
                                    viewModel.onEvent(
                                        AddOrEditPatientEvent.OnChangeSex(
                                            viewModel.getSexInt(text)
                                        )
                                    )
                                }) {
                                    Text(text)
                                }
                            }
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                ) {
                    Button(onClick = {
                        viewModel.onEvent(AddOrEditPatientEvent.OnClickSave)
                    }) {
                        Text(text = stringResource(id = R.string.button_save))
                    }
                    Button(onClick = {
                        viewModel.onEvent(AddOrEditPatientEvent.OnClickCancel)
                    }) {
                        Text(text = stringResource(id = R.string.button_cancel))
                    }
                }
            }
        }
    }
}