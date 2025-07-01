package com.marioalonso.enclave.screens

import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.marioalonso.enclave.navigation.NavRoutes
import com.marioalonso.enclave.utils.AESCipherGCM

//@Composable
//fun Home(navController: NavController) {
//    Box(
//        Modifier.fillMaxSize(),
//        contentAlignment = Alignment.Center
//    ) {
//        Button( onClick = {
//            navController.navigate(NavRoutes.Secrets.route)
//        }) {
//            Text("ENCLAVE")
//        }
//    }
//}

@Composable
fun Home(navController: NavController, context: Context) {
    var password by remember { mutableStateOf("") }
    val isPasswordSet = AESCipherGCM.getVerificationText(context) != null
    var showDialog by remember { mutableStateOf(false) }

    if (showDialog) {
        IncorrectPasswordDialog(
            showDialog = showDialog,
            onDismiss = { showDialog = false }
        )
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = if (isPasswordSet) "Introduce la contraseña existente" else "Elige una contraseña nueva",
            style = MaterialTheme.typography.bodyLarge
        )
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
        )
        Button(
            onClick = {
                if (isPasswordSet) {
                    if (AESCipherGCM.verifyPassword(context, password)) {
                        navController.navigate(NavRoutes.Secrets.route)
                    } else {
                        Log.e("Home", "Contraseña incorrecta" + password)
                        showDialog = true
                    }
                } else {
                    // Inicializar una nueva contraseña
                    AESCipherGCM.initializeKey(context, password)
                    AESCipherGCM.initializeVerificationText(context)
                    navController.navigate(NavRoutes.Secrets.route)
                }
            },
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text(text = if (isPasswordSet) "Verificar" else "Inicializar")
        }
    }
}

@Composable
fun IncorrectPasswordDialog(showDialog: Boolean, onDismiss: () -> Unit) {
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { onDismiss() },
            title = { Text("Contraseña incorrecta") },
            text = {
                Text(
                    "La contraseña que ingresaste no es correcta. Por favor, inténtalo nuevamente.",
                    textAlign = TextAlign.Center
                )
            },
            confirmButton = {
                Button(onClick = { onDismiss() }) {
                    Text("Aceptar")
                }
            }
        )
    }
}