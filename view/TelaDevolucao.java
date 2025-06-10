package view;

import dao.LocacaoDAO;
import dao.VeiculoDAO;
import model.Locacao;
import model.Veiculo;
import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Tela para devolução de veículos
 * @author lucas
 */
public class TelaDevolucao extends JFrame {
    
    private static final Logger LOGGER = Logger.getLogger(TelaDevolucao.class.getName());
    
    private JTextField txtIdLocacao;
    private JTextField txtKmFinal;
    private JTextArea txtObservacoes;
    private JButton btnBuscarLocacao;
    private JButton btnConfirmar;
    private JButton btnCancelar;
    private JButton btnImprimir;
    
    // Campos informativos
    private JLabel lblCliente;
    private JLabel lblVeiculo;
    private JLabel lblDataInicio;
    private JLabel lblDataFimPrevista;
    private JLabel lblKmInicial;
    private JLabel lblValorDiaria;
    private JLabel lblDiasPrevistos;
    private JLabel lblValorPrevisto;
    private JLabel lblMulta;
    private JLabel lblValorTotal;
    
    private LocacaoDAO locacaoDAO;
    private VeiculoDAO veiculoDAO;
    private Locacao locacaoAtual;
    private String clienteInfoAtual;
    private boolean devolucaoRealizada = false;

    public TelaDevolucao() {
        this.locacaoDAO = new LocacaoDAO();
        this.veiculoDAO = new VeiculoDAO();
        inicializarComponentes();
    }

    private void inicializarComponentes() {
        setTitle("Devolução de Veículo");
        setSize(600, 750);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel painelPrincipal = new JPanel(new BorderLayout(10, 10));
        painelPrincipal.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Painel de busca da locação
        JPanel painelBusca = new JPanel(new FlowLayout(FlowLayout.LEFT));
        painelBusca.setBorder(BorderFactory.createTitledBorder("Buscar Locação"));
        
        painelBusca.add(new JLabel("ID da Locação:"));
        txtIdLocacao = new JTextField(10);
        painelBusca.add(txtIdLocacao);
        
        btnBuscarLocacao = new JButton("Buscar");
        btnBuscarLocacao.addActionListener(e -> buscarLocacao());
        painelBusca.add(btnBuscarLocacao);

        // Painel de informações da locação
        JPanel painelInfo = new JPanel(new GridBagLayout());
        painelInfo.setBorder(BorderFactory.createTitledBorder("Informações da Locação"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        int row = 0;
        
        gbc.gridx = 0; gbc.gridy = row;
        painelInfo.add(new JLabel("Cliente:"), gbc);
        gbc.gridx = 1;
        lblCliente = new JLabel("-");
        painelInfo.add(lblCliente, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row;
        painelInfo.add(new JLabel("Veículo:"), gbc);
        gbc.gridx = 1;
        lblVeiculo = new JLabel("-");
        painelInfo.add(lblVeiculo, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row;
        painelInfo.add(new JLabel("Data Início:"), gbc);
        gbc.gridx = 1;
        lblDataInicio = new JLabel("-");
        painelInfo.add(lblDataInicio, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row;
        painelInfo.add(new JLabel("Data Fim Prevista:"), gbc);
        gbc.gridx = 1;
        lblDataFimPrevista = new JLabel("-");
        painelInfo.add(lblDataFimPrevista, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row;
        painelInfo.add(new JLabel("Km Inicial:"), gbc);
        gbc.gridx = 1;
        lblKmInicial = new JLabel("-");
        painelInfo.add(lblKmInicial, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row;
        painelInfo.add(new JLabel("Valor Diária:"), gbc);
        gbc.gridx = 1;
        lblValorDiaria = new JLabel("-");
        painelInfo.add(lblValorDiaria, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row;
        painelInfo.add(new JLabel("Dias Previstos:"), gbc);
        gbc.gridx = 1;
        lblDiasPrevistos = new JLabel("-");
        painelInfo.add(lblDiasPrevistos, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row;
        painelInfo.add(new JLabel("Valor Previsto:"), gbc);
        gbc.gridx = 1;
        lblValorPrevisto = new JLabel("-");
        painelInfo.add(lblValorPrevisto, gbc);

        // Painel de devolução
        JPanel painelDevolucao = new JPanel(new GridBagLayout());
        painelDevolucao.setBorder(BorderFactory.createTitledBorder("Dados da Devolução"));
        gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.EAST;
        painelDevolucao.add(new JLabel("Km Final:"), gbc);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        txtKmFinal = new JTextField(15);
        txtKmFinal.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent evt) {
                calcularValores();
            }
        });
        painelDevolucao.add(txtKmFinal, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.anchor = GridBagConstraints.NORTHEAST;
        painelDevolucao.add(new JLabel("Observações:"), gbc);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        txtObservacoes = new JTextArea(4, 15);
        txtObservacoes.setBorder(BorderFactory.createLoweredBevelBorder());
        painelDevolucao.add(new JScrollPane(txtObservacoes), gbc);

        // Painel de valores finais
        JPanel painelValores = new JPanel(new GridBagLayout());
        painelValores.setBorder(BorderFactory.createTitledBorder("Valores Finais"));
        gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0;
        painelValores.add(new JLabel("Multa por Atraso:"), gbc);
        gbc.gridx = 1;
        lblMulta = new JLabel("R$ 0,00");
        lblMulta.setForeground(Color.RED);
        painelValores.add(lblMulta, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        painelValores.add(new JLabel("Valor Total:"), gbc);
        gbc.gridx = 1;
        lblValorTotal = new JLabel("R$ 0,00");
        lblValorTotal.setFont(lblValorTotal.getFont().deriveFont(Font.BOLD, 14f));
        painelValores.add(lblValorTotal, gbc);

        // Painel de botões
        JPanel painelBotoes = new JPanel(new FlowLayout());
        btnConfirmar = new JButton("Confirmar Devolução");
        btnImprimir = new JButton("Imprimir Nota Fiscal");
        btnCancelar = new JButton("Cancelar");

        btnConfirmar.addActionListener(e -> confirmarDevolucao());
        btnImprimir.addActionListener(e -> imprimirNotaFiscal());
        btnCancelar.addActionListener(e -> dispose());
        
        btnConfirmar.setEnabled(false);
        btnImprimir.setEnabled(false);

        painelBotoes.add(btnConfirmar);
        painelBotoes.add(btnImprimir);
        painelBotoes.add(btnCancelar);

        // Layout principal
        JPanel painelSuperior = new JPanel(new BorderLayout(5, 5));
        painelSuperior.add(painelBusca, BorderLayout.NORTH);
        painelSuperior.add(painelInfo, BorderLayout.CENTER);

        JPanel painelCentral = new JPanel(new BorderLayout(5, 5));
        painelCentral.add(painelDevolucao, BorderLayout.NORTH);
        painelCentral.add(painelValores, BorderLayout.CENTER);

        painelPrincipal.add(painelSuperior, BorderLayout.NORTH);
        painelPrincipal.add(painelCentral, BorderLayout.CENTER);
        painelPrincipal.add(painelBotoes, BorderLayout.SOUTH);

        add(painelPrincipal);
        
        // Foco inicial
        txtIdLocacao.requestFocus();
    }

    private void buscarLocacao() {
        String idTexto = txtIdLocacao.getText().trim();
        
        if (idTexto.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Por favor, informe o ID da locação.", 
                "Campo obrigatório", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int id = Integer.parseInt(idTexto); 
            locacaoAtual = locacaoDAO.buscarPorId(id);
            
            if (locacaoAtual == null) {
                JOptionPane.showMessageDialog(this, 
                    "Locação não encontrada.", 
                    "Locação não encontrada", 
                    JOptionPane.WARNING_MESSAGE);
                limparCampos();
                return;
            }

            if (!"ativa".equals(locacaoAtual.getStatus())) {
                JOptionPane.showMessageDialog(this, 
                    "Esta locação não está ativa ou já foi devolvida.", 
                    "Status inválido", 
                    JOptionPane.WARNING_MESSAGE);
                limparCampos();
                return;
            }

            preencherInformacoes();
            btnConfirmar.setEnabled(true);
            txtKmFinal.requestFocus();

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, 
                "ID da locação deve ser um número válido.", 
                "Erro de formato", 
                JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro ao buscar locação", e);
            JOptionPane.showMessageDialog(this, 
                "Erro ao buscar locação: " + e.getMessage(), 
                "Erro", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void preencherInformacoes() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        
        // Buscar dados do cliente e veículo para exibir informações mais detalhadas
        clienteInfoAtual = "Cliente ID: " + locacaoAtual.getClienteId();
        String veiculoInfo = "Veículo ID: " + locacaoAtual.getVeiculoId();
        
        // Tentar buscar informações do veículo
        try {
            Veiculo veiculo = veiculoDAO.buscarPorId(locacaoAtual.getVeiculoId());
            if (veiculo != null) {
                veiculoInfo = veiculo.getMarca() + " " + veiculo.getModelo() + 
                            " - " + veiculo.getPlaca();
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Erro ao buscar dados do veículo", e);
        }
        
        lblCliente.setText(clienteInfoAtual);
        lblVeiculo.setText(veiculoInfo);
        lblDataInicio.setText(locacaoAtual.getDataInicio().format(formatter));
        lblDataFimPrevista.setText(locacaoAtual.getDataFimPrevista().format(formatter));
        
        // Verificar se kmInicial não é null
        if (locacaoAtual.getKmInicial() != null) {
            lblKmInicial.setText(String.format("%.0f km", locacaoAtual.getKmInicial().doubleValue()));
        } else {
            lblKmInicial.setText("0 km");
        }
        
        lblValorDiaria.setText(String.format("R$ %.2f", locacaoAtual.getValorDiaria()));
        lblDiasPrevistos.setText(locacaoAtual.getDiasPrevistos() + " dias");
        lblValorPrevisto.setText(String.format("R$ %.2f", locacaoAtual.getValorTotalPrevisto()));
        
        calcularValores();
    }

    private void calcularValores() {
        if (locacaoAtual == null) return;
        
        try {
            String kmTexto = txtKmFinal.getText().trim();
            if (kmTexto.isEmpty()) {
                lblMulta.setText("R$ 0,00");
                lblValorTotal.setText(String.format("R$ %.2f", locacaoAtual.getValorTotalPrevisto()));
                return;
            }

            BigDecimal kmFinal = new BigDecimal(kmTexto);
            BigDecimal kmInicial = locacaoAtual.getKmInicial() != null ? 
                                  locacaoAtual.getKmInicial() : BigDecimal.ZERO;
            
            if (kmFinal.compareTo(kmInicial) < 0) {
                lblMulta.setText("Km inválido");
                lblValorTotal.setText("Erro");
                return;
            }

            LocalDate hoje = LocalDate.now();
            LocalDate dataFimPrevista = locacaoAtual.getDataFimPrevista();
            
            BigDecimal multa = BigDecimal.ZERO;
            BigDecimal valorTotal = locacaoAtual.getValorTotalPrevisto();

            // Calcular multa por atraso (se houver)
            if (hoje.isAfter(dataFimPrevista)) {
                long diasAtraso = ChronoUnit.DAYS.between(dataFimPrevista, hoje);
                // Multa de 10% do valor da diária por dia de atraso
                BigDecimal multaPorDia = locacaoAtual.getValorDiaria().multiply(new BigDecimal("0.10"));
                multa = multaPorDia.multiply(new BigDecimal(diasAtraso));
                valorTotal = valorTotal.add(multa);
            }

            lblMulta.setText(String.format("R$ %.2f", multa));
            lblValorTotal.setText(String.format("R$ %.2f", valorTotal));

        } catch (NumberFormatException e) {
            lblMulta.setText("Formato inválido");
            lblValorTotal.setText("Erro");
        }
    }

    private void confirmarDevolucao() {
        if (locacaoAtual == null) {
            JOptionPane.showMessageDialog(this, 
                "Nenhuma locação selecionada.", 
                "Erro", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        String kmTexto = txtKmFinal.getText().trim();
        if (kmTexto.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Por favor, informe a quilometragem final.", 
                "Campo obrigatório", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            BigDecimal kmFinal = new BigDecimal(kmTexto);
            BigDecimal kmInicial = locacaoAtual.getKmInicial() != null ? 
                                  locacaoAtual.getKmInicial() : BigDecimal.ZERO;
            
            if (kmFinal.compareTo(kmInicial) < 0) {
                JOptionPane.showMessageDialog(this, 
                    "A quilometragem final não pode ser menor que a inicial.", 
                    "Valor inválido", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            int confirmacao = JOptionPane.showConfirmDialog(this, 
                "Confirmar a devolução do veículo?\n" +
                "Valor total: " + lblValorTotal.getText(), 
                "Confirmar Devolução", 
                JOptionPane.YES_NO_OPTION);

            if (confirmacao == JOptionPane.YES_OPTION) {
                // Atualizar locação
                LocalDate hoje = LocalDate.now();
                locacaoAtual.setDataFimReal(hoje);
                locacaoAtual.setKmFinal(kmFinal);
                locacaoAtual.setObservacoesDevolucao(txtObservacoes.getText());
                locacaoAtual.setStatus("devolvida");

                // Calcular valores finais
                String valorTotalTexto = lblValorTotal.getText().replace("R$ ", "").replace(",", ".");
                String multaTexto = lblMulta.getText().replace("R$ ", "").replace(",", ".");
                
                BigDecimal valorTotal = new BigDecimal(valorTotalTexto);
                BigDecimal multa = new BigDecimal(multaTexto);
                
                locacaoAtual.setValorTotalFinal(valorTotal);
                locacaoAtual.setMulta(multa);

                // Salvar no banco
                locacaoDAO.atualizar(locacaoAtual);

                // Atualizar status do veículo para disponível
                veiculoDAO.atualizarStatus(locacaoAtual.getVeiculoId(), "disponivel");
                
                // Atualizar KM do veículo
                veiculoDAO.atualizarKm(locacaoAtual.getVeiculoId(), kmFinal);

                // Marcar que a devolução foi realizada
                devolucaoRealizada = true;
                
                // Habilitar botão de impressão e desabilitar confirmação
                btnImprimir.setEnabled(true);
                btnConfirmar.setEnabled(false);
                txtKmFinal.setEnabled(false);
                txtObservacoes.setEnabled(false);

                // Mostrar mensagem de sucesso com opção de imprimir
                int opcaoImprimir = JOptionPane.showConfirmDialog(this, 
                    """
                    Devolu\u00e7\u00e3o registrada com sucesso!
                    Valor total: """ + lblValorTotal.getText() + "\n\n" +
                    "Deseja imprimir a nota fiscal agora?", 
                    "Sucesso", 
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.INFORMATION_MESSAGE);
                
                if (opcaoImprimir == JOptionPane.YES_OPTION) {
                    imprimirNotaFiscal();
                }
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, 
                "Quilometragem final deve ser um número válido.", 
                "Erro de validação", 
                JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro ao confirmar devolução", e);
            JOptionPane.showMessageDialog(this, 
                "Erro ao processar devolução: " + e.getMessage(), 
                "Erro", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Método para imprimir a nota fiscal de devolução
     */
    private void imprimirNotaFiscal() {
        if (locacaoAtual == null) {
            JOptionPane.showMessageDialog(this, 
                "Nenhuma locação disponível para impressão.", 
                "Erro", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!devolucaoRealizada) {
            JOptionPane.showMessageDialog(this, 
                "É necessário confirmar a devolução antes de imprimir.", 
                "Devolução não confirmada", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            // Criar e executar a impressão
            NotaFiscalDevolucao notaFiscal = new NotaFiscalDevolucao(locacaoAtual, clienteInfoAtual);
            notaFiscal.imprimir();
            
            JOptionPane.showMessageDialog(this, 
                "Nota fiscal enviada para impressão.", 
                "Impressão", 
                JOptionPane.INFORMATION_MESSAGE);
                
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro ao imprimir nota fiscal", e);
            JOptionPane.showMessageDialog(this, 
                "Erro ao imprimir nota fiscal: " + e.getMessage(), 
                "Erro de Impressão", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limparCampos() {
        locacaoAtual = null;
        clienteInfoAtual = null;
        devolucaoRealizada = false;
        
        lblCliente.setText("-");
        lblVeiculo.setText("-");
        lblDataInicio.setText("-");
        lblDataFimPrevista.setText("-");
        lblKmInicial.setText("-");
        lblValorDiaria.setText("-");
        lblDiasPrevistos.setText("-");
        lblValorPrevisto.setText("-");
        lblMulta.setText("R$ 0,00");
        lblValorTotal.setText("R$ 0,00");
        txtKmFinal.setText("");
        txtObservacoes.setText("");
        
        btnConfirmar.setEnabled(false);
        btnImprimir.setEnabled(false);
        txtKmFinal.setEnabled(true);
        txtObservacoes.setEnabled(true);
    }
}
