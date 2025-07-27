package com.example.thesolemate.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.thesolemate.model.response.CartItemResponse
import com.example.thesolemate.navigation.Screen
import kotlinx.coroutines.delay

@Composable
fun ReceiptScreen(
    navController: NavController,
    userId: Int
) {
    val receiptItems = navController.previousBackStackEntry
        ?.savedStateHandle
        ?.get<ArrayList<CartItemResponse>>("receipt_items") ?: arrayListOf()

    val totalPrice = navController.previousBackStackEntry
        ?.savedStateHandle
        ?.get<Int>("total_price") ?: 0

    val alamatPengiriman = navController.previousBackStackEntry
        ?.savedStateHandle
        ?.get<String>("alamat_pengiriman") ?: "Alamat tidak tersedia"

    // Auto kembali ke Home dalam 2 menit
    LaunchedEffect(Unit) {
        delay(120_000L)
        navController.navigate(Screen.Home.route) {
            popUpTo("receipt_screen/$userId") { inclusive = true }
        }
    }

    // Background gambar
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Image(
            painter = rememberAsyncImagePainter("https://static.vecteezy.com/system/resources/thumbnails/020/567/748/small/abstract-gradient-colorful-background-photo.jpg"),
            contentDescription = "Background",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Overlay konten
        Scaffold(
            containerColor = Color.Transparent,
            content = { paddingValues ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(16.dp)
                ) {
                    // ✅ Ucapan Terima Kasih
                    Text(
                        text = buildAnnotatedString {
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                append("TERIMAKASIH SUDAH BERBELANJA DI THE SOLE MATE\n")
                            }
                            append("Pesanan anda akan segera dikirim")
                        },
                        style = MaterialTheme.typography.bodyLarge.copy(fontSize = 16.sp),
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 24.dp)
                    )

                    // ✅ Judul
                    Text(
                        text = "Struk Pembelian",
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(bottom = 12.dp)
                    )

                    // ✅ Daftar Produk
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    ) {
                        items(receiptItems) { item ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp),
                                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Image(
                                        painter = rememberAsyncImagePainter(item.image_url),
                                        contentDescription = item.shoe_name,
                                        modifier = Modifier
                                            .size(64.dp)
                                            .padding(end = 12.dp)
                                    )

                                    Column(
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text(item.shoe_name, style = MaterialTheme.typography.titleSmall)
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text("Harga: Rp${item.price}")
                                        Text("Jumlah: ${item.quantity}")
                                    }
                                }
                            }
                        }
                    }

                    // ✅ Total Bayar
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Total Bayar:",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.align(Alignment.End)
                    )
                    Text(
                        text = "Rp$totalPrice",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        ),
                        modifier = Modifier
                            .align(Alignment.End)
                            .padding(bottom = 12.dp)
                    )

                    // ✅ Alamat Pengiriman
                    Text(
                        text = "Alamat Pengiriman:",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    Text(
                        text = alamatPengiriman,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 24.dp)
                    )

                    Button(
                        onClick = {
                            navController.navigate(Screen.Home.route) {
                                popUpTo(0)
                                launchSingleTop = true
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                    ) {
                        Text("Kembali ke Menu", fontSize = 16.sp)
                    }
                }
            }
        )
    }
}
