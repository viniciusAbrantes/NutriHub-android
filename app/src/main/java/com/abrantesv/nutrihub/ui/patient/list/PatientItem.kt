package com.abrantesv.nutrihub.ui.patient.list

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.Composable
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
import com.abrantesv.nutrihub.data.entities.Patient
import java.text.DateFormat.getDateTimeInstance
import java.util.Date

@Composable
fun PatientItem(
    patient: Patient, onEvent: (PatientListEvent) -> Unit, modifier: Modifier = Modifier
) {
    var showDialog by remember { mutableStateOf(false) }
    if (showDialog) {

        AlertDialog(title = {
            Text(
                text = stringResource(
                    id = R.string.delete_patient_dialog_title, patient.name.split(" ")[0]
                )
            )
        }, text = {
            Text(
                text = stringResource(
                    id = R.string.delete_patient_dialog_description, patient.name
                )
            )
        }, onDismissRequest = { showDialog = false }, confirmButton = {
            TextButton(onClick = {
                onEvent(PatientListEvent.OnClickDeletePatient(patient))
                showDialog = false
            }) {
                Text(stringResource(id = R.string.dialog_yes))
            }
        }, dismissButton = {
            TextButton(onClick = { showDialog = false }) {
                Text(stringResource(id = R.string.dialog_no))
            }
        })
    }

    Row(
        modifier = modifier
            .background(
                color = Color.Gray.copy(alpha = 0.3f), shape = RoundedCornerShape(size = 10.dp)
            )
            .padding(start = 10.dp, top = 10.dp, bottom = 10.dp),
        verticalAlignment = Alignment.CenterVertically

    ) {
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.Center) {
            Text(text = patient.name, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            val date = getDateTimeInstance().format(Date(patient.lastUpdated))
            Text(text = stringResource(id = R.string.patient_last_update_date, date))
        }

        var expanded by remember { mutableStateOf(false) }
        Column {
            IconButton(onClick = { expanded = !expanded }) {
                Icon(
                    imageVector = Icons.Default.MoreVert, contentDescription = stringResource(
                        id = R.string.patient_options_content_description
                    )
                )
            }

            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                DropdownMenuItem(onClick = {
                    expanded = false
                    showDialog = true
                }) {
                    Text(stringResource(id = R.string.patient_delete_option))
                }
                DropdownMenuItem(onClick = {
                    expanded = false
                    onEvent(PatientListEvent.OnClickEditPatient(patient))
                }) {
                    Text(stringResource(id = R.string.patient_edit_option))
                }
                DropdownMenuItem(onClick = {
                    onEvent(PatientListEvent.OnClickUpdatePlan(patient))
                    expanded = false
                }) {
                    val stringRes = if (patient.planId == null) {
                        R.string.patient_add_meal_plan_option
                    } else {
                        R.string.patient_edit_meal_plan_option
                    }
                    Text(stringResource(id = stringRes))
                }
            }
        }
    }
}