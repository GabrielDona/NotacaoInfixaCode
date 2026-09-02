import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Stack;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Conversor de Notacao Infixa para RPN");
        System.out.print("Digite uma expressao matematica: ");

        String expressao = scanner.nextLine();
        apresentarResultado(expressao);

        scanner.close();
    }

    private static void apresentarResultado(String expressao) {
        try {
            List<String> rpn = converterParaRPN(expressao);

            System.out.println("Expressao original: " + expressao);
            System.out.println("Expressao em RPN: " + String.join(" ", rpn));
        } catch (IllegalArgumentException exception) {
            System.out.println("Erro: " + exception.getMessage());
        }
    }

    private static List<String> converterParaRPN(String expressao) {
        List<String> tokens = tokenizar(expressao);
        List<String> saida = new ArrayList<>();
        Stack<String> operadores = new Stack<>();

        for (String token : tokens) {
            if (ehNumero(token)) {
                saida.add(token);
            } else if (ehOperador(token)) {
                while (!operadores.isEmpty()
                        && ehOperador(operadores.peek())
                        && precedencia(operadores.peek()) >= precedencia(token)) {
                    saida.add(operadores.pop());
                }
                operadores.push(token);
            } else if (token.equals("(")) {
                operadores.push(token);
            } else if (token.equals(")")) {
                while (!operadores.isEmpty() && !operadores.peek().equals("(")) {
                    saida.add(operadores.pop());
                }

                if (operadores.isEmpty()) {
                    throw new IllegalArgumentException("parenteses fechando sem abertura correspondente.");
                }

                operadores.pop();
            }
        }

        while (!operadores.isEmpty()) {
            String operador = operadores.pop();

            if (operador.equals("(") || operador.equals(")")) {
                throw new IllegalArgumentException("parenteses desbalanceados.");
            }

            saida.add(operador);
        }

        return saida;
    }

    private static List<String> tokenizar(String expressao) {
        List<String> tokens = new ArrayList<>();
        int i = 0;

        while (i < expressao.length()) {
            char caractere = expressao.charAt(i);

            if (Character.isWhitespace(caractere)) {
                i++;
                continue;
            }

            if (Character.isDigit(caractere) || caractere == '.' || caractere == ','
                    || (caractere == '-' && ehSinalNegativo(expressao, tokens, i))) {
                StringBuilder numero = new StringBuilder();

                if (caractere == '-') {
                    numero.append(caractere);
                    i++;

                    while (i < expressao.length() && Character.isWhitespace(expressao.charAt(i))) {
                        i++;
                    }
                }

                boolean possuiSeparadorDecimal = false;

                while (i < expressao.length()) {
                    char atual = expressao.charAt(i);

                    if (Character.isDigit(atual)) {
                        numero.append(atual);
                        i++;
                    } else if (atual == '.' || atual == ',') {
                        if (possuiSeparadorDecimal) {
                            throw new IllegalArgumentException("numero decimal invalido.");
                        }

                        numero.append('.');
                        possuiSeparadorDecimal = true;
                        i++;
                    } else {
                        break;
                    }
                }

                String token = numero.toString();

                if (token.equals("-") || token.equals(".") || token.equals("-.")) {
                    throw new IllegalArgumentException("numero invalido perto de '" + token + "'.");
                }

                tokens.add(token);
            } else if (caractere == '+' || caractere == '-' || caractere == '*' || caractere == '/'
                    || caractere == '(' || caractere == ')') {
                tokens.add(String.valueOf(caractere));
                i++;
            } else {
                throw new IllegalArgumentException("caractere invalido: " + caractere);
            }
        }

        if (tokens.isEmpty()) {
            throw new IllegalArgumentException("nenhuma expressao foi informada.");
        }

        return tokens;
    }

    private static boolean ehSinalNegativo(String expressao, List<String> tokens, int indice) {
        if (expressao.charAt(indice) != '-') {
            return false;
        }

        int proximoIndice = indice + 1;

        while (proximoIndice < expressao.length() && Character.isWhitespace(expressao.charAt(proximoIndice))) {
            proximoIndice++;
        }

        if (proximoIndice >= expressao.length()
                || (!Character.isDigit(expressao.charAt(proximoIndice))
                && expressao.charAt(proximoIndice) != '.'
                && expressao.charAt(proximoIndice) != ',')) {
            return false;
        }

        return tokens.isEmpty()
                || ehOperador(tokens.get(tokens.size() - 1))
                || tokens.get(tokens.size() - 1).equals("(");
    }

    private static boolean ehNumero(String token) {
        try {
            Double.parseDouble(token);
            return true;
        } catch (NumberFormatException exception) {
            return false;
        }
    }

    private static boolean ehOperador(String token) {
        return token.equals("+") || token.equals("-") || token.equals("*") || token.equals("/");
    }

    private static int precedencia(String operador) {
        if (operador.equals("*") || operador.equals("/")) {
            return 2;
        }

        return 1;
    }
}
