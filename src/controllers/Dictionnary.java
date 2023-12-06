package controllers;

import java.io.FileInputStream;
import graphics.MotusFrame;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class Dictionnary {
	public ArrayList<String> dico;
	int min;
	int max;
	
	public Dictionnary() {
		dico = creadico();
		min = minimum();
		max = maximum();
	}
	
	public ArrayList<String> creadico(){
		ArrayList<String> ar = new ArrayList<>();
		try (FileInputStream fichier = new FileInputStream("Dictionnaire.txt"); Scanner scanner = new Scanner(fichier)) {
				while(scanner.hasNextLine()) {
					ar.add(scanner.nextLine());
				}
		}catch (IOException e) {
			System.out.println("Le dictionnaire est enregistrer");
		}
		return ar;
	}
		
	
	public String motaleatoire(int taille) {
		ArrayList<String> list = new ArrayList<>();
		for(int i = 0; i < dico.size(); i++) {
			int line = dico.get(i).length();
			if (line == taille) {
				list.add(dico.get(i));
			}
		}
		Random r2 = new Random();
		int n = r2.nextInt(list.size());
        String mot = list.get(n);
		System.out.println("Le mot de taille "+taille+" choisit est : " + mot ); 
		return mot;
	}
	
	public int minimum() {
		int min = 1000;
		for(int i = 0; i < dico.size(); i++) {
			int number_letter = dico.get(i).length();
			if (number_letter < min) {
				min = number_letter;
			}
		}
		return min;
	}
	
	public int maximum() {
		int max = 0;
		for(int i = 0; i < dico.size(); i++) {
			int number_letter = dico.get(i).length();
			if (number_letter > max) {
				max = number_letter;
			}
		}
		return max;
	}
	
	public int size_choice() {
		int indice = 0;
		Scanner sc = new Scanner(System.in);

		while (true) {
		    System.out.println("Choisissez la taille du mot à deviner entre " + min + " et " + max);
		    try {
		        if (sc.hasNextInt()) {
		            indice = sc.nextInt();
		            if (indice >= min && indice <= max) {
		                break; // Sorti de la boucle si la valeur choisit est entre le min et le max
		            } else {
		                System.out.println("La taille doit être entre " + min + " et " + max);
		            }
		        } else {
		            System.out.println("Veuillez entrer un nombre valide.");
		            sc.next(); // Effacement les variables invalide du scanneur
		        }
		    } catch (Exception e) {
		        System.out.println("Une erreur s'est produite. Veuillez réessayer.");
		        sc.next(); // Clear invalid input from the scanner
		    }
		}
		System.out.println("La taille choisie est " + indice);

		sc.close(); // Fermeture du scanner
		return indice;
		
	}
	
	public ArrayList<String> getdico() {
		return dico;
	}
	
	public int getMin() {
		return min;
	}
	
	public int getMax() {
		return max;
	}
	
}
