package graphics;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class MotusFrame {

    private JFrame initialFrame, gameFrame;
    private JPanel panel;
    private JTextField[][] textFields;
    private int size;
    private Timer timer;
    private JLabel timerLabel;
    private int elapsedTime;

    private static final int MIN_GRID_SIZE = 5;
    private static final int MAX_GRID_SIZE = 10;

    public MotusFrame() {
        createInitialFrame();
    }

    private void createInitialFrame() {
        initialFrame = new JFrame("Select Grid Size");
        initialFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        initialFrame.setLayout(new FlowLayout());

        Integer[] sizeOptions = new Integer[MAX_GRID_SIZE - MIN_GRID_SIZE + 1];
        for (int i = 0; i < sizeOptions.length; i++) {
            sizeOptions[i] = MIN_GRID_SIZE + i;
        }

        JComboBox<Integer> gridSizes = new JComboBox<>(sizeOptions);
        JButton submitButton = new JButton("Start Game");
        submitButton.addActionListener(e -> {
            size = (int) gridSizes.getSelectedItem();
            createAndShowGameGUI();
            initialFrame.dispose();
        });

        initialFrame.add(gridSizes);
        initialFrame.add(submitButton);
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

        gameFrame.add(panel, BorderLayout.CENTER);
        gameFrame.add(controlPanel, BorderLayout.EAST);

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

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                textFields[i][j] = new JTextField();
                textFields[i][j].setHorizontalAlignment(JTextField.CENTER);
                textFields[i][j].addKeyListener(keyAdapter);
                panel.add(textFields[i][j]);
            }
        }
    }

    private KeyAdapter createKeyAdapter() {
        return new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                JTextField source = (JTextField) e.getSource();
                if (source.getText().length() >= 1) {
                    e.consume();
                }
            }

            public void keyPressed(KeyEvent e) {
                JTextField source = (JTextField) e.getSource();
                if (Character.isLetter(e.getKeyChar())) {
                    source.setText(String.valueOf(e.getKeyChar()));
                    moveFocusRight(source);
                } else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    moveFocusDown(source);
                }
            }
        };
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
        for (int j = 0; j < size; j++) {
            if (textFields[currentRow][j].getText().trim().isEmpty()) {
                allFilled = false;
                break;
            }
        }

        if (allFilled && currentRow < size - 1) {
            textFields[currentRow + 1][0].requestFocus();
        }
    }

    private void setupControlPanel(JPanel controlPanel) {
        elapsedTime = 0;
        timerLabel = new JLabel("Temps: 0 s");
        timer = new Timer(1000, e -> {
            elapsedTime++;
            timerLabel.setText("Temps: " + elapsedTime + " s");
        });
        timer.start();

        JButton restartButton = new JButton("Recommencer");
        restartButton.addActionListener(e -> restartGame());

        JButton quitButton = new JButton("Quitter");
        quitButton.addActionListener(e -> System.exit(0));

        controlPanel.add(timerLabel);
        controlPanel.add(restartButton);
        controlPanel.add(quitButton);
    }

    private void restartGame() {
        timer.stop();
        elapsedTime = 0;
        timerLabel.setText("Temps: 0 s");

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                textFields[i][j].setText("");
            }
        }

        timer.start();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MotusFrame::new);
    }
}
