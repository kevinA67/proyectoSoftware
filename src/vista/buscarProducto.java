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
public class buscarProducto extends javax.swing.JInternalFrame {

    String usuario, cod, sentenciaSQL;
    Connection con = null;
    ConexionBD connec;
    PreparedStatement ps = null;
    ResultSet rs = null;
    DefaultTableModel modelo;
    Object datosProductos[] = new Object[10];
    String nombreTabla = "CLIENTES";

    /**
     * Creates new form buscarProducto
     */
    public buscarProducto() {
        initComponents();
        leerproducto();

    }

    public void ConexionBD() {
        connec = new ConexionBD("abarroteria");
        con = connec.getConexion();
    }

    public void leerproducto() {
        try {
            ConexionBD();
            sentenciaSQL = "SELECT * FROM productos WHERE estado=1";
            ps = con.prepareStatement(sentenciaSQL);
            rs = ps.executeQuery();
            modelo = (DefaultTableModel) jtableProductos.getModel();
            while (rs.next()) {
                datosProductos[0] = (rs.getInt(1));
                datosProductos[1] = (rs.getString(2));
                datosProductos[2] = (rs.getString(3));
                datosProductos[3] = (rs.getString(4));
                datosProductos[4] = (rs.getString(5));
                datosProductos[5] = (rs.getString(6));
                datosProductos[6] = (rs.getString(7));
                datosProductos[7] = (rs.getString(8));
                datosProductos[8] = (rs.getString(9));
                datosProductos[9] = (rs.getString(10));
                modelo.addRow(datosProductos);
            }
            jtableProductos.setModel(modelo);
            con.close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "ERROR" + ex.getMessage());
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
                sentenciaSQL = "SELECT * FROM productos "
                        + "WHERE estado = 1 "
                        + "AND (id_cliente = ? OR nombre_cliente LIKE ?) ";

                ps = con.prepareStatement(sentenciaSQL);
                ps.setInt(1, Integer.parseInt(codigo));
                ps.setString(2, "%" + usuario + "%");

                rs = ps.executeQuery();
                modelo = (DefaultTableModel) jtableProductos.getModel();
                modelo.setRowCount(0);
                while (rs.next()) {
                    datosProductos[0] = (rs.getInt(1));
                    datosProductos[1] = (rs.getString(2));
                    datosProductos[2] = (rs.getString(3));
                    datosProductos[3] = (rs.getString(4));
                    datosProductos[4] = (rs.getString(5));
                    datosProductos[5] = (rs.getString(6));
                    datosProductos[6] = (rs.getString(7));
                    datosProductos[7] = (rs.getString(8));
                    datosProductos[8] = (rs.getString(9));
                    datosProductos[9] = (rs.getString(10));

                    modelo.addRow(datosProductos);
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

        int fila = jtableProductos.getSelectedRow();

        ventas1.jTxtCodProducto.setText(jtableProductos.getValueAt(fila, 0).toString());
        ventas1.jTxtNombreProducto.setText(jtableProductos.getValueAt(fila, 1).toString());
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
        jPanel3 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jtableProductos = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jTxtCodigo = new javax.swing.JTextField();
        jBtnBuscarCod1 = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        jtxtNombre = new javax.swing.JTextField();
        jBtnBuscarCod = new javax.swing.JButton();

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));
        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 2, true), "LISTA DE REGISTROS"));

        jtableProductos.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(71, 84, 130)));
        jtableProductos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID ", "NOMBRE", "DESCRIPCION", "PRECIO UNITARIO", "CATEGORIA", "PROVEEDOR", "VIDA UTIL", "STOCK MINIMO", "STOCK MAXIMO", "ESTADO"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, true, true, true, true, true, false, false, true
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jtableProductos.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jtableProductosMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(jtableProductos);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 330, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel2.setBackground(new java.awt.Color(255, 0, 0));

        jLabel1.setFont(new java.awt.Font("Arial Rounded MT Bold", 0, 48)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("PRODUCTOS");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(410, 410, 410)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 324, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(436, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(26, Short.MAX_VALUE))
        );

        jLabel2.setText("Código");

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

        jLabel3.setText("Nombre");

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

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(0, 15, Short.MAX_VALUE)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(34, 34, 34)
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
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(41, 41, 41)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jTxtCodigo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel2)
                        .addComponent(jtxtNombre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel3))
                    .addComponent(jBtnBuscarCod)
                    .addComponent(jBtnBuscarCod1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 45, Short.MAX_VALUE)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jtableProductosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jtableProductosMouseClicked
        establecerValores();
    }//GEN-LAST:event_jtableProductosMouseClicked

    private void jBtnBuscarCod1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtnBuscarCod1ActionPerformed
        buscar();
    }//GEN-LAST:event_jBtnBuscarCod1ActionPerformed

    private void jBtnBuscarCodActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtnBuscarCodActionPerformed
        buscar();
    }//GEN-LAST:event_jBtnBuscarCodActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jBtnBuscarCod;
    private javax.swing.JButton jBtnBuscarCod1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextField jTxtCodigo;
    private javax.swing.JTable jtableProductos;
    private javax.swing.JTextField jtxtNombre;
    // End of variables declaration//GEN-END:variables
}
