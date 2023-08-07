/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vista;

import Conexion.ConexionBD;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
/**
 *
 * @author Usuario
 */
public class JInternalProductos extends javax.swing.JInternalFrame {
static Connection c=null;
static Statement sentencia;
static ResultSet rs1;
Connection con=null;
ConexionBD conecta;
String sentenciaSQL;
ResultSet rs=null;
PreparedStatement ps=null;
DefaultTableModel modelo;
Object datosproducto[]=new Object[10];
    /**
     * Creates new form Productos
     */
    public JInternalProductos() {
        initComponents();
         //this.setLocationRelativeTo(null);
        txtproveedor.setVisible(false);
         txtcategoria.setVisible(false);
         conectar();
        cbocategoria.removeAllItems();
        cbocategoria.addItem("Seleccione una categoria");
         ArrayList<String> lista = new ArrayList<String>();
        lista =tCategoria();
        for(int i = 0; i<lista.size(); i++){
            
            cbocategoria.addItem(lista.get(i));
        }
               
        cboproveedor.removeAllItems();
        cboproveedor.addItem("Seleccione un proveedor");
         ArrayList<String> lista1 = new ArrayList<String>();
        lista1 =tProveedor();
        for(int i = 0; i<lista1.size(); i++){
            
            cboproveedor.addItem(lista1.get(i));
        }
    }
    
     public void conectarbd()
    {
        conecta=new ConexionBD("abarroteria");
        con=conecta.getConexion();
    }
    
    public static void conectar(){
       
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            String url = "jdbc:mysql://db4free.net:3306/abarroteria";
            String user = "progra_admin";
            String pass = "Pr0gr4m4c10n.w3b";
            c=DriverManager.getConnection(url,user,pass); 
            sentencia= c.createStatement();
            System.out.println("CONEXION REALIZADA CORRECTAMENTE");
        } catch (Exception e) {
            
            System.out.println("NO SE PUDO CONECTAR A BD");
        }
    }
    
 public static ArrayList<String> tCategoria(){
        ArrayList<String> lista = new ArrayList<String>();
        
        String q = "SELECT id_categoria,nombre_categoria FROM categoria";
        try {
            rs1 = sentencia.executeQuery(q);
        } catch (Exception e) {
            
        }
        try {
            while(rs1.next()){
                lista.add(rs1.getString("nombre_categoria"));
              
            }
        } catch (Exception e) {
        }
        return lista;
    }

    public static ArrayList<String> tProveedor(){
        ArrayList<String> lista = new ArrayList<String>();
        
        String q = "SELECT id_proveedor,nombre_proveedor FROM proveedores";
        try {
            rs1 = sentencia.executeQuery(q);
        } catch (Exception e) {
            
        }
        try {
            while(rs1.next()){
                lista.add(rs1.getString("nombre_proveedor"));
              
            }
        } catch (Exception e) {
        }
        return lista;
    }
    
     public void pruebaCategoria()
    {
           String q = "SELECT id_categoria,nombre_categoria FROM categoria WHERE nombre_categoria='"+cbocategoria.getSelectedItem()+"'";
        try {
            rs1 = sentencia.executeQuery(q);
        } catch (Exception e) {
            
        }
        try {
            while(rs1.next()){
                txtcategoria.setText(rs1.getString("id_categoria"));
              
            }
        } catch (Exception e) {
        }      
    }
    
     public void pruebaCategoriacombo()
    {
           String q = "SELECT id_categoria,nombre_categoria FROM categoria WHERE id_categoria='"+txtcategoria.getText()+"'";
        try {
            rs1 = sentencia.executeQuery(q);
        } catch (Exception e) {
            
        }
        try {
            while(rs1.next()){
                cbocategoria.setSelectedItem(rs1.getString("nombre_categoria"));
              
            }
        } catch (Exception e) {
        }      
    }
     
    public void pruebaProveedor()
    {
           String q = "SELECT id_proveedor,nombre_proveedor FROM proveedores WHERE nombre_proveedor='"+cboproveedor.getSelectedItem()+"'";
        try {
            rs1 = sentencia.executeQuery(q);
        } catch (Exception e) {
            
        }
        try {
            while(rs1.next()){
                txtproveedor.setText(rs1.getString("id_proveedor"));
              
            }
        } catch (Exception e) {
        }      
    }
    
    public void pruebaProveedorcombo()
    {
           String q = "SELECT id_proveedor,nombre_proveedor FROM proveedores WHERE id_proveedor='"+txtproveedor.getText()+"'";
        try {
            rs1 = sentencia.executeQuery(q);
        } catch (Exception e) {
            
        }
        try {
            while(rs1.next()){
                cboproveedor.setSelectedItem(rs1.getString("nombre_proveedor"));
              
            }
        } catch (Exception e) {
        }      
    }
    
      public void limpiarTabla()
    {
        modelo.setRowCount(0);
    }
    
      public void limpiar()
      {
          txtnombre1.setText("");
          txtdescripcion.setText("");
          txtprecio.setText("");
          txtvida.setText("");
          txtstockmin.setText("");
          txtstockmax2.setText("");
      }
      
       public void crearproducto() 
    {       
        try {

        conectarbd();
        sentenciaSQL="INSERT INTO productos(id_producto,nombre_producto,descripcion,precio_unitario,catagoria_id,proveedor_id,new_field,stock_minimo,stock_maximo,estado) VALUES(?,?,?,?,?,?,?,?,?,?)";
    
        ps=conecta.getConexion().prepareStatement(sentenciaSQL);
        ps.setInt(1,0);
        ps.setString(2,txtnombre1.getText());
        ps.setString(3,txtdescripcion.getText());
        ps.setDouble(4,Double.parseDouble(txtprecio.getText()));
        ps.setString(5,txtcategoria.getText());
        ps.setString(6,txtproveedor.getText());
        ps.setString(7,txtvida.getText());
        ps.setString(8,txtstockmin.getText());
        ps.setString(9,txtstockmax2.getText());
        ps.setInt(10,1);
        ps.execute();
        
        JOptionPane.showMessageDialog(null,"DATOS INGRESADO CORRECTAMENTE");
        limpiar();
        con.close();
    } catch (SQLException ex) {
        JOptionPane.showMessageDialog(null,"ERROR"+ex.getMessage());
    }
    }
     
     public void leerproducto()
    {  
    try {
       conectarbd();
        sentenciaSQL="SELECT * FROM productos WHERE estado=1";
        ps=con.prepareStatement(sentenciaSQL);
        rs=ps.executeQuery();
        modelo=(DefaultTableModel)Tldatos.getModel();
        while(rs.next())
        {
            datosproducto[0]=(rs.getInt(1));
            datosproducto[1]=(rs.getString(2));
            datosproducto[2]=(rs.getString(3));
            datosproducto[3]=(rs.getString(4));
            datosproducto[4]=(rs.getString(5));
            datosproducto[5]=(rs.getString(6));
            datosproducto[6]=(rs.getString(7));
            datosproducto[7]=(rs.getString(8));
            datosproducto[8]=(rs.getString(9));
            datosproducto[9]=(rs.getString(10));
            modelo.addRow(datosproducto);
        }
        Tldatos.setModel(modelo);
        con.close();
    } catch (SQLException ex) {
        JOptionPane.showMessageDialog(null,"ERROR"+ex.getMessage());
    }       
    }  
     
    public void actualizarProducto() 
    {
        try {
            conectarbd();
            sentenciaSQL="UPDATE productos SET nombre_producto=?,descripcion=?,precio_unitario=?,catagoria_id=?,proveedor_id=?,new_field=?,stock_minimo=?,stock_maximo=? WHERE id_producto=?";   
            ps=conecta.getConexion().prepareStatement(sentenciaSQL);
            ps.setString(1,txtnombre1.getText());
            ps.setString(2,txtdescripcion.getText());
            ps.setDouble(3,Double.parseDouble(txtprecio.getText()));
            ps.setString(4,txtcategoria.getText());
            ps.setString(5,txtproveedor.getText());
            ps.setString(6,txtvida.getText());
            ps.setString(7,txtstockmin.getText());
            ps.setString(8,txtstockmax2.getText());
            ps.setString(9,txtcodigo1.getText());
            ps.execute();
            JOptionPane.showMessageDialog(null,"DATOS ACTUALIZADO CORRECTAMENTE");
            limpiar();
            con.close();
        } catch (SQLException ex) {
        JOptionPane.showMessageDialog(null,"ERROR"+ex.getMessage());
            }
    } 

     public void EliminarProducto() 
    {
        try {
        conectarbd();
        sentenciaSQL="UPDATE productos SET estado=? WHERE id_producto=?";   
        ps=conecta.getConexion().prepareStatement(sentenciaSQL);
        ps.setInt(1,2);
        ps.setString(2,txtcodigo1.getText());
        ps.execute();
        JOptionPane.showMessageDialog(null,"DATOS ELIMINADO CORRECTAMENTE");
        limpiar();
        con.close();
    } catch (SQLException ex) {
        JOptionPane.showMessageDialog(null,"ERROR"+ex.getMessage());
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

        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        txtprecio = new javax.swing.JTextField();
        jLabel16 = new javax.swing.JLabel();
        CamposObligatorios = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtdescripcion = new javax.swing.JTextArea();
        jLabel17 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        txtvida = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        txtcodigo1 = new javax.swing.JTextField();
        txtnombre1 = new javax.swing.JTextField();
        txtstockmin = new javax.swing.JTextField();
        txtstockmax2 = new javax.swing.JTextField();
        txtproveedor = new javax.swing.JTextField();
        cbocategoria = new javax.swing.JComboBox<>();
        cboproveedor = new javax.swing.JComboBox<>();
        txtcategoria = new javax.swing.JTextField();
        jPanel5 = new javax.swing.JPanel();
        btnlimpiar = new javax.swing.JButton();
        btncrear = new javax.swing.JButton();
        btnEliminar = new javax.swing.JButton();
        btnleer = new javax.swing.JButton();
        btnmodificar = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        Tldatos = new javax.swing.JTable();

        getContentPane().setLayout(null);

        jPanel2.setBackground(new java.awt.Color(0, 51, 51));

        jLabel2.setFont(new java.awt.Font("Century Schoolbook", 1, 44)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("REGISTRO DE PRODUCTOS");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(215, Short.MAX_VALUE)
                .addComponent(jLabel2)
                .addGap(197, 197, 197))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(21, Short.MAX_VALUE)
                .addComponent(jLabel2)
                .addContainerGap())
        );

        getContentPane().add(jPanel2);
        jPanel2.setBounds(0, 0, 1080, 86);

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));
        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 2, true), "FORMULARIO DATOS"));
        jPanel4.setLayout(null);

        jLabel11.setFont(new java.awt.Font("Century Schoolbook", 1, 12)); // NOI18N
        jLabel11.setText("Stock minimo");
        jLabel11.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        jPanel4.add(jLabel11);
        jLabel11.setBounds(280, 230, 100, 20);

        txtprecio.setFont(new java.awt.Font("Century Schoolbook", 1, 12)); // NOI18N
        jPanel4.add(txtprecio);
        txtprecio.setBounds(50, 260, 130, 24);

        jLabel16.setFont(new java.awt.Font("Century Schoolbook", 1, 12)); // NOI18N
        jLabel16.setText("Precio unitario");
        jPanel4.add(jLabel16);
        jLabel16.setBounds(50, 236, 100, 20);

        CamposObligatorios.setFont(new java.awt.Font("Dialog", 2, 12)); // NOI18N
        CamposObligatorios.setForeground(new java.awt.Color(255, 51, 51));
        jPanel4.add(CamposObligatorios);
        CamposObligatorios.setBounds(386, 228, 140, 20);

        txtdescripcion.setColumns(20);
        txtdescripcion.setFont(new java.awt.Font("Century Schoolbook", 1, 12)); // NOI18N
        txtdescripcion.setRows(5);
        jScrollPane1.setViewportView(txtdescripcion);

        jPanel4.add(jScrollPane1);
        jScrollPane1.setBounds(50, 146, 160, 80);

        jLabel17.setFont(new java.awt.Font("Century Schoolbook", 1, 12)); // NOI18N
        jLabel17.setText("Descripcion");
        jPanel4.add(jLabel17);
        jLabel17.setBounds(50, 126, 80, 20);

        jLabel12.setFont(new java.awt.Font("Century Schoolbook", 1, 12)); // NOI18N
        jLabel12.setText("Codigo");
        jLabel12.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        jPanel4.add(jLabel12);
        jLabel12.setBounds(50, 20, 60, 20);

        jLabel13.setFont(new java.awt.Font("Century Schoolbook", 1, 12)); // NOI18N
        jLabel13.setText("Categoria");
        jLabel13.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        jPanel4.add(jLabel13);
        jLabel13.setBounds(280, 30, 60, 20);

        txtvida.setFont(new java.awt.Font("Century Schoolbook", 1, 12)); // NOI18N
        jPanel4.add(txtvida);
        txtvida.setBounds(280, 150, 120, 21);

        jLabel14.setFont(new java.awt.Font("Century Schoolbook", 1, 12)); // NOI18N
        jLabel14.setText("Proveedor");
        jLabel14.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        jPanel4.add(jLabel14);
        jLabel14.setBounds(280, 80, 80, 20);

        jLabel15.setFont(new java.awt.Font("Century Schoolbook", 1, 12)); // NOI18N
        jLabel15.setText("Vida util");
        jLabel15.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        jPanel4.add(jLabel15);
        jLabel15.setBounds(280, 130, 100, 20);

        jLabel18.setFont(new java.awt.Font("Century Schoolbook", 1, 12)); // NOI18N
        jLabel18.setText("Stock maximo");
        jLabel18.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        jPanel4.add(jLabel18);
        jLabel18.setBounds(278, 182, 100, 20);

        jLabel19.setFont(new java.awt.Font("Century Schoolbook", 1, 12)); // NOI18N
        jLabel19.setText("Nombre");
        jLabel19.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        jPanel4.add(jLabel19);
        jLabel19.setBounds(50, 76, 60, 20);

        txtcodigo1.setFont(new java.awt.Font("Century Schoolbook", 1, 12)); // NOI18N
        txtcodigo1.setEnabled(false);
        jPanel4.add(txtcodigo1);
        txtcodigo1.setBounds(50, 46, 130, 24);

        txtnombre1.setFont(new java.awt.Font("Century Schoolbook", 1, 12)); // NOI18N
        jPanel4.add(txtnombre1);
        txtnombre1.setBounds(50, 100, 130, 24);

        txtstockmin.setFont(new java.awt.Font("Century Schoolbook", 1, 12)); // NOI18N
        jPanel4.add(txtstockmin);
        txtstockmin.setBounds(280, 250, 120, 21);

        txtstockmax2.setFont(new java.awt.Font("Century Schoolbook", 1, 12)); // NOI18N
        jPanel4.add(txtstockmax2);
        txtstockmax2.setBounds(280, 200, 120, 21);

        txtproveedor.setEnabled(false);
        jPanel4.add(txtproveedor);
        txtproveedor.setBounds(400, 80, 60, 20);

        cbocategoria.setFont(new java.awt.Font("Century Schoolbook", 1, 12)); // NOI18N
        cbocategoria.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cbocategoria.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbocategoriaActionPerformed(evt);
            }
        });
        jPanel4.add(cbocategoria);
        cbocategoria.setBounds(280, 50, 65, 21);

        cboproveedor.setFont(new java.awt.Font("Century Schoolbook", 1, 12)); // NOI18N
        cboproveedor.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cboproveedor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboproveedorActionPerformed(evt);
            }
        });
        jPanel4.add(cboproveedor);
        cboproveedor.setBounds(280, 100, 65, 21);

        txtcategoria.setEnabled(false);
        jPanel4.add(txtcategoria);
        txtcategoria.setBounds(400, 30, 50, 20);

        getContentPane().add(jPanel4);
        jPanel4.setBounds(0, 90, 470, 300);

        jPanel5.setBackground(new java.awt.Color(255, 255, 255));
        jPanel5.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 2, true));
        jPanel5.setLayout(null);

        btnlimpiar.setFont(new java.awt.Font("Century Schoolbook", 1, 12)); // NOI18N
        btnlimpiar.setText("Limpiar");
        btnlimpiar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnlimpiarActionPerformed(evt);
            }
        });
        jPanel5.add(btnlimpiar);
        btnlimpiar.setBounds(220, 60, 190, 30);

        btncrear.setFont(new java.awt.Font("Century Schoolbook", 1, 12)); // NOI18N
        btncrear.setText("Crear");
        btncrear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btncrearActionPerformed(evt);
            }
        });
        jPanel5.add(btncrear);
        btncrear.setBounds(20, 20, 190, 30);

        btnEliminar.setFont(new java.awt.Font("Century Schoolbook", 1, 12)); // NOI18N
        btnEliminar.setText("Eliminar");
        btnEliminar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEliminarActionPerformed(evt);
            }
        });
        jPanel5.add(btnEliminar);
        btnEliminar.setBounds(130, 100, 190, 30);

        btnleer.setFont(new java.awt.Font("Century Schoolbook", 1, 12)); // NOI18N
        btnleer.setText("Leer");
        btnleer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnleerActionPerformed(evt);
            }
        });
        jPanel5.add(btnleer);
        btnleer.setBounds(220, 20, 190, 30);

        btnmodificar.setFont(new java.awt.Font("Century Schoolbook", 1, 12)); // NOI18N
        btnmodificar.setText("Modificar");
        btnmodificar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnmodificarActionPerformed(evt);
            }
        });
        jPanel5.add(btnmodificar);
        btnmodificar.setBounds(20, 60, 190, 30);

        getContentPane().add(jPanel5);
        jPanel5.setBounds(0, 390, 470, 140);

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));
        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 2, true), "LISTA DE REGISTROS"));

        Tldatos.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(71, 84, 130)));
        Tldatos.setModel(new javax.swing.table.DefaultTableModel(
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
        Tldatos.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                TldatosMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(Tldatos);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 578, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 390, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 28, Short.MAX_VALUE))
        );

        getContentPane().add(jPanel3);
        jPanel3.setBounds(470, 90, 610, 440);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void cbocategoriaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbocategoriaActionPerformed
        // TODO add your handling code here:
        pruebaCategoria();
    }//GEN-LAST:event_cbocategoriaActionPerformed

    private void cboproveedorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboproveedorActionPerformed
        // TODO add your handling code here:
        pruebaProveedor();
    }//GEN-LAST:event_cboproveedorActionPerformed

    private void btnlimpiarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnlimpiarActionPerformed
        // TODO add your handling code here:
        limpiar();
        limpiarTabla();
    }//GEN-LAST:event_btnlimpiarActionPerformed

    private void btncrearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btncrearActionPerformed
        // TODO add your handling code here:
        if(!txtnombre1.getText().isEmpty() && !txtdescripcion.getText().isEmpty() && !cboproveedor.getSelectedItem().equals("Seleccione un proveedor") && !txtproveedor.getText().isEmpty()&&
            !cbocategoria.getSelectedItem().equals("Seleccione una categoria") && !txtcategoria.getText().isEmpty() && !txtprecio.getText().isEmpty() && !txtvida.getText().isEmpty() && !txtstockmin.getText().isEmpty() &&
            !txtstockmax2.getText().isEmpty())
        {
            crearproducto();
            limpiar();
        }else
        {
            JOptionPane.showMessageDialog(null,"COMPLETE TODOS LOS FORMULARIO");
            limpiar();
        }

    }//GEN-LAST:event_btncrearActionPerformed

    private void btnEliminarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminarActionPerformed
        // TODO add your handling code here:
        if(!txtnombre1.getText().isEmpty() && !txtdescripcion.getText().isEmpty() && !cboproveedor.getSelectedItem().equals("Seleccione un proveedor") && !txtproveedor.getText().isEmpty()&&
            !cbocategoria.getSelectedItem().equals("Seleccione una categoria") && !txtcategoria.getText().isEmpty() && !txtprecio.getText().isEmpty() && !txtvida.getText().isEmpty() && !txtstockmin.getText().isEmpty() &&
            !txtstockmax2.getText().isEmpty())
        {
            EliminarProducto();
        }else
        {
            JOptionPane.showMessageDialog(null,"ERROR PARA ELIMINAR");
        }

    }//GEN-LAST:event_btnEliminarActionPerformed

    private void btnleerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnleerActionPerformed
        // TODO add your handling code here:
        leerproducto();

    }//GEN-LAST:event_btnleerActionPerformed

    private void btnmodificarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnmodificarActionPerformed
        // TODO add your handling code here:
        if(!txtnombre1.getText().isEmpty() && !txtdescripcion.getText().isEmpty() && !cboproveedor.getSelectedItem().equals("Seleccione un proveedor") && !txtproveedor.getText().isEmpty()&&
            !cbocategoria.getSelectedItem().equals("Seleccione una categoria") && !txtcategoria.getText().isEmpty() && !txtprecio.getText().isEmpty() && !txtvida.getText().isEmpty() && !txtstockmin.getText().isEmpty() &&
            !txtstockmax2.getText().isEmpty())
        {
            actualizarProducto();
            limpiarTabla();
            limpiar();
        }else
        {
            JOptionPane.showMessageDialog(null,"NO PUEDE DEJAR UN FORMULARIO EN BLANCO");
        }

    }//GEN-LAST:event_btnmodificarActionPerformed

    private void TldatosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_TldatosMouseClicked
        int fila=Tldatos.getSelectedRow();
        txtcodigo1.setText(Tldatos.getValueAt(fila,0).toString());
        txtnombre1.setText(Tldatos.getValueAt(fila,1).toString());
        txtdescripcion.setText(Tldatos.getValueAt(fila,2).toString());
        txtprecio.setText(Tldatos.getValueAt(fila,3).toString());
        txtcategoria.setText(Tldatos.getValueAt(fila,4).toString());
        pruebaCategoriacombo();
        txtproveedor.setText(Tldatos.getValueAt(fila,5).toString());
        pruebaProveedorcombo();
        txtvida.setText(Tldatos.getValueAt(fila,6).toString());
        txtstockmin.setText(Tldatos.getValueAt(fila,7).toString());
        txtstockmax2.setText(Tldatos.getValueAt(fila,8).toString());
    }//GEN-LAST:event_TldatosMouseClicked

  /**
     * @param args the command line arguments
     */
                    

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel CamposObligatorios;
    private javax.swing.JTable Tldatos;
    private javax.swing.JButton btnEliminar;
    private javax.swing.JButton btncrear;
    private javax.swing.JButton btnleer;
    private javax.swing.JButton btnlimpiar;
    private javax.swing.JButton btnmodificar;
    private javax.swing.JComboBox<String> cbocategoria;
    private javax.swing.JComboBox<String> cboproveedor;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextField txtcategoria;
    private javax.swing.JTextField txtcodigo1;
    private javax.swing.JTextArea txtdescripcion;
    private javax.swing.JTextField txtnombre1;
    private javax.swing.JTextField txtprecio;
    private javax.swing.JTextField txtproveedor;
    private javax.swing.JTextField txtstockmax2;
    private javax.swing.JTextField txtstockmin;
    private javax.swing.JTextField txtvida;
    // End of variables declaration//GEN-END:variables
}
