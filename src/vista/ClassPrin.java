/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vista;

import Conexion.ConexionBD;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 *
 * @author User
 */
public class ClassPrin {
       Connection con = null;
    ConexionBD connec;
    PreparedStatement ps = null;
    ResultSet rs = null;

    public static String usuario;
    public static int idUsuario;
    
    public static boolean tienePermisoRegistroCliente = false;
    public static boolean tienePermisoRegistroAutor = false;
    public static boolean tienePermisoRegistroEditorial = false;
    public static boolean tienePermisoRegistroLibro = false;
    public static boolean tienePermisoRegistroVenta = false;
    public static boolean tienePermisoReportes = false;



    public static void main(String[] args) {
        new Login().setVisible(true);
    }
}
