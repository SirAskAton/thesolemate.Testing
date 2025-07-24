package com.example.thesolemate.screen

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.thesolemate.data.repository.CartRepository
import com.example.thesolemate.model.response.CartResponse
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    navController: NavController,
    cartRepository: CartRepository
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var cartItems by remember { mutableStateOf<List<CartResponse>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }

    fun loadCart() {
        scope.launch {
            loading = true
            try {
                val response = cartRepository.getCartItems()
                if (response.isSuccessful) {
                    cartItems = response.body() ?: emptyList()
                } else {
                    Toast.makeText(context, "Gagal memuat keranjang", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Terjadi kesalahan saat memuat", Toast.LENGTH_SHORT).show()
            } finally {
                loading = false
            }
        }
    }

    fun updateQuantity(item: CartResponse, newQty: Int) {
        scope.launch {
            try {
                cartRepository.updateCartItem(item.id, newQty)
                loadCart()
            } catch (e: Exception) {
                Toast.makeText(context, "Gagal mengubah jumlah", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun deleteItem(itemId: Int) {
        scope.launch {
            try {
                cartRepository.deleteCartItem(itemId)
                loadCart()
                Toast.makeText(context, "Item dihapus", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(context, "Gagal menghapus item", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun clearCart() {
        scope.launch {
            try {
                cartItems.forEach { cartRepository.deleteCartItem(it.id) }
                loadCart()
                Toast.makeText(context, "Checkout berhasil!", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(context, "Gagal checkout", Toast.LENGTH_SHORT).show()
            }
        }
    }

    val totalHarga = cartItems.sumOf { it.price * it.quantity }

    LaunchedEffect(Unit) {
        loadCart()
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Keranjang Belanja") })
        },
        bottomBar = {
            if (cartItems.isNotEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text("Total: Rp$totalHarga", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = { clearCart() },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Checkout")
                    }
                }
            }
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
        } else if (cartItems.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("Keranjang kosong")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                items(cartItems) { item ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(8.dp)
                        ) {
                            Image(
                                painter = rememberAsyncImagePainter(item.imageUrl),
                                contentDescription = item.name,
                                modifier = Modifier
                                    .size(80.dp)
                                    .padding(end = 8.dp)
                            )

                            Column(modifier = Modifier.weight(1f)) {
                                Text(item.name ?: "-", style = MaterialTheme.typography.titleMedium)
                                Text("Brand: ${item.brand}", style = MaterialTheme.typography.bodySmall)
                                Text("Harga satuan: Rp${item.price}", style = MaterialTheme.typography.bodySmall)
                                Text("Subtotal: Rp${item.price * item.quantity}", style = MaterialTheme.typography.bodyMedium)

                                Spacer(modifier = Modifier.height(4.dp))

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    IconButton(
                                        onClick = {
                                            if (item.quantity > 1) {
                                                updateQuantity(item, item.quantity - 1)
                                            }
                                        }
                                    ) {
                                        Icon(Icons.Default.Remove, contentDescription = "Kurangi")
                                    }

                                    Text("${item.quantity}", style = MaterialTheme.typography.bodyMedium)

                                    IconButton(
                                        onClick = {
                                            updateQuantity(item, item.quantity + 1)
                                        }
                                    ) {
                                        Icon(Icons.Default.Add, contentDescription = "Tambah")
                                    }
                                }
                            }

                            IconButton(onClick = { deleteItem(item.id) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Hapus")
                            }
                        }
                    }
                }
            }
        }
    }
}
