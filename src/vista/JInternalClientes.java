/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vista;

import Conexion.ConexionBD;
import java.awt.Color;

import java.sql.*;

import java.util.Date;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author User
 */
public class JInternalClientes extends javax.swing.JInternalFrame {
 String usuario, cod, sentenciaSQL;
    Connection con = null;
    ConexionBD connec;
    PreparedStatement ps = null;
    ResultSet rs = null;
    DefaultTableModel modelo;
    Object datosClientes[] = new Object[7];
    String nombreTabla = "CLIENTES";
    /**
     * Creates new form JInternalClientes
     */
    public JInternalClientes() {
        initComponents();
         jCmbSexo.addItem("SELECCIONE UNA OPCIÓN: ");
        llenarCmbSexo();
        jTxtIdentidad.requestFocus();
    }

     public void ConexionBD() {
        connec = new ConexionBD("abarroteria");
        con = connec.getConexion();
    }

    public void llenarCmbSexo() {
        try {
            ConexionBD();
            ps = (PreparedStatement) con.prepareStatement("SELECT id_sexo, sexo FROM sexo");
            rs = ps.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id_sexo");
                id++;
                String nombre = rs.getString("sexo");
                jCmbSexo.addItem(nombre);
                jCmbSexo.setSelectedItem(id);
            }

            con.close();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage());
        }
    }

    public void bitacora(int idUsuario, String accionUsuario) {
        try {
            ConexionBD();
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

    public void limpiar() {
        limpiarCampos();

        cod = "";
        if (jTableClientes.getRowCount() == 0) {
            JOptionPane.showMessageDialog(null, "No hay datos para limpiar.", "Tabla vacía", JOptionPane.INFORMATION_MESSAGE);
        } else {
            int fila = jTableClientes.getRowCount();
            for (int i = fila - 1; i >= 0; i--) {
                modelo.removeRow(i);
            }
        }
    }

    public void limpiarCampos() {

        TxtId.setText(null);
        jTxtIdentidad.setText(null);
        jTxtNombreCliente1.setText(null);
        jTxtTelefono.setText(null);
        jTxtCorreo.setText(null);
        jCmbSexo.setSelectedIndex(0);
        jTxtDireccion.setText(null);
        jTxtIdentidad.requestFocus();
    }

    public void crearCliente() {
        try {
            ConexionBD();
            sentenciaSQL = "INSERT INTO clientes ( id_cliente, num_identidad, nombre_cliente, id_sexo, direccion, telefono, correo_electronico, estado) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ? )";
            ps = con.prepareStatement(sentenciaSQL);
            int indiceSexo = jCmbSexo.getSelectedIndex();

            int estado = 1; //1 será activo, y 0 desactivo (eliminado)

            ps.setInt(1, 0);
            ps.setString(2, jTxtIdentidad.getText());
            ps.setString(3, jTxtNombreCliente1.getText());
            ps.setInt(4, indiceSexo);
            ps.setString(5, jTxtDireccion.getText());
            ps.setString(6, jTxtTelefono.getText());

            ps.setString(7, jTxtCorreo.getText());

            ps.setInt(8, estado);
            ps.execute();

            // String accion = "CREACIÓN DE REGISTRO EN LA TABLA " + nombreTabla;
            // bitacora(classPrin.idUsuario, accion);
            JOptionPane.showMessageDialog(null, "DATOS INGRESADOS CORRECTAMENTE");
            con.close();
            limpiarCampos();
            leerClientes();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "ERROR AL INTENTAR INGRESAR LOS DATOS:  " + ex.getMessage());
        }
    }

    public void leerClientes() {
        cod = "";
        ConexionBD();
        sentenciaSQL = "SELECT id_cliente, num_identidad, nombre_cliente, sexo, telefono, direccion, correo_electronico FROM clientes INNER JOIN sexo ON clientes.id_sexo = sexo.id_sexo "
                + "WHERE estado = " + " 1";
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

            //String accion = "LECTURA A LOS REGISTROS DE LA TABLA " + nombreTabla;
            // bitacora(classPrin.idUsuario, accion);
            con.close();

            if (jTableClientes.getRowCount() == 0) {
                JOptionPane.showMessageDialog(null, "No hay datos para mostrar.", "Clientes", 1);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "ERROR NO SE PUDO LEER LOS DATOS DE LA TABLA.  " + ex.getMessage());
        }

    }

    public void actualizarCliente() {

        try {
            ConexionBD();
            sentenciaSQL = "UPDATE clientes SET num_identidad=?, nombre_cliente=?, id_sexo=?, direccion=?, telefono=?,  correo_electronico=? "
                    + " WHERE id_cliente =" + TxtId.getText();
            ps = con.prepareStatement(sentenciaSQL);
            int indiceSexo = jCmbSexo.getSelectedIndex();

            ps.setString(1, jTxtIdentidad.getText());
            ps.setString(2, jTxtNombreCliente1.getText());
            ps.setInt(3, indiceSexo);
            ps.setString(4, jTxtDireccion.getText());
            ps.setString(5, jTxtTelefono.getText());
            ps.setString(6, jTxtCorreo.getText());

            ps.execute();

            // String accion = "ACTUALIZACIÓN DE REGISTRO EN LA TABLA " + nombreTabla;
            //bitacora(classPrin.idUsuario, accion);
            JOptionPane.showMessageDialog(null, "DATOS ACTUALIZADOS CORRECTAMENTE");
            con.close();
            leerClientes();
            limpiarCampos();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "ERROR, NO SE PUDO ACTUALIZAR LOS DATOS " + ex.getMessage());
        }
    }

    public void eliminarCliente() {
        try {
            ConexionBD();
            sentenciaSQL = "UPDATE clientes SET estado='0' WHERE id_cliente =" + TxtId.getText();
            ps = con.prepareStatement(sentenciaSQL);
            ps.execute();
            limpiarCampos();

            //String accion = "ELIMINACIÓN DE REGISTRO EN LA TABLA " + nombreTabla;
            // bitacora(classPrin.idUsuario, accion);
            JOptionPane.showMessageDialog(null, "DATOS ELIMINADOS CORRECTAMENTE!");
            leerClientes();
            cod = "";
            con.close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "ERROR, NO SE PUDO ELIMINAR LOS DATOS " + ex.getMessage());
        }
    }

    public void establecerValores() {

        int fila = jTableClientes.getSelectedRow();
        TxtId.setText(jTableClientes.getValueAt(fila, 0).toString());

        jTxtIdentidad.setText(jTableClientes.getValueAt(fila, 1).toString());
        jTxtNombreCliente1.setText(jTableClientes.getValueAt(fila, 2).toString());

        String sexo = jTableClientes.getValueAt(fila, 3).toString();
        jTxtTelefono.setText(jTableClientes.getValueAt(fila, 4).toString());

        jTxtDireccion.setText(jTableClientes.getValueAt(fila, 5).toString());
        jTxtCorreo.setText(jTableClientes.getValueAt(fila, 6).toString());
        jCmbSexo.setSelectedItem(sexo);

    }
    
     //Validar que no se repita la identidad
    public boolean validarIdentidad(){
        
        boolean vacios =false;
         for (int i =0 ;i< jTableClientes.getRowCount();i++){
               if(jTableClientes.getValueAt(i, 1).equals(jTxtIdentidad.getText())){
                   
                   JOptionPane.showMessageDialog(null, "El numero de Identidad ya existe");
                   modelo.removeRow(i);
                   limpiar();
                          leerClientes();
               vacios=true;
               }}
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

        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jTxtIdentidad = new javax.swing.JFormattedTextField();
        jLabel9 = new javax.swing.JLabel();
        jBtnBuscarIdentidad1 = new javax.swing.JButton();
        jBtnBuscarNombre1 = new javax.swing.JButton();
        TxtId = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jCmbSexo = new javax.swing.JComboBox<>();
        jTxtTelefono = new javax.swing.JFormattedTextField();
        jLabel12 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jTxtCorreo = new javax.swing.JTextField();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTxtDireccion = new javax.swing.JTextArea();
        jLabel10 = new javax.swing.JLabel();
        jTxtNombreCliente1 = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTableClientes = new javax.swing.JTable();
        jPanel6 = new javax.swing.JPanel();
        jButtonCrear = new javax.swing.JButton();
        jButtonLeer = new javax.swing.JButton();
        jButtonAct = new javax.swing.JButton();
        jButtonElim = new javax.swing.JButton();
        jButtonLimpiar = new javax.swing.JButton();

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setLayout(null);

        jPanel2.setBackground(new java.awt.Color(255, 0, 0));

        jLabel1.setFont(new java.awt.Font("Arial Rounded MT Bold", 0, 48)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("CLIENTES");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(426, 426, 426)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 281, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(433, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(26, Short.MAX_VALUE))
        );

        jPanel1.add(jPanel2);
        jPanel2.setBounds(0, 0, 1140, 90);

        jPanel3.setBackground(new java.awt.Color(204, 204, 204));
        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "REGISTRO", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 1, 12), new java.awt.Color(255, 0, 0))); // NOI18N

        try {
            jTxtIdentidad.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("####-####-#####")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }

        jLabel9.setText("NÚMERO DE IDENTIDAD");
        jLabel9.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);

        jBtnBuscarIdentidad1.setToolTipText("Presione para buscar un cliente por su número de identidad");
        jBtnBuscarIdentidad1.setBorderPainted(false);
        jBtnBuscarIdentidad1.setContentAreaFilled(false);
        jBtnBuscarIdentidad1.setFocusPainted(false);
        jBtnBuscarIdentidad1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBtnBuscarIdentidad1ActionPerformed(evt);
            }
        });

        jBtnBuscarNombre1.setToolTipText("Presione para buscar un cliente por nombre");
        jBtnBuscarNombre1.setBorderPainted(false);
        jBtnBuscarNombre1.setContentAreaFilled(false);
        jBtnBuscarNombre1.setFocusPainted(false);
        jBtnBuscarNombre1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBtnBuscarNombre1ActionPerformed(evt);
            }
        });

        jLabel11.setText("NOMBRE COMPLETO");
        jLabel11.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);

        jLabel13.setText("CODIGO");

        try {
            jTxtTelefono.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("(+504) ####-####")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }

        jLabel12.setText("TELÉFONO");
        jLabel12.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);

        jLabel14.setText("CORREO");

        jTxtDireccion.setColumns(20);
        jTxtDireccion.setRows(5);
        jScrollPane3.setViewportView(jTxtDireccion);

        jLabel10.setText("DIRECCION");

        jLabel15.setText("SEXO");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(158, 158, 158)
                        .addComponent(jCmbSexo, javax.swing.GroupLayout.PREFERRED_SIZE, 230, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jTxtNombreCliente1, javax.swing.GroupLayout.PREFERRED_SIZE, 230, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(TxtId, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jTxtIdentidad, javax.swing.GroupLayout.PREFERRED_SIZE, 226, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jBtnBuscarNombre1, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jBtnBuscarIdentidad1, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(36, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, 0)
                                .addComponent(jTxtTelefono, javax.swing.GroupLayout.PREFERRED_SIZE, 230, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGap(10, 10, 10)
                                .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, 0)
                                .addComponent(jTxtCorreo, javax.swing.GroupLayout.PREFERRED_SIZE, 230, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, 0)
                                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 290, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(21, 21, 21))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(156, 156, 156))))
            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel3Layout.createSequentialGroup()
                    .addGap(102, 102, 102)
                    .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(308, Short.MAX_VALUE)))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(TxtId, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jBtnBuscarIdentidad1, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(11, 11, 11)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jTxtNombreCliente1, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jTxtIdentidad, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jBtnBuscarNombre1, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(40, 40, 40))
                    .addComponent(jCmbSexo, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTxtTelefono, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTxtCorreo, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel3Layout.createSequentialGroup()
                    .addGap(132, 132, 132)
                    .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(147, Short.MAX_VALUE)))
        );

        jPanel1.add(jPanel3);
        jPanel3.setBounds(10, 100, 480, 320);

        jPanel5.setBackground(new java.awt.Color(204, 204, 204));
        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(71, 84, 130), 2, true), "LISTA DE REGISTROS", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 1, 12), new java.awt.Color(255, 51, 0))); // NOI18N

        jTableClientes.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(71, 84, 130)));
        jTableClientes.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "IDENTIDAD", "NOMBRE", "SEXO", "TELEFONO", "DIRECCION", "CORREO"
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

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 600, Short.MAX_VALUE)
                .addGap(12, 12, 12))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 480, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel1.add(jPanel5);
        jPanel5.setBounds(500, 100, 630, 510);

        jPanel6.setBackground(new java.awt.Color(204, 204, 204));
        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Gestion", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI Historic", 1, 12), new java.awt.Color(0, 0, 0))); // NOI18N

        jButtonCrear.setBackground(new java.awt.Color(255, 255, 255));
        jButtonCrear.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jButtonCrear.setForeground(new java.awt.Color(0, 0, 0));
        jButtonCrear.setText("  Crear");
        jButtonCrear.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jButtonCrear.setBorderPainted(false);
        jButtonCrear.setFocusable(false);
        jButtonCrear.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jButtonCrearMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jButtonCrearMouseExited(evt);
            }
        });
        jButtonCrear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCrearActionPerformed(evt);
            }
        });

        jButtonLeer.setBackground(new java.awt.Color(255, 255, 255));
        jButtonLeer.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jButtonLeer.setForeground(new java.awt.Color(0, 0, 0));
        jButtonLeer.setText("  Leer");
        jButtonLeer.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jButtonLeer.setBorderPainted(false);
        jButtonLeer.setFocusable(false);
        jButtonLeer.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jButtonLeerMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jButtonLeerMouseExited(evt);
            }
        });
        jButtonLeer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonLeerActionPerformed(evt);
            }
        });

        jButtonAct.setBackground(new java.awt.Color(255, 255, 255));
        jButtonAct.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jButtonAct.setForeground(new java.awt.Color(0, 0, 0));
        jButtonAct.setText("Actualizar");
        jButtonAct.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jButtonAct.setBorderPainted(false);
        jButtonAct.setFocusable(false);
        jButtonAct.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jButtonActMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jButtonActMouseExited(evt);
            }
        });
        jButtonAct.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonActActionPerformed(evt);
            }
        });

        jButtonElim.setBackground(new java.awt.Color(255, 255, 255));
        jButtonElim.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jButtonElim.setForeground(new java.awt.Color(0, 0, 0));
        jButtonElim.setText("Eliminar");
        jButtonElim.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jButtonElim.setBorderPainted(false);
        jButtonElim.setFocusable(false);
        jButtonElim.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jButtonElimMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jButtonElimMouseExited(evt);
            }
        });
        jButtonElim.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonElimActionPerformed(evt);
            }
        });

        jButtonLimpiar.setBackground(new java.awt.Color(255, 255, 255));
        jButtonLimpiar.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jButtonLimpiar.setForeground(new java.awt.Color(0, 0, 0));
        jButtonLimpiar.setText("Limpiar");
        jButtonLimpiar.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jButtonLimpiar.setFocusable(false);
        jButtonLimpiar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jButtonLimpiarMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jButtonLimpiarMouseExited(evt);
            }
        });
        jButtonLimpiar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonLimpiarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButtonLimpiar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jButtonAct, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jButtonCrear, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jButtonElim, javax.swing.GroupLayout.DEFAULT_SIZE, 152, Short.MAX_VALUE)
                            .addComponent(jButtonLeer, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButtonCrear, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonLeer, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButtonAct, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonElim, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jButtonLimpiar, javax.swing.GroupLayout.DEFAULT_SIZE, 41, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel1.add(jPanel6);
        jPanel6.setBounds(80, 430, 330, 180);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 1148, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 618, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jBtnBuscarIdentidad1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtnBuscarIdentidad1ActionPerformed
        //   if (!jTxtIdentidad.getText().equals("    -    -     ")) {
            //       buscarCliente(1);
            //   } else {
            //     JOptionPane.showMessageDialog(null, "El campo está vacío.", "Buscar", 1);
            //   }
    }//GEN-LAST:event_jBtnBuscarIdentidad1ActionPerformed

    private void jBtnBuscarNombre1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtnBuscarNombre1ActionPerformed
        //   if (!jTxtNombreCliente.getText().isEmpty()) {
            //     buscarCliente(0);
            //   } else {
            //   JOptionPane.showMessageDialog(null, "El campo está vacío.", "Buscar", 1);
            //   }
    }//GEN-LAST:event_jBtnBuscarNombre1ActionPerformed

    private void jTableClientesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableClientesMouseClicked
        establecerValores();
    }//GEN-LAST:event_jTableClientesMouseClicked

    private void jButtonCrearMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButtonCrearMouseEntered
        jButtonCrear.setBackground(Color.red);
        jButtonCrear.setForeground(Color.white);

        // TODO add your handling code here:
    }//GEN-LAST:event_jButtonCrearMouseEntered

    private void jButtonCrearMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButtonCrearMouseExited
        jButtonCrear.setBackground(Color.white);
        jButtonCrear.setForeground(Color.black);
        // TODO add your handling code here:
    }//GEN-LAST:event_jButtonCrearMouseExited

    private void jButtonCrearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCrearActionPerformed

        // TODO add your handling code here:

        boolean  validarid= validarIdentidad();
        if(validarid ==true){

        }else{
            crearCliente();
            limpiarCampos();
        }
    }//GEN-LAST:event_jButtonCrearActionPerformed

    private void jButtonLeerMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButtonLeerMouseEntered
        jButtonLeer.setBackground(Color.red);
        jButtonLeer.setForeground(Color.white);
        // TODO add your handling code here:
    }//GEN-LAST:event_jButtonLeerMouseEntered

    private void jButtonLeerMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButtonLeerMouseExited
        jButtonLeer.setBackground(Color.white);
        jButtonLeer.setForeground(Color.black);
        // TODO add your handling code here:
    }//GEN-LAST:event_jButtonLeerMouseExited

    private void jButtonLeerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonLeerActionPerformed
        // TODO add your handling code here:
        leerClientes();
    }//GEN-LAST:event_jButtonLeerActionPerformed

    private void jButtonActMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButtonActMouseEntered
        jButtonAct.setBackground(Color.red);
        jButtonAct.setForeground(Color.white);
        // TODO add your handling code here:
    }//GEN-LAST:event_jButtonActMouseEntered

    private void jButtonActMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButtonActMouseExited
        jButtonAct.setBackground(Color.white);
        jButtonAct.setForeground(Color.black);
        // TODO add your handling code here:
    }//GEN-LAST:event_jButtonActMouseExited

    private void jButtonActActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonActActionPerformed
        // TODO add your handling code here:
        actualizarCliente();
        limpiarCampos();
    }//GEN-LAST:event_jButtonActActionPerformed

    private void jButtonElimMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButtonElimMouseEntered
        jButtonElim.setBackground(Color.red);
        jButtonElim.setForeground(Color.white);
        // TODO add your handling code here:
    }//GEN-LAST:event_jButtonElimMouseEntered

    private void jButtonElimMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButtonElimMouseExited
        jButtonElim.setBackground(Color.white);
        jButtonElim.setForeground(Color.black);
        // TODO add your handling code here:
    }//GEN-LAST:event_jButtonElimMouseExited

    private void jButtonElimActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonElimActionPerformed
        // TODO add your handling code here:
        eliminarCliente();
        limpiarCampos();
    }//GEN-LAST:event_jButtonElimActionPerformed

    private void jButtonLimpiarMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButtonLimpiarMouseEntered
        jButtonLimpiar.setBackground(Color.red);
        jButtonLimpiar.setForeground(Color.white);
        // TODO add your handling code here:
    }//GEN-LAST:event_jButtonLimpiarMouseEntered

    private void jButtonLimpiarMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButtonLimpiarMouseExited
        jButtonLimpiar.setBackground(Color.white);
        jButtonLimpiar.setForeground(Color.black);
        // TODO add your handling code here:
    }//GEN-LAST:event_jButtonLimpiarMouseExited

    private void jButtonLimpiarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonLimpiarActionPerformed
        // TODO add your handling code here:
        limpiar();
        jTxtIdentidad.setEnabled(true);
    }//GEN-LAST:event_jButtonLimpiarActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField TxtId;
    private javax.swing.JButton jBtnBuscarIdentidad1;
    private javax.swing.JButton jBtnBuscarNombre1;
    private javax.swing.JButton jButtonAct;
    private javax.swing.JButton jButtonCrear;
    private javax.swing.JButton jButtonElim;
    private javax.swing.JButton jButtonLeer;
    private javax.swing.JButton jButtonLimpiar;
    private javax.swing.JComboBox<String> jCmbSexo;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTable jTableClientes;
    private javax.swing.JTextField jTxtCorreo;
    private javax.swing.JTextArea jTxtDireccion;
    private javax.swing.JFormattedTextField jTxtIdentidad;
    private javax.swing.JTextField jTxtNombreCliente1;
    private javax.swing.JFormattedTextField jTxtTelefono;
    // End of variables declaration//GEN-END:variables
}
