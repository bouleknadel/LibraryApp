package com.example.navdrawerkotpractice

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.integration.android.IntentResult
import com.squareup.picasso.Picasso
import org.json.JSONException
import org.json.JSONObject

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.util.Base64
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import java.io.ByteArrayOutputStream

class BookScanner : AppCompatActivity() {
    private val CAMERA_PERMISSION_REQUEST = 100

    private var titleText: TextView? = null
    private var authorText: TextView? = null
    private var genreText: TextView? = null
    private var languageText: TextView? = null
    private var priceText: TextView? = null
    private var descriptionText: TextView? = null
    private var coverImage: ImageView? = null
    private var scanButton: Button? = null
    private var requestQueue: RequestQueue? = null
    private var confirmButton: Button? = null // Ajout du bouton confirmer
    private val insertUrl = "http://192.168.0.182/livreApp/ws/createLivre.php"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_scanner)

        // Initialiser Volley RequestQueue
        requestQueue = Volley.newRequestQueue(this)

        // Initialiser les vues
        titleText = findViewById(R.id.titleText)
        authorText = findViewById(R.id.authorText)
        genreText = findViewById(R.id.genreText)
        languageText = findViewById(R.id.languageText)
        priceText = findViewById(R.id.priceText)
        descriptionText = findViewById(R.id.descriptionText)
        coverImage = findViewById(R.id.coverImage)
        scanButton = findViewById(R.id.scanButton)
        confirmButton = findViewById(R.id.confirmButton) // Initialisation du bouton confirmer


        scanButton?.setOnClickListener {
            if (checkCameraPermission()) {
                startBarcodeScanner()
            }
        }

        confirmButton?.setOnClickListener {
            // Logique pour ajouter le livre
            addBook()
        }
    }

    private fun checkCameraPermission(): Boolean {
        return if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                CAMERA_PERMISSION_REQUEST
            )
            false
        } else {
            true
        }
    }

    private fun startBarcodeScanner() {
        val integrator = IntentIntegrator(this)
        integrator.setDesiredBarcodeFormats(IntentIntegrator.EAN_13)
        integrator.setPrompt("Scannez le code-barres du livre")
        integrator.setCameraId(0)
        integrator.setBeepEnabled(true)
        integrator.setBarcodeImageEnabled(true)
        integrator.initiateScan()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val result: IntentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null && result.contents != null) {
            val isbn: String = result.contents
            fetchBookInfo(isbn)
        }
    }

    private fun fetchBookInfo(isbn: String) {
        val url = "https://www.googleapis.com/books/v1/volumes?q=isbn:$isbn"

        val request = JsonObjectRequest(
            Request.Method.GET, url, null,
            { response: JSONObject? ->
                try {
                    parseBookResponse(response)
                } catch (e: JSONException) {
                    Toast.makeText(
                        this,
                        "Erreur de parsing", Toast.LENGTH_SHORT
                    ).show()
                }
            },
            { error: VolleyError? ->
                Toast.makeText(
                    this,
                    "Erreur de connexion", Toast.LENGTH_SHORT
                ).show()
            }
        )

        requestQueue?.add(request)
    }

    @Throws(JSONException::class)
    private fun parseBookResponse(response: JSONObject?) {
        if (response != null && response.has("items")) { // Vérifiez si 'response' n'est pas nul
            val items = response.getJSONArray("items")
            if (items.length() > 0) {
                val bookItem = items.getJSONObject(0)
                val volumeInfo = bookItem.getJSONObject("volumeInfo")

                // Titre
                val title = volumeInfo.getString("title")
                titleText?.text = "Titre: $title"

                // Auteurs
                if (volumeInfo.has("authors")) {
                    val authors = volumeInfo.getJSONArray("authors")
                    val authorsList = StringBuilder()
                    for (i in 0 until authors.length()) {
                        if (i > 0) authorsList.append(", ")
                        authorsList.append(authors.getString(i))
                    }
                    authorText?.text = "Auteur(s): $authorsList"
                }

                // Genres
                if (volumeInfo.has("categories")) {
                    val categories = volumeInfo.getJSONArray("categories")
                    val categoriesList = StringBuilder()
                    for (i in 0 until categories.length()) {
                        if (i > 0) categoriesList.append(", ")
                        categoriesList.append(categories.getString(i))
                    }
                    genreText?.text = "Genre(s): $categoriesList"
                }

                // Langue
                if (volumeInfo.has("language")) {
                    val language = volumeInfo.getString("language")
                    languageText?.text = "Langue: $language"
                }

                // Prix
                if (bookItem.has("saleInfo")) {
                    val saleInfo = bookItem.getJSONObject("saleInfo")
                    if (saleInfo.has("listPrice")) {
                        val listPrice = saleInfo.getJSONObject("listPrice")
                        val price = listPrice.getDouble("amount")
                        val currency = listPrice.getString("currencyCode")
                        priceText?.text = String.format("Prix: %.2f %s", price, currency)
                    }
                }

                // Description
                if (volumeInfo.has("description")) {
                    val description = volumeInfo.getString("description")
                    descriptionText?.text = "Description: $description"
                }

                // Image de couverture
                if (volumeInfo.has("imageLinks")) {
                    val imageLinks = volumeInfo.getJSONObject("imageLinks")
                    if (imageLinks.has("thumbnail")) {
                        val imageUrl = imageLinks.getString("thumbnail").replace("http://", "https://")
                        Picasso.get().load(imageUrl).into(coverImage)
                    }
                }
            } else {
                Toast.makeText(this, "Livre non trouvé", Toast.LENGTH_SHORT).show()
            }
            confirmButton?.visibility = View.VISIBLE // Rendre le bouton visible
        } else {
            Toast.makeText(this, "Erreur lors de la récupération des informations sur le livre", Toast.LENGTH_SHORT).show()
        }
    }

    private fun imageToString(bitmap: Bitmap): String {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        val imageBytes = outputStream.toByteArray()
        return Base64.encodeToString(imageBytes, Base64.DEFAULT)
    }

    private fun validateFields(): Boolean {
        var isValid = true

        // Extraire le texte des TextViews en enlevant les labels
        val title = titleText?.text?.toString()?.removePrefix("Titre: ") ?: ""
        val author = authorText?.text?.toString()?.removePrefix("Auteur(s): ") ?: ""
        val genre = genreText?.text?.toString()?.removePrefix("Genre(s): ") ?: ""
        val language = languageText?.text?.toString()?.removePrefix("Langue: ") ?: ""
        val priceText = priceText?.text?.toString() ?: ""
        val price = try {
            if (priceText.startsWith("Prix: ")) {
                priceText.removePrefix("Prix: ")
                    .split(" ")[0]  // Prend la partie numérique avant l'espace et la devise
                    .replace(",", ".")  // Remplace la virgule par un point si nécessaire
                    .toDouble()
            } else {
                0.0
            }
        } catch (e: Exception) {
            0.0
        }
        // Vérifier chaque champ
        when {
            title.trim().isEmpty() -> {
                Toast.makeText(this, "Le titre est requis", Toast.LENGTH_SHORT).show()
                isValid = false
            }
            author.trim().isEmpty() -> {
                Toast.makeText(this, "L'auteur est requis", Toast.LENGTH_SHORT).show()
                isValid = false
            }
            genre.trim().isEmpty() -> {
                Toast.makeText(this, "Le genre est requis", Toast.LENGTH_SHORT).show()
                isValid = false
            }
            language.trim().isEmpty() -> {
                Toast.makeText(this, "La langue est requise", Toast.LENGTH_SHORT).show()
                isValid = false
            }
            price <= 0 -> {
                // Ne pas afficher de message d'erreur pour le prix, car il peut être optionnel
                // ou non disponible depuis l'API Google Books
                 0.0  // Mettre un prix par défaut
            }
            coverImage?.drawable == null -> {
                Toast.makeText(this, "L'image de couverture est requise", Toast.LENGTH_SHORT).show()
                isValid = false
            }
        }

        return isValid
    }


    private fun addBook() {

        if (!validateFields()) {
            return
        }

        // Extraire le texte des TextViews en enlevant les labels
        val title = titleText?.text?.toString()?.removePrefix("Titre: ") ?: ""
        val author = authorText?.text?.toString()?.removePrefix("Auteur(s): ") ?: ""
        val genre = genreText?.text?.toString()?.removePrefix("Genre(s): ") ?: ""
        val language = languageText?.text?.toString()?.removePrefix("Langue: ") ?: ""
        val description = descriptionText?.text?.toString()?.removePrefix("Description: ") ?: ""

        // Extraire le prix et le convertir en nombre
        val priceValue = try {
            val priceText = priceText?.text?.toString() ?: ""
            if (priceText.startsWith("Prix: ")) {
                priceText.removePrefix("Prix: ")
                    .split(" ")[0]
                    .replace(",", ".")
                    .toDouble()
            } else {
                0.0
            }
        } catch (e: Exception) {
            0.0
        }
        // Convertir l'image en base64
        val bitmap = (coverImage?.drawable as? BitmapDrawable)?.bitmap
        val imageBase64 = bitmap?.let { imageToString(it) } ?: ""

        val request = object : StringRequest(
            Method.POST,
            insertUrl,
            { response ->
                try {
                    val jsonResponse = JSONObject(response)
                    if (!jsonResponse.has("error")) {
                        Toast.makeText(this, "Livre ajouté avec succès", Toast.LENGTH_SHORT).show()
                        resetFields()
                        finish() // Retourner à l'écran précédent
                    } else {
                        Toast.makeText(
                            this,
                            "Erreur : ${jsonResponse.getString("error")}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(this, "Erreur lors de l'ajout du livre", Toast.LENGTH_SHORT).show()
                    e.printStackTrace()
                }
            },
            { error ->
                error.printStackTrace()
                val message = "Erreur : ${error.networkResponse?.statusCode ?: error.message}"
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            }
        ) {
            override fun getParams(): Map<String, String> {
                return mapOf(
                    "titre" to title,
                    "auteur" to author,
                    "genre" to genre,
                    "langue" to language,
                    "price" to priceValue.toString(),
                    "description" to description,
                    "picPath" to imageBase64
                )
            }
        }

        // Configurer le timeout pour les grandes images
        request.retryPolicy = DefaultRetryPolicy(
            30000, // 30 secondes de timeout
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )

        // Ajouter la requête à la queue
        requestQueue?.add(request)
    }

    private fun resetFields() {
        titleText?.text = ""
        authorText?.text = ""
        genreText?.text = ""
        languageText?.text = ""
        priceText?.text = ""
        descriptionText?.text = ""
        coverImage?.setImageDrawable(null)
        confirmButton?.visibility = View.GONE // Masquer le bouton après ajout
    }

}
