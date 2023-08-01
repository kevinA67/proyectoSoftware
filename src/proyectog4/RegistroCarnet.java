/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package proyectog4;

import Conexion.ConexionBD;
import com.mysql.cj.jdbc.Blob;
import com.toedter.calendar.JDateChooser;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author HTS
 */
public class RegistroCarnet extends javax.swing.JInternalFrame {

    String usuario, sentenciaSQL, cod = "", cod1 = "", fechaEX, fechaEM;
    Connection con = null;
    ConexionBD connec;
    PreparedStatement ps = null;
    ResultSet rs = null;
    DefaultTableModel modelo;
    Object datosClientes[] = new Object[7];
    String direccion;
    FileInputStream archivoFoto;
    String nombreTabla = "CARNET CLIENTES";

    /**
     * Creates new form RegistroCarnet
     */
    public RegistroCarnet() {
        initComponents();
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
            ps.setTimestamp(4, new Timestamp(new java.util.Date().getTime())); //Obtener la fecha del sistema
            ps.setTimestamp(5, new Timestamp(new java.util.Date().getTime())); //Obtener la hora del sistema
            ps.setInt(6, 1);
            ps.executeUpdate();
            con.close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error al insertar en la bitácora: " + ex.getMessage());
        }
    }

    public void limpiar() {
        limpiarCampos();

        cod = "";
//        if (jTableClientes.getRowCount() == 0) {
//            JOptionPane.showMessageDialog(null, "No hay datos para limpiar.", "Tabla vacía", JOptionPane.INFORMATION_MESSAGE);
//        } else if{
        int fila = jTablaCarnet.getRowCount();
        for (int i = fila - 1; i >= 0; i--) {
            modelo.removeRow(i);
//            }
        }
    }

    public void limpiarCampos() {
        jTxtIdentidad.setValue(null);
        jTxtNombreClientes.setText(null);
        jTxtIdentidad.requestFocus();
        jdcFechaExpiracion.setCalendar(null);
        jdcFechaEmision.setCalendar(null);
        jlblFoto.setIcon(null);

    }

    public void crearCarnet() {
        try {
            SimpleDateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd");
            fechaEM = dFormat.format(jdcFechaEmision.getDate());
            fechaEX = dFormat.format(jdcFechaExpiracion.getDate());
            conectarBD();
            sentenciaSQL = "INSERT INTO carnet_cliente (idCarnet, idCliente, fotoCliente, fechaEmision, fechaExpiracion, estadoCarnet) "
                    + "VALUES (?, ?, ?, ?, ?, ?)";
            ps = con.prepareStatement(sentenciaSQL);
            int estado = 1;
            archivoFoto = new FileInputStream(direccion);

            ps.setInt(1, 0);
            ps.setInt(2, Integer.valueOf(cod1));
            ps.setBinaryStream(3, archivoFoto);
            ps.setString(4, fechaEM);
            ps.setString(5, fechaEX);
            ps.setInt(6, estado);
            ps.execute();

            String accion = "CREACIÓN DE REGISTRO EN LA TABLA " + nombreTabla;
            bitacora(classPrin.idUsuario, accion);

            JOptionPane.showMessageDialog(null, "DATOS INGRESADOS CORRECTAMENTE");
            con.close();
            limpiarCampos();
            leerCarnet();

        } catch (FileNotFoundException ex) {
            JOptionPane.showMessageDialog(null, "ERROR AL INTENTAR INGRESAR LOS DATOS:  " + ex.getMessage());
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "ERROR AL INTENTAR INGRESAR LOS DATOS:  " + ex.getMessage());
        }
    }

    public void crearCarnetCobro() {
        try {
            SimpleDateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd");
            int fila = jTablaCarnet.getSelectedRow();
            this.cod = jTablaCarnet.getValueAt(jTablaCarnet.getRowCount() - 1, 0).toString();
            conectarBD();
            sentenciaSQL = "INSERT INTO cobro_carnet (idCobro, idCarnet, fechaCobro, montoCobro) "
                    + "VALUES (?, ?, ?, ?)";
            ps = con.prepareStatement(sentenciaSQL);

            ps.setInt(1, 0);
            ps.setInt(2, Integer.valueOf(cod));
            ps.setString(3, fechaEM);
            ps.setDouble(4, Double.valueOf(jtxtCosto.getText()));
            ps.execute();
//            JOptionPane.showMessageDialog(null, "DATOS INGRESADOS CORRECTAMENTE");
            con.close();
            limpiarCampos();
            leerCarnet();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "ERROR AL INTENTAR INGRESAR LOS DATOS:  " + ex.getMessage());
        }
    }

    public void actualizarCarnet() {

        try {
            SimpleDateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd");
            String fechaEM = dFormat.format(jdcFechaEmision.getDate());
            String fechaEX = dFormat.format(jdcFechaExpiracion.getDate());
            conectarBD();
            sentenciaSQL = "UPDATE carnet_cliente SET fotoCliente=?, fechaEmision=?, fechaExpiracion=? WHERE idCarnet =" + this.cod;
            ps = con.prepareStatement(sentenciaSQL);
            archivoFoto = new FileInputStream(direccion);

            ps.setBinaryStream(1, archivoFoto);
            ps.setString(2, fechaEM);
            ps.setString(3, fechaEX);
            ps.execute();
            JOptionPane.showMessageDialog(null, "DATOS ACTUALIZADOS CORRECTAMENTE");
            con.close();
            leerCarnet();
            limpiarCampos();

            String accion = "ACTUALIZACIÓN DE REGISTRO EN LA TABLA " + nombreTabla;
            bitacora(classPrin.idUsuario, accion);

        } catch (FileNotFoundException ex) {
            JOptionPane.showMessageDialog(null, "ERROR, NO SE PUDO ACTUALIZAR LOS DATOS: " + ex.getMessage());
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "ERROR, NO SE PUDO ACTUALIZAR LOS DATOS: " + ex.getMessage());
        }
    }

    public void leerCarnet() {
        cod = "";
        conectarBD();
        sentenciaSQL = "select idCarnet, c.numIdentidad, c.nombreCliente, fotoCliente, fechaEmision, fechaExpiracion, "
                + "estadoCarnet from carnet_cliente as cc inner join cliente as c "
                + "on cc.idCliente=c.idCliente WHERE estadoCarnet=1";
        try {
            ps = con.prepareStatement(sentenciaSQL);
            rs = ps.executeQuery();
            modelo = (DefaultTableModel) jTablaCarnet.getModel();
            modelo.setRowCount(0);
            while (rs.next()) {
                datosClientes[0] = (rs.getInt(1));
                datosClientes[1] = (rs.getString(2));
                datosClientes[2] = (rs.getString(3));
                datosClientes[3] = (rs.getString(4));
                datosClientes[4] = (rs.getString(5));
                datosClientes[5] = (rs.getString(6));
                modelo.addRow(datosClientes);
            }
            jTablaCarnet.setModel(modelo);
            String accion = "LECTURA DE REGISTROS DE LA TABLA " + nombreTabla;
            bitacora(classPrin.idUsuario, accion);

            con.close();

            if (jTablaCarnet.getRowCount() == 0) {
                JOptionPane.showMessageDialog(null, "No hay datos para mostrar.", "Clientes", 1);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "ERROR NO SE PUDO LEER LOS DATOS DE LA TABLA.  " + ex.getMessage());
        }
    }

    public void eliminarCarnet() {
        try {
            conectarBD();
            sentenciaSQL = "UPDATE carnet_cliente SET estadoCarnet='0' WHERE idCarnet =" + this.cod;
            ps = con.prepareStatement(sentenciaSQL);
            ps.execute();
            limpiarCampos();

            String accion = "ELIMINACIÓN DE REGISTRO EN LA TABLA " + nombreTabla;
            bitacora(classPrin.idUsuario, accion);

            JOptionPane.showMessageDialog(null, "DATOS ELIMINADOS CORRECTAMENTE!");
            leerCarnet();
            cod = "";
            con.close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "ERROR, NO SE PUDO ELIMINAR LOS DATOS " + ex.getMessage());
        }
    }

    public void buscarCliente(int condicion) {
        try {
            if (condicion == 1) {
                sentenciaSQL = "SELECT idCliente, numIdentidad, nombreCliente, sexo, telefono, correo, direccion FROM cliente "
                        + "INNER JOIN sexo ON cliente.idSexo = sexo.idSexo "
                        + "WHERE numIdentidad like '%" + jTxtIdentidad.getText() + "' and estadoCliente=1";
            } else if (condicion == 0) {
                sentenciaSQL = "SELECT idCliente, numIdentidad, nombreCliente, sexo, telefono, correo, direccion FROM cliente "
                        + "INNER JOIN sexo ON cliente.idSexo = sexo.idSexo "
                        + "WHERE nombreCliente like '%" + jTxtNombreClientes.getText() + "' and estadoCliente=1";
            }

            int fila = jTableClientes.getRowCount();
            for (int i = fila - 1; i >= 0; i--) {
                modelo.removeRow(i);
            }
            conectarBD();

            ps = con.prepareStatement(sentenciaSQL);
            rs = ps.executeQuery();
            modelo = (DefaultTableModel) jTableClientes.getModel();
            while (rs.next()) {
                datosClientes[0] = (rs.getInt(1));
                datosClientes[1] = (rs.getString(2));
                datosClientes[2] = (rs.getString(3));
                datosClientes[3] = (rs.getString(4));
                datosClientes[4] = (rs.getString(5));
                datosClientes[5] = (rs.getString(6));
                datosClientes[6] = (rs.getString(7));
                modelo.addRow(datosClientes);
            }
            jTableClientes.setModel(modelo);
            con.close();

            String accion = "BÚSQUEDA DE REGISTRO EN LA TABLA " + nombreTabla;
            bitacora(classPrin.idUsuario, accion);

            if (jTableClientes.getRowCount() == 0) {
                JOptionPane.showMessageDialog(null, "NO SE PUDO ENCONTRAR EL CLIENTE");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "ERROR, NO SE PUDO ENCONTRAR EL CLIENTE. " + ex.getMessage());
        }
    }

    public void establecerValores() {
        int fila = jTableClientes.getSelectedRow();
        this.cod1 = jTableClientes.getValueAt(fila, 0).toString();
        jTxtIdentidad.setText(jTableClientes.getValueAt(fila, 1).toString());
        jTxtNombreClientes.setText(jTableClientes.getValueAt(fila, 2).toString());
        jlblFoto.setIcon(null);
    }

    public void establecerValores2() {
        conectarBD();
        try {
            int fila = jTablaCarnet.getSelectedRow();
            this.cod = jTablaCarnet.getValueAt(fila, 0).toString();
            jTxtIdentidad.setText(jTablaCarnet.getValueAt(fila, 1).toString());
            jTxtNombreClientes.setText(jTablaCarnet.getValueAt(fila, 2).toString());
            SimpleDateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date fecha;
            fecha = dFormat.parse(jTablaCarnet.getValueAt(fila, 4).toString());
            jdcFechaEmision.setDate(fecha);
            fecha = dFormat.parse(jTablaCarnet.getValueAt(fila, 5).toString());
            jdcFechaExpiracion.setDate(fecha);

            sentenciaSQL = "select fotoCliente from carnet_cliente where idCarnet=" + cod;
            ps = con.prepareStatement(sentenciaSQL);
            rs = ps.executeQuery();
            while (rs.next()) {
                Blob fotos = (Blob) rs.getBlob(1);
                byte[] recuperar = fotos.getBytes(1, (int) fotos.length());
                BufferedImage img = ImageIO.read(new ByteArrayInputStream(recuperar));
                Image imagen = img.getScaledInstance(jlblFoto.getWidth(), jlblFoto.getHeight(), Image.SCALE_DEFAULT);
                jlblFoto.setIcon(new ImageIcon(imagen));
            }

        } catch (ParseException ex) {
            JOptionPane.showMessageDialog(null, "ERROR NO SE PUDO LEER LOS DATOS DE LA TABLA.  " + ex.getMessage());
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, "ERROR NO SE PUDO LEER LOS DATOS DE LA TABLA.  " + ex.getMessage());
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "ERROR NO SE PUDO LEER LOS DATOS DE LA TABLA.  " + ex.getMessage());
        }

    }

    public void leerClientes() {
        cod = "";
        conectarBD();
        sentenciaSQL = "SELECT idCliente, numIdentidad, nombreCliente, sexo, telefono, correo, direccion FROM cliente "
                + "INNER JOIN sexo ON cliente.idSexo = sexo.idSexo "
                + "WHERE estadoCliente = " + "1";
        try {
            ps = con.prepareStatement(sentenciaSQL);
            rs = ps.executeQuery();
            modelo = (DefaultTableModel) jTableClientes.getModel();
            modelo.setRowCount(0);
            while (rs.next()) {
                datosClientes[0] = (rs.getInt(1));
                datosClientes[1] = (rs.getString(2));
                datosClientes[2] = (rs.getString(3));
                datosClientes[3] = (rs.getString(4));
                datosClientes[4] = (rs.getString(5));
                datosClientes[5] = (rs.getString(6));
                datosClientes[6] = (rs.getString(7));
                modelo.addRow(datosClientes);
            }
            jTableClientes.setModel(modelo);

            con.close();

            if (jTableClientes.getRowCount() == 0) {
                JOptionPane.showMessageDialog(null, "No hay datos para mostrar.", "Clientes", 1);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "ERROR NO SE PUDO LEER LOS DATOS DE LA TABLA.  " + ex.getMessage());
        }

    }

    //marcar los campos obligatorios *
    public void TextoObligatorio(JTextField Field, JLabel label, String s) {
        if (Field.getText().isEmpty()) {
            label.setText(s);
        } else {
            label.setText("");
        }
    }

    public void TextoObligatorio(JFormattedTextField Field, JLabel label, String s, String mascara) {
        if (Field.getText().equals(mascara)) {
            label.setText(s);
        } else {
            label.setText("");
        }
    }

    public void limpiarLabel() {
        CamposObligatorios.setText(" ");
        validarNombre.setText("");
        validarid.setText("");
        validarEmision.setText("");
        validarExpiracion.setText("");

    }

    //Validar campos vacios
    public boolean validarCarnet() {
        boolean vacios = false;

        if (jTxtNombreClientes.getText().trim().isEmpty() || jTxtIdentidad.getText().trim().isEmpty() || jdcFechaEmision.getDate() == null || jdcFechaExpiracion.getDate() == null) {
            JOptionPane.showMessageDialog(null, "NO DEBE DEJAR CAMPOS VACIOS", "CAMPOS VACIOS", 0);
            CamposObligatorios.setText("* Campos Obligatorios ");

            TextoObligatorio(jTxtNombreClientes, validarNombre, "*");
            TextoObligatorio(jTxtIdentidad, validarid, "*", "    -    -     ");

            if (jdcFechaEmision.getDate() == null) {
                validarEmision.setText("*");

            } else {
                validarEmision.setText("");
            }
            if (jdcFechaExpiracion.getDate() == null) {
                validarExpiracion.setText("*");

            } else {
                validarExpiracion.setText("");
            }

            vacios = true;

        } else {
            vacios = false;
            limpiarLabel();
        }
        return vacios;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTxtUsuario = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jlblImagen1 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jBtnBuscarIdentidad1 = new javax.swing.JButton();
        jBtnBuscarNombre1 = new javax.swing.JButton();
        jTxtNombreClientes = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jTxtIdentidad = new javax.swing.JFormattedTextField();
        validarNombre = new javax.swing.JLabel();
        validarid = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTableClientes = new javax.swing.JTable();
        jBtnLeer1 = new javax.swing.JButton();
        jBtnLimpiar1 = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        jLabel14 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        validarEmision = new javax.swing.JLabel();
        validarExpiracion = new javax.swing.JLabel();
        jdcFechaEmision = new com.toedter.calendar.JDateChooser();
        jdcFechaExpiracion = new com.toedter.calendar.JDateChooser();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTablaCarnet = new javax.swing.JTable();
        jPanel6 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jlblFoto = new javax.swing.JLabel();
        jPanel8 = new javax.swing.JPanel();
        jBtnCrear = new javax.swing.JButton();
        jBtnLeer = new javax.swing.JButton();
        jBtnEliminar = new javax.swing.JButton();
        jBtnActualizar = new javax.swing.JButton();
        jBtnLimpiar = new javax.swing.JButton();
        jPanel9 = new javax.swing.JPanel();
        jtxtCosto = new javax.swing.JTextField();
        CamposObligatorios = new javax.swing.JLabel();

        setBackground(new java.awt.Color(255, 255, 255));
        setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle("CARNET");
        setFrameIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/cliente_icon2.png"))); // NOI18N
        setMaximumSize(new java.awt.Dimension(1000, 700));
        setMinimumSize(new java.awt.Dimension(1105, 626));
        setNormalBounds(new java.awt.Rectangle(0, 0, 107, 0));

        jTxtUsuario.setBackground(new java.awt.Color(255, 255, 255));
        jTxtUsuario.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jTxtUsuario.setForeground(new java.awt.Color(237, 120, 74));
        jTxtUsuario.setText("AQUÍ IRÁ EL USUARIO QUE ESTÁ INGRESANDO");

        jLabel6.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jLabel6.setText("USUARIO: ");

        jPanel2.setBackground(new java.awt.Color(237, 120, 74));
        jPanel2.setLayout(null);

        jlblImagen1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/08- Logo The Book House.png"))); // NOI18N
        jPanel2.add(jlblImagen1);
        jlblImagen1.setBounds(900, 0, 130, 100);

        jLabel3.setFont(new java.awt.Font("Century Schoolbook", 1, 44)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setText("REGISTRO DE CARNET");
        jPanel2.add(jLabel3);
        jLabel3.setBounds(20, 20, 497, 57);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(71, 84, 130), 2, true), "Cliente", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 16), new java.awt.Color(71, 84, 130))); // NOI18N

        jLabel2.setText("IDENTIDAD");

        jBtnBuscarIdentidad1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/search_24px.png"))); // NOI18N
        jBtnBuscarIdentidad1.setToolTipText("Presione para buscar un cliente por su número de identidad");
        jBtnBuscarIdentidad1.setBorderPainted(false);
        jBtnBuscarIdentidad1.setContentAreaFilled(false);
        jBtnBuscarIdentidad1.setFocusPainted(false);
        jBtnBuscarIdentidad1.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/search_32px.png"))); // NOI18N
        jBtnBuscarIdentidad1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBtnBuscarIdentidad1ActionPerformed(evt);
            }
        });

        jBtnBuscarNombre1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/search_24px.png"))); // NOI18N
        jBtnBuscarNombre1.setToolTipText("Presione para buscar un cliente por nombre");
        jBtnBuscarNombre1.setBorderPainted(false);
        jBtnBuscarNombre1.setContentAreaFilled(false);
        jBtnBuscarNombre1.setFocusPainted(false);
        jBtnBuscarNombre1.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/search_32px.png"))); // NOI18N
        jBtnBuscarNombre1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBtnBuscarNombre1ActionPerformed(evt);
            }
        });

        jTxtNombreClientes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTxtNombreClientesActionPerformed(evt);
            }
        });

        jLabel4.setText("NOMBRE");

        try {
            jTxtIdentidad.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("####-####-#####")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }

        validarNombre.setFont(new java.awt.Font("Dialog", 2, 12)); // NOI18N
        validarNombre.setForeground(new java.awt.Color(255, 51, 51));

        validarid.setFont(new java.awt.Font("Dialog", 2, 12)); // NOI18N
        validarid.setForeground(new java.awt.Color(255, 51, 51));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(91, 91, 91)
                        .addComponent(validarNombre, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jTxtNombreClientes, javax.swing.GroupLayout.PREFERRED_SIZE, 193, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jBtnBuscarNombre1, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(12, 12, 12)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(validarid, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jTxtIdentidad, javax.swing.GroupLayout.PREFERRED_SIZE, 206, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jBtnBuscarIdentidad1, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(2, 2, 2)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel4)
                            .addComponent(validarNombre, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTxtNombreClientes, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(validarid, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(8, 8, 8)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jBtnBuscarIdentidad1, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTxtIdentidad, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jBtnBuscarNombre1, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));
        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(71, 84, 130), 2, true), "Crear", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 16), new java.awt.Color(71, 84, 130))); // NOI18N

        jTableClientes.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(71, 84, 130)));
        jTableClientes.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "IDENTIDAD", "NOMBRE", "SEXO", "TELEFONO", "CORREO", "DIRECCION"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTableClientes.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTableClientesMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jTableClientes);

        jBtnLeer1.setBackground(new java.awt.Color(255, 255, 255));
        jBtnLeer1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/view_24px.png"))); // NOI18N
        jBtnLeer1.setText("MOSTRAR CLIENTES");
        jBtnLeer1.setFocusPainted(false);
        jBtnLeer1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBtnLeer1ActionPerformed(evt);
            }
        });

        jBtnLimpiar1.setBackground(new java.awt.Color(255, 255, 255));
        jBtnLimpiar1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/erase_24px.png"))); // NOI18N
        jBtnLimpiar1.setText("LIMPIAR");
        jBtnLimpiar1.setFocusPainted(false);
        jBtnLimpiar1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBtnLimpiar1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jBtnLeer1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jBtnLimpiar1, javax.swing.GroupLayout.PREFERRED_SIZE, 208, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jBtnLimpiar1)
                    .addComponent(jBtnLeer1))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel5.setBackground(new java.awt.Color(255, 255, 255));
        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(71, 84, 130), 2, true), "FECHAS", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 16), new java.awt.Color(71, 84, 130))); // NOI18N

        jLabel14.setText("FECHA EMISIÓN");

        jLabel10.setText("FECHA EXPIRACIÓN");

        validarEmision.setFont(new java.awt.Font("Dialog", 2, 12)); // NOI18N
        validarEmision.setForeground(new java.awt.Color(255, 51, 51));

        validarExpiracion.setFont(new java.awt.Font("Dialog", 2, 12)); // NOI18N
        validarExpiracion.setForeground(new java.awt.Color(255, 51, 51));

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(41, 41, 41)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(29, 29, 29)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jdcFechaEmision, javax.swing.GroupLayout.DEFAULT_SIZE, 217, Short.MAX_VALUE)
                    .addComponent(jdcFechaExpiracion, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(validarEmision, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(validarExpiracion, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(125, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(validarEmision, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jdcFechaExpiracion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(validarExpiracion, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jdcFechaEmision, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10))
        );

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));
        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(71, 84, 130), 2, true), "Crear", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 16), new java.awt.Color(71, 84, 130))); // NOI18N

        jTablaCarnet.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "IDENTIDAD", "NOMBRE", "FOTO CLIENTE", "FECHA EMICION", "FECHA EXPIRACION"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTablaCarnet.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTablaCarnetMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(jTablaCarnet);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 254, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(40, 40, 40))
        );

        jPanel6.setBackground(new java.awt.Color(255, 255, 255));
        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(71, 84, 130), 2, true), "Crear", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 16), new java.awt.Color(71, 84, 130))); // NOI18N

        jButton1.setText("SUBIR FOTO");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton3.setText("ELIMINAR");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jlblFoto.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, 136, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jlblFoto, javax.swing.GroupLayout.PREFERRED_SIZE, 143, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(23, 23, 23))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addComponent(jlblFoto, javax.swing.GroupLayout.PREFERRED_SIZE, 188, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 15, Short.MAX_VALUE))
        );

        jPanel8.setBackground(new java.awt.Color(255, 255, 255));
        jPanel8.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(71, 84, 130), 2, true), "Crear", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 16), new java.awt.Color(71, 84, 130))); // NOI18N

        jBtnCrear.setBackground(new java.awt.Color(255, 255, 255));
        jBtnCrear.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/Add User Group Woman Man2_24px.png"))); // NOI18N
        jBtnCrear.setText("CREAR");
        jBtnCrear.setFocusPainted(false);
        jBtnCrear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBtnCrearActionPerformed(evt);
            }
        });

        jBtnLeer.setBackground(new java.awt.Color(255, 255, 255));
        jBtnLeer.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/view_24px.png"))); // NOI18N
        jBtnLeer.setText("LEER");
        jBtnLeer.setFocusPainted(false);
        jBtnLeer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBtnLeerActionPerformed(evt);
            }
        });

        jBtnEliminar.setBackground(new java.awt.Color(255, 255, 255));
        jBtnEliminar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/Delete User Male_24px.png"))); // NOI18N
        jBtnEliminar.setText("ELIMINAR");
        jBtnEliminar.setFocusPainted(false);
        jBtnEliminar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBtnEliminarActionPerformed(evt);
            }
        });

        jBtnActualizar.setBackground(new java.awt.Color(255, 255, 255));
        jBtnActualizar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/update_24px.png"))); // NOI18N
        jBtnActualizar.setText("ACTUALIZAR");
        jBtnActualizar.setFocusPainted(false);
        jBtnActualizar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBtnActualizarActionPerformed(evt);
            }
        });

        jBtnLimpiar.setBackground(new java.awt.Color(255, 255, 255));
        jBtnLimpiar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/erase_24px.png"))); // NOI18N
        jBtnLimpiar.setText("LIMPIAR");
        jBtnLimpiar.setFocusPainted(false);
        jBtnLimpiar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBtnLimpiarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jBtnLeer, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jBtnEliminar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jBtnActualizar, javax.swing.GroupLayout.DEFAULT_SIZE, 208, Short.MAX_VALUE)
                        .addComponent(jBtnCrear, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jBtnLimpiar, javax.swing.GroupLayout.PREFERRED_SIZE, 208, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(22, Short.MAX_VALUE))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jBtnCrear)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jBtnLeer)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jBtnEliminar)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jBtnActualizar)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jBtnLimpiar)
                .addContainerGap(42, Short.MAX_VALUE))
        );

        jPanel9.setBackground(new java.awt.Color(255, 255, 255));
        jPanel9.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(71, 84, 130), 2, true), "COSTO", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 16), new java.awt.Color(71, 84, 130))); // NOI18N

        jtxtCosto.setEditable(false);
        jtxtCosto.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jtxtCosto.setText("100.00");

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addComponent(jtxtCosto, javax.swing.GroupLayout.PREFERRED_SIZE, 248, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jtxtCosto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        CamposObligatorios.setFont(new java.awt.Font("Dialog", 2, 12)); // NOI18N
        CamposObligatorios.setForeground(new java.awt.Color(255, 51, 51));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jPanel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                        .addComponent(CamposObligatorios, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(6, 6, 6))))
                            .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(17, 17, 17)
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTxtUsuario)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(CamposObligatorios, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jTxtUsuario)
                            .addComponent(jLabel6)))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(10, 10, 10)
                        .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jTableClientesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableClientesMouseClicked
        establecerValores();
    }//GEN-LAST:event_jTableClientesMouseClicked

    private void jBtnLeer1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtnLeer1ActionPerformed
        leerClientes();
    }//GEN-LAST:event_jBtnLeer1ActionPerformed

    private void jBtnBuscarNombre1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtnBuscarNombre1ActionPerformed
        if (!jTxtNombreClientes.getText().equals("")) {
            buscarCliente(0);
        } else {
            JOptionPane.showMessageDialog(null, "El campo está vacío.", "Buscar", 1);
        }
    }//GEN-LAST:event_jBtnBuscarNombre1ActionPerformed

    private void jBtnBuscarIdentidad1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtnBuscarIdentidad1ActionPerformed
        if (!jTxtIdentidad.getText().equals("    -    -     ")) {
            buscarCliente(1);
        } else {
            JOptionPane.showMessageDialog(null, "El campo está vacío.", "Buscar", 1);
        }
    }//GEN-LAST:event_jBtnBuscarIdentidad1ActionPerformed

    private void jTxtNombreClientesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTxtNombreClientesActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTxtNombreClientesActionPerformed

    private void jBtnCrearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtnCrearActionPerformed
        boolean vacios = validarCarnet();

        if (vacios == true) {

        } else {

            if (!jTxtNombreClientes.equals("")) {
                int respuesta = JOptionPane.showConfirmDialog(null, "¿Está seguro de crear el carnet?, El cliente debera pagar un monto de LPS. " + jtxtCosto.getText(), "Crear", JOptionPane.YES_NO_OPTION, 1);
                if (respuesta == 0) {
                    crearCarnet();
                    crearCarnetCobro();
                    limpiarLabel();
                }
            } else {
                JOptionPane.showMessageDialog(null, "Debe seleccionar un carnet para poder ejecutar esta acción.", "Crear", 1);
            }
        }
    }//GEN-LAST:event_jBtnCrearActionPerformed

    private void jBtnLeerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtnLeerActionPerformed
        leerCarnet();
    }//GEN-LAST:event_jBtnLeerActionPerformed

    private void jBtnEliminarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtnEliminarActionPerformed
        if (!cod.equals("")) {
            int respuesta = JOptionPane.showConfirmDialog(null, "¿Está seguro de eliminar el carnet?", "Advertencia", JOptionPane.YES_NO_OPTION, 1);
            if (respuesta == 0) {
                eliminarCarnet();
                limpiarLabel();
            }
        } else {
            JOptionPane.showMessageDialog(null, "Debe seleccionar un carnet para poder ejecutar esta acción.", "Elimar", 1);
        }
    }//GEN-LAST:event_jBtnEliminarActionPerformed

    private void jBtnActualizarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtnActualizarActionPerformed
        boolean vacios = validarCarnet();
        if (vacios == true) {

        } else {

            if (!jTxtNombreClientes.equals("")) {
                int respuesta = JOptionPane.showConfirmDialog(null, "¿Está seguro de actualizar el carnet?", "Advertencia", JOptionPane.YES_NO_OPTION, 1);
                if (respuesta == 0) {
                    actualizarCarnet();
                    limpiarLabel();
                }
            } else {
                JOptionPane.showMessageDialog(null, "Debe seleccionar un carnet para poder ejecutar esta acción.", "Actualizar", 1);
            }
        }
    }//GEN-LAST:event_jBtnActualizarActionPerformed

    private void jBtnLimpiarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtnLimpiarActionPerformed
        limpiar();
    }//GEN-LAST:event_jBtnLimpiarActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        FileNameExtensionFilter filtro = new FileNameExtensionFilter("Formatos de archivo JPEG(*.JPG;*.JPEG)", "jpg", "jpeg");
        JFileChooser archivo = new JFileChooser();
        archivo.addChoosableFileFilter(filtro);
        archivo.setDialogTitle("Archivos");
        File ruta = new File("C:/Users/User/Pictures");
        archivo.setCurrentDirectory(ruta);
        int ventana = archivo.showOpenDialog(null);
        if (ventana == JFileChooser.APPROVE_OPTION) {
            File file = archivo.getSelectedFile();
            direccion = String.valueOf(file);
            Image foto = getToolkit().getImage(direccion);
            foto = foto.getScaledInstance(jlblFoto.getWidth(), jlblFoto.getHeight(), Image.SCALE_DEFAULT);
            jlblFoto.setIcon(new ImageIcon(foto));
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jBtnLimpiar1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtnLimpiar1ActionPerformed
        int fila = jTableClientes.getRowCount();
        for (int i = fila - 1; i >= 0; i--) {
            modelo.removeRow(i);
        }
    }//GEN-LAST:event_jBtnLimpiar1ActionPerformed

    private void jTablaCarnetMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTablaCarnetMouseClicked
        establecerValores2();
    }//GEN-LAST:event_jTablaCarnetMouseClicked

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        jlblFoto.setIcon(null);
        direccion = "";
    }//GEN-LAST:event_jButton3ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel CamposObligatorios;
    private javax.swing.JButton jBtnActualizar;
    private javax.swing.JButton jBtnBuscarIdentidad1;
    private javax.swing.JButton jBtnBuscarNombre1;
    private javax.swing.JButton jBtnCrear;
    private javax.swing.JButton jBtnEliminar;
    private javax.swing.JButton jBtnLeer;
    private javax.swing.JButton jBtnLeer1;
    private javax.swing.JButton jBtnLimpiar;
    private javax.swing.JButton jBtnLimpiar1;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton3;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTablaCarnet;
    private javax.swing.JTable jTableClientes;
    private javax.swing.JFormattedTextField jTxtIdentidad;
    private javax.swing.JTextField jTxtNombreClientes;
    private javax.swing.JLabel jTxtUsuario;
    private com.toedter.calendar.JDateChooser jdcFechaEmision;
    private com.toedter.calendar.JDateChooser jdcFechaExpiracion;
    private javax.swing.JLabel jlblFoto;
    private javax.swing.JLabel jlblImagen1;
    private javax.swing.JTextField jtxtCosto;
    private javax.swing.JLabel validarEmision;
    private javax.swing.JLabel validarExpiracion;
    private javax.swing.JLabel validarNombre;
    private javax.swing.JLabel validarid;
    // End of variables declaration//GEN-END:variables
}
