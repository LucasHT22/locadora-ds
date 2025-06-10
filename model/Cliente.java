package model;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Modelo Cliente revisado para corresponder ao esquema SQL
 * @author lucas
 */
public class Cliente {
    // Declaração dos atributos da classe
    private int id;
    private String nome;
    private String cpf;
    private String telefone;
    private String email;
    private String endereco;
    private String cnh;
    private LocalDate dataNascimento;
    private boolean ativo;
    private LocalDateTime dataCadastro;
    private String observacoes;

    // Constructors
    public Cliente() {}

    public Cliente(String nome, String cpf, String telefone) {
        this.nome = nome;
        this.cpf = cpf;
        this.telefone = telefone;
        this.ativo = true; // Padrão
    }

    public Cliente(String nome, String cpf, String telefone, String email, String endereco, String cnh, LocalDate dataNascimento) {
        this.nome = nome;
        this.cpf = cpf;
        this.telefone = telefone;
        this.email = email;
        this.endereco = endereco;
        this.cnh = cnh;
        this.dataNascimento = dataNascimento;
        this.ativo = true;
    }

    // Getters e Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }

    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getEndereco() { return endereco; }
    public void setEndereco(String endereco) { this.endereco = endereco; }

    public String getCnh() { return cnh; }
    public void setCnh(String cnh) { this.cnh = cnh; }

    public LocalDate getDataNascimento() { return dataNascimento; }
    public void setDataNascimento(LocalDate dataNascimento) { this.dataNascimento = dataNascimento; }

    public boolean isAtivo() { return ativo; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }

    public LocalDateTime getDataCadastro() { return dataCadastro; }
    public void setDataCadastro(LocalDateTime dataCadastro) { this.dataCadastro = dataCadastro; }

    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }
}
