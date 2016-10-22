#include <iostream>
#include <list>
#include <vector>

using namespace std;

class Graph{
private:
	int _numPeople;
	std::list<int> *_nodes;
	bool *_fundamentals;
	int _time;
	int *_discovered;
	int *_parent;
	int *_low;
	bool *_visited;
	
public:
	Graph(int num_vertices){
		_time=0;
		_numPeople = num_vertices;
		_fundamentals = new bool[num_vertices];
		_nodes = new std::list<int>[num_vertices];
		_visited = new bool[num_vertices];
		_parent = new int[num_vertices];
		_discovered = new int[num_vertices];
		_low = new int[num_vertices];
		for (int i = 0; i < num_vertices; i++){ 
			_parent[i] = -1;
		}
	}
	
	void criarLigacao(int v1, int v2){
		_nodes[v1].push_back(v2);
		_nodes[v2].push_back(v1);
	}
	
	void gerarOutput(){
		int num_fundamentals=0;
		algorithm(0); 
		for(int i=0; i<_numPeople; i++){
			if(_fundamentals[i]){ num_fundamentals++; }
		}
		std::cout << num_fundamentals << std::endl;
		std::cout << fundamentalsMin() << " " << fundamentalsMax() << std::endl;
		
	}	
	
	void algorithm(int v){
		int numChildren = 0;
		_visited[v] = true;
		_discovered[v] = _low[v] = ++_time;
		std::list<int>::const_iterator j, end;
		for (j = _nodes[v].begin(), end = _nodes[v].end(); j != end; ++j) {
			if (!_visited[*j]){
				numChildren++;
				_parent[*j] = v;
				algorithm(*j);
				_low[v] = min(_low[v], _low[*j]);    
				if (_parent[v] == -1 && numChildren > 1)
					_fundamentals[v]=true;
				if (_parent[v] != -1 && _low[*j] >= _discovered[v])
					_fundamentals[v]=true;
			}
			else if (*j != _parent[v])
				_low[v] = min(_low[v], _discovered[*j]);
		}
	}

	int fundamentalsMin(){
		for(int i=0; i<_numPeople; i++){
		  if(_fundamentals[i]){return i+1;}
		}  
		return -1;
 		
	}
	int fundamentalsMax(){
		int max=-1;
		for(int i=0; i<_numPeople; i++){
		  if(_fundamentals[i]){
		    max=i+1;
		  }
		}
		return max;	 
	}
};

 int main(){
	int num_pessoas, num_conexoes, node1, node2;
	std::cin >> num_pessoas >> num_conexoes;
	Graph grafo(num_pessoas);
	for (int i=0; i<num_conexoes; i++){
		std::cin >> node1 >> node2;
		grafo.criarLigacao(node1-1, node2-1);
	}
	grafo.gerarOutput();
	return 0;
}