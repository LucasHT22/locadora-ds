package dao;

import model.Veiculo;
import util.ConnectionFactory;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DATA ACCESS OBJECT para Veiculo - Revisado
 * @author lucas
 */
public class VeiculoDAO {
    
    /**
     * Insere um novo veículo
     */
/**
 * Classe responsável pelo acesso ao banco de dados para operações relacionadas aos veículos.
 * Inclui métodos de inserção, listagem, busca, atualização e remoção.
 */
public void inserir(Veiculo v) {
    String sql = "INSERT INTO veiculos (modelo, marca, ano, placa, cor, combustivel, " +
                "km_atual, valor_diaria, observacoes) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

    /*
     * Conecta ao banco de dados e prepara o comando de inserção com os dados do veículo.
     * Os parâmetros são preenchidos com os valores do objeto Veiculo, tratando casos nulos.
     */
    try (Connection conn = ConnectionFactory.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setString(1, v.getModelo());
        stmt.setString(2, v.getMarca());
        stmt.setInt(3, v.getAno());
        stmt.setString(4, v.getPlaca() != null ? v.getPlaca() : gerarPlacaAleatoria());
        stmt.setString(5, v.getCor());
        stmt.setString(6, v.getCombustivel());

        if (v.getKmAtual() != null) {
            stmt.setBigDecimal(7, v.getKmAtual());
        } else {
            stmt.setBigDecimal(7, BigDecimal.ZERO);
        }

        if (v.getValorDiaria() != null) {
            stmt.setBigDecimal(8, v.getValorDiaria());
        } else {
            stmt.setNull(8, Types.DECIMAL);
        }

        stmt.setString(9, v.getObservacoes());
        stmt.executeUpdate();
    } catch (SQLException e) {
        e.printStackTrace();
    }
}

/*
 * Lista todos os veículos cadastrados no banco, ordenando por marca e modelo.
 */
public List<Veiculo> listarTodos() {
    List<Veiculo> lista = new ArrayList<>();
    String sql = "SELECT * FROM veiculos ORDER BY marca, modelo";

    try (Connection conn = ConnectionFactory.getConnection();
         Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery(sql)) {

        while (rs.next()) {
            Veiculo v = criarVeiculoDoResultSet(rs);
            lista.add(v);
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return lista;
}

/*
 * Lista veículos com base em um status (ex: disponível, alugado), ordenando por marca e modelo.
 */
public List<Veiculo> listarPorStatus(String status) {
    List<Veiculo> lista = new ArrayList<>();
    String sql = "SELECT * FROM veiculos WHERE status = ? ORDER BY marca, modelo";

    try (Connection conn = ConnectionFactory.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setString(1, status);
        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            Veiculo v = criarVeiculoDoResultSet(rs);
            lista.add(v);
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return lista;
}

/*
 * Busca e retorna um veículo específico com base no ID.
 */
public Veiculo buscarPorId(int id) {
    String sql = "SELECT * FROM veiculos WHERE id = ?";
    try (Connection conn = ConnectionFactory.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setInt(1, id);
        ResultSet rs = stmt.executeQuery();

        if (rs.next()) {
            return criarVeiculoDoResultSet(rs);
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return null;
}

/*
 * Busca e retorna um veículo com base na placa informada.
 */
public Veiculo buscarPorPlaca(String placa) {
    String sql = "SELECT * FROM veiculos WHERE placa = ?";
    try (Connection conn = ConnectionFactory.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setString(1, placa);
        ResultSet rs = stmt.executeQuery();

        if (rs.next()) {
            return criarVeiculoDoResultSet(rs);
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return null;
}

/*
 * Busca veículos por texto, verificando se o modelo ou a marca contém a string informada.
 * A busca é feita de forma case-insensitive (ILIKE).
 */
public List<Veiculo> buscarPorTexto(String texto) {
    List<Veiculo> lista = new ArrayList<>();
    String sql = "SELECT * FROM veiculos WHERE modelo ILIKE ? OR marca ILIKE ? ORDER BY marca, modelo";

    try (Connection conn = ConnectionFactory.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        String parametro = "%" + texto + "%";
        stmt.setString(1, parametro);
        stmt.setString(2, parametro);
        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            Veiculo v = criarVeiculoDoResultSet(rs);
            lista.add(v);
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return lista;
}

/*
 * Atualiza todos os dados de um veículo existente com base no ID.
 */
public void atualizar(Veiculo v) {
    String sql = "UPDATE veiculos SET modelo = ?, marca = ?, ano = ?, placa = ?, " +
                "cor = ?, combustivel = ?, km_atual = ?, valor_diaria = ?, " +
                "status = ?, observacoes = ? WHERE id = ?";
    try (Connection conn = ConnectionFactory.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setString(1, v.getModelo());
        stmt.setString(2, v.getMarca());
        stmt.setInt(3, v.getAno());
        stmt.setString(4, v.getPlaca());
        stmt.setString(5, v.getCor());
        stmt.setString(6, v.getCombustivel());
        stmt.setBigDecimal(7, v.getKmAtual());
        stmt.setBigDecimal(8, v.getValorDiaria());
        stmt.setString(9, v.getStatus());
        stmt.setString(10, v.getObservacoes());
        stmt.setInt(11, v.getId());
        stmt.executeUpdate();
    } catch (SQLException e) {
        e.printStackTrace();
    }
}

/*
 * Atualiza apenas o status de um veículo (ex: de “disponível” para “alugado”).
 */
public void atualizarStatus(int id, String status) {
    String sql = "UPDATE veiculos SET status = ? WHERE id = ?";
    try (Connection conn = ConnectionFactory.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setString(1, status);
        stmt.setInt(2, id);
        stmt.executeUpdate();
    } catch (SQLException e) {
        e.printStackTrace();
    }
}

/*
 * Atualiza a quilometragem atual de um veículo.
 */
public void atualizarKm(int id, BigDecimal novoKm) {
    String sql = "UPDATE veiculos SET km_atual = ? WHERE id = ?";
    try (Connection conn = ConnectionFactory.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setBigDecimal(1, novoKm);
        stmt.setInt(2, id);
        stmt.executeUpdate();
    } catch (SQLException e) {
        e.printStackTrace();
    }
}

/*
 * Remove um veículo do banco de dados com base no ID.
 * Atenção: operação permanente (hard delete).
 */
public void remover(int id) {
    String sql = "DELETE FROM veiculos WHERE id = ?";
    try (Connection conn = ConnectionFactory.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setInt(1, id);
        stmt.executeUpdate();
    } catch (SQLException e) {
        e.printStackTrace();
    }
}

/*
 * Cria um objeto Veiculo a partir do ResultSet de uma consulta SQL.
 * Transforma os dados do banco em um objeto utilizável na aplicação.
 */
private Veiculo criarVeiculoDoResultSet(ResultSet rs) throws SQLException {
    Veiculo v = new Veiculo();
    v.setId(rs.getInt("id"));
    v.setModelo(rs.getString("modelo"));
    v.setMarca(rs.getString("marca"));
    v.setAno(rs.getInt("ano"));
    v.setPlaca(rs.getString("placa"));
    v.setCor(rs.getString("cor"));
    v.setCombustivel(rs.getString("combustivel"));
    v.setKmAtual(rs.getBigDecimal("km_atual"));
    v.setValorDiaria(rs.getBigDecimal("valor_diaria"));
    v.setStatus(rs.getString("status"));
    v.setObservacoes(rs.getString("observacoes"));

    Timestamp dataCadastro = rs.getTimestamp("data_cadastro");
    if (dataCadastro != null) {
        v.setDataCadastro(dataCadastro.toLocalDateTime());
    }

    return v;
}

/*
 * Gera uma placa aleatória no formato ABC-1234, usada como fallback.
 */
private String gerarPlacaAleatoria() {
    String letras = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    StringBuilder placa = new StringBuilder();

    for (int i = 0; i < 3; i++) {
        placa.append(letras.charAt((int)(Math.random() * letras.length())));
    }

    placa.append("-");
    placa.append((int)(Math.random() * 9000 + 1000));

    return placa.toString();
}
}
