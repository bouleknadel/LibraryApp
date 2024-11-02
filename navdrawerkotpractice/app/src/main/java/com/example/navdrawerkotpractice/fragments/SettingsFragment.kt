    package com.example.navdrawerkotpractice.fragments

    import java.io.ByteArrayOutputStream
    import android.util.Base64
    import android.util.Log
    import android.content.Context

    import java.io.FileNotFoundException
    import android.graphics.BitmapFactory // Assurez-vous d'importer BitmapFactory
    import android.graphics.drawable.BitmapDrawable
    import android.app.Activity
    import android.content.Intent
    import android.graphics.Bitmap
    import android.net.Uri
    import android.os.Bundle
    import android.view.LayoutInflater
    import android.view.View
    import android.view.ViewGroup
    import android.widget.Button
    import android.widget.EditText
    import android.widget.ImageView
    import android.widget.Toast
    import androidx.fragment.app.Fragment
    import com.android.volley.Request
    import com.android.volley.Response
    import com.android.volley.toolbox.StringRequest
    import com.android.volley.toolbox.Volley
    import com.example.navdrawerkotpractice.R


    class SettingsFragment : Fragment() {

        private lateinit var editTextBookTitle: EditText
        private lateinit var editTextBookGenre: EditText
        private lateinit var buttonSubmit: Button
        companion object {
            private const val PICK_IMAGE_REQUEST = 1
        }
        private lateinit var imageViewBook: ImageView
        private var imageUri: Uri? = null


        override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {
            val view = inflater.inflate(R.layout.fragment_settings, container, false)



            // Récupérer les informations de l'utilisateur à partir des préférences partagées
            val userName = getUserNameFromPreferences()
            val userEmail = getUserEmailFromPreferences()
            val userId = getUserIdFromPreferences()

            // Afficher les informations dans le Logcat
            Log.d("localstorageID", "User ID: $userId")
            Log.d("localstorageNAME", "User Name: $userName")
            Log.d("localstorageEmail", "localstorageIDEmail: $userEmail")

            // Initialiser les éléments de la mise en page
            imageViewBook = view.findViewById(R.id.imageViewBook)
            val imageViewAddIcon: ImageView = view.findViewById(R.id.imageViewAddIcon)

            editTextBookTitle = view.findViewById(R.id.editTextBookTitle)
            editTextBookGenre = view.findViewById(R.id.editTextBookGenre)
            buttonSubmit = view.findViewById(R.id.buttonSubmit)


            // Gérer le clic sur l'icône d'ajout
            imageViewAddIcon.setOnClickListener {
                openGallery()
            }


            buttonSubmit.setOnClickListener {
                submitDemandeLivre()
            }


            return view
        }

        private fun getUserIdFromPreferences(): Int {
            val sharedPref = requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
            return sharedPref.getInt("userId", 1) // Utiliser 1 comme valeur par défaut si l'id n'est pas trouvé
        }

        private fun getUserNameFromPreferences(): String {
            val sharedPref = requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
            return sharedPref.getString("userName", "Inconnu") ?: "Inconnu" // Valeur par défaut
        }

        private fun getUserEmailFromPreferences(): String {
            val sharedPref = requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
            return sharedPref.getString("userEmail", "Inconnu") ?: "Inconnu" // Valeur par défaut
        }
        private fun openGallery() {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, PICK_IMAGE_REQUEST)
        }

        private fun imageToString(bitmap: Bitmap): String {
            val outputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            val imageBytes = outputStream.toByteArray()
            return Base64.encodeToString(imageBytes, Base64.DEFAULT)
        }


        override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
            super.onActivityResult(requestCode, resultCode, data)
            if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
                imageUri = data.data
                try {
                    val inputStream = requireContext().contentResolver.openInputStream(imageUri!!)
                    val bitmap = BitmapFactory.decodeStream(inputStream)
                    imageViewBook.setImageBitmap(bitmap) // Afficher l'image sélectionnée dans imageViewBook

                    // Masquer l'icône d'ajout après la sélection de l'image
                    val imageViewAddIcon: ImageView = requireView().findViewById(R.id.imageViewAddIcon)
                    imageViewAddIcon.visibility = View.GONE // Cacher l'icône d'ajout
                } catch (e: FileNotFoundException) {
                    e.printStackTrace()
                }
            }
        }




        private fun submitDemandeLivre() {
            val titre = editTextBookTitle.text.toString().trim()
            val genre = editTextBookGenre.text.toString().trim()

            if (titre.isEmpty() || genre.isEmpty() || imageUri == null) {
                Toast.makeText(
                    requireContext(),
                    "Veuillez remplir tous les champs.",
                    Toast.LENGTH_SHORT
                ).show()
                return
            }

            // Convertir l'image en Bitmap
            val inputStream = requireContext().contentResolver.openInputStream(imageUri!!)
            val bitmap = BitmapFactory.decodeStream(inputStream)



            // Créer une requête POST
            val url = "http://192.168.0.182/LivreApp/ws/createDemandeLivre.php"
            val stringRequest = object : StringRequest(Request.Method.POST, url,
                Response.Listener<String> { response ->
                    // Réponse du serveur
                    Toast.makeText(
                        requireContext(),
                        "Demande soumise avec succès.",
                        Toast.LENGTH_SHORT
                    ).show()
                },
                Response.ErrorListener { error ->
                    // Gérer l'erreur
                    Toast.makeText(requireContext(), "Erreur: ${error.message}", Toast.LENGTH_SHORT)
                        .show()
                }) {
                override fun getParams(): Map<String, String> {
                    val params = HashMap<String, String>()
                    params["titre"] = titre
                    params["genre"] = genre
                    params["picPath"] = imageToString(bitmap) // Convertir le bitmap en chaîne
                    params["idUser"] = getUserIdFromPreferences().toString() // Mettez ici l'ID de l'utilisateur

                    return params
                }
            }

            // Ajouter la requête à la file d'attente de Volley
            val requestQueue = Volley.newRequestQueue(requireContext())
            requestQueue.add(stringRequest)
        }
    }
