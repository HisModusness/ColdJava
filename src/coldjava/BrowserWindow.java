package coldjava;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.html.HTMLEditorKit;


public class BrowserWindow extends JFrame {

    private ContentPane contentPane;
    
    public BrowserWindow()
    {
  	super("ColdJava");
	contentPane = new ContentPane();
	setContentPane(contentPane);
	setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	pack();
	setVisible(true);
    }
    
    public BrowserWindow(String location)
    {
	this();
	setAddress(location);
    }
    
    public void setAddress(String location) {
	//THIS IS WHERE I ASK TIM'S CODE WHAT A LOCATION MEANS AND HOPEFULLY GET SOME HTML BACK
	//UNTIL THEN, DEMO CODE:
	Http http = new Http();
	contentPane.setContent(http.doProtocol(location), location);
	pack();
    }
    
    public class ContentPane extends JPanel {
	private JEditorPane htmlPane;
	private InterfaceMenu uiMenu;
	public ContentPane() 
	{
	    super(new BorderLayout());
	    uiMenu = new InterfaceMenu();
	    add(uiMenu, BorderLayout.PAGE_START);
	    
	    htmlPane = new JEditorPane();
	    htmlPane.setEditorKit(new HTMLEditorKit());
	    htmlPane.setEditable(false);
	    htmlPane.addHyperlinkListener(new HyperlinkListener() {
		public void hyperlinkUpdate(HyperlinkEvent link) {
		    if(link.getEventType() == HyperlinkEvent.EventType.ACTIVATED && link.getURL() != null)
			BrowserWindow.this.setAddress(link.getURL().toString());
		}
	    });
	    htmlPane.setPreferredSize(new Dimension(500,500));
	    JScrollPane scrollPane = new JScrollPane(htmlPane);
	    add(scrollPane, BorderLayout.CENTER); 
	}
	
	public void setContent(String html, String location)
	{
	    uiMenu.updateAddressBar(location);
	    htmlPane.setText(html);
	}
	
	public class InterfaceMenu extends JPanel {
	    final JTextField addressBar = new JTextField(30);
	     public InterfaceMenu() 
	     {
		 super(new BorderLayout());
		 setBorder(BorderFactory.createCompoundBorder(
		        	getBorder(), 
		        	BorderFactory.createEmptyBorder(5, 5, 5, 5))
		             );
		
		 addressBar.setFont(new Font("San-Serif", Font.PLAIN, 16));
		 addressBar.addKeyListener(new KeyListener() {
		    public void keyPressed(KeyEvent key) { }
		    public void keyReleased(KeyEvent key) {
			if(key.getKeyCode() == KeyEvent.VK_ENTER)
			{
			    BrowserWindow.this.setAddress(addressBar.getText());
			}
		    }
		    public void keyTyped(KeyEvent key) {  }
		 });
		 add(addressBar, BorderLayout.CENTER);
		 
		 JButton go = new JButton("Go");
		 go.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent action) {
			BrowserWindow.this.setAddress(addressBar.getText());
		    }
		 });
		 add(go, BorderLayout.LINE_END);
	     }
	     public void updateAddressBar(String location) {
		 addressBar.setText(location);
	     }
	}
	
    }

}
