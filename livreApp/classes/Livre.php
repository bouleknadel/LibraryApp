<?php
class Livre
{
    private $id;
    private $titre;
    private $auteur;
    private $genre;
    private $langue;
    private $price;
    private $description;
    private $picPath;

    function __construct($id, $titre, $auteur, $genre, $langue, $price, $description, $picPath)
    {
        $this->id = $id;
        $this->titre = $titre;
        $this->auteur = $auteur;
        $this->genre = $genre;
        $this->langue = $langue;
        $this->price = $price;
        $this->description = $description;
        $this->picPath = $picPath;
    }

    function getId()
    {
        return $this->id;
    }

    function getTitre()
    {
        return $this->titre;
    }

    function getAuteur()
    {
        return $this->auteur;
    }

    function getGenre()
    {
        return $this->genre;
    }

    function getLangue()
    {
        return $this->langue;
    }

    function getPrice()
    {
        return $this->price;
    }

    function getDescription()
    {
        return $this->description;
    }

    function getPicPath()
    {
        return $this->picPath;
    }

    function setId($id)
    {
        $this->id = $id;
    }

    function setTitre($titre)
    {
        $this->titre = $titre;
    }

    function setAuteur($auteur)
    {
        $this->auteur = $auteur;
    }

    function setGenre($genre)
    {
        $this->genre = $genre;
    }

    function setLangue($langue)
    {
        $this->langue = $langue;
    }

    function setPrice($price)
    {
        $this->price = $price;
    }

    function setDescription($description)
    {
        $this->description = $description;
    }

    function setPicPath($picPath)
    {
        $this->picPath = $picPath;
    }

    public function __toString()
    {
        return $this->titre . " par " . $this->auteur;
    }
}
