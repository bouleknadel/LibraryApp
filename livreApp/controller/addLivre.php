<?php
include_once '../racine.php';
include_once RACINE . '/service/LivreService.php';
extract($_GET);

// Vérifiez si tous les champs requis sont présents
    $ls = new LivreService();
    // Créez un nouvel objet Livre
    $livre = new Livre(NULL, $titre, $auteur, $genre, $langue, $price, $description, $picPath);
    // Ajoutez le livre via le service
    $ls->create($livre);


// Redirigez vers la page d'index
header("location:../index.php");
