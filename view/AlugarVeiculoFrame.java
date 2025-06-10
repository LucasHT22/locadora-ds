package view;

import dao.ClienteDAO;
import dao.VeiculoDAO;
import dao.LocacaoDAO;
import model.Cliente;
import model.Veiculo;
import model.Locacao;
import model.Usuario;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * Frame para aluguel de veículos
 * @author lucas
 */
public class AlugarVeiculoFrame extends JFrame {
    private JTextField campoClienteCpf;
    private JButton btnBuscarCliente;
    private JLabel lblClienteNome;
    private JTable tabelaVeiculos;
    private DefaultTableModel modeloTabela;
    private JSpinner spinnerDias;
    private JTextField campoDataInicio;
    private JTextField campoKmInicial;
    private JTextArea campoObservacoes;
    private JLabel lblValorTotal;
    private JButton btnRegistrarLocacao;
    private JButton btnLimparCampos;
    private JButton btnCancelar;
    private JLabel lblStatus;
    
    private Cliente clienteSelecionado;
    private Veiculo veiculoSelecionado;
    private Usuario usuario;

    public AlugarVeiculoFrame() {
        this(null);
    }

    // Construtor com usuário
    public AlugarVeiculoFrame(Usuario usuario) {
        this.usuario = usuario;
        initializeComponents();
        setupLayout();
        setupEvents();
        carregarVeiculosDisponiveis();
        setVisible(true);
    }

    private void initializeComponents() {
        setTitle("Alugar Veículo");
        setSize(750, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(true);
        
        // Campos
        campoClienteCpf = new JTextField(15);
        btnBuscarCliente = new JButton("Buscar");
        lblClienteNome = new JLabel("Cliente não selecionado");
        lblClienteNome.setForeground(Color.GRAY);
        
        // Tabela de veículos
        String[] colunas = {"ID", "Modelo", "Marca", "Ano", "Placa", "Cor", "Valor/Dia"};
        modeloTabela = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabelaVeiculos = new JTable(modeloTabela);
        tabelaVeiculos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Campos de locação
        spinnerDias = new JSpinner(new SpinnerNumberModel(1, 1, 365, 1));
        campoDataInicio = new JTextField(LocalDate.now().toString(), 12);
        campoKmInicial = new JTextField(12);
        campoObservacoes = new JTextArea(3, 20);
        campoObservacoes.setLineWrap(true);
        campoObservacoes.setWrapStyleWord(true);
        
        lblValorTotal = new JLabel("R$ 0,00");
        lblValorTotal.setFont(new Font("Arial", Font.BOLD, 14));
        lblValorTotal.setForeground(new Color(34, 139, 34));
        
        // Botões
        btnRegistrarLocacao = new JButton("Registrar Locação");
        btnLimparCampos = new JButton("Limpar Campos");
        btnCancelar = new JButton("Cancelar");
        lblStatus = new JLabel(" ");
        
        // Estilização dos botões
        btnRegistrarLocacao.setBackground(new Color(34, 139, 34));
        btnRegistrarLocacao.setForeground(Color.WHITE);
        btnRegistrarLocacao.setFocusPainted(false);
        btnRegistrarLocacao.setEnabled(false);
        btnRegistrarLocacao.setPreferredSize(new Dimension(150, 35));
        btnRegistrarLocacao.setFont(new Font("Arial", Font.BOLD, 12));
        
        btnLimparCampos.setBackground(new Color(255, 193, 7));
        btnLimparCampos.setForeground(Color.BLACK);
        btnLimparCampos.setFocusPainted(false);
        btnLimparCampos.setPreferredSize(new Dimension(130, 35));
        btnLimparCampos.setFont(new Font("Arial", Font.BOLD, 12));
        
        btnCancelar.setBackground(new Color(220, 53, 69));
        btnCancelar.setForeground(Color.WHITE);
        btnCancelar.setFocusPainted(false);
        btnCancelar.setPreferredSize(new Dimension(100, 35));
        btnCancelar.setFont(new Font("Arial", Font.BOLD, 12));
        
        lblStatus.setHorizontalAlignment(SwingConstants.CENTER);
        lblStatus.setFont(new Font("Arial", Font.PLAIN, 12));
    }

    private void setupLayout() {
        setLayout(new BorderLayout());
        
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Painel superior - Cliente
        JPanel painelCliente = new JPanel(new FlowLayout(FlowLayout.LEFT));
        painelCliente.setBorder(BorderFactory.createTitledBorder("Selecionar Cliente"));
        painelCliente.add(new JLabel("CPF:"));
        painelCliente.add(campoClienteCpf);
        painelCliente.add(btnBuscarCliente);
        painelCliente.add(Box.createHorizontalStrut(10));
        painelCliente.add(new JLabel("Cliente:"));
        painelCliente.add(lblClienteNome);
        
        // Painel central - Veículos
        JPanel painelVeiculos = new JPanel(new BorderLayout());
        painelVeiculos.setBorder(BorderFactory.createTitledBorder("Veículos Disponíveis"));
        JScrollPane scrollVeiculos = new JScrollPane(tabelaVeiculos);
        scrollVeiculos.setPreferredSize(new Dimension(700, 180));
        painelVeiculos.add(scrollVeiculos, BorderLayout.CENTER);
        
        // Painel de dados da locação
        JPanel painelLocacao = new JPanel();
        painelLocacao.setLayout(new BoxLayout(painelLocacao, BoxLayout.Y_AXIS));
        painelLocacao.setBorder(BorderFactory.createTitledBorder("Dados da Locação"));
        
        // Primeira linha: Data e Dias
        JPanel linha1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        linha1.add(new JLabel("Data Início:"));
        linha1.add(campoDataInicio);
        linha1.add(Box.createHorizontalStrut(20));
        linha1.add(new JLabel("Dias:"));
        linha1.add(spinnerDias);
        linha1.add(Box.createHorizontalStrut(20));
        linha1.add(new JLabel("Valor Total:"));
        linha1.add(lblValorTotal);
        
        // Segunda linha: Km inicial
        JPanel linha2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        linha2.add(new JLabel("Km Inicial:"));
        linha2.add(campoKmInicial);
        
        // Terceira linha: Observações
        JPanel linha3 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        linha3.add(new JLabel("Observações:"));
        JScrollPane scrollObs = new JScrollPane(campoObservacoes);
        scrollObs.setPreferredSize(new Dimension(400, 60));
        linha3.add(scrollObs);
        
        painelLocacao.add(linha1);
        painelLocacao.add(linha2);
        painelLocacao.add(linha3);
        
        // Painel de botões
        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        painelBotoes.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        painelBotoes.setBackground(getBackground());
        
        painelBotoes.add(btnRegistrarLocacao);
        painelBotoes.add(btnLimparCampos);
        painelBotoes.add(btnCancelar);
        
        // Painel de status
        JPanel painelStatus = new JPanel(new FlowLayout(FlowLayout.CENTER));
        painelStatus.setBorder(BorderFactory.createEmptyBorder(5, 5, 10, 5));
        painelStatus.add(lblStatus);
        
        // Montagem do painel inferior
        JPanel painelInferior = new JPanel(new BorderLayout());
        painelInferior.add(painelLocacao, BorderLayout.CENTER);
        painelInferior.add(painelBotoes, BorderLayout.SOUTH);
        
        // Montagem principal
        mainPanel.add(painelCliente, BorderLayout.NORTH);
        mainPanel.add(painelVeiculos, BorderLayout.CENTER);
        mainPanel.add(painelInferior, BorderLayout.SOUTH);
        
        // Painel principal
        add(mainPanel, BorderLayout.CENTER);
        add(painelStatus, BorderLayout.SOUTH);
    }

    private void setupEvents() {
        btnBuscarCliente.addActionListener(e -> buscarCliente());
        btnCancelar.addActionListener(e -> dispose());
        btnRegistrarLocacao.addActionListener(e -> confirmarAluguel());
        btnLimparCampos.addActionListener(e -> limparFormulario());
        
        // Seleção de veículo na tabela
        tabelaVeiculos.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                selecionarVeiculo();
            }
        });
        
        // Atualizar valor total quando dias mudarem
        spinnerDias.addChangeListener(e -> calcularValorTotal());
    }

    private void buscarCliente() {
        String cpf = campoClienteCpf.getText().trim().replaceAll("[.-]", "");
        
        if (cpf.isEmpty()) {
            mostrarErro("Digite o CPF do cliente!");
            return;
        }
        
        try {
            ClienteDAO dao = new ClienteDAO();
            clienteSelecionado = dao.buscarPorCpf(cpf);
            
            if (clienteSelecionado != null) {
                lblClienteNome.setText(clienteSelecionado.getNome());
                lblClienteNome.setForeground(new Color(34, 139, 34));
                verificarPodeAlugar();
            } else {
                lblClienteNome.setText("Cliente não encontrado");
                lblClienteNome.setForeground(Color.RED);
                clienteSelecionado = null;
                btnRegistrarLocacao.setEnabled(false);
            }
        } catch (Exception ex) {
            mostrarErro("Erro ao buscar cliente: " + ex.getMessage());
        }
    }

    private void carregarVeiculosDisponiveis() {
        try {
            VeiculoDAO dao = new VeiculoDAO();
            // Busca veículos com status "disponivel"
            List<Veiculo> veiculos = dao.listarPorStatus("disponivel");
            
            modeloTabela.setRowCount(0);
            for (Veiculo v : veiculos) {
                Object[] row = {
                    v.getId(),
                    v.getModelo(),
                    v.getMarca(),
                    v.getAno(),
                    v.getPlaca(),
                    v.getCor(),
                    String.format("R$ %.2f", v.getValorDiaria())
                };
                modeloTabela.addRow(row);
            }
        } catch (Exception ex) {
            mostrarErro("Erro ao carregar veículos: " + ex.getMessage());
        }
    }

    private void selecionarVeiculo() {
        int selectedRow = tabelaVeiculos.getSelectedRow();
        if (selectedRow >= 0) {
            try {
                int veiculoId = (Integer) modeloTabela.getValueAt(selectedRow, 0);
                VeiculoDAO dao = new VeiculoDAO();
                veiculoSelecionado = dao.buscarPorId(veiculoId);
                
                if (veiculoSelecionado != null) {
                    // Converte BigDecimal para String para exibição
                    campoKmInicial.setText(veiculoSelecionado.getKmAtual().toString());
                    calcularValorTotal();
                    verificarPodeAlugar();
                }
            } catch (Exception ex) {
                mostrarErro("Erro ao selecionar veículo: " + ex.getMessage());
            }
        }
    }

    private void calcularValorTotal() {
        if (veiculoSelecionado != null && veiculoSelecionado.getValorDiaria() != null) {
            int dias = (Integer) spinnerDias.getValue();
            BigDecimal valorTotal = veiculoSelecionado.getValorDiaria().multiply(new BigDecimal(dias));
            lblValorTotal.setText(String.format("R$ %.2f", valorTotal));
        }
    }

    private void verificarPodeAlugar() {
        boolean podeAlugar = clienteSelecionado != null && veiculoSelecionado != null;
        btnRegistrarLocacao.setEnabled(podeAlugar);
    }

    private void confirmarAluguel() {
        try {
            if (!validarDados()) {
                return;
            }
            
            // Criar locação
            Locacao locacao = new Locacao();
            locacao.setClienteId(clienteSelecionado.getId());
            locacao.setVeiculoId(veiculoSelecionado.getId());
            locacao.setDataInicio(LocalDate.parse(campoDataInicio.getText()));
            
            int dias = (Integer) spinnerDias.getValue();
            locacao.setDataFimPrevista(locacao.getDataInicio().plusDays(dias));
            locacao.setDiasPrevistos(dias);
            
            // Converter para BigDecimal
            BigDecimal kmInicial = new BigDecimal(campoKmInicial.getText());
            locacao.setKmInicial(kmInicial);
            
            locacao.setValorDiaria(veiculoSelecionado.getValorDiaria());
            
            // Calcular valor total previsto usando BigDecimal
            BigDecimal valorTotalPrevisto = veiculoSelecionado.getValorDiaria().multiply(new BigDecimal(dias));
            locacao.setValorTotalPrevisto(valorTotalPrevisto);
            
            locacao.setObservacoesRetirada(campoObservacoes.getText().trim());
            locacao.setStatus("ativa");
            
            // Salvar no banco
            LocacaoDAO locacaoDAO = new LocacaoDAO();
            locacaoDAO.inserir(locacao);
            
            // Atualizar status do veículo para alugado
            VeiculoDAO veiculoDAO = new VeiculoDAO();
            veiculoDAO.atualizarStatus(veiculoSelecionado.getId(), "alugado");
            
            mostrarSucesso("Locação registrada com sucesso!");
            
            // Limpar formulário
            limparFormulario();
            carregarVeiculosDisponiveis();
            
        } catch (Exception ex) {
            mostrarErro("Erro ao registrar locação: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private boolean validarDados() {
        if (clienteSelecionado == null) {
            mostrarErro("Selecione um cliente!");
            return false;
        }
        
        if (veiculoSelecionado == null) {
            mostrarErro("Selecione um veículo!");
            return false;
        }
        
        String dataInicio = campoDataInicio.getText().trim();
        if (dataInicio.isEmpty()) {
            mostrarErro("Data de início é obrigatória!");
            return false;
        }
        
        try {
            LocalDate data = LocalDate.parse(dataInicio);
            if (data.isBefore(LocalDate.now())) {
                mostrarErro("Data de início não pode ser anterior a hoje!");
                return false;
            }
        } catch (Exception e) {
            mostrarErro("Data de início inválida! Use o formato: YYYY-MM-DD");
            return false;
        }
        
        String kmInicial = campoKmInicial.getText().trim();
        if (kmInicial.isEmpty()) {
            mostrarErro("Km inicial é obrigatório!");
            return false;
        }
        
        try {
            BigDecimal km = new BigDecimal(kmInicial);
            if (km.compareTo(BigDecimal.ZERO) < 0) {
                mostrarErro("Km inicial não pode ser negativo!");
                return false;
            }
        } catch (NumberFormatException e) {
            mostrarErro("Km inicial deve ser um número válido!");
            return false;
        }
        
        return true;
    }

    private void limparFormulario() {
        campoClienteCpf.setText("");
        lblClienteNome.setText("Cliente não selecionado");
        lblClienteNome.setForeground(Color.GRAY);
        tabelaVeiculos.clearSelection();
        spinnerDias.setValue(1);
        campoDataInicio.setText(LocalDate.now().toString());
        campoKmInicial.setText("");
        campoObservacoes.setText("");
        lblValorTotal.setText("R$ 0,00");
        lblStatus.setText(" ");
        
        clienteSelecionado = null;
        veiculoSelecionado = null;
        btnRegistrarLocacao.setEnabled(false);
    }

    private void mostrarSucesso(String mensagem) {
        lblStatus.setText(mensagem);
        lblStatus.setForeground(new Color(34, 139, 34));
        
        Timer timer = new Timer(3000, e -> lblStatus.setText(" "));
        timer.setRepeats(false);
        timer.start();
    }

    private void mostrarErro(String mensagem) {
        lblStatus.setText(mensagem);
        lblStatus.setForeground(Color.RED);
        
        Timer timer = new Timer(4000, e -> lblStatus.setText(" "));
        timer.setRepeats(false);
        timer.start();
    }
}
