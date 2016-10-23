/*Grupo 46
Carolina Ines Xavier 81172
Ines Leite 81328*/


/*RESOLVE MANUAL*/

%pedeMovimento/2
%pedeMovimento(C1,C2) - pede o movimento e chama a funcao cicloJogoManual
pedeMovimento(C,C):-  
			cicloJogoManual(C,C,_).
pedeMovimento(C1,C2):- 
			writeln('Qual o seu movimento?'),
			read(M),
			cicloJogoManual(C1,C2,M).

%cicloJogoManual/3
%cicloJogoManual(C1,C2,M) - efetua um ciclo de jogo de acordo com o movimento (M)
cicloJogoManual(C,C,_):- 
			writeln('Parabens!').
cicloJogoManual(C1,C2,M):-
			mov_legal(C1,M,_,C3), nl,
			escreve_matriz(C3), nl,
			pedeMovimento(C3,C2),!.
cicloJogoManual(C1,C2,_):-
			writeln('Movimento ilegal'),
			pedeMovimento(C1,C2).

%resolve_manual/2
%resolve_manual(C1,C2) - recebe a configuracao incial (C1) e final (C2), permite efetuar um jogo manual
resolve_manual(C1,C2):- 
			transformacao_possivel(C1,C2),
			escreve_matrizes(C1,C2),
			pedeMovimento(C1,C2),!.

/*PROCURA CEGA*/

%cicloJogoManual/4
%cicloJogoCego(C1,C2,Movs,Config) - efetua um ciclo de jogo cego
cicloJogoCego(C,C,Movs,_) :- 
			reverse(Movs,I),
			escreve_movimentos(I).
cicloJogoCego(C1,C2,Movs,Config):- 
			mov_legal(C1,M,P,C3),
			\+member(C3,Config),
			cicloJogoCego(C3,C2,[[P,M]|Movs],[C3|Config]),!.

%resolve_cego/2
%resolve_cego(C1,C2) - recebe a configuracao incial (C1) e final (C2), permite efetuar um jogo cego
resolve_cego(C1,C2):- 
			transformacao_possivel(C1,C2),
			escreve_matrizes(C1,C2),
			cicloJogoCego(C1,C2,[],[C1]),!.

/*PROCURA INFORMADA*/

%cicloJogoInformado/6
%cicloJogoInformado(C1,C2,Movs,Abertos,Fechados,G) - efetua um ciclo de jogo informado
cicloJogoInformado(C2,C2,Movs,_,_,_):- 
			reverse(Movs,I),
			escreve_movimentos(I).
cicloJogoInformado(C1,C2,Movs,Abertos,Fechados,G):- 
			findall(C3,mov_legal(C1,M,P,C3),A),
			adicionaF(A,C2,G,Abertos1),
			eliminaElem(Abertos1,Fechados,Abertos2),
			reverse(Abertos2,Abertos22),
			append(Abertos22,Abertos,Abertos3),
			escolhe(Abertos3,Expandir),
			removePrimeiroElem(Expandir,Expandir1),
			mov_legal(C1,M,P,Expandir1),
			append(Fechados,[Expandir],Fechados1),
			delete(Abertos3,Expandir,Abertos4),
			G1 is G+1,
			cicloJogoInformado(Expandir1,C2,[[P,M]|Movs],Abertos4,Fechados1,G1).

%resolve_info_h/2	
%resolve_info_h(C1,C2) - recebe a configuracao incial (C1) e final (C2), permite efetuar um jogo informado
resolve_info_h(C1,C2):- 
			transformacao_possivel(C1,C2),
			escreve_matrizes(C1,C2),
			adicionaF([C1],C2,0,C),
			cicloJogoInformado(C1,C2,[],[],C,1),!.

/*________________________________FUNCOES AUXILIARES___________________________________*/


%eliminaElem/3
%eliminaElem(L1,L2,Lfinal) - afirma que a lista Lfinal e a lista L1 sem os elementos da lista L2
eliminaElem(L1,L2,Lfinal):- 
			eliminaElem(L1,L2,Lfinal,[]),!.
eliminaElem([],_,Aux,Aux).
eliminaElem([P|R],L2,Lfinal,Aux):- 
			member(P,L2), 
			eliminaElem(R,L2,Lfinal,Aux).
eliminaElem([P|R],L2,Lfinal,Aux):- 
			\+member(P,L2),
			eliminaElem(R,L2,Lfinal,[P|Aux]).

%difere/3
%difere(L1,L2,H) - afirma que a lista L1 difere da lista L2 em G posicoes (exceto o 0)
difere(L1,L2,H):-
			difere(L1,L2,H,0),!. 
difere([],[],Aux,Aux). 
difere([0|R],[_|R1],H,Aux):-
			difere(R,R1,H,Aux),!. 
difere([P|R],[P|R1],H,Aux):-
			difere(R,R1,H,Aux).
difere([_|R],[_|R1],H,Aux):-
			Aux1 is Aux + 1,
			difere(R,R1,H,Aux1).

%adicionaF/4
%adicionaF(Lista,C2,G,L) - afirma que a lista L e a lista Lista com o F acrescentado ao inicio de cada um dos seus elementos
adicionaF(Lista,C2,G,L):-
			adicionaF(Lista,C2,G,L,[]).
adicionaF([],_,_,Aux,Aux).
adicionaF([P|R],C2,G,L,Aux):-
			difere(P,C2,H),
			F is G+H,
			add(F,P,L1),
			adicionaF(R,C2,G,L,[L1|Aux]).

%add/3
%add(X,L,L1) - afirma que L1 e a lista L acrescentando o valor X no inicio
add(X, L, [X|L]).

%removePrimeiroElem/2
%removePrimeiroElem(L,L1) - afirma que L1 e a lista L sem o seu primeiro elemento
removePrimeiroElem([_|R],R). 

%escolhe/2
%escolhe(L,Expandir) - afirma que a lista Expandir e o estado a expandir da lista L
escolhe([P|R],Expandir):-
			prim_elem(P,Ac),
			escolhe(R,Expandir,P,Ac).
escolhe([],Aux,Aux,_).
escolhe([P|R],Expandir,_,Ac):-
			prim_elem(P,Prim),
			Prim<Ac,
			escolhe(R,Expandir,P,Prim),!.
escolhe([_|R],Expandir,Aux,Ac):-
			escolhe(R,Expandir,Aux,Ac).

%prim_elem/2
%prim_elem(L,P) - afirma que P e o primeiro elenento da listsa L
prim_elem([P|_],P).

%possivel_mover/3
%possivel_mover(C1,M,P) - afirma que e possivel fazer o movimento M com a peca P da lista C1
possivel_mover(C1,c,P):-
			valor_posicao(C1,Pos_zero,0),
			Pos_zero=\=6, Pos_zero=\=7, Pos_zero=\=8,
			Pos is Pos_zero+3,
			valor_posicao(C1,Pos,P).
possivel_mover(C1,b,P):- 
			valor_posicao(C1,Pos_zero,0),
			Pos_zero=\=0, Pos_zero=\=1, Pos_zero=\=2,
			Pos is Pos_zero-3,
			valor_posicao(C1,Pos,P). 
possivel_mover(C1,e,P):-
			valor_posicao(C1,Pos_zero,0),
			Pos_zero=\=2, Pos_zero=\=5, Pos_zero=\=8,
			Pos is Pos_zero+1,
			valor_posicao(C1,Pos,P).
possivel_mover(C1,d,P):-
			valor_posicao(C1,Pos_zero,0),
			Pos_zero=\=0, Pos_zero=\=3, Pos_zero=\=6, 
			Pos is Pos_zero-1,
			valor_posicao(C1,Pos,P).
%mov_legal/4
%mov_legal(C1,M,P,C2) - afirma que a configuracao C2 e obtida da configuracao C1, fazendo o movimento M,com a peca P.
mov_legal(C1,c,P,C2):-
			possivel_mover(C1,c,P),
			troca(C1,C2,0,P).
mov_legal(C1,b,P,C2):-
			possivel_mover(C1,b,P),
			troca(C1,C2,0,P).
mov_legal(C1,e,P,C2):-
			possivel_mover(C1,e,P), 
			troca(C1,C2,0,P).
mov_legal(C1,d,P,C2):-
			possivel_mover(C1,d,P),
			troca(C1,C2,0,P).


%valor_posicao/3
%valor_posicao(L,Pos,V) - afirma que V e o valor na posicao Pos da lista L
valor_posicao(L,Pos,V):-
			valor_posicao(L,Pos,V,0).
valor_posicao([V|_],Pos,V,Pos):-!.
valor_posicao([_|R],Pos,V,Cont):-
			Cont1 is Cont+1,
			valor_posicao(R,Pos,V,Cont1).

%troca/4
%troca(C1,C2,V1,V2) - afirma que a lista C2 e a C1 mas com os valores V1 e V2 trocados
troca([],[],_,_).
troca([V1|R],[V2|R1],V1,V2):- 
			troca(R,R1,V1,V2),!.
troca([V2|R],[V1|R1],V1,V2):- 
			troca(R,R1,V1,V2),!.
troca([V3|R],[V3|R1],V1,V2):- 
			troca(R,R1,V1,V2),!.   

%escreve_matrizes/2                        
%escreve_matrizes(X,Y) - escreve as matrizes X e Y separadas por uma seta
escreve_matrizes([A, B, C, D, E, F, G, H, I],
                 [J, K, L, M, N, O, P, Q, R]) :-
			write('Transformacao desejada:'), nl, 
			escreve(A), escreve(B), escreve(C),  
			write('    '), 
			escreve(J), escreve(K), escreve(L),nl, 
			escreve(D), escreve(E), escreve(F), 
			write(' -> '), 
			escreve(M), escreve(N), escreve(O), nl,
			escreve(G), escreve(H), escreve(I), 
			write('    '), 
			escreve(P), escreve(Q), escreve(R), nl.

%escreve_matriz/1
%escreve_matriz(L) - escreve a matriz L
escreve_matriz([]).
escreve_matriz([X ,Y, Z |W]):- 
			escreve(X), escreve(Y), escreve(Z), nl,
			escreve_matriz(W).

%escreve/1
%escreve(N) - escreve o algarismo N (se este for igual a 0 escreve um espaco vazio)
escreve(N) :- N=0, write('   ').
escreve(N) :- N<10, write(' '), write(N), write(' ').

%escreve_movimentos/1
%escreve_movimentos(L) - escreve os movimentos recebidos sob a forma de lista de pares
escreve_movimentos([[P,M] | []]) :- 
			write('mova a peca '), 
			write(P), 
			traduz(M, Mp), 
			write(Mp),
			write('.'), nl.

escreve_movimentos([[P,M] | R]) :-
			write('mova a peca '), 
			write(P), 
			traduz(M, Mp), 
			write(Mp), nl, 
			escreve_movimentos(R).


%traduz/2
%traduz/2 - e um predicado auxiliar de escreve_movimentos
traduz(c,' para cima').
traduz(b,' para baixo').
traduz(e,' para a esquerda').
traduz(d,' para a direita').


/*_______________________________________BONUS_______________________________________*/


%transformacao_possivel/2
%transformacao_possivel(C1,C2) - afirma que e possivel transformar a lista C1 na lista C2
transformacao_possivel(C1,C2):-
			calcula_menores(C1,M1),
			calcula_menores(C2,M2),
			M1 mod 2 =:= M2 mod 2.

%calcula_menores/2
%calcula_menores(C,M) - afirma que M e a soma do numero de numeros que sucedem e que sao inferiores a cada um dos elementos da lista C
calcula_menores(C,M):-
			calcula_menores(C,C,M,0).
calcula_menores([],_,Ac,Ac).
calcula_menores([P|R],C,M,Ac):-
			valor_posicao(C,Pos,P),
			soma(P,S,C,Pos),
			Ac1 is Ac+S,
			calcula_menores(R,C,M,Ac1).

%soma/4
%soma(P,S,C,Pos) - afirma que S e a soma dos elementos menores de P que se encontram a seguir a posicao Pos  da lista C
soma(P,S,C,Pos):- soma(P,S,C,0,Pos,0),!.
soma(_,Ac,[],Ac,_,_).
soma(P,S,[_|R],Ac,Pos,Cont):-
			Pos>=Cont,
			Cont1 is Cont+1 , 
			soma(P,S,R,Ac,Pos,Cont1).
soma(P,S,[0|R],Ac,Pos,Cont):-
			Cont1 is Cont+1,  
			soma(P,S,R,Ac,Pos,Cont1).
soma(P,S,[P1|R],Ac,Pos,Cont):-
			P1<P, 
			Ac1 is Ac+1,
			Cont1 is Cont+1,
			soma(P,S,R,Ac1,Pos,Cont1).
soma(P,S,[P1|R],Ac,Pos,Cont):-
			P1>P,
			Cont1 is Cont+1,
			soma(P,S,R,Ac,Pos,Cont1).