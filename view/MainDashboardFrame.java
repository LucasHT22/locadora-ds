package view;

import model.Usuario;
import javax.swing.*;
import java.awt.*;

/**
 * Painel principal do sistema após login
 * @author lucas
 */
public class MainDashboardFrame extends JFrame {
    private Usuario usuario;

    public MainDashboardFrame(Usuario usuario) {
        this.usuario = usuario;
        inicializarComponentes();
        configurarMenu();
        setVisible(true);
    }

    private void inicializarComponentes() {
        setTitle("Sistema Locadora - " + usuario.getTipoUsuario());
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        // Painel principal
        JPanel painelPrincipal = new JPanel(new BorderLayout());
        
        // Painel de boas-vindas
        JPanel painelBoasVindas = new JPanel(new FlowLayout(FlowLayout.LEFT));
        painelBoasVindas.setBorder(BorderFactory.createEtchedBorder());
        
        JLabel lblBoasVindas = new JLabel("Olá, " + usuario.getNome() + " (" + usuario.getTipoUsuario() + ")");
        lblBoasVindas.setFont(new Font("Arial", Font.BOLD, 14));
        painelBoasVindas.add(lblBoasVindas);

        // Painel central com funcionalidades
        JPanel painelCentral = criarPainelFuncionalidades();

        painelPrincipal.add(painelBoasVindas, BorderLayout.NORTH);
        painelPrincipal.add(painelCentral, BorderLayout.CENTER);

        add(painelPrincipal);
    }

    private JPanel criarPainelFuncionalidades() {
        JPanel painel = new JPanel(new GridLayout(3, 3, 10, 10));
        painel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Botões comuns a todos os usuários
        JButton btnCadastrarCliente = new JButton("Cadastrar Cliente");
        btnCadastrarCliente.setFont(new Font("Arial", Font.PLAIN, 12));
        btnCadastrarCliente.addActionListener(e -> new CadastrarClienteFrame().setVisible(true));

        JButton btnListarLocacoes = new JButton("Listar Locações");
        btnListarLocacoes.setFont(new Font("Arial", Font.PLAIN, 12));
        btnListarLocacoes.addActionListener(e -> new ListarLocacoesFrame().setVisible(true));

        // Botões específicos por tipo de usuário
        if ("admin".equalsIgnoreCase(usuario.getTipoUsuario())) {
            // Admin tem acesso a TODAS as funcionalidades
            JButton btnCadastrarVeiculo = new JButton("Cadastrar Veículo");
            btnCadastrarVeiculo.setFont(new Font("Arial", Font.PLAIN, 12));
            btnCadastrarVeiculo.addActionListener(e -> abrirCadastroVeiculo());
            
            JButton btnAlugarVeiculo = new JButton("Alugar Veículo");
            btnAlugarVeiculo.setFont(new Font("Arial", Font.PLAIN, 12));
            btnAlugarVeiculo.addActionListener(e -> abrirAluguelVeiculo());

            JButton btnDevolverVeiculo = new JButton("Devolver Veículo");
            btnDevolverVeiculo.setFont(new Font("Arial", Font.PLAIN, 12));
            btnDevolverVeiculo.addActionListener(e -> abrirDevolucaoVeiculo());
            
            JButton btnRelatorios = new JButton("Relatórios");
            btnRelatorios.setFont(new Font("Arial", Font.PLAIN, 12));
            btnRelatorios.addActionListener(e -> abrirRelatorios());
            
            JButton btnListarClientes = new JButton("Listar Clientes");
            btnListarClientes.setFont(new Font("Arial", Font.PLAIN, 12));
            btnListarClientes.addActionListener(e -> abrirListarClientes());
            
            JButton btnListarVeiculos = new JButton("Listar Veículos");
            btnListarVeiculos.setFont(new Font("Arial", Font.PLAIN, 12));
            btnListarVeiculos.addActionListener(e -> abrirListarVeiculos());

            painel.add(btnCadastrarCliente);
            painel.add(btnCadastrarVeiculo);
            painel.add(btnAlugarVeiculo);
            painel.add(btnDevolverVeiculo);
            painel.add(btnListarClientes);
            painel.add(btnListarVeiculos);
            painel.add(btnListarLocacoes);
            painel.add(btnRelatorios);

        } else if ("funcionario".equalsIgnoreCase(usuario.getTipoUsuario())) {
            JButton btnAlugarVeiculo = new JButton("Alugar Veículo");
            btnAlugarVeiculo.setFont(new Font("Arial", Font.PLAIN, 12));
            btnAlugarVeiculo.addActionListener(e -> abrirAluguelVeiculo());

            JButton btnDevolverVeiculo = new JButton("Devolver Veículo");
            btnDevolverVeiculo.setFont(new Font("Arial", Font.PLAIN, 12));
            btnDevolverVeiculo.addActionListener(e -> abrirDevolucaoVeiculo());
            
            JButton btnListarClientes = new JButton("Listar Clientes");
            btnListarClientes.setFont(new Font("Arial", Font.PLAIN, 12));
            btnListarClientes.addActionListener(e -> abrirListarClientes());

            painel.add(btnCadastrarCliente);
            painel.add(btnAlugarVeiculo);
            painel.add(btnDevolverVeiculo);
            painel.add(btnListarClientes);
            painel.add(btnListarLocacoes);
        }

        return painel;
    }

    private void configurarMenu() {
        JMenuBar menuBar = new JMenuBar();
        
        JMenu menuArquivo = new JMenu("Arquivo");
        JMenuItem itemLogout = new JMenuItem("Logout");
        itemLogout.addActionListener(e -> logout());
        JMenuItem itemSair = new JMenuItem("Sair");
        itemSair.addActionListener(e -> System.exit(0));
        menuArquivo.add(itemLogout);
        menuArquivo.addSeparator();
        menuArquivo.add(itemSair);
        
        JMenu menuCadastros = new JMenu("Cadastros");
        JMenuItem itemCliente = new JMenuItem("Cliente");
        itemCliente.addActionListener(e -> new CadastrarClienteFrame().setVisible(true));
        menuCadastros.add(itemCliente);
        
        if ("admin".equalsIgnoreCase(usuario.getTipoUsuario())) {
            JMenuItem itemVeiculo = new JMenuItem("Veículo");
            itemVeiculo.addActionListener(e -> abrirCadastroVeiculo());
            menuCadastros.add(itemVeiculo);
        }
        
        JMenu menuOperacoes = new JMenu("Operações");
        if ("funcionario".equalsIgnoreCase(usuario.getTipoUsuario()) || "admin".equalsIgnoreCase(usuario.getTipoUsuario())) {
            JMenuItem itemAlugar = new JMenuItem("Alugar Veículo");
            itemAlugar.addActionListener(e -> abrirAluguelVeiculo());
            menuOperacoes.add(itemAlugar);
            
            JMenuItem itemDevolver = new JMenuItem("Devolver Veículo");
            itemDevolver.addActionListener(e -> abrirDevolucaoVeiculo());
            menuOperacoes.add(itemDevolver);
        }
        
        JMenu menuRelatorios = new JMenu("Relatórios");
        JMenuItem itemListarLocacoes = new JMenuItem("Locações");
        itemListarLocacoes.addActionListener(e -> new ListarLocacoesFrame().setVisible(true));
        menuRelatorios.add(itemListarLocacoes);
        
        if ("admin".equalsIgnoreCase(usuario.getTipoUsuario())) {
            JMenuItem itemRelatorioCompleto = new JMenuItem("Relatórios Gerenciais");
            itemRelatorioCompleto.addActionListener(e -> abrirRelatorios());
            menuRelatorios.add(itemRelatorioCompleto);
        }
        
        JMenu menuAjuda = new JMenu("Ajuda");
        JMenuItem itemSobre = new JMenuItem("Sobre");
        itemSobre.addActionListener(e -> mostrarSobre());
        menuAjuda.add(itemSobre);
        
        menuBar.add(menuArquivo);
        menuBar.add(menuCadastros);
        menuBar.add(menuOperacoes);
        menuBar.add(menuRelatorios);
        menuBar.add(menuAjuda);
        
        setJMenuBar(menuBar);
    }

    private void abrirCadastroVeiculo() {
        try {
            new CadastrarVeiculoFrame().setVisible(true);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Erro ao abrir cadastro de veículo: " + e.getMessage(), 
                "Erro", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void abrirRelatorios() {
        try {
            new RelatoriosFrame().setVisible(true);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Erro ao abrir relatórios: " + e.getMessage(), 
                "Erro", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void abrirAluguelVeiculo() {
        try {
            new AlugarVeiculoFrame(usuario).setVisible(true);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Erro ao abrir aluguel de veículo: " + e.getMessage(), 
                "Erro", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void abrirDevolucaoVeiculo() {
        try {
            new TelaDevolucao().setVisible(true);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Erro ao abrir devolução de veículo: " + e.getMessage(), 
                "Erro", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void abrirListarClientes() {
        try {
            new ListarClientesFrame().setVisible(true);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Erro ao abrir lista de clientes: " + e.getMessage(), 
                "Erro", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void abrirListarVeiculos() {
        try {
            new ListarVeiculosFrame().setVisible(true);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Erro ao abrir lista de veículos: " + e.getMessage(), 
                "Erro", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void logout() {
        int opcao = JOptionPane.showConfirmDialog(this, 
            "Deseja realmente fazer logout?", 
            "Confirmar Logout", 
            JOptionPane.YES_NO_OPTION);
            
        if (opcao == JOptionPane.YES_OPTION) {
            dispose();
            new LoginFrame().setVisible(true);
        }
    }

    private void mostrarSobre() {
        JOptionPane.showMessageDialog(this, 
            "Sistema de Locadora de Veículos\n\n" +
            "Desenvolvido como trabalho final bimestral da matéria\n" +
            "Desenvolvimento de Sistemas\n" +
            "Prof. Carlos Alberto\n\n" +
            "Versão: 1.0\n" +
            "© 2025 - Todos os direitos reservados", 
            "Sobre o Sistema", 
            JOptionPane.INFORMATION_MESSAGE);
    }
}
