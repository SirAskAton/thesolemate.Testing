package com.example.thesolemate.screen

import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.ShoppingCartCheckout
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.ImageLoader
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.thesolemate.R
import com.example.thesolemate.data.repository.ShoeRepository
import com.example.thesolemate.model.request.CartRequest
import com.example.thesolemate.model.response.ShoeResponse
import com.example.thesolemate.navigation.Screen
import com.example.thesolemate.repository.CartRepository
import com.example.thesolemate.session.SessionManager
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient

@Composable
fun DropdownMenuFilter(
    label: String,
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit // // Fungsi callback saat user memilih opsi
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        OutlinedButton(
            onClick = { expanded = true },
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = Color.White,
                contentColor = Color.Black
            )
        ) {
            Text("$label: $selectedOption")
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(Color.White)
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = {
                        Text(option, color = Color.Black)
                    },
                    onClick = {
                        onOptionSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun ShoeCard(
    shoe: ShoeResponse,
    userId: Int,
    navController: NavController,
    cartRepository: CartRepository,
    context: android.content.Context,
    onCartChanged: () -> Unit
) {
    val scope = rememberCoroutineScope()
    var isAdding by remember { mutableStateOf(false) }

    val imageLoader = ImageLoader.Builder(context)
        .crossfade(true)
        .okHttpClient {
            OkHttpClient.Builder()
                .addInterceptor { chain ->
                    val newRequest = chain.request().newBuilder()
                        .header("User-Agent", "Mozilla/5.0")
                        .build()
                    chain.proceed(newRequest)
                }
                .build()
        }
        .build()

    val imagePainter = rememberAsyncImagePainter(
        model = ImageRequest.Builder(context)
            .data(shoe.image_url)
            .crossfade(true)
            .placeholder(R.drawable.missingitem)
            .error(R.drawable.missingitem)
            .build(),
        imageLoader = imageLoader
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                navController.navigate(Screen.ShoeDetail.createRoute(shoe.id, userId))
            },
        elevation = CardDefaults.cardElevation(4.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = imagePainter,
                contentDescription = shoe.name,
                modifier = Modifier
                    .height(120.dp)
                    .fillMaxWidth()
                    .padding(8.dp),
                contentScale = ContentScale.Crop
            )

            Text(shoe.name, style = MaterialTheme.typography.titleMedium)
            Text("Brand: ${shoe.brand}", style = MaterialTheme.typography.bodySmall)
            Text("Gender: ${shoe.gender}", style = MaterialTheme.typography.bodySmall)
            Text("Rp${shoe.price}", style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(6.dp))

            Button(
                onClick = {
                    scope.launch {
                        isAdding = true
                        try {
                            val request = CartRequest(user_id = userId, shoe_id = shoe.id, quantity = 1)
                            val response = cartRepository.addToCart(request)
                            if (response.isSuccessful) {
                                Toast.makeText(context, "Ditambahkan ke keranjang", Toast.LENGTH_SHORT).show()  // Jika berhasil ditambahkan ke keranjang
                                onCartChanged()
                            } else {
                                Toast.makeText(context, "Gagal menambahkan", Toast.LENGTH_SHORT).show()  // Jika gagal karena alasan dari server (misalnya status 400, 500, dll)
                            }
                        } catch (e: Exception) {
                            Toast.makeText(context, "Terjadi kesalahan", Toast.LENGTH_SHORT).show() //// Menangani semua jenis eror Bisa karena null pointer, JSON error, server error yang tidak ditangani, dsb.
                        } finally {
                            isAdding = false
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isAdding
            ) {
                if (isAdding) {
                    CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                } else {
                    Text("Tambah")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    navController: NavController,
    shoeRepository: ShoeRepository,
    cartRepository: CartRepository,
    user_id: Int
) {
    val context = LocalContext.current
    val session = remember { SessionManager(context) }
    val scope = rememberCoroutineScope()
    val userId = session.getUserId()

    var shoes by remember { mutableStateOf<List<ShoeResponse>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    var cartItemCount by remember { mutableStateOf(0) }

    var selectedBrand by remember { mutableStateOf("Semua") }
    var selectedGender by remember { mutableStateOf("Semua") }

    val brandOptions = listOf("Semua", "Nike", "Adidas", "Puma")
    val genderOptions = listOf("Semua", "Male", "Female", "Unisex")

    fun checkCart() {
        scope.launch {
            try {
                val cartItems = cartRepository.getCart(userId)
                cartItemCount = cartItems.size
            } catch (e: Exception) {
                cartItemCount = 0
            }
        }
    }

    fun filterShoes(list: List<ShoeResponse>): List<ShoeResponse> {
        return list.filter {
            (selectedBrand == "Semua" || it.brand.equals(selectedBrand, true)) &&
                    (selectedGender == "Semua" || it.gender.equals(selectedGender, true))
        }
    }

    LaunchedEffect(true) {
        try {
            val response = shoeRepository.getShoes()
            if (response.isSuccessful) {
                shoes = response.body() ?: emptyList()
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Gagal memuat sepatu", Toast.LENGTH_SHORT).show() // // Jika gagal karena error saat mengambil data dari server (HTTP error, tidak ada koneksi
        } finally {
            // Pastikan loading berhenti walaupun terjadi error
            loading = false
            checkCart()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = rememberAsyncImagePainter("https://static.vecteezy.com/system/resources/thumbnails/020/567/748/small/abstract-gradient-colorful-background-photo.jpg"),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.15f)))

        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = { Text("TheSoleMate", style = MaterialTheme.typography.titleLarge) },
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
            if (loading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                Column(
                    modifier = Modifier.padding(padding).padding(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        DropdownMenuFilter("Brand", brandOptions, selectedBrand) { selectedBrand = it }
                        DropdownMenuFilter("Gender", genderOptions, selectedGender) { selectedGender = it }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    val filteredShoes = filterShoes(shoes)

                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(bottom = 80.dp)
                    ) {
                        items(filteredShoes) { shoe ->
                            ShoeCard(shoe, userId, navController, cartRepository, context) {
                                checkCart()
                            }
                        }
                    }
                }
            }
        }
    }
}
