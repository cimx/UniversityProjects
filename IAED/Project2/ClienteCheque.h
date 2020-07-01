#ifndef CLIENTECHEQUE_H
#define CLIENTECHEQUE_H
/* Definicao da estrutura Cheque */
typedef struct cheque {
	long int valor;			/* valor do cheque */
	long int referencia;	/* referencia do cheque */
	long int emitente;		/* referencia do cliente emitente */
	long int beneficiario;	/* referencia do cliente beneficiario */
}*Cheque;	/* Cheque - ponteiro para a estrutura cheque */

/* Definicao da estrutura Cliente */
typedef struct cliente {
	long int ref;	/* referencia do cliente */
	int nche;	/* numero de cheques emitidos */ 
	int vche;	/* valor dos cheques emitidos */ 
	int nchb;	/* numero de cheques em que e beneficiario */
	int vchb;	/* valor dos cheques em que e beneficiario */
}*Cliente;	/* Cliente - ponteiro para a estrutura cliente */

/* Funcoes para Cliente */
Cliente newCliente(long int ref, int nchb, int vchb, int nche, int vche);	/* Cria um Cliente e devolve o seu ponteiro */
void mostraCliente(Cliente a); 	/* Escreve as informacoes do Cliente */
void freeCliente(Cliente a);	/* Liberta a memoria do Cliente */

/* Funcoes para Cheque */
Cheque newCheque(long int refc, long int refe, long int refb, int valor);	/* Cria um Cheque e devolve o seu ponteiro */
void mostraCheque(Cheque a);	/* Escreve as informacoes do Cheque */
void freeCheque(Cheque a);	/* Liberta a memoria do Cheque */
#endif