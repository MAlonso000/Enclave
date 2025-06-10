package com.marioalonso.enclave.lists

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import com.marioalonso.enclave.classes.Secret
import com.marioalonso.enclave.items.SecretItem
import com.marioalonso.enclave.viewmodel.SecretViewModel
import androidx.compose.runtime.getValue

@Composable
fun SecretList (
    viewModel: SecretViewModel,
    secrets: List<Secret>,
    onItemClick : (Secret) -> Unit,
){
    val secrets by viewModel.secrets.observeAsState(listOf())
    LazyColumn() {
        item{
            Text(
                "Lista de secretos",
                Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }
        items(secrets) { secret ->
            SecretItem(secret, onItemClick)
        }
    }
}