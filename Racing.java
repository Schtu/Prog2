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
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;


public class Racing extends JFrame implements Serializable{

        int width=600;  // Breite des Fensters
        int height=800; // H�he des Fensters
        
        RacingPanel rpanel;
        JTextField score = new JTextField();
        
        int step=0; // Geschwindigkeit der Animation
        int points=0;
        File file = new File("/home/schtu/workspace/a/test.txt");
        
        
        public static void main(String[] args) { Racing wnd = new Racing(); }

        public Racing() {
          setSize(width,height); 
          setTitle("Das ultimativ langweilige Rennspiel");
          setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

          rpanel=new RacingPanel(this);

          Container cpane=getContentPane();
          cpane.setLayout(new BorderLayout());
          cpane.add(rpanel,BorderLayout.CENTER);

          JButton startbutton = new JButton("Start");
          cpane.add(startbutton,BorderLayout.WEST);
          startbutton.resetKeyboardActions();
          
          cpane.add(score,BorderLayout.NORTH);
          score.setText("PUNKTE: "+points);

          addKeyListener(new KeyAdapter() {
                           public void keyPressed(KeyEvent e) {
                               if (e.getKeyCode()==KeyEvent.VK_DOWN) {
                                   // Aktion, wenn "Pfeil nach unten" gedr�ckt
                                   rpanel.yFahrrad=rpanel.yFahrrad+10; }
                               if (e.getKeyCode()==KeyEvent.VK_UP) {
                                   // Aktion, wenn "Pfeil nach oben" gedr�ckt
                                  rpanel.yFahrrad=rpanel.yFahrrad-10; }
                           } } );

          startbutton.addActionListener(new ActionListener()
           { public void actionPerformed(ActionEvent e){
                   if (step==0) { step=1; ((JButton)e.getSource()).setText("Stop"); }
                    else { step=0; ((JButton)e.getSource()).setText("Start"); 
                    try{
                    	FileOutputStream fs = new FileOutputStream(file);
                    	ObjectOutputStream os= new ObjectOutputStream(fs);
                    	os.writeInt(points);
                    	os.flush();
                    	os.close();
                    }catch(IOException e1){
                    	System.err.println(e1.toString());
                    }
                    } } } );
                              
          pack();
          setVisible(true);

          Thread t=new Thread(rpanel);
          t.start(); }
}

class RacingPanel extends JPanel implements Runnable {
      Racing rc;
      int offset=0;
      int streckenlaenge=8000;
      int yFahrrad =300; // die vertikale Position des Fahrradfahrers7
      int motorradVisible=0;
      int motorradDrive=0;
      
      boolean start=false;

      RacingPanel(Racing racing){
          Bilder.init(this);
    	  rc=racing;
          setPreferredSize(new Dimension(rc.width,rc.height)); }

  
      public void paint(Graphics g) {
         g.setColor(Color.blue);
         Dimension d=getSize();
         g.fillRect(0,0,d.width,d.height);
         for(int i=0; i<d.width; i++) {
           int y1=0,y2=0;
           //g.setColor(Color.green);
           double x= (Math.PI*2.0*(offset+i))/streckenlaenge;
           g.setColor(new Color((int)(140+115*Math.sin(2*x)),
        		   				(int)(140+115*Math.sin(x)),
        		   				(int)(140+115*Math.sin(3*x))));
           y1=(int) (300+200*Math.sin(2*x)*Math.cos(3*x));
           y2=(int) y1+200;
           g.drawLine(i,y1,i,y2); }
         
         double check=(Math.PI*2.0*(10+offset+Bilder.bildBreite[0]))/streckenlaenge;
         int og= (int)(300+200*Math.sin(2*check)*Math.cos(3*check))-Bilder.bildHoehe[0];
         int ug= og+200;
         
         double motorradPosX=(Math.PI*2.0*(offset+motorradDrive))/streckenlaenge;
         int motorradPosY= (int)(300+200*Math.sin(2*motorradPosX)*Math.cos(3*motorradPosX));
         
         if(yFahrrad>=og && yFahrrad<=ug){
        	 rc.points=rc.points+rc.step;
        	 rc.score.setText("PUNKTE :"+rc.points);
         }
         
         if(10+Bilder.bildBreite[0]>=motorradDrive){
        	 if(yFahrrad+(Bilder.bildHoehe[0]/2)<=motorradPosY+Bilder.bildHoehe[1] &&
        			 yFahrrad+(Bilder.bildHoehe[0]/2)>=motorradPosY){
        		 rc.points=0;
        		 rc.score.setText("PUNKTE :"+rc.points);
        	 }
         }
         

         // das Fahrrad kann nun gezeichnet werden
         Bilder.zeichneBild(this,g,0,10,yFahrrad);
         
         motorradVisible=0-Bilder.bildBreite[1];
         
         if(!start||motorradDrive<=motorradVisible){
        	 start=true;
        	 motorradDrive=d.width-Bilder.bildBreite[1]-10;
         }
         else{
        	 motorradDrive=motorradDrive-(4*rc.step);
         }

         
         Bilder.zeichneBild(this,g,1,motorradDrive,motorradPosY);
            
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
        Image img=panel.getToolkit().getImage(cl.getResource(bildName[id]));
        panel.prepareImage(img, panel);
        while ((panel.checkImage(img, panel) & ImageObserver.WIDTH) != ImageObserver.WIDTH) {
              try { Thread.sleep(50); } catch(Exception e) { } }
        bild[id]=img;
        bildHoehe[id]=bild[id].getHeight(panel);
        bildBreite[id]=bild[id].getWidth(panel); }
     }
    
    /* x, y sind die Koordinaten der linken oberen Ecke! */
    public static void zeichneBild(JPanel panel,  Graphics g, int id, int x, int y) {
       if (bild[id]==null) init(panel);
       g.drawImage(bild[id],x,y, panel); }
}





