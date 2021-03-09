package calculator;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.math.BigInteger;

public class Operation {

    Map<String, BigInteger> variables = new HashMap<>();
    
    String[] formatArray(String[] arr) {
        for (int i = 0; i < arr.length; i++) {
            arr[i] = arr[i].trim();
        }
        return arr;
    }

    int getPrecedence(String str) {
        switch (str) {
            case "+":
            case "-":
                return 1;
            case "*":
            case "/":
                return 2;
            case "^":
                return 3;
        }
        return -1;
    }

    void findInMap(String str) {
        if (variables.containsKey(str)) {
            System.out.println(variables.get(str));
        } else {
            System.out.println("Unknown variable");
        }
    }

    boolean checkValidity(String str) {
        Pattern command = Pattern.compile("^/[a-zA-Z]");
        Matcher matcherCommand = command.matcher(str);
        if (matcherCommand.find()) {
            System.out.println("Unknown command");
            return false;
        } else {
            return true;
        }
    }

    boolean checkForSign(String str) {
        Pattern sign = Pattern.compile("[-/*+]+");
        Matcher matcherWithSign = sign.matcher(str);
        return matcherWithSign.find();
    }

    void variableAssignment(String str) {
        String[] arr = str.split("=");
        arr = formatArray(arr);
        if (!variables.containsKey((arr[1]))) {
            System.out.println("Unknown variable");
        } else {
            variables.put(arr[0], variables.get(arr[1]));
        }
    }

    void variableToString(String str) {
        Pattern pattern = Pattern.compile(".*[a-zA-Z]+.*");
        Matcher matcher = pattern.matcher(str);
        if (matcher.find() && !str.contains("=")) {
            String regexAddition = "(?<=[-+*/])|(?=[-+*/])";
            String[] arr = str.split(regexAddition);
            String temp = "";
            for (int i=0; i < arr.length; i++) {
                arr[i] = arr[i].trim();
            }
            for (String s : arr) {
                if (!s.matches("[a-zA-Z]+")) {
                    temp += s;
                } else {
                    if (variables.containsKey(s)) {
                        temp += variables.get(s);
                    } else {
                        System.out.println("Unknown variable");
                        return;
                    }
                }
            }
            str = temp;
        }
        str = redundantSigns(str);
        if (!str.equals("")) {
            Operate(str);
        }
    }

    void singleNumber(String str) {
        String regexAddition = "(?<=[-+*/])|(?=[-+*/])";
        String[] arr = str.split(regexAddition);
        if (str.contains("=")) {
            arr = str.split("=");
            if (arr.length > 2) {
                System.out.println("Invalid expression");
            } else {
                operationOnVariables(str);
            }
        } else if (arr.length == 2) {
            arr[0] = arr[0].trim();
            arr[1] = arr[1].trim();
            if (arr[0].equals("+")) {
                System.out.println(arr[1]);
            } else if (arr[1].equals("+") || arr[1].equals("-")) {
                System.out.println("Invalid expression");
            } else {
                System.out.println(arr[0] + arr[1]);
            }
        } else {
            Pattern pattern = Pattern.compile("[a-zA-Z]+|[=]");
            Matcher matcher = pattern.matcher(arr[0]);
            if (matcher.find()) {
                operationOnVariables(arr[0]);
            } else {
                System.out.println((arr[0].equals("+") || arr[0].equals("-")) ? "Invalid expression" : arr[0]);
            }
        }
    }

    boolean operationOnVariables(String str) {
        String recallValue = "[a-zA-Z]+";
        Pattern pattern = Pattern.compile(recallValue);
        Matcher matcher = pattern.matcher(str);
        if (str.contains("=")) {
            String valid = "^[a-zA-Z]+[ \\t]*=[ \\t]*[-]*[0-9]+$";
            String validVariable = "[a-zA-Z]+[ \\t]*=[ \\t]*[a-zA-Z]+";
            Pattern validPattern = Pattern.compile(valid);
            Matcher validMatcher = validPattern.matcher(str);
            Pattern validVariablePattern = Pattern.compile(validVariable);
            Matcher validVariableMatcher = validVariablePattern.matcher(str);
            String invalidIdentifier = "^.*[0-9]+[a-zA-Z]*=";
            Pattern invalidIdentifierPattern = Pattern.compile(invalidIdentifier);
            Matcher invalidIdentifierMatcher = invalidIdentifierPattern.matcher(str);
            String invalidAssignment = "[=].*[=]|[=][ ]*[0-9]+[a-zA-Z]";
            pattern = Pattern.compile(invalidAssignment);
            matcher = pattern.matcher(str);
            if (validMatcher.find()) {
                String[] arr = str.split("=");
                arr[0] = arr[0].trim(); arr[1] = arr[1].trim();
                BigInteger bigInteger = new BigInteger(arr[1]);
                variables.put(arr[0], bigInteger);
                return true;
            } else if (validVariableMatcher.find()) {
                variableAssignment(str);
            } else if (matcher.find()) {
                System.out.println("Invalid assignment");
                return false;
            } else if (invalidIdentifierMatcher.find()) {
                System.out.println("Invalid identifier");
                return false;
            }
        } else if (matcher.find()) {
            findInMap(str);
            return true;
        }
        return true;
    }

    void Operate(String str) {
        String regexAddition = "(?<=[-+*/])|(?=[-+*/])";
        String[] arr = str.split(regexAddition);
        if (arr.length <= 2 || str.contains("=")) {
            singleNumber(str);
        } else {
            if (!checkForSign(str)) {
                System.out.println("Invalid expression");
            } else {
                String postFix = makePostfix(str);
                if (!postFix.equals("")){
                    System.out.println(calculateFromPostFix(postFix));
                }
            }
        }
    }

    private BigInteger calculateFromPostFix(String str) {
        BigInteger result = BigInteger.ZERO, num;
        Stack<BigInteger> stack = new Stack<>();
        String[] arr = str.split(" ");
        for (String temp : arr) {
            if (!temp.equals(" ")) {
                try {
                    num = new BigInteger(temp);
                    stack.push(num);
                } catch (NumberFormatException e) {
                    BigInteger b = stack.pop();
                    BigInteger a = stack.pop();
                    result = operation(a, b, temp);
                    stack.push(result);
                }
            }
        }
        return result;
    }

    private BigInteger operation(BigInteger a, BigInteger b, String ch) {
        switch (ch) {
            case "+" :
                return a.add(b);
            case "-" :
                return a.subtract(b);
            case "*" :
                return a.multiply(b);
            default:
                return a.divide(b);
        }
    }

    String redundantSigns(String str) {
        int sign = 0;
        String plusRegex = "[+]+";
        String productRegex = "[*]{2,}";
        String divisionRegex = "[/]{2,}";
        Pattern pattern = Pattern.compile(productRegex);
        Matcher matcher = pattern.matcher(str);
        Pattern pattern2 = Pattern.compile(divisionRegex);
        Matcher matcher2 = pattern2.matcher(str);
        if (matcher.find() || matcher2.find()) {
            System.out.println("Invalid expression");
            return "";
        }
        for (int i = 0; i < str.length(); i++) {
            if ( i < str.length() - 1) {
                if (str.charAt(i) == '+' && str.charAt(i + 1) == '-') {
                    str = str.substring(0,i) + str.substring(i + 1);
                }
            }
            if (str.charAt(i) == '-') {
                sign++;
            } else {
                String ch;
                if (sign > 1) {
                    String minusRegex ="-".repeat(sign);
                    ch = sign % 2 == 0 ? "+" : "-";
                    str = str.replace(minusRegex, ch);
                }
                sign = 0;
            }
        }
        str = str.replaceAll(plusRegex, "+");
        return str;
    }

    String makePostfix(String str) {
        String postFix = "";
        String[] arr = str.split("(?<=[-+*/()])|(?=[-+*/()])");
        Stack<String> stack = new Stack<>();
        for (int i = 0; i < arr.length; i++) {
            String temp = arr[i].trim();
            try {
                BigInteger num = new BigInteger(temp);
                postFix += (temp + " ");
            } catch (NumberFormatException e) {
                if (temp.matches("[a-zA-Z]+")) {
                    if (variables.containsKey(temp)) {
                        postFix += (variables.get(temp) + " ");
                    } else {
                        System.out.println("Invalid expression");
                        return "";
                    }
                } else if (temp.equals("(")) {
                    stack.push(temp);
                } else if (temp.equals(")")) {
                    while (!stack.isEmpty() && !stack.peek().equals("(")) {
                        postFix += (stack.pop() + " ");
                    }
                    if (stack.isEmpty() || !stack.peek().equals("(")) {
                        System.out.println("Invalid expression");
                        return "";
                    } else {
                        stack.pop();
                    }
                } else {
                    while (!stack.isEmpty() && getPrecedence(temp) <= getPrecedence(stack.peek())) {
                        postFix += (stack.pop() + " ");
                    }
                    stack.push(temp);
                }
            }

        }
        while (!stack.isEmpty()) {
            if (stack.peek().equals("(") || stack.peek().equals(")")) {
                System.out.println("Invalid expression");
                return "";
            }
            postFix += (stack.pop() + " ");
        }
        return postFix;
    }

}
