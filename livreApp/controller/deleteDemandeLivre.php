<?php
include_once '../racine.php';
include_once RACINE . '/service/DemandeLivreService.php'; // Inclure le service DemandeLivre

// Extraire l'ID de la demande à supprimer
extract($_GET);

// Créer une instance du service DemandeLivre
$dls = new DemandeLivreService();

// Supprimer la demande de livre par son ID
$dls->delete($dls->findById($id));

// Rediriger vers la page d'index
header("location:../index2.php");
exit; // Terminer le script après la redirection
