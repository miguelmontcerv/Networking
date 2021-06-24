import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import static java.lang.ProcessBuilder.Redirect.Type.READ;
import static java.lang.ProcessBuilder.Redirect.Type.WRITE;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.channels.FileChannel;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author tdwda
 */
public class Server_Flujo extends Thread{
    
    private int puerto;
    
    public Server_Flujo(){ }
    
    public Server_Flujo(int puerto){
        this.puerto = puerto;
    }
    
    @Override
    public void run(){
        
        try {
            ServerSocket ss;
            ss = new ServerSocket(puerto);
            ss.setReuseAddress(true);
            
            for(;;){
                Socket sc = ss.accept();
                BufferedInputStream bis = new BufferedInputStream(sc.getInputStream());
                DataInputStream dis = new DataInputStream(bis);

                String dir = dis.readUTF();
                int inicio = dis.readInt();
                int termino = dis.readInt();
                
                System.out.println("Inicio:" + inicio);
                System.out.println("Termino:" + termino);
                
                DataOutputStream dos = new DataOutputStream(sc.getOutputStream());
                
                RandomAccessFile raf = new RandomAccessFile(new File(dir), "rw");
                byte[] bytes = new byte[(-1*(inicio-termino))];
                raf.seek((long) inicio);
                raf.readFully(bytes, 0, (termino-inicio));
                
                dos.write(bytes);
                
                dis.close();
                bis.close();
                dos.close();
                raf.close();
            }
            
        } catch (IOException ex) {
            Logger.getLogger(Server_Flujo.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
    }
    
}
