package br.edu.fiap.banco.dto;

import jakarta.validation.constraints.NotBlank;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

public record TipoContaRequest(
        @Schema(example = "CONTA CORRENTE" )
        @NotBlank( message = "Nome é obrigatório." )
        String nome
) {
}
