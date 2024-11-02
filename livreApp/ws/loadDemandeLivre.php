<?php
if ($_SERVER["REQUEST_METHOD"] == "POST") {
    include_once '../racine.php';
    include_once RACINE . '/service/DemandeLivreService.php';
    loadAll();
}

function loadAll()
{
    $dls = new DemandeLivreService(); // Créez une instance de DemandeLivreService
    header('Content-type: application/json'); // Définir le type de contenu en JSON
    echo json_encode($dls->findAllApi()); // Retourne toutes les demandes au format JSON
}
