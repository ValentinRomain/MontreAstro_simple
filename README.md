# MontreAstro - Application Astrologique Sidérale

Une application Android qui affiche un zodiaque lunaire sidéral avec une précision en secondes d'arc, basée sur les algorithmes d'éphémérides de Paul Schlyter.

## Fonctionnalités

- Calcul précis de la position de la Lune en utilisant les algorithmes de Paul Schlyter
- Affichage du zodiaque lunaire avec une précision en secondes d'arc
- Représentation visuelle de la position de la Lune dans le zodiaque
- Affichage des coordonnées en degrés, minutes, secondes et secondes d'arc

## Prérequis

- Android Studio 4.2 ou supérieur
- JDK 11 ou supérieur
- Android SDK 21 ou supérieur

## Installation

1. Clonez ce dépôt
2. Ouvrez le projet dans Android Studio
3. Synchronisez le projet avec les fichiers Gradle
4. Exécutez l'application sur un émulateur ou un appareil Android

## Build avec Codemagic

Ce projet est configuré pour être construit avec [Codemagic](https://codemagic.io/). Le fichier `codemagic.yaml` contient la configuration nécessaire pour générer un fichier APK.

Pour configurer le build sur Codemagic :

1. Créez un compte sur [Codemagic](https://codemagic.io/)
2. Ajoutez ce dépôt à votre compte
3. Configurez les variables d'environnement nécessaires (clés de signature, etc.)
4. Lancez le build

## Algorithmes d'éphémérides

Cette application utilise les algorithmes d'éphémérides de Paul Schlyter pour calculer la position précise de la Lune. Ces algorithmes sont décrits en détail sur [son site web](http://www.stjarnhimlen.se/comp/ppcomp.html).

## Zodiaque Lunaire

Le zodiaque lunaire est représenté comme suit :
- Début du zodiaque : 0° (0 seconde d'arc)
- Fin du zodiaque : 60 secondes d'arc
- La position de la Lune est affichée avec une précision en secondes d'arc

## Versions futures

- V2 : Ajout des autres corps célestes (planètes, Soleil) avec la même précision

## Licence

Ce projet est sous licence MIT. Voir le fichier LICENSE pour plus de détails.
