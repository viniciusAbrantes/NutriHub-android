package com.abrantesv.nutrihub.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.abrantesv.nutrihub.R

@Composable
fun ScreenTitle(screenName: String = stringResource(id = R.string.app_name)) {
    Text(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp),
        text = screenName,
        fontSize = 26.sp,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center
    )
}