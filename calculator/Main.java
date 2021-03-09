package calculator;

import java.util.*;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Operation operation = new Operation();
        boolean state = true, validityStatus;
        while (state) {
            validityStatus = false;
            String str = scanner.nextLine();
            str = str.replaceAll("[ \\t]", "");
            if (!str.isEmpty()) {
                switch (str) {
                    case "/exit": {
                        System.out.println("Bye!");
                        state = false;
                        break;
                    }
                    case "/help": {
                        System.out.println("The program does mathematical operation with variables");
                        break;
                    }
                    default: {
                        validityStatus = operation.checkValidity(str);
                    }
                }
                if (validityStatus) {
                    operation.variableToString(str);
                }
            }
        }
    }
}
