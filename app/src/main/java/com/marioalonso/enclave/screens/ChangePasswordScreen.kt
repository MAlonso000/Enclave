package com.marioalonso.enclave.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.marioalonso.enclave.R
import com.marioalonso.enclave.utils.AESCipherGCM
import com.marioalonso.enclave.utils.PasswordUtils
import com.marioalonso.enclave.viewmodel.SecretViewModel

/**
 * Pantalla para cambiar la contraseña maestra del sistema.
 *
 * @param navController Controlador de navegación para manejar la navegación entre pantallas
 * @param viewModel ViewModel que maneja los secretos y la lógica de negocio relacionada
 */
@Composable
fun ChangePasswordScreen(
    navController: androidx.navigation.NavController,
    viewModel: SecretViewModel,
) {

    var oldPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmNewPassword by remember { mutableStateOf("") }

    val context = LocalContext.current

    var showDialog by remember { mutableStateOf(false) }
    var validation by remember { mutableStateOf(Triple(true, 0, 0)) }

    fun validateInputs(): Triple<Boolean, Int, Int> {
        if(oldPassword.isEmpty() || newPassword.isEmpty() || confirmNewPassword.isEmpty()) {
            return Triple(false, R.string.all_fields_required, R.string.all_fields_required_extended)
        }
        else if(newPassword != confirmNewPassword) {
            return Triple(false, R.string.passwords_do_not_match, R.string.passwords_do_not_match_extended)
        }
        PasswordUtils.isPasswordSecure(newPassword).let{ it: Triple<Boolean, Int, Int> ->
            if(!it.first) return it
        }
        if(!AESCipherGCM.verifyPassword(context, oldPassword)) {
            return Triple(false, R.string.wrong_password, R.string.wrong_password_extended)
        }
        return Triple(true, 0, 0)
    }

    val onCanceled = {
        navController.popBackStack()
    }

    val onAccepted = {
        validation = validateInputs()
        if(validation.first) {
            changePassword(context, oldPassword, newPassword, viewModel)
            Toast.makeText(context, R.string.password_changed, Toast.LENGTH_SHORT).show()
            navController.popBackStack()
        } else {
            showDialog = true
        }
        Unit
    }

    if (showDialog) {
        ChangePasswordDialog(
            dialogMessageTitle = validation.second,
            dialogMessageDetail = validation.third,
            showDialog = showDialog,
            onDismiss = { showDialog = false }
        )
    }

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
//        modifier = Modifier.fillMaxWidth().fillMaxHeight()
        modifier = Modifier
    ) {
        Text(
            text = stringResource(R.string.choose_new_password),
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth()
        )
        OutlinedTextField(
            value = oldPassword,
            onValueChange = { oldPassword = it },
            label = { Text(stringResource(R.string.password)) },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
        )
        OutlinedTextField(
            value = newPassword,
            onValueChange = { newPassword = it },
            label = { Text(stringResource(R.string.new_password)) },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
        )
        OutlinedTextField(
            value = confirmNewPassword,
            onValueChange = { confirmNewPassword = it },
            label = { Text(stringResource(R.string.confirm_password)) },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
        )
        Row(
            modifier = Modifier.padding(10.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = { onCanceled() },
                modifier = Modifier.padding(10.dp)
            ) {
                Text(text = stringResource(R.string.cancel))
            }
            Button(
                onClick = onAccepted,
                modifier = Modifier.padding(10.dp)
            ) {
                Text(text = stringResource(R.string.accept))
            }
        }
    }
}

/**
 * Cambia la contraseña maestra del sistema y reencripta todos los secretos.
 *
 * @param context Contexto de la aplicación
 * @param currentPassword Contraseña actual
 * @param newPassword Nueva contraseña a establecer
 * @param decryptAllSecrets Función que desencripta todos los secretos y los guarda temporalmente
 * @param encryptAllSecrets Función que reencripta todos los secretos con la nueva clave
 * @return true si el cambio fue exitoso, false en caso contrario
 */
fun changePassword(
    context: Context,
    currentPassword: String,
    newPassword: String,
    viewModel: SecretViewModel
): Boolean {
    try {
        viewModel.decryptAllSecrets()

        AESCipherGCM.logout(context)

        AESCipherGCM.initializeKey(context, newPassword)
        AESCipherGCM.initializeVerificationText(context)

        viewModel.encryptAllSecrets()

        AESCipherGCM.logout(context)

        return true
    } catch (e: Exception) {
        return false
    }
}

/**
 * Diálogo de alerta para mostrar mensajes relacionados con el cambio de contraseña.
 *
 * @param dialogMessageTitle Título del mensaje del diálogo (recurso de cadena)
 * @param dialogMessageDetail Detalle del mensaje del diálogo (recurso de cadena)
 * @param showDialog Booleano que indica si el diálogo debe mostrarse
 * @param onDismiss Función que se llama cuando el diálogo es descartado
 */
@Composable
fun ChangePasswordDialog(
    dialogMessageTitle: Int,
    dialogMessageDetail: Int,
    showDialog: Boolean,
    onDismiss: () -> Unit
){
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { onDismiss() },
            title = { Text(stringResource(dialogMessageTitle)) },
            text = {
                Text(
                    stringResource(dialogMessageDetail),
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