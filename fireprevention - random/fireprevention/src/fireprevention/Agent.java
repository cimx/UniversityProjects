package fireprevention;

import java.awt.Point;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

/**
 * Agent behavior
 * @author Rui Henriques
 */
public class Agent {

	public Point position;
	public Point destination;
	public boolean available;
	public double[] utility;
	public ArrayList<Point> path;
	public int fuel;
	public int waiting_time;

	public int id;
	public double cleared_risk = 0;
	public String file;
	BufferedWriter bw;
	
	public Agent(Point position,int id){ //extend for the parameterized instantiation of agents
		this.position = position;
		this.available = true;
		this.path = new ArrayList<Point>();
		Random rand = new Random();
		this.fuel = rand.nextInt(1000)+200;
		this.waiting_time = 50;
		this.id = id;
	} 
	
	public void write() throws IOException{
		String file = new File("").getAbsolutePath()+this.id + ".txt";
		System.out.println(file);
		BufferedWriter bw = new BufferedWriter(new FileWriter(new File(file), true));
	//	bw.write(cleared_risk);
		bw.write(",");
	}

	public Point go(){
		fuel--;
		if (!path.isEmpty()){
			Point newPos = path.remove(0);
			this.position = newPos;
			
		}
		if (this.position.equals(this.destination) && !this.position.equals(new Point(0,0))){
			available = true;
			return this.destination;
		}
		return null;		
	}

	public Point naiveDecide(ArrayList<Point> destination_points, Point[] higherRiskPositions, ArrayList<Point> newFires, int nX, int nY){

		//double[] aux_utility = this.utility;
		if(newFires.isEmpty()) {
			Random rand = new Random();
			
			int x = rand.nextInt(nX); 
			int y = rand.nextInt(nY);
			this.destination = new Point(x,y);
			/*for (Point p: higherRiskPositions)
				if (destination_points.contains(p)){
					int i = getIndex(higherRiskPositions, p);
					this.utility[i] = 0;
				}
			double max = getMax(utility);
			int index = getIndex(utility, max);
			this.utility = aux_utility;
		
			this.destination = higherRiskPositions[index];*/
		}
		else {
			Point p;
			while(!newFires.isEmpty()) {
				//visita fogo 0 -> que começou há mais tempo
				p = newFires.remove(0);
				if(!destination_points.contains(p)) {
					this.destination = p;
					this.available = false;
					break;
				}
			}
		}
		determinPath();
		this.available = false;
		return this.destination;
	}


	public Point decide(ArrayList<Point> destination_points, Point[] higherRiskPositions, ArrayList<Point> newFires){
		//available = false; 				//TODO alterar quando houver negociacao

		//se uma das posicoes em higherRiskPositions ja for o destino 
		//de outro UAV a utilidade dessa posicao passa a 0
		double[] aux_utility = this.utility;
		if(newFires.isEmpty()) {
			for (Point p: higherRiskPositions)
				if (destination_points.contains(p)){
					int i = getIndex(higherRiskPositions, p);
					this.utility[i] = 0;
				}
			double max = getMax(utility);
			int index = getIndex(utility, max);
			this.utility = aux_utility;
		
			this.destination = higherRiskPositions[index];
		}
		else {
			Point p;
			while(!newFires.isEmpty()) {
				//visita fogo 0 -> que começou há mais tempo
				p = newFires.remove(0);
				if(!destination_points.contains(p)) {
					this.destination = p;
					this.available = false;
					break;
				}
			}
		}
		determinPath();
		return this.destination;
	}

	public void determinPath(){
		int move_x = this.destination.x;
		int move_y = this.destination.y;

		this.path = new ArrayList<Point>();

		/* DETERMINAR CAMINHO ANDANDO NA HORIZONTAL E DEPOIS VERTICAL
			for (int xx = position.x; xx <= move_x; xx++)
				path.add(new Point(xx,position.y));
			for (int xx = position.x; xx > move_x; xx--)
				path.add(new Point(xx,position.y));
			for (int yy = position.y; yy <= move_y; yy++)
				path.add(new Point(move_x,yy));
			for (int yy = position.y; yy > move_y; yy--)
				path.add(new Point(move_x,yy));
			if (!path.contains(this.destination))
				path.add(this.destination);
		*/

		//DETERMINAR CAMINHO ANDANDO NA DIAGONAL
		int xx = this.position.x;
		int yy = this.position.y;
		if (this.position.x < move_x && this.position.y < move_y)
			for (xx = this.position.x, yy = this.position.y; xx < move_x && yy < move_y; xx++, yy++)
				this.path.add(new Point(xx,yy));
		else if (this.position.x < move_x && this.position.y > move_y)
			for (xx = this.position.x, yy = this.position.y; xx < move_x && yy > move_y; xx++, yy--)
				this.path.add(new Point(xx,yy));
		else if (this.position.x > move_x && this.position.y > move_y)
			for (xx = this.position.x, yy = this.position.y; xx > move_x && yy > move_y; xx--, yy--)
				this.path.add(new Point(xx,yy));
		else if (this.position.x > move_x && this.position.y < move_y)
			for (xx = this.position.x, yy = this.position.y; xx > move_x && yy < move_y; xx--, yy++)
				this.path.add(new Point(xx,yy));
		this.path.add(new Point(xx,yy));

		if (xx != move_x && yy == move_y){
			if (xx < move_x)
				for (int xxx = xx; xxx <= move_x; xxx++)
					this.path.add(new Point(xxx,move_y));
			else if (xx > move_x)
				for (int xxx = xx; xxx >= move_x; xxx--)
					this.path.add(new Point(xxx,move_y));
		}
		if (xx == move_x && yy != move_y){
			if (yy < move_y)
				for (int yyy = yy; yyy <= move_y; yyy++)
					this.path.add(new Point(move_x,yyy));
			else if (yy > move_y)
				for (int yyy = yy; yyy >= move_y; yyy--)
					this.path.add(new Point(move_x,yyy));
		}
	}

	/** A: actuators */

	/** B: perceptors */

	/** C: decision process */


	/** AUXILIARES*/
	 
	public double getMax(double[] inputArray){ 
	    double maxValue = inputArray[0]; 
	    for(int i=0;i < inputArray.length;i++){ 
			if (inputArray[i] > maxValue)
	        	maxValue = inputArray[i]; 
	    } 
	    return maxValue;
	}

	public int getIndex(double[] array, double e){
	  	int index = 0;
	  	for (int i = 0; i<array.length; i++){
			if (array[i] == e){
				index = i;
				break;
			}
		}
		return index;
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
