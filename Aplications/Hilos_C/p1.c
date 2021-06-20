#include <pthread.h>
#include<stdio.h>
#include<stdlib.h>

void *imprime(void *p);

int main(int argc, char const *argv[])
{
	pthread_t t1,t2;
	char *mensaje1 = "Hilo 1";
	char *mensaje2 = "Hilo 2";
	int r1,r2;

	r1 = pthread_create(&t1,NULL,imprime,(void*)mensaje1);
	printf("Seccion 1\n");
	r2 = pthread_create(&t2,NULL,imprime,(void*)mensaje2);

	pthread_join(t2,NULL);
	pthread_join(t1,NULL);
	
	return 0;
}

void *imprime(void *p){
	printf("%s \n",(char *)p);
}