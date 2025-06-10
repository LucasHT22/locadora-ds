package dao;

import model.Cliente;
import util.ConnectionFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DATA ACCESS OBJECT para Cliente
 * @author lucas
 */
public class ClienteDAO {
    
    /**
     * Insere um novo cliente
     */
   /*
 * Insere um novo cliente no banco de dados.
 * Prepara a instrução SQL para inserção com os campos nome, cpf, telefone, email, endereço, cnh, data de nascimento e observações.
 * Define os parâmetros da query com os valores do objeto Cliente passado como argumento.
 * Se a data de nascimento for nula, insere valor NULL no banco.
 * Executa a atualização no banco e trata possíveis exceções SQL.
 */
public void inserir(Cliente c) {
    String sql = "INSERT INTO clientes (nome, cpf, telefone, email, endereco, cnh, " +
                "data_nascimento, observacoes) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
    try (Connection conn = ConnectionFactory.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setString(1, c.getNome());
        stmt.setString(2, c.getCpf());
        stmt.setString(3, c.getTelefone());
        stmt.setString(4, c.getEmail());
        stmt.setString(5, c.getEndereco());
        stmt.setString(6, c.getCnh());

        if (c.getDataNascimento() != null) {
            stmt.setDate(7, Date.valueOf(c.getDataNascimento()));
        } else {
            stmt.setNull(7, Types.DATE);
        }

        stmt.setString(8, c.getObservacoes());
        stmt.executeUpdate();
    } catch (SQLException e) {
        e.printStackTrace();
    }
}

/*
 * Lista todos os clientes ativos no banco de dados.
 * Executa uma consulta SQL que seleciona todos os registros onde o campo ativo é verdadeiro.
 * Ordena os resultados pelo nome do cliente.
 * Para cada resultado do ResultSet, cria um objeto Cliente e adiciona na lista que será retornada.
 */
public List<Cliente> listarTodos() {
    List<Cliente> lista = new ArrayList<>();
    String sql = "SELECT * FROM clientes WHERE ativo = true ORDER BY nome";

    try (Connection conn = ConnectionFactory.getConnection();
         Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery(sql)) {

        while (rs.next()) {
            Cliente c = criarClienteDoResultSet(rs);
            lista.add(c);
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return lista;
}

/*
 * Busca um cliente ativo pelo seu ID.
 * Prepara a instrução SQL para buscar o cliente pelo campo id e ativo igual a true.
 * Se encontrado, cria e retorna o objeto Cliente correspondente.
 * Caso contrário, retorna null.
 */
public Cliente buscarPorId(int id) {
    String sql = "SELECT * FROM clientes WHERE id = ? AND ativo = true";
    try (Connection conn = ConnectionFactory.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setInt(1, id);
        ResultSet rs = stmt.executeQuery();

        if (rs.next()) {
            return criarClienteDoResultSet(rs);
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return null;
}

/*
 * Busca um cliente ativo pelo seu CPF.
 * Prepara a instrução SQL para buscar o cliente pelo campo cpf e ativo igual a true.
 * Se encontrado, cria e retorna o objeto Cliente correspondente.
 * Caso contrário, retorna null.
 */
public Cliente buscarPorCpf(String cpf) {
    String sql = "SELECT * FROM clientes WHERE cpf = ? AND ativo = true";
    try (Connection conn = ConnectionFactory.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setString(1, cpf);
        ResultSet rs = stmt.executeQuery();

        if (rs.next()) {
            return criarClienteDoResultSet(rs);
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return null;
}

/*
 * Busca clientes ativos cujo nome contenha parcialmente o valor informado.
 * Utiliza a cláusula ILIKE para busca case-insensitive.
 * Retorna uma lista de clientes ordenada pelo nome.
 */
public List<Cliente> buscarPorNome(String nome) {
    List<Cliente> lista = new ArrayList<>();
    String sql = "SELECT * FROM clientes WHERE nome ILIKE ? AND ativo = true ORDER BY nome";

    try (Connection conn = ConnectionFactory.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setString(1, "%" + nome + "%");
        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            Cliente c = criarClienteDoResultSet(rs);
            lista.add(c);
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return lista;
}

/*
 * Atualiza os dados de um cliente existente no banco.
 * Prepara a instrução SQL para atualizar os campos nome, cpf, telefone, email, endereço, cnh, data de nascimento e observações pelo id.
 * Caso a data de nascimento seja nula, atualiza com valor NULL.
 * Executa a atualização e trata possíveis exceções.
 */
public void atualizar(Cliente c) {
    String sql = "UPDATE clientes SET nome = ?, cpf = ?, telefone = ?, email = ?, " +
                "endereco = ?, cnh = ?, data_nascimento = ?, observacoes = ? WHERE id = ?";
    try (Connection conn = ConnectionFactory.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setString(1, c.getNome());
        stmt.setString(2, c.getCpf());
        stmt.setString(3, c.getTelefone());
        stmt.setString(4, c.getEmail());
        stmt.setString(5, c.getEndereco());
        stmt.setString(6, c.getCnh());

        if (c.getDataNascimento() != null) {
            stmt.setDate(7, Date.valueOf(c.getDataNascimento()));
        } else {
            stmt.setNull(7, Types.DATE);
        }

        stmt.setString(8, c.getObservacoes());
        stmt.setInt(9, c.getId());
        stmt.executeUpdate();
    } catch (SQLException e) {
        e.printStackTrace();
    }
}

/*
 * Desativa um cliente no banco de dados, realizando um soft delete.
 * Atualiza o campo ativo para false com base no id informado.
 */
public void desativar(int id) {
    String sql = "UPDATE clientes SET ativo = false WHERE id = ?";
    try (Connection conn = ConnectionFactory.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setInt(1, id);
        stmt.executeUpdate();
    } catch (SQLException e) {
        e.printStackTrace();
    }
}

/*
 * Método auxiliar que cria um objeto Cliente a partir dos dados do ResultSet.
 * Extrai os campos do ResultSet e popula um novo objeto Cliente.
 * Trata campos dataNascimento e dataCadastro convertendo para os tipos Java adequados.
 */
private Cliente criarClienteDoResultSet(ResultSet rs) throws SQLException {
    Cliente c = new Cliente();
    c.setId(rs.getInt("id"));
    c.setNome(rs.getString("nome"));
    c.setCpf(rs.getString("cpf"));
    c.setTelefone(rs.getString("telefone"));
    c.setEmail(rs.getString("email"));
    c.setEndereco(rs.getString("endereco"));
    c.setCnh(rs.getString("cnh"));

    Date dataNascimento = rs.getDate("data_nascimento");
    if (dataNascimento != null) {
        c.setDataNascimento(dataNascimento.toLocalDate());
    }

    c.setAtivo(rs.getBoolean("ativo"));

    Timestamp dataCadastro = rs.getTimestamp("data_cadastro");
    if (dataCadastro != null) {
        c.setDataCadastro(dataCadastro.toLocalDateTime());
    }

    c.setObservacoes(rs.getString("observacoes"));

    return c;
}
}
