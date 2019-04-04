package fireprevention;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;


/**
 * Graphical interface
 * @author Rui Henriques
 */
public class GraphicalInterface extends JFrame {

	private static final long serialVersionUID = 1L;
	
	static JTextField speed;
	static JPanel boardPanel;
	static JButton run, reset, step;
	
	public GraphicalInterface(Board board) {
		setTitle("FirePrevention");		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(null);
		setSize(640, 600);
		add(createButtonPanel(board));
		boardPanel = new JPanel();
		boardPanel.setSize(new Dimension(600,500));
		boardPanel.setLocation(new Point(20,60));
		boardPanel.setLayout(new GridLayout(board.nX,board.nY));
		for(int i=0; i<board.nX; i++)
			for(int j=0; j<board.nY; j++)
				boardPanel.add(new JPanel());
		displayBoard(board);
		displayAgents(board);
		board.GUI = this;
		add(boardPanel);
	}

	public void displayBoard(Board board) {
		for(int i=0; i<board.nX; i++){
			for(int j=0; j<board.nY; j++){
				final int fireX = i;
				final int fireY = j;
				double value = board.board[i][j];
				int R = (int) (255*value)/5;
				int G = (int) (255*(5-value))/5; 
				JPanel p = ((JPanel)boardPanel.getComponent(j*board.nY+i));
				p.setBorder(BorderFactory.createLineBorder(Color.white));
				if(board.fire[i][j]) 
					p.setBackground(new Color(255,140,0));
				else
					p.setBackground(new Color(R,G,0));
				p.addMouseListener(new MouseAdapter() { 
			        public void mousePressed(MouseEvent me) {
						board.fire[fireX][fireY] = true;
						//board.fire[fireX][fireY] = !board.fire[fireX][fireY];
			            if(board.fire[fireX][fireY] && !board.newFires.contains(new Point(fireX,fireY))) {
							board.newFires.add(new Point(fireX,fireY));
			            }
			        } 
			    }); 
				if(board.fire[i][j])
					p.setBackground(new Color(255,140,0));
				else
					p.setBackground(new Color(R,G,0));

				
			}
		}
		JPanel p = ((JPanel)boardPanel.getComponent(0));
		p.setBackground(new Color(196,0,255));
		boardPanel.invalidate();
	}
	
	public void removeAgents(Board board) {
		for(Agent agent : board.UAVs){
			JPanel p = ((JPanel)boardPanel.getComponent(agent.position.x+agent.position.y*board.nY));
			p.setBorder(BorderFactory.createLineBorder(Color.white));			
		}
		boardPanel.invalidate();
	}

	public void displayAgents(Board board) {
		for(Agent agent : board.UAVs){
			JPanel p = ((JPanel)boardPanel.getComponent(agent.position.x+agent.position.y*board.nY));
			p.setBorder(BorderFactory.createLineBorder(Color.blue,3));			
		}
		boardPanel.invalidate();
	}

	private Component createButtonPanel(Board board) {
		JPanel panel = new JPanel();
		panel.setSize(new Dimension(600,50));
		panel.setLocation(new Point(0,0));
		
		step = new JButton("Step");
		panel.add(step);
		step.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(run.getText().equals("Run")) board.step();
				else board.stop();
			}
		});
		reset = new JButton("Reset");
		panel.add(reset);
		reset.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				board.reset();
			}
		});
		run = new JButton("Run");
		panel.add(run);
		run.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(run.getText().equals("Run")){
					int time = -1;
					try {
						time = Integer.valueOf(speed.getText());
					} catch(Exception e){
						JTextPane output = new JTextPane();
						output.setText("Please insert an integer value to set the time per step\nValue inserted = "+speed.getText());
						JOptionPane.showMessageDialog(null, output, "Error", JOptionPane.PLAIN_MESSAGE);
					}
					if(time>0){
						board.run(time);
	 					run.setText("Stop");						
					}
 				} else {
					board.stop();
 					run.setText("Run");
 				}
			}
		});
		speed = new JTextField(" time per step in [1,100] ");
		speed.setMargin(new Insets(5,5,5,5));
		panel.add(speed);
		
		return panel;
	}
}
