package com.example.documentvault.screens

import android.Manifest
import android.net.Uri
import android.os.Build
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
import com.example.documentvault.models.Images
import com.example.documentvault.repository.ImageRepository
import com.example.documentvault.viewModels.PhotoViewModelFactory
import com.example.documentvault.viewModels.PhotosViewModel
import com.google.accompanist.permissions.*

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PhotoView(folderId:Int,navController: NavController) {

    val context = LocalContext.current.applicationContext
    val db = AppDatabase.getDatabase(context)

    val repo = ImageRepository(db.imagesDao())
    val factory = PhotoViewModelFactory(repo)
    val selectedImages = remember { mutableStateListOf<Images>() }
    val photoModel: PhotosViewModel = viewModel(factory = factory)
    var selectedImageUris=photoModel.images
    var isSelectedImage by remember { mutableStateOf(false) }
    val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        Manifest.permission.READ_MEDIA_IMAGES
    } else {
        Manifest.permission.READ_EXTERNAL_STORAGE
    }

    val permissionState = rememberPermissionState(permission)

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris: List<Uri> ->
        photoModel.addImages(folderId = folderId, uris = uris)
    }
    LaunchedEffect(Unit) {
        photoModel.loadImages(folderId)
    }
    fun handleBrowseClick() {
        when {
            permissionState.status.isGranted -> {
                imagePickerLauncher.launch("image/*")
            }
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
                    AsyncImage(
                        model = uri.imageUri,
                        contentDescription = "Selected Image",
                        modifier = Modifier
                            .aspectRatio(1f)
                            .background(MaterialTheme.colorScheme.surface)
                            .pointerInput(Unit){
                                detectTapGestures(
                                    onTap = {
                                        if(selectedImages.isEmpty()){
                                            navController.navigate("details_screen/${Uri.encode(uri.toString())}")
                                        }else{
                                            selectedImages.add(uri)
                                        }

                                    },
                                    onLongPress = {
                                        if(isSelected){
                                            selectedImages.remove(uri)
                                        }else{
                                            isSelectedImage=true
                                                selectedImages.add(uri)
                                        }
                                    }
                                )
                            },
                        contentScale = ContentScale.Crop
                    )
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
        if (isSelectedImage) {
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