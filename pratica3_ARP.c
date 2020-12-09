/*
    Practica 2: Analizador de protocolo LLC
    Integrantes: Tovar Espejo Mariana Josefina
                 Monteros Cervantes Miguel Angel
    Redes de computadora
*/
#include <stdio.h>
#include <stdlib.h>
#include <pcap.h>
#define LINE_LEN 16
#define     PCAP_OPENFLAG_PROMISCUOUS   1
#define     PCAP_SRC_FILE   2
#define     PCAP_BUF_SIZE   1024
#ifdef _MSC_VER
#define _CRT_SECURE_NO_WARNINGS
#endif

void dispatcher_handler(u_char *, const struct pcap_pkthdr *, const u_char *);
void to_binary(int n);
int analyze_byte(int byte, int position);
void analyze_control_byte(int byte1, int byte2,int tipo_control);
void sap(const u_char *pkt_data, int position);

int main(int argc, char **argv){
    pcap_if_t *alldevs; //Lista las tarjetas de red (pcap interface table)
    pcap_if_t *d; //Apuntador auxiliar para la lista de dispositivos (lista enlazada)
    int inum;
    int i=0;
    pcap_t *adhandle; //Apuntador al Controlador
    char errbuf[PCAP_ERRBUF_SIZE]; //Variable para registro de error en el buffer
    
    /* Enlistar dispositivos*/
    if(pcap_findalldevs(&alldevs, errbuf) == -1)
    {
        fprintf(stderr,"Error en la funcion pcap_findalldevs: %s\n", errbuf);
        exit(1);
    }
    
    /* Mostramos la lista */
    for(d=alldevs; d; d=d->next) //Recorremos la lista e imprimimos la información del controlador mientras que exista, sino, pasamos al siguiente
    {
        printf("%d. %s", ++i, d->name);
        if (d->description)
            printf(" (%s)\n", d->description);
        else
            printf("(Descripcion no disponible)\n");
    }
    
    if(i==0) //Si no se recolecto ningun dispositivo
    {
        printf("\n No se encontraron interfases, asegurece de que WinPcap este instalado.\n");
        return -1;
    }
    
    printf("Digite un numero de interfaz (1-%d):",i);
    scanf("%d", &inum);
    
    if(inum < 1 || inum > i) //Numero invalido
    {
        printf("\n Numero de interfaz fuera de rango.\n");
        /* Liberamos la lista de dispositivos */
        pcap_freealldevs(alldevs);
        return -1;
    }
    
    /* Recorremos la lista hasta llegar a la opcion indicada */
    for(d=alldevs, i=0; i< inum-1 ;d=d->next, i++);
    
    /* Abrimos el dispositivo y configuramos el controlador (captura al vuelo) */
    if ((adhandle= pcap_open_live(d->name,  // Nombre del dispositivo
                             65536,         // Porcion del paquete de captura
                                            // 65536 garantiza que todo el paquete se capturará en todos los MAC.
                             1,             // modo promiscuo (nonzero significa promiscuo), es decir, va a tomar todo lo que pase
                                            // si ponemos un 0 lee solo las tramas dirigidas a su direccion MAC.
                             1000,          // tiempo de lectura (en milisegundos)
                             errbuf         // guardar el error en el buffer (si llegara a haber uno)
                             )) == NULL)
    {
        //Si llega a haber un error, regresa NULL...
        fprintf(stderr,"\nNo se puede abrir el adaptador . %s (%s)por que no puede ser procesado por WinPcap\n", d->name, d->description);
        /* Liberamos la lista de dispositivos */
        pcap_freealldevs(alldevs);
        return -1;
    }
    
    printf("\nEscuchando a %s...\n", d->description); 
    
    /* Para este momento ya no necesitamos la lista de dispositivos, entonces la eliminamos */
    pcap_freealldevs(alldevs);
    
    /* Empezamos con la captura de datos */
    pcap_loop(adhandle, 15, dispatcher_handler, NULL);//Tarjeta de red, numero de tramas a escuchar (si poneos -1 nunca dejara de escuchar),funcion a utilizar para el analizis de la trama en crudo, menejo de error (por ahora no ponemos)
    
    pcap_close(adhandle); //Cierra el controlador de tarjeta de red
    return 0;
}

void dispatcher_handler(u_char *temp1, const struct pcap_pkthdr *header, const u_char *pkt_data){
    u_int i=0;
    int count = 0;
    int tipo_control=0; //8 o 16 bits

    /*
     * Parametros no utilizamos, casteamos a void para eliminar memoria
     */
    (VOID)temp1;
    
    printf("----------------------------------------------------------\n");
    
    //printf("Longitud Trama:%d\n",header->len);//Imprimimos la hora de la consulta
    int j=0,k=0;
    
    printf("\nMAC destino:\n ");
    for(j=0;j<6;j++) printf("%02X:",pkt_data[j]);// Imprime la MAC destino del encabezado de IP 0-5
    
    printf("\nMAC origen:\n ");
    for(k=6;k<12;k++) printf("%02X: ",pkt_data[k]);// Imprime la MAC origen del encabezado de IP 6-11   
    
    
    /* Los siguientes dos bytes muestra la longitud o el tipo de protocolo a partir del byte 14 */    
    unsigned short tipo = (pkt_data[12]*256)+pkt_data[13]; //La multiplicación es por el corriemiento de bits y se le junta a la parte alta (12) la parte baja(13)
    printf("\n\nLonitud PDU / Tipo: %d ",tipo);
    
    if(tipo <= 1499){
        printf("IEEE 802.2\n\n");       
        //printf("Direccion destino:\n%02X\nDireccion origen:\n%02X\n\n",pkt_data[12],pkt_data[13]);//No estoy muy seguro si eso significa
        //PDU_de_DLL(header,pkt_data,tipo);
    }
    else if(tipo == 2054)
        printf("Mensaje ARP\n");   
    
    printf("\n\n");   
}

void PDU_de_DLL( const struct pcap_pkthdr *header,const u_char *pkt_data, int tipo){
    u_int i=0;
    int count = 0;
    int tipo_control=0; //8 o 16 bits

    /*PDU del LLC*/
    printf("Punto de acceso al servicio de destino(DSAP): "); printf("%d (",pkt_data[14]);
    to_binary(pkt_data[14]); printf(")");
    sap(pkt_data,14);
    //Analizamos el primer bit de ese byte
    if( analyze_byte(pkt_data[14],0) == 1)
        printf("\n Entrega Grupal");
    else
        printf("\n Entrega Individual");
        
    printf("\n\nPunto de acceso al servicio de origen(SSAP): "); printf("%d (",pkt_data[15]);
    to_binary(pkt_data[15]); printf(")");
    sap(pkt_data,15);
    //Analizamos el primer bit de ese byte
    if( analyze_byte(pkt_data[15],0) == 1)
        printf("\n Respuesta\n\n");
    else
        printf("\n Comando\n\n");
    
    //Analizamos el campo de control, este puede medir 1 o 2 bytes, si "tipo/len" > 3, es de 2 bytes, si fuera <= serian 1 byte
    if(tipo > 3){ 
        printf("Control: %02X:%02X",pkt_data[16],pkt_data[17]);
        tipo_control = 16; i = 18;
        analyze_control_byte(pkt_data[16],pkt_data[17],tipo_control);
        }
    
    else{ 
        printf("Control: %02X",pkt_data[16]); 
        tipo_control = 8; i = 17;       
        analyze_control_byte(pkt_data[16],0,tipo_control);      
        }
    printf("\n\n");
    /* Print the packet */
    for (i; (i <= header->caplen) ; i++)
    {
        printf("%02X ", pkt_data[i]);
        if ( ((i-17) % LINE_LEN) == 15) printf("\n");
    }
}

void to_binary(int n){      
    int i = 7;
    for(;i >= 0; i--)
        printf("%d",analyze_byte(n,i));     
}

int analyze_byte(int byte, int position){
    int i = 0;
    while(i<position){
        byte >>= 1;
        i++;
    }
    
    if (byte & 1)
        return 1;
    else
       return 0;       
}

void analyze_control_byte(int byte1, int byte2,int tipo_control){
    int i = 0;
    int arreglo[tipo_control];
    int codigo_dec = 0;

    if (tipo_control == 8){ //solo es un byte
        for(;i <= 7; i++) arreglo[i] = analyze_byte(byte1,i);               
    }
    else if (tipo_control == 16){ //dos bytes
        for(;i <= 7; i++) arreglo[i] = analyze_byte(byte1,i);       
        for(i = 0;i <= 7; i++) arreglo[i+8] = analyze_byte(byte2,i);        
    }
    else {printf("¡¡Bytes de control no validos!!\n\n"); return;}
    /* Analisis del control */
    printf("\n Byte Control Invertido: "); for (i = 0; i < tipo_control; ++i) printf("%d",arreglo[i]);

    if(arreglo[0] == 0){
        printf("\n Trama de Informacion\n");
        if(tipo_control == 8){
            printf("  Numero de secuencia: %d%d%d",arreglo[1],arreglo[2],arreglo[3]);
            printf("\n  Sondeo/Bit Final: %d",arreglo[4]);
            printf("\n  Numero de Acuse: %d%d%d",arreglo[5],arreglo[6],arreglo[7]);
        }
        else{
            printf("  Numero de secuencia: %d%d%d%d%d%d%d",arreglo[1],arreglo[2],arreglo[3],arreglo[4],arreglo[5],arreglo[6],arreglo[7]);
            printf("\n  Sondeo/Bit Final: %d",arreglo[8]);
            printf("\n  Numero de acuse: %d%d%d%d%d%d%d",arreglo[9],arreglo[10],arreglo[11],arreglo[12],arreglo[13],arreglo[14],arreglo[15]);
        }
    }

    if(arreglo[0] == 1 && arreglo[1] == 0){
        printf("\n Trama de Supervision\n");
        if(tipo_control == 8){
            printf("  Codigo: [%d%d]",arreglo[2],arreglo[3]);
            codigo_dec = (arreglo[2]*4)+(arreglo[3]*8);
            printf(" %d",codigo_dec);
            printf("\n  Sondeo/Bit Final: %d",arreglo[4]);
            printf("\n  Numero de Acuse: %d%d%d",arreglo[5],arreglo[6],arreglo[7]);
        }
        else{
            printf("  Codigo: [%d%d]",arreglo[2],arreglo[3]);
            codigo_dec = (arreglo[2]*4)+(arreglo[3]*8);
            printf(" %d",codigo_dec);
            printf("\n  Sondeo/Bit Final: %d",arreglo[8]);
            printf("\n  Numero de acuse: %d%d%d%d%d%d%d",arreglo[9],arreglo[10],arreglo[11],arreglo[12],arreglo[13],arreglo[14],arreglo[15]);
        }
    }
    if(arreglo[0] == 1 && arreglo[1] == 1){
        printf("\n  Trama de Sin Numerar\n");
        printf("  Codigo: [%d%d %d%d%d]",arreglo[2],arreglo[3],arreglo[5],arreglo[6],arreglo[7]);
        codigo_dec = (arreglo[2]*4)+(arreglo[3]*8)+(arreglo[5]*32)+(arreglo[6]*64)+(arreglo[7]*128);
        printf(" %d",codigo_dec);
        printf("\n  Sondeo/Bit Final: %d",arreglo[4]);      
    }

}

void sap(const u_char *pkt_data, int position){
    printf("\n SAP:%02X - ",pkt_data[position]);
    switch((int)(pkt_data[position])){
        case 0:
            printf("Null SAP -\n");
        break;      
        case 4: case 5: case 8: case 12:
            printf("SNA -\n");
        break;      
        case 6:
            printf("TCP -\n");
        break;      
        case 66:
            printf("Spanning Tree -\n");
        break;
        case 127:
            printf("ISO 802.2 -\n");
        break;
        case 128:
            printf("XNS -\n");
        break;
        case 170:
            printf("SNAP -\n");
        break;
        case 224:
            printf("IPX -\n");
        break;
        case 240:
            printf("NetBios -\n");
        break;
        case 248: case 242:
            printf("RPL -\n");
        break;
        case 254:
            printf("OSI -\n");
        break;
        case 255:
            printf("Global SAP -\n");
        break;
        default:
            printf("SAP Desconocido -\n");
        break;
                            
    }
}