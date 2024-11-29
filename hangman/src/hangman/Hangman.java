package hangman;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class Hangman {
    public static void main(String[] args) throws IOException {
        Scanner keyboard = new Scanner(System.in);
        
        // Kategorien definieren
        String[] categories = {"Kategorie1", "Kategorie2", "Kategorie3", "Kategorie4", "Kategorie5"};
        
        // Kategorie auswählen
        System.out.println("Willkommen zu Hangman!");
        System.out.println("Wähle eine Kategorie:");
        for (int i = 0; i < categories.length; i++) {
            System.out.println((i + 1) + ". " + categories[i]);
        }
        int categoryChoice = 0;
        while (categoryChoice < 1 || categoryChoice > categories.length) {
            System.out.print("Gib die Nummer der Kategorie ein: ");
            categoryChoice = keyboard.nextInt();
            keyboard.nextLine(); // Verbraucht die Zeilenumbruch-Eingabe
        }
        
        // Datei basierend auf der Kategorie auswählen
        String filePath = "/words/kategorie" + categoryChoice + ".txt";
        
        // Wörter aus der gewählten Datei laden
        InputStream inputStream = Hangman.class.getResourceAsStream(filePath);
        if (inputStream == null) {
            System.out.println("Fehler: Die Datei " + filePath + " wurde nicht gefunden!");
            return;
        }
        Scanner scanner = new Scanner(inputStream);
        List<String> words = new ArrayList<>();
        while (scanner.hasNext()) {
            words.add(scanner.nextLine());
        }
        scanner.close();
        
        // Spiel starten
        Random rand = new Random();
        int score = 0;  // Zähler für korrekt erratene Wörter
        
        while (true) {
            String word = words.get(rand.nextInt(words.size())).toLowerCase();
            List<Character> playerGuesses = new ArrayList<>();
            int wrongCount = 0;
            
            while (true) {
                printHangedMan(wrongCount);
                
                if (wrongCount >= 6) {
                    System.out.println("Du hast verloren!");
                    System.out.println("Das richtige Wort war: " + word);
                    System.out.println("Deine Punktzahl: " + score);
                    updateHighScores(score);
                    displayHighScores();
                    return;  // Spiel endet nach Niederlage
                }
                
                printWordState(word, playerGuesses);
                if (!getPlayerGuess(keyboard, word, playerGuesses)) {
                    wrongCount++;
                }
                
                if (printWordState(word, playerGuesses)) {
                    System.out.println("Du hast gewonnen!");
                    score++;  // Punktzahl erhöhen
                    break;  // Zum nächsten Wort
                }
            }
        }
    }

    private static void printHangedMan(Integer wrongCount) {
        System.out.println(" -------");
        System.out.println(" |     |");
        if (wrongCount >= 1) {
            System.out.println(" O");
        }
        
        if (wrongCount >= 2) {
            System.out.print("\\ ");
            if (wrongCount >= 3) {
                System.out.println("/");
            } else {
                System.out.println("");
            }
        }
        
        if (wrongCount >= 4) {
            System.out.println(" |");
        }
        
        if (wrongCount >= 5) {
            System.out.print("/ ");
            if (wrongCount >= 6) {
                System.out.println("\\");
            } else {
                System.out.println("");
            }
        }
        System.out.println("");
        System.out.println("");
    }

    private static boolean getPlayerGuess(Scanner keyboard, String word, List<Character> playerGuesses) {
        System.out.println("Bitte gebe einen Buchstaben ein:");
        String letterGuess = keyboard.nextLine().toLowerCase();
        playerGuesses.add(letterGuess.charAt(0));
        
        return word.contains(letterGuess);
    }

    private static boolean printWordState(String word, List<Character> playerGuesses) {
        int correctCount = 0;
        for (int i = 0; i < word.length(); i++) {
            if (playerGuesses.contains(word.charAt(i))) {
                System.out.print(word.charAt(i));
                correctCount++;
            } else {
                System.out.print("-");
            }
        }
        System.out.println();
        
        return (word.length() == correctCount);
    }

    private static void updateHighScores(int score) throws IOException {
        File file = new File("highscores.txt");
        List<Integer> scores = new ArrayList<>();
        
        if (file.exists()) {
            Scanner fileScanner = new Scanner(file);
            while (fileScanner.hasNextInt()) {
                scores.add(fileScanner.nextInt());
            }
            fileScanner.close();
        }
        
        scores.add(score);
        Collections.sort(scores, Collections.reverseOrder());
        
        PrintWriter writer = new PrintWriter(file);
        for (int i = 0; i < Math.min(scores.size(), 5); i++) {
            writer.println(scores.get(i));
        }
        writer.close();
    }

    private static void displayHighScores() throws FileNotFoundException {
        File file = new File("highscores.txt");
        if (!file.exists()) {
            System.out.println("Keine Einträge in der Bestenliste.");
            return;
        }
        
        System.out.println("Bestenliste:");
        Scanner fileScanner = new Scanner(file);
        int rank = 1;
        while (fileScanner.hasNextInt()) {
            System.out.println(rank + ". " + fileScanner.nextInt() + " Punkte");
            rank++;
        }
        fileScanner.close();
    }
}
