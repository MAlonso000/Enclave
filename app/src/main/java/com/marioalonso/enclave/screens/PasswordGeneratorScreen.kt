package com.marioalonso.enclave.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.marioalonso.enclave.R
import com.marioalonso.enclave.utils.PasswordGenerator

/**
 * Pantalla para generar contraseñas seguras.
 *
 * @param navController Controlador de navegación para manejar la navegación entre pantallas.
 */
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

/**
 * Genera una contraseña segura basada en las opciones proporcionadas.
 *
 * @param length Longitud de la contraseña.
 * @param useUpper Incluir letras mayúsculas.
 * @param useLower Incluir letras minúsculas.
 * @param useDigits Incluir dígitos.
 * @param useSymbols Incluir símbolos.
 * @param avoidAmbiguous Evitar caracteres ambiguos (como 'O' y '0').
 * @return La contraseña generada o null si no se pueden generar con las opciones dadas.
 */
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
    } catch (_: IllegalArgumentException) {
        null
    }
}

/**
 * Composable que muestra las opciones para generar la contraseña.
 *
 * @param useLower Estado de inclusión de letras minúsculas.
 * @param onLowerChange Callback para cambiar el estado de inclusión de letras minúsculas.
 * @param useUpper Estado de inclusión de letras mayúsculas.
 * @param onUpperChange Callback para cambiar el estado de inclusión de letras mayúsculas.
 * @param useDigits Estado de inclusión de dígitos.
 * @param onDigitsChange Callback para cambiar el estado de inclusión de dígitos.
 * @param useSymbols Estado de inclusión de símbolos.
 * @param onSymbolsChange Callback para cambiar el estado de inclusión de símbolos.
 * @param avoidAmbiguous Estado de evitar caracteres ambiguos.
 * @param onAvoidAmbiguousChange Callback para cambiar el estado de evitar caracteres ambiguos.
 */
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

/**
 * Composable que muestra una opción con un interruptor (Switch).
 *
 * @param text Texto descriptivo de la opción.
 * @param checked Estado del interruptor.
 * @param onCheckedChange Callback para cambiar el estado del interruptor.
 */
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