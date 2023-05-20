package org.bgjug.jprime.registration;

import java.awt.*;

public class Utilities {
    public static Point centerComponentOnTheScreen(Component component) {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension frameSize = component.getSize();
        if (frameSize.height > screenSize.height) {
            frameSize.height = screenSize.height;
        }
        if (frameSize.width > screenSize.width) {
            frameSize.width = screenSize.width;
        }

        return new Point((screenSize.width - frameSize.width) / 2, (screenSize.height - frameSize.height) / 2);
    }
}
