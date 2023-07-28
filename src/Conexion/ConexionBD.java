/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Conexion;

import java.sql.*;
import javax.swing.JOptionPane;

/**
 *
 * @author User
 */
public class ConexionBD {

    Connection con = null;

    public ConexionBD(String bd) {
        try {
            //DRIVER
            Class.forName("com.mysql.jdbc.Driver");
            //DEFINIMOS LA BD
            String url = "jdbc:mysql://db4free.net:3306/"+bd;
            String user = "progra_admin";
            String pass = "Pr0gr4m4c10n.w3b";

            //CONECTAMOS LA BD
            con = DriverManager.getConnection(url, user, pass);
            System.out.println("Conexión realizada correctamente.");
        } catch (ClassNotFoundException ex) {
            JOptionPane.showMessageDialog(null, "Error, no se pudo establecer la conexión con la BD. " + ex.getMessage());
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error, no se pudo establecer la conexión con la BD. " + ex.getMessage());
        }
    }
    
    public Connection getConexion(){
        return con;
    }
    
    public static void main(String[] args) {
        new ConexionBD("abarroteria");
    }
}
