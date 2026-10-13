package br.edu.fiap.banco.dto;

import br.edu.fiap.banco.entity.TipoConta;

public record TipoContaResponse(
        Long id,
        String nome
) {
    public static TipoContaResponse de(TipoConta tipoConta ){
        return new TipoContaResponse(tipoConta.id(), tipoConta.nome() );
    }
}
