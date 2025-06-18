package com.marioalonso.enclave.layout

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.ui.res.stringResource
import com.marioalonso.enclave.R

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