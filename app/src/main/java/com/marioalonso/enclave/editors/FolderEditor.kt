package com.marioalonso.enclave.editors

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
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
import com.marioalonso.enclave.classes.Folder
import com.marioalonso.enclave.navigation.NavRoutes
import com.marioalonso.enclave.viewmodel.SecretViewModel

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

        OutlinedTextField(
            value = name,
            onValueChange = onNameChanged,
            label = { Text(stringResource(R.string.folder_name)) }
        )

        Button(
            onClick = {
                folder.name = name
                viewModel.insertFolder(folder)
                navController.navigate(NavRoutes.Folders.route) {
                    popUpTo(NavRoutes.Home.route)
                }
            },
            modifier = Modifier.padding(10.dp)
        ) {
            Text(text = stringResource(R.string.accept))
        }
    }
}