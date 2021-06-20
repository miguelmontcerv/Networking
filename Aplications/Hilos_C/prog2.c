#include<stdio.h>
#include<stdlib.h>
#include<pthread.h>

struct suma
{
	int a;
	int b;
};

void *fun_suma(void *arg){
	struct suma *datos = (struct suma *)arg;
	printf("Los datos son: %d y %d\n",datos->a,datos->b);
	int res = datos->b + datos->a;
	return (void *)res;
	
}

int main(int argc, char const *argv[])
{
	pthread_t thread;
	int resultado;

	struct suma *x = (struct suma *) malloc(sizeof(struct suma));
	x -> a = 5;
	x -> b = 15;

	pthread_create(&thread,NULL,fun_suma,x);
	printf(" - Ya se creo el hilo, solo falta que se ejecute -\n");

	pthread_join(thread,(void *)&resultado);

	printf("El resultado de la suma de ambos numeros es: %d\n",resultado);

	return 0;
}