package com.marioalonso.enclave.layout

/**
 * Clase que representa un elemento de la barra de navegación inferior.
 *
 * @property title El título del elemento (referencia a un recurso de cadena).
 * @property icon El icono del elemento (referencia a un recurso drawable).
 * @property route La ruta de navegación asociada al elemento.
 */
data class BarItem(
    val title: Int,
    val icon: Int,
    val route: String
)