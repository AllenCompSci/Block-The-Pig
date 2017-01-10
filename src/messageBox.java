import javax.swing.JOptionPane;
public class messageBox {
private int levelCount;
private int gamesPlayed;
	public messageBox(){
	    levelCount = 0;
	    gamesPlayed = 0;
	    
}

	

public void directions(String titleBar, boolean pig, boolean impass)
{
	String dir = "Key Press   : Event      " + "\n";
		   dir+= "Press F1        Reset Level" + "\n";
		   dir+= "Press F2        Display Path" + "\n";
		   dir+= "Press F5        Hide Path" + "\n";
		   dir+= "Press F6        Draw Block Location:";
		   dir+= (impass)? "On" : "Off" ;
		   dir+= "\n";
		   dir+= "Press F7        Draw Level";
		   dir+= "\n";
		   dir+= "Press F12     Display Directions" + "\n";
		   dir+= "Level : " + levelCount + "\n";
		   dir+= "Attempts : " + gamesPlayed +"\n";
    JOptionPane.showMessageDialog(null, dir, titleBar, JOptionPane.INFORMATION_MESSAGE);
}
public void TrappedPig(){
	 JOptionPane.showMessageDialog(null, "You have successfully trapped the pig", "WINNER", JOptionPane.INFORMATION_MESSAGE);
	 gamesPlayed++;
	 levelCount++;
}
public void ESCAPE(){
	JOptionPane.showMessageDialog(null, "You have failed to trapped the pig", "Try Again", JOptionPane.INFORMATION_MESSAGE);
	 gamesPlayed++;
}

public void Imprisoned() {
	JOptionPane.showMessageDialog(null, "You have Imprisoned the pig.", "Congratulations", JOptionPane.INFORMATION_MESSAGE);
	gamesPlayed++;
	levelCount++;
}

}
