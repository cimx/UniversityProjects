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
#define MAX_ARGS 7
#define BUFFER_SIZE 100
#define MAXPAR 4
#define LINHA_SIZE 50

int nfilhos = 0;
list_t *list;
bool exit_ative = false;
pthread_mutex_t mutex;
pthread_cond_t podeCriarFilhos, podeMonitorizar;
int iteracao = -1; // numero de processos que ja foram corridos -> procurar no ficheiro de texto
double tempo_total_execucao = 0; // total execution time -> procurar no ficheiro de texto
FILE *fp;

void *tarefa_monitor(){
	int pid, estado;
	time_t endtime;
	time_t starttime;
	while(1){
		pthread_mutex_lock(&mutex);
		while(!exit_ative && nfilhos == 0){ // nfilhos == 0 && exit_ative == false
			pthread_cond_wait(&podeMonitorizar,&mutex);
		}
		if(nfilhos == 0){ // nfilhos == 0
			if(exit_ative){ // exit_ative == true
				pthread_mutex_unlock(&mutex);
				pthread_exit(NULL);
			}
			pthread_mutex_unlock(&mutex); // nfilhos == 0 && exit_ative == false
		}
		else{ // nfilhos != 0
			pthread_mutex_unlock(&mutex);
			pid = wait(&estado);
			if (pid < 0) {
				if (errno == EINTR) {
	    		/* Este codigo de erro significa que chegou signal que interrompeu a espera pela terminacao de filho; logo voltamos a esperar */
					continue;
				}
				else {
					perror("Error waiting for child\n");
					exit(EXIT_FAILURE);
				}
			}
			endtime = time(NULL);
			
			pthread_mutex_lock(&mutex);
			starttime = update_terminated_process(list, pid, estado,WIFEXITED(estado),endtime);
			// atualizar os valores de iteracao e tempo_total_execucao
			iteracao += 1;
			tempo_total_execucao += difftime(endtime,starttime);
				// escrever no ficheiro de texto
				fprintf(fp, "iteracao %d\n",iteracao); // iteracao
				fprintf(fp, "pid: %d execution time: %g s\n",pid,difftime(endtime,starttime)); // informacoes do filho
				fprintf(fp, "total execution time: %g s\n",tempo_total_execucao); // tempo total de execucao
				fflush(fp);
			
			nfilhos--;
			pthread_cond_signal(&podeCriarFilhos);
			pthread_mutex_unlock(&mutex);
		}
	}
}

int main()
{
	char* argumentos[MAX_ARGS];
	char buffer[BUFFER_SIZE];
	char linha[LINHA_SIZE];
	list = lst_new();
    int pid; // variaveis do exit
    pthread_t tid;
    pthread_mutex_init(&mutex, NULL);
    pthread_cond_init(&podeCriarFilhos,NULL);
    pthread_cond_init(&podeMonitorizar,NULL);

    // criar tarefa para monitorizar os filhos
    if(pthread_create(&tid, 0, tarefa_monitor,NULL) != 0) {
    	perror("Erro no pthread_create: ");
    	exit(EXIT_FAILURE);
    }

    // abrir e ler o ficheiro de texto log.txt

	fp = fopen("log.txt", "a+");
		// ler o ficheiro de texto e obter os valores de iteracao e tempo_total_execucao
		while (fgets(linha,LINHA_SIZE,fp) != NULL) { // pergunta se esta no fim do ficheiro
			sscanf(linha, "iteracao %d\n", &iteracao);
			fgets(linha,LINHA_SIZE,fp); // le linha 2 -> nao precisamos de processar
			fgets(linha,LINHA_SIZE,fp); // le linha 3
			sscanf(linha, "total execution time: %lf s\n", &tempo_total_execucao);
		}
	

    while(1){
    	if(readLineArguments(argumentos,MAX_ARGS,buffer,BUFFER_SIZE) <= 0){ 
    		continue;
    	}
		if(strcmp(argumentos[0],"exit")==0) {
			// exit - esperar pelos filhos e fechar a shell

			pthread_mutex_lock(&mutex);
		    exit_ative = true;
		    pthread_cond_signal(&podeMonitorizar);
		    pthread_mutex_unlock(&mutex);
			
			pthread_join(tid,NULL);
			lst_print(list);
			pthread_mutex_destroy(&mutex);
			pthread_cond_destroy(&podeCriarFilhos);
			pthread_cond_destroy(&podeMonitorizar);
			lst_destroy(list);
			fclose(fp);
			exit(EXIT_SUCCESS);		
			
		}
		else {
			pthread_mutex_lock(&mutex);
			while (! (nfilhos < MAXPAR))
				pthread_cond_wait(&podeCriarFilhos,&mutex);
			pthread_mutex_unlock(&mutex);
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
			pthread_cond_signal(&podeMonitorizar);
			insert_new_process(list,pid,starttime);
			pthread_mutex_unlock(&mutex);
		}	
	}
	return 0;
}
