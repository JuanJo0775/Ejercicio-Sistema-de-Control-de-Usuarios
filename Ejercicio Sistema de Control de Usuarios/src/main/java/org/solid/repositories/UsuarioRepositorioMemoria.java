package org.solid.repositories;

import org.solid.interfaces.IUsuarioRepositorio;
import org.solid.models.Usuario;
import java.util.ArrayList;
import java.util.List;


/**
 * Implementación en memoria del repositorio.
 * Cumple SRP: solo gestiona almacenamiento.
 */
public class UsuarioRepositorioMemoria implements IUsuarioRepositorio {


    private final List<Usuario> usuarios;


    public UsuarioRepositorioMemoria() {
        this.usuarios = new ArrayList<>();
    }


    @Override
    public void agregarUsuario(Usuario usuario) {
        usuarios.add(usuario);
    }


    @Override
    public Usuario buscarPorUsername(String username) {
        return usuarios.stream()
                .filter(u -> u.getUsername().equals(username))
                .findFirst()
                .orElse(null);
    }


    @Override
    public List<Usuario> obtenerTodos() {
        return usuarios;
    }
}
