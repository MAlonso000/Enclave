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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.marioalonso.enclave.R
import com.marioalonso.enclave.editors.SecretEditor
import com.marioalonso.enclave.navigation.NavRoutes
import com.marioalonso.enclave.screens.FolderListScreen
import com.marioalonso.enclave.screens.SecretListScreen
import com.marioalonso.enclave.utils.AESCipherGCM

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppScaffold(
    navController: NavController,
    viewModel: SecretViewModel,
    route: String,
    folderId: String = "all"
) {
    var fabExpanded by remember { mutableStateOf(false) }
    val context = LocalContext.current
    // Verificar si la clave está inicializada
    LaunchedEffect(Unit) {
        if (!AESCipherGCM.isKeyInitialized(context)) {
            // Redirigir al usuario a la pantalla de inicio de sesión
            navController.navigate(NavRoutes.Home.route) {
                popUpTo(NavRoutes.Home.route) { inclusive = true }
            }
        }
    }

    Scaffold(
        content = { paddingValues ->
            Column(Modifier.padding(paddingValues)) {
                when(route){
                    NavRoutes.Secrets.route -> {
                        SecretListScreen(
                            navController = navController,
                            viewModel = viewModel,
                            folderId = folderId
                        )
                    }
                    NavRoutes.Folders.route -> {
                        FolderListScreen(
                            navController = navController,
                            viewModel = viewModel
                        )
                    }
                }
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
//                Icon(
//                    imageVector = Icons.Filled.Settings,
//                    tint = MaterialTheme.colorScheme.onPrimary,
//                    contentDescription = "Settings",
//                    modifier = Modifier
//                        .clickable {}
//                        .padding(8.dp)
//                )
                Icon(
                    painter = painterResource(R.drawable.logout),
                    tint = MaterialTheme.colorScheme.onPrimary,
                    contentDescription = "Logout",
                    modifier = Modifier
                        .clickable {
                            AESCipherGCM.logout(context)
                            Toast.makeText(context, R.string.logged_out, Toast.LENGTH_SHORT).show()
                            navController.navigate(NavRoutes.Home.route) {
                                popUpTo(NavRoutes.Home.route) { inclusive = true }
                            }
                        }
                        .padding(8.dp)
                )
            })
        },
        floatingActionButton = {
            if (route == NavRoutes.Secrets.route) {
                ExpandableFab(
                    isExpanded = fabExpanded,
                    onFabToggle = { fabExpanded = !fabExpanded },
                    onAddType1 = {
                        fabExpanded = false
                        navController.navigate(NavRoutes.SecretEditor.route + "/add_credential" + "/${folderId}")
                    },
                    onAddType2 = {
                        fabExpanded = false
                        navController.navigate(NavRoutes.SecretEditor.route + "/add_note" + "/${folderId}")
                    },
                    onAddType3 = {
                        fabExpanded = false
                        navController.navigate(NavRoutes.SecretEditor.route + "/add_credit_card" + "/${folderId}")
                    }
                )
            } else if (route == NavRoutes.Folders.route) {
                FloatingActionButton(
                    onClick = { navController.navigate(NavRoutes.FolderEditor.route) },
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(imageVector = Icons.Filled.Add, contentDescription = "Add folder")
                }
            }
        },
        floatingActionButtonPosition = FabPosition.End,
        bottomBar = { AppBottomNavigationBar(navController) }
    )
}