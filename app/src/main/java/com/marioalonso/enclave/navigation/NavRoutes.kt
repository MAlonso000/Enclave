package com.marioalonso.enclave.navigation

/**
 * Clase sellada que define las rutas de navegación en la aplicación.
 *
 * @property route La ruta de navegación como una cadena.
 */
sealed class NavRoutes(val route: String) {
    object Home : NavRoutes("home")
    object Secrets : NavRoutes("secrets")
    object Folders : NavRoutes("folders")
    object SecretEditor: NavRoutes("secret_editor")
    object FolderEditor: NavRoutes("folder_editor")
    object PasswordGenerator: NavRoutes("generate_password")
    object ChangePassword: NavRoutes("change_password")
}