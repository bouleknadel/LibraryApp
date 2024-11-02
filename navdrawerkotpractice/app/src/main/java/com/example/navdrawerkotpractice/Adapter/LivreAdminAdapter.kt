package com.example.navdrawerkotpractice.Adapter

import android.app.AlertDialog
import android.content.Context
import android.util.Log
import com.android.volley.DefaultRetryPolicy

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.example.navdrawerkotpractice.Domain.LivreDomain
import com.example.navdrawerkotpractice.R
import org.json.JSONObject

class LivreAdminAdapter(
    private val items: ArrayList<LivreDomain>,
    private val context: Context,
    private val onUpdateSuccess: () -> Unit
) : RecyclerView.Adapter<LivreAdminAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context).inflate(R.layout.viewholder_list3, parent, false)
        return ViewHolder(inflater)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = items[position]
        holder.title.text = currentItem.titre
        holder.genre.text = currentItem.genre
        holder.price.text = "$${currentItem.price}"

        Glide.with(context)
            .load(currentItem.picPath)
            .into(holder.pic)

        // Configuration du bouton de suppression
        holder.deleteButton.setOnClickListener {
            showDeleteConfirmationDialog(currentItem)
        }
        // Configuration des boutons
        holder.editButton.setOnClickListener {
            showUpdateDialog(currentItem)
        }

        // Configuration du fond selon la position
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
    }

    private fun showDeleteConfirmationDialog(livre: LivreDomain) {
        Log.d("DELETE", "le id de livre que vous voulez supprimes est "+livre.id)
        AlertDialog.Builder(context)
            .setTitle("Confirmer la suppression")
            .setMessage("Êtes-vous sûr de vouloir supprimer ce livre ?")
            .setPositiveButton("Oui") { dialog, _ ->
                deleteLivre(livre.id)
                dialog.dismiss()
            }
            .setNegativeButton("Non", null)
            .show()
    }

    private fun deleteLivre(livreId: Int) {
        val url = "http://192.168.0.182/LivreApp/ws/deleteLivre.php"

        val request = JsonObjectRequest(
            Request.Method.POST, url, JSONObject().apply {
                put("id", livreId)
            },
            { response ->
                if (response.optBoolean("success", false)) {
                    Toast.makeText(context, "Livre supprimé avec succès", Toast.LENGTH_SHORT).show()
                    items.removeAll { it.id == livreId }
                    notifyDataSetChanged() // Recharger la liste
                } else {
                    Toast.makeText(context, "Erreur lors de la suppression", Toast.LENGTH_SHORT).show()
                }
            },
            { error ->
                Log.e("DeleteLivre", "Erreur: ${error.message}", error)
                Toast.makeText(context, "Erreur réseau", Toast.LENGTH_SHORT).show()
            }
        ).apply {
            retryPolicy = DefaultRetryPolicy(
                30000, // 30 secondes timeout
                0,     // pas de retry
                1.0f   // pas de backoff multiplier
            )
        }

        Volley.newRequestQueue(context).add(request)
    }

    private fun showUpdateDialog(livre: LivreDomain) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_update_livre, null)

        // Initialisation des champs du dialogue
        val titleEdit = dialogView.findViewById<EditText>(R.id.editTitle)
        val authorEdit = dialogView.findViewById<EditText>(R.id.editAuthor)
        val genreEdit = dialogView.findViewById<EditText>(R.id.editGenre)
        val langueEdit = dialogView.findViewById<EditText>(R.id.editLangue)
        val priceEdit = dialogView.findViewById<EditText>(R.id.editPrice)
        val descriptionEdit = dialogView.findViewById<EditText>(R.id.editDescription)

        // Remplir les champs avec les données actuelles
        titleEdit.setText(livre.titre)
        authorEdit.setText(livre.auteur)
        genreEdit.setText(livre.genre)
        langueEdit.setText(livre.langue)
        priceEdit.setText(livre.price.toString())
        descriptionEdit.setText(livre.description)

        AlertDialog.Builder(context)
            .setTitle("Modifier le livre")
            .setView(dialogView)
            .setPositiveButton("Mettre à jour") { dialog, _ ->
                val updatedLivre = JSONObject().apply {
                    put("id", livre.id)
                    put("titre", titleEdit.text.toString())
                    put("auteur", authorEdit.text.toString())
                    put("genre", genreEdit.text.toString())
                    put("langue", langueEdit.text.toString())
                    put("price", priceEdit.text.toString().toDouble())
                    put("description", descriptionEdit.text.toString())
                }
                updateLivre(updatedLivre)
            }
            .setNegativeButton("Annuler", null)
            .show()
    }
    private fun updateLivre(livreData: JSONObject) {
        val url = "http://192.168.0.182/LivreApp/ws/updateLivre.php"

        val request = JsonObjectRequest(
            Request.Method.POST, url, livreData,
            { response ->
                if (response.optBoolean("success", false)) {
                    Toast.makeText(context, "Livre mis à jour avec succès", Toast.LENGTH_SHORT).show()
                    onUpdateSuccess.invoke() // Recharger la liste
                } else {
                    Toast.makeText(context, "Erreur lors de la mise à jour", Toast.LENGTH_SHORT).show()
                }
            },
            { error ->
                Log.e("UpdateLivre", "Erreur: ${error.message}", error)
                Toast.makeText(context, "Erreur réseau", Toast.LENGTH_SHORT).show()
            }
        ).apply {
            retryPolicy = DefaultRetryPolicy(
                30000, // 30 secondes timeout
                0,     // pas de retry
                1.0f   // pas de backoff multiplier
            )
        }

        Volley.newRequestQueue(context).add(request)
    }

    override fun getItemCount(): Int = items.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.titleTxt)
        val genre: TextView = itemView.findViewById(R.id.genreTxt)
        val price: TextView = itemView.findViewById(R.id.priceTxt)
        val pic: ImageView = itemView.findViewById(R.id.pic)
        val backgroundImg: ImageView = itemView.findViewById(R.id.background_img)
        val layout: ConstraintLayout = itemView.findViewById(R.id.main_layout)
        val editButton: ImageButton = itemView.findViewById(R.id.editButton)
        val deleteButton: ImageButton = itemView.findViewById(R.id.deleteButton) // Ajoutez cette ligne
    }
}