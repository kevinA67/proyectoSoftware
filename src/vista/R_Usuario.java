/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package vista;

/**
 *
 * @author everg
 */
public class R_Usuario extends javax.swing.JPanel {

    /**
     * Creates new form R_Usuario
     */
    public R_Usuario() {
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblidempleado = new javax.swing.JLabel();
        txtIdempleado = new javax.swing.JTextField();
        lblusuario = new javax.swing.JLabel();
        txtUsuario = new javax.swing.JTextField();
        lblpassword = new javax.swing.JLabel();
        txtPassword = new javax.swing.JTextField();
        jSeparator1 = new javax.swing.JSeparator();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jSeparator2 = new javax.swing.JSeparator();
        jLabel3 = new javax.swing.JLabel();
        cbadmin = new javax.swing.JCheckBox();
        cbventas = new javax.swing.JCheckBox();
        cbinevtario = new javax.swing.JCheckBox();
        jCheckBox4 = new javax.swing.JCheckBox();
        btnCrear = new javax.swing.JButton();
        btnActualizar = new javax.swing.JButton();
        btnEliminar = new javax.swing.JButton();
        btnLimpiar = new javax.swing.JButton();

        setBackground(new java.awt.Color(255, 255, 255));
        setBorder(javax.swing.BorderFactory.createTitledBorder("Usuarios"));
        setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblidempleado.setText("Id Empleado");
        add(lblidempleado, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 90, -1, -1));

        txtIdempleado.setToolTipText("");
        add(txtIdempleado, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 110, 170, -1));

        lblusuario.setText("Usuario");
        add(lblusuario, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 140, -1, -1));
        add(txtUsuario, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 160, 170, -1));

        lblpassword.setText("Contraseña");
        add(lblpassword, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 190, -1, -1));
        add(txtPassword, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 210, 170, -1));

        jSeparator1.setForeground(new java.awt.Color(0, 0, 0));
        jSeparator1.setOrientation(javax.swing.SwingConstants.VERTICAL);
        add(jSeparator1, new org.netbeans.lib.awtextra.AbsoluteConstraints(620, 100, 10, 150));

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "Usuario", "Contraseña", "Id Empleado", "Rol", "Estado"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(jTable1);

        add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 257, 1230, 230));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("REGISTRO DE USUARIOS");
        add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 20, 1250, -1));

        jLabel2.setText("Datos de iniciar sesion");
        add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 60, -1, -1));

        jSeparator2.setForeground(new java.awt.Color(0, 0, 0));
        jSeparator2.setOrientation(javax.swing.SwingConstants.VERTICAL);
        add(jSeparator2, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 100, 10, 150));

        jLabel3.setText("Permisos de Control");
        add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 60, -1, -1));

        cbadmin.setText("Administrador");
        add(cbadmin, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 120, -1, -1));

        cbventas.setText("Ventas");
        add(cbventas, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 160, -1, -1));

        cbinevtario.setText("Inventario");
        add(cbinevtario, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 210, -1, -1));

        jCheckBox4.setText("Reporteria");
        add(jCheckBox4, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 120, -1, -1));

        btnCrear.setText("CREAR");
        add(btnCrear, new org.netbeans.lib.awtextra.AbsoluteConstraints(650, 150, 100, -1));

        btnActualizar.setText("ACTUALIZAR");
        add(btnActualizar, new org.netbeans.lib.awtextra.AbsoluteConstraints(780, 150, -1, -1));

        btnEliminar.setText("ELIMINAR");
        add(btnEliminar, new org.netbeans.lib.awtextra.AbsoluteConstraints(920, 150, 100, -1));

        btnLimpiar.setText("LIMPIAR CAMPOS");
        add(btnLimpiar, new org.netbeans.lib.awtextra.AbsoluteConstraints(1060, 150, -1, -1));
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    public javax.swing.JButton btnActualizar;
    public javax.swing.JButton btnCrear;
    public javax.swing.JButton btnEliminar;
    public javax.swing.JButton btnLimpiar;
    private javax.swing.JCheckBox cbadmin;
    private javax.swing.JCheckBox cbinevtario;
    private javax.swing.JCheckBox cbventas;
    private javax.swing.JCheckBox jCheckBox4;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JTable jTable1;
    private javax.swing.JLabel lblidempleado;
    private javax.swing.JLabel lblpassword;
    private javax.swing.JLabel lblusuario;
    public javax.swing.JTextField txtIdempleado;
    public javax.swing.JTextField txtPassword;
    public javax.swing.JTextField txtUsuario;
    // End of variables declaration//GEN-END:variables
}