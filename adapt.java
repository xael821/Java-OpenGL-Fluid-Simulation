/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package h2o;

/**
 *
 * @author Alex
 */
import com.sun.opengl.util.Animator;
import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCanvas;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.glu.GLU;

public class adapt extends WindowAdapter {

    Animator animator;

    public adapt(Animator A) {
        animator = A;
    }

    @Override
    public void windowClosing(WindowEvent e) {
        // Run this on another thread than the AWT event queue to
        // make sure the call to Animator.stop() completes before
        // exiting
        new Thread(new Runnable() {

            public void run() {
                animator.stop();
                System.exit(0);
            }
        }).start();
    }
}
