<?php
if ($_SERVER["REQUEST_METHOD"] == "POST") {
    include_once '../racine.php';
    include_once RACINE . '/service/DemandeLivreService.php';
    create();
}
function create()
{
    // Vérifiez si les clés existent dans $_POST
    if (isset($_POST['titre'], $_POST['genre'], $_POST['picPath'], $_POST['idUser'])) {
        extract($_POST);
        $dls = new DemandeLivreService();

        // Créez un nouvel objet DemandeLivre avec les données reçues
        $demandeLivre = new DemandeLivre(NULL, $idUser, $titre, $genre, $picPath, date('Y-m-d H:i:s'));
        $dls->create($demandeLivre);

        header('Content-type: application/json');
        echo json_encode($dls->findAllApi());
    } else {
        // Retournez un message d'erreur si les données ne sont pas fournies
        header('Content-type: application/json');
        echo json_encode(['error' => 'Les données requises ne sont pas fournies.']);
    }
}

?>
