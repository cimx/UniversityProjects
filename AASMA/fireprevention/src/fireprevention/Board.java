package fireprevention;

import java.awt.Point;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import java.util.*;

/**
 * Environment
 * @author Rui Henriques
 */
public class Board {

	/** A: Environment */

	public int nX, nY, nUAVs;
	public List<Agent> UAVs;
	public GraphicalInterface GUI;
	public double[][] board;
	public double[][] risk_limit;
	public double[][] risk_sum;
	public double[][] last_visited;	
	public boolean[][] fire;
	double warning_time = 10000;
	ArrayList<Point> destination_points;
	ArrayList<Point> newFires;

	int iterations = 0;
	int tests = 0;
	
	public Board(int nX, int nY, int nUAVs) {
		this.nX = nX;
		this.nY = nY;
		this.nUAVs = nUAVs;
		initialize();
	}

	private void initialize() {
		iterations = 0;
		UAVs = new ArrayList<Agent>();
		destination_points = new ArrayList<Point>(); 
		newFires = new ArrayList<Point>();
		for(int i=0; i<nUAVs && i<nY; i++) UAVs.add(new Agent(new Point(0,0),i));
		
		Random r = new Random();
		board = new double[nX][nY];
		risk_limit = new double[nX][nY];
		risk_sum = new double[nX][nY];
		fire = new boolean[nX][nY];
		last_visited = new double[nX][nY];
		
		for(int i=0; i<nX; i++)
			for(int j=0; j<nY; j++) {
				//board[i][j] = Math.abs(r.nextGaussian());
				risk_limit[i][j] = Math.abs(r.nextGaussian());
				board[i][j] = risk_limit[i][j];
				last_visited[i][j] = risk_limit[i][j];
				this.fire[i][j] = false;
			}
		board[0][0] = 0;
		last_visited[0][0] = 0;
		risk_limit[0][0] = 0;

	}

	
	/** B: Elicit agent actions */
	
	RunThread runThread;

	public class RunThread extends Thread {
		
		int time;

		
		public RunThread(int time){
			this.time = time;
		}
		
	    public void run() {

			String file = new File("").getAbsolutePath()+"/ola.txt";
			//System.out.println(file);
			BufferedWriter bw;
			
	    	/*if (tests == 100){
	    		for(Agent a : UAVs)
	    			a.closeFile();
	    	}*/


	    	while(tests < 50){
	    		try {
					bw = new BufferedWriter(new FileWriter(new File(file), true));
		
			    	if(iterations == 500){
			    		System.out.println(tests);
			    		tests ++;
			    		for (Agent a: UAVs) {
							try {
								//a.write();
								bw.write(String.valueOf(a.cleared_risk));
								//System.out.println(a.cleared_risk);
								bw.write(",");
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
			    		}
			    		initialize();
			    	}
			    	bw.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}	
	    		iterations ++;

		    	removeAgents();
		    	updateRisks();
				//updateRisks2();

				//displayAgents();
				displayBoard();

		    	Point[] higherRiskPos = higherRiskPositions(time);
		    	
				for(Agent a : UAVs){
					if (a.fuel == 0 && !a.destination.equals(new Point(0,0))){
						a.destination = new Point(0,0);
						a.available = false;
						a.determinPath();
						//se estava a vigiar um fogo mandar outro drone para la
						if(fire[a.position.x][a.position.y])
							newFires.add(a.position);
					}
					else if (a.position.equals(new Point(0,0))){
						a.waiting_time--;
						if(a.waiting_time == 0){
							a.waiting_time = 50;
							a.available = true;
							Random rand = new Random();
							a.fuel = rand.nextInt(1000)+200;
						}
					}
					if(a.available && !fire[a.position.x][a.position.y]){ 	
						//a.utility = calcUtilities2(a,higherRiskPos);
						a.utility = calcUtilities(a,higherRiskPos);
						a.decide(destination_points, higherRiskPos, newFires);
						//a.naiveDecide(destination_points, higherRiskPos, newFires, nX, nY);
						if (fire[a.destination.x][a.destination.y])
							destination_points.add(a.destination);
					}
				}
				// COMMENT IF NAIVE
				for(Point p: higherRiskPos){
					double max = 0;
					int maxA = -1;
					for(Agent a: UAVs){
						if(!a.available || a.destination.equals(new Point(0,0)))
							continue;
						int index = getIndex(higherRiskPos,p);
						if(a.destination.equals(p) && a.utility[index]>=max){
							max = a.utility[index];
							maxA = UAVs.indexOf(a);
						}
					}
					if (maxA != -1){
						destination_points.add(UAVs.get(maxA).destination);
						UAVs.get(maxA).available = false;
						for(Agent a: UAVs){
							if(a.available){
								a.decide(destination_points, higherRiskPos, newFires);
							}
						}
					}
				}

				/*for (Agent a: UAVs){
					if(!(a.available && !fire[a.position.x][a.position.y]))
						continue;
					for (Agent a2: UAVs){
						if(!(a2.available && !fire[a2.position.x][a2.position.y]) && a==a2)
							continue;
						if (a.destination.equals(a2.destination)){
							int index_a = getIndex(higherRiskPos,a.destination);
							int index_a2 = getIndex(higherRiskPos,a2.destination);
							double a_utility = a.utility[index_a];
							double a2_utility = a2.utility[index_a2];
							if (a_utility >= a2_utility){
								destination_points.add(a.destination);
								a2.decide(destination_points, higherRiskPos, newFires);
								destination_points.remove(a.destination);
							}
							else{
								destination_points.add(a2.destination);
								a.decide(destination_points, higherRiskPos, newFires);	
								destination_points.remove(a2.destination);
							}
						}
					}
					destination_points.add(a.destination);
					a.available = false;
				}*/


				for (Agent a: UAVs){
					Point reached = a.go();

					a.cleared_risk += risk_sum[a.position.x][a.position.y];

					if (fire[a.position.x][a.position.y])
						a.available = false;
					markAsVisited(a);
					if (reached != null)
						destination_points.remove(reached);
				}	
				clearRandomFire();

				try {
					sleep(time*10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
	    	}
	    }
	}
	
	public void run(int time) {
		runThread = new RunThread(time);
		runThread.start();
		//displayAgents();
	}

	public void reset() {
		initialize();
		displayAgents();	
		displayBoard();
	}

	public void step() {
		
		int time = 100;
		
	}

	public void stop() {
		runThread.interrupt();
		runThread.stop();
		//displayAgents();
	}

	public void displayBoard(){
		GUI.displayBoard(this);
		GUI.displayAgents(this);
	}

	public void displayAgents(){
		GUI.displayAgents(this);
	}
	
	public void removeAgents(){
		GUI.removeAgents(this);
	}
	
	public int calcMin(Point[] array) {
		int lowest = 0;
		for (int i = 1; i < array.length; i++) {
			if(calcRealRisks(array[i].x,array[i].y) < calcRealRisks(array[lowest].x,array[lowest].y))
				lowest = i;
		}
		return lowest;
	}
	
	//calcs 5 positiond with highest risk, ALSO updates risk
	public Point[] higherRiskPositions(int time){
		Point[] higherRiskPos = new Point[nUAVs*2];
		Arrays.fill(higherRiskPos, new Point(0,0));

		int min_index = 0;
		for(int x=0; x<nX; x++) {
			for(int y=0; y<nY; y++) {
				last_visited[x][y] += time*10;
				//find the 5 positions with higher risk of fire
				if(calcRealRisks(x,y) > calcRealRisks(higherRiskPos[min_index].x,higherRiskPos[min_index].y)) { 
					boolean taken = false;
					Point p = new Point(x,y);
					for (Agent a : UAVs) {
						if(p.equals(a.destination))
							taken = true;
					}
					if(taken) continue;
					higherRiskPos[min_index] = p;
					// the 5th highest one (for now)
					min_index = calcMin(higherRiskPos);			
				}
			}
		}
		return higherRiskPos;
	}
	
	public double calcRealRisks(int x,int y){
		double risk;
		if (last_visited[x][y] >= warning_time) {
			risk = risk_limit[x][y];
		}
		else {
			risk = (last_visited[x][y]/warning_time)*5;
			double new_risk;
			if(risk>risk_limit[x][y])
				new_risk = risk_limit[x][y];
			else
				new_risk = risk;
			risk = new_risk;
		}	
		return risk;
	}

	public void updateRisks() {	
		risk_sum = new double[nX][nY];
		for(int x=0; x<nX; x++) {
			for(int y=0; y<nY; y++) {
				if (last_visited[x][y] >= warning_time) {
					board[x][y] = risk_limit[x][y];
					risk_sum[x][y] +=  Math.pow(risk_limit[x][y]/5,2);
				}
				else {
					double risk = (last_visited[x][y]/warning_time)*5;
					double new_risk;
					if(risk>risk_limit[x][y])
						new_risk = risk_limit[x][y];
					else
						new_risk = risk;
					board[x][y] = new_risk;
					risk_sum[x][y] +=  Math.pow(new_risk/5,2);
				}
				for(int i=-1; i<2; i++) {
					for(int j=-1; j<2; j++) {
						if ( (0 <= x+i && x+i < nX) && (0 <= y+j && y+j < nY)) {
							risk_sum[x+i][y+j] +=  Math.pow(board[x][y]/5,2);
						}
					}
				}
			}
		}
	}

	public void markAsVisited(Agent a){
		int x = a.position.x;
		int y = a.position.y;
		for(int i=-1; i<2; i++) {
			for(int j=-1; j<2; j++) {
				if ( (0 <= x+i && x+i < nX) && (0 <= y+j && y+j < nY)) {
					last_visited[x+i][y+j] = 0;
				}
			}
		}
	}

	//CALCULAR UTILIDADES COM DIFERENTES POTENCIAS
	public void updateRisks2() {	
		risk_sum = new double[nX][nY];
		for(int x=0; x<nX; x++) {
			for(int y=0; y<nY; y++) {
				if (last_visited[x][y] >= warning_time) {
					board[x][y] = risk_limit[x][y];
					risk_sum[x][y] +=  risk_limit[x][y]/5;
				}
				else {
					double risk = (last_visited[x][y]/warning_time)*5;
					double new_risk;
					if(risk>risk_limit[x][y])
						new_risk = risk_limit[x][y];
					else
						new_risk = risk;
					board[x][y] = new_risk;
					risk_sum[x][y] += new_risk/5;
				}
				for(int i=-1; i<2; i++) {
					for(int j=-1; j<2; j++) {
						if ( (0 <= x+i && x+i < nX) && (0 <= y+j && y+j < nY)) {
							risk_sum[x+i][y+j] += board[x][y]/5;
						}
					}
				}
			}
		}
	}
	//CALCULAR UTILIDADES COM DIFERENTES POTENCIAS
	public double[] calcUtilities2(Agent agent, Point[] higherRiskPositins) {
		double[] utility = new double[higherRiskPositins.length];
		int x = agent.position.x;
		int y = agent.position.y;
		for (int i = 0; i < higherRiskPositins.length; i++) {
			int move_x = higherRiskPositins[i].x;
			int move_y = higherRiskPositins[i].y;
			int dist = 0;

			int xx = x,yy = y;
			if (x < move_x && y < move_y)
				for (xx = x, yy = y; xx < move_x && yy < move_y; xx++, yy++){
					utility[i] += Math.pow(risk_sum[xx][yy]/9,2);
					dist++;
				}
			else if (x < move_x && y > move_y)
				for (xx = x, yy = y; xx < move_x && yy > move_y; xx++, yy--){
					utility[i] += Math.pow(risk_sum[xx][yy]/9,2);
					dist++;
				}
			else if (x > move_x && y > move_y)
				for (xx = x, yy = y; xx > move_x && yy > move_y; xx--, yy--){
					utility[i] += Math.pow(risk_sum[xx][yy]/9,2);
					dist++;
				}
			else if (x > move_x && y < move_y)
				for (xx = x, yy = y; xx > move_x && yy < move_y; xx--, yy++){
					utility[i] += Math.pow(risk_sum[xx][yy]/9,2);
					dist++;
				}
			utility[i] += Math.pow(risk_sum[xx][yy]/9,2);
			dist++;
			if (xx != move_x && yy == move_y){
				if (xx < move_x)
					for (int xxx = xx; xxx <= move_x; xxx++){
						utility[i] += Math.pow(risk_sum[xxx][move_y]/9,2);
						dist++;
					}
				else if (xx > move_x)
					for (int xxx = xx; xxx >= move_x; xxx--){
						utility[i] += Math.pow(risk_sum[xxx][move_y]/9,2);
						dist++;
					}
			}
			if (xx == move_x && yy != move_y){
				if (yy < move_y)
					for (int yyy = yy; yyy <= move_y; yyy++){
						utility[i] += Math.pow(risk_sum[move_x][yyy]/9,2);
						dist++;
					}
				else if (yy > move_y)
					for (int yyy = yy; yyy >= move_y; yyy--){
						utility[i] += Math.pow(risk_sum[move_x][yyy]/9,2);
						dist++;
					}
			}
			//utility[i] = utility[i]/dist;
			utility[i] = utility[i];
		}
		return utility;
	}
	
	
	//CALCULAR UTILIDADE ANDANDO NA DIAGONAL
	public double[] calcUtilities(Agent agent, Point[] higherRiskPositins) {
		double[] utility = new double[higherRiskPositins.length];
		int x = agent.position.x;
		int y = agent.position.y;
		for (int i = 0; i < higherRiskPositins.length; i++) {
			int move_x = higherRiskPositins[i].x;
			int move_y = higherRiskPositins[i].y;
			int dist = 0;

			int xx = x,yy = y;
			if (x < move_x && y < move_y)
				for (xx = x, yy = y; xx < move_x && yy < move_y; xx++, yy++){
					utility[i] += risk_sum[xx][yy];
					dist++;
				}
			else if (x < move_x && y > move_y)
				for (xx = x, yy = y; xx < move_x && yy > move_y; xx++, yy--){
					utility[i] += risk_sum[xx][yy];
					dist++;
				}
			else if (x > move_x && y > move_y)
				for (xx = x, yy = y; xx > move_x && yy > move_y; xx--, yy--){
					utility[i] += risk_sum[xx][yy];
					dist++;
				}
			else if (x > move_x && y < move_y)
				for (xx = x, yy = y; xx > move_x && yy < move_y; xx--, yy++){
					utility[i] += risk_sum[xx][yy];
					dist++;
				}
			utility[i] += risk_sum[xx][yy];
			dist++;
			if (xx != move_x && yy == move_y){
				if (xx < move_x)
					for (int xxx = xx; xxx <= move_x; xxx++){
						utility[i] += risk_sum[xxx][move_y];
						dist++;
					}
				else if (xx > move_x)
					for (int xxx = xx; xxx >= move_x; xxx--){
						utility[i] += risk_sum[xxx][move_y];
						dist++;
					}
			}
			if (xx == move_x && yy != move_y){
				if (yy < move_y)
					for (int yyy = yy; yyy <= move_y; yyy++){
						utility[i] += risk_sum[move_x][yyy];
						dist++;
					}
				else if (yy > move_y)
					for (int yyy = yy; yyy >= move_y; yyy--){
						utility[i] += risk_sum[move_x][yyy];
						dist++;
					}
			}
			utility[i] = utility[i]/dist;
		}
		return utility;
	}

	public void clearRandomFire(){
		//clears 3 random
		Point p;
		Random rand = new Random();

		int x = rand.nextInt(nX);
		int y = rand.nextInt(nY);
		fire[x][y] = false;
		p = new Point(x,y);
		newFires.remove(p);
		

		x = rand.nextInt(nX);
		y = rand.nextInt(nY);
		fire[x][y] = false;
		p = new Point(x,y);
		newFires.remove(p);
		

		x = rand.nextInt(nX);
		y = rand.nextInt(nY);
		fire[x][y] = false;
		p = new Point(x,y);
		newFires.remove(p);
	}

	public int getIndex(Point[] array, Point p){
	  	int index = 0;
	  	for (int i = 0; i<array.length; i++){
			if (array[i].equals(p)){
				index = i;
				break;
			}
		}
		return index;
	}
}
