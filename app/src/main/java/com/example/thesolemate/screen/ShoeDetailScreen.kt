package com.example.thesolemate.screen

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.thesolemate.data.repository.ShoeRepository
import com.example.thesolemate.model.request.CartRequest
import com.example.thesolemate.model.response.ShoeResponse
import com.example.thesolemate.navigation.Screen
import com.example.thesolemate.repository.CartRepository
import kotlinx.coroutines.launch

@Composable
fun ShoeDetailScreen(
    shoeId: Int,
    navController: NavHostController,
    userId: Int,
    shoeRepository: ShoeRepository,
    cartRepository: CartRepository
) {
    var shoe by remember { mutableStateOf<ShoeResponse?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    LaunchedEffect(shoeId) {
        try {
            val response = shoeRepository.getShoeById(shoeId)
            if (response.isSuccessful) {
                shoe = response.body()
            } else {
                errorMessage = "Gagal mengambil data sepatu: ${response.code()}"
            }
        } catch (e: Exception) {
            errorMessage = "Terjadi kesalahan: ${e.message}"
        } finally {
            isLoading = false
        }
    }

    when {
        isLoading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        errorMessage != null -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = errorMessage ?: "Terjadi kesalahan",
                    color = MaterialTheme.colorScheme.error
                )
            }
        }

        shoe != null -> {
            val item = shoe!!

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                val imageUrl = item.image_url ?: ""
                Image(
                    painter = rememberAsyncImagePainter(imageUrl),
                    contentDescription = item.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(text = item.name, style = MaterialTheme.typography.headlineSmall)
                Text(text = "Brand: ${item.brand}", style = MaterialTheme.typography.bodyMedium)
                Text(text = "Gender: ${item.gender}", style = MaterialTheme.typography.bodyMedium)

                val parsedPrice = item.price
                Text(text = "Price: Rp${parsedPrice ?: "N/A"}", style = MaterialTheme.typography.bodyMedium)

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Deskripsi Produk",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                Text(
                    text = getDescriptionForShoe(item.brand, item.gender),
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Justify
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        scope.launch {
                            try {
                                val cartItem = CartRequest(
                                    user_id = userId,
                                    shoe_id = item.id,
                                    quantity = 1
                                )
                                val response = cartRepository.addToCart(cartItem)
                                if (response.isSuccessful) {
                                    val body = response.body()
                                    if (body?.success == true) {
                                        successMessage = body.message ?: "Berhasil ditambahkan"
                                        errorMessage = null
                                    } else {
                                        errorMessage = body?.message ?: "Gagal menambahkan"
                                        successMessage = null
                                    }
                                } else {
                                    errorMessage = "Gagal menambahkan: ${response.code()}"
                                    successMessage = null
                                }
                            } catch (e: Exception) {
                                errorMessage = "Terjadi kesalahan: ${e.message}"
                                successMessage = null
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Tambahkan ke Keranjang")
                }

                successMessage?.let {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = it, color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = {
                            navController.navigate(Screen.Cart.createRoute(userId))
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Lihat Keranjang")
                    }
                }

                errorMessage?.let {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = it, color = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}

// Deskripsi Dinamis Sepatu
fun getDescriptionForShoe(brand: String?, gender: String?): String {
    val brandName = brand ?: "Brand ternama"
    return when (gender?.lowercase()) {
        "men", "pria" -> "$brandName menghadirkan sepatu pria dengan desain maskulin dan kenyamanan maksimal. Cocok untuk aktivitas harian hingga olahraga ringan. Dilengkapi sol anti-slip dan material tahan lama."
        "women", "wanita" -> "$brandName mempersembahkan sepatu wanita yang memadukan gaya elegan dan kenyamanan. Sempurna untuk tampil percaya diri di setiap langkah, baik santai maupun formal."
        "unisex" -> "$brandName menghadirkan desain unisex yang trendi dan versatile. Pas digunakan oleh siapa saja, dalam berbagai suasana. Ringan, stylish, dan pastinya nyaman."
        else -> "$brandName menghadirkan kenyamanan dan desain modern untuk semua kalangan. Sepatu ini cocok untuk berbagai aktivitas, menjadikan setiap langkah lebih percaya diri."
    }
}
