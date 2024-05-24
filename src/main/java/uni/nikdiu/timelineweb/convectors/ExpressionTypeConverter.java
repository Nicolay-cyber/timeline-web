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
        return "\\[" + classicExpression  + "\\]";
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
        System.out.println("latexExpression: "+latexExpression);
        latexExpression = latexExpression.replaceAll("\\\\\\[","");
        System.out.println("latexExpression: "+latexExpression);
        latexExpression = latexExpression.replaceAll("\\\\]", "");
        System.out.println("latexExpression: "+latexExpression);


        // Reverse replace powers
        latexExpression = reverseReplacePowers(latexExpression);

        // Reverse replace functions
        latexExpression = reverseReplaceFunctions(latexExpression);

        // Reverse replace operators
        latexExpression = reverseReplaceOperators(latexExpression);

        return latexExpression;
    }

    private String reverseReplacePowers(String expression) {
        // Reverse the pattern
        Pattern pattern = Pattern.compile("\\{([^{}]+)\\}\\^\\{([^{}]+)\\}");
        Matcher matcher = pattern.matcher(expression);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, matcher.group(1) + "^" + matcher.group(2));
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    private String reverseReplaceFunctions(String expression) {
        // Reverse the replacement of functions
        expression = expression.replaceAll("\\\\sin\\\\left\\(([^{}]+)\\\\right\\)", "sin($1)");
        expression = expression.replaceAll("\\\\cos\\\\left\\(([^{}]+)\\\\right\\)", "cos($1)");
        expression = expression.replaceAll("\\\\tan\\\\left\\(([^{}]+)\\\\right\\)", "tan($1)");
        expression = expression.replaceAll("\\\\log\\\\left\\(([^{}]+)\\\\right\\)", "log($1)");
        expression = expression.replaceAll("\\\\sqrt\\\\left\\(([^{}]+)\\\\right\\)", "root($1)");
        return expression;
    }

    private String reverseReplaceOperators(String expression) {
        // Reverse the replacement of operators
        expression = expression.replaceAll("\\\\cdot", " * ");
        expression = expression.replaceAll("/", " / ");
        expression = expression.replaceAll("\\+", " + ");
        expression = expression.replaceAll("\\\\left\\|", " |");
        expression = expression.replaceAll("\\\\right\\|", "| ");
        return expression;
    }


}
