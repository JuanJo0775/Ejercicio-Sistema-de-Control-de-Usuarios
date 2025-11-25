package org.solid.interfaces;

import org.solid.models.Usuario;
import java.util.List;


/**
 * Repositorio abstracto para manejar usuarios.
 * Aplica DIP: las capas superiores dependen de esta abstracción.
 */
public interface IUsuarioRepositorio {
    void agregarUsuario(Usuario usuario);
    Usuario buscarPorUsername(String username);
    List<Usuario> obtenerTodos();
}
