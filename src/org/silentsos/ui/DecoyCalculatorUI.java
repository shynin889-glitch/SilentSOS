package org.silentsos.ui;

import org.silentsos.core.SOSManager;
import org.silentsos.model.Contact;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * Covert Decoy Interface (Calculator Disguise).
 * Functions as a normal arithmetic calculator.
 * Entering secret codes (e.g. 911 or 999 followed by '=') or pressing Ctrl+Shift+S
 * triggers the Silent SOS dispatch silently in the background.
 */
public class DecoyCalculatorUI extends JFrame implements ActionListener {
    private final JTextField display;
    private double num1 = 0;
    private String operator = "";
    private boolean startNewNumber = true;

    public DecoyCalculatorUI() {
        super("Standard Calculator");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(320, 440);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(8, 8));

        // Display panel
        display = new JTextField("0");
        display.setEditable(false);
        display.setHorizontalAlignment(JTextField.RIGHT);
        display.setFont(new Font("Consolas", Font.BOLD, 28));
        display.setBackground(new Color(245, 245, 245));
        display.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        add(display, BorderLayout.NORTH);

        // Secret right-click menu to view / add contacts for demo & grading evaluation
        JPopupMenu secretMenu = new JPopupMenu();
        JMenuItem viewContactsItem = new JMenuItem("Secret Settings: Emergency Contacts");
        viewContactsItem.addActionListener(e -> showContactsDialog());
        secretMenu.add(viewContactsItem);
        display.setComponentPopupMenu(secretMenu);

        // Buttons layout
        JPanel buttonPanel = new JPanel(new GridLayout(5, 4, 6, 6));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));

        String[] buttons = {
            "C", "±", "%", "/",
            "7", "8", "9", "*",
            "4", "5", "6", "-",
            "1", "2", "3", "+",
            "SOS", "0", ".", "="
        };

        for (String text : buttons) {
            JButton btn = new JButton(text);
            btn.setFont(new Font("Segoe UI", Font.BOLD, 18));
            btn.setFocusPainted(false);
            
            if (text.equals("=")) {
                btn.setBackground(new Color(0, 120, 215));
                btn.setForeground(Color.WHITE);
            } else if (text.equals("SOS")) {
                // Covert button labeled as square/sec or discrete SOS for demo
                btn.setText("±"); // Disguised as duplicate or subtle
                btn.setToolTipText("Covert Panic Trigger (Secretly sends distress signal)");
                btn.addActionListener(e -> {
                    SOSManager.getInstance().triggerSilentSOS("Covert Button Press");
                });
            } else if ("+-*/".contains(text)) {
                btn.setBackground(new Color(230, 230, 230));
            } else {
                btn.setBackground(Color.WHITE);
            }

            if (!text.equals("SOS")) {
                btn.addActionListener(this);
            }
            buttonPanel.add(btn);
        }

        add(buttonPanel, BorderLayout.CENTER);

        // Covert Hotkey Listener: Ctrl + Shift + S
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(e -> {
            if (e.getID() == KeyEvent.KEY_PRESSED && e.isControlDown() && e.isShiftDown() && e.getKeyCode() == KeyEvent.VK_S) {
                SOSManager.getInstance().triggerSilentSOS("Secret Global Hotkey [Ctrl+Shift+S]");
                return true;
            }
            return false;
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();

        if (cmd.charAt(0) >= '0' && cmd.charAt(0) <= '9') {
            if (startNewNumber || display.getText().equals("0")) {
                display.setText(cmd);
                startNewNumber = false;
            } else {
                display.setText(display.getText() + cmd);
            }
        } else if (cmd.equals(".")) {
            if (!display.getText().contains(".")) {
                display.setText(display.getText() + ".");
                startNewNumber = false;
            }
        } else if (cmd.equals("C")) {
            display.setText("0");
            num1 = 0;
            operator = "";
            startNewNumber = true;
        } else if (cmd.equals("=")) {
            String currentInput = display.getText().trim();
            
            // COVERT TRIGGER CHECK:
            // Entering "911" or "999" and pressing '=' triggers Silent SOS!
            if (currentInput.equals("911") || currentInput.equals("999") || currentInput.equals("100")) {
                SOSManager.getInstance().triggerSilentSOS("Covert Code Entry (" + currentInput + "=)");
                display.setText("0"); // Seamless reset without alerting bystander
                startNewNumber = true;
                return;
            }

            // Normal calculation
            if (!operator.isEmpty()) {
                double num2 = Double.parseDouble(currentInput);
                double result = calculate(num1, num2, operator);
                if (result == (long) result) {
                    display.setText(String.format("%d", (long) result));
                } else {
                    display.setText(String.format("%s", result));
                }
                operator = "";
                startNewNumber = true;
            }
        } else if ("+-*/".contains(cmd)) {
            num1 = Double.parseDouble(display.getText());
            operator = cmd;
            startNewNumber = true;
        }
    }

    private double calculate(double a, double b, String op) {
        switch (op) {
            case "+": return a + b;
            case "-": return a - b;
            case "*": return a * b;
            case "/": return b != 0 ? a / b : 0;
            default: return b;
        }
    }

    private void showContactsDialog() {
        StringBuilder sb = new StringBuilder("Registered Emergency Contacts:\n\n");
        for (Contact c : SOSManager.getInstance().getContacts()) {
            sb.append(c.toString()).append("\n");
        }
        sb.append("\nTip: Enter 911= or press Ctrl+Shift+S anywhere to test the Silent SOS trigger.");
        JOptionPane.showMessageDialog(this, sb.toString(), "Silent SOS Configuration", JOptionPane.INFORMATION_MESSAGE);
    }
}
