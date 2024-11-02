package com.example.navdrawerkotpractice

import android.Manifest
import android.content.Context
import android.text.InputType
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.util.Base64
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.navdrawerkotpractice.databinding.ActivityRegisterBinding
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.InputStream

class ActivityRegister : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private var bitmap: Bitmap? = null
    private val PERMISSION_CODE = 1001

    // Gestionnaire de résultat pour la sélection d'image
    private val getContent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let { selectedImageUri ->
            try {
                val inputStream: InputStream? = contentResolver.openInputStream(selectedImageUri)
                bitmap = BitmapFactory.decodeStream(inputStream)
                binding.profileImage.setImageBitmap(bitmap)
            } catch (e: Exception) {
                Toast.makeText(this, "Erreur lors du chargement de l'image", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // Ajouter ceci dans la fonction onCreate après l'initialisation du binding
        binding.passwordToggle.setOnClickListener {
            // Toggle le type d'input du mot de passe
            if (binding.password.inputType == (InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD)) {
                // Montrer le mot de passe
                binding.password.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                binding.passwordToggle.setImageResource(R.drawable.baseline_visibility_24)
            } else {
                // Cacher le mot de passe
                binding.password.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                binding.passwordToggle.setImageResource(R.drawable.baseline_visibility_off_24)
            }
            // Maintenir le curseur à la fin du texte
            binding.password.setSelection(binding.password.text.length)
        }
        // Configuration du clic sur l'image de profil et le bouton d'ajout
        binding.profileImage.setOnClickListener {
            checkPermissionAndPickImage()
        }

        binding.addPhotoButton.setOnClickListener {
            checkPermissionAndPickImage()
        }

        // Récupérer le TextView pour "Se connecter"
        val loginText: TextView = findViewById(R.id.loginText)

        // Créer le SpannableString
        val text = "Vous avez déjà un compte ? Se connecter"
        val spannableString = SpannableString(text)
        val start = text.indexOf("Se connecter")
        val end = start + "Se connecter".length
        spannableString.setSpan(
            ForegroundColorSpan(resources.getColor(R.color.lavender)),
            start,
            end,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        loginText.text = spannableString

        loginText.setOnClickListener {
            val intent = Intent(this, ActivityLogin::class.java)
            startActivity(intent)
            finish()
        }

        binding.registerButton.setOnClickListener {
            registerUser()
        }
    }

    private fun checkPermissionAndPickImage() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                PERMISSION_CODE
            )
        } else {
            openImagePicker()
        }
    }

    private fun openImagePicker() {
        getContent.launch("image/*")
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openImagePicker()
                } else {
                    Toast.makeText(
                        this,
                        "Permission nécessaire pour accéder aux images",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun imageToBase64(bitmap: Bitmap): String {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        val imageBytes = outputStream.toByteArray()
        return Base64.encodeToString(imageBytes, Base64.DEFAULT)
    }

    private fun registerUser() {
        val url = "http://192.168.0.182/livreApp/ws/register.php"

        // Créer l'objet JSON avec l'image en base64
        val jsonBody = JSONObject().apply {
            put("nom", binding.firstName.text.toString().trim())
            put("prenom", binding.lastName.text.toString().trim())
            put("email", binding.email.text.toString().trim())
            put("mdps", binding.password.text.toString().trim())
            // Ajouter l'image en base64 si elle existe
            bitmap?.let {
                put("picPath", imageToBase64(it))
            }
        }

        // Créer la requête POST
        val jsonObjectRequest = object : JsonObjectRequest(
            Request.Method.POST, url, jsonBody,
            { response ->
                try {
                    val success = response.getBoolean("success")
                    val message = response.getString("message")

                    if (success) {
                        // Stocker les informations dans SharedPreferences
                        val sharedPref = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
                        with(sharedPref.edit()) {
                            putInt("userId", response.getInt("id_user"))
                            val nomComplet = "${binding.firstName.text} ${binding.lastName.text}"
                            putString("userName", nomComplet)
                            putString("userEmail", binding.email.text.toString())
                            putString("picPath", response.getString("picPath")) // Ajout du stockage du picPath

//                            // Stocker le chemin de l'image si disponible
//                            response.optString("picPath", "").let { picPath ->
//                                if (picPath.isNotEmpty()) {
//                                    putString("userPicPath", picPath)
//                                }
//                            }
                            apply()
                        }

                        Toast.makeText(this, "Inscription réussie", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(
                        this,
                        "Erreur de traitement: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            },
            { error ->
                Toast.makeText(this, "Erreur réseau", Toast.LENGTH_SHORT).show()
            }
        ) {
            override fun getHeaders(): MutableMap<String, String> {
                return HashMap<String, String>().apply {
                    put("Content-Type", "application/json")
                }
            }
        }

        // Ajouter la requête à la file d'attente
        Volley.newRequestQueue(this).add(jsonObjectRequest)
    }
}