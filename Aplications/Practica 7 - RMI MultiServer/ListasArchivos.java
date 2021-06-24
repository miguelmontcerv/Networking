import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

/**
 *
 * @author tdwda
 */
public interface ListasArchivos extends Remote{
    ArrayList <Lista_elem> busqueda(String archivo) throws RemoteException;
}
