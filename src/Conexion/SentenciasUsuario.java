/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Conexion;
import java.sql.*;
import modelo.Usuario;

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
    
    
    public void insertar(Usuario usu){
        String sql="INSERT INTO `usuarios`(`id_usuario`, `nombre_usuario`, `password`, `empleado_id`, `rol`, `estado`) "
                + "VALUES ('NULL','Eber','123','241','admin','1')";
		
		try {
            st=getConexion().prepareStatement(sql);
			
			st.setInt(1, usu.getIdempleado());
			st.setString(2, usu.getUsuario());
			st.setString(3, usu.getPassword());
                        st.setString(4, usu.getRol());
                        st.setInt(6,usu.getEstado());
			st.execute();
			System.out.println("Los datos se inserto correctamente");
		}catch(SQLException e) {e.printStackTrace();
			System.out.println("Error al insertar los datos!");
			
		}
    
    }
    
    public static void main(String arg[]){
    //new SentenciasUsuario().insertar(new Usuario());
    }
    
}
