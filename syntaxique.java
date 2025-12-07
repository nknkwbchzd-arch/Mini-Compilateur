import java.util.List;
import javax.swing.*;
import java.awt.*;

public class syntaxique {

    private final List<LexicalePHP.lexeme> tokens;
    private int i = 0;
    private boolean R = false; 

    public StringBuilder logs = new StringBuilder();

    public syntaxique(List<LexicalePHP.lexeme> tokens) {
        this.tokens = tokens;
    }

    // --- OUTILS (Exactement comme Youlidas) ---
    private LexicalePHP.TokenType getType() {
        // Méthode brute sans sécurité (comme demandé)
        return tokens.get(i).type;
    }

    private void accepter(LexicalePHP.TokenType typeAttendu) {
        if (getType() == typeAttendu) {
            if (getType() != LexicalePHP.TokenType.FIN) {
                i++; 
            }
        } else {
            R = true;
            int pos = (i < tokens.size()) ? tokens.get(i).position : -1;
            logs.append(">>> ERREUR (Pos ").append(pos).append(") : ")
                .append("Attendu '").append(typeAttendu).append("', ")
                .append("mais trouvé '").append(getType()).append("'\n");
            
            if (getType() != LexicalePHP.TokenType.FIN) {
                i++; 
            }
        }
    }

    // ==========================================
    //      STRUCTURE GLOBALE
    // ==========================================

    public void Z() {
        S(); 
        
        if (getType() == LexicalePHP.TokenType.FIN && R == false ) {
            logs.append("\n✅ [SUCCÈS] Analyse PHP terminée.\n");
        } 
        else {
            logs.append("\n❌ [ECHEC] Le code contient des erreurs.\n");
        }
    }

    public void S() {
        accepter(LexicalePHP.TokenType.OPEN_TAG); // <?php
        C(); 
        if (getType() == LexicalePHP.TokenType.CLOSE_TAG) {
            accepter(LexicalePHP.TokenType.CLOSE_TAG); // ?>
        }
    }

    // ==========================================
    //      INSTRUCTIONS
    // ==========================================

    public void C() {
        LexicalePHP.TokenType t = getType();

        if (t != LexicalePHP.TokenType.FIN && t != LexicalePHP.TokenType.CLOSE_TAG && t != LexicalePHP.TokenType.ACCOLADESDROITES) {
            
            // 1. Déclarations
            if (t == LexicalePHP.TokenType.INT || t == LexicalePHP.TokenType.DOUBLE) {
                D(); 
                C();
            }
            // 2. Affectations
            else if (t == LexicalePHP.TokenType.VARIABLE) {
                E();
                C();
            }
            // 3. ECHO / PRINT
            else if (t == LexicalePHP.TokenType.ECHO || t == LexicalePHP.TokenType.PRINT) {
                accepter(t);
                if (getType() == LexicalePHP.TokenType.VARIABLE) accepter(LexicalePHP.TokenType.VARIABLE);
                else if (getType() == LexicalePHP.TokenType.NUM) accepter(LexicalePHP.TokenType.NUM);
                accepter(LexicalePHP.TokenType.POINTVERGULE);
                C();
            }
            // 4. IF / ELSE (ACTIF)
            else if (t == LexicalePHP.TokenType.IF) {
                L(); 
                C();
            }
            // 5. SWITCH / WHILE / FOR (IGNORÉS - LOGIQUE ROBUSTE AVEC COMPTEURS)
            else if (t == LexicalePHP.TokenType.SWITCH || t == LexicalePHP.TokenType.WHILE || t== LexicalePHP.TokenType.FOR || t== LexicalePHP.TokenType.DO || t== LexicalePHP.TokenType.FOREACH ) {
                 i++; // On mange le mot clé
                 
                 // ETAPE 1 : On saute les parenthèses de condition (...)
                 if (getType() == LexicalePHP.TokenType.PARENTHESESGAUCHE) {
                     int p = 1; 
                     i++; // On mange '('
                     while (p > 0 && getType() != LexicalePHP.TokenType.FIN) {
                         if (getType() == LexicalePHP.TokenType.PARENTHESESGAUCHE) p++;
                         else if (getType() == LexicalePHP.TokenType.PARENTHESESDROITES) p--;
                         i++;
                     }
                 }

                 // ETAPE 2 : On saute le bloc { ... }
                 if (getType() == LexicalePHP.TokenType.ACCOLADESGAUCHES) {
                     int b = 1;
                     i++; // On mange '{'
                     while (b > 0 && getType() != LexicalePHP.TokenType.FIN) {
                         if (getType() == LexicalePHP.TokenType.ACCOLADESGAUCHES) b++;
                         else if (getType() == LexicalePHP.TokenType.ACCOLADESDROITES) b--;
                         i++;
                     }
                 } 
                 // Cas simple (point virgule seul)
                 else if (getType() == LexicalePHP.TokenType.POINTVERGULE) {
                     i++;
                 }
                 
                 C(); // On reprend l'analyse normalement
            }
            // 6. GESTION DES NOMS ETUDIANTS (Pour éviter erreur syntaxique si présents)
            else if (t == LexicalePHP.TokenType.NOM_ETUDIANT) {
                i++; // On les ignore simplement (comme un commentaire)
                C();
            }
            // 7. Reste ignoré
            else {
                i++; 
                C(); 
            }
        }
    }

    // ==========================================
    //      AFFECTATIONS & DECLARATIONS
    // ==========================================

    public void D() {
        if (getType() == LexicalePHP.TokenType.INT) accepter(LexicalePHP.TokenType.INT);
        else accepter(LexicalePHP.TokenType.DOUBLE);

        accepter(LexicalePHP.TokenType.VARIABLE); 
        accepter(LexicalePHP.TokenType.EGALE);       
        I(); 
        accepter(LexicalePHP.TokenType.POINTVERGULE);
    }

    public void E() {
        accepter(LexicalePHP.TokenType.VARIABLE); 
        F(); 
    }

    public void F() {
        if (getType() == LexicalePHP.TokenType.EGALE) {
            accepter(LexicalePHP.TokenType.EGALE);
            I(); 
        } 
        else if (getType() == LexicalePHP.TokenType.SUIVANT) {
            accepter(LexicalePHP.TokenType.SUIVANT);
        } 
        else if (getType() == LexicalePHP.TokenType.PRECEDANT) {
            accepter(LexicalePHP.TokenType.PRECEDANT);
        } 
        else {
            logs.append(">>> ERREUR : Affectation invalide.\n");
            R = true;
        }
        accepter(LexicalePHP.TokenType.POINTVERGULE);
    }

    public void I() {
        J(); 
        if (getType() == LexicalePHP.TokenType.PLUS || getType() == LexicalePHP.TokenType.MOINS || 
            getType() == LexicalePHP.TokenType.MULT || getType() == LexicalePHP.TokenType.DIV) {
            i++; 
            I(); 
        }
    }

    public void J() {
        if (getType() == LexicalePHP.TokenType.VARIABLE) {
            accepter(LexicalePHP.TokenType.VARIABLE);
        } else if (getType() == LexicalePHP.TokenType.NUM) {
            accepter(LexicalePHP.TokenType.NUM);
        } else {
            R = true;
            if (getType() != LexicalePHP.TokenType.FIN) i++;
        }
    }

    // ==========================================
    //      STRUCTURE IF / ELSE
    // ==========================================

    public void L() {
        accepter(LexicalePHP.TokenType.IF);
        accepter(LexicalePHP.TokenType.PARENTHESESGAUCHE);
        
        while(getType() != LexicalePHP.TokenType.PARENTHESESDROITES && getType() != LexicalePHP.TokenType.FIN) {
             i++;
        }

        accepter(LexicalePHP.TokenType.PARENTHESESDROITES);
        accepter(LexicalePHP.TokenType.ACCOLADESGAUCHES);
        
        C(); 
        
        accepter(LexicalePHP.TokenType.ACCOLADESDROITES);
        
        if (getType() == LexicalePHP.TokenType.ELSE) {
            accepter(LexicalePHP.TokenType.ELSE);
            if (getType() == LexicalePHP.TokenType.ACCOLADESGAUCHES) {
                accepter(LexicalePHP.TokenType.ACCOLADESGAUCHES);
                C(); 
                accepter(LexicalePHP.TokenType.ACCOLADESDROITES);
            } else if (getType() == LexicalePHP.TokenType.IF) {
                 L();
            }
        }
    }
}