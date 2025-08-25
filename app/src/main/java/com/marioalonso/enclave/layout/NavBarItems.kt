package com.marioalonso.enclave.layout

import com.marioalonso.enclave.R

/**
 * Objeto que contiene los elementos de la barra de navegación.
 */
object NavBarItems {
    val BarItems = listOf(
        BarItem(
            title = R.string.folders,
            icon = R.drawable.folders,
            route = "folders"
        ),
        BarItem(
            title = R.string.secrets,
            icon = R.drawable.secret,
            route = "secrets"
        ),
        BarItem(
            title = R.string.generate,
            icon = R.drawable.generate_password,
            route = "generate_password"
        )
    )
}