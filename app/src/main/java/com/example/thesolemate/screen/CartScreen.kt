package com.example.thesolemate.screen

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.thesolemate.data.remote.ApiClient
import com.example.thesolemate.model.request.DeleteCartRequest
import com.example.thesolemate.model.request.UpdateCartRequest
import com.example.thesolemate.model.request.UserIdRequest
import com.example.thesolemate.model.response.CartItemResponse
import com.example.thesolemate.session.SessionManager
import kotlinx.coroutines.launch

@Composable
fun CartScreen(navController: NavController) {
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    val userId = sessionManager.getUserId()

    val scope = rememberCoroutineScope()
    var cartItems by remember { mutableStateOf<List<CartItemResponse>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var isCheckoutLoading by remember { mutableStateOf(false) }
    var address by remember { mutableStateOf("") }

    val totalHarga = cartItems.sumOf { it.price * it.quantity }

    fun loadCart() {
        scope.launch {
            try {
                val response = ApiClient.apiService.getCart(UserIdRequest(userId))
                if (response.isSuccessful) {
                    cartItems = response.body()?.data ?: emptyList()
                } else {
                    Toast.makeText(context, "Gagal memuat cart", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Terjadi kesalahan", Toast.LENGTH_SHORT).show()
            } finally {
                isLoading = false
            }
        }
    }

    fun updateQuantity(cartId: Int, newQuantity: Int) {
        scope.launch {
            try {
                val response = ApiClient.apiService.updateCartQuantity(
                    UpdateCartRequest(cart_id = cartId, quantity = newQuantity)
                )
                if (response.isSuccessful && response.body()?.success == true) {
                    loadCart()
                } else {
                    Toast.makeText(context, "Gagal update jumlah", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Error update qty", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun deleteCartItem(cartId: Int) {
        scope.launch {
            try {
                val response = ApiClient.apiService.deleteCartItem(
                    DeleteCartRequest(cart_id = cartId)
                )
                if (response.isSuccessful && response.body()?.success == true) {
                    loadCart()
                    Toast.makeText(context, "Item berhasil dihapus", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Gagal menghapus item", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Terjadi error saat hapus item", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun performCheckout() {
        if (address.isBlank()) {
            Toast.makeText(context, "Alamat pengiriman wajib diisi", Toast.LENGTH_SHORT).show()
            return
        }

        scope.launch {
            isCheckoutLoading = true
            try {
                val response = ApiClient.apiService.checkout(mapOf("user_id" to userId))
                if (response.isSuccessful && response.body()?.success == true) {
                    navController.currentBackStackEntry?.savedStateHandle?.set("receipt_items", cartItems)
                    navController.currentBackStackEntry?.savedStateHandle?.set("total_price", totalHarga)
                    navController.currentBackStackEntry?.savedStateHandle?.set("alamat_pengiriman", address)

                    cartItems = emptyList()

                    Toast.makeText(context, "Checkout berhasil!", Toast.LENGTH_SHORT).show()
                    navController.navigate("receipt_screen/$userId")
                } else {
                    Toast.makeText(context, response.body()?.message ?: "Gagal checkout", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Terjadi kesalahan saat checkout", Toast.LENGTH_SHORT).show()
            } finally {
                isCheckoutLoading = false
            }
        }
    }

    LaunchedEffect(Unit) {
        loadCart()
    }

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        Box(modifier = Modifier.fillMaxSize()) {
            AsyncImage(
                model = "https://static.vecteezy.com/system/resources/thumbnails/020/567/748/small/abstract-gradient-colorful-background-photo.jpg",
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
            )

            Column(modifier = Modifier.fillMaxSize()) {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .padding(16.dp)
                ) {
                    items(cartItems) { item ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            elevation = CardDefaults.cardElevation(4.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                AsyncImage(
                                    model = item.image_url,
                                    contentDescription = item.shoe_name,
                                    modifier = Modifier.size(80.dp)
                                )

                                Spacer(modifier = Modifier.width(16.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(item.shoe_name ?: "Tanpa Nama")
                                    Text("Harga: Rp${item.price}")
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text("Jumlah: ")
                                        IconButton(onClick = {
                                            if (item.quantity > 1) {
                                                updateQuantity(item.cart_id, item.quantity - 1)
                                            }
                                        }) {
                                            Text("-")
                                        }
                                        Text("${item.quantity}")
                                        IconButton(onClick = {
                                            updateQuantity(item.cart_id, item.quantity + 1)
                                        }) {
                                            Text("+")
                                        }
                                    }
                                }

                                IconButton(onClick = {
                                    deleteCartItem(item.cart_id)
                                }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Hapus")
                                }
                            }
                        }
                    }
                }

                if (cartItems.isNotEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        OutlinedTextField(
                            value = address,
                            onValueChange = { address = it },
                            label = { Text("Alamat Pengiriman") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        Text("Total: Rp$totalHarga", style = MaterialTheme.typography.titleLarge)
                        Spacer(modifier = Modifier.height(8.dp))

                        Button(
                            onClick = { performCheckout() },
                            enabled = !isCheckoutLoading,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            if (isCheckoutLoading) {
                                CircularProgressIndicator(
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Memproses...")
                            } else {
                                Text("Checkout")
                            }
                        }
                    }
                } else {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Keranjang kosong", style = MaterialTheme.typography.bodyLarge)
                    }
                }
            }
        }
    }
}
