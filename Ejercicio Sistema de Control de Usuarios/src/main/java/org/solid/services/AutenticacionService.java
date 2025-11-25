package org.solid.services;

import org.solid.interfaces.IAutenticacion;
import org.solid.models.Usuario;


/**
 * Servicio que implementa autenticación.
 * - DIP: depende de la abstracción IAutenticacion
 * - SRP: solo se encarga de autenticar
 */
public class AutenticacionService implements IAutenticacion {


    @Override
    public boolean autenticar(Usuario usuario, String username, String password) {
        if (usuario == null) {
            return false;
        }


        return usuario.getUsername().equals(username)
                && usuario.getPassword().equals(password);
    }
}