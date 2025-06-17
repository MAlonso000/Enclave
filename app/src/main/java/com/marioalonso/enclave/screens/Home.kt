package com.marioalonso.enclave.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.marioalonso.enclave.navigation.NavRoutes

@Composable
fun Home(navController: NavController) {
    Box(
        Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Button( onClick = {
            navController.navigate(NavRoutes.Secrets.route)
        }) {
            Text("ENCLAVE")
        }
    }
}