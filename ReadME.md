# Type Test

A simple, non-blocking console typing game written in Java. Pick a difficulty, wait out a 3-2-1 countdown, then type each word you are shown, letter by letter, before the timer runs out. Finish as many words as you can.

**Group 23** — Lucas Thompson, Krish, Josiah Samuel

---

## How to play

1. From the main menu, choose **Start** (or **Quit**).
2. Pick a difficulty: **Easy**, **Medium**, or **Hard**.
3. After the countdown, a target word appears. Type its letters in order.
   - A correct letter advances you through the word.
   - A wrong letter does not advance you; type the correct letter to continue.
   - Finish the whole word to move on to the next one. Partial words earn nothing.
4. When the timer reaches zero the round ends and your results are shown. Press **Enter** to return to the main menu.

### Controls

| Key | Action |
|---|---|
| Up / Down | Move the menu selection |
| Enter | Confirm a menu choice / leave the results screen |
| Esc | Go back one screen (quits from the main menu) |
| Letters | Type the target word (input is lowercased) |
| Backspace | Remove the last typed letter |

---

## Building and running

**Requirements:** JDK 23 (the Gradle toolchain is set to Java 23). The Gradle wrapper is included, so no separate Gradle install is needed.

```bash
./gradlew run            # build and launch the game
./gradlew test           # run the tests
./gradlew format         # auto-format the source (Spotless)
./gradlew check-format   # verify formatting (also run in CI)
```

On Windows use `gradlew.bat`. The `run` task launches the game in its own console window, because the game needs a real terminal for raw keyboard input. On Linux/macOS it finds the terminal of the parent process. `start-debug.ps1` starts the game with the JVM debugger attached on port 5005.

**Dependencies** (declared in `build.gradle`):

- [JLine](https://github.com/jline/jline3) `4.4.6` — raw terminal input and output
- [Banana-Utils](https://github.com/leego/banana-utils) `2.1.0` — ANSI foreground/background colors
- `lib/student.jar` — course test framework (`student.TestCase`)

Continuous integration (`.github/workflows/ci.yml`) runs the formatting check, the build, and the tests on every push and pull request to `main`.

---

## Architecture

The project follows Model-View-Controller. No class spans two roles. The model has no knowledge of the view, the controller, or the terminal.

| Role | Class | Responsibility |
|---|---|---|
| **Model** | `State` | All game state (current screen, menu selection, difficulty, current word, typed text, correct/incorrect counts, timer) and the logic that modifies and validates it |
| **Model** | `Queue<T>` | Generic singly linked FIFO queue (`enqueue`, `dequeue`, `peek`, `isEmpty`, `size`). Currently holds pending input `Action`s |
| **Model** | `Screen`, `Difficulty` | Enums for the current screen and the selected difficulty |
| **View** | `Display` | Reads `State` through getters and draws the current screen. It never modifies the model or interprets input |
| **Controller** | `Controller` | Takes queued `Action`s and translates them into `State` operations, depending on the current screen |
| **Controller** | `Action` | Immutable record for one input event: a `Type` (character, enter, escape, arrows, backspace, other) plus a code point |
| Coordination | `Game` | Owns `State`, `Controller`, `Display`, and the `Queue<Action>`; runs the input, update, render loop |
| Infrastructure | `Console` | Wraps JLine: puts the terminal in raw mode, converts key presses into `Action`s (non-blocking, with a timeout), and buffers styled output until `display()` |
| Infrastructure | `Main` | Entry point; runs `Game` and restores the terminal on exit |

### How the loop works

Each pass of `Game.run()`:

1. `Console.getActions` reads keys for up to 15 ms and enqueues them as `Action`s.
2. `Controller.process` drains the queue in order and calls the matching `State` methods.
3. `State.update(dt)` advances the countdown and round timer.
4. `State.endGeneration()` increments a **generation counter** if anything changed.
5. If the generation differs from the last one drawn, `Display.render()` redraws the screen.

### Model / view boundary

`Display` knows only `State`'s public getters. `State` does not know `Display` exists. Instead of the Observer pattern, the view learns about changes through the generation counter: every mutation marks the state dirty, `endGeneration()` bumps the counter once per loop, and `Game` re-renders only when the counter moved. This lets many input events be processed before a single redraw, and lets `State` and `Queue` be tested without a terminal.

### Project layout

```
src/main/java/project3/group23/typetest/
    Main.java          entry point
    Game.java          main loop
    State.java         model
    Queue.java         model (generic linked FIFO queue)
    Screen.java        model enum
    Difficulty.java    model enum
    Controller.java    controller
    Action.java        controller input record
    Display.java       view
    Console.java       JLine wrapper
src/test/java/project3/group23/typetest/
    BaseTest.java      shared test base class
config/                Spotless formatter settings
lib/student.jar        course test framework
```

---

## Current status

| MVP feature | Status |
|---|---|
| Main menu and difficulty menu with arrow-key selection | Done |
| 3-2-1 countdown, then timed typing round | Done (timer is a fixed 30 s for all difficulties) |
| Type the word letter by letter; wrong letter does not advance | Done (wrong letters are counted but not yet shown) |
| Next word appears when the word is complete | Done (from a small hard-coded list per difficulty) |
| Time-left counter | Done |
| Results screen | Partly done (shows correct/incorrect counts, no score yet) |
| Green/red letter feedback | Not started |
| Words loaded from a file, shuffled, and queued | Not started |
| Scoring by word-length group | Not started |
| Difficulty scaling (time limit and word length) | Not started |
| Bad-input handling (missing/empty word file, invalid words) | Not started |
| Automated tests | Not started (only the shared `BaseTest` exists) |

---

## Next Steps

This is the plan for Deliverable 4. The point values, time limits, and owners below are proposals for the team to confirm.

### 1. Finishing the MVP, in order

1. **Fix round-setup bugs in `State`.** Starting a round does not set the first word for the chosen difficulty (Medium and Hard open on "apple"), and `typedText`, `correctCharacters`, and `incorrectCharacters` are not reset between rounds. Reset all of them when a round begins.
2. **Fix redraw and countdown display.** `State.update` marks the state dirty on every loop, so the screen is cleared and redrawn about 60 times a second. Mark it dirty only when the displayed value changes (for example, when the visible tenth of a second or the countdown digit changes). Show the countdown as 3, 2, 1, GO instead of values like "3.9...".
3. **Make wrong letters visible and settle backspace.** Track the last wrong character in `State` and have `Display` draw correct letters green and the wrong letter red, as the scope specifies. Stop `backspace()` from decrementing `correctCharacters`, which corrupts the accuracy counts.
4. **Load words from a file.** Add a bundled word file. Load it, validate each word, shuffle, and put the words in a `Queue<String>` owned by `State`, replacing the hard-coded lists. When the queue runs out before the timer does, reshuffle and refill it.
5. **Scale difficulty.** Make the time limit and word length depend on difficulty (proposed: Easy 60 s, Medium 45 s, Hard 30 s) instead of the fixed 30 s.
6. **Add scoring.** Award points only for fully completed words, by length group (proposed: 3–5 letters = 1, 6–8 = 2, 9+ = 3). Show the score on the typing screen and the results screen.
7. **Finish bad-input handling.** Cover the cases in the scope (see the tests below). Because difficulty is chosen from a menu, out-of-range and non-numeric difficulty input can no longer occur; this change goes in the Revised scope section of the spec.
8. **Update the docs.** Bring the UML, Data & state, and Method signatures sections in line with the final code.

### 2. Change feature: persistent high scores

**What it is:** after each round the player enters a name, and the score is saved to a high-score file. A new **High Scores** menu option shows the top scores for each difficulty. This is not part of the MVP and involves file input and output, a new input mode (typing a name), and two new screens.

**Prediction of what changes:**

| Layer | Classes | Prediction |
|---|---|---|
| Model | `State` | **Changes.** New screen flow (results, then name entry, then high scores), stores the player name, records the score |
| Model | new `HighScores` class | **New.** Holds entries, sorts them, reads and writes the file, and handles missing or corrupt files. Plain Java with no terminal use |
| Model | `Screen` | **Changes.** Add `NAME_ENTRY` and `HIGH_SCORES` |
| Controller | `Controller` | **Changes.** It currently accepts characters only on the typing screen, so it needs new cases for `NAME_ENTRY` |
| View | `Display` | **Changes.** Two new screens to render, and the results screen shows the player's rank |
| Should not change | `Queue`, `Action`, `Console`, `Game`, `Main`, `Difficulty` | The design says new screens and features live in the model, controller, and view only, and that input, the loop, and the terminal are reusable |

We will compare this table with the actual diff and report any differences in the Deliverable 4 reflection.

### 3. Stretch goals

| Stretch goal | Decision |
|---|---|
| Calculate accuracy | **Attempt.** `State` already counts correct and incorrect keystrokes, so this adds a getter and one line on the results screen |
| Store high scores for each section and player in a file | **Attempt.** This is the change feature above |
| Load a custom word set from a file | **Drop.** The bundled word file is the only source; the loader will be written so a path option could be added later |
| Additional game mode: survival (mistakes reduce time) | **Drop.** Not enough time alongside the change feature |
| Nicer GUI (colors, shapes, better visuals) | **Partly.** The green/red letter feedback is in the MVP. Shapes and other visual extras are dropped |

### 4. New tests

All tests extend `BaseTest` and run without a terminal. `State`, `Queue`, `Controller`, and `HighScores` never use `Console`, so none of them need a terminal.

| Class | Methods | Normal case | Bad-input case |
|---|---|---|---|
| `Queue` | `enqueue`, `dequeue`, `peek`, `isEmpty`, `size` | Items come out in the order they went in; size and `isEmpty` track correctly; reusable after being emptied | `dequeue` and `peek` on an empty queue throw `NoSuchElementException` |
| `State` | `moveSelection`, `confirm`, `back` | Selection wraps; confirm moves through the screens | Delta of 0 changes nothing; `confirm` is ignored on the typing screen |
| `State` | `setDifficulty` | Sets difficulty and time limit | `null` throws `NullPointerException` |
| `State` | `type`, `backspace`, `advanceWord` | Correct letter advances; finishing a word loads the next and scores it; the queue refills when empty | Wrong letter does not advance and is counted; backspace on empty text does nothing; letters ignored once the round ends |
| `State` | `update` | Countdown leads to typing, and typing leads to results at zero | Negative `deltaTime` throws `IllegalArgumentException`; an unfinished word earns no points |
| `State` | `endGeneration` | Generation increases once after one or more changes | No change since last call leaves the generation the same |
| `Controller` | `process` | Each action type reaches the right `State` method on each screen | Characters on a menu and Enter during typing are ignored; `OTHER` actions do nothing |
| Word loader | load, validate, shuffle | Valid words are loaded, lowercased, and shuffled (seeded `Random` for repeatable tests) | Missing file, empty file, blank lines, words with spaces or digits, and words containing apostrophes or hyphens (accepted) |
| Scoring | points by length | Each length group awards the right points | Zero-length and partial words award 0 |
| `HighScores` | add, top *N*, save, load | Entries are sorted by score and survive a save and load | Missing file starts empty; a corrupt line is skipped; empty or oversized names are rejected; ties keep a stable order |

### 5. Who does what

| Item | Owner | Support |
|---|---|---|
| Round-setup and redraw fixes (steps 1–2), countdown display | Lucas | |
| Wrong-letter state and green/red rendering (step 3) | Lucas | Krish |
| Word file loading, validation, shuffling, word queue (step 4) | Krish | Josiah |
| Difficulty scaling and scoring (steps 5–6) | Krish | Lucas |
| Bad-input handling and its tests (step 7) | Josiah | Krish |
| Change feature: `HighScores` model and file handling | Josiah | |
| Change feature: new screens, `Controller` and `Display` changes | Lucas | Josiah |
| Accuracy on the results screen | Krish | |
| `Queue`, `State`, and `Controller` tests | Josiah | Krish |
| UML, Data & state, Method signatures updates (step 8) | Krish | |
| Revised scope and the change-feature prediction vs. actual comparison | Josiah | Lucas |

Each member also reviews at least one pull request from another member before it merges into `main`.