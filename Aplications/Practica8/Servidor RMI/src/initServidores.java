import static colors.colors.ANSI_GREEN;
import static colors.colors.ANSI_RESET;
import static colors.colors.ANSI_YELLOW;


public class initServidores extends Thread{

    ServidorMulticast ServidorMulticast = new ServidorMulticast();
    ServidorRMI ServidorRMI = new ServidorRMI();
    ServidorUnicast ServidorUnicast = new ServidorUnicast();
    
    public initServidores() {
        System.out.println("Iniciando Servidores:");      
        ServidorMulticast.start();
        System.out.println(ANSI_GREEN+"\tServidor Multicast Iniciado");
        ServidorRMI.start();
        System.out.println(ANSI_GREEN+"\tServidor RMI Iniciado");
        ServidorUnicast.start();
        System.out.println(ANSI_GREEN+"\tServidor Unicast Iniciado");
    }
    
    public static void main(String[] args) {
        try{
	    initServidores servidores = new initServidores();
	    servidores.start();
	}catch(Exception e){
            e.printStackTrace();
        }
    }
    
    
}
