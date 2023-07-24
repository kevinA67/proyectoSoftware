/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package proyectog4;

import Conexion.ConexionBD;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.*;
import java.sql.Timestamp;



/**
 *
 * @author
 */
public class RegistroEditorial extends javax.swing.JInternalFrame {

    String usuario, sentenciaSQL, cod = "";
    Connection con = null;
    ConexionBD connec;
    PreparedStatement ps = null;
    ResultSet rs = null;
    DefaultTableModel modelo;
    Object datosEditorial[] = new Object[6];
    String nombreTabla = "EDITORIAL";

    public RegistroEditorial() {
        initComponents();
        jTxtUsuario.setText(classPrin.usuario);
        jTxtIdEditorial.setText("0");
        jTxtNombreEditorial.requestFocus();
    }

    public void conectarBD() {
        connec = new ConexionBD("openfirekafz");
        con = connec.getConexion();
    }

    /*
    public void llenarCmbSexo() {
        try {
            conectarBD();
            ps = (PreparedStatement) con.prepareStatement("SELECT idSexo, sexo FROM sexo");
            rs = ps.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("idSexo");
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
     */
    public void limpiar() {
        limpiarCampos();

        cod = "";
//        if (jTableClientes.getRowCount() == 0) {
//            JOptionPane.showMessageDialog(null, "No hay datos para limpiar.", "Tabla vacía", JOptionPane.INFORMATION_MESSAGE);
//        } else if{
        int fila = jTableEditorial.getRowCount();
        for (int i = fila - 1; i >= 0; i--) {
            modelo.removeRow(i);
//            }
        }
    }

    public void limpiarCampos() {
        jTxtIdEditorial.setText(null);
        jTxtNombreEditorial.setText(null);
        jTxtTelefono1.setText(null);
        jTxtCorreo.setText(null);
        jTxtDireccion.setText(null);
        jTxtNombreEditorial.requestFocus();

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


    public void crearEditorial() {
        try {
            conectarBD();
            sentenciaSQL = "INSERT INTO editorial (idEditorial, nombreEditorial, direccion, telefono, correo, estadoEditorial) "
                    + "VALUES (?, ?, ?, ?, ?, ?)";
            ps = con.prepareStatement(sentenciaSQL);

            int estado = 1; //1 será activo, y 0 desactivo (eliminado)

            ps.setInt(1, 0);
            ps.setString(2, jTxtNombreEditorial.getText());
            ps.setString(3, jTxtDireccion.getText());
            ps.setString(4, jTxtTelefono1.getText());
            ps.setString(5, jTxtCorreo.getText());
            ps.setInt(6, estado);
            ps.execute();
            
            String accion = "CREACIÓN DE REGISTRO EN LA TABLA " + nombreTabla;
            bitacora(classPrin.idUsuario,accion);
            
            JOptionPane.showMessageDialog(null, "DATOS INGRESADOS CORRECTAMENTE");
            con.close();
            limpiarCampos();
            leerEditoral();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "ERROR AL INTENTAR INGRESAR LOS DATOS:  " + ex.getMessage());
        }
    }

    public void leerEditoral() {
        cod="";
        conectarBD();
        sentenciaSQL = "SELECT idEditorial, nombreEditorial, direccion, telefono, correo FROM editorial "
                + "WHERE estadoEditorial = " + "1";
        try {
            ps = con.prepareStatement(sentenciaSQL);
            rs = ps.executeQuery();
            modelo = (DefaultTableModel) jTableEditorial.getModel();
            modelo.setRowCount(0);
            while (rs.next()) {
                datosEditorial[0] = (rs.getInt(1));
                datosEditorial[1] = (rs.getString(2));
                datosEditorial[2] = (rs.getString(4));
                datosEditorial[3] = (rs.getString(5));
                datosEditorial[4] = (rs.getString(3));

                modelo.addRow(datosEditorial);
            }
            jTableEditorial.setModel(modelo);

            con.close();
           String accion = "LECTURA A LOS REGISTROS DE LA TABLA " + nombreTabla;
            bitacora(classPrin.idUsuario,accion);
            if (jTableEditorial.getRowCount() == 0) {
                JOptionPane.showMessageDialog(null, "No hay datos para mostrar.", "Editorial", 1);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "ERROR NO SE PUDO LEER LOS DATOS DE LA TABLA.  " + ex.getMessage());
        }

    }

    public void actualizarEditorial() {

        try {
            conectarBD();
            sentenciaSQL = "UPDATE editorial SET  nombreEditorial=? , direccion=? , telefono=?,  correo=?"
                    + " WHERE idEditorial =" + jTxtIdEditorial.getText();
            ps = con.prepareStatement(sentenciaSQL);

            ps.setString(1, jTxtNombreEditorial.getText());
            ps.setString(2, jTxtDireccion.getText());
            ps.setString(3, jTxtTelefono1.getText());
            ps.setString(4, jTxtCorreo.getText());

            ps.execute();
            
            String accion = "ACTUALIZACIÓN DE REGISTRO EN LA TABLA " + nombreTabla;
            bitacora(classPrin.idUsuario,accion);
            
            JOptionPane.showMessageDialog(null, "DATOS ACTUALIZADOS CORRECTAMENTE");
            con.close();
            leerEditoral();
            limpiarCampos();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "ERROR, NO SE PUDO ACTUALIZAR LOS DATOS " + ex.getMessage());
        }
    }

    public void eliminarEditorial() {
        try {
            conectarBD();
            sentenciaSQL = "UPDATE editorial SET estadoEditorial='0' WHERE idEditorial =" + jTxtIdEditorial.getText();
            ps = con.prepareStatement(sentenciaSQL);
            ps.execute();
            limpiarCampos();
            leerEditoral();
            
           String accion = "ELIMINACIÓN DE REGISTRO EN LA TABLA " + nombreTabla;
            bitacora(classPrin.idUsuario,accion);
            
            JOptionPane.showMessageDialog(null, "DATOS ELIMINADOS CORRECTAMENTE!");
            cod = "";
            con.close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "ERROR, NO SE PUDO ELIMINAR LOS DATOS " + ex.getMessage());
        }
    }

    public void buscarEditorial(int condicion) {
        try {
            if (condicion == 1) {
                sentenciaSQL = "SELECT idEditorial, nombreEditorial, telefono, correo, direccion FROM editorial "
                        + "WHERE idEditorial like '%" + jTxtIdEditorial.getText() + "' and estadoEditorial=1";
            } else if (condicion == 0) {
                sentenciaSQL = "SELECT idEditorial, nombreEditorial, telefono, correo, direccion FROM editorial "
                        + "WHERE nombreEditorial like '%" + jTxtNombreEditorial.getText() + "%' and estadoEditorial=1";
            }

            int fila = jTableEditorial.getRowCount();
            for (int i = fila - 1; i >= 0; i--) {
                modelo.removeRow(i);
            }
            conectarBD();

            ps = con.prepareStatement(sentenciaSQL);
            rs = ps.executeQuery();
            modelo = (DefaultTableModel) jTableEditorial.getModel();
            while (rs.next()) {
                datosEditorial[0] = (rs.getInt(1));
                datosEditorial[1] = (rs.getString(2));
                datosEditorial[2] = (rs.getString(4));
                datosEditorial[3] = (rs.getString(5));
                datosEditorial[4] = (rs.getString(3));
                modelo.addRow(datosEditorial);
            }
            jTableEditorial.setModel(modelo);
            con.close();
            String accion = "BUSCÓ UN REGISTRO EN LA TABLA " + nombreTabla;
            bitacora(classPrin.idUsuario,accion);
            
            if (jTableEditorial.getRowCount() == 0) {
                JOptionPane.showMessageDialog(null, "NO SE PUDO ENCONTRAR EL EDITORIAL");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "ERROR, NO SE PUDO ENCONTRAR EL EDITORIAL. " + ex.getMessage());
        }
    }
    
    public void establecerValores() {

        int fila = jTableEditorial.getSelectedRow();
        this.cod = jTableEditorial.getValueAt(fila, 0).toString();

        jTxtIdEditorial.setText(jTableEditorial.getValueAt(fila, 0).toString());
        jTxtNombreEditorial.setText(jTableEditorial.getValueAt(fila, 1).toString());
        jTxtDireccion.setText(jTableEditorial.getValueAt(fila, 4).toString());
        jTxtTelefono1.setText(jTableEditorial.getValueAt(fila, 2).toString());
        jTxtCorreo.setText(jTableEditorial.getValueAt(fila, 3).toString());

    }
    
    //METODOS SOBRECARGADOS
    //marcar los campos obligatorios *
    public void TextoObligatorio(JTextField Field, JLabel label, String s) {
        if (Field.getText().isEmpty()) {
            label.setText(s);
        } else {
            label.setText("");
        }
    }

    public void TextoObligatorio(JTextArea Field, JLabel label, String s) {
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

    public void TextoObligatorio(JComboBox Field, JLabel label, String s) {
        if (Field.getSelectedItem() == "SELECCIONE UNA OPCIÓN: ") {
            label.setText(s);
        } else {
            label.setText("");
        }
    }
    
      
  
    
    //Limpiar los * de los campos obligatorios
    public void limpiarLabel(){
           CamposObligatorios.setText(" ");
          
            validarEditorial.setText("");
        
            validarTelefono.setText("");
            validarCorreo.setText("");
            validarDireccion.setText("");
    }

    //Validar campos vacios
    public boolean validarEditorial() {
        boolean vacios = false;

        if (  jTxtNombreEditorial.getText().trim().isEmpty() || jTxtTelefono1.getText().equals("(+504)     -    ") || jTxtCorreo.getText().trim().isEmpty()
                || jTxtDireccion.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "NO DEBE DEJAR CAMPOS VACIOS", "CAMPOS VACIOS", 0);
            CamposObligatorios.setText("* Campos Obligatorios ");

           
            TextoObligatorio(jTxtNombreEditorial, validarEditorial, "*");
     
            TextoObligatorio(jTxtTelefono1, validarTelefono, "*", "(+504)     -    ");
            TextoObligatorio(jTxtCorreo, validarCorreo, "*");
            TextoObligatorio(jTxtDireccion, validarDireccion, "*");

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
        jTableEditorial = new javax.swing.JTable();
        jPanel4 = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        jBtnBuscarIdentidad1 = new javax.swing.JButton();
        jLabel10 = new javax.swing.JLabel();
        jTxtCorreo = new javax.swing.JTextField();
        jBtnBuscarNombre1 = new javax.swing.JButton();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jTxtIdEditorial = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTxtDireccion = new javax.swing.JTextArea();
        jTxtTelefono1 = new javax.swing.JFormattedTextField();
        jTxtNombreEditorial = new javax.swing.JTextField();
        CamposObligatorios = new javax.swing.JLabel();
        validarEditorial = new javax.swing.JLabel();
        validarTelefono = new javax.swing.JLabel();
        validarCorreo = new javax.swing.JLabel();
        validarDireccion = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jBtnActualizar = new javax.swing.JButton();
        jBtnLeer = new javax.swing.JButton();
        jBtnCrear = new javax.swing.JButton();
        jBtnEliminar = new javax.swing.JButton();
        jBtnLimpiar = new javax.swing.JButton();
        jTxtUsuario = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();

        setBackground(new java.awt.Color(255, 255, 255));
        setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(238, 123, 81)));
        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle("EDITORIAL");
        setToolTipText("");
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        setFrameIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/cliente_icon2.png"))); // NOI18N
        setMaximumSize(new java.awt.Dimension(1000, 700));
        setMinimumSize(new java.awt.Dimension(1000, 700));

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setPreferredSize(new java.awt.Dimension(1046, 645));

        jPanel2.setBackground(new java.awt.Color(237, 120, 74));

        jlblImagen.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/08- Logo The Book House.png"))); // NOI18N

        jLabel2.setFont(new java.awt.Font("Century Schoolbook", 1, 44)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("REGISTRO DE EDITORIAL");

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
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel2)
                .addGap(21, 21, 21))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jlblImagen, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));
        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(71, 84, 130), 2, true), "LISTA DE REGISTROS", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 0, 12), new java.awt.Color(71, 84, 130))); // NOI18N

        jTableEditorial.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(71, 84, 130)));
        jTableEditorial.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "EDITORIAL", "TELEFONO", "CORREO", "DIRECCION"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, true, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTableEditorial.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTableEditorialMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jTableEditorial);
        if (jTableEditorial.getColumnModel().getColumnCount() > 0) {
            jTableEditorial.getColumnModel().getColumn(0).setPreferredWidth(15);
        }

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(4, 4, 4)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 487, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(2, 2, 2)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 470, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));
        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(71, 84, 130), 2, true), "FORMULARIO DATOS", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 0, 12), new java.awt.Color(71, 84, 130))); // NOI18N
        jPanel4.setLayout(null);

        jLabel9.setText("ID EDITORIAL");
        jLabel9.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        jPanel4.add(jLabel9);
        jLabel9.setBounds(70, 40, 90, 20);

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
        jPanel4.add(jBtnBuscarIdentidad1);
        jBtnBuscarIdentidad1.setBounds(320, 40, 40, 30);

        jLabel10.setText("DIRECCION");
        jPanel4.add(jLabel10);
        jLabel10.setBounds(80, 240, 80, 20);
        jPanel4.add(jTxtCorreo);
        jTxtCorreo.setBounds(160, 190, 230, 30);

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
        jPanel4.add(jBtnBuscarNombre1);
        jBtnBuscarNombre1.setBounds(430, 90, 40, 30);

        jLabel11.setText("NOMBRE EDITORIAL");
        jLabel11.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        jPanel4.add(jLabel11);
        jLabel11.setBounds(30, 90, 130, 20);

        jLabel12.setText("TELÉFONO");
        jLabel12.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        jPanel4.add(jLabel12);
        jLabel12.setBounds(80, 140, 80, 20);
        jPanel4.add(jTxtIdEditorial);
        jTxtIdEditorial.setBounds(160, 40, 140, 30);

        jLabel14.setText("CORREO");
        jPanel4.add(jLabel14);
        jLabel14.setBounds(90, 190, 70, 20);

        jTxtDireccion.setColumns(20);
        jTxtDireccion.setRows(5);
        jScrollPane3.setViewportView(jTxtDireccion);

        jPanel4.add(jScrollPane3);
        jScrollPane3.setBounds(160, 240, 270, 90);

        try {
            jTxtTelefono1.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("(+504) ####-####")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }
        jPanel4.add(jTxtTelefono1);
        jTxtTelefono1.setBounds(160, 140, 230, 30);
        jPanel4.add(jTxtNombreEditorial);
        jTxtNombreEditorial.setBounds(160, 90, 250, 30);

        CamposObligatorios.setFont(new java.awt.Font("Dialog", 2, 12)); // NOI18N
        CamposObligatorios.setForeground(new java.awt.Color(255, 51, 51));
        jPanel4.add(CamposObligatorios);
        CamposObligatorios.setBounds(10, 340, 140, 20);

        validarEditorial.setFont(new java.awt.Font("Dialog", 2, 12)); // NOI18N
        validarEditorial.setForeground(new java.awt.Color(255, 51, 51));
        jPanel4.add(validarEditorial);
        validarEditorial.setBounds(410, 90, 20, 20);

        validarTelefono.setFont(new java.awt.Font("Dialog", 2, 12)); // NOI18N
        validarTelefono.setForeground(new java.awt.Color(255, 51, 51));
        jPanel4.add(validarTelefono);
        validarTelefono.setBounds(390, 140, 20, 20);

        validarCorreo.setFont(new java.awt.Font("Dialog", 2, 12)); // NOI18N
        validarCorreo.setForeground(new java.awt.Color(255, 51, 51));
        jPanel4.add(validarCorreo);
        validarCorreo.setBounds(390, 190, 20, 20);

        validarDireccion.setFont(new java.awt.Font("Dialog", 2, 12)); // NOI18N
        validarDireccion.setForeground(new java.awt.Color(255, 51, 51));
        jPanel4.add(validarDireccion);
        validarDireccion.setBounds(430, 240, 20, 20);

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
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(jBtnCrear, javax.swing.GroupLayout.PREFERRED_SIZE, 208, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(12, 12, 12)
                        .addComponent(jBtnLeer, javax.swing.GroupLayout.PREFERRED_SIZE, 208, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(jBtnEliminar, javax.swing.GroupLayout.PREFERRED_SIZE, 208, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(12, 12, 12)
                        .addComponent(jBtnActualizar, javax.swing.GroupLayout.PREFERRED_SIZE, 208, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(128, 128, 128)
                        .addComponent(jBtnLimpiar, javax.swing.GroupLayout.PREFERRED_SIZE, 208, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(20, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(8, 8, 8)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jBtnCrear)
                    .addComponent(jBtnLeer))
                .addGap(9, 9, 9)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jBtnEliminar)
                    .addComponent(jBtnActualizar))
                .addGap(9, 9, 9)
                .addComponent(jBtnLimpiar)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

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
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(10, 10, 10)
                        .addComponent(jTxtUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, 370, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, 360, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jTxtUsuario, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(16, 16, 16))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 1001, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 629, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jBtnActualizarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtnActualizarActionPerformed
      
        boolean vacios = validarEditorial();

        if (vacios == true) {

        } else {
        
        if (!cod.equals("")) {
            int respuesta = JOptionPane.showConfirmDialog(null, "¿Está seguro de actualizar el editorial?", "Advertencia", JOptionPane.YES_NO_OPTION, 1);
            if (respuesta == 0) {
                actualizarEditorial();
            }
        } else {
            JOptionPane.showMessageDialog(null, "Debe seleccionar un editorial para poder ejecutar esta acción.", "Actualizar", 1);
        }}
    }//GEN-LAST:event_jBtnActualizarActionPerformed

    private void jBtnLeerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtnLeerActionPerformed
        leerEditoral();
        limpiarLabel();
    }//GEN-LAST:event_jBtnLeerActionPerformed

    private void jBtnCrearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtnCrearActionPerformed
     
        
        boolean vacios = validarEditorial();

        if (vacios == true) {

        } else {
        crearEditorial();
        }
    }//GEN-LAST:event_jBtnCrearActionPerformed

    private void jBtnLimpiarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtnLimpiarActionPerformed
        limpiar();
        limpiarLabel();
    }//GEN-LAST:event_jBtnLimpiarActionPerformed

    private void jTableEditorialMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableEditorialMouseClicked
        establecerValores();
    }//GEN-LAST:event_jTableEditorialMouseClicked

    private void jBtnEliminarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtnEliminarActionPerformed
        if (!cod.equals("")) {
            int respuesta = JOptionPane.showConfirmDialog(null, "¿Está seguro de eliminar el editorial?", "Advertencia", JOptionPane.YES_NO_OPTION, 1);
            if (respuesta == 0) {
                eliminarEditorial();
                limpiarLabel();
            }
        } else {
            JOptionPane.showMessageDialog(null, "Debe seleccionar un editorial para poder ejecutar esta acción.", "Elimar", 1);
        }
    }//GEN-LAST:event_jBtnEliminarActionPerformed

    private void jBtnBuscarIdentidad1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtnBuscarIdentidad1ActionPerformed
        if (!jTxtIdEditorial.getText().equals("")) {
            buscarEditorial(1);
        } else {
            JOptionPane.showMessageDialog(null, "El campo está vacío.", "Buscar", 1);
        }
    }//GEN-LAST:event_jBtnBuscarIdentidad1ActionPerformed

    private void jBtnBuscarNombre1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtnBuscarNombre1ActionPerformed
        if (!jTxtNombreEditorial.getText().equals("")) {
            buscarEditorial(0);
        } else {
            JOptionPane.showMessageDialog(null, "El campo está vacío.", "Buscar", 1);
        }
    }//GEN-LAST:event_jBtnBuscarNombre1ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel CamposObligatorios;
    private javax.swing.JButton jBtnActualizar;
    private javax.swing.JButton jBtnBuscarIdentidad1;
    private javax.swing.JButton jBtnBuscarNombre1;
    private javax.swing.JButton jBtnCrear;
    private javax.swing.JButton jBtnEliminar;
    private javax.swing.JButton jBtnLeer;
    private javax.swing.JButton jBtnLimpiar;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTable jTableEditorial;
    private javax.swing.JTextField jTxtCorreo;
    private javax.swing.JTextArea jTxtDireccion;
    private javax.swing.JTextField jTxtIdEditorial;
    private javax.swing.JTextField jTxtNombreEditorial;
    private javax.swing.JFormattedTextField jTxtTelefono1;
    private javax.swing.JLabel jTxtUsuario;
    private javax.swing.JLabel jlblImagen;
    private javax.swing.JLabel validarCorreo;
    private javax.swing.JLabel validarDireccion;
    private javax.swing.JLabel validarEditorial;
    private javax.swing.JLabel validarTelefono;
    // End of variables declaration//GEN-END:variables
}
