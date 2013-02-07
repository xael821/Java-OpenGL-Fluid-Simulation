package h2o;

import com.sun.opengl.util.Animator;
import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.*;
import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCanvas;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.glu.GLU;

public class Main implements GLEventListener {

    static int W = 140;
    static int H = 140;
    static H2O hydro = new H2O(W, H, 1);
    static Thread sleeper = new Thread();
    static float camx = W / 2 + 80;
    static float camy = H / 2 + 100;
    static float camz = 130;
    static boolean haverain = true;
    static boolean mode = true;
    static dropmanager man;
    static float[] lightPos = {0, 0, 120, 1};
    static float theta = 0;
    static GLCanvas canvas = new GLCanvas();
    static window frame;

    public static void main(String[] args) {
        // various setup steps


        man = new dropmanager(hydro);
        frame = new window(800, 700, "HYDRO");
        man.gravity = (float) hydro.grav;

        canvas.addGLEventListener(new Main());
        frame.add(canvas);

        Animator animator = new Animator(canvas);
        frame.addWindowListener(new adapt(animator));


        frame.setVisible(true);
        animator.start();

        while (frame.isVisible()) {

            hydro.go();
            try {
                man.go();
            } catch (Exception managerError) {
            }

            try {
                sleeper.sleep(5);
            } catch (Exception e) {
            }
        }
    }

    public void init(GLAutoDrawable drawable) {
        // Use debug pipeline
        // drawable.setGL(new DebugGL(drawable.getGL()));

        GL gl = drawable.getGL();
        //  System.err.println("INIT GL IS: " + gl.getClass().getName());

        // Enable VSync
        gl.setSwapInterval(1);

        // Setup the drawing area and shading mode
        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        gl.glShadeModel(GL.GL_SMOOTH); // try setting this to GL_FLAT and see what happens.
    }

    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        GL gl = drawable.getGL();
        GLU glu = new GLU();

        if (height <= 0) { // avoid a divide by zero error

            height = 1;
        }
        final float h = (float) width / (float) height;
        gl.glViewport(0, 0, width, height);

        gl.glMatrixMode(GL.GL_PROJECTION);
        gl.glLoadIdentity();
        glu.gluPerspective(35.0f, h, 1.0, 1500.0);
        glu.gluLookAt(camx, camy, camz, 0, 0, -100, -camx, -camy, (-camx - camy) / camz);
        gl.glMatrixMode(GL.GL_MODELVIEW);
        gl.glLoadIdentity();
    }

    public void display(GLAutoDrawable drawable) {
        // note on method structure, the center of the mesh is at(0,0,0)

        GL gl = drawable.getGL();

        // Clear the drawing area
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
        // Reset the current matrix to the "identity"
        gl.glLoadIdentity();

        //enabling Z-buffer, this is crucial to draw order based on depth
        gl.glEnable(GL.GL_DEPTH_TEST);
        gl.glEnable(GL.GL_BLEND);

        //lighting

        gl.glEnable(GL.GL_LIGHT1);
        gl.glEnable(GL.GL_LIGHTING);

        //gl.glRotated(Math.atan(camy/camx)*180/Math.PI, 0,0,1);
        float[] lightColorAmbient = {0.2f, 0.2f, 0.2f, 1f};
        gl.glLightfv(GL.GL_LIGHT1, GL.GL_AMBIENT, lightColorAmbient, 0);


        float[] lightColorSpecular = {1f, 1f, 1f, 1f};

        gl.glLightfv(GL.GL_LIGHT1, GL.GL_POSITION, lightPos, 0);
        gl.glRotated(theta * 180 / Math.PI, 0, 0, 1);// theta in radians
        gl.glTranslated(-camx / 2, -camy / 2, -camz);

        gl.glLightfv(GL.GL_LIGHT1, GL.GL_SPECULAR, lightColorSpecular, 0);


        //***************************************************************8
        // DRAWING
        // surface:
        for (int i = 2; i < hydro.cols.length - 2; i++) {
            for (int e = 2; e < hydro.cols[0].length - 2; e++) {

                gl.glBegin(GL.GL_QUADS);
                byte[] clr = rgb(i + 1, e, hydro.cols[i + 1][e],
                        i, e, hydro.cols[i][e],
                        i, e + 1, hydro.cols[i][e + 1]);

                byte[] clr2 = rgb(i, e, hydro.cols[i][e],
                        i + 1, e, hydro.cols[i + 1][e],
                        i + 1, e + 1, hydro.cols[i + 1][e + 1]);
                byte[] clr3 = rgb(
                        i + 1, e, hydro.cols[i + 1][e],
                        i + 1, e + 1, hydro.cols[i + 1][e + 1],
                        i, e + 1, hydro.cols[i][e + 1]);
                byte[] clr4 = rgb(i + 1, e + 1, hydro.cols[i + 1][e + 1],
                        i + 1, e, hydro.cols[i + 1][e],
                        i, e, hydro.cols[i][e]);
                byte R = (byte) ((clr[0] + clr2[0] + clr3[0] + clr4[0]) / 4);
                byte G = (byte) ((clr[1] + clr2[1] + clr3[1] + clr4[1]) / 4);
                byte B = (byte) ((clr[2] + clr2[2] + clr3[2] + clr4[2]) / 4);
                float[] rgba = {(float) R / 155 + 0.2f, (float) G / 155 + 0.2f, (float) B / 155 + 0.2f};
                //  float[] rgba ={0.4f,0.3f,1f};
                gl.glMaterialfv(GL.GL_FRONT, GL.GL_SPECULAR, rgba, 0);
                gl.glMaterialf(GL.GL_FRONT, GL.GL_SHININESS, 100f);

                float h1 = hydro.cols[i][e] / 1;
                float h2 = hydro.cols[i + 1][e] / 1;
                float h3 = hydro.cols[i + 1][e + 1] / 1;
                float h4 = hydro.cols[i][e + 1] / 1;
                gl.glVertex3f((float) i, (float) e, h1);
                gl.glVertex3f((float) (i + 1f), (float) e, h2);
                gl.glVertex3f((float) (i + 1f), (float) (e + 1f), h3);
                gl.glVertex3f((float) i, (float) (e + 1f), h4);
                gl.glEnd();

            }

        }

        // water along walls of tank (each loop is a different wall);
        //wall1(opposite wall4)
        float[] rgba = {0.4f, 0.2f, 1f};
        gl.glMaterialfv(GL.GL_FRONT, GL.GL_SPECULAR, rgba, 0);
        gl.glMaterialf(GL.GL_FRONT, GL.GL_SHININESS, 100f);
        for (int i = 2; i < hydro.cols[0].length - 2; i++) {

            gl.glBegin(GL.GL_QUADS);

            gl.glVertex3f((float) 2, (float) i, 0f);
            gl.glVertex3f(2, (float) (i + 1f), 0f);
            gl.glVertex3f(2, (float) (i + 1f), (float) hydro.cols[2][i + 1]);
            gl.glVertex3f(2, (float) i, (float) hydro.cols[2][i]);
            gl.glEnd();

        }
        //wall2 (opposite from wall3)
        for (int i = 2; i < hydro.cols.length - 2; i++) {

            gl.glBegin(GL.GL_QUADS);

            gl.glVertex3f((float) i, (float) 2, 0f);
            gl.glVertex3f((float) (i + 1f), 2, 0f);
            gl.glVertex3f((float) (i + 1f), 2, (float) hydro.cols[i + 1][2]);
            gl.glVertex3f((float) i, 2, (float) hydro.cols[i][2]);
            gl.glEnd();

        }
        //wall3
        for (int i = 2; i < hydro.cols.length - 2; i++) {

            gl.glBegin(GL.GL_QUADS);

            gl.glVertex3f((float) i, (float) hydro.cols[0].length - 2, 0f);
            gl.glVertex3f((float) (i + 1f), hydro.cols[0].length - 2, 0f);
            gl.glVertex3f((float) (i + 1f), hydro.cols[0].length - 2, (float) hydro.cols[i + 1][hydro.cols[0].length - 2]);
            gl.glVertex3f((float) i, hydro.cols[0].length - 2, (float) hydro.cols[i][hydro.cols[0].length - 2]);
            gl.glEnd();

        }
        //wall4, opposite wall3
        for (int i = 2; i < hydro.cols[0].length - 2; i++) {

            gl.glBegin(GL.GL_QUADS);

            gl.glVertex3f((float) hydro.cols.length - 2, (float) i, 0f);
            gl.glVertex3f((float) hydro.cols.length - 2, (float) (i + 1f), 0f);
            gl.glVertex3f((float) hydro.cols.length - 2, (float) (i + 1f), (float) hydro.cols[hydro.cols.length - 2][i + 1]);
            gl.glVertex3f((float) hydro.cols.length - 2, (float) i, (float) hydro.cols[hydro.cols.length - 2][i]);
            gl.glEnd();


        }

        // drawing falling drops
        try {
            int num = man.drops.size();
            for (int i = 0; i < num; i++) {
                {
                    drop d = man.drops.get(i);


                    gl.glBegin(GL.GL_QUADS);

                    gl.glVertex3f(d.x, d.y, d.z + 3);
                    gl.glVertex3f(d.x + 3, d.y, d.z);
                    gl.glVertex3f(d.x, d.y, d.z - 3);
                    gl.glVertex3f(d.x - 3, d.y, d.z);

                    gl.glEnd();

                    gl.glBegin(GL.GL_QUADS);

                    gl.glVertex3f(d.x, d.y, d.z + 3);
                    gl.glVertex3f(d.x, d.y + 3, d.z);
                    gl.glVertex3f(d.x, d.y, d.z - 3);
                    gl.glVertex3f(d.x, d.y - 3, d.z);

                    gl.glEnd();
                    gl.glBegin(GL.GL_QUADS);

                    gl.glVertex3f(d.x + 3, d.y, d.z);
                    gl.glVertex3f(d.x, d.y + 3, d.z);
                    gl.glVertex3f(d.x - 3, d.y, d.z);
                    gl.glVertex3f(d.x, d.y - 3, d.z);

                    gl.glEnd();
                }//catch(Exception e){}
                num = man.drops.size();

            }
        } catch (Exception noDropManagerError) {
        }
        gl.glFlush();

    }

    public void displayChanged(GLAutoDrawable drawable, boolean modeChanged, boolean deviceChanged) {
    }

    public byte[] rgb(double x1, double y1, double z1, double x2, double y2,
            double z2, double x3, double y3, double z3) {
        // x2,y2,z2 is the mutual point between the vectors
        return rgbAUX(x1 - x2, y1 - y2, z1 - z2, x3 - x2, y3 - y2, z3 - z2);
    }

    private byte[] rgbAUX(double x, double y, double z, double q, double r, double s) {
        //returns color based on normal vector plane composed of [x,y,z] [q,r,s]
        // finding comps of V=[1,t,u]


        double t;
        double u;

        t = (-q + s * x / z) / (r - s * y / z);
        u = (-x - y * t) / z;
        double length = Math.sqrt(u * u + t * t + 1);
        t /= length;
        u /= length;
        // r,g,b= x,y,z of v

        //    int outR = (int) ((1 / length) * 127) + 127;

        //  int outG = (int) (t * 127) + 127;
        // int outB = (int) (u * 127) + 127;
        // x vs vert(z)
        int outR = (int) (Math.abs(Math.atan((1 / length) / u)) / (Math.PI) * 255);
        // y vs vert
        int outG = (int) (Math.abs(Math.atan(t / u)) / (Math.PI) * 255);
        // tortional
        int outB = 100;//(int)(Math.abs(Math.atan( (1/length)/t ))/(Math.PI) *255);
        byte[] out = new byte[]{(byte) outR, (byte) outG, (byte) outB};
        return out;
    }

    static public void reset() {
        if (haverain) {
            man = new dropmanager(hydro);
        } else {
            man = null;
        }
        camx = W / 2f;
        camy = H / 2f;
        canvas = new GLCanvas();
        frame.add(canvas);
        float[] f = new float[]{0, 0, 200, 1};
        //  System.out.println("HI"+mode);
        lightPos = f;
    }
}
