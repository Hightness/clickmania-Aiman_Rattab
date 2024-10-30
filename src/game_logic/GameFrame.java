package game_logic;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class GameFrame extends JFrame{
	public GamePanel gp;
	public interface Callback{void callback(int ir);}
	private JPanel menu;
	private ImageIcon playImage, quitImage, restartImage, playHoverImage, quitHoverImage, restartHoverImage;

	GameFrame(){
		gp = new GamePanel(this::callback);
		gp.addKeyListener(new MyKeyAdapter());
		gp.addMouseListener(new MyMouseListener());
		this.setTitle("ClickMania");
		this.setSize(900, 900);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setResizable(true);
		this.setVisible(true);
		this.setLocationRelativeTo(null);
		add(gp);
		gp.requestFocus();
		//gp.timer.stop();
	}

	public void menu(){
		System.out.println("adding menu");
		//setTitle("Game Menu");
		//setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// Load images
		playImage = new ImageIcon("texture_packs/new_game.png");
		playHoverImage = new ImageIcon("texture_packs/new_game_h.png");
		quitImage = new ImageIcon("texture_packs/quit.png");
		quitHoverImage = new ImageIcon("texture_packs/quit_h.png");
		restartImage = new ImageIcon("texture_packs/restart.png");
		restartHoverImage = new ImageIcon("texture_packs/restart_h.png");

		// Create buttons with images
		menu = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 10));
		MButton playButton = new MButton(playImage,playHoverImage, 0, this::callback);
		MButton quitButton = new MButton(quitImage,quitHoverImage ,2, this::callback);
		MButton restartButton = new MButton(restartImage,restartHoverImage,1, this::callback);

		playButton.setBounds(getWidth()/4 , getHeight()/20, getWidth()/2, getHeight()/4);
		restartButton.setBounds(getWidth()/4, getHeight()*7/20, getWidth()/2, getHeight()/4);
		quitButton.setBounds(getWidth()/4, getHeight()*13/20, getWidth()/2, getHeight()/4);
		menu.setBackground(new Color(4,12,48));
		menu.add(playButton);
		menu.add(quitButton);
		menu.add(restartButton);

		menu.addComponentListener(new ComponentAdapter() {
								 @Override
								 public void componentResized(ComponentEvent e) {
									 playButton.setBounds(getWidth() / 4, getHeight() / 20, getWidth() / 2, getHeight() / 4);
									 restartButton.setBounds(getWidth() / 4, getHeight() * 7 / 20, getWidth() / 2, getHeight() / 4);
									 quitButton.setBounds(getWidth() / 4, getHeight() * 13 / 20, getWidth() / 2, getHeight() / 4);
									 repaint();
								 }
							 });
		add(menu);
		setVisible(true);
		this.setFocusable(true);

	}

	public void callback(int button_type) {
		if(button_type == 2) dispose();
		if(menu != null)remove(menu);
		if(button_type == 0){
			gp.getData();
			add(gp);
		}else if(button_type == 1){
			gp.timer.start();
			gp.player.P_down = gp.player.P_left = gp.player.P_right = gp.player.P_up = 0;
			add(gp);
		}
		gp.requestFocusInWindow();
	}

	public static void main(String[] args) {
		new GameFrame();
	}

	public class MyKeyAdapter extends KeyAdapter{
		@Override
		public void keyPressed(KeyEvent e) {
			if(e.getKeyCode() == KeyEvent.VK_M){
				if(gp.timer.isRunning()){
					gp.timer.stop();
					remove(gp);
					menu();
				}
			}
			if (e.getKeyCode() >= 49 && e.getKeyCode() <= 57){
				gp.player.cur_weapon = e.getKeyCode() - 49;
				if(gp.player.cur_weapon < gp.player.weap_size)for (int i = 0; i < gp.player.weapons[gp.player.cur_weapon].bullets.size(); i++)gp.map.delete(gp.player.weapons[gp.player.cur_weapon].bullets.get(i));
			}

			if (e.getKeyCode() == KeyEvent.VK_W) {
				gp.player.P_up = (int) gp.player.MAXACC;
			}

			if (e.getKeyCode() == KeyEvent.VK_A)
				gp.player.P_left = (int)gp.player.MAXACC;

			if (e.getKeyCode() == KeyEvent.VK_S)
				gp.player.P_down = (int)gp.player.MAXACC;

			if (e.getKeyCode() == KeyEvent.VK_D)
				gp.player.P_right = (int)gp.player.MAXACC;

		}

		@Override
		public void keyReleased(KeyEvent e){
			if (e.getKeyCode() == KeyEvent.VK_W)
				gp.player.P_up = 0;

			if (e.getKeyCode() == KeyEvent.VK_A)
				gp.player.P_left = 0;

			if (e.getKeyCode() == KeyEvent.VK_S)
				gp.player.P_down = 0;

			if (e.getKeyCode() == KeyEvent.VK_D)
				gp.player.P_right = 0;
		}
	}

	public class MyMouseListener implements MouseListener{
		@Override
		public void mouseClicked(MouseEvent e) {
		}

		@Override
		public void mousePressed(MouseEvent mouseEvent) {

		}

		@Override
		public void mouseReleased(MouseEvent e) {
			Vec2d click_pos = new Vec2d(e.getX(), e.getY());
			click_pos.add(gp.camera.pos);
			if(gp.player.cur_weapon < gp.player.weap_size)gp.player.weapons[gp.player.cur_weapon].fire(click_pos.clone(), 0);
		}

		@Override
		public void mouseEntered(MouseEvent mouseEvent) {

		}

		@Override
		public void mouseExited(MouseEvent mouseEvent) {

		}
	}
}