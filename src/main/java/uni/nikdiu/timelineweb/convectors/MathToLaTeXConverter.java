package uni.nikdiu.timelineweb.convectors;

import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Component
@NoArgsConstructor
public class MathToLaTeXConverter {

    public String toLaTeX(String expression) {
        expression = expression.replaceAll("\\s+", ""); // Удаление пробелов
        System.out.println("before: "+expression);
        expression = replaceOperators(expression);
        System.out.println("after: "+expression);
        expression = replaceFunctions(expression);
        expression = replacePowers(expression);
        return "\\[" + expression  + "\\]";
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


}
