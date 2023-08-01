/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

/**
 *
 * @author everg
 */
public class Usuario {
    //Atributos
    private int idusuario;
    private String usuario;
    private String password;
    private int idempleado;
    private String rol;
    private int estado;

    //Constructor

    public Usuario(int idusuario, String usuario, String password, int idempleado, String rol, int estado) {
        this.idusuario = idusuario;
        this.usuario = usuario;
        this.password = password;
        this.idempleado = idempleado;
        this.rol = rol;
        this.estado = estado;
    }
    
    
    public Usuario(){};
    
    //Metodos

    public int getIdusuario() {
        return idusuario;
    }

    public void setIdusuario(int idusuario) {
        this.idusuario = idusuario;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getIdempleado() {
        return idempleado;
    }

    public void setIdempleado(int idempleado) {
        this.idempleado = idempleado;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }
    
}
