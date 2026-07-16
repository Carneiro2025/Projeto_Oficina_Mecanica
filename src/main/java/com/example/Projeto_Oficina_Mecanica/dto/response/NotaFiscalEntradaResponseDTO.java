package com.example.Projeto_Oficina_Mecanica.dto.response;


import lombok.Data;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;



@Data
public class NotaFiscalEntradaResponseDTO {


    private Long id;


    private String numero;


    private Long fornecedorId;


    private String fornecedorRazaoSocial;


    private LocalDate dataEmissao;


    private LocalDate dataEntrada;


    private BigDecimal valorTotal;


    private String observacoes;


    private Boolean processada;


    private List<ItemNotaFiscalEntradaResponseDTO> itens;


    private LocalDateTime createdAt;


}
