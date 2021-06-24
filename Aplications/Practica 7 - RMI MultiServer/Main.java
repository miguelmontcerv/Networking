import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import static java.lang.System.exit;
import java.net.MulticastSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.UUID;

/**
 *
 * @author tdwda
 */
public class Main {
    
    public static int puertosDisponibles(int lim_menor, int lim_superior){
        for (int port = lim_menor; port < lim_superior; port++) {
            try{
                ServerSocket s = new ServerSocket(port);
                s.close();
                return port;
            }catch(Exception e){}
        }
        return 0;
    }
    
    public static void main(String main[]) throws IOException, InterruptedException{
        //Inicialización de puertos
        Scanner sc = new Scanner(System.in);
        int rmi_envio = puertosDisponibles(1999, 4000);
        int flujo_envio = puertosDisponibles(7778, 10000);
        String uniqueID = UUID.randomUUID().toString();
        
        //Inicialización del socket
        MulticastSocket socket;
        socket = new MulticastSocket(7777);
        socket.setReuseAddress(true);
        socket.setTimeToLive(255);
        
        //Inicialización de listas para busqueda de archivos
        ArrayList <Nodo> nodos_encontrados = new ArrayList();
        ArrayList <Lista_elem> allFiles = new ArrayList();
        
        //Creación/Verificación de carpeta para descargas
        Archivos creaArchivos = new Archivos();
        File ruta = creaArchivos.crearArchivo(new File("").getAbsolutePath(), "\\Downloads");
        
        //Inicialización de hilos para anuncio de puertos y descubrimiento de nodos                
        new ServidorMulticast(socket, rmi_envio, flujo_envio, uniqueID, "Puertos").start();
        new ClienteNodos(socket, nodos_encontrados, rmi_envio, flujo_envio, uniqueID).start();
        
        System.out.println("Escriba el directorio donde se realizaran las peticiones de busqueda: ");
        String Dir_Busqueda = sc.nextLine();
        System.out.println("Inicializando servidor RMI...");
        
        new Server_RMI(rmi_envio, Dir_Busqueda).start();
        
        Thread.sleep(1000);
        
        new Server_Flujo(flujo_envio).start();
        //Directorio: Archivos
        //Archivo: temp
        
        String archivo = "";
        
        while(!archivo.equalsIgnoreCase("salir")){
            System.out.println("Escriba el nombre del archivo que desea buscar entre los servidores: ");
            archivo = sc.nextLine();
            if(!archivo.equalsIgnoreCase("salir")){
                ArrayList <ListaResponse> respuestas = new ArrayList();
        
                for (Nodo encontrado : nodos_encontrados) {
                    ArrayList <Lista_elem> respuesta = new ArrayList();
                    new Client_RMI(archivo, Integer.parseInt(encontrado.getPuerto_RMI()), respuesta).start();
                    respuestas.add(new ListaResponse(respuesta));
                }

                Thread.sleep(1000);
                for (ListaResponse respuesta : respuestas) {
                    allFiles.addAll(respuesta.getArchivos());
                }

                System.out.println("Se encontraron " + allFiles.size() + " archivos que coinciden con ese nombre");

                if(allFiles.size() > 0){
                    
                    for (int i = 0; i < allFiles.size(); i++) {
                        System.out.println("Archivo " + i + " :");
                        System.out.println("\tNombre: " + allFiles.get(i).getArchivo());
                        System.out.println("\tHash: " + allFiles.get(i).getHash());                        
                    }
                    
                    //Conseguimos los indices de los nodos donde el archivo coincida
                    System.out.print("\nEscriba el numero del archivo que quiere descargar: ");
                    int opc_sel = sc.nextInt();
                    sc.nextLine();
                    ArrayList <Integer> indices = new ArrayList();
                    ArrayList <String> direcciones = new ArrayList();
                    for (int i = 0; i < respuestas.size(); i++) {
                        ListaResponse lista = respuestas.get(i);
                        for (int j = 0; j < lista.getArchivos().size(); j++) {
                            Lista_elem elemento = lista.getArchivos().get(j);
                            if(allFiles.get(opc_sel).getHash().equals(elemento.getHash())){
                                indices.add(i);
                                direcciones.add(elemento.getArchivo());
                                break;
                            }
                        }
                    }

                    //Empezamos con el procedimiento para descargar el archivo por partes                    
                    System.out.println("\nLa descarga se dividira en " + indices.size() + " servidores");
                    String fileName = allFiles.get(opc_sel).getArchivo();
                    String [] fileParts = fileName.split("\\\\");
                    fileName = fileParts[fileParts.length - 1];

                    //System.out.println(fileName);

                    RandomAccessFile raf = new RandomAccessFile(new File(ruta, fileName), "rw");
                    raf.setLength(allFiles.get(opc_sel).getTam());
                    int bloque = (int) (allFiles.get(opc_sel).getTam() / indices.size()); //Calculamos cuanto va a descargar cada hilo

                    for (int i = 0; i < indices.size(); i++) {
                        int inicio = i*bloque;
                        int termino = (i+1)*bloque - 1;
                        if(i == (indices.size() - 1)){
                            termino = (int) (allFiles.get(opc_sel).getTam() - 1);
                        }

                        //Inicializamos hilos de Client_Flujo
                        int puerto = Integer.parseInt(nodos_encontrados.get(indices.get(i)).getPuerto_Flujo());
                        new Client_Flujo(raf, direcciones.get(i), puerto, inicio, termino).start();
                        
                    }

                    Thread.sleep(1000);
                    raf.close();
                    allFiles.clear();
                    System.out.println("\n");
                    
                }
                
            }else{
                exit(0);
            }   
        }
    }
}


