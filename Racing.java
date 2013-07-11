import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.ImageObserver;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;


public class Racing extends JFrame {

        int width=600;  // Breite des Fensters
        int height=800; // H�he des Fensters
        
        RacingPanel rpanel;
        JTextField score=new JTextField();
        
        int step=0; // Geschwindigkeit der Animation
        int points=0;

        public static void main(String[] args) { Racing wnd = new Racing(); }

        public Racing() {
          setSize(width,height); 	// größe des Frames
          setTitle("Das ultimativ langweilige Rennspiel"); // Titel des Frames
          setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // closing operation des frames

          rpanel=new RacingPanel(this);  // Panel wird erstellt

          Container cpane=getContentPane(); // inhalt des gesammten container
          cpane.setLayout(new BorderLayout()); // cotainer layout auf border setzen
          cpane.add(rpanel,BorderLayout.CENTER); //rpanel( die strecke) im center plazieren

          JButton startbutton = new JButton("Start"); //Button hinzufügen ( liker rand)
          cpane.add(startbutton,BorderLayout.WEST);
          startbutton.resetKeyboardActions();
          
         
          cpane.add(score, BorderLayout.NORTH);
          score.setSize(100,100);
          score.setText("PUNKTE: "+points);
          
          
          addKeyListener(new KeyAdapter() { // Steuerung von Moped und Fahrrad
                           public void keyPressed(KeyEvent e) {
                               if (e.getKeyCode()==KeyEvent.VK_DOWN) {
                                   // Aktion, wenn "Pfeil nach unten" gedr�ckt
                                   rpanel.yBike=rpanel.yBike+10; }
                               if (e.getKeyCode()==KeyEvent.VK_UP) {
                                   // Aktion, wenn "Pfeil nach oben" gedr�ckt
                                  rpanel.yBike=rpanel.yBike-10; }
                           } } );

          startbutton.addActionListener(new ActionListener() //aussehren vom start/stop button
           { public void actionPerformed(ActionEvent e){
                   if (step==0) { step=1; ((JButton)e.getSource()).setText("Stop"); }
                    else { step=0; ((JButton)e.getSource()).setText("Start"); } } } );
                              
          pack();
          setVisible(true);

          Thread t=new Thread(rpanel);
          t.start(); }
        
        public JTextField getTField(){
        	return this.score;        	
        }
        public int getyBike(){
        	return this.rpanel.yBike;
        }
        
    
}

class RacingPanel extends JPanel implements Runnable {
      Racing rc; // später aus übergabe das rpanel
      int offset=0;
      int streckenlaenge=8000;
      int yBike =300; // die vertikale Position des Fahrradfahrers
      int yMcycle = 300; // die vertikale position des Motorrades
      boolean start=false;
      int streckeCount=0;
      int cycleVisible;
      
      
 
      
      
  
      
      RacingPanel(Racing racing){
          Bilder.init(this);
    	  rc=racing;
          setPreferredSize(new Dimension(rc.width,rc.height)); }

  
      public void paint(Graphics g) {
         g.setColor(Color.blue);
         Dimension d=getSize();
         g.fillRect(0,0,d.width,d.height);
         for(int i=0; i<d.width; i++) {
           int y1,y2;
           //g.setColor(Color.green);
           double x= (Math.PI*2.0*(offset+i))/streckenlaenge;
           g.setColor(new Color((int)(140+115*Math.sin(2*x)),
        		   				(int)(140+115*Math.sin(x)),
        		   				(int)(140+115*Math.sin(3*x))));
           y1=(int) (300+200*Math.sin(2*x)*Math.cos(3*x));          
           y2=y1+200;
           g.drawLine(i,y1,i,y2);
           
         }
                  	        
         // das Fahrrad kann nun gezeichnet werden
         Bilder.zeichneBild(this,g,0,10,yBike); 
         
         double xCheck=(Math.PI*2.0*(offset+10+Bilder.bildBreite[0]))/streckenlaenge; 
         int obergrenze= (int)(300+200*Math.sin(2*xCheck)*Math.cos(3*xCheck))-Bilder.bildHoehe[0];
         int untergrenze=obergrenze+200;
         
         double streckeX=(Math.PI*2.0*(offset+streckeCount))/streckenlaenge;
         int streckeY=(int)(300+200*Math.sin(2*streckeX)*Math.cos(3*streckeX));
         
         
         
         if(rc.getyBike()>=obergrenze&&rc.getyBike()<=untergrenze){
        	 rc.points=rc.points+rc.step;
         }
         
         
         
         
         if (10+Bilder.bildBreite[0]>=streckeCount) {
			if (rc.getyBike() + ((Bilder.bildHoehe[0]) / 2) <= streckeY
					+ Bilder.bildHoehe[1]
					&& rc.getyBike() + ((Bilder.bildHoehe[0]) / 2) >= streckeY) {
						rc.points=0;
						rc.getTField().setText("PUNKTE: "+rc.points);
			} 
		}
        

        
         rc.getTField().setText("PUNKTE: "+rc.points);
         
         cycleVisible=0-2*(Bilder.bildBreite[1]);
         
         
      
         if(!start || streckeCount<=cycleVisible){
        	 streckeCount=d.width-Bilder.bildBreite[1]-10;
        	 start=true;
         }
         else{
        	 streckeCount=streckeCount-2*rc.step;
         }
       
         Bilder.zeichneBild(this,g,1,streckeCount,streckeY);
            
         // um Probleme mit dem KeyEvents der Buttons zu vermeiden
         rc.requestFocusInWindow(); }
      	
      
   

      public void run() {
         int speed=10;
         long now,last=System.currentTimeMillis();
         while(true) {
            now=System.currentTimeMillis();
            long delay=(long)speed-(now-last);
            last=now;
            try { Thread.sleep(delay); } catch (Exception e)  { }
            repaint();

            // Die horizontale Position des Bildausschnitts wird um eins "weitergeschoben":
            offset=offset+rc.step;
            if (offset>=streckenlaenge) offset=0; }
      }    
}


class Bilder
{
    static int bilderzahl=3;
    static Image bild[]=new Image[bilderzahl];
    static int bildHoehe[]= new int[bilderzahl];
    static int bildBreite[]= new int[bilderzahl];
    static String bildName[] = new String[]{"bike1.gif", "motorbike2.gif", "motorbike1.gif"};
    static ClassLoader cl = Bilder.class.getClassLoader();
	
    public static void init(JPanel panel)
     { for(int id=0; id<bilderzahl; id++) {    	
        Image img=panel.getToolkit().getImage(cl.getResource(bildName[id]));//Bild mit id wird geladen
        panel.prepareImage(img, panel); //image wird vorgeladen
        while ((panel.checkImage(img, panel) & ImageObserver.WIDTH) != ImageObserver.WIDTH) { 
              try { Thread.sleep(50); } catch(Exception e) { } }
        bild[id]=img; //arrays werden mit werten des bildes gefüllt
        bildHoehe[id]=bild[id].getHeight(panel);
        bildBreite[id]=bild[id].getWidth(panel); }
     }
    
    /* x, y sind die Koordinaten der linken oberen Ecke! */
    public static void zeichneBild(JPanel panel,  Graphics g, int id, int x, int y) {
       if (bild[id]==null) init(panel);
       g.drawImage(bild[id],x,y, panel); }
}





