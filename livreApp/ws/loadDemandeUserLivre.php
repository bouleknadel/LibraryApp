<?php
if ($_SERVER["REQUEST_METHOD"] == "POST") {
    include_once '../racine.php';
    include_once RACINE . '/service/DemandeLivreUserService.php';

    loadDemandeLivre();
}

function loadDemandeLivre()
{
    $dls = new DemandeLivreUserService();

    // Chargez les demandes de livres avec les informations des utilisateurs
    $demandes = $dls->load();

    // Définir l'en-tête de la réponse pour JSON
    header('Content-Type: application/json');

    // Vérifier si des demandes ont été chargées
    if (!empty($demandes)) {
        // Si des demandes existent, les renvoyer en format JSON
        echo json_encode($demandes);
    } else {
        // Si aucune demande n'existe, renvoyer un tableau vide
        echo json_encode([]);
    }
}
