/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package proyectog4;

import Conexion.ConexionBD;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 *
 * @author User
 */
public class classPrin {
    
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
