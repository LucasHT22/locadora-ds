package view;

import dao.UsuarioDAO;
import model.Usuario;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Tela de login do sistema de locadora
 * @author lucas
 */
public class LoginFrame extends JFrame {
    private static final Logger LOGGER = Logger.getLogger(LoginFrame.class.getName());
    
    private JTextField txtLogin;
    private JPasswordField txtSenha;
    private JButton btnLogin;
    private JButton btnCancelar;
    private UsuarioDAO usuarioDAO;

    public LoginFrame() {
        this.usuarioDAO = new UsuarioDAO();
        inicializarComponentes();
        configurarEventos();
    }

    private void inicializarComponentes() {
        setTitle("Login - Sistema Locadora");
        setSize(400, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // Criação dos componentes
        txtLogin = new JTextField(15);
        txtSenha = new JPasswordField(15);
        btnLogin = new JButton("Entrar");
        btnCancelar = new JButton("Cancelar");

        // Configuração do layout
        JPanel painelPrincipal = new JPanel(new BorderLayout(10, 10));
        painelPrincipal.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Painel de campos
        JPanel painelCampos = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.EAST;
        painelCampos.add(new JLabel("Login:"), gbc);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        painelCampos.add(txtLogin, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.anchor = GridBagConstraints.EAST;
        painelCampos.add(new JLabel("Senha:"), gbc);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        painelCampos.add(txtSenha, gbc);

        // Painel de botões
        JPanel painelBotoes = new JPanel(new FlowLayout());
        painelBotoes.add(btnLogin);
        painelBotoes.add(btnCancelar);

        painelPrincipal.add(painelCampos, BorderLayout.CENTER);
        painelPrincipal.add(painelBotoes, BorderLayout.SOUTH);

        add(painelPrincipal);
        
        // Foco inicial no campo login
        txtLogin.requestFocus();
    }

    private void configurarEventos() {
        btnLogin.addActionListener(e -> autenticar());
        btnCancelar.addActionListener(e -> System.exit(0));

        // Enter para fazer login
        KeyListener enterListener = new KeyListener() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    autenticar();
                }
            }
            @Override public void keyReleased(KeyEvent e) {}
            @Override public void keyTyped(KeyEvent e) {}
        };

        txtLogin.addKeyListener(enterListener);
        txtSenha.addKeyListener(enterListener);
    }

    private void autenticar() {
        String login = txtLogin.getText().trim();
        char[] senhaChars = txtSenha.getPassword();
        String senha = new String(senhaChars);
        
        // Limpar array de senha da memória por segurança
        java.util.Arrays.fill(senhaChars, ' ');

        if (login.isEmpty() || senha.isEmpty()) {
            mostrarMensagem("Por favor, preencha todos os campos.", "Campos obrigatórios", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            // Cursor de espera
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            btnLogin.setEnabled(false);

            Usuario usuario = usuarioDAO.autenticar(login, senha);
            
            if (usuario != null) {
                mostrarMensagem("Bem-vindo, " + usuario.getNome() + "!", "Login realizado", JOptionPane.INFORMATION_MESSAGE);
                new MainDashboardFrame(usuario);
                dispose();
            } else {
                mostrarMensagem("Login ou senha incorretos.", "Erro de autenticação", JOptionPane.ERROR_MESSAGE);
                txtSenha.setText("");
                txtLogin.requestFocus();
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro durante autenticação", e);
            mostrarMensagem("Erro interno do sistema. Tente novamente.", "Erro", JOptionPane.ERROR_MESSAGE);
        } finally {
            setCursor(Cursor.getDefaultCursor());
            btnLogin.setEnabled(true);
        }
    }

    private void mostrarMensagem(String mensagem, String titulo, int tipo) {
        JOptionPane.showMessageDialog(this, mensagem, titulo, tipo);
    }
}
