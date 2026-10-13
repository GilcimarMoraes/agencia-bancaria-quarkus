package br.edu.fiap.banco.repository;

import br.edu.fiap.banco.entity.TipoConta;
import br.edu.fiap.banco.exception.TipoContaNaoEncontradaException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import javax.sql.DataSource;
import java.sql.*;
import java.util.Optional;

@ApplicationScoped
public class TipoContaRepositoryImpl implements TipoContaRepository{

    private final DataSource dataSource;

    @Inject
    public TipoContaRepositoryImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public boolean existePorNomeIgnoreCase(String nome) {
        String sql = "SELECT 1 FROM tipoConta WHERE UPPER(nome) = UPPER(?);";

        try (Connection connection = dataSource.getConnection();
        PreparedStatement stmt = connection.prepareStatement( sql ) ){
            stmt.setString( 1, nome );
            try (ResultSet rs = stmt.executeQuery() ){
                return rs.next();
            }
        } catch ( SQLException ex ) {
            throw new IllegalStateException( "Falha ao consultar tipo de conta.", ex );
        }
    }

    @Override
    public TipoConta salvar(TipoConta tipoConta) {

        String sql = "INSERT INTO tipoConta (nome) VALUES (?);";

        try( Connection connection = dataSource.getConnection();
        PreparedStatement stmt = connection.prepareStatement( sql, Statement.RETURN_GENERATED_KEYS) ) {
            stmt.setString(1, tipoConta.nome() );
            stmt.executeUpdate();
            try(ResultSet rs = stmt.getGeneratedKeys() ){
                if( !rs.next() ) {
                    throw new IllegalStateException( "O INSERT não devolveu o ID do tipo da conta." );
                }
                return tipoConta.comId( rs.getLong( "id" ) );
            }
        } catch ( SQLException ex ) {
            throw new IllegalStateException( "Falha ao persistir tipo de conta.", ex );
        }
    }

    @Override
    public Optional<TipoConta> getTipoContaById(Long id) {
        String sql = "SELECT id, nome FROM tipoConta WHERE id = ?;";

        try( Connection connection = dataSource.getConnection();
        PreparedStatement stmt = connection.prepareStatement( sql ) ) {
            stmt.setLong( 1, id );

            try( ResultSet rs = stmt.executeQuery() ) {
                if( !rs.next() ) {
                    return Optional.empty();
                }

                return Optional.of( new TipoConta(
                        rs.getLong( "id" ),
                        rs.getString( "nome" )
                ) );
            }

        } catch ( SQLException ex ) {
            throw new IllegalStateException( "Falha ao consultar o tipo de conta.", ex );
        }

    }

    @Override
    public TipoConta atualizar(TipoConta tipoConta) {
        String sql = "UPDATE tipoConta SET nome = ? WHERE id = ?;";

        try(Connection connection = dataSource.getConnection();
        PreparedStatement stmt = connection.prepareStatement( sql ) ) {
            stmt.setString( 1, tipoConta.nome() );
            stmt.setLong( 2, tipoConta.id() );

            int linhasAtualizadas = stmt.executeUpdate();

            if( linhasAtualizadas == 0 ) {
                throw new TipoContaNaoEncontradaException();
            }

            return tipoConta;

        } catch ( SQLException ex ) {
            throw new IllegalStateException( "Não foi possivel atualizar o tipo de conta.", ex );
        }
    }

    @Override
    public boolean excluir(Long id) {
        String sql = "DELETE FROM tipoConta WHERE id = ?;";

        try( Connection connection = dataSource.getConnection();
        PreparedStatement stmt = connection.prepareStatement( sql )) {
            stmt.setLong( 1, id );

            return stmt.executeUpdate() > 0;

        } catch ( SQLException ex ) {
            throw new IllegalStateException( "Falha ao excluir o tipo de conta", ex );
        }

    }
}
