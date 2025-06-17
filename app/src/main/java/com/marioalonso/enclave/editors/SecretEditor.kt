package com.marioalonso.enclave.editors

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.marioalonso.enclave.R
import com.marioalonso.enclave.classes.CardSecret
import com.marioalonso.enclave.classes.CredentialSecret
import com.marioalonso.enclave.classes.NoteSecret
import com.marioalonso.enclave.classes.Secret
import com.marioalonso.enclave.viewmodel.SecretViewModel

@Composable
fun SecretEditor(
    viewModel: SecretViewModel,
    secret: Secret
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
//        modifier = Modifier.fillMaxWidth().fillMaxHeight()
        modifier = Modifier
    ) {
        when (secret) {
            is CredentialSecret -> CredentialSecretEditor(viewModel, secret)
            is NoteSecret -> NoteSecretEditor(viewModel, secret)
            is CardSecret -> CardSecretEditor(viewModel, secret)
            else -> throw IllegalArgumentException("Unsupported secret type: ${secret::class.simpleName}")
        }
    }
}

@Composable
fun CardSecretEditor(viewModel: SecretViewModel, secret: CardSecret) {
    var title by remember { mutableStateOf(secret.title) }
    var ownerName by remember { mutableStateOf(secret.ownerName) }
    var encryptedCardNumber by remember { mutableStateOf(secret.encryptedCardNumber) }
    var encryptedPin by remember { mutableStateOf(secret.encryptedPin) }
    var brand by remember { mutableStateOf(secret.brand) }
    var expirationDate by remember { mutableStateOf(secret.expirationDate) }
    var encryptedCVV by remember { mutableStateOf(secret.encryptedCVV) }

    val onAccepted = {
        secret.title = title
        secret.ownerName = ownerName
        secret.encryptedCardNumber = encryptedCardNumber
        secret.encryptedPin = encryptedPin
        secret.brand = brand
        secret.expirationDate = expirationDate
        secret.encryptedCVV = encryptedCVV
        viewModel.insertSecret(secret)
    }
    val onCanceled = {

    }
    OutlinedTextField(
        value = title,
        onValueChange = { title = it },
        label = { Text("Title") }
    )
    OutlinedTextField(
        value = ownerName,
        onValueChange = { ownerName = it },
        label = { Text("Owner Name") }
    )
    OutlinedTextField(
        value = encryptedCardNumber,
        onValueChange = { encryptedCardNumber = it },
        label = { Text("Encrypted Card Number") }
    )
    OutlinedTextField(
        value = encryptedPin,
        onValueChange = { encryptedPin = it },
        label = { Text("Encrypted PIN") }
    )
    OutlinedTextField(
        value = brand,
        onValueChange = { brand = it },
        label = { Text("Brand") }
    )
    OutlinedTextField(
        value = expirationDate,
        onValueChange = { expirationDate = it },
        label = { Text("Expiration Date") }
    )
    OutlinedTextField(
        value = encryptedCVV,
        onValueChange = { encryptedCVV = it },
        label = { Text("Encrypted CVV") }
    )
    Row(
        modifier = Modifier.padding(10.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(
            onClick = onAccepted,
            modifier = Modifier.padding(10.dp)
        ) {
            Text(text = stringResource(R.string.accept))
        }
        Button(
            onClick = { onCanceled() },
            modifier = Modifier.padding(10.dp)
        ) {
            Text(text = stringResource(R.string.cancel))
        }
    }


}

@Composable
fun NoteSecretEditor(viewModel: SecretViewModel, secret: NoteSecret) {
    var title by remember { mutableStateOf(secret.title) }
    var note by remember { mutableStateOf(secret.encryptedNote) }
    val onAccepted = {
        secret.title = title
        secret.encryptedNote = note
        viewModel.insertSecret(secret)
    }
    val onCanceled = {

    }
    OutlinedTextField(
        value = title,
        onValueChange = { title = it },
        label = { Text("Title") }
    )
    OutlinedTextField(
        value = note,
        onValueChange = { note = it },
        label = { Text("Encrypted Note") }
    )
    Row(
        modifier = Modifier.padding(10.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(
            onClick = onAccepted,
            modifier = Modifier.padding(10.dp)
        ) {
            Text(text = stringResource(R.string.accept))
        }
        Button(
            onClick = { onCanceled() },
            modifier = Modifier.padding(10.dp)
        ) {
            Text(text = stringResource(R.string.cancel))
        }
    }
}

@Composable
fun CredentialSecretEditor(viewModel: SecretViewModel, secret: CredentialSecret) {
    var title by remember { mutableStateOf(secret.title) }
    var username by remember { mutableStateOf(secret.username) }
    var password by remember { mutableStateOf(secret.encryptedPassword) }
    var url by remember { mutableStateOf(secret.url) }
    var email by remember { mutableStateOf(secret.email) }

    val onAccepted = {
        secret.title = title
        secret.username = username
        secret.encryptedPassword = password
        secret.url = url
        secret.email = email
        viewModel.insertSecret(secret)
    }
    val onCanceled = {

    }
    OutlinedTextField(
        value = title,
        onValueChange = { title = it },
        label = { Text("Title") }
    )
    OutlinedTextField(
        value = username,
        onValueChange = { username = it },
        label = { Text("Username") }
    )
    OutlinedTextField(
        value = password,
        onValueChange = { password = it },
        label = { Text("Encrypted Password") }
    )
    OutlinedTextField(
        value = url,
        onValueChange = { url = it },
        label = { Text("URL") }
    )
    OutlinedTextField(
        value = email,
        onValueChange = { email = it },
        label = { Text("Email") }
    )
    Row(
        modifier = Modifier.padding(10.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(
            onClick = onAccepted,
            modifier = Modifier.padding(10.dp)
        ) {
            Text(text = stringResource(R.string.accept))
        }
        Button(
            onClick = { onCanceled() },
            modifier = Modifier.padding(10.dp)
        ) {
            Text(text = stringResource(R.string.cancel))
        }
    }
}