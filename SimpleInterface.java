import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class SimpleInterface extends JFrame {

    private JTextArea zoneCode;      
    private JTextArea zoneResultat;  
    private JButton boutonCompiler;

    public SimpleInterface() {
        setTitle("Compilateur PHP (If/Else) - Salmani & Youlidas");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); 

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        zoneCode = new JTextArea();
        zoneCode.setFont(new Font("Monospaced", Font.PLAIN, 14));
        // Code par défaut pour tester les noms
        zoneCode.setText("<?php\n" +
                "// Projet par Salmani Fakhreddine \n" +
                "$x = 10;\n" +
                "if ($x > 5) { echo $x; }\n" +
                "?>");
        JScrollPane scrollCode = new JScrollPane(zoneCode);
        scrollCode.setBorder(BorderFactory.createTitledBorder("Code Source PHP"));

        zoneResultat = new JTextArea();
        zoneResultat.setFont(new Font("Monospaced", Font.PLAIN, 14));
        zoneResultat.setEditable(false); 
        zoneResultat.setForeground(Color.BLUE);
        JScrollPane scrollResultat = new JScrollPane(zoneResultat);
        scrollResultat.setBorder(BorderFactory.createTitledBorder("Console / Logs"));
        scrollResultat.setPreferredSize(new Dimension(800, 200));

        boutonCompiler = new JButton("COMPILER / ANALYSER");
        boutonCompiler.setFont(new Font("Arial", Font.BOLD, 16));
        boutonCompiler.setBackground(new Color(50, 150, 50));
        boutonCompiler.setForeground(Color.WHITE);

        mainPanel.add(scrollCode, BorderLayout.CENTER);
        mainPanel.add(boutonCompiler, BorderLayout.NORTH);
        mainPanel.add(scrollResultat, BorderLayout.SOUTH);

        this.add(mainPanel);

        boutonCompiler.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                lancerAnalyse();
            }
        });
    }

    private void lancerAnalyse() {
        String code = zoneCode.getText();
        
        if (code.trim().isEmpty()) {
            zoneResultat.setText(">>> Erreur : Veuillez écrire du code !");
            return;
        }

        try {
            LexicalePHP lexer = new LexicalePHP(code);
            lexer.scanlexicale();
            List<LexicalePHP.lexeme> tokens = lexer.getTokens();

            StringBuilder sb = new StringBuilder();
            
            sb.append("=== ANALYSE LEXICALE ===\n");
            for (LexicalePHP.lexeme token : tokens) {
                sb.append(token.toString()).append("\n");
            }
            sb.append("\n=== ANALYSE SYNTAXIQUE ===\n");

            // Attention : on appelle bien 'syntaxique' (la classe fournie)
            syntaxique parser = new syntaxique(tokens);
            parser.Z();

            sb.append(parser.logs.toString());
            zoneResultat.setText(sb.toString());
            
        } catch (Exception ex) {
            zoneResultat.setText("Erreur Java : " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new SimpleInterface().setVisible(true);
        });
    }
}