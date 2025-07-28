package com.example.thesolemate.screen

// Import library yang dibutuhkan
import android.util.Patterns // Untuk validasi email
import android.widget.Toast // Untuk menampilkan pesan ke user
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.thesolemate.R
import com.example.thesolemate.model.request.RegisterRequest // Model untuk data registrasi
import com.example.thesolemate.data.remote.ApiClient // Retrofit API client
import com.example.thesolemate.navigation.Screen // Navigasi ke layar login
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

@Composable
fun RegisterScreen(navController: NavHostController) {
    // Menyimpan input user
    var fullName by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    // Menyimpan status error tiap input
    var fullNameError by remember { mutableStateOf(false) }
    var usernameError by remember { mutableStateOf(false) }
    var emailError by remember { mutableStateOf(false) }
    var passwordError by remember { mutableStateOf(false) }
    var confirmPasswordError by remember { mutableStateOf(false) }

    // Menampilkan loading dialog saat proses registrasi
    var isLoading by remember { mutableStateOf(false) }

    // Untuk mengatur fokus keyboard
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope() // Untuk menjalankan API di background

    Box(modifier = Modifier.fillMaxSize()) {

        Image(
            painter = painterResource(id = R.drawable.gambar1),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Overlay hitam semi-transparan agar teks terlihat
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.6f))
        )

        // Kolom isi formulir
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo aplikasi
            Image(
                painter = painterResource(id = R.drawable.solematelogo_putih),
                contentDescription = "Logo",
                modifier = Modifier
                    .size(250.dp)
                    .padding(bottom = 16.dp)
            )

            // Judul halaman
            Text(
                text = "Register",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Input Nama Lengkap
            OutlinedTextField(
                value = fullName,
                onValueChange = {
                    fullName = it
                    fullNameError = false
                },
                isError = fullNameError,
                label = { Text("Nama Lengkap", color = Color.White) },
                modifier = Modifier.fillMaxWidth(),
                textStyle = TextStyle(color = Color.White),
            )
            if (fullNameError) Text("Nama lengkap wajib diisi", color = MaterialTheme.colorScheme.error)

            Spacer(modifier = Modifier.height(8.dp))

            // Input Username
            OutlinedTextField(
                value = username,
                onValueChange = {
                    username = it
                    usernameError = false
                },
                isError = usernameError,
                label = { Text("Username", color = Color.White) },
                modifier = Modifier.fillMaxWidth(),
                textStyle = TextStyle(color = Color.White),
            )
            if (usernameError) Text("Username wajib diisi", color = MaterialTheme.colorScheme.error)

            Spacer(modifier = Modifier.height(8.dp))

            // Input Email
            OutlinedTextField(
                value = email,
                onValueChange = {
                    email = it
                    emailError = false
                },
                isError = emailError,
                label = { Text("Email", color = Color.White) },
                modifier = Modifier.fillMaxWidth(),
                textStyle = TextStyle(color = Color.White),
            )
            if (emailError) Text("Email tidak valid", color = MaterialTheme.colorScheme.error)

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
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                textStyle = TextStyle(color = Color.White),
            )
            if (passwordError) Text("Password wajib diisi", color = MaterialTheme.colorScheme.error)

            Spacer(modifier = Modifier.height(8.dp))

            // Input Konfirmasi Password
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = {
                    confirmPassword = it
                    confirmPasswordError = false
                },
                isError = confirmPasswordError,
                label = { Text("Konfirmasi Password", color = Color.White) },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                textStyle = TextStyle(color = Color.White),
            )
            if (confirmPasswordError) Text("Password tidak cocok", color = MaterialTheme.colorScheme.error)

            Spacer(modifier = Modifier.height(16.dp))

            // Tombol Register
            Button(
                onClick = {
                    focusManager.clearFocus()
                    // Validasi input
                    fullNameError = fullName.isBlank()
                    usernameError = username.isBlank()
                    emailError = email.isBlank() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()
                    passwordError = password.isBlank()
                    confirmPasswordError = confirmPassword != password

                    // Jika semua input valid, kirim data ke server
                    if (!fullNameError && !usernameError && !emailError && !passwordError && !confirmPasswordError) {
                        isLoading = true
                        coroutineScope.launch {
                            try {
                                // Kirim data ke API
                                val response = ApiClient.apiService.register(
                                    RegisterRequest(
                                        name = fullName,
                                        email = email,
                                        username = username,
                                        password = password
                                    )
                                )
                                isLoading = false
                                if (response.isSuccessful) {
                                    // Jika sukses, tampilkan Toast dan navigasi ke login
                                    Toast.makeText(context, "Registrasi berhasil!", Toast.LENGTH_SHORT).show()
                                    navController.navigate(Screen.Login.route)
                                } else {
                                    // Jika gagal, tampilkan error dari server
                                    val errorMsg = response.errorBody()?.string()
                                    Toast.makeText(context, "Gagal: $errorMsg", Toast.LENGTH_LONG).show()
                                }
                            } catch (e: HttpException) {
                                isLoading = false
                                Toast.makeText(context, "Http Error: ${e.message()}", Toast.LENGTH_LONG).show() // Menangani kesalahan HTTP dari server.
                            } catch (e: IOException) {
                                isLoading = false
                                Toast.makeText(context, "Network Error: ${e.message}", Toast.LENGTH_LONG).show() //Menangani kesalahan dari koneksi internet.
                            } catch (e: Exception) {
                                isLoading = false
                                Toast.makeText(context, "Error: ${e.localizedMessage}", Toast.LENGTH_LONG).show() // Menangkap error yang belum ditangani sebelumnya
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Register")
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Teks navigasi ke halaman login
            TextButton(
                onClick = { navController.navigate(Screen.Login.route) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Sudah punya akun? Login", color = Color.White)
            }
        }

        // Dialog loading saat proses API
        if (isLoading) {
            AlertDialog(
                onDismissRequest = {}, // Tidak bisa ditutup manual
                confirmButton = {},
                title = { Text("Mohon tunggu") },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(16.dp))
                        Text("Sedang mengirim data...")
                    }
                }
            )
        }
    }
}
