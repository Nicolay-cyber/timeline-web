package uni.nikdiu.timelineweb.models;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import uni.nikdiu.timelineweb.convectors.MathNotationConvector;
import uni.nikdiu.timelineweb.entities.Function;
import uni.nikdiu.timelineweb.entities.Parameter;

import java.util.List;
import java.util.Stack;

@Component
@AllArgsConstructor
public class Calculator {
    public Double calculate(
            List<String> infixExpression,
            List<Function> relatedFunctions,
            List<Parameter> relatedParameters,
            Double step
    ) {
        MathNotationConvector converter = new MathNotationConvector();
        List<String> PostfixExpression = converter.toPostfix(infixExpression);
        List<String> operators = converter.getOperators();
        Stack<Double> stack = new Stack<>();

        for (String token : PostfixExpression) {
            if (!token.matches("-?\\d+(\\.\\d+)?") && !operators.contains(token)) { //It's not a number and not an operator
                if (token.matches("t")) {
                    token = String.valueOf(step);
                } else { //It's parameter variable
                    token = replaceParameter(token, relatedParameters, relatedFunctions, step);
                }
            }
            if (token == null) {
                return null;
            }
            switch (token) {
                case "+":
                    stack.push(stack.pop() + stack.pop());
                    break;
                case "-":
                    double subtrahend = stack.pop();
                    stack.push(stack.pop() - subtrahend);
                    break;
                case "*":
                    stack.push(stack.pop() * stack.pop());
                    break;
                case "/":
                    double divisor = stack.pop();
                    stack.push(stack.pop() / divisor);
                    break;
                case "~":
                    stack.push(stack.pop() * -1);
                    break;
                case "^":
                    Double degree = stack.pop();
                    stack.push(Math.pow(stack.pop(), degree));
                    break;
                case "rt":
                    stack.push(Math.pow(stack.pop(), 1.0 / stack.pop()));
                    break;
                case "sin":
                    stack.push(Math.sin(stack.pop()));
                    break;
                case "cos":
                    stack.push(Math.cos(stack.pop()));
                    break;
                case "tan":
                    stack.push(Math.tan(stack.pop()));
                    break;
                case "cot":
                    stack.push(1 / Math.tan(stack.pop()));
                    break;
                case "log":
                    stack.push(Math.log(stack.pop()) / Math.log(stack.pop()));
                    break;
                case "abs":
                    stack.push(Math.abs(stack.pop()));
                    break;
                case "artan":
                    stack.push(Math.atan(stack.pop()));
                    break;
                default:
                    stack.push(Double.valueOf(token));
            }
        }
        return stack.pop();
    }

    private String replaceParameter(String parameterAbbr, List<Parameter> relatedParameters, List<Function> relatedFunctions, Double step) {
        Parameter relatedParameter = relatedParameters.stream().filter(p -> p.getAbbreviation().equals(parameterAbbr)).findFirst().orElseThrow();

        Function relatedFunction = relatedFunctions.stream()
                .filter(f -> f.getParentParameter().getId().equals(
                        relatedParameter.getId())
                        && f.getStartPoint() <= step
                        && f.getEndPoint() >= step)
                .findFirst().orElse(null);
        if (relatedFunction != null) {
            Double innerExpressionResult = calculate(relatedFunction.getExpression(), relatedFunctions, relatedParameters, step);
            if (innerExpressionResult == null) return null;
            return String.valueOf(innerExpressionResult);
        } else return null;
    }
}
