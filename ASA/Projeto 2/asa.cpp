#include <utility>
#include <iostream>
#include <fstream>
#include <vector>
#include <climits>
#include <list>
#include <set>
#include <vector>

#define INF INT_MAX

using namespace std;

class Edge {
private:
	int _destino;
	int _peso;
public:
	Edge(int destino, int peso){
		_destino = destino;
		_peso = peso;
	}
	int getPeso(){ return _peso; }
	int getDestino(){ return _destino; }
	void setPeso(int p) { _peso = p; }
};

class Graph{
private:
	vector<vector<Edge> > _grafo;
	int _num_vertices;
	int _num_filiais;
	int *_filiais;					//array para todas as localidades, true-filial, false-nao filial
	vector<int> _filiaisOrdem;			//guarda filiais por ordem de entrada no input
	int _index_filial;
	vector<int> _altura;		                //array com altura de todos os vertices - BELLMAN FORD
	vector<int> _distanciaLocalidades;	 	//soma da distancia das filiais para cada uma das localidades
	int _ponto_encontro;
	int _fim;					//flag - ultimo chamamento da funcao dijkstra
	vector<int> _perdas_minimas;
public:
	Graph(int num_vertices, int num_filiais, int num_ligacoes){
		_grafo.resize(num_vertices);
		_num_vertices = num_vertices;
		_num_filiais = num_filiais;
		_filiais = new int[num_vertices];
		_filiaisOrdem.resize(num_filiais);
		_index_filial=0;
		_altura.resize(num_vertices,0);
		_distanciaLocalidades.resize(num_vertices,0);
		_perdas_minimas.resize(num_vertices,0);
		_ponto_encontro=-1;
		_fim=false;
	}	
	void criarLigacao(int v1, int v2, int perda){
		Edge v(v2,perda);
		_grafo[v1].push_back(v);
	}
	void criarFilial(int filial){
		_filiais[filial]=true;
		_filiaisOrdem[_index_filial]=filial;
		_index_filial++;
	}
	void bellmanFord(){
		for (int v=0; v<_num_vertices; v++){
			if(_grafo[v].size()!=0 )
				for (std::vector<Edge>::iterator w = _grafo[v].begin(); w != _grafo[v].end(); ++w)
					if(w->getPeso()+_altura[v] < _altura[w->getDestino()])
						  _altura[w->getDestino()] = w->getPeso()+_altura[v];  
		}
	}
	void repesagem(){
		for(int i=0; i<_num_vertices; i++){
			if(_grafo[i].size()!=0){
				for (std::vector<Edge>::iterator w = _grafo[i].begin(); w != _grafo[i].end(); ++w){
					w->setPeso(w->getPeso()+_altura[i]-_altura[w->getDestino()]);
				}
			}
		}
	}
	int dijkstra(int s) {
		int perda=0, peso_deste, destino_deste;
		vector<int> caminho_minimo(_num_vertices,INF);
		set<pair<int,int> > listaQ;
		listaQ.insert(std::make_pair(0,s));
		while (!listaQ.empty()) {
			pair<int,int> p = *listaQ.begin();
			listaQ.erase(listaQ.begin());
			int d = p.first;
			int n = p.second;
			caminho_minimo[n] = d;
			if (_grafo[n].size()!=0)
				for(std::vector<Edge>::iterator e = _grafo[n].begin(); e != _grafo[n].end(); ++e){
					peso_deste = e->getPeso();
					destino_deste = e->getDestino();
					std::pair<int,int> to_erase = *listaQ.find(std::make_pair(caminho_minimo[destino_deste], destino_deste));
					if ( (caminho_minimo[n]!=INF && (caminho_minimo[n]+(peso_deste)) < caminho_minimo[destino_deste] )) {
						if (caminho_minimo[destino_deste] != INF)
							listaQ.erase(to_erase);
						listaQ.insert(std::make_pair(caminho_minimo[n] + peso_deste, destino_deste));
						caminho_minimo[destino_deste] = caminho_minimo[n] + peso_deste;
					}
				}
		}
		for(int j=0; j<_num_vertices;j++){
			if(_distanciaLocalidades[j]==INF || caminho_minimo[j]==INF)
				_distanciaLocalidades[j] = INF;
			else
				_distanciaLocalidades[j] += caminho_minimo[j] - _altura[s] + _altura[j];
		}
		if (_fim==true){
			perda += caminho_minimo[_ponto_encontro] - _altura[s] + _altura[_ponto_encontro];
			_perdas_minimas[s] += caminho_minimo[_ponto_encontro] - _altura[s] + _altura[_ponto_encontro];
		}
		return perda;
	}
	void getPontoEncontro(){
		int soma = INF;
		for(int i=0; i<_num_vertices ; i++){
			if( (_distanciaLocalidades[i] < soma) && _distanciaLocalidades[i]!=INF){
				soma = _distanciaLocalidades[i];
				_ponto_encontro = i;
			}
		}
	}
	void gerarOutput(){
		int perda_total=0;
		bellmanFord();
		repesagem();
 		for(int fil=0; fil<_num_vertices; fil++){   
			if(_filiais[fil]==true)
				dijkstra(fil);
		}
		getPontoEncontro();
		_fim=true;
		if(_ponto_encontro==-1) { 
			std::cout << "N" << std::endl;
		}
		else{
			for(int i=0; i<_num_vertices; i++){
				if (_filiais[i]==true){
					perda_total += dijkstra(i);
				}
			}
			std::cout << _ponto_encontro+1 << " " << perda_total << std::endl;
			for (int i=0; i<_num_filiais;i++)
				std::cout << _perdas_minimas[_filiaisOrdem[i]] << " ";
			std::cout<< std::endl;
		}
	}
};

int main(){
	int num_localidades, num_filiais, filial, num_ligacoes, node1, node2, perda;
	std::cin >> num_localidades >> num_filiais >> num_ligacoes;
	Graph grafo(num_localidades, num_filiais, num_ligacoes);
	for (int i=0; i<num_filiais; i++){
		std::cin >> filial;
		grafo.criarFilial(filial-1);
	}
	for (int i=0; i<num_ligacoes; i++){
		std::cin >> node1 >> node2 >> perda;
		grafo.criarLigacao(node1-1, node2-1, perda);
	}
	grafo.gerarOutput();
	return 0;
} 