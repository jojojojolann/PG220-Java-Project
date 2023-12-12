package graphics;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.io.File;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;
import java.net.URL;

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
	private static final Color DARK_BACK = new Color(45, 45, 45);
	private static final Color DARK_TEXT = Color.WHITE;
	private boolean isDarkMode = false;

	public static final int MIN_GRID_SIZE = 7;
	public static final int MAX_GRID_SIZE = 15;
	public static char INITIAL_LETTER;
	public static char SECOND_LETTER;
	public String selectedWord;
	public int indice;

	public MotusFrame() {
		dictionnary = new Dictionnary();
		createWelcomeFrame();
	}

	private void createWelcomeFrame() {
        JFrame welcomeFrame = new JFrame("Bienvenue dans Motus");
        welcomeFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        welcomeFrame.setLayout(new BorderLayout());
		try {
            ImageIcon icon = new ImageIcon(getClass().getResource("icone.png"));
            Image image = icon.getImage();
            welcomeFrame.setIconImage(image);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Panneau pour le contenu central
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.PAGE_AXIS));

        // Étiquette de bienvenue centrée
        JLabel welcomeLabel = new JLabel("<html><center>Bienvenue dans le jeu Motus!<br>Choisissez votre mode de jeu et votre langue:</center></html>");
        welcomeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Panneau pour les boutons de mode
        JPanel modeButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JCheckBox classicModeCheckbox = new JCheckBox("Mode Classique");
        JCheckBox customModeCheckbox = new JCheckBox("Mode Personnalisé");

        // Groupe de boutons pour les checkboxes
        ButtonGroup modeGroup = new ButtonGroup();
        modeGroup.add(classicModeCheckbox);
        modeGroup.add(customModeCheckbox);

        // Ajouter les checkboxes au panneau
        modeButtonPanel.add(classicModeCheckbox);
        modeButtonPanel.add(customModeCheckbox);

        // Panneau pour les boutons de langue
        JPanel languageButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton frenchButton = new JButton("Français");
        JButton englishButton = new JButton("English");
        JButton spanishButton = new JButton("Español");

        // Désactiver les boutons de langue par défaut
        frenchButton.setEnabled(false);
        englishButton.setEnabled(false);
        spanishButton.setEnabled(false);

        // Écouteurs pour les checkboxes
        ActionListener modeCheckboxListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean enableLanguageButtons = classicModeCheckbox.isSelected() || customModeCheckbox.isSelected();
                frenchButton.setEnabled(enableLanguageButtons);
                englishButton.setEnabled(enableLanguageButtons);
                spanishButton.setEnabled(enableLanguageButtons);
            }
        };

        classicModeCheckbox.addActionListener(modeCheckboxListener);
        customModeCheckbox.addActionListener(modeCheckboxListener);

        // Ajouter les écouteurs d'événements pour les boutons de langue
        frenchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                welcomeFrame.dispose();
                createInitialFrame(); // Lance le jeu en français
            }
        });

        // Ajouter les boutons de langue au panneau
        languageButtonPanel.add(frenchButton);
        languageButtonPanel.add(englishButton);
        languageButtonPanel.add(spanishButton);

        // Ajout des composants au panneau central
        centerPanel.add(welcomeLabel);
        centerPanel.add(modeButtonPanel);
        centerPanel.add(languageButtonPanel);

        // Ajout du panneau central à la fenêtre
        welcomeFrame.add(centerPanel, BorderLayout.CENTER);

        welcomeFrame.pack();
        welcomeFrame.setLocationRelativeTo(null);
        welcomeFrame.setVisible(true);
    }

	class LanguageItem {
        private ImageIcon flagIcon;
        private String language;

        public LanguageItem(ImageIcon flagIcon, String language) {
            this.flagIcon = flagIcon;
            this.language = language;
        }

        public ImageIcon getFlagIcon() {
            return flagIcon;
        }

        public String getLanguage() {
            return language;
        }
    }

	class LanguageRenderer extends JLabel implements ListCellRenderer<LanguageItem> {
        @Override
        public Component getListCellRendererComponent(JList<? extends LanguageItem> list, LanguageItem value, int index, boolean isSelected, boolean cellHasFocus) {
            if (value != null) {
                setText(value.getLanguage());
                setIcon(value.getFlagIcon());
            }
            return this;
        }
    }

	private void createInitialFrame() {
		initialFrame = new JFrame("Select Grid Size");
		initialFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		initialFrame.setLayout(new BoxLayout(initialFrame.getContentPane(), BoxLayout.PAGE_AXIS));
		try {
            ImageIcon icon = new ImageIcon(getClass().getResource("icone.png"));
            Image image = icon.getImage();
            initialFrame.setIconImage(image);
        } catch (Exception e) {
            e.printStackTrace();
        }
		initialFrame.setResizable(false);
		initialFrame.setPreferredSize(new Dimension(400, 200));
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

		// Ligne 4 : Bouton de Dark Mode
		JPanel darkModePanel = new JPanel(new FlowLayout());
		JRadioButton darkYes = new JRadioButton("Oui");
		JRadioButton darkNo = new JRadioButton("Non");
		ButtonGroup darkGroup = new ButtonGroup();
		darkGroup.add(darkYes);
		darkGroup.add(darkNo);
		darkModePanel.add(new JLabel("Mode Sombre :"));
		darkModePanel.add(darkYes);
		darkModePanel.add(darkNo);

		darkYes.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				applyTheme(false);
			}
		});

		darkNo.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				applyTheme(true);
			}
		});


		// Ligne 3: Bouton de démarrage du jeu
		JPanel startGamePanel = new JPanel(new FlowLayout());
		JButton submitButton = new JButton("Start Game");
		submitButton.setBackground(DARK_BACK);
		submitButton.setForeground(Color.WHITE);
		submitButton.setFont(new Font("Arial", Font.BOLD, 14));
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
		initialFrame.add(darkModePanel);
		initialFrame.add(startGamePanel);

		initialFrame.pack();
		initialFrame.setLocationRelativeTo(null);
		initialFrame.setVisible(true);
	}

	private void applyTheme(boolean darkMode) {
		isDarkMode = !darkMode;
		Color bgColor = isDarkMode ? DARK_BACK : Color.WHITE;

		initialFrame.getContentPane().setBackground(bgColor);
	}


	private void createAndShowGameGUI() {
		gameFrame = new JFrame("Motus Game");
		gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		gameFrame.setLayout(new BorderLayout());

		try {
			ImageIcon icon = new ImageIcon(getClass().getResource("icone.png"));
			Image image = icon.getImage();
			gameFrame.setIconImage(image);
		} catch (Exception e) {
			e.printStackTrace();
		}

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
		
		playBackgroundMusic();
	}

	private void setupGrid() {
		KeyAdapter keyAdapter = createKeyAdapter();
		Font gridFont = new Font("SansSerif", Font.BOLD, 20);

		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				final JTextField textField = new JTextField();
				textField.setHorizontalAlignment(JTextField.CENTER);
				textField.setFont(gridFont);
				if (!isDarkMode) {
					textField.setBackground(new Color(255, 255, 255));
					textField.setForeground(new Color(0, 0, 0));
				} else {
					textField.setBackground(DARK_BACK);
					textField.setForeground(DARK_TEXT);
				}
				if (isDarkMode) {
					textField.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
				} else {
					textField.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
				}
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
					textField.setBackground(new Color(0, 123, 167));
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
		String[] keys = {"AZERTYUIOP", "QSDFGHJKLM", "WXCVBN"};
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
					animateLetterPop(source);
					moveFocusRight(source);
				} else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					if (isRowComplete(getCurrentRow(source))) {
						submitWord(source);
						moveFocusDown(source);
					}
				} else if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
					handleBackspace(source);
				}
			}
		};
	}

	private void animateLetterPop(JTextField textField) {
		final Color canardColor = new Color(0, 123, 167); // Couleur bleu canard pour le fond
		final int initialFontSize = 20;
		final int targetFontSize = 30;
		final int animationSteps = 5;
		final int delay = 50; // Délai en millisecondes entre les étapes de l'animation
	
		Timer animationTimer = new Timer(delay, new ActionListener() {
			private int currentStep = 0;
	
			@Override
			public void actionPerformed(ActionEvent e) {
				if (currentStep <= animationSteps) {
					float interpolatedSize = initialFontSize + (targetFontSize - initialFontSize) * ((float) currentStep / animationSteps);
					Font font = textField.getFont().deriveFont(interpolatedSize);
					textField.setFont(font);
					textField.setBackground(canardColor);
					currentStep++;
				} else {
					textField.setFont(textField.getFont().deriveFont((float) initialFontSize));
					((Timer) e.getSource()).stop(); // Arrête l'animation
				}
			}
		});
		animationTimer.start();
	}

	private boolean isRowComplete(int rowIndex) {
		for (int col = 0; col < size; col++) {
			if (textFields[rowIndex][col].getText().isEmpty()) {
				return false;
			}
		}
		return true;
	}

	private void submitWord(JTextField source) {
        int row = getCurrentRow(source);
        String word = getWordFromRow(row);
        matrice.addTry(word, row);
        colorRow(row);

        // Vérification si le mot soumis est correct
        if (word.equals(selectedWord)) {
            timer.stop();
            showVictoryDialog();
        } 
        // Vérification si c'est le dernier essai et que le mot est incorrect
        else if (row == size - 1 || matrice.getIdxRow() >= matrice.getMaxTry() - 1) {
            timer.stop();
            showEndGameDialog("Nombre d'essais épuisé ou mot incorrect");
        }
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
        int currentRow = getCurrentRow(currentField);

        // Vérifie si tous les champs de la ligne actuelle sont remplis
        boolean allFilled = true;
        for (int j = 1; j < size; j++) {
            if (textFields[currentRow][j].getText().trim().isEmpty()) {
                allFilled = false;
                break;
            }
        }

        // Si tous les champs sont remplis
        if (allFilled) {
            // Si ce n'est pas la dernière ligne, déplace le focus vers le bas
            if (currentRow < size - 1) {
                colorRow(currentRow);
                updateKeyboard(currentRow);
                textFields[currentRow + 1][1].requestFocus();
            } else {
                // Pour la dernière ligne, soumettre le mot et ne pas déplacer le focus
                submitWord(currentField);
            }
        }
    }

	private void handleBackspace(JTextField currentField) {
		int row = -1, col = -1;
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				if (textFields[i][j] == currentField) {
					row = i;
					col = j;
					if (j == indice) {
						textFields[i][j].setText(String.valueOf(SECOND_LETTER));
					}
					break;
				}
			}
			if (row != -1) break;
		}
	
		if (col > 1) {
			textFields[row][col - 1].requestFocus();
			animateLetterBackspace(textFields[row][col - 1]); // Animer lors du backspace
		}
	}
	
	private void animateLetterBackspace(JTextField textField) {
		final int initialFontSize = 30;
		final int targetFontSize = 20;
		final int animationSteps = 5;
		final int delay = 50;
	
		Timer animationTimer = new Timer(delay, new ActionListener() {
			private int currentStep = animationSteps;
	
			@Override
			public void actionPerformed(ActionEvent e) {
				if (currentStep >= 0) {
					float interpolatedSize = targetFontSize + (initialFontSize - targetFontSize) * ((float) currentStep / animationSteps);
					Font font = textField.getFont().deriveFont(interpolatedSize);
					textField.setFont(font);
					if (!isDarkMode) {
						textField.setBackground(Color.WHITE);
					} else {
						textField.setBackground(DARK_BACK);
					}
					currentStep--;
				} else {
					textField.setFont(textField.getFont().deriveFont((float) targetFontSize));
					((Timer) e.getSource()).stop(); // Arrête l'animation
				}
			}
		});
		animationTimer.start();
	}

	private void colorRow(int rowIndex) {
	    String result = matrice.isValid(getWordFromRow(rowIndex)); // Utilise isValid pour obtenir le résultat

	    for (int col = 0; col < size; col++) {
	        JTextField textField = textFields[rowIndex][col];
	        char status = result.charAt(col); // Obtient le statut de chaque lettre

	        switch (status) {
	            case 'A': // Correcte et bien placée
	                textField.setBackground(new Color(0, 255, 50));
	                if (rowIndex < size - 1) {
	                    // Mettre à jour uniquement la lettre bien placée dans les lignes suivantes
	                    for (int nextRow = rowIndex + 1; nextRow < size; nextRow++) {
	                        textFields[nextRow][col].setText(String.valueOf(getWordFromRow(rowIndex).charAt(col)));
	                    }
	                }
	                break;
	            case 'B': // Présente mais mal placée
	                textField.setBackground(new Color(255, 165, 0));
	                break;
	            case 'C': // Incorrecte
	                textField.setBackground(new Color(255, 0, 60));
	                break;
	        }
	    }
	}


	private void updateKeyboard(int currentRow) {
		String word = getWordFromRow(currentRow);
		String result = matrice.isValid(word);

		for (int i = 0; i < word.length(); i++) {
			char letter = word.charAt(i);
			JButton keyButton = keyboardButtons.get(Character.toUpperCase(letter));

			if (keyButton != null) {
				switch (result.charAt(i)) {
				case 'A': // Lettre correcte et bien placée
					keyButton.setBackground(new Color(0, 255, 50));
					break;
				case 'B': // Lettre correcte mais mal placée
					keyButton.setBackground(new Color(255, 165, 0));
					break;
				case 'C': // Lettre incorrecte
					keyButton.setBackground(new Color(255, 0, 60));
					break;
				}
			}
		}
	}


	private void setupControlPanel(JPanel controlPanel) {
		elapsedTime = 300; // 5 minutes
		timerLabel = new JLabel("Temps restant: " + elapsedTime + " s");
		timer = new Timer(1000, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				elapsedTime--;
				timerLabel.setText("Temps restant: " + elapsedTime + " s");

				if (elapsedTime <= 0) {
					timer.stop();
					showEndGameDialog("Le temps est écoulé !");
				}
			}
		});
		timer.start();

		JButton restartButton = new JButton("Recommencer");
		restartButton.setBackground(DARK_BACK);
		restartButton.setForeground(Color.WHITE);
		restartButton.setFont(new Font("Arial", Font.BOLD, 14));
		restartButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				restartGame();
			}
		});

		JButton quitButton = new JButton("Quitter");
		quitButton.setBackground(DARK_BACK);
		quitButton.setForeground(Color.WHITE);
		quitButton.setFont(new Font("Arial", Font.BOLD, 14));
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

	private void showEndGameDialog(String endReason) {
        JDialog endGameDialog = new JDialog(gameFrame, "Jeu terminé", true);
        endGameDialog.setLayout(new BorderLayout());

        String message = endReason + "\nLe mot à deviner était : " + selectedWord;
        JLabel messageLabel = new JLabel("<html><center>" + message + "<br>Voulez-vous recommencer ou quitter ?</center></html>", JLabel.CENTER);
        endGameDialog.add(messageLabel, BorderLayout.NORTH);

        ImageIcon defeatIcon = new ImageIcon(getClass().getResource("fortnite-dance-take-the-l.gif"));
        JLabel gifLabel = new JLabel(defeatIcon);
        endGameDialog.add(gifLabel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton restartButton = new JButton("Recommencer");
        restartButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                restartGame();
                endGameDialog.dispose();
            }
        });
        buttonPanel.add(restartButton);

        JButton quitButton = new JButton("Quitter");
        quitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        buttonPanel.add(quitButton);

        endGameDialog.add(buttonPanel, BorderLayout.SOUTH);

        endGameDialog.pack();
        endGameDialog.setLocationRelativeTo(gameFrame);
        endGameDialog.setVisible(true);
    }

	private void showVictoryDialog() {
        JDialog victoryDialog = new JDialog(gameFrame, "Victoire !", true);
        victoryDialog.setLayout(new BorderLayout());

        String message = "Félicitations !\nVous avez trouvé le mot : " + selectedWord;
        JLabel messageLabel = new JLabel("<html><center>" + message + "<br>Voulez-vous recommencer ou quitter ?</center></html>", JLabel.CENTER);
        victoryDialog.add(messageLabel, BorderLayout.NORTH);

        ImageIcon victoryIcon = new ImageIcon(getClass().getResource("OMv.gif"));
        JLabel gifLabel = new JLabel(victoryIcon);
        victoryDialog.add(gifLabel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton restartButton = new JButton("Recommencer");
		restartButton.setBackground(DARK_BACK);
		restartButton.setForeground(Color.WHITE);
		restartButton.setFont(new Font("Arial", Font.BOLD, 14));
        restartButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                restartGame();
                victoryDialog.dispose();
            }
        });
        buttonPanel.add(restartButton);

        JButton quitButton = new JButton("Quitter");
		quitButton.setBackground(DARK_BACK);
		quitButton.setForeground(Color.WHITE);
		quitButton.setFont(new Font("Arial", Font.BOLD, 14));
        quitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        buttonPanel.add(quitButton);

        victoryDialog.add(buttonPanel, BorderLayout.SOUTH);

        // Affichage du dialogue
        victoryDialog.pack();
        victoryDialog.setLocationRelativeTo(gameFrame);
        victoryDialog.setVisible(true);
    }
	
	private void playBackgroundMusic() {
        try {
            URL musicPath = getClass().getResource("music.wav");
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(musicPath);

            Clip clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            clip.loop(Clip.LOOP_CONTINUOUSLY); // Jouer en boucle
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }

	@SuppressWarnings("unused")
	public static void main(String[] args) {
		MotusFrame h = new MotusFrame();
	}
}