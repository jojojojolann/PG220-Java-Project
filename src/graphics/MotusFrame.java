package graphics;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import controllers.Dictionnary;
import controllers.Matrice;

public class MotusFrame {

    private JFrame initialFrame, gameFrame;
    private JPanel panel;
    private JTextField[][] textFields;
    private int size;
    private Timer timer;
    private JLabel timerLabel;
    private int elapsedTime;
    private Map<Character, JButton> keyboardButtons;
    private Dictionnary dictionnary;
    private Matrice matrice;

    public static final int MIN_GRID_SIZE = 7;
    public static final int MAX_GRID_SIZE = 15;
    public static char INITIAL_LETTER;
    public static char SECOND_LETTER;
    public String selectedWord;
    public int indice;

    public MotusFrame() {
        dictionnary = new Dictionnary();
        createInitialFrame();
    }

    private void createInitialFrame() {
        initialFrame = new JFrame("Select Grid Size");
        initialFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        initialFrame.setLayout(new BoxLayout(initialFrame.getContentPane(), BoxLayout.PAGE_AXIS));

        // Ligne 1: Choix de la taille du mot
        JPanel sizePanel = new JPanel(new FlowLayout());
        Integer[] sizeOptions = new Integer[MAX_GRID_SIZE - MIN_GRID_SIZE + 1];
        for (int i = 0; i < sizeOptions.length; i++) {
            sizeOptions[i] = MIN_GRID_SIZE + i;
        }
        JComboBox<Integer> gridSizes = new JComboBox<>(sizeOptions);
        sizePanel.add(new JLabel("Taille du mot :"));
        sizePanel.add(gridSizes);

        // Ligne 2: Choix de l'affichage de la deuxième lettre aléatoire
        JPanel letterChoicePanel = new JPanel(new FlowLayout());
        JRadioButton yesButton = new JRadioButton("Oui", true);
        JRadioButton noButton = new JRadioButton("Non");
        ButtonGroup group = new ButtonGroup();
        group.add(yesButton);
        group.add(noButton);
        letterChoicePanel.add(new JLabel("Afficher une deuxième lettre aléatoire :"));
        letterChoicePanel.add(yesButton);
        letterChoicePanel.add(noButton);

        // Ligne 3: Bouton de démarrage du jeu
        JPanel startGamePanel = new JPanel(new FlowLayout());
        JButton submitButton = new JButton("Start Game");
        submitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                size = (int) gridSizes.getSelectedItem();
                selectedWord = dictionnary.motaleatoire(size);
                selectedWord = selectedWord.toUpperCase();
                INITIAL_LETTER = selectedWord.charAt(0);
                matrice = new Matrice(size, selectedWord.length(), selectedWord);

                // Choix aléatoire de la deuxième lettre si "Oui" est sélectionné
                if (yesButton.isSelected()) {
                    indice = new Random().nextInt(size-1);
                    indice += 1;
                    SECOND_LETTER = selectedWord.charAt(indice);
                } else {
                    indice = -1; // Valeur pour indiquer que la deuxième lettre ne doit pas être affichée
                }
                
                createAndShowGameGUI();
                initialFrame.dispose();
            }
        });
        startGamePanel.add(submitButton);

        // Ajout des panneaux à la fenêtre principale
        initialFrame.add(sizePanel);
        initialFrame.add(letterChoicePanel);
        initialFrame.add(startGamePanel);

        initialFrame.pack();
        initialFrame.setLocationRelativeTo(null);
        initialFrame.setVisible(true);
    }



    private void createAndShowGameGUI() {
        gameFrame = new JFrame("Motus Game");
        gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gameFrame.setLayout(new BorderLayout());

        panel = new JPanel(new GridLayout(size, size));
        textFields = new JTextField[size][size];
        setupGrid();

        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.PAGE_AXIS));
        setupControlPanel(controlPanel);

        JPanel keyboardPanel = createKeyboardPanel();

        gameFrame.add(panel, BorderLayout.CENTER);
        gameFrame.add(controlPanel, BorderLayout.EAST);
        gameFrame.add(keyboardPanel, BorderLayout.SOUTH);

        int cellSize = 50;
        int windowWidth = size * cellSize + 200;
        int windowHeight = size * cellSize;
        gameFrame.setPreferredSize(new Dimension(windowWidth, windowHeight));

        gameFrame.pack();
        gameFrame.setLocationRelativeTo(null);
        gameFrame.setVisible(true);
    }

    private void setupGrid() {
        KeyAdapter keyAdapter = createKeyAdapter();
        Font gridFont = new Font("SansSerif", Font.BOLD, 20);

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                final JTextField textField = new JTextField();
                textField.setHorizontalAlignment(JTextField.CENTER);
                textField.setFont(gridFont);
                textField.setBackground(new Color(255, 255, 255));
                textField.setForeground(new Color(0, 0, 0));
                textField.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
                textField.addKeyListener(keyAdapter);

                // Empêcher la sélection de texte avec la souris
                textField.addMouseListener(new MouseAdapter() {
                    public void mousePressed(MouseEvent e) {
                        textField.setFocusable(false);
                    }
                    public void mouseReleased(MouseEvent e) {
                        textField.setFocusable(true);
                    }
                });

                // Afficher la première lettre dans la première colonne
                if (j == 0) {
                    textField.setText(String.valueOf(INITIAL_LETTER));
                    textField.setEditable(false);
                } 
                // Afficher la deuxième lettre choisie aléatoirement si applicable
                else if (indice != -1 && j == indice) {
                    textField.setText(String.valueOf(SECOND_LETTER));
                    textField.setEditable(false);
                }

                textFields[i][j] = textField;
                panel.add(textField);
            }
        }

        // Activer le focus pour le premier champ de saisie éditable
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                textFields[0][1].requestFocusInWindow();
            }
        });
    }


    private JPanel createKeyboardPanel() {
        String[] keys = {"AZERTYUIOP", "QSDFGHJKLM", "<WXCVBN>?"};
        JPanel keyboardPanel = new JPanel();
        keyboardPanel.setLayout(new GridLayout(3, 1));
        keyboardButtons = new HashMap<>();

        for (String keyRow : keys) {
            JPanel rowPanel = new JPanel(new FlowLayout());
            for (char key : keyRow.toCharArray()) {
                JButton keyButton = new JButton(String.valueOf(key));
                keyButton.setEnabled(false);
                rowPanel.add(keyButton);
                keyboardButtons.put(key, keyButton);
            }
            keyboardPanel.add(rowPanel);
        }

        return keyboardPanel;
    }

    private KeyAdapter createKeyAdapter() {
        return new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                JTextField source = (JTextField) e.getSource();
                if (source.getText().length() >= 1 && e.getKeyChar() != KeyEvent.VK_BACK_SPACE) {
                    e.consume();
                }
            }

            public void keyPressed(KeyEvent e) {
                JTextField source = (JTextField) e.getSource();
                if (Character.isLetter(e.getKeyChar())) {
                    source.setText(String.valueOf(Character.toUpperCase(e.getKeyChar())));
                    moveFocusRight(source);
                } else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                	submitWord(source);
                    moveFocusDown(source);
                } else if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
                    handleBackspace(source);
                }
            }
        };
    }
    
    private void submitWord(JTextField source) {
        int row = getCurrentRow(source);
        String word = getWordFromRow(row);
        matrice.addTry(word,row);
        colorRow(row);
    }

    private int getCurrentRow(JTextField source) {
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                if (textFields[row][col] == source) {
                    return row;
                }
            }
        }
        return -1;
    }

    private String getWordFromRow(int rowIndex) {
        StringBuilder sb = new StringBuilder();
        for (int j = 0; j < size; j++) {
            sb.append(textFields[rowIndex][j].getText());
        }
        return sb.toString();
    }

    private void moveFocusRight(JTextField currentField) {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size - 1; j++) {
                if (textFields[i][j] == currentField) {
                    textFields[i][j + 1].requestFocus();
                    return;
                }
            }
        }
    }

    private void moveFocusDown(JTextField currentField) {
        int currentRow = -1;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (textFields[i][j] == currentField) {
                    currentRow = i;
                    break;
                }
            }
            if (currentRow != -1) {
                break;
            }
        }

        boolean allFilled = true;
        for (int j = 1; j < size; j++) {
            if (textFields[currentRow][j].getText().trim().isEmpty()) {
                allFilled = false;
                break;
            }
        }

        if (allFilled && currentRow < size - 1) {
            colorRow(currentRow);
            updateKeyboard(currentRow);
            textFields[currentRow + 1][1].requestFocus();
        }
    }
    
    private void handleBackspace(JTextField currentField) {
        int row = -1, col = -1;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (textFields[i][j] == currentField) {
                    row = i;
                    col = j;
                    break;
                }
            }
            if (row != -1) break;
        }

        if (col > 1) { // S'arrête à la deuxième case
            textFields[row][col - 1].requestFocus();
        }
    }

    private void colorRow(int rowIndex) {
        String result = matrice.isValid(getWordFromRow(rowIndex)); // Utilise isValid pour obtenir le résultat
        
        for (int col = 0; col < size; col++) {
            JTextField textField = textFields[rowIndex][col];
            char status = result.charAt(col); // Obtient le statut de chaque lettre
    
            switch (status) {
                case 'A': // Correcte et bien placée
                    textField.setBackground(Color.RED);
                    break;
                case 'B': // Présente mais mal placée
                    textField.setBackground(Color.ORANGE);
                    break;
                case 'C': // Incorrecte
                    textField.setBackground(Color.WHITE);
                    break;
            }
        }
    }

    private void updateKeyboard(int currentRow) {
        for (int j = 1; j < size; j++) {
            char letter = textFields[currentRow][j].getText().toUpperCase().charAt(0);
            if (keyboardButtons.containsKey(letter)) {
                JButton keyButton = keyboardButtons.get(letter);
                keyButton.setEnabled(false);
                keyButton.setBackground(Color.GRAY);
            }
        }
    }

    private void setupControlPanel(JPanel controlPanel) {
        elapsedTime = 0;
        timerLabel = new JLabel("Temps: 0 s");
        timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                elapsedTime++;
                timerLabel.setText("Temps: " + elapsedTime + " s");
            }
        });
        timer.start();

        JButton restartButton = new JButton("Recommencer");
        restartButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                restartGame();
            }
        });

        JButton quitButton = new JButton("Quitter");
        quitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        controlPanel.add(timerLabel);
        controlPanel.add(restartButton);
        controlPanel.add(quitButton);
    }

    private void restartGame() {
        timer.stop();
        gameFrame.dispose();
        createInitialFrame();
    }

    public static void main(String[] args) {
    	MotusFrame h = new MotusFrame();
    }
}