package com.marioalonso.enclave.items

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.marioalonso.enclave.classes.Folder
import com.marioalonso.enclave.classes.Secret
import com.marioalonso.enclave.viewmodel.SecretViewModel

@Composable
fun FolderItem(
    folder: Folder,
    secrets: List<Secret>,
    onFolderClick: (Folder) -> Unit,
    modifier: Modifier = Modifier
) {
    val numberOfSecretsInFolder = secrets.filter { it.folderId == folder.id }.size
    Row(
        modifier
            .fillMaxWidth()
            .padding(all = 10.dp)
            .clickable { onFolderClick(folder) },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ){
        Column(){
            Text(
                text = folder.name,
                modifier = Modifier,
            )
        }
        Column() {
            Text(
                text = "(${numberOfSecretsInFolder})",
                modifier = Modifier,
            )
        }
    }
}