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

	int width = 600; // Breite des Fensters
	int height = 800; // H�he des Fensters

	RacingPanel rpanel;
	boolean läuft=false;

	 JTextField score = new JTextField();
	  int points=0;
	
	int step = 0; // Geschwindigkeit der Animation

	public static void main(String[] args) {
		Racing wnd = new Racing();
		Control wnd2 = new Control(wnd.rpanel);
	}

	public Racing() {
		setSize(width, height);
		setTitle("Das ultimativ langweilige Rennspiel");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		rpanel = new RacingPanel(this);

		Container cpane = getContentPane();
		cpane.setLayout(new BorderLayout());
		cpane.add(rpanel, BorderLayout.CENTER);

		JButton startbutton = new JButton("Start");
		cpane.add(startbutton, BorderLayout.WEST);
		startbutton.resetKeyboardActions();
		
		 cpane.add(score,BorderLayout.NORTH);
         score.setText("PUNKTE: "+points);

		addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_DOWN&&läuft) {
					// Aktion, wenn "Pfeil nach unten" gedr�ckt
					rpanel.yBike=rpanel.yBike+10;;
				}
				if (e.getKeyCode() == KeyEvent.VK_UP&&läuft) {
					// Aktion, wenn "Pfeil nach oben" gedr�ckt
					rpanel.yBike=rpanel.yBike-10;;
				}
			}
		});

		startbutton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (step == 0) {
					step = 1;
					läuft=true;
					((JButton) e.getSource()).setText("Stop");
				} else {
					step = 0;
					läuft=false;
					((JButton) e.getSource()).setText("Start");
				}
			}
		});

		pack();
		setVisible(true);

		Thread t = new Thread(rpanel);
		t.start();
	}
}

class Control extends JFrame {
	int width = 600; // Breite des Fensters
	int height = 800; // Höhe des Fensters

	ControlPanel cpanel;
	RacingPanel rpanel;

	Control(RacingPanel rpanel) {
		
		this.rpanel=rpanel;
		
		setSize(width, height);
		setTitle("FUCK YA");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		cpanel = new ControlPanel(this,rpanel);
		
		Container cpane = getContentPane();
		cpane.setLayout(new BorderLayout());
		cpane.add(cpanel, BorderLayout.CENTER);
	

		setVisible(true);
	}
}

class ControlPanel extends JPanel implements Runnable {
	Control cc;
	RacingPanel rc;
	int ausweich=0;
	
	
	ControlPanel(Control control,RacingPanel rpanel){
		Bilder.init(this);
		this.cc=control;
		this.rc=rpanel;
		setPreferredSize(new Dimension(cc.width, cc.height));
	}
	
	
	
	public void paint(Graphics g) {
		g.setColor(Color.BLACK);
		Dimension d = getSize();
		g.fillRect(0, 0, d.width, d.height);
		g.setColor(Color.RED);
		g.drawLine(d.width/2, 0, d.width/2, d.height);
		
		ausweich=(rc.yBike+Bilder.bildHoehe[0]/2)-rc.motorradPosY+Bilder.bildHoehe[1]/2;
		
		
		
		
		
		
		Bilder.zeichneBild(cc.cpanel, g, 2, (d.width/2-Bilder.bildBreite[2]/2)+ausweich,d.height/2-Bilder.bildHoehe[2]/2 );
		
	}
	
	public void run() {
		int speed = 10;
		long now, last = System.currentTimeMillis();
		while (true) {
			now = System.currentTimeMillis();
			long delay = (long) speed - (now - last);
			last = now;
			try {
				Thread.sleep(delay);
			} catch (Exception e) {
			}
			repaint();
		}
	}
}

class RacingPanel extends JPanel implements Runnable {
	 Racing rc;
     int offset=0;
     int streckenlaenge=8000;
     int yBike=300; // die vertikale Position des Fahrradfahrers
     int motorradPosX=0;
     int motorradPosY=400;
     double motorraddrive=0;
     boolean start=false;

	RacingPanel(Racing racing) {
		Bilder.init(this);
		rc = racing;
		setPreferredSize(new Dimension(rc.width, rc.height));
	}

	public void paint(Graphics g) {
		g.setColor(Color.blue);
		Dimension d = getSize();
		g.fillRect(0, 0, d.width, d.height);
		for (int i = 0; i < d.width; i++) {
			int y1 = 0, y2 = 0;
			// g.setColor(Color.green);
			double x = (Math.PI * 2.0 * (offset + i)) / streckenlaenge;
			g.setColor(new Color((int) (140 + 115 * Math.sin(2 * x)),
					(int) (140 + 115 * Math.sin(x)), (int) (140 + 115 * Math
							.sin(3 * x))));
			y1 = (int) (300 + 200 * Math.sin(2 * x) * Math.cos(3 * x));
			y2 = y1 + 200;
			g.drawLine(i, y1, i, y2);
		}

		   double check=(Math.PI*2.0*(offset+Bilder.bildBreite[0]+10))/streckenlaenge;
	         int obergrenze=(int)(300+200*Math.sin(2*check)*Math.cos(3*check))-Bilder.bildHoehe[0]+10;
	         int untergrenze=obergrenze+200;
	         
	         if(yBike>=obergrenze && yBike<=untergrenze){
	        	 rc.points=rc.points+rc.step;
	        	 rc.score.setText("PUNKTE: "+rc.points);
	         }
	         
	       
	         g.fillOval(Bilder.bildBreite[0]+10, yBike+(Bilder.bildHoehe[0]/2), 10, 10);
	         
	         Bilder.zeichneBild(this,g,0,10,yBike);
	         
	         //AB HIER ANDERS ALS VORHER!!!!!
	         
	         int motorradsichtbar=0-((Bilder.bildBreite[1])/2);
	         
	         if(!start||motorradPosX<=motorradsichtbar){
	        	 start=true;
	        	 motorradPosX=d.width-Bilder.bildBreite[1]-10;
	        	 motorradPosY=400;    // damit wieder am in der Mitte gestartet wird 
	        	 
	         }
	         else{
	        	 motorradPosX=motorradPosX-2*rc.step;
	         }
	         
	         
	        motorraddrive=(Math.PI*2.0*(offset+motorradPosX))/streckenlaenge;
	        
	        //Damit die strecke nicht mehr abgehfahren wird ,auskommentiert
	        //motorradPosY=(int) (300+200*Math.sin(2*motorraddrive)*Math.cos(3*motorraddrive));
	         
	        
	        //--------------------------Hier is der Check
	         if(yBike<d.height/2){
	        	 if(motorradPosY+Bilder.bildHoehe[1]/2<=d.height&&rc.step==1
	        			 &&motorradPosX+Bilder.bildHoehe[1]/2<10+Bilder.bildBreite[0]*3){
	        		 motorradPosY+=2;
	        		 System.out.println(motorradPosY);
	        		 System.out.println(d.height);
	        	 }
	         }
	         else{
	        	 if(motorradPosY+Bilder.bildHoehe[1]/2<=d.height&&rc.step==1
	        			 &&motorradPosX+Bilder.bildHoehe[1]/2<10+Bilder.bildBreite[0]*3){
	        		 motorradPosY-=2;
	        	 }
	         }
	         
	         
	         
	         if(Bilder.bildBreite[0]+10>=motorradPosX){
	        	 if(yBike+((Bilder.bildHoehe[0])/2)<=motorradPosY+Bilder.bildHoehe[1] &&
	        			 yBike+((Bilder.bildHoehe[0])/2)>=motorradPosY)
	        	 {
	        		 rc.points=0;
	        		 rc.score.setText("PUNKTE: "+rc.points);
	        	 }
	         }
	      
	         Bilder.zeichneBild(this,g,1,motorradPosX,motorradPosY);

		// um Probleme mit dem KeyEvents der Buttons zu vermeiden
		rc.requestFocusInWindow();

	}

	public void run() {
		int speed = 10;
		long now, last = System.currentTimeMillis();
		while (true) {
			now = System.currentTimeMillis();
			long delay = (long) speed - (now - last);
			last = now;
			try {
				Thread.sleep(delay);
			} catch (Exception e) {
			}
			
		
			repaint();

			// Die horizontale Position des Bildausschnitts wird um eins
			// "weitergeschoben":
			offset = offset + rc.step;
			if (offset >= streckenlaenge)
				offset = 0;
		}
	}
}

class Bilder {
	static int bilderzahl = 3;
	static Image bild[] = new Image[bilderzahl];
	static int bildHoehe[] = new int[bilderzahl];
	static int bildBreite[] = new int[bilderzahl];
	static String bildName[] = new String[] { "bike1.gif", "motorbike2.gif",
			"motorbike1.gif" };
	static ClassLoader cl = Bilder.class.getClassLoader();

	public static void init(JPanel panel) {
		for (int id = 0; id < bilderzahl; id++) {
			Image img = panel.getToolkit().getImage(
					cl.getResource(bildName[id]));
			panel.prepareImage(img, panel);
			while ((panel.checkImage(img, panel) & ImageObserver.WIDTH) != ImageObserver.WIDTH) {
				try {
					Thread.sleep(50);
				} catch (Exception e) {
				}
			}
			bild[id] = img;
			bildHoehe[id] = bild[id].getHeight(panel);
			bildBreite[id] = bild[id].getWidth(panel);
		}
	}

	/* x, y sind die Koordinaten der linken oberen Ecke! */
	public static void zeichneBild(JPanel panel, Graphics g, int id, int x,
			int y) {
		if (bild[id] == null)
			init(panel);
		g.drawImage(bild[id], x, y, panel);
	}
}
