package information_management_practice;

public class MainApp {

    public static void main(String[] args) {
        DBConnection.getConnection();
        DatabaseInitializer.initialize();
        DataSeeder.seed();
        com.formdev.flatlaf.FlatDarkLaf.setup();
        java.awt.EventQueue.invokeLater(() -> {
            GUI gui = new GUI();
            gui.setVisible(true);
            gui.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosing(java.awt.event.WindowEvent e) {
                    DBConnection.close();
                }
            });
        });
    }
}
