package com.marioalonso.enclave.layout

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.marioalonso.enclave.classes.Folder
import com.marioalonso.enclave.classes.Secret
import com.marioalonso.enclave.lists.FolderList
import com.marioalonso.enclave.lists.SecretList
import com.marioalonso.enclave.viewmodel.SecretViewModel
import androidx.compose.material.icons.Icons
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.NavigationBar
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.marioalonso.enclave.R
import com.marioalonso.enclave.editors.SecretEditor
import com.marioalonso.enclave.navigation.NavRoutes
import com.marioalonso.enclave.screens.FolderListScreen
import com.marioalonso.enclave.screens.SecretListScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppScaffold(
    navController: NavController,
    viewModel: SecretViewModel,
    route: String
) {
    val secrets = mutableListOf<Secret>()
    var selectedSecret by remember { mutableStateOf<Secret?>(null) }
    Scaffold(
        content = { paddingValues ->
            Column(Modifier.padding(paddingValues)) {
//                if (selectedSecret != null) {
//                    SecretEditor(
//                        viewModel = viewModel,
//                        secret = selectedSecret!!
//                    )
//                } else {
//                    // If no secret is selected, show the list of secrets
//                    SecretList(
//                        viewModel = viewModel,
//                        secrets = secrets,
//                        onItemClick = { secret: Secret ->
//                            selectedSecret = secret
//                        }
//                    )
//                }
                when(route){
                    NavRoutes.Secrets.route -> {
                        SecretListScreen(
                            navController = navController,
                            viewModel = viewModel,
                        )
                    }
                    NavRoutes.Folders.route -> {
                        FolderListScreen(
                            navController = navController,
                            viewModel = viewModel
                        )
                    }
                }
//                SecretList(
//                    viewModel = viewModel,
//                    secrets = secrets,
//                    onItemClick = { secret: Secret ->
//                        selectedSecret = secret
//                    }
//                )
            }
        },
        topBar = {
            CenterAlignedTopAppBar(title = {
                Text(
                    text = stringResource(R.string.app_name),
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleLarge
                )
            }, colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = MaterialTheme.colorScheme.primary
            ), actions = {
                Icon(
                    imageVector = Icons.Filled.Settings,
                    tint = MaterialTheme.colorScheme.onPrimary,
                    contentDescription = "Settings",
                    modifier = Modifier
                        .clickable {}
                        .padding(8.dp)
                )
            })
        },
        floatingActionButton = {
            if (route == NavRoutes.Secrets.route || route == NavRoutes.Folders.route) {
                FloatingActionButton(
                    onClick = {
                        when (route) {
//                            NavRoutes.Secrets.route -> navController.navigate(NavRoutes.SecretEditor.route + "/adding card" + "/${deckId}")
                            NavRoutes.Secrets.route -> navController.navigate(NavRoutes.SecretEditor.route)
                            NavRoutes.Folders.route -> navController.navigate(NavRoutes.FolderEditor.route)
                        }
                        //                    navController.navigate(NavRoutes.CardEditor.route)
                    },
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Add card"
                    )
                }
            }
        },
        floatingActionButtonPosition = FabPosition.End,
        bottomBar = { AppBottomNavigationBar(navController) }
    )
}