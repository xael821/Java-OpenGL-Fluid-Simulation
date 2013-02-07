/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package h2o;

/**
 *
 * @author Alex
 */
import java.awt.*;
import javax.swing.*;
import java.util.ArrayList;
import java.awt.event.*;

public class builder extends JFrame implements ActionListener,
        ItemListener {

    int W;
    int H;
    JButton done = new JButton("done");
    JCheckBox[][] chx;
    JCheckBox rainbox;

    public builder(int w, int h) {
        super("Simulation Setup");
        setSize(w + 0, h + 0);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setVisible(true);
        this.isAlwaysOnTop();
        W = w;
        H = h;
        GridLayout layout = new GridLayout(h / 50 + 2, w / 50);
        // dimensions of gridlayout is height,width
        chx = new JCheckBox[h / 50][w / 50];
        for (int i = 0; i < (h / 50); i++) {
            for (int e = 0; e < (w / 50); e++) {
                chx[i][e] = new JCheckBox((50 * i) + "," + (50 * e), false);
                chx[i][e].addItemListener(this);
                this.add(chx[i][e]);
            }

        }
        this.add(done);
        this.add(new JLabel("Check regions to start elevated"));
        rainbox = new JCheckBox("Enable rain", false);
        this.add(rainbox);
        rainbox.addItemListener(this);
        done.addActionListener(this);
        setLayout(layout);
        pack();
    }

    public void actionPerformed(ActionEvent e) {
        String source = e.getActionCommand();


        if (source.equals("done")) {
            Main.mode = false;
            Main.haverain = rainbox.isSelected();
            Main.hydro = new H2O(W, H, 1);


            Main.reset();
            ArrayList<int[]> data = new ArrayList<int[]>();
            for (int i = 0; i < (H / 50); i++) {
                for (int a = 0; a < (W / 50); a++) {
                    if (chx[i][a].isSelected()) {


                        int x = i * 50 + 25;
                        int y = a * 50 + 25;
                        data.add(new int[]{x - 25, y - 25, x + 25, y + 25});
                    }
                }

            }

            //actually modifying water:
            // laying flat base
            for (int i = 0; i < W; i++) {

                for (int a = 0; a < H; a++) {
                    Main.hydro.cols[i][a] = 30;

                }
            }
            // raising the selecting areas
            for (int[] arr : data) {
                for (int i = arr[0]; i < arr[2]; i++) {
                    for (int a = arr[1]; a < arr[3]; a++) {
                        try {
                            Main.hydro.cols[i][a] = 130;
                        } catch (Exception xx) {
                        }
                    }

                }

            }

        }
        Main.canvas.display();
        this.dispose();

    }

    public void itemStateChanged(ItemEvent e) {
        Object source = e.getItem();
    }
}
