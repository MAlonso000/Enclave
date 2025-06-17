package com.marioalonso.enclave.screens

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.marioalonso.enclave.classes.Secret
import com.marioalonso.enclave.lists.SecretList
import com.marioalonso.enclave.viewmodel.SecretViewModel
import android.util.Log

@Composable
fun SecretListScreen(
    navController: NavController,
    viewModel: SecretViewModel,
) {
    val context = LocalContext.current
    val secrets by viewModel.secrets.observeAsState(emptyList())
    val onSecretClick = { secret: Secret ->
        Toast.makeText(
            context,
            "Clicked on folder: ${secret.title}",
            Toast.LENGTH_SHORT
        ).show()
    }
    //Log imprimiendo el número de secretos
    Log.d("SecretListScreen", "Number of secrets: ${secrets.size}")
    SecretList(viewModel = viewModel, secrets = secrets, onItemClick = onSecretClick)

}