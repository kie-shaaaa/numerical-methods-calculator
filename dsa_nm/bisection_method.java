import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import java.util.Scanner;
import java.util.Stack;

// javac -cp ".;lib/exp4j-0.4.8.jar" bisection_method.java
// java -cp ".;lib/exp4j-0.4.8.jar" bisection_method
public class bisection_method {
    public static class IterationData {
        double a, b, c, fa, fb, fc;
        IterationData(double a, double b, double c, double fa, double fb, double fc) {
            this.a = a;
            this.b = b;
            this.c = c;
            this.fa = fa;
            this.fb = fb;
            this.fc = fc;
        }
    }

    public static double bisectionMethod(Expression f, double a, double b, double tolerance, int maxIterations, Stack<IterationData> history) {
        double fa = evaluate(f, a);
        double fb = evaluate(f, b);

        if (fa * fb >= 0) {
            throw new IllegalArgumentException("Function must have opposite signs at endpoints.");
        }

        for (int i = 0; i < maxIterations; i++) {
            double c = (a + b) / 2;
            double fc = evaluate(f, c);

            history.push(new IterationData(a, b, c, fa, fb, fc));

            if (Math.abs(fc) < tolerance || (b - a) / 2 < tolerance) {
                return c;
            }

            if (fc * fa < 0) {
                b = c;
                fb = fc;
            } else {
                a = c;
                fa = fc;
            }
        }

        System.out.println("Method did not converge within maximum iterations.");
        return (a + b) / 2;
    }

    private static double evaluate(Expression f, double x) {
        return f.setVariable("x", x).evaluate();
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Stack<IterationData> history = new Stack<>();
        int maxIterations = 100;

        try {
            System.out.println("Enter function f(x) (e.g., x^2 - 4, x^3 - 2*x - 5, sin(x) - x/2):");
            String funcStr = scanner.nextLine().toLowerCase().trim().replaceAll("\\s+", ""); // Convert input to lowercase
            
            Expression expression = new ExpressionBuilder(funcStr).variable("x").build();

            System.out.println("Enter first guess (a):");
            double a = Double.parseDouble(scanner.nextLine().trim().replaceAll("\\s+", ""));

            System.out.println("Enter second guess (b):");
            double b = Double.parseDouble(scanner.nextLine().trim().replaceAll("\\s+", ""));

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

            double result = bisectionMethod(expression, a, b, tolerance, maxIterations, history);
            
            String formatStr = "%." + decimalPlaces + "f";
            System.out.println("Result: " + String.format(formatStr, result));
            System.out.println("Total Iterations: " + history.size());

            printHistory(history, decimalPlaces);

        } catch (Exception e) {
            System.out.println("An error occurred: " + e.getMessage());
            System.out.println("Please check your input and try again.");
        }
    }

    public static void printHistory(Stack<IterationData> history, int decimalPlaces) {
        String formatStr = "%-10d %-" + (decimalPlaces + 8) + "." + decimalPlaces + "f %-" + 
                         (decimalPlaces + 8) + "." + decimalPlaces + "f %-" + 
                         (decimalPlaces + 8) + "." + decimalPlaces + "f %-" +
                         (decimalPlaces + 8) + "." + decimalPlaces + "f%n";

        System.out.println("\nIteration History:");
        System.out.printf("%-10s %-" + (decimalPlaces + 8) + "s %-" + 
                         (decimalPlaces + 8) + "s %-" + (decimalPlaces + 8) + "s %-" + 
                         (decimalPlaces + 8) + "s%n", 
                         "Iter", "a", "b", "c", "f(c)");
        System.out.println("-".repeat(60));

        int iter = 1;
        for (IterationData d : history) {
            System.out.printf(formatStr, iter++, d.a, d.b, d.c, d.fc);
        }
    }

    public static int getDecimalPlaces(double tolerance) {
        String tolStr = String.format("%e", tolerance);
        int idx = tolStr.indexOf('e');
        if (idx >= 0) {
            int exp = Integer.parseInt(tolStr.substring(idx + 1));
            return Math.abs(exp);
        }
        String[] parts = Double.toString(tolerance).split("\\.");
        return (parts.length > 1) ? parts[1].length() : 0;
    }
}
