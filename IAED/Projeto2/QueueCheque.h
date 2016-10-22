#ifndef QUEUECHEQUE_H
#define QUEUECHEQUE_H
#include "ClienteCheque.h"

/* Ponteiros para as estruturas */
typedef struct queue* Q;	/* Q - ponteiro para a estrutura queue */
typedef struct QUEUEnode* linkCheque;	/* linkCheque - ponteiro para a estrutura QUEUEnode */

/* Definicao de estruturas */
/* QUEUEnode - elemento da fila */
struct QUEUEnode { 
 	Cheque item;	/* informacao do cheque */
 	linkCheque next;	/* elemento seguinte da fila */
};

/* queue - guarda a head e a tail da fila */
struct queue {linkCheque head, tail; };

/* Funcoes para a fila de Cheques */
Q QUEUEinit();	/* Inicializa a fila */
linkCheque NEW(Cheque item, linkCheque next);	/* Cria um elemento da fila e devolve o seu ponteiro */
int QUEUEempty(Q);	/* Verifica se a fila esta vazia */
void QUEUEclean(Q);	/* Apaga a fila e liberta a memoria correspondente */
void QUEUEput(Q,Cheque item); /* Adiciona o Cheque a fila */
Cheque QUEUEget(Q);	/* Retira o elemento mais antigo da fila e devolve o seu Cheque */
Cheque QUEUEdelete(Q,long int refc); /* Procura um Cheque por referencia, apaga-o da fila e devolve o Cheque apagado */
Cheque QUEUEsearch(Q, long int refc); /* Procura um Cheque por referencia e devolve o Cheque encontrado */
#endif