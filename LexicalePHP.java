import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class LexicalePHP { 
    
    public enum TokenType {
       OPEN_TAG, CLOSE_TAG,
       CASE, IF, ELSE, WHILE, FOR, RETURN, BREAK, CONTINUE, SWITCH, DEFAULT, DO, FOREACH,

       // Types et Mots-clés spéciaux
       INT, DOUBLE, VOID,
       NOM_ETUDIANT, // <--- AJOUT POUR LE PROF (Consigne 61)

       PARENTHESESDROITES, PARENTHESESGAUCHE,
       ACCOLADESDROITES, ACCOLADESGAUCHES,
       CROCHETSDROITES, CROCHETSGAUCHE,

       POINTVERGULE, DEUXPOINT, VIRGULE, POINT, 
       
       IDENTIFIANT,  
       VARIABLE,     
       NUM,      

       EGALE,       
       SUIVANT, PRECEDANT, 
       SUP, INF, EGALEEGALE, NONEGALE, SUPEGALE, INFEGALE, 
       PLUS, MOINS, MULT, DIV, MOD, 
       FLECHE, 
       
       PUBLIC, CLASS, STATIC, PRIVATE, PROTECTED, FUNCTION, 
       ECHO, PRINT, NEW, NULL, THIS, EXTENDS, IMPLEMENTS,
       
       FIN,
       PHP,
       ERREUR 
    }
    
    public static class lexeme {
        public final TokenType type;
        public final String lexeme;
        public final int position; 

        public lexeme(TokenType type, String lexeme, int position) {
            this.type = type;
            this.lexeme = lexeme;
            this.position = position;
        }

        @Override
        public String toString() {
            return "< "+type+" , '"+lexeme+"' , position: "+position +">";
        }
    }
   
    private static final Map<String, TokenType> KEYWORDS = new HashMap<>();
    static {
        KEYWORDS.put("if", TokenType.IF);
        KEYWORDS.put("else", TokenType.ELSE);
        KEYWORDS.put("while", TokenType.WHILE);
        KEYWORDS.put("for", TokenType.FOR);
        KEYWORDS.put("return", TokenType.RETURN);
        KEYWORDS.put("break", TokenType.BREAK);
        KEYWORDS.put("continue", TokenType.CONTINUE);
        KEYWORDS.put("switch", TokenType.SWITCH);
        KEYWORDS.put("case", TokenType.CASE);
        KEYWORDS.put("default", TokenType.DEFAULT);
        KEYWORDS.put("do", TokenType.DO);
        KEYWORDS.put("foreach", TokenType.FOREACH);
        
        KEYWORDS.put("int", TokenType.INT);
        KEYWORDS.put("double", TokenType.DOUBLE);
        KEYWORDS.put("void", TokenType.VOID);

        // --- AJOUT OBLIGATOIRE POUR LE PROJET ---
        KEYWORDS.put("salmani", TokenType.NOM_ETUDIANT);
        KEYWORDS.put("fakhreddine", TokenType.NOM_ETUDIANT);
        
        // ----------------------------------------

        KEYWORDS.put("public", TokenType.PUBLIC);
        KEYWORDS.put("class", TokenType.CLASS);
        KEYWORDS.put("static", TokenType.STATIC);
        KEYWORDS.put("private", TokenType.PRIVATE);
        KEYWORDS.put("protected", TokenType.PROTECTED);
        KEYWORDS.put("function", TokenType.FUNCTION); 
        KEYWORDS.put("echo", TokenType.ECHO);        
        KEYWORDS.put("print", TokenType.PRINT);
        KEYWORDS.put("new", TokenType.NEW);
        KEYWORDS.put("null", TokenType.NULL);
        KEYWORDS.put("extends", TokenType.EXTENDS);
        KEYWORDS.put("implements", TokenType.IMPLEMENTS);
        KEYWORDS.put("php", TokenType.PHP);
        KEYWORDS.put("this", TokenType.THIS); 
    }

    private final String chaine;                
    private int i = 0;              
    private final List<lexeme> lexemes = new ArrayList<>();
    private  final char CARACTERE_FIN = '\0'; 
    String mot="";
    String nombre="";
    int position;

    public LexicalePHP(String chaine) { 
        this.chaine = chaine + CARACTERE_FIN;
    }

    public boolean estUneLettre(char c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');
    }

    public boolean estUnChiffre(char c) {
        return (c >= '0' && c <= '9');
    }

    public boolean estUnCaractereAlphaNum(char c) {
        return estUneLettre(c) || estUnChiffre(c) || c == '_';
    }

     public void scanlexicale(){
        while (chaine.charAt(i)!= CARACTERE_FIN) {

            if (chaine.charAt(i)==' ' || chaine.charAt(i)=='\n' || chaine.charAt(i)=='\t' || chaine.charAt(i)=='\r'){
                i++;
            }
            else if (chaine.charAt(i) == '?') {
                if (chaine.charAt(i + 1) == '>') {
                    lexemes.add(new lexeme(TokenType.CLOSE_TAG, "?>", i));
                    i += 2;
                } else {
                    lexemes.add(new lexeme(TokenType.ERREUR, "?", i));
                    i++;
                }
            }
            else if (chaine.charAt(i) == '<') {
                if (chaine.charAt(i+1) == '?' && chaine.charAt(i+2) == 'p' && chaine.charAt(i+3) == 'h' && chaine.charAt(i+4) == 'p') {
                      lexemes.add(new lexeme(TokenType.OPEN_TAG, "<?php", i));
                      i += 5; 
                } else {
                    lexemes.add(new lexeme(TokenType.INF, "<", i));
                    i++;
                }
            }
            else if (chaine.charAt(i)=='('){ lexemes.add(new lexeme (TokenType.PARENTHESESGAUCHE,"(",i)); i++; }
            else if (chaine.charAt(i)==')'){ lexemes.add(new lexeme (TokenType.PARENTHESESDROITES,")",i)); i++; }
            else if (chaine.charAt(i)=='{'){ lexemes.add(new lexeme (TokenType.ACCOLADESGAUCHES,"{",i)); i++; }
            else if (chaine.charAt(i)=='}'){ lexemes.add(new lexeme (TokenType.ACCOLADESDROITES,"}",i)); i++; }
            else if (chaine.charAt(i)=='['){ lexemes.add(new lexeme (TokenType.CROCHETSGAUCHE,"[",i)); i++; }
            else if (chaine.charAt(i)==']'){ lexemes.add(new lexeme (TokenType.CROCHETSDROITES,"]",i)); i++; }
            else if (chaine.charAt(i)==';'){ lexemes.add(new lexeme (TokenType.POINTVERGULE,";",i)); i++; }
            else if (chaine.charAt(i)==','){ lexemes.add(new lexeme (TokenType.VIRGULE,",",i)); i++; }
            else if (chaine.charAt(i)==':'){ lexemes.add(new lexeme (TokenType.DEUXPOINT,":",i)); i++; }
            else if (chaine.charAt(i)=='.'){ lexemes.add(new lexeme (TokenType.POINT,".",i)); i++; }
            
            else if (chaine.charAt(i)=='='){
                if (chaine.charAt(i+1)=='='){ lexemes.add(new lexeme (TokenType.EGALEEGALE,"==",i)); i+=2; }
                else { lexemes.add(new lexeme (TokenType.EGALE,"=",i)); i++; }
            }else if (chaine.charAt(i)=='>'){
                if (chaine.charAt(i+1)=='='){ lexemes.add(new lexeme (TokenType.SUPEGALE,">=",i)); i+=2; }
                else { lexemes.add(new lexeme (TokenType.SUP,">",i)); i++; }
            }else if (chaine.charAt(i)=='<'){
                if (chaine.charAt(i+1)=='='){ lexemes.add(new lexeme (TokenType.INFEGALE,"<=",i)); i+=2; }
                else { lexemes.add(new lexeme (TokenType.INF,"<",i)); i++; }
            }else if (chaine.charAt(i)=='!'){
                if (chaine.charAt(i+1)=='='){ lexemes.add(new lexeme (TokenType.NONEGALE,"!=",i)); i+=2; }
                else { lexemes.add(new lexeme (TokenType.ERREUR,"!",i)); i++; }
            }else if (chaine.charAt(i)=='+'){
                if (chaine.charAt(i+1)=='+'){ lexemes.add(new lexeme (TokenType.SUIVANT,"++",i)); i+=2; }
                else { lexemes.add(new lexeme (TokenType.PLUS,"+",i)); i++; }
            }else if (chaine.charAt(i)=='-'){
                if (chaine.charAt(i+1)=='-'){ lexemes.add(new lexeme (TokenType.PRECEDANT,"--",i)); i+=2; }
                else if (chaine.charAt(i+1)=='>'){ lexemes.add(new lexeme (TokenType.FLECHE,"->",i)); i+=2; }
                else { lexemes.add(new lexeme (TokenType.MOINS,"-",i)); i++; }
            }else if (chaine.charAt(i)=='*'){ lexemes.add(new lexeme (TokenType.MULT,"*",i)); i++; }
            else if (chaine.charAt(i)=='%'){ lexemes.add(new lexeme (TokenType.MOD,"%",i)); i++; }
            
            // GESTION DES COMMENTAIRES (Consigne 59)
            else if (chaine.charAt(i) == '/') {
                if (i + 1 < chaine.length() && chaine.charAt(i + 1) == '/') {
                    while (i < chaine.length() && chaine.charAt(i) != '\n' && chaine.charAt(i) != CARACTERE_FIN) {
                        i++;
                    }
                } else {
                    lexemes.add(new lexeme(TokenType.DIV, "/", i));
                    i++;
                }
            }
            
            // VARIABLES ($)
            else if (chaine.charAt(i) == '$') {
                mot = "$";
                position = i;
                i++; 
                while(estUnCaractereAlphaNum(chaine.charAt(i))) {
                    mot = mot + chaine.charAt(i);
                    i++;
                    if(chaine.charAt(i)== CARACTERE_FIN) break;
                }
                lexemes.add(new lexeme(TokenType.VARIABLE, mot, position));
            }
            // IDENTIFIANTS ET MOTS CLES
            else if(estUneLettre(chaine.charAt(i)) || chaine.charAt(i) == '_'){
                mot="";
                position=i;
                while(estUnCaractereAlphaNum(chaine.charAt(i))){
                    mot= mot+ chaine.charAt(i);
                    i++;
                    if(chaine.charAt(i)== CARACTERE_FIN) break;
                }
                // Vérifie si le mot est dans la map (dont salmani, youlidas...)
                if (KEYWORDS.containsKey(mot.toLowerCase())){
                    lexemes.add(new lexeme (KEYWORDS.get(mot.toLowerCase()),mot,position));
                }else{
                    lexemes.add(new lexeme (TokenType.IDENTIFIANT,mot,position));
                }
            }
            // NOMBRES (ENTIERS ET FLOTTANTS)
            else if(estUnChiffre(chaine.charAt(i))){
                nombre="";
                position=i;
                while(estUnChiffre(chaine.charAt(i))){
                    nombre=nombre+chaine.charAt(i);
                    i++;
                    if(chaine.charAt(i)== CARACTERE_FIN) break;
                }
                if (chaine.charAt(i) == '.') {
                    nombre = nombre + ".";
                    i++;
                    while(estUnChiffre(chaine.charAt(i))){
                        nombre=nombre+chaine.charAt(i);
                        i++;
                        if(chaine.charAt(i)== CARACTERE_FIN) break;
                    }
                }
                lexemes.add(new lexeme(TokenType.NUM, nombre, position));
            }
            else{
                lexemes.add(new lexeme (TokenType.ERREUR,"erreur : caractère non défini",i));
                i++;
            }
        }
        lexemes.add(new lexeme(TokenType.FIN, "", i));
    }
    
    public List<lexeme> getTokens(){ return lexemes; }
}