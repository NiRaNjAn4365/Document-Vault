package com.example.documentvault.screens

import android.Manifest
import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.OpenableColumns
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.documentvault.R
import com.example.documentvault.database.AppDatabase
import com.example.documentvault.models.Files
import com.example.documentvault.repository.FilesRepository
import com.example.documentvault.viewModels.FilesViewModel
import com.example.documentvault.viewModels.FilesViewModelFactory
import com.google.accompanist.permissions.*
import androidx.core.net.toUri

@SuppressLint("SuspiciousIndentation")
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PhotoView(folderId:Int,navController: NavController) {

    val context = LocalContext.current.applicationContext
    val db = AppDatabase.getDatabase(context)

    val repo = FilesRepository(db.filesDao())
    val factory = FilesViewModelFactory(repo)
    val selectedImages = remember { mutableStateListOf<Files>() }
    val photoModel: FilesViewModel = viewModel(factory = factory)
    var selectedImageUris=photoModel.files
    var isSelectedImage by remember { mutableStateOf(false) }
    val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        Manifest.permission.READ_MEDIA_IMAGES
    } else {
        Manifest.permission.READ_EXTERNAL_STORAGE
    }

    val permissionState = rememberPermissionState(permission)
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.OpenMultipleDocuments()) { uris ->
        uris.let {
            it.forEach { uri ->
                context.contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                )


            }

            val filesWithTypes = it.map { uri ->
                val type = context.contentResolver.getType(uri) ?: "unknown"
                Pair(uri, type)
            }

            photoModel.addImages(folderId, filesWithTypes,context)
        }
    }
    LaunchedEffect(Unit) {
        photoModel.loadImages(folderId)
    }
    fun handleBrowseClick() {
        when {
            permissionState.status.isGranted -> {
                launcher.launch(arrayOf("image/*","application/pdf"))            }
            permissionState.status.shouldShowRationale -> {
                Toast.makeText(context, "Gallery permission is needed.", Toast.LENGTH_LONG).show()
                permissionState.launchPermissionRequest()
            }
            else -> {
                permissionState.launchPermissionRequest()
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        if (selectedImageUris.value.isEmpty()) {
            Card(
                modifier = Modifier
                    .align(Alignment.Center)
                    .clickable { handleBrowseClick() },
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(8.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier
                        .padding(32.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        painter = painterResource(R.drawable.uploadbigarrow),
                        contentDescription = "Upload Arrow",
                        modifier = Modifier
                            .size(80.dp)
                            .padding(bottom = 16.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Upload Photos From Here",
                        style = MaterialTheme.typography.titleMedium.copy(fontSize = 18.sp)
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(onClick = { handleBrowseClick() }) {
                        Text("Browse")
                    }
                }
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(selectedImageUris.value) { uri ->
                val isSelected=uri in selectedImages
                    if(uri.filePath.contains("image", ignoreCase = true)) {
                        AsyncImage(
                            model = uri.fileUri,
                            contentDescription = "Selected Image",
                            modifier = Modifier
                                .aspectRatio(1f)
                                .background(MaterialTheme.colorScheme.surface)
                                .pointerInput(Unit) {
                                    detectTapGestures(
                                        onTap = {
                                            if (selectedImages.isEmpty()) {
                                                navController.navigate(
                                                    "image_details_screen/${
                                                        Uri.encode(
                                                            uri.fileUri
                                                        )
                                                    }"
                                                )
                                            } else {
                                                selectedImages.add(uri)
                                            }
                                        },
                                        onLongPress = {
                                            if (isSelected) {
                                                selectedImages.remove(uri)
                                            } else {
                                                isSelectedImage = true
                                                selectedImages.add(uri)
                                            }
                                        }
                                    )
                                },
                            contentScale = ContentScale.Crop
                        )
                    }else if(uri.filePath.contains("pdf", ignoreCase = true)) {
                        val t=uri.fileName.substring(0,15)
                      Column() {
                          Icon(
                              painter = painterResource(R.drawable.pdf),
                              contentDescription = "PDF",
                              modifier = Modifier
                                  .size(80.dp)
                                  .pointerInput(Unit) {
                                      detectTapGestures(
                                          onTap = {
                                              if (selectedImages.isEmpty()) {
                                                  openPdfInExternalApp(context, uri.fileUri.toUri())
                                              } else {
                                                  selectedImages.add(uri)
                                              }
                                          },
                                          onLongPress = {
                                              if (isSelected) {
                                                  selectedImages.remove(uri)
                                              } else {
                                                  isSelectedImage = true
                                                  selectedImages.add(uri)
                                              }
                                          }
                                      )
                                  }
                          )
                          Text(
                              text = t+"...",
                              fontSize = 12.sp,
                              modifier = Modifier.padding(top = 4.dp)
                          )
                      }
                    }
                    if (isSelected) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Selected",
                            tint = Color.Green,
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(4.dp)
                                .size(24.dp)
                                .clickable{
                                    selectedImages.remove(uri)
                                }
                        )
                    }
                }

                item {
                    IconButton(
                        onClick = { handleBrowseClick() },
                        modifier = Modifier
                            .aspectRatio(1f)
                            .background(
                                color = MaterialTheme.colorScheme.primary,
                                shape = CircleShape
                            )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add More",
                            tint = Color.White
                        )
                    }
                }

            }
        }
        if (isSelectedImage && selectedImages.isNotEmpty()) {
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
                                selectedImages.let {
                                    for(x in selectedImages){
                                        photoModel.deleteImages(x)
                                    }
                                    isSelectedImage = false
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

fun openPdfInExternalApp(context: Context, pdfUri: Uri) {
    val intent = Intent(Intent.ACTION_VIEW).apply {
        setDataAndType(pdfUri, "application/pdf")
        flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or
                Intent.FLAG_ACTIVITY_NO_HISTORY or
                Intent.FLAG_ACTIVITY_NEW_TASK
    }

    val chooser = Intent.createChooser(intent, "Open PDF with").apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }

    try {
        context.startActivity(chooser)
    } catch (e: ActivityNotFoundException) {
        Toast.makeText(context, "No app found to open PDF$e", Toast.LENGTH_SHORT).show()
    }
}

fun getFileNameFromUri(context: Context, uri: Uri): String? {
    val cursor = context.contentResolver.query(uri, null, null, null, null)
    var name: String? = null

    cursor?.use {
        if (it.moveToFirst()) {
            val nameIndex = it.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
            if (nameIndex != -1) {
                name = it.getString(nameIndex)
            }
        }
    }
    return name
}