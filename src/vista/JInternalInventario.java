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
import java.util.Date;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
/**
 *
 * @author Usuario
 */
public class JInternalInventario extends javax.swing.JInternalFrame {
static Connection c=null;
static Statement sentencia;
static ResultSet rs1;
Connection con=null;
ConexionBD conecta;
String sentenciaSQL;
ResultSet rs=null;
PreparedStatement ps=null;
DefaultTableModel modelo;
Object datosinventario[]=new Object[7];
Date fechasSQL;
Date fechaI;
long feI;
java.sql.Date fI;
Date fechasSQLE;
Date fechaE;
long feE;
java.sql.Date fE;
Date fechasSQLV;
Date fechaV;
long feV;
java.sql.Date fV;
    /**
     * Creates new form inventario1
     */
    public JInternalInventario() {
        initComponents();
         conectar();
        cboproducto.removeAllItems();
        cboproducto.addItem("Seleccione un producto");
         ArrayList<String> lista = new ArrayList<String>();
        lista =tProducto();
        for(int i = 0; i<lista.size(); i++){
            
            cboproducto.addItem(lista.get(i));
        }
    }
    
    public void conectarbd()
    {
        conecta=new ConexionBD("abarroteria");
        con=conecta.getConexion();
    }
    
     public void pruebaProducto()
    {
           String q = "SELECT id_producto,nombre_producto FROM productos WHERE nombre_producto='"+cboproducto.getSelectedItem()+"'";
        try {
            rs1 = sentencia.executeQuery(q);
        } catch (Exception e) {
            
        }
        try {
            while(rs1.next()){
                txtproducto.setText(rs1.getString("id_producto"));
              
            }
        } catch (Exception e) {
        }      
    }
    
    public void pruebaProductocombo()
    {
           String q = "SELECT id_producto,nombre_producto FROM productos WHERE id_producto='"+txtproducto.getText()+"'";
        try {
            rs1 = sentencia.executeQuery(q);
        } catch (Exception e) {
            
        }
        try {
            while(rs1.next()){
                cboproducto.setSelectedItem(rs1.getString("nombre_producto"));
              
            }
        } catch (Exception e) {
        }      
    }
     
     public void fechaI() {
        fechaI = jdcingreso.getDate();
        feI = fechaI.getTime();
        fI = new java.sql.Date(feI);
    }
     
     public void fechaE() {
        fechaE = jdcEla.getDate();
        feE = fechaE.getTime();
        fE = new java.sql.Date(feE);
    }
     
     public void fechaV() {
        fechaV = jdcVenc.getDate();
        feV = fechaV.getTime();
        fV = new java.sql.Date(feV);
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
    
    public static ArrayList<String> tProducto(){
        ArrayList<String> lista = new ArrayList<String>();
        
        String q = "SELECT id_producto,nombre_producto FROM productos WHERE estado=1";
        try {
            rs1 = sentencia.executeQuery(q);
        } catch (Exception e) {
            
        }
        try {
            while(rs1.next()){
                lista.add(rs1.getString("nombre_producto"));
              
            }
        } catch (Exception e) {
        }
        return lista;
    }
    
    public void limpiarTabla()
    {
        modelo.setRowCount(0);
    } 
    public void limpiar()
    {
        jdcingreso.setCalendar(null);
        jdcEla.setCalendar(null);
        jdcVenc.setCalendar(null);
        txtproducto.setText("");
        txtstock.setText("");
        
    }        
    
     public void crearInventario() 
    {       
        try {

        conectarbd();
        sentenciaSQL="INSERT INTO inventario(id_inventario,producto_id,stock_actual,fecha_ingreso,fecha_elaboracion,fecha_vencimiento,estado) VALUES(?,?,?,?,?,?,?)";
    
        ps=conecta.getConexion().prepareStatement(sentenciaSQL);
        ps.setInt(1,0);
        ps.setString(2,txtproducto.getText());
        ps.setInt(3,Integer.parseInt(txtstock.getText()));
        ps.setDate(4, fI);
        ps.setDate(5, fE);
        ps.setDate(6, fV);
        ps.setInt(7, 1);
        ps.execute();
        
        JOptionPane.showMessageDialog(null,"DATOS INGRESADO CORRECTAMENTE");
        limpiar();
        con.close();
    } catch (SQLException ex) {
        JOptionPane.showMessageDialog(null,"ERROR"+ex.getMessage());
    }
    }
     
    public void actualizarinventario() 
    {
        try {
            conectarbd();
            sentenciaSQL="UPDATE inventario SET producto_id=?,stock_actual=?,fecha_ingreso=?,fecha_elaboracion=?,fecha_vencimiento=? WHERE id_inventario=?";   
            ps=conecta.getConexion().prepareStatement(sentenciaSQL);
            ps.setString(1,txtproducto.getText());
            ps.setString(2,txtstock.getText());
            ps.setDate(3, fI);
            ps.setDate(4, fE);
            ps.setDate(5, fV);
            ps.setString(6,txtcodigo.getText());
            ps.execute();
            JOptionPane.showMessageDialog(null,"DATOS ACTUALIZADO CORRECTAMENTE");
            //limpiar();
            con.close();
        } catch (SQLException ex) {
        JOptionPane.showMessageDialog(null,"ERROR"+ex.getMessage());
            }
    }  
    
    public void Eliminarinventario() 
    {
        try {
            conectarbd();
            sentenciaSQL="UPDATE inventario SET estado=? WHERE id_inventario=?";   
            ps=conecta.getConexion().prepareStatement(sentenciaSQL);
            ps.setInt(1,2);
            ps.setString(2,txtcodigo.getText());
            ps.execute();
            JOptionPane.showMessageDialog(null,"DATOS ELIMINADO CORRECTAMENTE");
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
            sentenciaSQL="SELECT * FROM inventario WHERE estado=1";
            ps=con.prepareStatement(sentenciaSQL);
            rs=ps.executeQuery();
            modelo=(DefaultTableModel)Tldatos.getModel();
            while(rs.next())
            {
                datosinventario[0]=(rs.getInt(1));
                datosinventario[1]=(rs.getString(2));
                datosinventario[2]=(rs.getString(3));
                datosinventario[3]=(rs.getDate(4));
                datosinventario[4]=(rs.getDate(5));
                datosinventario[5]=(rs.getDate(6));
                datosinventario[6]=(rs.getString(7));
                modelo.addRow(datosinventario);
            }
            Tldatos.setModel(modelo);
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
        jlblImagen = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        cboproducto = new javax.swing.JComboBox<>();
        jLabel3 = new javax.swing.JLabel();
        txtcodigo = new javax.swing.JTextField();
        jdcVenc = new com.toedter.calendar.JDateChooser();
        jdcEla = new com.toedter.calendar.JDateChooser();
        jdcingreso = new com.toedter.calendar.JDateChooser();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        txtstock = new javax.swing.JTextField();
        txtproducto = new javax.swing.JTextField();
        jPanel3 = new javax.swing.JPanel();
        btncrear = new javax.swing.JButton();
        btnleer = new javax.swing.JButton();
        btnmodificar = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        btneliminar = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        Tldatos = new javax.swing.JTable();

        getContentPane().setLayout(null);

        jPanel2.setBackground(new java.awt.Color(0, 102, 153));

        jLabel2.setBackground(new java.awt.Color(0, 153, 153));
        jLabel2.setFont(new java.awt.Font("Century Schoolbook", 1, 44)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("REGISTRO DE INVENTARIO");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(209, Short.MAX_VALUE)
                .addComponent(jLabel2)
                .addGap(187, 187, 187)
                .addComponent(jlblImagen)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jlblImagen, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 7, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel2)
                .addContainerGap())
        );

        getContentPane().add(jPanel2);
        jPanel2.setBounds(0, 0, 1080, 86);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 3), "Formulario de datos", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Century Schoolbook", 1, 12))); // NOI18N
        jPanel1.setLayout(null);

        jLabel1.setFont(new java.awt.Font("Century Schoolbook", 1, 12)); // NOI18N
        jLabel1.setText("Codigo");
        jPanel1.add(jLabel1);
        jLabel1.setBounds(50, 50, 42, 15);

        cboproducto.setFont(new java.awt.Font("Century Schoolbook", 1, 12)); // NOI18N
        cboproducto.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cboproducto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboproductoActionPerformed(evt);
            }
        });
        jPanel1.add(cboproducto);
        cboproducto.setBounds(50, 120, 110, 21);

        jLabel3.setFont(new java.awt.Font("Century Schoolbook", 1, 12)); // NOI18N
        jLabel3.setText("Stock actual");
        jPanel1.add(jLabel3);
        jLabel3.setBounds(50, 160, 76, 15);

        txtcodigo.setFont(new java.awt.Font("Century Schoolbook", 1, 12)); // NOI18N
        jPanel1.add(txtcodigo);
        txtcodigo.setBounds(50, 70, 110, 21);
        jPanel1.add(jdcVenc);
        jdcVenc.setBounds(230, 180, 130, 20);
        jPanel1.add(jdcEla);
        jdcEla.setBounds(240, 120, 120, 20);
        jPanel1.add(jdcingreso);
        jdcingreso.setBounds(240, 70, 120, 20);

        jLabel4.setFont(new java.awt.Font("Century Schoolbook", 1, 12)); // NOI18N
        jLabel4.setText("Fecha de ingreso");
        jPanel1.add(jLabel4);
        jLabel4.setBounds(240, 50, 104, 15);

        jLabel5.setFont(new java.awt.Font("Century Schoolbook", 1, 12)); // NOI18N
        jLabel5.setText("Fecha de Elaboracion");
        jPanel1.add(jLabel5);
        jLabel5.setBounds(240, 100, 133, 15);

        jLabel6.setFont(new java.awt.Font("Century Schoolbook", 1, 12)); // NOI18N
        jLabel6.setText("Fecha de Vencimiento");
        jPanel1.add(jLabel6);
        jLabel6.setBounds(230, 160, 137, 15);

        jLabel7.setFont(new java.awt.Font("Century Schoolbook", 1, 12)); // NOI18N
        jLabel7.setText("Producto");
        jPanel1.add(jLabel7);
        jLabel7.setBounds(50, 100, 57, 15);

        txtstock.setFont(new java.awt.Font("Century Schoolbook", 1, 12)); // NOI18N
        jPanel1.add(txtstock);
        txtstock.setBounds(50, 180, 110, 21);

        txtproducto.setEnabled(false);
        jPanel1.add(txtproducto);
        txtproducto.setBounds(160, 100, 40, 20);

        getContentPane().add(jPanel1);
        jPanel1.setBounds(0, 90, 500, 250);

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));
        jPanel3.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 3));

        btncrear.setFont(new java.awt.Font("Century Schoolbook", 1, 12)); // NOI18N
        btncrear.setText("Crear");
        btncrear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btncrearActionPerformed(evt);
            }
        });

        btnleer.setFont(new java.awt.Font("Century Schoolbook", 1, 12)); // NOI18N
        btnleer.setText("Leer");
        btnleer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnleerActionPerformed(evt);
            }
        });

        btnmodificar.setFont(new java.awt.Font("Century Schoolbook", 1, 12)); // NOI18N
        btnmodificar.setText("Modificar");
        btnmodificar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnmodificarActionPerformed(evt);
            }
        });

        jButton4.setFont(new java.awt.Font("Century Schoolbook", 1, 12)); // NOI18N
        jButton4.setText("Limpiar");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        btneliminar.setFont(new java.awt.Font("Century Schoolbook", 1, 12)); // NOI18N
        btneliminar.setText("Eliminar");
        btneliminar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btneliminarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(jPanel3Layout.createSequentialGroup()
                            .addContainerGap()
                            .addComponent(btnmodificar, javax.swing.GroupLayout.PREFERRED_SIZE, 162, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(18, 18, 18)
                            .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 162, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel3Layout.createSequentialGroup()
                            .addGap(55, 55, 55)
                            .addComponent(btncrear, javax.swing.GroupLayout.PREFERRED_SIZE, 162, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(18, 18, 18)
                            .addComponent(btnleer, javax.swing.GroupLayout.PREFERRED_SIZE, 162, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(145, 145, 145)
                        .addComponent(btneliminar, javax.swing.GroupLayout.PREFERRED_SIZE, 162, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(97, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnleer)
                    .addComponent(btncrear))
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton4)
                    .addComponent(btnmodificar))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btneliminar)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        getContentPane().add(jPanel3);
        jPanel3.setBounds(0, 340, 500, 120);

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));
        jPanel4.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 3));

        Tldatos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "CODIGO", "PRODUCTO", "STOCK ANUAL", "FECHA DE INGRESO", "FECHA DE ELABORACION", "FECHA DE VENCIMIENTO", "ESTADO"
            }
        ));
        Tldatos.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                TldatosMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(Tldatos);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 526, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(18, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 327, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(26, Short.MAX_VALUE))
        );

        getContentPane().add(jPanel4);
        jPanel4.setBounds(500, 90, 570, 370);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void cboproductoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboproductoActionPerformed
        // TODO add your handling code here:
        pruebaProducto();
    }//GEN-LAST:event_cboproductoActionPerformed

    private void btncrearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btncrearActionPerformed
        // TODO add your handling code here:
        if (jdcingreso.getDate() != null && jdcEla.getDate() != null && jdcVenc.getDate() != null && !txtproducto.getText().isEmpty()
            && !txtstock.getText().isEmpty() && !cboproducto.getSelectedItem().equals("Seleccione un producto"))
        {
            fechaI();
            fechaE();
            fechaV();
            crearInventario();
        }else
        {

            JOptionPane.showMessageDialog(null,"COMPLETE TODO LOS FORMULARIO");
        }

    }//GEN-LAST:event_btncrearActionPerformed

    private void btnleerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnleerActionPerformed
        // TODO add your handling code here:
        leerproducto();
    }//GEN-LAST:event_btnleerActionPerformed

    private void btnmodificarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnmodificarActionPerformed
        // TODO add your handling code here:

        if (jdcingreso.getDate() != null && jdcEla.getDate() != null && jdcVenc.getDate() != null && !txtproducto.getText().isEmpty()
            && !txtstock.getText().isEmpty() && !cboproducto.getSelectedItem().equals("Seleccione un producto"))
        {
            fechaI();
            fechaE();
            fechaV();
            actualizarinventario();
        }else
        {

            JOptionPane.showMessageDialog(null,"NO PUEDE DEJAR NINGUN FORMULARIO VACIO");
        }

    }//GEN-LAST:event_btnmodificarActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        // TODO add your handling code here:
        limpiar();
        limpiarTabla();
    }//GEN-LAST:event_jButton4ActionPerformed

    private void btneliminarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btneliminarActionPerformed
        // TODO add your handling code here:
        if (jdcingreso.getDate() != null && jdcEla.getDate() != null && jdcVenc.getDate() != null && !txtproducto.getText().isEmpty()
            && !txtstock.getText().isEmpty() && !cboproducto.getSelectedItem().equals("Seleccione un producto"))
        {
            Eliminarinventario();
            limpiar();
            limpiarTabla();
        }else
        {
            JOptionPane.showMessageDialog(null,"ERROR");
        }

    }//GEN-LAST:event_btneliminarActionPerformed

    private void TldatosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_TldatosMouseClicked
        // TODO add your handling code here:
        int fila=Tldatos.getSelectedRow();
        txtcodigo.setText(Tldatos.getValueAt(fila,0).toString());
        txtproducto.setText(Tldatos.getValueAt(fila,1).toString());
        pruebaProductocombo();
        txtstock.setText(Tldatos.getValueAt(fila,2).toString());
        fechasSQL = (Date) (Tldatos.getValueAt(fila, 3));
        jdcingreso.setDate(fechasSQL);
        fechasSQLE = (Date) (Tldatos.getValueAt(fila, 4));
        jdcEla.setDate(fechasSQLE);
        fechasSQLV = (Date) (Tldatos.getValueAt(fila, 5));
        jdcVenc.setDate(fechasSQLV);
    }//GEN-LAST:event_TldatosMouseClicked
/**
     * @param args the command line arguments
     */
    

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTable Tldatos;
    private javax.swing.JButton btncrear;
    private javax.swing.JButton btneliminar;
    private javax.swing.JButton btnleer;
    private javax.swing.JButton btnmodificar;
    private javax.swing.JComboBox<String> cboproducto;
    private javax.swing.JButton jButton4;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private com.toedter.calendar.JDateChooser jdcEla;
    private com.toedter.calendar.JDateChooser jdcVenc;
    private com.toedter.calendar.JDateChooser jdcingreso;
    private javax.swing.JLabel jlblImagen;
    private javax.swing.JTextField txtcodigo;
    private javax.swing.JTextField txtproducto;
    private javax.swing.JTextField txtstock;
    // End of variables declaration//GEN-END:variables
}
