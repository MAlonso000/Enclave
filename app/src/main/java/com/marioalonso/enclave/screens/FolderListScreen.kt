package com.marioalonso.enclave.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.navigation.NavController
import com.marioalonso.enclave.lists.FolderList
import com.marioalonso.enclave.viewmodel.SecretViewModel
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.marioalonso.enclave.R
import com.marioalonso.enclave.classes.Folder
import com.marioalonso.enclave.navigation.NavRoutes

@Composable
fun FolderListScreen(
    navController: NavController,
    viewModel: SecretViewModel,
) {
    val context = LocalContext.current
    val secrets by viewModel.secrets.observeAsState(listOf())
    val folders by viewModel.folders.observeAsState(listOf())
    val onFolderClick = { folder: Folder ->
//            Toast.makeText(
//                context,
//                "Clicked on folder: ${folder.name}",
//                Toast.LENGTH_SHORT
//            ).show()
            Log.d("FolderListScreen", "Clicked on folder: ${folder.id}")
            navController.navigate(
                NavRoutes.Secrets.route + "/${folder.id}"
            )
        }
    if(folders.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize().padding(40.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(stringResource(R.string.no_folders))
        }
    } else {
        FolderList(viewModel = viewModel, secrets = secrets, folders = folders, onFolderClick = onFolderClick)
    }
}