package br.edu.fiap.banco.repository;

import br.edu.fiap.banco.entity.Pessoa;
import br.edu.fiap.banco.exception.PessoaNaoEncontradaException;
import br.edu.fiap.banco.exception.PessoaPossuiContaException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import javax.sql.DataSource;
import java.sql.*;
import java.util.Optional;

@ApplicationScoped
public class PessoaRepositoryImpl implements PessoaRepository{
    private final DataSource dataSource;

    @Inject
    public PessoaRepositoryImpl(DataSource dataSource){
        this.dataSource = dataSource;
    }

    @Override
    public boolean existePorCpf(String cpf){
        String sqlQuery = "SELECT 1 FROM pessoas Where cpf = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sqlQuery)) {
            statement.setString(1, cpf);
            try (ResultSet resultado = statement.executeQuery()) {
                return resultado.next();
            }
        }catch(SQLException err){
            throw new IllegalStateException("Falha ao consultar pessoas.", err);
        }
    }

    @Override
    public Pessoa salvar(Pessoa pessoa){
        String sql = "INSERT INTO pessoas (nome, cpf, email) VALUES (?, ?, ?)";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, pessoa.nome());
            statement.setString(2, pessoa.cpf());
            statement.setString(3, pessoa.email());
            statement.executeUpdate();
            try (ResultSet resultado = statement.getGeneratedKeys()) {
                if(!resultado.next()){
                    throw new IllegalStateException("O INSERT não devolveu o ID da Pessoa.");
                }
                return pessoa.comId(resultado.getLong("id"));
            }
        }catch(SQLException err){
            throw new IllegalStateException("Falha ao persistir pessoa.", err);
        }


    }

    @Override
    public Optional<Pessoa> getPessoaById(Long id){
        String sql = "SELECT * FROM pessoas WHERE id = ?";

        try (
                Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql))
        {
            statement.setLong(1, id);

            try (ResultSet resultado = statement.executeQuery()) {
                if (!resultado.next()) {
                    return Optional.empty();
                }
                return Optional.of(new Pessoa(
                        resultado.getLong("id"),
                        resultado.getString("nome"),
                        resultado.getString("cpf"),
                        resultado.getString("email")));
            }
        }catch(SQLException err){
            throw new IllegalStateException("Falha ao consultar pessoaa.", err);
        }
    }

    @Override
    public Pessoa atualizar(Pessoa pessoa){
        String sql = "UPDATE pessoas SET nome = ?, cpf = ?, email = ? WHERE id = ?";

        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql))
        {
            statement.setString(1, pessoa.nome());
            statement.setString(2, pessoa.cpf());
            statement.setString(3, pessoa.email());

            //O id ele vai no WHERE, não entra na lista de colunas alteradas.
            statement.setLong(4, pessoa.id());

            //Zero linhas ; o id sumiu entre a consulta e o update
            if( statement.executeUpdate() == 0){
                throw new PessoaNaoEncontradaException();
            }

            return pessoa;
        }catch(SQLException err){
            throw new IllegalStateException("Falha ao atualizar pessoaa.", err);
        }
    }

    @Override
    public boolean excluir(Long id){
        String sql = "DELETE FROM pessoas Where id = ?";
        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql))
        {
            statement.setLong(1, id);

            // executeUpdate devolve quantas linhas sairam. Zero significa id ausente
            return statement.executeUpdate() > 0;
        }catch(SQLException err){
            // 23503 é violação de chave extrangeira, a pessoa ainda é titular de conta que esta ativa
            if("23503".equals(err.getSQLState())){
                throw new PessoaPossuiContaException();
            }

            throw new IllegalStateException("Falha ao excluir pessoaa.", err);
        }
    }









}
