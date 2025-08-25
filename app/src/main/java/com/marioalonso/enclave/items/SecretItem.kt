package com.marioalonso.enclave.items

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.marioalonso.enclave.R
import com.marioalonso.enclave.classes.CardSecret
import com.marioalonso.enclave.classes.CredentialSecret
import com.marioalonso.enclave.classes.NoteSecret
import com.marioalonso.enclave.classes.Secret
import com.marioalonso.enclave.utils.AESCipherGCM
import com.marioalonso.enclave.viewmodel.SecretViewModel

/**
 * Composable para mostrar un elemento de secreto (nota, tarjeta, credencial) en una lista.
 *
 * @param secret El secreto a mostrar.
 * @param onItemClick Función que se llama cuando se hace clic en el elemento.
 * @param modifier Modificador opcional para personalizar el diseño.
 */
@Composable
fun SecretItem(
    secret: Secret,
    onItemClick : (Secret) -> Unit,
    modifier: Modifier = Modifier
) {
    var switchState by remember { mutableStateOf(false) }
    val onSwitchChange = { it: Boolean -> switchState = it }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(all = 10.dp)
            .clickable { onItemClick(secret) },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Box(modifier = Modifier.weight(1f)) {
            when (secret) {
                is NoteSecret -> SecretData(secret = secret, switchState = switchState, modifier = modifier)
                is CardSecret -> SecretData(secret = secret, switchState = switchState, modifier = modifier)
                is CredentialSecret -> SecretData(secret = secret, switchState = switchState, modifier = modifier)
            }
        }
        SwitchIcon(
            switchState = switchState,
            onSwitchChange = onSwitchChange,
        )
    }
}

/**
 * Función para mostrar "N/A" si el campo está vacío o es nulo
 *
 * @param input El campo de texto a evaluar
 * @return El campo de texto o "N/A" si está vacío o es nulo
 */
fun display(input: String): String {
    return if (input == null || input.isBlank()) "N/A" else input
}

/**
 * Función para mostrar "N/A" si el campo está vacío o es nulo, desencriptando el valor
 *
 * @param input El campo de texto a evaluar
 * @return El campo de texto desencriptado o "N/A" si está vacío o es nulo
 */
fun displayEncrypted(input: String): String {
    return if (input == null || input.isBlank()) "N/A" else AESCipherGCM.decrypt(input)
}

/**
 * Composable para mostrar los datos de un secreto de tipo nota.
 *
 * @param secret El secreto de tipo nota a mostrar.
 * @param switchState Estado del switch para mostrar/ocultar información sensible.
 * @param modifier Modificador opcional para personalizar el diseño.
 */
@Composable
fun SecretData(
    secret: NoteSecret,
    switchState: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.Top,
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .padding(end = 10.dp),
            contentAlignment = Alignment.TopStart
        ) {
            SecretIcon(secret)
        }
        Column(
            verticalArrangement = Arrangement.Top,
        ) {
            Text(
                text = secret.title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (switchState) {
                Text("${stringResource(R.string.note)}: ${displayEncrypted(secret.encryptedNote)}", modifier, style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.secondary)
            }
        }
    }
}

/**
 * Composable para mostrar los datos de un secreto de tipo tarjeta.
 *
 * @param secret El secreto de tipo tarjeta a mostrar.
 * @param switchState Estado del switch para mostrar/ocultar información sensible.
 * @param modifier Modificador opcional para personalizar el diseño.
 */
@Composable
fun SecretData(
    secret: CardSecret,
    switchState: Boolean,
    modifier: Modifier = Modifier
) {

    Row(
        verticalAlignment = Alignment.Top,
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .padding(end = 10.dp),
            contentAlignment = Alignment.TopStart
        ) {
            SecretIcon(secret)
        }
        Column(
            verticalArrangement = Arrangement.Top,
        ) {
            Text(
                text = secret.title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (switchState) {
                Text("${stringResource(R.string.owner)}: ${display(secret.ownerName)}", modifier, style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.secondary)
                Text("${stringResource(R.string.card_number)}: ${displayEncrypted(secret.encryptedCardNumber)}", modifier, style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.secondary)
                Text("${stringResource(R.string.pin)}: ${displayEncrypted(secret.encryptedPin)}", modifier, style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.secondary)
                Text("${stringResource(R.string.brand)}: ${display(secret.brand)}", modifier, style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.secondary)
                Text("${stringResource(R.string.expiration_date)}: ${display(secret.expirationDate)}", modifier, style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.secondary)
                Text("${stringResource(R.string.cvv)}: ${displayEncrypted(secret.encryptedCVV)}", modifier, style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.secondary)
            }
        }
    }
}

/**
 * Composable para mostrar los datos de un secreto de tipo credencial.
 *
 * @param secret El secreto de tipo credencial a mostrar.
 * @param switchState Estado del switch para mostrar/ocultar información sensible.
 * @param modifier Modificador opcional para personalizar el diseño.
 */
@Composable
fun SecretData(
    secret: CredentialSecret,
    switchState: Boolean,
    modifier: Modifier = Modifier
) {

    Row(
        verticalAlignment = Alignment.Top,
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .padding(end = 10.dp),
            contentAlignment = Alignment.TopStart
        ) {
            SecretIcon(secret)
        }
        Column(
            verticalArrangement = Arrangement.Top,
        ) {
            Text(
                text = secret.title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (switchState) {
                Text("${stringResource(R.string.username)}: ${display(secret.username)}", modifier, style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.secondary)
                Text("${stringResource(R.string.email)}: ${display(secret.email)}", modifier, style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.secondary)
                Text("${stringResource(R.string.url)}: ${display(secret.url)}", modifier, style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.secondary)
                Text("${stringResource(R.string.password)}: ${displayEncrypted(secret.encryptedPassword)}", modifier, style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.secondary)
            }
        }
    }
}

/**
 * Composable que envuelve un SecretItem con funcionalidad de swipe to delete.
 *
 * @param viewModel ViewModel para manejar la lógica de negocio.
 * @param secret El secreto a mostrar.
 * @param onItemClick Función que se llama cuando se hace clic en el elemento.
 * @param modifier Modificador opcional para personalizar el diseño.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipeableSecretItem(
    viewModel: SecretViewModel,
    secret: Secret,
    onItemClick: (Secret) -> Unit,
    modifier: Modifier = Modifier,
) {
    val dismissState = rememberSwipeToDismissBoxState()
    var showDialog by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = dismissState.currentValue) {
        when (dismissState.currentValue) {
            SwipeToDismissBoxValue.EndToStart -> {
                showDialog = true
                dismissState.snapTo(SwipeToDismissBoxValue.Settled)
            }
            else -> { /* No hacer nada */ }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(stringResource(R.string.secret_confirmation)) },
            text = { Text(stringResource(R.string.secret_confirmation_extended)) },
            confirmButton = {
                Button(onClick = {
                        viewModel.deleteSecret(secret.id)
                        showDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text(
                        stringResource(R.string.delete)
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDialog = false }
                ) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }
    Surface(
        modifier = modifier.padding(5.dp).fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surfaceVariant,
    ) {
        SwipeToDismissBox(
            state = dismissState,
            backgroundContent = {
                val color by animateColorAsState(
                    when (dismissState.targetValue) {
                        SwipeToDismissBoxValue.StartToEnd -> Color.Green
                        SwipeToDismissBoxValue.EndToStart -> Color.Red
                        else -> Color.Transparent
                    }, label = ""
                )
                val iconScale by animateFloatAsState(
                    targetValue =
                        if (dismissState.targetValue == SwipeToDismissBoxValue.EndToStart) 1.3f else 0.0f,
                    label = "DELETE"
                )
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(color)
                ) {
                    Icon(
                        modifier = Modifier
                            .scale(iconScale)
                            .align(Alignment.CenterEnd),
                        imageVector = Icons.Outlined.Delete,
                        contentDescription = "Delete",
                        tint = Color.White
                    )
                }
            },
            enableDismissFromStartToEnd = false
        ) {
            SecretItem(secret = secret, onItemClick = onItemClick)
        }
    }
}

/**
 * Composable para mostrar el icono correspondiente según el tipo de secreto.
 *
 * @param secret El secreto cuyo icono se va a mostrar.
 */
@Composable
fun SecretIcon(secret: Secret){
    when (secret) {
        is NoteSecret -> Icon(
            painter = painterResource(id = R.drawable.note_icon),
            contentDescription = "Note Icon",
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        is CardSecret -> Icon(
            painter = painterResource(id = R.drawable.credit_icon),
            contentDescription = "Credit Card Icon",
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        is CredentialSecret -> Icon(
            painter = painterResource(id = R.drawable.key_icon),
            contentDescription = "Key Icon",
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/**
 * Composable para mostrar un icono de switch (ojo) que permite alternar entre mostrar y ocultar información sensible.
 *
 * @param switchState Estado actual del switch (true para mostrar, false para ocultar).
 * @param onSwitchChange Función que se llama cuando el estado del switch cambia.
 */
@Composable
fun SwitchIcon(switchState: Boolean, onSwitchChange: (Boolean) -> Unit) {
    val drawableResource = if (switchState) R.drawable.eye_on
    else R.drawable.eye_off

    Icon(
        painter = painterResource(id = drawableResource),
        contentDescription = "contentDescription",
        modifier = Modifier
            .size(32.dp)
            .clickable { onSwitchChange(!switchState) },
        tint = MaterialTheme.colorScheme.primary
    )
}