import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;

/**
 *
 * @author tdwda
 */
public class Client_RMI extends Thread{
    private String File_name;
    private int RMI_puerto;
    private ArrayList <Lista_elem> Lista;
    
    public Client_RMI(){ }
    
    public Client_RMI(String nombre, int puerto){
        this.File_name = nombre;
        this.RMI_puerto = puerto;
    }
    
    public Client_RMI(String nombre, int puerto, ArrayList <Lista_elem> Lista_prev){
        this.File_name = nombre;
        this.RMI_puerto = puerto;
        this.Lista = Lista_prev;
    }
    
    @Override
    public void run(){
    
        String host = "127.0.0.1";
        try{
            Registry registry = LocateRegistry.getRegistry(host, RMI_puerto);
            ListasArchivos stub = (ListasArchivos) registry.lookup("ListasArchivos");
            
            ArrayList <Lista_elem> response = stub.busqueda(File_name);
            Lista.addAll(response);
            
            
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
