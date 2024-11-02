<?php
// Ajouter ces headers au début
header('Content-Type: application/json');
header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Methods: POST");
header("Access-Control-Allow-Headers: Content-Type");

if ($_SERVER["REQUEST_METHOD"] == "POST") {
    include_once '../racine.php';
    include_once RACINE . '/service/LoginService.php';
    loadAll();
}

function loadAll()
{
    $es = new LoginService();
    echo json_encode($es->login());
}
