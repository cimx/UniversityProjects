#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "QueueCheque.h"
#include "ArvoreCliente.h"

/* Projecto 2 IAED
   Grupo AL006:	Daniel Correia	80967
				Ines Xavier		81172
				Ines Leite		81328
*/

/*Prototipos das funcoes do programa */
void adicionaCheque(Q fila, A arvore);	/* Adiciona um Cheque a fila e atualiza a arvore de Clientes */
void processa(Q fila, A arvore);	/* Remove o Cheque mais antigo da fila e atualiza a arvore de Clientes */
void processaR(Q fila, A arvore);	/* Remove um Cheque da fila por referencia e atualiza a arvore de Clientes */
void infocheque(Q fila);	/*	Escreve as informacoes do Cheque correspondente a referencia dada */
void infocliente(A arvore);	/*	Escreve as informacoes do Cliente correspondente a referencia dada */
void info(A arvore);	/* Escreve as informacoes de cada Cliente ativo por ordem crescente de referencia */
void sair(A arvore);	/* Escreve o numero de clientes ativos do sistema, e o numero e valor total de cheques por processar */

/* Programa principal */
int main() {
	char comando[12];
	Q fila = QUEUEinit();
	A arvore = ARVOREinit();
	/* Menu de comandos */
	while (1) {
		scanf("%s", comando);
		if (strcmp(comando,"cheque") == 0) {
			adicionaCheque(fila,arvore);
			continue;
		}
		else if (strcmp(comando,"processa") == 0) {
			processa(fila,arvore);
			continue;
		}
		else if (strcmp(comando,"processaR") == 0) {
			processaR(fila,arvore);
			continue;
		}
		else if (strcmp(comando,"infocheque") == 0) {
			infocheque(fila);
			continue;
		}
		else if (strcmp(comando,"infocliente") == 0) {
			infocliente(arvore);
			continue;
		}
		else if (strcmp(comando,"info") == 0) {
			info(arvore);
			continue;
		}
		else if (strcmp(comando,"sair") == 0) {
			sair(arvore);
			/* Apaga a fila e arvore do sistema */
			QUEUEclean(fila);
			limpaArvore(arvore->head);
			free(arvore);
			return 0;
		}
		getchar();
	}
	return 0;
}

void adicionaCheque(Q fila, A arvore) {
	long int valor, refe, refb, refc;
	Cheque x;
	Cliente cliente_e,cliente_b;
	linkCliente flag_e,flag_b;
	/* Adiciona o Cheque a fila */
	scanf("%ld %ld %ld %ld", &valor, &refe, &refb, &refc);
	x = newCheque(refc,refe,refb,valor);
	QUEUEput(fila,x);

	/* Atualiza a arvore com as informacoes do Cliente emitente */
	flag_e = procuraCliente(arvore->head,refe);
	cliente_e = newCliente(refe,1,valor,0,0);
	if (flag_e == NULL)
		arvore->head = adicionaCliente(arvore->head,cliente_e);
	else {
		atualizaCliente(flag_e,cliente_e);
		freeCliente(cliente_e);
	}	
	/* Atualiza a arvore com as informacoes do Cliente beneficiario */
	flag_b = procuraCliente(arvore->head,refb);
	cliente_b = newCliente(refb,0,0,1,valor);
	if (flag_b == NULL) 
		arvore->head = adicionaCliente(arvore->head,cliente_b);
	else {
		atualizaCliente(flag_b,cliente_b);
		freeCliente(cliente_b);
	}
}

void processa(Q fila, A arvore) {
	Cheque x;
	Cliente update_e, update_b;

	/* Fila de cheques esta vazia */
	if (QUEUEempty(fila))
		printf("Nothing to process\n");
	else {
		x = QUEUEget(fila);
		/* Atualiza as informacoes dos clientes emitentes e beneficiarios do Cheque retirado */
		update_e = newCliente(x->emitente,-1,-x->valor,0,0);
		update_b = newCliente(x->beneficiario,0,0,-1,-x->valor);
		atualizaCliente(procuraCliente(arvore->head,x->emitente),update_e);
		atualizaCliente(procuraCliente(arvore->head,x->beneficiario),update_b);

		/* Liberta a memoria dos Clientes temporarios e do Cheque retirado */
		freeCliente(update_e);
		freeCliente(update_b);
		freeCheque(x);
	}		
}
void processaR(Q fila, A arvore) {
	long int refc;
	Cheque x;
	Cliente update_e,update_b;
	scanf("%ld", &refc);
	x = QUEUEdelete(fila,refc);

	/* O Cheque procurado nao existe na fila */
	if(x == NULL)
		printf("Cheque %ld does not exist\n", refc);

	else {
		/* Atualiza as informacoes dos clientes emitentes e beneficiarios do Cheque retirado */
		update_e = newCliente(x->emitente,-1,-x->valor,0,0);
		update_b = newCliente(x->beneficiario,0,0,-1,-x->valor);
		atualizaCliente(procuraCliente(arvore->head,x->emitente),update_e);
		atualizaCliente(procuraCliente(arvore->head,x->beneficiario),update_b);

		/* Liberta a memoria dos Clientes temporarios e do Cheque retirado */
		freeCliente(update_e);
		freeCliente(update_b);
		freeCheque(x);
	}
}

void infocheque(Q fila) {
	long int refc;
	scanf("%ld",&refc);
	mostraCheque(QUEUEsearch(fila,refc));
}

void infocliente(A arvore) {
	long int ref;
	linkCliente x;
	scanf("%ld", &ref);
	x = procuraCliente(arvore->head,ref);
	printf("Cliente-info: %ld %d %d %d %d\n", x->item->ref, x->item->nche, x->item->vche, x->item->nchb, x->item->vchb);
}

void info(A arvore) {
	Ativos clientes = (Ativos)malloc(sizeof(struct ativos));
	clientes->ncl = 0; clientes->nch = 0; clientes->vch = 0;
	Clientes_ativos(arvore->head,clientes);

	/* O sistema tem clientes ativos */
	if (clientes->ncl != 0)
		mostraArvoreOrdenada(arvore->head);
	/* O sistema nao tem clientes ativos */
	else
		printf("No active clients\n");

	free(clientes);
}

void sair(A arvore) {
	Ativos clientes = (Ativos)malloc(sizeof(struct ativos));
	clientes->ncl = 0; clientes->nch = 0; clientes->vch = 0;
	Clientes_ativos(arvore->head,clientes);
	printf("%d %d %d\n", clientes->ncl, clientes->nch, clientes->vch);
	free(clientes);
}