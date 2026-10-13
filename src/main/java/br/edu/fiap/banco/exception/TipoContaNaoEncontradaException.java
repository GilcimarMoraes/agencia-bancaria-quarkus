package br.edu.fiap.banco.exception;

import jakarta.ws.rs.core.Response;

public class TipoContaNaoEncontradaException extends ErroNegocioException {
    public TipoContaNaoEncontradaException() {
        super(Response.Status.NOT_FOUND, "Tipo de conta não encontrada." );
    }
}
