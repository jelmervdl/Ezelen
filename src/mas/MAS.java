/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mas;

/**
 *
 * @author jelmer
 */
public class MAS {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception
    {
        Table table = new Table(4);
        
        MainWindow mainWindow = new MainWindow(table);
        
        mainWindow.setTable(table);
        
        mainWindow.setVisible(true);
    }
}
