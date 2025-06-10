package model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Modelo Locacao revisado para corresponder ao esquema SQL
 * @author lucas
 */
public class Locacao {
    // Declaração dos atributos da classe
    private int id;
    private int clienteId;
    private int veiculoId;
    private int usuarioId;
    private LocalDate dataInicio;
    private LocalDate dataFimPrevista;
    private LocalDate dataFimReal;
    private BigDecimal kmInicial;
    private BigDecimal kmFinal;
    private BigDecimal valorDiaria;
    private int diasPrevistos;
    private BigDecimal valorTotalPrevisto;
    private BigDecimal valorTotalFinal;
    private BigDecimal multa;
    private String status;
    private String observacoesRetirada;
    private String observacoesDevolucao;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataUltimaAlteracao;

    // Constructors
    public Locacao() {}

    public Locacao(int clienteId, int veiculoId, BigDecimal valorDiaria, int diasPrevistos) {
        this.clienteId = clienteId;
        this.veiculoId = veiculoId;
        this.valorDiaria = valorDiaria;
        this.diasPrevistos = diasPrevistos;
        this.dataInicio = LocalDate.now();
        this.dataFimPrevista = dataInicio.plusDays(diasPrevistos);
        this.valorTotalPrevisto = valorDiaria.multiply(BigDecimal.valueOf(diasPrevistos));
        this.status = "ativa";
        this.multa = BigDecimal.ZERO;
    }

    public Locacao(double valorDiaria, int numeroDias, double kmRodados, double valorPorKm) {
        this.valorDiaria = BigDecimal.valueOf(valorDiaria);
        this.diasPrevistos = numeroDias;
        this.dataInicio = LocalDate.now();
        this.dataFimPrevista = dataInicio.plusDays(numeroDias);
        this.valorTotalPrevisto = this.valorDiaria.multiply(BigDecimal.valueOf(numeroDias));
        this.status = "ativa";
        this.multa = BigDecimal.ZERO;
    }

    // Getters e Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getClienteId() { return clienteId; }
    public void setClienteId(int clienteId) { this.clienteId = clienteId; }

    public int getVeiculoId() { return veiculoId; }
    public void setVeiculoId(int veiculoId) { this.veiculoId = veiculoId; }

    public int getUsuarioId() { return usuarioId; }
    public void setUsuarioId(int usuarioId) { this.usuarioId = usuarioId; }

    public LocalDate getDataInicio() { return dataInicio; }
    public void setDataInicio(LocalDate dataInicio) { this.dataInicio = dataInicio; }

    public LocalDate getDataFimPrevista() { return dataFimPrevista; }
    public void setDataFimPrevista(LocalDate dataFimPrevista) { this.dataFimPrevista = dataFimPrevista; }

    public LocalDate getDataFim() { return dataFimPrevista; }
    public void setDataFim(LocalDate dataFim) { this.dataFimPrevista = dataFim; }

    public LocalDate getDataFimReal() { return dataFimReal; }
    public void setDataFimReal(LocalDate dataFimReal) { this.dataFimReal = dataFimReal; }

    public BigDecimal getKmInicial() { return kmInicial; }
    public void setKmInicial(BigDecimal kmInicial) { this.kmInicial = kmInicial; }

    public BigDecimal getKmFinal() { return kmFinal; }
    public void setKmFinal(BigDecimal kmFinal) { this.kmFinal = kmFinal; }

    public BigDecimal getValorDiaria() { return valorDiaria; }
    public void setValorDiaria(BigDecimal valorDiaria) { this.valorDiaria = valorDiaria; }

    public int getDiasPrevistos() { return diasPrevistos; }
    public void setDiasPrevistos(int diasPrevistos) { this.diasPrevistos = diasPrevistos; }

    // Mantém compatibilidade com código existente
    public int getNumeroDias() { return diasPrevistos; }

    public BigDecimal getValorTotalPrevisto() { return valorTotalPrevisto; }
    public void setValorTotalPrevisto(BigDecimal valorTotalPrevisto) { this.valorTotalPrevisto = valorTotalPrevisto; }

    public BigDecimal getValorTotalFinal() { return valorTotalFinal; }
    public void setValorTotalFinal(BigDecimal valorTotalFinal) { this.valorTotalFinal = valorTotalFinal; }

    public BigDecimal getMulta() { return multa; }
    public void setMulta(BigDecimal multa) { this.multa = multa; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getObservacoesRetirada() { return observacoesRetirada; }
    public void setObservacoesRetirada(String observacoesRetirada) { this.observacoesRetirada = observacoesRetirada; }

    public String getObservacoesDevolucao() { return observacoesDevolucao; }
    public void setObservacoesDevolucao(String observacoesDevolucao) { this.observacoesDevolucao = observacoesDevolucao; }

    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(LocalDateTime dataCriacao) { this.dataCriacao = dataCriacao; }

    public LocalDateTime getDataUltimaAlteracao() { return dataUltimaAlteracao; }
    public void setDataUltimaAlteracao(LocalDateTime dataUltimaAlteracao) { this.dataUltimaAlteracao = dataUltimaAlteracao; }

    public double getKmRodados() { 
        if (kmFinal != null && kmInicial != null) {
            /*
            kmFinal.subtract(kmInicial) calcula a diferença entre os dois valores. Isso só é possível se kmFinal e kmInicial forem objetos de tipo BigDecimal.
            O método .doubleValue() converte o BigDecimal resultante para um valor do tipo double, que é o tipo de retorno do método.
            */
            return kmFinal.subtract(kmInicial).doubleValue();
        }
        return 0.0;
    }

    public double getValorPorKm() { 
        return 0.5;
    }
}
