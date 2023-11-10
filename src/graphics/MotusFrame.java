package graphics;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class TusmoGameWithLetters {

    private JFrame frame;
    private JPanel panel;
    private JTextField[][] textFields;
    private int size; 

    public TusmoGameWithLetters(int size) {
        this.size = size;

        frame = new JFrame("MOTUS");
        frame.setSize(400, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        panel = new JPanel(new GridLayout(size, size));
        textFields = new JTextField[size][size];

        KeyAdapter keyAdapter = new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                JTextField source = (JTextField) e.getSource();
                if (source.getText().length() >= 1) {
                    e.consume();
                }
            }

            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    moveFocus((JTextField) e.getSource());
                }
            }
        };

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                textFields[i][j] = new JTextField();
                textFields[i][j].setHorizontalAlignment(JTextField.CENTER);
                textFields[i][j].addKeyListener(keyAdapter);
                panel.add(textFields[i][j]);
            }
        }

        frame.add(panel);
        frame.setVisible(true);
    }

    private void moveFocus(JTextField currentField) {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (textFields[i][j] == currentField) {
                    if (j < size - 1) {
                        textFields[i][j + 1].requestFocus();
                    } else if (i < size - 1) {
                        textFields[i + 1][0].requestFocus();
                    }
                    return;
                }
            }
        }
    }

    public static void main(String[] args) {
        int size = Integer.parseInt(JOptionPane.showInputDialog("Enter the size of the grid:"));
        new TusmoGameWithLetters(size);
    }
}
