package org.solid.ui;

import org.solid.models.Administrador;
import org.solid.models.Invitado;
import org.solid.models.Rol;
import org.solid.models.UsuarioRegular;
import org.solid.services.UsuarioService;

import javax.swing.*;
import java.awt.*;
import java.io.Serial;
import java.util.regex.Pattern;

/**
 * Ventana para registro de nuevos usuarios en el sistema.
 *
 * <p>Responsabilidades (SRP):
 * - Gestionar interfaz de registro
 * - Validar datos de entrada
 * - Delegar creación de usuarios al servicio</p>
 *
 * <p>Principios aplicados:
 * - SRP: Solo maneja UI de registro
 * - DIP: Recibe UsuarioService por constructor (abstracción)
 * - OCP: Fácil agregar nuevos roles al combo sin modificar lógica</p>
 *
 * @author Sistema SOLID
 * @version 2.0
 */
public class RegistroFrame extends JFrame {

    // ================= CONSTANTES PARA EVITAR STRINGS DUPLICADOS =================

    private static final String DESCRIPCION_ROL_TEMPLATE = """
        Rol: %s

        %s

        %s
        """;

    private static final String PERMISOS_ADMIN = """
        Permisos:
        • Acceso total
        • Gestión de usuarios
        • Configuración del sistema
        """;

    private static final String PERMISOS_USUARIO = """
        Permisos:
        • Acceso a funciones básicas
        • Lectura y escritura
        • Perfil personalizado
        """;

    private static final String PERMISOS_INVITADO = """
        Permisos:
        • Solo lectura
        • Vista pública
        • Acceso limitado
        """;

    // ================= CONSTANTES PARA MENSAJES =================

    private static final String MSG_USERNAME_REQUIRED = "El nombre de usuario es obligatorio.";
    private static final String MSG_USERNAME_MIN = "El usuario debe tener al menos %d caracteres.";
    private static final String MSG_USERNAME_MAX = "El usuario no puede exceder %d caracteres.";
    private static final String MSG_USERNAME_PATTERN = "El usuario solo puede contener letras, números y guión bajo.";

    private static final String MSG_PASSWORD_REQUIRED = "La contraseña es obligatoria.";
    private static final String MSG_PASSWORD_MIN = "La contraseña debe tener al menos %d caracteres.";
    private static final String MSG_PASSWORD_MAX = "La contraseña no puede exceder %d caracteres.";

    private static final String MSG_PASSWORD_NO_MATCH = """
        Las contraseñas no coinciden.

        Por favor, verifique que ambos campos sean idénticos.
        """;

    @Serial
    private static final long serialVersionUID = 1L;

    // Constantes de validación
    private static final int MIN_USERNAME_LENGTH = 3;
    private static final int MAX_USERNAME_LENGTH = 20;
    private static final int MIN_PASSWORD_LENGTH = 4;
    private static final int MAX_PASSWORD_LENGTH = 50;
    private static final Pattern USERNAME_PATTERN = Pattern.compile("^\\w+$");

    // Dimensiones de ventana
    private static final int WINDOW_WIDTH = 450;
    private static final int WINDOW_HEIGHT = 320;

    // Componentes UI
    private JTextField userField;
    private JPasswordField passField;
    private JPasswordField confirmPassField;
    private JComboBox<String> rolCombo;
    private JTextArea descripcionRolArea;

    // Servicio (inyección de dependencias)
    private final transient UsuarioService usuarioService;

    /**
     * Constructor principal. Recibe el servicio de usuarios por inyección.
     *
     * @param usuarioService Servicio para gestionar usuarios (no puede ser null)
     * @throws IllegalArgumentException si usuarioService es null
     */
    public RegistroFrame(final UsuarioService usuarioService) {
        super("Registro de Nuevo Usuario");

        // Validación de dependencia (fail-fast)
        if (usuarioService == null) {
            throw new IllegalArgumentException("El servicio de usuarios no puede ser nulo");
        }
        this.usuarioService = usuarioService;

        // Configuración de ventana
        configurarVentana();

        // Construcción de interfaz
        construirInterfaz();
    }

    /**
     * Configura las propiedades básicas de la ventana.
     */
    private void configurarVentana() {
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setResizable(false);
        setLocationRelativeTo(null);
    }

    /**
     * Construye toda la interfaz gráfica del formulario de registro.
     */
    private void construirInterfaz() {
        // Panel principal
        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        setContentPane(contentPanel);

        // Título
        JLabel tituloLabel = new JLabel("Crear Nueva Cuenta", SwingConstants.CENTER);
        tituloLabel.setFont(new Font("Arial", Font.BOLD, 16));
        contentPanel.add(tituloLabel, BorderLayout.NORTH);

        // Formulario central
        JPanel formPanel = crearPanelFormulario();
        contentPanel.add(formPanel, BorderLayout.CENTER);

        // Panel de descripción de rol
        JPanel descripcionPanel = crearPanelDescripcionRol();
        contentPanel.add(descripcionPanel, BorderLayout.EAST);

        // Botones inferiores
        JPanel buttonPanel = crearPanelBotones();
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);
    }

    /**
     * Crea el panel del formulario con todos los campos.
     *
     * @return Panel con formulario completo
     */
    private JPanel crearPanelFormulario() {
        JPanel formPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createTitledBorder("Datos del Usuario"));

        // Campo usuario
        formPanel.add(new JLabel("Usuario:"));
        userField = new JTextField();
        userField.setToolTipText("Solo letras, números y guión bajo (" +
                MIN_USERNAME_LENGTH + "-" + MAX_USERNAME_LENGTH + " caracteres)");
        formPanel.add(userField);

        // Campo contraseña
        formPanel.add(new JLabel("Contraseña:"));
        passField = new JPasswordField();
        passField.setToolTipText("Mínimo " + MIN_PASSWORD_LENGTH + " caracteres");
        formPanel.add(passField);

        // Campo confirmar contraseña
        formPanel.add(new JLabel("Confirmar:"));
        confirmPassField = new JPasswordField();
        confirmPassField.setToolTipText("Debe coincidir con la contraseña");
        confirmPassField.addActionListener(e -> intentarRegistro());
        formPanel.add(confirmPassField);

        // Selector de rol
        formPanel.add(new JLabel("Rol:"));
        rolCombo = new JComboBox<>(new String[]{"Usuario", "Administrador", "Invitado"});
        rolCombo.setToolTipText("Seleccione el nivel de acceso del usuario");
        rolCombo.addActionListener(e -> actualizarDescripcionRol());
        formPanel.add(rolCombo);

        return formPanel;
    }

    /**
     * Crea el panel que muestra la descripción del rol seleccionado.
     *
     * @return Panel con área de texto informativa
     */
    private JPanel crearPanelDescripcionRol() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Información del Rol"));
        panel.setPreferredSize(new Dimension(180, 0));

        descripcionRolArea = new JTextArea();
        descripcionRolArea.setEditable(false);
        descripcionRolArea.setLineWrap(true);
        descripcionRolArea.setWrapStyleWord(true);
        descripcionRolArea.setBackground(new Color(240, 240, 240));
        descripcionRolArea.setFont(new Font("Arial", Font.PLAIN, 11));

        // Descripción inicial
        actualizarDescripcionRol();

        JScrollPane scrollPane = new JScrollPane(descripcionRolArea);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    /**
     * Actualiza la descripción del rol según la selección del combo.
     * Aplica OCP: Fácil agregar nuevos roles sin modificar este método.
     */
    private void actualizarDescripcionRol() {
        String rolSeleccionado = (String) rolCombo.getSelectedItem();
        Rol rol = mapearRol(rolSeleccionado);

        String permisos = "";

        if (rol instanceof Administrador) {
            permisos = PERMISOS_ADMIN;
        } else if (rol instanceof UsuarioRegular) {
            permisos = PERMISOS_USUARIO;
        } else if (rol instanceof Invitado) {
            permisos = PERMISOS_INVITADO;
        }

        String descripcionFinal = String.format(
                DESCRIPCION_ROL_TEMPLATE,
                rol.getNombre(),
                rol.descripcionRol(),
                permisos
        );

        descripcionRolArea.setText(descripcionFinal);
        descripcionRolArea.setCaretPosition(0);
    }


    /**
     * Crea el panel de botones (Crear y Cancelar).
     *
     * @return Panel con botones de acción
     */
    private JPanel crearPanelBotones() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));

        JButton cancelButton = new JButton("Cancelar");
        cancelButton.addActionListener(e -> dispose());
        cancelButton.setToolTipText("Cerrar sin crear usuario");

        JButton crearButton = new JButton("Crear Usuario");
        crearButton.addActionListener(e -> intentarRegistro());
        crearButton.setToolTipText("Registrar nuevo usuario en el sistema");

        // Estilo para botón principal
        crearButton.setBackground(new Color(34, 139, 34));
        crearButton.setForeground(Color.WHITE);
        crearButton.setFocusPainted(false);

        buttonPanel.add(cancelButton);
        buttonPanel.add(crearButton);

        return buttonPanel;
    }

    /**
     * Valida los datos y solicita la creación del usuario al servicio.
     * Aplica SRP: Método específico para lógica de registro.
     */
    private void intentarRegistro() {
        // Obtener datos del formulario
        String username = userField.getText();
        char[] passwordChars = passField.getPassword();
        char[] confirmPasswordChars = confirmPassField.getPassword();
        String password = new String(passwordChars);
        String confirmPassword = new String(confirmPasswordChars);

        // Validar entrada
        if (!validarDatosRegistro(username, password, confirmPassword)) {
            limpiarPasswordsMemoria(passwordChars, confirmPasswordChars);
            return;
        }

        // Mapear rol seleccionado (OCP: extensible a nuevos roles)
        String rolSeleccionado = (String) rolCombo.getSelectedItem();
        Rol rol = mapearRol(rolSeleccionado);

        // Intentar crear usuario (DIP: delegado al servicio)
        boolean creado = usuarioService.crearUsuario(username.trim(), password, rol);

        // Limpiar contraseñas de memoria
        limpiarPasswordsMemoria(passwordChars, confirmPasswordChars);

        if (creado) {
            mostrarExito(username.trim(), rol.getNombre());
            dispose();
        } else {
            mostrarErrorUsuarioExistente(username.trim());
        }
    }

    /**
     * Valida todos los campos del formulario de registro.
     *
     * @param username Nombre de usuario ingresado
     * @param password Contraseña ingresada
     * @param confirmPassword Confirmación de contraseña
     * @return true si todos los datos son válidos
     */
    private boolean validarDatosRegistro(String username, String password, String confirmPassword) {

        // ===== Validar username =====
        if (username == null || username.trim().isEmpty()) {
            mostrarAdvertencia(MSG_USERNAME_REQUIRED);
            return false;
        }

        String usernameTrim = username.trim();

        if (usernameTrim.length() < MIN_USERNAME_LENGTH) {
            mostrarAdvertencia(String.format(MSG_USERNAME_MIN, MIN_USERNAME_LENGTH));
            return false;
        }

        if (usernameTrim.length() > MAX_USERNAME_LENGTH) {
            mostrarAdvertencia(String.format(MSG_USERNAME_MAX, MAX_USERNAME_LENGTH));
            return false;
        }

        if (!USERNAME_PATTERN.matcher(usernameTrim).matches()) {
            mostrarAdvertencia(MSG_USERNAME_PATTERN);
            return false;
        }

        // ===== Validar password =====
        if (password == null || password.isEmpty()) {
            mostrarAdvertencia(MSG_PASSWORD_REQUIRED);
            return false;
        }

        if (password.length() < MIN_PASSWORD_LENGTH) {
            mostrarAdvertencia(String.format(MSG_PASSWORD_MIN, MIN_PASSWORD_LENGTH));
            return false;
        }

        if (password.length() > MAX_PASSWORD_LENGTH) {
            mostrarAdvertencia(String.format(MSG_PASSWORD_MAX, MAX_PASSWORD_LENGTH));
            return false;
        }

        // ===== Validar confirmación =====
        if (!password.equals(confirmPassword)) {
            mostrarAdvertencia(MSG_PASSWORD_NO_MATCH);
            return false;
        }

        return true;
    }


    /**
     * Mapea el nombre del rol a su implementación concreta.
     * Aplica OCP: Fácil agregar nuevos casos sin modificar la estructura.
     *
     * @param nombreRol Nombre del rol seleccionado en el combo
     * @return Instancia concreta del rol
     */
    private Rol mapearRol(final String nombreRol) {
        return switch (nombreRol) {
            case "Administrador" -> new Administrador();
            case "Usuario" -> new UsuarioRegular();
            case "Invitado" -> new Invitado();
            default -> new UsuarioRegular(); // Valor por defecto seguro
        };
    }

    /**
     * Muestra mensaje de éxito tras crear el usuario.
     *
     * @param username Nombre del usuario creado
     * @param rolNombre Nombre del rol asignado
     */
    private void mostrarExito(String username, String rolNombre) {
        JPanel panel = new JPanel(new BorderLayout(10, 10));

        JLabel mensaje = new JLabel(
                "<html><center>" +
                        "Usuario creado correctamente<br><br>" +
                        "<b>" + username + "</b><br>" +
                        "Rol: <i>" + rolNombre + "</i>" +
                        "</center></html>",
                SwingConstants.CENTER
        );

        panel.add(mensaje, BorderLayout.CENTER);

        JOptionPane.showMessageDialog(this,
                panel,
                "¡Registro Exitoso!",
                JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Muestra mensaje de error cuando el usuario ya existe.
     *
     * @param username Nombre del usuario que ya existe
     */
    private void mostrarErrorUsuarioExistente(String username) {
        JOptionPane.showMessageDialog(this,
                "No se pudo crear el usuario.\n\n" +
                        "Ya existe un usuario con el nombre \"" + username + "\".\n" +
                        "Por favor, elija otro nombre.",
                "Usuario Existente",
                JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Muestra mensaje de advertencia genérico.
     *
     * @param mensaje Texto a mostrar
     */
    private void mostrarAdvertencia(String mensaje) {
        JOptionPane.showMessageDialog(this,
                mensaje,
                "Validación de Datos",
                JOptionPane.WARNING_MESSAGE);
    }

    /**
     * Limpia arrays de contraseñas de la memoria por seguridad.
     *
     * @param passwordChars Array de contraseña
     * @param confirmPasswordChars Array de confirmación
     */
    private void limpiarPasswordsMemoria(char[] passwordChars, char[] confirmPasswordChars) {
        if (passwordChars != null) {
            java.util.Arrays.fill(passwordChars, ' ');
        }
        if (confirmPasswordChars != null) {
            java.util.Arrays.fill(confirmPasswordChars, ' ');
        }
    }
}