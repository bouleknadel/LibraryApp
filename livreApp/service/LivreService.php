<?php

header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: POST');
header('Access-Control-Allow-Headers: Content-Type');
include_once RACINE . '/classes/Livre.php'; // Inclure la classe Livre
include_once RACINE . '/connexion/Connexion.php'; // Inclure la classe de connexion
include_once RACINE . '/dao/IDao.php'; // Inclure l'interface IDao

class LivreService implements IDao
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
        $query = "INSERT INTO livre (titre, auteur, genre, langue, price, description, picPath) "
            . "VALUES (:titre, :auteur, :genre, :langue, :price, :description, :picPath)";

        $req = $this->connexion->getConnexion()->prepare($query);
        $req->execute([
            ':titre' => $o->getTitre(),
            ':auteur' => $o->getAuteur(),
            ':genre' => $o->getGenre(),
            ':langue' => $o->getLangue(),
            ':price' => $o->getPrice(),
            ':description' => $o->getDescription(),
            ':picPath' => $nom_image_bdd // Utiliser le chemin enregistré
        ]) or die('Erreur SQL lors de l\'insertion du livre');
    }


    // Méthode pour supprimer un livre
    public function delete($o)
    {
        $query = "DELETE FROM livre WHERE id = " . $o->getId();
        $req = $this->connexion->getConnexion()->prepare($query);
        $req->execute() or die('Erreur SQL lors de la suppression');
    }

    // Méthode pour trouver tous les livres
    public function findAll()
    {
        $livres = array();
        $query = "SELECT * FROM livre";
        $req = $this->connexion->getConnexion()->prepare($query);
        $req->execute();
        while ($e = $req->fetch(PDO::FETCH_OBJ)) {
            $livres[] = new Livre(
                $e->id,
                $e->titre,
                $e->auteur,
                $e->genre,
                $e->langue,
                $e->price,
                $e->description,
                $e->picPath
            );
        }
        return $livres;
    }

    // Méthode pour trouver un livre par son ID
    public function findById($id)
    {
        $query = "SELECT * FROM livre WHERE id = " . $id;
        $req = $this->connexion->getConnexion()->prepare($query);
        $req->execute();
        if ($e = $req->fetch(PDO::FETCH_OBJ)) {
            $livre = new Livre(
                $e->id,
                $e->titre,
                $e->auteur,
                $e->genre,
                $e->langue,
                $e->price,
                $e->description,
                $e->picPath
            );
        }
        return isset($livre) ? $livre : null; // Retourner null si aucun livre n'est trouvé
    }

    // Méthode pour mettre à jour un livre
    public function update($o)
    {
        // Obtenir l'image en base64
        $image = $o->getPicPath();
        $nom_image_bdd = $o->getPicPath(); // Chemin initial (non modifié)

        if ($image) { // Si une nouvelle image est fournie, on la sauvegarde
            $nom_image = time() . '.jpg';
            $chemin_upload = __DIR__ . '/../uploads/' . $nom_image;

            // Créer le dossier 'uploads' s'il n'existe pas
            if (!file_exists(__DIR__ . '/../uploads/')) {
                mkdir(__DIR__ . '/../uploads/', 0777, true);
            }

            // Décoder et sauvegarder l'image
            $donnees_image = base64_decode(preg_replace('#^data:image/\w+;base64,#i', '', $image));
            if (file_put_contents($chemin_upload, $donnees_image)) {
                $nom_image_bdd = 'uploads/' . $nom_image;
            } else {
                die('Erreur lors de la sauvegarde de l\'image'); // Gestion d'erreur si la sauvegarde échoue
            }
        }

        // Préparer et exécuter la requête de mise à jour
        $query = "UPDATE livre SET titre = :titre, auteur = :auteur, genre = :genre, langue = :langue, price = :price, description = :description WHERE id = :id";
        $req = $this->connexion->getConnexion()->prepare($query);
        $req->execute([
            ':titre' => $o->getTitre(),
            ':auteur' => $o->getAuteur(),
            ':genre' => $o->getGenre(),
            ':langue' => $o->getLangue(),
            ':price' => $o->getPrice(),
            ':description' => $o->getDescription(),
            ':id' => $o->getId()
        ]) or die('Erreur SQL lors de la mise à jour');
    }


    public function findAllApi()
    {
        $query = "SELECT * FROM Livre"; // Remplacez 'Livre' par le nom de votre table de livres
        $req = $this->connexion->getConnexion()->prepare($query);
        $req->execute();
        return $req->fetchAll(PDO::FETCH_ASSOC);
    }
}
