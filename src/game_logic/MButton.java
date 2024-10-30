package game_logic;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

public class MButton extends JLabel{
    public ImageIcon icon, hover_icon;
    GameFrame.Callback callback;
    int type;

    public MButton(ImageIcon icon,ImageIcon hover_icon, int type, GameFrame.Callback callback) {
        super();
        this.callback = callback;
        this.icon = icon;
        this.hover_icon = hover_icon;
        this.type = type;
        addMouseListener(new MyMouseAdapter());
        setVisible(true);
    }


    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(icon.getImage(), 0, 0, getWidth(), getHeight(), null);
    }

    public class MyMouseAdapter extends MouseAdapter {
        @Override
        public void mouseReleased(MouseEvent e) {
            if(type == 0)callback.callback(0);
            if(type == 1)callback.callback(1);
            if(type == 2)callback.callback(2);
        }

        @Override
        public void mouseEntered(MouseEvent e) {
            Image temp = icon.getImage();
            icon.setImage(hover_icon.getImage());
            hover_icon.setImage(temp);
            repaint();
        }

        @Override
        public void mouseExited(MouseEvent e) {
            Image temp = icon.getImage();
            icon.setImage(hover_icon.getImage());
            hover_icon.setImage(temp);
            repaint();
        }
    }
}