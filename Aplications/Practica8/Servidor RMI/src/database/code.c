#include<stdlib.h>
#include<stdio.h>

void analizarPromedio(int);//prototipo

int main(int argc, char const *argv[])
{
	//programa de papá cuando le entregas calificaciones
	//in: numero
	//1: Ver si esta entre 0 y 10, 2: ver si es aprovatorio, 3:Si es 10 te felicita, 4:Si no es aprovatoria, repetir el programa
	int calificacion;
	
	printf("Digita tu calificacion: ");
	scanf("%d",&calificacion);

	analizarPromedio(calificacion);//implementacion
		
	return 0;
}

void analizarPromedio(int score){	
	int bandera = 0;
	do{	
		if(bandera == 1){
			printf("\nDigita tu calificacion de nuevo: ");
			scanf("%d",&score);					
		}
		else bandera = 1;
			
		if(score > 0 && score <= 10){
			if(score > 5){
				printf("Es aprobatoria:)\n");
				if(score == 10){
					printf("Felicidades, eres un genio\n");
				}					
			}
			else{
				printf("Es reprobatoria, intentelo de nuevo\n");
			}				
		}
		else{
			printf("ERROR\n");
		}
			
	}while(score < 6);
}

/// OR,AND,NOT

// true || true = true
// true || not = true
// not || true = true
// not || not = not

// true && true = true
// true && not = false
// not && true = false
// not && not = false

/* PARADIGMAS DE PROGRAMACION
	
*/