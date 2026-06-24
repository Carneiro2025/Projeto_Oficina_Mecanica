package com.example.Projeto_Oficina_Mecanica.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class EnderecoDTO {

    @Size(max = 200)
    @Schema(example = "Av. Caxangá")
    private String logradouro;

    @Size(max = 10)
    @Schema(example = "1500")
    private String numero;

    @Size(max = 50)
    private String complemento;

    @Size(max = 100)
    @Schema(example = "Iputinga")
    private String bairro;

    @Size(max = 100)
    @Schema(example = "Recife")
    private String cidade;

    @Size(min = 2, max = 2, message = "UF deve ter 2 caracteres")
    @Schema(example = "PE")
    private String uf;

    @Pattern(regexp = "^\\d{5}-?\\d{3}$", message = "CEP inválido")
    @Schema(example = "50710-000")
    private String cep;
}