package com.marioalonso.enclave.lists

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.marioalonso.enclave.classes.Secret
import com.marioalonso.enclave.viewmodel.SecretViewModel
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.marioalonso.enclave.R
import com.marioalonso.enclave.items.SwipeableSecretItem

@Composable
fun SecretList (
    viewModel: SecretViewModel,
    secrets: List<Secret>,
    onItemClick : (Secret) -> Unit,
){
//    val secrets by viewModel.secrets.observeAsState(listOf())
    LazyColumn() {
        item{
            Text(
                stringResource(R.string.secret_list),
                Modifier.padding(12.dp).fillMaxWidth(),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
            )
        }
        items(secrets) { secret ->
//            SecretItem(secret, onItemClick)
            SwipeableSecretItem(
                viewModel = viewModel,
                secret = secret,
                onItemClick = onItemClick
            )
        }
    }
}