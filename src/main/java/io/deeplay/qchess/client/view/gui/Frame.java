package io.deeplay.qchess.client.view.gui;

import java.awt.Component;
import javax.swing.JFrame;

public abstract class Frame extends Component {
    protected JFrame frame;
    protected MainFrame mf;

    void destroy() {
        frame.setVisible(false);
        frame.dispose();
    }
}
