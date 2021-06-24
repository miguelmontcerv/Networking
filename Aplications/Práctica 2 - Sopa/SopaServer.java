package acr.practica2;
import java.net.*;
import java.io.*;
import java.util.Arrays;


public class SopaServer {
    public static void printRow(char[] row) {
        for (char i : row) {
            System.out.print(i);
            System.out.print("  ");
        }
        System.out.println();
    }
    public static void main(String[] args){
        
        String words[];
        
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
        
        
        try{
            int pto = 8000;
            ServerSocket s =  new ServerSocket(pto);
            System.out.println("Servidor inciado en el puerto "+pto+" esperando cliente...");
            
            
            for(;;){
                Socket c1 = s.accept();                      
                System.out.println("\tCliente conectado desde "+c1.getInetAddress()+":"+c1.getPort());
                PrintWriter pw = new PrintWriter(new OutputStreamWriter(c1.getOutputStream(),"ISO-8859-1"));
                BufferedReader br = new BufferedReader(new InputStreamReader(c1.getInputStream(),"ISO-8859-1"));
                DataOutputStream dos = new DataOutputStream(c1.getOutputStream());
                
                int choice = Integer.parseInt(br.readLine());
                System.out.println("Opcion: "+choice);
                
                 
                
                if(choice==0)
                {
                    int longi = paises.length;
                    words = Arrays.copyOf(paises, longi);
                }
                else if(choice==1)
                {
                    int longi = productores.length;
                    words = Arrays.copyOf(productores, longi);
                    
                }
                else if(choice==2)
                {
                    int longi = animales.length;
                    words = Arrays.copyOf(animales, longi);
                }
                else if(choice==3)
                {
                    int longi = videojuegos.length;
                    words = Arrays.copyOf(videojuegos, longi);
                }
                else if(choice==4)
                {
                    System.out.println("El cliente cerro la sesion...");
                    c1.close();                        
                    br.close();
                    pw.close();
                    break;
                }
                else
                {
                    System.out.println("Opción no valida"); 
                    break;
                }
                     //r,c
                char m[][] = new char[16][16];
                int rowb, colb;
                boolean flags[] = new boolean[5];
                
                for(int i=0; i<words.length; i++)
                {
                    Arrays.fill(flags, false);
                    colb =(int)(Math.random()*15);
                    rowb = (int)(Math.random()*15);
                    while(m[rowb][colb]!=0)
                    {
                        colb = (int)(Math.random()*15);
                        rowb = (int)(Math.random()*15);
                    }
                    System.out.println("Posicion random: "+rowb+", "+colb);
                    String word=words[i];
                    int lword = word.length();
                    char[] c = new char[lword];
                    
                    for (int k = 0; k < lword; k++) 
                        c[k] = word.charAt(k); 
                   
                    System.out.println("Longitud de la palabra " + word +" es "+ lword);
                    
                    for(int w=0; w<(15-(colb-1)); w++) 
                        if(m[rowb][colb+w]!=0)
                            flags[0]=true;
                    
                    if(flags[0])
                    {
                        for(int r=0; r<15-(rowb-1); r++)
                            for(int co=0; co<15-(colb-1); co++)
                                if(m[rowb+r][colb+co]!=0) 
                                    flags[1]=true;
                        if(flags[1])
                        {          
                            for(int x=0; x<(15-(rowb-1)); x++) 
                                if(m[rowb+x][colb]!=0)
                                    flags[2]=true;
                                    
                                if(flags[2])
                                {
                                    for(int y=0; y<colb+1; y++) 
                                        if(m[rowb][colb-y]!=0)
                                            flags[3]=true;

                                    if(flags[3])
                                    {
                                        for(int z=0; z<rowb+1; z++) 
                                            if(m[rowb-z][colb]!=0)
                                                flags[4]=true;
                                    }

                                }        
                                
                        }
                    }
                    
                   
                    if(lword<=15-(colb-1) && !flags[0])// Dirección 3:00 Horas
                    {       
                        System.out.println("La palabra: " + word + " entro a las 3");
                        for(int j=0; j<c.length; j++) 
                            m[rowb][colb+j]=c[j];  
                    }
                 
                    else if(lword<=15-(colb-1) && lword<=15-(rowb-1) && !flags[1])//Dirección 4:30 Horas
                    {
                        System.out.println("La palabra: " + word + " entro a las 4:30");
                        for(int j=0; j<c.length; j++) 
                            m[rowb+j][colb+j]=c[j];
                    }
                    
                    else if(lword<=15-(rowb-1) && !flags[2])// Dirección 6:00 Horas
                    {
                        System.out.println("La palabra: " + word + " entro a las 6");
                        for(int j=0; j<c.length; j++) 
                            m[rowb+j][colb]=c[j]; 
                    }
                    
                    else if(lword<=15-(rowb-1) && lword<=colb+1)//Dirección 7:30 Horas
                    {
                        System.out.println("La palabra: " + word + " entro a las 7:30");
                        for(int j=0; j<c.length; j++) 
                            m[rowb+j][colb-j]=c[j];
                    }
                    
                    else if(lword<=colb+1 && !flags[3])// Dirección 9:00 Horas
                    {
               
                        System.out.println("La palabra: " + word + " entro a las 9");
                        for(int j=0; j<c.length; j++) 
                            m[rowb][colb-j]=c[(c.length-1)-j];  
                    }
                    
                    else if(lword<=colb+1 && lword<=rowb+1)//Dirección 10:30 Horas
                    {
                        System.out.println("La palabra: " + word + " entro a las 10:30");
                        for(int j=0; j<c.length; j++) 
                            m[rowb-j][colb-j]=c[(c.length-1)-j]; 
                    }
                    
                    else if (lword<=rowb+1 && !flags[4])// Dirección 12:00 Horas
                    {
                        System.out.println("La palabra: " + word + " entro a las 12");
                        for(int j=0; j<c.length; j++) 
                            m[rowb-j][colb]=c[(c.length-1)-j]; 
                    }
                    
                    else if(lword<=rowb+1 && lword<=15-(colb-1))//Dirección 1:30 Horas
                    {
                        System.out.println("La palabra: " + word + " entro a las 1:30");
                        for(int j=0; j<c.length; j++) 
                            m[rowb-j][colb+j]=c[j];  
                    }
                    
                    else 
                        System.out.println("La palabra " + word + " no cabe XD");
                }
                
                //Antes de Rellenar
                for(char[] row : m) {
                    printRow(row);
                }
                
                System.out.println("*******************************************************************");
                //Rellenar la matriz con letras aleatorias
                for(int x=0; x<16; x++)
                    for(int y=0; y<16; y++)
                        if(m[x][y]==0)  
                            m[x][y]= (char)((Math.random() * (122 - 97)) + 97);

                //Despues de Rellenar
                for(char[] row : m) {
                    printRow(row);
                }
                
                
                String mat="";
                for(int x=0; x<16; x++)
                    for(int y=0; y<16; y++)
                        mat+=m[x][y];
                String matrix = mat.toString();
                System.out.println(mat);
                System.out.println(mat.length());
                dos.writeUTF(matrix);
                dos.flush();
                
                

            }//For
            
        }catch(Exception e){
           e.printStackTrace();
        }//catch
    }//main
} //class
