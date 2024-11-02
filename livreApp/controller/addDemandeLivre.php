<?php
include_once '../racine.php';
include_once RACINE . '/service/DemandeLivreService.php';

// Extraire les paramètres de la requête POST
$titre = $_POST['titre'] ?? null;
$genre = $_POST['genre'] ?? null;
$picPath = null;

if (isset($_FILES['picPath']) && $_FILES['picPath']['error'] == 0) {
    $picPath = 'uploads/' . basename($_FILES['picPath']['name']);
    move_uploaded_file($_FILES['picPath']['tmp_name'], $picPath);
} else {
    die('Erreur lors du téléchargement de l\'image');
}

// Vérifiez si tous les champs requis sont présents
if ($titre && $genre && $picPath) {
    $dls = new DemandeLivreService();

    // Créez un nouvel objet DemandeLivre
    $demandeLivre = new DemandeLivre(NULL, 1, $titre, $genre, $picPath, date('Y-m-d H:i:s'));

    // Ajoutez la demande de livre via le service
    $dls->create($demandeLivre);
}

// Redirigez vers la page d'index
header("location:../index2.php");
exit;
