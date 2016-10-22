/*===================================================
=               Includes                            =
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
#include <sys/types.h>
/*===================================================
=               Constantes                          =
===================================================*/
#define MAX_ARGS 7              //  Numero maximo de argumentos
#define INPUT_SIZE 100          //  Tamanho maximo do input da leitura do pipe
#define PIPE_SIZE 19            //  Tamanho do pipe
#define READWRITE_ONLY 0666     //  Codigo de permissoes -> permite leitura e escrita

/*===================================================
=             Variáveis globais                     =
===================================================*/
int fd_write,fd_read;           //  Descritor de ficheiro usado na escrita e leitura do pipe
char* output;                   //  Leirura dos comandos enviados ao terminal
char input[INPUT_SIZE];         //  Input de leitura do pipe
char inputpipe[PIPE_SIZE];      //  Nome do pipe de leitura de dados enviados da par-shell       

/*===================================================
=         Função de escrita no pipe                 =
===================================================*/
void write_to_pipe(char* pipename){
    //  Abrir o pipe para escrita
    if((fd_write = open(pipename, O_WRONLY)) < 0){
       perror("Erro no open do write_to_pipe: ");
       exit(EXIT_FAILURE); 
    } 
    //  Escrever dados no pipe     
    if(write(fd_write,output,strlen(output)) < 0){
        perror("Erro no write do write_to_pipe: ");
    }
    //  Fechar o pipe apos a escrita
    if(close(fd_write) < 0){
        perror("Erro no close do write_to_pipe: ");
    }
    //  Limpar o output
    strcpy(output,"\0");
}

/*===================================================
=         Função de leitura do pipe                 =
===================================================*/
void read_from_pipe(char* pipename){
    //  Abrir o pipe para leitura
    if((fd_read = open(pipename, O_RDONLY)) < 0){
       perror("Erro no open do read_from_pipe: ");
       exit(EXIT_FAILURE); 
    }     
    //  Ler dados do pipe           
    if(read(fd_read,input,INPUT_SIZE) < 0){
        perror("Erro no write do read_from_pipe:");
    }
    //  Fechar pipe
    if(close(fd_read) < 0){
        perror("Erro no close do read_from_pipe: ");
    }
    //  Limpar o output
    strcpy(output,"\0");    
}

/*===================================================
=        Função de registo do terminal              =
===================================================*/
void register_terminal(char* pipename){
    //  Construir o nome do pipe associado ao terminal
    if(sprintf(inputpipe,"/tmp/terminal-%d*",getpid()) < 0){
        perror("Erro no sprintf: ");
        exit(EXIT_FAILURE);
    }
    //  Marcar o final da string porque os pid's podem ter um numero de algarismos diferentes
    strtok(inputpipe,"*"); 
    //  Garantir que nao existe nenhuma ligação com este nome antes de criar o pipe              
    if(unlink(inputpipe) < 0){
        if (errno != ENOENT) {
            perror("Erro no unlink do register_terminal: ");
            exit(EXIT_FAILURE);
        }
    }
    
    //  Criar o pipe associado ao terminal
    if(mkfifo(inputpipe, READWRITE_ONLY) < 0){
        perror("Erro no mkfifo do register_terminal: ");
        exit(EXIT_FAILURE);
    }
    //  Guardar o nome do pipe do terminal no output
    if((output = strdup(inputpipe)) == NULL){  //?????????????????????????????????????????????????????
        perror("Erro no strdup do register_terminal: ");
    }
    //  Enviar o nome deste pipe para ser registado na par-shell
    write_to_pipe(pipename);           
}

/*===================================================
=            Programa principal                     =
===================================================*/
int main(int argc, char* argv[]){
	char* argumentos[MAX_ARGS];
	char* namedpipe = argv[1];
	int nbytes, i;
    size_t len = 0;

    //  Registar o terminal e o seu pipe na par-shell
    register_terminal(namedpipe);   

	while(1) {
        //  Ler uma linha da linha de comandos
	    nbytes = getline(&output,&len,stdin);
        //  Obrigar a string de output a ser bem-formada com um "\0" no fim   
        output[strlen(output)-1] = '\0';

        if(nbytes <= 0){ 
    		continue;
    	}
        if (strcmp(output,"exit") == 0)
        /** 
         *  Sair do programa par-shell-terminal
         */
        {
            //  Eliminar a ligacao do pipe a este nome
            if(unlink(inputpipe) < 0)
                perror("Erro no unlink do exit: ");
            //  Sair da par-shell-terminal
            exit(EXIT_SUCCESS);         

        }

        if (strcmp(output,"stats") == 0)
        {
        /**  
         *  Imprime o numero de processos filho da par-shell 
         *  que estao em execucao e o tempo total de execucao da par-shell
         */
            sprintf(output,"stats %d",getpid());
            write_to_pipe(namedpipe);   //  
            read_from_pipe(inputpipe);  //  
            printf("%s\n",input);       //  
            if(fflush(stdout) < 0)
                perror("Erro no fflush do stats: ");
        }

        if (strcmp(output,"exit-global") == 0)
        {   
        /** 
         *  Terminar a par-shell e todos os terminais 
         */
            //  Enviar comando para a par-shell
            write_to_pipe(namedpipe);   
            //  Eliminar a ligacao do pipe a este nome
            if(unlink(inputpipe) < 0)
                perror("Error no unlink do exit-global: ");  
            //  Sair da par-shell-terminal        
            exit(EXIT_SUCCESS);        

        }
        else{
        /**
         *  Enviar executavel e argumentos para a par-shell fazer fork + execv
         */   
            write_to_pipe(namedpipe);
        }        

	}
}