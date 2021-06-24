import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Interfaz extends JFrame {
    private static final long serialVersionUID = 2L;

    public Interfaz(String host, int puerto, String nombre) {
        // -----------------Recibiendo Parametros------------------//
        this.host = host;
        this.puerto = puerto;
        this.nombre = nombre;
        // ----------------Creando Interfaz-------------------------//
        setBounds(325, 100, 800, 500);
        setTitle("Practica 3: " + nombre);
        setResizable(false);

        panelPrincipal = new JPanel();
        panelCentral = new JPanel();
        panelInferior = new JPanel();
       // panelEmojis = new JPanel();
        panelFunciones = new JPanel();
        panelUsuarios = new JPanel();
        panelCombo = new JPanel();
        editor = new JEditorPane("text/html", null);
        editor.setEditable(false);
        areaMensaje = new JTextArea();
        areaMensaje.setLineWrap(true);
        //botonesEmojis = new JButton[textoBotonesEmojis.length];
        enviar = new JButton("Enviar");
        archivo = new JButton("Archivo");
        desconectar = new JButton("Desconectar");
        //seleccion = new JButton("Seleccionar");
        usuariosConectados = new JLabel("    Usuarios Conectados   ");
        //escuchaEmojis = new ManejoEmojis();
        usuarioConectado = new JComboBox<>();
        usuarioConectado.addItem("Todos");

        desconectar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                miCliente.EliminarConexion(nombre);
                System.exit(0);
            }
        });
        
        enviar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String destino = (String) usuarioConectado.getSelectedItem();
                int tipo = 4;
                if(destino == "Todos"){
                    System.out.println("Selección "+destino);
                    destino = "";
                    tipo = 1;
                }
                else{
                    destino = (String) usuarioConectado.getSelectedItem();
                    tipo = 4;
                }
                    
                miCliente.enviar(new Mensaje("[" + nombre + "]: " + areaMensaje.getText(), nombre, destino, tipo));
                areaMensaje.setText("");
            }
        });

        archivo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String destino = (String) usuarioConectado.getSelectedItem();
                int tipo = 4;
                if(destino == "Todos"){
                    System.out.println("Selección "+destino);
                    destino = "";
                    tipo = 1;
                }
                else{
                    destino = (String) usuarioConectado.getSelectedItem();
                    tipo = 4;
                }
                JFileChooser jf = new JFileChooser();
                jf.requestFocus();
                int r = jf.showOpenDialog(Interfaz.this);
                if (r == JFileChooser.APPROVE_OPTION) {
                    //Archivo
                    miCliente.enviarArchivo(jf.getSelectedFile(),destino);
                    //Solo es el aviso
                    String mensaje = "El usuario [" + nombre + "] ha compartido un archivo";
                    miCliente.enviar(new Mensaje(mensaje, nombre, destino, tipo));
                }
            }
        });

 

        panelPrincipal.setLayout(new BorderLayout(5, 5));
        panelCentral.setLayout(new BorderLayout(5, 5));
        panelInferior.setLayout(new BoxLayout(this.panelInferior, BoxLayout.Y_AXIS));
        panelFunciones.setLayout(new BoxLayout(this.panelFunciones, BoxLayout.X_AXIS));
        panelUsuarios.setLayout(new BorderLayout(5, 5));

        //colocarBotones();
        addWindowListener(new CorreCliente());
        panelCombo.add(usuarioConectado);
        panelUsuarios.add(usuariosConectados, BorderLayout.NORTH);
        usuariosConectados.setAlignmentX(SwingConstants.CENTER);
        panelUsuarios.add(panelCombo, BorderLayout.CENTER);
        //panelUsuarios.add(seleccion, BorderLayout.SOUTH);
        panelCentral.add(new JScrollPane(editor), BorderLayout.CENTER);
        panelCentral.add(panelUsuarios, BorderLayout.EAST);
        panelFunciones.add(new JScrollPane(areaMensaje));
        panelFunciones.add(enviar);
        panelFunciones.add(archivo);
        panelFunciones.add(desconectar);
        //panelInferior.add(panelEmojis);
        panelInferior.add(panelFunciones);
        panelPrincipal.add(panelCentral, BorderLayout.CENTER);
        panelPrincipal.add(panelInferior, BorderLayout.SOUTH);
        add(panelPrincipal);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    private class CorreCliente extends WindowAdapter {
        public void windowOpened(WindowEvent we) {
            System.out.println("Ventana abierta");
            miCliente = new Cliente(nombre, host, puerto, editor, usuarioConectado);
            miCliente.saludo(nombre);
        }
    }

    private String host;
    private int puerto;
    private String nombre;
    private JPanel panelPrincipal;
    private JPanel panelCentral;
    private JPanel panelInferior;
    private JPanel panelFunciones;
    private JPanel panelUsuarios; 
    private JEditorPane editor;
    private JButton[] botonesEmojis;
    private JTextArea areaMensaje;
    private JButton enviar;
    private JButton archivo;
    private JButton desconectar;
    public static JComboBox<String> usuarioConectado;
    private JLabel usuariosConectados;
    private Cliente miCliente;
    private JPanel panelCombo;
}