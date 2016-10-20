#include <stdio.h>
/* Projecto IAED - Rede de Bancos
/* Grupo AL006:	Daniel Correia	80967
				Ines Xavier		81172
				Ines Leite		81328
*/

/* Constantes */
#define MAXNOME		42
#define MAXBANCOS 	1000
#define	BOM			1
#define	MAU			0

/* Definicao da estrutura Banco */
typedef struct banco {
	char nome[MAXNOME];
	int classificacao;
	long int ref;
}Banco;

/* Prototipos das funcoes */
Banco adicionaBanco();
void classmauBanco();
void promoveBanco();
void emprestimo();
void amortizar();
void listagem1();
void listagem1_aux(int i);
void despromover();
void numerobancos();
void listarbancos();
void ligacoes();

/* Variaveis globais*/
Banco lista[MAXBANCOS];
int contador;
int dinheiro[MAXBANCOS][MAXBANCOS]; /* [linha][coluna] */
int parceiros[MAXBANCOS];

int main() {
/* Programa principal */
	int n;
	char comando;
	while (1) {
		comando = getchar();
		switch (comando) {
			case 'a': /* adicionaBanco */
				lista[contador] = adicionaBanco();
				contador += 1;
				break;
			case 'k': /* classmauBanco */
				classmauBanco();
				break;
			case 'r': /* promoveBanco*/
				promoveBanco();
				break;
			case 'e': /* emprestimo */
				emprestimo();
				break;
			case 'p': /* amortizar */
				amortizar();
				break;
			case 'l': /* listagem */
				scanf("%d", &n);
				if (n==0)
					listarbancos();
				else if (n == 1)
					listagem1();
				else if (n==2)
					ligacoes();
				break;
			case 'K': /* despromover */
				despromover();
				break;
			case 'x': /* termina o programa */
				numerobancos();
				return 0;
		}
		getchar();
	}
	return 0;
}
Banco adicionaBanco() {
/* Comando a - adiciona um banco a matriz Banco */
	Banco a;
	scanf("%s %d %ld", a.nome, &a.classificacao, &a.ref);
	return a;
}

void classmauBanco() {
/* Comando k - atribui a classificacao “0” (mau) a um banco */
	long int ref;
	int i=0;
	scanf("%ld", &ref);
	while (i <= contador) {
		if (lista[i].ref == ref) {
			lista[i].classificacao = MAU;
		}
		i++;
	}
}

void promoveBanco() {
/* Comando r - atribui a classificacao “1” (bom) a um banco */
	long int ref;
	int i=0;
	scanf("%ld", &ref);
	while (i <= contador) {
		if (lista[i].ref == ref) {
			lista[i].classificacao = BOM;
		}
		i++;
	}
}

void emprestimo() { 
/* Comando e - O banco de ref 1 empresta ao banco de ref 2 */
	long int ref1, ref2;
	int i, valor, linha=0, coluna=0;
	scanf("%ld %ld %d", &ref1, &ref2, &valor);
	for (i = 0; i < contador; i++) {
		if (ref1 == lista[i].ref)
			linha = i;
		else if (ref2 == lista[i].ref)
			coluna = i;
	}
	if (dinheiro[linha][coluna] == 0 && dinheiro[coluna][linha] == 0) {
		parceiros[coluna]+= 1;
		parceiros[linha] += 1;
	}
	dinheiro[linha][coluna] += valor;
}

void amortizar() { 
/* Comando p - O banco de ref1 paga ao banco de ref2 */
	long int ref1, ref2;
	int i, valor, coluna=0, linha=0;
	scanf("%ld %ld %d", &ref1, &ref2, &valor);
	for (i = 0; i < contador; i++)
	{
		if (ref1 == lista[i].ref)
			coluna = i;
		else if (ref2 == lista[i].ref)
			linha = i;
	}
	if (valor <= dinheiro[linha][coluna])
		dinheiro[linha][coluna] -= valor;

	if (dinheiro[coluna][linha] == 0 && dinheiro[linha][coluna] == 0) {
		parceiros[linha] -= 1;
		parceiros[coluna] -= 1;		
	}
}

void despromover() {
/* Comando K - Procura e desclassifica o banco “bom” com maior exposicao a divida de bancos “maus” */
	int i,j, indice=0, soma, maior_soma = 0;
	for (i = 0; i < contador; i++) {
		/* Procura um banco "bom" */
		if (lista[i].classificacao == BOM) {
			soma = 0;
			for (j = 0; j < contador; j++) {
			/* Calculo da divida de bancos maus com esse banco */
				if (lista[j].classificacao == MAU)
					soma += dinheiro[i][j]; 
			}
			/* Determina o banco "bom" com maior exposicao a divida de bancos "maus" */
			if (soma >= maior_soma) {
				maior_soma = soma;
				indice = i;
			}
		}
	}
	if (maior_soma != 0) {
		lista[indice].classificacao = MAU;
		printf("*");
		listagem1_aux(indice);
	}
	numerobancos();
}

void numerobancos() {
/* Comando x - escreve o numero total de bancos registados,seguidos do numero total de bancos bons */
	int i,contador2=0;
	for (i = 0; i < contador; i++) {
		if (lista[i].classificacao == BOM) {
			contador2++;
		}
	}
	printf( "%d %d\n", contador, contador2);
}

void listarbancos() {
/* Comando l 0 - Imprime todos os bancos ordenados pela ordem de introducao no sistema, em linhas distintas da forma:
ref nome classificacao */
	int i;	
	for (i = 0; i < contador; i++) {
		printf("%ld %s %d\n", lista[i].ref, lista[i].nome, lista[i].classificacao);
	}
}

void listagem1() {
/* Comando l 1 - Imprime todos os bancos ordenados pela ordem de introducao no sistema, em linhas distintas da forma:
ref nome classificacao inP outP outV outVM inV inVM */
	int i;
	for (i = 0; i < contador; i++) {
		listagem1_aux(i);
	}
}

void listagem1_aux(int i) {
/* Funcao auxiliar ao comando l 1 */
	int j, inp = 0, outp = 0, outv = 0, outvm = 0, inv = 0, invm = 0;
	for (j = 0; j < contador; j++) {
	/* Parceiros a quem o banco a tem uma divida - inp, inv, invm */
		if (dinheiro[j][i] > 0) {
			inp += 1;
			inv += dinheiro[j][i];
			if (lista[j].classificacao == MAU)
				invm += dinheiro[j][i];
		}
	/* Parceiros a quem o banco a emprestou - outp, outv, outvm */
		if (dinheiro[i][j] > 0) {
			outp += 1;
			outv += dinheiro[i][j];
			if (lista[j].classificacao == MAU)
				outvm += dinheiro[i][j];
		}
	}
	printf("%ld %s %d %d %d %d %d %d %d\n",lista[i].ref, lista[i].nome, lista[i].classificacao, inp, outp, outv, outvm, inv, invm);
}

void ligacoes() {
/* Comando l 2 - escreve a distribuicao do numero de bancos com exactamente k parceiros comerciais, em linhas distintas da forma:
k parceiros 	numero de bancos com k parceiros */
	int ligacoes,i,num_bancos;
	for (ligacoes = 0; ligacoes <= contador; ligacoes++) {
		num_bancos = 0;
		for (i=0; i < contador; i++) {
			if (parceiros[i] == ligacoes)
				num_bancos++;
		}
		if (num_bancos != 0)
			printf("%d %d\n", ligacoes, num_bancos);
	}
}
