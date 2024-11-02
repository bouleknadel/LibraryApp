<?php
include_once '../racine.php';
include_once RACINE . '/service/LivreService.php';
extract($_GET);

// Créez une instance de LivreService
$ls = new LivreService();
// Supprimez le livre avec l'ID spécifié
$ls->delete($ls->findById($id));

// Redirigez vers la page d'index
header("location:../index.php");
