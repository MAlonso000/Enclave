package com.marioalonso.enclave.screens

import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.marioalonso.enclave.R
import com.marioalonso.enclave.navigation.NavRoutes
import com.marioalonso.enclave.utils.AESCipherGCM
import com.marioalonso.enclave.viewmodel.SecretViewModel
import com.marioalonso.enclave.utils.PasswordUtils

@Composable
fun Home(navController: NavController, viewModel: SecretViewModel,context: Context) {
    var password by remember { mutableStateOf("") }
    val isPasswordSet = AESCipherGCM.getVerificationText(context) != null
    var showDialog by remember { mutableStateOf(false) }
    var dialogMessageTitle by remember { mutableIntStateOf(R.string.wrong_password) }
    var dialogMessageDetail by remember { mutableIntStateOf(R.string.wrong_password_extended) }

    if (showDialog) {
        IncorrectPasswordDialog(
            dialogMessageTitle = dialogMessageTitle,
            dialogMessageDetail = dialogMessageDetail,
            showDialog = showDialog,
            onDismiss = { showDialog = false }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = if (isPasswordSet) stringResource(R.string.introduce_existing_password) else stringResource(R.string.choose_new_password),
            style = MaterialTheme.typography.bodyLarge
        )
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text(stringResource(R.string.password)) },
            modifier = Modifier.padding(vertical = 8.dp),
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
        )
        Button(
            onClick = {
                if(password.isEmpty()) {
                    Log.e("Home", "Contraseña vacía")
                    dialogMessageTitle = R.string.wrong_password
                    dialogMessageDetail = R.string.empty_password_error_extended
                    showDialog = true
                }
                else if (isPasswordSet) {
                    if (AESCipherGCM.verifyPassword(context, password)) {
                        navController.navigate(NavRoutes.Secrets.route)
                    } else {
                        Log.e("Home", "Contraseña incorrecta" + password)
                        dialogMessageTitle = R.string.wrong_password
                        dialogMessageDetail = R.string.wrong_password_extended
                        showDialog = true
                    }
                } else {
                    // Inicializar una nueva contraseña
                    val (result, error, errorDetail) = PasswordUtils.isPasswordSecure(password)
                    if(!result) {
                        dialogMessageTitle = error
                        dialogMessageDetail = errorDetail
                        showDialog = true
                    }
                    else{
                        AESCipherGCM.initializeKey(context, password)
                        AESCipherGCM.initializeVerificationText(context)
                        navController.navigate(NavRoutes.Secrets.route)
                    }
                }
            },
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text(text = if (isPasswordSet) stringResource(R.string.access) else stringResource(R.string.start))
        }
        //Boton para probar cambiar la contraseña
        if(isPasswordSet)
            TextButton(
                onClick = {
                    navController.navigate(NavRoutes.ChangePassword.route)
                },
                modifier = Modifier
            ) {
                Text(text = stringResource(R.string.change_password))
            }
    }
}

@Composable
fun IncorrectPasswordDialog(
    dialogMessageTitle: Int,
    dialogMessageDetail: Int,
    showDialog: Boolean,
    onDismiss: () -> Unit
) {
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { onDismiss() },
            title = { Text(stringResource(dialogMessageTitle)) },
            text = {
                Text(
                    stringResource(dialogMessageDetail)
                )
            },
            confirmButton = {
                Button(onClick = { onDismiss() }) {
                    Text(stringResource(R.string.accept))
                }
            }
        )
    }
}

///**
// * Cambia la contraseña maestra del sistema y reencripta todos los secretos.
// *
// * @param context Contexto de la aplicación
// * @param currentPassword Contraseña actual
// * @param newPassword Nueva contraseña a establecer
// * @param decryptAllSecrets Función que desencripta todos los secretos y los guarda temporalmente
// * @param encryptAllSecrets Función que reencripta todos los secretos con la nueva clave
// * @return true si el cambio fue exitoso, false en caso contrario
// */
//fun changePassword(
//    context: Context,
//    currentPassword: String,
//    newPassword: String,
//    viewModel: SecretViewModel
//): Boolean {
//    // 1. Verificar que la contraseña actual sea correcta
//    if (!AESCipherGCM.verifyPassword(context, currentPassword)) {
//        return false
//    }
//
//    try {
//        // 2. Desencriptar todos los secretos con la clave actual
//        viewModel.decryptAllSecrets()
//
//        // 3. Hacer logout para limpiar el estado actual
//        AESCipherGCM.logout(context)
//
//        // 4. Inicializar con la nueva contraseña
//        AESCipherGCM.initializeKey(context, newPassword)
//        AESCipherGCM.initializeVerificationText(context)
//
//        // 5. Reencriptar todos los secretos con la nueva clave
//        viewModel.encryptAllSecrets()
//
//        // 6. Hacer logout nuevamente
//        AESCipherGCM.logout(context)
//
//        return true
//    } catch (e: Exception) {
//        return false
//    }
//}