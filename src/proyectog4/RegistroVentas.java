/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package proyectog4;

import Conexion.ConexionBD;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.toedter.calendar.JDateChooser;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

/**
 *
 * @author Alejandra
 */
public class RegistroVentas extends javax.swing.JInternalFrame {

    String usuario, sentenciaSQL, cod = "", idLibro, encontrado = "";
    Connection con = null;
    ConexionBD connec;
    PreparedStatement ps = null;
    ResultSet rs = null;
    DefaultTableModel modelo = new DefaultTableModel();
    String nombreTabla = "VENTAS", tituloLibro = "";
    int contadorProducto = 0;
    int acumCantidad = 0;
    double acumSubtotal = 0;
    double acumISV = 0;
    double acumTotalC = 0;
    double acumRecargo = 0;
    int idVenta = 0;

    PdfPTable tabla = new PdfPTable(7);

    //CREAMOS UN DOCUMENTO DONDE SE AGREGARA EL LOGO, EL ENCABEZADO Y LA TABLA PDF
    Document reporte = new Document();

    public RegistroVentas() {
        initComponents();
        jTxtUsuario.setText(classPrin.usuario);
        jCmbLibro.setEnabled(false);
        llenarCmbCliente();
        llenarCmbMetodoPago();
        jBtnFacturar.setEnabled(false);
        jTxtLibro.setVisible(false);
        jTxtStockMin.setVisible(false);
        jTxtStockMax.setVisible(false);
        jTxtStockD.setVisible(false);
        jdcFechaVenta.setEnabled(false);
        // Obtener la fecha actual
        Date fechaActual = new Date();
        // Asignar la fecha actual al componente JDateChooser
        jdcFechaVenta.setDate(fechaActual);
        jTxtUsuario.setText(classPrin.usuario);
    }

    public void conectarBD() {
        connec = new ConexionBD("openfirekafz");
        con = connec.getConexion();
    }

    public void bitacora(int idUsuario, String accionUsuario) {
        try {
            conectarBD();
            sentenciaSQL = "INSERT INTO bitacoraUsuario (idBitacora, idUsuario, accionUsuario, fecha, hora, estadoBitacora) VALUES (?, ?, ?, ?, ?, ?)";
            ps = con.prepareStatement(sentenciaSQL);
            ps.setInt(1, 0);
            ps.setInt(2, idUsuario);
            ps.setString(3, accionUsuario);
            ps.setTimestamp(4, new Timestamp(new Date().getTime())); //Obtener la fecha del sistema
            ps.setTimestamp(5, new Timestamp(new Date().getTime())); //Obtener la hora del sistema
            ps.setInt(6, 1);
            ps.executeUpdate();
            con.close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error al insertar en la bitácora: " + ex.getMessage());
        }
    }

    public void llenarCmbMetodoPago() {
        jCmbMetodoPago.addItem("SELECCIONE MÉTODO DE PAGO");
        jCmbMetodoPago.addItem("Tarjeta");
        jCmbMetodoPago.addItem("Efectivo");
    }

    public void llenarCmbIdLibro() {
        try {
            jCmbLibro.removeAllItems();
            jCmbLibro.addItem("SELECCIONE UNA OPCIÓN: ");
            String idCliente = jTxtCodCliente.getText();
            conectarBD();
            //ps = (PreparedStatement) con.prepareStatement("SELECT idLibro, concat_ws(' ', libro.cod_libro, libro.tituloLibro) as nombre, precio FROM libro WHERE libro.estadoLibro=1");
            ps = (PreparedStatement) con.prepareStatement("SELECT libro.idLibro, concat_ws(' ', libro.cod_libro, libro.tituloLibro) as nombre, precio FROM libro \n"
                    + "INNER JOIN inventario i ON i.idLibro = libro.idLibro \n"
                    + "WHERE ((libro.tipo = 'Estándar') \n"
                    + "OR (libro.tipo = 'Premium' AND EXISTS(SELECT idCliente FROM carnet_cliente\n"
                    + "WHERE idCliente = ? AND fechaExpiracion >=NOW()))) \n"
                    + "AND libro.estadoLibro = 1\n"
                    + "AND i.stock_disp > 0");

            ps.setString(1, idCliente);
            rs = ps.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("idLibro");
                String nombre = rs.getString("nombre");
                double precio = rs.getDouble("precio");
                jCmbLibro.addItem(nombre);
            }

            con.close();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage());
        }

        jCmbLibro.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                String nombreLibro = (String) jCmbLibro.getSelectedItem();
                try {
                    conectarBD();
                    ps = (PreparedStatement) con.prepareStatement("SELECT libro.idLibro, libro.tituloLibro, libro.precio, inventario.stock_disp, inventario.stock_min FROM libro "
                            + "INNER JOIN inventario ON inventario.idLibro = libro.idLibro "
                            + "WHERE concat_ws(' ', libro.cod_libro, libro.tituloLibro)=?");
                    ps.setString(1, nombreLibro);
                    rs = ps.executeQuery();
                    if (rs.next()) {
                        idLibro = rs.getString("idLibro");
                        tituloLibro = rs.getString("tituloLibro");
                        double precioLibro = rs.getDouble("precio");
                        int stockDisponible = rs.getInt("stock_disp");
                        int stockMin = rs.getInt("stock_min");

                        jTxtLibro.setText(idLibro);
                        jTxtPrecio.setText(Double.toString(precioLibro));
                        jTxtStockD.setText(Integer.toString(stockDisponible));
                        jTxtStockMin.setText(Integer.toString(stockMin));

                    }
                    con.close();
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage());
                }
            }
        });
    }

    public void llenarCmbCliente() {
        try {
            jCmbCliente.addItem("SELECCIONE UNA OPCIÓN: ");
            conectarBD();
            ps = (PreparedStatement) con.prepareStatement("SELECT idCliente, nombreCliente FROM cliente WHERE estadoCliente=1");
            rs = ps.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("idCliente");
                String nombre = rs.getString("nombreCliente");
                jCmbCliente.addItem(nombre);
            }

            con.close();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage());
        }

        jCmbCliente.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {

                String nombreCliente = (String) jCmbCliente.getSelectedItem();

                try {
                    conectarBD();
                    ps = (PreparedStatement) con.prepareStatement("SELECT idCliente FROM cliente WHERE nombreCliente=?");
                    ps.setString(1, nombreCliente);
                    rs = ps.executeQuery();
                    if (rs.next()) {
                        int idCliente = rs.getInt("idCliente");
                        jTxtCodCliente.setText(Integer.toString(idCliente));
                        jCmbLibro.setEnabled(true);
                        llenarCmbIdLibro();
                    }
                    con.close();
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage());
                }
            }
        });

    }

    public void factura() {
        try {
            int estado = 1; //1 será activo, y 0 desactivo (eliminado)
            double montoTotal = Double.parseDouble(jTxtTotalC.getText());
            double recargo = Double.parseDouble(jTxtRecargo.getText());
            double isv = Double.parseDouble(jTxtISV.getText());

            SimpleDateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd");
            String fechaVenta = dFormat.format(jdcFechaVenta.getDate());
            String metodoPago = jCmbMetodoPago.getSelectedItem().toString();

            conectarBD();
            sentenciaSQL = "INSERT INTO venta (idVenta, idCliente, idUsuario, fechaVenta, montoTotal, metodoPago, recargo, isv, estadoVenta) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            ps = con.prepareStatement(sentenciaSQL);

            ps.setInt(1, 0); // Id de la venta
            ps.setInt(2, Integer.parseInt(jTxtCodCliente.getText())); //Id del cliente
            ps.setInt(3, classPrin.idUsuario); // Usuario que realizó la venta
            ps.setString(4, fechaVenta); //Fecha en la que se realizó la venta
            ps.setDouble(5, montoTotal); // Monto total
            ps.setString(6, metodoPago); //Método de pago,si es con tarjeta o efectivo
            ps.setDouble(7, recargo); //Recargo
            ps.setDouble(8, isv); //ISV
            ps.setInt(9, estado); //Estado, o sea activo
            ps.execute();

            String accion = "CREACIÓN DE REGISTRO EN LA TABLA " + nombreTabla;
            bitacora(classPrin.idUsuario, accion);

            JOptionPane.showMessageDialog(null, "DATOS INGRESADOS CORRECTAMENTE");
            con.close();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "ERROR AL INTENTAR INGRESAR LOS DATOS:  " + ex.getMessage());
        }
    }

    public void detalleFactura() {
        int ultimoId = obtenerUltimoIdVentas();
        //Obtener la última fila de la jtable
        int rowCount = jTablaVenta.getRowCount();
        for (int i = 0; i < rowCount; i++) {
            //Campos para la tabla venta_libro
            int id = Integer.parseInt(jTablaVenta.getValueAt(i, 1).toString());
            double precio = Double.parseDouble(jTablaVenta.getValueAt(i, 3).toString());
            int cantidad = Integer.parseInt(jTablaVenta.getValueAt(i, 4).toString());
            double subt = Double.parseDouble(jTablaVenta.getValueAt(i, 5).toString());
            int stock_disp = 0;

            try {
                conectarBD();
                //Esta consulta recorrerá la tabla, y hará tantos ciclos como libros comprados
                ps = con.prepareStatement("INSERT INTO venta_libro (idLibro, idVenta, cantidad, precio_unitario, subtotal) VALUES (?, ?, ?, ?, ?)");
                ps.setInt(1, id);
                ps.setInt(2, ultimoId);
                ps.setInt(3, cantidad);
                ps.setDouble(4, precio);
                ps.setDouble(5, subt);
                ps.executeUpdate();
                con.close();
            } catch (SQLException ex) {
                Logger.getLogger(RegistroVentas.class.getName()).log(Level.SEVERE, null, ex);
            }

            try {
                conectarBD();
                //Esta consulta consultará el stock disponible y lo almacenará en una variable, con esa variable
                //actualizaremos según la cantidad de producto que se está llevando.
                sentenciaSQL = "SELECT stock_disp FROM inventario WHERE idLibro = ?";
                ps = con.prepareStatement(sentenciaSQL);
                ps.setInt(1, id);
                rs = ps.executeQuery();
                if (rs.next()) {
                    stock_disp = rs.getInt("stock_disp");
                    stock_disp = stock_disp - cantidad;
                }

                //stock_disp = stock_disp - cantidad;
                con.close();
            } catch (SQLException ex) {
                Logger.getLogger(RegistroVentas.class.getName()).log(Level.SEVERE, null, ex);
            }

            try {
                conectarBD();
                //Esta consulta actualizará el stock disponible
                ps = con.prepareStatement("UPDATE inventario SET stock_disp = ? WHERE idLibro = ?");
                ps.setInt(1, stock_disp);
                ps.setInt(2, id);
                ps.executeUpdate();
                con.close();
            } catch (SQLException ex) {
                Logger.getLogger(RegistroVentas.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    }

    public int obtenerUltimoIdVentas() {
        int ultimoid = 0;
        try {
            conectarBD();
            sentenciaSQL = "SELECT MAX(idVenta) AS ultimo_id FROM venta";
            ps = con.prepareStatement(sentenciaSQL);
            rs = ps.executeQuery();
            if (rs.next()) {
                ultimoid = rs.getInt("ultimo_id");
            }
            rs.close();
            ps.close();
            con.close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage());
        }
        return ultimoid;
    }

    public void tablaListaProductos() {
        DefaultTableModel modelo = (DefaultTableModel) jTablaVenta.getModel();
        String libro = jTxtLibro.getText();
        double precio = Double.parseDouble(jTxtPrecio.getText());
        int cantidad = Integer.parseInt(jTxtCantidad.getText());
        double subtotal = Double.parseDouble(jTxtTotal.getText());
        double isv = subtotal * 0.15;
        Object[] fila = new Object[]{"", libro, tituloLibro, precio, cantidad, subtotal, isv};
        modelo.addRow(fila);
        jTablaVenta.setModel(modelo);

    }

    public void valoresTotales() {
        String subtotalString, isvString, cantidadString, totalCString, totalRecargoString;

        int rowIndex = jTablaVenta.getRowCount() - 1; //Obtener la última fila

        //La cantidad de productos que está comprando el cliente
        int valueCantidad = (int) jTablaVenta.getValueAt(rowIndex, 4);
        acumCantidad = acumCantidad + valueCantidad;
        cantidadString = String.valueOf(acumCantidad);
        jTxtArticulos.setText(cantidadString);

        //Subtotal de la compra
        double valueTotal = (double) jTablaVenta.getValueAt(rowIndex, 5);
        acumSubtotal = acumSubtotal + valueTotal;
        subtotalString = String.valueOf(acumSubtotal);
        jTxtSubtotal.setText(subtotalString);

        //El total del ISV de la compra
        double valueISV = (double) jTablaVenta.getValueAt(rowIndex, 6);
        acumISV = acumISV + valueISV;
        isvString = String.valueOf(acumISV);
        jTxtISV.setText(isvString);

        //El recargo si está utilizando 
        String metodoPago = (String) jCmbMetodoPago.getSelectedItem();

        // Verificar si el método de pago es "Tarjeta"
        if (metodoPago.equals("Tarjeta")) {
            // Si el método de pago es "Tarjeta", crear el recargo;
            acumRecargo = acumSubtotal * 0.05;
            totalRecargoString = String.valueOf(acumRecargo);
            jTxtRecargo.setText(totalRecargoString);
        } else {
            acumRecargo = 0;
            totalRecargoString = String.valueOf(acumRecargo);
            jTxtRecargo.setText(totalRecargoString);
        }

        //El total de la compra
        acumTotalC = acumSubtotal + acumISV + acumRecargo;
        totalCString = String.valueOf(acumTotalC);
        jTxtTotalC.setText(totalCString);
    }

    public void eliminarLibro() {
        String subtotalString, isvString, cantidadString, totalCString, totalRecargoString;

        int rowIndex = jTablaVenta.getRowCount() - 1; //Obtener la última fila

        //La cantidad de productos que está comprando el cliente
        int valueCantidad = (int) jTablaVenta.getValueAt(rowIndex, 4);
        acumCantidad = acumCantidad - valueCantidad;
        cantidadString = String.valueOf(acumCantidad);
        jTxtArticulos.setText(cantidadString);

        //Subtotal de la compra
        double valueTotal = (double) jTablaVenta.getValueAt(rowIndex, 5);
        acumSubtotal = acumSubtotal - valueTotal;
        subtotalString = String.valueOf(acumSubtotal);
        jTxtSubtotal.setText(subtotalString);

        //El total del ISV de la compra
        double valueISV = (double) jTablaVenta.getValueAt(rowIndex, 6);
        acumISV = acumISV - valueISV;
        isvString = String.valueOf(acumISV);
        jTxtISV.setText(isvString);

        //El recargo si está utilizando 
        String metodoPago = (String) jCmbMetodoPago.getSelectedItem();

        // Verificar si el método de pago es "Tarjeta"
        if (metodoPago.equals("Tarjeta")) {
            // Si el método de pago es "Tarjeta", crear el recargo;
            acumRecargo = acumSubtotal * 0.05;
            totalRecargoString = String.valueOf(acumRecargo);
            jTxtRecargo.setText(totalRecargoString);
        } else {
            acumRecargo = 0;
            totalRecargoString = String.valueOf(acumRecargo);
            jTxtRecargo.setText(totalRecargoString);
        }

        //El total de la compra
        acumTotalC = acumSubtotal + acumISV + acumRecargo;
        totalCString = String.valueOf(acumTotalC);
        jTxtTotalC.setText(totalCString);
    }

    public void bloquear() {
        jdcFechaVenta.setEnabled(false);
        jTxtCodCliente.setEnabled(false);
        jCmbCliente.setEnabled(false);
        jCmbMetodoPago.setEnabled(false);
        jBtnNuevaFactura.setEnabled(false);
    }

    public void desbloquear() {
        jdcFechaVenta.setEnabled(true);
        jTxtCodCliente.setEnabled(true);
        jCmbCliente.setEnabled(true);
        jCmbMetodoPago.setEnabled(true);
    }

    public void limpiar() {

        //Limpiar los campos que se rellenan
        Date fechaActual = new Date();
        jdcFechaVenta.setDate(fechaActual);
        jTxtCodCliente.setText(null);
        jCmbCliente.setSelectedIndex(0);
        jCmbLibro.setSelectedIndex(0);
        jTxtPrecio.setText(null);
        jTxtCantidad.setText(null);
        jCmbMetodoPago.setSelectedIndex(0);
        jTxtTotal.setText(null);

        //Limpiar la tabla
        DefaultTableModel model = (DefaultTableModel) jTablaVenta.getModel();
        int fila = jTablaVenta.getRowCount();
        for (int i = fila - 1; i >= 0; i--) {
            model.removeRow(i);
        }

        //Campos acumuladores, se reinician en 0
        jTxtTotalC.setText("0");
        jTxtRecargo.setText("0");
        jTxtSubtotal.setText("0");
        jTxtISV.setText("0");
        jTxtArticulos.setText("0");
        jBtnFacturar.setEnabled(false);
    }

    public void enumerarListaProductos() {
        DefaultTableModel model = (DefaultTableModel) jTablaVenta.getModel();
        int rowCount = model.getRowCount();
        int contador = 1;

        for (int i = 0; i < rowCount; i++) {
            model.setValueAt(contador, i, 0); // Asigna el valor del contador a la primera columna de la fila i
            contador++;
        }

    }

    public void crearFacturaPDF() {

        try {
            //RUTA DE LA CARPETA DE USUARIO
            String ruta = System.getProperty("user.home");

            String nombreCliente = (String) jCmbCliente.getSelectedItem();
            //AGREGO LA RUTA DEL DIRECTORIO PRINCIPAL MAS LA RUTA Y CARPETA DONDE QUIERO QUE ME 
            //APAREZCA EL PDF Y LE COLOCO EL NOMBRE QUE QUIERO QUE LLEVE EL DOCUMENTO, EN ESTE CASO TUVE QUE CREAR
            //UNA CARPETA EN EL ESCRITORIO QUE SE LLAMA REPORTES Y AHI MANDO A CREAR EL Reporte_PorFechas.pdf
            PdfWriter.getInstance(reporte, new FileOutputStream(ruta + "/Desktop/Factura (" + nombreCliente +").pdf"));

            //IMAGEN DEL ENCABEZADO (COLOCAMOS LA RUTA DE NUESTRA IMAGEN Y EL NOMBRE DE LA IMAGEN CON SU EXTENSION)
            Image logo = Image.getInstance("src/Imagenes/01- Logo The Book House.png");

            //MEDIDA DE LA IMAGEN DEL ENCABEZADO (LARGO Y ALTO)
            logo.scaleToFit(200, 200);

            //ALINEACION DEL ENCABEZADO
            logo.setAlignment(Chunk.ALIGN_CENTER);

            
            String metodoPago = (String) jCmbMetodoPago.getSelectedItem();
            Date fechaSeleccionada = jdcFechaVenta.getDate();
            String totalCompra = jTxtTotalC.getText();
            String recargo = jTxtRecargo.getText();
            String subtotalCompra = jTxtSubtotal.getText();
            String isv = jTxtISV.getText();
            SimpleDateFormat formatoFecha = new SimpleDateFormat("dd/MM/yyyy");
            String fechaSeleccionadaString = formatoFecha.format(fechaSeleccionada);

            //DAMOS FORMATO AL TEXTO DEL ENCABEZADO
            Paragraph encabezado = new Paragraph();
            encabezado.setAlignment(Element.ALIGN_CENTER);//ALINEACION
            encabezado.setFont(FontFactory.getFont("Arial", 20, Font.BOLD, BaseColor.ORANGE));//TIPO DE FUENTE, FORMATO Y COLOR
            encabezado.add("FACTURA\n");//TEXTO QUE APARECERÁ
            encabezado.setFont(FontFactory.getFont("Arial", 14, Font.BOLD, BaseColor.DARK_GRAY));
            encabezado.setAlignment(Element.ALIGN_LEFT);//ALINEACION
            encabezado.setFont(FontFactory.getFont("Arial", 12, Font.NORMAL, BaseColor.DARK_GRAY));
            encabezado.add("Cliente: " + nombreCliente + "\n");
            encabezado.add("ID de Venta: " + idVenta + "\n");
            encabezado.add("Fecha de Venta: " + fechaSeleccionadaString + "\n");
            encabezado.add("Método de Pago: " + metodoPago + "\n");
            encabezado.add("Recargo: " + recargo + "\n");
            encabezado.add("Subtotal de Compra: " + subtotalCompra + "\n");
            encabezado.add("ISV: " + isv + "\n");
            encabezado.add("Total de Compra: " + totalCompra + "\n\n\n");

            tabla.setWidths(new float[]{1, 2, 1, 1, 1, 1, 1});
            //AGREGAMOS LAS CELDAS A LA TABLA
            tabla.addCell("ID LIBRO");
            tabla.addCell("TÍTULO DEL LIBRO");
            tabla.addCell("TIPO");
            tabla.addCell("GÉNERO");
            tabla.addCell("PRECIO");
            tabla.addCell("CANT. COMPRADA");
            tabla.addCell("SUBTOTAL");

            //ABRIMOS EL DOCUMENTO
            reporte.open();
            //AGREGAMOS AL DOCUMENTO, EL LOGO (IMAGEN) Y ENCABEZADO
            reporte.add(logo);
            reporte.add(encabezado);
            //LLAMAMOS AL METODO BUSCAR EN LA BASE DE DATOS LOS REGISTROS QUE TENEMOS
            BuscarEnBD();
            //ABRIMOS EL PDF UNA VEZ QUE HA SIDO CREADO
            abrirReporte();

        } catch (DocumentException | FileNotFoundException e) {
            System.out.print("ERROR " + e);
        } catch (IOException e) {
            System.out.print("ERROR " + e);
        }
    }

    public void abrirReporte() {
        try {
            String nombreCliente = (String) jCmbCliente.getSelectedItem();
            //RUTA DEL DIRECTORIO
            String ruta = System.getProperty("user.home");
            //AGREGAMOS LA RUTA DONDE DEBE IR A BUSCAR EL PDF PARA ABRIRLO
            Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + ruta + "/Desktop/Factura (" + nombreCliente +").pdf");
            System.out.println("REPORTE VISUALIZADO");
        } catch (IOException e) {
        }
    }

    public void BuscarEnBD() {
        encontrado = "NO";
        conectarBD();
        try {
            sentenciaSQL = "SELECT l.idLibro, l.tituloLibro, l.tipo, l.genero, l.precio, SUM(vl.cantidad) as cantidad, SUM(vl.subtotal) as subtotal "
                    + "FROM venta v \n"
                    + "INNER JOIN venta_libro vl ON vl.idVenta = v.idVenta \n"
                    + "INNER JOIN libro l ON l.idLibro = vl.idLibro \n"
                    + "WHERE v.idVenta = " + idVenta + " \n"
                    + "GROUP BY l.idLibro";

            ps = con.prepareStatement(sentenciaSQL);
            //ps.setInt(1, idVenta);
            rs = ps.executeQuery();
            //HACE EL RECORRIDO EN LA TABLA DE LA BD Y LOS VA AGREGANDO A LA TABLA DEL PDF
            if (rs.next()) {
                do {
                    tabla.addCell(rs.getString(1)); //ID LIBRO
                    tabla.addCell(rs.getString(2)); //TITULO DEL LIBRO
                    tabla.addCell(rs.getString(3)); //TIPO DE LIBRO
                    tabla.addCell(rs.getString(4)); //GÉNERO DE LIBRO
                    tabla.addCell(rs.getString(5)); //PRECIO DE LIBRO
                    tabla.addCell(rs.getString(6)); //CANTIDAD COMPRADA DE ESE LIBRO
                    tabla.addCell(rs.getString(7)); //SUBTOTAL, PRECIO * CANTIDAD
                } while (rs.next());
                reporte.add(tabla);//AGREGAMOS AL DOCUMENTO LA TABLA QUE YA ESTÁ LLENA CON LOS DATOS OBTENIDOS DE LA BD                                 
                encontrado = "SI";
                if (encontrado.equals("NO")) {
                    JOptionPane.showMessageDialog(null, "ID DE VENTA NO ENCONTRADO", "ATENCION!", JOptionPane.ERROR_MESSAGE);
                }
                reporte.close();//CERRAMOS EL DOCUMENTO
                JOptionPane.showMessageDialog(null, "LA FACTURA HA SIDO CREADA!", "ATENCION", 1);
                con.close();//CERRAMOS LA CONEXION
            }
        } catch (DocumentException | HeadlessException | SQLException x) {
            System.out.print("ERROR " + x);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jlblImagen = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        jTxtTotal = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jCmbLibro = new javax.swing.JComboBox<>();
        jLabel13 = new javax.swing.JLabel();
        jTxtCodCliente = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jCmbCliente = new javax.swing.JComboBox<>();
        jTxtPrecio = new javax.swing.JTextField();
        jTxtCantidad = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        jbtnAgregar = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        jBtnEliminar = new javax.swing.JButton();
        jBtnFacturar = new javax.swing.JButton();
        jBtnNuevaFactura = new javax.swing.JButton();
        jPanel8 = new javax.swing.JPanel();
        jTxtArticulos = new javax.swing.JTextField();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jTxtTotalC = new javax.swing.JTextField();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTablaVenta = new javax.swing.JTable();
        jTxtISV = new javax.swing.JTextField();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jTxtSubtotal = new javax.swing.JTextField();
        jLabel20 = new javax.swing.JLabel();
        jdcFechaVenta = new com.toedter.calendar.JDateChooser();
        jTxtLibro = new javax.swing.JTextField();
        jLabel21 = new javax.swing.JLabel();
        jCmbMetodoPago = new javax.swing.JComboBox<>();
        jLabel22 = new javax.swing.JLabel();
        jTxtRecargo = new javax.swing.JTextField();
        jTxtStockMin = new javax.swing.JTextField();
        jTxtStockD = new javax.swing.JTextField();
        jTxtStockMax = new javax.swing.JTextField();
        jTxtUsuario = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jbtnListaFacturas = new javax.swing.JButton();

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setTitle("VENTAS");

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setMaximumSize(new java.awt.Dimension(1100, 680));
        jPanel1.setMinimumSize(new java.awt.Dimension(1100, 680));

        jPanel2.setBackground(new java.awt.Color(237, 120, 74));

        jlblImagen.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/08- Logo The Book House.png"))); // NOI18N

        jLabel2.setFont(new java.awt.Font("Century Schoolbook", 1, 44)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("REGISTRO DE VENTAS");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jlblImagen)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addGap(20, 25, Short.MAX_VALUE)
                .addComponent(jLabel2)
                .addGap(21, 21, 21))
            .addComponent(jlblImagen, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
        );

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));
        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(71, 84, 130), 2, true), "FORMULARIO DATOS", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 1, 12), new java.awt.Color(71, 84, 130))); // NOI18N
        jPanel4.setLayout(null);

        jLabel10.setText("MÉTODO PAGO");
        jPanel4.add(jLabel10);
        jLabel10.setBounds(480, 150, 110, 20);

        jTxtTotal.setEnabled(false);
        jPanel4.add(jTxtTotal);
        jTxtTotal.setBounds(860, 150, 170, 30);

        jLabel11.setText("CÓDIGO CLIENTE");
        jLabel11.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        jPanel4.add(jLabel11);
        jLabel11.setBounds(30, 60, 110, 20);

        jLabel12.setText("FECHA");
        jLabel12.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        jPanel4.add(jLabel12);
        jLabel12.setBounds(30, 20, 60, 20);

        jPanel4.add(jCmbLibro);
        jCmbLibro.setBounds(90, 110, 990, 30);

        jLabel13.setText("CLIENTE");
        jPanel4.add(jLabel13);
        jLabel13.setBounds(380, 60, 60, 20);
        jPanel4.add(jTxtCodCliente);
        jTxtCodCliente.setBounds(140, 60, 230, 30);

        jLabel14.setText("PRECIO");
        jPanel4.add(jLabel14);
        jLabel14.setBounds(30, 150, 70, 20);
        jPanel4.add(jSeparator1);
        jSeparator1.setBounds(30, 102, 1050, 10);

        jPanel4.add(jCmbCliente);
        jCmbCliente.setBounds(440, 60, 640, 30);

        jTxtPrecio.setEnabled(false);
        jPanel4.add(jTxtPrecio);
        jTxtPrecio.setBounds(90, 150, 150, 30);

        jTxtCantidad.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTxtCantidadActionPerformed(evt);
            }
        });
        jTxtCantidad.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTxtCantidadKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTxtCantidadKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTxtCantidadKeyTyped(evt);
            }
        });
        jPanel4.add(jTxtCantidad);
        jTxtCantidad.setBounds(320, 150, 150, 30);

        jLabel15.setText("CANTIDAD");
        jPanel4.add(jLabel15);
        jLabel15.setBounds(250, 150, 80, 20);

        jbtnAgregar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/Plus_32px.png"))); // NOI18N
        jbtnAgregar.setBorderPainted(false);
        jbtnAgregar.setContentAreaFilled(false);
        jbtnAgregar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnAgregarActionPerformed(evt);
            }
        });
        jPanel4.add(jbtnAgregar);
        jbtnAgregar.setBounds(1040, 150, 40, 30);

        jPanel5.setBackground(new java.awt.Color(255, 255, 255));
        jPanel5.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(71, 84, 130), 2, true));
        jPanel5.setLayout(null);

        jBtnEliminar.setBackground(new java.awt.Color(255, 255, 255));
        jBtnEliminar.setText("ELIMINAR");
        jBtnEliminar.setFocusPainted(false);
        jBtnEliminar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBtnEliminarActionPerformed(evt);
            }
        });
        jPanel5.add(jBtnEliminar);
        jBtnEliminar.setBounds(20, 110, 208, 40);

        jBtnFacturar.setBackground(new java.awt.Color(255, 255, 255));
        jBtnFacturar.setText("FACTURAR");
        jBtnFacturar.setFocusPainted(false);
        jBtnFacturar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBtnFacturarActionPerformed(evt);
            }
        });
        jPanel5.add(jBtnFacturar);
        jBtnFacturar.setBounds(20, 60, 208, 40);

        jBtnNuevaFactura.setBackground(new java.awt.Color(255, 255, 255));
        jBtnNuevaFactura.setText("NUEVA FACTURA");
        jBtnNuevaFactura.setFocusPainted(false);
        jBtnNuevaFactura.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBtnNuevaFacturaActionPerformed(evt);
            }
        });
        jPanel5.add(jBtnNuevaFactura);
        jBtnNuevaFactura.setBounds(20, 10, 208, 40);

        jPanel4.add(jPanel5);
        jPanel5.setBounds(840, 190, 244, 160);

        jPanel8.setBackground(new java.awt.Color(255, 255, 255));
        jPanel8.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(71, 84, 130), 2, true));
        jPanel8.setLayout(null);

        jTxtArticulos.setDisabledTextColor(new java.awt.Color(102, 102, 102));
        jTxtArticulos.setEnabled(false);
        jPanel8.add(jTxtArticulos);
        jTxtArticulos.setBounds(20, 30, 200, 30);

        jLabel18.setText("ARTÍCULOS");
        jPanel8.add(jLabel18);
        jLabel18.setBounds(20, 10, 80, 20);

        jLabel19.setText("TOTAL COMPRA");
        jPanel8.add(jLabel19);
        jLabel19.setBounds(20, 70, 170, 20);

        jTxtTotalC.setDisabledTextColor(new java.awt.Color(102, 102, 102));
        jTxtTotalC.setEnabled(false);
        jPanel8.add(jTxtTotalC);
        jTxtTotalC.setBounds(20, 90, 200, 30);

        jPanel4.add(jPanel8);
        jPanel8.setBounds(840, 360, 244, 130);

        jTablaVenta.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "CODIGO", "ID LIBRO", "LIBRO", "PRECIO", "CANTIDAD", "SUBTOTAL", "ISV"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane3.setViewportView(jTablaVenta);

        jPanel4.add(jScrollPane3);
        jScrollPane3.setBounds(20, 190, 810, 250);

        jTxtISV.setDisabledTextColor(new java.awt.Color(102, 102, 102));
        jTxtISV.setEnabled(false);
        jPanel4.add(jTxtISV);
        jTxtISV.setBounds(470, 450, 140, 30);

        jLabel16.setText("ISV");
        jPanel4.add(jLabel16);
        jLabel16.setBounds(440, 450, 50, 20);

        jLabel17.setText("SUBTOTAL");
        jPanel4.add(jLabel17);
        jLabel17.setBounds(620, 450, 80, 20);

        jTxtSubtotal.setDisabledTextColor(new java.awt.Color(102, 102, 102));
        jTxtSubtotal.setEnabled(false);
        jPanel4.add(jTxtSubtotal);
        jTxtSubtotal.setBounds(690, 450, 140, 30);

        jLabel20.setText("LIBRO");
        jLabel20.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        jPanel4.add(jLabel20);
        jLabel20.setBounds(30, 110, 60, 20);
        jPanel4.add(jdcFechaVenta);
        jdcFechaVenta.setBounds(140, 20, 230, 30);

        jTxtLibro.setEnabled(false);
        jPanel4.add(jTxtLibro);
        jTxtLibro.setBounds(960, 20, 120, 30);

        jLabel21.setText("TOTAL");
        jPanel4.add(jLabel21);
        jLabel21.setBounds(810, 150, 60, 20);

        jCmbMetodoPago.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCmbMetodoPagoActionPerformed(evt);
            }
        });
        jPanel4.add(jCmbMetodoPago);
        jCmbMetodoPago.setBounds(580, 150, 210, 30);

        jLabel22.setText("RECARGO");
        jPanel4.add(jLabel22);
        jLabel22.setBounds(210, 450, 80, 20);

        jTxtRecargo.setDisabledTextColor(new java.awt.Color(102, 102, 102));
        jTxtRecargo.setEnabled(false);
        jTxtRecargo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTxtRecargoActionPerformed(evt);
            }
        });
        jPanel4.add(jTxtRecargo);
        jTxtRecargo.setBounds(290, 450, 140, 30);

        jTxtStockMin.setEnabled(false);
        jPanel4.add(jTxtStockMin);
        jTxtStockMin.setBounds(700, 20, 120, 30);

        jTxtStockD.setEnabled(false);
        jPanel4.add(jTxtStockD);
        jTxtStockD.setBounds(830, 20, 120, 30);

        jTxtStockMax.setEnabled(false);
        jPanel4.add(jTxtStockMax);
        jTxtStockMax.setBounds(570, 20, 120, 30);

        jTxtUsuario.setBackground(new java.awt.Color(255, 255, 255));
        jTxtUsuario.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jTxtUsuario.setForeground(new java.awt.Color(237, 120, 74));
        jTxtUsuario.setText("AQUÍ IRÁ EL USUARIO QUE ESTÁ INGRESANDO");

        jLabel6.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jLabel6.setText("USUARIO: ");

        jbtnListaFacturas.setBackground(new java.awt.Color(237, 120, 74));
        jbtnListaFacturas.setForeground(new java.awt.Color(255, 255, 255));
        jbtnListaFacturas.setText("VER FACTURAS");
        jbtnListaFacturas.setToolTipText("");
        jbtnListaFacturas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnListaFacturasActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTxtUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, 370, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 381, Short.MAX_VALUE)
                .addComponent(jbtnListaFacturas, javax.swing.GroupLayout.PREFERRED_SIZE, 247, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, 500, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTxtUsuario)
                    .addComponent(jLabel6)
                    .addComponent(jbtnListaFacturas, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(40, 40, 40))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 650, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents


    private void jBtnEliminarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtnEliminarActionPerformed
        int selectedRow = jTablaVenta.getSelectedRow();

        if (selectedRow != -1) {
            eliminarLibro();
            DefaultTableModel model = (DefaultTableModel) jTablaVenta.getModel();
            model.removeRow(selectedRow);
            modelo.fireTableDataChanged();
        }

        enumerarListaProductos();


    }//GEN-LAST:event_jBtnEliminarActionPerformed

    private void jBtnFacturarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtnFacturarActionPerformed

        int respuesta = JOptionPane.showConfirmDialog(
                null, "¿Está seguro de realizar la factuación? \nUna vez realizada, no podrá agregar más productos.",
                "Realizar factura", JOptionPane.YES_NO_OPTION
        );

        // Verificar la respuesta del usuario
        if (respuesta == JOptionPane.YES_OPTION) {
            factura();
            detalleFactura();
            idVenta = obtenerUltimoIdVentas();
            crearFacturaPDF();
            jBtnNuevaFactura.setEnabled(true);
            limpiar();

        }


    }//GEN-LAST:event_jBtnFacturarActionPerformed

    private void jBtnNuevaFacturaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtnNuevaFacturaActionPerformed
        desbloquear();
    }//GEN-LAST:event_jBtnNuevaFacturaActionPerformed

    private void jTxtCantidadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTxtCantidadActionPerformed


    }//GEN-LAST:event_jTxtCantidadActionPerformed

    private void jbtnAgregarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnAgregarActionPerformed
        
        int cantidad = Integer.parseInt(jTxtCantidad.getText());
        int stockDisponible = Integer.parseInt(jTxtStockD.getText());
        int stockMin = Integer.parseInt(jTxtStockMin.getText());
         String libro = (String) jCmbLibro.getSelectedItem();
        if ((stockDisponible - cantidad) < stockMin) {
            JOptionPane.showMessageDialog(null, "Ha llegado al límite del stock mínimo de " + libro + ". \nPronto se quedará sin stock suficiente. Reabastezca el inventario pronto.",
                    "Stock Mínimo", JOptionPane.WARNING_MESSAGE);
        }

        tablaListaProductos();
        valoresTotales();
        bloquear();
        llenarCmbIdLibro();
        jBtnFacturar.setEnabled(true);
        jTxtPrecio.setText("0.0");
        jTxtCantidad.setText(null);
        jTxtTotal.setText("0.0");

        enumerarListaProductos();

    }//GEN-LAST:event_jbtnAgregarActionPerformed

    private void jTxtCantidadKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTxtCantidadKeyPressed


    }//GEN-LAST:event_jTxtCantidadKeyPressed

    private void jTxtCantidadKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTxtCantidadKeyTyped

    }//GEN-LAST:event_jTxtCantidadKeyTyped

    private void jCmbMetodoPagoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCmbMetodoPagoActionPerformed
        /* jCmbMetodoPago.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String selectedMethod = jCmbMetodoPago.getSelectedItem().toString();
                if (selectedMethod.equals("Tarjeta")) {
                    double recargo = 
                    jTxtRecargo.setText("150");
                } else if (selectedMethod.equals("Efectivo")) {
                    jTxtRecargo.setText("0");
                }
            }
        });*/
    }//GEN-LAST:event_jCmbMetodoPagoActionPerformed

    private void jTxtRecargoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTxtRecargoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTxtRecargoActionPerformed

    private void jbtnListaFacturasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnListaFacturasActionPerformed
        ViewFacturas listaFacturas = new ViewFacturas();
        Menu_Principal.jDesktopPane1.add(listaFacturas);
        Dimension desktopSize = Menu_Principal.jDesktopPane1.getSize();
        Dimension FrameSize = listaFacturas.getSize();
        listaFacturas.setLocation((desktopSize.width - FrameSize.width) / 2, (desktopSize.height - FrameSize.height) / 2);
        listaFacturas.show();
    }//GEN-LAST:event_jbtnListaFacturasActionPerformed

    private void jTxtCantidadKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTxtCantidadKeyReleased
        try {
            int cantidad = Integer.parseInt(jTxtCantidad.getText());
            int stockDisponible = Integer.parseInt(jTxtStockD.getText());
            int stockMin = Integer.parseInt(jTxtStockMin.getText());
            if (cantidad > stockDisponible) {
                JOptionPane.showMessageDialog(null, "No puede comprar esa cantidad de productos por stock insuficiente",
                        "Advertencia", JOptionPane.WARNING_MESSAGE);
                jTxtTotal.setText("0");
            } else {
                double precio = Double.parseDouble(jTxtPrecio.getText());
                double total = cantidad * precio;
                jTxtTotal.setText(Double.toString(total));
            }

        } catch (NumberFormatException ex) {
            System.err.println("Error al convertir el valor: " + ex.getMessage());
        }
    }//GEN-LAST:event_jTxtCantidadKeyReleased


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jBtnEliminar;
    private javax.swing.JButton jBtnFacturar;
    private javax.swing.JButton jBtnNuevaFactura;
    private javax.swing.JComboBox<String> jCmbCliente;
    private javax.swing.JComboBox<String> jCmbLibro;
    private javax.swing.JComboBox<String> jCmbMetodoPago;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JTable jTablaVenta;
    private javax.swing.JTextField jTxtArticulos;
    private javax.swing.JTextField jTxtCantidad;
    private javax.swing.JTextField jTxtCodCliente;
    private javax.swing.JTextField jTxtISV;
    private javax.swing.JTextField jTxtLibro;
    private javax.swing.JTextField jTxtPrecio;
    private javax.swing.JTextField jTxtRecargo;
    private javax.swing.JTextField jTxtStockD;
    private javax.swing.JTextField jTxtStockMax;
    private javax.swing.JTextField jTxtStockMin;
    private javax.swing.JTextField jTxtSubtotal;
    private javax.swing.JTextField jTxtTotal;
    private javax.swing.JTextField jTxtTotalC;
    private javax.swing.JLabel jTxtUsuario;
    private javax.swing.JButton jbtnAgregar;
    private javax.swing.JButton jbtnListaFacturas;
    private com.toedter.calendar.JDateChooser jdcFechaVenta;
    private javax.swing.JLabel jlblImagen;
    // End of variables declaration//GEN-END:variables
}
