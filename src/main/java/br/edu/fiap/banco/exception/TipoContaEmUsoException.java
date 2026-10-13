package br.edu.fiap.banco.exception;

import jakarta.ws.rs.core.Response;

public class TipoContaEmUsoException extends ErroNegocioException {

    public TipoContaEmUsoException() {
        super( Response.Status.CONFLICT, "Tipo de conta em uso e não pode ser excluída." );
    }
}
