/*===================================================
=				Includes		              		=
===================================================*/
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <errno.h>
#include <wait.h>
#include <pthread.h>
#include <fcntl.h>
#include <sys/stat.h>
#include "list.h"
#include "commandlinereader.h"
#include "list-terminais.h"
#include <signal.h>

/*===================================================
=            	Constantes				           	=
===================================================*/
#define bool int 						//	Definição de um tipo equivalente a booleano	
#define true 1							//	Definição de true
#define false 0							//	Definição de false
#define MAX_ARGS 7						//	Número máximo de argumentos
#define BUFFER_SIZE 100					//	Tamanho máximo do buffer
#define MAXPAR 4						//	Número máximo de processos filhos em execução ao mesmo tempo
#define LINHA_SIZE 50					//	Tamanho máximo de cada linha lida
#define FILENAME_SIZE 50				//	Tamanho máximo do nome do ficheiro log
#define READWRITE_ONLY 0666				//	Código de permissões -> permite leitura e escrita
#define PIPENAME "/tmp/par-shell-in"	//	Nome do pipe usado para receber comandos
#define PIPE_SIZE 18					//	Tamanho do pipe usado para enviar dados para cada terminal
#define OUTPUT_SIZE 50					//	Tamanho máximo do output enviado pelo pipe para cada terminal

/*===================================================
=            Variaveis globais	              		=
===================================================*/
int nfilhos = 0; 						// 	Número de filhos em execução
int iteracao = -1; 						// 	Número de processos que ja foram corridos -> procurar no ficheiro de texto
int fd_write;							//	Descritor de ficheiro usado na escrita para o pipe
int pidterminal;						//	Pid de um terminal
int inputdesc;							//	Descritor de ficheiro usado na leitura de um pipe
double tempo_total_execucao = 0;		// 	Tempo total de execução -> procurar no ficheiro de texto
bool exit_ative = false;				// 	Flag que diz se foi recebido o comando "exit"

pthread_mutex_t mutex;					//	Mutex
pthread_cond_t podeCriarFilhos;			// 	Variável de condição
pthread_cond_t podeMonitorizar;			//	Variável de condição
pthread_t tid;							// 	Id da tarefa (neste caso, tarefa monitora)

list_t *list;							// 	Lista de informações dos processos filho 
list_terminais *lista_terminais;		//	Lista dos pids dos terminais associados a par-shell
FILE *ficheiro_log;						// 	Ficheiro log que guarda informações sobre as execucoes do par-shell

char linha[LINHA_SIZE];					//	Linha lida do ficheiro log	
char filename[FILENAME_SIZE];			//	Nome do ficheiro para onde foi redirecionado o output de cada filho
char output[OUTPUT_SIZE];				//	Output enviado para o terminal no comando "stats"
char terminalpipe[PIPE_SIZE];			//	Nome do pipe para um terminal especifico
char* inputpipe = PIPENAME;				//	Nome do pipe que recebe comandos de terminais

/*===================================================
=            Prototipos de funcoes              	=
===================================================*/
void initialize_variables();			//	Função que inicializa todas as variáveis e estruturas da par-shell
void update_log_values();				//	Função que atualiza o tempo total de execução e número de iterações da par-shell
void redirect_stdout();					//	Função que redireciona o output dos processos filho para um ficheiro
void mutex_lock();						//	Função que realiza o lock do mutex
void mutex_unlock();					//	Função que realiza o unlock do mutex
void destroy_variables();				//	Função que destroi todas as variáveis e estruturas da par-shell
void exit_par_shell();					//	Função que realiza a saída ordeira da par-shell

/*===================================================
=            Tratamento do signal               	=
===================================================*/
void sig_handler(int signo){
	if(signo == SIGINT){
		exit_par_shell();
	}
}

/*===================================================
=				Tarefa monitora            	    	= 
===================================================*/
void *tarefa_monitora(){
	int pid;			//	Pid do processo filho
	int estado;			//	Valor do estado devolvido pelo wait do processo filho
	time_t starttime;	// 	Tempo inicial do processo filho
	time_t endtime;		// 	Tempo final do processo filho

	while(1){
		mutex_lock();
		/**
		 * 	Verificar se existem processos filhos para monitorizar
		 * 	ou se a par-shell pretende fazer exit
		 */
		while(!exit_ative && nfilhos == 0){	//	exit_ative == false && nfilhos == 0
			if(pthread_cond_wait(&podeMonitorizar,&mutex) != 0){
				perror("Erro no pthread_cond_wait na tarefa_monitora: ");
			}
		}
		/**
		 * 	Verificar se a par-shell pretende fazer exit quando não tem processos filho
		 */
		if(nfilhos == 0){ 	
			if(exit_ative){ 
				mutex_unlock();
				pthread_exit(NULL);
			}
			mutex_unlock(); 
		}
		/**
		 * 	A par-shell tem processos filho para monitorizar
		 */
		else{
			mutex_unlock();
			/**
			 * 	Realizar wait por um processo filho
			 */
			pid = wait(&estado);
			if (pid < 0) {
				if (errno == EINTR) {
	    		/**
	    		 * 	Este codigo de erro significa que chegou signal que interrompeu 
	    		 *  a espera pela terminação de filho; logo voltamos a esperar 
	    		 */
					continue;
				}
				else {
					perror("Error waiting for child\n");
					exit(EXIT_FAILURE);
				}
			}
			//	Obter tempo final de execução do processo filho
			endtime = time(NULL);
			mutex_lock();
			/**
			 * 	Atualizar a lista de processos filhos com as informações necessárias
			 */
			if ((starttime = update_terminated_process(list, pid, estado,WIFEXITED(estado),endtime)) < 0){
				// 	Verificar se houve erro na atualizacao da lista de processos filhos -> nao existe o pid na lista
				perror("Erro na atualizacao da lista de processos filhos: ");
			}

			/**
			 * 	Atualizar os valores de iteracao e tempo_total_execucao
			 */
			iteracao += 1;
			tempo_total_execucao += difftime(endtime,starttime);
				// 	Escrever no ficheiro de texto
				fprintf(ficheiro_log, "iteracao %d\n",iteracao); 											//	iteracao
				fprintf(ficheiro_log, "pid: %d execution time: %g s\n",pid,difftime(endtime,starttime)); 	//	informações do filho
				fprintf(ficheiro_log, "total execution time: %g s\n",tempo_total_execucao); 				//	tempo total de execução
				if(fflush(ficheiro_log) < 0)
				    perror("Erro no fflush de escrita no log.txt: ");
			
			// 	Diminuir o contador do número de filhos em execução
			nfilhos--;
			//	Sinalizar a variavel de condição relacionada com a criação de novos processos filho	
			if(pthread_cond_signal(&podeCriarFilhos) != 0){
				perror("Erro no pthread_cond_signal na tarefa_monitora: ");
			}
			mutex_unlock();
		}
	}
}

/*===================================================
=				Programa principal        			=
===================================================*/
int main()
{
	char* argumentos[MAX_ARGS];		//	Argumentos dos executáveis
	char buffer[BUFFER_SIZE];		//	Buffer que guarda os dados da leitura do pipe
	int input;		 				// 	Descritor de ficheiro para o pipe que recebe input dos terminais
	/**
	 * 	Inicializar as variáveis e estruturas da par-shell
	 */
	initialize_variables();	
	/**
	 * 	Atualizar as informações do log da par-shell
	 */
	update_log_values();
	/**
	 * 	Catch do signal ctrl+c ???????????????????????????????????????????????????????????????????????????????????????
	 */
	if(signal(SIGINT, sig_handler) == SIG_ERR){
		perror("Erro no signal: ");
	}

    while(1){
    	/**
    	 * 	Abrir o pipe que recebe input dos terminais
    	 */
    	if((inputdesc = open(inputpipe,O_RDONLY)) < 0){
    		perror("Erro no open do pipe: ");
    		exit(EXIT_FAILURE);
    	} 
    	/**
    	 * 	Ler informações do pipe
    	 */
    	if((input = read(inputdesc,buffer,BUFFER_SIZE)) <= 0){
    		//	Fechar o pipe apos a leitura
    		close(inputdesc);
    		//	Não lê nada mas tambem não dá erro, continua o ciclo (e.g press enter) 
    		if(input == 0){
    			continue;
    		}
    		perror("Erro no read do input");
    	}
    	else{
    		buffer[input] = '\0'; //???????????????????????????????????????????????????????????????????????????????
			if(strcmp(buffer,"exit-global") == 0) {
				/**
				 * 	Sair da par-shell
				 */
				exit_par_shell();
			}
			
			if (sscanf(buffer, "/tmp/terminal-%d", &pidterminal) > 0) {
				/**
				 *	Registar um terminal especifico e o seu pipe
				 */
				printf("Terminal %d criado\n",pidterminal);
				if(fflush(stdout) < 0)
				    perror("Erro no fflush de escrita no par-shell na criação de terminal: ");
				//	Inserir pid do terminal na lista de terminais
				insert_new_terminal(lista_terminais,pidterminal);  
				continue;
			}
			
			if(sscanf(buffer,"stats %d",&pidterminal) > 0){
				/**
				 * 	Enviar o número de processos filhos em execução e 
				 *  o tempo total de execução da par-shell para o terminal
				 */
				//	Construir strings de output e do nome do pipe do terminal
				sprintf(output,"Número de filhos em execução: %d \nTempo total: %g",nfilhos,tempo_total_execucao);
				sprintf(terminalpipe,"/tmp/terminal-%d",pidterminal);
				//	Abrir o pipe do terminal para escrita
				if((fd_write = open(terminalpipe, O_WRONLY)) < 0){
					perror("Erro no open do stats da par-shell: ");
					exit(EXIT_FAILURE); 
				}    
				//	Escrever no pipe do terminal             
				if(write(fd_write,output,strlen(output)) < 0){
					perror("Erro no write do stats da par-shell: ");
				}
				//	Fechar o pipe do terminal
				if(close(fd_write) < 0){
					perror("Erro no close do stats da par-shell: ");
				}
				strcpy(output,"\0");
				continue;
			}
			else {
				int i;		//	Inteiro temporário usado no ciclo de processamento dos argumentos 
				int pid;	//	Pid do processo filho criado com fork
				/**
				 * 	Processar todos os argumentos que poderá receber
				 */
				argumentos[0] = strtok(buffer," ");
				for(i = 1; i < MAX_ARGS; i++){ 
					argumentos[i] = strtok(NULL," ");
				}
				/**
				 * 	Confirmar se podem ser criados filhos
				 */
				mutex_lock();
				while (! (nfilhos < MAXPAR))
					if(pthread_cond_wait(&podeCriarFilhos,&mutex) != 0){
						perror("Erro no pthread_cond_wait na par-shell: ");
					}
				mutex_unlock();

				/**
				 * 	Criar processo filho usando fork
				 */
				pid = fork(); 					
				//	Registar tempo de inicio do processo filho
				time_t starttime = time(NULL);  
				if (pid < 0) {
					perror("Erro no fork: ");
					continue;
				}
				/**
				 * 	Código do processo filho
				 */
				if (pid == 0) {  		
					// 	Redirecionar o output do processo filho		
					redirect_stdout();
					// 	Evitar que o ctrl+c se propague para o processo filho -> signal é ignorado
					signal(SIGINT,SIG_IGN);
					//	Trocar código do processo filho pelo do executável	
					if(execv(argumentos[0], argumentos) < 0){
						perror("Erro no execv:");
						exit(EXIT_FAILURE);
					}
				}
				/**
				 * 	Código do processo pai
				 */
				mutex_lock();		
				nfilhos++;
				if(pthread_cond_signal(&podeMonitorizar) != 0){
					perror("Erro no pthread_cond_signal na par-shell: ");
				}
				insert_new_process(list,pid,starttime);
				mutex_unlock();
			}
		}	
	}
	return 0;
}


/*==================================================
=		Função que realiza o lock do mutex         =
==================================================*/
void mutex_lock(){
	if (pthread_mutex_lock(&mutex) < 0){
		perror("Erro no pthread_mutex_lock: ");
		exit(EXIT_FAILURE);	
	}
}

/*===================================================
=		Função que realiza o unlock do mutex        =
===================================================*/
void mutex_unlock(){
	if(pthread_mutex_unlock(&mutex) < 0){
		perror("Erro no pthread_mutex_unlock: ");
		exit(EXIT_FAILURE);
	}
}
/*===================================================
=		Função que realiza o exit da par-shell      =
===================================================*/
void exit_par_shell(){
	mutex_lock();
	//	Ativar a flag de exit
	exit_ative = true;
	if(pthread_cond_signal(&podeMonitorizar) != 0){  //?????????????????????????????????????????????????????????????
		perror("Erro no pthread_cond_signal no exit-global da par-shell: ");
	}
	mutex_unlock();

	//	Percorrer os pid's de todos os terminais, terminando-os
	while((pidterminal = remove_terminal(lista_terminais)) > 0){
		kill(pidterminal,SIGKILL);
	}	
	//	Esperar que a tarefa monitora termine
	if(pthread_join(tid,NULL) < 0){
		perror("Erro no pthread_join: ");
		exit(EXIT_FAILURE);
	}
	//	Imprimir lista de informações dos processos filhos executados
	lst_print(list);
	//	Destruir as variáveis e estruturas da par-shell
	destroy_variables();
	//	Sair da par-shell
	exit(EXIT_SUCCESS);	
}

/*===================================================
=		Função que atualiza o tempo total           =
=		de execução e número de iterações 			=
===================================================*/
void update_log_values(){
	// Abrir o ficheiro log.txt para leitura
	if((ficheiro_log = fopen("log.txt", "a+")) == NULL)
		perror("Erro no fopen do log.txt: ");

	// Ler o ficheiro de texto e obter os valores de iteracao e tempo total de execução
	while(fgets(linha,LINHA_SIZE,ficheiro_log) != NULL) { 	// Confirma se esta no fim do ficheiro
		if(sscanf(linha, "iteracao %d\n", &iteracao) > 0){	
			continue;	
		}
		if(sscanf(linha, "total execution time: %lf s\n", &tempo_total_execucao) > 0){	
			continue;	
		}
	}
}

/*===================================================
=		Função que inicializa todas as 				=
=			variáveis e estruturas                  =
===================================================*/
void initialize_variables(){
	/**
	 * 	Criar listas dos processos filhos e dos terminais
	 */
	list = lst_new();					
	lista_terminais = terminais_new();

    /**
     * 	Criar tarefa para monitorizar os filhos
     */
	if(pthread_create(&tid, 0, tarefa_monitora,NULL) != 0) {
		perror("Erro no pthread_create: ");
		exit(EXIT_FAILURE);
	}	

	/**
	 * 	Inicializar mutex e variáveis de condição
	 */
	if(pthread_mutex_init(&mutex, NULL) < 0){
		perror("Error no pthread_mutex_init: ");
		exit(EXIT_FAILURE);
	}
	if(pthread_cond_init(&podeCriarFilhos,NULL) < 0){
		perror("Erro no pthread_cond_init do podeCriarFilhos: ");
		exit(EXIT_FAILURE);		
	}
	if(pthread_cond_init(&podeMonitorizar,NULL) < 0){
		perror("Erro no pthread_cond_init do podeMonitorizar: ");
		exit(EXIT_FAILURE);
	}

	// 	Garantir que não existe nenhuma ligação com este nome
	if(unlink(inputpipe) < 0){
		if(errno != ENOENT) {
			perror("Erro no unlink no initialize_variables: ");
			exit(EXIT_FAILURE);
		}
	}

	// 	Criar pipe que recebe comandos dos terminais
	if(mkfifo(inputpipe, READWRITE_ONLY) < 0){
		perror("Erro no mkfifo: ");
		exit(EXIT_FAILURE);
	}
}

/*===================================================
=		Função que redireciona o output dos	        =
=		 processos filho para um ficheiro 			=
===================================================*/
void redirect_stdout(){
	//	Definir nome do ficheiro de output 
	sprintf(filename,"par-shell-out-%d.txt",getpid());
	if((inputdesc = open(filename,O_WRONLY|O_CREAT)) < 0){
		perror("Erro no open do redirect_stdout: ");
	}
	//	Fechar stdout
	if(close(fileno(stdout)) < 0){
		perror("Erro no close do redirect_stdout: ");
	}
	//	Redirecionar stdout para o ficheiro de output
	if(dup2(inputdesc,fileno(stdout)) < 0){
		perror("Erro no dup2 do redirect_stdout: ");
	}
	//	Abrir o ficheiro de output para escrita
	if(fopen(filename,"w") == NULL){
	  if(errno != EACCES)
	    perror("Erro no fopen do redirect_stdout: ");
	}
}

/*===================================================
=		Função que destroi todas as variáveis		=
=			e estruturas da par-shell            	=
===================================================*/
void destroy_variables(){
	/**
	 * 	Destruir mutex e variáveis de condição
	 */
	if(pthread_mutex_destroy(&mutex) < 0){
		perror("Erro no pthread_mutex_destroy: ");
		exit(EXIT_FAILURE);
	}
	if(pthread_cond_destroy(&podeCriarFilhos) < 0){
		perror("Erro no pthread_cond_destroy do podeCriarFilhos: ");
		exit(EXIT_FAILURE);		
	}
	if(pthread_cond_destroy(&podeMonitorizar) < 0){
		perror("Erro no pthread_cond_destroy do podeMonitorizar: ");
		exit(EXIT_FAILURE);
	}
	/**
	 * 	Destruir listas
	 */
	lst_destroy(list);
	terminais_destroy(lista_terminais);
	/**
	 * 	Fechar ficheiro log e ligação com o pipe
	 */
	if(fclose(ficheiro_log) == EOF){
		perror("Erro no fclose do destroy_variables: ");
	}
	if(close(inputdesc) < 0){
		perror("Erro no close do destroy_variables: ");
	}
	if(unlink(inputpipe) < 0){
		if (errno != ENOENT) {
			perror("Erro no unlink do destroy_variables: ");
			exit(EXIT_FAILURE);
		}
	}
}