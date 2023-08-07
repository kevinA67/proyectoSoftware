/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Conexion;

import java.util.HashMap;
import java.util.Map;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.view.JasperViewer;

/**
 *
 * @author everg
 */
public class Conexion_iReport {
    //Metodo para vializar un reporte sin parametros----------------------------------------------
	public void reportesSinParametros(String reporte)
	{
		ConexionBD con = new ConexionBD();//coneccion a la base de datos
		
		try {
		JasperPrint rep=JasperFillManager.fillReport(reporte,null,con.getConexion());
		JasperViewer view = new JasperViewer(rep,false);
		view.setVisible(true);
		
		}catch (JRException e) {
			e.printStackTrace();
		}
	}
	//Metodo para visualizar un reporte con parametros tipo String---------------------------------------------
	public void reportesConParametros(String archivo,String parametro)
	{
		ConexionBD con = new ConexionBD();
		
		try {
		
		Map parametros = new HashMap();
		parametros.put("cod_ofi",parametro);
		
		JasperPrint rep=JasperFillManager.fillReport(archivo,parametros,con.getConexion());
		JasperViewer view = new JasperViewer(rep,false);
		view.setVisible(true);
		
		}catch (JRException e) {
			e.printStackTrace();
		}
	}
    
}
