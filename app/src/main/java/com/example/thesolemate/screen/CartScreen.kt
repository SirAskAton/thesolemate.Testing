package com.example.thesolemate.screen

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.thesolemate.data.remote.ApiClient
import com.example.thesolemate.repository.CartRepository
import com.example.thesolemate.model.response.CartItemResponse
import kotlinx.coroutines.launch

@Composable
fun CartScreen(navController: NavController, userId: Int) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val cartRepo = remember { CartRepository(ApiClient.apiService) }

    var cartItems by remember { mutableStateOf<List<CartItemResponse>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(true) {
        scope.launch {
            try {
                val response = cartRepo.getCart(userId)
                cartItems = response
            } catch (e: Exception) {
                Log.e("CartScreen", "Error loading cart: ${e.message}")
                Toast.makeText(context, "Gagal memuat cart: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                isLoading = false
            }
        }
    }

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            items(cartItems) { item ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Row(modifier = Modifier.padding(16.dp)) {
                        AsyncImage(
                            model = item.image,
                            contentDescription = item.name,
                            modifier = Modifier
                                .size(80.dp)
                                .padding(end = 16.dp)
                        )
                        Column {
                            Text(item.name, style = MaterialTheme.typography.titleMedium)
                            Text("Harga: Rp${item.price}")
                            Text("Jumlah: ${item.quantity}")
                        }
                    }
                }
            }
        }
    }
}
