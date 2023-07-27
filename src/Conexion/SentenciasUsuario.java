/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Conexion;
import java.sql.*;

/**
 *
 * @author everg
 */
public class SentenciasUsuario extends ConexionBD{
    PreparedStatement st;
    ResultSet rs;
    String registros[][];
    
    public SentenciasUsuario(String bd) {
        super(bd);
    }
    
}
