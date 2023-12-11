package controllers;

import java.util.HashMap;
import java.util.Map;

public class Matrice {
	// Attributes
	private char[][] matrice;
	private int idxRow;
	private String Solution;
	private final int maxTry;
	private final int wordLen;

	// Constructor
	public Matrice(int maxTry, int wordLen, String solution) {
		this.maxTry = maxTry;
		this.wordLen = wordLen;
		this.Solution = solution;
		matrice = new char[maxTry][wordLen];
		setIdxRow(0);
	}

	// Getters and Setters
	public int getIdxRow() {
		return idxRow;
	}

	public int getMaxTry() {
		return maxTry;
	}

	public void setIdxRow(int idxRow) {
		this.idxRow = idxRow;
	}

	public String getSolution() {
		return Solution;
	}
	
	public char getCharAt(int row, int col) {
		return matrice[row][col];
	}

	// Methods
	public void display() {
		for (int i = 0; i < maxTry; i++) {
			for (int j = 0; j < wordLen; j++) {
				System.out.print(matrice[i][j] + " ");
			}
			System.out.println();
		}
	}

	public String isValid(String word) {
		StringBuilder result = new StringBuilder();
		Map<Character, Integer> letterCount = new HashMap<>();
		Map<Character, Integer> targetLetterCount = getLetterCount(Solution);

		// Compter les occurrences de chaque lettre dans le mot solution
		for (char c : Solution.toCharArray()) {
			letterCount.put(c, letterCount.getOrDefault(c, 0) + 1);
		}

		for (int i = 0; i < word.length(); i++) {
			char currentChar = word.charAt(i);

			if (currentChar == Solution.charAt(i)) { // Correct + Good place
				result.append("A");
				if (letterCount.get(currentChar) <= targetLetterCount.get(currentChar)) {
					for (int j = i-1; j >= 0; j--) {
						char search = word.charAt(j);
						if (search == currentChar && result.charAt(j) == 'B') {
							result.deleteCharAt(j);
							result.insert(j, 'C');
							break;
						}
					}
					letterCount.put(currentChar, letterCount.getOrDefault(currentChar, 0) - 1);
				}
			} else if (Solution.indexOf(currentChar) >= 0) {
			if (letterCount.getOrDefault(currentChar, 0) > 0) {
				// Correct + Bad place
				result.append("B");
				letterCount.put(currentChar, letterCount.getOrDefault(currentChar, 0) - 1);
			} else {
				// La lettre est en surplus
				result.append("C");
			}
		} else { // Incorrect
			result.append("C");
		}
	}
	return result.toString();
}

private Map<Character, Integer> getLetterCount(String word) {
	Map<Character, Integer> letterCount = new HashMap<>();
	for (char c : word.toCharArray()) {
		letterCount.put(c, letterCount.getOrDefault(c, 0) + 1);
	}
	return letterCount;
}


public void addTry(String word, int idxRow) {
	String Test = isValid(word);
	if ((Test.contains("C") || (Test.contains("B"))) && (idxRow < maxTry-1)) {
		for (int i = 0; i < wordLen-1; i++) {
			matrice[this.idxRow][i] = word.charAt(i);
		}
		idxRow++;
	} 
}
}