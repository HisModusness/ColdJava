package coldjava;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.html.HTMLEditorKit;

public class BrowserWindow extends JFrame {

    private MainMenu mainMenu;
    private ContentPane contentPane;
    private ProtocolHandler handler;
    private JFileChooser chooser;

    public BrowserWindow() {
        super("ColdJava");
        handler = new ProtocolHandler();
        mainMenu = new MainMenu();
        contentPane = new ContentPane();
        chooser = new JFileChooser();
        chooser.setFileFilter(new FileNameExtensionFilter("Java Source Code", "java"));

        setJMenuBar(mainMenu);
        setContentPane(contentPane);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setVisible(true);
    }

    public BrowserWindow(String location) {
        this();
        setAddress(location);
    }

    public void setAddress(String location) {
        Protocol protocol = handler.getProtocol(location);
        contentPane.setContent(protocol.doProtocol(location), location);
        pack();
    }
    
    public class MainMenu extends JMenuBar {
        public MainMenu() {
            JMenu file = new JMenu("File");
            JMenuItem upload = new JMenuItem("Upload New Protocol...");
            upload.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_U, ActionEvent.CTRL_MASK));
            upload.addActionListener(new ActionListener() {
               public void actionPerformed(ActionEvent action) {
                   UploadPanel up = new UploadPanel();
               } 
            });
            file.add(upload);
            file.addSeparator();
            
            JMenuItem exit = new JMenuItem("Quit");
            exit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, ActionEvent.CTRL_MASK));
            exit.addActionListener(new ActionListener() {
               public void actionPerformed(ActionEvent action) {
                   Object[] options = {"Quit", "Don't Quit" };
                   int choice = JOptionPane.showOptionDialog(null, "Are you sure you want to quit?", "Quit", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[1]);
                   if (choice == 0) System.exit(0);
               } 
            });
            file.add(exit);
            add(file);
        }
    }

    public class ContentPane extends JPanel {

        private JEditorPane htmlPane;
        private InterfaceMenu uiMenu;
        

        public ContentPane() {
            super(new BorderLayout());
            uiMenu = new InterfaceMenu();
            add(uiMenu, BorderLayout.PAGE_START);

            htmlPane = new JEditorPane();
            htmlPane.setEditorKit(new HTMLEditorKit());
            htmlPane.setEditable(false);
            htmlPane.addHyperlinkListener(new HyperlinkListener() {
                public void hyperlinkUpdate(HyperlinkEvent link) {
                    if (link.getEventType() == HyperlinkEvent.EventType.ACTIVATED && link.getURL() != null) {
                        BrowserWindow.this.setAddress(link.getURL().toString());
                    }
                }
            });
            htmlPane.setPreferredSize(new Dimension(500, 500));
            JScrollPane scrollPane = new JScrollPane(htmlPane);
            add(scrollPane, BorderLayout.CENTER);
        }

        public void setContent(String html, String location) {
            uiMenu.updateAddressBar(location);
            htmlPane.setText(html);
        }

        public class InterfaceMenu extends JPanel {

            final JTextField addressBar = new JTextField(30);

            public InterfaceMenu() {
                super(new BorderLayout());
                setBorder(BorderFactory.createCompoundBorder(
                        getBorder(),
                        BorderFactory.createEmptyBorder(5, 5, 5, 5)));

                addressBar.setFont(new Font("San-Serif", Font.PLAIN, 16));
                addressBar.addKeyListener(new KeyListener() {
                    public void keyPressed(KeyEvent key) {
                    }

                    public void keyReleased(KeyEvent key) {
                        if (key.getKeyCode() == KeyEvent.VK_ENTER) {
                            BrowserWindow.this.setAddress(addressBar.getText());
                        }
                    }

                    public void keyTyped(KeyEvent key) {
                    }
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
    
    public class UploadPanel extends JFrame implements ActionListener {
        JTextField protocolEntry;
        JTextField fileEntry;
                
        
        public UploadPanel() {
            
            setLayout(new BoxLayout(getContentPane(), BoxLayout.X_AXIS));
            
            JPanel labels = new JPanel(new GridLayout(3, 1));
            JPanel protocolLabelPanel = new JPanel(new FlowLayout());
            JPanel fileLabelPanel = new JPanel(new FlowLayout());
            JPanel classLabelPanel = new JPanel();
            
            JPanel textInputs = new JPanel(new GridLayout(3, 1));
            JPanel protocolEntryPanel = new JPanel(new FlowLayout());
            JPanel fileEntryPanel = new JPanel(new FlowLayout());
            JPanel classValuePanel = new JPanel();
            
            JPanel buttons = new JPanel(new GridLayout(3, 1));
            JPanel protocolSpacePanel = new JPanel();
            JPanel fileButtonPanel = new JPanel(new FlowLayout());
            JPanel sendButtonPanel = new JPanel(new FlowLayout());
            
            JLabel protocolLabel = new JLabel("Protocol:");
            JLabel fileLabel = new JLabel("File:");
            
            JButton chooseFile = new JButton("Browse");
            chooseFile.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent action) {
                    int dialogResult = chooser.showOpenDialog(getContentPane());
                    if (dialogResult == JFileChooser.APPROVE_OPTION) {
                        File chosen = chooser.getSelectedFile();
                        String filePath = chosen.getAbsolutePath();
                        fileEntry.setText(filePath);
                        protocolEntry.setText(chosen.getName().substring(0, chosen.getName().lastIndexOf(".java")).toLowerCase());
                    }
                }
            });
            JButton sendFile = new JButton("Send");
            sendFile.addActionListener(this);
            
            protocolEntry = new JTextField(30);
            fileEntry = new JTextField(30);
            
            protocolLabelPanel.add(protocolLabel);
            fileLabelPanel.add(fileLabel);
            
            protocolEntryPanel.add(protocolEntry);
            fileEntryPanel.add(fileEntry);
            
            fileButtonPanel.add(chooseFile);
            sendButtonPanel.add(sendFile);
            
            labels.add(protocolLabelPanel);
            labels.add(fileLabelPanel);
            labels.add(classLabelPanel);
            
            textInputs.add(protocolEntryPanel);
            textInputs.add(fileEntryPanel);
            textInputs.add(classValuePanel);
            
            buttons.add(protocolSpacePanel);
            buttons.add(fileButtonPanel);
            buttons.add(sendButtonPanel);
            
            add(labels);
            add(textInputs);
            add(buttons);
            
            pack();
            setVisible(true);
        }
        
        public void actionPerformed(ActionEvent action) {
            ProtocolHandler sender = new ProtocolHandler();
            String result = sender.sendProtocol(fileEntry.getText(), protocolEntry.getText());
            JOptionPane.showMessageDialog(this, result, "Response", JOptionPane.INFORMATION_MESSAGE);
            if (!result.startsWith("ERROR:")) dispose();
        }
    }
}
