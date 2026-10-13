package br.edu.fiap.banco.service;

import br.edu.fiap.banco.dto.TipoContaRequest;
import br.edu.fiap.banco.dto.TipoContaResponse;
import br.edu.fiap.banco.entity.TipoConta;
import br.edu.fiap.banco.exception.TipoContaJaCadastradaException;
import br.edu.fiap.banco.exception.TipoContaNaoEncontradaException;
import br.edu.fiap.banco.repository.TipoContaRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class TipoContaService {

    private final TipoContaRepository repository;

    @Inject
    public TipoContaService(TipoContaRepository repository) {
        this.repository = repository;
    }

    public TipoContaResponse cadastrar( TipoContaRequest request ) {
        if( repository.existePorNomeIgnoreCase( request.nome() ) ) {
            throw new TipoContaJaCadastradaException();
        }

        TipoConta novaTipo = new TipoConta( null, request.nome() );
        TipoContaResponse response = TipoContaResponse.de( repository.salvar( novaTipo ) );

        return response;
    }

    public TipoContaResponse getTipoContaById( Long id ) {
        TipoConta tipoConta = repository.getTipoContaById( id )
                .orElseThrow( TipoContaNaoEncontradaException::new );

        TipoContaResponse tipo = TipoContaResponse.de( tipoConta );

        return tipo;
    }

    public TipoContaResponse atualizar( Long id, TipoContaRequest request ) {
        TipoConta tipo = repository.getTipoContaById( id )
                .orElseThrow( TipoContaNaoEncontradaException::new );

        if( tipo.nomeAlterado( request.nome() ) && repository.existePorNomeIgnoreCase( request.nome() ) ) {
            throw new TipoContaJaCadastradaException();
        }

        TipoConta atualizada = tipo.atualizar( request.nome() );
        atualizada = repository.atualizar( atualizada );

        TipoContaResponse response = TipoContaResponse.de( atualizada );

        return response;
    }
    
    public void excluir( Long id ) {
        if( !repository.excluir( id ) ) {
            throw new TipoContaNaoEncontradaException();
        }
    }
}
