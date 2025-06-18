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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.marioalonso.enclave.classes.Secret
import com.marioalonso.enclave.R
import com.marioalonso.enclave.classes.CardSecret
import com.marioalonso.enclave.classes.CredentialSecret
import com.marioalonso.enclave.classes.NoteSecret
import com.marioalonso.enclave.viewmodel.SecretViewModel

//@Composable
//fun SecretItem(
//    secret: Secret,
//    onItemClick : (Secret) -> Unit,
//    modifier: Modifier = Modifier
//) {
//    val context = LocalContext.current
//    var switchState by remember { mutableStateOf(false) }
//    val onSwitchChange = { it: Boolean -> switchState = it }
//
//    Row(
//        modifier.fillMaxWidth().padding(all = 20.dp).clickable { onItemClick(secret) },
//        horizontalArrangement = Arrangement.SpaceBetween,
//        verticalAlignment = Alignment.Top
//    ) {
//        Row{
//            SwitchICon(
//                switchState = switchState,
//                onSwitchChange = onSwitchChange,
//            )
//            when (secret) {
//                is NoteSecret -> SecretData(secret = secret, switchState = switchState, modifier = modifier)
//                is CardSecret -> SecretData(secret = secret, switchState = switchState, modifier = modifier)
//                is CredentialSecret -> SecretData(secret = secret, switchState = switchState, modifier = modifier)
//            }
//        }
//        Text("prueba")
//    }
//}

@Composable
fun SecretItem(
    secret: Secret,
    onItemClick : (Secret) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var switchState by remember { mutableStateOf(false) }
    val onSwitchChange = { it: Boolean -> switchState = it }

    Row(
        modifier.fillMaxWidth().padding(all = 10.dp).clickable { onItemClick(secret) },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        when (secret) {
            is NoteSecret -> SecretData(secret = secret, switchState = switchState, modifier = modifier)
            is CardSecret -> SecretData(secret = secret, switchState = switchState, modifier = modifier)
            is CredentialSecret -> SecretData(secret = secret, switchState = switchState, modifier = modifier)
        }
        SwitchIcon(
            switchState = switchState,
            onSwitchChange = onSwitchChange,
        )
    }
}

fun display(input: String): String {
    return if (input == null || input.isBlank()) "N/A" else input
}

@Composable
fun SecretData(
    secret: NoteSecret,
    switchState: Boolean,
    modifier: Modifier = Modifier
) {
//    val salt = Base64.getDecoder().decode("OnNCziG6nTZeun+nWHtTOw==")
//    val key = remember { AESCipherGCM.deriveKey("miClaveMaestra123".toCharArray(), salt) }
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
            Text(secret.title, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            if (switchState) {
                Text("${stringResource(R.string.note)}: ${display(secret.encryptedNote)}", modifier, style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.secondary)
            }
        }
    }
}


@Composable
fun SecretData(
    secret: CardSecret,
    switchState: Boolean,
    modifier: Modifier = Modifier
) {
//    val salt = Base64.getDecoder().decode("OnNCziG6nTZeun+nWHtTOw==")
//    val key = remember { AESCipherGCM.deriveKey("miClaveMaestra123".toCharArray(), salt) }

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
            Text(secret.title, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            if (switchState) {
                Text("${stringResource(R.string.owner)}: ${display(secret.ownerName)}", modifier, style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.secondary)
                Text("${stringResource(R.string.card_number)}: ${display(secret.encryptedCardNumber)}", modifier, style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.secondary)
                Text("${stringResource(R.string.pin)}: ${display(secret.encryptedPin)}", modifier, style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.secondary)
                Text("${stringResource(R.string.brand)}: ${display(secret.brand)}", modifier, style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.secondary)
                Text("${stringResource(R.string.expiration_date)}: ${display(secret.expirationDate)}", modifier, style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.secondary)
                Text("${stringResource(R.string.cvv)}: ${display(secret.encryptedCVV)}", modifier, style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.secondary)
            }
        }
    }
}

@Composable
fun SecretData(
    secret: CredentialSecret,
    switchState: Boolean,
    modifier: Modifier = Modifier
) {
//    val salt = Base64.getDecoder().decode("OnNCziG6nTZeun+nWHtTOw==")
//    val key = remember { AESCipherGCM.deriveKey("miClaveMaestra123".toCharArray(), salt) }

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
//            Text(secret.title, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(secret.title, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            if (switchState) {
                Text("${stringResource(R.string.username)}: ${display(secret.username)}", modifier, style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.secondary)
                Text("${stringResource(R.string.email)}: ${display(secret.email)}", modifier, style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.secondary)
                Text("${stringResource(R.string.url)}: ${display(secret.url)}", modifier, style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.secondary)
                Text("${stringResource(R.string.password)}: ${display(secret.encryptedPassword)}", modifier, style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.secondary)
            }
        }
    }
}

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
                Text(
                    stringResource(R.string.delete),
                    modifier = Modifier.clickable {
                        viewModel.deleteSecret(secret.id)
                        showDialog = false
                    }
                )
            },
            dismissButton = {
                Text(
                    stringResource(R.string.cancel),
                    modifier = Modifier.clickable { showDialog = false }
                )
            }
        )
    }
    Surface(
        modifier = modifier.padding(5.dp).fillMaxWidth(),
        shape = MaterialTheme.shapes.medium, // Redondea los bordes
        color = MaterialTheme.colorScheme.surfaceVariant, // Color de fondo
//        tonalElevation = 1.dp, // Pequeña elevación para dar efecto de tarjeta
//        shadowElevation = 4.dp, // Si quieres una sombra más pronunciada
//         border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline) // Si quieres un borde
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