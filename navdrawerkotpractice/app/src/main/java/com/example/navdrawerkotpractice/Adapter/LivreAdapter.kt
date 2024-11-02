package com.example.navdrawerkotpractice.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.content.Intent
import android.widget.Filter
import android.widget.Filterable
import android.widget.EditText
import com.example.navdrawerkotpractice.DetailsLivreActivity
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.navdrawerkotpractice.Domain.LivreDomain
import com.example.navdrawerkotpractice.R
import java.util.*
import kotlin.collections.ArrayList

class LivreAdapter(
    private val items: ArrayList<LivreDomain>,
    private val context: Context
) : RecyclerView.Adapter<LivreAdapter.ViewHolder>(), Filterable {

    private var filteredList: ArrayList<LivreDomain> = ArrayList(items)
    private var minPrice: Double? = null
    private var maxPrice: Double? = null
    private var currentSearchQuery: String = ""


    // Nouvelle méthode pour mettre à jour la liste
    fun updateList(newItems: ArrayList<LivreDomain>) {
        items.clear()
        items.addAll(newItems)
        // Met également à jour la liste filtrée
        filteredList.clear()
        filteredList.addAll(newItems)
        filter.filter("") // Reset le filtre pour afficher tous les items
        notifyDataSetChanged()
    }

    // Ajout d'une référence à EditText
    private var searchEditText: EditText? = null




    // Méthode pour définir l'EditText
    fun setSearchEditText(editText: EditText) {
        searchEditText = editText
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
            .inflate(R.layout.viewholder_list2, parent, false)
        return ViewHolder(inflater)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = filteredList[position]
        holder.title.text = currentItem.titre
        holder.genre.text = currentItem.genre
        holder.price.text = "$${currentItem.price}"

        Glide.with(context)
            .load(currentItem.picPath)
            .into(holder.pic)

        when (position % 5) {
            0 -> {
                holder.backgroundImg.setImageResource(R.drawable.bg_1)
                holder.layout.setBackgroundResource(R.drawable.list_background_1)
            }
            1 -> {
                holder.backgroundImg.setImageResource(R.drawable.bg_2)
                holder.layout.setBackgroundResource(R.drawable.list_background_2)
            }
            2 -> {
                holder.backgroundImg.setImageResource(R.drawable.bg_3)
                holder.layout.setBackgroundResource(R.drawable.list_background_3)
            }
            3 -> {
                holder.backgroundImg.setImageResource(R.drawable.bg_4)
                holder.layout.setBackgroundResource(R.drawable.list_background_4)
            }
            4 -> {
                holder.backgroundImg.setImageResource(R.drawable.bg_5)
                holder.layout.setBackgroundResource(R.drawable.list_background_5)
            }
        }

        holder.detailsTextView.setOnClickListener {
            val intent = Intent(context, DetailsLivreActivity::class.java).apply {
                putExtra("livreId", currentItem.id)
                putExtra("livreTitre", currentItem.titre)
                putExtra("livreGenre", currentItem.genre)
                putExtra("livreLangue", currentItem.langue)
                putExtra("livreAuteur", currentItem.auteur)
                putExtra("livrePrice", currentItem.price)
                putExtra("livrePicPath", currentItem.picPath)
                putExtra("livreDescription", currentItem.description)
            }
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = filteredList.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.titleTxt)
        val genre: TextView = itemView.findViewById(R.id.genreTxt)
        val price: TextView = itemView.findViewById(R.id.priceTxt)
        val pic: ImageView = itemView.findViewById(R.id.pic)
        val backgroundImg: ImageView = itemView.findViewById(R.id.background_img)
        val layout: ConstraintLayout = itemView.findViewById(R.id.main_layout)
        val detailsTextView: TextView = itemView.findViewById(R.id.detailsTextView)
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val searchString = constraint?.toString()?.lowercase(Locale.getDefault()) ?: ""
                currentSearchQuery = searchString

                val filteredItems = if (searchString.isEmpty() && minPrice == null && maxPrice == null) {
                    ArrayList(items)
                } else {
                    items.filter { livre ->
                        val matchesSearch = livre.titre.lowercase(Locale.getDefault()).contains(searchString) ||
                                livre.genre.lowercase(Locale.getDefault()).contains(searchString)

                        val matchesPrice = if (minPrice != null && maxPrice != null) {
                            livre.price in minPrice!!..maxPrice!!
                        } else true

                        matchesSearch && matchesPrice
                    } as ArrayList<LivreDomain>
                }

                return FilterResults().apply {
                    values = filteredItems
                }
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filteredList = results?.values as ArrayList<LivreDomain>
                notifyDataSetChanged()
            }
        }
    }

    fun setPriceRange(min: Double, max: Double) {
        minPrice = min
        maxPrice = max
        // Utiliser la dernière requête de recherche stockée
        filter.filter(currentSearchQuery)
    }

    fun clearPriceRange() {
        minPrice = null
        maxPrice = null
        // Utiliser la dernière requête de recherche stockée
        filter.filter(currentSearchQuery)
    }
}