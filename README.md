# Read.me

## Structure du Projet
Nous avons divisé le projet en trois classes réparties sur deux packages différents, en plus du package `test` contenant le `main`.

### Package `controllers`
Ce package contient :
- **Dictionnary** : Crée une `ArrayList` contenant tous les mots du dictionnaire. Calcule également le minimum et le maximum de la longueur des mots pour choisir une taille adéquate au début du jeu. Nous n'avons pas eu le temps d'ajouter différents dictionnaires en fonction de la langue choisie.
- **Matrice** : Gère l'aspect technique du jeu, y compris le test des mots et l'ajout au jeu.

### Package `graphics`
Il contient :
- **MotusFrame** : Initialement, l'interface graphique et les contrôleurs devaient être séparés, mais pour faciliter l'affichage des lettres directement dans la grille (à la manière du jeu Tusmo), nous avons regroupé les deux. Cette classe gère toute l'interface graphique, le choix du mode et de la langue, la taille du mot en fonction du dictionnaire, et la possibilité d'avoir une deuxième lettre aléatoire. 
    - Le jeu n'accepte pas de mot incomplet ou hors dictionnaire. Un message apparaît dans le terminal pour les mots invalides ou trop courts.
    - La lettre aléatoire peut être remplacée et réapparaît lors de la suppression.
    - L'affichage des lettres bien placées et l'interface en couleurs correspondent au cahier des charges.
    - Possibilité de quitter et recommencer à tout moment.
    - Mode d'affichage sombre disponible.
    - Message de victoire ou défaite avec GIF correspondant.
    - Timer basé sur le nombre d'essais (20 secondes par essai).
    - Musique de fond avec option de désactivation.
    - Animation lors de la frappe ou suppression d'une lettre.
    - Écran de chargement fictif avec logo du jeu au lancement.

## Lancement du Jeu
Le jeu est lancé en exécutant la classe `Test` dans le package `Test`.
