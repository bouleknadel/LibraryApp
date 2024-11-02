<?php
include_once RACINE . '/classes/DemandeLivre.php'; // Inclure la classe DemandeLivre
include_once RACINE . '/connexion/Connexion.php'; // Inclure la classe de connexion

class DemandeLivreUserService
{
    private $connexion;

    // Constructeur pour initialiser la connexion
    function __construct()
    {
        $this->connexion = new Connexion();
    }

    // Méthode pour charger toutes les demandes de livres avec les informations de l'utilisateur
    public function load()
    {
        $demandes = array();

        // Requête avec jointure pour inclure le champ picPath
        $query = "SELECT dl.id_demande, dl.titre, dl.genre, dl.date_demande,
                         dl.picPath, u.nom, u.prenom, u.email 
                  FROM demandeLivre dl 
                  JOIN users u ON dl.id_user = u.id_user";

        $req = $this->connexion->getConnexion()->prepare($query);
        $req->execute();

        while ($e = $req->fetch(PDO::FETCH_OBJ)) {
            // Créer un tableau associatif avec les informations de la demande et de l'utilisateur
            $demande = array(
                'id_demande' => $e->id_demande,
                'titre' => $e->titre,
                'genre' => $e->genre,
                'date_demande' => $e->date_demande,
                'picPath' => $e->picPath, // Ajouter picPath ici
                'nom' => $e->nom,
                'prenom' => $e->prenom,
                'email' => $e->email,
            );

            // Ajouter le tableau à la liste des demandes
            $demandes[] = $demande;
        }

        return $demandes;
    }
}
