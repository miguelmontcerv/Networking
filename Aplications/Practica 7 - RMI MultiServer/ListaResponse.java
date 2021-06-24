import java.util.ArrayList;

/**
 *
 * @author tdwda
 */
public class ListaResponse {
    private ArrayList <Lista_elem> archivos;
    
    public ListaResponse(){}
    
    public ListaResponse( ArrayList <Lista_elem> respuesta ){
        this.archivos = respuesta;
    }

    public ArrayList<Lista_elem> getArchivos() {
        return archivos;
    }

    public void setArchivos(ArrayList<Lista_elem> archivos) {
        this.archivos = archivos;
    }
}
