import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author tdwda
 */
public class Lista_elem implements Serializable{
    
    private String archivo;
    private String Hash;
    private long tam;

    public Lista_elem(){ }
    
    public Lista_elem(String File_Path, String Hash){
        this.archivo = File_Path;
        this.Hash = Hash;
    }
    
    
    public String getArchivo() {
        return archivo;
    }

    public void setArchivo(String archivo) {
        this.archivo = archivo;
    }

    public String getHash() {
        return Hash;
    }

    public void setHash(String Hash) {
        this.Hash = Hash;
    }
    
    public long getTam() {
        return tam;
    }

    public void setTam(long tam) {
        this.tam = tam;
    }
    
}
