; ------------------------------Projecto Tron------------------------------;
; Grupo 29
; Turno 13
; 81172 Carolina Ines Maltez Xavier
; 81590	Bernardo Dias Simoes
; 81888	Pedro Miguel Carvalho Caldeira


;Constantes
CURSOR 			EQU	FFFCh
IO_WRITE		EQU	FFFEh
SP_INICIAL      	EQU     FDFFh
INT_MASK_ADDR		EQU	FFFAh
INT_MASK		EQU	1000000000000010b
INT_MASK_JOGO		EQU	1000101010000001b
IO_DISPLAY      	EQU     FFF0h
CONTROLOLCD		EQU	FFF4h
ESCREVELCD		EQU	FFF5h
CONTAGEM		EQU	FFF6h
CONTROLO		EQU	FFF7h
LEDS			EQU	FFF8h
INTERRUPTORES		EQU	FFF9h
FIM			EQU	'K'
VECTORUP		EQU	FF00h
VECTORDOWN		EQU	0100h
VECTORLEFT		EQU	FFFFh
VECTORRIGHT		EQU	0001h
NIBBLE_MASK     	EQU     000fh
DEC_MASK		EQU	0009h
NIBBLE_DEL		EQU	fff0h
NUM_NIBBLES     	EQU     4
BITS_PER_NIBBLE 	EQU     4
POS1LINHABEMV		EQU	0C21h
POS2LINHABEMV		EQU	0D18h


;VARIAVEIS/STRINGS
			ORIG    8000h
TempoMax		WORD	0000h
SemRasto		WORD	0000h
J1Sentido		WORD	0001h
J2Sentido		WORD	FFFFh
MoveJ1DFlag		WORD	0000h
MoveJ2DFlag		WORD	0000h
MoveJ1EFlag		WORD	0000h
MoveJ2EFlag		WORD	0000h
VARIAVELLEDS		WORD	0000h
Posbon1Ant		WORD	0000h
Posbon2Ant		WORD	0000h
Posbon1			WORD	0B18h
Posbon2			WORD	0B38h
Contador		WORD	0000h
ColisaoJ1		WORD	0h
ColisaoJ2		WORD	0h
NIVEL1_vel		WORD	7
NIVEL2_vel		WORD	5
NIVEL3_vel		WORD	3
NIVEL4_vel		WORD	2
NIVEL5_vel		WORD	1
NIVEL2_tempo		WORD	100d
NIVEL3_tempo		WORD	100d
NIVEL4_tempo		WORD	200d
NIVEL5_tempo		WORD	200d
ScoreJ1Dig1		WORD	0030h
ScoreJ1Dig2		WORD	0030h
ScoreJ2Dig1		WORD	0030h
ScoreJ2Dig2		WORD	0030h

Linha1			STR	'+------------------------------------------------+' , FIM
Linhas			STR	'|                                                |' , FIM
RastosP1		STR	'+',FIM
RastosP2		STR	'O',FIM
Particula1		STR	'X',FIM
Particula2		STR	'#',FIM
BatemdeCabeca		STR	'@',FIM
LinhaBemV1		STR	'Bem-vindo ao TRON',FIM
LinhaBemV2		STR	'Prima o interruptor I1 para comecar'    ,FIM
Frame			STR	'+-------------------------------------+',FIM
LinhaRec		STR	'|Prima o interruptor I1 para recomecar|',FIM
MSGLCD			STR	'TEMPO MAX: 0000sJ1: 00    J2: 00'	 ,FIM 
Empate			STR	'|                Empate               |',FIM
GanhaJog1		STR	'|         Ganhou o Jogador 1 (X)      |',FIM
GanhaJog2		STR	'|         Ganhou o Jogador 2 (#)      |',FIM
Rastos			TAB	960

;Tabela de interrupcoes
			ORIG	FE00h
INT0			WORD	MoveJ1E
INT1			WORD	Comecar

			ORIG	FE07h
INT7			WORD	MoveJ2E

			ORIG	FE09h
INT9			WORD	MoveJ2D

			ORIG	FE0Bh
INT12			WORD	MoveJ1D

			ORIG	FE0Fh
INT15			WORD	Temporizador

Comecar:		MOV	R6,0001h
				RTI
Temporizador:		MOV	R3,0001h
				RTI
				
MoveJ2E:		CMP	M[MoveJ2DFlag],R0
			BR.NZ	NaoMuda1
			INC	M[MoveJ2EFlag]
NaoMuda1:		RTI

MoveJ1E:		CMP	M[MoveJ1DFlag],R0
			BR.NZ	NaoMuda2
			INC	M[MoveJ1EFlag]
NaoMuda2:		RTI

MoveJ2D:		CMP	M[MoveJ2EFlag],R0
			BR.NZ	NaoMuda3
			INC	M[MoveJ2DFlag]
NaoMuda3:		RTI
			
MoveJ1D:		CMP	M[MoveJ1EFlag],R0
			BR.NZ	NaoMuda4
			INC	M[MoveJ1DFlag]
NaoMuda4:		RTI



;Inicializacao

			ORIG	0000h
			MOV     R7, SP_INICIAL
			MOV     SP, R7
			MOV     R7, INT_MASK
			MOV     M[INT_MASK_ADDR], R7
			MOV	R7, FFFFh
			MOV	M[CURSOR], R7
;ESTRUTURA DO JOGO
;Conjunto de Rotinas que permite a realizacao do jogo TRON
;Entradas:M[INT_MASK_ADDR]
;Saidas: -
INICIO:			MOV	R6,R0
			DSI
			CALL	Bem_vindo
			ENI
			CMP	R6,R0
			BR.Z	-2
			DSI
			CALL	IniciaLCD
OutroJogo:		CALL	DesenhaMoldura
			CALL	Movimentos
			MOV	R7,INT_MASK_JOGO
			MOV     M[INT_MASK_ADDR], R7
			ENI
			CALL	Ronda
			DSI
			MOV     R7, INT_MASK
			MOV     M[INT_MASK_ADDR], R7
			CALL	RESULTADO
			MOV	R6,R0
			ENI
Espera: 		CMP	R6,1h
			BR.NZ	Espera
			DSI
			CALL	Reset
			BR	OutroJogo

				
;Bem-Vindo - Escreve a mensagem de Boas Vindas na janela de Texto
;Entradas: STR -LINHABEMV1 ; LINHABEMV2
;Saidas: -
Bem_vindo:		PUSH	R1
			PUSH	R2
			MOV	R1,POS1LINHABEMV
			MOV	R2,LinhaBemV1
			Call	EscString
			MOV	R2,LinhaBemV2
			MOV	R1,0D18h
			Call	EscString
			POP	R2
			POP	R1				
			RET
			
;IniciaLCD - Inicia o LCD Liquido
;Entradas: VARS - M[CONTROLOLCD] ; M[ESCREVELCD]
;Saidas: -
IniciaLCD:		PUSH	R1
			PUSH	R2
			PUSH	R3
			MOV	R1,8000h
			MOV	R2,MSGLCD
CicloIniciaLCD:		MOV	M[CONTROLOLCD],R1
			MOV     R3, M[R2]
			MOV	M[ESCREVELCD],R3
			INC	R1
			INC     R2
			CMP	R1,8020h
			BR.NZ   CicloIniciaLCD			
FIMLCD:			POP	R3
			POP	R2
			POP	R1
			RET
				
;DesenhaMoldura - Desenha a Moldura inicial de Jogo
;Entradas: STR - Linha1 ; Linhas		
;Saidas: -			
DesenhaMoldura:		PUSH	R1
			PUSH	R2
			PUSH	R3
			MOV 	R1,0110h
			MOV	R2,Linha1
			Call	EscString
			MOV	R2,Linhas
			MOV	R3,20
EscLinhas:		CMP	R3,R0
			BR.Z	EscUltima
			ADD	R1,0100h
			Call	EscString
			DEC	R3
			BR	EscLinhas
EscUltima:		MOV	R2,Linha1
			ADD	R1,0100h
			Call	EscString
			POP	R3
			POP	R2
			POP	R1
			RET

;Ronda - Realiza uma Ronda Completa de jogo
;Entradas: M[ColisaoJ1],M[ColisaoJ2]
;Saidas -
Ronda:			PUSH	R1
			PUSH	R2
			PUSH	R3
			PUSH	R4
			PUSH	R5
			PUSH	R6
			MOV	R5,0Ah
			MOV	R6,4h
			MOV	R4,NIVEL1_vel	;Posicao de memoria com velocidade para o nivel actual
			MOV	R3,NIVEL2_tempo	;Posicao de memoria com tempo para o nivel seguinte
NivelSeguinte:		MOV	R2,M[R3]		;Tempo para o nivel seguinte
InputVel:		MOV	R1,M[R4]		;Velocidade para o nivel actual
Conta:			CMP	R1,R0			;se a velocidade ja chegou a 
			BR.Z	InputVel		;0 reinicia-se com a velocidade do nivel actual.
			CALL    contar_decseg	;Senao conta-se uma decima de segundo
			CMP	R0,M[ColisaoJ1]	;E verificada a colisao de uma das particulas
			BR.NZ	FIMJOGO			
			CMP	R0,M[ColisaoJ2]
			BR.NZ	FIMJOGO
			DEC	R5
			BR.NZ	NaoIncCont
			CALL	ContDEC
			CALL    EscCont
			MOV	R5,0Ah
NaoIncCont:		DEC	R1
			BR.NZ	NaoAnda			;Verifica se ja passou o tempo para o movimento
			Call	VerificaDireccao	;Verifica se foi alterado o sentido de algum Jogador
			CALL	Movimentos		;e realiza os movimentos
NaoAnda:		CALL	Pausa
			DEC	R2
			BR.NZ	Conta
			CMP	R6,R0
			BR.Z	Conta
			CALL	ActualizaLeds
			INC	R3				;Passa a ter a posicao de memoria com o tempo para o nivel seguinte
			INC	R4				;Passa a ter a posicao de memoria com a velocidade para o nivel actual
			DEC	R6
			JMP	NivelSeguinte
FIMJOGO:		POP	R6
			POP	R5
			POP	R4
			POP	R3
			POP	R2
			POP	R1
			RET
;VerificaDireccao - Actualiza os sentidos dos movimentos de cada particula
;Entradas: M[MoveJ1DFlag];M[J1Sentido];M[MoveJ1EFlag];M[MoveJ2DFlag];
;	   M[MoveJ2EFlag];M[J2Sentido]
;Saidas:-
VerificaDireccao:	CMP	M[MoveJ1DFlag],R0
			BR.Z	J1NaoViraDireita
			PUSH	M[J1Sentido]
			PUSH	M[MoveJ1EFlag]
			Call	AlteraSentido
			POP	M[J1Sentido]
					
J1NaoViraDireita:	CMP	M[MoveJ1EFlag],R0
			BR.Z	J1NaoViraEsquerda
			PUSH	M[J1Sentido]
			PUSH	M[MoveJ1EFlag]
			Call	AlteraSentido
			POP	M[J1Sentido]
					
J1NaoViraEsquerda:	CMP	M[MoveJ2DFlag],R0
			BR.Z	J2NaoViraDireita
			PUSH	M[J2Sentido]
			PUSH	M[MoveJ2EFlag]
			Call	AlteraSentido
			POP	M[J2Sentido]
						
J2NaoViraDireita:	CMP	M[MoveJ2EFlag],R0
			BR.Z	J2NaoViraEsquerda
			PUSH	M[J2Sentido]
			PUSH	M[MoveJ2EFlag]
			Call	AlteraSentido
			POP	M[J2Sentido]
						
J2NaoViraEsquerda:	MOV	M[MoveJ1DFlag],R0;No fim coloca as flag de alteracao de sentido a 0
			MOV	M[MoveJ2DFlag],R0
			MOV	M[MoveJ2EFlag],R0
			MOV	M[MoveJ1EFlag],R0
			RET
;AlteraSentido - Altera o sentido que a particula ira tomar consoante o sentido actual
;Entradas: M[J1Sentido]/M[J2Sentido] ; M[MoveJ2EFlag]/M[MoveJ1EFlag]
;Saidas: M[J1Sentido]/M[J2Sentido]
AlteraSentido:		PUSH	R1
			MOV	R1,VECTORUP;Se a particula se estiver a movimentar para cima
			CMP	M[SP+3],R0 ;Se a interrupcao 
			BR.Z	ViraCima
			Neg	R1
ViraCima:		CMP	M[SP+4],R1
			BR.NZ	Naoalterasent1
			MOV	R1,VECTORRIGHT
			MOV	M[SP+4],R1
			JMP	FimAlteraSentido
			
Naoalterasent1:		MOV	R1,VECTORRIGHT
			CMP	M[SP+3],R0
			BR.Z	Jump2
			Neg	R1
Jump2:			CMP	M[SP+4],R1
			BR.NZ	Naoalterasent2
			MOV	R1,VECTORDOWN
			MOV	M[SP+4],R1
			JMP	FimAlteraSentido
				
Naoalterasent2:		MOV	R1,VECTORDOWN
			CMP	M[SP+3],R0
			BR.Z	Jump3
			Neg	R1
Jump3:			CMP	M[SP+4],R1
			BR.NZ	Naoalterasent3
			MOV	R1,VECTORLEFT
			MOV	M[SP+4],R1
			JMP	FimAlteraSentido
				
Naoalterasent3:		MOV	R1,VECTORLEFT
			CMP	M[SP+3],R0
			BR.Z	Jump4
			Neg	R1
Jump4:			CMP	M[SP+4],R1
			BR.NZ	FimAlteraSentido
			MOV	R1,VECTORUP
			MOV	M[SP+4],R1
FimAlteraSentido:	POP	R1
			RETN	1
;Movimentos - Realiza o movimento das particulas na janela de texto assim como escreve o rasto.
;	      Preenche ainda a tabela de Rastos.
;Entradas: M[Posbon1];M[Posbon1Ant];M[Posbon2];M[Posbon2Ant]
	 ; M[ColisaoJ1];M[ColisaoJ2];M[SemRasto]
;Saidas: M[ColisaoJ1];M[ColisaoJ2]

Movimentos:		PUSH	R1
			PUSH	R2
			MOV	R1,M[Posbon1]
			MOV	M[Posbon1Ant],R1
			MOV	R1,M[J1Sentido]
			ADD	M[Posbon1],R1
			MOV	R1,M[Posbon1]
			PUSH	M[ColisaoJ1]
			CALL	Colisao
			POP	M[ColisaoJ1]
			CMP	M[ColisaoJ1],R0
			BR.NZ	NaoescreveP1
			MOV	R2,Particula1
			Call	EscString
			Call	EscTabela
			CMP	M[SemRasto],R0;Apenas nao escreve rasto na primeira e na ultima jogada
			BR.Z	NaoescreveP1
			MOV	R1,M[Posbon1Ant]
			MOV	R2,RastosP1
			Call	EscString
					
NaoescreveP1:		MOV	R1,M[Posbon2]
			MOV	M[Posbon2Ant],R1         
			MOV	R1,M[J2Sentido]
			ADD	M[Posbon2],R1
			MOV	R1,M[Posbon2]
			PUSH	M[ColisaoJ2]
			CALL	Colisao
			POP	M[ColisaoJ2]
			CMP	M[ColisaoJ2],R0
			BR.NZ	NaoescreveP2
			MOV	R2,Particula2
			Call	EscString
			CALL	EscTabela
			CMP	M[SemRasto],R0
			BR.Z	NaoescreveP2
			MOV	R1,M[Posbon2Ant]
			MOV	R2,RastosP2
			Call	EscString
						
NaoescreveP2:		INC	M[SemRasto]
			MOV	R1,M[Posbon1]
			MOV	R2,M[Posbon2]
			CMP	R1,R2
			BR.NZ	Sai
			MOV	R2,BatemdeCabeca
			Call	EscString
			MOV	R1,M[Posbon2Ant]
			MOV	R2,RastosP2
			Call 	EscString
Sai:			POP	R2
			POP	R1
			RET
;Colisao- Verifica se a particula colide, primeiro contra um rasto
;	  e se nao bater contra um rasto verifica contra a moldura 
;Entradas:M[ColisaoJ1];M[ColisaoJ2] (em posicoes de SP)
;Saidas:  M[ColisaoJ1];M[ColisaoJ2] (em posicoes de SP)   
Colisao:		PUSH	R1
			PUSH	R2
			PUSH	R3
			MOV	R3,Rastos
ContinuaVer1:		CMP	M[R3],R0
			BR.Z	FimVerificacaoRastos
			CMP	R1,M[R3]
			BR.NZ	NaoActFlagRas1
			INC	M[SP+5]
			BR	FimColisao
NaoActFlagRas1:		Inc	R3
			BR	ContinuaVer1
FimVerificacaoRastos:	CALL	ColisaoMoldura
FimColisao:		POP	R3
			POP	R2
			POP	R1
			RET
;ColisaoMoldura- Verifica se a particula colide contra a moldura
;Entradas: M[Posbon1];M[Posbon2] no registo R1
;Saidas:-
ColisaoMoldura:		PUSH	R1
			PUSH	R7				
			MOV	R7,R1	;R7 <-posicao
			AND	R7,00FFh
			CMP	R7,0010h
			BR.Z	ActFlagMol1
			CMP	R7,0041h
			BR.Z	ActFlagMol1
			MOV	R7,R1
			AND	R7,FF00h
			CMP	R7,0100h
			BR.Z	ActFlagMol1
			CMP	R7,1600h
			BR.Z	ActFlagMol1
			BR	Fimflags
ActFlagMol1:		INC	M[SP+8]
Fimflags:		POP	R7
			POP	R1
			RET

EscTabela:		PUSH	R1
			PUSH	R3
			MOV  	R3,Rastos
NaoVazio:		CMP	M[R3],R0
			BR.Z	Avanca
			INC	R3
			BR	NaoVazio
Avanca:			MOV	M[R3],R1 	
			POP	R3
			POP	R1
			RET
				

;Pausa- Verifica o interruptor definido para a pausa esta activo, esperando que fique desactivado
;	para continuar a rotina Ronda
;Entradas:M[INTERRUPTORES]
;Saidas:-
Pausa:			PUSH	R1
JogoPausado:		MOV	R1,M[INTERRUPTORES]
			AND	R1,0000000010000000b
			CMP	R1,R0
			BR.NZ	JogoPausado
			POP	R1
			RET
;ActualizaLeds- Actualiza os Leds apos cada nivel
;Entradas:M[VARIAVELLEDS];M[LEDS]
;Saidas:-
ActualizaLeds:		Push	R1							
			MOV	R1,M[VARIAVELLEDS]
			SHL	R1,4h
			ADD	R1,000Fh
			MOV	M[VARIAVELLEDS],R1
			MOV	M[LEDS],R1
			POP	R1
			RET
			
;Resultado: Realiza a Escrita de uma mensagem na janela de texto consoante o resultado.
;Actualiza tambem o LCD Liquido com o Tempo Max	e com o score de cada Jogador
;Entradas;M[Posbon1];M[Posbon2];M[CONTROLOLCD];M[ESCREVELCD];M[ScoreJ1Dig1]
	 ;M[ScoreJ1Dig2];M[ColisaoJ1];M[ColisaoJ2];M[ScoreJ2Dig1];M[ScoreJ2Dig2]
;Saidas:-
RESULTADO:		Push	R1
			Push	R2
			CALL	ActualizaTempoMax
			Mov	R1,M[Posbon1]		
			Mov	R2,M[Posbon2]		
			CMP	R1,R2			;verifica se colidiram um contra o outro frente a frente
			Br.NZ	NaoFaF
Empataram:		Mov	R1,M[Posbon1]		
			Mov	R2,M[Posbon2]		
			MOV	R1, 0A15h			
			Mov	R2,Empate			
			CALL	EscString			
			JMP	SaiResultado		
NaoFaF:			Mov	R1,M[ColisaoJ1]		
			Mov	R2,M[ColisaoJ2]		
			CMP	R1,R2			;verifica se colidiram os dois contra a parede ou rastos
			Br.Z	Empataram
			CMP	R1,R0			;Verifica se ganhou o jogador 1 ou o jogador 2
			Br.NZ	Ganha2			
			MOV	R2,GanhaJog1	;Se ganhou o 1 escreve a mensagem de 1
			MOV	R1,0A15h		;e actualiza o score do jogador 1
			CALL	EscString
			INC	M[ScoreJ1Dig2]
			Mov	R1,M[ScoreJ1Dig2]
			CMP	R1,003ah
			Br.nZ	Escreve
			Mov	R1,30h
			Mov	M[ScoreJ1Dig2],R1
			INC	M[ScoreJ1Dig1]
			Br	Escreve
Ganha2:			MOV	R2,GanhaJog2	;Se ganhou o 1 escreve a mensagem de 2
			MOV	R1,0A15h		;e actualiza o score do jogador 2
			CALL	EscString
			INC	M[ScoreJ2Dig2]
			MOV	R1,M[ScoreJ2Dig2]
			CMP	R1,003ah
			BR.NZ	Escreve
			MOV	R1,30h
			MOV	M[ScoreJ2Dig2],R1
			INC	M[ScoreJ2Dig1]
			BR	Escreve
Escreve:		MOV	R1,801Fh		;Actualiza o LCD Liquido com os scores de cada jogador
			MOV	M[CONTROLOLCD],R1
			MOV	R2,M[ScoreJ2Dig2]
			MOV	M[ESCREVELCD],R2
			DEC	R1
			MOV	M[CONTROLOLCD],R1
			MOV	R2,M[ScoreJ2Dig1]
			MOV	M[ESCREVELCD],R2
			MOV	R1,8015h
			MOV	M[CONTROLOLCD],R1
			MOV	R2,M[ScoreJ1Dig2]
			MOV	M[ESCREVELCD],R2
			DEC	R1
			MOV	M[CONTROLOLCD],R1
			MOV	R2,M[ScoreJ1Dig1]
			MOV	M[ESCREVELCD],R2
					
SaiResultado:		MOV	R2,LinhaRec		;Escreve o resto da janela de resulado
			MOV	R1,0B15h
			CALL	EscString
			MOV	R2,Frame
			MOV	R1,0915h
			CALL	EscString
			MOV	R1,0C15h
			CALL	EscString
			POP	R1
			POP	R2
			RET
;ActualizaTempoMax- Actualiza o tempo maximo apos cada Ronda
;Entradas:M[Contador];M[TempoMax];M[CONTROLOLCD];M[ESCREVELCD]
;Saidas:-

ActualizaTempoMax:	PUSH	R1
			PUSH	R2
			PUSH	R3
			PUSH	R4
			MOV	R1,M[Contador]
			CMP	R1,M[TempoMax]
			BR.NP	NaoeTempoMax
			MOV	R3,NUM_NIBBLES
			MOV	M[TempoMax],R1
			MOV	R2,800Eh
ContinuaAtualizar:	MOV	M[CONTROLOLCD],R2
			MOV	R4,R1
			AND	R4,NIBBLE_MASK
			ADD	R4,30h
			MOV	M[ESCREVELCD],R4
			ROR	R1,BITS_PER_NIBBLE
			DEC	R2
			DEC	R3
			BR.NZ	ContinuaAtualizar
NaoeTempoMax:		POP	R4
			POP	R3
			POP	R2
			POP	R1
			RET
						
;TEMPORIZADOR
;contar_decseg- Rotina que deixa passar 1 dec de segundo
;Entradas: M[CONTAGEM];M[CONTROLO]
;Saidas: - 
contar_decseg:          PUSH	R1
			PUSH	R2
			PUSH	R3
			MOV	R3,R0
			MOV	R1, 1
			MOV	R2, 1
			MOV	M[CONTAGEM], R1
			MOV 	M[CONTROLO], R2
MicroCiclo:		CMP	R3,R2
			BR.NZ	MicroCiclo
			POP	R3
			POP	R2
			POP	R1
			RET		
;ContDEC-Realiza a incrementacao do M[Contador] em decimal
;Entradas: M[Contador]
;Saidas: -					
ContDEC:		PUSH	R1
			PUSH	R2
			PUSH	R3
			PUSH	R4
			PUSH	R5
			PUSH	R6
			MOV	R6,4h
			MOV	R4,DEC_MASK
			MOV	R5,NIBBLE_DEL
			MOV     R2, NIBBLE_MASK
ContinuaContador:	MOV	R1, M[Contador]	; R1 = numero no display
			MOV	R3, R2			; Guarda-se R3 para depois inc contador no local correcto
			AND	R1, R2			; guarda o nibble da casa que esta a actualizar
			CMP	R1, R4			; verifica se o numero dessa casa e 9 para somar 1 ao nibble seguinte
			BR.NZ	Incrementa		; se nao, inc essa casa
			AND	M[Contador], R5	; se for 9 coloca essa casa a 0
			ROL	R2,4h
			ROL	R4,4h
			ROL	R5,4h
			DEC	R6				;Ate actualizar todas as casas repete o ciclo
			BR.NZ	ContinuaContador
FIM7:			POP	R6
			POP	R5
			POP	R4
			POP	R3
			POP 	R2
			POP	R1 
			RET			
Incrementa:		MOV     R1, 1111h
			AND 	R1, R3
			ADD	M[Contador], R1
			BR	FIM7
;-----------------------------------------------------------------
;ESCRITORES
;EscString - Escreve na Janela de Texto a string
;Entradas:posicao de escrita em R1 e a posicao de inicio da string em R2
;Saidas:-
EscString:		PUSH	R1
			PUSH    R2
			PUSH	R3
Ciclo:         		MOV     R3, M[R2]
			CMP     R3, FIM
			BR.Z    FimEsc
			CALL    EscCar
			INC	R1
			INC     R2
			BR     Ciclo
		
FimEsc:        		POP	R3
			POP     R2
			POP     R1
			RET
			
	;EscCar - Escreve o caracter na posicao da Janela de Texto
	;Entradas: M[CURSOR]; M[IO_WRITE]
	;posicao de Escrita em R1; codigo ascii do caracter a escrever em R3
	;Saidas: -
EscCar:      		PUSH    R1
			PUSH	R3
			MOV     M[CURSOR], R1
			MOV     M[IO_WRITE], R3
			POP	R3
			POP     R1
			RET
	
	;EscCont- Escritor no Contador de 7 Segmentos
	;Entradas: M[Contador]
	;Saidas:-
EscCont:        	PUSH    R1
			PUSH    R2
			PUSH    R3
			DSI
			MOV     R2, NUM_NIBBLES
			MOV     R3, IO_DISPLAY
Ciclo_cont:     	MOV     R1, M[Contador]
			AND     R1, NIBBLE_MASK
			MOV     M[R3], R1
			ROR     M[Contador], BITS_PER_NIBBLE
			INC     R3
			DEC     R2
			BR.NZ   Ciclo_cont
			ENI
			POP     R3
			POP     R2
			POP     R1
			RET
;RESET-Reinicia todas as variveis necessarias para o inicio da proxima ronda
;assim como o coloca o display de 7 segmentos com 0000
;Entradas:M[Posbon1];M[Posbon2];M[SemRasto];M[J1Sentido];M[J2Sentido];M[ColisaoJ1]
;M[ColisaoJ2];M[VARIAVELLEDS];M[LEDS];M[Contador];M[Posbon1Ant];M[Posbon2Ant]
;Saidas:-
Reset:			PUSH	R1
			PUSH	R3
			MOV	R1,0B18h
			MOV	M[Posbon1],R1
			MOV	R1,0B38h
			MOV	M[Posbon2],R1
			MOV	M[SemRasto],R0
			MOV	R1,VECTORRIGHT
			MOV	M[J1Sentido],R1
			MOV	R1,VECTORLEFT
			MOV	M[J2Sentido],R1
			MOV	M[ColisaoJ1],R0
			MOV	M[ColisaoJ2],R0
			MOV	M[VARIAVELLEDS],R0
			MOV	M[LEDS],R0
			MOV	M[Contador],R0
			CALL	EscCont
			MOV	M[Posbon1Ant],R0
			MOV	M[Posbon2Ant],R0
			MOV	R1,Rastos
LimpaRastos:		MOV	R3,M[R1]
			CMP	R3,R0
			BR.Z	TaLimpo
			MOV	M[R1],R0
			INC	R1
			BR	LimpaRastos
TaLimpo:		POP	R3
			POP	R1
			RET
