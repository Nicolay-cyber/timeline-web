package uni.nikdiu.timelineweb.convectors;

import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Component
@NoArgsConstructor
public class ExpressionTypeConverter {


    public String classicToLatex(String classicExpression) {
        classicExpression = classicExpression.replaceAll("\\s+", ""); // Удаление пробелов

        classicExpression = replaceOperators(classicExpression);

        classicExpression = replaceFunctions(classicExpression);
        classicExpression = replacePowers(classicExpression);
        return "\\[" + classicExpression + "\\]";
    }


    private String replaceOperators(String expression) {
        expression = expression.replaceAll("\\*", " \\\\cdot ");
        expression = expression.replaceAll("/", " / ");
        expression = expression.replaceAll("\\+", " + ");
        expression = expression.replaceAll("\\|", " \\\\left| ");
        expression = expression.replaceAll("\\|", " \\\\right| ");
        return expression;
    }

    private String replaceFunctions(String expression) {
        expression = expression.replaceAll("sin\\(([^)]+)\\)", "\\\\sin\\\\left($1\\\\right)");
        expression = expression.replaceAll("cos\\(([^)]+)\\)", "\\\\cos\\\\left($1\\\\right)");
        expression = expression.replaceAll("tan\\(([^)]+)\\)", "\\\\tan\\\\left($1\\\\right)");
        expression = expression.replaceAll("log\\(([^)]+)\\)", "\\\\log\\\\left($1\\\\right)");
        expression = expression.replaceAll("root\\(([^)]+)\\)", "\\\\sqrt\\\\left($1\\\\right)");
        return expression;
    }


    private String replacePowers(String expression) {
        Pattern pattern = Pattern.compile("([a-zA-Z0-9.]+)\\^([a-zA-Z0-9.]+)");
        Matcher matcher = pattern.matcher(expression);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, "{" + matcher.group(1) + "}^{" + matcher.group(2) + "}");
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    //From LaTeX to classic syntax

    public String latexToClassic(String latexExpression) {
        // Remove LaTeX delimiters
        System.out.println();
        System.out.println("row latexExpression: " + latexExpression);
        latexExpression = latexExpression.replaceAll("\\\\\\[", "");
        latexExpression = latexExpression.replaceAll("\\\\]", "");
        System.out.println("latexExpression: " + latexExpression);

        latexExpression = latexExpression.replaceAll("\\\\left\\(", "(");
        latexExpression = latexExpression.replaceAll("\\\\right\\)", ")");

        latexExpression = latexExpression.replaceAll("\\{", "(");
        latexExpression = latexExpression.replaceAll("\\}", ")");

        System.out.println("latexExpression (replaced brackets): " + latexExpression);
        // Reverse replace divisions
        while (latexExpression.contains("frac")) {
            latexExpression = reverseReplaceDivisions(latexExpression);
        }
        System.out.println("latexExpression (reverseReplaceDivisions): " + latexExpression);


        // Reverse replace functions
        latexExpression = reverseReplaceFunctions(latexExpression);
        System.out.println("latexExpression (reverseReplaceFunctions): " + latexExpression);

        // Reverse replace operators
        latexExpression = reverseReplaceOperators(latexExpression);
        System.out.println("latexExpression (reverseReplaceOperators): " + latexExpression);


        latexExpression = latexExpression.replaceAll("\\(", " ( ");
        latexExpression = latexExpression.replaceAll("\\)", " ) ");

        while (latexExpression.contains("  ")) {
            latexExpression = latexExpression.replaceAll(" {2}", " ");
        }
        latexExpression = latexExpression.trim();
        System.out.println("latexExpression (final version): " + latexExpression);
        return latexExpression;
    }

    private String reverseReplaceDivisions(String input) {
        int startIndex = input.indexOf("\\frac");
        if (startIndex == -1) // Если не найдено \frac, вернуть исходную строку
            return input;

        // Найти начало делимого (до первой открывающей скобки после \frac)
        int startNumerator = startIndex + 5; // Индекс после "\\frac"
        int bracketCount = 0;
        int i;
        for (i = startNumerator; i < input.length(); i++) {
            if (input.charAt(i) == '(') {
                bracketCount++;
            } else if (input.charAt(i) == ')') {
                bracketCount--;
            }
            if (bracketCount == 0) {
                break;
            }
        }
        String numerator = input.substring(startNumerator + 1, i); // Найденное делимое
        int startDenominator = i + 1; // Индекс после закрывающей скобки делимого
        bracketCount = 0;
        for (i = startDenominator; i < input.length(); i++) {
            if (input.charAt(i) == '(') {
                bracketCount++;
            } else if (input.charAt(i) == ')') {
                bracketCount--;
            }
            if (bracketCount == 0) {
                break;
            }
        }
        String denominator = input.substring(startDenominator + 1, i); // Найденный делитель

        // Сформировать строку с отформатированным делимым и делителем
        String formattedFraction = " (" + numerator.trim() + ") / (" + denominator.trim() + ") ";

        // Заменить подстроку с \frac и аргументами на отформатированную строку
        return input.substring(0, startIndex) + formattedFraction + input.substring(i + 1);
    }


    private static int findClosingBracket(String input, int start) {
        int bracketCount = 0;
        for (int i = start; i < input.length(); i++) {
            if (input.charAt(i) == '(') {
                bracketCount++;
            } else if (input.charAt(i) == ')') {
                bracketCount--;
                if (bracketCount == 0) {
                    return i;
                }
            }
        }
        return -1;
    }


    private String reverseReplaceFunctions(String expression) {
        // Reverse the replacement of functions
        expression = expression.replaceAll("\\\\sin", "sin");
        expression = expression.replaceAll("\\\\cos", "cos");
        expression = expression.replaceAll("\\\\tan", "tan");
        expression = expression.replaceAll("\\\\log", "log");
        expression = expression.replaceAll("\\\\sqrt", "root");
        return expression;
    }

    private String reverseReplaceOperators(String expression) {
        // Reverse the replacement of operators
        expression = expression.replaceAll("\\\\cdot", " * ");
        expression = expression.replaceAll("\\+", " + ");
        expression = expression.replaceAll("\\\\left\\|", " |");
        expression = expression.replaceAll("\\\\right\\|", "| ");
        return expression;
    }


}
