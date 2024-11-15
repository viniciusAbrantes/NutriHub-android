package com.abrantesv.nutrihub.ui.plan.list

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.abrantesv.nutrihub.R
import com.abrantesv.nutrihub.data.entities.Plan

@Composable
fun PlanItem(
    plan: Plan, onEvent: (PlanListEvent) -> Unit, modifier: Modifier = Modifier
) {
    val showDialog = remember { mutableStateOf(false) }
    if (showDialog.value) {
        AlertDialog(title = {
            Text(text = stringResource(id = R.string.delete_plan_dialog_title))
        }, text = {
            Text(
                text = stringResource(
                    id = R.string.delete_plan_dialog_description, plan.name
                )
            )
        }, onDismissRequest = { showDialog.value = false }, confirmButton = {
            TextButton(onClick = {
                onEvent(PlanListEvent.OnClickDeletePlan(plan))
                showDialog.value = false
            }) {
                Text(stringResource(id = R.string.dialog_yes))
            }
        }, dismissButton = {
            TextButton(onClick = { showDialog.value = false }) {
                Text(stringResource(id = R.string.dialog_no))
            }
        })
    }

    var isCollapsed by remember { mutableStateOf(false) }
    Column(modifier = modifier
        .clickable { isCollapsed = !isCollapsed }
        .background(
            color = Color.Gray.copy(alpha = 0.3f), shape = RoundedCornerShape(size = 10.dp)
        )
        .padding(start = 10.dp, bottom = 10.dp)) {

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                modifier = Modifier.weight(1f),
                text = plan.name,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            OptionsDropDown(showDialog, onEvent, plan)
        }
        if (!isCollapsed) PlanContent(
            modifier = Modifier
                .then(modifier)
                .padding(end = 10.dp),
            plan = plan
        )
    }
}

@Composable
private fun OptionsDropDown(
    showDialog: MutableState<Boolean>, onEvent: (PlanListEvent) -> Unit, plan: Plan
) {
    Column {
        var isDropDownExpanded by remember { mutableStateOf(false) }
        IconButton(onClick = { isDropDownExpanded = !isDropDownExpanded }) {
            Icon(
                imageVector = Icons.Default.MoreVert, contentDescription = stringResource(
                    id = R.string.plan_options_content_description
                )
            )
        }
        DropdownMenu(expanded = isDropDownExpanded,
            onDismissRequest = { isDropDownExpanded = false }) {
            DropdownMenuItem(onClick = {
                isDropDownExpanded = false
                showDialog.value = true
            }) {
                Text(stringResource(id = R.string.plan_delete_option))
            }
            DropdownMenuItem(onClick = {
                isDropDownExpanded = false
                onEvent(PlanListEvent.OnClickEditPlan(plan))
            }) {
                Text(stringResource(id = R.string.plan_edit_option))
            }
        }
    }
}

@Composable
private fun PlanContent(modifier: Modifier, plan: Plan) {
    Column(
        modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        plan.meals.forEach { meal ->
            Column(
                modifier = modifier
                    .fillMaxWidth()
                    .background(
                        color = Color.Gray.copy(alpha = 0.35f),
                        shape = RoundedCornerShape(size = 10.dp)
                    )
                    .padding(10.dp),
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Text(text = meal.name, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                meal.foods.forEach { food ->
                    Text(
                        text = stringResource(
                            id = R.string.food_text, food.amount, food.unit, food.name
                        ), fontSize = 16.sp
                    )
                }
            }
        }
    }
}