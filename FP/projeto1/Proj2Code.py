#Grupo 075
#81137 Daniel Goncalves Fernandes
#81172 Carolina Ines Maltez Xavier

direcoes={'N':[range(1,4,1),1],'S':[range(4,1,-1),-1],'W':[range(1,4,1),1],'E':[range(4,1,-1),-1]} 
# o primeiro elemeto de cada lista corresponde ao range utilizado no tabuleiro_reduz
# o segundo elemento de cada lista sera utilizado para achar a linha ou coluna anterior na altura da reducao

#TAD coordenada

def cria_coordenada(l,c):
    '''cria_coordenada: int,int->tuple
    esta funcao recebe dois argumentos do tipo inteiro e devolve uma coordenada,
    cuja linha corresponde ao primeiro argumento e a coluna ao segundo'''
    if l in (1,2,3,4) and c in (1,2,3,4):
        return (l,c)
    else:
        raise ValueError ('cria_coordenada: argumentos invalidos')
    
def coordenada_linha(coordenada):
    '''coordenada_linha: tuple->int
    esta funcao recebe como argumento uma coordenada e devolve a linha respetiva'''
    return coordenada[0] 
   
def coordenada_coluna(coordenada):
    '''coordenada_coluna: tuple->int
    esta funcao recebe como argumento uma coordenada e devolve a coluna respetiva'''    
    return coordenada[1]

def e_coordenada (argumento):
    '''e_coordenada: universal->logico
    esta funcao recebe um argumento e verifica se ele e uma coordenada'''
    if isinstance (argumento,tuple) and len(argumento)==2:
        if isinstance(coordenada_linha(argumento),int) \
           and isinstance (coordenada_coluna(argumento),int) \
           and 1<=coordenada_linha(argumento)<=4 \
           and 1<=coordenada_coluna(argumento)<=4:
            return True
    return False

def coordenadas_iguais(coordenada1,coordenada2):
    '''coordenada_iguais: tuple,tuple->logico
    esta funcao recebe como argumentos duas coordenadas e verifica se sao iguais'''
    return coordenada1==coordenada2


#TAD tabuleiro

def cria_tabuleiro():
    '''cria_tabuleiro: {}->list
    esta funcao nao recebe qualquer argumento e devolve uma lista correspondente
    a um tabuleiro vazio'''
    return [[0,0,0,0], [0,0,0,0], [0,0,0,0], [0,0,0,0], 0]
    
def tabuleiro_posicao(t,c):
    '''tabuleiro_posicao: ist,tuple->int
    esta funcao recebe como argumentos uma lista correspondente a um tabuleiro 
    e um tuplo correspondente a uma coordenada e devolve o numero que se encontra
    no tabuleiro na respetiva coordenada'''
    if e_coordenada(c):
        return t[coordenada_linha(c)-1][coordenada_coluna(c)-1]
    else:
        raise ValueError('tabuleiro_posicao: argumentos invalidos')

def tabuleiro_pontuacao(t):
    '''tabuleiro_pontuacao: list->int
    esta funcao recebe como argumento uma lista correspondente a um tabuleiro e
    devolve a pontuacao atual do respetivo tabuleiro'''
    return t[4]          #quarta posicao da lista tabuleiro corresponde a pontuacao         

def tabuleiro_posicoes_vazias(t):
    '''tabuleiro_posicoes_vaias: list->list
    esta funcao recebe como argumento uma lista correspondente a um tabuleiro e 
    devolve uma lista que contem as coordenadas de todas as posicoes vazias do 
    respetivo tabuleiro'''
    lista_pos_vazias=[ ]
    for l in range(1,5):
        for c in range(1,5):
            if e_coordenada(cria_coordenada(l,c)) and tabuleiro_posicao(t,cria_coordenada(l,c))==0:
                lista_pos_vazias = lista_pos_vazias + [cria_coordenada(l,c)]
    return lista_pos_vazias


def tabuleiro_preenche_posicao(t,c,v):
    '''tabuleiro_preenche_posicao: list,tuple,int->list
    esta funcao recebe como argumentos uma lista correspondente a um tabuleiro, 
    um tuplo correspondente a uma coordenada e um inteiro, e devolve o tabuleiro 
    modificado, com o inteiro na posicao correspondente a coordenada recebida'''
    if not e_tabuleiro(t) or not e_coordenada(c) or not isinstance (v,int):
        raise ValueError ('tabuleiro_preenche_posicao: argumentos invalidos')
    else:
        t[coordenada_linha(c) - 1][coordenada_coluna(c) - 1]=v
    return t

def tabuleiro_actualiza_pontuacao(t,v):
    '''tabuleiro_actualiza_pontuacao: list,int->list
    esta funcao recebe como argumentos uma lista correspondente a um tabuleiro e
    um inteiro, e devolve o tabuleiro modificado, acrescentando o inteiro ao 
    valor da respectiva pontuacao'''
    if v%4!=0 or v<0 or not e_tabuleiro(t):
        raise ValueError ('tabuleiro_actualiza_pontuacao: argumentos invalidos')
    else:
        t[4]= tabuleiro_pontuacao(t) + v
    return t

def tabuleiro_reduz(t,d):
    '''tabuleiro_reduz: list,str->list
    esta funcao recebe como argumentos uma lista correspondente a um tabuleiro e 
    uma cadeia de caracteres correspondente a uma direcao de movimento,
    e devolde o tabuleiro modificado, reduzindo-o na respetiva direcao de acordo 
    com as regras do jogo 2048'''
    def aux_move_vertical(t,d): #responsavel pelo movimento do tabuleiro na vertical
        for c in range (1,5):
            for l in direcoes[d][0]: 
                if tabuleiro_posicao(t,cria_coordenada(l,c))==0:
                    tabuleiro_preenche_posicao(t,cria_coordenada(l,c),tabuleiro_posicao(t,cria_coordenada(l+direcoes[d][1],c)))
                    tabuleiro_preenche_posicao(t,cria_coordenada(l+direcoes[d][1],c),0)
        return t
    def aux_move_horizontal(t,d): #responsavel pelo movimento do tabuleiro na horizontal
        for l in range (1,5):
            for c in direcoes[d][0]:
                if tabuleiro_posicao(t,cria_coordenada(l,c))==0:
                    tabuleiro_preenche_posicao(t,cria_coordenada(l,c),tabuleiro_posicao(t,cria_coordenada(l,c+direcoes[d][1])))
                    tabuleiro_preenche_posicao(t,cria_coordenada(l,c+direcoes[d][1]),0)     
        return t
    def aux_soma_vertical(t,d): #responsavel pelas somas do tabuleiro na vertical
        for c in range (1,5):
            for l in direcoes[d][0]:            
                if tabuleiro_posicao(t,cria_coordenada(l,c))==tabuleiro_posicao(t,cria_coordenada(l+direcoes[d][1],c)):
                    tabuleiro_preenche_posicao(t,cria_coordenada(l,c),tabuleiro_posicao(t,cria_coordenada(l,c))*2)
                    tabuleiro_preenche_posicao(t,cria_coordenada(l+direcoes[d][1],c),0)
                    tabuleiro_actualiza_pontuacao(t,tabuleiro_posicao(t,cria_coordenada(l,c)))
        return t
    def aux_soma_horizontal(t,d): #responsavel pelas somas do tabuleiro na horizontal
        for l in range (1,5):
            for c in direcoes[d][0]:            
                if tabuleiro_posicao(t,cria_coordenada(l,c))==tabuleiro_posicao(t,cria_coordenada(l,c+direcoes[d][1])):
                    tabuleiro_preenche_posicao(t,cria_coordenada(l,c),tabuleiro_posicao(t,cria_coordenada(l,c))*2)
                    tabuleiro_preenche_posicao(t,cria_coordenada(l,c+direcoes[d][1]),0)
                    tabuleiro_actualiza_pontuacao(t,tabuleiro_posicao(t,cria_coordenada(l,c))) 
        return t

    if d in direcoes and e_tabuleiro(t):
        if d=='N' or d=='S':
            return aux_move_vertical(aux_soma_vertical(aux_move_vertical(aux_move_vertical(t,d),d),d),d)
        elif d=='W' or d=='E':
            return aux_move_horizontal(aux_soma_horizontal(aux_move_horizontal(aux_move_horizontal(t,d),d),d),d)
    else:
        raise ValueError ('tabuleiro_reduz: argumentos invalidos')    
                          
def e_tabuleiro(argumento):
    '''e_tabuleiro: universal->logico
    esta funcao recebe um argumento e verifica se este e, ou nao, um tabuleiro'''   
    if not isinstance(argumento,list) and len(argumento)!=5 or not isinstance(argumento[-1], int) \
       or argumento[-1] < 0 or argumento[-1]%4!=0:
        return False
    for i in range(0,len(cria_tabuleiro())-1):
        if not isinstance(argumento[i],list):
            return False
    return True

def tabuleiro_terminado(t):
    '''tabuleiro_terminado: list->logico
    esta funcao recebe como argumento uma lista correspondente a um tabuleiro e 
    verifica se este esta, ou nao, terminado, ou seja se esta cheio e nao existem
    movimentos possiveis'''
    copia1 = copia_tabuleiro(t) 
    copia2 = copia_tabuleiro(copia1)
    for d in direcoes:
        if not tabuleiros_iguais(tabuleiro_reduz(copia1,d),copia2):
            return False
    return True

def tabuleiros_iguais(t1,t2):
    '''tabuleiro_iguais: dict,dict->logico
    esta funcao recebe como argumentos dois dicionarios correspondentes a 
    tabuleiros e verifica se estes sao, ou nao, iguais'''
    return t1==t2       

def escreve_tabuleiro(t):
    '''escreve_tabuleiro: list->{}
    esta funcao recebe como argumento uma lista correspondente a um tabuleiro e
    devolve a representacao externa desse tabuleiro de acordo com o 2048'''    
    if not e_tabuleiro(t):
        raise ValueError("escreve_tabuleiro: argumentos invalidos")
    else:
        linha = ''
        for l in range(1,5):
            for c in range(1,5):
                linha = linha + '[ ' + str(tabuleiro_posicao(t, cria_coordenada(l,c))) + ' ] '
            print(linha)
            linha = ''
        print('Pontuacao:', tabuleiro_pontuacao(t))


#Funcoes Adicionais

def pede_jogada():
    '''pede_jogada: {}->str
    esta funcao nao recebe nenhum  argumento, e pede uma direcao ao utilizador,
    devolvendo essa mesma direcao'''    
    j = input('Introduza uma jogada (N, S, E, W): ')  #j=direcao introduzida pelo jogador
    if j in direcoes:
        return j
    else:
        print('Jogada invalida.')
        return pede_jogada()
      
def copia_tabuleiro(t):
    '''copia_tabuleiro: list->list
    esta funcao recebe como argumento uma lista correspondente a um tabuleiro e 
    devolve uma copia desse tabuleiro'''     
    copia = cria_tabuleiro()
    tabuleiro_actualiza_pontuacao(copia, tabuleiro_pontuacao(t))
    for coordenada in tabuleiro_posicoes_vazias(cria_tabuleiro()):
        tabuleiro_preenche_posicao(copia, coordenada, tabuleiro_posicao(t, coordenada))
    return copia

from random import random 

def preenche_posicao_aleatoria(t):
    '''escreve_tabuleiro: list->list
    esta funcao recebe como argumento uma lista correspondente a um tabuleiro
    e devolve uma lista correspondente a esse tabuleiro com uma das suas posicoes
    vazias preenchida aleatriamente com um 2 ou um 4'''    
    x = int(random()*100)                                       #variavel aleatoria entre o 0 e o 100, usada para derminar se a posicao sera preenchida com um 2 ou um 4
    y = round(random()*len(tabuleiro_posicoes_vazias(t))-1)     #variavel que ira ser utilizada para selecionar uma das posicoes vazias aleatoriamente
    if x<80:
        t = tabuleiro_preenche_posicao(t,tabuleiro_posicoes_vazias(t)[y],2)
    else:
        t = tabuleiro_preenche_posicao(t,tabuleiro_posicoes_vazias(t)[y],4)
    return t

def jogo_2048():
    '''jogo_2048: {}->{}
    esta funcao nao recebe nenhum argumento e permite realizar um jogo completo
    de 2048, escrevendo o tabuleiro no ecra e um pedido de uma nova jogada ate ao
    jogo ter terminado'''      
    t = preenche_posicao_aleatoria(preenche_posicao_aleatoria(cria_tabuleiro()))
    escreve_tabuleiro(t)
    def aux(t):
        copia = copia_tabuleiro(t)
        t_reduzido = tabuleiro_reduz(t,pede_jogada())
        if copia != t_reduzido:        
            t = preenche_posicao_aleatoria(t_reduzido)
            copia = copia_tabuleiro(t)
        escreve_tabuleiro(t)
        if not tabuleiro_terminado(copia):
            return aux(t)
    return aux(t)