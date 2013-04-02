/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mas;

import java.util.ArrayList;

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
        
//        Image tinyCatPicture = new Card(Card.Type.ACE, Card.Suit.SPADES).getImage().getScaledInstance(-1, 65, Image.SCALE_SMOOTH);
//        window.card41.setIcon(new ImageIcon(tinyCatPicture));
//        window.card41.setText("");
//        window.setVisible(true);
    }
}
