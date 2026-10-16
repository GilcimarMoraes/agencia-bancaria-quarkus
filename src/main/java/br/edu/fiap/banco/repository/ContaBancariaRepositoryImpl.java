package br.edu.fiap.banco.repository;

import br.edu.fiap.banco.entity.ContaBancaria;
import br.edu.fiap.banco.entity.Pessoa;
import br.edu.fiap.banco.entity.TipoConta;
import br.edu.fiap.banco.exception.ContaJaCadastradaException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class ContaBancariaRepositoryImpl implements ContaBancariaRepository{

    private final DataSource dataSource;

    private static final String SELECT_BASE = """
            SELECT c.id, c.agencia, c.numero, c.saldo, c.ativa,
            p.id AS pessoa_id, p.nome AS pessoa_nome, p.cpf, p.email,
            t.id AS tipo_id, t.nome AS tipo_nome
            FROM contas_bancarias c
            JOIN pessoas p ON p.id=c.titular_id
            JOIN tipos_conta t ON t.id = c.tipo_conta_id
            """;

    @Inject
    public ContaBancariaRepositoryImpl( DataSource dataSource ) {
        this.dataSource = dataSource;
    }

    @Override
    public boolean existePorAgenciaENumero(String agencia, String numero) {
        String sql = "SELECT 1 FROM contas_bancarias WHERE agencia = ? AND numero = ?;";

        try(Connection connection = dataSource.getConnection();
        PreparedStatement stmt = connection.prepareStatement( sql ) ) {
            stmt.setString( 1, agencia );
            stmt.setString( 2, numero );

            try(ResultSet rs = stmt.executeQuery() ) {
                return rs.next();
            }

        } catch ( SQLException ex ) {
            throw infraestrutura( "buscar", ex );
        }
    }

    @Override
    public ContaBancaria salvar(ContaBancaria contaBancaria) {
        String sql = "INSERT INTO contas_bancarias (agencia, numero, saldo, ativa, titular_id, tipo_conta_id) " +
                "VALUES ( ?, ?, ?, ?, ?, ? );";
        try( Connection connection = dataSource.getConnection();
        PreparedStatement stmt = connection.prepareStatement( sql, Statement.RETURN_GENERATED_KEYS) ) {

            montarObjeto( stmt, contaBancaria );

            stmt.executeUpdate();

            try(ResultSet rs = stmt.getGeneratedKeys() ) {
                if( !rs.next() ) {
                    throw new IllegalStateException( "O INSERT não devolveu o ID da conta." );
                }
                return contaBancaria.comId( rs.getLong( 1 ) ) ;
            }

        } catch ( SQLException ex ) {
            if( "23505".equals( ex.getSQLState() ) ){
                throw new ContaJaCadastradaException();
            }
            throw new IllegalStateException( "Conta bancaria não pode ser salva", ex );
        }
    }

    @Override
    public Optional<ContaBancaria> buscarPorId(Long id) {
        try(Connection connection = dataSource.getConnection();
        PreparedStatement stmt = connection.prepareStatement( SELECT_BASE + " WHERE c.id = ?" ) ) {
            stmt.setLong( 1, id);
            try(ResultSet rs = stmt.executeQuery() ) {
                if( rs.next() ) {
                    return Optional.of( mapear( rs ) );
                }
                return Optional.empty();
            }
        } catch (SQLException ex ) {
            throw infraestrutura( "buscar", ex );
        }
    }

    @Override
    public List<ContaBancaria> listar() {
        return consultarLista( SELECT_BASE + " ORDER BY c.id", null );
    }

    @Override
    public List<ContaBancaria> listarPorTitular(Long titularId) {
        return consultarLista( SELECT_BASE + " WHERE c.titular_id = ? ORDER BY c.id", titularId );
    }

    @Override
    public ContaBancaria atualizar(ContaBancaria conta) {
        String sql = "UPDATE contas_bancarias SET agencia=?, numero=?, tipo_conta_id=? WHERE id=?;";

        try( Connection connection = dataSource.getConnection();
        PreparedStatement stmt = connection.prepareStatement( sql ) ) {
            stmt.setString( 1, conta.agencia() );
            stmt.setString( 2, conta.numero() );
            stmt.setLong( 3, conta.tipoConta().id() );
            stmt.setLong( 4, conta.id() );
            stmt.executeUpdate();
            return conta;
        } catch ( SQLException ex ) {
            throw infraestrutura( " atualizar ", ex );
        }
    }

    @Override
    public boolean encerrar(Long id) {
        String sql = "UPDATE contas_bancarias SET ativa = FALSE WHERE id = ?;";

        try( Connection connection = dataSource.getConnection();
        PreparedStatement stmt = connection.prepareStatement( sql ) ) {
            stmt.setLong( 1, id );
            return stmt.executeUpdate() > 0;
        } catch ( SQLException ex ) {
            throw infraestrutura( " encerrar ", ex );
        }
    }

    private void montarObjeto( PreparedStatement stmt, ContaBancaria conta ) throws SQLException {
        stmt.setString(1, conta.agencia() );
        stmt.setString( 2, conta.numero() );
        stmt.setBigDecimal( 3, conta.saldo() );
        stmt.setBoolean( 4, conta.ativa() );
        stmt.setLong( 5, conta.titular().id() );
        stmt.setLong( 6, conta.tipoConta().id() );
    }

    private ContaBancaria mapear( ResultSet rs ) throws SQLException {
        Pessoa pessoa = new Pessoa(
                rs.getLong( "pessoa_id" ),
                rs.getString( "pessoa_nome" ),
                rs.getString( "cpf" ),
                rs.getString( "email" )
        );
        TipoConta tipo = new TipoConta(
                rs.getLong( "tipo_conta_id" ),
                rs.getString( "tipo_conta_nome" )
        );
        return new ContaBancaria(
                rs.getLong( "id" ),
                rs.getString( "agencia" ),
                rs.getString( "numero" ),
                rs.getBigDecimal( "saldo" ),
                rs.getBoolean( "ativa" ),
                pessoa,
                tipo
        );
    }

    private IllegalStateException infraestrutura( String acao, SQLException ex ) {
        return new IllegalStateException( "Falha ao " +acao + " conta bancaria.", ex );
    }

    private List<ContaBancaria> consultarLista( String sql, Long parametro ) {
        List<ContaBancaria> contas = new ArrayList<>();

        try( Connection connection = dataSource.getConnection();
        PreparedStatement stmt = connection.prepareStatement( sql ) ) {
            if( parametro != null ) {
                stmt.setLong( 1, parametro );
            }
            try( ResultSet rs = stmt.executeQuery() ) {
                while ( rs.next() ) {
                    contas.add( mapear( rs ) );
                }
            }
            return contas;

        } catch ( SQLException ex ) {
            throw infraestrutura( " encontrar ", ex );
        }
    }
}
