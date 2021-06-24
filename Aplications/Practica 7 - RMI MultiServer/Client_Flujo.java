import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author tdwda
 */
public class Client_Flujo extends Thread{
    private int puerto;
    private int inicio;
    private int termino;
    private RandomAccessFile archivo;
    private String path;
    
    public Client_Flujo(){}
    
    public Client_Flujo( RandomAccessFile archivo, String path, int puerto, int inicio, int termino){
        this.archivo = archivo;
        this.path = path;
        this.puerto = puerto;
        this.inicio = inicio;
        this.termino = termino;
    }
    
    @Override
    public void run(){
        //System.out.println((-1*(inicio-termino)));
        
        try {
            
            Socket cl = new Socket("localhost", puerto);
            BufferedOutputStream bos = new BufferedOutputStream(cl.getOutputStream());
            DataOutputStream dos = new DataOutputStream(bos);
            
            dos.writeUTF(this.path);
            dos.flush();
            
            dos.writeInt(this.inicio);
            dos.flush();
            
            dos.writeInt(this.termino);
            dos.flush();
            
            DataInputStream dis = new DataInputStream(cl.getInputStream());
            byte[] bytes = new byte[(termino - inicio)];
            
            
            dis.readFully(bytes, 0, (termino - inicio));
            
            this.archivo.seek(inicio);
            this.archivo.write(bytes, 0, (termino - inicio));
            
            //System.out.println("Parte del archivo descargada desde "+this.puerto+"...");
            
            dos.close();
            bos.close();
            dis.close();
            
        } catch (IOException ex) {
            ex.printStackTrace();
            Logger.getLogger(Client_Flujo.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
    }
}
