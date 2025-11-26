package org.solid.app;

import org.solid.models.*;
import org.solid.services.AutenticacionService;


public class MainSimple {
    public static void main(String[] args) {


        AutenticacionService auth = new AutenticacionService();


        Usuario admin = new Usuario("maria", "1234", new Administrador());
        Usuario invitado = new Usuario("juan", "1111", new Invitado());


        if (auth.autenticar(admin, "maria", "1234")) {
            System.out.println("Admin autenticado: " + admin.getRol().descripcionRol());
        }


        if (auth.autenticar(invitado, "juan", "1111")) {
            System.out.println("Invitado autenticado: " + invitado.getRol().descripcionRol());
        }
    }
}