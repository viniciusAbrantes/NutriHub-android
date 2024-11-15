package com.abrantesv.nutrihub.ui.plan.add_edit

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.abrantesv.nutrihub.R
import com.abrantesv.nutrihub.ui.ScreenTitle
import com.abrantesv.nutrihub.util.UiEvent


@Composable
fun AddOrEditPlanScreen(
    onPopBackStack: () -> Unit, viewModel: AddOrEditPlanViewModel = hiltViewModel()
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            ScreenTitle(stringResource(id = R.string.plan_screen_title))
            if (viewModel.isInitialized) {
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
                            value = viewModel.planName,
                            onValueChange = {
                                viewModel.onEvent(AddOrEditPlanEvent.OnChangePlanName(it))
                            },
                            placeholder = {
                                Text(text = stringResource(id = R.string.plan_name_placeholder))
                            },
                            keyboardOptions = KeyboardOptions(
                                capitalization = KeyboardCapitalization.Words
                            ),
                            singleLine = true
                        )
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .verticalScroll(rememberScrollState()),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            viewModel.meals.forEach { meal ->
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(
                                            color = Color.Gray.copy(alpha = 0.35f),
                                            shape = RoundedCornerShape(size = 10.dp)
                                        )
                                        .padding(start = 10.dp, bottom = 10.dp),
                                    verticalArrangement = Arrangement.spacedBy(5.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            modifier = Modifier.weight(1f),
                                            text = meal.name,
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        IconButton(onClick = {
                                            viewModel.onEvent(
                                                AddOrEditPlanEvent.OnClickEditMeal(
                                                    meal
                                                )
                                            )
                                        }) {
                                            Icon(
                                                imageVector = Icons.Default.Create,
                                                contentDescription = stringResource(
                                                    id = R.string.meal_edit_button
                                                )
                                            )
                                        }
                                    }
                                    meal.foods.forEach { food ->
                                        Text(
                                            text = stringResource(
                                                id = R.string.food_text,
                                                food.amount,
                                                food.unit,
                                                food.name
                                            ), fontSize = 16.sp
                                        )
                                    }
                                }
                            }
                            Button(modifier = Modifier.align(Alignment.End),
                                onClick = { viewModel.onEvent(AddOrEditPlanEvent.OnClickAddMeal) }) {
                                Text(text = stringResource(id = R.string.add_meal_button))
                            }
                        }
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                    ) {
                        Button(onClick = {
                            viewModel.onEvent(AddOrEditPlanEvent.OnClickClosePlan)
                        }) {
                            Text(text = stringResource(id = R.string.button_close))
                        }
                    }
                }
            }
        }
        if (viewModel.isEditingMeal) {
            AddOrEditMealDialog(modifier = Modifier.padding(paddingValues), viewModel)
        }
    }
}

@Composable
private fun AddOrEditMealDialog(modifier: Modifier, viewModel: AddOrEditPlanViewModel) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f))
            .safeDrawingPadding()
            .padding(horizontal = 16.dp, vertical = 50.dp)
    ) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(
                    color = Color.White, shape = RoundedCornerShape(size = 10.dp)
                )
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column(
                modifier = modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                var mealName by remember { mutableStateOf(viewModel.mealName) }
                TextField(
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold),
                    value = mealName,
                    onValueChange = {
                        viewModel.onEvent(AddOrEditPlanEvent.OnChangeMealName(it))
                        mealName = it
                    },
                    placeholder = {
                        Text(text = stringResource(id = R.string.meal_name_placeholder))
                    },
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Words
                    ),
                    singleLine = true
                )

                viewModel.foods.forEach { food ->
                    var foodName by remember { mutableStateOf(food.name) }
                    var foodUnit by remember { mutableStateOf(food.unit) }
                    var foodAmount by remember { mutableIntStateOf(food.amount) }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        TextField(
                            modifier = Modifier.weight(1f),
                            value = foodAmount.toString(),
                            onValueChange = {
                                foodAmount = if (it.isEmpty()) 0 else it.toInt()
                                viewModel.onEvent(
                                    AddOrEditPlanEvent.OnChangeFood(
                                        id = food.id ?: -1,
                                        name = foodName,
                                        unit = foodUnit,
                                        amount = foodAmount
                                    )
                                )
                            },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number
                            ),
                            singleLine = true
                        )
                        TextField(
                            modifier = Modifier.weight(1f), value = foodUnit, onValueChange = {
                                foodUnit = it
                                viewModel.onEvent(
                                    AddOrEditPlanEvent.OnChangeFood(
                                        id = food.id ?: -1,
                                        name = foodName,
                                        unit = foodUnit,
                                        amount = foodAmount
                                    )
                                )
                            }, keyboardOptions = KeyboardOptions(
                                capitalization = KeyboardCapitalization.Words
                            ), singleLine = true
                        )
                        TextField(
                            modifier = Modifier.weight(3f), value = foodName, onValueChange = {
                                foodName = it
                                viewModel.onEvent(
                                    AddOrEditPlanEvent.OnChangeFood(
                                        id = food.id ?: -1,
                                        name = foodName,
                                        unit = foodUnit,
                                        amount = foodAmount
                                    )
                                )
                            }, keyboardOptions = KeyboardOptions(
                                capitalization = KeyboardCapitalization.Words
                            ), singleLine = true
                        )
                    }
                }

                Button(modifier = Modifier.align(Alignment.End),
                    onClick = { viewModel.onEvent(AddOrEditPlanEvent.OnClickAddFood) }) {
                    Icon(
                        imageVector = Icons.Default.Add, contentDescription = stringResource(
                            id = R.string.add_food_button
                        )
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                Button(onClick = {
                    viewModel.onEvent(AddOrEditPlanEvent.OnClickCloseMeal)
                }) {
                    Text(text = stringResource(id = R.string.button_close))
                }
            }
        }
    }
}