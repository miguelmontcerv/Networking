import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.ArrayList;

/**
 *
 * @author tdwda
 */
public class ClienteNodos extends Thread{
    private MulticastSocket cl;
    private ArrayList <Nodo> nodos_encontrados;
    private int RMI_RESGISTRY_PORT;
    private int FILE_SEND_PORT;
    private String ID;
    
    public ClienteNodos(MulticastSocket socket, ArrayList <Nodo> Lista){
        this.cl = socket;
        this.nodos_encontrados = Lista;
    }
    
    public ClienteNodos(MulticastSocket socket, ArrayList <Nodo> Lista, int rmi, int flujo, String id){
        this.cl = socket;
        this.nodos_encontrados = Lista;
        this.ID = id;
        this.FILE_SEND_PORT = flujo;
        this.RMI_RESGISTRY_PORT = rmi;
    }
    
    @Override
    public void run(){
        try{
            //cl = new MulticastSocket(7779);
            InetAddress gpo = InetAddress.getByName("229.1.2.3");
            cl.joinGroup(gpo);
            DatagramPacket p = new DatagramPacket(new byte[65535], 65535);
            
            for(;;){
                
                cl.receive(p);
                String mensaje= new String(p.getData(), 0, p.getLength());
                String elems[] = mensaje.split("\\|");
                
                if(elems[0].equals("Puertos")){
                    Nodo nuevo = new Nodo();
                    
                    nuevo.setID(elems[1]);
                    
                    nuevo.setPuerto_RMI(elems[2]);

                    nuevo.setPuerto_Flujo(elems[3]);
                    
                    int encontrado = 0;
                    for (int i = 0; i < nodos_encontrados.size(); i++) {
                        if(nodos_encontrados.get(i).getID().equals(nuevo.getID())){
                            encontrado++;
                        }
                    }
                    if(encontrado == 0 && !nuevo.getID().equals(this.ID)){
                        nodos_encontrados.add(nuevo);
                        //System.out.println("ID: " + nuevo.getID());
                        //System.out.println("Puerto de RMI: " + nuevo.getPuerto_RMI());
                        //System.out.println("Puerto de Flujo: " + nuevo.getPuerto_Flujo());
                        new ServidorMulticast(this.cl, this.RMI_RESGISTRY_PORT, this.FILE_SEND_PORT, this.ID, "Puertos").start();
                        //System.out.println("Mensaje recibido");
                    }
                    
                    
                }else if(elems[0].equals("Salida")){
                    for (int i = 0; i < nodos_encontrados.size(); i++) {
                        if(nodos_encontrados.get(i).getID().equals(elems[1])){
                            nodos_encontrados.remove(i);
                        }
                    }
                    
                }
                
            }//for
          
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
}





