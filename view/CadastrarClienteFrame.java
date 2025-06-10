package view;

import dao.ClienteDAO;
import model.Cliente;
import util.ValidadorCPF;

import javax.swing.*;
import javax.swing.text.MaskFormatter;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.text.ParseException;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.Period;

/**
 * Frame para cadastro de clientes com validações e máscaras aprimoradas
 * @author lucas
 */
public class CadastrarClienteFrame extends JFrame {
    private JTextField campoNome;
    private JFormattedTextField campoCpf;
    private JFormattedTextField campoTelefone;
    private JTextField campoEmail;
    private JTextArea campoEndereco;
    private JFormattedTextField campoCnh;
    private JFormattedTextField campoDataNascimento;
    private JFormattedTextField campoCep;
    private JTextArea campoObservacoes;
    private JButton botaoCadastrar;
    private JButton botaoLimpar;
    private JLabel lblStatus;
    
    private ClienteDAO clienteDAO;

    public CadastrarClienteFrame() {
        this.clienteDAO = new ClienteDAO();
        initializeComponents();
        setupLayout();
        setupEvents();
        setVisible(true);
    }

    private void initializeComponents() {
        setTitle("Cadastrar Cliente");
        setSize(520, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);
        
        // Campos básicos
        campoNome = new JTextField(20);
        campoEmail = new JTextField(20);
        campoEndereco = new JTextArea(3, 20);
        campoEndereco.setLineWrap(true);
        campoEndereco.setWrapStyleWord(true);
        
        campoObservacoes = new JTextArea(3, 20);
        campoObservacoes.setLineWrap(true);
        campoObservacoes.setWrapStyleWord(true);
        
        setupMaskedFields();
        
        // Botões
        botaoCadastrar = new JButton("Cadastrar Cliente");
        botaoLimpar = new JButton("Limpar Campos");
        lblStatus = new JLabel(" ");
        
        // Estilização
        botaoCadastrar.setBackground(new Color(34, 139, 34));
        botaoCadastrar.setForeground(Color.WHITE);
        botaoCadastrar.setFocusPainted(false);
        botaoCadastrar.setPreferredSize(new Dimension(140, 30));
        
        botaoLimpar.setBackground(new Color(255, 140, 0));
        botaoLimpar.setForeground(Color.WHITE);
        botaoLimpar.setFocusPainted(false);
        botaoLimpar.setPreferredSize(new Dimension(140, 30));
        
        lblStatus.setHorizontalAlignment(SwingConstants.CENTER);
        lblStatus.setFont(new Font("Arial", Font.PLAIN, 12));
    }

    private void setupMaskedFields() {
        try {
            // Máscara para CPF
            MaskFormatter cpfFormatter = new MaskFormatter("###.###.###-##");
            cpfFormatter.setPlaceholderCharacter('_');
            cpfFormatter.setValidCharacters("0123456789");
            campoCpf = new JFormattedTextField(cpfFormatter);
            campoCpf.setColumns(20);
            
            // Máscara para Telefone com validação automática
            MaskFormatter telFormatter = new MaskFormatter("(##) #####-####");
            telFormatter.setPlaceholderCharacter('_');
            telFormatter.setValidCharacters("0123456789");
            campoTelefone = new JFormattedTextField(telFormatter);
            campoTelefone.setColumns(20);
            
            // Máscara para CNH
            MaskFormatter cnhFormatter = new MaskFormatter("###########");
            cnhFormatter.setPlaceholderCharacter('_');
            cnhFormatter.setValidCharacters("0123456789");
            campoCnh = new JFormattedTextField(cnhFormatter);
            campoCnh.setColumns(20);
            
            // Máscara para Data de Nascimento
            MaskFormatter dataFormatter = new MaskFormatter("##/##/####");
            dataFormatter.setPlaceholderCharacter('_');
            dataFormatter.setValidCharacters("0123456789");
            campoDataNascimento = new JFormattedTextField(dataFormatter);
            campoDataNascimento.setColumns(20);
            
            // Máscara para CEP
            MaskFormatter cepFormatter = new MaskFormatter("#####-###");
            cepFormatter.setPlaceholderCharacter('_');
            cepFormatter.setValidCharacters("0123456789");
            campoCep = new JFormattedTextField(cepFormatter);
            campoCep.setColumns(20);
            
        } catch (ParseException e) {
            // Fallback para campos sem máscara em caso de erro
            campoCpf = new JFormattedTextField();
            campoTelefone = new JFormattedTextField();
            campoCnh = new JFormattedTextField();
            campoDataNascimento = new JFormattedTextField();
            campoCep = new JFormattedTextField();
            
            // Definir tamanho dos campos de fallback
            campoCpf.setColumns(20);
            campoTelefone.setColumns(20);
            campoCnh.setColumns(20);
            campoDataNascimento.setColumns(20);
            campoCep.setColumns(20);
        }
        
        // Adicionar tooltips informativos
        campoCpf.setToolTipText("Digite o CPF no formato: 000.000.000-00");
        campoTelefone.setToolTipText("Digite o telefone no formato: (00) 00000-0000");
        campoCnh.setToolTipText("Digite a CNH com 11 dígitos (opcional)");
        campoDataNascimento.setToolTipText("Digite a data no formato: DD/MM/AAAA");
        campoCep.setToolTipText("Digite o CEP no formato: 00000-000");
        campoEmail.setToolTipText("Digite um email válido (opcional)");
    }

    private void setupLayout() {
        setLayout(new BorderLayout());
        
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Título
        JLabel titleLabel = new JLabel("Cadastro de Cliente");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(new Color(34, 139, 34));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        mainPanel.add(titleLabel, gbc);
        
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;
        
        int row = 1;
        
        // Nome
        gbc.gridx = 0; gbc.gridy = row;
        JLabel lblNome = new JLabel("Nome Completo: *");
        lblNome.setFont(new Font("Arial", Font.BOLD, 12));
        mainPanel.add(lblNome, gbc);
        gbc.gridx = 1;
        mainPanel.add(campoNome, gbc);
        
        // CPF
        gbc.gridx = 0; gbc.gridy = ++row;
        JLabel lblCpf = new JLabel("CPF: *");
        lblCpf.setFont(new Font("Arial", Font.BOLD, 12));
        mainPanel.add(lblCpf, gbc);
        gbc.gridx = 1;
        mainPanel.add(campoCpf, gbc);
        
        // Telefone
        gbc.gridx = 0; gbc.gridy = ++row;
        JLabel lblTel = new JLabel("Telefone: *");
        lblTel.setFont(new Font("Arial", Font.BOLD, 12));
        mainPanel.add(lblTel, gbc);
        gbc.gridx = 1;
        mainPanel.add(campoTelefone, gbc);
        
        // Email
        gbc.gridx = 0; gbc.gridy = ++row;
        mainPanel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        mainPanel.add(campoEmail, gbc);
        
        // Data de Nascimento
        gbc.gridx = 0; gbc.gridy = ++row;
        mainPanel.add(new JLabel("Data Nascimento:"), gbc);
        gbc.gridx = 1;
        mainPanel.add(campoDataNascimento, gbc);
        
        // CNH
        gbc.gridx = 0; gbc.gridy = ++row;
        mainPanel.add(new JLabel("CNH:"), gbc);
        gbc.gridx = 1;
        mainPanel.add(campoCnh, gbc);
        
        // CEP
        gbc.gridx = 0; gbc.gridy = ++row;
        mainPanel.add(new JLabel("CEP:"), gbc);
        gbc.gridx = 1;
        mainPanel.add(campoCep, gbc);
        
        // Endereço
        gbc.gridx = 0; gbc.gridy = ++row;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        mainPanel.add(new JLabel("Endereço:"), gbc);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        JScrollPane scrollEndereco = new JScrollPane(campoEndereco);
        scrollEndereco.setPreferredSize(new Dimension(200, 70));
        scrollEndereco.setBorder(BorderFactory.createLoweredBevelBorder());
        mainPanel.add(scrollEndereco, gbc);
        
        // Observações
        gbc.gridx = 0; gbc.gridy = ++row;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        mainPanel.add(new JLabel("Observações:"), gbc);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        JScrollPane scrollObs = new JScrollPane(campoObservacoes);
        scrollObs.setPreferredSize(new Dimension(200, 70));
        scrollObs.setBorder(BorderFactory.createLoweredBevelBorder());
        mainPanel.add(scrollObs, gbc);
        
        // Botões
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonPanel.add(botaoCadastrar);
        buttonPanel.add(botaoLimpar);
        
        gbc.gridx = 0; gbc.gridy = ++row; gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        mainPanel.add(buttonPanel, gbc);
        
        // Status
        gbc.gridy = ++row;
        gbc.insets = new Insets(10, 8, 8, 8);
        mainPanel.add(lblStatus, gbc);
        
        // Nota
        JLabel notaLabel = new JLabel("* Campos obrigatórios");
        notaLabel.setFont(new Font("Arial", Font.ITALIC, 11));
        notaLabel.setForeground(Color.GRAY);
        gbc.gridy = ++row;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 8, 8, 8);
        mainPanel.add(notaLabel, gbc);
        
        add(mainPanel, BorderLayout.CENTER);
    }

    private void setupEvents() {
        botaoCadastrar.addActionListener(e -> cadastrarCliente());
        botaoLimpar.addActionListener(e -> limparCampos());
        
        // Navegação entre campos com Enter
        campoNome.addActionListener(e -> campoCpf.requestFocus());
        campoCpf.addActionListener(e -> campoTelefone.requestFocus());
        campoTelefone.addActionListener(e -> campoEmail.requestFocus());
        campoEmail.addActionListener(e -> campoDataNascimento.requestFocus());
        campoDataNascimento.addActionListener(e -> campoCnh.requestFocus());
        campoCnh.addActionListener(e -> campoCep.requestFocus());
        campoCep.addActionListener(e -> campoEndereco.requestFocus());
        
        // Validação em tempo real do CPF
        campoCpf.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                validarCpfEmTempoReal();
            }
        });
        
        // Formatação automática do nome (primeira letra maiúscula)
        campoNome.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                String nome = campoNome.getText().trim();
                if (!nome.isEmpty()) {
                    // Converte para formato nome próprio
                    String[] palavras = nome.toLowerCase().split("\\s+");
                    StringBuilder nomeFormatado = new StringBuilder();
                    for (String palavra : palavras) {
                        if (palavra.length() > 0) {
                            nomeFormatado.append(Character.toUpperCase(palavra.charAt(0)))
                                        .append(palavra.substring(1))
                                        .append(" ");
                        }
                    }
                    campoNome.setText(nomeFormatado.toString().trim());
                }
            }
        });
        
        // Validação do email em tempo real
        campoEmail.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                String email = campoEmail.getText().trim();
                if (!email.isEmpty() && !isEmailValido(email)) {
                    lblStatus.setText("Email inválido!");
                    lblStatus.setForeground(Color.RED);
                    Timer timer = new Timer(3000, evt -> lblStatus.setText(" "));
                    timer.setRepeats(false);
                    timer.start();
                }
            }
        });
    }
    
    private void validarCpfEmTempoReal() {
        String cpf = ValidadorCPF.limparCPF(campoCpf.getText());
        if (cpf.length() == 11 && !ValidadorCPF.isValid(cpf)) {
            lblStatus.setText("CPF inválido!");
            lblStatus.setForeground(Color.RED);
            Timer timer = new Timer(3000, e -> lblStatus.setText(" "));
            timer.setRepeats(false);
            timer.start();
        }
    }

    private void cadastrarCliente() {
        try {
            if (!validarCampos()) {
                return;
            }
            
            // Verificar se CPF já existe
            String cpfLimpo = ValidadorCPF.limparCPF(campoCpf.getText());
            Cliente clienteExistente = clienteDAO.buscarPorCpf(cpfLimpo);
            if (clienteExistente != null) {
                mostrarErro("CPF já cadastrado no sistema!");
                campoCpf.requestFocus();
                return;
            }
            
            String nome = campoNome.getText().trim();
            String cpf = cpfLimpo;
            String telefone = campoTelefone.getText().replaceAll("[()\\s-]", "");
            String email = campoEmail.getText().trim();
            String endereco = campoEndereco.getText().trim();
            String cnh = campoCnh.getText().replaceAll("[^0-9]", "");
            String cep = campoCep.getText().replaceAll("[^0-9]", "");
            String observacoes = campoObservacoes.getText().trim();
            
            // Converter data de nascimento para LocalDate
            LocalDate dataNascimento = null;
            String dataTexto = campoDataNascimento.getText().replaceAll("[/_]", "");
            if (!dataTexto.isEmpty() && dataTexto.length() == 8) {
                try {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMyyyy");
                    dataNascimento = LocalDate.parse(dataTexto, formatter);
                } catch (DateTimeParseException ex) {
                    mostrarErro("Data de nascimento inválida!");
                    campoDataNascimento.requestFocus();
                    return;
                }
            }
            
            // Criar cliente
            Cliente cliente = new Cliente();
            cliente.setNome(nome);
            cliente.setCpf(cpf);
            cliente.setTelefone(telefone);
            cliente.setEmail(email.isEmpty() ? null : email);
            cliente.setEndereco(endereco.isEmpty() ? null : endereco);
            cliente.setCnh(cnh.isEmpty() ? null : cnh);
            cliente.setDataNascimento(dataNascimento);
            cliente.setObservacoes(observacoes.isEmpty() ? null : observacoes);
            
            // Inserir no banco
            clienteDAO.inserir(cliente);
            
            mostrarSucesso("Cliente cadastrado com sucesso!");
            limparCampos();
            
        } catch (Exception ex) {
            mostrarErro("Erro ao cadastrar cliente: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private boolean validarCampos() {
        String nome = campoNome.getText().trim();
        String cpf = ValidadorCPF.limparCPF(campoCpf.getText());
        String telefone = campoTelefone.getText().replaceAll("[-()\\s]", "");
        String cnh = campoCnh.getText().replaceAll("[^0-9]", "");
        String dataNasc = campoDataNascimento.getText().replaceAll("[/_]", "");
        String cep = campoCep.getText().replaceAll("[^0-9]", "");
        
        // Validação do nome
        if (nome.isEmpty()) {
            mostrarErro("Nome é obrigatório!");
            campoNome.requestFocus();
            return false;
        }
        
        if (nome.length() < 3) {
            mostrarErro("Nome deve ter pelo menos 3 caracteres!");
            campoNome.requestFocus();
            return false;
        }
        
        // Validação do CPF
        if (cpf.length() != 11) {
            mostrarErro("CPF deve ter 11 dígitos!");
            campoCpf.requestFocus();
            return false;
        }
        
        if (!ValidadorCPF.isValid(cpf)) {
            mostrarErro("CPF inválido!");
            campoCpf.requestFocus();
            return false;
        }
        
        // Validação do telefone
        if (telefone.length() != 11) {
            mostrarErro("Telefone deve ter 11 dígitos!");
            campoTelefone.requestFocus();
            return false;
        }
        
        // Validação da CNH (não obrigatória)
        if (!cnh.isEmpty() && cnh.length() != 11) {
            mostrarErro("CNH deve ter 11 dígitos!");
            campoCnh.requestFocus();
            return false;
        }
        
        // Validação do CEP (não obrigatório)
        if (!cep.isEmpty() && cep.length() != 8) {
            mostrarErro("CEP deve ter 8 dígitos!");
            campoCep.requestFocus();
            return false;
        }
        
        // Validação da data de nascimento (não obrigatória)
        if (!dataNasc.isEmpty()) {
            if (dataNasc.length() != 8) {
                mostrarErro("Data de nascimento deve ter formato DD/MM/AAAA!");
                campoDataNascimento.requestFocus();
                return false;
            }
            
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMyyyy");
                LocalDate dataNascimento = LocalDate.parse(dataNasc, formatter);
                LocalDate hoje = LocalDate.now();
                
                Period idade = Period.between(dataNascimento, hoje);
                
                if (idade.getYears() < 18) {
                    mostrarErro("Cliente deve ter pelo menos 18 anos!");
                    campoDataNascimento.requestFocus();
                    return false;
                }
                
                if (idade.getYears() > 120) {
                    mostrarErro("Data de nascimento inválida!");
                    campoDataNascimento.requestFocus();
                    return false;
                }
                
            } catch (DateTimeParseException e) {
                mostrarErro("Data de nascimento inválida!");
                campoDataNascimento.requestFocus();
                return false;
            }
        }
        
        // Validação do email (se preenchido)
        String email = campoEmail.getText().trim();
        if (!email.isEmpty() && !isEmailValido(email)) {
            mostrarErro("Email inválido!");
            campoEmail.requestFocus();
            return false;
        }
        
        return true;
    }

    private boolean isEmailValido(String email) {
        return email.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$");
    }

    private void limparCampos() {
        campoNome.setText("");
        campoCpf.setValue(null);
        campoTelefone.setValue(null);
        campoEmail.setText("");
        campoEndereco.setText("");
        campoCnh.setValue(null);
        campoDataNascimento.setValue(null);
        campoCep.setValue(null);
        campoObservacoes.setText("");
        lblStatus.setText(" ");
        campoNome.requestFocus();
    }

    private void mostrarSucesso(String mensagem) {
        lblStatus.setText(mensagem);
        lblStatus.setForeground(new Color(34, 139, 34));
        lblStatus.setFont(new Font("Arial", Font.BOLD, 12));
        
        Timer timer = new Timer(4000, e -> {
            lblStatus.setText(" ");
            lblStatus.setFont(new Font("Arial", Font.PLAIN, 12));
        });
        timer.setRepeats(false);
        timer.start();
    }

    private void mostrarErro(String mensagem) {
        lblStatus.setText(mensagem);
        lblStatus.setForeground(Color.RED);
        lblStatus.setFont(new Font("Arial", Font.BOLD, 12));
        
        Timer timer = new Timer(5000, e -> {
            lblStatus.setText(" ");
            lblStatus.setFont(new Font("Arial", Font.PLAIN, 12));
        });
        timer.setRepeats(false);
        timer.start();
    }
}
