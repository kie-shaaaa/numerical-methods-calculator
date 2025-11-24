import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;
// For Windows:
// javac -cp ".;lib\exp4j-0.4.8.jar" newton_raphson.java
// java -cp ".;lib\exp4j-0.4.8.jar" newton_raphson
public class newton_raphson {
    public static class IterationData {
        int iteration;
        double x;
        double fx;
        double dfx;
        double xNew;

        IterationData(int iteration, double x, double fx, double dfx, double xNew) {
            this.iteration = iteration;
            this.x = x;
            this.fx = fx;
            this.dfx = dfx;
            this.xNew = xNew;
        }
    }

    public static Queue<IterationData> newtonRaphson(String funcStr, double x0, double tolerance, int maxIterations) throws Exception {
        final double DEFAULT_H = 1e-5;
        
        // Pre-process the input to handle exponential expressions
        funcStr = funcStr.trim()
                        .toLowerCase()  // Convert everything to lowercase first
                        .replaceAll("\\s+", "")  // Remove all whitespace
                        .replaceAll("e\\^([\\w\\-\\+\\(\\)]+)", "exp($1)");  // Convert e^expression to exp(expression)
        
        Expression function = new ExpressionBuilder(funcStr).variable("x").build();

        Queue<IterationData> iterations = new LinkedList<>();
        double x = x0;
        double h = DEFAULT_H;

        for (int i = 1; i <= maxIterations; i++) {
            double fx = evaluate(function, x);
            double dfx = derivative(function, x, h);

            if (Double.isNaN(fx) || Double.isNaN(dfx) || Double.isInfinite(fx) || Double.isInfinite(dfx)) {
                throw new ArithmeticException("Function or derivative evaluated to NaN or Infinity. Try a different initial guess.");
            }

            if (Math.abs(dfx) < 1e-10) {
                throw new ArithmeticException("Derivative too small, division by zero risk.");
            }

            double xNew = x - fx / dfx;

            iterations.add(new IterationData(i, x, fx, dfx, xNew));

            if (Math.abs(xNew - x) < tolerance || Math.abs(fx) < tolerance) {
                break;
            }

            x = xNew;
            h = DEFAULT_H * (1 + Math.abs(x));
        }

        return iterations;
    }

    private static double evaluate(Expression f, double x) {
        return f.setVariable("x", x).evaluate();
    }

    private static double derivative(Expression f, double x, double h) {
        return (evaluate(f, x + h) - evaluate(f, x - h)) / (2 * h);
    }

    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);

        System.out.println("Enter the function f(x) (e.g., x^3 - x - 2, x^2 - 4*x + 4, sin(x) - x^2, e^(-x)):");
        String function = input.nextLine().trim();

        double x0;
        while (true) {
            System.out.println("Enter the initial guess:");
            String x0Str = input.nextLine().trim().replaceAll("\\s+", "");
            try {
                x0 = Double.parseDouble(x0Str);
                if (Double.isNaN(x0) || Double.isInfinite(x0)) {
                    System.out.println("Invalid initial guess. Please enter a valid number:");
                    continue;
                }
                break;
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number for initial guess:");
            }
        }

        double tolerance;
        int decimalPlaces;
        while (true) {
            System.out.println("Enter the tolerance (e.g., 0.001 or 1e-3):");
            String tolInput = input.nextLine().replaceAll("\\s+", "").trim();
            try {
                tolerance = Double.parseDouble(tolInput);
                if (tolerance <= 0) {
                    System.out.println("Tolerance must be greater than 0. Please enter again:");
                    continue;
                }
                if (tolerance < 1e-6) {
                    System.out.println("Tolerance cannot be smaller than 1e-6. Please enter again:");
                    continue;
                }
                decimalPlaces = getDecimalPlaces(tolerance);
                break;
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number for tolerance (e.g., 0.001 or 1e-3):");
            }
        }

        try {
            int maxIterations = 100;
            Queue<IterationData> iterations = newtonRaphson(function, x0, tolerance, maxIterations);

            printIterationTable(iterations, decimalPlaces);
            printSolution(iterations, decimalPlaces);

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            System.out.println("Please check your input and try again.");
            System.out.println("Note: For exponential expressions, use e^(-x) or exp(-x)");
        }
    }

    public static void printIterationTable(Queue<IterationData> iterations, int decimals) {
        System.out.println("\nIteration Table:");
        System.out.printf("%-4s %-" + (decimals + 12) + "s %-" + (decimals + 12) + "s %-" + (decimals + 12) + "s %-" + (decimals + 12) + "s%n",
                         "Iter", "x", "f(x)", "f'(x)", "x_new");
        System.out.println("-".repeat(4 + 4*(decimals + 12)));

        String formatString = "%-4d %" + (decimals + 12) + "." + decimals + "f %" + (decimals + 12) + "." + decimals + "f %" + 
                            (decimals + 12) + "." + decimals + "f %" + (decimals + 12) + "." + decimals + "f%n";

        for (IterationData data : iterations) {
            System.out.printf(formatString, data.iteration, data.x, data.fx, data.dfx, data.xNew);
        }
    }

    public static void printSolution(Queue<IterationData> iterations, int decimals) {
        System.out.println("\nSolving Steps:");

        String formatStep = "x%d = x - f(x)/f'(x) = %." + decimals + "f - (%." + decimals + "f) / (%." + decimals + "f) = %." + decimals + "f%n";

        IterationData last = null;
        for (IterationData data : iterations) {
            System.out.printf(formatStep, data.iteration, data.x, data.fx, data.dfx, data.xNew);
            last = data;
        }

        if (last != null) {
            System.out.printf("%nFinal solution: Root = %." + decimals + "f%n", last.xNew);
            System.out.println("Total Iterations: " + last.iteration);
        }
    }

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
