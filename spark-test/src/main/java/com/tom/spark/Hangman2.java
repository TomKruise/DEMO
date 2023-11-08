package com.tom.spark;

import java.util.Arrays;
import java.util.Scanner;

public class Hangman2 {

    public static void main(String[] args) {
        //intro message
        System.out.println("-------------------------------");
        System.out.println(" Welcome to HANGMAN");
        System.out.println("-------------------------------");
        System.out.println();
        System.out.println("OK Guessing Player ... turn around, while your friend enters the word to guess!");
        System.out.println();
        System.out.println();
        Scanner scanner = new Scanner(System.in);

        // Step 1: Get the word to guess from the other player

//        String wordToGuess = "";
        char[] wordToGuessCharArray;
        int wordToGuessCharArrayLength;

        while (true) {
            System.out.print("Other player - Enter your word (up to 10 letters only, not case sensitive): ");
            wordToGuessCharArray = scanner.nextLine().toUpperCase().toCharArray();
            wordToGuessCharArrayLength = wordToGuessCharArray.length;

            if (wordToGuessCharArrayLength == 0 || wordToGuessCharArrayLength > 10) {
                System.out.println("Invalid word. Please enter a word with up to 10 letters only.");
            } else {
                boolean containsNonLetter = false;
                for (int i = 0; i < wordToGuessCharArrayLength; i++) {
                    char c = wordToGuessCharArray[i];
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

        // Step 2: Print 20 blank lines to clear the screen
        for (int i = 0; i < 20; i++) {
            System.out.println();
        }

        // Step 3: Set up the game
        int guessesLeft = 10;
//        String guessedLetters = "";
        char[] guessedLettersCharArray = null;
//        String hiddenWord = "";
        char[] hiddenWordCharArray = new char[wordToGuessCharArrayLength];
        for (int i = 0; i < hiddenWordCharArray.length; i++) {
            hiddenWordCharArray[i]='*';
        }
//        hiddenWord = String.valueOf(hiddenWordCharArray);
        // Loop through the characters of the word to guess.
        // If a character is a letter, replace it with an asterisk in the hidden word.
        // Otherwise, keep the original character in the hidden word.
        // The hidden word starts out empty and gets built up one character at a time.
        for (int i = 0; i < wordToGuessCharArrayLength; i++) {
            if (wordToGuessCharArray[i] >= 'A' && wordToGuessCharArray[i] <= 'Z') {
                hiddenWordCharArray[i] = '*';
            } else {
                hiddenWordCharArray[i] = wordToGuessCharArray[i];
            }
        }

        System.out.print("Want to solve the puzzle? Enter \"Y\" to solve the puzzle, or \"N\" to guess a character: ");
        char choice = scanner.next().charAt(0);
        // Validate the player's choice, and ask again if the input is not valid
        while(choice != 'Y' && choice  != 'y' && choice != 'N' && choice  != 'n') {
            System.out.print("Want to solve the puzzle? Enter \"Y\" to solve the puzzle, or \"N\" to guess a character: ");
            choice = scanner.next().charAt(0);
        }

        if(choice == 'Y' ||  choice == 'y') {
            System.out.print("Enter the word:");
            char[] wordCharArray = scanner.next().toUpperCase().toCharArray();
            if(Arrays.equals(wordCharArray,wordToGuessCharArray)) {
                System.out.println("----------------------------------------------------");
                System.out.println("Congratulations!!");
                System.out.println("You guessed the mystery word \"" + (String.valueOf(wordToGuessCharArray)) + "\" " + "in " + (guessesLeft) + " guesses!" );
                System.out.println("");
                System.out.println("Goodbye ....");
                System.out.println("--------------------------------------------------");
            }else {
                System.out.println("----------------------------------------------------");
                System.out.println("Sorry you didn't find the mystery word!");
                System.out.println("It was \"" + (String.valueOf(wordToGuessCharArray)) + "\"");
                System.out.println("");
                System.out.println("Goodbye ....");
                System.out.println("--------------------------------------------------");
            }
            return;
        }

        // Step 4 to 6: Play the game
        if(choice == 'N' || choice == 'n') {
            System.out.println("Word to date: " + String.valueOf(hiddenWordCharArray) + " (" + guessesLeft + " guess(es) left)");
            while (guessesLeft > 0 && !Arrays.equals(hiddenWordCharArray,wordToGuessCharArray)) {
//                String result = "";//ABCDEFGHIJK*MNOPQRS*UVWXYZ
                char[] resultCharArray = null;
                // loop through each character from A to Z
                for (char c = 'A'; c <= 'Z'; c++) {
                    boolean found = false;
                    // loop through each character in the wordToGuess
                    int letterNumInGuess = 0;
                    int letterNumInHidden = 0;
                    for(int i = 0; i < wordToGuessCharArrayLength;i++) {
                        if(wordToGuessCharArray[i] == c) {
                            // check if the character in hiddenWord matches the character in wordToGuess
                            if(hiddenWordCharArray[i] == c) {
                                found = true;
                                break;
                            } else {
                                found = false;
                            }
                        }
                    }

                    for (int i = 0; i < wordToGuessCharArrayLength; i++) {
                        if (wordToGuessCharArray[i] == c) {
                            letterNumInGuess++;
                        }
                    }

                    for (int i = 0; i < hiddenWordCharArray.length; i++) {
                        if (hiddenWordCharArray[i] == c) {
                            letterNumInHidden++;
                        }
                    }



                    // add a "*" to the result string if the character is not found, otherwise add the character itself
                    if(found) {
                        if (letterNumInGuess==letterNumInHidden) {
                            if (null == resultCharArray) {
                                resultCharArray = new char[] {'*'};
                            } else {
                                char[] temp = new char[resultCharArray.length + 1];
                                for (int i = 0; i < resultCharArray.length; i++) {
                                    temp[i] = resultCharArray[i];
                                }
                                temp[resultCharArray.length] = '*';
                                resultCharArray = temp;
                            }
                        } else {
                            if (null == resultCharArray) {
                                resultCharArray = new char[] {c};
                            } else {
                                char[] temp = new char[resultCharArray.length + 1];
                                for (int i = 0; i < resultCharArray.length; i++) {
                                    temp[i] = resultCharArray[i];
                                }
                                temp[resultCharArray.length] = c;
                                resultCharArray = temp;
                            }
                        }
                    }
                    else {
                        boolean gussed = false;
                        if (guessedLettersCharArray != null) {
                            for (int i = 0; i < guessedLettersCharArray.length; i++) {
                                if (c == guessedLettersCharArray[i]) {
                                    gussed = true;
                                }
                            }
                        }

                        if (gussed && !found) {
                            if (null == resultCharArray) {
                                resultCharArray = new char[] {'*'};
                            } else {
                                char[] temp = new char[resultCharArray.length + 1];
                                for (int i = 0; i < resultCharArray.length; i++) {
                                    temp[i] = resultCharArray[i];
                                }
                                temp[resultCharArray.length] = '*';
                                resultCharArray = temp;
                            }
                        } else {
                            if (null == resultCharArray) {
                                resultCharArray = new char[] {c};
                            } else {
                                char[] temp = new char[resultCharArray.length + 1];
                                for (int i = 0; i < resultCharArray.length; i++) {
                                    temp[i] = resultCharArray[i];
                                }
                                temp[resultCharArray.length] = c;
                                resultCharArray = temp;
                            }
                        }

                    }
                }

                System.out.println("Letters that could be tried: " + String.valueOf(resultCharArray));

//                String input = "";
                char[] inputCharArray = null;
                int k = 0;
                boolean validInput = false;

                //This begins a while loop that will continue until valid input is received.
                while (!validInput) {
                    System.out.print("Which letter should I check for? ");
                    //If this is the first time through the loop, the code will read a line of input from the scanner and convert it to uppercase. This is to clear any leftover input in the scanner buffer.
                    if(k==0) {
                        inputCharArray = scanner.nextLine().toUpperCase().toCharArray();
                        k++;
                    }
                    //This reads a line of input from the scanner and converts it to uppercase.
                    inputCharArray = scanner.nextLine().toUpperCase().toCharArray();
                    System.out.println();
                    boolean existsInGuessedLetters = false;
                    if (guessedLettersCharArray != null) {
                        for (int i = 0; i < guessedLettersCharArray.length; i++) {
                            if (guessedLettersCharArray[i] == inputCharArray[0]){
                                existsInGuessedLetters = true;
                                break;
                            }
                        }
                    }

                    int letterNumInGuess = 0;
                    int letterNumInHidden = 0;
                    for (int i = 0; i < wordToGuessCharArrayLength; i++) {
                        if (wordToGuessCharArray[i] == inputCharArray[0]) {
                            letterNumInGuess++;
                        }
                    }

                    for (int i = 0; i < hiddenWordCharArray.length; i++) {
                        if (hiddenWordCharArray[i] == inputCharArray[0]) {
                            letterNumInHidden++;
                        }
                    }

                    //If the input is not exactly one character long, an error message is printed to the console.
                    //If the first character of the input is not a letter between A and Z (inclusive), an error message is printed to the console.
                    if (existsInGuessedLetters && letterNumInGuess == letterNumInHidden) {
                        System.out.println("--> Not a valid request - either not a letter or already guessed.");
                    }
                    else if (inputCharArray.length != 1 || (inputCharArray.length == 1 && !(inputCharArray[0] >= 'A' && inputCharArray[0]<='Z'))) {
                        System.out.println("--> Not a valid request - either not a letter or already guessed.");
                    } else {
                        validInput = true;
                    }
                }

                // Add the guessed letter to the list of guessed letters
                if (null == guessedLettersCharArray) {
                    guessedLettersCharArray = new char[] {inputCharArray[0]};
                } else {
                    char[] temp = new char[guessedLettersCharArray.length + 1];
                    for (int i = 0; i < guessedLettersCharArray.length; i++) {
                        temp[i]=guessedLettersCharArray[i];
                    }
                    temp[guessedLettersCharArray.length] = inputCharArray[0];
                    guessedLettersCharArray = temp;

                }
                // Create a boolean variable to track if the guessed letter was found in the word
                boolean foundLetter = false;
                // Iterate over the letters in the word to guess and check if the guessed letter is present
                for (int i = 0; i < wordToGuessCharArrayLength; i++) {
                    // Skip over any previously guessed letters that are already revealed
                    if(hiddenWordCharArray[i] == inputCharArray[0]) {
                        continue;
                    }

                    // If the guessed letter is found, reveal it in the hidden word
                    if (inputCharArray[0] == wordToGuessCharArray[i] && !foundLetter) {
//                        hiddenWord = hiddenWord.substring(0, i) + inputCharArray[0] + hiddenWord.substring(i + 1);
//                        hiddenWordCharArray = hiddenWord.toCharArray();
                        hiddenWordCharArray[i] = inputCharArray[0];
                        foundLetter = true;
                    }

                }
                // Decrement the number of guesses left
                guessesLeft--;
//                if (!foundLetter) {
//
//                }

                // Step 7: Print the result of the game
                if (Arrays.equals(hiddenWordCharArray,wordToGuessCharArray)) {
                    System.out.println("----------------------------------------------------");
                    System.out.println("Congratulations!!");
                    System.out.println("You guessed the mystery word \"" + (String.valueOf(wordToGuessCharArray)) + "\" " + "in " + (guessesLeft) + " guesses!" );
                    System.out.println("");
                    System.out.println("Goodbye ....");
                    System.out.println("--------------------------------------------------");
                    return;
                }

                System.out.println("Word to date: " + String.valueOf(hiddenWordCharArray) + " (" + guessesLeft + " guess(es) left)");

                if(guessesLeft == 0) {
                    break;
                }


                System.out.print("Want to solve the puzzle? Enter \"Y\" to solve the puzzle, or \"N\" to guess a character: ");
                choice = scanner.next().charAt(0);
                while(choice != 'Y' && choice  != 'y' && choice != 'N' && choice  != 'n') {
                    System.out.print("Want to solve the puzzle? Enter \"Y\" to solve the puzzle, or \"N\" to guess a character: ");
                    choice = scanner.next().charAt(0);
                }
                if(choice == 'Y' ||  choice == 'y') {
                    System.out.print("Enter the word:");
                    String word = scanner.next().toUpperCase();
                    if(word.compareTo(String.valueOf(wordToGuessCharArray)) == 0) {
                        System.out.println("----------------------------------------------------");
                        System.out.println("Congratulations!!");
                        System.out.println("You guessed the mystery word \"" + (String.valueOf(wordToGuessCharArray)) + "\" " + "in " + (guessesLeft) + " guesses!" );
                        System.out.println("");
                        System.out.println("Goodbye ....");
                        System.out.println("--------------------------------------------------");
                    }else {
                        System.out.println("----------------------------------------------------");
                        System.out.println("");
                        System.out.println("Sorry you didn't find the mystery word!");
                        System.out.println("It was \"" + (String.valueOf(wordToGuessCharArray)) + "\"");
                        System.out.println("Goodbye ....");
                        System.out.println("--------------------------------------------------");
                    }
                    return;
                }
            }



        }


        // Step 7: Print the result of the game
        if (!Arrays.equals(hiddenWordCharArray,wordToGuessCharArray)) {
            System.out.println("----------------------------------------------------");
            System.out.println("");
            System.out.println("Sorry you didn't find the mystery word!");
            System.out.println("It was \"" + (String.valueOf(wordToGuessCharArray)) + "\"");
            System.out.println("Goodbye ....");
            System.out.println("--------------------------------------------------");
        }

    }
}