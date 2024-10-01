import com.formdev.flatlaf.FlatLightLaf;
import controller.WordController;
import view.Window;

import javax.swing.*;

public class App {

    public App() {

    }

    public static void main(String args[]) {
        try {
            UIManager.setLookAndFeel( new FlatLightLaf() );
        } catch( Exception ex ) {
            System.err.println( "Failed to initialize theme. Using fallback." );
        }
        SwingUtilities.invokeLater(() -> new Window(new WordController()));
    }
}
