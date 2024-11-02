<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: POST');
header('Access-Control-Allow-Headers: Content-Type');

if ($_SERVER["REQUEST_METHOD"] == "POST") {
    include_once '../racine.php';
    include_once RACINE . '/service/LivreService.php';
    delete();
} else {
    echo json_encode(['error' => 'Méthode non autorisée. Utilisez POST.']);
    exit;
}

function delete()
{
    // Lire le contenu de la requête JSON
    $data = json_decode(file_get_contents("php://input"), true);

    // Vérifier si l'ID du livre est présent dans les données POST
    if (!isset($data['id']) || empty($data['id'])) {
        echo json_encode([
            'error' => 'Champs manquants ou vides',
            'missing_fields' => ['id']
        ]);
        exit;
    }

    try {
        $ls = new LivreService();

        // Créer un objet Livre avec l'ID à supprimer
        $livre = new Livre(
            intval($data['id']), // Convertir l'ID en entier
            null, // Pas besoin de fournir d'autres informations
            null,
            null,
            null,
            null,
            null,
            null
        );

        // Appeler la méthode delete de LivreService
        $ls->delete($livre);

        echo json_encode([
            'success' => true,
            'message' => 'Livre supprimé avec succès',
            'data' => $ls->findAllApi() // Retourner la liste mise à jour des livres
        ]);
    } catch (Exception $e) {
        echo json_encode([
            'error' => 'Erreur lors de la suppression du livre',
            'message' => $e->getMessage()
        ]);
    }
}
