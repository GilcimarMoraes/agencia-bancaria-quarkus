package br.edu.fiap.banco.repository;

import br.edu.fiap.banco.entity.TipoConta;

import java.util.List;
import java.util.Optional;

public interface TipoContaRepository {

    boolean existeByNomeIgnoreCase(String nome );

    boolean existeByNomeIgnoreCaseAndIdNot( String nome, Long id );

    TipoConta salvar( TipoConta tipoConta );

    Optional<TipoConta> getTipoContaById( Long id );

    TipoConta atualizar( TipoConta tipoConta );

    boolean excluir( Long id );

    List<TipoConta> listarTodos();
}
