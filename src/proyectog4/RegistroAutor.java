/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package proyectog4;

import Conexion.ConexionBD;
import com.sun.org.apache.xpath.internal.operations.Bool;
import com.toedter.calendar.JDateChooser;
import java.sql.*;
import java.text.ParseException;
import javax.swing.*;
import javax.swing.table.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Alejandra
 */
public class RegistroAutor extends javax.swing.JInternalFrame {

    String usuario, sentenciaSQL, cod;
    Date fechaCreacion;

    Connection con = null;
    ConexionBD connec;
    PreparedStatement ps = null;
    ImageIcon icono;
    ResultSet rs = null;
    DefaultTableModel modelo;
    Object datosAutor[] = new Object[6];
    String nombreTabla = "AUTOR";

    public RegistroAutor() {
        initComponents();
        jTxtUsuario.setText(classPrin.usuario);
    }

    public void conectarBD() {
        connec = new ConexionBD("openfirekafz");
        con = connec.getConexion();
    }

    public void limpiar() {
        limpiarCampos();

        if (jTableAutor.getRowCount() == 0) {
            JOptionPane.showMessageDialog(null, "No hay datos para mostrar.", "Tabla vacía", JOptionPane.INFORMATION_MESSAGE);
        } else {
            int fila = jTableAutor.getRowCount();
            for (int i = fila - 1; i >= 0; i--) {
                modelo.removeRow(i);
            }
        }
    }

    public void limpiarCampos() {
        jTxtNombre.setText(null);
        jTxtApellido.setText(null);
        jdcFechaNacimiento.setCalendar(null);
        jdcFechaFallecimiento.setCalendar(null);
        jTxtNombre.requestFocus();
        jTxtNacionalidad.setText(null);

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

    public void crearAutor() {
        try {
            SimpleDateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd");
            String fechaN = dFormat.format(jdcFechaNacimiento.getDate());
            String fechaF = dFormat.format(jdcFechaFallecimiento.getDate());
            conectarBD();
            sentenciaSQL = "INSERT INTO autor (idAutor, nombreAutor, apellidoAutor, nacionalidad, fechaNac, fechaFac, estadoAutor) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?) ";
            ps = con.prepareStatement(sentenciaSQL);
            int estado = 1;

            ps.setInt(1, 0);
            ps.setString(2, jTxtNombre.getText());
            ps.setString(3, jTxtApellido.getText());
            ps.setString(4, jTxtNacionalidad.getText());
            ps.setString(5, fechaN);
            ps.setString(6, fechaF);
            ps.setInt(7, estado);
            ps.execute();

            String accion = "CREACIÓN DE REGISTRO EN LA TABLA " + nombreTabla;
            bitacora(classPrin.idUsuario, accion);

            JOptionPane.showMessageDialog(null, "DATOS INGRESADOS CORRECTAMENTE");
            con.close();
            leerAutor();
            limpiarCampos();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "ERROR AL INTENTAR INGRESAR LOS DATOS:  " + ex.getMessage());
        }
    }

    public void leerAutor() {
        conectarBD();
        sentenciaSQL = "SELECT idAutor,nombreAutor,apellidoAutor,nacionalidad,fechaNac,fechaFac FROM autor "
                + "WHERE estadoAutor= " + "1";
        try {
            ps = con.prepareStatement(sentenciaSQL);
            rs = ps.executeQuery();
            modelo = (DefaultTableModel) jTableAutor.getModel();
            modelo.setRowCount(0);
            while (rs.next()) {
                datosAutor[0] = (rs.getInt(1));
                datosAutor[1] = (rs.getString(2));
                datosAutor[2] = (rs.getString(3));
                datosAutor[3] = (rs.getString(4));
                datosAutor[4] = (rs.getString(5));
                datosAutor[5] = (rs.getString(6));
                modelo.addRow(datosAutor);
            }
            jTableAutor.setModel(modelo);

            String accion = "LECTURA A LOS REGISTROS DE LA TABLA " + nombreTabla;
            bitacora(classPrin.idUsuario, accion);
            con.close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "ERROR NO SE PUDO LEER LOS DATOS DE LA TABLA.  " + ex.getMessage());
        }

    }

    public void actualizarAutor() {

        try {
            SimpleDateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd");
            String fechaN = dFormat.format(jdcFechaNacimiento.getDate());
            String fechaF = dFormat.format(jdcFechaFallecimiento.getDate());
            conectarBD();
            sentenciaSQL = "UPDATE autor SET nombreAutor=?, apellidoAutor=?, nacionalidad=?, fechaNac=?, fechaFac=?"
                    + " WHERE idAutor =" + this.cod;
            ps = con.prepareStatement(sentenciaSQL);

            ps.setString(1, jTxtNombre.getText());
            ps.setString(2, jTxtApellido.getText());
            ps.setString(3, jTxtNacionalidad.getText());
            ps.setString(4, fechaN);
            ps.setString(5, fechaF);
            ps.execute();

            String accion = "ACTUALIZACIÓN DE REGISTRO EN LA TABLA " + nombreTabla;
            bitacora(classPrin.idUsuario, accion);

            JOptionPane.showMessageDialog(null, "DATOS ACTUALIZADOS CORRECTAMENTE");
            con.close();
            leerAutor();
            limpiarCampos();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "ERROR, NO SE PUDO ACTUALIZAR LOS DATOS " + ex.getMessage());
        }
    }

    public void eliminarAutor() {
        try {
            conectarBD();
            sentenciaSQL = "UPDATE autor SET estadoAutor='0' WHERE idAutor =" + this.cod;
            ps = con.prepareStatement(sentenciaSQL);
            ps.execute();
            limpiarCampos();
            leerAutor();

            String accion = "ELIMINACIÓN DE REGISTRO EN LA TABLA " + nombreTabla;
            bitacora(classPrin.idUsuario, accion);

            JOptionPane.showMessageDialog(null, "DATOS ELIMINADOS CORRECTAMENTE!");
            con.close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "ERROR, NO SE PUDO ELIMINAR LOS DATOS " + ex.getMessage());
        }
    }

    public void buscarAutor(int condicion) {
        try {
            if (condicion == 1) {
                sentenciaSQL = "SELECT idAutor, nombreAutor, apellidoAutor, nacionalidad, fechaNac, fechaFac FROM autor "
                        + "WHERE nombreAutor like '%" + jTxtNombre.getText() + "' and estadoAutor=1";
            }
//            } else if (condicion == 0) {
//                sentenciaSQL = "SELECT idEditorial, nombreEditorial, telefono, correo, direccion FROM editorial "
//                        + "WHERE nombreEditorial like '%" + jTxtNombreEditorial.getText() + "%' and estadoEditorial=1";
//            }

            int fila = jTableAutor.getRowCount();
            for (int i = fila - 1; i >= 0; i--) {
                modelo.removeRow(i);
            }
            conectarBD();

            ps = con.prepareStatement(sentenciaSQL);
            rs = ps.executeQuery();
            modelo = (DefaultTableModel) jTableAutor.getModel();
            while (rs.next()) {
                datosAutor[0] = (rs.getInt(1));
                datosAutor[1] = (rs.getString(2));
                datosAutor[2] = (rs.getString(3));
                datosAutor[3] = (rs.getString(4));
                datosAutor[4] = (rs.getString(5));
                datosAutor[5] = (rs.getString(6));
                modelo.addRow(datosAutor);
            }
            jTableAutor.setModel(modelo);
            con.close();

            String accion = "BUSCÓ UN REGISTRO EN LA TABLA " + nombreTabla;
            bitacora(classPrin.idUsuario, accion);

            if (jTableAutor.getRowCount() == 0) {
                JOptionPane.showMessageDialog(null, "NO SE PUDO ENCONTRAR EL AUTOR");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "ERROR, NO SE PUDO ENCONTRAR EL AUTOR. " + ex.getMessage());
        }
    }

    public void establecerValores() {

        int fila = jTableAutor.getSelectedRow();
        jTableAutor.getValueAt(fila, 4).toString();
        this.cod = jTableAutor.getValueAt(fila, 0).toString();
        jTxtNombre.setText(jTableAutor.getValueAt(fila, 1).toString());
        jTxtApellido.setText(jTableAutor.getValueAt(fila, 2).toString());
        jTxtNacionalidad.setText(jTableAutor.getValueAt(fila, 3).toString());

        SimpleDateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date fecha;
        try {
            fecha = dFormat.parse(jTableAutor.getValueAt(fila, 4).toString());
            jdcFechaNacimiento.setDate(fecha);
            fecha = dFormat.parse(jTableAutor.getValueAt(fila, 5).toString());
            jdcFechaFallecimiento.setDate(fecha);
        } catch (ParseException ex) {
            Logger.getLogger(RegistroAutor.class.getName()).log(Level.SEVERE, null, ex);
        }
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

    public void TextoObligatorio(JDateChooser Field, JLabel label, String s, String mascara) {
        if (Field.getDate().equals(mascara)) {
            label.setText(s);
        } else {
            label.setText("");
        }
    }

    //Validar campos vacios
    public boolean validarAutor() {
        boolean vacios = false;

        if (jTxtNombre.getText().trim().isEmpty() || jTxtApellido.getText().trim().isEmpty() || jdcFechaNacimiento.getDate() == null || jTxtNacionalidad.getText().trim().isEmpty() ) {
            JOptionPane.showMessageDialog(null, "NO DEBE DEJAR CAMPOS VACIOS", "CAMPOS VACIOS", 0);
            CamposObligatorios.setText("* Campos Obligatorios ");

            TextoObligatorio(jTxtNombre, validarNombre, "*");
            TextoObligatorio(jTxtApellido, validarApellido, "*");
            TextoObligatorio(jTxtNacionalidad, validarNacionalidad, "*");
            
            
            if (jdcFechaNacimiento.getDate() == null) {
                validarINacimiento.setText("*");
            } else {
                validarINacimiento.setText("");

            }
            
            
         /*   if (jdcFechaFallecimiento.getDate() == null) {
                validarFallecimiento.setText("*");
            }else {
                validarFallecimiento.setText("");
            }*/
            
            vacios = true;

        } else {
            vacios = false;
            CamposObligatorios.setText(" ");
            validarNombre.setText("");
            validarApellido.setText("");
            validarNacionalidad.setText("");
            validarINacimiento.setText("");
            //validarFallecimiento.setText("");

        }
        return vacios;
    }

    /*public void TomarFechaN(){
        fechaCreacion=this.FechaN.getDate();
        SimpleDateFormat fecha = new SimpleDateFormat("yyy-MM-dd");
        fech = fecha.format(fechaCreacion);
        
        
    }
    public void TomarFechaF(){
        fechaCreacion=this.FechaF.getDate();
        SimpleDateFormat fecha = new SimpleDateFormat("yyy-MM-dd");
        fech = fecha.format(fechaCreacion);
        
        
    }*/
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
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTableAutor = new javax.swing.JTable();
        jPanel4 = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        jTxtNombre = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        jBtnBuscarNombre1 = new javax.swing.JButton();
        jLabel11 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jTxtApellido = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        jTxtNacionalidad = new javax.swing.JTextField();
        jBtnBuscarNombre2 = new javax.swing.JButton();
        jdcFechaNacimiento = new com.toedter.calendar.JDateChooser();
        jdcFechaFallecimiento = new com.toedter.calendar.JDateChooser();
        CamposObligatorios = new javax.swing.JLabel();
        validarNombre = new javax.swing.JLabel();
        validarApellido = new javax.swing.JLabel();
        validarNacionalidad = new javax.swing.JLabel();
        validarINacimiento = new javax.swing.JLabel();
        validarFallecimiento = new javax.swing.JLabel();
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
        setTitle("AUTORES");
        setToolTipText("");
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        setFrameIcon(null);
        setMaximumSize(new java.awt.Dimension(1000, 700));
        setMinimumSize(new java.awt.Dimension(1000, 700));

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jPanel2.setBackground(new java.awt.Color(237, 120, 74));

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/08- Logo The Book House.png"))); // NOI18N

        jLabel2.setFont(new java.awt.Font("Century Schoolbook", 1, 44)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("REGISTRO DE AUTOR");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel1)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel2)))
                .addGap(19, 19, 19))
        );

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));
        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(71, 84, 130), 2, true), "LISTA DE REGISTROS", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 1, 12), new java.awt.Color(71, 84, 130))); // NOI18N

        jTableAutor.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(71, 84, 130)));
        jTableAutor.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "NOMBRE", "APELLIDO", "NACIONALIDAD", "FECHA NACIMIENTO", "FECHA FALLECIMIENTO"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTableAutor.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTableAutorMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jTableAutor);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(4, 4, 4)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 480, Short.MAX_VALUE)
                .addGap(4, 4, 4))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 446, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));
        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(71, 84, 130), 2, true), "FORMULARIO DATOS", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 1, 12), new java.awt.Color(71, 84, 130))); // NOI18N
        jPanel4.setLayout(null);

        jLabel9.setText("NOMBRE COMPLETO");
        jLabel9.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        jPanel4.add(jLabel9);
        jLabel9.setBounds(20, 50, 160, 20);
        jPanel4.add(jTxtNombre);
        jTxtNombre.setBounds(160, 50, 180, 30);

        jLabel10.setText("FECHA FALLECIMIENTO");
        jPanel4.add(jLabel10);
        jLabel10.setBounds(10, 260, 160, 20);

        jBtnBuscarNombre1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/search_24px.png"))); // NOI18N
        jBtnBuscarNombre1.setToolTipText("Presione para buscar un cliente por nombre");
        jBtnBuscarNombre1.setBorder(null);
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
        jBtnBuscarNombre1.setBounds(360, 50, 40, 30);

        jLabel11.setText("APELLIDO COMPLETO");
        jLabel11.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        jPanel4.add(jLabel11);
        jLabel11.setBounds(20, 100, 150, 20);

        jLabel13.setText("NACIONALIDAD");
        jPanel4.add(jLabel13);
        jLabel13.setBounds(50, 160, 120, 20);
        jPanel4.add(jTxtApellido);
        jTxtApellido.setBounds(160, 100, 240, 30);

        jLabel14.setText("FECHA NACIMIENTO");
        jPanel4.add(jLabel14);
        jLabel14.setBounds(20, 220, 140, 20);
        jPanel4.add(jTxtNacionalidad);
        jTxtNacionalidad.setBounds(160, 160, 240, 30);

        jBtnBuscarNombre2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/search_24px.png"))); // NOI18N
        jBtnBuscarNombre2.setToolTipText("Presione para buscar un cliente por nombre");
        jBtnBuscarNombre2.setBorderPainted(false);
        jBtnBuscarNombre2.setContentAreaFilled(false);
        jBtnBuscarNombre2.setFocusPainted(false);
        jBtnBuscarNombre2.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/search_32px.png"))); // NOI18N
        jPanel4.add(jBtnBuscarNombre2);
        jBtnBuscarNombre2.setBounds(360, 50, 40, 30);
        jPanel4.add(jdcFechaNacimiento);
        jdcFechaNacimiento.setBounds(160, 210, 240, 30);
        jPanel4.add(jdcFechaFallecimiento);
        jdcFechaFallecimiento.setBounds(160, 250, 240, 30);

        CamposObligatorios.setFont(new java.awt.Font("Dialog", 2, 12)); // NOI18N
        CamposObligatorios.setForeground(new java.awt.Color(255, 51, 51));
        jPanel4.add(CamposObligatorios);
        CamposObligatorios.setBounds(10, 330, 140, 20);

        validarNombre.setFont(new java.awt.Font("Dialog", 2, 12)); // NOI18N
        validarNombre.setForeground(new java.awt.Color(255, 51, 51));
        jPanel4.add(validarNombre);
        validarNombre.setBounds(340, 50, 20, 20);

        validarApellido.setFont(new java.awt.Font("Dialog", 2, 12)); // NOI18N
        validarApellido.setForeground(new java.awt.Color(255, 51, 51));
        jPanel4.add(validarApellido);
        validarApellido.setBounds(400, 100, 20, 20);

        validarNacionalidad.setFont(new java.awt.Font("Dialog", 2, 12)); // NOI18N
        validarNacionalidad.setForeground(new java.awt.Color(255, 51, 51));
        jPanel4.add(validarNacionalidad);
        validarNacionalidad.setBounds(400, 160, 20, 20);

        validarINacimiento.setFont(new java.awt.Font("Dialog", 2, 12)); // NOI18N
        validarINacimiento.setForeground(new java.awt.Color(255, 51, 51));
        jPanel4.add(validarINacimiento);
        validarINacimiento.setBounds(400, 210, 20, 20);

        validarFallecimiento.setFont(new java.awt.Font("Dialog", 2, 12)); // NOI18N
        validarFallecimiento.setForeground(new java.awt.Color(255, 51, 51));
        jPanel4.add(validarFallecimiento);
        validarFallecimiento.setBounds(400, 250, 20, 20);

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
                .addContainerGap(18, Short.MAX_VALUE))
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
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, 470, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(10, 10, 10))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jTxtUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, 341, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(41, 41, 41)))
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(10, 10, 10))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, 316, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jTxtUsuario)
                            .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, 30, Short.MAX_VALUE))
                        .addGap(2, 2, 2))
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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

    private void jBtnActualizarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtnActualizarActionPerformed
        Boolean vacios = validarAutor();

       if(vacios==true){
           
       }else{
           
            if (!jTxtNombre.getText().isEmpty()) {
                int respuesta = JOptionPane.showConfirmDialog(null, "¿Está seguro de actualizar el autor?", "Advertencia", JOptionPane.YES_NO_OPTION, 1);
                if ( respuesta == 0 ) {

                    actualizarAutor();
                }
            } else {
                JOptionPane.showMessageDialog(null, "Debe seleccionar un autor para poder ejecutar esta acción.", "Actualizar", 1);
            }}
       
    }//GEN-LAST:event_jBtnActualizarActionPerformed

    private void jBtnLeerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtnLeerActionPerformed
        leerAutor();
    }//GEN-LAST:event_jBtnLeerActionPerformed

    private void jBtnCrearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtnCrearActionPerformed
        Boolean vacios = validarAutor();

        if (vacios == true) {

        } else {
            crearAutor();
        }
    }//GEN-LAST:event_jBtnCrearActionPerformed

    private void jBtnLimpiarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtnLimpiarActionPerformed
        limpiar();
    }//GEN-LAST:event_jBtnLimpiarActionPerformed

    private void jTableAutorMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableAutorMouseClicked
        establecerValores();
    }//GEN-LAST:event_jTableAutorMouseClicked

    private void jBtnEliminarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtnEliminarActionPerformed
        if (!jTxtNombre.getText().isEmpty()) {
            int respuesta = JOptionPane.showConfirmDialog(null, "¿Está seguro de eliminar el autor?", "Advertencia", JOptionPane.YES_NO_OPTION, 1);
            if (respuesta == 0) {
                eliminarAutor();
            }
        } else {
            JOptionPane.showMessageDialog(null, "Debe seleccionar un autor para poder ejecutar esta acción.", "Eliminar", 1);
        }
    }//GEN-LAST:event_jBtnEliminarActionPerformed

    private void jBtnBuscarNombre1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtnBuscarNombre1ActionPerformed
        buscarAutor(1);
    }//GEN-LAST:event_jBtnBuscarNombre1ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel CamposObligatorios;
    private javax.swing.JButton jBtnActualizar;
    private javax.swing.JButton jBtnBuscarNombre1;
    private javax.swing.JButton jBtnBuscarNombre2;
    private javax.swing.JButton jBtnCrear;
    private javax.swing.JButton jBtnEliminar;
    private javax.swing.JButton jBtnLeer;
    private javax.swing.JButton jBtnLimpiar;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
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
    private javax.swing.JTable jTableAutor;
    private javax.swing.JTextField jTxtApellido;
    private javax.swing.JTextField jTxtNacionalidad;
    private javax.swing.JTextField jTxtNombre;
    private javax.swing.JLabel jTxtUsuario;
    private com.toedter.calendar.JDateChooser jdcFechaFallecimiento;
    private com.toedter.calendar.JDateChooser jdcFechaNacimiento;
    private javax.swing.JLabel validarApellido;
    private javax.swing.JLabel validarFallecimiento;
    private javax.swing.JLabel validarINacimiento;
    private javax.swing.JLabel validarNacionalidad;
    private javax.swing.JLabel validarNombre;
    // End of variables declaration//GEN-END:variables
}
