package com.example.Projeto_Oficina_Mecanica.dto.request;

import com.example.Projeto_Oficina_Mecanica.entity.EspecialidadeMecanico;
import com.example.Projeto_Oficina_Mecanica.validation.CpfCnpj;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "Dados para cadastro de mecânico")
public class CriarMecanicoRequestDTO {

    @NotBlank(message = "Nome é obrigatório")
    @Size(min = 3, max = 150)
    @Schema(example = "Carlos Eduardo Santos")
    private String nome;

    @NotBlank(message = "CPF é obrigatório")
    @CpfCnpj
    @Schema(example = "529.982.247-25")
    private String cpf;

    @Pattern(regexp = "^\\(?\\d{2}\\)?[\\s-]?\\d{4,5}-?\\d{4}$", message = "Telefone inválido")
    @Schema(example = "(81) 3333-4444")
    private String telefone;

    @Pattern(regexp = "^\\(?\\d{2}\\)?[\\s-]?9\\d{4}-?\\d{4}$", message = "Celular inválido")
    @Schema(example = "(81) 99999-1234")
    private String celular;

    @Email(message = "E-mail inválido")
    @Schema(example = "carlos@oficina.com")
    private String email;

    @Size(max = 20)
    @Schema(example = "CREA-PE 12345")
    private String crea;

    @NotEmpty(message = "Informe ao menos uma especialidade")
    @Schema(example = "[\"MOTOR\", \"ELETRICA\"]")
    private List<EspecialidadeMecanico> especialidades;

    @Size(max = 500)
    private String observacoes;

    @Schema(description = "ID do usuário do sistema vinculado ao mecânico (opcional)")
    private Long usuarioId;
}

