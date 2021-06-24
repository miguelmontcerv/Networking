import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author tdwda
 */
public class ServidorMulticast extends Thread{
    private MulticastSocket ss;
    private static int RMI_RESGISTRY_PORT;
    private static int FILE_SEND_PORT;
    private String tipo;
    private String ID;
    
    public ServidorMulticast(MulticastSocket socket, int rmi_port, int file_port, String id, String tipo){
        this.ss = socket;
        this.RMI_RESGISTRY_PORT = rmi_port;
        this.FILE_SEND_PORT = file_port;
        this.ID = id;
        this.tipo = tipo;
    }
    
    public ServidorMulticast(MulticastSocket socket, String id, String tipo){
        this.ss = socket;
    }
    
    @Override
    public void run(){
        try {
            InetAddress gpo = InetAddress.getByName("229.1.2.3");
            if(this.tipo.equals("Puertos")){
                String aux = tipo+"|"+ID+"|"+RMI_RESGISTRY_PORT+"|"+FILE_SEND_PORT;
                EnvioMensaje(aux, gpo);
            }else if(this.tipo.equals("Salir")){
                String aux = tipo+"|"+ID;
                EnvioMensaje(aux, gpo);
            }
            
            //System.out.println("Mensaje enviado...");
            try{
                Thread.sleep(1000);
            }catch(InterruptedException ie){}
        } catch (UnknownHostException ex) {
            Logger.getLogger(ServidorMulticast.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
        } catch (IOException ex) {
            Logger.getLogger(ServidorMulticast.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void EnvioMensaje(String Mensaje, InetAddress gpo) throws IOException{
        byte[] b = Mensaje.getBytes();
        DatagramPacket p = new DatagramPacket(b,b.length,gpo,7777);
        ss.send(p);
    }
}
