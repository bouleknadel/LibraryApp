<?php
class DemandeLivre
{
    private $id_demande;
    private $id_user;
    private $titre;
    private $genre;
    private $picPath;
    private $date_demande;

    // Constructeur
    function __construct($id_demande, $id_user, $titre, $genre, $picPath, $date_demande)
    {
        $this->id_demande = $id_demande;
        $this->id_user = $id_user;
        $this->titre = $titre;
        $this->genre = $genre;
        $this->picPath = $picPath;
        $this->date_demande = $date_demande;
    }

    // Getters
    function getIdDemande()
    {
        return $this->id_demande;
    }

    function getIdUser()
    {
        return $this->id_user;
    }

    function getTitre()
    {
        return $this->titre;
    }

    function getGenre()
    {
        return $this->genre;
    }

    function getPicPath()
    {
        return $this->picPath;
    }

    function getDateDemande()
    {
        return $this->date_demande;
    }

    // Setters
    function setIdDemande($id_demande)
    {
        $this->id_demande = $id_demande;
    }

    function setIdUser($id_user)
    {
        $this->id_user = $id_user;
    }

    function setTitre($titre)
    {
        $this->titre = $titre;
    }

    function setGenre($genre)
    {
        $this->genre = $genre;
    }

    function setPicPath($picPath)
    {
        $this->picPath = $picPath;
    }

    function setDateDemande($date_demande)
    {
        $this->date_demande = $date_demande;
    }

    // Méthode pour afficher la demande
    public function __toString()
    {
        return "Demande de livre: " . $this->titre . " par utilisateur ID: " . $this->id_user;
    }
}
