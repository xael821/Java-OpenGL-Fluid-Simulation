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
import java.awt.event.*;

public class sizeselector extends JFrame implements ActionListener {

    JTextField x = new JTextField(4);
    JTextField y = new JTextField(4);
    JButton ok = new JButton("ok");

    public sizeselector() {
        super("Select Simulation Size");
        setSize(300, 100);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setVisible(true);
        FlowLayout f = new FlowLayout();
        this.add(new JLabel("X:"));
        this.add(x);
        this.add(new JLabel("Y:"));
        this.add(y);
        this.add(ok);
        x.addActionListener(this);
        y.addActionListener(this);
        ok.addActionListener(this);
        setLayout(f);

    }

    public void actionPerformed(ActionEvent e) {
        String source = e.getActionCommand();
        if (source.equals("ok")) {
            int i1 = new Integer(x.getText());
            int i2 = new Integer(y.getText());

            builder b = new builder(i1, i2);

            this.dispose();
        }
    }
}
