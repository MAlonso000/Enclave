package com.marioalonso.enclave.items

import android.widget.Toast
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.marioalonso.enclave.classes.Secret
import com.marioalonso.enclave.utils.AESCipherGCM
import com.marioalonso.enclave.R
import java.util.Base64

@Composable
fun SecretItem(
    secret: Secret,
    onItemClick : (Secret) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var switchState by remember { mutableStateOf(false) }
    val onSwitchChange = { it: Boolean -> switchState = it }

    Row(
        modifier.fillMaxWidth().padding(all = 20.dp).clickable { onItemClick(secret) },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Row{
            SwitchICon(
                switchState = switchState,
                onSwitchChange = onSwitchChange,
            )
            SecretData(secret = secret, switchState, modifier)
        }
        Text("prueba")
    }
}

@Composable
fun SecretData(
    secret: Secret,
    switchState: Boolean,
    modifier: Modifier = Modifier
) {
//    val salt = Base64.getDecoder().decode("OnNCziG6nTZeun+nWHtTOw==")
//    val key = remember { AESCipherGCM.deriveKey("miClaveMaestra123".toCharArray(), salt) }

    Column() {
        Text(secret.title, modifier)
        if(switchState) {
            Text("prueba")
//            Text(secret.getContent(key), modifier)
        }
    }
}

@Composable
fun SwitchICon(switchState: Boolean, onSwitchChange: (Boolean) -> Unit) {
    val drawableResource = if (switchState) R.drawable.flecha_expanded
    else R.drawable.flecha_expand

    Icon(
        painter = painterResource(id = drawableResource),
        contentDescription = "contentDescription",
        modifier = Modifier
            .clickable { onSwitchChange(!switchState) }
    )
}