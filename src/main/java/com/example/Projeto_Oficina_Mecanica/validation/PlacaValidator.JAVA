package com.example.Projeto_Oficina_Mecanica.validation;

import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;

import java.lang.annotation.*;
import java.util.regex.Pattern;

/**
 * Validação de Placa de Veículo.
 * Formatos aceitos: padrão antigo (ABC1234) e Mercosul (ABC1D23).
 */
public class PlacaValidator {

    @Documented
    @Constraint(validatedBy = PlacaConstraintValidator.class)
    @Target({ElementType.FIELD, ElementType.PARAMETER})
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Placa {
        String message() default "Placa inválida. Formatos aceitos: ABC1234 ou ABC1D23";
        Class<?>[] groups() default {};
        Class<? extends Payload>[] payload() default {};
    }

    public static class PlacaConstraintValidator implements ConstraintValidator<Placa, String> {

        private static final Pattern PADRAO_ANTIGO   = Pattern.compile("^[A-Za-z]{3}[-]?[0-9]{4}$");
        private static final Pattern PADRAO_MERCOSUL = Pattern.compile("^[A-Za-z]{3}[-]?[0-9][A-Za-z][0-9]{2}$");

        @Override
        public boolean isValid(String value, ConstraintValidatorContext ctx) {
            if (value == null || value.isBlank()) return false;
            String placa = value.trim().toUpperCase();
            return PADRAO_ANTIGO.matcher(placa).matches()
                || PADRAO_MERCOSUL.matcher(placa).matches();
        }
    }

    public static String normalizar(String placa) {
        if (placa == null) return null;
        return placa.toUpperCase().replace("-", "");
    }
}