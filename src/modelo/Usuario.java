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

    //Constructor
    public Usuario(int idusuario, String usuario, String password) {
        this.idusuario = idusuario;
        this.usuario = usuario;
        this.password = password;
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
}
