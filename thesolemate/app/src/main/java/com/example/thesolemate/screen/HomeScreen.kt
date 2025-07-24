package com.example.thesolemate.screen

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
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
import com.example.thesolemate.data.remote.ApiClient
import com.example.thesolemate.data.repository.CartRepository
import com.example.thesolemate.data.repository.ShoeRepository
import com.example.thesolemate.model.response.ShoeResponse
import com.example.thesolemate.navigation.Screen
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    shoeRepository: ShoeRepository,
    cartRepository: CartRepository
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var shoes by remember { mutableStateOf<List<ShoeResponse>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }

    LaunchedEffect(true) {
        try {
            val response = shoeRepository.getShoes()
            if (response.isSuccessful) {
                shoes = response.body() ?: emptyList()
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Gagal memuat sepatu", Toast.LENGTH_SHORT).show()
        } finally {
            loading = false
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("TheSoleMate") },
            actions = {
                IconButton(onClick = { navController.navigate(Screen.Cart.route) }) {
                    Icon(Icons.Default.ShoppingCart, contentDescription = "Cart")
                }
            }
        )

        if (loading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp)
            ) {
                items(shoes.chunked(2)) { rowItems ->
                    Row(modifier = Modifier.fillMaxWidth()) {
                        for (shoe in rowItems) {
                            Card(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(8.dp)
                                    .clickable {
                                        navController.navigate(Screen.ShoeDetail.createRoute(shoe.id))
                                    },
                                elevation = CardDefaults.cardElevation(4.dp)
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.padding(8.dp)
                                ) {
                                    Image(
                                        painter = rememberAsyncImagePainter(
                                            ImageRequest.Builder(context)
                                                .data(shoe.imageUrl)
                                                .crossfade(true)
                                                .error(R.drawable.missingitem)
                                                .placeholder(R.drawable.missingitem)
                                                .build()
                                        ),
                                        contentDescription = shoe.name,
                                        modifier = Modifier
                                            .size(100.dp)
                                            .padding(8.dp)
                                    )
                                    Text(shoe.name, style = MaterialTheme.typography.titleMedium)
                                    Text("Brand: ${shoe.brand}", style = MaterialTheme.typography.bodySmall)
                                    Text("Rp${shoe.price}", style = MaterialTheme.typography.bodyMedium)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Button(
                                        onClick = {
                                            scope.launch {
                                                try {
                                                    val result = cartRepository.addToCart(shoe.id, 1)
                                                    Toast.makeText(
                                                        context,
                                                        if (result) "Ditambahkan ke keranjang" else "Gagal menambahkan",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                } catch (e: Exception) {
                                                    Toast.makeText(context, "Terjadi kesalahan", Toast.LENGTH_SHORT).show()
                                                }
                                            }
                                        },
                                        modifier = Modifier.padding(4.dp)
                                    ) {
                                        Text("Tambah")
                                    }
                                }
                            }
                        }
                        if (rowItems.size < 2) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}
