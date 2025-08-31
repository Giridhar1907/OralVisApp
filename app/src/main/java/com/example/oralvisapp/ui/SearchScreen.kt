package com.example.oralvisapp.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(viewModel: SessionViewModel) {
    val searchResults by viewModel.searchResults.collectAsStateWithLifecycle()
    val searchImages by viewModel.searchImages.collectAsStateWithLifecycle()
    val context = LocalContext.current

    var searchQuery by remember { mutableStateOf("") }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var selectedImage by remember { mutableStateOf<File?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Search Sessions",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold
            ),
            modifier = Modifier.align(Alignment.CenterHorizontally),
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Search Input
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Session ID") },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(8.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = {
                if (searchQuery.isNotBlank()) {
                        viewModel.searchSession(searchQuery)
                    }
                },
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Search")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
            viewModel.clearSearch()
                searchQuery = ""
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.outlinedButtonColors(),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text("Clear Results")
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Search Results
        searchResults?.let { session ->
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Session Details",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold
                            )
                        )

                        IconButton(
                            onClick = { showDeleteDialog = true },
                            colors = IconButtonDefaults.iconButtonColors(
                                contentColor = MaterialTheme.colorScheme.error
                            )
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete Session")
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        "Session ID: ${session.sessionId}",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text("Name: ${session.name}", style = MaterialTheme.typography.bodyLarge)
                    Text("Age: ${session.age}", style = MaterialTheme.typography.bodyLarge)
                    Text(
                        "Images: ${session.imageCount}",
                        style = MaterialTheme.typography.bodyLarge
                    )

                    val dateFormat = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
                    Text(
                        "Date: ${dateFormat.format(Date(session.timestamp))}",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (searchImages.isNotEmpty()) {
                Text(
                    text = "Images (${searchImages.size})",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                LazyColumn {
                    items(searchImages) { imageFile ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable { selectedImage = imageFile },
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                AsyncImage(
                                    model = ImageRequest.Builder(context)
                                        .data(imageFile)
                                        .build(),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(60.dp)
                                        .padding(end = 12.dp),
                                    contentScale = ContentScale.Crop
                                )

                                Column {
                                    Text(
                                        text = imageFile.name,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = FontWeight.Medium
                                        )
                                    )
                                    Text(
                                        text = "Size: ${imageFile.length() / 1024} KB",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = "Tap to view full size",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    }
                }
            }
        } ?: run {
            if (searchQuery.isNotBlank()) {
                Text(
                    text = "No session found with ID: $searchQuery",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }

    // Delete Confirmation Dialog
    if (showDeleteDialog && searchResults != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = {
                Text(
                    "Delete Session",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
            },
            text = {
                Text("Are you sure you want to delete session ${searchResults!!.sessionId}? This will permanently delete all images and cannot be undone.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteSession(searchResults!!)
                        showDeleteDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Image Viewer Dialog
    selectedImage?.let { image ->
        AlertDialog(
            onDismissRequest = { selectedImage = null },
            title = { Text(image.name) },
            text = {
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(image)
                        .build(),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 400.dp),
                    contentScale = ContentScale.Fit
                )
            },
            confirmButton = {
                TextButton(onClick = { selectedImage = null }) {
                    Text("Close")
                }
            }
        )
    }
}