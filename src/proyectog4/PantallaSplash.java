
package proyectog4;
import javax.swing.JOptionPane;

public class PantallaSplash extends javax.swing.JFrame {

    public PantallaSplash() {
        initComponents();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        BarraLoading = new javax.swing.JProgressBar();
        MensajeCargando = new javax.swing.JLabel();
        PorcentajeCarga = new javax.swing.JLabel();
        BienvenidoMensaje = new javax.swing.JLabel();
        btnIngresar = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jbtnSalir = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setUndecorated(true);
        setResizable(false);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setLayout(null);

        BarraLoading.setForeground(new java.awt.Color(133, 198, 194));
        jPanel1.add(BarraLoading);
        BarraLoading.setBounds(0, 420, 714, 29);

        MensajeCargando.setFont(new java.awt.Font("Century Gothic", 0, 14)); // NOI18N
        MensajeCargando.setForeground(new java.awt.Color(102, 102, 102));
        MensajeCargando.setText("Cargando Mensaje...");
        jPanel1.add(MensajeCargando);
        MensajeCargando.setBounds(10, 400, 380, 19);

        PorcentajeCarga.setFont(new java.awt.Font("Century Gothic", 0, 14)); // NOI18N
        PorcentajeCarga.setForeground(new java.awt.Color(102, 102, 102));
        PorcentajeCarga.setText("0%");
        jPanel1.add(PorcentajeCarga);
        PorcentajeCarga.setBounds(670, 400, 40, 19);

        BienvenidoMensaje.setFont(new java.awt.Font("Century Gothic", 0, 14)); // NOI18N
        BienvenidoMensaje.setForeground(new java.awt.Color(102, 102, 102));
        BienvenidoMensaje.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        BienvenidoMensaje.setText("BIENVENIDO AL SISTEMA");
        jPanel1.add(BienvenidoMensaje);
        BienvenidoMensaje.setBounds(200, 340, 310, 19);

        btnIngresar.setFont(new java.awt.Font("Century Gothic", 0, 12)); // NOI18N
        btnIngresar.setText("Ingresar");
        btnIngresar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnIngresarActionPerformed(evt);
            }
        });
        jPanel1.add(btnIngresar);
        btnIngresar.setBounds(240, 370, 236, 26);

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/05- Logo The Book House.png"))); // NOI18N
        jPanel1.add(jLabel1);
        jLabel1.setBounds(150, -10, 430, 350);

        jbtnSalir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/cerca.png"))); // NOI18N
        jbtnSalir.setBorderPainted(false);
        jbtnSalir.setContentAreaFilled(false);
        jbtnSalir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnSalirActionPerformed(evt);
            }
        });
        jPanel1.add(jbtnSalir);
        jbtnSalir.setBounds(670, 0, 50, 34);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 449, Short.MAX_VALUE)
        );

        setSize(new java.awt.Dimension(714, 449));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void btnIngresarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnIngresarActionPerformed
        Login login = new Login();
        this.setVisible(false);
        login.setVisible(true);

    }//GEN-LAST:event_btnIngresarActionPerformed

    private void jbtnSalirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnSalirActionPerformed
        System.exit(0);
    }//GEN-LAST:event_jbtnSalirActionPerformed

    public static void main(String args[]) {
        
        PantallaSplash pantalla = new PantallaSplash();
        pantalla.setVisible(true);
        pantalla.BienvenidoMensaje.setVisible(false);
        pantalla.btnIngresar.setVisible(false);
        
       try{
            for (int i=0; i<=100; i++){
                Thread.sleep(100);
                pantalla.PorcentajeCarga.setText(i + "%");
            
                if(i==0)
                    pantalla.MensajeCargando.setText("Cargando interfaz..");
                if(i==20)
                    pantalla.MensajeCargando.setText("Conectando con el servidor...");
                if(i==40)
                    pantalla.MensajeCargando.setText("Detectando errores...");
                if(i==60)
                    pantalla.MensajeCargando.setText("Cargando registros...");
                if(i==80)
                    pantalla.MensajeCargando.setText("Cargando reportes...");
                    
                pantalla.BarraLoading.setValue(i);
                
                if(i==100){
                    pantalla.BarraLoading.setVisible(false);
                    pantalla.PorcentajeCarga.setVisible(false);
                    pantalla.MensajeCargando.setVisible(false);
                    pantalla.BienvenidoMensaje.setVisible(true);
                   pantalla.btnIngresar.setVisible(true);
                    
                }
            }
        }catch (Exception e){
               JOptionPane.showMessageDialog(null,e);
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JProgressBar BarraLoading;
    private javax.swing.JLabel BienvenidoMensaje;
    private javax.swing.JLabel MensajeCargando;
    private javax.swing.JLabel PorcentajeCarga;
    private javax.swing.JButton btnIngresar;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JButton jbtnSalir;
    // End of variables declaration//GEN-END:variables
}
