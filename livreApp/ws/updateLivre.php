<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: POST');
header('Access-Control-Allow-Headers: Content-Type');

// Récupérer le JSON envoyé
$json = file_get_contents('php://input');
$data = json_decode($json, true);

if ($_SERVER["REQUEST_METHOD"] == "POST") {
    include_once '../racine.php';
    include_once RACINE . '/service/LivreService.php';
    
    if ($data === null) {
        echo json_encode([
            'success' => false,
            'error' => 'Données JSON invalides',
            'received' => $json
        ]);
        exit;
    }
    
    update($data);
} else {
    echo json_encode(['success' => false, 'error' => 'Méthode non autorisée. Utilisez POST.']);
    exit;
}

function update($data)
{
    if (!isset($data['id'])) {
        echo json_encode([
            'success' => false,
            'error' => 'ID manquant',
            'received_data' => $data
        ]);
        exit;
    }

    $ls = new LivreService();

    try {
        $id = intval($data['id']);
        $livre = $ls->findById($id);

        if (!$livre) {
            echo json_encode([
                'success' => false,
                'error' => 'Livre non trouvé avec cet ID: ' . $id
            ]);
            exit;
        }

        // Mise à jour des champs
        if (isset($data['titre'])) $livre->setTitre(htmlspecialchars($data['titre']));
        if (isset($data['auteur'])) $livre->setAuteur(htmlspecialchars($data['auteur']));
        if (isset($data['genre'])) $livre->setGenre(htmlspecialchars($data['genre']));
        if (isset($data['langue'])) $livre->setLangue(htmlspecialchars($data['langue']));
        if (isset($data['price'])) $livre->setPrice(floatval($data['price']));
        if (isset($data['description'])) $livre->setDescription(htmlspecialchars($data['description']));
        if (isset($data['picPath'])) $livre->setPicPath($data['picPath']);

        $ls->update($livre);

        echo json_encode([
            'success' => true,
            'message' => 'Livre mis à jour avec succès',
            'data' => [
                'id' => $livre->getId(),
                'titre' => $livre->getTitre(),
                'auteur' => $livre->getAuteur(),
                'genre' => $livre->getGenre(),
                'langue' => $livre->getLangue(),
                'price' => $livre->getPrice(),
                'description' => $livre->getDescription(),
                'picPath' => $livre->getPicPath()
            ]
        ]);
    } catch (Exception $e) {
        error_log("Erreur lors de la mise à jour: " . $e->getMessage());
        echo json_encode([
            'success' => false,
            'error' => 'Erreur lors de la mise à jour du livre',
            'message' => $e->getMessage()
        ]);
    }
}