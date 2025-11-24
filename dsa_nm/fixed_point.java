import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import java.util.Scanner;
import java.util.ArrayList;
// For Windows:
// javac -cp ".;lib\exp4j-0.4.8.jar" fixed_point.java
// java -cp ".;lib\exp4j-0.4.8.jar" fixed_point
public class fixed_point {
    public static class IterationData {
        double x, gx, error;
        IterationData(double x, double gx, double error) {
            this.x = x;
            this.gx = gx;
            this.error = error;
        }
    }
    public static void main(String[] args) throws Exception {
    	Scanner scanner = new Scanner(System.in);
        ArrayList<IterationData> history = new ArrayList<>();
        int maxIterations = 100;

        try {
            System.out.println("Enter g(x) function (e.g., sqrt(x + 1), (x + 5)^(1/3), e^(-x), exp(-x)): ");
            String user_input = scanner.nextLine()
                                    .toLowerCase()  // Convert everything to lowercase first
                                    .replaceAll("\\s+", "")  // Remove all whitespace
                                    .replaceAll("e\\^([\\w\\-\\+\\(\\)]+)", "exp($1)");  // Convert e^expression to exp(expression)
            
            Expression expression = new ExpressionBuilder(user_input).variable("x").build();

            System.out.println("Enter initial guess: ");
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

            // Check if the derivative is less than 1
            if (Math.abs(derive(expression, x0)) > 1) {
                System.out.println("The values of g`(x) is greater than 1. The method may not converge.");
                return;
            }

            // Perform fixed-point iteration
            double result = fixedPointRecursive(expression, x0, tolerance, maxIterations, 1, history);
            IterationData last = history.get(history.size() - 1);

            String formatStr = "%." + decimalPlaces + "f";
            System.out.println("Result: " + String.format(formatStr, result));
            System.out.println("Total Iterations: " + history.size());

            // Print iteration history
            printHistory(history, decimalPlaces);
        } catch (Exception e) {
            System.out.println("An error occurred: " + e.getMessage());
            System.out.println("Please check your input and try again.");
            System.out.println("Note: For exponential expressions, use e^(-x) or exp(-x)");
        }
    }

    // Derivative of g(x) (numerical approximation)
    public static double derive(Expression expression, double x) {
        double h = 1e-5;
        double fxh = expression.setVariable("x", x + h).evaluate();
        double fx = expression.setVariable("x", x).evaluate();
        return (fxh - fx) / h;
    }

    // Recursive fixed-point iteration
    public static double fixedPointRecursive(Expression expression, double x, double tolerance, int maxIterations, int iter, ArrayList<IterationData> history) {
        // Check for invalid value
        if (Double.isInfinite(x) || Double.isNaN(x)) {
            System.out.println("Invalid value detected. Try a different initial guess.");
            return x;
        }

        double gx = expression.setVariable("x", x).evaluate();
        double error = Math.abs(gx - x);

        // Store iteration data
        history.add(new IterationData(x, gx, error));

        if (error <= tolerance) {
            return gx;
        }
        if (iter >= maxIterations) {
            System.out.println("Did not converge within the maximum number of iterations.");
            return gx;
        }

        return fixedPointRecursive(expression, gx, tolerance, maxIterations, iter + 1, history);
    }

    // Print iteration history with formatting
    public static void printHistory(ArrayList<IterationData> history, int decimalPlaces) {
        String formatStr = "%-10d %-" + (decimalPlaces + 6) + "." + decimalPlaces + "f %-" + (decimalPlaces + 8) + "." + decimalPlaces + "f %-" + (decimalPlaces + 8) + "." + decimalPlaces + "f%n";
        System.out.println("\nIteration History:");
        System.out.printf("%-10s %-"+(decimalPlaces+6)+"s %-"+(decimalPlaces+8)+"s %-"+(decimalPlaces+8)+"s%n", "Iter", "x", "g(x)", "Error");
        System.out.println("------------------------------------------------");

        for (int i = 0; i < history.size(); i++) {
            IterationData d = history.get(i);
            System.out.printf(formatStr, i + 1, d.x, d.gx, d.error);
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

