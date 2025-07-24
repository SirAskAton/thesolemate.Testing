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
import com.example.thesolemate.data.repository.CartRepository
import com.example.thesolemate.model.response.CartItem
import com.example.thesolemate.model.response.CheckoutResponse
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReceiptScreen(navController: NavController) {
    val cartRepo = remember { CartRepository(ApiClient.apiService) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    var receiptItems by remember { mutableStateOf<List<CartItem>>(emptyList()) }
    var totalPrice by remember { mutableStateOf(0.0) }
    var loading by remember { mutableStateOf(true) }

    LaunchedEffect(true) {
        scope.launch {
            try {
                val response = cartRepo.checkout() // ✅ Panggil fungsi checkout() dari repository
                if (response.isSuccessful) {
                    val data = response.body()
                    receiptItems = data?.items ?: emptyList()
                    totalPrice = data?.totalPrice ?: 0.0
                } else {
                    Toast.makeText(context, "Gagal memuat struk", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Terjadi kesalahan: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                loading = false
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Struk Pembayaran") }
            )
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
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .padding(padding)
            ) {
                Text("Detail Pembelian:", style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(8.dp))

                LazyColumn {
                    items(receiptItems) { item ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("Nama: ${item.shoe.name}")
                                Text("Jumlah: ${item.quantity}")
                                Text("Harga: Rp${item.shoe.price}")
                                Text("Subtotal: Rp${item.quantity * item.shoe.price}")
                            }
                        }
                    }
                }

                Divider(modifier = Modifier.padding(vertical = 16.dp))

                Text("Total Bayar: Rp$totalPrice", style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { navController.navigate("home") },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Kembali ke Beranda")
                }
            }
        }
    }
}
