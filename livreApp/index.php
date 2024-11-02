<?php
include_once './racine.php'; // Assurez-vous que ce chemin est correct
include_once RACINE . '/service/LivreService.php'; // Inclure le service Livre

// Initialiser le service des livres
$ls = new LivreService();
?>

<!DOCTYPE html>
<html lang="fr">

<head>
    <meta charset="UTF-8">
    <title>Liste des Livres</title>
</head>

<body>
    <h1>Liste des Livres</h1>

    <form method="GET" action="controller/addLivre.php">
        <fieldset>
            <legend>Ajouter un nouveau livre</legend>
            <table border="0">
                <tr>
                    <td>Titre : </td>
                    <td><input type="text" name="titre" value="" /></td>
                </tr>
                <tr>
                    <td>Auteur :</td>
                    <td><input type="text" name="auteur" value="" /></td>
                </tr>
                <tr>
                    <td>Genre</td>
                    <td><input type="text" name="genre" value="" /></td>
                </tr>
                <tr>
                    <td>Langue </td>
                    <td><input type="text" name="langue" value="" /></td>
                </tr>
                <tr>
                    <td>Prix :</td>
                    <td><input type="text" name="price" value="" /></td>
                </tr>
                <tr>
                    <td>Description :</td>
                    <td><textarea name="description"></textarea></td>
                </tr>
                <tr>
                    <td>Image :</td>
                    <td><input type="text" name="picPath" value="" /></td>
                </tr>
                <tr>
                    <td></td>
                    <td>
                        <input type="submit" value="Ajouter" />
                        <input type="reset" value="Effacer" />
                    </td>
                </tr>
            </table>
        </fieldset>
    </form>

    <table border="1">
        <thead>
            <tr>
                <th>ID</th>
                <th>Titre</th>
                <th>Auteur</th>
                <th>Genre</th>
                <th>Langue</th>
                <th>Prix</th>
                <th>Description</th>
                <th>Image</th>
                <th>Supprimer</th>
                <th>Modifier</th>
            </tr>
        </thead>
        <tbody>
            <?php
            // Récupérer tous les livres
            foreach ($ls->findAll() as $livre) {
            ?>
                <tr>
                    <td><?php echo $livre->getId(); ?></td>
                    <td><?php echo $livre->getTitre(); ?></td>
                    <td><?php echo $livre->getAuteur(); ?></td>
                    <td><?php echo $livre->getGenre(); ?></td>
                    <td><?php echo $livre->getLangue(); ?></td>
                    <td><?php echo $livre->getPrice(); ?> MAD</td>
                    <td><?php echo $livre->getDescription(); ?></td>
                    <td><img src="<?php echo $livre->getPicPath(); ?>" alt="Image du livre" style="width:50px;height:50px;" /></td>
                    <td>
                        <a href="controller/deleteLivre.php?id=<?php echo $livre->getId(); ?>">Supprimer</a>
                    </td>
                    <td><a href="updateLivre.php?id=<?php echo $livre->getId(); ?>">Modifier</a></td>
                </tr>
            <?php } ?>
        </tbody>
    </table>
</body>

</html>