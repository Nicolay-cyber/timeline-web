package uni.nikdiu.timelineweb.convectors;

import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;


@Component
@NoArgsConstructor
public class ExpressionTypeConverter {


    public String classicToLatex(String classicExpression) {
        System.out.println("Converting classic syntax to latex expression ");
        System.out.println("Classic expression: " + classicExpression);

        classicExpression = classicExpression.replaceAll("\\s+", ""); // Удаление пробелов

        classicExpression = replaceDivision(classicExpression);

        classicExpression = replaceOperators(classicExpression);

        classicExpression = replaceFunctions(classicExpression);

        classicExpression = classicExpression.replaceAll("\\s+", ""); // Удаление пробелов
        classicExpression = classicExpression.replaceAll("cdot", "cdot ");

        System.out.println("Latex expression: " + classicExpression);
        return "\\[" + classicExpression + "\\]";
    }

    private String replaceDivision(String classicExpression) {
// Найти индекс знака деления
        int divisionIndex = classicExpression.indexOf("/");
        if (divisionIndex == -1) {
            // Если знак деления не найден, вернуть исходное выражение
            return classicExpression;
        }

        // Найти позицию начала числителя
        int startNumerator = findStartNumerator(classicExpression, divisionIndex);
        // Найти позицию конца числителя
        int endNumerator = divisionIndex - 1;

        // Найти позицию начала знаменателя
        int startDenominator = divisionIndex + 1;
        // Найти позицию конца знаменателя
        int endDenominator = findEndDenominator(classicExpression, divisionIndex);

        // Получить числитель и знаменатель
        String numerator = classicExpression.substring(startNumerator, endNumerator + 1);
        String denominator = classicExpression.substring(startDenominator, endDenominator + 1);

        // Сформировать строку с отформатированным знаком деления
        String formattedDivision = " \\frac{" + numerator.trim() + "}{" + denominator.trim() + "} ";

        // Заменить подстроку с делением на отформатированную строку
        return classicExpression.substring(0, startNumerator) + formattedDivision + classicExpression.substring(endDenominator + 1);


    }

    // Метод для поиска позиции начала числителя
    private int findStartNumerator(String classicExpression, int divisionIndex) {
        int bracketCount = 0;
        for (int i = divisionIndex - 1; i >= 0; i--) {
            char currentChar = classicExpression.charAt(i);
            if (currentChar == ')') {
                bracketCount++;
            } else if (currentChar == '(') {
                bracketCount--;
            } else if (bracketCount == 0 && (currentChar == '+' || currentChar == '-' || currentChar == '*' || currentChar == '/' || currentChar == '|')) {
                // Найдено начало числителя, если встречается оператор и не внутри скобок
                return i + 1;
            }
        }
        // Начало числителя - начало строки
        return 0;
    }

    // Метод для поиска позиции конца знаменателя
    private int findEndDenominator(String classicExpression, int divisionIndex) {
        int bracketCount = 0;
        for (int i = divisionIndex + 1; i < classicExpression.length(); i++) {
            char currentChar = classicExpression.charAt(i);
            if (currentChar == '(') {
                bracketCount++;
            } else if (currentChar == ')') {
                bracketCount--;
                if (bracketCount == 0) {
                    // Найден конец знаменателя, если закрывающая скобка и не внутри скобок
                    return i;
                }
            }
        }
        // Конец знаменателя - конец строки
        return classicExpression.length() - 1;
    }

    private String replaceOperators(String expression) {
        expression = expression.replaceAll("\\*", "\\\\cdot");

        expression = expression.replaceAll("\\|", "\\\\left|");
        expression = expression.replaceAll("\\|", "\\\\right|");

        expression = expression.replaceAll("\\(", "{");
        expression = expression.replaceAll("\\)", "}");

        expression = expression.replaceAll("\\}  \\}", " \\\\right\\)");
        expression = expression.replaceAll("\\{  \\{", " \\\\left\\(");
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

    //From LaTeX to classic syntax

    public String latexToClassic(String latexExpression) {

        System.out.println("Converting latex expression to classic syntax");
        System.out.println("Latex expression: " + latexExpression);
        // Remove LaTeX delimiters
        latexExpression = latexExpression.replaceAll("\\\\\\[", "");
        latexExpression = latexExpression.replaceAll("\\\\]", "");

        latexExpression = latexExpression.replaceAll("\\\\left\\(", "((");
        latexExpression = latexExpression.replaceAll("\\\\right\\)", "))");

        latexExpression = handlePowerSighs(latexExpression);
        latexExpression = latexExpression.replaceAll("\\{", "(");
        latexExpression = latexExpression.replaceAll("\\}", ")");

        // Reverse replace divisions
        while (latexExpression.contains("frac")) {
            latexExpression = reverseReplaceDivisions(latexExpression);
        }


        // Reverse replace functions
        latexExpression = reverseReplaceFunctions(latexExpression);

        // Reverse replace operators
        latexExpression = reverseReplaceOperators(latexExpression);


        latexExpression = latexExpression.replaceAll("\\(", " ( ");
        latexExpression = latexExpression.replaceAll("\\)", " ) ");

        latexExpression = handleNegativeSigns(latexExpression);

        while (latexExpression.contains("  ")) {
            latexExpression = latexExpression.replaceAll(" {2}", " ");
        }
        latexExpression = latexExpression.trim();
        System.out.println("Classic expression: " + latexExpression);
        return latexExpression;
    }

    public String handlePowerSighs(String latexExpression) {
        StringBuilder result = new StringBuilder();
        int length = latexExpression.length();
        for (int i = 0; i < length; i++) {
            char currentChar = latexExpression.charAt(i);

            if (currentChar == '^') {
                System.out.println("power sigh is found");
                System.out.println("result: " + result);
                result.append(currentChar);
                System.out.println("result: " + result);

                // Если следующий символ не '{'
                if (i + 1 < length && latexExpression.charAt(i + 1) != '{') {
                    result.append('(');
                    System.out.println("result: " + result);
                    // Пройти дальше до пробела или оператора
                    i++;
                    while (i < length && !isOperator(latexExpression.charAt(i))) {
                        System.out.println("result: " + result);
                        result.append(latexExpression.charAt(i));
                        i++;
                    }

                    result.append(')');

                    // Шаг назад, чтобы не пропустить текущий символ
                    i--;
                }
            } else {
                result.append(currentChar);
            }
        }

        return result.toString();
    }

    private String handleNegativeSigns(String expression) {
        StringBuilder result = new StringBuilder();
        boolean previousIsOperator = true; // Указывает, что предыдущий символ был оператором

        for (int i = 0; i < expression.length(); i++) {
            char currentChar = expression.charAt(i);
            if (currentChar == '-') {
                // Проверка, является ли текущий минус знаком отрицательности или оператором
                if (previousIsOperator) {
                    result.append("-"); // Знак отрицательности
                } else {
                    result.append(" - "); // Оператор
                }
                previousIsOperator = false;
            } else if (isOperator(currentChar)) {
                result.append(" ").append(currentChar).append(" ");
                previousIsOperator = true;
            } else {
                result.append(currentChar);
                if (currentChar != ' ') {
                    previousIsOperator = false;
                }
            }
        }
        return result.toString();
    }

    private boolean isOperator(char c) {
        return c == '+' || c == '-' || c == '*' || c == '\\' || c == '/' || c == '^' || c == '(';
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
