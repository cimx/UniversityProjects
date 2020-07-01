#ifndef ARVORECLIENTE_H
#define ARVORECLIENTE_H
#include "ClienteCheque.h"

/* Ponteiros para as estruturas */
typedef struct tree* A;	/* A - ponteiro para a estrutura tree */
typedef struct node* linkCliente;	/* linkCliente - ponteiro para a estrutura node */
typedef struct ativos* Ativos;	/* Ativos - ponteiros para a estrutura ativos */

/* Definicao de estruturas */
/* node - no da arvore */
struct node {
	Cliente item; /* informacao do cliente */
	linkCliente e, d; /* sub-arvore esquerda e direita */
	int altura; /* altura do no */
};

/* tree - guarda a head da arvore */
struct tree {linkCliente head; };

/* ativos - guarda as informacoes sobre os clientes ativos */
struct ativos {int ncl,nch,vch; };

/* Funcoes para a arvore de Clientes */
A ARVOREinit();	/* Inicializa a arvore */
linkCliente newNode(Cliente item, linkCliente esquerda, linkCliente direita);	/* Cria um node e devolve o seu ponteiro */	
int altura(linkCliente head);	/* Devolve a altura da arvore */
int equilibrio(linkCliente head);	/* Devolve o equilibrio de um node (diferenca entre as alturas) */
linkCliente AVLbalance(linkCliente head);	/* Equilibra a arvore e devolve a head */  
linkCliente maximo(linkCliente head);	/* Devolve o maximo de uma arvore */
void limpaArvore(linkCliente head);	/* Apaga a arvore e liberta a memoria correspondente */

/* Rotacoes */
linkCliente rotacaoLeft(linkCliente head);	/* Executa uma rotacao simples para a esquerda */
linkCliente rotacaoRight(linkCliente head);	/* Executa uma rotacao simples para a direita */
linkCliente rotacaoLeftRight(linkCliente head);	/* Executa uma rotação dupla esquerda direita */
linkCliente rotacaoRightLeft(linkCliente head);	/* Executa uma rotação dupla direita esquerda */

/* Clientes */
linkCliente adicionaCliente(linkCliente head, Cliente novo);	/* Adiciona o cliente a arvore */
linkCliente procuraCliente(linkCliente head, long int ref);	/* Procura o cliente por referencia e devolve o ponteiro do seu node */
void atualizaCliente(linkCliente cliente, Cliente update);	/* Atualiza as informacoes de um cliente existente na arvore */
void mostraArvoreOrdenada(linkCliente head);	/* Escreve as informacoes dos clientes ativos por ordem crescente da sua referencia */
void Clientes_ativos(linkCliente head, Ativos clientes); /*	Preenche a estrutura Ativos clientes com as informacoes sobre os clientes ativos */
#endif