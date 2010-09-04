/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * Base.java
 *
 * Created on 18/08/2010, 15:26:36
 */

/**
 *
 * @author Richard Frank
 */

import java.io.IOException;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.ImageIcon;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.html.HTMLEditorKit;
import twitter4j.DirectMessage;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;


public class Base extends javax.swing.JFrame {
    
    private Twitter t;

    public Base(Twitter t) {
        
        this.t = t;

        Timer tm = new Timer("Refresh", false);
        tm.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run(){
                getData();
            }
        }, 1000, 600000);
        initComponents();
    }

    public final void getData() {
        loadTimeline();
        loadDirectMessage();
        loadMention();
    }

    public void loadTimeline() {
        try {

            JPanel panelTimeline;
            JLabel avatarTimeline;
            JEditorPane statusTimeline;

            sTimeline.removeAll();

            List<Status> timeline = t.getHomeTimeline();
            for (Status s : timeline){

                panelTimeline = new JPanel(new FlowLayout(FlowLayout.LEFT));
                panelTimeline.setSize(150, 100);
                panelTimeline.setBackground(Color.white);
                panelTimeline.setBorder(javax.swing.BorderFactory.createEtchedBorder());

                avatarTimeline = new JLabel(new ImageIcon(this.getToolkit().getImage(s.getUser().getProfileImageURL())));

                statusTimeline = new JEditorPane();
                statusTimeline.setEditorKit(new HTMLEditorKit());
                statusTimeline.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, Boolean.TRUE);
                statusTimeline.setFont(new Font("DejaVu Sans Semi-Condensed", 0, 12));
                statusTimeline.setForeground(new Color(52, 52, 52));
                statusTimeline.setContentType("text/html");
                statusTimeline.setSize(220, 10);
                statusTimeline.setEditable(false);
                statusTimeline.setAutoscrolls(true);

                String hyperlinks = "";
                Pattern pattern_hyperlinks = Pattern.compile("(([\\w]+:)?//)?(([\\d\\w]|%[a-fA-f\\d]{2,2})+(:([\\d\\w]|%[a-fA-f\\d]{2,2})+)?@)?([\\d\\w][-\\d\\w]{0,253}[\\d\\w]\\.)+[\\w]{2,4}(:[\\d]+)?(/([-+_~.\\d\\w]|%[a-fA-f\\d]{2,2})*)*(\\?(&?([-+_~.\\d\\w]|%[a-fA-f\\d]{2,2})=?)*)?(#([-+_~.\\d\\w]|%[a-fA-f\\d]{2,2})*)?");
                String replace_hyperlinks = "<a style='color: rgb(77,77,77)' href=\"$0\">$0</a>";
                hyperlinks = String.valueOf(s.getText());
                Matcher matcher_hyperlinks = pattern_hyperlinks.matcher(hyperlinks);
                hyperlinks = matcher_hyperlinks.replaceAll(replace_hyperlinks);

                Pattern pattern_hashtags = Pattern.compile("#\\w+");
                String replace_hashtags = "<a style='color: rgb(77,77,77)' href=\"$0\">$0</a>";
                String hashtags = "";
                hashtags = String.valueOf(hyperlinks);
                Matcher matcher_hashtags = pattern_hashtags.matcher(hashtags);
                hashtags = matcher_hashtags.replaceAll(replace_hashtags);

                Pattern pattern_users = Pattern.compile("@\\w+");
                String replace_users = "<a style='color: rgb(77,77,77)' href=\"$0\">$0</a>";
                String users = "";
                users = String.valueOf(hashtags);
                Matcher matcher_users = pattern_users.matcher(users);
                users = matcher_users.replaceAll(replace_users);
                
                String status = new String();
                status ="<b>"+s.getUser().getScreenName()+"</b>"+"<br>"+users+"<br>"+"<font size='3' color='#999999ff'>"+s.getCreatedAt()+" via "+s.getSource()+"</font>";
                statusTimeline.setText(status);

                panelTimeline.add(avatarTimeline);
                panelTimeline.add(statusTimeline);
                sTimeline.add(panelTimeline);

                statusTimeline.addHyperlinkListener(new HyperlinkListener() {

                    public void hyperlinkUpdate( HyperlinkEvent event ) {
                        if (event.getEventType() == HyperlinkEvent.EventType.ACTIVATED )
                            try {
                                java.awt.Desktop.getDesktop().browse(
                                java.net.URI.create(event.getURL().toString()));
                            } catch (IOException e) {
                                JOptionPane.showMessageDialog (null, "IO Failed");
                                }
                    }
                });
            }

        } catch (TwitterException ex) {
            JOptionPane.showMessageDialog(this, "Opss!, error while loading your Timeline. " + ex.getMessage());
        }
    }

    public void loadDirectMessage() {
        try {
            JPanel panelMessage;
            JLabel avatarMessage;
            JEditorPane statusMessage;

            sMessage.removeAll();

            List<DirectMessage> dmessage = t.getDirectMessages();
            for (DirectMessage dm : dmessage) {

                panelMessage = new JPanel(new FlowLayout(FlowLayout.LEFT));
                panelMessage.setSize(150, 50);
                panelMessage.setBackground(Color.white);
                panelMessage.setBorder(javax.swing.BorderFactory.createEtchedBorder());

                avatarMessage = new JLabel(new ImageIcon(this.getToolkit().getImage(dm.getSender().getProfileImageURL())));

                statusMessage = new JEditorPane();
                statusMessage.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, Boolean.TRUE);
                statusMessage.setFont(new Font("DejaVu Sans Semi-Condensed", 0, 12));
                statusMessage.setForeground(new Color(52, 52, 52));
                statusMessage.setContentType("text/html");
                statusMessage.setSize(220, 10);
                statusMessage.setPreferredSize(null);
                statusMessage.setEditable(false);
                statusMessage.setAutoscrolls(true);

                String hyperlinks = "";
                Pattern pattern_hyperlinks = Pattern.compile("(([\\w]+:)?//)?(([\\d\\w]|%[a-fA-f\\d]{2,2})+(:([\\d\\w]|%[a-fA-f\\d]{2,2})+)?@)?([\\d\\w][-\\d\\w]{0,253}[\\d\\w]\\.)+[\\w]{2,4}(:[\\d]+)?(/([-+_~.\\d\\w]|%[a-fA-f\\d]{2,2})*)*(\\?(&?([-+_~.\\d\\w]|%[a-fA-f\\d]{2,2})=?)*)?(#([-+_~.\\d\\w]|%[a-fA-f\\d]{2,2})*)?");
                String replace_hyperlinks = "<a style='color: rgb(77,77,77)' href=\"$0\">$0</a>";
                hyperlinks = String.valueOf(dm.getText());
                Matcher matcher_hyperlinks = pattern_hyperlinks.matcher(hyperlinks);
                hyperlinks = matcher_hyperlinks.replaceAll(replace_hyperlinks);

                Pattern pattern_hashtags = Pattern.compile("#\\w+");
                String replace_hashtags = "<a style='color: rgb(77,77,77)' href=\"$0\">$0</a>";
                String hashtags = "";
                hashtags = String.valueOf(hyperlinks);
                Matcher matcher_hashtags = pattern_hashtags.matcher(hashtags);
                hashtags = matcher_hashtags.replaceAll(replace_hashtags);

                Pattern pattern_users = Pattern.compile("@\\w+");
                String replace_users = "<a style='color: rgb(77,77,77)' href=\"$0\">$0</a>";
                String users = "";
                users = String.valueOf(hashtags);
                Matcher matcher_users = pattern_users.matcher(users);
                users = matcher_users.replaceAll(replace_users);

                String status = new String();
                status ="<b>"+dm.getSender().getScreenName()+"</b>"+"<br>"+users+"<br>"+"<font size='3' color='#999999ff'>"+dm.getCreatedAt()+"</font>";
                statusMessage.setText(status);

                panelMessage.add(avatarMessage);
                panelMessage.add(statusMessage);
                sMessage.add(panelMessage);

                statusMessage.addHyperlinkListener(new HyperlinkListener() {

                    public void hyperlinkUpdate( HyperlinkEvent event ) {
                        if (event.getEventType() == HyperlinkEvent.EventType.ACTIVATED )
                            try {
                                java.awt.Desktop.getDesktop().browse(
                                java.net.URI.create(event.getURL().toString()));
                            } catch (IOException e) {
                                JOptionPane.showMessageDialog (null, "IO Failed");
                                }
                    }
                });
            }

        } catch (TwitterException ex) {
            JOptionPane.showMessageDialog(this, "Opss!, error while loading your Direct Messages. " + ex.getMessage());
        }
    }

    public void loadMention() {
        try {
            JPanel panelMention;
            JLabel avatarMention;
            JEditorPane statusMention;

            sMention.removeAll();

            List<Status> mention = t.getMentions();
            for (Status m : mention) {

                panelMention = new JPanel(new FlowLayout(FlowLayout.LEFT));
                panelMention.setSize(150, 50);
                panelMention.setBackground(Color.white);
                panelMention.setBorder(javax.swing.BorderFactory.createEtchedBorder());

                avatarMention = new JLabel(new ImageIcon(this.getToolkit().getImage(m.getUser().getProfileImageURL())));

                statusMention = new JEditorPane();
                statusMention.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, Boolean.TRUE);
                statusMention.setFont(new Font("DejaVu Sans Semi-Condensed", 0, 12));
                statusMention.setForeground(new Color(52, 52, 52));
                statusMention.setContentType("text/html");
                statusMention.setSize(220, 10);
                statusMention.setEditable(false);
                statusMention.setAutoscrolls(true);

                String hyperlinks = "";
                Pattern pattern_hyperlinks = Pattern.compile("(([\\w]+:)?//)?(([\\d\\w]|%[a-fA-f\\d]{2,2})+(:([\\d\\w]|%[a-fA-f\\d]{2,2})+)?@)?([\\d\\w][-\\d\\w]{0,253}[\\d\\w]\\.)+[\\w]{2,4}(:[\\d]+)?(/([-+_~.\\d\\w]|%[a-fA-f\\d]{2,2})*)*(\\?(&?([-+_~.\\d\\w]|%[a-fA-f\\d]{2,2})=?)*)?(#([-+_~.\\d\\w]|%[a-fA-f\\d]{2,2})*)?");
                String replace_hyperlinks = "<a style='color: rgb(77,77,77)' href=\"$0\">$0</a>";
                hyperlinks = String.valueOf(m.getText());
                Matcher matcher_hyperlinks = pattern_hyperlinks.matcher(hyperlinks);
                hyperlinks = matcher_hyperlinks.replaceAll(replace_hyperlinks);

                Pattern pattern_hashtags = Pattern.compile("#\\w+");
                String replace_hashtags = "<a style='color: rgb(77,77,77)' href=\"$0\">$0</a>";
                String hashtags = "";
                hashtags = String.valueOf(hyperlinks);
                Matcher matcher_hashtags = pattern_hashtags.matcher(hashtags);
                hashtags = matcher_hashtags.replaceAll(replace_hashtags);

                Pattern pattern_users = Pattern.compile("@\\w+");
                String replace_users = "<a style='color: rgb(77,77,77)' href=\"$0\">$0</a>";
                String users = "";
                users = String.valueOf(hashtags);
                Matcher matcher_users = pattern_users.matcher(users);
                users = matcher_users.replaceAll(replace_users);

                String status = new String();
                status ="<b>"+m.getUser().getScreenName()+"</b>"+"<br>"+users+"<br>"+"<font size='3' color='#999999ff'>"+m.getCreatedAt()+" via "+m.getSource()+"</font>";
                statusMention.setText(status);

                panelMention.add(avatarMention);
                panelMention.add(statusMention);
                sMention.add(panelMention);

                statusMention.addHyperlinkListener(new HyperlinkListener() {

                    public void hyperlinkUpdate( HyperlinkEvent event ) {
                        if (event.getEventType() == HyperlinkEvent.EventType.ACTIVATED )
                            try {
                                java.awt.Desktop.getDesktop().browse(
                                java.net.URI.create(event.getURL().toString()));
                            } catch (IOException e) {
                                JOptionPane.showMessageDialog (null, "IO Failed");
                                }
                    }
                });
            }

        } catch (TwitterException ex) {
            JOptionPane.showMessageDialog(this, "Opss!, error while loading yours Mentions. " + ex.getMessage());
        }
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jCheckBox1 = new javax.swing.JCheckBox();
        jScrollPane1 = new javax.swing.JScrollPane();
        sArea = new javax.swing.JTextArea();
        jScrollPane2 = new javax.swing.JScrollPane();
        sTimeline = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        sMention = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        sMessage = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem2 = new javax.swing.JMenuItem();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenu3 = new javax.swing.JMenu();
        jMenuItem3 = new javax.swing.JMenuItem();

        jCheckBox1.setText("jCheckBox1");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("TweetDuck v0.7 Alpha");
        setBackground(new java.awt.Color(255, 255, 255));
        setBounds(new java.awt.Rectangle(0, 0, 0, 0));
        setForeground(new java.awt.Color(255, 255, 255));
        setResizable(false);

        jScrollPane1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jScrollPane1.setAutoscrolls(true);

        sArea.setColumns(20);
        sArea.setLineWrap(true);
        sArea.setRows(2);
        sArea.setBorder(null);
        sArea.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                sAreaCaretUpdate(evt);
            }
        });
        sArea.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                sAreaKeyTyped(evt);
            }
        });
        jScrollPane1.setViewportView(sArea);

        jScrollPane2.setBackground(new java.awt.Color(255, 255, 255));
        jScrollPane2.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jScrollPane2.setAutoscrolls(true);

        sTimeline.setBackground(new java.awt.Color(255, 255, 255));
        sTimeline.setLayout(new javax.swing.BoxLayout(sTimeline, javax.swing.BoxLayout.Y_AXIS));
        jScrollPane2.setViewportView(sTimeline);

        jScrollPane3.setBackground(new java.awt.Color(255, 255, 255));
        jScrollPane3.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jScrollPane3.setAutoscrolls(true);

        sMention.setBackground(new java.awt.Color(255, 255, 255));
        sMention.setLayout(new javax.swing.BoxLayout(sMention, javax.swing.BoxLayout.Y_AXIS));
        jScrollPane3.setViewportView(sMention);

        jScrollPane4.setBackground(new java.awt.Color(255, 255, 255));
        jScrollPane4.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jScrollPane4.setAutoscrolls(true);

        sMessage.setBackground(new java.awt.Color(255, 255, 255));
        sMessage.setLayout(new javax.swing.BoxLayout(sMessage, javax.swing.BoxLayout.Y_AXIS));
        jScrollPane4.setViewportView(sMessage);

        jButton1.setBackground(new java.awt.Color(255, 255, 255));
        jButton1.setForeground(new java.awt.Color(51, 51, 51));
        jButton1.setText("send");
        jButton1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jLabel1.setForeground(new java.awt.Color(102, 102, 102));
        jLabel1.setText("Timeline");

        jLabel2.setForeground(new java.awt.Color(102, 102, 102));
        jLabel2.setText("Mentions");

        jLabel3.setForeground(new java.awt.Color(102, 102, 102));
        jLabel3.setText("Direct Messages");

        jLabel4.setForeground(new java.awt.Color(102, 102, 102));
        jLabel4.setText("What’s happening?");

        jButton2.setBackground(new java.awt.Color(255, 255, 255));
        jButton2.setForeground(new java.awt.Color(51, 51, 51));
        jButton2.setText("more");
        jButton2.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton3.setBackground(new java.awt.Color(255, 255, 255));
        jButton3.setForeground(new java.awt.Color(51, 51, 51));
        jButton3.setText("more");
        jButton3.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jButton4.setBackground(new java.awt.Color(255, 255, 255));
        jButton4.setForeground(new java.awt.Color(51, 51, 51));
        jButton4.setText("more");
        jButton4.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jLabel5.setFont(new java.awt.Font("Tahoma", 1, 10));
        jLabel5.setForeground(new java.awt.Color(102, 102, 102));
        jLabel5.setText("Refresh");
        jLabel5.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jLabel5.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel5MouseClicked(evt);
            }
        });

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 10));
        jLabel6.setForeground(new java.awt.Color(102, 102, 102));
        jLabel6.setText("Refresh");
        jLabel6.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jLabel6.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel6MouseClicked(evt);
            }
        });

        jLabel7.setFont(new java.awt.Font("Tahoma", 1, 10));
        jLabel7.setForeground(new java.awt.Color(102, 102, 102));
        jLabel7.setText("Refresh");
        jLabel7.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jLabel7.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel7MouseClicked(evt);
            }
        });

        jLabel8.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel8.setForeground(new java.awt.Color(102, 102, 102));
        jLabel8.setText("140");

        jMenu1.setForeground(new java.awt.Color(51, 51, 51));
        jMenu1.setText("File");

        jMenuItem2.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F3, java.awt.event.InputEvent.ALT_MASK));
        jMenuItem2.setForeground(new java.awt.Color(51, 51, 51));
        jMenuItem2.setText("Sign out");
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem2ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem2);

        jMenuItem1.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F4, java.awt.event.InputEvent.ALT_MASK));
        jMenuItem1.setForeground(new java.awt.Color(51, 51, 51));
        jMenuItem1.setText("Exit");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem1);

        jMenuBar1.add(jMenu1);

        jMenu3.setForeground(new java.awt.Color(51, 51, 51));
        jMenu3.setText("Help");

        jMenuItem3.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F10, java.awt.event.InputEvent.ALT_MASK));
        jMenuItem3.setForeground(new java.awt.Color(51, 51, 51));
        jMenuItem3.setText("About");
        jMenuItem3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem3ActionPerformed(evt);
            }
        });
        jMenu3.add(jMenuItem3);

        jMenuBar1.add(jMenu3);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)
                            .addComponent(jButton4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 274, Short.MAX_VALUE)
                                .addComponent(jLabel5)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel6))
                            .addComponent(jButton3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jButton2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 235, Short.MAX_VALUE)
                                .addComponent(jLabel7))
                            .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 850, Short.MAX_VALUE)
                                .addComponent(jLabel8))
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 874, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(jLabel8))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 52, Short.MAX_VALUE)
                    .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, 52, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 461, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(jLabel3)
                            .addComponent(jLabel6)
                            .addComponent(jLabel5)
                            .addComponent(jLabel7))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 461, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 461, Short.MAX_VALUE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jButton3, javax.swing.GroupLayout.DEFAULT_SIZE, 30, Short.MAX_VALUE)
                        .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jButton4, javax.swing.GroupLayout.DEFAULT_SIZE, 31, Short.MAX_VALUE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
	try {
	    t.updateStatus(sArea.getText());
            loadTimeline();
            sArea.setText("");
	} catch (TwitterException ex) {
	    JOptionPane.showMessageDialog(this, "Sorry, error while updating your Status. " + ex.getMessage());
	}
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        // TODO add your handling code here:
        this.setVisible(false);
        this.dispose();
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void jMenuItem3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem3ActionPerformed
        // TODO add your handling code here:
        About a = new About();
        a.setVisible(true);
        a.setLocationRelativeTo(null);
    }//GEN-LAST:event_jMenuItem3ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        // TODO add your handling code here:
        String url = "http://twitter.com/home";
        try {
            java.awt.Desktop.getDesktop().browse(java.net.URI.create(url));
        } catch (IOException ex) {
            Logger.getLogger(Base.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        // TODO add your handling code here:
        String url = "http://twitter.com/replies";
        try {
            java.awt.Desktop.getDesktop().browse(java.net.URI.create(url));
        } catch (IOException ex) {
            Logger.getLogger(Base.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
        String url = "http://twitter.com/inbox";
        try {
            java.awt.Desktop.getDesktop().browse(java.net.URI.create(url));
        } catch (IOException ex) {
            Logger.getLogger(Base.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed

            // TODO add your handling code here:
            this.dispose();
            Auth a = new Auth();
            a.setVisible(true);
            a.setLocationRelativeTo(null);
        
    }//GEN-LAST:event_jMenuItem2ActionPerformed

    private void jLabel5MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel5MouseClicked
        // TODO add your handling code here:
        loadTimeline();
    }//GEN-LAST:event_jLabel5MouseClicked

    private void jLabel6MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel6MouseClicked
        // TODO add your handling code here:
        loadMention();
    }//GEN-LAST:event_jLabel6MouseClicked

    private void jLabel7MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel7MouseClicked
        // TODO add your handling code here:
        loadDirectMessage();
    }//GEN-LAST:event_jLabel7MouseClicked

    private void sAreaKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_sAreaKeyTyped
        // TODO add your handling code here:
        if (evt.getKeyChar() == KeyEvent.VK_ENTER) {
            try {
                t.updateStatus(sArea.getText());
                sArea.setText("");
                loadTimeline();
            } catch (TwitterException ex) {
                JOptionPane.showMessageDialog(this, "Sorry, error while updating your Status. " + ex.getMessage());
                }
            }
    }//GEN-LAST:event_sAreaKeyTyped

    private void sAreaCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_sAreaCaretUpdate
        // TODO add your handling code here:
        jLabel8.setText(String.valueOf(140 - sArea.getText().length()));
    }//GEN-LAST:event_sAreaCaretUpdate

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                //new Base().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JTextArea sArea;
    private javax.swing.JPanel sMention;
    private javax.swing.JPanel sMessage;
    private javax.swing.JPanel sTimeline;
    // End of variables declaration//GEN-END:variables

}
