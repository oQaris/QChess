package io.deeplay.qchess.client.view.gui;

import javax.swing.*;
import java.awt.*;

public abstract class Frame extends Component {
    protected JFrame frame; // A field should not duplicate the name of its containing class
    protected MainFrame mf; // Make "mf" transient or serializable.

    void destroy() {
        frame.setVisible(false);
        frame.dispose();
    }
}
