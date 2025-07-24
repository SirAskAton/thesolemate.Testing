package com.example.thesolemate.screen

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.thesolemate.data.remote.ApiClient
import com.example.thesolemate.model.response.RegisterResponse
import com.example.thesolemate.navigation.Screen
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun LoginScreen(navController: NavHostController) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var usernameError by remember { mutableStateOf(false) }
    var passwordError by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .imePadding(),
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Login", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = username,
            onValueChange = {
                username = it
                usernameError = false
            },
            isError = usernameError,
            label = { Text("Username") },
            modifier = Modifier.fillMaxWidth()
        )

        if (usernameError) {
            Text(
                "Username tidak boleh kosong",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.labelSmall
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
                passwordError = false
            },
            isError = passwordError,
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        if (passwordError) {
            Text(
                "Password tidak boleh kosong",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.labelSmall
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (username.isBlank()) {
                    usernameError = true
                    return@Button
                }
                if (password.isBlank()) {
                    passwordError = true
                    return@Button
                }

                isLoading = true
                val call = ApiClient.apiService.loginWithQuery(username, password)
                call.enqueue(object : Callback<List<RegisterResponse>> {
                    override fun onResponse(
                        call: Call<List<RegisterResponse>>,
                        response: Response<List<RegisterResponse>>
                    ) {
                        isLoading = false
                        if (response.isSuccessful && response.body()?.isNotEmpty() == true) {
                            val user = response.body()!![0]
                            Toast.makeText(context, "Login berhasil: ${user.success}", Toast.LENGTH_SHORT).show()
                            navController.navigate(Screen.Home.route) {
                                popUpTo(Screen.Login.route) { inclusive = true }
                            }
                        } else {
                            Toast.makeText(context, "Username atau password salah", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<List<RegisterResponse>>, t: Throwable) {
                        isLoading = false
                        Toast.makeText(context, "Gagal login: ${t.localizedMessage}", Toast.LENGTH_SHORT).show()
                    }
                })
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Login")
        }

        Spacer(modifier = Modifier.height(8.dp))

        TextButton(
            onClick = { navController.navigate(Screen.Register.route) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Belum punya akun? Daftar")
        }
    }

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