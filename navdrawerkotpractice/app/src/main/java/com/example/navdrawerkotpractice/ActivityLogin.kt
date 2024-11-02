    package com.example.navdrawerkotpractice

    import android.content.Context
    import android.content.Intent
    import android.os.Bundle
    import android.widget.Button
    import android.widget.EditText
    import android.widget.Toast
    import android.widget.TextView
    import android.widget.ImageView
    import android.text.InputType

    import androidx.appcompat.app.AppCompatActivity
    import com.android.volley.Request
    import com.android.volley.Response
    import com.android.volley.toolbox.JsonObjectRequest
    import com.android.volley.toolbox.Volley
    import org.json.JSONObject
    import android.util.Log
    import com.android.volley.DefaultRetryPolicy

    class ActivityLogin : AppCompatActivity() {
        private lateinit var emailEditText: EditText
        private lateinit var passwordEditText: EditText
        private lateinit var loginButton: Button
        private lateinit var passwordToggle: ImageView


        override fun onCreate(savedInstanceState: Bundle?) {
            try {
                super.onCreate(savedInstanceState)
                setContentView(R.layout.activity_login)

                // Initialiser les vues
                emailEditText = findViewById(R.id.email)
                passwordEditText = findViewById(R.id.password)
                loginButton = findViewById(R.id.loginButton)
                passwordToggle = findViewById(R.id.passwordToggle)


                // Configuration du toggle de mot de passe
                passwordToggle.setOnClickListener {
                    // Toggle le type d'input du mot de passe
                    if (passwordEditText.inputType == (InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD)) {
                        // Montrer le mot de passe
                        passwordEditText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                        passwordToggle.setImageResource(R.drawable.baseline_visibility_24)
                    } else {
                        // Cacher le mot de passe
                        passwordEditText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                        passwordToggle.setImageResource(R.drawable.baseline_visibility_off_24)
                    }
                    // Maintenir le curseur à la fin du texte
                    passwordEditText.setSelection(passwordEditText.text.length)
                }


                // Configurer la logique de connexion
                loginButton.setOnClickListener {
                    try {
                        val email = emailEditText.text.toString()
                        val password = passwordEditText.text.toString()

                        if (email.isNotEmpty() && password.isNotEmpty()) {
                            login(email, password)
                        } else {
                            Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: Exception) {
                        Log.e("LoginError", "Erreur lors du clic sur le bouton: ${e.message}")
                        Toast.makeText(this, "Erreur: ${e.message}", Toast.LENGTH_LONG).show()
                    }
                }

                // Configurer la redirection vers l'activité d'inscription
                val registerText: TextView = findViewById(R.id.registerText)
                registerText.setOnClickListener {
                    val intent = Intent(this, ActivityRegister::class.java)
                    startActivity(intent)
                }

            } catch (e: Exception) {
                Log.e("LoginError", "Erreur dans onCreate: ${e.message}")
                Toast.makeText(this, "Erreur de démarrage: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }

        private fun login(email: String, password: String) {
            try {
                val url = "http://192.168.0.182/livreApp/ws/login.php" // À remplacer par votre URL

                val jsonParams = JSONObject().apply {
                    put("email", email)
                    put("mdps", password)
                }

                val jsonObjectRequest = JsonObjectRequest(
                    Request.Method.POST, url, jsonParams,
                    { response ->
                        try {
                            val success = response.getBoolean("success")
                            val message = response.getString("message")

                            if (success) {
                                // Stocker les informations dans SharedPreferences
                                val sharedPref = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
                                with(sharedPref.edit()) {
                                    putInt("userId", response.getInt("id"))
                                    putString("userName", response.getString("nom"))
                                    putString("userEmail", response.getString("email"))
                                    putInt("userRole", response.getInt("role"))
                                    putString("picPath", response.getString("picPath")) // Ajout du stockage du picPath
                                    apply() // Enregistrer les modifications
                                }

                                // Ajouter des logs pour vérifier le stockage
                                Log.d("abdo", "Données stockées: userId=${response.getInt("id")}, " +
                                        "userName=${response.getString("nom")}, " +
                                        "userEmail=${response.getString("email")}, " +
                                        "userRole=${response.getInt("role")}, " +
                                        "picPath=${response.getString("picPath")}") // Ajout du log pour picPath

                                // Vérifier le rôle de l'utilisateur et rediriger vers l'activité appropriée
                                val intent = if (response.getInt("role") == 0) {
                                    Intent(this, MainActivity2::class.java) // Activité admin
                                } else {
                                    Intent(this, MainActivity::class.java) // Activité utilisateur
                                }

                                startActivity(intent)
                                finish()
                            }
                            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                        } catch (e: Exception) {
                            Log.e("LoginError", "Erreur lors du traitement de la réponse: ${e.message}")
                            Toast.makeText(this, "Erreur de traitement: ${e.message}", Toast.LENGTH_LONG).show()
                        }
                    },
                    { error ->
                        Log.e("LoginError", "Erreur Volley: ${error.message}")
                        val errorMessage = when (error) {
                            is com.android.volley.NoConnectionError -> "Pas de connexion internet"
                            is com.android.volley.TimeoutError -> "Délai d'attente dépassé"
                            else -> "Erreur de connexion: ${error.message}"
                        }
                        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
                    }
                )

                // Ajouter un timeout plus long si nécessaire
                jsonObjectRequest.retryPolicy = DefaultRetryPolicy(
                    30000, // 30 secondes de timeout
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                )

                Volley.newRequestQueue(this).add(jsonObjectRequest)
            } catch (e: Exception) {
                Log.e("LoginError", "Erreur dans la fonction login: ${e.message}")
                Toast.makeText(this, "Erreur de login: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }

    }
