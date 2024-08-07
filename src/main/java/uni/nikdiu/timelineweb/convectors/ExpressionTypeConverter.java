package uni.nikdiu.timelineweb.convectors;

import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;


@Component
@NoArgsConstructor
public class ExpressionTypeConverter {


    public String classicToLatex(String classicExpression) {
        //System.out.println("classicToLatex");
        classicExpression = classicExpression.replaceAll("\\s+", ""); // Удаление пробелов
        //System.out.println("Удаление пробелов " + classicExpression);

        classicExpression = replaceDivision(classicExpression);
        //System.out.println("replaceDivision " + classicExpression);

        classicExpression = classicExpression.replaceAll("\\\\rb", "\\\\right\\)");
        classicExpression = classicExpression.replaceAll("\\\\lb", "\\\\left\\(");
        // System.out.println("brackets: " + classicExpression);
        classicExpression = replaceOperators(classicExpression);
//        System.out.println("replaceOperators " + classicExpression);
        classicExpression = replaceFunctions(classicExpression);
        //      System.out.println("replaceFunctions " + classicExpression);

        classicExpression = classicExpression.replaceAll("right}", "right\\)");
        classicExpression = classicExpression.replaceAll("left\\{", "left\\(");
        //    System.out.println("fix brackets " + classicExpression);
        classicExpression = classicExpression.replaceAll("~", "-");

        classicExpression = classicExpression.replaceAll("\\s+", ""); // Удаление пробелов
        //System.out.println("Удаление пробелов " + classicExpression);

        classicExpression = classicExpression.replaceAll("cdot", "cdot ");

        //   System.out.println("fix cdot " + classicExpression);

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
        int endNumerator = divisionIndex - 2;

        // Найти позицию начала знаменателя
        int startDenominator = divisionIndex + 2;
        // Найти позицию конца знаменателя
        int endDenominator = findEndDenominator(classicExpression, divisionIndex) - 1;

        // Получить числитель и знаменатель
        String numerator = classicExpression.substring(startNumerator, endNumerator + 1);
        String denominator = classicExpression.substring(startDenominator, endDenominator + 1);
        //System.out.println("числитель " + numerator);
        //  System.out.println("знаменатель " + denominator);
        // Сформировать строку с отформатированным знаком деления
        String formattedDivision = " \\frac{" + numerator.trim() + "}{" + denominator.trim() + "} ";

        // Заменить подстроку с делением на отформатированную строку
        return classicExpression.substring(0, startNumerator) + formattedDivision + classicExpression.substring(endDenominator + 1);


    }

    // Метод для поиска позиции начала числителя
    private int findStartNumerator(String classicExpression, int divisionIndex) {
        int bracketCount = 0;
        for (int i = divisionIndex - 1; i >= 0; i--) {
            //        System.out.println(classicExpression.charAt(i));
            //        System.out.println("bracketCount " + bracketCount);
            char currentChar = classicExpression.charAt(i);
            if (currentChar == ')') {
                bracketCount++;
            } else if (currentChar == '(') {
                bracketCount--;
            }
            if (bracketCount == 0) {
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

        // Remove LaTeX delimiters
        latexExpression = latexExpression.replaceAll("\\\\\\[", "");
        latexExpression = latexExpression.replaceAll("\\\\]", "");

        latexExpression = latexExpression.replaceAll("-\\\\left\\(", " ~ \\\\left\\(");

        latexExpression = latexExpression.replaceAll("\\\\left\\(", " \\\\lb ");
        latexExpression = latexExpression.replaceAll("\\\\right\\)", " \\\\rb ");

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

        latexExpression = latexExpression.replaceAll("\\\\ lb", " \\\\lb ");
        latexExpression = latexExpression.replaceAll("\\\\ rb", " \\\\rb ");
        latexExpression = latexExpression.replaceAll("sin", " sin ");
        latexExpression = latexExpression.replaceAll("cos", " cos ");
        latexExpression = latexExpression.replaceAll("tan", " tan ");
        latexExpression = latexExpression.replaceAll("log", " log ");
        latexExpression = latexExpression.replaceAll("root", " root ");

        while (latexExpression.contains("  ")) {
            latexExpression = latexExpression.replaceAll(" {2}", " ");
        }
        latexExpression = latexExpression.trim();

        return latexExpression;
    }

    public String handlePowerSighs(String latexExpression) {
        StringBuilder result = new StringBuilder();
        int length = latexExpression.length();
        for (int i = 0; i < length; i++) {
            char currentChar = latexExpression.charAt(i);

            if (currentChar == '^') {

                result.append(currentChar);

                // Если следующий символ не '{'
                if (i + 1 < length && latexExpression.charAt(i + 1) != '{') {
                    result.append('(');
                    // Пройти дальше до пробела или оператора
                    i++;
                    while (i < length && !isOperator(latexExpression.charAt(i))) {
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
        expression = expression.replaceAll("\\\\sin", " sin ");
        expression = expression.replaceAll("\\\\cos", " cos ");
        expression = expression.replaceAll("\\\\tan", " tan ");
        expression = expression.replaceAll("\\\\log", " log ");
        expression = expression.replaceAll("\\\\sqrt", " root ");
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
