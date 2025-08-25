package com.marioalonso.enclave

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.marioalonso.enclave.screens.MainScreen
import com.marioalonso.enclave.ui.theme.EnclaveTheme
import com.marioalonso.enclave.utils.AESCipherGCM
import com.marioalonso.enclave.viewmodel.SecretViewModel
import com.marioalonso.enclave.viewmodel.SecretViewModelFactory

/**
 * Actividad principal de la aplicación.
 * Configura el tema y la pantalla principal.
 * Limpia la clave criptográfica al destruirse.
 */
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

                    owner?.let {
                        val viewModel: SecretViewModel = viewModel(
                            it,
                            "SecretViewModel",
                            SecretViewModelFactory(
                                LocalContext.current.applicationContext as Application
                            )
                        )
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