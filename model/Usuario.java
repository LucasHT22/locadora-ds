package model;

import java.time.LocalDateTime;

/**
 * Modelo Usuario revisado para corresponder ao esquema SQL
 * @author lucas
 */
public class Usuario {
    // Declaração dos atributos da classe
    private int id;
    private String nome;
    private String login;
    private String senha;
    private String tipoUsuario;
    private boolean ativo;
    private LocalDateTime dataCriacao;
    private LocalDateTime ultimaAlteracao;

    // Constructors
    public Usuario() {}

    public Usuario(String nome, String login, String senha, String tipoUsuario) {
        this.nome = nome;
        this.login = login;
        this.senha = senha;
        this.tipoUsuario = tipoUsuario;
        this.ativo = true; // Padrão
    }

    // Getters e Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getLogin() { return login; }
    public void setLogin(String login) { this.login = login; }

    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }

    public String getTipoUsuario() { return tipoUsuario; }
    public void setTipoUsuario(String tipoUsuario) { this.tipoUsuario = tipoUsuario; }

    public boolean isAtivo() { return ativo; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }

    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(LocalDateTime dataCriacao) { this.dataCriacao = dataCriacao; }

    public LocalDateTime getUltimaAlteracao() { return ultimaAlteracao; }
    public void setUltimaAlteracao(LocalDateTime ultimaAlteracao) { this.ultimaAlteracao = ultimaAlteracao; }
}
