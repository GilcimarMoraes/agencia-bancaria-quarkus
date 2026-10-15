package br.edu.fiap.banco.exception;

import jakarta.ws.rs.core.Response;

public class ContaNaoEncontradaException extends ErroNegocioException {

    public ContaNaoEncontradaException() {
        super(Response.Status.NOT_FOUND, "Conta Bancária não encontrada." );
    }
}
