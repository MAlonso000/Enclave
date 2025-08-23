package com.marioalonso.enclave.screens

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.marioalonso.enclave.classes.Secret
import com.marioalonso.enclave.lists.SecretList
import com.marioalonso.enclave.viewmodel.SecretViewModel
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import com.marioalonso.enclave.navigation.NavRoutes
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.marioalonso.enclave.R

@Composable
fun SecretListScreen(
    navController: NavController,
    viewModel: SecretViewModel,
    folderId: String = "all"
) {
    val context = LocalContext.current
    var secrets by remember { mutableStateOf(emptyList<Secret>()) }
    if (folderId == "all") {
        secrets = viewModel.secrets.observeAsState(emptyList()).value
    } else {
        secrets = viewModel.getSecretsByFolderId(folderId).observeAsState(emptyList()).value
    }
    val onSecretClick = { secret: Secret ->
//        Toast.makeText(
//            context,
//            "Clicked on folder: ${secret.title}",
//            Toast.LENGTH_SHORT
//        ).show()
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