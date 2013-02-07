/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package h2o;

/**
 *
 * @author Alex
 */
public class mobile {

    float x;
    float y;
    float z;
    float velox;
    float veloy;
    float veloz;

    public void move() {
        x += velox;
        y += veloy;
        z += veloz;
    }
}
