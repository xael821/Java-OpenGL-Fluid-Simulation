/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package h2o;

/**
 *
 * @author Alex
 */
import javax.swing.*;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;

public class window extends JFrame implements KeyListener {

    public window(int w, int h, String s) {
        super(s);
        setSize(w, h);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
        addKeyListener(this);

    }

    public void keyPressed(KeyEvent e) {

        char c = e.getKeyChar();
        if (c == 'a' || c == 'A') {
            Main.theta -= 0.04;
        }
        if (c == 'D' || c == 'd') {
            Main.theta += 0.04;
        }
        if (c == 'T' || c == 't') {
            Main.haverain = !Main.haverain;

        }

        if (c == 'r' || c == 'R') {
            sizeselector s = new sizeselector();
        }


    }

    public void keyTyped(KeyEvent e) {
    }

    public void keyReleased(KeyEvent e) {
    }
}
