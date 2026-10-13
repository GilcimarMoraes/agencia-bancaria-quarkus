package br.edu.fiap.banco.entity;

public record TipoConta( Long id, String nome ) {

    public TipoConta {
        if( nome == null || nome.isBlank() ) {
            throw new IllegalArgumentException( "Nome é obrigatório." );
        }

        nome = nome.trim();
    }

    public TipoConta atualizar( String novoNome) {
        return new TipoConta( id, novoNome );
    }

    public TipoConta comId( Long novoId ) {
        return new TipoConta( novoId, nome );
    }

    public boolean nomeAlterado( String novoNome ) {
        return !nome.equalsIgnoreCase( novoNome.trim() );
    }
}


