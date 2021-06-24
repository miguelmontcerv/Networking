package acr.practica4;

import java.io.IOException;
import java.net.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

public class WebServer implements Runnable {

    protected int Port;
    protected ServerSocket ss = null;
    protected boolean detenido = false;
    protected Thread runningThread = null;
    protected ExecutorService pool = Executors.newFixedThreadPool(100);

    public WebServer(int Port) {
        this.Port = Port;
    }

    public static void main(String[] args) {
        WebServer sWEB = new WebServer(8000);
        new Thread(sWEB).start();
    }//main 

    public void run() {
        synchronized (this) {
            this.runningThread = Thread.currentThread();
        }
        iniciaServidor();
        while (!detenido()) {
            Socket cl = null;
            try {
                cl = this.ss.accept();
                System.out.println("Conexion aceptada..");
            } catch (IOException e) {
                if (detenido()) {
                    System.out.println("Servidor detenido.");
                    break;
                }
                throw new RuntimeException("Error al aceptar nueva conexion", e);
            }//catch
            try {
                this.pool.execute(new Switch(cl));
            } catch (Exception ex) {
                Logger.getLogger(WebServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }//while
        this.pool.shutdown();
        System.out.println("Servidor detenido.");
    }

    private synchronized boolean detenido() {
        return this.detenido;
    }

    public synchronized void stop() {
        this.detenido = true;
        try {
            this.ss.close();
        } catch (IOException e) {
            throw new RuntimeException("Error al cerrar el socket del servidor", e);
        }
    }

    private void iniciaServidor() {
        try {
            this.ss = new ServerSocket(this.Port);
            System.out.println("Servicio iniciado.. esperando cliente..");
        } catch (IOException e) {
            throw new RuntimeException("No puede iniciar el socket en el puerto: " + ss.getLocalPort(), e);
        }
    }
}

