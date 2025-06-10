package model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Modelo Veiculo revisado para corresponder ao esquema SQL
 * @author lucas
 */
public class Veiculo {
    // Declaração dos atributos da classe
    private int id;
    private String modelo;
    private String marca;
    private int ano;
    private String placa;
    private String cor;
    private String combustivel;
    private BigDecimal kmAtual;
    private BigDecimal valorDiaria;
    private String status;
    private String observacoes;
    private LocalDateTime dataCadastro;

    // Constructors
    public Veiculo() {}

    public Veiculo(String modelo, String marca, int ano) {
        this.modelo = modelo;
        this.marca = marca;
        this.ano = ano;
        this.kmAtual = BigDecimal.ZERO;
        this.status = "disponivel"; // Padrão
    }

    public Veiculo(String modelo, String marca, int ano, String placa, BigDecimal valorDiaria) {
        this.modelo = modelo;
        this.marca = marca;
        this.ano = ano;
        this.placa = placa;
        this.valorDiaria = valorDiaria;
        this.kmAtual = BigDecimal.ZERO;
        this.status = "disponivel";
    }

    // Getters e Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getModelo() { return modelo; }
    public void setModelo(String modelo) { this.modelo = modelo; }

    public String getMarca() { return marca; }
    public void setMarca(String marca) { this.marca = marca; }

    public int getAno() { return ano; }
    public void setAno(int ano) { this.ano = ano; }

    public int getAnoFabricacao() { return ano; }
    public void setAnoFabricacao(int ano) { this.ano = ano; }

    public String getPlaca() { return placa; }
    public void setPlaca(String placa) { this.placa = placa; }

    public String getCor() { return cor; }
    public void setCor(String cor) { this.cor = cor; }

    public String getCombustivel() { return combustivel; }
    public void setCombustivel(String combustivel) { this.combustivel = combustivel; }

    public BigDecimal getKmAtual() { return kmAtual; }
    public void setKmAtual(BigDecimal kmAtual) { this.kmAtual = kmAtual; }

    public BigDecimal getValorDiaria() { return valorDiaria; }
    public void setValorDiaria(BigDecimal valorDiaria) { this.valorDiaria = valorDiaria; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }

    public LocalDateTime getDataCadastro() { return dataCadastro; }
    public void setDataCadastro(LocalDateTime dataCadastro) { this.dataCadastro = dataCadastro; }
}
