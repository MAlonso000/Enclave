package com.marioalonso.enclave.editors

import android.icu.util.Calendar
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
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
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.marioalonso.enclave.R
import com.marioalonso.enclave.classes.CardSecret
import com.marioalonso.enclave.classes.CredentialSecret
import com.marioalonso.enclave.classes.NoteSecret
import com.marioalonso.enclave.navigation.NavRoutes
import com.marioalonso.enclave.utils.AESCipherGCM
import com.marioalonso.enclave.viewmodel.SecretViewModel
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.style.TextAlign

@Composable
fun SecretEditor(
    navController: NavController,
    viewModel: SecretViewModel,
    secretId: String,
    folderId: String
) {
    when {
        secretId.equals("add_credential") -> {
            CredentialSecretEditor(
                navController = navController,
                viewModel = viewModel,
                CredentialSecret(
                    title = "",
                    username = "",
                    encryptedPassword = "",
                    url = "",
                    email = "",
                    folderId = if (folderId.equals("all")) null else folderId
                ),
                creating = true
            )
            return
        }

        secretId.equals("add_note") -> {
            NoteSecretEditor(
                navController = navController,
                viewModel = viewModel,
                NoteSecret(
                    title = "",
                    folderId = if (folderId.equals("all")) null else folderId,
                    encryptedNote = ""
                ),
                creating = true
            )
            return
        }

        secretId.equals("add_credit_card") -> {
            CardSecretEditor(
                navController = navController,
                viewModel = viewModel,
                CardSecret(
                    title = "",
                    ownerName = "",
                    encryptedCardNumber = "",
                    encryptedPin = "",
                    brand = "",
                    expirationDate = "",
                    encryptedCVV = "",
                    folderId = if (folderId.equals("all")) null else folderId
                ),
                creating = true
            )
            return
        }
    }

    val secrets = viewModel.secrets.observeAsState(emptyList()).value
    val secret = secrets.find { it.id == secretId }
    when (secret) {
        is CredentialSecret -> CredentialSecretEditor(navController, viewModel, secret, folderId)
        is NoteSecret -> NoteSecretEditor(navController, viewModel, secret, folderId)
        is CardSecret -> CardSecretEditor(navController, viewModel, secret, folderId)
        else -> throw IllegalArgumentException("Unsupported secret type: ${secret!!::class.simpleName}")
    }
}

@Composable
fun CardSecretEditor(navController: NavController, viewModel: SecretViewModel, secret: CardSecret, folderId: String? = null, creating: Boolean = false) {
    var title by remember { mutableStateOf(secret.title) }
    var ownerName by remember { mutableStateOf(secret.ownerName) }
    var encryptedCardNumber by remember { mutableStateOf(displayEncrypted(secret.encryptedCardNumber)) }
    var encryptedPin by remember { mutableStateOf(displayEncrypted(secret.encryptedPin)) }
    var brand by remember { mutableStateOf(secret.brand) }
    var expirationDate by remember { mutableStateOf(secret.expirationDate) }
    var encryptedCVV by remember { mutableStateOf(displayEncrypted(secret.encryptedCVV)) }
    var selectedFolderId by remember { mutableStateOf(secret.folderId) }
    var showDatePicker by remember { mutableStateOf(false) }

    var titleError by remember { mutableStateOf("") }

    val requiredFieldText = stringResource(R.string.required_field)

    fun validateInputs(): Boolean {
        var isValid = true

        if (title.isBlank()) {
            titleError = requiredFieldText
            isValid = false
        } else {
            titleError = ""
        }
        return isValid
    }

    val onAccepted = {
        if (validateInputs()) {
            secret.title = title
            secret.ownerName = ownerName
            secret.encryptedCardNumber = AESCipherGCM.encrypt(encryptedCardNumber)
            secret.encryptedPin = AESCipherGCM.encrypt(encryptedPin)
            secret.brand = brand
            secret.expirationDate = expirationDate
            secret.encryptedCVV = AESCipherGCM.encrypt(encryptedCVV)
            secret.folderId = selectedFolderId
            viewModel.insertSecret(secret)
            navController.popBackStack()
//        navController.navigate(NavRoutes.Secrets.route) {
//            popUpTo(NavRoutes.Home.route)
//        }
        }
        Unit
    }
    val onCanceled = {
        navController.popBackStack()
    }
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
//        modifier = Modifier.fillMaxWidth().fillMaxHeight()
        modifier = Modifier
    ) {
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text(stringResource(R.string.title)) },
            isError = titleError.isNotEmpty(),
            supportingText = {
                if (titleError.isNotEmpty()) {
                    Text(
                        text = titleError,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Start,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        )
        FolderDropdown(
            viewModel = viewModel,
            selectedFolderId = selectedFolderId,
            onFolderSelected = { selectedFolderId = it }
        )
        OutlinedTextField(
            value = ownerName,
            onValueChange = { ownerName = it },
            label = { Text(stringResource(R.string.owner)) }
        )
        if(creating) {
            OutlinedTextField(
                value = encryptedCardNumber,
                onValueChange = { encryptedCardNumber = it },
                label = { Text(stringResource(R.string.card_number)) }
            )
            OutlinedTextField(
                value = encryptedPin,
                onValueChange = { encryptedPin = it },
                label = { Text(stringResource(R.string.pin)) }
            )
        }
        else {
            SecretTextField(
                secret = encryptedCardNumber,
                onValueChange = { encryptedCardNumber = it },
                label = stringResource(R.string.card_number)
            )
            SecretTextField(
                secret = encryptedPin,
                onValueChange = { encryptedPin = it },
                label = stringResource(R.string.pin)
            )
        }
        OutlinedTextField(
            value = brand,
            onValueChange = { brand = it },
            label = { Text(stringResource(R.string.brand)) }
        )
        OutlinedTextField(
            value = expirationDate,
            onValueChange = { expirationDate = it },
            label = { Text(stringResource(R.string.expiration_date)) },
            readOnly = true,  // Hacer el campo de solo lectura
            trailingIcon = {
                IconButton(onClick = { showDatePicker = true }) {
                    Icon(
                        painter = painterResource(id = R.drawable.calendar), // Asegúrate de tener este icono
                        contentDescription = "Seleccionar fecha"
                    )
                }
            },
            modifier = Modifier.clickable { showDatePicker = true }
        )
        if (showDatePicker) {
            DatePickerModalInput(
                onDateSelected = { dateMillis ->
                    dateMillis?.let {
                        val calendar = Calendar.getInstance().apply {
                            timeInMillis = it
                        }
                        // Formato para tarjetas: MM/YY
                        val month = calendar.get(Calendar.MONTH) + 1 // Calendar.MONTH es 0-based
                        val year = calendar.get(Calendar.YEAR) % 100 // Solo queremos los últimos 2 dígitos
                        expirationDate = String.format("%02d/%02d", month, year)
                    }
                },
                onDismiss = { showDatePicker = false }
            )
        }

//        MonthYearTextField(
//            label = { Text(stringResource(R.string.expiration_date)) },
//            value = expirationDate,
//            onValueChange = { expirationDate = it }
//        )

        if(creating)
            OutlinedTextField(
                value = encryptedCVV,
                onValueChange = { encryptedCVV = it },
                label = { Text(stringResource(R.string.cvv)) }
            )
        else
            SecretTextField(
                secret = encryptedCVV,
                onValueChange = { encryptedCVV = it },
                label = stringResource(R.string.cvv)
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

@Composable
fun NoteSecretEditor(navController: NavController, viewModel: SecretViewModel, secret: NoteSecret, folderId: String? = null, creating: Boolean = false) {
    var title by remember { mutableStateOf(secret.title) }
    var note by remember { mutableStateOf(displayEncrypted(secret.encryptedNote)) }
    var selectedFolderId by remember { mutableStateOf(secret.folderId) }

    var titleError by remember { mutableStateOf("") }

    val requiredFieldText = stringResource(R.string.required_field)

    fun validateInputs(): Boolean {
        var isValid = true

        if (title.isBlank()) {
            titleError = requiredFieldText
            isValid = false
        } else {
            titleError = ""
        }
        return isValid
    }

    val onAccepted = {
        if (validateInputs()) {
            secret.title = title
            secret.encryptedNote = AESCipherGCM.encrypt(note)
            secret.folderId = selectedFolderId
            viewModel.insertSecret(secret)
            navController.popBackStack()
//        navController.navigate(NavRoutes.Secrets.route) {
//            popUpTo(NavRoutes.Home.route)
//        }
        }
        Unit
    }
    val onCanceled = {
        navController.popBackStack()
    }
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
//        modifier = Modifier.fillMaxWidth().fillMaxHeight()
        modifier = Modifier
    ) {
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text(stringResource(R.string.title)) },
            isError = titleError.isNotEmpty(),
            supportingText = {
                if (titleError.isNotEmpty()) {
                    Text(
                        text = titleError,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Start,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        )
        FolderDropdown(
            viewModel = viewModel,
            selectedFolderId = selectedFolderId,
            onFolderSelected = { selectedFolderId = it }
        )
        if(creating == true)
            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                label = { Text(stringResource(R.string.note)) },
            )
        else
            SecretTextField(
                secret = note,
                onValueChange = { note = it },
                label = stringResource(R.string.note),
                isSingleLine = false
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

@Composable
fun CredentialSecretEditor(navController: NavController, viewModel: SecretViewModel, secret: CredentialSecret, folderId: String? = null, creating: Boolean = false) {
    var title by remember { mutableStateOf(secret.title) }
    var username by remember { mutableStateOf(secret.username) }
    var password by remember { mutableStateOf(displayEncrypted(secret.encryptedPassword)) }
    var url by remember { mutableStateOf(secret.url) }
    var email by remember { mutableStateOf(secret.email) }
    var selectedFolderId by remember { mutableStateOf(secret.folderId) }

    var titleError by remember { mutableStateOf("") }

    val requiredFieldText = stringResource(R.string.required_field)

    fun validateInputs(): Boolean {
        var isValid = true

        if (title.isBlank()) {
            titleError = requiredFieldText
            isValid = false
        } else {
            titleError = ""
        }
        return isValid
    }

    val onAccepted = {
        if (validateInputs()) {
            secret.title = title
            secret.username = username
            secret.encryptedPassword = AESCipherGCM.encrypt(password)
            secret.url = url
            secret.email = email
            secret.folderId = selectedFolderId
            viewModel.insertSecret(secret)
            navController.popBackStack()
//        navController.navigate(NavRoutes.Secrets.route) {
//            popUpTo(NavRoutes.Home.route)
//        }
        }
        Unit
    }
    val onCanceled = {
        navController.popBackStack()
    }
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
//        modifier = Modifier.fillMaxWidth().fillMaxHeight()
        modifier = Modifier
    ) {
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text(stringResource(R.string.title)) },
            isError = titleError.isNotEmpty(),
            supportingText = {
                if (titleError.isNotEmpty()) {
                    Text(
                        text = titleError,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Start,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        )
        FolderDropdown(
            viewModel = viewModel,
            selectedFolderId = selectedFolderId,
            onFolderSelected = { selectedFolderId = it }
        )
        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text(stringResource(R.string.username)) }
        )
        if(creating == true)
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text(stringResource(R.string.password)) }
            )
        else
            SecretTextField(
                secret = password,
                onValueChange = { password = it },
                label = stringResource(R.string.password)
            )
        OutlinedTextField(
            value = url,
            onValueChange = { url = it },
            label = { Text(stringResource(R.string.url)) }
        )
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text(stringResource(R.string.email)) }
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FolderDropdown(
    viewModel: SecretViewModel,
    selectedFolderId: String?,
    onFolderSelected: (String?) -> Unit
) {
    val folders = viewModel.folders.observeAsState(emptyList()).value

    // Añadir opción "Sin carpeta"
    val folderOptions = listOf(Pair("Sin carpeta", null)) +
            folders.map { Pair(it.name, it.id) }

    // Obtener el nombre de la carpeta seleccionada
    val selectedFolderName = if (selectedFolderId == null) {
        "Sin carpeta"
    } else {
        folders.find { it.id == selectedFolderId }?.name ?: "Sin carpeta"
    }

    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = Modifier
    ) {
        OutlinedTextField(
            value = selectedFolderName,
            onValueChange = {},
            readOnly = true,
            label = { Text("Carpeta") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
            modifier = Modifier
                .menuAnchor()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            folderOptions.forEach { (name, id) ->
                DropdownMenuItem(
                    text = { Text(name) },
                    onClick = {
                        onFolderSelected(id)
                        expanded = false
                    }
                )
            }
        }
    }
}

fun displayEncrypted(input: String): String {
    return try {
        if (input.isBlank()) return ""
        AESCipherGCM.decrypt(input)
    } catch (e: Exception) {
        input
    }
}

@Composable
fun SecretTextField(
    secret: String,
    onValueChange: (String) -> Unit,
    label: String,
    isSingleLine : Boolean = true
) {
    var isVisible by remember { mutableStateOf(false) }
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current

    Row(
        modifier = Modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = secret,
            onValueChange = onValueChange,
            label = { Text(label) },
            singleLine = isSingleLine,
            visualTransformation = if (isVisible) {
                VisualTransformation.None
            } else {
                PasswordVisualTransformation()
            },
            modifier = Modifier,
            trailingIcon = {
                Row {
                    IconButton(onClick = { isVisible = !isVisible }) {
                        Icon(
                            painter = painterResource(if (isVisible) R.drawable.eye_on else R.drawable.eye_off),
                            contentDescription = if (isVisible) "Hide" else "Show"
                        )
                    }

                    IconButton(
                        onClick = {
                            clipboardManager.setText(AnnotatedString(secret))
                            Toast
                                .makeText(context, R.string.copied_to_clipboard, Toast.LENGTH_SHORT)
                                .show()
                        }
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.content_copy),
                            contentDescription = "Copiar",
                            tint = LocalContentColor.current
                        )
                    }
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerModalInput(
    onDateSelected: (Long?) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState(initialDisplayMode = DisplayMode.Input)

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                onDateSelected(datePickerState.selectedDateMillis)
                onDismiss()
            }) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}