package br.edu.fiap.banco.exception;

import jakarta.ws.rs.core.Response;

public class ContaJaCadastradaException extends ErroNegocioException {

    public ContaJaCadastradaException() {
        super(Response.Status.CONFLICT, "Agência e Número já cadastrado." );
    }
}
