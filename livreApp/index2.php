<!DOCTYPE html>
<?php
include_once './racine.php';
?>
<html>

<head>
    <meta charset="UTF-8">
    <title>Gestion des Demandes de Livres</title>
</head>

<body>
    <form method="POST" action="controller/addDemandeLivre.php" enctype="multipart/form-data">
        <fieldset>
            <legend>Ajouter une nouvelle demande de livre</legend>
            <table border="0">
                <tr>
                    <td>Titre : </td>
                    <td><input type="text" name="titre" value="" required /></td>
                </tr>
                <tr>
                    <td>Genre :</td>
                    <td>
                        <input type="text" name="genre" value="" required />
                    </td>
                </tr>
                <tr>
                    <td>Image du livre :</td>
                    <td><input type="file" name="picPath" accept="image/*" required /></td>
                </tr>
                <tr>
                    <td></td>
                    <td>
                        <input type="submit" value="Envoyer" />
                        <input type="reset" value="Effacer" />
                    </td>
                </tr>
            </table>
        </fieldset>
    </form>

    <table border="1">
        <thead>
            <tr>
                <th>ID Demande</th>
                <th>ID Utilisateur</th>
                <th>Titre</th>
                <th>Genre</th>
                <th>Image</th>
                <th>Date de Demande</th>
                <th>Supprimer</th>
                <th>Modifier</th>
            </tr>
        </thead>
        <tbody>
            <?php
            include_once RACINE . '/service/DemandeLivreService.php';
            $dls = new DemandeLivreService();
            foreach ($dls->findAll() as $demande) {
            ?>
                <tr>
                    <td><?php echo $demande->getIdDemande(); ?></td>
                    <td><?php echo $demande->getIdUser(); ?></td>
                    <td><?php echo $demande->getTitre(); ?></td>
                    <td><?php echo $demande->getGenre(); ?></td>
                    <td><img src="<?php echo $demande->getPicPath(); ?>" alt="Image du livre" style="width:50px;height:75px;" /></td>
                    <td><?php echo $demande->getDateDemande(); ?></td>
                    <td>
                        <a href="controller/deleteDemandeLivre.php?id=<?php echo $demande->getIdDemande(); ?>">Supprimer</a>
                    </td>
                    <td><a href="updateDemandeLivre.php?id=<?php echo $demande->getIdDemande(); ?>">Modifier</a></td>
                </tr>
            <?php } ?>
        </tbody>
    </table>
</body>

</html>