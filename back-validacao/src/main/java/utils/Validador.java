package utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Validador {

    public static boolean cpfValido(String cpf) {
        Pattern pattern = Pattern.compile("\\d{3}\\.\\d{3}\\.\\d{3}\\-\\d{2}", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(cpf);
        return matcher.find();
    }

    public static boolean emailValido(String email) {
        Pattern pattern = Pattern.compile("^(.+)@(.+)$", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.find();
    }

    public static boolean cepValido(String cep) {
        Pattern pattern = Pattern.compile("\\d{5}-\\d{3}", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(cep);
        return matcher.find();
    }
}
