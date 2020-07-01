#include <stdio.h>
#include <stdlib.h>
#include "QueueCheque.h"

Q QUEUEinit() {
    Q fila = (Q)malloc(sizeof(struct queue));
	fila->head = NULL; 
	fila->tail = NULL; 
	return fila;
}

int QUEUEempty(Q fila) { 
	return fila->head == NULL; 
} 

void QUEUEclean(Q fila) {
    while(fila->head != NULL)
        freeCheque(QUEUEget(fila));
    free(fila);
}

linkCheque NEW(Cheque item, linkCheque next) { 
	linkCheque x = (linkCheque) malloc(sizeof(struct QUEUEnode)); 
	x->item = item; 
	x->next = next; 
	return x; 
} 

void QUEUEput(Q fila,Cheque item) { 
    /* Fila vazia */
	if (fila->head == NULL) { 
	   fila->tail = NEW(item, fila->head);
	   fila->head = fila->tail;
	   return; 
    } 
    fila->tail->next = NEW(item, fila->tail->next);
    fila->tail = fila->tail->next;
}

Cheque QUEUEget(Q fila) { 
	linkCheque t = fila->head->next;
	Cheque x;
	x = fila->head->item;
	free(fila->head);
	fila->head = t;
	return x;
}

Cheque QUEUEdelete(Q fila,long int refc) {
	linkCheque atual, anterior;
	Cheque x;
	anterior = NULL;
	for (atual = fila->head; atual !=NULL; anterior = atual, atual = atual->next) {
        /* Encontrei o Cheque */
        if (atual->item->referencia == refc) {
            /* O Cheque esta no inicio da fila */
            if (anterior == NULL) {
	            fila->head = atual->next;
                /* A fila tem um unico elemento */
	            if (atual->next == NULL) 
	                fila->tail = NULL;
	            x = atual->item;
	            free(atual);
	            return x;
            }
            else {
               if(atual->next == NULL) {
                   /* O Cheque esta no fim da fila */
                   fila->tail = anterior;
                   anterior->next = atual->next;
                   x = atual->item;
                   free(atual);
                   return x;
               }
               /* O Cheque esta no meio da fila */
                anterior->next = atual->next;
                x = atual->item;
                free(atual);
                return x;
            }
	    }
	}
    /* Nao encontrei o Cheque */
	return NULL;
}

Cheque QUEUEsearch(Q fila, long int refc) {
    linkCheque atual;
	for (atual = fila->head; atual !=NULL; atual = atual->next) {
        /* Encontrei o Cheque */
        if (atual->item->referencia == refc) 
            return atual->item;
	}
    return NULL;
}