package acr.practica4;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;
import java.util.StringTokenizer;

public class Switch extends Thread {

    protected Socket socket;
    protected PrintWriter pw;
    protected BufferedOutputStream bos;
    protected BufferedReader br;
    protected String FileName;

    public Switch(Socket _socket) throws Exception {
        this.socket = _socket;
    }

    public void run() {
        try {
            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            bos = new BufferedOutputStream(socket.getOutputStream());
            pw = new PrintWriter(new OutputStreamWriter(bos));
            String line = br.readLine();

            //Peticion nula
            if (line == null) {
                pw.print("<html><head><title>Servidor WEB");
                pw.flush();
                pw.print("</title><body bgcolor=\"#FFAAAA\"><br>Linea Vacia<br>");
                pw.flush();
                pw.print("</body></html>");
                pw.flush();
                socket.close();
                return;
            }
            //Peticion
            System.out.println("\nCliente Conectado desde: " + socket.getInetAddress());
            System.out.println("Por el puerto: " + socket.getPort());
            System.out.println("Datos: " + line + "\r\n\r\n");

            if (line.indexOf("?") == -1) {
                getArch(line);
                if (line.toUpperCase().startsWith("POST")) {
                    Post();
                } else if (line.toUpperCase().startsWith("GET")) {
                    Get();
                } else if (line.toUpperCase().startsWith("HEAD")) {
                    Head();
                } else if (line.toUpperCase().startsWith("PUT")) {
                    Put();
                }

            } else if (line.toUpperCase().startsWith("GET")) {
                StringTokenizer tokens = new StringTokenizer(line, "?");
                String req_a = tokens.nextToken();
                String req = tokens.nextToken();
                System.out.println("Token1: " + req_a + "\r\n");
                System.out.println("Token2: " + req + "\r\n");
                CreateW("#AACCFF", "GET");
                req = req.replace(" HTTP/1.1", "");
                GetInfo(req);
            } else if (line.toUpperCase().startsWith("HEAD")) {
                pw.println("HTTP/1.1 200 OK \n");
                pw.flush();
            } else {
                pw.println("HTTP/1.1 501 Not Implemented");
                pw.flush();
            }
        } catch (Exception e) {
            pw.println("HTTP/1.0 500 Internal Server Error \n");
            e.printStackTrace();
        }
        try {
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getArch(String line) { //getFile
        int i;
        int f;
        if (line.toUpperCase().startsWith("GET")
                || line.toUpperCase().startsWith("POST")
                || line.toUpperCase().startsWith("HEAD") || line.toUpperCase().startsWith("PUT")) {
            i = line.indexOf("/");
            f = line.indexOf(" ", i);
            FileName = line.substring(i + 1, f);
        }
    }

    public void SendA(String arg, String Resp, String ContT) {
        String sb = "";
        try {
            int b_leidos = 0;
            BufferedInputStream bis2 = new BufferedInputStream(new FileInputStream(arg));
            byte[] buf = new byte[1024];

            int tam_archivo = bis2.available();

            sb = sb + "HTTP/1.0 " + Resp + "\n";
            sb = sb + "Server: Servidor/1.0 \n";
            sb = sb + "Date: " + new Date() + " \n";
            sb = sb + "Content-Type:" + ContT + "\n";
            sb = sb + "Content-Length: " + tam_archivo + " \n";
            sb = sb + "\n";
            bos.write(sb.getBytes());
            bos.flush();
            while ((b_leidos = bis2.read(buf, 0, buf.length)) != -1) {
                bos.write(buf, 0, b_leidos);
            }
            bos.flush();
            bis2.close();

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void CreateW(String color, String method) {
        pw.println("HTTP/1.0 200 OK");
        pw.flush();
        pw.println();
        pw.flush();
        pw.print("<html><head><title>Web Server</title></head>");
        pw.flush();
        pw.print("<body bgcolor=\"" + color + "\"><center><h1><br> Peticion " + method + "</br></h1></center>");
        pw.flush();

    }

    public void GetInfo(String info) {
        String aux = " ";
        info = info + "&";
        for (int i = 0; i < info.length(); i++) {
            if (info.charAt(i) == '=') {
                pw.print("<p><b>" + aux + "</b>");
                aux = " ";
                pw.flush();
            }
            aux = aux + info.charAt(i);
            if (info.charAt(i) == '&') {
                aux = aux.replace('=', ' ');
                aux = aux.replace('&', ' ');
                pw.print(aux + "</p>");
                aux = " ";
                pw.flush();
            }
        }
        pw.print("</body></html>");
        pw.flush();
    }

    public void Post() {

        System.out.println("Peticion POST");
        pw.println("HTTP/1.0 200 OK");
        pw.flush();
        String nextLine, aux = "";

        try {
            int contentLength = 0;
            final String contentHeader = "Content-Length: ";
            do {
                nextLine = br.readLine();
                if (nextLine.startsWith(contentHeader)) {
                    contentLength = Integer.parseInt(nextLine.substring(contentHeader.length()));
                }

                aux = aux + nextLine + "\n";
            } while (nextLine.contains(":"));

            // Obtenemos el data
            StringBuilder body = new StringBuilder();
            int c = 0;
            for (int i = 0; i < contentLength; i++) {
                c = br.read();
                body.append((char) c);
            }
            String[] parametros = body.toString().split("&");

            aux = "";
            for (String parametro : parametros) {

                aux += parametro + "&";
            }
            System.out.println(aux);

            CreateW("#FFFFFF", "POST");
            GetInfo(aux);
        } catch (IOException ex) {
            pw.println("HTTP/1.1 500 Internal Server Error \n");
            pw.flush();
            System.out.println(ex);
        }
    }

    public void Head() {
        File temp = new File(FileName);
        if (temp.exists()) {
            StringTokenizer S = new StringTokenizer(FileName, ".");
            S.nextToken();
            String t = S.nextToken();
            String sb = "";
            sb = sb + "HTTP/1.0 200 OK \n";
            sb = sb + "Server: Servidor/1.0 \n";
            sb = sb + "Date: " + new Date() + " \n";
            sb = sb + "Content-Type:" + t + "\n";
            sb = sb + "Content-Length: " + temp.length() + " \n";
            sb = sb + "\n";
            pw.println(sb);
            pw.flush();
        } else {
            pw.println("HTTP/1.1 404 Not Found \n");
            pw.flush();
        }
    }

    public void Get() {
        File temp = new File(FileName);
        if (temp.exists()) {
            StringTokenizer S = new StringTokenizer(FileName, ".");
            S.nextToken();
            String t = S.nextToken();
            System.out.println("Type: " + t);
            if (t.equalsIgnoreCase("pdf")) {
                SendA(FileName, "200 OK", "application/pdf");
            } else if (t.equalsIgnoreCase("ico")) {
                SendA(FileName, "200 OK", "image/jpeg");
            } else if (t.equalsIgnoreCase("mp3")) {
                SendA(FileName, "200 OK", "audio/mpeg");
            } else if (t.equalsIgnoreCase("jpg")) {
                SendA(FileName, "200 OK", "image/jpeg");
            } else if (t.equalsIgnoreCase("gif")) {
                SendA(FileName, "200 OK", "image/gif");
            } else if (t.contains("htm")) {
                SendA(FileName, "200 OK", "text/html");
            } else if (t.equalsIgnoreCase("txt")) {
                SendA(FileName, "200 OK", "text/plain");
            } else {
                SendA(FileName, "415 Unsupported Media Type", "text/plain");
            }
        } else {
            pw.println("HTTP/1.1 404 Not Found \n");
            pw.flush();
        }
    }
  
    public void Put() throws IOException{
        File temp = new File(FileName);
        if (!temp.exists()) {
          temp.createNewFile();
          System.out.println("Archivo Creado: " + temp.getName());
          pw.println("HTTP/1.0 200 OK");
          pw.flush();
          }
        else {
            System.out.println("Ya existe el archivo");
            pw.println("HTTP/1.0 200 OK");
            pw.flush();
          }  
        }
        
}


