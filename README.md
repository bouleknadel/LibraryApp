# Application de Gestion de Bibliothèque

## Description
Cette application Android native développée en Kotlin offre une solution complète pour la gestion de bibliothèque personnelle. Elle permet aux utilisateurs de gérer leur collection de livres avec des fonctionnalités avancées comme le scan de livres et la gestion des favoris.

## Fonctionnalités Principales

### 📚 Gestion des Livres
- Ajout manuel de livres avec détails complets
- Modification et suppression des livres
- Affichage détaillé des informations du livre
- Catégorisation des livres par genre, auteur, etc.

### 📷 Scan de Livres
- Scan du code-barres ISBN pour ajouter rapidement des livres
- Reconnaissance automatique des informations du livre
- Récupération des métadonnées depuis des API externes
- Prévisualisation avant l'ajout

### 👤 Gestion de Profil
- Création et personnalisation du profil utilisateur

### 🔍 Recherche et Filtres
- Recherche avancée par :
  - Titre
  - Genre
  - prix

### 📱 Demande de Livres
- Système de demande de nouveaux livres
- Suivi des demandes en cours

### ❤️ Gestion des Favoris
- Ajout/Suppression de livres aux favoris
- Création de listes de lecture personnalisées

## Technologies Utilisées
- Kotlin
- Android Jetpack Components
- Navigation Component
- Material Design 3
- ViewBinding
- zxing pour le scan
- Volley pour les appels API
- Glide pour le chargement d'images

## Dépendances
```gradle
// Glide pour la gestion des images
implementation("com.github.bumptech.glide:glide:4.15.1")
kapt("com.github.bumptech.glide:compiler:4.15.1")

// ZXing pour le scan de codes-barres
implementation("com.journeyapps:zxing-android-embedded:4.3.0")

// Retrofit pour les appels API
implementation("com.squareup.retrofit2:retrofit:2.9.0")
implementation("com.squareup.retrofit2:converter-gson:2.9.0")

// Picasso pour le chargement d'images
implementation("com.squareup.picasso:picasso:2.8")

// Volley pour les requêtes réseau
implementation("com.android.volley:volley:1.2.1")

// CircleImageView pour les images de profil rondes
implementation("de.hdodenhof:circleimageview:3.1.0")
```

## Permissions Required
```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.CAMERA" />
```

## Configuration Requise
- Android Studio Arctic Fox ou version plus récente
- SDK minimum : Android 6.0 (API 23)
- SDK cible : Android 13 (API 33)
- Gradle version 7.0+

## Installation
1. Clonez le repository :
```bash
git clone https://github.com/bouleknadel/LibraryApp.git
```
2. Ouvrez le projet ( navdrawerkotpractice ) dans Android Studio
3. Synchronisez le projet avec Gradle
4. Lancez l'application sur un émulateur ou un appareil physique

```

## Contact
- Créé par Bouleknadel Abderrahmane
- GitHub : [@bouleknadel](https://github.com/bouleknadel)

## Vidéo Démonstrative
Regardez une démonstration de l'application :
https://github.com/user-attachments/assets/dc37b09e-ab52-49ff-a3e1-f8cb7218fb8b

