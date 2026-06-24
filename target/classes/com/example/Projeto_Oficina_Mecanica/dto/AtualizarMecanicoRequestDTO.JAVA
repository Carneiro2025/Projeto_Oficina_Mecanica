package com.example.Projeto_Oficina_Mecanica.dto.request;

import com.example.Projeto_Oficina_Mecanica.entity.EspecialidadeMecanico;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "Dados para atualização de mecânico (todos os campos opcionais)")
public class AtualizarMecanicoRequestDTO {

    @Size(min = 3, max = 150)
    private String nome;

    @Pattern(regexp = "^\\(?\\d{2}\\)?[\\s-]?\\d{4,5}-?\\d{4}$", message = "Telefone inválido")
    private String telefone;

    @Pattern(regexp = "^\\(?\\d{2}\\)?[\\s-]?9\\d{4}-?\\d{4}$", message = "Celular inválido")
    private String celular;

    @Email(message = "E-mail inválido")
    private String email;

    @Size(max = 20)
    private String crea;

    @Schema(description = "Substitui todas as especialidades existentes")
    private List<EspecialidadeMecanico> especialidades;

    @Size(max = 500)
    private String observacoes;

    private Long usuarioId;
}