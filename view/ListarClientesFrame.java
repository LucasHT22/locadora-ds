package view;

import dao.ClienteDAO;
import model.Cliente;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Tela para listar e gerenciar clientes
 * @author lucas
 */
public class ListarClientesFrame extends JFrame {
    
    private static final Logger LOGGER = Logger.getLogger(ListarClientesFrame.class.getName());
    
    private JTable tabelaClientes;
    private DefaultTableModel modeloTabela;
    private ClienteDAO clienteDAO;
    private JButton btnEditar;
    private JButton btnInativar;
    private JButton btnAtualizar;
    private JTextField txtBusca;
    
    public ListarClientesFrame() {
        this.clienteDAO = new ClienteDAO();
        inicializarComponentes();
        carregarClientes();
    }
    
    private void inicializarComponentes() {
        setTitle("Lista de Clientes");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        JPanel painelPrincipal = new JPanel(new BorderLayout(10, 10));
        painelPrincipal.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Painel de busca
        JPanel painelBusca = new JPanel(new FlowLayout(FlowLayout.LEFT));
        painelBusca.add(new JLabel("Buscar:"));
        txtBusca = new JTextField(20);
        painelBusca.add(txtBusca);
        JButton btnBuscar = new JButton("Buscar");
        btnBuscar.addActionListener(e -> buscarClientes());
        painelBusca.add(btnBuscar);
        
        // Configurar tabela
        String[] colunas = {"ID", "Nome", "CPF", "Telefone", "Email", "CNH", "Status"};
        modeloTabela = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tabelaClientes = new JTable(modeloTabela);
        tabelaClientes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabelaClientes.getTableHeader().setReorderingAllowed(false);
        
        // Configurar largura das colunas
        tabelaClientes.getColumnModel().getColumn(0).setPreferredWidth(50);
        tabelaClientes.getColumnModel().getColumn(1).setPreferredWidth(200);
        tabelaClientes.getColumnModel().getColumn(2).setPreferredWidth(120);
        tabelaClientes.getColumnModel().getColumn(3).setPreferredWidth(120);
        tabelaClientes.getColumnModel().getColumn(4).setPreferredWidth(200);
        tabelaClientes.getColumnModel().getColumn(5).setPreferredWidth(120);
        tabelaClientes.getColumnModel().getColumn(6).setPreferredWidth(80);
        
        // Evento de duplo clique
        tabelaClientes.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    editarCliente();
                }
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(tabelaClientes);
        
        // Painel de botões
        JPanel painelBotoes = new JPanel(new FlowLayout());
        btnEditar = new JButton("Editar");
        btnInativar = new JButton("Inativar");
        btnAtualizar = new JButton("Atualizar Lista");
        JButton btnFechar = new JButton("Fechar");
        
        btnEditar.addActionListener(e -> editarCliente());
        btnInativar.addActionListener(e -> inativarCliente());
        btnAtualizar.addActionListener(e -> carregarClientes());
        btnFechar.addActionListener(e -> dispose());
        
        painelBotoes.add(btnEditar);
        painelBotoes.add(btnInativar);
        painelBotoes.add(btnAtualizar);
        painelBotoes.add(btnFechar);
        
        painelPrincipal.add(painelBusca, BorderLayout.NORTH);
        painelPrincipal.add(scrollPane, BorderLayout.CENTER);
        painelPrincipal.add(painelBotoes, BorderLayout.SOUTH);
        
        add(painelPrincipal);
        
        // Configurar eventos de seleção
        tabelaClientes.getSelectionModel().addListSelectionListener(e -> {
            boolean selecionado = tabelaClientes.getSelectedRow() != -1;
            btnEditar.setEnabled(selecionado);
            btnInativar.setEnabled(selecionado);
        });
        
        // Inicialmente desabilitar botões
        btnEditar.setEnabled(false);
        btnInativar.setEnabled(false);
    }
    
    private void carregarClientes() {
        try {
            modeloTabela.setRowCount(0);
            List<Cliente> clientes = clienteDAO.listarTodos();
            
            for (Cliente cliente : clientes) {
                Object[] linha = {
                    cliente.getId(),
                    cliente.getNome(),
                    cliente.getCpf(),
                    cliente.getTelefone(),
                    cliente.getEmail() != null ? cliente.getEmail() : "",
                    cliente.getCnh() != null ? cliente.getCnh() : "",
                    cliente.isAtivo() ? "Ativo" : "Inativo"
                };
                modeloTabela.addRow(linha);
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro ao carregar clientes", e);
            JOptionPane.showMessageDialog(this, 
                "Erro ao carregar a lista de clientes: " + e.getMessage(), 
                "Erro", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void buscarClientes() {
        String termo = txtBusca.getText().trim();
        if (termo.isEmpty()) {
            carregarClientes();
            return;
        }
        
        try {
            modeloTabela.setRowCount(0);
            List<Cliente> clientes = clienteDAO.buscarPorNome(termo);
            
            for (Cliente cliente : clientes) {
                Object[] linha = {
                    cliente.getId(),
                    cliente.getNome(),
                    cliente.getCpf(),
                    cliente.getTelefone(),
                    cliente.getEmail() != null ? cliente.getEmail() : "",
                    cliente.getCnh() != null ? cliente.getCnh() : "",
                    cliente.isAtivo() ? "Ativo" : "Inativo"
                };
                modeloTabela.addRow(linha);
            }
            
            if (clientes.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "Nenhum cliente encontrado com o termo: " + termo, 
                    "Resultado da busca", 
                    JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro ao buscar clientes", e);
            JOptionPane.showMessageDialog(this, 
                "Erro ao buscar clientes: " + e.getMessage(), 
                "Erro", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void editarCliente() {
        int linhaSelecionada = tabelaClientes.getSelectedRow();
        if (linhaSelecionada == -1) {
            JOptionPane.showMessageDialog(this, 
                "Selecione um cliente para editar.", 
                "Seleção necessária", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        Integer clienteId = (Integer) modeloTabela.getValueAt(linhaSelecionada, 0);
        try {
            Cliente cliente = clienteDAO.buscarPorId(clienteId);
            if (cliente != null) {
                CadastrarClienteFrame frameEdicao = new CadastrarClienteFrame();
                frameEdicao.setVisible(true);
                
                // Adicionar listener para atualizar a lista quando a janela de edição for fechada
                frameEdicao.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosed(java.awt.event.WindowEvent windowEvent) {
                        carregarClientes(); // Atualizar lista após possível edição
                    }
                });
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Cliente não encontrado.", 
                    "Erro", 
                    JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro ao carregar cliente para edição", e);
            JOptionPane.showMessageDialog(this, 
                "Erro ao carregar dados do cliente: " + e.getMessage(), 
                "Erro", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void inativarCliente() {
        int linhaSelecionada = tabelaClientes.getSelectedRow();
        if (linhaSelecionada == -1) {
            JOptionPane.showMessageDialog(this, 
                "Selecione um cliente para inativar.", 
                "Seleção necessária", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String nomeCliente = (String) modeloTabela.getValueAt(linhaSelecionada, 1);
        String statusAtual = (String) modeloTabela.getValueAt(linhaSelecionada, 6);
        
        // Verificar se o cliente já está inativo
        if ("Inativo".equals(statusAtual)) {
            JOptionPane.showMessageDialog(this, 
                "Este cliente já está inativo.", 
                "Cliente já inativo", 
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        int confirmacao = JOptionPane.showConfirmDialog(this, 
            "Tem certeza que deseja inativar o cliente: " + nomeCliente + "?", 
            "Confirmar inativação", 
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);
        
        if (confirmacao == JOptionPane.YES_OPTION) {
            Integer clienteId = (Integer) modeloTabela.getValueAt(linhaSelecionada, 0);
            try {
                clienteDAO.desativar(clienteId);
                JOptionPane.showMessageDialog(this, 
                    "Cliente inativado com sucesso!", 
                    "Sucesso", 
                    JOptionPane.INFORMATION_MESSAGE);
                carregarClientes();
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Erro ao inativar cliente", e);
                JOptionPane.showMessageDialog(this, 
                    "Erro ao inativar cliente: " + e.getMessage(), 
                    "Erro", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
