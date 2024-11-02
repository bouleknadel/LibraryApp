package com.example.navdrawerkotpractice.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.navdrawerkotpractice.Adapter.FavoriteBookAdapter
import com.example.navdrawerkotpractice.Domain.LivreDomain
import com.example.navdrawerkotpractice.R
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley

class AboutFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: FavoriteBookAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_about, container, false)

        recyclerView = view.findViewById(R.id.recyclerViewFavoris)
        recyclerView.layoutManager = LinearLayoutManager(context)
        adapter = FavoriteBookAdapter(requireContext(), emptyList())
        recyclerView.adapter = adapter

        loadFavoriteBooks()

        return view
    }

    private fun loadFavoriteBooks() {
        // Récupérer les SharedPreferences
        val favoritesPrefs = requireContext().getSharedPreferences("Favorites", Context.MODE_PRIVATE)
        val ratingsPrefs = requireContext().getSharedPreferences("BookRatings", Context.MODE_PRIVATE)



        // Récupérer tous les IDs des livres favoris
        val favoriteIds = favoritesPrefs.all.filter { it.value as Boolean }.keys.map { it.toInt() }

        if (favoriteIds.isEmpty()) {
            Toast.makeText(context, "Aucun livre favori", Toast.LENGTH_SHORT).show()
            return
        }

        // Charger les détails des livres depuis l'API
        val url = "http://192.168.0.182/LivreApp/ws/loadLivre.php"
        val requestQueue = Volley.newRequestQueue(requireContext())

        val jsonArrayRequest = JsonArrayRequest(
            Request.Method.POST, url, null,
            { response ->
                val favoriteBooks = mutableListOf<FavoriteBookAdapter.FavoriteBook>()

                for (i in 0 until response.length()) {
                    val bookJson = response.getJSONObject(i)
                    val bookId = bookJson.getInt("id")

                    if (bookId in favoriteIds) {
                        val rating = ratingsPrefs.getFloat("rating_$bookId", 0f)
                        val picPath = "http://192.168.0.182/LivreApp/" + bookJson.getString("picPath")
                        Log.d("ImageURL", "URL de l'image: $picPath")


                        val livre = LivreDomain(
                            id = bookId,
                            titre = bookJson.getString("titre"),
                            auteur = bookJson.getString("auteur"),
                            genre = bookJson.getString("genre"),
                            langue = bookJson.getString("langue"),
                            price = bookJson.getDouble("price"),
                            description = bookJson.getString("description"),
                            picPath = picPath
                        )

                        favoriteBooks.add(FavoriteBookAdapter.FavoriteBook(livre, rating))
                    }
                }

                activity?.runOnUiThread {
                    adapter.updateBooks(favoriteBooks)
                }
            },
            { error ->
                Toast.makeText(context, "Erreur de chargement: ${error.message}", Toast.LENGTH_LONG).show()
            }
        )

        requestQueue.add(jsonArrayRequest)
    }

    override fun onResume() {
        super.onResume()
        // Recharger les favoris quand on revient sur le fragment
        loadFavoriteBooks()
    }
}