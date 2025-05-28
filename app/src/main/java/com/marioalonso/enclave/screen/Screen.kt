package com.marioalonso.enclave.screen

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.marioalonso.enclave.classes.CredentialSecret
import com.marioalonso.enclave.classes.Folder
import com.marioalonso.enclave.classes.NoteSecret
import com.marioalonso.enclave.classes.Secret
import com.marioalonso.enclave.items.SecretItem
import com.marioalonso.enclave.lists.SecretList
import com.marioalonso.enclave.ui.theme.EnclaveTheme

@Preview(showBackground = true)
@Composable
fun Screen() {
    val context = LocalContext.current
    val fodler = Folder(
        name = "Mis secretos",
    )
    val secrets = mutableListOf<Secret>()
    secrets += NoteSecret(
        folderId = fodler.folderId,
        title = "Titulo",
        encryptedNote = "cYljh820HF7IJ0hz+EZdq8rP0en3/+S//1n7Xmh2wvviDVO/7ev9/E88/AlcZUAgXymsauOe"
    )
    secrets += CredentialSecret(
        title = "Titulo2",
        username = "mario000",
        encryptedPassword = "Dc/MYeD8tndtxNw6msww1rfrHlMU3SucU+tKksUxPlvso+jvz3lll3BLPhisU87RStXpI7x0"
    )
    secrets += NoteSecret(
        title = "Titulo3",
        encryptedNote = "cYljh820HF7IJ0hz+EZdq8rP0en3/+S//1n7Xmh2wvviDVO/7ev9/E88/AlcZUAgXymsauOe"
    )
    secrets += CredentialSecret(
        title = "Titulo4",
        username = "mario000",
        encryptedPassword = "Dc/MYeD8tndtxNw6msww1rfrHlMU3SucU+tKksUxPlvso+jvz3lll3BLPhisU87RStXpI7x0"
    )
    SecretList(secrets = secrets, onItemClick = {  secret: Secret ->
        Toast.makeText(
            context,
            "prueba",
            Toast.LENGTH_SHORT
        ).show()
    })
}