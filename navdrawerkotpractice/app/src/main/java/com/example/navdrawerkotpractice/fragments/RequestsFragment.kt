package com.example.navdrawerkotpractice.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.example.navdrawerkotpractice.R
import com.example.navdrawerkotpractice.Adapter.DemandeBookAdapter
import com.example.navdrawerkotpractice.Domain.DemandeBook
import org.json.JSONObject

class RequestsFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_requests, container, false)

        recyclerView = view.findViewById(R.id.recyclerView)
        progressBar = view.findViewById(R.id.progressBar)

        recyclerView.layoutManager = LinearLayoutManager(context)

        loadDemandes()

        return view
    }

    private fun loadDemandes() {
        val url = "http://192.168.0.182/livreApp/ws/loadDemandeUserLivre.php"

        val jsonArrayRequest = JsonArrayRequest(
            Request.Method.POST, url, null,
            { response ->
                val demandes = mutableListOf<DemandeBook>()

                for (i in 0 until response.length()) {
                    val jsonObject = response.getJSONObject(i)
                    demandes.add(parseDemande(jsonObject))
                }

                progressBar.visibility = View.GONE
                recyclerView.adapter = DemandeBookAdapter(demandes)
            },
            { error ->
                progressBar.visibility = View.GONE
                Toast.makeText(context, "Erreur de chargement: ${error.message}", Toast.LENGTH_LONG).show()
            }
        )

        // On ajoute la requête à la file d'attente de Volley
        context?.let {
            Volley.newRequestQueue(it).add(jsonArrayRequest)
        }
    }

    private fun parseDemande(jsonObject: JSONObject): DemandeBook {
        return DemandeBook(
            id_demande = jsonObject.getInt("id_demande"),
            titre = jsonObject.getString("titre"),
            genre = jsonObject.getString("genre"),
            date_demande = jsonObject.getString("date_demande"),
            picPath = jsonObject.getString("picPath"),
            nom = jsonObject.getString("nom"),
            prenom = jsonObject.getString("prenom"),
            email = jsonObject.getString("email")
        )
    }
}