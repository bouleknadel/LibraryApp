<?php
include_once RACINE . '/classes/DemandeLivre.php'; // Inclure la classe DemandeLivre
include_once RACINE . '/connexion/Connexion.php'; // Inclure la classe de connexion
include_once RACINE . '/dao/IDao.php'; // Inclure l'interface IDao

class DemandeLivreService implements IDao
{
    private $connexion;

    // Constructeur pour initialiser la connexion
    function __construct()
    {
        $this->connexion = new Connexion();
    }

    public function create($o)
    {
        // Obtenir l'image en base64
        $image = $o->getPicPath(); // Supposons que cette méthode retourne l'image au format base64
        $nom_image = time() . '.jpg';  // Générer un nom unique pour l'image

        // Correction du chemin d'upload
        $chemin_upload = __DIR__ . '/../uploads/' . $nom_image;

        // Assurez-vous que le dossier uploads existe
        if (!file_exists(__DIR__ . '/../uploads/')) {
            mkdir(__DIR__ . '/../uploads/', 0777, true);
        }

        // Décoder l'image base64
        $donnees_image = base64_decode(preg_replace('#^data:image/\w+;base64,#i', '', $image));

        // Sauvegarder le fichier image
        if (file_put_contents($chemin_upload, $donnees_image)) {
            // L'image a été sauvegardée avec succès
            $nom_image_bdd = 'uploads/' . $nom_image; // Chemin relatif pour la BDD
        } else {
            // Erreur lors de la sauvegarde de l'image
            $nom_image_bdd = ''; // Ou gérer l'erreur comme vous le souhaitez
        }

        // Insérer les données dans la base de données
        $query = "INSERT INTO demandeLivre (id_user, titre, genre, picPath) VALUES (:id_user, :titre, :genre, :picPath)";
        $req = $this->connexion->getConnexion()->prepare($query);
        $req->execute([
            ':id_user' => $o->getIdUser(),
            ':titre' => $o->getTitre(),
            ':genre' => $o->getGenre(),
            ':picPath' => $nom_image_bdd // Utiliser le chemin enregistré
        ]) or die('Erreur SQL lors de l\'insertion de la demande de livre');
    }


    // Méthode pour supprimer une demande de livre
    public function delete($o)
    {
        $query = "DELETE FROM demandeLivre WHERE id_demande = :id_demande";
        $req = $this->connexion->getConnexion()->prepare($query);
        $req->execute([':id_demande' => $o->getIdDemande()]) or die('Erreur SQL lors de la suppression de la demande de livre');
    }

    // Méthode pour trouver toutes les demandes de livres
    public function findAll()
    {
        $demandes = array();
        $query = "SELECT * FROM demandeLivre";
        $req = $this->connexion->getConnexion()->prepare($query);
        $req->execute();
        while ($e = $req->fetch(PDO::FETCH_OBJ)) {
            $demandes[] = new DemandeLivre(
                $e->id_demande,
                $e->id_user,
                $e->titre,
                $e->genre,
                $e->picPath, // Changement ici
                $e->date_demande
            );
        }
        return $demandes;
    }

    // Méthode pour trouver une demande de livre par son ID
    public function findById($id)
    {
        $query = "SELECT * FROM demandeLivre WHERE id_demande = :id_demande";
        $req = $this->connexion->getConnexion()->prepare($query);
        $req->execute([':id_demande' => $id]);
        if ($e = $req->fetch(PDO::FETCH_OBJ)) {
            return new DemandeLivre(
                $e->id_demande,
                $e->id_user,
                $e->titre,
                $e->genre,
                $e->picPath, // Changement ici
                $e->date_demande
            );
        }
        return null; // Retourner null si aucune demande de livre n'est trouvée
    }

    // Méthode pour mettre à jour une demande de livre
    public function update($o)
    {
        $query = "UPDATE demandeLivre SET titre = :titre, genre = :genre, picPath = :picPath WHERE id_demande = :id_demande";
        $req = $this->connexion->getConnexion()->prepare($query);
        $req->execute([
            ':titre' => $o->getTitre(),
            ':genre' => $o->getGenre(),
            ':picPath' => $o->getPicPath(), // Changement ici
            ':id_demande' => $o->getIdDemande()
        ]) or die('Erreur SQL lors de la mise à jour de la demande de livre');
    }

    // Méthode pour trouver toutes les demandes de livres via une API (facultatif)
    public function findAllApi()
    {
        $query = "SELECT * FROM demandeLivre"; // Remplacez 'demandeLivre' par le nom de votre table de demandes de livres
        $req = $this->connexion->getConnexion()->prepare($query);
        $req->execute();
        return $req->fetchAll(PDO::FETCH_ASSOC);
    }
}
