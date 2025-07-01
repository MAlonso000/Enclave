package com.marioalonso.enclave

import android.app.Application
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.marioalonso.enclave.classes.CredentialSecret
import com.marioalonso.enclave.classes.NoteSecret
import com.marioalonso.enclave.items.SecretItem
import com.marioalonso.enclave.screen.Screen
import com.marioalonso.enclave.screens.MainScreen
import com.marioalonso.enclave.ui.theme.EnclaveTheme
import com.marioalonso.enclave.utils.AESCipherGCM
import com.marioalonso.enclave.viewmodel.SecretViewModel
import com.marioalonso.enclave.viewmodel.SecretViewModelFactory
import java.time.LocalDateTime
import java.util.Base64

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            EnclaveTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val owner = LocalViewModelStoreOwner.current
                    val context = LocalContext.current

                    owner?.let {
                        val viewModel: SecretViewModel = viewModel(
                            it,
                            "SecretViewModel",
                            SecretViewModelFactory(
                                LocalContext.current.applicationContext as Application
                            )
                        )

                        // Inicializar la clave de cifrado
//                        AESCipherGCM.initializeKey(context, "miClaveMaestra123")
                        // Logs de prueba para verificar un cifrado de prueba
//                        Log.d("MainActivity", "Clave de cifrado inicializada: ${AESCipherGCM.getCryptoKey()}")
//                        val plainText = "Este es un texto de prueba"
//                        val encryptedText = AESCipherGCM.encrypt(plainText, AESCipherGCM.getCryptoKey()!!)
//                        Log.d("MainActivity", "Texto cifrado: $encryptedText")
//                        val decryptedText = AESCipherGCM.decrypt(encryptedText, AESCipherGCM.getCryptoKey()!!)
//                        Log.d("MainActivity", "Texto descifrado: $decryptedText")

//                        Screen(viewModel)
                        MainScreen(viewModel)
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        AESCipherGCM.clearCryptoKey() // Limpia la clave al cerrar la aplicación
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    EnclaveTheme {
        Greeting("Android")
    }
}