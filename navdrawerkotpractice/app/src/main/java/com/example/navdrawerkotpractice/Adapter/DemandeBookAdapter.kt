package com.example.navdrawerkotpractice.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.navdrawerkotpractice.R
import com.example.navdrawerkotpractice.Domain.DemandeBook
import java.text.SimpleDateFormat
import java.util.Locale

class DemandeBookAdapter(private val demandes: List<DemandeBook>) :
    RecyclerView.Adapter<DemandeBookAdapter.DemandeViewHolder>() {

    class DemandeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivLivre: ImageView = itemView.findViewById(R.id.ivLivre)
        val tvDemandeInfo: TextView = itemView.findViewById(R.id.tvDemandeInfo)
        val tvGenre: TextView = itemView.findViewById(R.id.tvGenre)
        val tvDate: TextView = itemView.findViewById(R.id.tvDate)
        val tvEmail: TextView = itemView.findViewById(R.id.tvEmail)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DemandeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_demande_livre, parent, false)
        return DemandeViewHolder(view)
    }

    override fun onBindViewHolder(holder: DemandeViewHolder, position: Int) {
        val demande = demandes[position]
        val context = holder.itemView.context

        // Charger l'image avec Glide
        val imageUrl = "http://192.168.0.182/LivreApp/${demande.picPath}"
        Glide.with(context)
            .load(imageUrl)
            .into(holder.ivLivre)

        // Formater la date
        val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd MMMM yyyy à HH:mm", Locale.FRANCE)
        val date = inputFormat.parse(demande.date_demande)
        val dateFormatted = date?.let { outputFormat.format(it) } ?: demande.date_demande

        // Mettre à jour les textes
        holder.tvDemandeInfo.text = context.getString(
            R.string.demande_info_format,
            demande.nom,
            demande.prenom,
            demande.titre
        )
        holder.tvGenre.text = context.getString(R.string.genre_format, demande.genre)
        holder.tvDate.text = context.getString(R.string.date_format, dateFormatted)
        holder.tvEmail.text = demande.email
    }

    override fun getItemCount() = demandes.size
}