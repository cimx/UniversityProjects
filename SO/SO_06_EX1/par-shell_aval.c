#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <errno.h>
#include <wait.h>
#include "commandlinereader.h"
#define bool int
#define true 1
#define false 0
int main()
{
	char* argumentos[7]; // vector para guardar os argumentos lidos 
	int* pid_filhos; // vector para guardar o pid dos filhos
	int* estado_filhos; // vector para guardar o estado dos filhos
	int pid, estado; // variaveis do exit
	bool* wifexited_filhos; //vector para guardar o resultado do WIFEXITED
	int nfilhos = 0;
	int lim_filhos = 0;
	int i;
	int n_args;
	while(1){
		n_args = readLineArguments(argumentos,6);
		if(n_args <= 0){ continue; } ;		
		if(strcmp(argumentos[0], "exit") != 0) {
			// pathname argumentos - cria um processo-filho e executa o pathname
			nfilhos++;
			if(lim_filhos != 0 && nfilhos > lim_filhos) {break;}
			pid = fork();
			if (pid < 0) {
				perror("Erro no fork");
				continue;
			}
			if (pid == 0) {
				// se for o filho
				execv(argumentos[0], argumentos);
				perror("Erro no execv");
				exit(EXIT_FAILURE);
			} 
			else{ // se for o pai
				continue;
			}
		}
		else {
			if (n_args == 2) {
			  // caso em que o argumento e um numero
			  lim_filhos = atoi(argumentos[1]) + nfilhos;
			  if(lim_filhos == nfilhos) {break;}
			  continue;
			} 
			// caso em que o argumento e NULL
			lim_filhos = nfilhos;
			if(lim_filhos == nfilhos) {break;}

		}
	}
		// exit - esperar pelos filhos e fechar a shell
		i = 0;
		pid_filhos = malloc(sizeof(int)*lim_filhos);
		estado_filhos = malloc(sizeof(int)*lim_filhos);
		wifexited_filhos = malloc(sizeof(bool)*lim_filhos);
		while(1){
			pid = wait(&estado);
			if (pid < 0){
				// Ja nao ha filhos para esperar
				break;
			}
			wifexited_filhos[i] = WIFEXITED(estado);
			pid_filhos[i] = pid;
			if(WIFEXITED(estado)){  
				estado_filhos[i] = WEXITSTATUS(estado);
			}
			i++;
			continue;
		}
		// imprimir as informacoes dos filhos
		for(i = 0; i < lim_filhos ;i++){
			if(wifexited_filhos[i] == true) {
				printf("PID:%d\t Estado: %d\n", pid_filhos[i], estado_filhos[i]);
			}
			else{
				printf("Processo %d saiu sem chamar exit\n", pid_filhos[i]);
			}
		}
		free(pid_filhos);
		free(estado_filhos);
		free(wifexited_filhos);
		exit(EXIT_SUCCESS);
	return 0;
}
