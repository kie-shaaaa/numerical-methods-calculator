import java.util.ArrayList;
import java.util.Scanner;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
//javac -cp ".;lib\exp4j-0.4.8.jar;lib\flatlaf-3.2.5.jar" *.java
//java -cp ".;lib\exp4j-0.4.8.jar" jacobi_method
public class jacobi_method {
    public static class IterationData {
        double[] x;
        double error;
        
        IterationData(double[] x, double error) {
            this.x = x.clone();
            this.error = error;
        }
    }
    
    // Parse equation and extract coefficients
    public static double[] parseEquation(String equation, int numVars) {
        equation = equation.replaceAll("\\s+", "").toLowerCase();
        double[] coefficients = new double[numVars + 1]; // +1 for constant term
        
        // Split into left and right sides
        String[] sides = equation.split("=");
        if (sides.length != 2) {
            throw new IllegalArgumentException("Invalid equation format. Must contain exactly one '='");
        }
        
        String leftSide = sides[0];  
        try {
            double constant = Double.parseDouble(sides[1]);
            coefficients[numVars] = constant; // Store constant term
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Right side must be a number");
        }

        // Find all terms with variables
        String[] terms = leftSide.split("(?=[-+])"); // Split at + or - while keeping the sign
        for (String term : terms) {
            term = term.trim();
            if (term.isEmpty()) continue;

            // Find the variable in this term
            String varPart = term.replaceAll("[-+.0-9]", "");
            if (varPart.isEmpty()) continue; // Skip constant terms on left side

            // Extract coefficient
            double coef;
            if (term.matches("^[-+]?" + varPart + "$")) {
                coef = term.startsWith("-") ? -1 : 1;
            } else {
                String coefStr = term.substring(0, term.indexOf(varPart));
                if (coefStr.equals("+")) coefStr = "1";
                else if (coefStr.equals("-")) coefStr = "-1";
                try {
                    coef = Double.parseDouble(coefStr);
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Invalid coefficient format in term: " + term);
                }
            }

            // Map variable to position (x -> 0, y -> 1, z -> 2) 
            int varIndex;
            switch (varPart.toLowerCase()) {
                case "x":
                    varIndex = 0;
                    break;
                case "y":
                    varIndex = 1;
                    break;
                case "z":
                    varIndex = 2;
                    break;
                default:
                    throw new IllegalArgumentException("Invalid variable name: " + varPart + ". Use x, y, or z.");
            }

            // Check if this variable was already found in this equation
            if (coefficients[varIndex] != 0) {
                throw new IllegalArgumentException("Variable " + varPart + " appears more than once in the equation");
            }
            
            coefficients[varIndex] = coef;
        }
        
        // Verify all variables are present
        for (int i = 0; i < numVars; i++) {
            if (coefficients[i] == 0) {
                String missingVar = (i == 0) ? "x" : (i == 1) ? "y" : "z";
                throw new IllegalArgumentException("Missing variable " + missingVar + " in equation: " + equation);
            }
        }
        
        return coefficients;
    }
    
    private static double extractCoefficient(String term) {
        term = term.trim();
        if (term.matches("^[-+]?[a-zA-Z]+$")) { // Just a variable with optional sign
            return term.startsWith("-") ? -1 : 1;
        }
        // Remove the variable part to get coefficient
        String coefStr = term.replaceAll("[a-zA-Z]+$", "");
        if (coefStr.equals("+")) return 1;
        if (coefStr.equals("-")) return -1;
        return Double.parseDouble(coefStr);
    }

    public static double[] solveJacobi(String[] equations, double tolerance, int maxIterations, ArrayList<IterationData> history) {
        int n = equations.length;
        double[][] matrix = new double[n][n + 1];
        
        // Parse equations into matrix form
        try {
            for (int i = 0; i < n; i++) {
                double[] coeffs = parseEquation(equations[i], n);
                System.arraycopy(coeffs, 0, matrix[i], 0, n + 1);
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Error parsing equations: " + e.getMessage() + 
                "\nFormat should be: ax + by = c (for 2 variables) or ax + by + cz = d (for 3 variables)");
        }
        
        // Try to make matrix diagonally dominant by rearranging rows
        if (!isDiagonallyDominant(matrix)) {
            if (!makeMatrixDiagonallyDominant(matrix)) {
                throw new ArithmeticException("Cannot make system diagonally dominant. The method may not converge.");
            }       
        }
            
        // Initialize solution vector
        double[] x = new double[n];
        double[] xNew = new double[n];
        
        // Jacobi iteration
        for (int iter = 0; iter < maxIterations; iter++) {
            double maxError = 0;
            
            for (int i = 0; i < n; i++) {
                double sum = matrix[i][n]; // constant term
                for (int j = 0; j < n; j++) {
                    if (j != i) {
                        sum -= matrix[i][j] * x[j];
                    }
                }
                xNew[i] = sum / matrix[i][i];
            }
            
            // Calculate error and update solution
            maxError = 0;
            for (int i = 0; i < n; i++) {
                double error = Math.abs(xNew[i] - x[i]);
                maxError = Math.max(maxError, error);
                x[i] = xNew[i];
            }
            
            history.add(new IterationData(x, maxError));
            
            if (maxError < tolerance) {
                break;
            }
        }
        
        return x;
    }

    /*private static boolean makeMatrixDiagonallyDominant(double[][] matrix) {
        int n = matrix.length;
        boolean[] used = new boolean[n];
        
        // Try to find a diagonally dominant arrangement
        for (int i = 0; i < n; i++) {
            double maxRowSum = -1;
            int bestRow = -1;
            
            // Find the row that can be made diagonally dominant
            for (int row = 0; row < n; row++) {
                if (used[row]) continue;
                
                // For each position as diagonal
                for (int col = 0; col < n; col++) {
                    if (isRowDominantAtPosition(matrix[row], col)) {
                        double rowSum = Math.abs(matrix[row][col]);
                        if (rowSum > maxRowSum) {
                            maxRowSum = rowSum;
                            bestRow = row;
                        }
                    }
                }
            }
            
            if (bestRow == -1) return false;
            used[bestRow] = true;
            
            // Swap rows if necessary
            if (bestRow != i) {
                double[] temp = matrix[i];
                matrix[i] = matrix[bestRow];
                matrix[bestRow] = temp;
            }
        }
        
        return true;
    } */

    private static boolean makeMatrixDiagonallyDominant(double[][] matrix) {
    int n = matrix.length;
    
    // Try all permutations to find a diagonally dominant arrangement
    for (int i = 0; i < n; i++) {
        // Find the row with the largest element in column i
        int maxRow = i;
        for (int j = i + 1; j < n; j++) {
            if (Math.abs(matrix[j][i]) > Math.abs(matrix[maxRow][i])) {
                maxRow = j;
            }
        }
        
        // Swap rows
        double[] temp = matrix[i];
        matrix[i] = matrix[maxRow];
        matrix[maxRow] = temp;
        
        // Check if current row is diagonally dominant
        if (!isRowDiagonallyDominant(matrix, i)) {
            return false; // Cannot make diagonally dominant
        }
    }
    return true;
}

private static boolean isRowDiagonallyDominant(double[][] matrix, int row) {
    double diagonal = Math.abs(matrix[row][row]);
    double sum = 0.0;
    for (int j = 0; j < matrix.length; j++) {
        if (j != row) {
            sum += Math.abs(matrix[row][j]);
        }
    }
    return diagonal > sum;
}

    private static boolean isRowDominantAtPosition(double[] row, int pos) {
        double diagonal = Math.abs(row[pos]);
        double sum = 0;
        for (int j = 0; j < row.length - 1; j++) { // -1 to exclude constant term
            if (j != pos) {
                sum += Math.abs(row[j]);
            }
        }
        return diagonal > sum;
    }

    public static boolean isDiagonallyDominant(double[][] matrix) {
        int n = matrix.length;
        for (int i = 0; i < n; i++) {
            double diagonal = Math.abs(matrix[i][i]);
            double sum = 0;
            for (int j = 0; j < n; j++) {  
                if (i != j) sum += Math.abs(matrix[i][j]);
            }
            if (diagonal <= sum) return false;
        }       
        return true;        
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        ArrayList<IterationData> history = new ArrayList<>();
        
        try {
            System.out.println("Enter number of equations (2 or 3): ");
            int n = Integer.parseInt(scanner.nextLine().trim());
            
            if (n < 2 || n > 3) {
                System.out.println("Please enter either 2 or 3 equations.");
                return;
            }
            
            String[] equations = new String[n];
            System.out.println("\nEnter the equations (e.g., 3x + 2y = 5):");
            for (int i = 0; i < n; i++) {
                System.out.print("Equation " + (i + 1) + ": ");
                equations[i] = scanner.nextLine().trim();
            }
            
            System.out.println("\nEnter tolerance (e.g., 0.001):");
            double tolerance = Double.parseDouble(scanner.nextLine().trim());
            
            if (tolerance <= 0) {
                System.out.println("Tolerance must be greater than 0.");
                return;
            }
            if (tolerance < 1e-6) {
                System.out.println("Tolerance cannot be smaller than 1e-6.");
                return;
            }
            
            double[] solution = solveJacobi(equations, tolerance, 100, history);
            
            System.out.println("\nSolution:");
            for (int i = 0; i < solution.length; i++) {
                String var = (i == 0) ? "x" : (i == 1) ? "y" : "z";
                System.out.printf("%s = %.6f\n", var, solution[i]);
            }
            
            System.out.println("\nIterations: " + history.size());
            
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
