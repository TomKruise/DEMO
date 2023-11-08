package com.tom.spark.hang;

import java.util.Scanner;

public class Hangman {
    private int game_id;

    private String wordToGuess;

    private int guessesLeft = 10;

    private String guessedLetters = "";

    private String hiddenWord = "";

    private char choice;

    private String result = "";

    private String input = "";

    private boolean gameOver = false;

    public static int number = 1;

    public Hangman() {
        this.game_id = number++;
    }

    public void initializeGame_collectWord(Scanner key) {
        System.out.println("-------------------------------");
        System.out.println("     Welcome to HANGMAN " + this.game_id);
        System.out.println("-------------------------------");
        System.out.println();
        System.out.println("OK Guessing Player ... turn around, while your friend enters the word to guess!\n\n");
//        System.out.println();
//        System.out.println();

        // Step 1: Get the word to guess from the other player
        wordToGuess = null;
        while (true) {
            System.out.print("Other player - Enter your word (up to 10 letters only, not case sensitive): ");
            wordToGuess = key.next().toUpperCase();

            if (null != wordToGuess) {
                if (wordToGuess.length() == 0 || wordToGuess.length() > 10) {
                    System.out.println("Invalid word. Please enter a word with up to 10 letters only.");
                } else {
                    boolean containsNonLetter = false;
                    for (int i = 0; i < wordToGuess.length(); i++) {
                        char c = wordToGuess.charAt(i);
                        if (!Character.isLetter(c)) {
                            containsNonLetter = true;
                            break;
                        }
                    }
                    if (containsNonLetter) {
                        System.out.println("Invalid word. Please enter a word containing only letters.");
                    } else {
                        break;
                    }
                }
            }
        }

        // Step 2: Print 20 blank lines to clear the screen
        for (int i = 0; i < 20; i++) {
            System.out.println();
        }

        // Step 3: Set up the game
        checkHiddenWord();
    }

    public void playGame(Scanner key) {
        if (!gameOver && guessesLeft > 0) {
            while (guessesLeft > 0 && !hiddenWord.equals(wordToGuess)) {
                System.out.print("GameID "+this.game_id+": Want to solve the puzzle? Enter \"Y\" to solve the puzzle, or \"N\" to guess a character: ");
                if (key.hasNext()) {
                    String strs = key.next();
                    if (null != strs && strs.length() >0) {
                        choice = strs.charAt(0);
                    } else {
                        choice = 'N';
                    }

                }
                validateChoice(key);
                if (choice == 'Y' || choice == 'y') {
                    choiceY(key);
                }
                play_a_guess(key);
            }
        }
    }

    public void play_a_guess(Scanner key) {
        if (!gameOver && guessesLeft > 0) {
            System.out.println("GameID "+this.game_id+": Word to date: " + hiddenWord + " (" + guessesLeft + " guess(es) left)");
//            validateChoice(key);
            System.out.print("GameID "+this.game_id+": Want to solve the puzzle? Enter \"Y\" to solve the puzzle, or \"N\"(you can only enter n in play_a_guess method) to guess a character: ");
            String strs = key.next();
            if (null != strs && strs.length() >0) {
                choice = strs.charAt(0);
            }
            while (!(choice == 'N' || choice == 'n')) {
                System.out.print("GameID "+this.game_id+": Want to solve the puzzle? Enter \"Y\" to solve the puzzle, or \"N\"(you can only enter n in play_a_guess method) to guess a character: ");
                strs = key.next();
                if (null != strs && strs.length() >0) {
                    choice = strs.charAt(0);
                }
            }
            if (choice=='N' || choice == 'n') {
                choiceN(key);
            }
        }
    }

    private void choiceN(Scanner key) {
        if(choice == 'N' || choice == 'n') {
            constructResult();

            System.out.println("GameID "+this.game_id+": Letters that could be tried: " + result);

            validateInput(key);

            // Add the guessed letter to the list of guessed letters
            guessedLetters += input;
            // Create a boolean variable to track if the guessed letter was found in the word
            boolean foundLetter = false;
            // Iterate over the letters in the word to guess and check if the guessed letter is present
            for (int i = 0; i < wordToGuess.length(); i++) {
                // Skip over any previously guessed letters that are already revealed
                if(hiddenWord.charAt(i) == input.charAt(0)) {
                    continue;
                }

                // If the guessed letter is found, reveal it in the hidden word
                if (input.charAt(0) == wordToGuess.charAt(i) && !foundLetter) {
                    hiddenWord = hiddenWord.substring(0, i) + input.charAt(0) + hiddenWord.substring(i + 1);
                    foundLetter = true;
                }

            }

            if (foundLetter) {
                System.out.println("GameID "+this.game_id+": --> great guess!");
            } else {
                System.out.println("GameID "+this.game_id+": --> Sorry, wrong guess!");
            }
            System.out.println();

            // Decrement the number of guesses left
            guessesLeft--;


            // Step 7: Print the result of the game
            if (hiddenWord.equals(wordToGuess)) {
                System.out.println("----------------------------------------------------");
                System.out.println("GameID "+this.game_id+": Congratulations!!");
                System.out.println("          You guessed the mystery word \"" + (wordToGuess) + "\" " + "in " + (10-guessesLeft) + " guesses!" );
                System.out.println("");
                System.out.println("GameID "+this.game_id+": Goodbye ....");
                System.out.println("--------------------------------------------------");
                gameOver=true;
                return;
            } else if (guessesLeft == 0 && !hiddenWord.equals(wordToGuess)){
                System.out.println("----------------------------------------------------");
                System.out.println("GameID "+this.game_id+": Sorry you didn't find the mystery word!");
                System.out.println("GameID "+this.game_id+": It was \"" + (wordToGuess) + "\"");
                System.out.println("");
                System.out.println("GameID "+this.game_id+": Goodbye ....");
                System.out.println("--------------------------------------------------");
                return;
            }

//            System.out.println("GameID "+this.game_id+": Word to date: " + hiddenWord + " (" + guessesLeft + " guess(es) left)");


//            System.out.print("Want to solve the puzzle? Enter \"Y\" to solve the puzzle, or \"N\" to guess a character: ");
//            choice = key.next().charAt(0);
//            validateChoice(key);
//
//            choiceY(key);
        }
    }

    private void validateInput(Scanner key) {
//        int k = 0;
        boolean validInput = false;

        //This begins a while loop that will continue until valid input is received.
        while (!validInput) {
            System.out.print("GameID "+this.game_id+": Which letter should I check for? ");
            //If this is the first time through the loop, the code will read a line of input from the scanner and convert it to uppercase. This is to clear any leftover input in the scanner buffer.
//            if(k==0) {
//                input = key.nextLine().toUpperCase();
//                k++;
//            }
            //This reads a line of input from the scanner and converts it to uppercase.
            String strs = key.next();
            if (strs != null && strs.length()>0) {
                input = strs.toUpperCase();
            }

            boolean existsInGuessedLetters = false;
            for (int i = 0; i < guessedLetters.length(); i++) {
                if (guessedLetters.charAt(i) == input.charAt(0)){
                    existsInGuessedLetters = true;
                    break;
                }
            }

            int letterNumInGuess = 0;
            int letterNumInHidden = 0;
            for (int i = 0; i < wordToGuess.length(); i++) {
                if (wordToGuess.charAt(i) == input.charAt(0)) {
                    letterNumInGuess++;
                }
            }

            for (int i = 0; i < hiddenWord.length(); i++) {
                if (hiddenWord.charAt(i) == input.charAt(0)) {
                    letterNumInHidden++;
                }
            }

            //If the input is not exactly one character long, an error message is printed to the console.
            //If the first character of the input is not a letter between A and Z (inclusive), an error message is printed to the console.
            if (existsInGuessedLetters && letterNumInGuess == letterNumInHidden) {
                System.out.println("--> Not a valid request - either not a letter or already guessed.");
            }
            else if (input.length() != 1 || (input.length() == 1 && !(input.charAt(0) >= 'A' && input.charAt(0)<='Z'))) {
                System.out.println("--> Not a valid request - either not a letter or already guessed.");
            } else {
                validInput = true;
            }
        }
    }

    private void constructResult() {
        // loop through each character from A to Z
        if (null != result && result.length()>0) {
            result = "";
        }
        for (char c = 'A'; c <= 'Z'; c++) {
            boolean found = false;
            // loop through each character in the wordToGuess
            int letterNumInGuess = 0;
            int letterNumInHidden = 0;
            for(int i = 0; i < wordToGuess.length();i++) {
                if(wordToGuess.charAt(i) == c) {
                    // check if the character in hiddenWord matches the character in wordToGuess
                    if(hiddenWord.charAt(i) == c) {
                        found = true;
                        break;
                    } else {
                        found = false;
                    }
                }
            }

            for (int i = 0; i < wordToGuess.length(); i++) {
                if (wordToGuess.charAt(i) == c) {
                    letterNumInGuess++;
                }
            }

            for (int i = 0; i < hiddenWord.length(); i++) {
                if (hiddenWord.charAt(i) == c) {
                    letterNumInHidden++;
                }
            }



            // add a "*" to the result string if the character is not found, otherwise add the character itself
            if(found) {
                if (letterNumInGuess==letterNumInHidden) {
                    result += "*";
                } else {
                    result += c;
                }
            }
            else {
                boolean gussed = false;
                for (int i = 0; i < guessedLetters.length(); i++) {
                    if (c == guessedLetters.charAt(i)) {
                        gussed = true;
                    }
                }
                if (gussed && !found) {
                    result += "*";
                } else {
                    result += c;
                }

            }
        }
    }

    private void choiceY(Scanner key) {
        if(choice == 'Y' ||  choice == 'y') {
            // Decrement the number of guesses left
            guessesLeft--;
            System.out.print("Enter the complete word:");
            String word = key.next().toUpperCase();
            if(word.compareTo(wordToGuess) == 0) {
                System.out.println("----------------------------------------------------");
                System.out.println("GameID "+this.game_id+": Congratulations!!");
                System.out.println("          You guessed the mystery word \"" + (wordToGuess) + "\" " + "in " + (10-guessesLeft) + " guesses!" );
                System.out.println("");
                System.out.println("GameID "+this.game_id+": Goodbye ....");
                System.out.println("--------------------------------------------------");
                hiddenWord = word;
                gameOver = true;
            }else if (!word.equals(wordToGuess)){
                System.out.println("----------------------------------------------------");
                System.out.println("GameID "+this.game_id+": Sorry you didn't find the mystery word!");
                System.out.println("GameID "+this.game_id+": It was \"" + (wordToGuess) + "\"");
                System.out.println("");
                System.out.println("GameID "+this.game_id+": Goodbye ....");
                System.out.println("--------------------------------------------------");
                guessesLeft=0;
            }
            return;
        }
    }

    private void validateChoice(Scanner key) {
        // Validate the player's choice, and ask again if the input is not valid
        while(choice != 'Y' && choice  != 'y' && choice != 'N' && choice  != 'n') {
            System.out.print("GameID "+this.game_id+": Want to solve the puzzle? Enter \"Y\" to solve the puzzle, or \"N\" to guess a character: ");
            String strs = key.next();
            if (null != strs && strs.length() >0) {
                choice = strs.charAt(0);
            }
        }
    }

    private void checkHiddenWord() {
        // Loop through the characters of the word to guess.
        // If a character is a letter, replace it with an asterisk in the hidden word.
        // Otherwise, keep the original character in the hidden word.
        // The hidden word starts out empty and gets built up one character at a time.
        for (int i = 0; i < wordToGuess.length(); i++) {
            if (wordToGuess.charAt(i) >= 'A' && wordToGuess.charAt(i) <= 'Z') {
                hiddenWord += "*";
            } else {
                hiddenWord += wordToGuess.charAt(i);
            }
        }
    }
}
