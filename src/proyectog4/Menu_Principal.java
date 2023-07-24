/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package proyectog4;

import Conexion.ConexionBD;
import desplazable.Desface;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.table.*;

/**
 *
 * @author User
 */
public class Menu_Principal extends javax.swing.JFrame {

    Desface desplace;

    String sentenciaSQL;
    Connection con = null;
    ConexionBD connec;
    PreparedStatement ps = null;
    ResultSet rs = null;

    public Menu_Principal() {
        initComponents();
        this.setExtendedState(MAXIMIZED_BOTH);
        desplace = new Desface();
        tamanioImg(jlblLogo, "src/Imagenes/Logo1.jpg");
        validacionPermisos();
        if (!classPrin.usuario.equals("ADMIN")) {
            bitacora(classPrin.idUsuario, "EL USUARIO INICIÓ SESIÓN");
        }
    }

    public static void tamanioImg(JLabel imagen, String ruta) {
        ImageIcon img = new ImageIcon(ruta);
        Icon icono = new ImageIcon(img.getImage().getScaledInstance(imagen.getWidth(), imagen.getHeight(), Image.SCALE_DEFAULT));
        imagen.setIcon(icono);
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

    //Para acceder al jDesktopPane desde otra pantalla.
    public JDesktopPane getDesktopPane() {
        return jDesktopPane1;
    }

    public void conectarBD() {
        connec = new ConexionBD("openfirekafz");
        con = connec.getConexion();
    }

    public void validacionPermisos() {
        conectarBD();
        sentenciaSQL = "SELECT * FROM usuario WHERE idUsuario = ?";
        try {
            ps = con.prepareStatement(sentenciaSQL);
            ps.setInt(1, classPrin.idUsuario);
            rs = ps.executeQuery();
            if (rs.next()) {
                classPrin.usuario = rs.getString("user");
                classPrin.tienePermisoRegistroCliente = (rs.getInt("permisoCliente") == 1);
                classPrin.tienePermisoRegistroAutor = (rs.getInt("permisoAutor") == 1);
                classPrin.tienePermisoRegistroEditorial = (rs.getInt("permisoEditorial") == 1);
                classPrin.tienePermisoRegistroLibro = (rs.getInt("permisoLibro") == 1);
                classPrin.tienePermisoRegistroVenta = (rs.getInt("permisoVenta") == 1);
                classPrin.tienePermisoReportes = (rs.getInt("permisoReporteria") == 1);
            }
            rs.close();
            ps.close();
            con.close();
            validacionAccesos();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage());
        }

    }

    public void validacionAccesos() {
        if (classPrin.tienePermisoRegistroCliente) {
            jbtnCliente.setEnabled(true);
        } else {
            jbtnCliente.setEnabled(false);
        }
        if (classPrin.tienePermisoRegistroAutor) {
            jbtnAutor.setEnabled(true);
        } else {
            jbtnAutor.setEnabled(false);
        }
        if (classPrin.tienePermisoRegistroEditorial) {
            jbtnEditorial.setEnabled(true);
        } else {
            jbtnEditorial.setEnabled(false);
        }
        if (classPrin.tienePermisoRegistroLibro) {
            jbtnLibro.setEnabled(true);
        } else {
            jbtnLibro.setEnabled(false);
        }
        if (classPrin.tienePermisoRegistroVenta) {
            jbtnVenta.setEnabled(true);
        } else {
            jbtnVenta.setEnabled(false);
        }
        if (classPrin.tienePermisoReportes) {
            jbtnReportes.setEnabled(true);
        } else {
            jbtnReportes.setEnabled(false);
        }

        if (classPrin.usuario.equals("ADMIN")) {
            jbtnUsuarios.setVisible(true);
        } else {
            jbtnUsuarios.setVisible(false);
        }

        System.out.print(classPrin.usuario);
    }

    public void pintarBotonEntered(JButton boton) {
        boton.setBackground(new Color(0, 102, 204));
    }

    public void pintarBotonExited(JButton boton) {
        boton.setBackground(new Color(51, 153, 255));
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
        jDesktopPane1 = new javax.swing.JDesktopPane();
        jPanel2 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jpMenu = new javax.swing.JPanel();
        jbtnCliente = new javax.swing.JButton();
        jbtnEditorial = new javax.swing.JButton();
        jbtnAutor = new javax.swing.JButton();
        jbtnVenta = new javax.swing.JButton();
        jbtnLibro = new javax.swing.JButton();
        jbtnUsuarios = new javax.swing.JButton();
        jbtnAyuda = new javax.swing.JButton();
        jbtnReportes = new javax.swing.JButton();
        jlblLogo = new javax.swing.JLabel();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem7 = new javax.swing.JMenuItem();
        jMenuItem6 = new javax.swing.JMenuItem();
        jMenu4 = new javax.swing.JMenu();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("THE BOOK HOUSE");

        jPanel2.setBackground(new java.awt.Color(237, 120, 74));

        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/menu.png"))); // NOI18N
        jButton1.setBorderPainted(false);
        jButton1.setContentAreaFilled(false);
        jButton1.setFocusPainted(false);
        jButton1.setFocusable(false);
        jButton1.setRequestFocusEnabled(false);
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 37, Short.MAX_VALUE)
                .addContainerGap())
        );

        jpMenu.setBackground(new java.awt.Color(0, 51, 102));
        jpMenu.setPreferredSize(new java.awt.Dimension(170, 369));

        jbtnCliente.setBackground(new java.awt.Color(51, 153, 255));
        jbtnCliente.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jbtnCliente.setForeground(new java.awt.Color(0, 0, 102));
        jbtnCliente.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/cliente_1.png"))); // NOI18N
        jbtnCliente.setText("      Clientes");
        jbtnCliente.setBorderPainted(false);
        jbtnCliente.setFocusPainted(false);
        jbtnCliente.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jbtnClienteMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jbtnClienteMouseExited(evt);
            }
        });
        jbtnCliente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnClienteActionPerformed(evt);
            }
        });

        jbtnEditorial.setBackground(new java.awt.Color(51, 153, 255));
        jbtnEditorial.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jbtnEditorial.setForeground(new java.awt.Color(0, 0, 102));
        jbtnEditorial.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/editorial.png"))); // NOI18N
        jbtnEditorial.setText("Editoriales");
        jbtnEditorial.setBorderPainted(false);
        jbtnEditorial.setFocusPainted(false);
        jbtnEditorial.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jbtnEditorialMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jbtnEditorialMouseExited(evt);
            }
        });
        jbtnEditorial.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnEditorialActionPerformed(evt);
            }
        });

        jbtnAutor.setBackground(new java.awt.Color(51, 153, 255));
        jbtnAutor.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jbtnAutor.setForeground(new java.awt.Color(0, 0, 102));
        jbtnAutor.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/autor.png"))); // NOI18N
        jbtnAutor.setText("     Autores");
        jbtnAutor.setBorderPainted(false);
        jbtnAutor.setFocusPainted(false);
        jbtnAutor.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jbtnAutorMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jbtnAutorMouseExited(evt);
            }
        });
        jbtnAutor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnAutorActionPerformed(evt);
            }
        });

        jbtnVenta.setBackground(new java.awt.Color(51, 153, 255));
        jbtnVenta.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jbtnVenta.setForeground(new java.awt.Color(0, 0, 102));
        jbtnVenta.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/ventas.png"))); // NOI18N
        jbtnVenta.setText("      Ventas");
        jbtnVenta.setBorderPainted(false);
        jbtnVenta.setFocusPainted(false);
        jbtnVenta.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jbtnVentaMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jbtnVentaMouseExited(evt);
            }
        });
        jbtnVenta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnVentaActionPerformed(evt);
            }
        });

        jbtnLibro.setBackground(new java.awt.Color(51, 153, 255));
        jbtnLibro.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jbtnLibro.setForeground(new java.awt.Color(0, 0, 102));
        jbtnLibro.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/libro.png"))); // NOI18N
        jbtnLibro.setText("      Libros");
        jbtnLibro.setBorderPainted(false);
        jbtnLibro.setFocusPainted(false);
        jbtnLibro.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jbtnLibroMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jbtnLibroMouseExited(evt);
            }
        });
        jbtnLibro.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnLibroActionPerformed(evt);
            }
        });

        jbtnUsuarios.setBackground(new java.awt.Color(51, 153, 255));
        jbtnUsuarios.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jbtnUsuarios.setForeground(new java.awt.Color(0, 0, 102));
        jbtnUsuarios.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/usuarios.png"))); // NOI18N
        jbtnUsuarios.setText("    Usuarios");
        jbtnUsuarios.setBorderPainted(false);
        jbtnUsuarios.setFocusPainted(false);
        jbtnUsuarios.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jbtnUsuariosMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jbtnUsuariosMouseExited(evt);
            }
        });
        jbtnUsuarios.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnUsuariosActionPerformed(evt);
            }
        });

        jbtnAyuda.setBackground(new java.awt.Color(51, 153, 255));
        jbtnAyuda.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jbtnAyuda.setForeground(new java.awt.Color(0, 0, 102));
        jbtnAyuda.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/ayuda.png"))); // NOI18N
        jbtnAyuda.setText("       Ayuda");
        jbtnAyuda.setBorderPainted(false);
        jbtnAyuda.setFocusPainted(false);
        jbtnAyuda.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jbtnAyudaMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jbtnAyudaMouseExited(evt);
            }
        });
        jbtnAyuda.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnAyudaActionPerformed(evt);
            }
        });

        jbtnReportes.setBackground(new java.awt.Color(51, 153, 255));
        jbtnReportes.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jbtnReportes.setForeground(new java.awt.Color(0, 0, 102));
        jbtnReportes.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/reporte.png"))); // NOI18N
        jbtnReportes.setText("     Reportes");
        jbtnReportes.setBorderPainted(false);
        jbtnReportes.setFocusPainted(false);
        jbtnReportes.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jbtnReportesMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jbtnReportesMouseExited(evt);
            }
        });
        jbtnReportes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnReportesActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jpMenuLayout = new javax.swing.GroupLayout(jpMenu);
        jpMenu.setLayout(jpMenuLayout);
        jpMenuLayout.setHorizontalGroup(
            jpMenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jbtnCliente, javax.swing.GroupLayout.DEFAULT_SIZE, 170, Short.MAX_VALUE)
            .addComponent(jbtnEditorial, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jbtnAutor, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jbtnLibro, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jbtnVenta, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jbtnUsuarios, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jbtnAyuda, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jbtnReportes, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jpMenuLayout.setVerticalGroup(
            jpMenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpMenuLayout.createSequentialGroup()
                .addComponent(jbtnCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jbtnEditorial, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jbtnAutor, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jbtnLibro, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jbtnVenta, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jbtnReportes, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 23, Short.MAX_VALUE)
                .addComponent(jbtnUsuarios, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jbtnAyuda, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jlblLogo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        jDesktopPane1.setLayer(jPanel2, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jDesktopPane1.setLayer(jpMenu, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jDesktopPane1.setLayer(jlblLogo, javax.swing.JLayeredPane.DEFAULT_LAYER);

        javax.swing.GroupLayout jDesktopPane1Layout = new javax.swing.GroupLayout(jDesktopPane1);
        jDesktopPane1.setLayout(jDesktopPane1Layout);
        jDesktopPane1Layout.setHorizontalGroup(
            jDesktopPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jDesktopPane1Layout.createSequentialGroup()
                .addComponent(jpMenu, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jlblLogo, javax.swing.GroupLayout.DEFAULT_SIZE, 618, Short.MAX_VALUE)
                .addGap(14, 14, 14))
        );
        jDesktopPane1Layout.setVerticalGroup(
            jDesktopPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jDesktopPane1Layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(jDesktopPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jDesktopPane1Layout.createSequentialGroup()
                        .addComponent(jpMenu, javax.swing.GroupLayout.DEFAULT_SIZE, 411, Short.MAX_VALUE)
                        .addGap(25, 25, 25))
                    .addGroup(jDesktopPane1Layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(jlblLogo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jDesktopPane1)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jDesktopPane1)
                .addGap(0, 0, 0))
        );

        jMenu1.setText("Acciones");

        jMenuItem7.setText("Cerrar sesión");
        jMenuItem7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem7ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem7);

        jMenuItem6.setText("Salir");
        jMenuItem6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem6ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem6);

        jMenuBar1.add(jMenu1);

        jMenu4.setText("Información");
        jMenu4.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jMenu4MouseClicked(evt);
            }
        });
        jMenuBar1.add(jMenu4);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jMenuItem6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem6ActionPerformed
        System.exit(0);
    }//GEN-LAST:event_jMenuItem6ActionPerformed

    private void jMenuItem7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem7ActionPerformed
        Login login = new Login();
        login.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_jMenuItem7ActionPerformed

    private void jMenu4MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jMenu4MouseClicked
        Informacion inf = new Informacion();
        jDesktopPane1.add(inf);
        Dimension desktopSize = jDesktopPane1.getSize();
        Dimension FrameSize = inf.getSize();
        inf.setLocation((desktopSize.width - FrameSize.width) / 2, (desktopSize.height - FrameSize.height) / 2);
        inf.show();
    }//GEN-LAST:event_jMenu4MouseClicked

    private void jbtnReportesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnReportesActionPerformed
//        Reportes rp = new Reportes();
//        jDesktopPane1.add(rp);
//        Dimension desktopSize = jDesktopPane1.getSize();
//        Dimension FrameSize = rp.getSize();
//        rp.setLocation((desktopSize.width - FrameSize.width) / 2, (desktopSize.height - FrameSize.height) / 2);
//        rp.show();
    }//GEN-LAST:event_jbtnReportesActionPerformed

    private void jbtnReportesMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jbtnReportesMouseExited
        pintarBotonExited(jbtnReportes);
    }//GEN-LAST:event_jbtnReportesMouseExited

    private void jbtnReportesMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jbtnReportesMouseEntered
        pintarBotonEntered(jbtnReportes);
    }//GEN-LAST:event_jbtnReportesMouseEntered

    private void jbtnAyudaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnAyudaActionPerformed
        File pdfFile = new File("src/ManualUsuario/ManualUsuario-openfirekafz.pdf");

        // Abre el archivo con la aplicación predeterminada del sistema
        if (pdfFile.exists()) {
            try {
                Desktop.getDesktop().open(pdfFile);
            } catch (IOException ex) {
                Logger.getLogger(Menu_Principal.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }//GEN-LAST:event_jbtnAyudaActionPerformed

    private void jbtnAyudaMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jbtnAyudaMouseExited
        pintarBotonExited(jbtnAyuda);
    }//GEN-LAST:event_jbtnAyudaMouseExited

    private void jbtnAyudaMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jbtnAyudaMouseEntered
        pintarBotonEntered(jbtnAyuda);
    }//GEN-LAST:event_jbtnAyudaMouseEntered

    private void jbtnUsuariosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnUsuariosActionPerformed
        RegistroUsuarios usuarios = new RegistroUsuarios();
        jDesktopPane1.add(usuarios);
        Dimension desktopSize = jDesktopPane1.getSize();
        Dimension FrameSize = usuarios.getSize();
        usuarios.setLocation((desktopSize.width - FrameSize.width) / 2, (desktopSize.height - FrameSize.height) / 2);
        usuarios.show();
    }//GEN-LAST:event_jbtnUsuariosActionPerformed

    private void jbtnUsuariosMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jbtnUsuariosMouseExited
        pintarBotonExited(jbtnUsuarios);
    }//GEN-LAST:event_jbtnUsuariosMouseExited

    private void jbtnUsuariosMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jbtnUsuariosMouseEntered
        pintarBotonEntered(jbtnUsuarios);
    }//GEN-LAST:event_jbtnUsuariosMouseEntered

    private void jbtnLibroActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnLibroActionPerformed
        RegistroLibros rl = new RegistroLibros();
        jDesktopPane1.add(rl);
        Dimension desktopSize = jDesktopPane1.getSize();
        Dimension FrameSize = rl.getSize();
        rl.setLocation((desktopSize.width - FrameSize.width) / 2, (desktopSize.height - FrameSize.height) / 2);
        rl.show();

    }//GEN-LAST:event_jbtnLibroActionPerformed

    private void jbtnLibroMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jbtnLibroMouseExited
        pintarBotonExited(jbtnLibro);
    }//GEN-LAST:event_jbtnLibroMouseExited

    private void jbtnLibroMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jbtnLibroMouseEntered
        pintarBotonEntered(jbtnLibro);
    }//GEN-LAST:event_jbtnLibroMouseEntered

    private void jbtnVentaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnVentaActionPerformed
        RegistroVentas ventas = new RegistroVentas();
        jDesktopPane1.add(ventas);
        Dimension desktopSize = jDesktopPane1.getSize();
        Dimension FrameSize = ventas.getSize();
        ventas.setLocation((desktopSize.width - FrameSize.width) / 2, (desktopSize.height - FrameSize.height) / 2);
        ventas.show();
    }//GEN-LAST:event_jbtnVentaActionPerformed

    private void jbtnVentaMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jbtnVentaMouseExited
        pintarBotonExited(jbtnVenta);
    }//GEN-LAST:event_jbtnVentaMouseExited

    private void jbtnVentaMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jbtnVentaMouseEntered
        pintarBotonEntered(jbtnVenta);
    }//GEN-LAST:event_jbtnVentaMouseEntered

    private void jbtnAutorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnAutorActionPerformed
        RegistroAutor ra = new RegistroAutor();
        jDesktopPane1.add(ra);
        Dimension desktopSize = jDesktopPane1.getSize();
        Dimension FrameSize = ra.getSize();
        ra.setLocation((desktopSize.width - FrameSize.width) / 2, (desktopSize.height - FrameSize.height) / 2);
        ra.show();
    }//GEN-LAST:event_jbtnAutorActionPerformed

    private void jbtnAutorMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jbtnAutorMouseExited
        pintarBotonExited(jbtnAutor);
    }//GEN-LAST:event_jbtnAutorMouseExited

    private void jbtnAutorMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jbtnAutorMouseEntered
        pintarBotonEntered(jbtnAutor);
    }//GEN-LAST:event_jbtnAutorMouseEntered

    private void jbtnEditorialActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnEditorialActionPerformed
        RegistroEditorial re = new RegistroEditorial();
        jDesktopPane1.add(re);
        Dimension desktopSize = jDesktopPane1.getSize();
        Dimension FrameSize = re.getSize();
        re.setLocation((desktopSize.width - FrameSize.width) / 2, (desktopSize.height - FrameSize.height) / 2);
        re.show();
    }//GEN-LAST:event_jbtnEditorialActionPerformed

    private void jbtnEditorialMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jbtnEditorialMouseExited
        pintarBotonExited(jbtnEditorial);
    }//GEN-LAST:event_jbtnEditorialMouseExited

    private void jbtnEditorialMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jbtnEditorialMouseEntered
        pintarBotonEntered(jbtnEditorial);
    }//GEN-LAST:event_jbtnEditorialMouseEntered

    private void jbtnClienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnClienteActionPerformed
        RegistroClientes rc = new RegistroClientes();
        jDesktopPane1.add(rc);
        Dimension desktopSize = jDesktopPane1.getSize();
        Dimension FrameSize = rc.getSize();
        rc.setLocation((desktopSize.width - FrameSize.width) / 2, (desktopSize.height - FrameSize.height) / 2);
        rc.show();
    }//GEN-LAST:event_jbtnClienteActionPerformed

    private void jbtnClienteMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jbtnClienteMouseExited
        pintarBotonExited(jbtnCliente);
    }//GEN-LAST:event_jbtnClienteMouseExited

    private void jbtnClienteMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jbtnClienteMouseEntered
        pintarBotonEntered(jbtnCliente);
    }//GEN-LAST:event_jbtnClienteMouseEntered

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        if (jpMenu.getX() == 0) {
            desplace.desplazarIzquierda(jpMenu, jpMenu.getX(), -170, 10, 10);
        } else if (jpMenu.getX() == -170) {
            desplace.desplazarDerecha(jpMenu, jpMenu.getX(), 0, 10, 10);
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Menu_Principal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Menu_Principal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Menu_Principal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Menu_Principal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Menu_Principal().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    public static javax.swing.JDesktopPane jDesktopPane1;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu4;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem6;
    private javax.swing.JMenuItem jMenuItem7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JButton jbtnAutor;
    private javax.swing.JButton jbtnAyuda;
    private javax.swing.JButton jbtnCliente;
    private javax.swing.JButton jbtnEditorial;
    private javax.swing.JButton jbtnLibro;
    private javax.swing.JButton jbtnReportes;
    private javax.swing.JButton jbtnUsuarios;
    private javax.swing.JButton jbtnVenta;
    private javax.swing.JLabel jlblLogo;
    private javax.swing.JPanel jpMenu;
    // End of variables declaration//GEN-END:variables
}
