package view;

import dao.VeiculoDAO;
import model.Veiculo;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Tela para listar e gerenciar veículos
 * @author lucas
 */
public class ListarVeiculosFrame extends JFrame {
    
    private static final Logger LOGGER = Logger.getLogger(ListarVeiculosFrame.class.getName());
    
    private JTable tabelaVeiculos;
    private DefaultTableModel modeloTabela;
    private VeiculoDAO veiculoDAO;
    private JButton btnEditar;
    private JButton btnAlterarStatus;
    private JButton btnAtualizar;
    private JComboBox<String> cmbStatus;
    private JTextField txtBusca;
    
    public ListarVeiculosFrame() {
        this.veiculoDAO = new VeiculoDAO();
        inicializarComponentes();
        carregarVeiculos();
    }
    
    private void inicializarComponentes() {
        setTitle("Lista de Veículos");
        setSize(1000, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        JPanel painelPrincipal = new JPanel(new BorderLayout(10, 10));
        painelPrincipal.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Painel de filtros
        JPanel painelFiltros = new JPanel(new FlowLayout(FlowLayout.LEFT));
        painelFiltros.add(new JLabel("Buscar:"));
        txtBusca = new JTextField(15);
        painelFiltros.add(txtBusca);
        
        painelFiltros.add(new JLabel("Status:"));
        cmbStatus = new JComboBox<>(new String[]{"Todos", "disponivel", "alugado", "manutencao"});
        painelFiltros.add(cmbStatus);
        
        JButton btnFiltrar = new JButton("Filtrar");
        btnFiltrar.addActionListener(e -> filtrarVeiculos());
        painelFiltros.add(btnFiltrar);
        
        // Configurar tabela
        String[] colunas = {"ID", "Modelo", "Marca", "Ano", "Placa", "Cor", "Combustível", "KM Atual", "Valor Diária", "Status"};
        modeloTabela = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tabelaVeiculos = new JTable(modeloTabela);
        tabelaVeiculos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabelaVeiculos.getTableHeader().setReorderingAllowed(false);
        
        // Configurar largura das colunas
        tabelaVeiculos.getColumnModel().getColumn(0).setPreferredWidth(50);
        tabelaVeiculos.getColumnModel().getColumn(1).setPreferredWidth(150);
        tabelaVeiculos.getColumnModel().getColumn(2).setPreferredWidth(100);
        tabelaVeiculos.getColumnModel().getColumn(3).setPreferredWidth(60);
        tabelaVeiculos.getColumnModel().getColumn(4).setPreferredWidth(100);
        tabelaVeiculos.getColumnModel().getColumn(5).setPreferredWidth(80);
        tabelaVeiculos.getColumnModel().getColumn(6).setPreferredWidth(100);
        tabelaVeiculos.getColumnModel().getColumn(7).setPreferredWidth(80);
        tabelaVeiculos.getColumnModel().getColumn(8).setPreferredWidth(100);
        tabelaVeiculos.getColumnModel().getColumn(9).setPreferredWidth(100);
        
        // Evento de duplo clique
        tabelaVeiculos.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    editarVeiculo();
                }
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(tabelaVeiculos);
        
        // Painel de botões
        JPanel painelBotoes = new JPanel(new FlowLayout());
        btnEditar = new JButton("Editar");
        btnAlterarStatus = new JButton("Alterar Status");
        btnAtualizar = new JButton("Atualizar Lista");
        JButton btnFechar = new JButton("Fechar");
        
        btnEditar.addActionListener(e -> editarVeiculo());
        btnAlterarStatus.addActionListener(e -> alterarStatusVeiculo());
        btnAtualizar.addActionListener(e -> carregarVeiculos());
        btnFechar.addActionListener(e -> dispose());
        
        painelBotoes.add(btnEditar);
        painelBotoes.add(btnAlterarStatus);
        painelBotoes.add(btnAtualizar);
        painelBotoes.add(btnFechar);
        
        painelPrincipal.add(painelFiltros, BorderLayout.NORTH);
        painelPrincipal.add(scrollPane, BorderLayout.CENTER);
        painelPrincipal.add(painelBotoes, BorderLayout.SOUTH);
        
        add(painelPrincipal);
        
        // Configurar eventos de seleção
        tabelaVeiculos.getSelectionModel().addListSelectionListener(e -> {
            boolean selecionado = tabelaVeiculos.getSelectedRow() != -1;
            btnEditar.setEnabled(selecionado);
            btnAlterarStatus.setEnabled(selecionado);
        });
        
        // Inicialmente desabilitar botões
        btnEditar.setEnabled(false);
        btnAlterarStatus.setEnabled(false);
    }
    
    private void carregarVeiculos() {
        try {
            modeloTabela.setRowCount(0);
            List<Veiculo> veiculos = veiculoDAO.listarTodos();
            
            for (Veiculo veiculo : veiculos) {
                Object[] linha = {
                    veiculo.getId(),
                    veiculo.getModelo(),
                    veiculo.getMarca(),
                    veiculo.getAno(),
                    veiculo.getPlaca(),
                    veiculo.getCor(),
                    veiculo.getCombustivel(),
                    veiculo.getKmAtual() != null ? String.format("%.0f", veiculo.getKmAtual()) : "0",
                    veiculo.getValorDiaria() != null ? String.format("R$ %.2f", veiculo.getValorDiaria()) : "R$ 0,00",
                    veiculo.getStatus()
                };
                modeloTabela.addRow(linha);
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro ao carregar veículos", e);
            JOptionPane.showMessageDialog(this, 
                "Erro ao carregar a lista de veículos.", 
                "Erro", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void filtrarVeiculos() {
        String termo = txtBusca.getText().trim();
        String status = (String) cmbStatus.getSelectedItem();
        
        try {
            modeloTabela.setRowCount(0);
            List<Veiculo> veiculos;
            
            if ("Todos".equals(status)) {
                if (termo.isEmpty()) {
                    veiculos = veiculoDAO.listarTodos();
                } else {
                    // Usando o método correto buscarPorTexto em vez de buscarPorModelo
                    veiculos = veiculoDAO.buscarPorTexto(termo);
                }
            } else {
                veiculos = veiculoDAO.listarPorStatus(status);
                if (!termo.isEmpty()) {
                    veiculos = veiculos.stream()
                        .filter(v -> v.getModelo().toLowerCase().contains(termo.toLowerCase()) ||
                                   v.getMarca().toLowerCase().contains(termo.toLowerCase()) ||
                                   v.getPlaca().toLowerCase().contains(termo.toLowerCase()))
                        .toList();
                }
            }
            
            for (Veiculo veiculo : veiculos) {
                Object[] linha = {
                    veiculo.getId(),
                    veiculo.getModelo(),
                    veiculo.getMarca(),
                    veiculo.getAno(),
                    veiculo.getPlaca(),
                    veiculo.getCor(),
                    veiculo.getCombustivel(),
                    veiculo.getKmAtual() != null ? String.format("%.0f", veiculo.getKmAtual()) : "0",
                    veiculo.getValorDiaria() != null ? String.format("R$ %.2f", veiculo.getValorDiaria()) : "R$ 0,00",
                    veiculo.getStatus()
                };
                modeloTabela.addRow(linha);
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro ao filtrar veículos", e);
            JOptionPane.showMessageDialog(this, 
                "Erro ao filtrar veículos.", 
                "Erro", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void editarVeiculo() {
        int linhaSelecionada = tabelaVeiculos.getSelectedRow();
        if (linhaSelecionada == -1) {
            JOptionPane.showMessageDialog(this, 
                "Selecione um veículo para editar.", 
                "Seleção necessária", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        Integer veiculoId = (Integer) modeloTabela.getValueAt(linhaSelecionada, 0);
        try {
            Veiculo veiculo = veiculoDAO.buscarPorId(veiculoId);
            if (veiculo != null) {
                // Abrir tela de edição
                new CadastrarVeiculoFrame();
                carregarVeiculos(); // Atualizar lista após possível edição
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro ao carregar veículo para edição", e);
            JOptionPane.showMessageDialog(this, 
                "Erro ao carregar dados do veículo.", 
                "Erro", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void alterarStatusVeiculo() {
        int linhaSelecionada = tabelaVeiculos.getSelectedRow();
        if (linhaSelecionada == -1) {
            JOptionPane.showMessageDialog(this, 
                "Selecione um veículo para alterar o status.", 
                "Seleção necessária", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String statusAtual = (String) modeloTabela.getValueAt(linhaSelecionada, 9);
        String[] opcoes = {"disponivel", "alugado", "manutencao"};
        
        String novoStatus = (String) JOptionPane.showInputDialog(this,
            "Status atual: " + statusAtual + "\nSelecione o novo status:",
            "Alterar Status",
            JOptionPane.QUESTION_MESSAGE,
            null,
            opcoes,
            statusAtual);
        
        if (novoStatus != null && !novoStatus.equals(statusAtual)) {
            Integer veiculoId = (Integer) modeloTabela.getValueAt(linhaSelecionada, 0);
            try {
                veiculoDAO.atualizarStatus(veiculoId, novoStatus);
                JOptionPane.showMessageDialog(this, 
                    "Status alterado com sucesso!", 
                    "Sucesso", 
                    JOptionPane.INFORMATION_MESSAGE);
                carregarVeiculos();
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Erro ao alterar status do veículo", e);
                JOptionPane.showMessageDialog(this, 
                    "Erro ao alterar status do veículo.", 
                    "Erro", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
