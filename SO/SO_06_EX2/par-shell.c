#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <errno.h>
#include <wait.h>
#include <pthread.h>
#include "list.h"
#include "commandlinereader.h"

#define bool int
#define true 1
#define false 0
#define MAX_ARGS 6
#define BUFFER_SIZE 100

int nfilhos = 0;
list_t *list;
bool exit_ative = false;
pthread_mutex_t mutex;

void *tarefa_monitor(){
	int pid, estado;
  	while(1){
		pthread_mutex_lock(&mutex);
		if(nfilhos < 1) {
			if(exit_ative){
				pthread_mutex_unlock(&mutex);
		      		break;
		  	}
			pthread_mutex_unlock(&mutex);
		    	sleep(1);
		    	continue;
		}
		else {
			pthread_mutex_unlock(&mutex);
		    	pid = wait(&estado);
			if (pid < 0) {
			  if (errno == EINTR) {
			  /* Este codigo de erro significa que chegou signal que interrompeu a espera 
			  pela terminacao de filho; logo voltamos a esperar */
			    continue;
			  }
			  else {
			    perror("Error waiting for child.");
			    exit (EXIT_FAILURE);
			  }
			}
			// Atualizar a lista com as informacoes do filho esperado no wait
			pthread_mutex_lock(&mutex);
		    	update_terminated_process(list, pid, estado, WIFEXITED(estado),time(NULL));
		    	nfilhos--;
			pthread_mutex_unlock(&mutex);
		    	continue;
		}
	}
	
	pthread_exit(NULL);
  
}
int main()
{
	char* argumentos[MAX_ARGS + 1];
	char buffer[BUFFER_SIZE];
	list = lst_new();
	int pid;
	pthread_t tid;
	pthread_mutex_init(&mutex, NULL);
	
	// Criar tarefa para monitorizar os filhos
	if(pthread_create(&tid, 0, tarefa_monitor,NULL) != 0) {
	  perror("Erro no pthread_create: ");
	  exit(EXIT_FAILURE);
	}
	while(1){
		if(readLineArguments(argumentos,MAX_ARGS,buffer,BUFFER_SIZE) <= 0){ 
			continue;
		}
		if(strcmp(argumentos[0],"exit")==0) {
			// exit - esperar pela tarefa monitora e imprimir as informacoes dos filhos
			pthread_mutex_lock(&mutex);
			exit_ative = true;
			pthread_mutex_unlock(&mutex);
			
			pthread_join(tid,NULL);
			lst_print(list);
			pthread_mutex_destroy(&mutex);
			exit(EXIT_SUCCESS);		
		
		}
		else {	
			pid = fork();
			time_t starttime = time(NULL);
			
			if (pid < 0) {
				perror("Erro no fork");
				continue;
			}
			
			if (pid == 0) {  // Codigo do filho
				execv(argumentos[0], argumentos);
				perror("Erro no execv:");
				exit(EXIT_FAILURE);
			}
			// Codigo do pai
			pthread_mutex_lock(&mutex);
			nfilhos++;
			insert_new_process(list,pid,starttime);
			pthread_mutex_unlock(&mutex);
		}	
	}
	return 0;
}
