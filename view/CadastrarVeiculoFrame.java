package view;

import dao.VeiculoDAO;
import model.Veiculo;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Frame para cadastro e edição de veículos
 * @author lucas
 */
public class CadastrarVeiculoFrame extends JFrame {
    
    private static final Logger LOGGER = Logger.getLogger(CadastrarVeiculoFrame.class.getName());
    
    private JTextField campoModelo;
    private JTextField campoMarca;
    private JTextField campoAno;
    private JTextField campoPlaca;
    private JTextField campoCor;
    private JComboBox<String> comboCombustivel;
    private JTextField campoKmAtual;
    private JTextField campoValorDiaria;
    private JComboBox<String> comboStatus;
    private JTextArea campoObservacoes;
    private JButton botaoSalvar;
    private JButton botaoLimpar;
    private JLabel lblStatus;
    
    private Veiculo veiculoParaEdicao;
    private boolean modoEdicao = false;

    // Construtor para cadastro
    public CadastrarVeiculoFrame() {
        this.modoEdicao = false;
        initializeComponents();
        setupLayout();
        setupEvents();
        setVisible(true);
    }
    
    // Construtor para edição
    public CadastrarVeiculoFrame(Veiculo veiculo) {
        this.veiculoParaEdicao = veiculo;
        this.modoEdicao = true;
        initializeComponents();
        setupLayout();
        setupEvents();
        preencherCampos();
        setVisible(true);
    }

    private void initializeComponents() {
        setTitle(modoEdicao ? "Editar Veículo" : "Cadastrar Veículo");
        setSize(520, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);
        
        // Campos de texto com tamanhos padronizados
        campoModelo = new JTextField(20);
        campoMarca = new JTextField(20);
        campoAno = new JTextField(20);
        campoPlaca = new JTextField(20);
        campoCor = new JTextField(20);
        campoKmAtual = new JTextField(20);
        campoValorDiaria = new JTextField(20);
        
        // ComboBoxes com tamanho padronizado
        String[] combustiveis = {"gasolina", "etanol", "flex", "diesel"};
        comboCombustivel = new JComboBox<>(combustiveis);
        comboCombustivel.setPreferredSize(new Dimension(200, 25));
        
        String[] status = {"disponivel", "alugado", "manutencao"};
        comboStatus = new JComboBox<>(status);
        comboStatus.setPreferredSize(new Dimension(200, 25));
        
        // TextArea
        campoObservacoes = new JTextArea(4, 20);
        campoObservacoes.setLineWrap(true);
        campoObservacoes.setWrapStyleWord(true);
        
        // Botões
        botaoSalvar = new JButton(modoEdicao ? "Salvar Alterações" : "Cadastrar Veículo");
        botaoLimpar = new JButton("Limpar");
        lblStatus = new JLabel(" ");
        
        // Estilização
        botaoSalvar.setBackground(new Color(34, 139, 34));
        botaoSalvar.setForeground(Color.WHITE);
        botaoSalvar.setFocusPainted(false);
        
        botaoLimpar.setBackground(new Color(255, 140, 0));
        botaoLimpar.setForeground(Color.WHITE);
        botaoLimpar.setFocusPainted(false);
        
        lblStatus.setHorizontalAlignment(SwingConstants.CENTER);
    }

    private void setupLayout() {
        setLayout(new BorderLayout());
        
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Título
        JLabel titleLabel = new JLabel(modoEdicao ? "Edição de Veículo" : "Cadastro de Veículo");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        mainPanel.add(titleLabel, gbc);
        
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;
        
        // Campos
        int row = 1;
        
        gbc.gridx = 0; gbc.gridy = row;
        mainPanel.add(new JLabel("Modelo: *"), gbc);
        gbc.gridx = 1;
        mainPanel.add(campoModelo, gbc);
        
        gbc.gridx = 0; gbc.gridy = ++row;
        mainPanel.add(new JLabel("Marca: *"), gbc);
        gbc.gridx = 1;
        mainPanel.add(campoMarca, gbc);
        
        gbc.gridx = 0; gbc.gridy = ++row;
        mainPanel.add(new JLabel("Ano: *"), gbc);
        gbc.gridx = 1;
        mainPanel.add(campoAno, gbc);
        
        gbc.gridx = 0; gbc.gridy = ++row;
        mainPanel.add(new JLabel("Placa:"), gbc);
        gbc.gridx = 1;
        mainPanel.add(campoPlaca, gbc);
        
        gbc.gridx = 0; gbc.gridy = ++row;
        mainPanel.add(new JLabel("Cor:"), gbc);
        gbc.gridx = 1;
        mainPanel.add(campoCor, gbc);
        
        gbc.gridx = 0; gbc.gridy = ++row;
        mainPanel.add(new JLabel("Combustível:"), gbc);
        gbc.gridx = 1;
        mainPanel.add(comboCombustivel, gbc);
        
        gbc.gridx = 0; gbc.gridy = ++row;
        mainPanel.add(new JLabel("Km Atual:"), gbc);
        gbc.gridx = 1;
        mainPanel.add(campoKmAtual, gbc);
        
        gbc.gridx = 0; gbc.gridy = ++row;
        mainPanel.add(new JLabel("Valor Diária:"), gbc);
        gbc.gridx = 1;
        mainPanel.add(campoValorDiaria, gbc);
        
        gbc.gridx = 0; gbc.gridy = ++row;
        mainPanel.add(new JLabel("Status:"), gbc);
        gbc.gridx = 1;
        mainPanel.add(comboStatus, gbc);
        
        gbc.gridx = 0; gbc.gridy = ++row;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        mainPanel.add(new JLabel("Observações:"), gbc);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        JScrollPane scrollObservacoes = new JScrollPane(campoObservacoes);
        scrollObservacoes.setPreferredSize(new Dimension(200, 80));
        mainPanel.add(scrollObservacoes, gbc);
        
        // Botões
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(botaoSalvar);
        buttonPanel.add(botaoLimpar);
        
        gbc.gridx = 0; gbc.gridy = ++row; gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        mainPanel.add(buttonPanel, gbc);
        
        // Status
        gbc.gridy = ++row;
        mainPanel.add(lblStatus, gbc);
        
        // Nota
        JLabel notaLabel = new JLabel("* Campos obrigatórios");
        notaLabel.setFont(new Font("Arial", Font.ITALIC, 10));
        notaLabel.setForeground(Color.GRAY);
        gbc.gridy = ++row;
        gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(notaLabel, gbc);
        
        add(mainPanel, BorderLayout.CENTER);
    }

    private void setupEvents() {
        botaoSalvar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (modoEdicao) {
                    editarVeiculo();
                } else {
                    cadastrarVeiculo();
                }
            }
        });
        
        botaoLimpar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                limparCampos();
            }
        });
    }
    
    private void preencherCampos() {
        if (veiculoParaEdicao != null) {
            campoModelo.setText(veiculoParaEdicao.getModelo());
            campoMarca.setText(veiculoParaEdicao.getMarca());
            campoAno.setText(String.valueOf(veiculoParaEdicao.getAno()));
            campoPlaca.setText(veiculoParaEdicao.getPlaca());
            campoCor.setText(veiculoParaEdicao.getCor());
            
            if (veiculoParaEdicao.getCombustivel() != null) {
                comboCombustivel.setSelectedItem(veiculoParaEdicao.getCombustivel());
            }
            
            if (veiculoParaEdicao.getKmAtual() != null) {
                campoKmAtual.setText(veiculoParaEdicao.getKmAtual().toString());
            }
            
            if (veiculoParaEdicao.getValorDiaria() != null) {
                campoValorDiaria.setText(veiculoParaEdicao.getValorDiaria().toString());
            }
            
            if (veiculoParaEdicao.getStatus() != null) {
                comboStatus.setSelectedItem(veiculoParaEdicao.getStatus());
            }
            
            campoObservacoes.setText(veiculoParaEdicao.getObservacoes());
        }
    }

    private void cadastrarVeiculo() {
        try {
            if (!validarCampos()) {
                return;
            }
            
            Veiculo veiculo = criarVeiculoDosCampos();
            
            VeiculoDAO dao = new VeiculoDAO();
            dao.inserir(veiculo);
            
            mostrarSucesso("Veículo cadastrado com sucesso!");
            limparCampos();
            
        } catch (NumberFormatException ex) {
            LOGGER.log(Level.WARNING, "Erro de formato numérico", ex);
            mostrarErro("Valores numéricos inválidos. Verifique ano, km e valor da diária.");
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Erro ao cadastrar veículo", ex);
            mostrarErro("Erro ao cadastrar veículo: " + ex.getMessage());
        }
    }
    
    private void editarVeiculo() {
        try {
            if (!validarCampos()) {
                return;
            }
            
            Veiculo veiculo = criarVeiculoDosCampos();
            veiculo.setId(veiculoParaEdicao.getId());
            
            VeiculoDAO dao = new VeiculoDAO();
            dao.atualizar(veiculo);
            
            mostrarSucesso("Veículo atualizado com sucesso!");
            
            // Aguardar um pouco antes de fechar
            Timer timer = new Timer(2000, e -> dispose());
            timer.setRepeats(false);
            timer.start();
            
        } catch (NumberFormatException ex) {
            LOGGER.log(Level.WARNING, "Erro de formato numérico", ex);
            mostrarErro("Valores numéricos inválidos. Verifique ano, km e valor da diária.");
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Erro ao editar veículo", ex);
            mostrarErro("Erro ao editar veículo: " + ex.getMessage());
        }
    }
    
    private Veiculo criarVeiculoDosCampos() {
        String modelo = campoModelo.getText().trim();
        String marca = campoMarca.getText().trim();
        int ano = Integer.parseInt(campoAno.getText().trim());
        String placa = campoPlaca.getText().trim().toUpperCase();
        String cor = campoCor.getText().trim();
        String combustivel = (String) comboCombustivel.getSelectedItem();
        String status = (String) comboStatus.getSelectedItem();
        String observacoes = campoObservacoes.getText().trim();
        
        Veiculo veiculo = new Veiculo();
        veiculo.setModelo(modelo);
        veiculo.setMarca(marca);
        veiculo.setAno(ano);
        veiculo.setPlaca(placa.isEmpty() ? null : placa);
        veiculo.setCor(cor.isEmpty() ? null : cor);
        veiculo.setCombustivel(combustivel);
        veiculo.setStatus(status);
        veiculo.setObservacoes(observacoes.isEmpty() ? null : observacoes);
        
        // Tratar KM Atual
        String kmText = campoKmAtual.getText().trim();
        if (!kmText.isEmpty()) {
            veiculo.setKmAtual(new BigDecimal(kmText));
        } else {
            veiculo.setKmAtual(BigDecimal.ZERO);
        }
        
        // Tratar Valor Diária
        String valorText = campoValorDiaria.getText().trim();
        if (!valorText.isEmpty()) {
            veiculo.setValorDiaria(new BigDecimal(valorText));
        } else {
            veiculo.setValorDiaria(null);
        }
        
        return veiculo;
    }

    private boolean validarCampos() {
        String modelo = campoModelo.getText().trim();
        String marca = campoMarca.getText().trim();
        String ano = campoAno.getText().trim();
        String placa = campoPlaca.getText().trim();
        
        if (modelo.isEmpty()) {
            mostrarErro("Modelo é obrigatório!");
            campoModelo.requestFocus();
            return false;
        }
        
        if (marca.isEmpty()) {
            mostrarErro("Marca é obrigatória!");
            campoMarca.requestFocus();
            return false;
        }
        
        if (ano.isEmpty()) {
            mostrarErro("Ano é obrigatório!");
            campoAno.requestFocus();
            return false;
        }
        
        try {
            int anoInt = Integer.parseInt(ano);
            int anoAtual = java.time.Year.now().getValue();
            if (anoInt < 1900 || anoInt > anoAtual + 1) {
                mostrarErro("Ano deve estar entre 1900 e " + (anoAtual + 1));
                campoAno.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            mostrarErro("Ano deve ser um número válido!");
            campoAno.requestFocus();
            return false;
        }
        
        // Validar placa apenas se preenchida
        if (!placa.isEmpty()) {
            if (!placa.matches("[A-Z]{3}[0-9]{4}") && !placa.matches("[A-Z]{3}[0-9][A-Z][0-9]{2}")) {
                mostrarErro("Placa deve estar no formato ABC1234 ou ABC1D23!");
                campoPlaca.requestFocus();
                return false;
            }
        }
        
        // Validar valor diária se preenchido
        String valorDiaria = campoValorDiaria.getText().trim();
        if (!valorDiaria.isEmpty()) {
            try {
                BigDecimal valor = new BigDecimal(valorDiaria);
                if (valor.compareTo(BigDecimal.ZERO) <= 0) {
                    mostrarErro("Valor da diária deve ser maior que zero!");
                    campoValorDiaria.requestFocus();
                    return false;
                }
            } catch (NumberFormatException e) {
                mostrarErro("Valor da diária deve ser um número válido!");
                campoValorDiaria.requestFocus();
                return false;
            }
        }
        
        // Validar KM se preenchido
        String kmAtual = campoKmAtual.getText().trim();
        if (!kmAtual.isEmpty()) {
            try {
                BigDecimal km = new BigDecimal(kmAtual);
                if (km.compareTo(BigDecimal.ZERO) < 0) {
                    mostrarErro("Km atual não pode ser negativo!");
                    campoKmAtual.requestFocus();
                    return false;
                }
            } catch (NumberFormatException e) {
                mostrarErro("Km atual deve ser um número válido!");
                campoKmAtual.requestFocus();
                return false;
            }
        }
        
        return true;
    }

    private void limparCampos() {
        campoModelo.setText("");
        campoMarca.setText("");
        campoAno.setText("");
        campoPlaca.setText("");
        campoCor.setText("");
        comboCombustivel.setSelectedIndex(0);
        campoKmAtual.setText("");
        campoValorDiaria.setText("");
        comboStatus.setSelectedIndex(0);
        campoObservacoes.setText("");
        lblStatus.setText(" ");
        campoModelo.requestFocus();
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
