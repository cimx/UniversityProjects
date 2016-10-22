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
	int i;
	while(1){
		if(readLineArguments(argumentos,6) <= 0){ 
			continue; 
		};
		if(strcmp(argumentos[0],"exit")==0) {
			// exit - esperar pelos filhos e fechar a shell
			i = 0;
			pid_filhos = malloc(sizeof(int)*nfilhos);
			estado_filhos = malloc(sizeof(int)*nfilhos);
			wifexited_filhos = malloc(sizeof(bool)*nfilhos);
			while(1){
				//x wait- espera que um processo filho termine e depois retorna o process ID desse processo
				pid = wait(&estado);
				if (pid < 0){
					// Ja nao ha filhos para esperar / xERRO
					break;
				}
				//x WIFEXITED - true se tudo corre bem
				wifexited_filhos[i] = WIFEXITED(estado);
				pid_filhos[i] = pid;
				if(WIFEXITED(estado)){  
					//x WEXITSTATUS - se WIFEXITED é diferente de 0 (true) retorna 8 bits importantes do estado do filho
					estado_filhos[i] = WEXITSTATUS(estado);
				}
				i++;
				//x proximo ciclo while
				continue;
			}
			// imprimir as informacoes dos filhos
			for(i = 0; i < nfilhos ;i++){
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
			//x EXIT_SUCCESS - execução de um programa com sucesso
			exit(EXIT_SUCCESS);
		}

		else {
			// pathname argumentos - cria um processo-filho e executa o pathname
			nfilhos++;
			//x fork - cria um processo filho
			pid = fork();
			if (pid < 0) {
				//x perror - imprimir uma mensagem de erro
				perror("Erro no fork");
				continue;
			}
			if (pid == 0) {
				// se for o filho
				//x execv - replaces the current process image with a new process image
				execv(argumentos[0], argumentos);
				perror("Erro no execv");
				exit(EXIT_FAILURE);
			} 
			else{ // se for o pai
				continue;
			}
		}
	}
	return 0;
}