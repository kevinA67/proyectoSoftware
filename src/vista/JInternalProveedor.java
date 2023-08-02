/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vista;

import Conexion.ConexionBD;
import java.awt.Color;
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
 * @author User
 */
public class JInternalProveedor extends javax.swing.JInternalFrame {

     String usuario, cod, sentenciaSQL;
    Connection con = null;
    ConexionBD connec;
    PreparedStatement ps = null;
    ResultSet rs = null;
    DefaultTableModel modelo;
    Object datosProveedor[] = new Object[5];
    String nombreTabla = "PROVEEDORES";
    /**
     * Creates new form JInternalProveedor
     */
    public JInternalProveedor() {
        initComponents();
         TxtNombre.requestFocus();
        TxtCodigo.setEditable(false);
    }
public void ConexionBD() {
        connec = new ConexionBD("abarroteria");
        con = connec.getConexion();
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
        if (jTableProveedor.getRowCount() == 0) {
            JOptionPane.showMessageDialog(null, "No hay datos para limpiar.", "Tabla vacía", JOptionPane.INFORMATION_MESSAGE);
        } else {
            int fila = jTableProveedor.getRowCount();
            for (int i = fila - 1; i >= 0; i--) {
                modelo.removeRow(i);
            }
        }
    }

    public void limpiarCampos() {

        TxtNombre.setText(null);
        jTxtDireccion.setText(null);
        TxtTelefono.setText(null);

        TxtNombre.requestFocus();
    }

    public void crearProveedor() {
        try {
            ConexionBD();
            sentenciaSQL = "INSERT INTO proveedores ( id_proveedor, nombre_proveedor, direccion, telefono, correo_electronico, estado) "
                    + "VALUES ( ?, ?, ?, ?, ?, ?)";
            ps = con.prepareStatement(sentenciaSQL);

            int estado = 1; //1 será activo, y 0 desactivo (eliminado)

            ps.setInt(1, 0);
            ps.setString(2, TxtNombre.getText());
            ps.setString(3, jTxtDireccion.getText());

            ps.setString(4, TxtTelefono.getText());
            ps.setString(5, TxtCorreo.getText());

            ps.setInt(6, estado);
            ps.execute();

            // String accion = "CREACIÓN DE REGISTRO EN LA TABLA " + nombreTabla;
            // bitacora(classPrin.idUsuario, accion);
            JOptionPane.showMessageDialog(null, "DATOS INGRESADOS CORRECTAMENTE");
            con.close();
            limpiarCampos();
            leerProveedor();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "ERROR AL INTENTAR INGRESAR LOS DATOS:  " + ex.getMessage());
        }
    }

    public void leerProveedor() {
        cod = "";
        ConexionBD();
        sentenciaSQL = "SELECT id_proveedor, nombre_proveedor, direccion, telefono,  correo_electronico FROM proveedores  "
                + "WHERE estado = " + "1";
        try {
            ps = con.prepareStatement(sentenciaSQL);
            rs = ps.executeQuery();
            modelo = (DefaultTableModel) jTableProveedor.getModel();
            modelo.setRowCount(0);
            while (rs.next()) {
                datosProveedor[0] = (rs.getInt(1));
                datosProveedor[1] = (rs.getString(2));
                datosProveedor[2] = (rs.getString(3));
                datosProveedor[3] = (rs.getString(4));
                datosProveedor[4] = (rs.getString(5));

                modelo.addRow(datosProveedor);

            }
            jTableProveedor.setModel(modelo);

            //String accion = "LECTURA A LOS REGISTROS DE LA TABLA " + nombreTabla;
            // bitacora(classPrin.idUsuario, accion);
            con.close();

            if (jTableProveedor.getRowCount() == 0) {
                JOptionPane.showMessageDialog(null, "No hay datos para mostrar.", "Proveedores", 1);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "ERROR NO SE PUDO LEER LOS DATOS DE LA TABLA.  " + ex.getMessage());
        }

    }

    public void actualizarProveedor() {

        try {
            ConexionBD();
            sentenciaSQL = "UPDATE proveedores SET nombre_proveedor=?, direccion=?, telefono=?,   correo_electronico=? "
                    + " WHERE id_cliente =" + TxtCodigo.getText();
            ps = con.prepareStatement(sentenciaSQL);

            ps.setString(1, TxtNombre.getText());

            ps.setString(2, jTxtDireccion.getText());
            ps.setString(3, TxtTelefono.getText());
            ps.setString(4, TxtCorreo.getText());

            ps.execute();

            // String accion = "ACTUALIZACIÓN DE REGISTRO EN LA TABLA " + nombreTabla;
            //bitacora(classPrin.idUsuario, accion);
            JOptionPane.showMessageDialog(null, "DATOS ACTUALIZADOS CORRECTAMENTE");
            con.close();
            leerProveedor();
            limpiarCampos();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "ERROR, NO SE PUDO ACTUALIZAR LOS DATOS " + ex.getMessage());
        }
    }

    public void eliminarProveedor() {
        try {
            ConexionBD();
            sentenciaSQL = "UPDATE proveedores SET estado='0' WHERE id_proveedor =" + TxtCodigo.getText();
            ps = con.prepareStatement(sentenciaSQL);
            ps.execute();
            limpiarCampos();

            //String accion = "ELIMINACIÓN DE REGISTRO EN LA TABLA " + nombreTabla;
            // bitacora(classPrin.idUsuario, accion);
            JOptionPane.showMessageDialog(null, "DATOS ELIMINADOS CORRECTAMENTE!");
            leerProveedor();
            cod = "";
            con.close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "ERROR, NO SE PUDO ELIMINAR LOS DATOS " + ex.getMessage());
        }
    }

    public void establecerValores() {

        int fila = jTableProveedor.getSelectedRow();
        TxtCodigo.setText(jTableProveedor.getValueAt(fila, 0).toString());

        TxtNombre.setText(jTableProveedor.getValueAt(fila, 1).toString());
        jTxtDireccion.setText(jTableProveedor.getValueAt(fila, 2).toString());

        TxtTelefono.setText(jTableProveedor.getValueAt(fila, 3).toString());

        TxtCorreo.setText(jTableProveedor.getValueAt(fila, 4).toString());

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
        jLabel5 = new javax.swing.JLabel();
        jLabelLogo = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        TxtCodigo = new javax.swing.JTextField();
        TxtNombre = new javax.swing.JTextField();
        TxtTelefono = new javax.swing.JFormattedTextField();
        jBtnBuscarIdentidad1 = new javax.swing.JButton();
        jBtnBuscarIdentidad2 = new javax.swing.JButton();
        TxtCorreo = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTxtDireccion = new javax.swing.JTextArea();
        jPanel6 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTableProveedor = new javax.swing.JTable();
        jPanel5 = new javax.swing.JPanel();
        jButtonCrear = new javax.swing.JButton();
        jButtonLeer = new javax.swing.JButton();
        jButtonAct = new javax.swing.JButton();
        jButtonElim = new javax.swing.JButton();
        jButtonLimpiar = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jLabelFoto = new javax.swing.JLabel();
        jButtonFoto = new javax.swing.JButton();

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setLayout(null);

        jPanel2.setBackground(new java.awt.Color(255, 0, 0));

        jLabel5.setFont(new java.awt.Font("Segoe UI Historic", 1, 36)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(255, 255, 255));
        jLabel5.setText("P R O V E E D O R E S");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addGap(32, 32, 32)
                .addComponent(jLabelLogo, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(213, 213, 213)
                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 429, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(343, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabelLogo, javax.swing.GroupLayout.DEFAULT_SIZE, 80, Short.MAX_VALUE)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel1.add(jPanel2);
        jPanel2.setBounds(0, 0, 1140, 80);

        jPanel3.setBackground(new java.awt.Color(204, 204, 204));
        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Datos de Proveedor", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI Historic", 1, 12), new java.awt.Color(0, 0, 0))); // NOI18N

        jLabel1.setFont(new java.awt.Font("Segoe UI Historic", 1, 12)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(0, 0, 0));
        jLabel1.setText("CODIGO");

        jLabel2.setFont(new java.awt.Font("Segoe UI Historic", 1, 12)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(0, 0, 0));
        jLabel2.setText("NOMBRE");

        jLabel3.setFont(new java.awt.Font("Segoe UI Historic", 1, 12)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(0, 0, 0));
        jLabel3.setText("DIRECCION");

        jLabel4.setFont(new java.awt.Font("Segoe UI Historic", 1, 12)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(0, 0, 0));
        jLabel4.setText("TELEFONO");

        TxtCodigo.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        TxtNombre.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        TxtTelefono.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        try {
            TxtTelefono.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("(504) ####-####")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }

        jBtnBuscarIdentidad1.setToolTipText("Presione para buscar un cliente por su número de identidad");
        jBtnBuscarIdentidad1.setBorderPainted(false);
        jBtnBuscarIdentidad1.setContentAreaFilled(false);
        jBtnBuscarIdentidad1.setFocusPainted(false);
        jBtnBuscarIdentidad1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBtnBuscarIdentidad1ActionPerformed(evt);
            }
        });

        jBtnBuscarIdentidad2.setToolTipText("Presione para buscar un cliente por su número de identidad");
        jBtnBuscarIdentidad2.setBorderPainted(false);
        jBtnBuscarIdentidad2.setContentAreaFilled(false);
        jBtnBuscarIdentidad2.setFocusPainted(false);
        jBtnBuscarIdentidad2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBtnBuscarIdentidad2ActionPerformed(evt);
            }
        });

        TxtCorreo.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel6.setFont(new java.awt.Font("Segoe UI Historic", 1, 12)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(0, 0, 0));
        jLabel6.setText("CORREO");

        jTxtDireccion.setColumns(20);
        jTxtDireccion.setRows(5);
        jScrollPane3.setViewportView(jTxtDireccion);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(461, 461, 461)
                        .addComponent(jLabel1))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGap(24, 24, 24)
                                .addComponent(jLabel6))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGap(10, 10, 10)
                                .addComponent(jLabel4)))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(TxtCorreo, javax.swing.GroupLayout.PREFERRED_SIZE, 279, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(TxtTelefono, javax.swing.GroupLayout.PREFERRED_SIZE, 279, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(15, 15, 15)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jLabel3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 434, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addGap(25, 25, 25)
                                .addComponent(TxtNombre, javax.swing.GroupLayout.PREFERRED_SIZE, 269, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jBtnBuscarIdentidad2, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(TxtCodigo, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(6, 6, 6)
                        .addComponent(jBtnBuscarIdentidad1, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(jLabel1)
                .addGap(6, 6, 6)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(7, 7, 7)
                        .addComponent(jLabel2))
                    .addComponent(TxtNombre, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jBtnBuscarIdentidad2, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(1, 1, 1)
                        .addComponent(TxtCodigo, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jBtnBuscarIdentidad1, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(15, 15, 15)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(TxtTelefono, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(12, 12, 12)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(TxtCorreo, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(9, 9, 9))
        );

        jPanel1.add(jPanel3);
        jPanel3.setBounds(10, 90, 590, 270);

        jPanel6.setBackground(new java.awt.Color(204, 204, 204));

        jTableProveedor.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "CODIGO", "NOMBRE", "DIRECCION", "TELEFONO", "CORREO"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.Object.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        jTableProveedor.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTableProveedorMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jTableProveedor);

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 508, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 486, Short.MAX_VALUE)
                .addGap(28, 28, 28))
        );

        jPanel1.add(jPanel6);
        jPanel6.setBounds(610, 100, 520, 520);

        jPanel5.setBackground(new java.awt.Color(204, 204, 204));
        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Gestion", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI Historic", 1, 12), new java.awt.Color(0, 0, 0))); // NOI18N

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

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButtonLimpiar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jButtonAct, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jButtonCrear, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jButtonElim, javax.swing.GroupLayout.DEFAULT_SIZE, 152, Short.MAX_VALUE)
                            .addComponent(jButtonLeer, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButtonCrear, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonLeer, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButtonAct, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonElim, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jButtonLimpiar, javax.swing.GroupLayout.DEFAULT_SIZE, 41, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel1.add(jPanel5);
        jPanel5.setBounds(20, 390, 330, 180);

        jLabelFoto.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabelFoto, javax.swing.GroupLayout.DEFAULT_SIZE, 180, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jLabelFoto, javax.swing.GroupLayout.PREFERRED_SIZE, 164, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jPanel1.add(jPanel4);
        jPanel4.setBounds(371, 370, 180, 0);

        jButtonFoto.setBackground(new java.awt.Color(255, 255, 255));
        jButtonFoto.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jButtonFoto.setForeground(new java.awt.Color(0, 0, 0));
        jButtonFoto.setText("FOTO");
        jButtonFoto.setBorder(null);
        jButtonFoto.setBorderPainted(false);
        jButtonFoto.setFocusable(false);
        jButtonFoto.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jButtonFotoMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jButtonFotoMouseExited(evt);
            }
        });
        jPanel1.add(jButtonFoto);
        jButtonFoto.setBounds(376, 550, 170, 28);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 1139, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 630, javax.swing.GroupLayout.PREFERRED_SIZE)
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

    private void jBtnBuscarIdentidad2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtnBuscarIdentidad2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jBtnBuscarIdentidad2ActionPerformed

    private void jTableProveedorMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableProveedorMouseClicked
        // TODO add your handling code here:
        establecerValores();
    }//GEN-LAST:event_jTableProveedorMouseClicked

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
        crearProveedor();
        // TODO add your handling code here:
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
        leerProveedor();
        // TODO add your handling code here:
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
        actualizarProveedor();        // TODO add your handling code here:
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
        eliminarProveedor();
        // TODO add your handling code here:
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
        limpiar();
        // TODO add your handling code here:
    }//GEN-LAST:event_jButtonLimpiarActionPerformed

    private void jButtonFotoMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButtonFotoMouseEntered
        jButtonFoto.setBackground(Color.red);
        jButtonFoto.setForeground(Color.white);
        // TODO add your handling code here:
    }//GEN-LAST:event_jButtonFotoMouseEntered

    private void jButtonFotoMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButtonFotoMouseExited
        jButtonFoto.setBackground(Color.white);
        jButtonFoto.setForeground(Color.black);
        // TODO add your handling code here:
    }//GEN-LAST:event_jButtonFotoMouseExited


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField TxtCodigo;
    private javax.swing.JTextField TxtCorreo;
    private javax.swing.JTextField TxtNombre;
    private javax.swing.JFormattedTextField TxtTelefono;
    private javax.swing.JButton jBtnBuscarIdentidad1;
    private javax.swing.JButton jBtnBuscarIdentidad2;
    private javax.swing.JButton jButtonAct;
    private javax.swing.JButton jButtonCrear;
    private javax.swing.JButton jButtonElim;
    private javax.swing.JButton jButtonFoto;
    private javax.swing.JButton jButtonLeer;
    private javax.swing.JButton jButtonLimpiar;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabelFoto;
    private javax.swing.JLabel jLabelLogo;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTable jTableProveedor;
    private javax.swing.JTextArea jTxtDireccion;
    // End of variables declaration//GEN-END:variables
}
