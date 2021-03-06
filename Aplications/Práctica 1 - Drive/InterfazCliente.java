import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import javax.swing.table.*;
import java.io.*;
import java.util.*;

public class InterfazCliente extends JFrame {
    private static final long serialVersionUID = 1L;
    public InterfazCliente() {
        setTitle("Google Drive Fake");
        setBounds(400, 100, 450, 600);
        setResizable(false);

        panelPrincipal = new JPanel();
        panelInferior = new JPanel();
        cliente = new JLabel("Cliente");
        ImageIcon img = new ImageIcon("drive_logo.png");
        JLabel et_imag = new JLabel();
        et_imag.setIcon(new ImageIcon(img.getImage().getScaledInstance(433,150,Image.SCALE_SMOOTH)));        
        elegirArchivo = new JButton("Archivo");
        elegirCarpeta = new JButton("Carpeta");
        subirArchivo = new JButton("Subir");
        pedirArchivo = new JButton("Mover de ArchivosServidor");
        tablaCliente = new JTable();
        misArchivos = new ArrayList<>();
        modelo = (DefaultTableModel) tablaCliente.getModel();
        modelo.addColumn("Archivos");

        elegirArchivo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                file = new JFileChooser();
                file.requestFocus();
                int r = file.showOpenDialog(InterfazCliente.this);
                if(r == JFileChooser.APPROVE_OPTION) {
                    File f = file.getSelectedFile();
                    misArchivos.add(new Archivo(f.getName(), f.length(), f.getAbsolutePath(), f));
                    modelo.addRow(new Object[] { f.getName() });
                }
            }
        });

        subirArchivo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                miCliente = new Cliente(HOST, PUERTO);
                miCliente.enviarArchivo(misArchivos, "./");
                modelo.setRowCount(0);
            }
        });

        elegirCarpeta.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser jf = new JFileChooser();
                jf.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                jf.requestFocus();
                int r = jf.showOpenDialog(InterfazCliente.this);
                if (r == JFileChooser.APPROVE_OPTION) {
                    File archivoSeleccionado = jf.getSelectedFile(); //Carpeta
                    modelo.addRow(new Object[]{"Carpeta: " + archivoSeleccionado.getName()}); //carpeta
                    modelo.setRowCount(0);
                    miCliente = new Cliente(HOST, PUERTO);
                    miCliente.carpetas(archivoSeleccionado, "", misArchivos);//carpeta
                }
            }
        });

        pedirArchivo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser jf = new JFileChooser("./ArchivosServidor/");
                jf.requestFocus();
                int r = jf.showOpenDialog(InterfazCliente.this);
                if (r == JFileChooser.APPROVE_OPTION) {
                    JFileChooser jf2 = new JFileChooser();
                    jf2.requestFocus();
                    jf2.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                    int r2 = jf2.showOpenDialog(InterfazCliente.this);
                    if(r2 == JFileChooser.APPROVE_OPTION) {
                        File f = jf.getSelectedFile();
                        File f2 = jf2.getSelectedFile();
                        Archivo a = new Archivo(f.getName(), f.length(), f.getAbsolutePath(), f);
                        miCliente = new Cliente(HOST, PUERTO);
                        miCliente.peticionArchivo(a, f2.getAbsolutePath());
                    }
                }
            }
        });

        panelInferior.setBackground(Color.white);
        panelInferior.add(elegirArchivo);
        panelInferior.add(elegirCarpeta);
        panelInferior.add(subirArchivo);
        panelInferior.add(pedirArchivo);
        panelPrincipal.setLayout(new BorderLayout());
        panelPrincipal.setBackground(Color.white);
        panelPrincipal.add(cliente, BorderLayout.NORTH);
        panelPrincipal.add(et_imag, BorderLayout.NORTH);
        panelPrincipal.add(new JScrollPane(tablaCliente), BorderLayout.CENTER);
        panelPrincipal.add(panelInferior, BorderLayout.SOUTH);
        add(panelPrincipal);
        
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    public static void main(String[] args) {
        new InterfazCliente();
    }

    private JPanel panelPrincipal;
    private JLabel cliente;
    private JTable tablaCliente;
    private DefaultTableModel modelo;
    private JPanel panelInferior;
    private JButton elegirArchivo;
    private JButton subirArchivo;
    private JButton elegirCarpeta;
    private JButton pedirArchivo;
    private JFileChooser file;
    private ArrayList <Archivo> misArchivos;
    private Cliente miCliente;
    private final int PUERTO = 9000;
    private final String HOST = "192.168.0.6";
}