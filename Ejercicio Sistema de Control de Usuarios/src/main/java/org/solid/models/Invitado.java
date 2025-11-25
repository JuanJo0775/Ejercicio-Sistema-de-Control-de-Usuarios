package org.solid.models;

import org.solid.interfaces.IPermisosInvitado;
import org.solid.interfaces.IPermisosBasicos;


/**
 * Usuario invitado con permisos limitados.
 */
public class Invitado extends Rol implements IPermisosInvitado, IPermisosBasicos {


    public Invitado() {
        super("Invitado");
    }


    @Override
    public String descripcionRol() {
        return "Acceso muy limitado";
    }


    @Override
    public String acceder() {
        return "Acceso solo lectura";
    }


    @Override
    public String vistaPublica() {
        return "Acceso a vista pública";
    }
}