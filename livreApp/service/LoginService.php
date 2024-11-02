<?php
include_once RACINE . '/connexion/Connexion.php';

class LoginService
{
    private $conn;

    function __construct()
    {
        $this->conn = new Connexion();
    }

    public function login()
    {
        // Récupérer les données JSON envoyées
        $json = file_get_contents('php://input');
        $data = json_decode($json, true);

        // Récupérer email et mot de passe depuis le JSON
        $email = $data["email"];
        $password = $data["mdps"];

        $stmt = $this->conn->getConnexion()->prepare("SELECT * FROM users WHERE email = :email");
        $stmt->bindParam(':email', $email);
        $stmt->execute();

        $user = $stmt->fetch(PDO::FETCH_ASSOC);

        if ($user && $password == $user['mdps']) {
            return [
                "success" => true,
                "message" => "Connexion réussie",
                "id" => $user['id_user'],
                "nom" => $user['nom'] . " " . $user["prenom"],
                "email" => $user["email"],
                "role" => $user["role"],
                "picPath" => $user["picPath"] // Ajout du picPath
            ];
        } else {
            return [
                "success" => false,
                "message" => "Email ou mot de passe incorrect"
            ];
        }
    }

    public function registerUser()
    {
        // Récupérer les données JSON envoyées
        $json = file_get_contents('php://input');

        // Ajouter une vérification du contenu
        if (empty($json)) {
            return [
                "success" => false,
                "message" => "Aucune donnée reçue"
            ];
        }

        $data = json_decode($json, true);

        // Décoder avec vérification d'erreur
        if (json_last_error() !== JSON_ERROR_NONE) {
            return [
                "success" => false,
                "message" => "Erreur de format JSON: " . json_last_error_msg()
            ];
        }

        // Vérifier que toutes les données requises sont présentes
        if (
            !isset($data["nom"]) || !isset($data["prenom"]) ||
            !isset($data["email"]) || !isset($data["mdps"]) ||
            !isset($data["picPath"])  // Ajouter la vérification pour picPath
        ) {
            return [
                "success" => false,
                "message" => "Données manquantes"
            ];
        }

        // Extraire les informations de l'utilisateur depuis le JSON
        $nom = $data["nom"];
        $prenom = $data["prenom"];
        $email = $data["email"];
        $mdps = $data["mdps"];
        $image = $data["picPath"]; // Récupérer l'image en base64

        // Gérer l'upload de l'image
        $nom_image = time() . '.jpg';  // Générer un nom unique pour l'image

        // Correction du chemin d'upload
        $chemin_upload = __DIR__ . '/../uploads/users/' . $nom_image;

        // Assurez-vous que le dossier uploads/users existe
        if (!file_exists(__DIR__ . '/../uploads/users/')) {
            mkdir(__DIR__ . '/../uploads/users/', 0777, true);
        }

        // Décoder l'image base64
        $donnees_image = base64_decode(preg_replace('#^data:image/\w+;base64,#i', '', $image));

        // Initialiser le chemin de l'image pour la BDD
        $nom_image_bdd = '';

        // Sauvegarder le fichier image
        if (file_put_contents($chemin_upload, $donnees_image)) {
            $nom_image_bdd = 'uploads/users/' . $nom_image; // Chemin relatif pour la BDD
        }

        // Vérifier si l'utilisateur existe déjà
        $stmt = $this->conn->getConnexion()->prepare("SELECT * FROM users WHERE email = :email");
        $stmt->bindParam(':email', $email);
        $stmt->execute();

        if ($stmt->rowCount() > 0) {
            return [
                "success" => false,
                "message" => "L'email est déjà utilisé"
            ];
        } else {
            // Insérer le nouvel utilisateur avec l'image
            $insertStmt = $this->conn->getConnexion()->prepare(
                "INSERT INTO users (nom, prenom, email, mdps, role, picPath) 
             VALUES (:nom, :prenom, :email, :mdps, 1, :picPath)"
            );
            $insertStmt->bindParam(':email', $email);
            $insertStmt->bindParam(':nom', $nom);
            $insertStmt->bindParam(':prenom', $prenom);
            $insertStmt->bindParam(':mdps', $mdps);
            $insertStmt->bindParam(':picPath', $nom_image_bdd);

            if ($insertStmt->execute()) {
                // Récupérer l'ID du dernier utilisateur inséré
                $lastId = $this->conn->getConnexion()->lastInsertId();

                return [
                    "success" => true,
                    "message" => "Inscription réussie",
                    "id_user" => $lastId,
                    "picPath" => $nom_image_bdd  // Renvoyer aussi le chemin de l'image
                ];
            } else {
                return [
                    "success" => false,
                    "message" => "Erreur lors de l'inscription"
                ];
            }
        }
    }

    public function updateUser()
    {
        // Récupérer les données JSON envoyées
        $json = file_get_contents('php://input');

        // Vérifier si des données ont été reçues
        if (empty($json)) {
            return [
                "success" => false,
                "message" => "Aucune donnée reçue"
            ];
        }

        $data = json_decode($json, true);

        // Vérifier les erreurs de décodage JSON
        if (json_last_error() !== JSON_ERROR_NONE) {
            return [
                "success" => false,
                "message" => "Erreur de format JSON: " . json_last_error_msg()
            ];
        }

        // Vérifier que toutes les données requises sont présentes
        if (!isset($data["id"]) || !isset($data["nom"]) || !isset($data["prenom"]) || !isset($data["email"]) || !isset($data["mdps"])) {
            return [
                "success" => false,
                "message" => "Données manquantes"
            ];
        }

        // Extraire les informations de l'utilisateur depuis le JSON
        $id = $data["id"];
        $nom = $data["nom"];
        $prenom = $data["prenom"];
        $email = $data["email"];
        $mdps = $data["mdps"];

        // Vérifier si l'email existe déjà (sauf pour l'utilisateur qui l'update)
        $stmt = $this->conn->getConnexion()->prepare("SELECT * FROM users WHERE email = :email AND id_user != :id");
        $stmt->bindParam(':email', $email);
        $stmt->bindParam(':id', $id);
        $stmt->execute();

        if ($stmt->rowCount() > 0) {
            return [
                "success" => false,
                "message" => "L'email est déjà utilisé par un autre utilisateur"
            ];
        } else {
            // Récupérer l'ancien mot de passe si le nouveau est vide
            $stmtOldPassword = $this->conn->getConnexion()->prepare("SELECT mdps FROM users WHERE id_user = :id");
            $stmtOldPassword->bindParam(':id', $id);
            $stmtOldPassword->execute();
            $oldPassword = $stmtOldPassword->fetchColumn();

            // Si le nouveau mot de passe est vide, garder l'ancien mot de passe
            if (empty(trim($mdps))) {
                $mdps = $oldPassword;
            }

            // Mettre à jour l'utilisateur
            $updateStmt = $this->conn->getConnexion()->prepare("UPDATE users SET nom = :nom, prenom = :prenom, email = :email, mdps = :mdps WHERE id_user = :id");
            $updateStmt->bindParam(':nom', $nom);
            $updateStmt->bindParam(':prenom', $prenom);
            $updateStmt->bindParam(':email', $email);
            $updateStmt->bindParam(':mdps', $mdps);
            $updateStmt->bindParam(':id', $id);

            if ($updateStmt->execute()) {
                return [
                    "success" => true,
                    "message" => "Mise à jour réussie"
                ];
            } else {
                return [
                    "success" => false,
                    "message" => "Erreur lors de la mise à jour"
                ];
            }
        }
    }

}
