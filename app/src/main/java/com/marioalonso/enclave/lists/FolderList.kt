package com.marioalonso.enclave.lists

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import com.marioalonso.enclave.classes.Folder
import com.marioalonso.enclave.classes.Secret
import com.marioalonso.enclave.items.FolderItem
import com.marioalonso.enclave.viewmodel.SecretViewModel
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.getValue
import androidx.compose.foundation.lazy.items



@Composable
fun FolderList(
    viewModel: SecretViewModel,
    folders: List<Folder>,
    secrets: List<Secret>,
    onFolderClick: (Folder) -> Unit,
){
//    val folders by viewModel.folders.observeAsState(emptyList())
//    val secrets by viewModel.secrets.observeAsState(emptyList())

    LazyColumn() {
        items(folders) { folder ->
            FolderItem(
                folder = folder,
                secrets = secrets,
                onFolderClick = onFolderClick
            )
        }
    }
}