package org.solid.models;

/**
 * Clase base que representa un Rol dentro del sistema.
 * Principios aplicados:
 * - SRP: La clase solo representa información y comportamiento básico del Rol.
 * - LSP: Las subclases pueden reemplazar esta clase sin afectar el funcionamiento.
 */
public abstract class Rol {


    /** Nombre del rol */
    private final String nombre;


    protected Rol(String nombre) {
        this.nombre = nombre;
    }


    public String getNombre() {
        return nombre;
    }


    /** Acción genérica del rol */
    public abstract String descripcionRol();
}