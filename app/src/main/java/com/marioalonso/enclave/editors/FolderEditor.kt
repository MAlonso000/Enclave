package com.marioalonso.enclave.editors

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.marioalonso.enclave.R
import com.marioalonso.enclave.classes.Folder
import com.marioalonso.enclave.navigation.NavRoutes
import com.marioalonso.enclave.viewmodel.SecretViewModel

/**
 * Composable para crear o editar una carpeta.
 *
 * @param navController Controlador de navegación para manejar la navegación entre pantallas.
 * @param viewModel ViewModel que maneja la lógica de negocio y los datos.
 * @param folder Carpeta a editar. Si se está creando una nueva carpeta, se pasa una instancia vacía.
 */
@Composable
fun FolderEditor(
    navController: NavController,
    viewModel: SecretViewModel,
    folder: Folder
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        var name by remember { mutableStateOf(folder.name) }
        val onNameChanged = { value: String -> name = value }

        var titleError by remember { mutableStateOf("") }
        val requiredFieldText = stringResource(R.string.required_field)

        OutlinedTextField(
            value = name,
            onValueChange = onNameChanged,
            label = { Text(stringResource(R.string.folder_name)) },
            isError = titleError.isNotEmpty(),
            supportingText = {
                if (titleError.isNotEmpty()) {
                    Text(
                        text = requiredFieldText,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Start,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        )
        Row(
            modifier = Modifier.padding(10.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = { navController.popBackStack() },
                modifier = Modifier.padding(10.dp)
            ) {
                Text(text = stringResource(R.string.cancel))
            }
            Button(
                onClick = {
                    if(name.isNotBlank()) {
                        titleError = ""
                        folder.name = name
                        viewModel.insertFolder(folder)
                        navController.navigate(NavRoutes.Folders.route) {
                            popUpTo(NavRoutes.Home.route)
                        }
                    }
                    else{
                        titleError = requiredFieldText
                    }
                },
                modifier = Modifier.padding(10.dp)
            ) {
                Text(text = stringResource(R.string.accept))
            }
        }
    }
}