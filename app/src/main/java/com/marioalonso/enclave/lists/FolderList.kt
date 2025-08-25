package com.marioalonso.enclave.lists

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.marioalonso.enclave.R
import com.marioalonso.enclave.classes.Folder
import com.marioalonso.enclave.classes.Secret
import com.marioalonso.enclave.items.SwipeableDeckItem
import com.marioalonso.enclave.viewmodel.SecretViewModel

/**
 * Composable para mostrar una lista de carpetas con sus respectivos secretos.
 *
 * @param viewModel ViewModel para manejar la lógica de negocio.
 * @param folders Lista de carpetas a mostrar.
 * @param secrets Lista de secretos asociados a las carpetas.
 * @param onFolderClick Función lambda que se ejecuta al hacer clic en una carpeta.
 */
@Composable
fun FolderList(
    viewModel: SecretViewModel,
    folders: List<Folder>,
    secrets: List<Secret>,
    onFolderClick: (Folder) -> Unit,
){
    LazyColumn {
        item{
            Text(
                stringResource(R.string.folder_list),
                Modifier.padding(12.dp).fillMaxWidth(),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
            )
        }
        items(folders) { folder ->
            SwipeableDeckItem(
                viewModel = viewModel,
                folder = folder,
                secrets = secrets,
                onItemClick = onFolderClick
            )
        }
    }
}