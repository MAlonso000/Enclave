package com.marioalonso.enclave.screens

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.marioalonso.enclave.classes.Folder
import com.marioalonso.enclave.classes.NoteSecret
import com.marioalonso.enclave.editors.FolderEditor
import com.marioalonso.enclave.editors.SecretEditor
import com.marioalonso.enclave.layout.AppScaffold
import com.marioalonso.enclave.navigation.NavRoutes
import com.marioalonso.enclave.viewmodel.SecretViewModel

@Composable
fun MainScreen(viewModel: SecretViewModel) {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = NavRoutes.Home.route
    ) {
        composable(NavRoutes.Home.route) {
            Home(navController)
        }
        composable(NavRoutes.Secrets.route) {
            AppScaffold(navController, viewModel, NavRoutes.Secrets.route)
        }
        composable(NavRoutes.Folders.route) {
            AppScaffold(navController, viewModel, NavRoutes.Folders.route)
        }
        composable(NavRoutes.SecretEditor.route) {
            SecretEditor(navController, viewModel, NoteSecret(
                title = "",
                folderId = null,
                encryptedNote = ""
            ))
        }
        composable(NavRoutes.FolderEditor.route) { FolderEditor(navController, viewModel, Folder(
                id = "",
                name = ""
            ))
        }
    }
}