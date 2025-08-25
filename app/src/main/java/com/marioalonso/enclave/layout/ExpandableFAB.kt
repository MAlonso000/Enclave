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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.marioalonso.enclave.R

/**
 * Composable para un Floating Action Button (FAB) expandible que muestra opciones adicionales al ser presionado.
 *
 * @param isExpanded Indica si el FAB está expandido o no.
 * @param onFabToggle Función que se llama al presionar el FAB principal para alternar su estado.
 * @param onAddCredential Función que se llama al presionar la opción de credencial.
 * @param onAddNote Función que se llama al presionar la opción de nota.
 * @param onAddCard Función que se llama al presionar la opción de tarjeta de crédito.
 */
@Composable
fun ExpandableFab(
    isExpanded: Boolean,
    onFabToggle: () -> Unit,
    onAddCredential: () -> Unit,
    onAddNote: () -> Unit,
    onAddCard: () -> Unit
) {
    Box(modifier = Modifier, contentAlignment = Alignment.BottomCenter) {
        Column(
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(0.dp),
            modifier = Modifier.padding(bottom = 60.dp)
        ) {
            if (isExpanded) {
                SmallFloatingActionButton(
                    onClick = onAddCredential,
                    containerColor = MaterialTheme.colorScheme.secondary
                ) {
                    Icon(painter = painterResource(R.drawable.key_icon), contentDescription = "Add credential secret")
                }
                SmallFloatingActionButton(
                    onClick = onAddNote,
                    containerColor = MaterialTheme.colorScheme.secondary
                ) {
                    Icon(painter = painterResource(R.drawable.note_icon), contentDescription = "Add note secret")
                }
                SmallFloatingActionButton(
                    onClick = onAddCard,
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