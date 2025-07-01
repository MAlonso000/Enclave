package com.marioalonso.enclave.editors

import android.util.Log
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
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.marioalonso.enclave.R
import com.marioalonso.enclave.classes.CardSecret
import com.marioalonso.enclave.classes.CredentialSecret
import com.marioalonso.enclave.classes.NoteSecret
import com.marioalonso.enclave.classes.Secret
import com.marioalonso.enclave.entities.SecretEntity
import com.marioalonso.enclave.navigation.NavRoutes
import com.marioalonso.enclave.utils.AESCipherGCM
import com.marioalonso.enclave.viewmodel.SecretViewModel

@Composable
fun SecretEditor(
    navController: NavController,
    viewModel: SecretViewModel,
    secretId: String,
    folderId: String
) {
    when {
        secretId.equals("add_credential") -> {
            CredentialSecretEditor(
                navController = navController,
                viewModel = viewModel,
                CredentialSecret(
                    title = "",
                    username = "",
                    encryptedPassword = "",
                    url = "",
                    email = "",
                    folderId = if (folderId.equals("all")) null else folderId
                )
            )
            return
        }

        secretId.equals("add_note") -> {
            NoteSecretEditor(
                navController = navController,
                viewModel = viewModel,
                NoteSecret(
                    title = "",
                    folderId = if (folderId.equals("all")) null else folderId,
                    encryptedNote = ""
                )
            )
            return
        }

        secretId.equals("add_credit_card") -> {
            CardSecretEditor(
                navController = navController,
                viewModel = viewModel,
                CardSecret(
                    title = "",
                    ownerName = "",
                    encryptedCardNumber = "",
                    encryptedPin = "",
                    brand = "",
                    expirationDate = "",
                    encryptedCVV = "",
                    folderId = if (folderId.equals("all")) null else folderId
                )
            )
            return
        }
    }
    val secrets = viewModel.secrets.observeAsState(emptyList()).value
    val secret = secrets.find { it.id == secretId }
    when (secret) {
        is CredentialSecret -> CredentialSecretEditor(navController, viewModel, secret, folderId)
        is NoteSecret -> NoteSecretEditor(navController, viewModel, secret, folderId)
        is CardSecret -> CardSecretEditor(navController, viewModel, secret, folderId)
        else -> throw IllegalArgumentException("Unsupported secret type: ${secret!!::class.simpleName}")
    }
}

@Composable
fun CardSecretEditor(navController: NavController, viewModel: SecretViewModel, secret: CardSecret, folderId: String? = null) {
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
        secret.encryptedCardNumber = AESCipherGCM.encrypt(encryptedCardNumber)
        secret.encryptedPin = AESCipherGCM.encrypt(encryptedPin)
        secret.brand = brand
        secret.expirationDate = expirationDate
        secret.encryptedCVV = AESCipherGCM.encrypt(encryptedCVV)
        viewModel.insertSecret(secret)
        navController.navigate(NavRoutes.Secrets.route) {
            popUpTo(NavRoutes.Home.route)
        }
    }
    val onCanceled = {
        navController.popBackStack()
    }
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
//        modifier = Modifier.fillMaxWidth().fillMaxHeight()
        modifier = Modifier
    ) {
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
}

@Composable
fun NoteSecretEditor(navController: NavController, viewModel: SecretViewModel, secret: NoteSecret, folderId: String? = null) {
    var title by remember { mutableStateOf(secret.title) }
    var note by remember { mutableStateOf(secret.encryptedNote) }
    val onAccepted = {
        secret.title = title
        secret.encryptedNote = AESCipherGCM.encrypt(note)
        viewModel.insertSecret(secret)
        navController.navigate(NavRoutes.Secrets.route) {
            popUpTo(NavRoutes.Home.route)
        }
    }
    val onCanceled = {
        navController.popBackStack()
    }
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
//        modifier = Modifier.fillMaxWidth().fillMaxHeight()
        modifier = Modifier
    ) {
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
}

@Composable
fun CredentialSecretEditor(navController: NavController, viewModel: SecretViewModel, secret: CredentialSecret, folderId: String? = null) {
    var title by remember { mutableStateOf(secret.title) }
    var username by remember { mutableStateOf(secret.username) }
    var password by remember { mutableStateOf(secret.encryptedPassword) }
    var url by remember { mutableStateOf(secret.url) }
    var email by remember { mutableStateOf(secret.email) }

    val onAccepted = {
        secret.title = title
        secret.username = username
        secret.encryptedPassword = AESCipherGCM.encrypt(password)
        secret.url = url
        secret.email = email
        viewModel.insertSecret(secret)
        navController.navigate(NavRoutes.Secrets.route) {
            popUpTo(NavRoutes.Home.route)
        }
    }
    val onCanceled = {
        navController.popBackStack()
    }
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
//        modifier = Modifier.fillMaxWidth().fillMaxHeight()
        modifier = Modifier
    ) {
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
}