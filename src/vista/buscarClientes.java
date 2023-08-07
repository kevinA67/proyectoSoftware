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
import java.sql.SQLException;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author User
 */
public class buscarClientes extends javax.swing.JInternalFrame {

    String usuario, cod, sentenciaSQL;
    Connection con = null;
    ConexionBD connec;
    PreparedStatement ps = null;
    ResultSet rs = null;
    DefaultTableModel modelo;
    Object datosClientes[] = new Object[7];
    String nombreTabla = "CLIENTES";
    String codigo="",nombre="";

    /**
     * Creates new form buscarClientes
     */
    public buscarClientes() {
        initComponents();
        leerClientes();
    }

    public void ConexionBD() {
        connec = new ConexionBD("abarroteria");
        con = connec.getConexion();
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

    public void buscar() {
        ConexionBD();
        String codigo = jTxtCodigo.getText().trim();
        String usuario = jtxtNombre.getText().trim();

        if ("".equals(codigo) && "".equals(usuario)) {
            JOptionPane.showMessageDialog(null, "NO PUEDE DEJAR AMBOS CAMPOS VACÍOS PARA LA BÚSQUEDA, \n"
                    + "DEBE AL MENOS RELLENAR UN CAMPO.", "Error de búsqueda", JOptionPane.ERROR_MESSAGE);
        } else {
            try {
                if ("".equals(codigo)) {
                    codigo = "0";
                }
                if ("".equals(usuario)) {
                    usuario = "-";
                }
                sentenciaSQL = "SELECT * FROM clientes "
                        + "WHERE estado = 1 "
                        + "AND (id_cliente = ? OR nombre_cliente LIKE ?) ";

                ps = con.prepareStatement(sentenciaSQL);
                ps.setInt(1, Integer.parseInt(codigo));
                ps.setString(2, "%" + usuario + "%");

                rs = ps.executeQuery();
                modelo = (DefaultTableModel) jTableClientes.getModel();
                modelo.setRowCount(0);
                while (rs.next()) {
                    datosClientes[0] = (rs.getInt(1));
                    datosClientes[1] = (rs.getString(2));
                    datosClientes[2] = (rs.getString(3));
                    datosClientes[3] = (rs.getInt(4));
                    datosClientes[4] = (rs.getInt(5));
                    datosClientes[5] = (rs.getInt(6));
                    datosClientes[6] = (rs.getInt(7));

                    modelo.addRow(datosClientes);
                }
                int filas = modelo.getRowCount();
                if (filas < 1) {
                    JOptionPane.showMessageDialog(null, "NO SE ENCONTRÓ NINGÚN DATO QUE CONCORDASE");
                } else {
                    JOptionPane.showMessageDialog(null, "DATOS ENCONTRADOS CORRECTAMENTE.");
                }

                con.close();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null, "ERROR NO SE PUDO LEER LOS DATOS DE LA TABLA.  " + ex.getMessage());
            }
        }
    }

    public void establecerValores() {
        int fila = jTableClientes.getSelectedRow();
        ventas1.jTxtCodCliente1.setText(jTableClientes.getValueAt(fila, 0).toString());
        ventas1.jTxtCliente.setText(jTableClientes.getValueAt(fila, 2).toString());
//        codigo=(jTableClientes.getValueAt(fila, 0).toString());
//        nombre=(jTableClientes.getValueAt(fila, 3).toString());
//        ventas1 vent=new ventas1();
//        vent.jTxtCodCliente1.setText(codigo);
//        ventas.jTxtCliente.setText(nombre);
        this.dispose();
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
        jPanel5 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTableClientes = new javax.swing.JTable();
        jBtnBuscarCod = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jTxtCodigo = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jtxtNombre = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jBtnBuscarCod1 = new javax.swing.JButton();

        setClosable(true);

        jPanel5.setBackground(new java.awt.Color(204, 204, 204));
        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(71, 84, 130), 2, true), "LISTA DE REGISTROS", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 16), new java.awt.Color(255, 51, 0))); // NOI18N

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
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 932, Short.MAX_VALUE)
                .addGap(12, 12, 12))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 289, Short.MAX_VALUE)
                .addContainerGap())
        );

        jBtnBuscarCod.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/search_24px.png"))); // NOI18N
        jBtnBuscarCod.setToolTipText("Presione para buscar un cliente por su número de identidad");
        jBtnBuscarCod.setBorderPainted(false);
        jBtnBuscarCod.setContentAreaFilled(false);
        jBtnBuscarCod.setFocusPainted(false);
        jBtnBuscarCod.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/search_32px.png"))); // NOI18N
        jBtnBuscarCod.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBtnBuscarCodActionPerformed(evt);
            }
        });

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

        jLabel2.setText("Código");

        jLabel3.setText("Nombre");

        jBtnBuscarCod1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/search_24px.png"))); // NOI18N
        jBtnBuscarCod1.setToolTipText("Presione para buscar un cliente por su número de identidad");
        jBtnBuscarCod1.setBorderPainted(false);
        jBtnBuscarCod1.setContentAreaFilled(false);
        jBtnBuscarCod1.setFocusPainted(false);
        jBtnBuscarCod1.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/search_32px.png"))); // NOI18N
        jBtnBuscarCod1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBtnBuscarCod1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(63, 63, 63)
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jTxtCodigo, javax.swing.GroupLayout.PREFERRED_SIZE, 184, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jBtnBuscarCod1, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(65, 65, 65)
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jtxtNombre, javax.swing.GroupLayout.PREFERRED_SIZE, 184, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jBtnBuscarCod, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel1Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addContainerGap()))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(39, 39, 39)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jTxtCodigo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel2)
                        .addComponent(jtxtNombre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel3))
                    .addComponent(jBtnBuscarCod)
                    .addComponent(jBtnBuscarCod1))
                .addGap(0, 395, Short.MAX_VALUE))
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                    .addContainerGap(207, Short.MAX_VALUE)
                    .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jTableClientesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableClientesMouseClicked
        establecerValores();
    }//GEN-LAST:event_jTableClientesMouseClicked

    private void jBtnBuscarCodActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtnBuscarCodActionPerformed
        buscar();
    }//GEN-LAST:event_jBtnBuscarCodActionPerformed

    private void jBtnBuscarCod1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtnBuscarCod1ActionPerformed
        buscar();
    }//GEN-LAST:event_jBtnBuscarCod1ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jBtnBuscarCod;
    private javax.swing.JButton jBtnBuscarCod1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTableClientes;
    private javax.swing.JTextField jTxtCodigo;
    private javax.swing.JTextField jtxtNombre;
    // End of variables declaration//GEN-END:variables
}
