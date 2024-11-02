package com.example.navdrawerkotpractice.fragments

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.widget.ImageView

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.example.navdrawerkotpractice.R
import org.json.JSONObject

class ShareFragment : Fragment() {

    private lateinit var nomTextView: TextView
    private lateinit var prenomTextView: TextView
    private lateinit var emailTextView: TextView
    private lateinit var mdpsTextView: TextView
    private lateinit var modifierButton: AppCompatButton
    private lateinit var imageUserView: ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_share, container, false)

        // Initialiser les vues
        nomTextView = view.findViewById(R.id.nom)
        prenomTextView = view.findViewById(R.id.prenom)
        emailTextView = view.findViewById(R.id.email)
        mdpsTextView = view.findViewById(R.id.mdps)
        modifierButton = view.findViewById(R.id.button2)
        imageUserView = view.findViewById(R.id.imageView3)  // Assurez-vous que l'ID correspond à votre layout

        // Charger les données de l'utilisateur depuis SharedPreferences
        loadUserData()

        // Configurer le bouton de modification
        modifierButton.setOnClickListener {
            showUpdateDialog()
        }

        return view
    }

    private fun loadUserData() {
        val sharedPref = requireActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

        // Récupérer et séparer le nom complet
        val userName = sharedPref.getString("userName", "")?.split(" ")
        if (userName?.size == 2) {
            nomTextView.text = userName[0]
            prenomTextView.text = userName[1]
        }

        // Afficher l'email
        emailTextView.text = sharedPref.getString("userEmail", "")
        mdpsTextView.text = "********"

        // Charger l'image de profil
        val picPath = sharedPref.getString("picPath", null)
        val fullPicPath = "http://192.168.0.182/LivreApp/" + picPath

        if (!picPath.isNullOrEmpty()) {
            Glide.with(requireContext())
                .load(fullPicPath)
                .error(R.drawable.profile) // Image par défaut en cas d'erreur
                .into(imageUserView)
        } else {
            // Si pas d'image, utiliser les initiales comme avatar
            val initials = userName?.joinToString("") { it.first().toString() } ?: ""
            val avatarUrl = "https://ui-avatars.com/api/?name=$initials"
            Glide.with(requireContext())
                .load(avatarUrl)
                .error(R.drawable.profile)
                .into(imageUserView)
        }


    }

    private fun showUpdateDialog() {
        val dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_update_profile, null)

        // Initialiser les EditText du dialog
        val nomEdit = dialogView.findViewById<EditText>(R.id.editNom)
        val prenomEdit = dialogView.findViewById<EditText>(R.id.editPrenom)
        val emailEdit = dialogView.findViewById<EditText>(R.id.editEmail)
        val mdpsEdit = dialogView.findViewById<EditText>(R.id.editPassword)

        // Pré-remplir avec les valeurs actuelles
        nomEdit.setText(nomTextView.text)
        prenomEdit.setText(prenomTextView.text)
        emailEdit.setText(emailTextView.text)

        AlertDialog.Builder(requireContext())
            .setTitle("Modifier le profil")
            .setView(dialogView)
            .setPositiveButton("Modifier") { dialog, _ ->
                updateUserProfile(
                    nomEdit.text.toString(),
                    prenomEdit.text.toString(),
                    emailEdit.text.toString(),
                    mdpsEdit.text.toString()
                )
            }
            .setNegativeButton("Annuler", null)
            .show()
    }

    private fun updateUserProfile(nom: String, prenom: String, email: String, mdps: String) {
        val url = "http://192.168.0.182/livreApp/ws/updateUser.php"

        val sharedPref = requireActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val userId = sharedPref.getInt("userId", -1)

        val jsonBody = JSONObject().apply {
            put("id", userId)
            put("nom", nom)
            put("prenom", prenom)
            put("email", email)
            put("mdps", mdps)
        }

        val jsonObjectRequest = object : JsonObjectRequest(
            Request.Method.POST, url, jsonBody,
            { response ->
                try {
                    val success = response.getBoolean("success")
                    val message = response.getString("message")

                    if (success) {
                        with(sharedPref.edit()) {
                            putString("userName", "$nom $prenom")
                            putString("userEmail", email)
                            apply()
                        }

                        nomTextView.text = nom
                        prenomTextView.text = prenom
                        emailTextView.text = email
                        mdpsTextView.text = "********"

                        Toast.makeText(context, "Profil mis à jour avec succès", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(context, "Erreur: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            },
            { error ->
                Toast.makeText(context, "Erreur réseau: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        ) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Content-Type"] = "application/json"
                return headers
            }
        }

        Volley.newRequestQueue(context).add(jsonObjectRequest)
    }
}
