package com.example.thesolemate.screen

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.thesolemate.data.remote.ApiClient
import com.example.thesolemate.model.response.CartItemResponse
import com.example.thesolemate.repository.CartRepository
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReceiptScreen(navController: NavController, userId: Int) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val cartRepo = remember { CartRepository(ApiClient.apiService) }

    var cartItems by remember { mutableStateOf<List<CartItemResponse>>(emptyList()) }
    var totalPrice by remember { mutableStateOf(0) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(true) {
        scope.launch {
            try {
                val items = cartRepo.getCart(userId)
                cartItems = items
                totalPrice = items.sumOf { it.quantity * it.price }

                val checkoutResponse = cartRepo.checkout(userId)
                if (checkoutResponse.isSuccessful) {
                    Toast.makeText(context, "Checkout berhasil!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Checkout gagal", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                isLoading = false
            }
        }
    }

    if (isLoading) {
        CircularProgressIndicator()
        return
    }

    Column(Modifier.padding(16.dp)) {
        Text("Receipt", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(8.dp))

        cartItems.forEach {
            Text("${it.name} - ${it.quantity} x ${it.price}")
        }

        Spacer(Modifier.height(16.dp))
        Text("Total: Rp $totalPrice")
    }
}
