package org.solid.models;

import org.solid.interfaces.IPermisosBasicos;


/**
 * Usuario normal del sistema.
 */
public class UsuarioRegular extends Rol implements IPermisosBasicos {


    public UsuarioRegular() {
        super("Usuario Regular");
    }


    @Override
    public String descripcionRol() {
        return "Acceso básico permitido";
    }


    @Override
    public String acceder() {
        return "Acceso a funciones básicas";
    }
}