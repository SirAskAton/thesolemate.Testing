package com.example.thesolemate.screen

import android.util.Log
import android.widget.Toast
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
import com.example.thesolemate.data.remote.ApiClient.apiService
import com.example.thesolemate.data.repository.ShoeRepository
import com.example.thesolemate.model.request.CartRequest
import com.example.thesolemate.model.request.UserIdRequest
import com.example.thesolemate.model.response.ShoeResponse
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

    LaunchedEffect(Unit) {
        try {
            val response = apiService.getShoeById(shoeId)
            if (response.isSuccessful) {
                shoe = response.body()
                isLoading = false
            } else {
                errorMessage = "Gagal mengambil data sepatu: ${response.code()}"
                isLoading = false
            }
        } catch (e: Exception) {
            errorMessage = "Terjadi kesalahan: ${e.message}"
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
                Image(
                    painter = rememberAsyncImagePainter(item.image_url),
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
                Text(text = "Price: Rp${item.price}", style = MaterialTheme.typography.bodyMedium)

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = item.description,
                    style = MaterialTheme.typography.bodyLarge,
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
                                        successMessage = body.message ?: "Berhasil menambahkan ke keranjang"
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
                                Log.e("Cart", "Error: ${e.message}")
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Tambahkan ke Keranjang")
                }

                successMessage?.let {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = {
                            navController.navigate("cart/$userId")
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Lihat Keranjang")
                    }
                }

                errorMessage?.let {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}
