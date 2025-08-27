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
import androidx.compose.ui.unit.dp
import com.marioalonso.enclave.R
import com.marioalonso.enclave.classes.Folder
import com.marioalonso.enclave.classes.Secret
import com.marioalonso.enclave.viewmodel.SecretViewModel

/**
 * Composable para mostrar un elemento de carpeta con su nombre y el número de secretos que contiene.
 *
 * @param folder La carpeta a mostrar.
 * @param secrets Lista de secretos para contar cuántos están en la carpeta.
 * @param onFolderClick Función que se llama cuando se hace click en la carpeta.
 * @param modifier Modificador opcional para personalizar el diseño.
 */
@Composable
fun FolderItem(
    folder: Folder,
    secrets: List<Secret>,
    onFolderClick: (Folder) -> Unit,
    modifier: Modifier = Modifier
) {
    val numberOfSecretsInFolder = secrets.filter { it.folderId == folder.id }.size
    Row(
        modifier
            .fillMaxWidth()
            .padding(all = 10.dp)
            .clickable { onFolderClick(folder) },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ){
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(end = 8.dp)
        ){
            Icon(
                painter = painterResource(id = R.drawable.folder),
                contentDescription = "Note Icon",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = folder.name,
                modifier = Modifier,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Column() {
            Text(
                text = "$numberOfSecretsInFolder ${stringResource(R.string.secrets_lowercase)}",
                modifier = Modifier,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.secondary
            )
        }
    }
}

/**
 * Composable que envuelve un FolderItem en una caja deslizable para permitir la eliminación mediante un gesto de deslizamiento.
 *
 * @param viewModel ViewModel para manejar la lógica de eliminación de carpetas.
 * @param folder La carpeta a mostrar.
 * @param secrets Lista de secretos para contar cuántos están en la carpeta.
 * @param onItemClick Función que se llama cuando se hace click en la carpeta.
 * @param modifier Modificador opcional para personalizar el diseño.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipeableDeckItem(
    viewModel: SecretViewModel,
    folder: Folder,
    secrets: List<Secret>,
    onItemClick: (Folder) -> Unit,
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
            title = { Text(stringResource(R.string.folder_confirmation)) },
            text = { Text(stringResource(R.string.folder_confirmation_extended)) },
            confirmButton = {
                Button(onClick = {
                    viewModel.deleteFolder(folder.id)
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
                        .padding(8.dp)
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
            FolderItem(folder = folder, secrets = secrets, onFolderClick = onItemClick)
        }
    }
}