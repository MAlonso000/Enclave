package com.marioalonso.enclave.navigation

sealed class NavRoutes(val route: String) {
    object Home : NavRoutes("home")
    object Secrets : NavRoutes("secrets")
    object Folders : NavRoutes("folders")
}