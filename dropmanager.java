/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package h2o;

/**
 *
 * @author Alex
 */
import java.util.ArrayList;

public class dropmanager {

    float gravity;
    H2O water;
    ArrayList<drop> drops = new ArrayList<drop>();
    double threshold = 0.001;

    public dropmanager(H2O water) {
        this.water = water;

        gravity = (float) water.grav;
    }

    public void go() {
        // sporadically limits the number of drops that fall
        double threshold;
        if (Math.sqrt(water.cols.length * water.cols[0].length) < 140) {
            threshold = 0.9;
        } else {
            threshold = 0.9;
        }
        if (Math.random() > threshold) {
            float x = (float) Math.random() * water.cols.length;
            float y = (float) Math.random() * water.cols[0].length;
            drops.add(new drop(x, y,
                    (float) water.cols[(int) x][(int) y] + (float) Math.random() * 100f + 200f, 512));
        }
        // drops.add(new drop(100,100,100,200));
        moveall();
        collisions();

        //  System.out.println(drops.size());
    }

    private void moveall() {
        for (drop d : drops) {
            d.move();

            d.veloz -= gravity / 100;
        }

    }

    private void collisions() {
        if (drops.size() < 1) {
            return;
        }
        try {
            for (int a = 0; a < drops.size(); a++) {

                drop d = drops.get(a);

                if (d.x > water.cols.length - 11) {
                    d.x -= 11;
                }
                if (d.y > water.cols[0].length - 11) {
                    d.y -= 11;
                }
                if (d.z <= water.cols[(int) d.x][(int) d.y]) {// collision with surface
                    double volume = 1000;

                    int i = (int) d.x;
                    int e = (int) d.y;

                    for (int j = 0; j < (int) Math.cbrt(volume); j++) {
                        for (int k = 0; k < (int) Math.cbrt(volume); k++) {
                            water.cols[i + j][e + k] += Math.cbrt(volume);
                        }
                    }

                    // changing velocity of water column at this point, on lowest tier
                    //f=ma
                    // water.velo[0][i][e]-= d.volume*gravity;
                    drops.remove(d);
                    a++;
                }

            }
        } catch (Exception xc) {
            //  System.out.println("FAIL" + xc);
        }
    }
}
