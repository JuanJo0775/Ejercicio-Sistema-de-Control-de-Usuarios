package org.solid.app;

import org.solid.ui.LoginFrame;

/**
 * Clase principal. Ahora lanza una interfaz gráfica en vez de usar consola.
 */
public class Main {
    public static void main(String[] args) {
// Inicializar ventana gráfica
        javax.swing.SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}
