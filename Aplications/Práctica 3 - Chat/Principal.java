import javax.swing.*;
import java.awt.BorderLayout;

public class Principal extends JFrame {
    private static final long serialVersionUID = 1L;

    public Principal() {
        setBounds(450, 150, 250, 160);
        setResizable(false);
        setLocationRelativeTo(null);
        setTitle("Practica 3");

        panelPrincipal = new JPanel();
        panelCentral = new JPanel();
        panelPrincipal.setLayout(new BorderLayout(5, 5));
        panelCentral.setLayout(new BoxLayout(this.panelCentral, BoxLayout.Y_AXIS));
        nombreUsuario = new JLabel("Introduce tu nombre de usuario:");
        campoUsuario = new JTextField(30);
        linkLabel = new JLabel("Meeting ID or personal link:");
        link = new JTextField(30);
        botonConectar = new JButton("Conectar");

        botonConectar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                if (!campoUsuario.getText().equals("")) {
                    setVisible(false);
                    String Opc = JOptionPane.showInputDialog("Contraseña");
                    new Interfaz(HOST, PUERTO, campoUsuario.getText().trim());
                } else 
                    JOptionPane.showMessageDialog(Principal.this, "Nombre de usuario Vacio", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        panelCentral.add(nombreUsuario);
        panelCentral.add(campoUsuario);
        panelCentral.add(linkLabel);
        panelCentral.add(link);
        panelPrincipal.add(panelCentral, BorderLayout.CENTER);
        panelPrincipal.add(botonConectar, BorderLayout.SOUTH);
        add(panelPrincipal);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    public static void main(String[] args) {
        new Principal();
    }

    private final String HOST = "230.1.1.1";
    private final int PUERTO = 9000;
    private JPanel panelPrincipal;
    private JPanel panelCentral;
    private JLabel nombreUsuario;
    private JLabel linkLabel;
    private JTextField campoUsuario;
    private JTextField link;
    private JButton botonConectar;
}