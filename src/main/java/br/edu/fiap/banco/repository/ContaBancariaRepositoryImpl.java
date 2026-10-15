package br.edu.fiap.banco.repository;

import br.edu.fiap.banco.entity.ContaBancaria;
import br.edu.fiap.banco.entity.Pessoa;
import br.edu.fiap.banco.entity.TipoConta;
import br.edu.fiap.banco.exception.ContaJaCadastradaException;
import jakarta.inject.Inject;

import javax.sql.DataSource;
import java.sql.*;
import java.util.List;
import java.util.Optional;

public class ContaBancariaRepositoryImpl implements ContaBancariaRepository{

    private final DataSource dataSource;
    private final PessoaRepositoryImpl pessoaRepositoryImpl;
    private static final String SELECT_BASE = """
            SELECT c.id, c.agencia, c.numero, c.saldo, c.ativa,
            p.id AS pessoa_id, p.nome AS pessoa_nome, p.cpf, p.email,
            t.id AS tipo_id, t.nome AS tipo_nome
            FROM contas_bancarias c
            JOIN pessoa p ON p.id=c.titular_id
            JOIN tipo t ON t.id = c.tipo_conta_id;
            """;

    @Inject
    public ContaBancariaRepositoryImpl(DataSource dataSource, PessoaRepositoryImpl pessoaRepositoryImpl) {
        this.dataSource = dataSource;
        this.pessoaRepositoryImpl = pessoaRepositoryImpl;
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
            throw new IllegalStateException( "Conta Bancária não encontrada.", ex );
        }
    }

    @Override
    public ContaBancaria salvar(ContaBancaria contaBancaria) {
        String sql = "INSERT INTO contas_bancarias (agencia, numero, saldo, ativa, titular_id, tipo_conta_id) " +
                "VALUES ( ?, ?, ?, ?, ?, ? );";
        try( Connection connection = dataSource.getConnection();
        PreparedStatement stmt = connection.prepareStatement( sql, PreparedStatement.RETURN_GENERATED_KEYS) ) {

            montarObjeto( stmt, contaBancaria );

            stmt.executeUpdate();

            try(ResultSet rs = stmt.executeQuery() ) {
                if( !rs.next() ) {
                    throw new IllegalStateException( "O INSERT não devolveu o ID da conta." );
                }
                return ContaBancaria.comId( rs.getLong( 1 ) ) ;
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
                    Optional.of( mapear( rs ) );
                }
                return Optional.empty();
            }
        } catch (SQLException ex ) {
            throw infraestrutura( "buscar", ex );
        }
    }

    @Override
    public List<ContaBancaria> listar() {
        return List.of();
    }

    @Override
    public List<ContaBancaria> listarPorTitular(Long titularId) {
        return List.of();
    }

    @Override
    public boolean encerrar(Long id) {
        return false;
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
}
