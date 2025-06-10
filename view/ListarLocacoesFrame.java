package view;

import dao.LocacaoDAO;
import model.Locacao;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Tela para listar e gerenciar locações
 * @author lucas
 */
public class ListarLocacoesFrame extends JFrame {
    
    private static final Logger LOGGER = Logger.getLogger(ListarLocacoesFrame.class.getName());
    
    private JTable tabelaLocacoes;
    private DefaultTableModel modeloTabela;
    private LocacaoDAO locacaoDAO;
    private JButton btnDetalhes;
    private JButton btnDevolucao;
    private JButton btnAtualizar;
    private JComboBox<String> cmbStatus;
    private JTextField txtBusca;
    private DateTimeFormatter dateFormat;
    
    public ListarLocacoesFrame() {
        this.locacaoDAO = new LocacaoDAO();
        this.dateFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        inicializarComponentes();
        carregarLocacoes();
    }
    
    private void inicializarComponentes() {
        setTitle("Lista de Locações");
        setSize(1200, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        JPanel painelPrincipal = new JPanel(new BorderLayout(10, 10));
        painelPrincipal.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Painel de filtros
        JPanel painelFiltros = new JPanel(new FlowLayout(FlowLayout.LEFT));
        painelFiltros.add(new JLabel("Buscar (Cliente ID):"));
        txtBusca = new JTextField(15);
        painelFiltros.add(txtBusca);
        
        painelFiltros.add(new JLabel("Status:"));
        cmbStatus = new JComboBox<>(new String[]{"Todos", "ativa", "devolvida", "atrasada"});
        painelFiltros.add(cmbStatus);
        
        JButton btnFiltrar = new JButton("Filtrar");
        btnFiltrar.addActionListener(e -> filtrarLocacoes());
        painelFiltros.add(btnFiltrar);
        
        // Configurar tabela
        String[] colunas = {
            "ID", "Cliente ID", "Veículo ID", "Data Início", "Data Fim Prevista", 
            "Data Fim Real", "KM Inicial", "KM Final", "Valor Diária",
            "Dias Previstos", "Valor Total Previsto", "Valor Total Final", "Status"
        };
        
        modeloTabela = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tabelaLocacoes = new JTable(modeloTabela);
        tabelaLocacoes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabelaLocacoes.getTableHeader().setReorderingAllowed(false);
        
        // Configurar largura das colunas
        tabelaLocacoes.getColumnModel().getColumn(0).setPreferredWidth(50);   // ID
        tabelaLocacoes.getColumnModel().getColumn(1).setPreferredWidth(80);   // Cliente ID
        tabelaLocacoes.getColumnModel().getColumn(2).setPreferredWidth(80);   // Veículo ID
        tabelaLocacoes.getColumnModel().getColumn(3).setPreferredWidth(100);  // Data Início
        tabelaLocacoes.getColumnModel().getColumn(4).setPreferredWidth(120);  // Data Fim Prevista
        tabelaLocacoes.getColumnModel().getColumn(5).setPreferredWidth(100);  // Data Fim Real
        tabelaLocacoes.getColumnModel().getColumn(6).setPreferredWidth(80);   // KM Inicial
        tabelaLocacoes.getColumnModel().getColumn(7).setPreferredWidth(80);   // KM Final
        tabelaLocacoes.getColumnModel().getColumn(8).setPreferredWidth(100);  // Valor Diária
        tabelaLocacoes.getColumnModel().getColumn(9).setPreferredWidth(80);   // Dias Previstos
        tabelaLocacoes.getColumnModel().getColumn(10).setPreferredWidth(120); // Valor Total Previsto
        tabelaLocacoes.getColumnModel().getColumn(11).setPreferredWidth(120); // Valor Total Final
        tabelaLocacoes.getColumnModel().getColumn(12).setPreferredWidth(80);  // Status
        
        // Evento de duplo clique
        tabelaLocacoes.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    visualizarDetalhes();
                }
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(tabelaLocacoes);
        
        // Painel de botões
        JPanel painelBotoes = new JPanel(new FlowLayout());
        btnDetalhes = new JButton("Ver Detalhes");
        btnDevolucao = new JButton("Registrar Devolução");
        btnAtualizar = new JButton("Atualizar Lista");
        JButton btnFechar = new JButton("Fechar");
        
        btnDetalhes.addActionListener(e -> visualizarDetalhes());
        btnDevolucao.addActionListener(e -> registrarDevolucao());
        btnAtualizar.addActionListener(e -> carregarLocacoes());
        btnFechar.addActionListener(e -> dispose());
        
        painelBotoes.add(btnDetalhes);
        painelBotoes.add(btnDevolucao);
        painelBotoes.add(btnAtualizar);
        painelBotoes.add(btnFechar);
        
        painelPrincipal.add(painelFiltros, BorderLayout.NORTH);
        painelPrincipal.add(scrollPane, BorderLayout.CENTER);
        painelPrincipal.add(painelBotoes, BorderLayout.SOUTH);
        
        add(painelPrincipal);
        
        // Configurar eventos de seleção
        tabelaLocacoes.getSelectionModel().addListSelectionListener(e -> {
            boolean selecionado = tabelaLocacoes.getSelectedRow() != -1;
            btnDetalhes.setEnabled(selecionado);
            
            // Habilitar devolução apenas para locações ativas
            if (selecionado) {
                String status = (String) modeloTabela.getValueAt(tabelaLocacoes.getSelectedRow(), 12);
                btnDevolucao.setEnabled("ativa".equals(status) || "atrasada".equals(status));
            } else {
                btnDevolucao.setEnabled(false);
            }
        });
        
        // Inicialmente desabilitar botões
        btnDetalhes.setEnabled(false);
        btnDevolucao.setEnabled(false);
    }
    
    private void carregarLocacoes() {
        try {
            modeloTabela.setRowCount(0);
            List<Locacao> locacoes = locacaoDAO.listarTodas();
            
            for (Locacao locacao : locacoes) {
                Object[] linha = {
                    locacao.getId(),
                    locacao.getClienteId(),
                    locacao.getVeiculoId(),
                    locacao.getDataInicio() != null ? locacao.getDataInicio().format(dateFormat) : "N/A",
                    locacao.getDataFimPrevista() != null ? locacao.getDataFimPrevista().format(dateFormat) : "N/A",
                    locacao.getDataFimReal() != null ? locacao.getDataFimReal().format(dateFormat) : "-",
                    locacao.getKmInicial() != null ? String.format("%.0f", locacao.getKmInicial()) : "-",
                    locacao.getKmFinal() != null ? String.format("%.0f", locacao.getKmFinal()) : "-",
                    locacao.getValorDiaria() != null ? String.format("R$ %.2f", locacao.getValorDiaria()) : "-",
                    locacao.getDiasPrevistos(),
                    locacao.getValorTotalPrevisto() != null ? String.format("R$ %.2f", locacao.getValorTotalPrevisto()) : "-",
                    locacao.getValorTotalFinal() != null ? String.format("R$ %.2f", locacao.getValorTotalFinal()) : "-",
                    locacao.getStatus() != null ? locacao.getStatus() : "N/A"
                };
                modeloTabela.addRow(linha);
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro ao carregar locações", e);
            JOptionPane.showMessageDialog(this, 
                "Erro ao carregar a lista de locações: " + e.getMessage(), 
                "Erro", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void filtrarLocacoes() {
        String termo = txtBusca.getText().trim();
        String status = (String) cmbStatus.getSelectedItem();
        
        try {
            modeloTabela.setRowCount(0);
            List<Locacao> locacoes;
            
            if ("Todos".equals(status)) {
                if (termo.isEmpty()) {
                    locacoes = locacaoDAO.listarTodas();
                } else {
                    // Buscar por cliente ID
                    try {
                        int clienteId = Integer.parseInt(termo);
                        locacoes = locacaoDAO.listarPorCliente(clienteId);
                    } catch (NumberFormatException e) {
                        JOptionPane.showMessageDialog(this, 
                            "Digite um ID de cliente válido (número inteiro).", 
                            "Erro na busca", 
                            JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                }
            } else {
                locacoes = locacaoDAO.listarPorStatus(status);
                if (!termo.isEmpty()) {
                    try {
                        int clienteId = Integer.parseInt(termo);
                        locacoes = locacoes.stream()
                            .filter(l -> l.getClienteId() == clienteId)
                            .collect(Collectors.toList());
                    } catch (NumberFormatException e) {
                        JOptionPane.showMessageDialog(this, 
                            "Digite um ID de cliente válido (número inteiro).", 
                            "Erro na busca", 
                            JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                }
            }
            
            for (Locacao locacao : locacoes) {
                Object[] linha = {
                    locacao.getId(),
                    locacao.getClienteId(),
                    locacao.getVeiculoId(),
                    locacao.getDataInicio() != null ? locacao.getDataInicio().format(dateFormat) : "N/A",
                    locacao.getDataFimPrevista() != null ? locacao.getDataFimPrevista().format(dateFormat) : "N/A",
                    locacao.getDataFimReal() != null ? locacao.getDataFimReal().format(dateFormat) : "-",
                    locacao.getKmInicial() != null ? String.format("%.0f", locacao.getKmInicial()) : "-",
                    locacao.getKmFinal() != null ? String.format("%.0f", locacao.getKmFinal()) : "-",
                    locacao.getValorDiaria() != null ? String.format("R$ %.2f", locacao.getValorDiaria()) : "-",
                    locacao.getDiasPrevistos(),
                    locacao.getValorTotalPrevisto() != null ? String.format("R$ %.2f", locacao.getValorTotalPrevisto()) : "-",
                    locacao.getValorTotalFinal() != null ? String.format("R$ %.2f", locacao.getValorTotalFinal()) : "-",
                    locacao.getStatus() != null ? locacao.getStatus() : "N/A"
                };
                modeloTabela.addRow(linha);
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro ao filtrar locações", e);
            JOptionPane.showMessageDialog(this, 
                "Erro ao filtrar locações: " + e.getMessage(), 
                "Erro", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void visualizarDetalhes() {
        int linhaSelecionada = tabelaLocacoes.getSelectedRow();
        if (linhaSelecionada == -1) {
            JOptionPane.showMessageDialog(this, 
                "Selecione uma locação para ver os detalhes.", 
                "Seleção necessária", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        Integer locacaoId = (Integer) modeloTabela.getValueAt(linhaSelecionada, 0);
        try {
            Locacao locacao = locacaoDAO.buscarPorId(locacaoId);
            if (locacao != null) {
                // Criar e exibir tela de detalhes
                /*DetalhesLocacaoFrame detalhesFrame = new DetalhesLocacaoFrame(locacao);
                detalhesFrame.setVisible(true);*/
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Locação não encontrada.", 
                    "Erro", 
                    JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro ao carregar detalhes da locação", e);
            JOptionPane.showMessageDialog(this, 
                "Erro ao carregar detalhes da locação: " + e.getMessage(), 
                "Erro", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void registrarDevolucao() {
        int linhaSelecionada = tabelaLocacoes.getSelectedRow();
        if (linhaSelecionada == -1) {
            JOptionPane.showMessageDialog(this, 
                "Selecione uma locação para registrar a devolução.", 
                "Seleção necessária", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String status = (String) modeloTabela.getValueAt(linhaSelecionada, 12);
        if (!"ativa".equals(status) && !"atrasada".equals(status)) {
            JOptionPane.showMessageDialog(this, 
                "Só é possível registrar devolução para locações ativas ou atrasadas.", 
                "Status inválido", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        Integer locacaoId = (Integer) modeloTabela.getValueAt(linhaSelecionada, 0);
        try {
            Locacao locacao = locacaoDAO.buscarPorId(locacaoId);
            if (locacao != null) {
                // Criar e exibir tela de devolução
                TelaDevolucao devolucaoFrame = new TelaDevolucao();
                devolucaoFrame.setVisible(true);
                
                // Atualizar lista após possível devolução
                carregarLocacoes();
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Locação não encontrada.", 
                    "Erro", 
                    JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro ao carregar locação para devolução", e);
            JOptionPane.showMessageDialog(this, 
                "Erro ao carregar dados da locação: " + e.getMessage(), 
                "Erro", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Método para atualizar locações atrasadas
     */
    public void verificarLocacoesAtrasadas() {
        try {
            List<Locacao> locacoesAtrasadas = locacaoDAO.listarAtrasadas();
            if (!locacoesAtrasadas.isEmpty()) {
                carregarLocacoes(); // Recarregar para mostrar status atualizado
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Erro ao verificar locações atrasadas", e);
        }
    }
}
