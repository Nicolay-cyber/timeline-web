package uni.nikdiu.timelineweb.convectors;

import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/*
Algorithm
For each input character (token):

1. If the token is an operand (number), add it to the result.
2. If the token is an operator:
    2.1. If the stack is empty, push the token to the operator stack.
    2.2. If its precedence value is greater than the precedence value of the top operator in the stack,
    then push the current token to the stack.
    2.3. If its precedence value is lower or equal to the precedence value of the top operator in the stack,
    then move operators from the stack to the result until the precedence value of the current token is higher.
    After that, push the current token to the stack.
3. If the token is "(", then push it to the stack.
4. If the token is ")", then move the top operator from the stack to the result until the top is "(". After that, remove the "(" from the stack.
5. Repeat steps 1-4 until the input expression is completely read.
6. Move the remaining operators from the stack to the result simultaneously with removing "(" from the stack.
 */

/*
Operator Precedence:
1. Parentheses: (expression)
2. Functions: sin(x), cos(x), tan(x), (a)log(x), root(x), int(x), diff(x), (x)^(n)
3. Negation (unary minus): -x
4. Absolute value: |x|
5. Multiplication and Division: x * y, x / y (evaluated left-to-right)
6. Addition and Subtraction: x + y, x - y (evaluated left-to-right)
 */
public class MathNotationConvector {
    private final Map<String, Integer> operatorList = new HashMap<>();

    public List<String> getOperators() {
        return new ArrayList<>(operatorList.keySet());
    }

    public MathNotationConvector() {
        fillUpOperatorList();
    }

    public List<String> toPostfix(List<String> infixExpression) {
        List<String> result = new ArrayList<>();

        Stack<String> operatorStack = new Stack<>();

        for (String token : infixExpression) {
            if (isOperand(token)) { // 1. If the input character is an operand
                result.add(token);  // put it to the result
            } else if (isOperator(token)) {// 2. If the input character is an operator
                if (operatorStack.isEmpty()) { // 2.1. If stack is empty push it to the stack
                    operatorStack.push(token);
                } else if (hasHigherPrecedence(token, operatorStack.peek())) { // 2.2. If its precedence value is greater than the precedence value of the character on top, push.
                    operatorStack.push(token);
                } else {
                    while (!operatorStack.isEmpty() && !hasHigherPrecedence(token, operatorStack.peek())) {
                        result.add(operatorStack.pop());
                    }
                    operatorStack.push(token);
                }
            } else if (token.equals("(")) {
                operatorStack.push(token);
            } else if (token.equals(")")) {
                while (!operatorStack.isEmpty() && !operatorStack.peek().equals("(")) {
                    result.add(operatorStack.pop());
                }
                operatorStack.pop(); // pop symbol "(" from stack
            }
        }
        while (!operatorStack.isEmpty()) { // popping all operators exclude symbols "(" from stack and adding them to result
            if (operatorStack.peek().equals("(")) {
                operatorStack.pop();
            }
            result.add(operatorStack.pop());
        }
        return result;
    }

    private boolean isOperator(String token) {
        return operatorList.containsKey(token);
    }

    private boolean isOperand(String token) {
        return !isOperator(token) && !token.equals("(") && !token.equals(")");
    }

    private int getOperatorPrecedence(String token) {
        return operatorList.get(token);
    }

    private boolean hasHigherPrecedence(String op1, String op2) {
        if (op2.equals("(")) {
            return true;
        }
        return getOperatorPrecedence(op1) < getOperatorPrecedence(op2);
    }

    private void fillUpOperatorList() {
        List<String> secondaryOperators = List.of(
                "sin", "cos", "tan", "cot", "artan",
                "log", "^", "rt", // root
                "int", // integral
                "diff" // differentiation
        );

        List<String> tertiaryOperators = List.of("~"); // unary minus

        List<String> quaternaryOperators = List.of("abs"); // modulo

        List<String> quinaryOperators = List.of("*", "/");
        List<String> senaryOperators = List.of("+", "-");

        operatorList.putAll(secondaryOperators.stream().collect(Collectors.toMap(s -> s, s -> 2)));
        operatorList.putAll(tertiaryOperators.stream().collect(Collectors.toMap(s -> s, s -> 3)));
        operatorList.putAll(quaternaryOperators.stream().collect(Collectors.toMap(s -> s, s -> 4)));
        operatorList.putAll(quinaryOperators.stream().collect(Collectors.toMap(s -> s, s -> 5)));
        operatorList.putAll(senaryOperators.stream().collect(Collectors.toMap(s -> s, s -> 6)));
    }
}
