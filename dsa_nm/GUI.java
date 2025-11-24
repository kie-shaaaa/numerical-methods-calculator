import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Arrays;

import com.formdev.flatlaf.FlatLightLaf;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

//javac -cp ".;lib\exp4j-0.4.8.jar;lib\flatlaf-3.2.5.jar" *.java
//java -cp ".;lib\exp4j-0.4.8.jar;lib\flatlaf-3.2.5.jar" GUI

public class GUI {
    // Color constants
    private static final Color DARK_BACKGROUND = new Color(15, 20, 30);
    private static final Color DARK_SECONDARY = new Color(25, 30, 40);
    private static final Color ACCENT_BLUE = new Color(51, 153, 255);
    private static final Color TEXT_COLOR = new Color(220, 220, 220);

    private JFrame frame;
    private JTable resultTable;
    private JLabel finalResultLabel;
    private String selectedMethod = "Bisection Method";

    // Table renderers
    private DefaultTableCellRenderer leftRenderer;
    private DefaultTableCellRenderer centerRenderer;

    // Input components
    private JTextField functionField, lowerBoundField, upperBoundField, toleranceField;
    private JTextField guessField, guessField2, gxField;
    private JTextField[][] jacobiMatrixFields;
    private JTextField jacobiTolField;
    private JTextField[] jacobiEquationFields;

    private static final int MAX_DECIMAL_PLACES = 6; // Maximum decimal places for display

    public GUI() {
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
            UIManager.put("Panel.background", DARK_BACKGROUND);
            UIManager.put("TextField.background", DARK_SECONDARY);
            UIManager.put("TextField.foreground", TEXT_COLOR);
            UIManager.put("Label.foreground", TEXT_COLOR);
            UIManager.put("Button.background", DARK_SECONDARY);
            UIManager.put("Button.foreground", TEXT_COLOR);
            UIManager.put("Table.background", DARK_SECONDARY);
            UIManager.put("Table.foreground", TEXT_COLOR);
            UIManager.put("Table.gridColor", DARK_SECONDARY.brighter());
            UIManager.put("Table.selectionBackground", ACCENT_BLUE);
            UIManager.put("Table.selectionForeground", TEXT_COLOR);
            UIManager.put("TableHeader.background", DARK_SECONDARY.darker());
            UIManager.put("TableHeader.foreground", ACCENT_BLUE);
            UIManager.put("ComboBox.background", DARK_SECONDARY);
            UIManager.put("ComboBox.foreground", TEXT_COLOR);
            UIManager.put("ComboBox.selectionBackground", ACCENT_BLUE);
            UIManager.put("ComboBox.selectionForeground", TEXT_COLOR);
            UIManager.put("ScrollBar.thumb", DARK_SECONDARY.brighter());
            UIManager.put("ScrollBar.track", DARK_SECONDARY);
            UIManager.put("OptionPane.messageForeground", Color.BLACK);
        } catch (Exception ex) {
            System.err.println("Failed to initialize theme");
        }

        frame = new JFrame("Numerical Methods Calculator");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 800);
        frame.setResizable(false);
        frame.getContentPane().setBackground(DARK_BACKGROUND);

        showHomeScreen();
    }

    private void showHomeScreen() {
        JPanel homePanel = new JPanel(new BorderLayout());
        homePanel.setBackground(DARK_BACKGROUND);

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(DARK_BACKGROUND);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));

        JLabel titleLabel = new JLabel("Numerical Methods Calculator", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(TEXT_COLOR);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(titleLabel);

        centerPanel.add(Box.createVerticalStrut(30));

        JButton startButton = new JButton("Get Started");
        startButton.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        startButton.setBackground(ACCENT_BLUE);
        startButton.setForeground(TEXT_COLOR);
        startButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        startButton.setBorder(BorderFactory.createEmptyBorder(10, 40, 10, 40));
        startButton.addActionListener(e -> showCalculatorScreen());
        centerPanel.add(startButton);

        homePanel.add(centerPanel, BorderLayout.CENTER);
        frame.setContentPane(homePanel);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void showCalculatorScreen() {
        functionField = null;
        lowerBoundField = null;
        upperBoundField = null;
        toleranceField = null;
        guessField = null;
        guessField2 = null;
        gxField = null;
        jacobiMatrixFields = null;
        jacobiTolField = null;
        jacobiEquationFields = null;
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(DARK_BACKGROUND);

        // Left panel - Methods list
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBackground(DARK_SECONDARY);
        leftPanel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        leftPanel.setPreferredSize(new Dimension(200, 700));

        String[] methods = {
            "Bisection Method",
            "Fixed-Point Iteration",
            "Newton-Raphson",
            "Secant Method",
            "Jacobi Method"
        };

        for (String method : methods) {
            JButton btn = new JButton(method);
            btn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            btn.setBackground(selectedMethod.equals(method) ? ACCENT_BLUE : DARK_SECONDARY);
            btn.setForeground(TEXT_COLOR);
            btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(selectedMethod.equals(method) ? ACCENT_BLUE : ACCENT_BLUE.darker(), 1),
                BorderFactory.createEmptyBorder(9, 14, 9, 14)
            ));
            btn.setHorizontalAlignment(SwingConstants.LEFT);
            btn.setMaximumSize(new Dimension(180, 40));
            
            // Add hover effect
            btn.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    if (!selectedMethod.equals(method)) {
                        btn.setBackground(ACCENT_BLUE.darker());
                    }
                }
                public void mouseExited(java.awt.event.MouseEvent evt) {
                    if (!selectedMethod.equals(method)) {
                        btn.setBackground(DARK_SECONDARY);
                    }
                }
            });
            
            btn.addActionListener(e -> {
                selectedMethod = method;
                showCalculatorScreen();
            });
            leftPanel.add(btn);
            leftPanel.add(Box.createVerticalStrut(5));
        }

        leftPanel.add(Box.createVerticalGlue());

        JButton backBtn = new JButton("Back to Home");
        backBtn.setBackground(DARK_SECONDARY);
        backBtn.setForeground(TEXT_COLOR);
        backBtn.addActionListener(e -> showHomeScreen());
        leftPanel.add(backBtn);

        // Right panel - Calculator
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setBackground(DARK_BACKGROUND);
        rightPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Title
        JLabel methodLabel = new JLabel(selectedMethod, SwingConstants.LEFT);
        methodLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        methodLabel.setForeground(ACCENT_BLUE);
        methodLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        rightPanel.add(methodLabel);
        rightPanel.add(Box.createVerticalStrut(20));

        // Input panel
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(0, 2, 10, 10));
        inputPanel.setBackground(DARK_BACKGROUND);
        inputPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        inputPanel.setMaximumSize(new Dimension(600, 200));

        // Dynamic input fields based on method
        if (selectedMethod.equals("Bisection Method")) {
            inputPanel.add(new JLabel("Function f(x):"));
            functionField = createInputField();
            functionField.setToolTipText("Enter your function using any variable name.\n" +
                                       "Examples:\n" +
                                       "• x^2 - 4\n" +
                                       "• p^3 - 2*p - 5\n" +
                                       "• sin(t) - t/2");
            inputPanel.add(functionField);

            inputPanel.add(new JLabel("First Guess (a):"));
            lowerBoundField = createInputField();
            inputPanel.add(lowerBoundField);

            inputPanel.add(new JLabel("Second Guess (b):"));
            upperBoundField = createInputField();
            inputPanel.add(upperBoundField);

            inputPanel.add(new JLabel("Tolerance:"));
            toleranceField = createInputField();
            toleranceField.setToolTipText("Enter a value greater than 1e-6 (e.g., 0.001 or 1e-3)");
            inputPanel.add(toleranceField);

        } else if (selectedMethod.equals("Fixed-Point Iteration")) {
            inputPanel.add(new JLabel("Function g(x):"));
            gxField = createInputField();
            gxField.setToolTipText("Enter your function using any variable name.\n" +
                                  "Examples:\n" +
                                  "• sqrt(x + 1)\n" +
                                  "• (p + 5)^(1/3)\n" +
                                  "• cos(2*t)");
            inputPanel.add(gxField);

            inputPanel.add(new JLabel("Initial Guess:"));
            guessField = createInputField();
            inputPanel.add(guessField);

            inputPanel.add(new JLabel("Tolerance:"));
            toleranceField = createInputField();
            toleranceField.setToolTipText("Enter a value greater than 1e-6 (e.g., 0.001 or 1e-3)");
            inputPanel.add(toleranceField);

        } else if (selectedMethod.equals("Newton-Raphson")) {
            inputPanel.add(new JLabel("Function f(x):"));
            functionField = createInputField();
            functionField.setToolTipText("Enter your function using any variable name.\n" +
                                       "Examples:\n" +
                                       "• x^3 - x - 2\n" +
                                       "• p^2 - 4*p + 4\n" +
                                       "• sin(t) - t^2");
            inputPanel.add(functionField);

            inputPanel.add(new JLabel("Initial Guess:"));
            guessField = createInputField();
            inputPanel.add(guessField);

            inputPanel.add(new JLabel("Tolerance:"));
            toleranceField = createInputField();
            toleranceField.setToolTipText("Enter a value greater than 1e-6 (e.g., 0.001 or 1e-3)");
            inputPanel.add(toleranceField);

        } else if (selectedMethod.equals("Secant Method")) {
            inputPanel.add(new JLabel("Function f(x):"));
            functionField = createInputField();
            functionField.setToolTipText("Enter your function using any variable name.\n" +
                                       "Examples:\n" +
                                       "• x^3 - x - 2\n" +
                                       "• p^2 - 4*p + 4\n" +
                                       "• sin(t) - t^2");
            inputPanel.add(functionField);

            inputPanel.add(new JLabel("First Guess (x₀):"));
            guessField = createInputField();
            inputPanel.add(guessField);

            inputPanel.add(new JLabel("Second Guess (x₁):"));
            guessField2 = createInputField();
            inputPanel.add(guessField2);

            inputPanel.add(new JLabel("Tolerance:"));
            toleranceField = createInputField();
            toleranceField.setToolTipText("Enter a value greater than 1e-6 (e.g., 0.001 or 1e-3)");
            inputPanel.add(toleranceField);

        } else if (selectedMethod.equals("Jacobi Method")) {
            // Special layout for Jacobi Method
            inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));
            inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            inputPanel.setMaximumSize(new Dimension(600, 300));

            JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            topPanel.setOpaque(false);
            
            topPanel.add(new JLabel("Number of Equations:"));
            JComboBox<Integer> jacobiNCombo = new JComboBox<>(new Integer[]{2, 3});
            jacobiNCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            Dimension comboSize = new Dimension(60, 28);
            jacobiNCombo.setPreferredSize(comboSize);
            jacobiNCombo.setMaximumSize(comboSize);
            topPanel.add(jacobiNCombo);
            
            inputPanel.add(topPanel);
            inputPanel.add(Box.createVerticalStrut(10));

            JPanel equationsPanel = new JPanel();
            equationsPanel.setLayout(new BoxLayout(equationsPanel, BoxLayout.Y_AXIS));
            equationsPanel.setOpaque(false);
            
            // Initialize with 2 equations (default)
            jacobiEquationFields = new JTextField[2];
            JPanel initialEquations = createEquationsPanel(2);
            equationsPanel.add(initialEquations);
            inputPanel.add(equationsPanel);

            jacobiNCombo.addActionListener(e -> {
                int n = (Integer) jacobiNCombo.getSelectedItem();
                equationsPanel.removeAll();
                jacobiEquationFields = new JTextField[n];
                equationsPanel.add(createEquationsPanel(n));
                equationsPanel.revalidate();
                equationsPanel.repaint();
                
                // Update table columns when number of equations changes
                if (resultTable != null) {
                    DefaultTableModel model = (DefaultTableModel) resultTable.getModel();
                    String[] newColumns;
                    if (n == 2) {
                        newColumns = new String[]{"Iteration", "x", "y", "Error"};
                    } else {
                        newColumns = new String[]{"Iteration", "x", "y", "z", "Error"};
                    }
                    model.setColumnIdentifiers(newColumns);
                    model.setRowCount(0); // Clear existing data
                    
                    // Reset column renderers
                    resultTable.getColumnModel().getColumn(0).setCellRenderer(leftRenderer);
                    for (int i = 1; i < resultTable.getColumnCount(); i++) {
                        resultTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
                    }
                    resultTable.revalidate();
                    resultTable.repaint();
                }
            });
        }

        rightPanel.add(inputPanel);
        rightPanel.add(Box.createVerticalStrut(20));

        // Calculate button
        JButton calculateBtn = new JButton("Calculate");
        calculateBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        calculateBtn.setBackground(ACCENT_BLUE);
        calculateBtn.setForeground(TEXT_COLOR);
        calculateBtn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ACCENT_BLUE, 1),
            BorderFactory.createEmptyBorder(9, 19, 9, 19)
        ));
        calculateBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                calculateBtn.setBackground(ACCENT_BLUE.brighter());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                calculateBtn.setBackground(ACCENT_BLUE);
            }
        });
        calculateBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        calculateBtn.setMaximumSize(new Dimension(150, 40));
        calculateBtn.addActionListener(this::calculate);
        rightPanel.add(calculateBtn);
        rightPanel.add(Box.createVerticalStrut(20));

        // Results table
        String[] columnNames;
        if (selectedMethod.equals("Bisection Method")) {
            columnNames = new String[]{"Iteration", "x0", "x1", "x2", "f(x2)", "Error"};
        } else if (selectedMethod.equals("Fixed-Point Iteration")) {
            columnNames = new String[]{"Iteration", "x", "g(x)", "Error"};
        } else if (selectedMethod.equals("Newton-Raphson")) {
            columnNames = new String[]{"Iteration", "x", "f(x)", "f'(x)", "x_new"};
        } else if (selectedMethod.equals("Secant Method")) {
            columnNames = new String[]{"Iteration", "x0", "x1", "x2", "f(x0)", "f(x1)", "Error"};
        } else if (selectedMethod.equals("Jacobi Method")) {
            // Get the current number of equations
            int n = jacobiEquationFields != null ? jacobiEquationFields.length : 2;
            
            // Create column names array with correct size
            if (n == 2) {
                columnNames = new String[]{"Iteration", "x", "y", "Error"};
            } else {
                columnNames = new String[]{"Iteration", "x", "y", "z", "Error"};
            }
            
            // Update existing table if it exists
            if (resultTable != null) {
                DefaultTableModel model = (DefaultTableModel) resultTable.getModel();
                model.setColumnIdentifiers(columnNames);
                model.setRowCount(0);
                
                // Reset column renderers
                resultTable.getColumnModel().getColumn(0).setCellRenderer(leftRenderer);
                for (int i = 1; i < resultTable.getColumnCount(); i++) {
                    resultTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
                }
                resultTable.revalidate();
                resultTable.repaint();
            }
        } else {
            columnNames = new String[]{"Iteration", "Value"};
        }
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        resultTable = new JTable(tableModel) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table non-editable
            }
        };
        resultTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        resultTable.setRowHeight(25);
        resultTable.setBackground(DARK_SECONDARY);
        resultTable.setForeground(TEXT_COLOR);
        resultTable.setGridColor(DARK_SECONDARY.brighter());
        resultTable.getTableHeader().setBackground(DARK_SECONDARY.darker());
        resultTable.getTableHeader().setForeground(ACCENT_BLUE);
        resultTable.getTableHeader().setReorderingAllowed(false);

        // Initialize renderers if not already initialized
        if (centerRenderer == null) {
            centerRenderer = new DefaultTableCellRenderer();
            centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        }
        if (leftRenderer == null) {
            leftRenderer = new DefaultTableCellRenderer();
            leftRenderer.setHorizontalAlignment(SwingConstants.LEFT);
            leftRenderer.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 0));
        }

        // Apply renderers
        for (int i = 1; i < resultTable.getColumnCount(); i++) {
            resultTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
        resultTable.getColumnModel().getColumn(0).setCellRenderer(leftRenderer);
        resultTable.getColumnModel().getColumn(0).setPreferredWidth(90);

        JScrollPane tableScroll = new JScrollPane(resultTable);
        tableScroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        tableScroll.setPreferredSize(new Dimension(600, 300));
        rightPanel.add(tableScroll);
        rightPanel.add(Box.createVerticalStrut(20));

        // Final result
        finalResultLabel = new JLabel(" ", SwingConstants.LEFT);
        finalResultLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        finalResultLabel.setForeground(ACCENT_BLUE);
        finalResultLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        rightPanel.add(finalResultLabel);

        mainPanel.add(leftPanel, BorderLayout.WEST);
        mainPanel.add(rightPanel, BorderLayout.CENTER);

        frame.setContentPane(mainPanel);
        frame.setVisible(true);
    }

    private JPanel createMatrixPanel(int n) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2, 2, 2, 2);
        
        for (int i = 0; i < n; i++) {
            gbc.gridy = i;
            for (int j = 0; j < n + 1; j++) {
                if (j == n) {
                    JLabel equalsLabel = new JLabel("=");
                    equalsLabel.setForeground(ACCENT_BLUE);
                    equalsLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
                    gbc.gridx = j;
                    gbc.insets = new Insets(2, 10, 2, 10);
                    panel.add(equalsLabel, gbc);
                }
                
                gbc.gridx = (j == n) ? j + 1 : j;
                gbc.insets = new Insets(2, 2, 2, 2);
                jacobiMatrixFields[i][j] = createInputField();
                jacobiMatrixFields[i][j].setPreferredSize(new Dimension(60, 28));
                panel.add(jacobiMatrixFields[i][j], gbc);
            }
        }
        return panel;
    }

    private JTextField createInputField() {
        JTextField textField = new JTextField();
        textField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        textField.setBackground(DARK_SECONDARY);
        textField.setForeground(TEXT_COLOR);
        textField.setCaretColor(ACCENT_BLUE);
        textField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ACCENT_BLUE.darker(), 1),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));

        // Add focus listener for highlight effect
        textField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                textField.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(ACCENT_BLUE, 2),
                    BorderFactory.createEmptyBorder(4, 4, 4, 4)
                ));
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                textField.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(ACCENT_BLUE.darker(), 1),
                    BorderFactory.createEmptyBorder(5, 5, 5, 5)
                ));
            }
        });
        return textField;
    }

    private String getFormatString(double tolerance) {
        int decimalPlaces = Math.min(MAX_DECIMAL_PLACES, getDecimalPlaces(tolerance));
        return "%." + decimalPlaces + "f";
    }

    private int getDecimalPlaces(double tolerance) {
        String tolStr = String.format("%e", tolerance);
        int idx = tolStr.indexOf('e');
        if (idx >= 0) {
            int exp = Integer.parseInt(tolStr.substring(idx + 1));
            return Math.abs(exp);
        }
        String[] parts = Double.toString(tolerance).split("\\.");
        return (parts.length > 1) ? parts[1].length() : 0;
    }

    private void calculate(ActionEvent e) {
        try {
            DefaultTableModel tableModel = (DefaultTableModel) resultTable.getModel();
            tableModel.setRowCount(0); // Clear previous results
            int maxIterations = 100;
            
            if (selectedMethod.equals("Bisection Method")) {
                String function = functionField.getText().toLowerCase().trim().replaceAll("\\s+", "");
                if (function.isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "Please enter a function.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String x0Input = lowerBoundField.getText().trim().replaceAll("\\s+", "");
                String x1Input = upperBoundField.getText().trim().replaceAll("\\s+", "");
                String tolInput = toleranceField.getText().trim().replaceAll("\\s+", "");

                if (x0Input.isEmpty() || x1Input.isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "Please enter both initial guesses.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (tolInput.isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "Please enter a tolerance value.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                double x0, x1, tolerance;
                try {
                    x0 = Double.parseDouble(x0Input);
                    x1 = Double.parseDouble(x1Input);
                    tolerance = Double.parseDouble(tolInput);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(frame, "Invalid number format. Please enter valid numbers.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (Double.isNaN(x0) || Double.isInfinite(x0) ||
                    Double.isNaN(x1) || Double.isInfinite(x1)) {
                    JOptionPane.showMessageDialog(frame, "Invalid guesses. Please enter valid numbers.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (tolerance <= 0) {
                    JOptionPane.showMessageDialog(frame, "Tolerance must be greater than 0.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (tolerance < 1e-6) {
                    JOptionPane.showMessageDialog(frame, "Tolerance cannot be smaller than 1e-6.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String format = getFormatString(tolerance);
                Expression expression = new ExpressionBuilder(function).variable("x").build();
                
                // Verify that f(a) and f(b) have opposite signs
                double fa = expression.setVariable("x", x0).evaluate();
                double fb = expression.setVariable("x", x1).evaluate();
                if (fa * fb >= 0) {
                    JOptionPane.showMessageDialog(frame, "The function must have opposite signs at the bounds.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                java.util.Stack<bisection_method.IterationData> history = new java.util.Stack<>();
                double result = bisection_method.bisectionMethod(expression, x0, x1, tolerance, maxIterations, history);

                for (bisection_method.IterationData d : history) {
                    tableModel.addRow(new Object[]{
                        history.indexOf(d) + 1,
                        String.format(format, d.a),
                        String.format(format, d.b),
                        String.format(format, d.c),
                        String.format(format, d.fc),
                        String.format(format, Math.abs(d.b - d.a) / 2)
                    });
                }
                finalResultLabel.setText("Final Result: " + String.format(format, result));
            }
            else if (selectedMethod.equals("Fixed-Point Iteration")) {
                String function = gxField.getText().trim()
                                    .toLowerCase()  // Convert everything to lowercase first
                                    .replaceAll("\\s+", "")  // Remove all whitespace
                                    .replaceAll("e\\^([\\w\\-\\+\\(\\)]+)", "exp($1)");  // Convert e^expression to exp(expression)
                
                if (function.isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "Please enter g(x) function.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String x0Input = guessField.getText().trim().replaceAll("\\s+", "");
                String tolInput = toleranceField.getText().trim().replaceAll("\\s+", "");

                if (x0Input.isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "Please enter an initial guess.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (tolInput.isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "Please enter a tolerance value.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                double x0, tolerance;
                try {
                    x0 = Double.parseDouble(x0Input);
                    tolerance = Double.parseDouble(tolInput);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(frame, "Invalid number format. Please enter valid numbers.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (Double.isNaN(x0) || Double.isInfinite(x0)) {
                    JOptionPane.showMessageDialog(frame, "Invalid initial guess. Please enter a valid number.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (tolerance <= 0) {
                    JOptionPane.showMessageDialog(frame, "Tolerance must be greater than 0.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (tolerance < 1e-6) {
                    JOptionPane.showMessageDialog(frame, "Tolerance cannot be smaller than 1e-6.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String format = getFormatString(tolerance);

                Expression expression = new ExpressionBuilder(function).variable("x").build();
                double derivative = fixed_point.derive(expression, x0);
                if (Math.abs(derivative) > 1) {
                    JOptionPane.showMessageDialog(frame, "Warning: |g'(x)| > 1 at initial guess. Method may not converge.", "Convergence Warning", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                ArrayList<fixed_point.IterationData> history = new ArrayList<>();
                double result = fixed_point.fixedPointRecursive(expression, x0, tolerance, maxIterations, 1, history);

                for (int i = 0; i < history.size(); i++) {
                    fixed_point.IterationData d = history.get(i);
                    double error = (i == 0) ? Double.NaN : Math.abs(d.gx - history.get(i - 1).gx);
                    tableModel.addRow(new Object[]{
                        i + 1,
                        String.format(format, d.x),
                        String.format(format, d.gx),
                        (Double.isNaN(error) ? "N/A" : String.format(format, error))
                    });
                }
                finalResultLabel.setText("Final Result: " + String.format(format, result));
            }
            else if (selectedMethod.equals("Newton-Raphson")) {
                String function = functionField.getText().trim()
                                    .toLowerCase()  // Convert everything to lowercase first
                                    .replaceAll("\\s+", "")  // Remove all whitespace
                                    .replaceAll("e\\^([\\w\\-\\+\\(\\)]+)", "exp($1)");  // Convert e^expression to exp(expression)

                if (function.isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "Please enter a function.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String x0Input = guessField.getText().trim().replaceAll("\\s+", "");
                String tolInput = toleranceField.getText().trim().replaceAll("\\s+", "");

                if (x0Input.isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "Please enter an initial guess.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (tolInput.isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "Please enter a tolerance value.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                double x0, tolerance;
                try {
                    x0 = Double.parseDouble(x0Input);
                    tolerance = Double.parseDouble(tolInput);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(frame, "Invalid number format. Please enter valid numbers.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (Double.isNaN(x0) || Double.isInfinite(x0)) {
                    JOptionPane.showMessageDialog(frame, "Invalid initial guess. Please enter a valid number.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (tolerance <= 0) {
                    JOptionPane.showMessageDialog(frame, "Tolerance must be greater than 0.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (tolerance < 1e-6) {
                    JOptionPane.showMessageDialog(frame, "Tolerance cannot be smaller than 1e-6.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String format = getFormatString(tolerance);

                try {
                    Queue<newton_raphson.IterationData> iterations = newton_raphson.newtonRaphson(function, x0, tolerance, maxIterations);
                    newton_raphson.IterationData last = null;

                    for (newton_raphson.IterationData data : iterations) {
                        tableModel.addRow(new Object[]{
                            data.iteration,
                            String.format(format, data.x),
                            String.format(format, data.fx),
                            String.format(format, data.dfx),
                            String.format(format, data.xNew)
                        });
                        last = data;
                    }

                    if (last != null) {
                        finalResultLabel.setText("Final Result: " + String.format(format, last.xNew));
                    }
                } catch (ArithmeticException ex) {
                    JOptionPane.showMessageDialog(frame, ex.getMessage(), "Calculation Error", JOptionPane.ERROR_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(frame,
                        "Error: " + ex.getMessage() + "\nNote: For exponential expressions, use e^(-x) or exp(-x)",
                        "Calculation Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
            else if (selectedMethod.equals("Secant Method")) {
                String function = functionField.getText().toLowerCase().trim().replaceAll("\\s+", "");
                if (function.isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "Please enter a function.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String x0Input = guessField.getText().trim().replaceAll("\\s+", "");
                String x1Input = guessField2.getText().trim().replaceAll("\\s+", "");
                String tolInput = toleranceField.getText().trim().replaceAll("\\s+", "");

                if (x0Input.isEmpty() || x1Input.isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "Please enter both initial guesses.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (tolInput.isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "Please enter a tolerance value.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                double x0, x1, tolerance;
                try {
                    x0 = Double.parseDouble(x0Input);
                    x1 = Double.parseDouble(x1Input);
                    tolerance = Double.parseDouble(tolInput);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(frame, "Invalid number format. Please enter valid numbers.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (Double.isNaN(x0) || Double.isInfinite(x0) ||
                    Double.isNaN(x1) || Double.isInfinite(x1)) {
                    JOptionPane.showMessageDialog(frame, "Invalid guesses. Please enter valid numbers.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (tolerance <= 0) {
                    JOptionPane.showMessageDialog(frame, "Tolerance must be greater than 0.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (tolerance < 1e-6) {
                    JOptionPane.showMessageDialog(frame, "Tolerance cannot be smaller than 1e-6.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String format = getFormatString(tolerance);

                Expression expression = new ExpressionBuilder(function).variable("x").build();
                LinkedList<secant_method.IterationData> history = new LinkedList<>();
                double result = secant_method.secantIteration(expression, x0, x1, tolerance, maxIterations, history);

                for (secant_method.IterationData d : history) {
                    tableModel.addRow(new Object[]{
                        history.indexOf(d) + 1,
                        String.format(format, d.x0),
                        String.format(format, d.x1),
                        String.format(format, d.x2),
                        String.format(format, d.fx0),
                        String.format(format, d.fx1),
                        String.format(format, d.error)
                    });
                }
                finalResultLabel.setText("Final Result: " + String.format(format, result));
            }
            else if (selectedMethod.equals("Jacobi Method")) {
                int n = jacobiEquationFields.length;
                String[] equations = new String[n];
                
                // Get equations
                for (int i = 0; i < n; i++) {
                    String eq = jacobiEquationFields[i].getText().trim();
                    if (eq.isEmpty()) {
                        JOptionPane.showMessageDialog(frame, "Please fill in all equations.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    equations[i] = eq;
                }
                
                String tolInput = jacobiTolField.getText().trim().replaceAll("\\s+", "");
                if (tolInput.isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "Please enter a tolerance value.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                double tolerance;
                try {
                    tolerance = Double.parseDouble(tolInput);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(frame, "Invalid tolerance format. Please enter a valid number.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (tolerance <= 0) {
                    JOptionPane.showMessageDialog(frame, "Tolerance must be greater than 0.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (tolerance < 1e-6) {
                    JOptionPane.showMessageDialog(frame, "Tolerance cannot be smaller than 1e-6.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String format = getFormatString(tolerance);

                try {
                    ArrayList<jacobi_method.IterationData> iterations = new ArrayList<>();
                    double[] solution = jacobi_method.solveJacobi(equations, tolerance, maxIterations, iterations);
                    
                    // Clear existing table data
                    tableModel.setRowCount(0);
                    
                    // Populate table
                    for (int iter = 0; iter < iterations.size(); iter++) {
                        jacobi_method.IterationData data = iterations.get(iter);
                        
                        // Create row with correct number of columns
                        Object[] row = new Object[tableModel.getColumnCount()];
                        row[0] = iter + 1;  // Iteration number
                        
                        // Add values (x, y, z)
                        for (int i = 0; i < n; i++) {
                            row[i + 1] = String.format(format, data.x[i]);
                        }
                        
                        // Add error in last column
                        row[row.length - 1] = iter == 0 ? "N/A" : String.format(format, data.error);
                        
                        tableModel.addRow(row);
                    }
                    
                    // Display final result
                    StringBuilder result = new StringBuilder("Solution: ");
                    String[] varNames = {"x", "y", "z"};
                    for (int i = 0; i < solution.length; i++) {
                        result.append(varNames[i]).append(" = ").append(String.format(format, solution[i]));
                        if (i < solution.length - 1) {
                            result.append(",  ");
                        }
                    }
                    finalResultLabel.setText(result.toString());
                } catch (ArithmeticException ex) {
                    int choice = JOptionPane.showConfirmDialog(frame,
                        "The system is not diagonally dominant. The method may not converge.\nDo you want to continue?",
                        "Convergence Warning",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE);
                    if (choice != JOptionPane.YES_OPTION) {
                        return;
                    }
                }
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(frame,
                    "Error: " + ex.getMessage(),
                    "Calculation Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private JPanel createEquationsPanel(int n) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        for (int i = 0; i < n; i++) {
            JPanel eqPanel = new JPanel();
            eqPanel.setLayout(new BoxLayout(eqPanel, BoxLayout.X_AXIS));
            eqPanel.setOpaque(false);
            eqPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
            
            JLabel eqLabel = new JLabel("Equation " + (i + 1) + ": ");
            eqLabel.setForeground(TEXT_COLOR);
            eqLabel.setPreferredSize(new Dimension(80, 28));
            eqPanel.add(eqLabel);
            
            jacobiEquationFields[i] = createInputField();
            jacobiEquationFields[i].setMaximumSize(new Dimension(400, 28));
            jacobiEquationFields[i].setPreferredSize(new Dimension(400, 28));
            String tooltip = "Enter your equation using any variable names.\n" +
                           "Format: coefficient × variable [± coefficient × variable]* = number\n" +
                           "Examples:\n" +
                           "• 3x + 2y = 5\n" +
                           "• 2p - 3q = 1\n" +
                           "• a + 2b + 3c = 6\n" +
                           "Note: Use the same variable names consistently across all equations.";
            jacobiEquationFields[i].setToolTipText(tooltip);
            eqPanel.add(jacobiEquationFields[i]);
            eqPanel.add(Box.createHorizontalGlue());
            
            panel.add(eqPanel);
        }

        // Add tolerance field aligned with equations
        JPanel tolPanel = new JPanel();
        tolPanel.setLayout(new BoxLayout(tolPanel, BoxLayout.X_AXIS));
        tolPanel.setOpaque(false);
        tolPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
        
        JLabel tolLabel = new JLabel("Tolerance: ");
        tolLabel.setForeground(TEXT_COLOR);
        tolLabel.setPreferredSize(new Dimension(80, 28));
        tolPanel.add(tolLabel);
        
        jacobiTolField = createInputField();
        jacobiTolField.setMaximumSize(new Dimension(400, 28));
        jacobiTolField.setPreferredSize(new Dimension(400, 28));
        jacobiTolField.setToolTipText("Enter a value greater than 1e-6 (e.g., 0.001 or 1e-3)");
        tolPanel.add(jacobiTolField);
        tolPanel.add(Box.createHorizontalGlue());
        
        panel.add(tolPanel);

        return panel;
    }

    private String extractVariableName(String function) {
        // Default to 'x' if no variable is found
        String varName = "x";
        
        // Remove all spaces and special characters, keeping only letters
        String[] parts = function.replaceAll("[^a-zA-Z]", " ").trim().split("\\s+");
        
        // Use the first variable name found
        if (parts.length > 0 && !parts[0].isEmpty()) {
            varName = parts[0];
        }
        
        return varName;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GUI());
    }
}