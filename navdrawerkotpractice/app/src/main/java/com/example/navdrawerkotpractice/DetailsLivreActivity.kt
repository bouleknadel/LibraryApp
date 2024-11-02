package com.example.navdrawerkotpractice

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.TextView
import android.widget.ImageView
import com.bumptech.glide.Glide
import android.util.Log
import android.widget.RatingBar

class DetailsLivreActivity : AppCompatActivity() {

    private lateinit var favBtn: ImageView
    private lateinit var ratingBar: RatingBar // Ajout
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var ratingPreferences: SharedPreferences // Ajout pour les ratings
    private var isFavorite = false
    private var livreId: Int = -1

    companion object {
        private const val RATING_PREFS = "BookRatings" // Constante pour les ratings
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details_livre)

        // Dans DetailsLivreActivity, ajoutez ceci dans onCreate()
        findViewById<ImageView>(R.id.backBtn).setOnClickListener {
            onBackPressed() // ou finish()
        }

        favBtn = findViewById(R.id.favBtn)
        ratingBar = findViewById(R.id.ratingBar)
        sharedPreferences = getSharedPreferences("Favorites", Context.MODE_PRIVATE)
        ratingPreferences = getSharedPreferences(RATING_PREFS, Context.MODE_PRIVATE)

        // Retrieve the data passed through the intent
        livreId = intent.getIntExtra("livreId", -1)
        val livreTitre = intent.getStringExtra("livreTitre")
        val livreGenre = intent.getStringExtra("livreGenre")
        val livrePrice = intent.getDoubleExtra("livrePrice", 0.0)
        val livrePicPath = intent.getStringExtra("livrePicPath")
        val livreDescription = intent.getStringExtra("livreDescription")
        val livreLangue = intent.getStringExtra("livreLangue") ?: "" // Récupération de la langue
        val livreAuteur = intent.getStringExtra("livreAuteur") ?: "" // Récupération de l'auteur


        val savedRating = ratingPreferences.getFloat("rating_$livreId", 0f)
        ratingBar.rating = savedRating


        findViewById<TextView>(R.id.textView4).text = "(${savedRating.toInt()})"

        ratingBar.setOnRatingBarChangeListener { _, rating, fromUser ->
            if (fromUser) {
                saveRating(rating)
                findViewById<TextView>(R.id.textView4).text = "(${rating.toInt()})"
            }
        }
        // Vérifier si le livre est déjà en favoris
        isFavorite = sharedPreferences.getBoolean(livreId.toString(), false)
        updateFavIcon()

        // Lier les données aux TextView
        findViewById<TextView>(R.id.detailsTitreTxt).text = livreTitre ?: "Titre non disponible"
        findViewById<TextView>(R.id.detailsDescriptionTxt).text = livreDescription ?: "Description non disponible"
        findViewById<TextView>(R.id.detailsAuteurTxt).text = livreAuteur ?: "Auteur non disponible"
        findViewById<TextView>(R.id.detailsLangueTxt).text = livreLangue ?: "Langue non disponible"
        findViewById<TextView>(R.id.detailsGenreTxt).text = livreGenre ?: "Genre non disponible"
        findViewById<TextView>(R.id.detailsPriceTxt).text = "$livrePrice $"

        // Ajouter un click listener pour le bouton favoris
        favBtn.setOnClickListener {
            isFavorite = !isFavorite
            updateFavoriteStatus()
            updateFavIcon()
        }

        // Charger l'image du livre avec Glide
        val detailsLivreImageView: ImageView = findViewById(R.id.livreImg)
        Glide.with(this).load(livrePicPath).into(detailsLivreImageView)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        // Affiche les livres favoris dans le Logcat
        val favorisList = getAllFavoriteLivres()
        for (id in favorisList) {
            Log.d("FavoriteLivrepp", "Livre favori ID: $id")
        }
    }

    private fun updateFavoriteStatus() {
        val editor = sharedPreferences.edit()
        if (isFavorite) {
            editor.putBoolean(livreId.toString(), true)
        } else {
            editor.remove(livreId.toString())
        }
        editor.apply()
    }

    private fun updateFavIcon() {
        favBtn.setImageResource(
            if (isFavorite) R.drawable.favorite_yellow else R.drawable.favorite_white
        )
    }
    private fun getAllFavoriteLivres(): List<Int> {
        val favoriteLivres = mutableListOf<Int>()
        for ((key, value) in sharedPreferences.all) {
            if (value is Boolean && value) {
                favoriteLivres.add(key.toInt())
            }
        }
        return favoriteLivres
    }

    private fun saveRating(rating: Float) {
        val editor = ratingPreferences.edit()
        editor.putFloat("rating_$livreId", rating)
        editor.apply()

        Log.d("BookRating", "Saved rating for book $livreId: $rating")

    }

}
