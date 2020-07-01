#include <stdio.h>
#include <stdlib.h>
#include "ClienteCheque.h"

Cliente newCliente(long int ref, int nche, int vche, int nchb, int vchb) {
	Cliente x = (Cliente)malloc(sizeof(struct cliente));
	x->ref = ref;
	x->nche = nche;
	x->vche = vche;
	x->nchb = nchb;
	x->vchb = vchb;
	return x;
}

void freeCliente(Cliente a) {
	free(a);
}

void mostraCliente(Cliente a) {
	printf("*%ld %d %d %d %d\n",a->ref,a->nche,a->vche,a->nchb, a->vchb);
}

Cheque newCheque(long int refc, long int refe, long int refb, int valor) {
	Cheque x = (Cheque)malloc(sizeof(struct cheque));
	x->referencia = refc;
	x->emitente = refe;
	x->beneficiario = refb;
	x->valor = valor;
	return x;
}

void freeCheque(Cheque a) {
	free(a);
}

void mostraCheque(Cheque a) {
	printf("Cheque-info: %ld %ld %ld --> %ld\n", a->referencia, a->valor, a->emitente, a->beneficiario);
}