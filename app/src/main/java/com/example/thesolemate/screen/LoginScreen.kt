package com.example.thesolemate.screen

// Import yang dibutuhkan
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.thesolemate.R
import com.example.thesolemate.data.remote.ApiClient // Retrofit client untuk API
import com.example.thesolemate.model.request.LoginRequest // Model untuk data login
import com.example.thesolemate.navigation.Screen // Untuk navigasi antar screen
import com.example.thesolemate.session.SessionManager // Menyimpan session login (user ID)
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(navController: NavHostController) {
    // State untuk menyimpan input user
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // State untuk menandai apakah input error
    var usernameError by remember { mutableStateOf(false) }
    var passwordError by remember { mutableStateOf(false) }

    // State untuk loading dialog
    var isLoading by remember { mutableStateOf(false) }

    // Untuk mengatur fokus input field
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current

    // SessionManager untuk menyimpan user ID ke SharedPreferences
    val sessionManager = remember { SessionManager(context) }

    // Coroutine untuk melakukan request API secara async
    val coroutineScope = rememberCoroutineScope()

    Box(modifier = Modifier.fillMaxSize()) {
        // Gambar background
        Image(
            painter = painterResource(id = R.drawable.gambar1),
            contentDescription = "Background",
            contentScale = ContentScale.Crop,
            modifier = Modifier.matchParentSize()
        )

        // Kolom isi layar login
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(80.dp)) // Jarak dari atas

            // Logo aplikasi
            Image(
                painter = painterResource(id = R.drawable.logo4),
                contentDescription = "Logo",
                modifier = Modifier
                    .height(100.dp)
                    .padding(bottom = 24.dp)
            )

            // Judul halaman login
            Text(
                text = "Login",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Input Username
            OutlinedTextField(
                value = username,
                onValueChange = {
                    username = it
                    usernameError = false // Reset error saat diketik
                },
                isError = usernameError,
                label = { Text("Username", color = Color.White) },
                textStyle = TextStyle(color = Color.White),
                modifier = Modifier.fillMaxWidth()
            )

            // Pesan error jika username kosong
            if (usernameError) {
                Text(
                    "Username tidak boleh kosong",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.labelSmall
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Input Password
            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                    passwordError = false
                },
                isError = passwordError,
                label = { Text("Password", color = Color.White) },
                visualTransformation = PasswordVisualTransformation(), // Menyembunyikan karakter
                textStyle = TextStyle(color = Color.White),
                modifier = Modifier.fillMaxWidth()
            )

            // Pesan error jika password kosong
            if (passwordError) {
                Text(
                    "Password tidak boleh kosong",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.labelSmall
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Tombol Login
            Button(
                onClick = {
                    // Validasi input kosong
                    if (username.isBlank()) {
                        usernameError = true
                        return@Button
                    }
                    if (password.isBlank()) {
                        passwordError = true
                        return@Button
                    }

                    isLoading = true // Tampilkan loading
                    val loginRequest = LoginRequest(username, password) // Buat objek request

                    coroutineScope.launch {
                        try {
                            // Panggil API login
                            val response = ApiClient.apiService.login(loginRequest)
                            isLoading = false

                            // Jika berhasil dan status = true dari backend
                            if (response.isSuccessful && response.body()?.status == true) {
                                val userId = response.body()?.user_id ?: -1
                                sessionManager.saveUserId(userId) // Simpan user ID ke SharedPreferences

                                Toast.makeText(context, "Login berhasil", Toast.LENGTH_SHORT).show()

                                // Navigasi ke halaman Home dan hapus Login dari backstack
                                navController.navigate(Screen.Home.route) {
                                    popUpTo(Screen.Login.route) { inclusive = true }
                                }
                            } else {
                                // Jika gagal login (status false atau error)
                                Toast.makeText(
                                    context,
                                    response.body()?.message ?: "Username atau password salah",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                        } catch (e: Exception) {
                            // Tangani error saat koneksi gagal
                            isLoading = false
                            Toast.makeText(
                                context,
                                "Gagal login: ${e.localizedMessage}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Login")
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Navigasi ke halaman register
            TextButton(
                onClick = { navController.navigate(Screen.Register.route) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Belum punya akun? Daftar", color = Color.White)
            }
        }

        // Loading dialog saat login sedang diproses
        if (isLoading) {
            AlertDialog(
                onDismissRequest = {},
                confirmButton = {},
                title = { Text("Loading") },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(16.dp))
                        Text("Sedang login...")
                    }
                }
            )
        }
    }
}
