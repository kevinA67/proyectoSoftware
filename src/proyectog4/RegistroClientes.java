/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package proyectog4;

import Conexion.ConexionBD;
import java.awt.Dimension;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.*;
import static proyectog4.Menu_Principal.jDesktopPane1;

import java.sql.Timestamp;
import java.util.Date;

/**
 *
 * @author
 */
public class RegistroClientes extends javax.swing.JInternalFrame {

    String usuario, cod, sentenciaSQL;
    Connection con = null;
    ConexionBD connec;
    PreparedStatement ps = null;
    ResultSet rs = null;
    DefaultTableModel modelo;
    Object datosClientes[] = new Object[7];
    String nombreTabla = "CLIENTES";

    public RegistroClientes() {
        initComponents();
        usuario = classPrin.usuario;
        jTxtUsuario.setText(usuario);
        jCmbSexo.addItem("SELECCIONE UNA OPCIÓN: ");
        llenarCmbSexo();
    }

    public void conectarBD() {
        connec = new ConexionBD("openfirekafz");
        con = connec.getConexion();
    }

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
        jTxtIdentidad.setValue(null);
        jTxtNombreCliente.setText(null);
        jCmbSexo.setSelectedIndex(0);
        jTxtTelefono.setText(null);
        jTxtCorreo.setText(null);
        jTxtDireccion.setText(null);
        jTxtIdentidad.requestFocus();
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

    public void crearCliente() {
        try {
            conectarBD();
            sentenciaSQL = "INSERT INTO cliente (idCliente, numIdentidad, nombreCliente, idSexo, telefono, correo, direccion, estadoCliente) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            ps = con.prepareStatement(sentenciaSQL);
            int indiceSexo = jCmbSexo.getSelectedIndex(); //Se obtiene el índice del elemento de combobox que se seleccionó y se guarda en una variable
            int estado = 1; //1 será activo, y 0 desactivo (eliminado)

            ps.setInt(1, 0);
            ps.setString(2, jTxtIdentidad.getText());
            ps.setString(3, jTxtNombreCliente.getText());
            ps.setInt(4, indiceSexo);
            ps.setString(5, jTxtTelefono.getText());
            ps.setString(6, jTxtCorreo.getText());
            ps.setString(7, jTxtDireccion.getText());
            ps.setInt(8, estado);
            ps.execute();

            String accion = "CREACIÓN DE REGISTRO EN LA TABLA " + nombreTabla;
            bitacora(classPrin.idUsuario, accion);
            
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

            String accion = "LECTURA A LOS REGISTROS DE LA TABLA " + nombreTabla;
            bitacora(classPrin.idUsuario, accion);
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
            conectarBD();
            sentenciaSQL = "UPDATE cliente SET numIdentidad =?, nombreCliente=?, idSexo=?, telefono=?,  correo=?, direccion=?"
                    + " WHERE idCliente =" + this.cod;
            ps = con.prepareStatement(sentenciaSQL);
            int indiceSexo = jCmbSexo.getSelectedIndex();

            ps.setString(1, jTxtIdentidad.getText());
            ps.setString(2, jTxtNombreCliente.getText());
            ps.setInt(3, indiceSexo);
            ps.setString(4, jTxtTelefono.getText());
            ps.setString(5, jTxtCorreo.getText());
            ps.setString(6, jTxtDireccion.getText());
            ps.execute();

            String accion = "ACTUALIZACIÓN DE REGISTRO EN LA TABLA " + nombreTabla;
            bitacora(classPrin.idUsuario, accion);
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
            conectarBD();
            sentenciaSQL = "UPDATE cliente SET estadoCliente='0' WHERE idCliente =" + this.cod;
            ps = con.prepareStatement(sentenciaSQL);
            ps.execute();
            limpiarCampos();

            String accion = "ELIMINACIÓN DE REGISTRO EN LA TABLA " + nombreTabla;
            bitacora(classPrin.idUsuario, accion);

            JOptionPane.showMessageDialog(null, "DATOS ELIMINADOS CORRECTAMENTE!");
            leerClientes();
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
                        + "WHERE nombreCliente like '%" + jTxtNombreCliente.getText() + "%' and estadoCliente=1";
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

            String accion = "BUSCÓ UN REGISTRO EN LA TABLA " + nombreTabla;
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
        this.cod = jTableClientes.getValueAt(fila, 0).toString();
        jTxtIdentidad.setText(jTableClientes.getValueAt(fila, 1).toString());
        jTxtNombreCliente.setText(jTableClientes.getValueAt(fila, 2).toString());
        String sexo = jTableClientes.getValueAt(fila, 3).toString();
        jTxtTelefono.setText(jTableClientes.getValueAt(fila, 4).toString());
        jTxtCorreo.setText(jTableClientes.getValueAt(fila, 5).toString());
        jTxtDireccion.setText(jTableClientes.getValueAt(fila, 6).toString());
        jCmbSexo.setSelectedItem(sexo);

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
            validarIdentidad1.setText("");
            validarNombre.setText("");
            validarSexo1.setText("");
            validarTelefono1.setText("");
            validarCorreo2.setText("");
            validarDireccion1.setText("");
    }

    //Validar campos vacios
    public boolean validarCliente() {
        boolean vacios = false;

        if (jCmbSexo.getSelectedItem() == null || jTxtIdentidad.getText().trim().isEmpty() || jTxtNombreCliente.getText().trim().isEmpty() || jTxtTelefono.getText().equals("(+504)     -    ") || jTxtCorreo.getText().trim().isEmpty()
                || jTxtDireccion.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "NO DEBE DEJAR CAMPOS VACIOS", "CAMPOS VACIOS", 0);
            CamposObligatorios.setText("* Campos Obligatorios ");

            TextoObligatorio(jTxtIdentidad, validarIdentidad1, "*", "    -    -     ");
            TextoObligatorio(jTxtNombreCliente, validarNombre, "*");
            TextoObligatorio(jCmbSexo, validarSexo1, "*");
            TextoObligatorio(jTxtTelefono, validarTelefono1, "*", "(+504)     -    ");
            TextoObligatorio(jTxtCorreo, validarCorreo2, "*");
            TextoObligatorio(jTxtDireccion, validarDireccion1, "*");

            vacios = true;

        } else {
            vacios = false;
         limpiarLabel();
        }
        return vacios;
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
        jlblImagen = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTableClientes = new javax.swing.JTable();
        jPanel4 = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        jBtnBuscarIdentidad1 = new javax.swing.JButton();
        jLabel10 = new javax.swing.JLabel();
        jTxtCorreo = new javax.swing.JTextField();
        jBtnBuscarNombre1 = new javax.swing.JButton();
        jLabel11 = new javax.swing.JLabel();
        jTxtIdentidad = new javax.swing.JFormattedTextField();
        jLabel12 = new javax.swing.JLabel();
        jCmbSexo = new javax.swing.JComboBox<>();
        jLabel13 = new javax.swing.JLabel();
        jTxtNombreCliente = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTxtDireccion = new javax.swing.JTextArea();
        jBtnCarnet = new javax.swing.JButton();
        jTxtTelefono = new javax.swing.JFormattedTextField();
        validarNombre = new javax.swing.JLabel();
        validarSexo1 = new javax.swing.JLabel();
        validarTelefono1 = new javax.swing.JLabel();
        CamposObligatorios = new javax.swing.JLabel();
        validarDireccion1 = new javax.swing.JLabel();
        validarIdentidad1 = new javax.swing.JLabel();
        validarCorreo2 = new javax.swing.JLabel();
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
        setTitle("CLIENTES");
        setToolTipText("");
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        setFrameIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/cliente_icon2.png"))); // NOI18N
        setMaximumSize(new java.awt.Dimension(1000, 700));
        setMinimumSize(new java.awt.Dimension(1000, 700));

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jPanel2.setBackground(new java.awt.Color(237, 120, 74));

        jlblImagen.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/08- Logo The Book House.png"))); // NOI18N

        jLabel2.setFont(new java.awt.Font("Century Schoolbook", 1, 44)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("REGISTRO DE CLIENTES");

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
        if (jTableClientes.getColumnModel().getColumnCount() > 0) {
            jTableClientes.getColumnModel().getColumn(0).setPreferredWidth(15);
        }

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(9, 9, 9)
                .addComponent(jScrollPane1)
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

        jLabel9.setText("NÚMERO DE IDENTIDAD");
        jLabel9.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);

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

        jLabel10.setText("DIRECCION");

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

        jLabel11.setText("NOMBRE COMPLETO");
        jLabel11.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);

        try {
            jTxtIdentidad.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("####-####-#####")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }

        jLabel12.setText("TELÉFONO");
        jLabel12.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);

        jLabel13.setText("SEXO");

        jLabel14.setText("CORREO");

        jTxtDireccion.setColumns(20);
        jTxtDireccion.setRows(5);
        jScrollPane3.setViewportView(jTxtDireccion);

        jBtnCarnet.setBackground(new java.awt.Color(71, 84, 130));
        jBtnCarnet.setForeground(new java.awt.Color(255, 255, 255));
        jBtnCarnet.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/smart_card2_24px.png"))); // NOI18N
        jBtnCarnet.setText("Carnet de Cliente");
        jBtnCarnet.setToolTipText("Haga clic para visualizar la información de carnet para clientes");
        jBtnCarnet.setBorder(null);
        jBtnCarnet.setFocusPainted(false);
        jBtnCarnet.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBtnCarnetActionPerformed(evt);
            }
        });

        try {
            jTxtTelefono.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("(+504) ####-####")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }

        validarNombre.setFont(new java.awt.Font("Dialog", 2, 12)); // NOI18N
        validarNombre.setForeground(new java.awt.Color(255, 51, 51));

        validarSexo1.setFont(new java.awt.Font("Dialog", 2, 12)); // NOI18N
        validarSexo1.setForeground(new java.awt.Color(255, 51, 51));

        validarTelefono1.setFont(new java.awt.Font("Dialog", 2, 12)); // NOI18N
        validarTelefono1.setForeground(new java.awt.Color(255, 51, 51));

        CamposObligatorios.setFont(new java.awt.Font("Dialog", 2, 12)); // NOI18N
        CamposObligatorios.setForeground(new java.awt.Color(255, 51, 51));

        validarDireccion1.setFont(new java.awt.Font("Dialog", 2, 12)); // NOI18N
        validarDireccion1.setForeground(new java.awt.Color(255, 51, 51));

        validarIdentidad1.setFont(new java.awt.Font("Dialog", 2, 12)); // NOI18N
        validarIdentidad1.setForeground(new java.awt.Color(255, 51, 51));

        validarCorreo2.setFont(new java.awt.Font("Dialog", 2, 12)); // NOI18N
        validarCorreo2.setForeground(new java.awt.Color(255, 51, 51));

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(4, 4, 4)
                        .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(jTxtIdentidad, javax.swing.GroupLayout.PREFERRED_SIZE, 230, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(validarIdentidad1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(10, 10, 10)
                        .addComponent(jBtnBuscarIdentidad1, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(14, 14, 14)
                        .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(jTxtNombreCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 230, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(validarNombre, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(10, 10, 10)
                        .addComponent(jBtnBuscarNombre1, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(94, 94, 94)
                        .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(jCmbSexo, javax.swing.GroupLayout.PREFERRED_SIZE, 230, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(validarSexo1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(74, 74, 74)
                        .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(jTxtTelefono, javax.swing.GroupLayout.PREFERRED_SIZE, 230, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(validarTelefono1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(84, 84, 84)
                        .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(jTxtCorreo, javax.swing.GroupLayout.PREFERRED_SIZE, 230, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(validarCorreo2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(74, 74, 74)
                        .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 290, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(validarDireccion1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(4, 4, 4)
                        .addComponent(CamposObligatorios, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(10, 10, 10)
                        .addComponent(jBtnCarnet, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(29, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTxtIdentidad, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(validarIdentidad1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jBtnBuscarIdentidad1, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTxtNombreCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(validarNombre, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jBtnBuscarNombre1, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jCmbSexo, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(validarSexo1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTxtTelefono, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(validarTelefono1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTxtCorreo, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(validarCorreo2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(validarDireccion1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addComponent(CamposObligatorios, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jBtnCarnet, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)))
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
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jBtnCrear, javax.swing.GroupLayout.PREFERRED_SIZE, 208, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(12, 12, 12)
                        .addComponent(jBtnLeer, javax.swing.GroupLayout.PREFERRED_SIZE, 208, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jBtnEliminar, javax.swing.GroupLayout.PREFERRED_SIZE, 208, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(12, 12, 12)
                        .addComponent(jBtnActualizar, javax.swing.GroupLayout.PREFERRED_SIZE, 208, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(108, 108, 108)
                        .addComponent(jBtnLimpiar, javax.swing.GroupLayout.PREFERRED_SIZE, 208, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jBtnLimpiar)
                .addContainerGap())
        );

        jTxtUsuario.setBackground(new java.awt.Color(255, 255, 255));
        jTxtUsuario.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jTxtUsuario.setForeground(new java.awt.Color(237, 120, 74));

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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTxtUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, 441, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel6)
                    .addComponent(jTxtUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(22, Short.MAX_VALUE))
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

    private void jBtnActualizarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtnActualizarActionPerformed

       boolean vacios = validarCliente();
          

       if(vacios==true){
           
       }
       else
     if (!jTxtIdentidad.getText().isEmpty()) {
                int respuesta = JOptionPane.showConfirmDialog(null, "¿Está seguro de actualizar el autor?", "Advertencia", JOptionPane.YES_NO_OPTION, 1);
                if ( respuesta == 0 ) {

                    actualizarCliente();
                }
             else {
                JOptionPane.showMessageDialog(null, "Debe seleccionar un autor para poder ejecutar esta acción.", "Actualizar", 1);
            }}
    }//GEN-LAST:event_jBtnActualizarActionPerformed

    private void jBtnLeerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtnLeerActionPerformed
        leerClientes();
    }//GEN-LAST:event_jBtnLeerActionPerformed

    private void jBtnCrearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtnCrearActionPerformed
         boolean vacios = validarCliente();
        boolean  validarid= validarIdentidad();

        if (vacios == true) {

        } else if(validarid==true) {
            
        }else{
            crearCliente();
        }
    }//GEN-LAST:event_jBtnCrearActionPerformed

    private void jBtnLimpiarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtnLimpiarActionPerformed
        limpiar();
     limpiarLabel();
    }//GEN-LAST:event_jBtnLimpiarActionPerformed

    private void jTableClientesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableClientesMouseClicked
        establecerValores();
    }//GEN-LAST:event_jTableClientesMouseClicked

    private void jBtnEliminarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtnEliminarActionPerformed

        boolean vacios = validarCliente();

        if (vacios == true) {

        } else {
            if (!cod.equals("")) {
                int respuesta = JOptionPane.showConfirmDialog(null, "¿Está seguro de eliminar el cliente?", "Advertencia", JOptionPane.YES_NO_OPTION, 1);
                if (respuesta == 0) {
                    eliminarCliente();
              limpiarLabel();
                }
            } else {
                JOptionPane.showMessageDialog(null, "Debe seleccionar un cliente para poder ejecutar esta acción.", "Elimar", 1);
            }
        }
    }//GEN-LAST:event_jBtnEliminarActionPerformed

    private void jBtnBuscarIdentidad1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtnBuscarIdentidad1ActionPerformed
        if (!jTxtIdentidad.getText().equals("    -    -     ")) {
            buscarCliente(1);
        } else {
            JOptionPane.showMessageDialog(null, "El campo está vacío.", "Buscar", 1);
        }
    }//GEN-LAST:event_jBtnBuscarIdentidad1ActionPerformed

    private void jBtnBuscarNombre1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtnBuscarNombre1ActionPerformed
        if (!jTxtNombreCliente.getText().isEmpty()) {
            buscarCliente(0);
        } else {
            JOptionPane.showMessageDialog(null, "El campo está vacío.", "Buscar", 1);
        }
    }//GEN-LAST:event_jBtnBuscarNombre1ActionPerformed

    private void jBtnCarnetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtnCarnetActionPerformed
        RegistroCarnet carnet = new RegistroCarnet();
        Menu_Principal.jDesktopPane1.add(carnet);
        Dimension desktopSize = Menu_Principal.jDesktopPane1.getSize();
        Dimension FrameSize = carnet.getSize();
        carnet.setLocation((desktopSize.width - FrameSize.width) / 2, (desktopSize.height - FrameSize.height) / 2);
        carnet.show();

    }//GEN-LAST:event_jBtnCarnetActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel CamposObligatorios;
    private javax.swing.JButton jBtnActualizar;
    private javax.swing.JButton jBtnBuscarIdentidad1;
    private javax.swing.JButton jBtnBuscarNombre1;
    private javax.swing.JButton jBtnCarnet;
    private javax.swing.JButton jBtnCrear;
    private javax.swing.JButton jBtnEliminar;
    private javax.swing.JButton jBtnLeer;
    private javax.swing.JButton jBtnLimpiar;
    private javax.swing.JComboBox<String> jCmbSexo;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
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
    private javax.swing.JTable jTableClientes;
    private javax.swing.JTextField jTxtCorreo;
    private javax.swing.JTextArea jTxtDireccion;
    private javax.swing.JFormattedTextField jTxtIdentidad;
    private javax.swing.JTextField jTxtNombreCliente;
    private javax.swing.JFormattedTextField jTxtTelefono;
    private javax.swing.JLabel jTxtUsuario;
    private javax.swing.JLabel jlblImagen;
    private javax.swing.JLabel validarCorreo2;
    private javax.swing.JLabel validarDireccion1;
    private javax.swing.JLabel validarIdentidad1;
    private javax.swing.JLabel validarNombre;
    private javax.swing.JLabel validarSexo1;
    private javax.swing.JLabel validarTelefono1;
    // End of variables declaration//GEN-END:variables
}
