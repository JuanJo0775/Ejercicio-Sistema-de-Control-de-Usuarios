package org.solid.ui;

import org.solid.interfaces.IPermisosAdmin;
import org.solid.interfaces.IPermisosBasicos;
import org.solid.interfaces.IPermisosInvitado;
import org.solid.interfaces.IUsuarioRepositorio;
import org.solid.models.Usuario;
import org.solid.repositories.UsuarioRepositorioMemoria;
import org.solid.services.AutenticacionService;
import org.solid.services.UsuarioService;

import javax.swing.*;
import java.awt.*;
import java.io.Serial;

/**
 * Ventana principal de Login del sistema.
 *
 * <p>Responsabilidades (SRP):
 * - Gestionar la interfaz gráfica de inicio de sesión
 * - Delegar validación a servicios especializados
 * - Coordinar navegación entre ventanas</p>
 *
 * <p>Principios aplicados:
 * - SRP: Solo maneja UI, delega lógica a servicios
 * - DIP: Depende de IUsuarioRepositorio (abstracción)
 * - ISP: Verifica permisos según interfaces específicas</p>
 *
 * @author Sistema SOLID
 * @version 2.0
 */
public class LoginFrame extends JFrame {

    @Serial
    private static final long serialVersionUID = 1L;

    // Constantes para validación
    private static final int MIN_USERNAME_LENGTH = 3;
    private static final int MIN_PASSWORD_LENGTH = 4;
    private static final int WINDOW_WIDTH = 420;
    private static final int WINDOW_HEIGHT = 280;

    // Componentes UI
    private JTextField userField;
    private JPasswordField passField;

    // Servicios (inyección de dependencias)
    private final transient AutenticacionService authService;
    private final transient UsuarioService usuarioService;


    /**
     * Constructor principal. Inicializa servicios y componentes gráficos.
     *
     * En producción, los servicios deberían inyectarse desde un contenedor IoC.
     */
    public LoginFrame() {
        super("Sistema de Control de Usuarios - Inicio de Sesión");

        // Inicialización de servicios (DIP: depende de abstracción IUsuarioRepositorio)
        IUsuarioRepositorio repo = new UsuarioRepositorioMemoria();
        this.usuarioService = new UsuarioService(repo);
        this.authService = new AutenticacionService();

        // Configuración de ventana
        configurarVentana();

        // Construcción de interfaz
        construirInterfaz();
    }

    /**
     * Configura las propiedades básicas de la ventana.
     */
    private void configurarVentana() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setResizable(false);
        setLocationRelativeTo(null);
    }

    /**
     * Construye todos los componentes de la interfaz gráfica.
     * Aplica SRP: método específico para construcción de UI.
     */
    private void construirInterfaz() {
        // Panel principal con márgenes
        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        setContentPane(contentPanel);

        // Título superior
        JLabel tituloLabel = new JLabel("Bienvenido al Sistema", SwingConstants.CENTER);
        tituloLabel.setFont(new Font("Arial", Font.BOLD, 18));
        contentPanel.add(tituloLabel, BorderLayout.NORTH);

        // Formulario central
        JPanel formPanel = crearPanelFormulario();
        contentPanel.add(formPanel, BorderLayout.CENTER);

        // Botones inferiores
        JPanel buttonPanel = crearPanelBotones();
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);
    }

    /**
     * Crea el panel del formulario con campos de usuario y contraseña.
     *
     * @return Panel con formulario completo
     */
    private JPanel crearPanelFormulario() {
        JPanel formPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createTitledBorder("Credenciales"));

        // Campo usuario
        JLabel userLabel = new JLabel("Usuario:");
        userField = new JTextField(20);
        userField.setToolTipText("Ingrese su nombre de usuario (mínimo " + MIN_USERNAME_LENGTH + " caracteres)");

        // Campo contraseña
        JLabel passLabel = new JLabel("Contraseña:");
        passField = new JPasswordField(20);
        passField.setToolTipText("Ingrese su contraseña (mínimo " + MIN_PASSWORD_LENGTH + " caracteres)");

        // Enter para login
        passField.addActionListener(e -> intentarAutenticacion());

        formPanel.add(userLabel);
        formPanel.add(userField);
        formPanel.add(passLabel);
        formPanel.add(passField);

        return formPanel;
    }

    /**
     * Crea el panel de botones (Ingresar y Registrar).
     *
     * @return Panel con botones de acción
     */
    private JPanel crearPanelBotones() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));

        JButton registerButton = new JButton("Registrar Nuevo Usuario");
        registerButton.addActionListener(e -> abrirVentanaRegistro());
        registerButton.setToolTipText("Crear una nueva cuenta en el sistema");

        JButton loginButton = new JButton("Ingresar");
        loginButton.addActionListener(e -> intentarAutenticacion());
        loginButton.setToolTipText("Iniciar sesión con sus credenciales");

        // Estilo para botón principal
        loginButton.setBackground(new Color(70, 130, 180));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);

        buttonPanel.add(registerButton);
        buttonPanel.add(loginButton);

        return buttonPanel;
    }

    /**
     * Valida credenciales y autentica al usuario.
     * Aplica SRP: método específico para lógica de autenticación.
     * Aplica ISP: Verifica permisos según interfaces específicas del rol.
     */
    private void intentarAutenticacion() {
        String username = userField.getText();
        char[] passwordChars = passField.getPassword();
        String password = new String(passwordChars);

        // Validación de entrada
        if (!validarEntrada(username, password)) {
            limpiarPassword(passwordChars);
            return;
        }

        // Buscar usuario en repositorio
        Usuario usuario = usuarioService.obtenerUsuario(username.trim());

        // Intentar autenticación
        boolean autenticado = usuario != null &&
                authService.autenticar(usuario, username.trim(), password);

        // Limpiar contraseña de memoria por seguridad
        limpiarPassword(passwordChars);

        if (autenticado) {
            mostrarPanelBienvenida(usuario);
            limpiarCampos();
        } else {
            mostrarErrorAutenticacion();
        }
    }

    /**
     * Valida que las credenciales cumplan requisitos mínimos.
     *
     * @param username Nombre de usuario ingresado
     * @param password Contraseña ingresada
     * @return true si las credenciales son válidas
     */
    private boolean validarEntrada(String username, String password) {
        if (username == null || username.trim().isEmpty()) {
            mostrarAdvertencia("El nombre de usuario es obligatorio.");
            return false;
        }

        if (username.trim().length() < MIN_USERNAME_LENGTH) {
            mostrarAdvertencia("El usuario debe tener al menos " +
                    MIN_USERNAME_LENGTH + " caracteres.");
            return false;
        }

        if (password.isEmpty()) {
            mostrarAdvertencia("La contraseña es obligatoria.");
            return false;
        }

        if (password.length() < MIN_PASSWORD_LENGTH) {
            mostrarAdvertencia("La contraseña debe tener al menos " +
                    MIN_PASSWORD_LENGTH + " caracteres.");
            return false;
        }

        return true;
    }

    /**
     * Muestra un panel detallado de bienvenida con información del usuario y sus permisos.
     * Aplica ISP: Detecta capacidades específicas según interfaces implementadas.
     *
     * @param usuario Usuario autenticado exitosamente
     */
    private void mostrarPanelBienvenida(Usuario usuario) {
        // Panel principal
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Información del usuario
        JPanel infoPanel = new JPanel(new GridLayout(3, 1, 5, 5));
        infoPanel.add(new JLabel("✓ Usuario: " + usuario.getUsername()));
        infoPanel.add(new JLabel("✓ Rol: " + usuario.getRol().getNombre()));
        infoPanel.add(new JLabel("✓ Descripción: " + usuario.getRol().descripcionRol()));

        panel.add(infoPanel, BorderLayout.NORTH);

        // Panel de permisos (ISP: interfaces específicas)
        JPanel permisosPanel = crearPanelPermisos(usuario);
        panel.add(permisosPanel, BorderLayout.CENTER);

        // Mostrar diálogo
        JOptionPane.showMessageDialog(this,
                panel,
                "¡Bienvenido al Sistema!",
                JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Crea un panel que muestra los permisos específicos del usuario.
     * Demuestra ISP: Solo verifica las interfaces que el rol implementa.
     *
     * @param usuario Usuario autenticado
     * @return Panel con lista de permisos
     */
    private JPanel crearPanelPermisos(Usuario usuario) {
        JPanel permisosPanel = new JPanel(new BorderLayout());
        permisosPanel.setBorder(BorderFactory.createTitledBorder("Permisos Disponibles"));

        DefaultListModel<String> permisosModel = new DefaultListModel<>();

        // ISP: Verificar solo las interfaces que necesita cada rol
        if (usuario.getRol() instanceof IPermisosBasicos basicos) {
            permisosModel.addElement("• " + basicos.acceder());
        }

        if (usuario.getRol() instanceof IPermisosAdmin admin) {
            permisosModel.addElement("• " + admin.gestionarUsuarios());
        }

        if (usuario.getRol() instanceof IPermisosInvitado invitado) {
            permisosModel.addElement("• " + invitado.vistaPublica());
        }

        JList<String> permisosList = new JList<>(permisosModel);
        permisosList.setEnabled(false);
        JScrollPane scrollPane = new JScrollPane(permisosList);
        scrollPane.setPreferredSize(new Dimension(300, 80));

        permisosPanel.add(scrollPane, BorderLayout.CENTER);

        return permisosPanel;
    }

    /**
     * Muestra mensaje de error cuando las credenciales son incorrectas.
     */
    private void mostrarErrorAutenticacion() {
        String mensajeError = """
            Usuario o contraseña incorrectos.

            Verifique sus credenciales e intente nuevamente.
            """;

        JOptionPane.showMessageDialog(
                this,
                mensajeError,
                "Error de Autenticación",
                JOptionPane.ERROR_MESSAGE
        );
    }


    /**
     * Muestra mensaje de advertencia genérico.
     *
     * @param mensaje Texto a mostrar
     */
    private void mostrarAdvertencia(String mensaje) {
        JOptionPane.showMessageDialog(this,
                mensaje,
                "Advertencia",
                JOptionPane.WARNING_MESSAGE);
    }

    /**
     * Abre la ventana de registro de nuevos usuarios.
     * Pasa la misma instancia de UsuarioService para mantener consistencia.
     */
    private void abrirVentanaRegistro() {
        RegistroFrame registroFrame = new RegistroFrame(usuarioService);
        registroFrame.setLocationRelativeTo(this);
        registroFrame.setVisible(true);
    }

    /**
     * Limpia los campos del formulario (buena práctica de seguridad y UX).
     */
    private void limpiarCampos() {
        userField.setText("");
        passField.setText("");
        userField.requestFocus();
    }

    /**
     * Limpia un array de caracteres por seguridad.
     * Previene que las contraseñas permanezcan en memoria.
     *
     * @param passwordChars Array de caracteres a limpiar
     */
    private void limpiarPassword(char[] passwordChars) {
        if (passwordChars != null) {
            java.util.Arrays.fill(passwordChars, ' ');
        }
    }
}