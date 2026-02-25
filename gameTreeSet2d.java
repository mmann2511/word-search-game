// Data Structures
import java.util.TreeSet;
import java.util.SortedSet;
import java.util.List;
import java.util.ArrayList;

// File Processing
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;


/**
 * Implements the WordSearchGame using a TreeSet for the lexicon.
 * and a 2D array for the game board.
 *
 * Words are searched on the board using depth-first search (DFS)
 * with backtracking, and lexicon operations use efficient TreeSet methods.
 *
 * @author Michael Mann
 * @version 02/14/2026
 */

public class GameTreeSet2D implements WordSearchGame {
   
   // Lexicon Library
   private TreeSet<String> library = new TreeSet<>();
   
   // Default Board
   private String[][] board = {
    // col  0    1    2    3
          {"E", "E", "C", "A"},  // row 0
          {"A", "L", "E", "P"},  // row 1
          {"H", "N", "B", "O"},  // row 2
          {"Q", "T", "T", "Y"}   // row 3
   };
  
   private int size = 4;
   
   // Visited Indexes
   private boolean[][] visited;                         
   
   /**
    * Loads the lexicon into a data structure for later use. 
    * 
    * @param fileName A string containing the name of the file to be opened.
    * @throws IllegalArgumentException if fileName is null
    * @throws IllegalArgumentException if fileName cannot be opened.
    */
   public void loadLexicon(String fileName) {
      if (fileName == null) {
         throw new IllegalArgumentException(
            "file name cannot be null");
      }
      
      // need file Object to pass in
      File inFile = new File(fileName);
      
      // clear library
      library.clear();
      
      try {
         Scanner reader = new Scanner(inFile);
      
         while (reader.hasNextLine()) {
            // Data Cleaning/Pre Processing
            String line = reader.nextLine().trim();
            
            if (!line.isEmpty()) {
               String[] parts = line.split(" ");
               
               String word = parts[0].toLowerCase();
               
               library.add(word);
            }
         }
         reader.close();
      } 
      catch (FileNotFoundException e) {
         throw new IllegalArgumentException(
            "File will not open");
      }    
   }
   
   /**
    * Determines if the given word is in the lexicon.
    * 
    * @param wordToCheck The word to validate
    * @return true if wordToCheck appears in lexicon, false otherwise.
    * @throws IllegalArgumentException if wordToCheck is null.
    * @throws IllegalStateException if loadLexicon has not been called.
    */
   public boolean isValidWord(String wordToCheck) {
      if (wordToCheck == null) {
         throw new IllegalArgumentException(
            "wordToCheck must not be null");
      }
      if (this.library.isEmpty()) {
         throw new IllegalStateException(
            "must loadLexicon()");
      }
      
      return this.library.contains(wordToCheck.toLowerCase());
   }
   
   
   /**
    * Determines if there is at least one word in the lexicon with the 
    * given prefix.
    * 
    * @param prefixToCheck The prefix to validate
    * @return true if prefixToCheck appears in lexicon, false otherwise.
    * @throws IllegalArgumentException if prefixToCheck is null.
    * @throws IllegalStateException if loadLexicon has not been called.
    */
   public boolean isValidPrefix(String prefixToCheck) {
      if (prefixToCheck == null) {
         throw new IllegalArgumentException(
            "prefixToCheck must not be null");
      }
      if (this.library.isEmpty()) {
         throw new IllegalStateException(
            "must loadLexicon()");
      }
      
      String prefix = prefixToCheck.toLowerCase();
      
      String token = this.library.ceiling(prefix);
      if (token == null) {
         return false;
      }
      
      return token.startsWith(prefix);
   }
   
   
    /**
    * Stores the incoming array of Strings in a data structure that will make
    * it convenient to find words.
    * 
    * @param letterArray This array of length N^2 stores the contents of the
    *     game board in row-major order. Thus, index 0 stores the contents of board
    *     position (0,0) and index length-1 stores the contents of board position
    *     (N-1,N-1). Note that the board must be square and that the strings inside
    *     may be longer than one character.
    * @throws IllegalArgumentException if letterArray is null, or is  not
    *     square.
    */
   public void setBoard(String[] letterArray) {
      if (letterArray == null || letterArray.length == 0) {
         throw new IllegalArgumentException(
            "Array must not be null");
      }
      
      //size 
      int n = (int) Math.sqrt(letterArray.length);
      
      if (n * n != letterArray.length) {
         throw new IllegalArgumentException(
            "Board must be square");
      }
      
      // Update this.size
      this.size = n;
      
      // 2d board creation
      String[][] newBoard = new String[this.size][this.size];
      
      for (int index = 0; index < letterArray.length; index++) {
         int row = index / this.size;
         int col = index % this.size;
         newBoard[row][col] = letterArray[index];
      }
      // Update this.board
      this.board = newBoard;
      
   
   }
   
   /**
    * Creates a String representation of the board, suitable for printing to
    * standard out. Note that this method can always be called since
    * implementing classes should have a default board.
    */
   public String getBoard() {
      StringBuilder sb = new StringBuilder();
      
      for (int row = 0; row < this.size; row++) {
         for (int col = 0; col < this.size; col++) {
            sb.append(board[row][col]).append(" ");
         }
         sb.append("\n");
      }
      return sb.toString();
   }
   
   
    
  /**
   * Computes the cummulative score for the scorable words in the given set.
   * To be scorable, a word must (1) have at least the minimum number of characters,
   * (2) be in the lexicon, and (3) be on the board. Each scorable word is
   * awarded one point for the minimum number of characters, and one point for 
   * each character beyond the minimum number.
   *
   * @param words The set of words that are to be scored.
   * @param minimumWordLength The minimum number of characters required per word
   * @return the cummulative score of all scorable words in the set
   * @throws IllegalArgumentException if minimumWordLength < 1
   * @throws IllegalStateException if loadLexicon has not been called.
   */ 
   public int getScoreForWords(SortedSet<String> words, int minimumWordLength) {
      if (minimumWordLength < 1) {
         throw new IllegalArgumentException(
            "minimumWordLength must be greater than 1");
      }
      if (this.library.isEmpty()) {
         throw new IllegalStateException(
            "loadLexicon() has not be called");
      }
     
      int score = 0;
      
      for (String word: words) {
         if (isValidWord(word)) {
            if (word.length() >= minimumWordLength) {
               if (!isOnBoard(word).isEmpty()) {
                  score += (word.length() - minimumWordLength) + 1;
               }
            }
         }
            
         
      }
      return score;
   }
   
   /**
    * Retrieves all scorable words on the game board, according to the stated game
    * rules.
    * 
    * @param minimumWordLength The minimum allowed length (i.e., number of
    *     characters) for any word found on the board.
    * @return java.util.SortedSet which contains all the words of minimum length
    *     found on the game board and in the lexicon.
    * @throws IllegalArgumentException if minimumWordLength < 1
    * @throws IllegalStateException if loadLexicon has not been called.
    */
   public SortedSet<String> getAllScoreableWords(int minimumWordLength) {
      if (minimumWordLength < 1) {
         throw new IllegalArgumentException(
            "minimumWordLength must be greater than 1");
      }
      if (this.library.isEmpty()) {
         throw new IllegalStateException(
            "loadLexicon() has not been called");
      }
      SortedSet<String> foundWords = new TreeSet<String>();
      for (String word: library) {
         if (word.length() >= minimumWordLength) {
            if (!isOnBoard(word).isEmpty()) {
               foundWords.add(word);
            }   
         }
      }
      return foundWords;
   }
   
       
   /**
    * Determines if the given word is in on the game board. If so, it returns
    * the path that makes up the word.
    * @param wordToCheck The word to validate
    * @return java.util.List containing java.lang.Integer objects with  the path
    *     that makes up the word on the game board. If word is not on the game
    *     board, return an empty list. Positions on the board are numbered from zero
    *     top to bottom, left to right (i.e., in row-major order). Thus, on an NxN
    *     board, the upper left position is numbered 0 and the lower right position
    *     is numbered N^2 - 1.
    * @throws IllegalArgumentException if wordToCheck is null.
    * @throws IllegalStateException if loadLexicon has not been called.
    */
   public List<Integer> isOnBoard(String wordToCheck) {
      if (wordToCheck == null) {
         throw new IllegalArgumentException(
            "Please provide a wordToCheck");
      }
      if (this.library.isEmpty()) {
         throw new IllegalStateException(
            "loadLexicon() has not be called");
      }
      
      // Debug from testing MATCH board
      wordToCheck = wordToCheck.toUpperCase();
       
      for (int row = 0; row < this.size; row++) {
         for (int col = 0; col < this.size; col++) {
            
            Position current = new Position(row, col);
            
            if (matches(current, wordToCheck, 0)) {
               visited = new boolean[this.size][this.size];
               List<Integer> path = new ArrayList<>();
               
               if (dfs(current, 0, wordToCheck, path)) {
                  return path;
               }
            }
         }
      }
      return new ArrayList<>();
   }
   
   
   
   /**
    * Performs depth-first search with backtracking to determine whether
    * the given word exists on the board starting from the current position.
    *
    * @param current the current board position
    * @param charIndex the index of the character being matched in the word
    * @param wordToCheck the word being searched
    * @param path the row-major path of positions forming the word
    * @return true if the word is found, false otherwise
    */
   private boolean dfs(Position current, int charIndex, String wordToCheck, List<Integer> path) {
   
   // Check Out of Bounds
      if (!isValid(current)) {
         return false;
      }
   
   // If already visited
      if (isVisited(current)) {
         return false;
      }
   
   
   // Does letter match?
      if (!matches(current, wordToCheck, charIndex)) {
         return false;
      }
   
   
   // If valid index, not visited yet, and letter matches wordToCheck //
   
   
   // Mark Visited true
      visit(current);
      
   // Add to path List
      path.add(rowMajorConvert(current));
      
      int nextIndex = charIndex + tileLength(current);
   
   // Have we finished the word yet?
      if (nextIndex == wordToCheck.length()) {
         return true;
      }
   
   // If not finished explore neighbors
   
      for (Position neighbor : getNeighbors(current)) {
         if (dfs(neighbor, nextIndex, wordToCheck, path)) {
            return true;
         }
      }        
            
   
   // If no neighbor worked backtrack now
      unvisit(current);
      path.remove(path.size() - 1);
      return false;
   }
   
   
   
   
   /////////////////////
   // Private Helpers //
   ////////////////////
   
   
   /**
    * Position class for readability
    *
    */
   private static class Position {
      int row;
      int col;
      
      public Position(int row, int col) {
         this.row = row;
         this.col = col;
      }
   }
   
    /**
    * Is this position valid in the search area?
    */
   private boolean isValid(Position p) {
      return p.row >= 0 &&
             p.row < this.size &&
             p.col >= 0 &&
             p.col < this.size;
   }
   
   /**
    * Does current position letter match?
    */
   private boolean matches(Position p, String word, int charIndex) {
     
      String tile = this.board[p.row][p.col];
     
      if (charIndex + tile.length() > word.length()) {
         return false;
      }
     
      return word.startsWith(tile, charIndex);
   }
   
   /**
    * Returns List of all possible neighbors
    */
   private List<Position> getNeighbors(Position current) {
      List<Position> neighbors = new ArrayList<>();
      
      for (int i = -1; i <= 1; i++) {
         for (int j = -1; j <= 1; j++) {
            if (i == 0 && j == 0) {
               continue;
            }
            
            Position neighbor = new Position(current.row + i, current.col + j);
            if (isValid(neighbor)) {
               neighbors.add(neighbor);
            }
         }
         
      }
      return neighbors;
   } 
   
   /**
    * Has this valid position been visited?
    */
   private boolean isVisited(Position p) {
      return visited[p.row][p.col];
   } 
   
   /**
    * Mark this valid position as having been visited.
    */
   private void visit(Position p) {
      visited[p.row][p.col] = true;
   }
   
   /**
    * Mark this position as unvisited for backtracking
    */
   private void unvisit(Position p) {
      visited[p.row][p.col] = false;
   }
   
   /**
    * Converts position (row, col) into row-major order
    * (0,0) -> index 0
    * (0,1) - > index 1
    */
   private int rowMajorConvert(Position p) {
      return (p.row * this.size) + p.col;
   } 
   
   /**
    * Returns the number of characters represented by the tile at this position.
    */
   private int tileLength(Position p) {
      return this.board[p.row][p.col].length();
   }

   
   
   
   
}
