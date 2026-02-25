# Word Search Game

A Java implementation of a Boggle-style word search game engine. The engine finds all valid words on a square board using **depth-first search with backtracking**, supporting large lexicons and multiple board sizes.

---

## How It Works

The game is played on an NxN board where each position contains one or more letters. Words are formed by joining adjacent positions (horizontally, vertically, or diagonally) without reusing any position. Valid words are checked against a loaded lexicon, and only words meeting a minimum length requirement are scored.

### Scoring
Each valid word of length K with minimum length M is worth **1 + (K - M)** points.

---

## Features

- Load any lexicon from a text file
- Set custom NxN boards of any size
- Find all scorable words on the board using DFS with backtracking
- Check if a specific word exists on the board and return its position path
- Score a set of words
- Efficient prefix checking to prune the DFS search tree early

---

## Example

```java
WordSearchGame game = WordSearchGameFactory.createGame();
game.loadLexicon("wordfiles/words.txt");
game.setBoard(new String[]{"E", "E", "C", "A", "A", "L", "E", "P", "H",
                            "N", "B", "O", "Q", "T", "T", "Y"});

// Check if a word is on the board
game.isOnBoard("LENT");   // Returns [5, 6, 9, 14]
game.isOnBoard("POPE");   // Returns []

// Get all scorable words of length 6 or more
game.getAllScorableWords(6); // [ALEPOT, BENTHAL, PELEAN, TOECAP]
```

---

## Classes

### `WordSearchGame` (Interface)
Defines the full game engine contract including lexicon loading, board setup, and all game play methods.

### `WordSearchGameFactory`
Factory class that creates and returns an instance of the game engine.

### Game Engine (implements `WordSearchGame`)
Core implementation featuring:
- **DFS with backtracking** to traverse the board and find valid words
- **Efficient lexicon storage** using a `TreeSet` for fast `isValidWord` and `isValidPrefix` lookups
- Prefix pruning to avoid exploring paths that can't lead to valid words

---

## Provided Lexicons

| File | Words |
|---|---|
| `CSW12.txt` | 270,163 (international Scrabble) |
| `OWL.txt` | 167,964 (North American Scrabble) |
| `words.txt` | 234,371 (Unix dictionary) |
| `words_medium.txt` | 172,823 |
| `words_small.txt` | 19,912 |

---

## Tech Stack
- Java
- Depth-First Search with Backtracking
- `java.util.TreeSet` for lexicon storage
- `java.util.SortedSet`, `java.util.List`

---
