package org.solid.ui;

import org.solid.models.Administrador;
import org.solid.models.Invitado;
import org.solid.models.Rol;
import org.solid.models.UsuarioRegular;
import org.solid.services.UsuarioService;

import javax.swing.*;
import java.awt.*;
import java.io.Serial;

/**
 * Ventana para registrar nuevos usuarios.
 *
 * <p>Responsabilidades:
 * - SRP: solo maneja la UI de registro y delega la lógica al UsuarioService.
 * - DIP: recibe el servicio por constructor (dependencia por abstracción en capas superiores).</p>
 *
 * Diseñada para ser compatible con LoginFrame en el mismo paquete (org.solid.ui).
 */
public class RegistroFrame extends JFrame {

    @Serial
    private static final long serialVersionUID = 1L;

    private final JTextField userField;
    private final JPasswordField passField;
    private final JComboBox<String> rolCombo;
    private final UsuarioService usuarioService;

    /**
     * Crea la ventana de registro recibiendo la dependencia UsuarioService.
     *
     * @param usuarioService servicio para gestionar usuarios (no nulo)
     */
    public RegistroFrame(final UsuarioService usuarioService) {
        super("Registrar Nuevo Usuario");

        if (usuarioService == null) {
            throw new IllegalArgumentException("usuarioService no puede ser nulo");
        }
        this.usuarioService = usuarioService;

        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setSize(360, 240);
        setResizable(false);
        setLocationRelativeTo(null);

        JPanel content = new JPanel(new BorderLayout(8, 8));
        content.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setContentPane(content);

        // Formulario central
        JPanel form = new JPanel(new GridLayout(3, 2, 8, 8));
        form.add(new JLabel("Usuario:"));
        userField = new JTextField();
        form.add(userField);

        form.add(new JLabel("Contraseña:"));
        passField = new JPasswordField();
        form.add(passField);

        form.add(new JLabel("Rol:"));
        rolCombo = new JComboBox<>(new String[]{"Administrador", "Usuario", "Invitado"});
        form.add(rolCombo);

        // Botón crear
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton crearButton = new JButton("Crear Usuario");
        crearButton.addActionListener(e -> crearUsuario());
        footer.add(crearButton);

        content.add(form, BorderLayout.CENTER);
        content.add(footer, BorderLayout.SOUTH);
    }

    /**
     * Valida los campos y solicita al servicio crear el usuario.
     * Muestra mensajes claros y cierra la ventana al crear correctamente.
     */
    private void crearUsuario() {
        final String username = userField.getText() == null ? "" : userField.getText().trim();
        final String password = passField.getPassword() == null ? "" : new String(passField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "El nombre de usuario y la contraseña son obligatorios.",
                    "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        final String rolSeleccionado = (String) rolCombo.getSelectedItem();
        final Rol rol = mapearRol(rolSeleccionado);

        final boolean creado = usuarioService.crearUsuario(username, password, rol);
        if (creado) {
            JOptionPane.showMessageDialog(this,
                    "Usuario creado correctamente.",
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
            dispose();
            return;
        }

        JOptionPane.showMessageDialog(this,
                "No se pudo crear el usuario: ya existe un usuario con ese nombre.",
                "Error", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Mapea el texto del combo a la implementación concreta de Rol.
     * Separación de responsabilidad para facilitar futuras extensiones (OCP).
     *
     * @param nombreRol nombre del rol seleccionado
     * @return instancia de Rol correspondiente (nunca nulo)
     */
    private Rol mapearRol(final String nombreRol) {
        if ("Administrador".equalsIgnoreCase(nombreRol)) {
            return new Administrador();
        }
        if ("Usuario".equalsIgnoreCase(nombreRol)) {
            return new UsuarioRegular();
        }
        // Por defecto Invitado
        return new Invitado();
    }
}
