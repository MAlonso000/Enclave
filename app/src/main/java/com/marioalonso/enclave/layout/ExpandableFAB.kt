package com.marioalonso.enclave.layout

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.marioalonso.enclave.R
import androidx.compose.ui.res.painterResource

@Composable
fun ExpandableFab(
    isExpanded: Boolean,
    onFabToggle: () -> Unit,
    onAddType1: () -> Unit,
    onAddType2: () -> Unit,
    onAddType3: () -> Unit
) {
    Box(modifier = Modifier, contentAlignment = Alignment.BottomCenter) {
        Column(
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(0.dp),
            modifier = Modifier.padding(bottom = 60.dp)
        ) {
            if (isExpanded) {
                SmallFloatingActionButton(
                    onClick = onAddType1,
                    containerColor = MaterialTheme.colorScheme.secondary
                ) {
                    Icon(painter = painterResource(R.drawable.key_icon), contentDescription = "Add credential secret")
                }
                SmallFloatingActionButton(
                    onClick = onAddType2,
                    containerColor = MaterialTheme.colorScheme.secondary
                ) {
                    Icon(painter = painterResource(R.drawable.note_icon), contentDescription = "Add note secret")
                }
                SmallFloatingActionButton(
                    onClick = onAddType3,
                    containerColor = MaterialTheme.colorScheme.secondary
                ) {
                    Icon(painter = painterResource(R.drawable.credit_icon), contentDescription = "Add credit card secret")
                }
            }
        }

        // Botón principal
        FloatingActionButton(
            onClick = onFabToggle,
            containerColor = MaterialTheme.colorScheme.primary,
            modifier = Modifier
        ) {
            Icon(
                imageVector = if (isExpanded) Icons.Default.Close else Icons.Default.Add,
                contentDescription = if (isExpanded) "Close add" else "Add secret",
            )
        }
    }
}