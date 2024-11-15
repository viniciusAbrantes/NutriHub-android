package com.abrantesv.nutrihub.ui.plan.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.abrantesv.nutrihub.R
import com.abrantesv.nutrihub.ui.ScreenTitle
import com.abrantesv.nutrihub.util.UiEvent

@Composable
fun PlanListScreen(
    onNavigate: (UiEvent.Navigate) -> Unit, viewModel: PlanListViewModel = hiltViewModel()
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
                viewModel.onEvent(PlanListEvent.OnClickAddPlan)
            }) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(id = R.string.add_meal_plan_description)
                )
            }
        },
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize()) {
            ScreenTitle()
            val plans by viewModel.plans.collectAsState(initial = emptyList())
            if (plans.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp),
                        text = stringResource(id = R.string.no_meal_plans_registered),
                        textAlign = TextAlign.Center
                    )

                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(plans) { plan ->
                        PlanItem(
                            plan = plan,
                            onEvent = viewModel::onEvent,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}