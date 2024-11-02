package com.example.navdrawerkotpractice.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import android.widget.EditText
import android.widget.Button
import android.text.Editable
import android.text.TextWatcher

import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.example.navdrawerkotpractice.Adapter.LivreAdapter
import com.example.navdrawerkotpractice.Domain.LivreDomain
import com.example.navdrawerkotpractice.R
import com.bumptech.glide.Glide
import org.json.JSONArray
import org.json.JSONException

class HomeFragment : Fragment() {

    private lateinit var recyclerViewLivre: RecyclerView
    private lateinit var livreAdapter: LivreAdapter
    private lateinit var requestQueue: RequestQueue
    private lateinit var searchEditText: EditText
    private lateinit var minPriceEditText: EditText
    private lateinit var maxPriceEditText: EditText
    private lateinit var applyFilterButton: Button
    private val livreList = ArrayList<LivreDomain>()
    private val url = "http://192.168.0.182/LivreApp/ws/loadLivre.php"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        initViews(view)

        // Initialisation du RecyclerView
        initRecyclerView(view)

        // Initialisation de Volley
        initVolley()

        setupSearchListener()
        setupPriceFilter()

        // Chargement des données
        loadLivreData()

        return view
    }

    private fun initViews(view: View) {
        searchEditText = view.findViewById(R.id.searchEditText)
        minPriceEditText = view.findViewById(R.id.minPriceEditText)
        maxPriceEditText = view.findViewById(R.id.maxPriceEditText)
        applyFilterButton = view.findViewById(R.id.applyFilterButton)
    }

    private fun setupSearchListener() {
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                livreAdapter.filter.filter(s)
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun setupPriceFilter() {
        applyFilterButton.setOnClickListener {
            val minPrice = minPriceEditText.text.toString().toDoubleOrNull()
            val maxPrice = maxPriceEditText.text.toString().toDoubleOrNull()

            if (minPrice != null && maxPrice != null) {
                if (minPrice <= maxPrice) {
                    livreAdapter.setPriceRange(minPrice, maxPrice)
                } else {
                    Toast.makeText(context, "Le prix minimum doit être inférieur au prix maximum", Toast.LENGTH_SHORT).show()
                }
            } else {
                livreAdapter.clearPriceRange()
            }
        }
    }

    private fun initRecyclerView(view: View) {
        recyclerViewLivre = view.findViewById(R.id.recyclerViewLivres)
        recyclerViewLivre.layoutManager = LinearLayoutManager(requireContext())
        livreList.clear() // Nettoyage de la liste avant chargement
        livreAdapter = LivreAdapter(ArrayList(livreList), requireContext()) // Créer une nouvelle ArrayList
        recyclerViewLivre.adapter = livreAdapter
        // Connecter l'EditText à l'adapter
        livreAdapter.setSearchEditText(searchEditText)
        Log.d("HomeFragment", "RecyclerView initialisé avec succès")
    }

    private fun initVolley() {
        requestQueue = Volley.newRequestQueue(requireContext())
        Log.d("HomeFragment", "Volley RequestQueue initialisée")
    }

    private fun loadLivreData() {
        Log.d("HomeFragment", "Début du chargement des données depuis: $url")

        val jsonArrayRequest = JsonArrayRequest(
            Request.Method.POST, url, null,
            { response -> handleSuccessResponse(response) },
            { error -> handleErrorResponse(error) }
        ).apply {
            retryPolicy = DefaultRetryPolicy(
                30000, // 30 secondes de timeout
                1, // Pas de retry
                1.0f // Pas de backoff multiplier
            )
        }

        requestQueue.add(jsonArrayRequest)
    }

    private fun handleSuccessResponse(response: JSONArray) {
        try {
            Log.d("HomeFragment", "Réponse reçue: ${response.toString()}")
            if (response.length() > 0) {
                livreList.clear()
                parseJsonResponse(response)
                activity?.runOnUiThread {
                    // Mettre à jour l'adapter avec la nouvelle liste
                    livreAdapter.updateList(ArrayList(livreList))
                    Log.d("HomeFragment", "Liste mise à jour avec ${livreList.size} livres")
                    Toast.makeText(context, "Chargement réussi: ${livreList.size} livres", Toast.LENGTH_SHORT).show()
                }
            } else {
                Log.w("HomeFragment", "La réponse est vide")
                showError("Aucun livre trouvé")
            }
        } catch (e: Exception) {
            Log.e("HomeFragment", "Erreur lors du traitement de la réponse", e)
            showError("Erreur lors du traitement des données")
        }
    }

    private fun handleErrorResponse(error: com.android.volley.VolleyError) {
        val errorMessage = when {
            error.networkResponse == null -> "Erreur réseau - Vérifiez votre connexion"
            error.networkResponse.statusCode == 404 -> "URL non trouvée (404)"
            else -> "Erreur: ${error.message ?: "Inconnue"}"
        }

        Log.e("HomeFragment", "Erreur Volley: $errorMessage")
        error.networkResponse?.let { response ->
            val responseData = String(response.data)
            Log.e("HomeFragment", "Réponse d'erreur: $responseData")
        }

        showError(errorMessage)
    }

    private fun parseJsonResponse(response: JSONArray) {
        for (i in 0 until response.length()) {
            try {
                val livreJson = response.getJSONObject(i)
                val picPath = "http://192.168.0.182/LivreApp/" + livreJson.getString("picPath")

                Log.d("PicPathDebug", "Chemin de l'image pour le livre ${livreJson.getString("titre")}: $picPath")

                val livre = LivreDomain(
                    id = livreJson.getInt("id"),
                    titre = livreJson.getString("titre"),
                    auteur = livreJson.getString("auteur"),
                    genre = livreJson.getString("genre"),
                    langue = livreJson.getString("langue"),
                    price = livreJson.getDouble("price"),
                    description = livreJson.getString("description"),
                    picPath = picPath
                )
                livreList.add(livre)
                Log.d("HomeFragment", "Livre ajouté: ${livre.titre}")
            } catch (e: JSONException) {
                Log.e("HomeFragment", "Erreur parsing JSON pour l'index $i", e)
            }
        }
    }

    private fun showError(message: String) {
        activity?.runOnUiThread {
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        requestQueue.cancelAll { true }
    }
}
