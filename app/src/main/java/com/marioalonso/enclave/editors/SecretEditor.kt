package com.marioalonso.enclave.editors

import android.icu.util.Calendar
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.marioalonso.enclave.R
import com.marioalonso.enclave.classes.CardSecret
import com.marioalonso.enclave.classes.CredentialSecret
import com.marioalonso.enclave.classes.NoteSecret
import com.marioalonso.enclave.utils.AESCipherGCM
import com.marioalonso.enclave.utils.PasswordUtils
import com.marioalonso.enclave.viewmodel.SecretViewModel
import java.util.regex.Pattern

/**
 * Composable para editar o crear un secreto (credencial, nota o tarjeta).
 *
 * @param navController Controlador de navegación para manejar la navegación entre pantallas.
 * @param viewModel ViewModel que maneja la lógica de negocio y los datos.
 * @param secretId ID del secreto a editar. Si se está creando un nuevo secreto, se pasa "add_credential", "add_note" o "add_credit_card".
 * @param folderId ID de la carpeta en la que se creará el nuevo secreto. Si es "all", no se asigna ninguna carpeta.
 */
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

/**
* Composable para editar o crear un secreto de tipo tarjeta de crédito.
 *
 * @param navController Controlador de navegación para manejar la navegación entre pantallas.
 * @param viewModel ViewModel que maneja la lógica de negocio y los datos.
 * @param secret El secreto de tipo CardSecret a editar o crear.
 * @param folderId ID de la carpeta en la que se creará el nuevo secreto. Si es "all", no se asigna ninguna carpeta.
 * @param creating Indica si se está creando un nuevo secreto (true) o editando uno existente (false).
 */
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
        }
        Unit
    }
    val onCanceled = {
        navController.popBackStack()
    }
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
    ) {
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text(stringResource(R.string.title)) },
            isError = titleError.isNotEmpty(),
            supportingText = {
                Text(
                    text = titleError,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Start,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            singleLine = true
        )
        FolderDropdown(
            viewModel = viewModel,
            selectedFolderId = selectedFolderId,
            onFolderSelected = { selectedFolderId = it }
        )
        OutlinedTextField(
            value = ownerName,
            onValueChange = { ownerName = it },
            label = { Text(stringResource(R.string.owner)) },
            singleLine = true
        )
        if(creating) {
            OutlinedTextField(
                value = encryptedCardNumber,
                onValueChange = { encryptedCardNumber = it },
                label = { Text(stringResource(R.string.card_number)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true
            )
            OutlinedTextField(
                value = encryptedPin,
                onValueChange = { encryptedPin = it },
                label = { Text(stringResource(R.string.pin)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true
            )
        }
        else {
            SecretTextField(
                secret = encryptedCardNumber,
                onValueChange = { encryptedCardNumber = it },
                label = stringResource(R.string.card_number),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            SecretTextField(
                secret = encryptedPin,
                onValueChange = { encryptedPin = it },
                label = stringResource(R.string.pin),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)

            )
        }
        OutlinedTextField(
            value = brand,
            onValueChange = { brand = it },
            label = { Text(stringResource(R.string.brand)) },
            singleLine = true
        )
        OutlinedTextField(
            value = expirationDate,
            onValueChange = { expirationDate = it },
            label = { Text(stringResource(R.string.expiration_date)) },
            readOnly = true,
            trailingIcon = {
                IconButton(onClick = { showDatePicker = true }) {
                    Icon(
                        painter = painterResource(id = R.drawable.calendar),
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
                        val month = calendar.get(Calendar.MONTH) + 1
                        val year = calendar.get(Calendar.YEAR) % 100
                        expirationDate = String.format("%02d/%02d", month, year)
                    }
                },
                onDismiss = { showDatePicker = false }
            )
        }

        if(creating)
            OutlinedTextField(
                value = encryptedCVV,
                onValueChange = { encryptedCVV = it },
                label = { Text(stringResource(R.string.cvv)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true
            )
        else
            SecretTextField(
                secret = encryptedCVV,
                onValueChange = { encryptedCVV = it },
                label = stringResource(R.string.cvv),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
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
 * Composable para editar o crear un secreto de tipo nota.
 *
 * @param navController Controlador de navegación para manejar la navegación entre pantallas.
 * @param viewModel ViewModel que maneja la lógica de negocio y los datos.
 * @param secret El secreto de tipo NoteSecret a editar o crear.
 * @param folderId ID de la carpeta en la que se creará el nuevo secreto. Si es "all", no se asigna ninguna carpeta.
 * @param creating Indica si se está creando un nuevo secreto (true) o editando uno existente (false).
 */
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
        }
        Unit
    }
    val onCanceled = {
        navController.popBackStack()
    }
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
    ) {
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text(stringResource(R.string.title)) },
            isError = titleError.isNotEmpty(),
            supportingText = {
                Text(
                    text = titleError,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Start,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            singleLine = true
        )
        FolderDropdown(
            viewModel = viewModel,
            selectedFolderId = selectedFolderId,
            onFolderSelected = { selectedFolderId = it }
        )
        if(creating)
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

/**
 * Composable para editar o crear un secreto de tipo credencial.
 *
 * @param navController Controlador de navegación para manejar la navegación entre pantallas.
 * @param viewModel ViewModel que maneja la lógica de negocio y los datos.
 * @param secret El secreto de tipo CredentialSecret a editar o crear.
 * @param folderId ID de la carpeta en la que se creará el nuevo secreto. Si es "all", no se asigna ninguna carpeta.
 * @param creating Indica si se está creando un nuevo secreto (true) o editando uno existente (false).
 */
@Composable
fun CredentialSecretEditor(navController: NavController, viewModel: SecretViewModel, secret: CredentialSecret, folderId: String? = null, creating: Boolean = false) {
    var title by remember { mutableStateOf(secret.title) }
    var username by remember { mutableStateOf(secret.username) }
    var password by remember { mutableStateOf(displayEncrypted(secret.encryptedPassword)) }
    var url by remember { mutableStateOf(secret.url) }
    var email by remember { mutableStateOf(secret.email) }
    var selectedFolderId by remember { mutableStateOf(secret.folderId) }

    var titleError by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf("") }

    val requiredFieldText = stringResource(R.string.required_field)
    val invalidEmailText = stringResource(R.string.invalid_email)

    val strength = PasswordUtils.calculatePasswordStrength(password)

    fun validateInputs(): Boolean {
        var isValid = true

        if (title.isBlank()) {
            titleError = requiredFieldText
            isValid = false
        } else {
            titleError = ""
        }

        if(!isEmailValid(email)) {
            emailError = invalidEmailText
            isValid = false
        } else {
            emailError = ""
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
        }
        Unit
    }
    val onCanceled = {
        navController.popBackStack()
    }
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
    ) {
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text(stringResource(R.string.title)) },
            isError = titleError.isNotEmpty(),
            supportingText = {
                Text(
                    text = titleError,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Start,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            singleLine = true
        )
        FolderDropdown(
            viewModel = viewModel,
            selectedFolderId = selectedFolderId,
            onFolderSelected = { selectedFolderId = it }
        )
        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text(stringResource(R.string.username)) },
            singleLine = true
        )

        val (strengthTextRes, strengthColor) = when (strength) {
            1 -> R.string.password_strength_very_weak to Color.Red
            2 -> R.string.password_strength_weak to Color(0xFFFFA500) // Naranja
            3 -> R.string.password_strength_moderate to Color.Yellow
            4 -> R.string.password_strength_strong to Color.Green
            else -> R.string.password_strength_very_strong to Color.Blue
        }

        if (creating)
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text(stringResource(R.string.password)) },
                singleLine = true,
                supportingText = {
                    if (password.isNotBlank()) {
                        Text(
                            text = stringResource(id = strengthTextRes),
                            color = strengthColor
                        )
                    }
                }
            )
        else
            SecretTextField(
                secret = password,
                onValueChange = { password = it },
                label = stringResource(R.string.password),
                supportingText = {
                    if (password.isNotBlank()) {
                        Text(
                            text = stringResource(id = strengthTextRes),
                            color = strengthColor
                        )
                    }
                }
            )
        OutlinedTextField(
            value = url,
            onValueChange = { url = it },
            label = { Text(stringResource(R.string.url)) },
            singleLine = true
        )
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text(stringResource(R.string.email)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            isError = emailError.isNotEmpty(),
            supportingText = {
                Text(
                    text = emailError,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Start,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            singleLine = true
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
 * Composable para el dropdown de selección de carpetas.
 *
 * @param viewModel ViewModel que maneja la lógica de negocio y los datos.
 * @param selectedFolderId ID de la carpeta actualmente seleccionada. Puede ser null para "Sin carpeta".
 * @param onFolderSelected Callback que se llama cuando se selecciona una carpeta, pasando el ID de la carpeta seleccionada o null para "Sin carpeta".
 */
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

/**
 * Valida si una cadena de texto tiene un formato de email válido.
 *
 * @param email El email a validar.
 * @return `true` si el email es válido, `false` en caso contrario.
 */
fun isEmailValid(email: String): Boolean {
    if (email.isBlank()) return true
    val emailRegex = Pattern.compile(
        "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                "\\@" +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                "(" +
                "\\." +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                ")+"
    )
    return emailRegex.matcher(email).matches()
}

/**
 * Función para intentar desencriptar un texto. Si no se puede, se devuelve el texto original.
 *
 * @param input Texto a desencriptar.
 * @return Texto desencriptado o el original si no se pudo desencriptar.
 */
fun displayEncrypted(input: String): String {
    return try {
        if (input.isBlank()) return ""
        AESCipherGCM.decrypt(input)
    } catch (e: Exception) {
        input
    }
}

/**
 * Composable para un campo de texto que maneja secretos, con opción de mostrar/ocultar y copiar al portapapeles.
 *
 * @param secret El texto del secreto a mostrar/editar.
 * @param onValueChange Callback que se llama cuando el valor del texto cambia.
 * @param label Etiqueta para el campo de texto.
 * @param isSingleLine Indica si el campo de texto es de una sola línea o multilinea. Por defecto es true (una sola línea).
 */
@Composable
fun SecretTextField(
    secret: String,
    onValueChange: (String) -> Unit,
    label: String,
    isSingleLine : Boolean = true,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    supportingText: @Composable (() -> Unit)? = null
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
            },
            keyboardOptions = keyboardOptions,
            supportingText = supportingText,
        )
    }
}

/**
 * Composable para un selector de fecha.
 *
 * @param onDateSelected Callback que se llama cuando se selecciona una fecha.
 * @param onDismiss Callback que se llama cuando se cierra el selector de fecha sin seleccionar una fecha.
 */
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