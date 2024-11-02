package com.example.navdrawerkotpractice.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.util.Log
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.navdrawerkotpractice.DetailsLivreActivity
import com.example.navdrawerkotpractice.Domain.LivreDomain
import com.example.navdrawerkotpractice.R

class FavoriteBookAdapter(
    private val context: Context,
    private var favoriteBooks: List<FavoriteBook>
) : RecyclerView.Adapter<FavoriteBookAdapter.ViewHolder>() {

    data class FavoriteBook(
        val livre: LivreDomain,
        val rating: Float
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.viewholder_favorite_book, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val book = favoriteBooks[position]

        holder.apply {
            bookTitle.text = book.livre.titre
            ratingBar.rating = book.rating
            ratingText.text = "(${book.rating})"

            Log.d("ImageLoading", "Tentative de chargement de l'image: ${book.livre.picPath}")


            Glide.with(context)
                .load(book.livre.picPath)
                .into(bookImage)

            // Click listener pour ouvrir les détails
            itemView.setOnClickListener {
                val intent = Intent(context, DetailsLivreActivity::class.java).apply {
                    putExtra("livreId", book.livre.id)
                    putExtra("livreTitre", book.livre.titre)
                    putExtra("livreGenre", book.livre.genre)
                    putExtra("livreLangue", book.livre.langue)
                    putExtra("livreAuteur", book.livre.auteur)
                    putExtra("livrePrice", book.livre.price)
                    putExtra("livrePicPath", book.livre.picPath)
                    putExtra("livreDescription", book.livre.description)
                }
                context.startActivity(intent)
            }
        }
    }

    override fun getItemCount() = favoriteBooks.size

    fun updateBooks(newBooks: List<FavoriteBook>) {
        favoriteBooks = newBooks
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val bookImage: ImageView = itemView.findViewById(R.id.bookImage)
        val bookTitle: TextView = itemView.findViewById(R.id.bookTitle)
        val ratingBar: RatingBar = itemView.findViewById(R.id.ratingBar)
        val ratingText: TextView = itemView.findViewById(R.id.ratingText)
    }
}