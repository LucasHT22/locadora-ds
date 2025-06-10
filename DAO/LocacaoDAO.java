package dao;

import model.Locacao;
import util.ConnectionFactory;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DATA ACCESS OBJECT para Locacao - Revisado
 * @author lucas
 */
public class LocacaoDAO {
    
    /**
     * Insere uma nova locação
     */
   public void inserir(Locacao loc) {
    String sql = "INSERT INTO locacoes (cliente_id, veiculo_id, usuario_id, data_inicio, " +
                "data_fim_prevista, km_inicial, valor_diaria, dias_previstos, " +
                "valor_total_previsto, observacoes_retirada) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    try (Connection conn = ConnectionFactory.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setInt(1, loc.getClienteId());
        stmt.setInt(2, loc.getVeiculoId());

        // Se usuarioId for maior que zero, seta normalmente, senão seta NULL
        if (loc.getUsuarioId() > 0) {
            stmt.setInt(3, loc.getUsuarioId());
        } else {
            stmt.setNull(3, Types.INTEGER);
        }

        // Datas convertidas de LocalDate para java.sql.Date
        stmt.setDate(4, Date.valueOf(loc.getDataInicio()));
        stmt.setDate(5, Date.valueOf(loc.getDataFimPrevista()));

        // Km inicial pode ser nulo, por isso verifica e seta null se necessário
        if (loc.getKmInicial() != null) {
            stmt.setBigDecimal(6, loc.getKmInicial());
        } else {
            stmt.setNull(6, Types.DECIMAL);
        }

        // Valores obrigatórios da locação
        stmt.setBigDecimal(7, loc.getValorDiaria());
        stmt.setInt(8, loc.getDiasPrevistos());
        stmt.setBigDecimal(9, loc.getValorTotalPrevisto());
        stmt.setString(10, loc.getObservacoesRetirada());

        // Executa o comando de inserção no banco
        stmt.executeUpdate();

    } catch (SQLException e) {
        e.printStackTrace();
    }
}

/*
Método que retorna todas as locações do banco ordenadas por data de início decrescente.
Cria lista, executa SELECT *, converte cada linha do ResultSet em objeto Locacao,
adiciona na lista e retorna.
*/
public List<Locacao> listarTodas() {
    List<Locacao> lista = new ArrayList<>();
    String sql = "SELECT * FROM locacoes ORDER BY data_inicio DESC";

    try (Connection conn = ConnectionFactory.getConnection();
         Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery(sql)) {

        while (rs.next()) {
            Locacao loc = criarLocacaoDoResultSet(rs);
            lista.add(loc);
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return lista;
}

/*
Método para listar locações filtrando por status.
Prepara a consulta com parâmetro, seta status, executa e popula lista de Locacao.
*/
public List<Locacao> listarPorStatus(String status) {
    List<Locacao> lista = new ArrayList<>();
    String sql = "SELECT * FROM locacoes WHERE status = ? ORDER BY data_inicio DESC";

    try (Connection conn = ConnectionFactory.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setString(1, status);
        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            Locacao loc = criarLocacaoDoResultSet(rs);
            lista.add(loc);
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return lista;
}

/*
Método que lista locações ativas simplesmente chamando listarPorStatus com "ativa".
*/
public List<Locacao> listarAtivas() {
    return listarPorStatus("ativa");
}

/*
Método para listar locações atrasadas: status 'ativa' e data fim prevista anterior a hoje.
Para cada resultado, verifica se status é 'ativa' e atualiza para 'atrasada' no banco e no objeto.
Retorna lista atualizada.
*/
public List<Locacao> listarAtrasadas() {
    List<Locacao> lista = new ArrayList<>();
    String sql = "SELECT * FROM locacoes WHERE status = 'ativa' AND data_fim_prevista < CURRENT_DATE " +
                "ORDER BY data_fim_prevista";

    try (Connection conn = ConnectionFactory.getConnection();
         Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery(sql)) {

        while (rs.next()) {
            Locacao loc = criarLocacaoDoResultSet(rs);
            // Atualiza status para atrasada se necessário
            if ("ativa".equals(loc.getStatus())) {
                atualizarStatus(loc.getId(), "atrasada");
                loc.setStatus("atrasada");
            }
            lista.add(loc);
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return lista;
}

/*
Busca locação pelo ID.
Prepara query com parâmetro, seta ID, executa e retorna primeiro resultado como objeto Locacao.
Retorna null se não encontrado ou erro.
*/
public Locacao buscarPorId(int id) {
    String sql = "SELECT * FROM locacoes WHERE id = ?";
    try (Connection conn = ConnectionFactory.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setInt(1, id);
        ResultSet rs = stmt.executeQuery();

        if (rs.next()) {
            return criarLocacaoDoResultSet(rs);
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return null;
}

/*
Lista locações filtrando por cliente.
Prepara query com parâmetro cliente_id, executa e popula lista com resultados.
*/
public List<Locacao> listarPorCliente(int clienteId) {
    List<Locacao> lista = new ArrayList<>();
    String sql = "SELECT * FROM locacoes WHERE cliente_id = ? ORDER BY data_inicio DESC";

    try (Connection conn = ConnectionFactory.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setInt(1, clienteId);
        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            Locacao loc = criarLocacaoDoResultSet(rs);
            lista.add(loc);
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return lista;
}

/*
Lista locações filtrando por veículo.
Prepara query com parâmetro veiculo_id, executa e popula lista com resultados.
*/
public List<Locacao> listarPorVeiculo(int veiculoId) {
    List<Locacao> lista = new ArrayList<>();
    String sql = "SELECT * FROM locacoes WHERE veiculo_id = ? ORDER BY data_inicio DESC";

    try (Connection conn = ConnectionFactory.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setInt(1, veiculoId);
        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            Locacao loc = criarLocacaoDoResultSet(rs);
            lista.add(loc);
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return lista;
}

/*
Finaliza uma locação: seta data fim real como hoje, km final, valor total final, multa, status devolvida,
observações da devolução e data da última alteração.
*/
public void finalizarLocacao(int id, BigDecimal kmFinal, BigDecimal valorTotalFinal, 
                            BigDecimal multa, String observacoesDevolucao) {
    String sql = "UPDATE locacoes SET data_fim_real = CURRENT_DATE, km_final = ?, " +
                "valor_total_final = ?, multa = ?, status = 'devolvida', " +
                "observacoes_devolucao = ?, data_ultima_alteracao = CURRENT_TIMESTAMP WHERE id = ?";

    try (Connection conn = ConnectionFactory.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setBigDecimal(1, kmFinal);
        stmt.setBigDecimal(2, valorTotalFinal);
        // Se multa for null, seta zero para evitar erros
        stmt.setBigDecimal(3, multa != null ? multa : BigDecimal.ZERO);
        stmt.setString(4, observacoesDevolucao);
        stmt.setInt(5, id);
        stmt.executeUpdate();
    } catch (SQLException e) {
        e.printStackTrace();
    }
}

/*
Atualiza apenas o status da locação e a data da última alteração.
*/
public void atualizarStatus(int id, String status) {
    String sql = "UPDATE locacoes SET status = ?, data_ultima_alteracao = CURRENT_TIMESTAMP WHERE id = ?";
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
Atualiza todos os dados da locação no banco, usando os valores do objeto Locacao.
Faz tratamento para valores nulos e datas, atualiza data da última alteração.
*/
public void atualizar(Locacao loc) {
    String sql = "UPDATE locacoes SET cliente_id = ?, veiculo_id = ?, usuario_id = ?, " +
                "data_inicio = ?, data_fim_prevista = ?, data_fim_real = ?, km_inicial = ?, " +
                "km_final = ?, valor_diaria = ?, dias_previstos = ?, valor_total_previsto = ?, " +
                "valor_total_final = ?, multa = ?, status = ?, observacoes_retirada = ?, " +
                "observacoes_devolucao = ?, data_ultima_alteracao = CURRENT_TIMESTAMP WHERE id = ?";

    try (Connection conn = ConnectionFactory.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setInt(1, loc.getClienteId());
        stmt.setInt(2, loc.getVeiculoId());

        if (loc.getUsuarioId() > 0) {
            stmt.setInt(3, loc.getUsuarioId());
        } else {
            stmt.setNull(3, Types.INTEGER);
        }

        stmt.setDate(4, Date.valueOf(loc.getDataInicio()));
        stmt.setDate(5, Date.valueOf(loc.getDataFimPrevista()));

        if (loc.getDataFimReal() != null) {
            stmt.setDate(6, Date.valueOf(loc.getDataFimReal()));
        } else {
            stmt.setNull(6, Types.DATE);
        }

        stmt.setBigDecimal(7, loc.getKmInicial());
        stmt.setBigDecimal(8, loc.getKmFinal());
        stmt.setBigDecimal(9, loc.getValorDiaria());
        stmt.setInt(10, loc.getDiasPrevistos());
        stmt.setBigDecimal(11, loc.getValorTotalPrevisto());
        stmt.setBigDecimal(12, loc.getValorTotalFinal());
        stmt.setBigDecimal(13, loc.getMulta());
        stmt.setString(14, loc.getStatus());
        stmt.setString(15, loc.getObservacoesRetirada());
        stmt.setString(16, loc.getObservacoesDevolucao());
        stmt.setInt(17, loc.getId());

        stmt.executeUpdate();
    } catch (SQLException e) {
        e.printStackTrace();
    }
}

/*
Remove uma locação do banco pelo ID.
Cuidado ao usar, pois a exclusão é definitiva.
*/
public void remover(int id) {
    String sql = "DELETE FROM locacoes WHERE id = ?";
    try (Connection conn = ConnectionFactory.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setInt(1, id);
        stmt.executeUpdate();
    } catch (SQLException e) {
        e.printStackTrace();
    }
}

/*
Método auxiliar privado que cria um objeto Locacao a partir dos dados do ResultSet.
Extrai cada campo do banco, trata campos nulos, converte tipos SQL para Java.
*/
private Locacao criarLocacaoDoResultSet(ResultSet rs) throws SQLException {
    Locacao loc = new Locacao();
    loc.setId(rs.getInt("id"));
    loc.setClienteId(rs.getInt("cliente_id"));
    loc.setVeiculoId(rs.getInt("veiculo_id"));
    loc.setUsuarioId(rs.getInt("usuario_id"));

    loc.setDataInicio(rs.getDate("data_inicio").toLocalDate());
    loc.setDataFimPrevista(rs.getDate("data_fim_prevista").toLocalDate());

    Date dataFimReal = rs.getDate("data_fim_real");
    if (dataFimReal != null) {
        loc.setDataFimReal(dataFimReal.toLocalDate());
    }

    loc.setKmInicial(rs.getBigDecimal("km_inicial"));
    loc.setKmFinal(rs.getBigDecimal("km_final"));
    loc.setValorDiaria(rs.getBigDecimal("valor_diaria"));
    loc.setDiasPrevistos(rs.getInt("dias_previstos"));
    loc.setValorTotalPrevisto(rs.getBigDecimal("valor_total_previsto"));
    loc.setValorTotalFinal(rs.getBigDecimal("valor_total_final"));
    loc.setMulta(rs.getBigDecimal("multa"));
    loc.setStatus(rs.getString("status"));
    loc.setObservacoesRetirada(rs.getString("observacoes_retirada"));
    loc.setObservacoesDevolucao(rs.getString("observacoes_devolucao"));

    Timestamp ultimaAlteracao = rs.getTimestamp("data_ultima_alteracao");
    if (ultimaAlteracao != null) {
        loc.setDataUltimaAlteracao(ultimaAlteracao.toLocalDateTime());
    }

    return loc;
}
}
