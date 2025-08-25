package com.marioalonso.enclave.lists

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.marioalonso.enclave.R
import com.marioalonso.enclave.classes.Secret
import com.marioalonso.enclave.items.SwipeableSecretItem
import com.marioalonso.enclave.viewmodel.SecretViewModel

/**
 * Composable para mostrar una lista de secretos.
 *
 * @param viewModel ViewModel para manejar la lógica de los secretos.
 * @param secrets Lista de secretos a mostrar.
 * @param onItemClick Función lambda que se ejecuta al hacer clic en un secreto.
 * @param folderId ID de la carpeta cuyos secretos se muestran.
 */
@Composable
fun SecretList (
    viewModel: SecretViewModel,
    secrets: List<Secret>,
    onItemClick : (Secret) -> Unit,
    folderId: String
){
    val folder by viewModel.getFolderById(folderId).observeAsState(null)

    LazyColumn {
        item{
            if (folder != null) {
                Text(
                    text = stringResource(R.string.folder) + " \"" + folder!!.name + "\"",
                    Modifier
                        .padding(12.dp)
                        .fillMaxWidth(),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center
                )
            }
            else{
                Text(
                    text = stringResource(R.string.secret_list),
                    Modifier
                        .padding(12.dp)
                        .fillMaxWidth(),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center
                )
            }

        }
        items(secrets) { secret ->
            SwipeableSecretItem(
                viewModel = viewModel,
                secret = secret,
                onItemClick = onItemClick
            )
        }
    }
}