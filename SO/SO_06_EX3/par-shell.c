#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <errno.h>
#include <wait.h>
#include <pthread.h>
#include <semaphore.h>
#include "list.h"
#include "commandlinereader.h"

#define bool int
#define true 1
#define false 0
#define MAX_ARGS 7
#define BUFFER_SIZE 100
#define MAXPAR 4

int nfilhos = 0;
list_t *list;
bool exit_ative = false;
pthread_mutex_t mutex;
sem_t sem_filhos, sem_monitor;

void *tarefa_monitor(){
    int pid, estado;
    time_t endtime;
    while(1){
    	sem_wait(&sem_monitor);

    	pthread_mutex_lock(&mutex);
    	if(nfilhos == 0) {
    		if(exit_ative){
    			pthread_mutex_unlock(&mutex);
    			break;
			}
			pthread_mutex_unlock(&mutex);
		}
		else{
			pthread_mutex_unlock(&mutex);
			pid = wait(&estado);
			if (pid < 0) {
				if (errno == EINTR) {
				/* Este codigo de erro significa que chegou signal que interrompeu
				a espera pela terminacao de filho; logo voltamos a esperar */
					continue;
				}
				else {
					perror("Error waiting for child.");
					exit (EXIT_FAILURE);
	  			}
			}
			endtime = time(NULL);
			pthread_mutex_lock(&mutex);
			update_terminated_process(list, pid, estado,WIFEXITED(estado),endtime);
			nfilhos--;
			pthread_mutex_unlock(&mutex);
			sem_post(&sem_filhos);
		}
	}
	pthread_exit(NULL);
}

int main() {
	char* argumentos[MAX_ARGS];
	char buffer[BUFFER_SIZE];
    list = lst_new();
    int pid; // variaveis do exit
    pthread_t tid;
    pthread_mutex_init(&mutex, NULL);
    sem_init(&sem_filhos,0,MAXPAR);
    sem_init(&sem_monitor,0,0);
    // criar tarefa para monitorizar os filhos
    if(pthread_create(&tid, 0, tarefa_monitor,NULL) != 0) {
    	perror("Erro no pthread_create: ");
      	exit(EXIT_FAILURE);
    }
    while(1){
      	if(readLineArguments(argumentos,MAX_ARGS,buffer,BUFFER_SIZE) <= 0){ 
			continue;
      	}
      	if(strcmp(argumentos[0],"exit")==0) {
		// exit - esperar pelos filhos e fechar a shell
      		pthread_mutex_lock(&mutex);
			exit_ative = true;
			pthread_mutex_unlock(&mutex);
			sem_post(&sem_monitor); // nao bloquear no wait da tarefa monitora
	
			pthread_join(tid,NULL);
			lst_print(list);
			pthread_mutex_destroy(&mutex);
			sem_destroy(&sem_filhos);
			sem_destroy(&sem_monitor);
			lst_destroy(list);
			exit(EXIT_SUCCESS);		
		}
		else {
			sem_wait(&sem_filhos);	
			pid = fork();
			time_t starttime = time(NULL);
			if (pid < 0) {
				perror("Erro no fork");
	  			continue;
			}
			if (pid == 0) {  // se for o filho
				execv(argumentos[0], argumentos);
				perror("Erro no execv:");
				exit(EXIT_FAILURE);
			}
			pthread_mutex_lock(&mutex);		
			nfilhos++;
			insert_new_process(list,pid,starttime);
			pthread_mutex_unlock(&mutex);
			sem_post(&sem_monitor);
		}	
	}
	return 0;
}