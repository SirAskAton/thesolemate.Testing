package com.example.thesolemate.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.ShoppingCartCheckout
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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
fun GradientBackgroundBox(content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFB61717),
                        Color(0xFF4F0A1C)
                    )
                )
            )
    ) {
        content()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShoeDetailScreen(
    shoeId: Int, // ID sepatu yang ingin ditampilkan detailnya
    navController: NavHostController,
    userId: Int, //ID user yang login, digunakan untuk menambahkan ke keranjang
    shoeRepository: ShoeRepository, //mengambil detail sepatu dari API
    cartRepository: CartRepository
) {
    var shoe by remember { mutableStateOf<ShoeResponse?>(null) } // Menyimpan data sepatu dari API
    var isLoading by remember { mutableStateOf(true) }           // Indikator loading saat data dimuat
    var errorMessage by remember { mutableStateOf<String?>(null) } // Menyimpan pesan error jika terjadi
    var successMessage by remember { mutableStateOf<String?>(null) } // Menyimpan pesan sukses saat tambah ke keranjang
    var cartItemCount by remember { mutableStateOf(0) }          // Jumlah item dalam keranjang

    val scope = rememberCoroutineScope() // Coroutine untuk panggilan async API

    fun checkCart() {
        scope.launch {
            try {
                //Memanggil API untuk mendapatkan detail sepatu berdasarkan shoeId
                val cartItems = cartRepository.getCart(userId)
                cartItemCount = cartItems.size
            } catch (_: Exception) {
                cartItemCount = 0
            }
        }
    }

    LaunchedEffect(shoeId) {
        try {
            val response = shoeRepository.getShoeById(shoeId)
            if (response.isSuccessful) {
                shoe = response.body()
            } else {
                errorMessage = "Gagal mengambil data: ${response.code()}"
            }
        } catch (e: Exception) {
            errorMessage = "Terjadi kesalahan: ${e.message}"
        } finally {
            isLoading = false
            checkCart()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Background image
        Image(
            painter = rememberAsyncImagePainter("https://static.vecteezy.com/system/resources/thumbnails/020/567/748/small/abstract-gradient-colorful-background-photo.jpg"),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = { Text("Detail Sepatu", style = MaterialTheme.typography.titleLarge) },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent,
                        titleContentColor = Color.White,
                        actionIconContentColor = Color.White
                    ),
                    actions = {
                        IconButton(onClick = {
                            navController.navigate(Screen.Cart.createRoute(userId))
                        }) {
                            Icon(
                                imageVector = if (cartItemCount > 0)
                                    Icons.Filled.ShoppingCartCheckout
                                else
                                    Icons.Filled.ShoppingCart,
                                contentDescription = "Keranjang"
                            )
                        }
                    }
                )
            }
        ) { padding ->
            when {
                isLoading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                errorMessage != null -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding),
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
                            .padding(padding)
                            .padding(16.dp)
                    ) {
                        Image(
                            painter = rememberAsyncImagePainter(item.image_url),
                            contentDescription = item.name,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            contentScale = ContentScale.Fit
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            elevation = CardDefaults.cardElevation(4.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(item.name, style = MaterialTheme.typography.headlineSmall)
                                Spacer(modifier = Modifier.height(6.dp))
                                Text("Brand: ${item.brand}", style = MaterialTheme.typography.bodyMedium)
                                Text("Gender: ${item.gender}", style = MaterialTheme.typography.bodyMedium)
                                Text("Harga: Rp${item.price}", style = MaterialTheme.typography.titleMedium)
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Text("Deskripsi Produk", style = MaterialTheme.typography.titleMedium, color = Color.White)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = getDescriptionForShoe(item.brand, item.gender),
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Justify,
                            color = Color.White
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        Button(
                            onClick = {
                                scope.launch {
                                    try {
                                        //Membuat request ke API untuk menambahkan sepatu ke keranjang
                                        val cartItem = CartRequest(user_id = userId, shoe_id = item.id, quantity = 1)
                                        val response = cartRepository.addToCart(cartItem)
                                        if (response.isSuccessful) {
                                            val body = response.body()
                                            if (body?.success == true) {
                                                successMessage = body.message ?: "Berhasil ditambahkan"
                                                errorMessage = null
                                                checkCart()
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
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                        ) {
                            Text("Tambahkan ke Keranjang")
                        }

                        successMessage?.let {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(it, color = MaterialTheme.colorScheme.primary)
                        }
                        errorMessage?.let {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(it, color = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            }
        }
    }
}


fun getDescriptionForShoe(brand: String?, gender: String?): String {
    val brandName = brand ?: "Brand ternama"
    return when (gender?.lowercase()) {
        "men", "pria" -> "$brandName menghadirkan sepatu pria dengan desain maskulin dan kenyamanan maksimal. Cocok untuk aktivitas harian hingga olahraga ringan."
        "women", "wanita" -> "$brandName mempersembahkan sepatu wanita yang memadukan gaya elegan dan kenyamanan. Sempurna untuk tampil percaya diri di setiap langkah."
        "unisex" -> "$brandName menghadirkan desain unisex yang trendi dan versatile. Ringan, stylish, dan cocok digunakan dalam berbagai suasana."
        else -> "$brandName menghadirkan kenyamanan dan desain modern untuk semua kalangan. Sepatu ini cocok untuk berbagai aktivitas harian."
    }
}
