package com.example.Projeto_Oficina_Mecanica.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CpfCnpjValidator implements ConstraintValidator<CpfCnpj, String> {
    
    @Override
    public void initialize(CpfCnpj constraintAnnotation) {
    }
    
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isEmpty()) {
            return true;
        }
        
        // Remove caracteres especiais
        String clean = value.replaceAll("[^0-9]", "");
        
        // Valida CPF (11 dígitos) ou CNPJ (14 dígitos)
        return clean.length() == 11 || clean.length() == 14;
    }
}
