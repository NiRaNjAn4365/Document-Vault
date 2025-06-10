package com.example.documentvault.screens

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.documentvault.R
import com.example.documentvault.database.AppDatabase
import com.example.documentvault.models.Folders
import com.example.documentvault.repository.FolderRepository
import com.example.documentvault.viewmodel.FolderViewModel
import com.example.documentvault.viewmodel.FolderViewModelFactory

@Composable
fun HomeScreen(navController: NavController) {
    val context = LocalContext.current
    val db = remember { AppDatabase.getDatabase(context) }
    val repository = remember { FolderRepository(db.folderDao()) }
    var isLongPressed by remember { mutableStateOf(false) }
    var selectedFolder by remember { mutableStateOf<Folders?>(null) }
    val viewModel: FolderViewModel = viewModel(
        factory = FolderViewModelFactory(repository)
    )



    val folders by viewModel.folders.collectAsState()
    var dismiss by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.loadFolders()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (dismiss) {
            AddFolderDialog(
                onDismiss = { dismiss = false },
                onConfirm = { name ->
                    dismiss = false
                    viewModel.addFolders(Folders(name = name))
                }
            )
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 100.dp, top = 50.dp, start = 20.dp, end = 20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(items = folders) { folder ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(4.dp, RoundedCornerShape(16.dp))
                        .pointerInput(
                            Unit
                        ){
                            detectTapGestures(
                                onTap = {
                                   val folderId=folder.id
                                    isLongPressed=false
                                    navController.navigate("photo_screen/${folderId}")
                                },
                                onLongPress = {
                                    selectedFolder = folder
                                    isLongPressed=true
                                }
                            )
                        },
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.folder),
                            contentDescription = "folder",
                            modifier = Modifier.size(40.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = folder.name,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }

        Button(
            onClick = { dismiss = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 20.dp, bottom = 40.dp)
                .size(60.dp),
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
            contentPadding = PaddingValues(0.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add",
                tint = Color.White,
                modifier = Modifier.size(30.dp)
            )
        }
        if (isLongPressed) {
            BottomAppBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomEnd),
                actions = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = {
                                selectedFolder?.let {
                                    viewModel.deleteFolders(it)
                                    isLongPressed = false
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete"
                            )
                        }
                    }
                }
            )
        }
        }
    }

@Composable
fun AddFolderDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var textValue by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = {
                    if (textValue.isNotBlank()) {
                        onConfirm(textValue.trim())
                    }
                },
                enabled = textValue.isNotBlank()
            ) {
                Text("Create")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        title = { Text("Add New Folder") },
        text = {
            Column {
                Text("Enter folder name:")
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = textValue,
                    onValueChange = { textValue = it },
                    singleLine = true,
                    placeholder = { Text("e.g. Documents") }
                )
            }
        },
        shape = RoundedCornerShape(16.dp)
    )
}