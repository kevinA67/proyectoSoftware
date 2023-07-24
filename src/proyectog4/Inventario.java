/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package proyectog4;

import Conexion.ConexionBD;
import java.awt.Image;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.*;

/**
 *
 * @author
 */
public class Inventario extends javax.swing.JInternalFrame {

    String usuario, sentenciaSQL, cod = "";
    Connection con = null;
    ConexionBD connec;
    PreparedStatement ps = null;
    ResultSet rs = null;
    DefaultTableModel modelo;
    Object datosInventario[] = new Object[6];
    String nombreTabla = "INVENTARIO";


    public Inventario() {
        initComponents();
        jTxtUsuario.setText(classPrin.usuario);
        jCmbLibro.addItem("SELECCIONE UNA OPCIÓN: ");
        llenarCmbIdLibro();
    }

    public void conectarBD() {
        connec = new ConexionBD("openfirekafz");
        con = connec.getConexion();
    }

    public void llenarCmbIdLibro() {
        try {
            conectarBD();
            ps = (PreparedStatement) con.prepareStatement("SELECT idLibro, concat_ws(' ', libro.cod_libro, libro.tituloLibro) as nombre FROM libro WHERE libro.estadoLibro=1");
            rs = ps.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("idLibro");
                id++;
                String nombre = rs.getString("nombre");
                jCmbLibro.addItem(nombre);
                jCmbLibro.setSelectedItem(id);
            }

            con.close();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage());
        }
    }

    public void limpiar() {
        limpiarCampos();

        cod = "";
//        if (jTableClientes.getRowCount() == 0) {
//            JOptionPane.showMessageDialog(null, "No hay datos para limpiar.", "Tabla vacía", JOptionPane.INFORMATION_MESSAGE);
//        } else if{
        int fila = jTableInventario.getRowCount();
        for (int i = fila - 1; i >= 0; i--) {
            modelo.removeRow(i);
//            }
        }
    }

    public void limpiarCampos() {
        jCmbLibro.setSelectedIndex(0);
        jTxtidInventario.setText(null);
        jTxtStockMinimo1.setText(null);
        jTextStockMaximo.setText(null);
        jTextStockDisponible.setText(null);
        jCmbLibro.requestFocus();

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

    public void crearInventario() {
        try {
            conectarBD();
            sentenciaSQL = "INSERT INTO inventario (idInventario, idLibro, stock_min, stock_max, stock_disp, estadoInventario) "
                    + "VALUES (?, ?, ?, ?, ?, ?)";
            ps = con.prepareStatement(sentenciaSQL);
            int indiceIdLibro = jCmbLibro.getSelectedIndex(); //Se obtiene el índice del elemento de combobox que se seleccionó y se guarda en una variable
            int estado=1, min, max, disp; //1 será activo, y 0 desactivo (eliminado)

            ps.setInt(1, 0); //idInventario
            ps.setInt(2, indiceIdLibro); //idLibro
            ps.setString(3, jTxtStockMinimo1.getText()); //StockMin
            ps.setString(4, jTextStockMaximo.getText());//StockMax
            ps.setString(5, jTextStockDisponible.getText());//StockDisp
            ps.setInt(6, estado);//estado
            ps.execute();

            String accion = "CREACIÓN DE REGISTRO EN LA TABLA " + nombreTabla;
            bitacora(classPrin.idUsuario, accion);

            JOptionPane.showMessageDialog(null, "DATOS INGRESADOS CORRECTAMENTE");
            con.close();
            limpiarCampos();
            leerInventario();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "ERROR AL INTENTAR INGRESAR LOS DATOS:  " + ex.getMessage());
        }
    }

    public void leerInventario() {
        conectarBD();
        sentenciaSQL = "SELECT idInventario, CONCAT( libro.cod_libro, ' ', libro.tituloLibro) AS nombreLibros, stock_min,stock_max,stock_disp "
                + "FROM inventario INNER JOIN libro ON inventario.idLibro = libro.idLibro WHERE estadoInventario = '1' AND libro.estadoLibro= 1";

        try {
            ps = con.prepareStatement(sentenciaSQL);
            rs = ps.executeQuery();
            modelo = (DefaultTableModel) jTableInventario.getModel();
            modelo.setRowCount(0);
            while (rs.next()) {
                datosInventario[0] = (rs.getInt(1));
                datosInventario[1] = (rs.getString(2));
                datosInventario[2] = (rs.getString(3));
                datosInventario[3] = (rs.getString(4));
                datosInventario[4] = (rs.getString(5));

                modelo.addRow(datosInventario);
            }
            jTableInventario.setModel(modelo);
            
           String accion = "LECTURA DE REGISTROS DE LA TABLA " + nombreTabla;
            bitacora(classPrin.idUsuario,accion);
            con.close();

            if (jTableInventario.getRowCount() == 0) {
                JOptionPane.showMessageDialog(null, "No hay datos para mostrar.", "Inventario", 1);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "ERROR NO SE PUDO LEER LOS DATOS DE LA TABLA.  " + ex.getMessage());
        }

    }

    public void actualizarInventario() {

        try {
            conectarBD();
            sentenciaSQL = "UPDATE inventario SET idLibro=?, stock_min=?, stock_max=?, stock_disp=?"
                    + " WHERE idInventario =" + jTxtidInventario.getText();
            ps = con.prepareStatement(sentenciaSQL);
            int indiceIdLibro = jCmbLibro.getSelectedIndex();

            ps.setInt(1, indiceIdLibro);
            ps.setString(2, jTxtStockMinimo1.getText());
            ps.setString(3, jTextStockMaximo.getText());
            ps.setString(4, jTextStockDisponible.getText());
            ps.execute();
            
            String accion = "ACTUALIZACIÓN DE REGISTRO EN LA TABLA " + nombreTabla;
            bitacora(classPrin.idUsuario,accion);
            
            JOptionPane.showMessageDialog(null, "DATOS ACTUALIZADOS CORRECTAMENTE");
            con.close();
            leerInventario();
            limpiarCampos();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "ERROR, NO SE PUDO ACTUALIZAR LOS DATOS " + ex.getMessage());
        }
    }

    public void eliminarInventario() {
        try {
            conectarBD();
            sentenciaSQL = "UPDATE inventario SET estadoInventario='0' WHERE idInventario =" + jTxtidInventario.getText();
            ps = con.prepareStatement(sentenciaSQL);
            ps.execute();
            limpiarCampos();
            leerInventario();
            
           String accion = "ELIMINACIÓN DE REGISTRO EN LA TABLA " + nombreTabla;
            bitacora(classPrin.idUsuario,accion);
            JOptionPane.showMessageDialog(null, "DATOS ELIMINADOS CORRECTAMENTE!");
            cod = "";
            con.close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "ERROR, NO SE PUDO ELIMINAR LOS DATOS " + ex.getMessage());
        }
    }

    public void buscar() {

        conectarBD();

        sentenciaSQL = "SELECT * FROM  inventario WHERE idInventario LIKE '" + jTxtidInventario.getText() + "'";
        try {
            ps = con.prepareStatement(sentenciaSQL);
            rs = ps.executeQuery();
            modelo = (DefaultTableModel) jTableInventario.getModel();

            while (rs.next()) {
                datosInventario[0] = (rs.getInt(1));
                datosInventario[1] = (rs.getString(2));
                datosInventario[2] = (rs.getString(3));
                datosInventario[3] = (rs.getString(4));
                datosInventario[4] = (rs.getString(5));
                modelo.addRow(datosInventario);
            }

            String accion = "BÚSQUEDA DE REGISTRO EN LA TABLA " + nombreTabla;
            bitacora(classPrin.idUsuario, accion);
            jTableInventario.setModel(modelo);
            con.close();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "ERROR, NO SE PUDO LEER LOS DATOS " + ex.getMessage());
        }
    }

    public void establecerValores() {

        int fila = jTableInventario.getSelectedRow();
        jTxtidInventario.setText(jTableInventario.getValueAt(fila, 0).toString());
        String idLibro = jTableInventario.getValueAt(fila, 1).toString();
        jTxtStockMinimo1.setText(jTableInventario.getValueAt(fila, 2).toString());
        jTextStockMaximo.setText(jTableInventario.getValueAt(fila, 3).toString());
        jTextStockDisponible.setText(jTableInventario.getValueAt(fila, 4).toString());
        jCmbLibro.setSelectedItem(idLibro);

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

    public void TextoObligatorio(JComboBox Field, JLabel label, String s) {
        if (Field.getSelectedItem() == "SELECCIONE UNA OPCIÓN: ") {
            label.setText(s);
        } else {
            label.setText("");
        }
    }
    
    //Validacion de campos vacios
     public boolean validarInventario() {
        boolean vacios = false;

        if (jCmbLibro.getSelectedIndex() == 0 || jTxtStockMinimo1.getText().isEmpty() || jTextStockMaximo.getText().isEmpty() || jTextStockDisponible.getText().isEmpty()) {
            JOptionPane.showMessageDialog(null, "NO DEBE DEJAR CAMPOS VACIOS", "CAMPOS VACIOS", 0);
            TextoObligatorio(jCmbLibro, validarLibro2, "*");
            TextoObligatorio(jTxtStockMinimo1, validarMin1, "*");
            TextoObligatorio(jTextStockMaximo, validarMax, "*");
            TextoObligatorio(jTextStockDisponible, validarDisponible, "*");

            CamposObligatorios.setText("* Campos Obligatorios");
            vacios = true;

        } else {
            vacios = false;
            CamposObligatorios.setText("");
            validarLibro2.setText("");
            validarMin1.setText("");
            validarMax.setText("");
            validarDisponible.setText("");
            
            int min, max, disp;
            min = Integer.parseInt(jTxtStockMinimo1.getText());
            max = Integer.parseInt(jTextStockMaximo.getText());
            disp = Integer.parseInt(jTextStockDisponible.getText());

            if (min >= max) {
                JOptionPane.showMessageDialog(null, "DEBE INGRESAR UN VALOR SUPERIOR AL STOCK MINIMO", "ERROR DE STOCK", 0);
                vacios = true;
            }
            if (disp > max) {
                JOptionPane.showMessageDialog(null, "DEBE INGRESAR UN VALOR NO MAYOR AL MAXIMO", "VALOR ALTO", 0);
                vacios=true;
            }
            if (disp < min) {
                JOptionPane.showMessageDialog(null, "DEBE INGRESAR UN VALOR SIN PASAR EL LIMITE DEL STOCK MINIMO", " VALOR BAJO", 0);
                    vacios=true;
        }

        
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
        jTableInventario = new javax.swing.JTable();
        jPanel4 = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        jBtnBuscarIdentidad1 = new javax.swing.JButton();
        validarDisponible = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jTxtidInventario = new javax.swing.JTextField();
        jTextStockMaximo = new javax.swing.JTextField();
        jTextStockDisponible = new javax.swing.JTextField();
        jCmbLibro = new javax.swing.JComboBox<>();
        jTxtStockMinimo1 = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        validarMax = new javax.swing.JLabel();
        validarLibro2 = new javax.swing.JLabel();
        validarMin1 = new javax.swing.JLabel();
        CamposObligatorios = new javax.swing.JLabel();
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
        setTitle("INVENTARIO");
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
        jLabel2.setText("REGISTRO DE INVENTARIO");

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
        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(71, 84, 130), 2, true), "LISTA DE REGISTROS", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 1, 12), new java.awt.Color(71, 84, 130))); // NOI18N

        jTableInventario.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(71, 84, 130)));
        jTableInventario.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID INVENTARIO", "ID LOBRO", "STOCK MINIMO", "STOCK MAXIMO", "STOCK DISPONIBLE"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, true
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTableInventario.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTableInventarioMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jTableInventario);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 390, Short.MAX_VALUE)
        );

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));
        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(71, 84, 130), 2, true), "FORMULARIO DATOS", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 1, 12), new java.awt.Color(71, 84, 130))); // NOI18N
        jPanel4.setLayout(null);

        jLabel9.setText("ID INVENTARIO");
        jLabel9.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        jPanel4.add(jLabel9);
        jLabel9.setBounds(30, 30, 100, 30);

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
        jBtnBuscarIdentidad1.setBounds(420, 30, 40, 30);

        validarDisponible.setFont(new java.awt.Font("Dialog", 2, 12)); // NOI18N
        validarDisponible.setForeground(new java.awt.Color(255, 51, 51));
        jPanel4.add(validarDisponible);
        validarDisponible.setBounds(240, 200, 20, 20);

        jLabel11.setText("STOCK MINIMO");
        jLabel11.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        jPanel4.add(jLabel11);
        jLabel11.setBounds(30, 140, 100, 30);
        jPanel4.add(jTxtidInventario);
        jTxtidInventario.setBounds(140, 30, 260, 30);
        jPanel4.add(jTextStockMaximo);
        jTextStockMaximo.setBounds(350, 140, 100, 30);

        jTextStockDisponible.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextStockDisponibleActionPerformed(evt);
            }
        });
        jPanel4.add(jTextStockDisponible);
        jTextStockDisponible.setBounds(140, 200, 100, 30);

        jCmbLibro.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCmbLibroActionPerformed(evt);
            }
        });
        jPanel4.add(jCmbLibro);
        jCmbLibro.setBounds(140, 80, 310, 30);
        jPanel4.add(jTxtStockMinimo1);
        jTxtStockMinimo1.setBounds(140, 140, 90, 30);

        jLabel10.setText("LIBRO");
        jLabel10.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        jPanel4.add(jLabel10);
        jLabel10.setBounds(80, 80, 50, 20);

        jLabel14.setText("STOCK DISPONIBLE");
        jLabel14.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        jPanel4.add(jLabel14);
        jLabel14.setBounds(10, 200, 120, 20);

        jLabel16.setText("STOCK MAXIMO");
        jPanel4.add(jLabel16);
        jLabel16.setBounds(250, 140, 100, 30);

        validarMax.setFont(new java.awt.Font("Dialog", 2, 12)); // NOI18N
        validarMax.setForeground(new java.awt.Color(255, 51, 51));
        jPanel4.add(validarMax);
        validarMax.setBounds(450, 140, 20, 20);

        validarLibro2.setFont(new java.awt.Font("Dialog", 2, 12)); // NOI18N
        validarLibro2.setForeground(new java.awt.Color(255, 51, 51));
        jPanel4.add(validarLibro2);
        validarLibro2.setBounds(450, 80, 20, 20);

        validarMin1.setFont(new java.awt.Font("Dialog", 2, 12)); // NOI18N
        validarMin1.setForeground(new java.awt.Color(255, 51, 51));
        jPanel4.add(validarMin1);
        validarMin1.setBounds(230, 140, 20, 20);

        CamposObligatorios.setFont(new java.awt.Font("Dialog", 2, 12)); // NOI18N
        CamposObligatorios.setForeground(new java.awt.Color(255, 51, 51));
        jPanel4.add(CamposObligatorios);
        CamposObligatorios.setBounds(330, 230, 140, 20);

        jPanel5.setBackground(new java.awt.Color(255, 255, 255));
        jPanel5.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(71, 84, 130), 2, true));
        jPanel5.setLayout(null);

        jBtnActualizar.setBackground(new java.awt.Color(255, 255, 255));
        jBtnActualizar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/update_24px.png"))); // NOI18N
        jBtnActualizar.setText("ACTUALIZAR");
        jBtnActualizar.setFocusPainted(false);
        jBtnActualizar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBtnActualizarActionPerformed(evt);
            }
        });
        jPanel5.add(jBtnActualizar);
        jBtnActualizar.setBounds(240, 50, 208, 40);

        jBtnLeer.setBackground(new java.awt.Color(255, 255, 255));
        jBtnLeer.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/view_24px.png"))); // NOI18N
        jBtnLeer.setText("LEER");
        jBtnLeer.setFocusPainted(false);
        jBtnLeer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBtnLeerActionPerformed(evt);
            }
        });
        jPanel5.add(jBtnLeer);
        jBtnLeer.setBounds(240, 10, 208, 40);

        jBtnCrear.setBackground(new java.awt.Color(255, 255, 255));
        jBtnCrear.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/Add User Group Woman Man2_24px.png"))); // NOI18N
        jBtnCrear.setText("CREAR");
        jBtnCrear.setFocusPainted(false);
        jBtnCrear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBtnCrearActionPerformed(evt);
            }
        });
        jPanel5.add(jBtnCrear);
        jBtnCrear.setBounds(20, 10, 208, 40);

        jBtnEliminar.setBackground(new java.awt.Color(255, 255, 255));
        jBtnEliminar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/Delete User Male_24px.png"))); // NOI18N
        jBtnEliminar.setText("ELIMINAR");
        jBtnEliminar.setFocusPainted(false);
        jBtnEliminar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBtnEliminarActionPerformed(evt);
            }
        });
        jPanel5.add(jBtnEliminar);
        jBtnEliminar.setBounds(20, 50, 208, 40);

        jBtnLimpiar.setBackground(new java.awt.Color(255, 255, 255));
        jBtnLimpiar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/erase_24px.png"))); // NOI18N
        jBtnLimpiar.setText("LIMPIAR");
        jBtnLimpiar.setFocusPainted(false);
        jBtnLimpiar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBtnLimpiarActionPerformed(evt);
            }
        });
        jPanel5.add(jBtnLimpiar);
        jBtnLimpiar.setBounds(130, 90, 208, 40);

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
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jTxtUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, 370, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, 470, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, 470, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())))
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, 260, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel6)
                            .addComponent(jTxtUsuario))))
                .addContainerGap())
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

    private void jTableInventarioMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableInventarioMouseClicked
        establecerValores();
    }//GEN-LAST:event_jTableInventarioMouseClicked

    private void jBtnBuscarIdentidad1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtnBuscarIdentidad1ActionPerformed

        buscar();        // TODO add your handling code here:
    }//GEN-LAST:event_jBtnBuscarIdentidad1ActionPerformed

    private void jTextStockDisponibleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextStockDisponibleActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextStockDisponibleActionPerformed

    private void jCmbLibroActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCmbLibroActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jCmbLibroActionPerformed

    private void jBtnActualizarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtnActualizarActionPerformed
      
        boolean validar = validarInventario();
if(validar==true){
    
}else{
    actualizarInventario();
}
    }//GEN-LAST:event_jBtnActualizarActionPerformed

    private void jBtnLeerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtnLeerActionPerformed
        leerInventario();
    }//GEN-LAST:event_jBtnLeerActionPerformed

    private void jBtnCrearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtnCrearActionPerformed
boolean validar = validarInventario();
if(validar==true){
    
}else{
        crearInventario();
}
    }//GEN-LAST:event_jBtnCrearActionPerformed

    private void jBtnEliminarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtnEliminarActionPerformed
        if (!jTxtidInventario.getText().isEmpty()) {
            int respuesta = JOptionPane.showConfirmDialog(null, "¿Está seguro de eliminar el inventario?", "Advertencia", JOptionPane.YES_NO_OPTION, 1);
            if (respuesta == 0) {
                eliminarInventario();
            }
        } else {
            JOptionPane.showMessageDialog(null, "Debe seleccionar un inventario para poder ejecutar esta acción.", "Eliminar", 1);
        }
    }//GEN-LAST:event_jBtnEliminarActionPerformed

    private void jBtnLimpiarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtnLimpiarActionPerformed
        limpiar();
    }//GEN-LAST:event_jBtnLimpiarActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel CamposObligatorios;
    private javax.swing.JButton jBtnActualizar;
    private javax.swing.JButton jBtnBuscarIdentidad1;
    private javax.swing.JButton jBtnCrear;
    private javax.swing.JButton jBtnEliminar;
    private javax.swing.JButton jBtnLeer;
    private javax.swing.JButton jBtnLimpiar;
    private javax.swing.JComboBox<String> jCmbLibro;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTableInventario;
    private javax.swing.JTextField jTextStockDisponible;
    private javax.swing.JTextField jTextStockMaximo;
    private javax.swing.JTextField jTxtStockMinimo1;
    private javax.swing.JLabel jTxtUsuario;
    private javax.swing.JTextField jTxtidInventario;
    private javax.swing.JLabel jlblImagen;
    private javax.swing.JLabel validarDisponible;
    private javax.swing.JLabel validarLibro2;
    private javax.swing.JLabel validarMax;
    private javax.swing.JLabel validarMin1;
    // End of variables declaration//GEN-END:variables
}
