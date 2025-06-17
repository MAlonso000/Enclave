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
import com.marioalonso.enclave.layout.AppScaffold
import com.marioalonso.enclave.lists.FolderList
import com.marioalonso.enclave.lists.SecretList
import com.marioalonso.enclave.ui.theme.EnclaveTheme
import com.marioalonso.enclave.viewmodel.SecretViewModel

@Composable
fun Screen(viewModel: SecretViewModel,) {
    val context = LocalContext.current
    val folder = Folder(
        name = "Mis secretos",
    )
    val secrets = mutableListOf<Secret>()
//    SecretList(viewModel = viewModel, secrets = secrets, onItemClick = {  secret: Secret ->
//        Toast.makeText(
//            context,
//            "prueba",
//            Toast.LENGTH_SHORT
//        ).show()
//    })
//    FolderList(
//        viewModel = viewModel,
//        folders = listOf(folder),
//        secrets = secrets,
//        onFolderClick = { folder: Folder ->
//            Toast.makeText(
//                context,
//                "Clicked on folder: ${folder.name}",
//                Toast.LENGTH_SHORT
//            ).show()
//        }
//    )
//    AppScaffold(
//        viewModel = viewModel
//    )
}