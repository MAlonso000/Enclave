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
import android.util.Log
import androidx.compose.ui.platform.LocalContext

@Composable
fun MainScreen(viewModel: SecretViewModel) {

    val navController = rememberNavController()
    val context = LocalContext.current

    NavHost(
        navController = navController,
        startDestination = NavRoutes.Home.route
    ) {
        composable(NavRoutes.Home.route) {
            Home(navController, context)
        }
        composable(NavRoutes.Secrets.route) {
            AppScaffold(navController, viewModel, NavRoutes.Secrets.route)
        }
        composable(NavRoutes.Secrets.route + "/{folderId}") { backEntry ->
            val folderId = backEntry.arguments?.getString("folderId")
            folderId?.let {
                AppScaffold(navController, viewModel, NavRoutes.Secrets.route, it)
            }
        }
        composable(NavRoutes.Folders.route) {
            AppScaffold(navController, viewModel, NavRoutes.Folders.route)
        }
//        composable(NavRoutes.SecretEditor.route) {
//            SecretEditor(navController, viewModel, NoteSecret(
//                title = "",
//                folderId = null,
//                encryptedNote = ""
//            ))
//        }
        composable(NavRoutes.SecretEditor.route + "/{secret_id}" + "/{folder_id}") { backEntry ->
            val secretId = backEntry.arguments?.getString("secret_id")
            val folderId = backEntry.arguments?.getString("folder_id")
            secretId?.let { id ->
                folderId?.let { fid ->
                    SecretEditor(navController, viewModel, id, fid)
                }
            }
        }
        composable(NavRoutes.FolderEditor.route) { FolderEditor(navController, viewModel, Folder(
                name = ""
            ))
        }
    }
}