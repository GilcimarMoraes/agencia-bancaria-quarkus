package br.edu.fiap.banco.exception;

import jakarta.ws.rs.core.Response;

public class TipoContaJaCadastradaException extends ErroNegocioException {

    public TipoContaJaCadastradaException() {
        super( Response.Status.CONFLICT, "Tipo de conta já cadastrado." );
    }
}
