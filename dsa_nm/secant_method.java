import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import java.util.LinkedList;
import java.util.Scanner;
// javac -cp ".;lib/exp4j-0.4.8.jar" secant_method.java
// java -cp ".;lib/exp4j-0.4.8.jar" secant_method   
public class secant_method {
    public static class IterationData {
        double x0, x1, x2, fx0, fx1, error;
        IterationData(double x0, double x1, double x2, double fx0, double fx1, double error) {
            this.x0 = x0;
            this.x1 = x1;
            this.x2 = x2;
            this.fx0 = fx0;
            this.fx1 = fx1;
            this.error = error;
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        LinkedList<IterationData> history = new LinkedList<>();
        int maxIterations = 100;

        try {
            System.out.println("Enter function f(x) (e.g., x^3 - x - 2, x^2 - 4*x + 4, sin(x) - x^2): ");
            String funcInput = scanner.nextLine()
                                   .toLowerCase()  // Convert everything to lowercase first
                                   .replaceAll("\\s+", "")  // Remove all whitespace
                                   .replaceAll("e\\^([\\w\\-\\+\\(\\)]+)", "exp($1)");  // Convert e^expression to exp(expression)
            
            Expression expression = new ExpressionBuilder(funcInput).variable("x").build();

            System.out.println("Enter first guess (x₀): ");
            String x0Input = scanner.nextLine().trim().replaceAll("\\s+", "");
            double x0;
            try {
                x0 = Double.parseDouble(x0Input);
            } catch (NumberFormatException e) {
                System.out.println("Invalid number format. Please enter a valid number.");
                return;
            }

            if (Double.isNaN(x0) || Double.isInfinite(x0)) {
                System.out.println("Invalid initial guess. Please enter a valid number.");
                return;
            }

            System.out.println("Enter second guess (x₁): ");
            String x1Input = scanner.nextLine().trim().replaceAll("\\s+", "");
            double x1;
            try {
                x1 = Double.parseDouble(x1Input);
            } catch (NumberFormatException e) {
                System.out.println("Invalid number format. Please enter a valid number.");
                return;
            }

            if (Double.isNaN(x1) || Double.isInfinite(x1)) {
                System.out.println("Invalid second guess. Please enter a valid number.");
                return;
            }

            System.out.println("Enter tolerance (e.g., 0.001 or 1e-3):");
            String tolInput = scanner.nextLine().replaceAll("\\s+", "").trim();
            double tolerance;
            try {
                tolerance = Double.parseDouble(tolInput);
            } catch (NumberFormatException e) {
                System.out.println("Invalid tolerance input. Use formats like 0.001 or 1e-3.");
                return;
            }

            if (tolerance <= 0) {
                System.out.println("Tolerance must be greater than 0.");
                return;
            }
            if (tolerance < 1e-6) {
                System.out.println("Tolerance cannot be smaller than 1e-6.");
                return;
            }

            int decimalPlaces = getDecimalPlaces(tolerance);

            double result = secantIteration(expression, x0, x1, tolerance, maxIterations, history);

            String formatStr = "%." + decimalPlaces + "f";
            System.out.println("Result: " + String.format(formatStr, result));
            System.out.println("Total Iterations: " + history.size());

            printHistory(history, decimalPlaces);

        } catch (Exception e) {
            System.out.println("An error occurred: " + e.getMessage());
            System.out.println("Please check your input and try again.");
            System.out.println("Note: For exponential expressions, use e^(-x) or exp(-x)");
        }
    }

    // Secant method iteration
    public static double secantIteration(Expression expression, double x0, double x1, double tolerance, int maxIterations, LinkedList<IterationData> history) {
        double fx0 = evaluate(expression, x0);
        double fx1 = evaluate(expression, x1);

        for (int i = 0; i < maxIterations; i++) {
            if (Math.abs(fx1 - fx0) < 1e-12) {
                throw new ArithmeticException("Division by zero in the Secant formula. Try different initial guesses.");
            }

            if (Double.isNaN(fx0) || Double.isNaN(fx1) || Double.isInfinite(fx0) || Double.isInfinite(fx1)) {
                throw new ArithmeticException("Function evaluated to NaN or Infinity. Try different initial guesses.");
            }

            double x2 = x1 - fx1 * (x1 - x0) / (fx1 - fx0);
            double error = Math.abs(x2 - x1);

            history.add(new IterationData(x0, x1, x2, fx0, fx1, error));

            if (error < tolerance || Math.abs(fx1) < tolerance) {
                return x2;
            }

            x0 = x1;
            fx0 = fx1;
            x1 = x2;
            fx1 = evaluate(expression, x1);
        }
        System.out.println("Warning: Did not converge within the maximum number of iterations.");
        return x1;
    }

    private static double evaluate(Expression f, double x) {
        return f.setVariable("x", x).evaluate();
    }

    // Print iteration history with formatting
    public static void printHistory(LinkedList<IterationData> history, int decimalPlaces) {
        String formatStr = "%-10d %-" + (decimalPlaces + 8) + "." + decimalPlaces + "f %-" + 
                         (decimalPlaces + 8) + "." + decimalPlaces + "f %-" + 
                         (decimalPlaces + 8) + "." + decimalPlaces + "f %-" + 
                         (decimalPlaces + 10) + "." + decimalPlaces + "f %-" + 
                         (decimalPlaces + 10) + "." + decimalPlaces + "f %-" + 
                         (decimalPlaces + 10) + "." + decimalPlaces + "f%n";

        System.out.println("\nIteration Table:");
        System.out.printf("%-10s %-"+(decimalPlaces+8)+"s %-"+(decimalPlaces+8)+"s %-"+
                         (decimalPlaces+8)+"s %-"+(decimalPlaces+10)+"s %-"+
                         (decimalPlaces+10)+"s %-"+(decimalPlaces+10)+"s%n", 
                         "Iter", "x0", "x1", "x2", "f(x0)", "f(x1)", "Error");
        System.out.println("-".repeat(80));

        int i = 1;
        for (IterationData d : history) {
            System.out.printf(formatStr, i++, d.x0, d.x1, d.x2, d.fx0, d.fx1, d.error);
        }
    }

    // get decimal places from tolerance
    public static int getDecimalPlaces(double tolerance) {
        String tolStr = String.format("%e", tolerance); // scientific notation
        int idx = tolStr.indexOf('e');
        if (idx >= 0) {
            int exp = Integer.parseInt(tolStr.substring(idx + 1));
            return Math.abs(exp);
        }
        String[] parts = Double.toString(tolerance).split("\\.");
        return (parts.length > 1) ? parts[1].length() : 0;
    }
}

