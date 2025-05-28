package com.marioalonso.enclave

import android.os.Bundle
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.marioalonso.enclave.classes.CredentialSecret
import com.marioalonso.enclave.classes.NoteSecret
import com.marioalonso.enclave.items.SecretItem
import com.marioalonso.enclave.screen.Screen
import com.marioalonso.enclave.ui.theme.EnclaveTheme
import com.marioalonso.enclave.utils.AESCipherGCM
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
////                    val salt = Base64.getDecoder().decode("Kb3oHhhNoWiCrcETeZ9HgQ==")
////                    val key = AESCipherGCM.deriveKey("miClaveMaestra123".toCharArray(), salt)
////                    val text = AESCipherGCM.decrypt("1+c+q467KsyYzaUuU5S14ryechFuIOPY+A/Kw9HyUCgrYmdo8FHUkQ3CwXCsd0Iu+DCHfjIph5eA1vPtVkqT7L73EKeSUQ==", key)
////                    Text(
////                        text = text,
////                        modifier = Modifier.padding(16.dp)
////                    )
////                    Column(
////
////                    ){
////                        SecretItem(NoteSecret("Titulo", "cYljh820HF7IJ0hz+EZdq8rP0en3/+S//1n7Xmh2wvviDVO/7ev9/E88/AlcZUAgXymsauOe"))
////                        SecretItem(CredentialSecret("Titulo", "mario000", "Dc/MYeD8tndtxNw6msww1rfrHlMU3SucU+tKksUxPlvso+jvz3lll3BLPhisU87RStXpI7x0"))
////                    }
                    Screen()
                }
            }
        }
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