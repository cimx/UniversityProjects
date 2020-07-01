#include <stdio.h>
#include <stdlib.h>
#include "ArvoreCliente.h"

A ARVOREinit() {
	A arvore = (A)malloc(sizeof(struct tree));
  	arvore->head = NULL;
  	return arvore;
}

linkCliente newNode(Cliente item, linkCliente esquerda, linkCliente direita) {
	linkCliente x = (linkCliente) malloc(sizeof(struct node));
	x->item = item;
	x->e = esquerda;
	x->d = direita;
	x->altura = 1;
	return x;
}

int altura(linkCliente head) {
	if (head == NULL) 
		return 0;
	return head->altura;
}

linkCliente maximo(linkCliente head) {
	linkCliente atual = head;
	while(atual != NULL) {
		if(atual->d == NULL)
			return atual;
		else
			atual = atual->d;
	}
	return atual;
}

linkCliente rotacaoLeft(linkCliente head) {
	int altura_esquerda, altura_direita;
	/* Rotacao para a esquerda */
	linkCliente x = head->d;
	head->d = x->e;
	x->e = head;

	/*Atualizacao das alturas dos nodes envolvidos */
	altura_esquerda = altura(head->e);
	altura_direita = altura(head->d);
	head->altura = altura_esquerda > altura_direita ? altura_esquerda + 1 : altura_direita + 1;

	altura_esquerda = altura(head->e);
	altura_direita = altura(x->d);
	x->altura = altura_esquerda > altura_direita ? altura_esquerda + 1 : altura_direita + 1;

	return x;
}

linkCliente rotacaoRight(linkCliente head) {
	int altura_esquerda, altura_direita;
	/* Rotacao para a direita */
	linkCliente x = head->e;
	head->e = x->d;
	x->d = head;

	/*Atualizacao das alturas dos nodes envolvidos */
	altura_esquerda = altura(head->e);
	altura_direita = altura(head->d);
	head->altura = altura_esquerda > altura_direita ? altura_esquerda + 1 : altura_direita + 1;

	altura_esquerda = altura(x->e);
	altura_direita = altura(head->d);
	x->altura = altura_esquerda > altura_direita ? altura_esquerda + 1 : altura_direita + 1;

	return x; 
}

linkCliente rotacaoLeftRight(linkCliente head) {
	if (head==NULL) 
		return head;

	head->e = rotacaoLeft(head->e);
	return rotacaoRight(head); 
}

linkCliente rotacaoRightLeft(linkCliente head) {
	if (head==NULL) 
		return head;

	head->d = rotacaoRight(head->d); 
	return rotacaoLeft(head); 
}

int equilibrio(linkCliente head) { 
	if (head == NULL) 
		return 0; 
	return altura(head->e) - altura(head->d); 
} 

linkCliente AVLbalance(linkCliente head) {  
	int balanceFactor, altura_esquerda, altura_direita;
	if (head==NULL)
		/* Arvore vazia */ 
		return head;

	balanceFactor = equilibrio(head); 
	if (balanceFactor > 1) {
		/* Desequiblirio a esquerda */ 
		if (equilibrio(head->e) > 0) 
			head = rotacaoRight(head); 
		else                   
			head = rotacaoLeftRight(head);  
	} 
	else if (balanceFactor < -1) {
		/* Desequilibrio a direita */ 
		if (equilibrio(head->d) < 0) 
			head = rotacaoLeft(head); 
		else                   
			head = rotacaoRightLeft(head); 
	} 
	
	else {
		/* Atualiza as alturas dos nodes */
		altura_esquerda = altura(head->e);
		altura_direita = altura(head->d);
		head->altura = altura_esquerda > altura_direita ?  altura_esquerda + 1 : altura_direita + 1;
	}

	return head; 
} 

linkCliente adicionaCliente(linkCliente head, Cliente novo) 
{
	if (head == NULL) 
		/* Arvore vazia */
		return newNode(novo, NULL, NULL);
		
	if (novo->ref < head->item->ref)
		/* Cliente novo e menor que a head -> esquerda */
		head->e = adicionaCliente(head->e, novo);

	if (novo->ref > head->item->ref)
		/* Cliente novo e maior que a head -> direita */
		head->d = adicionaCliente(head->d, novo);
	
	/* Equilibra a arvore depois de adicionar o cliente novo */
	head = AVLbalance(head);
	return head;
}

linkCliente procuraCliente(linkCliente head, long int ref) {
	linkCliente atual = head;
	while (atual != NULL) {
		/* O cliente esta na sub-arvore esquerda */
		if (ref < atual->item->ref)
			atual = atual->e;
		/* O cliente esta na sub-arvore direita */
		else if (ref > atual->item->ref)
			atual = atual->d;
		/* Encontrei o cliente */
		else
			return atual;
	}
	/* Nao encontrei o cliente na arvore */
	return NULL;
}

void atualizaCliente(linkCliente cliente, Cliente update) {
	cliente->item->nche += update->nche;
	cliente->item->vche += update->vche;
	cliente->item->nchb += update->nchb;
	cliente->item->vchb += update->vchb;
}

void mostraArvoreOrdenada(linkCliente head)
{
  	if (head == NULL) 	
	    return;
  	mostraArvoreOrdenada(head->e);
  	if(head->item->nche == 0 && head->item->nchb == 0)
  		mostraArvoreOrdenada(head->d);
  	else {
  		mostraCliente(head->item);
  		mostraArvoreOrdenada(head->d);
  	}
}

void Clientes_ativos(linkCliente head, Ativos clientes)	{
  	if (head == NULL)
    	return;

    Clientes_ativos(head->e,clientes);
    /* O cliente e ativo */
  	if (head->item->nche != 0 || head->item->nchb != 0) {
  		clientes->ncl++;
  		/* Basta utilizar as informacoes dos emitentes */
  		if(head->item->nche != 0) {
			clientes->nch += head->item->nche; 
			clientes->vch += head->item->vche;
		}	
  	}
  	Clientes_ativos(head->d,clientes);
  	return;
}

void limpaArvore(linkCliente head) {
	if (head == NULL)
		return;
	limpaArvore(head->e);
	limpaArvore(head->d);
	/* Liberta a memoria do cliente */
	freeCliente(head->item);
	/* Liberta a memoria do node */
	free(head);
	return;
}