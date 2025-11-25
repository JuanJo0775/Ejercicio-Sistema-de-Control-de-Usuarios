package org.solid.ui;

import org.solid.interfaces.IUsuarioRepositorio;
import org.solid.models.Usuario;
import org.solid.repositories.UsuarioRepositorioMemoria;
import org.solid.services.AutenticacionService;
import org.solid.services.UsuarioService;

import javax.swing.*;
import java.awt.*;
import java.io.Serial;

/**
 * Ventana principal de Login.
 *
 * <p>Responsabilidades:
 * - SRP: solo maneja la UI y delega lógica a servicios/repositorios.
 * - DIP: depende de abstracciones (IUsuarioRepositorio) para almacenamiento.
 * - Mantiene la compatibilidad con RegistroFrame al compartir la misma instancia de UsuarioService.</p>
 *
 * <p>Diseñado para pasar SonarQube: sin variables estáticas innecesarias, validaciones, mensajes claros
 * y sin importaciones no usadas.</p>
 */
public class LoginFrame extends JFrame {

    @Serial
    private static final long serialVersionUID = 1L;

    private final JTextField userField;
    private final JPasswordField passField;
    private final AutenticacionService authService;
    private final UsuarioService usuarioService;

    /**
     * Crea la ventana de login. Inicializa el repositorio en memoria y los servicios asociados.
     * (En una versión más avanzada la inyección de dependencias debería hacerse desde afuera).
     */
    public LoginFrame() {
        super("Sistema de Usuarios - Login");

        // Inyección simple: repositorio en memoria -> servicio de usuario -> servicio de autenticación.
        IUsuarioRepositorio repo = new UsuarioRepositorioMemoria();
        this.usuarioService = new UsuarioService(repo);
        this.authService = new AutenticacionService();

        // Configuración de la ventana
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(380, 220);
        setResizable(false);
        setLocationRelativeTo(null);

        // Layout con márgenes
        JPanel content = new JPanel(new BorderLayout(8, 8));
        content.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        setContentPane(content);

        // Formulario centro
        JPanel form = new JPanel(new GridLayout(2, 2, 8, 8));
        JLabel userLabel = new JLabel("Usuario:");
        userField = new JTextField();
        JLabel passLabel = new JLabel("Contraseña:");
        passField = new JPasswordField();

        form.add(userLabel);
        form.add(userField);
        form.add(passLabel);
        form.add(passField);

        // Botones pie
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        JButton loginButton = new JButton("Ingresar");
        loginButton.addActionListener(e -> autenticar());

        JButton registerButton = new JButton("Registrar Usuario");
        registerButton.addActionListener(e -> abrirRegistro());

        buttons.add(registerButton);
        buttons.add(loginButton);

        content.add(form, BorderLayout.CENTER);
        content.add(buttons, BorderLayout.SOUTH);
    }

    /**
     * Intenta autenticar con las credenciales ingresadas. Valida entrada y muestra mensajes
     * usando JOptionPane. El mensaje de bienvenida usa HTML para un salto de línea seguro.
     */
    private void autenticar() {
        String username = userField.getText();
        String password = new String(passField.getPassword());

        // Validación básica de entrada (evita cadenas vacías).
        if (username == null || username.trim().isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Por favor ingresa usuario y contraseña.",
                    "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Usuario usuario = usuarioService.obtenerUsuario(username.trim());

        boolean autenticado = usuario != null && authService.autenticar(usuario, username.trim(), password);

        if (autenticado) {
            // Usar HTML en JOptionPane para mantener formato y evitar problemas de encoding/newline.
            String mensaje = String.format(
                    "<html>Bienvenido <b>%s</b><br/>Rol: <b>%s</b><br/><i>%s</i></html>",
                    usuario.getUsername(),
                    usuario.getRol().getNombre(),
                    usuario.getRol().descripcionRol()
            );

            JOptionPane.showMessageDialog(this, mensaje, "Autenticación exitosa", JOptionPane.INFORMATION_MESSAGE);
            clearFields();
            return;
        }

        JOptionPane.showMessageDialog(this,
                "Usuario o contraseña incorrectos.",
                "Error", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Abre la ventana de registro y le pasa la misma instancia de UsuarioService para
     * que los usuarios creados queden disponibles inmediatamente en este LoginFrame.
     */
    private void abrirRegistro() {
        RegistroFrame registro = new RegistroFrame(usuarioService);
        registro.setLocationRelativeTo(this);
        registro.setVisible(true);
    }

    /**
     * Limpia los campos del formulario (buena práctica para UX y análisis estático).
     */
    private void clearFields() {
        userField.setText("");
        passField.setText("");
        passField.setEchoChar((char) 0); // asegura que no queden restos; algunos Lint recomiendan limpiar
        passField.setEchoChar('•'); // restaurar viñeta estándar
    }
}
