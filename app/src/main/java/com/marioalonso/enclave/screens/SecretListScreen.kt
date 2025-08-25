package com.marioalonso.enclave.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.marioalonso.enclave.R
import com.marioalonso.enclave.classes.Secret
import com.marioalonso.enclave.lists.SecretList
import com.marioalonso.enclave.navigation.NavRoutes
import com.marioalonso.enclave.viewmodel.SecretViewModel

/**
 * Pantalla que muestra la lista de secretos.
 * Si no hay secretos, muestra un mensaje indicando que no hay secretos disponibles.
 *
 * @param navController Controlador de navegación para manejar la navegación entre pantallas.
 * @param viewModel ViewModel que maneja la lógica de negocio y los datos de los secretos.
 * @param folderId ID de la carpeta cuyos secretos se van a mostrar.
 * Si es "all", se muestran todos los secretos.
 */
@Composable
fun SecretListScreen(
    navController: NavController,
    viewModel: SecretViewModel,
    folderId: String = "all"
) {
    var secrets by remember { mutableStateOf(emptyList<Secret>()) }
    if (folderId == "all") {
        secrets = viewModel.secrets.observeAsState(emptyList()).value
    } else {
        secrets = viewModel.getSecretsByFolderId(folderId).observeAsState(emptyList()).value
    }
    val onSecretClick = { secret: Secret ->
        navController.navigate(
            NavRoutes.SecretEditor.route + "/${secret.id}" + "/${secret.folderId ?: "all"}"
        )
    }
    if (secrets.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize().padding(40.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(stringResource(R.string.no_secrets))
        }
    }
    else{
        SecretList(viewModel = viewModel, secrets = secrets, onItemClick = onSecretClick, folderId = folderId)
    }
}