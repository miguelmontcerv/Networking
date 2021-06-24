package acr.practica2;

import java.net.*;
import java.io.*;
import java.util.HashMap;
import java.util.Scanner;
import javax.swing.*;
import java.util.Arrays;

public class SopaClient{
    
    public static void printRow(char[] row, int num) {
        System.out.print(num);
        if(num<10)
            System.out.print("   ");
        else
            System.out.print("  ");
        for (char i : row) {
            System.out.print(i);
            System.out.print("  ");
        }
        System.out.println();
    }
    public static void main(String[] args){
        String categorias[] = {
               "paises",
               "productores",
               "animales",
               "videojuegos",
               "salir"
        };
        String words[] = {};
        
        String animales[]={
            "perro","gato","caballo","vaca","burro",
            "elefante","leon","cebra","tigre","serpiente",
            "raton","huron","cerdo","gallina","abeja",
            "hormiga","tortuga","delfin","mosca","hamster"
        };
        String paises[]={
            "mexico","austria","chipre","mongolia","serbia",
            "turquia","myanmar","nauru","kosovo","guyana",
            "tunez","belgica","fiji","surinam","angola",
            "suiza","bulgaria","yemen","canada","italia"
        };
        String productores[]={
          "skrillex","oliverse","phaseone","bandlez",
           "trinergy","chime","infekt","koven","tynan",
           "cloudnone","au5","kompany","soltan","ecraze",
           "graphyt","tchami","zhu","ivory","madeon"
        };
        String videojuegos[]={
           "valorant", "halo", "gta", "left4dead","osu",
           "fifa","madden","guitarhero","cod","cyberpunk",
           "farcry","csgo","padrino","starwars","microvolts",
           "audiosurf","gow","watchdogs","aimlab","lol"
        };
        HashMap<String, Integer>lista;
        lista = new HashMap<>();
	lista.put("paises",0);
        lista.put("productores",1);
        lista.put("animales",2);
        lista.put("videojuegos",3);
        lista.put("salir",4);
        try{
            int pto = 8000;
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in,"ISO-8859-1"));
            InetAddress host = null;
            Scanner sc = new Scanner(System.in);
            String dir = "localhost";
            try{
                /*System.out.println("Favor de escribir la direccion del servidor: ");
                dir = br.readLine();*/
                host = InetAddress.getByName(dir);
            }catch(Exception e){
                e.printStackTrace();
                //Que nos envie al inicio
                main(args);
            }//Catch
            
            Socket c1 = new Socket(host,pto);
            System.out.println("Conexion con el servidor "+dir+":"+pto+" establecida\n");
            PrintWriter pw = new PrintWriter(new OutputStreamWriter(c1.getOutputStream(),"ISO-8859-1"));
            BufferedReader br1 = new BufferedReader(new InputStreamReader(c1.getInputStream(),"ISO-8859-1"));
            DataInputStream dis = new DataInputStream(c1.getInputStream());
            String choice;
            choice = (String) JOptionPane.showInputDialog(null, "Selecciona una categoria", "Sopa de Letras", JOptionPane.DEFAULT_OPTION, null, categorias, categorias[0]);
            int opc = lista.get(choice);
            pw.println(lista.get(choice));
            pw.flush();    
            
            if(opc==0)
                words = Arrays.copyOf(paises, paises.length);
            else if(opc==1)
                words = Arrays.copyOf(productores, productores.length);
            else if(opc==2)
                words = Arrays.copyOf(animales, animales.length);
            else if(opc==3)
                words = Arrays.copyOf(videojuegos, videojuegos.length);
            
            char m[][] = new char[16][16];
            String mat = dis.readUTF();
            //System.out.println(mat);
            //System.out.println(mat.length());
            int k=0;
            for(int x=0; x<16; x++)
                for(int y=0; y<16; y++)
                    m[x][y]= mat.charAt(k++);
            System.out.println("Palabras a encontrar");
            for(String s : words)
                System.out.println(s);
            
            System.out.println("*   0  1  2  3  4  5  6  7  8  9  10 11 12 13 14 15");
            int num=0;
            for(char[] row : m) {
                    printRow(row,num++);
                }
                    
            
            
            JOptionPane.showMessageDialog(null, "Comienza el Juego!"); 
            long start = System.currentTimeMillis();
            int x0, xf, y0, yf;
            int score=0, n=0;
            int continuar = JOptionPane.YES_NO_OPTION;
            boolean flag=true;
            do
            {
   
                if(continuar == JOptionPane.YES_OPTION || flag)
                {
                    flag = false;
                    JTextField x0Field = new JTextField(5);
                    JTextField y0Field = new JTextField(5);
                    
                    JTextField xfField = new JTextField(5);
                    JTextField yfField = new JTextField(5);

                    JPanel myPanel = new JPanel();
                    myPanel.add(new JLabel("x0:"));
                    myPanel.add(x0Field);
                    myPanel.add(Box.createHorizontalStrut(15)); // a spacer
                    myPanel.add(new JLabel("y0:"));
                    myPanel.add(y0Field);

                    continuar = JOptionPane.showConfirmDialog(null, myPanel, 
                             "Ingresa Coordenada Inicial", JOptionPane.OK_CANCEL_OPTION);
                       
                    x0 = Integer.parseInt(x0Field.getText());
                    y0 = Integer.parseInt(y0Field.getText());
                    
                    JPanel myPanelf = new JPanel();
                    myPanelf.add(new JLabel("xf:"));
                    myPanelf.add(xfField);
                    myPanelf.add(Box.createHorizontalStrut(15)); // a spacer
                    myPanelf.add(new JLabel("yf:"));
                    myPanelf.add(yfField);
                    
                    continuar = JOptionPane.showConfirmDialog(null, myPanelf, 
                             "Ingresa Coordenanda Final", JOptionPane.OK_CANCEL_OPTION);
                    
                    xf = Integer.parseInt(xfField.getText());
                    yf = Integer.parseInt(yfField.getText());
                    
                    /*
                    System.out.println("Coordenada Inicial: "+ x0 + " " + y0);
                    System.out.println("Coordenada Final: "+ xf + " " + yf);
                    */
                    
                    String word="";
                    
                    if(x0==xf && y0<yf)// 3 t
                    {
                        //System.out.println("3");
                        for(int y=y0; y<=yf; y++)
                            word+=m[x0][y];
                    }
                    
                    else if(y0==yf && x0<xf)// 6  t
                    {
                        //System.out.println("6");
                        for(int x=x0; x<=xf; x++)
                            word+=m[x][y0];
                    }
                    
                    else if(x0<xf && yf<y0)// 7:30  t
                    {
                        //System.out.println("7:30");
                        for(int x=x0; x<=xf; x++)
                            for(int y=y0; y>=yf; y--)
                            {
                                word+=m[x][y];
                                x++;
                            }
                    }
                    
                    else if(x0==xf && yf<y0)// 9
                    {
                        //System.out.println("9");
                        for(int y=y0; y>=yf; y--)  
                            word+=m[x0][y];
                    }
                    
                    else if(xf<x0 && yf<y0)// 10:30
                    {
                        //System.out.println("10:30");
                         for(int x=x0; x>=xf; x--)
                             for(int y=y0; y>=yf; y--)  
                             {
                                 word+=m[x][y];
                                 x--;
                             }
                    }
                    
                    else if(xf<x0 && y0==yf)//12
                    {
                        //System.out.println("12");
                        for(int x=x0; x>=xf; x--)
                            word+=m[x][y0];
                    }
                    
                    else if(xf<x0 && y0<yf) // 1:30
                    {
                        //System.out.println("1:30");
                        for(int x=x0; x>=xf; x--) 
                            for(int y=y0; y<=yf; y++) 
                            {
                                 word+=m[x][y];
                                 x--;
                            }
                    }
                    
                    else if(x0<xf && y0<yf)// 4:30    t
                    {
                        //System.out.println("4:30");
                        for(int x=x0; x<=xf; x++)    
                            for(int y=y0; y<=yf; y++)
                            {
                                   word+=m[x][y];
                                   x++;
                            }
                    }
                    else
                        System.out.println("No option");

                    
                    if(Arrays.asList(words).contains(word))
                    {
                        n++;
                        System.out.println("Palabra acertada "+ n +" : " + word);
                        JOptionPane.showMessageDialog(null, "Acertaste la palabra " + word+"!");
                        score++;
                    }
                    else
                        JOptionPane.showMessageDialog(null, "No hay una palabra valida, intenta de nuevo!");
                    
                }
                else
                    break;
                
                continuar = JOptionPane.showConfirmDialog (null, "Deseas continuar?","Sopa de Letras", continuar);
                                
            }
            while(continuar == JOptionPane.YES_OPTION && score < 7);
            long finish = System.currentTimeMillis();
            long timeElapsed = finish - start;
            int finalTime = (int)(timeElapsed/1000);
            JOptionPane.showMessageDialog(null, "Felicidades acertaste " + score +" palabras en "+ finalTime + " segundos!"); 
            
            
   
        }catch(Exception e){
            e.printStackTrace();
        }//catch
    }//main
}

