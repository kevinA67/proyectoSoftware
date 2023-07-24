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

public class RegistroLibros extends javax.swing.JInternalFrame {

    String usuario, sentenciaSQL, cod = "";
    Connection con = null;
    ConexionBD connec;
    PreparedStatement ps = null;
    ResultSet rs = null;
    DefaultTableModel modelo;
    Object datosLibro[] = new Object[9];
    String nombreTabla = "LIBROS";

    public RegistroLibros() {
        initComponents();
        jTxtUsuario.setText(classPrin.usuario);
        jCmbAutor.addItem("SELECCIONE UNA OPCIÓN: ");
        jCmbEditorial.addItem("SELECCIONE UNA OPCIÓN: ");
          jCmbTipo.addItem("SELECCIONE UNA OPCIÓN: ");
        llenarCmbEditorial();
        llenarCmbAutor();
        llenarCmbTipoLibro();
        leerLibro();
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

    public void conectarBD() {
        connec = new ConexionBD("openfirekafz");
        con = connec.getConexion();
    }

    public void llenarCmbTipoLibro() {
    
        jCmbTipo.addItem("Estándar");
        jCmbTipo.addItem("Premium");
    }

    public void llenarCmbEditorial() {
        try {
            conectarBD();
            ps = (PreparedStatement) con.prepareStatement("SELECT idEditorial, nombreEditorial FROM editorial");
            rs = ps.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("idEditorial");
                id++;
                String nombre = rs.getString("nombreEditorial");
                jCmbEditorial.addItem(nombre);
                jCmbEditorial.setSelectedItem(id);
            }

            con.close();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage());
        }
    }

    public void llenarCmbAutor() {
        try {
            conectarBD();
            ps = (PreparedStatement) con.prepareStatement("SELECT idAutor ,concat_ws(' ', nombreAutor, apellidoAutor) as nombre FROM autor");
            rs = ps.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("idAutor");
                id++;
                String nombre = rs.getString("nombre");
                jCmbAutor.addItem(nombre);
                jCmbAutor.setSelectedItem(id);
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
        int fila = jTableLibros.getRowCount();
        for (int i = fila - 1; i >= 0; i--) {
            modelo.removeRow(i);
//            }
        }
    }

    public void limpiarCampos() {
        jTxtIdLibro.setText(null);
        jTxtCodLibro.setValue(null);
        jTxtTitulo.setText(null);
        jCmbEditorial.setSelectedIndex(0);
        jCmbAutor.setSelectedIndex(0);
        jTxtGenero.setText(null);
        jCmbTipo.setSelectedIndex(0);
        jTxtPrecio.setText(null);
        jTxtCodLibro.requestFocus();

    }
    public void crearLibro() {
        try {
            conectarBD();
            sentenciaSQL = "INSERT INTO libro (idLibro, cod_libro, tituloLibro, idEditorial, idAutor, genero, tipo, precio, estadoLibro) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            ps = con.prepareStatement(sentenciaSQL);
            int indiceEditorial = jCmbEditorial.getSelectedIndex(); //Se obtiene el índice del elemento de combobox que se seleccionó y se guarda en una variable
            int indiceAutor = jCmbAutor.getSelectedIndex();
            int estado = 1; //1 será activo, y 0 desactivo (eliminado)
            String tipoLibro = jCmbTipo.getSelectedItem().toString();

            ps.setInt(1, 0);
            ps.setString(2, jTxtCodLibro.getText());
            ps.setString(3, jTxtTitulo.getText());
            ps.setInt(4, indiceEditorial);
            ps.setInt(5, indiceAutor);
            ps.setString(6, jTxtGenero.getText());
            ps.setString(7, tipoLibro);
            ps.setString(8, jTxtPrecio.getText());
            ps.setInt(9, estado);
            ps.execute();
            
            String accion = "CREACIÓN DE REGISTRO EN LA TABLA " + nombreTabla;
            bitacora(classPrin.idUsuario,accion);
            
            JOptionPane.showMessageDialog(null, "DATOS INGRESADOS CORRECTAMENTE");
            con.close();
            limpiarCampos();
            leerLibro();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "ERROR AL INTENTAR INGRESAR LOS DATOS:  " + ex.getMessage());
        }
    }

    public void leerLibro() {
        conectarBD();
        sentenciaSQL = "SELECT idLibro, cod_libro, tituloLibro, editorial.nombreEditorial, CONCAT(autor.nombreAutor, ' ', autor.apellidoAutor) AS nombreCompletoAutor, genero, tipo, precio FROM libro"
                + "                INNER JOIN editorial ON  libro.idEditorial = editorial.idEditorial"
                + "                INNER JOIN autor ON libro.idAutor = autor.idAutor"
                + "                WHERE estadoLibro =" + " 1";
        try {
            ps = con.prepareStatement(sentenciaSQL);
            rs = ps.executeQuery();
            modelo = (DefaultTableModel) jTableLibros.getModel();
            modelo.setRowCount(0);
            while (rs.next()) {
                datosLibro[0] = (rs.getInt(1)); //idLibro
                datosLibro[1] = (rs.getString(2)); //cod_libro
                datosLibro[2] = (rs.getString(3)); //tituloLibro
                datosLibro[3] = (rs.getString(4)); //idEditorial
                datosLibro[4] = (rs.getString(5)); //idAutor
                datosLibro[5] = (rs.getString(6)); //genero
                datosLibro[6] = (rs.getString(7)); //tipo
                datosLibro[7] = (rs.getString(8)); //precio
                modelo.addRow(datosLibro);
            }
            jTableLibros.setModel(modelo);

            con.close();

           String accion = "LECTURA DE REGISTROS DE LA TABLA " + nombreTabla;
            bitacora(classPrin.idUsuario,accion);
            
            if (jTableLibros.getRowCount() == 0) {
                JOptionPane.showMessageDialog(null, "No hay datos para mostrar.", "Libros", 1);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "ERROR NO SE PUDO LEER LOS DATOS DE LA TABLA.  " + ex.getMessage());
        }

    }

    public void actualizarLibro() {

        try {
            conectarBD();
            sentenciaSQL = "UPDATE libro SET cod_libro =?, tituloLibro=?, idEditorial=?, idAutor=?,  genero=?, tipo=?, precio=?"
                    + " WHERE idLibro =" + jTxtIdLibro.getText();
            ps = con.prepareStatement(sentenciaSQL);
            int indiceEditorial = jCmbEditorial.getSelectedIndex();
            int indiceAutor = jCmbAutor.getSelectedIndex();
            String tipoLibro = jCmbTipo.getSelectedItem().toString();

            ps.setString(1, jTxtCodLibro.getText());
            ps.setString(2, jTxtTitulo.getText());
            ps.setInt(3, indiceEditorial);
            ps.setInt(4, indiceAutor);
            ps.setString(5, jTxtGenero.getText());
            ps.setString(6, tipoLibro);
            ps.setString(7, jTxtPrecio.getText());

            ps.execute();
            
            String accion = "ACTUALIZACIÓN DE REGISTRO EN LA TABLA " + nombreTabla;
            bitacora(classPrin.idUsuario,accion);
            
            JOptionPane.showMessageDialog(null, "DATOS ACTUALIZADOS CORRECTAMENTE");
            con.close();
            leerLibro();
            limpiarCampos();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "ERROR, NO SE PUDO ACTUALIZAR LOS DATOS " + ex.getMessage());
        }
    }

    public void eliminarLibro() {
        try {
            conectarBD();
            sentenciaSQL = "UPDATE libro SET estadoLibro='0' WHERE idLibro =" + jTxtIdLibro.getText();
            ps = con.prepareStatement(sentenciaSQL);
            ps.execute();
            limpiarCampos();
            leerLibro();

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

        sentenciaSQL = "SELECT * FROM  libro WHERE idLibro LIKE '" + jTxtIdLibro.getText() + "' OR tituloLibro LIKE '" + jTxtTitulo.getText() + "'";
        try {
            ps = con.prepareStatement(sentenciaSQL);
            rs = ps.executeQuery();
            modelo = (DefaultTableModel) jTableLibros.getModel();

            while (rs.next()) {
                datosLibro[0] = (rs.getInt(1)); //idLibro
                datosLibro[1] = (rs.getString(2)); //cod_libro
                datosLibro[2] = (rs.getString(3)); //tituloLibro
                datosLibro[3] = (rs.getString(4)); //idEditorial
                datosLibro[4] = (rs.getString(5)); //idAutor
                datosLibro[5] = (rs.getString(6)); //genero
                datosLibro[6] = (rs.getString(7)); //tipo
                datosLibro[7] = (rs.getString(8)); //precio
                modelo.addRow(datosLibro);
            }

            jTableLibros.setModel(modelo);
            con.close();

            String accion = "BÚSQUEDA DE REGISTRO EN LA TABLA " + nombreTabla;
            bitacora(classPrin.idUsuario, accion);
            
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "ERROR, NO SE PUDO LEER LOS DATOS " + ex.getMessage());
        }
    }

    public void establecerValores() {

        int fila = jTableLibros.getSelectedRow();
        jTxtIdLibro.setText(jTableLibros.getValueAt(fila, 0).toString());
        jTxtCodLibro.setText(jTableLibros.getValueAt(fila, 1).toString());
        jTxtTitulo.setText(jTableLibros.getValueAt(fila, 2).toString());
        String Editorial = jTableLibros.getValueAt(fila, 3).toString();
        String Autor = jTableLibros.getValueAt(fila, 4).toString();
        jTxtGenero.setText(jTableLibros.getValueAt(fila, 5).toString());
        String tipo = (String) jTableLibros.getValueAt(fila, 6);
        jTxtPrecio.setText(jTableLibros.getValueAt(fila, 7).toString());
        jCmbEditorial.setSelectedItem(Editorial);
        jCmbAutor.setSelectedItem(Autor);
        jCmbTipo.setSelectedItem(tipo);

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
            validarcodigo.setText("");
            validarTipo.setText("");
            validarTitulo.setText("");
            validarEditorial.setText("");
            validarAutor.setText("");
            validarGenero.setText("");
            validarPrecio.setText("");
    }
public boolean validarCodigo(){
        
        boolean vacios =false;
         for (int i =0 ;i< jTableLibros.getRowCount();i++){
               if(jTableLibros.getValueAt(i, 1).equals(jTxtCodLibro.getText())){
                   
                   JOptionPane.showMessageDialog(null, "El codigo de Libro ya existe");
                   modelo.removeRow(i);
                   limpiar();
                          leerLibro();
               vacios=true;
               }}
         return vacios;
    }
    //Validar campos vacios
    public boolean validarLibros() {
        boolean vacios = false;

        if (jCmbEditorial.getSelectedItem().equals("SELECCIONE UNA OPCIÓN: ")|| jCmbAutor.getSelectedItem().equals("SELECCIONE UNA OPCIÓN: ") || jTxtCodLibro.getText().trim().isEmpty() || jTxtTitulo.getText().trim().isEmpty() || jTxtPrecio.getText().trim().isEmpty() || jCmbTipo.getSelectedItem().equals("SELECCIONE UNA OPCIÓN: ")
                || jTxtGenero.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "NO DEBE DEJAR CAMPOS VACIOS", "CAMPOS VACIOS", 0);
            CamposObligatorios.setText("* Campos Obligatorios ");

            TextoObligatorio(jTxtCodLibro, validarcodigo, "*", "   - -  -      - ");
            TextoObligatorio(jTxtTitulo,validarTitulo, "*");
            TextoObligatorio(jCmbEditorial, validarEditorial, "*");
             TextoObligatorio(jCmbAutor, validarAutor, "*");
              TextoObligatorio(jCmbTipo, validarTipo, "*");
          
            TextoObligatorio(jTxtGenero, validarGenero, "*");
            TextoObligatorio(jTxtPrecio, validarPrecio, "*");

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
        jTableLibros = new javax.swing.JTable();
        jPanel4 = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        jBtnBuscarIdentidad1 = new javax.swing.JButton();
        jLabel10 = new javax.swing.JLabel();
        jTxtPrecio = new javax.swing.JTextField();
        jBtnBuscarNombre1 = new javax.swing.JButton();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jCmbAutor = new javax.swing.JComboBox<>();
        jLabel13 = new javax.swing.JLabel();
        jTxtTitulo = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        jCmbTipo = new javax.swing.JComboBox<>();
        jTxtGenero = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        jTxtIdLibro = new javax.swing.JTextField();
        jLabel16 = new javax.swing.JLabel();
        jCmbEditorial = new javax.swing.JComboBox<>();
        jTxtCodLibro = new javax.swing.JFormattedTextField();
        CamposObligatorios = new javax.swing.JLabel();
        validarIdLibro = new javax.swing.JLabel();
        validarcodigo = new javax.swing.JLabel();
        validarTitulo = new javax.swing.JLabel();
        validarEditorial = new javax.swing.JLabel();
        validarAutor = new javax.swing.JLabel();
        validarGenero = new javax.swing.JLabel();
        validarTipo = new javax.swing.JLabel();
        validarPrecio = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jBtnActualizar = new javax.swing.JButton();
        jBtnLeer = new javax.swing.JButton();
        jBtnCrear = new javax.swing.JButton();
        jBtnEliminar = new javax.swing.JButton();
        jBtnLimpiar = new javax.swing.JButton();
        jTxtUsuario = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jbtnInventario = new javax.swing.JButton();

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
        jLabel2.setText("REGISTRO DE LIBROS");

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
            .addComponent(jlblImagen, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
        );

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));
        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(71, 84, 130), 2, true), "LISTA DE REGISTROS", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 0, 12), new java.awt.Color(71, 84, 130))); // NOI18N

        jTableLibros.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(71, 84, 130)));
        jTableLibros.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "CODIGO", "TITULO", "EDITORIAL", "AUTOR", "GENERO", "TIPO", "PRECIO"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, true, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTableLibros.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTableLibrosMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jTableLibros);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 490, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap(8, Short.MAX_VALUE)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 411, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));
        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(71, 84, 130), 2, true), "FORMULARIO DATOS", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 0, 12), new java.awt.Color(71, 84, 130))); // NOI18N
        jPanel4.setLayout(null);

        jLabel9.setText("Titulo");
        jLabel9.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        jPanel4.add(jLabel9);
        jLabel9.setBounds(90, 100, 60, 30);

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
        jBtnBuscarIdentidad1.setBounds(280, 30, 40, 30);

        jLabel10.setText("Precio");
        jPanel4.add(jLabel10);
        jLabel10.setBounds(90, 310, 70, 30);
        jPanel4.add(jTxtPrecio);
        jTxtPrecio.setBounds(160, 310, 230, 30);

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
        jBtnBuscarNombre1.setBounds(430, 110, 40, 30);

        jLabel11.setText("Codigo de Libro");
        jLabel11.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        jPanel4.add(jLabel11);
        jLabel11.setBounds(50, 60, 90, 30);

        jLabel12.setText("ID Autor");
        jLabel12.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        jPanel4.add(jLabel12);
        jLabel12.setBounds(90, 190, 70, 30);

        jCmbAutor.setBackground(new java.awt.Color(255, 255, 255));
        jPanel4.add(jCmbAutor);
        jCmbAutor.setBounds(160, 190, 230, 30);

        jLabel13.setText("ID Editorial");
        jPanel4.add(jLabel13);
        jLabel13.setBounds(70, 140, 90, 30);
        jPanel4.add(jTxtTitulo);
        jTxtTitulo.setBounds(160, 110, 260, 30);

        jLabel14.setText("Genero");
        jPanel4.add(jLabel14);
        jLabel14.setBounds(90, 230, 70, 30);

        jCmbTipo.setBackground(new java.awt.Color(255, 255, 255));
        jPanel4.add(jCmbTipo);
        jCmbTipo.setBounds(160, 270, 230, 30);
        jPanel4.add(jTxtGenero);
        jTxtGenero.setBounds(160, 230, 230, 30);

        jLabel15.setText("Tipo");
        jPanel4.add(jLabel15);
        jLabel15.setBounds(110, 270, 50, 30);
        jPanel4.add(jTxtIdLibro);
        jTxtIdLibro.setBounds(160, 30, 100, 30);

        jLabel16.setText("ID Libro");
        jLabel16.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        jPanel4.add(jLabel16);
        jLabel16.setBounds(90, 30, 50, 30);

        jCmbEditorial.setBackground(new java.awt.Color(255, 255, 255));
        jPanel4.add(jCmbEditorial);
        jCmbEditorial.setBounds(160, 150, 230, 30);

        try {
            jTxtCodLibro.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("###-#-##-######-#")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }
        jPanel4.add(jTxtCodLibro);
        jTxtCodLibro.setBounds(160, 70, 260, 30);

        CamposObligatorios.setFont(new java.awt.Font("Dialog", 2, 12)); // NOI18N
        CamposObligatorios.setForeground(new java.awt.Color(255, 51, 51));
        jPanel4.add(CamposObligatorios);
        CamposObligatorios.setBounds(10, 340, 140, 20);

        validarIdLibro.setFont(new java.awt.Font("Dialog", 2, 12)); // NOI18N
        validarIdLibro.setForeground(new java.awt.Color(255, 51, 51));
        jPanel4.add(validarIdLibro);
        validarIdLibro.setBounds(260, 30, 20, 20);

        validarcodigo.setFont(new java.awt.Font("Dialog", 2, 12)); // NOI18N
        validarcodigo.setForeground(new java.awt.Color(255, 51, 51));
        jPanel4.add(validarcodigo);
        validarcodigo.setBounds(420, 70, 20, 20);

        validarTitulo.setFont(new java.awt.Font("Dialog", 2, 12)); // NOI18N
        validarTitulo.setForeground(new java.awt.Color(255, 51, 51));
        jPanel4.add(validarTitulo);
        validarTitulo.setBounds(420, 110, 20, 20);

        validarEditorial.setFont(new java.awt.Font("Dialog", 2, 12)); // NOI18N
        validarEditorial.setForeground(new java.awt.Color(255, 51, 51));
        jPanel4.add(validarEditorial);
        validarEditorial.setBounds(390, 150, 20, 20);

        validarAutor.setFont(new java.awt.Font("Dialog", 2, 12)); // NOI18N
        validarAutor.setForeground(new java.awt.Color(255, 51, 51));
        jPanel4.add(validarAutor);
        validarAutor.setBounds(390, 190, 20, 20);

        validarGenero.setFont(new java.awt.Font("Dialog", 2, 12)); // NOI18N
        validarGenero.setForeground(new java.awt.Color(255, 51, 51));
        jPanel4.add(validarGenero);
        validarGenero.setBounds(390, 230, 20, 20);

        validarTipo.setFont(new java.awt.Font("Dialog", 2, 12)); // NOI18N
        validarTipo.setForeground(new java.awt.Color(255, 51, 51));
        jPanel4.add(validarTipo);
        validarTipo.setBounds(390, 270, 20, 20);

        validarPrecio.setFont(new java.awt.Font("Dialog", 2, 12)); // NOI18N
        validarPrecio.setForeground(new java.awt.Color(255, 51, 51));
        jPanel4.add(validarPrecio);
        validarPrecio.setBounds(390, 310, 20, 20);

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
                        .addComponent(jBtnActualizar, javax.swing.GroupLayout.PREFERRED_SIZE, 208, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(20, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jBtnLimpiar, javax.swing.GroupLayout.PREFERRED_SIZE, 208, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(128, 128, 128))
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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jBtnLimpiar)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTxtUsuario.setBackground(new java.awt.Color(255, 255, 255));
        jTxtUsuario.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jTxtUsuario.setForeground(new java.awt.Color(237, 120, 74));
        jTxtUsuario.setText("AQUÍ IRÁ EL USUARIO QUE ESTÁ INGRESANDO");

        jLabel6.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jLabel6.setText("USUARIO: ");

        jbtnInventario.setBackground(new java.awt.Color(237, 120, 74));
        jbtnInventario.setForeground(new java.awt.Color(255, 255, 255));
        jbtnInventario.setText("IR A INVENTARIO");
        jbtnInventario.setToolTipText("");
        jbtnInventario.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnInventarioActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, 474, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(10, 10, 10)
                                .addComponent(jTxtUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, 370, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jbtnInventario, javax.swing.GroupLayout.PREFERRED_SIZE, 235, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, 360, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(2, 2, 2)
                        .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jbtnInventario, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGap(10, 10, 10)
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

    private void jTableLibrosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableLibrosMouseClicked
        establecerValores();
    }//GEN-LAST:event_jTableLibrosMouseClicked

    private void jBtnBuscarIdentidad1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtnBuscarIdentidad1ActionPerformed

        buscar();        // TODO add your handling code here:
    }//GEN-LAST:event_jBtnBuscarIdentidad1ActionPerformed

    private void jBtnBuscarNombre1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtnBuscarNombre1ActionPerformed

        buscar();        // TODO add your handling code here:
    }//GEN-LAST:event_jBtnBuscarNombre1ActionPerformed

    private void jBtnActualizarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtnActualizarActionPerformed
boolean vacios =validarLibros();


if(vacios==true){
    
}else{

        actualizarLibro();
        limpiarLabel();
}
    }//GEN-LAST:event_jBtnActualizarActionPerformed

    private void jBtnLeerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtnLeerActionPerformed
        leerLibro();
    }//GEN-LAST:event_jBtnLeerActionPerformed

    private void jBtnCrearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtnCrearActionPerformed
     boolean vacios = validarLibros();
     boolean validarcod=validarCodigo();
     
     if(vacios==true){
         
     }else if(validarcod==true){
}else{
       
        crearLibro();
     }
    }//GEN-LAST:event_jBtnCrearActionPerformed

    private void jBtnEliminarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtnEliminarActionPerformed
        if (!jTxtCodLibro.getText().isEmpty()) {
            int respuesta = JOptionPane.showConfirmDialog(null, "¿Está seguro de eliminar el libro?", "Advertencia", JOptionPane.YES_NO_OPTION, 1);
            if (respuesta == 0) {
                eliminarLibro();
                limpiarLabel();
            }
        } else {
            JOptionPane.showMessageDialog(null, "Debe seleccionar un libro para poder ejecutar esta acción.", "Elimar", 1);
        }
    }//GEN-LAST:event_jBtnEliminarActionPerformed

    private void jBtnLimpiarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtnLimpiarActionPerformed
        limpiar();
        limpiarLabel();
    }//GEN-LAST:event_jBtnLimpiarActionPerformed

    private void jbtnInventarioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnInventarioActionPerformed
        Inventario inv = new Inventario();
        Menu_Principal.jDesktopPane1.add(inv);
        Dimension desktopSize = Menu_Principal.jDesktopPane1.getSize();
        Dimension FrameSize = inv.getSize();
        inv.setLocation((desktopSize.width - FrameSize.width) / 2, (desktopSize.height - FrameSize.height) / 2);
        inv.show();
    }//GEN-LAST:event_jbtnInventarioActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel CamposObligatorios;
    private javax.swing.JButton jBtnActualizar;
    private javax.swing.JButton jBtnBuscarIdentidad1;
    private javax.swing.JButton jBtnBuscarNombre1;
    private javax.swing.JButton jBtnCrear;
    private javax.swing.JButton jBtnEliminar;
    private javax.swing.JButton jBtnLeer;
    private javax.swing.JButton jBtnLimpiar;
    private javax.swing.JComboBox<String> jCmbAutor;
    private javax.swing.JComboBox<String> jCmbEditorial;
    private javax.swing.JComboBox<String> jCmbTipo;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
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
    private javax.swing.JTable jTableLibros;
    private javax.swing.JFormattedTextField jTxtCodLibro;
    private javax.swing.JTextField jTxtGenero;
    private javax.swing.JTextField jTxtIdLibro;
    private javax.swing.JTextField jTxtPrecio;
    private javax.swing.JTextField jTxtTitulo;
    private javax.swing.JLabel jTxtUsuario;
    private javax.swing.JButton jbtnInventario;
    private javax.swing.JLabel jlblImagen;
    private javax.swing.JLabel validarAutor;
    private javax.swing.JLabel validarEditorial;
    private javax.swing.JLabel validarGenero;
    private javax.swing.JLabel validarIdLibro;
    private javax.swing.JLabel validarPrecio;
    private javax.swing.JLabel validarTipo;
    private javax.swing.JLabel validarTitulo;
    private javax.swing.JLabel validarcodigo;
    // End of variables declaration//GEN-END:variables
}
