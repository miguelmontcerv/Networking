import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author tdwda
 */
public class Server_RMI extends Thread implements ListasArchivos{
    
    private int Puerto;
    private String dir = "Archivos"; //Valor default de la direccion
    
    public Server_RMI(){ }
    
    public Server_RMI(int Puerto){ 
        this.Puerto = Puerto;
    }
    
    public Server_RMI(int Puerto, String direccion){ 
        this.Puerto = Puerto;
        this.dir = direccion;
    }
    
    private String getHash(File archivo, MessageDigest digest) throws FileNotFoundException, IOException{
        FileInputStream fis = new FileInputStream(archivo);
        
        byte[] byteArray = new byte[1024];
        int bytesCount = 0;
        
        while((bytesCount = fis.read(byteArray)) != -1){
            digest.update(byteArray, 0, bytesCount);
        }
        
        fis.close();
        
        byte[] bytes = digest.digest();
        
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
        }
        
        return sb.toString();
    }
    
    public ArrayList <Lista_elem> busqueda(String archivo) throws RemoteException{
        
        ArrayList <Lista_elem> encontrados = new ArrayList();
        File directorio = new File((new File("").getAbsolutePath()) + "\\" + this.dir);
        File[] coincidentes = directorio.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.startsWith(archivo);
            }
        });
        
        try {
            MessageDigest md5Digest = MessageDigest.getInstance("MD5");
            for (File coincidente : coincidentes) {
                Lista_elem archivo_encontrado = new Lista_elem();
                archivo_encontrado.setArchivo(coincidente.getAbsolutePath());
                archivo_encontrado.setHash(getHash(coincidente, md5Digest));
                archivo_encontrado.setTam(coincidente.length());
                encontrados.add(archivo_encontrado);
            }
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(Server_RMI.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Server_RMI.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return encontrados;
    }
    
    @Override
    public void run(){
        
        try {
            java.rmi.registry.LocateRegistry.createRegistry(this.Puerto); //puerto default del rmiregistry
            System.out.println("RMI registry ready.");
	} catch (Exception e) {
            System.out.println("Exception starting RMI registry:");
            e.printStackTrace();
	}//catch
	
        
        try{
            System.setProperty("java.rmi.server.codebase","http://8.25.100.18/clases/"); ///file:///f:\\redes2\\RMI\\RMI2
	    Server_RMI obj = new Server_RMI();
	    ListasArchivos stub = (ListasArchivos) UnicastRemoteObject.exportObject((Remote) this, 0);
            
	    // Bind the remote object's stub in the registry
	    Registry registry = LocateRegistry.getRegistry(this.Puerto);
	    registry.bind("ListasArchivos", stub);

	    System.err.println("Servidor listo...");
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
}



