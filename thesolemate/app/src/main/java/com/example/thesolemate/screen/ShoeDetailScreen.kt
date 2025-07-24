package com.example.thesolemate.screen

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.thesolemate.R
import com.example.thesolemate.data.repository.CartRepository
import com.example.thesolemate.data.repository.ShoeRepository
import com.example.thesolemate.model.response.ShoeResponse
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShoeDetailScreen(
    navController: NavController,
    shoeId: Int,
    shoeRepository: ShoeRepository,
    cartRepository: CartRepository
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var shoe by remember { mutableStateOf<ShoeResponse?>(null) }
    var loading by remember { mutableStateOf(true) }

    LaunchedEffect(shoeId) {
        val result = shoeRepository.getShoeById(shoeId)
        if (result.isSuccessful) {
            shoe = result.body()
        }
        loading = false
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Detail Sepatu") })
        }
    ) { padding ->
        if (loading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (shoe == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("Data tidak ditemukan")
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .padding(padding),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = rememberAsyncImagePainter(
                        ImageRequest.Builder(context)
                            .data(shoe!!.imageUrl)
                            .crossfade(true)
                            .error(R.drawable.missingitem)
                            .placeholder(R.drawable.missingitem)
                            .build()
                    ),
                    contentDescription = shoe!!.name,
                    modifier = Modifier
                        .height(200.dp)
                        .padding(8.dp)
                )

                Text(shoe!!.name, style = MaterialTheme.typography.headlineSmall)
                Text("Brand: ${shoe!!.brand}", style = MaterialTheme.typography.bodyMedium)
                Text("Rp${shoe!!.price}", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Text(shoe!!.description ?: "Tidak ada deskripsi", style = MaterialTheme.typography.bodySmall)

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        scope.launch {
                            try {
                                val success = cartRepository.addToCart(shoe!!.id, 1)
                                if (success) {
                                    Toast.makeText(context, "Ditambahkan ke keranjang", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(context, "Gagal menambahkan", Toast.LENGTH_SHORT).show()
                                }
                            } catch (e: Exception) {
                                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Tambah ke Keranjang")
                }
            }
        }
    }
}
