#81172 Carolina Ines Maltez Xavier

AE=('34','37') #American Express
DCI=('309','36','38','39') #Diners Club International
DC=('65',) #Discover Card
M=('5018','5020','5038') #Maestro
MC=('50','51','52', '53','54','19') #Master Card
VE=('4026','426','4405','4508') #Visa Electron
V=('4024','4532','4556') #Visa

#algoritmo de Luhn
def calc_soma (n_sem_dig):
    '''esta funcao recebe uma cadeia de caracteres do numero de cartao de credito sem o ultimo digito - n_sem_dig - e calcula a soma de todos os digitos desse numero'''
    soma = 0 
    i = len(n_sem_dig)-1 #selacao dos algarismos de n_sem_dig individualmente, comecando pelo ultimo
    cont=1 #contagem da posicao onde o algarismo i ficaria depois do numero ser invertido, comecando pela primeira posicao
    while i>=0:
        if cont%2!=0: #se a posicao onde o algarismo se encontra depois do numero ser invertido for impar
            digito =eval(n_sem_dig[i])*2
            if digito > 9: #se o dobro do algarismo for superior a 9
                digito = digito - 9
        else:
            digito=eval(n_sem_dig[i])
        cont=cont+1 #posicao seguinte
        i=i-1 #algarismo anterior
        soma = soma + digito
    return soma
        
def luhn_verifica (n_string):
    '''esta funcao recebe uma cadeia de caracteres com o numero de cartao de credito - n_string - e verifica se este e valido segundo o algoritmo de Luhn'''
    n_sem_dig=str(eval(n_string)//10) #numero de cc sem o ultimo algarismo
    ult_dig = eval(n_string) % 10 #digito de verificacao do numero de cc
    soma_final = calc_soma (n_sem_dig) + ult_dig
    if soma_final % 10 == 0:
        return True
    else:
        return False
    
#Prefixo do numero - IIN
def comeca_por (cad1, cad2):
    '''esta funcao verifica se a cadeia de caracteres cad2 e igual aos digitos iniciais da cadeia de caracteres cad1'''
    l1 = len(cad1) #numero de caracteres da primeira cadeia
    l2 = len(cad2) #numero de caracteres da segunda cadeia
    cad3 = str (eval(cad1)//10**(l1-l2)) #cad3 corresponde aos digitos inicias de cad1 - numero de digitos inicias de cad1 igual ao numero de digitos de cad2
    if cad3 == cad2:
        return True
    else:
        return False


def comeca_por_um (cad, t_cads):
    '''esta funcao verifica se o o tuplo de cadeias de caracteres - t_cads - contem uma cadeia igual aos algarismos iniciais da cadeia de caracteres cad'''
    for i in t_cads: #para qualquer elemento do tuplo t_cads - i
        if comeca_por (cad, i): #se i for igual aos digitos iniciais de cad
            return True
    return False

#Rede emissora
def valida_iin(n_string):
    '''esta funcao recebe uma cadeia de caracteres com o numero de carctao de credito - n_string- e devolve a rede emissora correspondente a esse numero, ou nada, se esta nao existir'''
    if comeca_por_um (n_string, AE) and len(n_string) == 15:
        return 'American Express'
    elif comeca_por_um (n_string, DCI) and len(n_string) == 14:
        return 'Diners Club International'
    elif comeca_por_um (n_string, DC) and len(n_string) == 16:
        return 'Discover Card'
    elif comeca_por_um (n_string, M) and len(n_string) == 13:
        return 'Maestro'
    elif comeca_por_um (n_string, M) and len(n_string) == 19:
        return 'Maestro'    
    elif comeca_por_um (n_string, MC) and len(n_string) == 16:
        return 'Master Card'
    elif comeca_por_um (n_string, VE) and len(n_string) == 16:
        return 'Visa Electron'
    elif comeca_por_um (n_string, V) and len(n_string) == 13:
        return 'Visa'
    elif comeca_por_um (n_string, V) and len(n_string) == 16:
        return 'Visa'    
    else:
        return ''

#MII 
def categoria(n_string):
    '''esta funcao devolve a categoria da entidade a partir do primeiro digito da cadeia de caracteres correspondente ao numero de cartao de credito - n_string'''
    if eval(n_string[0])==1:
        return 'Companhias aereas'
    elif eval(n_string[0])==2:
        return 'Companhias aereas e outras tarefas futuras da industria'
    elif eval(n_string[0])==3:
        return 'Viagens e entretenimento e bancario / financeiro'
    elif eval(n_string[0])==4 or eval(n_string[0])==5:
        return 'Servicos bancarios e financeiros'
    elif eval(n_string[0])==6:
        return 'Merchandising e bancario / financeiro'
    elif eval(n_string[0])==7:
        return 'Petroleo e outras atribuicoes futuras da industria'
    elif eval(n_string[0])==8:
        return 'Saude, telecomunicacoes e outras atribuicoes futuras da industria'
    elif eval(n_string[0])==9:
        return 'Atribuicao Nacional'  
    else: #se o primerio digito do numero for 0
        raise ValueError ('o primeiro digito deve ser entre 1 e 9')
        

#Verificar o numero de cartao de credito
def verifica_cc (n_cc):
    '''esta funcao verifica a validade do numero de cartao de credito e, se for valido, devolve a categoria e a rede do cartao, caso contrario informa que o cartao e invalido'''
    n_sem_dig = str(int(n_cc) // 10) #cadeia de caracteres representando o numero do cartao sem o ultimo digito
    n_string = str(n_cc) #cadeia de caracteres representando o numero do cartao
    if luhn_verifica(n_string) and valida_iin(n_string) != '': #se o numero de cartao for valido e se corresponder a uma das redes emissoras
        return (categoria(n_string), valida_iin(n_string))
    else:
        return 'cartao invalido'     

#gerar numero de cartao de credito
from random import random

def elemento_aleatorio(tuplo):
    '''esta funcao recebe um tuplo e devolve um dos seus elementos aleatoriamente'''
    elemento = round(random()*(len(tuplo)-1)) #variavel que representa o elemento do tuplo
    return tuplo[elemento]

def numero_aleatorio(rede_emissora):
    '''esta funcao devolve um numero de cartao de credito aleatorio, sem o ultimo digito'''
    y = round(random()) #variavel aleatoria que tanto pode ter o valor 0 como 1
    if rede_emissora == 'AE':
        return int(elemento_aleatorio(AE))*10**12 + int(random()*10**12)
    elif rede_emissora == 'DCI':
        ele_aleat = elemento_aleatorio(DCI)
        if len(ele_aleat) == 3:
            return int(ele_aleat)*10**10 + int(random()*10**10)
        else:
            return int(ele_aleat)*10**11 + int(random()*10**11)
    elif rede_emissora == 'DC':
        return 65*10**13 + int(random()*10**13)
    elif rede_emissora == 'M':
        if y == 0:
            return int(elemento_aleatorio(M))*10**8 + int(random()*10**8)
        else:
            return int(elemento_aleatorio(M))*10**14 + int(random()*10**14)
    elif rede_emissora == 'MC':
        return int(elemento_aleatorio(MC))*10**13 + int(random()*10**13)
    elif rede_emissora == 'VE':
        ele_aleat = elemento_aleatorio(VE)
        if len(ele_aleat) == 3:
            return int(ele_aleat)*10**12 + int(random()*10**12)
        else:
            return int(ele_aleat)*10**11 + int(random()*10**11)
    else:
        if y == 0:
            return int(elemento_aleatorio(V))*10**8 + int(random()*10**8)
        else:
            return int(elemento_aleatorio(V))*10**11 + int(random()*10**11)


def digito_verificacao(num_random_string):
    '''esta funcao devolve o numero de verificacao para o numero aleatorio gerado - num_random_string'''
    resto = calc_soma(num_random_string) % 10
    if resto == 0: #se o valor obtido na funcao calc_soma for multiplo de 10
        dig_verificacao = '0'
    else: #se o valor obtido na funcao calc_soma nao for multiplo de 10
        dig_verificacao = str ( 10 - resto ) #determinar o digito de verificacao, de maneira a que ao ser somado com o valor obtido no calc_soma este seja multiplo de 10
    return dig_verificacao

def gera_num_cc(rede_emissora):
    '''esta funcao gera um numero de cartao de credito aleatorio para uma determinada rede emissora'''
    num_aleatorio = numero_aleatorio(rede_emissora) #numero aleatorio sem o digito de verificacao
    digito_de_verificacao = digito_verificacao(str(num_aleatorio))
    num_gerado = str(int(num_aleatorio)*10 + int(digito_de_verificacao))
    return num_gerado