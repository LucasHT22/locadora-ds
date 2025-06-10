package dao;

import model.Usuario;
import util.ConnectionFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DATA ACCESS OBJECT para Usuario - Revisado
 * @author lucas
 */
public class UsuarioDAO {
    
    /**
     * Insere um novo funcionário no banco
     */
   // Insere um funcionário com tipo_usuario fixo 'funcionario'
    public void inserirFuncionario(Usuario u) {
        String sql = "INSERT INTO usuarios (nome, login, senha, tipo_usuario) VALUES (?, ?, ?, 'funcionario')";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, u.getNome());
            stmt.setString(2, u.getLogin());
            stmt.setString(3, u.getSenha());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    // Insere um usuário genérico (admin ou funcionário) com tipo e status ativo definidos
    public void inserir(Usuario u) {
        String sql = "INSERT INTO usuarios (nome, login, senha, tipo_usuario, ativo) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, u.getNome());
            stmt.setString(2, u.getLogin());
            stmt.setString(3, u.getSenha());
            stmt.setString(4, u.getTipoUsuario());
            stmt.setBoolean(5, u.isAtivo());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    // Autentica usuário no sistema verificando login, senha e status ativo
    public Usuario autenticar(String login, String senha) {
        String sql = "SELECT * FROM usuarios WHERE login = ? AND senha = ? AND ativo = true";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, login);
            stmt.setString(2, senha);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Usuario u = new Usuario();
                u.setId(rs.getInt("id"));
                u.setNome(rs.getString("nome"));
                u.setLogin(rs.getString("login"));
                u.setTipoUsuario(rs.getString("tipo_usuario"));
                u.setAtivo(rs.getBoolean("ativo"));

                Timestamp dataCriacao = rs.getTimestamp("data_criacao");
                if (dataCriacao != null) {
                    u.setDataCriacao(dataCriacao.toLocalDateTime());
                }

                Timestamp ultimaAlteracao = rs.getTimestamp("ultima_alteracao");
                if (ultimaAlteracao != null) {
                    u.setUltimaAlteracao(ultimaAlteracao.toLocalDateTime());
                }
                return u;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    // Retorna lista de todos os usuários ativos, ordenados por nome
    public List<Usuario> listarTodos() {
        List<Usuario> lista = new ArrayList<>();
        String sql = "SELECT * FROM usuarios WHERE ativo = true ORDER BY nome";

        try (Connection conn = ConnectionFactory.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Usuario u = new Usuario();
                u.setId(rs.getInt("id"));
                u.setNome(rs.getString("nome"));
                u.setLogin(rs.getString("login"));
                u.setTipoUsuario(rs.getString("tipo_usuario"));
                u.setAtivo(rs.getBoolean("ativo"));

                Timestamp dataCriacao = rs.getTimestamp("data_criacao");
                if (dataCriacao != null) {
                    u.setDataCriacao(dataCriacao.toLocalDateTime());
                }
                lista.add(u);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }
    
    // Busca um usuário pelo ID
    public Usuario buscarPorId(int id) {
        String sql = "SELECT * FROM usuarios WHERE id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                Usuario u = new Usuario();
                u.setId(rs.getInt("id"));
                u.setNome(rs.getString("nome"));
                u.setLogin(rs.getString("login"));
                u.setTipoUsuario(rs.getString("tipo_usuario"));
                u.setAtivo(rs.getBoolean("ativo"));

                Timestamp dataCriacao = rs.getTimestamp("data_criacao");
                if (dataCriacao != null) {
                    u.setDataCriacao(dataCriacao.toLocalDateTime());
                }
                return u;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    // Atualiza os dados de um usuário existente no banco
    public void atualizar(Usuario u) {
        String sql = "UPDATE usuarios SET nome = ?, login = ?, tipo_usuario = ?, ativo = ?, " +
                     "ultima_alteracao = CURRENT_TIMESTAMP WHERE id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, u.getNome());
            stmt.setString(2, u.getLogin());
            stmt.setString(3, u.getTipoUsuario());
            stmt.setBoolean(4, u.isAtivo());
            stmt.setInt(5, u.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    // Desativa um usuário (soft delete) marcando ativo como false e atualizando a data da alteração
    public void desativar(int id) {
        String sql = "UPDATE usuarios SET ativo = false, ultima_alteracao = CURRENT_TIMESTAMP WHERE id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
