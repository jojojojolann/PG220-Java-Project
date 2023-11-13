package controllers;

public class Matrice {
	// Attributes
	private char[][] matrice;
	private int idxRow;
	private String Solution;
	private final int maxTry;
	private final int wordLen;
	
	// Constructor
	public Matrice(int maxTry, int wordLen) {
		this.maxTry = maxTry;
        this.wordLen = wordLen;
        matrice = new char[maxTry][wordLen];
        setIdxRow(0);
	}
	
	// Getters and Setters
	public int getIdxRow() {
		return idxRow;
	}

	public void setIdxRow(int idxRow) {
		this.idxRow = idxRow;
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
    	
    	for (int i = 0; i < word.length(); i++) {
    		if (word.charAt(i) == Solution.charAt(i)) { // Correct + Good place
    			result.append("A ");
    		}
    		else if (Solution.indexOf(word.charAt(i)) == -1) { // Correct + Bad place
    			result.append("B ");
    		}
    		else { // Incorrect
    			result.append("C ");
    		}
    	}
    	return result.toString();
    }
    
    public void addTry(String word, int idxRow) {
    	String Test = isValid(word);
        if (!((Test.contains("C") || (Test.contains("B"))) && (idxRow < maxTry))) {
            for (int i = 0; i < wordLen; i++) {
                matrice[this.idxRow][i] = word.charAt(i);
            }
            idxRow++;
            
            // End of game de
            if (word.equals(Solution)) {
                System.out.println("Félicitations ! Vous avez trouvé le mot !");
            } else if (this.idxRow == maxTry) {
            	System.out.println("Game Over. Vous avez épuisé toutes vos tentatives.");
                System.out.println("Le mot correct était : " + Solution);
            }
        } else {
        	System.out.println("Essai invalide. Veuillez réessayer.");
        }
    }
}





