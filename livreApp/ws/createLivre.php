<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: POST');
header('Access-Control-Allow-Headers: Content-Type');

if ($_SERVER["REQUEST_METHOD"] == "POST") {
    include_once '../racine.php';
    include_once RACINE . '/service/LivreService.php';
    create();
} else {
    echo json_encode(['error' => 'Méthode non autorisée. Utilisez POST.']);
    exit;
}

function create()
{
    // Déboguer les données reçues
    $requiredFields = ['titre', 'auteur', 'genre', 'langue', 'price', 'description', 'picPath'];
    $missingFields = [];

    // Vérifier chaque champ requis
    foreach ($requiredFields as $field) {
        if (!isset($_POST[$field]) || empty($_POST[$field])) {
            $missingFields[] = $field;
        }
    }

    // Si des champs sont manquants, retourner l'erreur avec les détails
    if (!empty($missingFields)) {
        echo json_encode([
            'error' => 'Champs manquants ou vides',
            'missing_fields' => $missingFields,
            'received_data' => $_POST
        ]);
        exit;
    }

    try {
        extract($_POST);
        $ls = new LivreService();

        // Créer un nouvel objet Livre
        $livre = new Livre(
            NULL,
            htmlspecialchars($titre),
            htmlspecialchars($auteur),
            htmlspecialchars($genre),
            htmlspecialchars($langue),
            floatval($price),
            htmlspecialchars($description),
            $picPath
        );

        $ls->create($livre);

        echo json_encode([
            'success' => true,
            'message' => 'Livre créé avec succès',
            'data' => $ls->findAllApi()
        ]);
    } catch (Exception $e) {
        echo json_encode([
            'error' => 'Erreur lors de la création du livre',
            'message' => $e->getMessage()
        ]);
    }
}
