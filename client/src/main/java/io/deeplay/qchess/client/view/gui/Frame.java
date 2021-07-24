package io.deeplay.qchess.client.view.gui;

import java.awt.Component;
import javax.swing.JFrame;

public abstract class Frame extends Component {
    protected JFrame frame; // A field should not duplicate the name of its containing class
    protected MainFrame mf; // Make "mf" transient or serializable.

    void destroy() {
        frame.setVisible(false);
        frame.dispose();
    }
}
