package com.marioalonso.enclave.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.marioalonso.enclave.R
import com.marioalonso.enclave.utils.PasswordGenerator

@Composable
fun PasswordGeneratorScreen(navController: NavController) {
    var password by remember { mutableStateOf("") }
    var passwordLength by remember { mutableStateOf(16) }
    var useUppercase by remember { mutableStateOf(true) }
    var useLowercase by remember { mutableStateOf(true) }
    var useDigits by remember { mutableStateOf(true) }
    var useSymbols by remember { mutableStateOf(true) }
    var avoidAmbiguous by remember { mutableStateOf(false) }

    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current

    // Generar contraseña inicial
    LaunchedEffect(key1 = true) {
        generatePassword(
            passwordLength, useUppercase, useLowercase,
            useDigits, useSymbols, avoidAmbiguous
        )?.let { password = it }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.password_generator_title),
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Mostrar contraseña
        OutlinedCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = password,
                    fontSize = 16.sp,
                    modifier = Modifier.weight(1f)
                )

                IconButton(
                    onClick = {
                        clipboardManager.setText(AnnotatedString(password))
                        Toast.makeText(context, R.string.copied_to_clipboard, Toast.LENGTH_SHORT).show()
                    }
                ) {
                    Icon(
                        painter = painterResource(R.drawable.content_copy),
                        contentDescription = "Copiar"
                    )
                }
            }
        }

        // Controles
        Text(
            text = "${stringResource(R.string.length)}: $passwordLength",
            modifier = Modifier.padding(top = 10.dp)
        )
        Slider(
            value = passwordLength.toFloat(),
            onValueChange = { passwordLength = it.toInt() },
            valueRange = 6f..32f,
            steps = 27,
            modifier = Modifier.padding(horizontal = 10.dp)
        )

        PasswordGeneratorOptions(
            useLower = useLowercase,
            onLowerChange = { useLowercase = it },
            useUpper = useUppercase,
            onUpperChange = { useUppercase = it },
            useDigits = useDigits,
            onDigitsChange = { useDigits = it },
            useSymbols = useSymbols,
            onSymbolsChange = { useSymbols = it },
            avoidAmbiguous = avoidAmbiguous,
            onAvoidAmbiguousChange = { avoidAmbiguous = it }
        )

//        Spacer(modifier = Modifier.weight(1f))

        TextButton(
            onClick = { navController.popBackStack() }
        ) {
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = stringResource(R.string.back))
        }

        Button(
            onClick = {
                generatePassword(
                    passwordLength, useUppercase, useLowercase,
                    useDigits, useSymbols, avoidAmbiguous
                )?.let { password = it }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Text(stringResource(R.string.generate), modifier = Modifier.padding(8.dp))
        }
    }
}

private fun generatePassword(
    length: Int,
    useUpper: Boolean,
    useLower: Boolean,
    useDigits: Boolean,
    useSymbols: Boolean,
    avoidAmbiguous: Boolean
): String? {
    return try {
        PasswordGenerator.generate(
            length = length,
            useUpper = useUpper,
            useLower = useLower,
            useDigits = useDigits,
            useSymbols = useSymbols,
            avoidAmbiguous = avoidAmbiguous
        )
    } catch (e: IllegalArgumentException) {
        null
    }
}

@Composable
fun PasswordGeneratorOptions(
    useLower: Boolean,
    onLowerChange: (Boolean) -> Unit,
    useUpper: Boolean,
    onUpperChange: (Boolean) -> Unit,
    useDigits: Boolean,
    onDigitsChange: (Boolean) -> Unit,
    useSymbols: Boolean,
    onSymbolsChange: (Boolean) -> Unit,
    avoidAmbiguous: Boolean,
    onAvoidAmbiguousChange: (Boolean) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // Opciones de caracteres
        CharacterOptionSwitch(
            text = stringResource(R.string.include_lowercase),
            checked = useLower,
            onCheckedChange = {
                // Si se intentan desactivar todas las opciones, no permitir deshabilitar minúsculas
                if (it || (useUpper || useDigits || useSymbols)) {
                    onLowerChange(it)
                }
            }
        )

        CharacterOptionSwitch(
            text = stringResource(R.string.include_uppercase),
            checked = useUpper,
            onCheckedChange = {
                if (it || useLower || useDigits || useSymbols) {
                    onUpperChange(it)
                } else {
                    // Activar minúsculas si intentan desactivar todo
                    onUpperChange(it)
                    onLowerChange(true)
                }
            }
        )

        CharacterOptionSwitch(
            text = stringResource(R.string.include_digits),
            checked = useDigits,
            onCheckedChange = {
                if (it || useLower || useUpper || useSymbols) {
                    onDigitsChange(it)
                } else {
                    onDigitsChange(it)
                    onLowerChange(true)
                }
            }
        )

        CharacterOptionSwitch(
            text = stringResource(R.string.include_symbols),
            checked = useSymbols,
            onCheckedChange = {
                if (it || useLower || useUpper || useDigits) {
                    onSymbolsChange(it)
                } else {
                    onSymbolsChange(it)
                    onLowerChange(true)
                }
            }
        )

        CharacterOptionSwitch(
            text = stringResource(R.string.avoid_ambiguous),
            checked = avoidAmbiguous,
            onCheckedChange = onAvoidAmbiguousChange
        )
    }
}

@Composable
fun CharacterOptionSwitch(
    text: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text,
            modifier = Modifier.weight(1f)
        )
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}