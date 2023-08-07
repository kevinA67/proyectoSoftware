/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vista;

import Conexion.ConexionBD;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author User
 */
public class JInternalCompras extends javax.swing.JInternalFrame {
 String usuario, sentenciaSQL, cod = "", idArticulo, encontrado = "";
    Connection con = null;
    ConexionBD connec;
    PreparedStatement ps = null;
    ResultSet rs = null;
    DefaultTableModel modelo = new DefaultTableModel();
    String nombreTabla = "COMPRAS", nombreArticulo = "";
     private ArrayList<String> listaArticulos = new ArrayList<>();
    private ArrayList<Double> listaPrecios = new ArrayList<>();
    private ArrayList<Integer> listaCantidades = new ArrayList<>(); 
     private int idCompra = 0;
    /**
     * Creates new form JInternalCompras
     */
    public JInternalCompras() {
        initComponents();
         llenarCmbMetodoPago();
    llenarCmbProveedor();
    JComboArticulo.setEnabled(false);
    
     // Obtener la fecha actual
    Date fechaActual = new Date();
    // Asignar la fecha actual al componente JDateChooser
    jdcFechaOrdenCompra.setDate(fechaActual);

    // Inicializar las listas de artículos, precios y cantidades
    listaArticulos = new ArrayList<>();
    listaPrecios = new ArrayList<>();
    listaCantidades = new ArrayList<>();
    
      // Evento del ComboBox de proveedores
    JComboProvCompra.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            // Cuando se seleccione un proveedor, obtener los productos del proveedor seleccionado
            String proveedorSeleccionado = JComboProvCompra.getSelectedItem().toString();
            llenarCmbArticulo(proveedorSeleccionado);
            // Habilitar el ComboBox de artículos
            JComboArticulo.setEnabled(true);
            
               // Crear el DefaultTableModel
    modelo = new DefaultTableModel();
    
    // Configurar las columnas del modelo
    modelo.addColumn("CODIGO");
    modelo.addColumn("DESCRIPCION");
    modelo.addColumn("CANTIDAD");
    modelo.addColumn("PRECIO");
    modelo.addColumn("IMPORTE");

    // Asignar el modelo al JTable
    jTablaCompra.setModel(modelo);
        }
    });
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
    
    public void Compras() {
    try {
        int estado = 2; // 2 será activo, y 1 desactivo (eliminado)
        double isvPorcentaje = 0.15; // 15% (Impuesto Sobre Ventas)
        double montoTotal = 0.0;
        double isv = 0.0;

        SimpleDateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd");
        String fechaCompra = dFormat.format(jdcFechaOrdenCompra.getDate());
        String fechaCompraEntrega = dFormat.format(jdcFechaEnregaCompra1.getDate());
        String metodoPago = JComboPago.getSelectedItem().toString();
        String proveedor = JComboProvCompra.getSelectedItem().toString();
        String articulo = JComboArticulo.getSelectedItem().toString();
        double precio = Double.parseDouble(jTxtPrecioCompra.getText());
        int cantidad = Integer.parseInt(jTxtCantidadCompra.getText());

        // Obtener el ID del proveedor en base al nombre del proveedor seleccionado
        int idProveedor = obtenerIdProveedor(proveedor);
        
        // Obtener el ID del artículo en base al nombre del artículo seleccionado
        int idArticulo = obtenerIdArticulo(articulo);

        // Obtener el ID del usuario del campo TxtSolicitado
        int idUsuario = Integer.parseInt(TxtSolicitado.getText());

        ConexionBD();
        sentenciaSQL = "INSERT INTO compras (id_compra, fecha_compra, fecha_entrega, proveedor_id, direccion, modo_pago, descripcion, precio, cantidad, importe, subtotal, total_compra, usuario_id, estado) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        ps = con.prepareStatement(sentenciaSQL);

        // Reiniciar los acumulados para cada nueva compra
        double subtotalAcumulado = 0.0;
        double isvAcumulado = 0.0;

        // Iterar sobre los artículos seleccionados para la compra
        for (int i = 0; i < listaArticulos.size(); i++) {
            String articuloCompra = listaArticulos.get(i).toString();
            double precioCompra = Double.parseDouble(listaPrecios.get(i).toString());
            int cantidadCompra = Integer.parseInt(listaCantidades.get(i).toString());

            // Obtener el ID del artículo en base al nombre del artículo seleccionado
            int idArticuloCompra = obtenerIdArticulo(articuloCompra);

            // Calcular el importe del artículo sin ISV
            double importe = precioCompra * cantidadCompra;
            // Acumular el importe al subtotal total sin ISV
            subtotalAcumulado += importe;

            // Calcular el ISV del artículo
            double isvArticulo = importe * isvPorcentaje;
            // Acumular el ISV al total acumulado de ISV
            isvAcumulado += isvArticulo;

            // Calcular el monto total acumulado sumando el subtotal con el ISV
            montoTotal = subtotalAcumulado + isvAcumulado;

            // Agregar los valores a la base de datos para cada artículo
            ps.setInt(1, 0); // ID de la Compra (este campo se incrementará automáticamente en la base de datos)
            ps.setString(2, fechaCompra); // Fecha en la que se realizó la compra
            ps.setString(3, fechaCompraEntrega); // Fecha en la que se entregará la compra
            ps.setInt(4, idProveedor); // ID del proveedor
            ps.setString(5, TxtDireccion.getText()); // Direccion
            ps.setString(6, metodoPago); // Método de pago, si es con tarjeta o efectivo
            ps.setInt(7, idArticuloCompra); // ID del artículo
            ps.setDouble(8, precioCompra); // Precio
            ps.setInt(9, cantidadCompra); // Cantidad
            ps.setDouble(10, importe); // Importe del artículo (precio * cantidad sin ISV)
            ps.setDouble(11, subtotalAcumulado); // Subtotal acumulado de todos los artículos sin ISV
            ps.setDouble(12, montoTotal); // Monto total con ISV
               ps.setInt(13, idUsuario); // ID del usuario
            ps.setInt(14, estado); // Estado, o sea activo
         

            //agregarDetalleCompra(idCompra, idArticuloCompra, cantidad, montoTotal);
            ps.execute();
        }

        String accion = "CREACIÓN DE REGISTROS EN LA TABLA " + nombreTabla;
      //  bitacora(classPrin.idUsuario, accion);

        // Actualizar los campos de ISV, subtotal y total en la interfaz de usuario
        TxtISV.setText(String.valueOf(isvAcumulado));
        TxtSubtotal.setText(String.valueOf(subtotalAcumulado));
        TxtTotal.setText(String.valueOf(montoTotal));

        // Limpiar las listas después de guardar la compra
        listaArticulos.clear();
        listaPrecios.clear();
        listaCantidades.clear();

        // Limpiar los campos de ingreso del artículo
        JComboArticulo.setSelectedIndex(0);
        jTxtPrecioCompra.setText("");
        jTxtCantidadCompra.setText("");

        JOptionPane.showMessageDialog(null, "DATOS INGRESADOS CORRECTAMENTE");
        con.close();

    } catch (SQLException ex) {
        JOptionPane.showMessageDialog(null, "ERROR AL INSERTAR DATOS EN LA BASE DE DATOS: " + ex.getMessage());
    }
}
    
    public void llenarCmbProveedor() {
    try {
        JComboProvCompra.removeAllItems(); // Limpiar el ComboBox antes de llenarlo nuevamente

        ConexionBD();
        ps = con.prepareStatement("SELECT nombre_proveedor FROM proveedores WHERE estado=2");
        rs = ps.executeQuery();

        while (rs.next()) {
            String nombreProveedor = rs.getString("nombre_proveedor");
            JComboProvCompra.addItem(nombreProveedor);
        }

        con.close();

    } catch (SQLException ex) {
        JOptionPane.showMessageDialog(null, "Error al obtener los proveedores: " + ex.getMessage());
    }
    
}
        public void llenarCmbMetodoPago() {
        JComboPago.addItem("SELECCIONE MÉTODO DE PAGO");
        JComboPago.addItem("Tarjeta");
        JComboPago.addItem("Efectivo");}
    

    public void llenarCmbArticulo(String proveedorSeleccionado) {
    try {
        JComboArticulo.removeAllItems();

        ConexionBD();
        ps = con.prepareStatement("SELECT nombre_producto FROM productos WHERE proveedor_id IN (SELECT id_proveedor FROM proveedores WHERE nombre_proveedor = ?)");
        ps.setString(1, proveedorSeleccionado);
        rs = ps.executeQuery();

        while (rs.next()) {
            String nombreProducto = rs.getString("nombre_producto");
            JComboArticulo.addItem(nombreProducto);
        }

        con.close();

    } catch (SQLException ex) {
        JOptionPane.showMessageDialog(null, "Error al obtener los artículos: " + ex.getMessage());
    }
}

    
    
    // Método para obtener el ID del proveedor en base al nombre del proveedor
private int obtenerIdProveedor(String nombreProveedor) {
    int idProveedor = -1; // Valor predeterminado en caso de que no se encuentre el proveedor
    
    try {
        ConexionBD();
        String sentenciaSQL = "SELECT id_proveedor FROM proveedores WHERE nombre_proveedor = ?";
        PreparedStatement ps = con.prepareStatement(sentenciaSQL);
        ps.setString(1, nombreProveedor);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            idProveedor = rs.getInt("id_proveedor");
        }

        con.close();

    } catch (SQLException ex) {
        JOptionPane.showMessageDialog(null, "ERROR AL OBTENER EL ID DEL PROVEEDOR: " + ex.getMessage());
    }

    return idProveedor;
}

// Método para obtener el ID del artículo en base al nombre del artículo
private int obtenerIdArticulo(String nombreArticulo) {
    int idArticulo = -1; // Valor predeterminado en caso de que no se encuentre el artículo
    
    try {
        ConexionBD();
        String sentenciaSQL = "SELECT id_producto FROM productos WHERE nombre_producto = ?";
        PreparedStatement ps = con.prepareStatement(sentenciaSQL);
        ps.setString(1, nombreArticulo);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            idArticulo = rs.getInt("id_producto");
        }

        con.close();

    } catch (SQLException ex) {
        JOptionPane.showMessageDialog(null, "ERROR AL OBTENER EL ID DEL ARTÍCULO: " + ex.getMessage());
    }

    return idArticulo;
}

public void agregarDetalleCompra(int idCompra, String idArticulo, int cantidad, double subtotal) {
    try {
        int estado = 2; // 2 será activo, y 1 desactivo (eliminado)

        ConexionBD();
        sentenciaSQL = "INSERT INTO detalles_compra (compra_id, producto_id, cantidad, subtotal, estado) "
                + "VALUES (?, ?, ?, ?, ?)";
        ps = con.prepareStatement(sentenciaSQL);

        ps.setInt(1, idCompra); // ID de la compra a la que pertenece el detalle
        ps.setString(2, idArticulo); // ID del artículo (producto) comprado
        ps.setInt(3, cantidad); // Cantidad del artículo comprado
        ps.setDouble(4, subtotal); // Subtotal del artículo comprado (precio * cantidad)
        ps.setInt(5, estado); // Estado, o sea activo

        ps.execute();

        con.close();
    } catch (SQLException ex) {
        JOptionPane.showMessageDialog(null, "ERROR AL INTENTAR INGRESAR LOS DETALLES DE LA COMPRA:  " + ex.getMessage());
    }
}


private void agregarCompra() {
    ConexionBD();
    // Obtener datos de la compra desde la interfaz
    String proveedor = JComboProvCompra.getSelectedItem().toString();
    String metodoPago = JComboPago.getSelectedItem().toString();
    String solicitado = TxtSolicitado.getText();

    // Reiniciar los acumuladores para cada nueva compra
    double subtotalAcumulado = 0.0;
    double isvAcumulado = 0.0;

    // Iterar sobre los productos agregados a la tabla
    DefaultTableModel modelo = (DefaultTableModel) jTablaCompra.getModel();
    for (int i = 0; i < modelo.getRowCount(); i++) {
        String codigo = modelo.getValueAt(i, 0).toString();
        String descripcion = modelo.getValueAt(i, 1).toString();
        int cantidad = Integer.parseInt(modelo.getValueAt(i, 2).toString());
        double precio = Double.parseDouble(modelo.getValueAt(i, 3).toString());
        double importe = Double.parseDouble(modelo.getValueAt(i, 4).toString());
        
       agregarDetalleCompra(idCompra, descripcion, cantidad, importe);

        // Agregar el producto a la base de datos si es necesario (puedes hacer esto aquí si lo deseas)

        // Acumular los totales
        subtotalAcumulado += importe;
        double isvArticulo = importe * 0.15;
        isvAcumulado += isvArticulo;

        // Agregar el producto a la tabla visual
        agregarProductoTabla(codigo, descripcion, cantidad, precio, importe);
     
    }

    // Calcular el monto total
    double montoTotal = subtotalAcumulado + isvAcumulado;

    // Actualizar los campos de la interfaz
    TxtSubtotal.setText(String.valueOf(subtotalAcumulado));
    TxtISV.setText(String.valueOf(isvAcumulado));
    TxtTotal.setText(String.valueOf(montoTotal));

    // Limpiar las listas después de guardar la compra
    listaArticulos.clear();
    listaPrecios.clear();
    listaCantidades.clear();

    // Limpiar los campos de ingreso del artículo
    JComboArticulo.setSelectedIndex(0);
    jTxtPrecioCompra.setText("");
    jTxtCantidadCompra.setText("");

    JOptionPane.showMessageDialog(null, "DATOS INGRESADOS CORRECTAMENTE");
}

private void eliminarCompra() {
    int filaSeleccionada = jTablaCompra.getSelectedRow();

    if (filaSeleccionada != -1) {
        int idCompra = Integer.parseInt(jTablaCompra.getValueAt(filaSeleccionada, 0).toString());

        // Eliminar la compra de la tabla visual
        modelo.removeRow(filaSeleccionada);

       
    } else {
        JOptionPane.showMessageDialog(null, "Por favor, seleccione una compra para eliminar.", "Compra no seleccionada", JOptionPane.WARNING_MESSAGE);
    }
}
private void agregarProductoTabla(String id_compra, String descripcion, int cantidad, double precio, double importe) {
    DefaultTableModel modelo = (DefaultTableModel) jTablaCompra.getModel();
    modelo.addRow(new Object[]{id_compra, descripcion, cantidad, precio, importe});
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
        jLabel1 = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        jTxtTotal = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jCmbLibro = new javax.swing.JComboBox<>();
        jLabel13 = new javax.swing.JLabel();
        jTxtCodCliente = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jCmbCliente = new javax.swing.JComboBox<>();
        jTxtPrecio = new javax.swing.JTextField();
        jTxtCantidad = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        jbtnAgregar = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        jBtnEliminar1 = new javax.swing.JButton();
        jBtnFacturar = new javax.swing.JButton();
        jBtnNuevaFactura = new javax.swing.JButton();
        jPanel8 = new javax.swing.JPanel();
        jTxtArticulos = new javax.swing.JTextField();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jTxtTotalC = new javax.swing.JTextField();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTablaVenta = new javax.swing.JTable();
        jTxtISV = new javax.swing.JTextField();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jTxtSubtotal = new javax.swing.JTextField();
        jLabel20 = new javax.swing.JLabel();
        jTxtLibro = new javax.swing.JTextField();
        jLabel21 = new javax.swing.JLabel();
        jCmbMetodoPago = new javax.swing.JComboBox<>();
        jLabel22 = new javax.swing.JLabel();
        jTxtRecargo = new javax.swing.JTextField();
        jTxtStockMin = new javax.swing.JTextField();
        jTxtStockD = new javax.swing.JTextField();
        jTxtStockMax = new javax.swing.JTextField();
        jPanel7 = new javax.swing.JPanel();
        jLabel24 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        jPanel9 = new javax.swing.JPanel();
        jBtnEliminarCompra = new javax.swing.JButton();
        jBtnComprar = new javax.swing.JButton();
        jBtnNuevaCompra = new javax.swing.JButton();
        jPanel10 = new javax.swing.JPanel();
        TxtSubtotal = new javax.swing.JTextField();
        jLabel29 = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        TxtISV = new javax.swing.JTextField();
        TxtTotal = new javax.swing.JTextField();
        jLabel34 = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        jTablaCompra = new javax.swing.JTable();
        TxtOrden = new javax.swing.JTextField();
        jLabel36 = new javax.swing.JLabel();
        TxtDireccion = new javax.swing.JTextField();
        jLabel37 = new javax.swing.JLabel();
        jLabel38 = new javax.swing.JLabel();
        TxtSolicitado = new javax.swing.JTextField();
        jLabel40 = new javax.swing.JLabel();
        jTxtPrecioCompra = new javax.swing.JTextField();
        jLabel41 = new javax.swing.JLabel();
        jTxtCantidadCompra = new javax.swing.JTextField();
        jLabel39 = new javax.swing.JLabel();
        jLabel45 = new javax.swing.JLabel();
        JComboArticulo = new javax.swing.JComboBox<>();
        JComboPago = new javax.swing.JComboBox<>();
        jdcFechaOrdenCompra = new com.toedter.calendar.JDateChooser();
        JComboProvCompra = new javax.swing.JComboBox<>();
        jdcFechaEnregaCompra1 = new com.toedter.calendar.JDateChooser();
        JButonAgregar = new javax.swing.JButton();

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setLayout(null);

        jPanel2.setBackground(new java.awt.Color(255, 0, 0));

        jLabel1.setFont(new java.awt.Font("Arial Rounded MT Bold", 3, 48)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("ORDEN DE COMPRA");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(272, Short.MAX_VALUE)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 629, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(239, 239, 239))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(26, Short.MAX_VALUE))
        );

        jPanel1.add(jPanel2);
        jPanel2.setBounds(0, 0, 1140, 90);

        jPanel6.setBackground(new java.awt.Color(255, 255, 255));
        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(71, 84, 130), 2, true), "FORMULARIO DATOS", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 1, 12), new java.awt.Color(71, 84, 130))); // NOI18N
        jPanel6.setLayout(null);

        jLabel10.setText("MÉTODO PAGO");
        jPanel6.add(jLabel10);
        jLabel10.setBounds(480, 150, 110, 20);

        jTxtTotal.setEnabled(false);
        jPanel6.add(jTxtTotal);
        jTxtTotal.setBounds(860, 150, 170, 30);

        jLabel11.setText("CÓDIGO CLIENTE");
        jLabel11.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        jPanel6.add(jLabel11);
        jLabel11.setBounds(30, 60, 110, 20);

        jLabel12.setText("FECHA");
        jLabel12.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        jPanel6.add(jLabel12);
        jLabel12.setBounds(30, 20, 60, 20);

        jPanel6.add(jCmbLibro);
        jCmbLibro.setBounds(90, 110, 990, 30);

        jLabel13.setText("CLIENTE");
        jPanel6.add(jLabel13);
        jLabel13.setBounds(380, 60, 60, 20);
        jPanel6.add(jTxtCodCliente);
        jTxtCodCliente.setBounds(140, 60, 230, 30);

        jLabel14.setText("PRECIO");
        jPanel6.add(jLabel14);
        jLabel14.setBounds(30, 150, 70, 20);
        jPanel6.add(jSeparator1);
        jSeparator1.setBounds(30, 102, 1050, 10);

        jPanel6.add(jCmbCliente);
        jCmbCliente.setBounds(440, 60, 640, 30);

        jTxtPrecio.setEnabled(false);
        jPanel6.add(jTxtPrecio);
        jTxtPrecio.setBounds(90, 150, 150, 30);

        jTxtCantidad.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTxtCantidadActionPerformed(evt);
            }
        });
        jTxtCantidad.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTxtCantidadKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTxtCantidadKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTxtCantidadKeyTyped(evt);
            }
        });
        jPanel6.add(jTxtCantidad);
        jTxtCantidad.setBounds(320, 150, 150, 30);

        jLabel15.setText("CANTIDAD");
        jPanel6.add(jLabel15);
        jLabel15.setBounds(250, 150, 80, 20);

        jbtnAgregar.setBorderPainted(false);
        jbtnAgregar.setContentAreaFilled(false);
        jbtnAgregar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnAgregarActionPerformed(evt);
            }
        });
        jPanel6.add(jbtnAgregar);
        jbtnAgregar.setBounds(1040, 150, 40, 30);

        jPanel5.setBackground(new java.awt.Color(255, 255, 255));
        jPanel5.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(71, 84, 130), 2, true));
        jPanel5.setLayout(null);

        jBtnEliminar1.setBackground(new java.awt.Color(255, 255, 255));
        jBtnEliminar1.setText("ELIMINAR");
        jBtnEliminar1.setFocusPainted(false);
        jBtnEliminar1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBtnEliminar1ActionPerformed(evt);
            }
        });
        jPanel5.add(jBtnEliminar1);
        jBtnEliminar1.setBounds(20, 110, 208, 40);

        jBtnFacturar.setBackground(new java.awt.Color(255, 255, 255));
        jBtnFacturar.setText("FACTURAR");
        jBtnFacturar.setFocusPainted(false);
        jBtnFacturar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBtnFacturarActionPerformed(evt);
            }
        });
        jPanel5.add(jBtnFacturar);
        jBtnFacturar.setBounds(20, 60, 208, 40);

        jBtnNuevaFactura.setBackground(new java.awt.Color(255, 255, 255));
        jBtnNuevaFactura.setText("NUEVA FACTURA");
        jBtnNuevaFactura.setFocusPainted(false);
        jBtnNuevaFactura.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBtnNuevaFacturaActionPerformed(evt);
            }
        });
        jPanel5.add(jBtnNuevaFactura);
        jBtnNuevaFactura.setBounds(20, 10, 208, 40);

        jPanel6.add(jPanel5);
        jPanel5.setBounds(840, 190, 244, 160);

        jPanel8.setBackground(new java.awt.Color(255, 255, 255));
        jPanel8.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(71, 84, 130), 2, true));
        jPanel8.setLayout(null);

        jTxtArticulos.setDisabledTextColor(new java.awt.Color(102, 102, 102));
        jTxtArticulos.setEnabled(false);
        jPanel8.add(jTxtArticulos);
        jTxtArticulos.setBounds(20, 30, 200, 30);

        jLabel18.setText("ARTÍCULOS");
        jPanel8.add(jLabel18);
        jLabel18.setBounds(20, 10, 80, 20);

        jLabel19.setText("TOTAL COMPRA");
        jPanel8.add(jLabel19);
        jLabel19.setBounds(20, 70, 170, 20);

        jTxtTotalC.setDisabledTextColor(new java.awt.Color(102, 102, 102));
        jTxtTotalC.setEnabled(false);
        jPanel8.add(jTxtTotalC);
        jTxtTotalC.setBounds(20, 90, 200, 30);

        jPanel6.add(jPanel8);
        jPanel8.setBounds(840, 360, 244, 130);

        jTablaVenta.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "CODIGO", "ID LIBRO", "LIBRO", "PRECIO", "CANTIDAD", "SUBTOTAL", "ISV"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane3.setViewportView(jTablaVenta);

        jPanel6.add(jScrollPane3);
        jScrollPane3.setBounds(20, 190, 810, 250);

        jTxtISV.setDisabledTextColor(new java.awt.Color(102, 102, 102));
        jTxtISV.setEnabled(false);
        jPanel6.add(jTxtISV);
        jTxtISV.setBounds(470, 450, 140, 30);

        jLabel16.setText("ISV");
        jPanel6.add(jLabel16);
        jLabel16.setBounds(440, 450, 50, 20);

        jLabel17.setText("SUBTOTAL");
        jPanel6.add(jLabel17);
        jLabel17.setBounds(620, 450, 80, 20);

        jTxtSubtotal.setDisabledTextColor(new java.awt.Color(102, 102, 102));
        jTxtSubtotal.setEnabled(false);
        jPanel6.add(jTxtSubtotal);
        jTxtSubtotal.setBounds(690, 450, 140, 30);

        jLabel20.setText("LIBRO");
        jLabel20.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        jPanel6.add(jLabel20);
        jLabel20.setBounds(30, 110, 60, 20);

        jTxtLibro.setEnabled(false);
        jPanel6.add(jTxtLibro);
        jTxtLibro.setBounds(960, 20, 120, 30);

        jLabel21.setText("TOTAL");
        jPanel6.add(jLabel21);
        jLabel21.setBounds(810, 150, 60, 20);

        jCmbMetodoPago.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCmbMetodoPagoActionPerformed(evt);
            }
        });
        jPanel6.add(jCmbMetodoPago);
        jCmbMetodoPago.setBounds(580, 150, 210, 30);

        jLabel22.setText("RECARGO");
        jPanel6.add(jLabel22);
        jLabel22.setBounds(210, 450, 80, 20);

        jTxtRecargo.setDisabledTextColor(new java.awt.Color(102, 102, 102));
        jTxtRecargo.setEnabled(false);
        jTxtRecargo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTxtRecargoActionPerformed(evt);
            }
        });
        jPanel6.add(jTxtRecargo);
        jTxtRecargo.setBounds(290, 450, 140, 30);

        jTxtStockMin.setEnabled(false);
        jPanel6.add(jTxtStockMin);
        jTxtStockMin.setBounds(700, 20, 120, 30);

        jTxtStockD.setEnabled(false);
        jPanel6.add(jTxtStockD);
        jTxtStockD.setBounds(830, 20, 120, 30);

        jTxtStockMax.setEnabled(false);
        jPanel6.add(jTxtStockMax);
        jTxtStockMax.setBounds(570, 20, 120, 30);

        jPanel1.add(jPanel6);
        jPanel6.setBounds(0, 0, 0, 0);

        jPanel7.setBackground(new java.awt.Color(255, 255, 255));
        jPanel7.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(71, 84, 130), 2, true), "FORMULARIO DATOS", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 1, 12), new java.awt.Color(71, 84, 130))); // NOI18N
        jPanel7.setLayout(null);

        jLabel24.setText("PROVEEDOR");
        jLabel24.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        jPanel7.add(jLabel24);
        jLabel24.setBounds(40, 200, 110, 20);

        jLabel25.setText("FECHA ORDEN");
        jLabel25.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        jPanel7.add(jLabel25);
        jLabel25.setBounds(740, 20, 100, 20);

        jLabel26.setText("ARTICULO/SERVICIO");
        jPanel7.add(jLabel26);
        jLabel26.setBounds(10, 250, 160, 20);

        jPanel9.setBackground(new java.awt.Color(255, 255, 255));
        jPanel9.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(71, 84, 130), 2, true));
        jPanel9.setLayout(null);

        jBtnEliminarCompra.setBackground(new java.awt.Color(255, 255, 255));
        jBtnEliminarCompra.setText("ELIMINAR");
        jBtnEliminarCompra.setFocusPainted(false);
        jBtnEliminarCompra.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBtnEliminarCompraActionPerformed(evt);
            }
        });
        jPanel9.add(jBtnEliminarCompra);
        jBtnEliminarCompra.setBounds(280, 10, 140, 40);

        jBtnComprar.setBackground(new java.awt.Color(255, 255, 255));
        jBtnComprar.setText("ACEPTAR");
        jBtnComprar.setFocusPainted(false);
        jBtnComprar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBtnComprarActionPerformed(evt);
            }
        });
        jPanel9.add(jBtnComprar);
        jBtnComprar.setBounds(140, 10, 140, 40);

        jBtnNuevaCompra.setBackground(new java.awt.Color(255, 255, 255));
        jBtnNuevaCompra.setText("NUEVA COMPRA");
        jBtnNuevaCompra.setFocusPainted(false);
        jPanel9.add(jBtnNuevaCompra);
        jBtnNuevaCompra.setBounds(10, 10, 130, 40);

        jPanel7.add(jPanel9);
        jPanel9.setBounds(400, 450, 430, 60);

        jPanel10.setBackground(new java.awt.Color(255, 255, 255));
        jPanel10.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(71, 84, 130), 2, true));
        jPanel10.setLayout(null);

        TxtSubtotal.setDisabledTextColor(new java.awt.Color(102, 102, 102));
        TxtSubtotal.setEnabled(false);
        jPanel10.add(TxtSubtotal);
        TxtSubtotal.setBounds(80, 20, 160, 30);

        jLabel29.setText("SUB TOTAL");
        jPanel10.add(jLabel29);
        jLabel29.setBounds(10, 20, 80, 20);

        jLabel30.setText("ISV");
        jPanel10.add(jLabel30);
        jLabel30.setBounds(10, 60, 50, 20);

        TxtISV.setDisabledTextColor(new java.awt.Color(102, 102, 102));
        TxtISV.setEnabled(false);
        jPanel10.add(TxtISV);
        TxtISV.setBounds(80, 60, 160, 30);

        TxtTotal.setEnabled(false);
        jPanel10.add(TxtTotal);
        TxtTotal.setBounds(80, 100, 160, 30);

        jLabel34.setText("TOTAL");
        jPanel10.add(jLabel34);
        jLabel34.setBounds(10, 100, 60, 20);

        jPanel7.add(jPanel10);
        jPanel10.setBounds(864, 330, 250, 150);

        jTablaCompra.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "CODIGO", "DESCRIPCION", "CANTIDAD", "PRECIO", "IMPORTE"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTablaCompra.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTablaCompraMouseClicked(evt);
            }
        });
        jScrollPane4.setViewportView(jTablaCompra);

        jPanel7.add(jScrollPane4);
        jScrollPane4.setBounds(20, 330, 810, 110);
        jPanel7.add(TxtOrden);
        TxtOrden.setBounds(150, 20, 130, 30);

        jLabel36.setText("NO. DE ORDEN");
        jLabel36.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        jPanel7.add(jLabel36);
        jLabel36.setBounds(30, 30, 110, 20);
        jPanel7.add(TxtDireccion);
        TxtDireccion.setBounds(150, 110, 930, 30);

        jLabel37.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        jLabel37.setText("DIRECCION DE ENTREGA");
        jLabel37.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        jPanel7.add(jLabel37);
        jLabel37.setBounds(10, 110, 210, 20);

        jLabel38.setText("SOLICITADO POR");
        jLabel38.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        jPanel7.add(jLabel38);
        jLabel38.setBounds(10, 70, 110, 20);
        jPanel7.add(TxtSolicitado);
        TxtSolicitado.setBounds(150, 70, 570, 30);

        jLabel40.setText("PRECIO");
        jPanel7.add(jLabel40);
        jLabel40.setBounds(70, 290, 110, 20);
        jPanel7.add(jTxtPrecioCompra);
        jTxtPrecioCompra.setBounds(150, 280, 150, 30);

        jLabel41.setText("CANTIDAD");
        jPanel7.add(jLabel41);
        jLabel41.setBounds(330, 290, 110, 16);

        jTxtCantidadCompra.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTxtCantidadCompraActionPerformed(evt);
            }
        });
        jPanel7.add(jTxtCantidadCompra);
        jTxtCantidadCompra.setBounds(410, 280, 90, 30);

        jLabel39.setText("FECHA ENTREGA");
        jLabel39.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        jPanel7.add(jLabel39);
        jLabel39.setBounds(740, 70, 100, 20);

        jLabel45.setText("MODO DE PAGO");
        jLabel45.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        jPanel7.add(jLabel45);
        jLabel45.setBounds(20, 150, 110, 20);

        jPanel7.add(JComboArticulo);
        JComboArticulo.setBounds(150, 240, 570, 30);

        jPanel7.add(JComboPago);
        JComboPago.setBounds(150, 150, 150, 30);

        jdcFechaOrdenCompra.setEnabled(false);
        jPanel7.add(jdcFechaOrdenCompra);
        jdcFechaOrdenCompra.setBounds(850, 20, 230, 30);

        jPanel7.add(JComboProvCompra);
        JComboProvCompra.setBounds(150, 200, 570, 30);
        jPanel7.add(jdcFechaEnregaCompra1);
        jdcFechaEnregaCompra1.setBounds(850, 70, 230, 30);

        JButonAgregar.setText("Agregar");
        JButonAgregar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                JButonAgregarActionPerformed(evt);
            }
        });
        jPanel7.add(JButonAgregar);
        JButonAgregar.setBounds(1030, 280, 76, 32);

        jPanel1.add(jPanel7);
        jPanel7.setBounds(0, 100, 1130, 520);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 1138, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 631, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jTxtCantidadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTxtCantidadActionPerformed

    }//GEN-LAST:event_jTxtCantidadActionPerformed

    private void jTxtCantidadKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTxtCantidadKeyPressed

    }//GEN-LAST:event_jTxtCantidadKeyPressed

    private void jTxtCantidadKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTxtCantidadKeyReleased
        /*    try {
            int cantidad = Integer.parseInt(jTxtCantidad.getText());
            int stockDisponible = Integer.parseInt(jTxtStockD.getText());
            int stockMin = Integer.parseInt(jTxtStockMin.getText());
            if (cantidad > stockDisponible) {
                JOptionPane.showMessageDialog(null, "No puede comprar esa cantidad de productos por stock insuficiente",
                    "Advertencia", JOptionPane.WARNING_MESSAGE);
                jTxtTotal.setText("0");
            } else {
                double precio = Double.parseDouble(jTxtPrecio.getText());
                double total = cantidad * precio;
                jTxtTotal.setText(Double.toString(total));
            }

        } catch (NumberFormatException ex) {
            System.err.println("Error al convertir el valor: " + ex.getMessage());
        }*/
    }//GEN-LAST:event_jTxtCantidadKeyReleased

    private void jTxtCantidadKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTxtCantidadKeyTyped

    }//GEN-LAST:event_jTxtCantidadKeyTyped

    private void jbtnAgregarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnAgregarActionPerformed

        /*    int cantidad = Integer.parseInt(jTxtCantidad.getText());
        int stockDisponible = Integer.parseInt(jTxtStockD.getText());
        int stockMin = Integer.parseInt(jTxtStockMin.getText());
        String libro = (String) jCmbLibro.getSelectedItem();
        if ((stockDisponible - cantidad) < stockMin) {
            JOptionPane.showMessageDialog(null, "Ha llegado al límite del stock mínimo de " + libro + ". \nPronto se quedará sin stock suficiente. Reabastezca el inventario pronto.",
                "Stock Mínimo", JOptionPane.WARNING_MESSAGE);
        }

        tablaListaProductos();
        valoresTotales();
        bloquear();
        llenarCmbIdLibro();
        jBtnFacturar.setEnabled(true);
        jTxtPrecio.setText("0.0");
        jTxtCantidad.setText(null);
        jTxtTotal.setText("0.0");

        enumerarListaProductos();*/
    }//GEN-LAST:event_jbtnAgregarActionPerformed

    private void jBtnEliminar1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtnEliminar1ActionPerformed
        /*  int selectedRow = jTablaVenta.getSelectedRow();

        if (selectedRow != -1) {
            eliminarLibro();
            DefaultTableModel model = (DefaultTableModel) jTablaVenta.getModel();
            model.removeRow(selectedRow);
            modelo.fireTableDataChanged();
        }

        enumerarListaProductos(); */
    }//GEN-LAST:event_jBtnEliminar1ActionPerformed

    private void jBtnFacturarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtnFacturarActionPerformed

        /*  int respuesta = JOptionPane.showConfirmDialog(
            null, "¿Está seguro de realizar la factuación? \nUna vez realizada, no podrá agregar más productos.",
            "Realizar factura", JOptionPane.YES_NO_OPTION
        );

        // Verificar la respuesta del usuario
        if (respuesta == JOptionPane.YES_OPTION) {
            factura();
            detalleFactura();
            idVenta = obtenerUltimoIdVentas();
            crearFacturaPDF();
            jBtnNuevaFactura.setEnabled(true);
            limpiar();

        }*/
    }//GEN-LAST:event_jBtnFacturarActionPerformed

    private void jBtnNuevaFacturaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtnNuevaFacturaActionPerformed
        //    desbloquear();
    }//GEN-LAST:event_jBtnNuevaFacturaActionPerformed

    private void jCmbMetodoPagoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCmbMetodoPagoActionPerformed
        /* jCmbMetodoPago.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String selectedMethod = jCmbMetodoPago.getSelectedItem().toString();
                if (selectedMethod.equals("Tarjeta")) {
                    double recargo =
                    jTxtRecargo.setText("150");
                } else if (selectedMethod.equals("Efectivo")) {
                    jTxtRecargo.setText("0");
                }
            }
        });*/
    }//GEN-LAST:event_jCmbMetodoPagoActionPerformed

    private void jTxtRecargoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTxtRecargoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTxtRecargoActionPerformed

    private void jBtnEliminarCompraActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtnEliminarCompraActionPerformed

        // TODO add your handling code here:
        eliminarCompra();
    }//GEN-LAST:event_jBtnEliminarCompraActionPerformed

    private void jBtnComprarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtnComprarActionPerformed

        // TODO add your handling code here:
        int respuesta = JOptionPane.showConfirmDialog(
            null, "¿Está seguro de realizar la factuación? \nUna vez realizada, no podrá agregar más productos.",
            "Realizar factura", JOptionPane.YES_NO_OPTION
        );

        // Verificar la respuesta del usuario
        if (respuesta == JOptionPane.YES_OPTION) {
            Compras();
          

        }
    }//GEN-LAST:event_jBtnComprarActionPerformed

    private void jTablaCompraMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTablaCompraMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_jTablaCompraMouseClicked

    private void jTxtCantidadCompraActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTxtCantidadCompraActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTxtCantidadCompraActionPerformed

    private void JButonAgregarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_JButonAgregarActionPerformed

        // TODO add your handling code here:
        agregarCompra();

    }//GEN-LAST:event_JButonAgregarActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton JButonAgregar;
    private javax.swing.JComboBox<String> JComboArticulo;
    private javax.swing.JComboBox<String> JComboPago;
    private javax.swing.JComboBox<String> JComboProvCompra;
    private javax.swing.JTextField TxtDireccion;
    private javax.swing.JTextField TxtISV;
    private javax.swing.JTextField TxtOrden;
    private javax.swing.JTextField TxtSolicitado;
    private javax.swing.JTextField TxtSubtotal;
    private javax.swing.JTextField TxtTotal;
    private javax.swing.JButton jBtnComprar;
    private javax.swing.JButton jBtnEliminar1;
    private javax.swing.JButton jBtnEliminarCompra;
    private javax.swing.JButton jBtnFacturar;
    private javax.swing.JButton jBtnNuevaCompra;
    private javax.swing.JButton jBtnNuevaFactura;
    private javax.swing.JComboBox<String> jCmbCliente;
    private javax.swing.JComboBox<String> jCmbLibro;
    private javax.swing.JComboBox<String> jCmbMetodoPago;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel45;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JTable jTablaCompra;
    private javax.swing.JTable jTablaVenta;
    private javax.swing.JTextField jTxtArticulos;
    private javax.swing.JTextField jTxtCantidad;
    private javax.swing.JTextField jTxtCantidadCompra;
    private javax.swing.JTextField jTxtCodCliente;
    private javax.swing.JTextField jTxtISV;
    private javax.swing.JTextField jTxtLibro;
    private javax.swing.JTextField jTxtPrecio;
    private javax.swing.JTextField jTxtPrecioCompra;
    private javax.swing.JTextField jTxtRecargo;
    private javax.swing.JTextField jTxtStockD;
    private javax.swing.JTextField jTxtStockMax;
    private javax.swing.JTextField jTxtStockMin;
    private javax.swing.JTextField jTxtSubtotal;
    private javax.swing.JTextField jTxtTotal;
    private javax.swing.JTextField jTxtTotalC;
    private javax.swing.JButton jbtnAgregar;
    private com.toedter.calendar.JDateChooser jdcFechaEnregaCompra1;
    private com.toedter.calendar.JDateChooser jdcFechaOrdenCompra;
    // End of variables declaration//GEN-END:variables
}
