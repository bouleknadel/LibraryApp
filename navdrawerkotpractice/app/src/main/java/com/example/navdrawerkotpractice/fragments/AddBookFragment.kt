package com.example.navdrawerkotpractice.fragments


import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import android.util.Base64

import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.content.ContentProviderCompat.requireContext
import com.android.volley.DefaultRetryPolicy
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException
import org.json.JSONObject
import androidx.fragment.app.Fragment
import java.io.ByteArrayOutputStream
import java.io.FileNotFoundException
import java.io.InputStream
import com.example.navdrawerkotpractice.R  // Changer ici
import com.example.navdrawerkotpractice.BookScanner



class AddBookFragment : Fragment() {
    private companion object {
        private const val PICK_IMAGE_REQUEST = 1
    }

    private lateinit var buttonScanBarcode: Button  // Nouvelle déclaration
    private lateinit var imageViewBookCover: ImageView
    private lateinit var imageViewAddPhoto: ImageView
    private lateinit var editTextBookTitle: EditText
    private lateinit var editTextAuthor: EditText
    private lateinit var editTextGenre: EditText
    private lateinit var editTextLanguage: EditText
    private lateinit var editTextPrice: EditText
    private lateinit var editTextDescription: EditText
    private lateinit var buttonAddBook: Button
    private var bitmap: Bitmap? = null
    private lateinit var requestQueue: RequestQueue
    private val insertUrl = "http://192.168.0.182/livreApp/ws/createLivre.php"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add_book, container, false)

        // Initialisation des vues
        with(view) {

            buttonScanBarcode = findViewById(R.id.buttonScanBarcode)  // Nouvelle initialisation
            imageViewBookCover = findViewById(R.id.imageViewBookCover)
            imageViewAddPhoto = findViewById(R.id.imageViewAddPhoto)
            editTextBookTitle = findViewById(R.id.editTextBookTitle)
            editTextAuthor = findViewById(R.id.editTextAuthor)
            editTextGenre = findViewById(R.id.editTextGenre)
            editTextLanguage = findViewById(R.id.editTextLanguage)
            editTextPrice = findViewById(R.id.editTextPrice)
            editTextDescription = findViewById(R.id.editTextDescription)
            buttonAddBook = findViewById(R.id.buttonAddBook)
        }

        // Ajout du gestionnaire de clic pour le bouton Scanner
        buttonScanBarcode.setOnClickListener {
            val intent = Intent(activity, BookScanner::class.java)
            startActivity(intent)
        }

        // Gestionnaire de clic pour sélectionner une image
        imageViewAddPhoto.setOnClickListener {
            openFileChooser()
        }

        // Gestionnaire de clic pour ajouter un livre
        buttonAddBook.setOnClickListener {
            if (validateInputs()) {
                addBook()
            }
        }

        return view
    }

    private fun openFileChooser() {
        val intent = Intent().apply {
            type = "image/*"
            action = Intent.ACTION_GET_CONTENT
        }
        startActivityForResult(
            Intent.createChooser(intent, "Sélectionner une image"),
            PICK_IMAGE_REQUEST
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST &&
            resultCode == Activity.RESULT_OK &&
            data?.data != null) {
            val imageUri = data.data
            try {
                val inputStream = requireActivity().contentResolver.openInputStream(imageUri!!)
                bitmap = BitmapFactory.decodeStream(inputStream)
                imageViewBookCover.setImageBitmap(bitmap)
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
                Toast.makeText(context, "Erreur lors du chargement de l'image", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun validateInputs(): Boolean {
        when {
            editTextBookTitle.text.toString().trim().isEmpty() -> {
                editTextBookTitle.error = "Le titre est requis"
                return false
            }
            editTextAuthor.text.toString().trim().isEmpty() -> {
                editTextAuthor.error = "L'auteur est requis"
                return false
            }
            editTextGenre.text.toString().trim().isEmpty() -> {
                editTextGenre.error = "Le genre est requis"
                return false
            }
            editTextLanguage.text.toString().trim().isEmpty() -> {
                editTextLanguage.error = "La langue est requise"
                return false
            }
            editTextPrice.text.toString().trim().isEmpty() -> {
                editTextPrice.error = "Le prix est requis"
                return false
            }
            bitmap == null -> {
                Toast.makeText(context, "Veuillez sélectionner une image", Toast.LENGTH_SHORT).show()
                return false
            }
        }
        return true
    }

    private fun imageToString(bitmap: Bitmap): String {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        val imageBytes = outputStream.toByteArray()
        return Base64.encodeToString(imageBytes, Base64.DEFAULT)
    }

    private fun addBook() {
        requestQueue = Volley.newRequestQueue(requireContext())

        val request = object : StringRequest(
            Method.POST,
            insertUrl,
            { response ->
                try {
                    Log.d("Response", response)
                    val jsonResponse = JSONObject(response)
                    if (!jsonResponse.has("error")) {
                        Toast.makeText(context, "Livre ajouté avec succès", Toast.LENGTH_SHORT).show()
                        clearForm()
                    } else {
                        Toast.makeText(
                            context,
                            "Erreur : ${jsonResponse.getString("error")}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                    Toast.makeText(context, "Erreur de réponse du serveur", Toast.LENGTH_SHORT).show()
                }
            },
            { error ->
                error.printStackTrace()
                val message = buildString {
                    append("Erreur : ")
                    append(error.networkResponse?.statusCode ?: error.message)
                }
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                Log.e("Volley Error", error.toString())
            }
        ) {
            override fun getParams(): Map<String, String> {
                return mapOf(
                    "titre" to editTextBookTitle.text.toString().trim(),
                    "auteur" to editTextAuthor.text.toString().trim(),
                    "genre" to editTextGenre.text.toString().trim(),
                    "langue" to editTextLanguage.text.toString().trim(),
                    "price" to editTextPrice.text.toString().trim(),
                    "description" to editTextDescription.text.toString().trim(),
                    "picPath" to imageToString(bitmap!!)
                )
            }
        }

        // Augmenter le timeout si nécessaire pour les grandes images
        request.retryPolicy = DefaultRetryPolicy(
            30000,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )

        requestQueue.add(request)
    }

    private fun clearForm() {
        editTextBookTitle.setText("")
        editTextAuthor.setText("")
        editTextGenre.setText("")
        editTextLanguage.setText("")
        editTextPrice.setText("")
        editTextDescription.setText("")
        imageViewBookCover.setImageResource(android.R.color.transparent)
        bitmap = null
    }
}