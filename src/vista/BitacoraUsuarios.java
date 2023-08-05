/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vista;

import proyectog4.*;
import Conexion.ConexionBD;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Alejandra
 */
public class BitacoraUsuarios extends javax.swing.JInternalFrame {

    String usuario, cod, sentenciaSQL;
    Connection con = null;
    ConexionBD connec;
    PreparedStatement ps = null;
    ResultSet rs = null;
    DefaultTableModel modelo;
    Object datosBitacora[] = new Object[6];

    public BitacoraUsuarios() {
        initComponents();
    }

    public void conectarBD() {
        connec = new ConexionBD("abarroteria");
        con = connec.getConexion();
    }

    public void limpiar() {
        limpiarCampos();

        cod = "";
        if (jTableBitacora.getRowCount() == 0) {
            JOptionPane.showMessageDialog(null, "No hay datos para limpiar.", "Tabla vacía", JOptionPane.INFORMATION_MESSAGE);
        } else {
            int fila = jTableBitacora.getRowCount();
            for (int i = fila - 1; i >= 0; i--) {
                modelo.removeRow(i);
            }
        }
    }

    public void limpiarCampos() {
        jTxtUsuario.setText(null);
        jDateFecha.setDate(null);
    }

    public void buscarUsuario() {
        try {
            usuario = jTxtUsuario.getText();
            conectarBD();
            sentenciaSQL = "SELECT idBitacora, usuario.user, accionUsuario, fecha, hora, estadoBitacora \n"
                    + "FROM bitacorausuario \n"
                    + "INNER JOIN usuario ON bitacorausuario.idUsuario = usuario.idUsuario \n"
                    + "WHERE estadoBitacora = 1 \n"
                    + "AND (usuario.user != 'ADMIN' AND usuario.user LIKE ?)";
            ps = con.prepareStatement(sentenciaSQL);
            ps.setString(1, "%" + usuario + "%");
            rs = ps.executeQuery();
            modelo = (DefaultTableModel) jTableBitacora.getModel();
            modelo.setRowCount(0);
            while (rs.next()) {
                datosBitacora[0] = (rs.getInt(1));
                datosBitacora[1] = (rs.getString(2));
                datosBitacora[2] = (rs.getString(3));
                datosBitacora[3] = (rs.getString(4));
                datosBitacora[4] = (rs.getString(5));
                datosBitacora[5] = (rs.getInt(6));
                modelo.addRow(datosBitacora);
            }
            jTableBitacora.setModel(modelo);

            con.close();

            if (jTableBitacora.getRowCount() == 0) {
                JOptionPane.showMessageDialog(null, "No hay datos para mostrar.", "Usuarios", 1);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "ERROR NO SE PUDO LEER LOS DATOS DE LA TABLA.  " + ex.getMessage());
        }
    }
    
        public void buscarFecha() {
        try {
        Date fechaSeleccionada = jDateFecha.getDate();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String fechaComoCadena = dateFormat.format(fechaSeleccionada);

        conectarBD();
        sentenciaSQL = "SELECT idBitacora, bitacoraUsuario.idUsuario, accionUsuario, fecha, hora, estadoBitacora \n" +
                                "FROM bitacorausuario \n" +
                                "INNER JOIN usuario ON bitacorausuario.idUsuario = usuario.idUsuario \n" +
                                "WHERE estadoBitacora = 1 \n" +
                                "AND (usuario.user != 'ADMIN' AND fecha = ?)";
        ps = con.prepareStatement(sentenciaSQL);
        ps.setString(1, fechaComoCadena);
        rs = ps.executeQuery();
        modelo = (DefaultTableModel) jTableBitacora.getModel();
        modelo.setRowCount(0);

            while (rs.next()) {
                datosBitacora[0] = (rs.getInt(1));
                datosBitacora[1] = (rs.getString(2));
                datosBitacora[2] = (rs.getString(3));
                datosBitacora[3] = (rs.getString(4));
                datosBitacora[4] = (rs.getString(5));
                datosBitacora[5] = (rs.getInt(6));
                modelo.addRow(datosBitacora);
            }
            jTableBitacora.setModel(modelo);

            con.close();

            if (jTableBitacora.getRowCount() == 0) {
                JOptionPane.showMessageDialog(null, "No hay datos para mostrar.", "Usuarios", 1);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "ERROR NO SE PUDO LEER LOS DATOS DE LA TABLA.  " + ex.getMessage());
        }
    }


    public void leerBitacora() {
        cod = "";
        conectarBD();
        sentenciaSQL = "SELECT bitacorausuario.idBitacora, user, accionUsuario, fecha, hora, estadoBitacora FROM bitacorausuario\n"
                + "INNER JOIN usuario ON usuario.idUsuario = bitacorausuario.idUsuario\n"
                + "WHERE usuario.user!='ADMIN'"
                + "AND estadoBitacora = 1";

        try {
            ps = con.prepareStatement(sentenciaSQL);
            rs = ps.executeQuery();
            modelo = (DefaultTableModel) jTableBitacora.getModel();
            modelo.setRowCount(0);
            while (rs.next()) {
                datosBitacora[0] = (rs.getInt(1));
                datosBitacora[1] = (rs.getString(2));
                datosBitacora[2] = (rs.getString(3));
                datosBitacora[3] = (rs.getString(4));
                datosBitacora[4] = (rs.getString(5));
                datosBitacora[5] = (rs.getInt(6));
                modelo.addRow(datosBitacora);
            }
            jTableBitacora.setModel(modelo);

            con.close();

            if (jTableBitacora.getRowCount() == 0) {
                JOptionPane.showMessageDialog(null, "No hay datos para mostrar.", "Usuarios", 1);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "ERROR NO SE PUDO LEER LOS DATOS DE LA TABLA.  " + ex.getMessage());
        }
    }
    
    public void eliminarBitacora(){
          try {
            conectarBD();
            sentenciaSQL = "UPDATE bitacoraUsuario SET estadoBitacora='0' WHERE idBitacora = " + this.cod;
            ps = con.prepareStatement(sentenciaSQL);
            ps.execute();
            limpiarCampos();
            leerBitacora();
            JOptionPane.showMessageDialog(null, "DATOS ELIMINADOS CORRECTAMENTE!");
            cod = "";
            con.close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "ERROR, NO SE PUDO ELIMINAR LOS DATOS " + ex.getMessage());
        }
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
        jlblImagen = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTableBitacora = new javax.swing.JTable();
        jPanel4 = new javax.swing.JPanel();
        jBtnBuscarCod = new javax.swing.JButton();
        jLabel11 = new javax.swing.JLabel();
        jTxtUsuario = new javax.swing.JTextField();
        jBtnBuscarUser = new javax.swing.JButton();
        jLabel13 = new javax.swing.JLabel();
        jDateFecha = new com.toedter.calendar.JDateChooser();
        jPanel5 = new javax.swing.JPanel();
        jBtnLeer = new javax.swing.JButton();
        jBtnEliminar = new javax.swing.JButton();
        jBtnLimpiar1 = new javax.swing.JButton();

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setTitle("BITÁCORA");
        setToolTipText("");

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setLayout(null);

        jPanel2.setBackground(new java.awt.Color(237, 120, 74));

        jlblImagen.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/08- Logo The Book House.png"))); // NOI18N

        jLabel2.setFont(new java.awt.Font("Century Schoolbook", 1, 44)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("BITÁCORA DE USUARIOS");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 232, Short.MAX_VALUE)
                .addComponent(jlblImagen)
                .addGap(23, 23, 23))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel2)
                .addGap(21, 21, 21))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addComponent(jlblImagen, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        jPanel1.add(jPanel2);
        jPanel2.setBounds(0, 0, 0, 100);

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));
        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(71, 84, 130), 2, true), "LISTA DE REGISTROS", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 0, 12), new java.awt.Color(71, 84, 130))); // NOI18N

        jTableBitacora.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(71, 84, 130)));
        jTableBitacora.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "COD", "USUARIO", "ACCIÓN DEL USUARIO", "FECHA", "HORA", "ESTADO"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTableBitacora.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTableBitacoraMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jTableBitacora);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 946, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 336, Short.MAX_VALUE)
        );

        jPanel1.add(jPanel3);
        jPanel3.setBounds(10, 280, 970, 360);

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));
        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(71, 84, 130), 2, true), "BÚSQUEDA DE DATOS", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 0, 12), new java.awt.Color(71, 84, 130))); // NOI18N
        jPanel4.setLayout(null);

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
        jPanel4.add(jBtnBuscarCod);
        jBtnBuscarCod.setBounds(330, 80, 40, 30);

        jLabel11.setText("POR FECHA");
        jLabel11.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        jPanel4.add(jLabel11);
        jLabel11.setBounds(400, 60, 80, 20);
        jPanel4.add(jTxtUsuario);
        jTxtUsuario.setBounds(40, 80, 290, 30);

        jBtnBuscarUser.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/search_24px.png"))); // NOI18N
        jBtnBuscarUser.setToolTipText("Presione para buscar un cliente por su número de identidad");
        jBtnBuscarUser.setBorderPainted(false);
        jBtnBuscarUser.setContentAreaFilled(false);
        jBtnBuscarUser.setFocusPainted(false);
        jBtnBuscarUser.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/search_32px.png"))); // NOI18N
        jBtnBuscarUser.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBtnBuscarUserActionPerformed(evt);
            }
        });
        jPanel4.add(jBtnBuscarUser);
        jBtnBuscarUser.setBounds(660, 80, 40, 30);

        jLabel13.setText("POR USUARIO");
        jLabel13.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        jPanel4.add(jLabel13);
        jLabel13.setBounds(40, 60, 150, 20);
        jPanel4.add(jDateFecha);
        jDateFecha.setBounds(400, 80, 260, 30);

        jPanel1.add(jPanel4);
        jPanel4.setBounds(10, 106, 720, 160);

        jPanel5.setBackground(new java.awt.Color(255, 255, 255));
        jPanel5.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(71, 84, 130), 2, true));
        jPanel5.setMinimumSize(new java.awt.Dimension(470, 118));
        jPanel5.setLayout(null);

        jBtnLeer.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/view_24px.png"))); // NOI18N
        jBtnLeer.setText("LEER");
        jBtnLeer.setFocusPainted(false);
        jBtnLeer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBtnLeerActionPerformed(evt);
            }
        });
        jPanel5.add(jBtnLeer);
        jBtnLeer.setBounds(20, 20, 208, 31);

        jBtnEliminar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/Delete User Male_24px.png"))); // NOI18N
        jBtnEliminar.setText("ELIMINAR");
        jBtnEliminar.setFocusPainted(false);
        jBtnEliminar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBtnEliminarActionPerformed(evt);
            }
        });
        jPanel5.add(jBtnEliminar);
        jBtnEliminar.setBounds(20, 60, 208, 31);

        jBtnLimpiar1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/erase_24px.png"))); // NOI18N
        jBtnLimpiar1.setText("LIMPIAR");
        jBtnLimpiar1.setFocusPainted(false);
        jBtnLimpiar1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBtnLimpiar1ActionPerformed(evt);
            }
        });
        jPanel5.add(jBtnLimpiar1);
        jBtnLimpiar1.setBounds(20, 100, 208, 31);

        jPanel1.add(jPanel5);
        jPanel5.setBounds(740, 114, 240, 150);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 988, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 652, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jTableBitacoraMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableBitacoraMouseClicked
        int fila = jTableBitacora.getSelectedRow();
        this.cod = jTableBitacora.getValueAt(fila, 0).toString();
    }//GEN-LAST:event_jTableBitacoraMouseClicked

    private void jBtnBuscarCodActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtnBuscarCodActionPerformed
        buscarUsuario();
    }//GEN-LAST:event_jBtnBuscarCodActionPerformed

    private void jBtnBuscarUserActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtnBuscarUserActionPerformed
        buscarFecha();
    }//GEN-LAST:event_jBtnBuscarUserActionPerformed

    private void jBtnLeerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtnLeerActionPerformed
        leerBitacora();
    }//GEN-LAST:event_jBtnLeerActionPerformed

    private void jBtnEliminarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtnEliminarActionPerformed
        eliminarBitacora();
    }//GEN-LAST:event_jBtnEliminarActionPerformed

    private void jBtnLimpiar1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtnLimpiar1ActionPerformed
        limpiar();
    }//GEN-LAST:event_jBtnLimpiar1ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jBtnBuscarCod;
    private javax.swing.JButton jBtnBuscarUser;
    private javax.swing.JButton jBtnEliminar;
    private javax.swing.JButton jBtnLeer;
    private javax.swing.JButton jBtnLimpiar1;
    private com.toedter.calendar.JDateChooser jDateFecha;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTableBitacora;
    private javax.swing.JTextField jTxtUsuario;
    private javax.swing.JLabel jlblImagen;
    // End of variables declaration//GEN-END:variables
}
