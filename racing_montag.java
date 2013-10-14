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

public class Racing extends JFrame {

	int width = 600; // Breite des Fensters
	int height = 800; // H�he des Fensters

	RacingPanel rpanel;

	int step = 0; // Geschwindigkeit der Animation

	public static void main(String[] args) {
		Racing wnd = new Racing();
		Control ctrl = new Control(wnd.rpanel);
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

		addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_DOWN) {
					// Aktion, wenn "Pfeil nach unten" gedr�ckt
					rpanel.y = rpanel.y + 4;
				}
				if (e.getKeyCode() == KeyEvent.VK_UP) {
					// Aktion, wenn "Pfeil nach oben" gedr�ckt
					rpanel.y = rpanel.y - 4;
				}
			}
		});

		startbutton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (step == 0) {
					step = 1;
					((JButton) e.getSource()).setText("Stop");
				} else {
					step = 0;
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
	int height = 800; // H�he des Fensters

	ControllPanel cpanel;
	RacingPanel rpanel;

	int step = 0; // Geschwindigkeit der Animation

	public Control(RacingPanel racing) {

		this.rpanel = racing;
		cpanel = new ControllPanel(this, rpanel);

		setSize(width, height);
		setTitle("ControllWindow");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		Container cpane = getContentPane();
		cpane.setLayout(new BorderLayout());
		cpane.add(cpanel, BorderLayout.CENTER);

		pack();
		setVisible(true);

		Thread t = new Thread(cpanel);
		t.start();
	}
}

class ControllPanel extends JPanel implements Runnable {
	Control cc;
	RacingPanel rc;
	int offset = 0;
	int y = 300;
	int ausweich=0;// die vertikale Position des Fahrradfahrers

	ControllPanel(Control ctrl, RacingPanel rpanel) {
		Bilder.init(this);
		this.rc = rpanel;
		this.cc = ctrl;

		setPreferredSize(new Dimension(cc.width, cc.height));
	}

	public void paint(Graphics g) {

		g.setColor(Color.BLACK);
		Dimension d =getSize();
		g.fillRect(0, 0, d.width, d.height);
		g.setColor(Color.RED);
		g.drawLine(d.width/2, 0, d.width/2, d.height);
		
		ausweich=(rc.y+Bilder.bildHoehe[0]/2)-(rc.yMotorrad+Bilder.bildHoehe[0]/2);
		
		Bilder.zeichneBild(cc.cpanel, g,2, d.width/2-Bilder.bildBreite[2]/2+ausweich, d.height/2-Bilder.bildHoehe[2]/2);
		
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

		}
	}
}

class RacingPanel extends JPanel implements Runnable {
	Racing rc;
	int offset = 0;
	int streckenlaenge = 8000;
	int y = 300; // die vertikale Position des Fahrradfahrers
	int xMotorrad=0;
	int yMotorrad=0;
	double drive=0;
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
			y2 = y1+200;
			g.drawLine(i, y1, i, y2);
		}

		// das Fahrrad kann nun gezeichnet werden
		Bilder.zeichneBild(this, g, 0, 10, y);
		
		
		int motorradSichtbar = 0-Bilder.bildBreite[1];
		
		if(!start||xMotorrad<=motorradSichtbar){
			start=true;
			xMotorrad=d.width - Bilder.bildBreite[1] - 10;
			yMotorrad=400;
		}
		else{
			xMotorrad-=rc.step*2;
		}
		
	//	drive=(Math.PI * 2.0 * (offset + xMotorrad)) / streckenlaenge;
	//	yMotorrad=(int) (300 + 200 * Math.sin(2 * drive) * Math.cos(3 * drive));
		
		
		int mitteMotorrad=yMotorrad+Bilder.bildHoehe[1]/2;
		int mitteFahrrad=y+Bilder.bildHoehe[0]/2;
		
		if(mitteMotorrad>=y-Bilder.bildHoehe[0]/2 && mitteMotorrad<=y+Bilder.bildHoehe[0]+Bilder.bildHoehe[0]/2){
			if(mitteFahrrad<=d.height/2){
				yMotorrad+=rc.step+1;
			}
			else{
				yMotorrad-=rc.step+1;
			}
		}
		
		
		Bilder.zeichneBild(this, g, 1, xMotorrad, yMotorrad);

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
