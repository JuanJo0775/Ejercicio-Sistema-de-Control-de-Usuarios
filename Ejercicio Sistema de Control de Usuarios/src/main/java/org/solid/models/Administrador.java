package org.solid.models;

import org.solid.interfaces.IPermisosAdmin;
import org.solid.interfaces.IPermisosBasicos;


/**
 * Representa un usuario Administrador.
 * Implementa interfaces específicas para cumplir ISP.
 */
public class Administrador extends Rol implements IPermisosBasicos, IPermisosAdmin {


    public Administrador() {
        super("Administrador");
    }


    @Override
    public String descripcionRol() {
        return "Acceso completo al sistema";
    }


    @Override
    public String acceder() {
        return "Acceso general permitido";
    }


    @Override
    public String gestionarUsuarios() {
        return "Gestión de usuarios habilitada";
    }
}