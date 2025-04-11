package com.marioalonso.enclave.items

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun SecretItem(
    name: String,
    secret: String,
    lastUpdate :String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier.fillMaxWidth().padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top,
    ) {
        Column(
            horizontalAlignment = Alignment.Start
        ) {
            Text(name, modifier)
            Text(secret, modifier)
        }
        Column(
            horizontalAlignment = Alignment.End
        ){
            Text(lastUpdate.toString().substring(0..9))
        }

    }
}