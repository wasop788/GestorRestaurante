package com.restaurante.util;

import com.restaurante.model.Usuario;

public class Sesion {
    private static Usuario usuarioActual;

    public static Usuario getUsuarioActual() { return usuarioActual; }
    public static void setUsuarioActual(Usuario u) { usuarioActual = u; }
    public static void cerrar() { usuarioActual = null; }
}