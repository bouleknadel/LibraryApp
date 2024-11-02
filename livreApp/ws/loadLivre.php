<?php
if ($_SERVER["REQUEST_METHOD"] == "POST") {
    include_once '../racine.php';
    include_once RACINE . '/service/LivreService.php';
    loadAll();
}

function loadAll()
{
    $ls = new LivreService();
    header('Content-type: application/json');
    echo json_encode($ls->findAllApi());
}
