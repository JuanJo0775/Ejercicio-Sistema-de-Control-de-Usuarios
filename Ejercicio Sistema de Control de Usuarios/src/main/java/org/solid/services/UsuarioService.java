package org.solid.services;

import org.solid.interfaces.IUsuarioRepositorio;
import org.solid.models.Rol;
import org.solid.models.Usuario;


/**
 * Servicio para gestionar usuarios.
 * Sigue SRP: solo maneja lógica de usuarios.
 */
public class UsuarioService {


    private final IUsuarioRepositorio repo;


    public UsuarioService(IUsuarioRepositorio repo) {
        this.repo = repo;
    }


    public boolean crearUsuario(String username, String password, Rol rol) {
        if (repo.buscarPorUsername(username) != null) {
            return false;
        }
        repo.agregarUsuario(new Usuario(username, password, rol));
        return true;
    }


    public Usuario obtenerUsuario(String username) {
        return repo.buscarPorUsername(username);
    }
}