package org.solid.models;

/**
 * Clase Usuario que tiene información básica.
 * Principio SRP: solo almacena datos del usuario.
 */
public class Usuario {


    private final String username;
    private final String password;
    private final Rol rol;


    public Usuario(String username, String password, Rol rol) {
        this.username = username;
        this.password = password;
        this.rol = rol;
    }


    public String getUsername() {
        return username;
    }


    public String getPassword() {
        return password;
    }


    public Rol getRol() {
        return rol;
    }
}