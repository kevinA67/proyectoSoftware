/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package proyectog4;

import Conexion.ConexionBD;
import java.awt.Dimension;
import java.awt.Image;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.*;
import static proyectog4.Menu_Principal.jDesktopPane1;

/**
 *
 * @author
 */
public class RegistroUsuarios extends javax.swing.JInternalFrame {

    String sentenciaSQL, cod = "", user = "";
    Connection con = null;
    ConexionBD connec;
    PreparedStatement ps = null;
    ResultSet rs = null;
    DefaultTableModel modelo;
    Object datosUsuarios[] = new Object[11];

    //Permisos
    int permisoCliente, permisoAutor, permisoEditorial, permisoLibro, permisoInventario, permisoVenta, permisoReporteria;

    public RegistroUsuarios() {
        initComponents();
    }

    public void limpiar() {
        limpiarCampos();

        cod = "";
        if (jTableUsuarios.getRowCount() == 0) {
            JOptionPane.showMessageDialog(null, "No hay datos para limpiar.", "Tabla vacía", JOptionPane.INFORMATION_MESSAGE);
        } else {
            int fila = jTableUsuarios.getRowCount();
            for (int i = fila - 1; i >= 0; i--) {
                modelo.removeRow(i);
            }
        }
    }

    public void limpiarCampos() {
        jTxtCodigo.setText(null);
        jTxtUsuario.setText(null);
        jTxtContrasena.setText(null);

        //CHECKBOXS CON PERMISOS PARA ENTRAR A LOS REGISTROS
        jchkCliente.setSelected(false);
        jchkEditorial.setSelected(false);
        jchkAutor.setSelected(false);
        jchkLibro.setSelected(false);
        jchkVenta.setSelected(false);
        jchkReporte.setSelected(false);

        jTxtUsuario.requestFocus();

    }

    public void conectarBD() {
        connec = new ConexionBD("openfirekafz");
        con = connec.getConexion();
    }

    public void permisos() {

        //El valor 0 significa que el permiso es nulo o no existe
        //El valor 1 significa que el usuario tiene permiso a ese registro
        permisoCliente = 0;
        permisoAutor = 0;
        permisoEditorial = 0;
        permisoLibro = 0;
        permisoInventario = 0;
        permisoVenta = 0;
        permisoReporteria = 0;

        if (jchkCliente.isSelected()) {
            permisoCliente = 1;
        }
        if (jchkAutor.isSelected()) {
            permisoAutor = 1;
        }
        if (jchkEditorial.isSelected()) {
            permisoEditorial = 1;
        }
        if (jchkLibro.isSelected()) {
            permisoLibro = 1;
        }
        if (jchkVenta.isSelected()) {
            permisoVenta = 1;
        }
        if (jchkReporte.isSelected()) {
            permisoReporteria = 1;
        }
    }

    public void crearUsuario() {
        try {
            conectarBD();
            sentenciaSQL = "INSERT INTO usuario (idUsuario, user, password, permisoCliente, permisoAutor, permisoEditorial, permisoLibro, "
                    + "permisoInventario, permisoVenta, permisoReporteria, estadoUsuario) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            permisos();

            ps = con.prepareStatement(sentenciaSQL);
            ps.setInt(1, 0);
            ps.setString(2, jTxtUsuario.getText());
            ps.setString(3, jTxtContrasena.getText());
            ps.setInt(4, permisoCliente);
            ps.setInt(5, permisoAutor);
            ps.setInt(6, permisoEditorial);
            ps.setInt(7, permisoLibro);
            ps.setInt(8, permisoInventario);
            ps.setInt(9, permisoVenta);
            ps.setInt(10, permisoReporteria);
            ps.setInt(11, 1);
            //El valor 0 significa que el permiso está inactivo
            //El valor 1 significa que el usuario está activo
            ps.execute();
            JOptionPane.showMessageDialog(null, "DATOS INGRESADOS CORRECTAMENTE");
            con.close();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "ERROR AL INTENTAR INGRESAR LOS DATOS:  " + ex.getMessage());
        }
    }

    public void leerUsuarios() {

        conectarBD();
        sentenciaSQL = "SELECT * FROM usuario WHERE estadoUsuario = 1";
        try {
            ps = con.prepareStatement(sentenciaSQL);
            rs = ps.executeQuery();
            modelo = (DefaultTableModel) jTableUsuarios.getModel();
            modelo.setRowCount(0);
            while (rs.next()) {
                datosUsuarios[0] = (rs.getInt(1));
                datosUsuarios[1] = (rs.getString(2));
                datosUsuarios[2] = (rs.getString(3));
                datosUsuarios[3] = (rs.getInt(4));
                datosUsuarios[4] = (rs.getInt(5));
                datosUsuarios[5] = (rs.getInt(6));
                datosUsuarios[6] = (rs.getInt(7));
                datosUsuarios[7] = (rs.getInt(8));
                datosUsuarios[8] = (rs.getInt(9));
                datosUsuarios[9] = (rs.getInt(10));
                datosUsuarios[10] = (rs.getInt(11));

                modelo.addRow(datosUsuarios);
            }
            jTableUsuarios.setModel(modelo);

            con.close();

            if (jTableUsuarios.getRowCount() == 0) {
                JOptionPane.showMessageDialog(null, "No hay datos para mostrar.", "Usuarios", 1);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "ERROR NO SE PUDO LEER LOS DATOS DE LA TABLA.  " + ex.getMessage());
        }

    }

    public void actualizarUsuario() {

        try {
            conectarBD();
            sentenciaSQL = "UPDATE usuario SET user =?, password=?, permisoCliente=?, permisoAutor=?,  permisoEditorial=?, "
                    + "permisoLibro=?, permisoInventario=?, permisoVenta=?, permisoReporteria=?"
                    + " WHERE idUsuario =" + jTxtCodigo.getText().trim();

            ps = con.prepareStatement(sentenciaSQL);

            permisos();

            ps.setString(1, jTxtUsuario.getText());
            ps.setString(2, jTxtContrasena.getText());
            ps.setInt(3, permisoCliente);
            ps.setInt(4, permisoAutor);
            ps.setInt(5, permisoEditorial);
            ps.setInt(6, permisoLibro);
            ps.setInt(7, permisoInventario);
            ps.setInt(8, permisoVenta);
            ps.setInt(9, permisoReporteria);
            ps.execute();
            JOptionPane.showMessageDialog(null, "DATOS ACTUALIZADOS CORRECTAMENTE");
            con.close();
            leerUsuarios();
            limpiarCampos();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "ERROR, NO SE PUDO ACTUALIZAR LOS DATOS " + ex.getMessage());
        }
    }

    public void eliminarUsuario() {
        try {
            conectarBD();
            sentenciaSQL = "UPDATE usuario SET estadoUsuario='0' WHERE idUsuario = " + jTxtCodigo.getText().trim();
            ps = con.prepareStatement(sentenciaSQL);
            ps.execute();
            limpiarCampos();
            leerUsuarios();
            JOptionPane.showMessageDialog(null, "DATOS ELIMINADOS CORRECTAMENTE!");
            cod = "";
            con.close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "ERROR, NO SE PUDO ELIMINAR LOS DATOS " + ex.getMessage());
        }
    }

    public void buscarRegistro() {
        conectarBD();
        String codigo = jTxtCodigo.getText().trim();
        String usuario = jTxtUsuario.getText().trim();

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
                sentenciaSQL = "SELECT * FROM usuario "
                        + "WHERE estadoUsuario = 1 "
                        + "AND (idUsuario = ? OR user LIKE ?) "
                        + "AND user!='ADMIN'";
                ps = con.prepareStatement(sentenciaSQL);
                ps.setInt(1, Integer.parseInt(codigo));
                ps.setString(2, "%" + usuario + "%");

                rs = ps.executeQuery();
                modelo = (DefaultTableModel) jTableUsuarios.getModel();
                modelo.setRowCount(0);
                while (rs.next()) {
                    datosUsuarios[0] = (rs.getInt(1));
                    datosUsuarios[1] = (rs.getString(2));
                    datosUsuarios[2] = (rs.getString(3));
                    datosUsuarios[3] = (rs.getInt(4));
                    datosUsuarios[4] = (rs.getInt(5));
                    datosUsuarios[5] = (rs.getInt(6));
                    datosUsuarios[6] = (rs.getInt(7));
                    datosUsuarios[7] = (rs.getInt(8));
                    datosUsuarios[8] = (rs.getInt(9));
                    datosUsuarios[9] = (rs.getInt(10));
                    datosUsuarios[10] = (rs.getInt(11));

                    modelo.addRow(datosUsuarios);
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

        int fila = jTableUsuarios.getSelectedRow();
        jTxtCodigo.setText(jTableUsuarios.getValueAt(fila, 0).toString());
        jTxtUsuario.setText(jTableUsuarios.getValueAt(fila, 1).toString());
        jTxtContrasena.setText(jTableUsuarios.getValueAt(fila, 2).toString());
        int chkCliente = Integer.parseInt(jTableUsuarios.getValueAt(fila, 3).toString());
        int chkAutor = Integer.parseInt(jTableUsuarios.getValueAt(fila, 4).toString());
        int chkEditorial = Integer.parseInt(jTableUsuarios.getValueAt(fila, 5).toString());
        int chkLibro = Integer.parseInt(jTableUsuarios.getValueAt(fila, 6).toString());
        int chkInventario = Integer.parseInt(jTableUsuarios.getValueAt(fila, 7).toString());
        int chkVenta = Integer.parseInt(jTableUsuarios.getValueAt(fila, 8).toString());
        int chkReporte = Integer.parseInt(jTableUsuarios.getValueAt(fila, 9).toString());

        if (chkCliente == 1) {
            jchkCliente.setSelected(true);
        } else {
            jchkCliente.setSelected(false);
        }

        if (chkAutor == 1) {
            jchkAutor.setSelected(true);
        } else {
            jchkAutor.setSelected(false);
        }

        if (chkEditorial == 1) {
            jchkEditorial.setSelected(true);
        } else {
            jchkEditorial.setSelected(false);
        }

        if (chkLibro == 1) {
            jchkLibro.setSelected(true);
        } else {
            jchkLibro.setSelected(false);
        }

        if (chkVenta == 1) {
            jchkVenta.setSelected(true);
        } else {
            jchkVenta.setSelected(false);
        }

        if (chkReporte == 1) {
            jchkReporte.setSelected(true);
        } else {
            jchkReporte.setSelected(false);
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
    
     public void limpiarLabel(){
           CamposObligatorios.setText(" ");
            validarUsuario.setText("");
            validarContrasena.setText("");
        
    }

    //Validar campos vacios
    public boolean validarUsuarios() {
        boolean vacios = false;

        if (jTxtUsuario.getText().trim().isEmpty() || jTxtContrasena.getText().trim().isEmpty())
                {
            JOptionPane.showMessageDialog(null, "NO DEBE DEJAR CAMPOS VACIOS", "CAMPOS VACIOS", 0);
            CamposObligatorios.setText("* Campos Obligatorios ");

            
            TextoObligatorio(jTxtUsuario,validarUsuario, "*");
            TextoObligatorio(jTxtContrasena, validarContrasena, "*");
         
          
         

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

        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jlblImagen = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTableUsuarios = new javax.swing.JTable();
        jPanel4 = new javax.swing.JPanel();
        jBtnBuscarCod = new javax.swing.JButton();
        jLabel11 = new javax.swing.JLabel();
        jTxtUsuario = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        jTxtContrasena = new javax.swing.JPasswordField();
        jLabel12 = new javax.swing.JLabel();
        jTxtCodigo = new javax.swing.JTextField();
        jBtnBuscarUser = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel13 = new javax.swing.JLabel();
        jchkAutor = new javax.swing.JCheckBox();
        jchkCliente = new javax.swing.JCheckBox();
        jchkEditorial = new javax.swing.JCheckBox();
        jchkLibro = new javax.swing.JCheckBox();
        jchkVenta = new javax.swing.JCheckBox();
        jchkReporte = new javax.swing.JCheckBox();
        jBtnInfo = new javax.swing.JButton();
        jSeparator3 = new javax.swing.JSeparator();
        jbtnBitacora = new javax.swing.JButton();
        CamposObligatorios = new javax.swing.JLabel();
        validarUsuario = new javax.swing.JLabel();
        validarContrasena = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jBtnActualizar = new javax.swing.JButton();
        jBtnLeer = new javax.swing.JButton();
        jBtnCrear = new javax.swing.JButton();
        jBtnEliminar = new javax.swing.JButton();
        jBtnLimpiar = new javax.swing.JButton();

        setBackground(new java.awt.Color(255, 255, 255));
        setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(238, 123, 81)));
        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle("USUARIOS");
        setToolTipText("");
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        setFrameIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/cliente_icon2.png"))); // NOI18N
        setMaximumSize(new java.awt.Dimension(1000, 650));
        setMinimumSize(new java.awt.Dimension(1000, 650));

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jPanel2.setBackground(new java.awt.Color(237, 120, 74));

        jlblImagen.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/08- Logo The Book House.png"))); // NOI18N

        jLabel2.setFont(new java.awt.Font("Century Schoolbook", 1, 44)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("REGISTRO DE USUARIOS");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 215, Short.MAX_VALUE)
                .addComponent(jlblImagen)
                .addGap(21, 21, 21))
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

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));
        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(71, 84, 130), 2, true), "LISTA DE REGISTROS", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 1, 12), new java.awt.Color(71, 84, 130))); // NOI18N

        jTableUsuarios.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(71, 84, 130)));
        jTableUsuarios.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "COD", "USUARIO", "CONTRASEÑA", "R. CLIENTE", "R.AUTOR", "R. EDITORIAL", "R. LIBRO", "R. INVENTARIO", "R. VENTA", "REPORTES", "ESTADO"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTableUsuarios.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTableUsuariosMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jTableUsuarios);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 246, Short.MAX_VALUE)
        );

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));
        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(71, 84, 130), 2, true), "FORMULARIO DATOS", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 1, 12), new java.awt.Color(71, 84, 130))); // NOI18N

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

        jLabel11.setText("USUARIO");
        jLabel11.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);

        jLabel14.setText("CONTRASEÑA");

        jLabel12.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel12.setText("PERMISOS DE ACCESO");
        jLabel12.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);

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

        jSeparator1.setOrientation(javax.swing.SwingConstants.VERTICAL);

        jLabel13.setText("CODIGO");
        jLabel13.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);

        jchkAutor.setBackground(new java.awt.Color(255, 255, 255));
        jchkAutor.setText("Autores");

        jchkCliente.setBackground(new java.awt.Color(255, 255, 255));
        jchkCliente.setText("Clientes");

        jchkEditorial.setBackground(new java.awt.Color(255, 255, 255));
        jchkEditorial.setText("Editoriales");

        jchkLibro.setBackground(new java.awt.Color(255, 255, 255));
        jchkLibro.setText("Libros");

        jchkVenta.setBackground(new java.awt.Color(255, 255, 255));
        jchkVenta.setText("Ventas");

        jchkReporte.setBackground(new java.awt.Color(255, 255, 255));
        jchkReporte.setText("Reportería");

        jBtnInfo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/info_16px.png"))); // NOI18N
        jBtnInfo.setBorderPainted(false);
        jBtnInfo.setContentAreaFilled(false);
        jBtnInfo.setFocusPainted(false);
        jBtnInfo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBtnInfoActionPerformed(evt);
            }
        });

        jbtnBitacora.setBackground(new java.awt.Color(237, 120, 74));
        jbtnBitacora.setForeground(new java.awt.Color(255, 255, 255));
        jbtnBitacora.setText("BITÁCORA");
        jbtnBitacora.setToolTipText("");
        jbtnBitacora.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnBitacoraActionPerformed(evt);
            }
        });

        CamposObligatorios.setFont(new java.awt.Font("Dialog", 2, 12)); // NOI18N
        CamposObligatorios.setForeground(new java.awt.Color(255, 51, 51));

        validarUsuario.setFont(new java.awt.Font("Dialog", 2, 12)); // NOI18N
        validarUsuario.setForeground(new java.awt.Color(255, 51, 51));

        validarContrasena.setFont(new java.awt.Font("Dialog", 2, 12)); // NOI18N
        validarContrasena.setForeground(new java.awt.Color(255, 51, 51));

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(4, 4, 4)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(40, 40, 40)
                        .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(jTxtCodigo, javax.swing.GroupLayout.PREFERRED_SIZE, 240, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(40, 40, 40)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addGap(60, 60, 60)
                                .addComponent(jTxtUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, 240, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addGap(90, 90, 90)
                                .addComponent(jTxtContrasena, javax.swing.GroupLayout.PREFERRED_SIZE, 240, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(CamposObligatorios, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(validarUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(validarContrasena, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jBtnBuscarCod, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jBtnBuscarUser, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(100, 100, 100)
                        .addComponent(jLabel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(40, 40, 40)
                        .addComponent(jBtnInfo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel4Layout.createSequentialGroup()
                                        .addGap(43, 43, 43)
                                        .addComponent(jchkCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel4Layout.createSequentialGroup()
                                        .addGap(41, 41, 41)
                                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jchkAutor, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jchkEditorial, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE))))
                                .addGap(18, 18, Short.MAX_VALUE)
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel4Layout.createSequentialGroup()
                                        .addComponent(jchkReporte, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(37, 37, 37))
                                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jchkLibro, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addGroup(jPanel4Layout.createSequentialGroup()
                                            .addComponent(jchkVenta, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addGap(0, 0, Short.MAX_VALUE))))
                                .addGap(43, 43, 43))
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jbtnBitacora, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addContainerGap())))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(30, 30, 30)
                        .addComponent(jSeparator3)
                        .addGap(10, 10, 10))))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTxtCodigo, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(30, 30, 30)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTxtUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(30, 30, 30)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTxtContrasena, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(20, 20, 20)
                .addComponent(CamposObligatorios, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addComponent(jBtnBuscarCod, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(30, 30, 30)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jBtnBuscarUser, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(validarUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(30, 30, 30)
                .addComponent(validarContrasena, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addGap(10, 10, 10)
                                .addComponent(jBtnInfo, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(6, 6, 6)
                        .addComponent(jchkLibro))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(30, 30, 30)
                        .addComponent(jchkCliente)))
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(14, 14, 14)
                        .addComponent(jchkVenta))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(jchkEditorial)))
                .addGap(18, 18, 18)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jchkAutor)
                    .addComponent(jchkReporte))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jbtnBitacora, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jPanel5.setBackground(new java.awt.Color(255, 255, 255));
        jPanel5.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(71, 84, 130), 2, true));

        jBtnActualizar.setBackground(new java.awt.Color(255, 255, 255));
        jBtnActualizar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/update_24px.png"))); // NOI18N
        jBtnActualizar.setText("ACTUALIZAR");
        jBtnActualizar.setFocusPainted(false);
        jBtnActualizar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBtnActualizarActionPerformed(evt);
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

        jBtnCrear.setBackground(new java.awt.Color(255, 255, 255));
        jBtnCrear.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/Add User Group Woman Man2_24px.png"))); // NOI18N
        jBtnCrear.setText("CREAR");
        jBtnCrear.setFocusPainted(false);
        jBtnCrear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBtnCrearActionPerformed(evt);
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

        jBtnLimpiar.setBackground(new java.awt.Color(255, 255, 255));
        jBtnLimpiar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/erase_24px.png"))); // NOI18N
        jBtnLimpiar.setText("LIMPIAR");
        jBtnLimpiar.setFocusPainted(false);
        jBtnLimpiar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBtnLimpiarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jBtnCrear, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jBtnLeer, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jBtnActualizar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jBtnEliminar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jBtnLimpiar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(jBtnCrear)
                .addGap(9, 9, 9)
                .addComponent(jBtnLeer)
                .addGap(9, 9, 9)
                .addComponent(jBtnActualizar)
                .addGap(9, 9, 9)
                .addComponent(jBtnEliminar)
                .addGap(9, 9, 9)
                .addComponent(jBtnLimpiar)
                .addGap(18, 18, 18))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(10, 10, 10)
                        .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(11, 11, 11))
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(6, 6, 6)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(4, 4, 4)
                        .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(2, 2, 2))
                    .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, 240, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(15, 15, 15))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jTableUsuariosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableUsuariosMouseClicked
        establecerValores();
    }//GEN-LAST:event_jTableUsuariosMouseClicked

    private void jBtnBuscarCodActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtnBuscarCodActionPerformed
        buscarRegistro();
    }//GEN-LAST:event_jBtnBuscarCodActionPerformed

    private void jBtnBuscarUserActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtnBuscarUserActionPerformed
        buscarRegistro();
    }//GEN-LAST:event_jBtnBuscarUserActionPerformed

    private void jBtnInfoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtnInfoActionPerformed
        JOptionPane.showMessageDialog(this, "Marcar cada casilla si desea que el usuario tenga acceso a cada uno de los formularios de registros del sistema", "Información", JOptionPane.INFORMATION_MESSAGE);
    }//GEN-LAST:event_jBtnInfoActionPerformed

    private void jBtnActualizarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtnActualizarActionPerformed
         boolean vacios = validarUsuarios();
     if(vacios==true){
         
     }else{
        
        actualizarUsuario();
        limpiarLabel();
     }
    }//GEN-LAST:event_jBtnActualizarActionPerformed

    private void jBtnLeerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtnLeerActionPerformed
        leerUsuarios();
    }//GEN-LAST:event_jBtnLeerActionPerformed

    private void jBtnCrearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtnCrearActionPerformed
     boolean vacios = validarUsuarios();
     if(vacios==true){
         
     }else{
        crearUsuario();
        limpiarLabel();
     }
    }//GEN-LAST:event_jBtnCrearActionPerformed

    private void jBtnEliminarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtnEliminarActionPerformed
        if (!jTxtCodigo.getText().equals("")) {
            int respuesta = JOptionPane.showConfirmDialog(null, "¿Está seguro de eliminar el cliente?", "Advertencia", JOptionPane.YES_NO_OPTION, 1);
            if (respuesta == 0) {
                eliminarUsuario();
            }
        } else {
            JOptionPane.showMessageDialog(null, "Debe seleccionar un cliente para poder ejecutar esta acción.", "Elimar", 1);
        }
    }//GEN-LAST:event_jBtnEliminarActionPerformed

    private void jBtnLimpiarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtnLimpiarActionPerformed
        limpiar();
    }//GEN-LAST:event_jBtnLimpiarActionPerformed

    private void jbtnBitacoraActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnBitacoraActionPerformed
        BitacoraUsuarios bitacora = new BitacoraUsuarios();
        Menu_Principal.jDesktopPane1.add(bitacora);
        Dimension desktopSize = Menu_Principal.jDesktopPane1.getSize();
        Dimension FrameSize = bitacora.getSize();
        bitacora.setLocation((desktopSize.width - FrameSize.width) / 2, (desktopSize.height - FrameSize.height) / 2);
        bitacora.show();
    }//GEN-LAST:event_jbtnBitacoraActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel CamposObligatorios;
    private javax.swing.JButton jBtnActualizar;
    private javax.swing.JButton jBtnBuscarCod;
    private javax.swing.JButton jBtnBuscarUser;
    private javax.swing.JButton jBtnCrear;
    private javax.swing.JButton jBtnEliminar;
    private javax.swing.JButton jBtnInfo;
    private javax.swing.JButton jBtnLeer;
    private javax.swing.JButton jBtnLimpiar;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JTable jTableUsuarios;
    private javax.swing.JTextField jTxtCodigo;
    private javax.swing.JPasswordField jTxtContrasena;
    private javax.swing.JTextField jTxtUsuario;
    private javax.swing.JButton jbtnBitacora;
    private javax.swing.JCheckBox jchkAutor;
    private javax.swing.JCheckBox jchkCliente;
    private javax.swing.JCheckBox jchkEditorial;
    private javax.swing.JCheckBox jchkLibro;
    private javax.swing.JCheckBox jchkReporte;
    private javax.swing.JCheckBox jchkVenta;
    private javax.swing.JLabel jlblImagen;
    private javax.swing.JLabel validarContrasena;
    private javax.swing.JLabel validarUsuario;
    // End of variables declaration//GEN-END:variables
}
