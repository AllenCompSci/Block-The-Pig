import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JOptionPane;



public class board implements Runnable, KeyListener, WindowListener, MouseListener{
private square [][] game;
public final String TITLE = "Block the Pig";
public final Dimension SIZE = new Dimension(600, 950);
public JFrame frame;
private boolean isRunning, isDone;
private Image imgBuffer;
private BufferedImage stone, grass, pig, dirt;
private TexturePaint stoneOcta, grassOcta, dirty;
private int side = 50;
private int PigI;
private int PigJ;
private boolean change, drawImpass, DrawPig;
@SuppressWarnings("unused")
private Color BROWN;
private boolean showPath;
@SuppressWarnings("unused")
private boolean AITurn, UserTurn;
private int TurnCount = 0;
private pigPath ShortestPath;
private messageBox info;

private void loadImages() {

    try {
    	
       
        stone = ImageIO.read(this.getClass().getResource("java.png"));
        grass = ImageIO.read(this.getClass().getResource("grass.png"));
        pig = ImageIO.read(this.getClass().getResource("16.png")); 
        dirt = ImageIO.read(this.getClass().getResource("dirt.png"));
        grassOcta = new TexturePaint(grass, new Rectangle(0, 0, 90, 60));
		stoneOcta = new TexturePaint(stone, new Rectangle(0, 0, 90, 60));
		dirty = new TexturePaint(dirt, new Rectangle(0, 0, 50, 50));
		
 
    } catch (IOException ex) {

        Logger.getLogger(driver.class.getName()).log(Level.SEVERE,null, ex);
    }
}
	public board() {
		loadImages();
		
		ShortestPath = new pigPath();
		info = new messageBox();
		showPath = false;
		setChange(true);
		setDrawImpass(false);
		setDrawPig(false);
		BROWN = new Color(139,69,19);
		setGame(new square [11][5]); 
		createTiles();
		frame = new JFrame();
		frame.addKeyListener(this);
		frame.addWindowListener(this);
		frame.addMouseListener(this);
		frame.setSize(SIZE);
		frame.setTitle(TITLE);
		isRunning = true;
		isDone = false;
		frame.setVisible(true);
		frame.setLayout(null);
			
		imgBuffer = frame.createImage(SIZE.width, SIZE.height);
	}

	
	private void Game(){
		ShortestPath.dijikstra(game, PigI, PigJ);
		if(TurnCount < 3){
			setDrawImpass(true);
		}
		else if(TurnCount % 2 == 0){
			setDrawImpass(true);
		}
		else{
			setDrawImpass(false);
			AIDecision();
		}
	}
	private void AIDecision(){
		if(game[PigI][PigJ].isBlocked()){
			draw();
			for(square node : game[PigI][PigJ].getSurrounding()){
				if(node.isImpassable()){
					
				}
				else{
					info.TrappedPig();
					resetLevel();
					return;
				}
			}
			info.Imprisoned();
			resetLevel();
			return;
		}
		for(int i = 5; i >= 0; i--){
				if(game[PigI][PigJ].getSurrounding()[i] != null){
					if(game[PigI][PigJ].getSurrounding()[i].isPath()){
						game[PigI][PigJ].setPig(false);
						getPig(game[PigI][PigJ].getSurrounding()[i]);
						game[PigI][PigJ].setPig(true);
						i = -5;
						TurnCount ++;
					}
				}
		}
		if(game[PigI][PigJ].isEdge()){
			info.ESCAPE();
			resetLevel();
		}
		setChange(true);		
		
	}
	
	public square [][] getGame() {
		return game;
	}
	public void setGame(square [][] game) {
		this.game = game;
	}
	private void createTiles(){
		int x = 0; 
		int y = 50;
		setChange(false);
		double h = CalculateH(side);
        double r = CalculateR(side); 
		for(int i = 0; i < 11; i++){
			System.out.print((i%2 == 0)? "" : " ");
			x = (i%2 == 0)? 100: 100 + side-5;
			for(int j = 0; j < 5; j++){
				game[i][j] = new square((i== 0) || (i == 10) || (j==0) || (j==4) , new Point(x,y),side, h, r, i, j);
				x+= 2*r;
				//System.out.println("i : " + i + ", j : " + j + "is Edge : " + game[i][j].isEdge());
				//System.out.print((game[i][j].isEdge())? "x" : "o");
			}
			y+= h+side;
			//System.out.println();
		}
		game[5][2].setPig(true);
		getPig(game[5][2]);
		//PigI = 5;
		//PigJ = 2;
		setConnection();
		setImpass(0,0);
		setImpass(1,4);
		setImpass(2,4);
		setImpass(4,1);
		setImpass(4,2);
		setImpass(6,1);
		setImpass(6,2);
		setImpass(7,0);
		setImpass(7,2);
		setImpass(8,0);
		setImpass(8,3);
		setImpass(9,0);
		setImpass(9,4);
	}
	private void setConnection(){
		
		for(int j = 0; j < 11; j++){
			for(int i = 0; i < 5; i++){
				// Connection 0 - 1 o'clock
				if(j > 0){
					if(j%2 == 0)
						game[j][i].getSurrounding()[0] = game[j-1][i];
					else if(i < 4)
						game[j][i].getSurrounding()[0] = game[j-1][i+1];
				}
				// Connection 1 - 3 o'clock
				if(i < 4){
					game[j][i].getSurrounding()[1] = game[j][i+1];
				}
				// Connection 2 - 5 o'clock
				if(j < 10){
					if(j%2 == 0){
						game[j][i].getSurrounding()[2] = game[j+1][i];
						
					}
					else if(i < 4){
						game[j][i].getSurrounding()[2] = game[j+1][i+1];
					}
				// Connection 3 - 7 o'clock
					if(j%2 == 0){
						if(i > 0){
							game[j][i].getSurrounding()[3] = game[j+1][i-1];
						}	
					}
					else {
						game[j][i].getSurrounding()[3] = game[j+1][i];
					}
				}
				
				
				// Connection 4 - 9 o'clock
				if(i > 0){
					game[j][i].getSurrounding()[4] = game[j][i-1];
				}
				// Connection 5 - 11 o'clock
				if(j > 0){
					if(j % 2 == 0){
						if(i > 0){
							game[j][i].getSurrounding()[5] = game[j-1][i-1];
						}
					}
					else{
						game[j][i].getSurrounding()[5] = game[j-1][i];
					}
				}
			}
		}
		
	
	
				
	}
	private void setImpass(int x, int y){
		if(!game[x][y].isPig() && !game[x][y].isImpassable())
			game[x][y].setImpassable(true);
	}
	private void MimicLevel(){
		DrawPig = true;
		TurnCount = 0;	
		for(int i = 0; i < 11; i++){
				for(int j = 0; j < 5; j++){
					game[i][j].setPath(false);
					game[i][j].setImpassable(false);
					game[i][j].setPig(false);
					game[i][j].setBlocked(false);
				}
			}
		game[5][2].setPig(true);
		PigI = 5;
		PigJ = 2;
		TurnCount -= infoBox();
	}
	public static int infoBox()
    {
        int x = 0;
		//JOptionPane.showMessageDialog(null, infoMessage, "InfoBox: " + titleBar, JOptionPane.INFORMATION_MESSAGE);
		x = Integer.valueOf(JOptionPane.showInputDialog("Input number of Blocks to place : "));
        return x;
    }
	private void resetLevel(){
	TurnCount = 0;	
	for(int i = 0; i < 11; i++){
			for(int j = 0; j < 5; j++){
				game[i][j].setPath(false);
				game[i][j].setImpassable(false);
				game[i][j].setPig(false);
				game[i][j].setBlocked(false);
			}
		}
	int NumImpass = (int)(Math.random()*8 +5);
	int Current = 0;
	while(Current != NumImpass){
		int Ni, Nj;
		Ni = (int)(Math.random()*11);
		Nj = (int)(Math.random()*5);
		if(Ni != 5 && Nj != 2){
			if(!game[Ni][Nj].isImpassable()){
				game[Ni][Nj].setImpassable(true);
				Current++;
			}
		}
	}
	game[5][2].setPig(true);
	PigI = 5;
	PigJ = 2;
	
}
	@Override
	public void windowActivated(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void windowClosed(WindowEvent arg0) {
		// TODO Auto-generated method stub
		while(true){
			
			if(isDone){
				//System.out.println("EXIT"); // never gets here
				System.exit(0);
			}
			try{
				Thread.sleep(100);
			}
			catch(InterruptedException ie){
				ie.printStackTrace();
			}
		}
	}
	@Override
	public void windowClosing(WindowEvent arg0) {
		// TODO Auto-generated method stub
		frame.setVisible(false);
		frame.dispose();
		isRunning = false;
	}
	@Override
	public void windowDeactivated(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void windowDeiconified(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void windowIconified(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void windowOpened(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void keyPressed(KeyEvent arg0) {
		// TODO Auto-generated method stub
		int Key;
		Key = arg0.getKeyCode();
		
		if(Key == 105){
			
			//System.out.println("Travel up right");
			if(game[PigI][PigJ].getSurrounding()[0] != null   ) if(  !game[PigI][PigJ].getSurrounding()[0].isImpassable()){
				game[PigI][PigJ].setPig(false);
				getPig(game[PigI][PigJ].getSurrounding()[0]);
				game[PigI][PigJ].setPig(true);
				
			}
		}
		else if(Key == 102){
			
			//System.out.println("Travel right");
		
			if(game[PigI][PigJ].getSurrounding()[1] != null  ) if(  !game[PigI][PigJ].getSurrounding()[1].isImpassable()){
				game[PigI][PigJ].setPig(false);
				getPig(game[PigI][PigJ].getSurrounding()[1]);
				game[PigI][PigJ].setPig(true);
				
			}
		}
		else if(Key == 99){
			
		//	System.out.println("Travel down right");
			if(game[PigI][PigJ].getSurrounding()[2] != null  ) if(  !game[PigI][PigJ].getSurrounding()[2].isImpassable()){
				game[PigI][PigJ].setPig(false);
				getPig(game[PigI][PigJ].getSurrounding()[2]);
				game[PigI][PigJ].setPig(true);
				
			}
		}
		else if(Key == 97){
			
			//System.out.println("Travel down left");
			if(game[PigI][PigJ].getSurrounding()[3] != null  ) if(  !game[PigI][PigJ].getSurrounding()[3].isImpassable()){
				game[PigI][PigJ].setPig(false);
				getPig(game[PigI][PigJ].getSurrounding()[3]);
				game[PigI][PigJ].setPig(true);
				
			}
		}else if(Key == 100){
			
			//System.out.println("Travel left");
			if(game[PigI][PigJ].getSurrounding()[4] != null ) if(  !game[PigI][PigJ].getSurrounding()[4].isImpassable()){
				game[PigI][PigJ].setPig(false);
				getPig(game[PigI][PigJ].getSurrounding()[4]);
				game[PigI][PigJ].setPig(true);
			}
		}else if(Key == 103){
			
			//System.out.println("Travel up left");
			if(game[PigI][PigJ].getSurrounding()[5] != null ) if( !game[PigI][PigJ].getSurrounding()[5].isImpassable()){
				game[PigI][PigJ].setPig(false);
				getPig(game[PigI][PigJ].getSurrounding()[5]);
				game[PigI][PigJ].setPig(true);
			}
		}
		else if(Key == KeyEvent.VK_F1){
			resetLevel();
		}
		else if(Key == KeyEvent.VK_F2){
			setShowPath(!showPath);			
		}
		else if(Key == KeyEvent.VK_F5){
			resetPath();
		}
		else if(Key == KeyEvent.VK_F6){
			setDrawImpass(!drawImpass);
		}
		else if(Key == KeyEvent.VK_F7){
			MimicLevel();
		}
		else if(Key == KeyEvent.VK_F8){
			setDrawPig(false);
			setDrawImpass(false);
		}
		else if(Key == KeyEvent.VK_F12){
			info.directions("Directions : ", DrawPig, drawImpass);
			
		}
	}
	private void setShowPath(boolean b) {
		// TODO Auto-generated method stub
		showPath = b;
	}


	private void resetPath(){
		for(square [] nodes : game)
			for(square node : nodes)
				node.setPath(false);
	}
	@Override
	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		while(isRunning){
			Game();
			draw();
			
			if(change){
				setChange(false);
				ShortestPath.dijikstra(game, PigI, PigJ);
			}
			try{Thread.sleep(50);}
			catch(InterruptedException ie){
				ie.printStackTrace();
			}
		}
		isDone = true;
	}
	private void draw() {
		
		// TODO Auto-generated method stub
		Graphics2D g2d = (Graphics2D) imgBuffer.getGraphics();
		//g2d.setBackground(Color.YELLOW);
		g2d.setPaint(dirty);
		g2d.fillRect(0, 0, SIZE.width, SIZE.height);
		for(int i = 0; i < 11; i++){
			for(int j = 0; j < 5; j++){
				//g2d.setColor(Color.GREEN);
				
				g2d.setPaint(grassOcta);
				if(game[i][j].isPig())
				{
				
				}
				else if(game[i][j].isPath() && showPath){
					g2d.fill(game[i][j].getSquareset());
					//g2d.setPaint(null);
					Color pathColor = new Color(255, 255, 0, 100);
					g2d.setColor(pathColor);
					//g2d.fill(game[i][j].getSquareset());
				}
				else if(game[i][j].isImpassable()){
					//g2d.setColor(Color.DARK_GRAY);
					
					g2d.setPaint(stoneOcta);
				}
				
				g2d.fill(game[i][j].getSquareset());
				
				g2d.setColor(Color.BLACK);
				if(game[i][j].isEdge() && !game[i][j].isImpassable())
					g2d.setColor(Color.WHITE);
				if(game[i][j].isPig())
					g2d.setColor(Color.PINK);
				
				g2d.draw(game[i][j].getSquareset());
				}
		}
		g2d.drawImage(pig, game[PigI][PigJ].getSquareset().xpoints[5], game[PigI][PigJ].getSquareset().ypoints[5], game[PigI][PigJ].getSquareset().xpoints[2], game[PigI][PigJ].getSquareset().ypoints[2], 0, 0, 256, 256, null);
		g2d.setColor(Color.PINK);
		Stroke old = g2d.getStroke();
		g2d.setStroke(new BasicStroke(3));
		g2d.draw(game[PigI][PigJ].getSquareset());
		g2d.setStroke(old);
		if(isRunning)
		g2d = (Graphics2D) frame.getGraphics();
		g2d.drawImage(imgBuffer, 0,  0, SIZE.width, SIZE.height, 0, 0, SIZE.width, SIZE.height, null);
		g2d.dispose();
	}
	
	public static double CalculateH(float side)
	{
	    return Math.sin(DegreesToRadians(30)) * side;
	}

	public static double CalculateR(float side)
	{
	    return (Math.cos(DegreesToRadians(30)) * side);
	} 
	public static double DegreesToRadians(double degrees)
	{
	    return degrees * Math.PI / 180;
	}
	private void setPigI(square node){
		PigI = node.getX();
	}
	private void setPigJ(square node){
		PigJ = node.getY();
	}
	private void getPig(square node){
		setPigI(node);
		setPigJ(node);
	}
	public boolean isChange() {
		return change;
	}
	public void setChange(boolean change) {
		this.change = change;
	}
	@Override
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub
		Point click = arg0.getPoint();
		if(arg0.isMetaDown()){
			for(square [] nodes : game)
				for(square node : nodes){
					if(node.getSquareset().contains(click)){
						if(!node.isPig() && node.isImpassable()){
							node.setImpassable(false);
							setChange(true);
							TurnCount--;
						}
					}
				}
		}
		//System.out.println(click.toString());
		if(drawImpass || DrawPig){
			if(drawImpass){
				for(square [] nodes : game)
				for(square node : nodes){
					if(node.getSquareset().contains(click)){
						if(!node.isPig() && !node.isImpassable()){
							node.setImpassable(true);
							setChange(true);
							TurnCount++;
						}
					}
				}
			}
			else if(DrawPig){
				for(square [] nodes : game)
					for(square node : nodes){
						if(node.getSquareset().contains(click)){
							if(!node.isImpassable()){
								game[PigI][PigJ].setPig(false);
								getPig(node);
								node.setPig(true);
								setChange(true);
							}
						}
					}
			}
		}
	}
	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	public boolean isDrawImpass() {
		return drawImpass;
	}
	private void setDrawPig(boolean b) {
		if(b) {
			drawImpass = false;
		}
		DrawPig = b;		
	}
	public void setDrawImpass(boolean drawImpass) {
		if(drawImpass){
			DrawPig = false;
		}
		this.drawImpass = drawImpass;
	}
}
