# Type Test

A non-blocking console typing game written in Java. Players select a difficulty, complete a countdown, and type as many words as possible before the timer runs out. Correct and incorrect keystrokes receive colored feedback, and the results screen displays the number of correct and incorrect characters entered.

**Group 23 — Lucas Thompson, Krish, Josiah Samuel**

---

## How to Play

1. From the main menu, select **Start** or **Quit**.
2. Choose a difficulty: **Easy**, **Medium**, or **Hard**.
3. Wait through the 3-2-1 countdown and brief GO screen.
4. Type the displayed word, one character at a time:
   - Correct letters advance your progress and appear green.
   - Incorrect letters appear red underneath the word and do not advance your progress.
   - Typing the expected character after a mistake clears the incorrect-character indicator.
   - Completing the word automatically advances to the next word.
5. When the 15-second round ends, the results screen displays the correct and incorrect character counts. Press Enter to return to the main menu.

### Controls

| Key       | Action                                                            |
| --------- | ----------------------------------------------------------------- |
| Up / Down | Move through menu options                                         |
| Enter     | Confirm a menu selection or leave the results screen              |
| Escape    | Return to the main menu from gameplay, or quit from the main menu |
| Letters   | Type the current word (input is lowercased)                       |
| Backspace | Recognized but currently ignored during gameplay                  |

---

## Building and Running

**Requirements:** JDK 23. The Gradle wrapper is included, so a separate Gradle installation is not required.

```bash
./gradlew run            # Build and launch the game
./gradlew test           # Run automated tests
./gradlew assemble       # Compile and build the project
./gradlew format         # Apply source formatting
./gradlew check-format   # Verify formatting
```

The custom Gradle run task opens a separate console window on Windows because the game needs a real terminal for raw keyboard input. On Linux, it attempts to locate an appropriate parent terminal. The included `start-debug.ps1` script supports JVM debugging on port 5005.

### Dependencies

- **JLine 4.4.6:** Raw terminal input and output.
- **Banana 2.1.0:** ANSI colors and text formatting.
- **student.jar:** Course-provided testing framework.

### Running Tests

Tests are located under `src/test/java/project3/group23/typetest/` and use the course-provided `student.TestCase` framework through our `BaseTest` class.

Run all tests using:

```bash
./gradlew test
```

The current repository contains **33 test methods**:

| Test Class       |  Tests | Coverage                                                                                            |
| ---------------- | -----: | --------------------------------------------------------------------------------------------------- |
| `QueueTest`      |      8 | FIFO ordering, empty-queue exceptions, null values, reuse, interleaved operations                   |
| `StateTest`      |     21 | Navigation, typing, feedback, countdown transitions, invalid arguments, round resets, timer updates |
| `ControllerTest` |      4 | Normal input handling, invalid menu input, unsupported characters, and ignored backspace            |
| **Total**        | **33** |                                                                                                     |

The tests exercise the model independently of the terminal and view. `ControllerTest` also tests interaction between the controller and model without creating a `Display` or `Console`.

The latest GitHub Actions run on `main` successfully completed the build, formatting, and test jobs on November 18, 2026.

GitHub Actions automatically runs these checks on pushes and pull requests to `main`.

---

## Architecture

The project follows the Model-View-Controller (MVC) pattern, separating game state, user input, and rendering.

| Role           | Class                  | Responsibility                                                                                                  |
| -------------- | ---------------------- | --------------------------------------------------------------------------------------------------------------- |
| Model          | `State`                | Holds game state, word progression, timers, difficulty, menu selection, character counts, and state transitions |
| Model          | `Queue<T>`             | Generic singly linked FIFO queue used for pending input actions                                                 |
| Model          | `Screen`, `Difficulty` | Enums representing the current screen and difficulty                                                            |
| Controller     | `Controller`           | Processes input actions and calls the appropriate model methods                                                 |
| Controller     | `Action`               | Immutable record representing a keyboard input event                                                            |
| View           | `Display`              | Renders the current model state, including menus, countdown, colored typing feedback, and results               |
| Coordination   | `Game`                 | Coordinates the input, update, and rendering loop                                                               |
| Infrastructure | `Console`              | Handles terminal input, output, key decoding, and colors through JLine and Banana                               |
| Infrastructure | `Main`                 | Application entry point                                                                                         |

### Main Loop

Each iteration of `Game.run()` performs the following operations:

1. `Console.getActions()` collects input events into `Queue<Action>`.
2. `Controller.process()` consumes those actions and calls the appropriate `State` methods.
3. `State.update(dt)` advances the countdown or round timer when appropriate.
4. `State.endGeneration()` increments the generation counter if the model changed.
5. `Game` compares the generation counter with the last rendered generation and redraws through `Display` when necessary.

### Model/View Boundary

The model does not depend on the terminal or display implementation.

`Display` only accesses game information through the public getters in `State` and does not maintain a separate copy of model state. `Controller` translates input events into model operations without directly modifying the view.

Instead of implementing an Observer pattern, we use a generation counter to determine when the view should redraw.

We also improved `State.update()` so the timer only advances while the game is in one of three states:

- `COUNTDOWN`
- `GO_SCREEN`
- `TYPING`

The method returns without updating the timer or dirty-state flag on the main menu, difficulty menu, and results screen. This prevents unnecessary redraws on inactive screens.

Active timer screens still redraw frequently, so additional rendering optimization remains possible.

### Project Layout

```text
src/main/java/project3/group23/typetest/
    Main.java
    Game.java
    State.java
    Queue.java
    Screen.java
    Difficulty.java
    Controller.java
    Action.java
    Display.java
    Console.java

src/test/java/project3/group23/typetest/
    BaseTest.java
    QueueTest.java
    StateTest.java
    ControllerTest.java

config/
lib/student.jar
```

---

## Current Status

| Feature                                          | Status           |
| ------------------------------------------------ | ---------------- |
| Main menu and difficulty selection               | Complete         |
| Controller-driven gameplay                       | Complete         |
| Correct character typing and word progression    | Complete         |
| Incorrect input validation                       | Complete         |
| Green/red character feedback                     | Complete         |
| 3-2-1 countdown and GO transition                | Complete         |
| 15-second typing round                           | Complete         |
| Round initialization and counter resets          | Complete         |
| Results screen with correct/incorrect counts     | Complete         |
| Different hard-coded word lists per difficulty   | Complete         |
| Timer restricted to active game states           | Complete         |
| Queue and model unit tests                       | 33 passing tests |
| File-based word loading and shuffling            | Not implemented  |
| Scoring by completed word length                 | Not implemented  |
| Difficulty-based round durations                 | Not implemented  |
| Persistent high scores                           | Not implemented  |
| Additional optimization of active-screen redraws | Not implemented  |

The current prototype supports the core typing interaction, including a playable round, menu navigation, visual feedback, and results.

The remaining MVP work focuses on word loading, scoring, and difficulty scaling.

---

## Reflection

### How Our Design Absorbed the Change

In our Deliverable 3 Next Steps, we originally planned to implement persistent high scores as our Deliverable 4 change feature. This would have required a new high-score model, additional screens, and file persistence.

After working with the prototype, we decided that our core gameplay needed more attention before adding stretch goals. We prioritized correcting typing behavior, improving the countdown and visual feedback, and adding automated tests.

Rather than implementing persistent high scores, we completed two related gameplay changes.

**1. Correct and incorrect character feedback**

Previously, the typing behavior and handling of incorrect input were incomplete. We changed `State.type()` to compare the entered character with the expected character before advancing.

Correct letters now advance progress and are rendered in green. Incorrect letters do not advance progress and are tracked separately through the `lastIncorrect` field, which `Display` uses to show the last incorrect character in red.

Entering the correct character clears the incorrect-character indicator, while the incorrect keystroke remains included in the total incorrect count.

This improved the gameplay feel and made the typing behavior easier to understand.

**2. Countdown and gameplay transitions**

We introduced `GO_SCREEN` as a separate state between the countdown and typing round.

The player now sees a 3-2-1 countdown, followed by a brief GO screen, before the typing timer begins.

We also improved timer handling so the countdown only changes during `COUNTDOWN`, `GO_SCREEN`, and `TYPING`. Additionally, Escape now correctly returns to the main menu during the GO screen.

These changes made the transition between menu selection and gameplay clearer and prevented the timer from continuing to update on unrelated screens.

Both features changed model logic or state and are reachable through the existing controller and view.

### Predicted Changes vs. Actual Changes

Our D3 prediction was based on implementing high scores. Because we switched features, the actual changes were different.

| Class                                                      | D3 Prediction                                         | Actual Changes                                                                                               |
| ---------------------------------------------------------- | ----------------------------------------------------- | ------------------------------------------------------------------------------------------------------------ |
| `State`                                                    | Add player names, scoring, and high-score transitions | Added incorrect-character feedback, improved round initialization, countdown transitions, and timer handling |
| `Screen`                                                   | Add `NAME_ENTRY` and `HIGH_SCORES`                    | Added `GO_SCREEN`                                                                                            |
| `Controller`                                               | Handle name-entry input                               | Removed backspace gameplay handling; existing character handling supported the new feature                   |
| `Display`                                                  | Render high scores and name-entry screens             | Added colored typing feedback and GO screen rendering                                                        |
| New `HighScores`                                           | Store, sort, load, and save scores                    | Not created because high scores were postponed                                                               |
| `Queue`, `Action`, `Console`, `Game`, `Main`, `Difficulty` | Remain unchanged                                      | Remained unchanged                                                                                           |

Although the feature changed, the class responsibilities mostly matched our original prediction.

The model handled gameplay rules, the view handled presentation, and the controller handled input. We did not have to change the general input abstraction or main game loop to implement the updated gameplay behavior.

None of the classes that we predicted should remain unchanged needed to change for these features.

### Did the Change Cross the Model/View Line?

The changes did require updates to both the model and view, but they did not introduce inappropriate dependencies between them.

For typing feedback, `State` tracks the entered characters and stores the last incorrect character. `Display` uses the existing getters and the new `getLastIncorrect()` method to render the feedback.

For the countdown feature, `State` manages the transition into `GO_SCREEN`, and `Display` renders that screen based on the current `Screen` enum.

The view needed to learn about the new screen and feedback data, but it did not need access to internal state or copies of the model's fields.

The existing `Controller` and `Game` architecture continued to support the new behavior without major changes.

This was an important benefit of our MVC separation.

### What Changed From the D3 Plan?

The largest change was prioritizing MVP functionality and gameplay feel over stretch goals.

We originally intended to complete more of the gameplay features and add persistent high scores. During implementation, we discovered that incorrect-character behavior, countdown presentation, round initialization, and missing tests were more immediate concerns.

We therefore prioritized these issues instead of adding additional functionality to a prototype that was not yet sufficiently tested.

Other important changes included:

- Fixing the first word selected for each difficulty.
- Resetting typed text and correct/incorrect character counts between rounds.
- Removing backspace handling rather than trying to maintain its previous behavior.
- Adding a separate GO screen.
- Limiting timer updates to active gameplay states.
- Expanding the automated tests from only a shared test base to 33 executable test methods.
- Adding controller tests that verify user actions correctly reach the model.

The currently implemented typing round uses a fixed 15-second timer. The variable time limits proposed in D3 remain unfinished.

### Testing and Bad Input

One of the largest improvements since D3 was adding tests that execute independently of the user interface.

`QueueTest` now covers normal FIFO behavior, empty-queue exceptions, null elements, queue reuse, and interleaved enqueue/dequeue operations.

`StateTest` covers normal gameplay behavior as well as invalid and boundary cases, including:

- Incorrect characters not advancing the word.
- Correct input clearing the incorrect-character indicator.
- Null difficulty arguments.
- Negative time deltas.
- Zero menu-selection movement.
- Repeated calls to stop the game.
- Word-list wrapping.
- Resetting the round between games.
- Escape during the GO screen.
- Timer updates on inactive screens.

`ControllerTest` verifies that normal input passes through the controller into the model and that unsupported character events, invalid menu input, and ignored backspace events do not corrupt gameplay state.

We also corrected our floating-point assertions to use a tolerance where required by the testing framework.

The current tests pass in GitHub Actions. However, testing will need to continue as we add word loading, scoring, and difficulty scaling.

### What Doesn't Work Yet?

The prototype is playable, but several planned MVP features are still missing.

**Word loading:** Words still come from small hard-coded lists instead of a validated and shuffled file-based word source. Missing files, empty files, and invalid file contents cannot yet be handled because the loader has not been implemented.

**Scoring:** The results screen only shows correct and incorrect character counts. It does not calculate a score based on completed words.

**Difficulty scaling:** Easy, Medium, and Hard currently use different word lists but all have the same 15-second round duration.

**High scores:** Player names and persistent high-score storage were deferred.

**Other limitations:** Backspace is currently ignored during gameplay. The controller restricts character entry to the typing screen, but the model's `type()` method does not independently validate the active screen. The timer no longer updates on inactive screens, although active screens still redraw frequently.

Our existing tests cover the current prototype, but additional tests will be needed as these features are implemented.

### What Would Make the Next Change Cheaper?

The MVC separation helped us introduce gameplay changes without modifying the terminal abstraction or overall game loop.

However, `State` currently manages many responsibilities, including menu navigation, word selection, typing validation, timers, and results.

A useful future improvement would be to move word loading and selection into a separate model component. Scoring could also be separated from general game-state management.

This would make it easier to add file-based words, new difficulty rules, and high scores without repeatedly expanding `State`.

We would also continue writing tests alongside each feature rather than waiting until the end. Our prototype developed faster than our testing, and catching up afterward required more effort than testing each change as it was introduced.

---

## Next Steps

### Remaining Work for the Final Build

Our priority is completing the remaining MVP features before considering stretch goals.

Catching up on the current prototype's tests was our first priority and has now been completed through the expanded 33-test suite.

The remaining work is ordered as follows:

| Priority  | Item                         | Plan                                                                                              |
| --------- | ---------------------------- | ------------------------------------------------------------------------------------------------- |
| Completed | Catch up on current tests    | Added model, Queue, and controller tests, including normal and bad-input cases                    |
| 1         | Word loading                 | Load words from a bundled file, validate and shuffle them, and use a word queue                   |
| 2         | Scoring                      | Calculate points for fully completed words according to word length                               |
| 3         | Additional tests             | Test word loading, invalid files, scoring, and any new model methods                              |
| 4         | Difficulty scaling           | Implement different time limits and appropriate word lengths per difficulty                       |
| 5         | Final fixes and cleanup      | Improve active-screen redraw efficiency, resolve remaining input issues, and update documentation |
| 6         | High scores, if time permits | Add persistent high scores only after the MVP and its tests are complete                          |

We will continue testing throughout development rather than treating tests as a separate final step.

### Stretch Goals

| Stretch Goal                      | Decision                                             |
| --------------------------------- | ---------------------------------------------------- |
| Persistent high scores            | Deferred; attempt only if MVP and tests are complete |
| Accuracy percentage               | Optional, not committed                              |
| Custom word sets                  | Dropped                                              |
| Survival mode                     | Dropped                                              |
| Additional GUI effects and shapes | Dropped                                              |
| Green/red typing feedback         | Already implemented                                  |

We are not committing to additional stretch goals at this stage. Our main goal is a complete, stable, and testable typing game.

### Planned Three-Minute Demo

| Time      | Demonstration                                                                                                          |
| --------- | ---------------------------------------------------------------------------------------------------------------------- |
| 0:00–0:20 | Launch the game, show main-menu navigation, and choose a difficulty                                                    |
| 0:20–0:40 | Demonstrate the 3-2-1 countdown and separate GO transition                                                             |
| 0:40–1:15 | Demonstrate typing, green correct letters, red incorrect input, and word progression                                   |
| 1:15–1:35 | Show the round ending, results screen, and return to the main menu                                                     |
| 1:35–2:15 | Explain the flow through `Console`, `Controller`, `State`, and `Display`, including the new features                   |
| 2:15–2:50 | Demonstrate or explain running the 33 tests through Gradle, including bad-input tests and interleaved Queue operations |
| 2:50–3:00 | Summarize remaining MVP features and explain why high scores were deferred                                             |

The demo will focus on the current working prototype, including both replacement change features.

We will be prepared to explain how the new features interact with the model, controller, and view, and how the tests verify that behavior.

### Team Responsibilities

The following planned assignments carry forward our D3 responsibilities, adjusted for the work remaining after D4.

| Remaining Item                            | Primary Owner | Support          |
| ----------------------------------------- | ------------- | ---------------- |
| File-based word loading and validation    | Krish         | Josiah           |
| Word shuffling and queue integration      | Krish         | Josiah           |
| Scoring and results calculations          | Krish         | Lucas            |
| Additional tests for new features         | Josiah        | Krish            |
| Difficulty scaling                        | Krish         | Lucas            |
| Rendering improvements and gameplay fixes | Lucas         | Josiah           |
| High-score model, if attempted            | Josiah        | Krish            |
| High-score screens, if attempted          | Lucas         | Josiah           |
| UML, scope, and method documentation      | Krish         | Josiah           |
| README integration and final review       | Lucas         | Krish and Josiah |

Every member is responsible for reviewing changes, running tests, and understanding the implementation before the final demonstration.

---

## Attribution

The following table lists every Java class and test file in the repository, along with the contributions identified from our development history.

| File                  | Contributor(s)       | Contribution                                                                                                                                                                                               |
| --------------------- | -------------------- | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `Main.java`           | Lucas                | Wrote and revised the application entry point                                                                                                                                                              |
| `Game.java`           | Lucas                | Implemented the game loop and coordination between model, controller, and view                                                                                                                             |
| `State.java`          | Krish, Lucas, Josiah | Krish implemented the initial game-state model; Lucas revised round initialization, countdown behavior, incorrect-input feedback, and timer handling; Josiah contributed earlier backspace-related changes |
| `Queue.java`          | Krish, Lucas         | Krish implemented the linked FIFO queue; Lucas created the initial class template and integrated the class                                                                                                 |
| `Screen.java`         | Lucas                | Created and updated the screen enum, including `GO_SCREEN`                                                                                                                                                 |
| `Difficulty.java`     | Lucas                | Created the difficulty enum                                                                                                                                                                                |
| `Controller.java`     | Lucas, Josiah        | Lucas implemented and revised input processing; Josiah contributed earlier backspace handling                                                                                                              |
| `Action.java`         | Lucas, Josiah        | Lucas created the input event record; Josiah revised backspace-related input handling                                                                                                                      |
| `Console.java`        | Lucas, Josiah        | Lucas implemented terminal input/output and key decoding; Josiah revised backspace input handling                                                                                                          |
| `Display.java`        | Lucas, Krish         | Lucas implemented rendering and updated countdown and colored typing feedback; Krish made earlier revisions                                                                                                |
| `BaseTest.java`       | Lucas                | Created the shared course testing base                                                                                                                                                                     |
| `QueueTest.java`      | Krish, Lucas         | Krish wrote the original Queue tests; Lucas added the interleaved-operation test with GenAI assistance                                                                                                     |
| `StateTest.java`      | Krish, Lucas         | Krish wrote the original State tests; Lucas added and revised tests for feedback, invalid input, countdown transitions, round resets, navigation, and timer behavior, with GenAI assistance                |
| `ControllerTest.java` | Lucas                | Added normal-input and bad-input controller tests with GenAI assistance                                                                                                                                    |

### GenAI Use

We used ChatGPT as an assistant during development and documentation.

GenAI helped us review the repository and compare our implementation with the Deliverable 3 plan and Deliverable 4 requirements. It was used to generate candidate test cases for `State`, `Queue`, and `Controller`, suggest improvements to countdown handling, identify possible bugs, and draft sections of this README.

The generated test suggestions and code changes were reviewed, adapted where necessary, and integrated into the project by the team. We ran the tests through Gradle and GitHub Actions to verify their behavior.

GenAI also assisted in organizing our reflection, identifying the differences between planned and actual changes, preparing the final-build priorities, and drafting the attribution table.

The team remains responsible for understanding the implementation, verifying the tests, confirming individual contributions, and explaining the design decisions during grading.
