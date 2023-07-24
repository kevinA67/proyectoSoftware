/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package proyectog4;

import Conexion.ConexionBD;
import java.awt.Dimension;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Alejandra
 */
public class ViewFacturas extends javax.swing.JInternalFrame {

    String usuario, sentenciaSQL, cod = "";
    Connection con = null;
    ConexionBD connec;
    PreparedStatement ps = null;
    ResultSet rs = null;
    DefaultTableModel modelo = new DefaultTableModel();
    Object datosFactura[] = new Object[8];

    public ViewFacturas() {
        initComponents();
        leerFacturas();
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

    public void leerFacturas() {
        cod = "";
        conectarBD();
        sentenciaSQL = "SELECT venta.idVenta, cliente.nombreCliente, usuario.user, venta.fechaVenta, venta.metodoPago, venta.recargo, venta.isv, venta.montoTotal\n"
                + "FROM venta\n"
                + "JOIN usuario ON venta.idUsuario = usuario.idUsuario\n"
                + "JOIN cliente ON venta.idCliente = cliente.idCliente";
        try {

            ps = con.prepareStatement(sentenciaSQL);
            rs = ps.executeQuery();
            modelo = (DefaultTableModel) jTablaFactura.getModel();
            modelo.setRowCount(0);
            while (rs.next()) {
                datosFactura[0] = (rs.getInt(1));
                datosFactura[1] = (rs.getString(2));
                datosFactura[2] = (rs.getString(3));
                datosFactura[3] = (rs.getString(4));
                datosFactura[4] = (rs.getString(5));
                datosFactura[5] = (rs.getString(6));
                datosFactura[6] = (rs.getString(7));
                datosFactura[7] = (rs.getString(8));
                modelo.addRow(datosFactura);
            }
            jTablaFactura.setModel(modelo);

            String accion = "VISUALIZACIÓN DE LA TABLA DE FACTURAS";
            bitacora(classPrin.idUsuario, accion);
            
            con.close();

            if (jTablaFactura.getRowCount() == 0) {
                JOptionPane.showMessageDialog(null, "No hay datos para mostrar.", "Clientes", 1);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "ERROR NO SE PUDO LEER LOS DATOS DE LA TABLA.  " + ex.getMessage());
        }

    }

    public void buscarCliente() {
        try {
            String datoCliente = jTxtCliente.getText();
            sentenciaSQL = "SELECT v.idVenta, c.nombreCliente, u.user, v.fechaVenta, v.metodoPago, v.recargo, v.isv, v.montoTotal\n"
                    + "FROM venta v \n"
                    + "JOIN cliente c ON c.idCliente = v.idCliente\n"
                    + "JOIN usuario u ON u.idUsuario = v.idUsuario "
                    + "WHERE c.numIdentidad LIKE ? OR c.nombreCliente LIKE ?";

            int fila = jTablaFactura.getRowCount();
            for (int i = fila - 1; i >= 0; i--) {
                modelo.removeRow(i);
            }
            conectarBD();

            ps = con.prepareStatement(sentenciaSQL);
            ps.setString(1, "%" + datoCliente + "%");
            ps.setString(2, "%" + datoCliente + "%");

            rs = ps.executeQuery();
            modelo = (DefaultTableModel) jTablaFactura.getModel();
            while (rs.next()) {
                datosFactura[0] = (rs.getInt(1));
                datosFactura[1] = (rs.getString(2));
                datosFactura[2] = (rs.getString(3));
                datosFactura[3] = (rs.getString(4));
                datosFactura[4] = (rs.getString(5));
                datosFactura[5] = (rs.getString(6));
                datosFactura[6] = (rs.getString(7));
                datosFactura[7] = (rs.getString(8));
                modelo.addRow(datosFactura);
            }
            jTablaFactura.setModel(modelo);
            con.close();

            //String accion = "BUSCÓ UN REGISTRO EN LA TABLA " + nombreTabla;
            //bitacora(classPrin.idUsuario, accion);

            if (jTablaFactura.getRowCount() == 0) {
                JOptionPane.showMessageDialog(null, "NO SE PUDO ENCONTRAR EL CLIENTE");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "ERROR, NO SE PUDO ENCONTRAR EL CLIENTE. " + ex.getMessage());
        }
    }
    
    public void buscarIdVenta() {
        try {
            String datoId = jTxtIdVenta.getText();
            sentenciaSQL = "SELECT v.idVenta, c.nombreCliente, u.user, v.fechaVenta, v.metodoPago, v.recargo, v.isv, v.montoTotal\n"
                    + "FROM venta v \n"
                    + "JOIN cliente c ON c.idCliente = v.idCliente\n"
                    + "JOIN usuario u ON u.idUsuario = v.idUsuario "
                    + "WHERE v.idVenta = ?";

            int fila = jTablaFactura.getRowCount();
            for (int i = fila - 1; i >= 0; i--) {
                modelo.removeRow(i);
            }
            conectarBD();

            ps = con.prepareStatement(sentenciaSQL);
            ps.setString(1, datoId);

            rs = ps.executeQuery();
            modelo = (DefaultTableModel) jTablaFactura.getModel();
            while (rs.next()) {
                datosFactura[0] = (rs.getInt(1));
                datosFactura[1] = (rs.getString(2));
                datosFactura[2] = (rs.getString(3));
                datosFactura[3] = (rs.getString(4));
                datosFactura[4] = (rs.getString(5));
                datosFactura[5] = (rs.getString(6));
                datosFactura[6] = (rs.getString(7));
                datosFactura[7] = (rs.getString(8));
                modelo.addRow(datosFactura);
            }
            jTablaFactura.setModel(modelo);
            con.close();

            //String accion = "BUSCÓ UN REGISTRO EN LA TABLA " + nombreTabla;
            //bitacora(classPrin.idUsuario, accion);

            if (jTablaFactura.getRowCount() == 0) {
                JOptionPane.showMessageDialog(null, "NO SE PUDO ENCONTRAR EL CLIENTE");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "ERROR, NO SE PUDO ENCONTRAR EL CLIENTE. " + ex.getMessage());
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
        jPanel4 = new javax.swing.JPanel();
        jBtnBuscarCliente = new javax.swing.JButton();
        jLabel11 = new javax.swing.JLabel();
        jTxtCliente = new javax.swing.JTextField();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTablaFactura = new javax.swing.JTable();
        jTxtIdVenta = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        jBtnIdVenta = new javax.swing.JButton();
        jBtnLimpiar1 = new javax.swing.JButton();
        jTxtUsuario = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setTitle("FACTURAS");

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setMaximumSize(new java.awt.Dimension(1100, 680));
        jPanel1.setMinimumSize(new java.awt.Dimension(1100, 680));

        jPanel2.setBackground(new java.awt.Color(237, 120, 74));

        jlblImagen.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/08- Logo The Book House.png"))); // NOI18N

        jLabel2.setFont(new java.awt.Font("Century Schoolbook", 1, 44)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("LISTA DE FACTURAS");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 433, Short.MAX_VALUE)
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

        jBtnBuscarCliente.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/search_24px.png"))); // NOI18N
        jBtnBuscarCliente.setToolTipText("Presione para buscar un cliente por su número de identidad");
        jBtnBuscarCliente.setBorderPainted(false);
        jBtnBuscarCliente.setContentAreaFilled(false);
        jBtnBuscarCliente.setFocusPainted(false);
        jBtnBuscarCliente.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/search_32px.png"))); // NOI18N
        jBtnBuscarCliente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBtnBuscarClienteActionPerformed(evt);
            }
        });
        jPanel4.add(jBtnBuscarCliente);
        jBtnBuscarCliente.setBounds(750, 90, 40, 30);

        jLabel11.setText("CLIENTE");
        jLabel11.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        jPanel4.add(jLabel11);
        jLabel11.setBounds(60, 90, 60, 20);
        jPanel4.add(jTxtCliente);
        jTxtCliente.setBounds(130, 90, 620, 30);

        jTablaFactura.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "COD", "CLIENTE", "USUARIO", "FECHA", "MÉTODO", "RECARGO", "ISV", "TOTAL"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTablaFactura.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTablaFacturaMouseClicked(evt);
            }
        });
        jScrollPane3.setViewportView(jTablaFactura);

        jPanel4.add(jScrollPane3);
        jScrollPane3.setBounds(10, 130, 1080, 360);
        jPanel4.add(jTxtIdVenta);
        jTxtIdVenta.setBounds(130, 40, 230, 30);

        jLabel12.setText("CÓDIGO VENTA");
        jLabel12.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        jPanel4.add(jLabel12);
        jLabel12.setBounds(30, 40, 110, 20);

        jBtnIdVenta.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/search_24px.png"))); // NOI18N
        jBtnIdVenta.setToolTipText("Presione para buscar un cliente por su número de identidad");
        jBtnIdVenta.setBorderPainted(false);
        jBtnIdVenta.setContentAreaFilled(false);
        jBtnIdVenta.setFocusPainted(false);
        jBtnIdVenta.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/search_32px.png"))); // NOI18N
        jBtnIdVenta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBtnIdVentaActionPerformed(evt);
            }
        });
        jPanel4.add(jBtnIdVenta);
        jBtnIdVenta.setBounds(360, 40, 40, 30);

        jBtnLimpiar1.setBackground(new java.awt.Color(255, 255, 255));
        jBtnLimpiar1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/erase_24px.png"))); // NOI18N
        jBtnLimpiar1.setText("LIMPIAR");
        jBtnLimpiar1.setFocusPainted(false);
        jBtnLimpiar1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBtnLimpiar1ActionPerformed(evt);
            }
        });
        jPanel4.add(jBtnLimpiar1);
        jBtnLimpiar1.setBounds(930, 80, 150, 34);

        jTxtUsuario.setBackground(new java.awt.Color(255, 255, 255));
        jTxtUsuario.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jTxtUsuario.setForeground(new java.awt.Color(237, 120, 74));
        jTxtUsuario.setText("AQUÍ IRÁ EL USUARIO QUE ESTÁ INGRESANDO");

        jLabel6.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jLabel6.setText("USUARIO: ");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jTxtUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, 370, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, 503, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel6)
                    .addComponent(jTxtUsuario))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jBtnBuscarClienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtnBuscarClienteActionPerformed
        buscarCliente();
    }//GEN-LAST:event_jBtnBuscarClienteActionPerformed

    private void jBtnLimpiar1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtnLimpiar1ActionPerformed
        leerFacturas();
        jTxtIdVenta.setText(null);
        jTxtCliente.setText(null);
    }//GEN-LAST:event_jBtnLimpiar1ActionPerformed

    private void jBtnIdVentaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtnIdVentaActionPerformed
        buscarIdVenta();
    }//GEN-LAST:event_jBtnIdVentaActionPerformed

    private void jTablaFacturaMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTablaFacturaMouseClicked
        jTablaFactura.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int fila = jTablaFactura.getSelectedRow();
                    String idFactura = jTablaFactura.getValueAt(fila, 0).toString();
                    DetalleFacturaVenta detalleF = new DetalleFacturaVenta(idFactura);
                    Menu_Principal.jDesktopPane1.add(detalleF);
                    Dimension desktopSize = Menu_Principal.jDesktopPane1.getSize();
                    Dimension FrameSize = detalleF.getSize();
                    detalleF.setLocation((desktopSize.width - FrameSize.width) / 2, (desktopSize.height - FrameSize.height) / 2);
                    detalleF.show();
                }
            }
        });
    }//GEN-LAST:event_jTablaFacturaMouseClicked


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jBtnBuscarCliente;
    private javax.swing.JButton jBtnIdVenta;
    private javax.swing.JButton jBtnLimpiar1;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTable jTablaFactura;
    private javax.swing.JTextField jTxtCliente;
    private javax.swing.JTextField jTxtIdVenta;
    private javax.swing.JLabel jTxtUsuario;
    private javax.swing.JLabel jlblImagen;
    // End of variables declaration//GEN-END:variables
}
