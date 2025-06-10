package view;

import model.Locacao;
import model.Veiculo;
import dao.VeiculoDAO;
import java.awt.*;
import java.awt.print.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.math.BigDecimal;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Classe responsável pela impressão da nota fiscal de devolução
 * @author lucas
 */
public class NotaFiscalDevolucao implements Printable {
    
    private static final Logger LOGGER = Logger.getLogger(NotaFiscalDevolucao.class.getName());
    private Locacao locacao;
    private String clienteInfo;
    private String veiculoInfo;
    private VeiculoDAO veiculoDAO;

    public NotaFiscalDevolucao(Locacao locacao, String clienteInfo) {
        this.locacao = locacao;
        this.clienteInfo = clienteInfo;
        this.veiculoDAO = new VeiculoDAO();
        this.veiculoInfo = obterInfoVeiculo();
    }
    
    private String obterInfoVeiculo() {
        try {
            Veiculo veiculo = veiculoDAO.buscarPorId(locacao.getVeiculoId());
            if (veiculo != null) {
                return veiculo.getMarca() + " " + veiculo.getModelo() + " - " + veiculo.getPlaca();
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Erro ao buscar dados do veículo para impressão", e);
        }
        return "Veículo ID: " + locacao.getVeiculoId();
    }

    @Override
    public int print(Graphics g, PageFormat pf, int pageIndex) throws PrinterException {
        if (pageIndex > 0) return NO_SUCH_PAGE;

        Graphics2D g2d = (Graphics2D) g;
        g2d.translate(pf.getImageableX(), pf.getImageableY());

        // Configurações de fonte
        Font tituloFont = new Font("Arial", Font.BOLD, 14);
        Font normalFont = new Font("Arial", Font.PLAIN, 10);
        Font totalFont = new Font("Arial", Font.BOLD, 12);
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        
        int y = 30;
        int leftMargin = 20;
        
        // Cabeçalho
        g.setFont(tituloFont);
        g.drawString("========= NOTA FISCAL - DEVOLUÇÃO DE VEÍCULO =========", leftMargin, y);
        y += 25;
        
        g.setFont(normalFont);
        g.drawString("Data da Impressão: " + java.time.LocalDate.now().format(formatter), leftMargin, y);
        y += 20;
        
        // Dados da Locação
        g.drawString("═══════════════ DADOS DA LOCAÇÃO ═══════════════", leftMargin, y);
        y += 15;
        
        g.drawString("ID da Locação: " + locacao.getId(), leftMargin, y);
        y += 12;
        
        g.drawString("Cliente: " + clienteInfo, leftMargin, y);
        y += 12;
        
        g.drawString("Veículo: " + veiculoInfo, leftMargin, y);
        y += 12;
        
        g.drawString("Data de Início: " + locacao.getDataInicio().format(formatter), leftMargin, y);
        y += 12;
        
        g.drawString("Data Fim Prevista: " + locacao.getDataFimPrevista().format(formatter), leftMargin, y);
        y += 12;
        
        g.drawString("Data de Devolução: " + 
                    (locacao.getDataFimReal() != null ? locacao.getDataFimReal().format(formatter) : "N/A"), 
                    leftMargin, y);
        y += 20;
        
        // Dados de Quilometragem
        g.drawString("════════════════ QUILOMETRAGEM ════════════════", leftMargin, y);
        y += 15;
        
        BigDecimal kmInicial = locacao.getKmInicial() != null ? locacao.getKmInicial() : BigDecimal.ZERO;
        BigDecimal kmFinal = locacao.getKmFinal() != null ? locacao.getKmFinal() : BigDecimal.ZERO;
        BigDecimal kmRodados = kmFinal.subtract(kmInicial);
        
        g.drawString("Km Inicial: " + String.format("%.0f km", kmInicial.doubleValue()), leftMargin, y);
        y += 12;
        
        g.drawString("Km Final: " + String.format("%.0f km", kmFinal.doubleValue()), leftMargin, y);
        y += 12;
        
        g.drawString("Km Rodados: " + String.format("%.0f km", kmRodados.doubleValue()), leftMargin, y);
        y += 20;
        
        // Cálculos de Valores
        g.drawString("═══════════════ VALORES CALCULADOS ═══════════════", leftMargin, y);
        y += 15;
        
        g.drawString("Valor da Diária: R$ " + String.format("%.2f", locacao.getValorDiaria()), leftMargin, y);
        y += 12;
        
        g.drawString("Dias Previstos: " + locacao.getDiasPrevistos() + " dias", leftMargin, y);
        y += 12;
        
        // Calcular dias reais
        long diasReais = 0;
        if (locacao.getDataFimReal() != null) {
            diasReais = ChronoUnit.DAYS.between(locacao.getDataInicio(), locacao.getDataFimReal());
            if (diasReais == 0) diasReais = 1; // Mínimo 1 dia
        }
        
        g.drawString("Dias Utilizados: " + diasReais + " dias", leftMargin, y);
        y += 12;
        
        g.drawString("Valor Previsto: R$ " + String.format("%.2f", locacao.getValorTotalPrevisto()), leftMargin, y);
        y += 12;
        
        // Multa por atraso (se houver)
        BigDecimal multa = locacao.getMulta() != null ? locacao.getMulta() : BigDecimal.ZERO;
        if (multa.compareTo(BigDecimal.ZERO) > 0) {
            g.setFont(new Font("Arial", Font.BOLD, 10));
            g.setColor(Color.RED);
            g.drawString("Multa por Atraso: R$ " + String.format("%.2f", multa), leftMargin, y);
            g.setColor(Color.BLACK);
            g.setFont(normalFont);
            y += 12;
        }
        
        y += 8;
        
        // Valor Total
        g.drawString("────────────────────────────────────────────────", leftMargin, y);
        y += 15;
        
        g.setFont(totalFont);
        BigDecimal valorTotal = locacao.getValorTotalFinal() != null ? 
                               locacao.getValorTotalFinal() : locacao.getValorTotalPrevisto();
        g.drawString("VALOR TOTAL: R$ " + String.format("%.2f", valorTotal), leftMargin, y);
        y += 20;
        
        // Observações (se houver)
        if (locacao.getObservacoesDevolucao() != null && !locacao.getObservacoesDevolucao().trim().isEmpty()) {
            g.setFont(normalFont);
            g.drawString("═════════════════ OBSERVAÇÕES ═════════════════", leftMargin, y);
            y += 15;
            
            // Quebrar texto das observações em linhas
            String[] linhas = quebrarTexto(locacao.getObservacoesDevolucao(), 60);
            for (String linha : linhas) {
                g.drawString(linha, leftMargin, y);
                y += 12;
            }
            y += 10;
        }
        
        // Rodapé
        g.setFont(new Font("Arial", Font.ITALIC, 9));
        y += 20;
        g.drawString("────────────────────────────────────────────────", leftMargin, y);
        y += 12;
        g.drawString("Obrigado pela preferência!", leftMargin + 150, y);
        y += 10;
        g.drawString("Sistema de Locação de Veículos", leftMargin + 140, y);

        return PAGE_EXISTS;
    }
    
    /**
     * Quebra um texto em linhas com tamanho máximo especificado
     */
    private String[] quebrarTexto(String texto, int maxCaracteres) {
        if (texto.length() <= maxCaracteres) {
            return new String[]{texto};
        }
        
        java.util.List<String> linhas = new java.util.ArrayList<>();
        String[] palavras = texto.split(" ");
        StringBuilder linhaAtual = new StringBuilder();
        
        for (String palavra : palavras) {
            if (linhaAtual.length() + palavra.length() + 1 <= maxCaracteres) {
                if (linhaAtual.length() > 0) {
                    linhaAtual.append(" ");
                }
                linhaAtual.append(palavra);
            } else {
                if (linhaAtual.length() > 0) {
                    linhas.add(linhaAtual.toString());
                    linhaAtual = new StringBuilder(palavra);
                } else {
                    linhas.add(palavra);
                }
            }
        }
        
        if (linhaAtual.length() > 0) {
            linhas.add(linhaAtual.toString());
        }
        
        return linhas.toArray(new String[0]);
    }

    /**
     * Método para executar a impressão
     */
    public void imprimir() {
        try {
            PrinterJob job = PrinterJob.getPrinterJob();
            job.setPrintable(this);

            // Configurar formato da página
            PageFormat pageFormat = job.defaultPage();
            Paper paper = pageFormat.getPaper();
            
            // Definir margens (em pontos - 72 pontos = 1 polegada)
            double margin = 36; // 0.5 polegada de margem
            paper.setImageableArea(margin, margin, 
                                 paper.getWidth() - 2 * margin, 
                                 paper.getHeight() - 2 * margin);
            pageFormat.setPaper(paper);
            
            job.setPrintable(this, pageFormat);

            // Mostrar diálogo de impressão
            if (job.printDialog()) {
                job.print();
                LOGGER.info("Nota fiscal de devolução impressa com sucesso");
            }
            
        } catch (PrinterException e) {
            LOGGER.log(Level.SEVERE, "Erro ao imprimir nota fiscal de devolução", e);
            throw new RuntimeException("Erro ao imprimir: " + e.getMessage(), e);
        }
    }
    
    public void visualizar() {
        try {
            PrinterJob job = PrinterJob.getPrinterJob();
            job.setPrintable(this);
            job.printDialog();
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro ao visualizar nota fiscal", e);
        }
    }
}
