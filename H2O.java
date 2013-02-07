package h2o;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class H2O {

    final float grav = 9.8f;
    Thread t;
    /*
     * previously had used a class to contain the height and velocity values but
     * that was too hard to implement on multiple tiers so I'm back to
     * primitives. The elements in cols and velos correspond the way the old
     * classes would have: cols[i] is a height with a velocity stored at
     * velos[i]
     */
    float[][] cols;
    float[][] velos;
    float[][] otherCols;
    float[][] otherVelos;
    float dt = 0.0005f;
    float vz = 0;
    int tiers = 3;
    int rectWidth;

    public H2O(int l, int w, int rectWidth) {

        tiers = 10;//(int) (Math.sqrt(l * w) / 20);
        this.rectWidth = rectWidth;
        cols = new float[l][w];
        velos = new float[l][w];
        for (int i = 0; i < cols.length; i++) {
            for (int e = 0; e < cols[0].length; e++) {
                //if ((e < cols.length / 4 || e > 2 * cols.length / 4)&& (i < cols.length / 4 || i > 2 * cols.length / 4))
                if ((int) Math.sqrt((i - 50) * (i - 50) + (e - 70) * (e - 70)) < 20) {
                    cols[i][e] = 130f;
                    velos[i][e] = 0f;
                } else {
                    cols[i][e] = 30f;
                    velos[i][e] = 0f;
                }
                vz += cols[i][e];
            }
        }
        otherCols = new float[cols[0].length][cols.length];
        otherVelos = new float[velos[0].length][velos.length];

        for (int i = 0; i < cols.length; i++) {
            for (int e = 0; e < cols[0].length; e++) {
                otherCols[e][i] = cols[i][e];
                otherVelos[e][i] = velos[i][e];
            }
        }
        //  tiers=2;
    }
    int counter = 0;

    public void go() {
        counter++;
        for (int i = 0; i < velos.length; i++) {
            flow(cols[i], velos[i]);
            ///  dampen(velos[i], 0.9995f);
            even(cols[i], 10);
        }
        //***********************
        for (int i = 0; i < cols.length; i++) {
            for (int e = 0; e < cols[0].length; e++) {
                otherCols[e][i] = cols[i][e];
                otherVelos[e][i] = velos[i][e];
            }
        }

        for (int i = 0; i < otherVelos.length; i++) {
            flow(otherCols[i], otherVelos[i]);
            dampen(otherVelos[i], 0.9995f);
            even(otherCols[i], 10);
        }
        for (int i = 0; i < cols.length; i++) {
            for (int e = 0; e < cols[0].length; e++) {
                cols[e][i] = otherCols[i][e];
                velos[e][i] = otherVelos[i][e];
            }
        }

        if (counter == -650) {
            save(hilove());
        }
    }
    int working = 0;

    public String hilove() {
        String out = "";

        for (int i = 0; i < cols.length - 1; i++) {
            for (int e = 0; e < cols[1].length - 1; e++) {
                out += "triangle {" + colcoords(i, e) + "," + colcoords(i + 1, e) + "," + colcoords(i, e + 1) + "1 texture { T_Chrome_4E}}";
                out += "triangle {" + colcoords(i + 1, e + 1) + "," + colcoords(i + 1, e) + "," + colcoords(i, e + 1) + "1 texture { T_Chrome_4E}}";
                working++;
                if (working > 1000) {
                    working = 0;
                    System.out.println("not done");
                }
            }

        }
        return out;
    }

    public String colcoords(int i, int e) {
        String out = "";
        out += "<" + i + "," + e + "," + cols[i][e] + ">";
        return out;
    }

    public void flow(float[] hMap, float[] vMap) {   // hMap is heights, vMap is corresponding velocities
         /*
         * see simpleFlow() for an idea of the basic algorithm- this gets messy
         * with all the index variables which are used to bunch the columns into
         * groups that are treated as one giant column that interact in the same
         * way against the other groups of columns the multi-tier approach is
         * only used on the force portion of the algorithm. the bottoming check
         * and height changing can operate the same old way.
         */
        // the base tier (when a=1)is individual columns
        for (int a = 1; a < tiers + 1; a++) {
            for (int i = 0; i < hMap.length / a - 1; i++) {
                float myVolume = 0;
                for (int e = 0; e < a; e++) {
                    myVolume += hMap[a * i + e];
                }

                float otherVolume = 0;
                for (int e = 0; e < a; e++) {
                    otherVolume += hMap[a * (1 + i) + e];
                }
                float myForceDown = myVolume * grav;
                float otherForceDown = otherVolume * grav;
                float netForce = myForceDown - otherForceDown;
                float netVolume = myVolume + otherVolume;
                // if the net vol is zero then would get a divzero error,
                // but those scenarios would also always have no net force
                if (netForce != 0) {
                    for (int e = 0; e < a; e++) {
                        vMap[a * i + e] -= netForce / netVolume;
                    }

                    for (int e = 0; e < a; e++) {
                        vMap[a * (1 + i) + e] += netForce / netVolume;
                    }
                }
            }
        }
        // movement!
        for (int i = 0; i < cols.length; i++) {
            // handling "bottoming out" where down velo outstrips remaining h
            hMap[i] += vMap[i] * dt;
        }
        // reversing bottom-outs
        // step 1:
        for (int i = 0; i < cols.length; i++) {
            // this fixes little errors, like if one column goes under and the
            // surrounding ones are still positive or slightly negative
            if (hMap[i] < 0) {
                if (i == 0) {
                    hMap[i + 1] -= Math.abs(hMap[i]);
                    hMap[i] = 0f;
                } else if (i == hMap.length - 1) {
                    hMap[i - 1] -= Math.abs(hMap[i]);
                    hMap[i] = 0f;
                } else {
                    hMap[i - 1] -= Math.abs(hMap[i]) / 2;
                    hMap[i + 1] -= Math.abs(hMap[i]) / 2;
                    hMap[i] = 0f;

                }
            }
        }/*
         * //step 2: // taking all negative volume and moving it up top by //
         * evenly distributing it over the other columns float negativeVolume =
         * 0; float numberOfNegatives = 0; for (int i = 0; i < cols.length; i++)
         * { // finding net negative if (hMap[i] < 0) { negativeVolume +=
         * Math.abs(hMap[i]); numberOfNegatives++; hMap[i] = 0f; }
         *
         * }
         *
         * if (numberOfNegatives != 0) { float amountToAdd = negativeVolume /
         * (hMap.length - numberOfNegatives); for (int i = 0; i < hMap.length;
         * i++) // redistributing net negative { if (hMap[i] > 0) { hMap[i] -=
         * amountToAdd; } } }
         */

        // step 2 redo: taking all negative volume and redistributing it to
        // non-negative adjacent columns rather than evenly- works better for
        // simulating waves hitting walls
        ArrayList<Integer> negativeIndices = new ArrayList<Integer>();
        float negativeVolume = 0;
        float numberOfNegatives = 0;
        for (int i = 0; i < cols.length; i++) {
            // finding net negative
            if (hMap[i] < 0) {
                negativeVolume += Math.abs(hMap[i]);
                numberOfNegatives++;
                hMap[i] = 0f;
                negativeIndices.add(i);
            }
        }

        if (numberOfNegatives != 0) {
            float amountToAdd = negativeVolume / (hMap.length - numberOfNegatives);
            for (int i = 0; i < hMap.length; i++) // redistributing net negative
            {
                if (hMap[i] > 0) {
                    hMap[i] -= amountToAdd;
                }
            }
        }

        //end method
    }

    public void dampen(float[] vMap, float fraction) {
        // decreases all velos is vMap by a certain fraction
        for (int i = 0; i < vMap.length; i++) {
            vMap[i] *= fraction;
        }
    }

    public void even(float[] hMap, float scalar) {
        // averaging technique, scalar is by how quickly it averages out.
        // this is basically surface tension I think...

        for (int i = 0; i < hMap.length - 1; i++) {
            float diff = hMap[i] - hMap[i + 1];
            // moving every  pair 
            // closer to its average
            hMap[i] -= diff / scalar;
            hMap[i + 1] += diff / scalar;
        }


    }

    public int lesserof(int num1, int num2) {
        if (num1 > num2) {
            return num1;
        } else {
            return num2;
        }
    }

    public void save(String s) {

        try {
            BufferedWriter out = new BufferedWriter(new FileWriter("mesh.txt"));

            out.append(s);
            out.close();
        } catch (IOException e) {
        }
    }
}
