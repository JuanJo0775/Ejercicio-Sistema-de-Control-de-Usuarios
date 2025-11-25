package org.solid.interfaces;

import org.solid.models.Usuario;


/**
 * Interface para autenticación.
 * Aplica DIP: las clases dependen de abstracciones.
 */
public interface IAutenticacion {
    boolean autenticar(Usuario usuario, String username, String password);
}
