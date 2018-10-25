# Multiplication Trainer

## Design

* :white_check_mark: Welcome screen when starting
* :white_check_mark: Each run requires the user to press a button saying: "Level N"
* :white_check_mark: Each level consists of 10 different challenges
* :white_check_mark: Challenges are picked by the app so that the player succeeds 80% of the time
* :white_check_mark: On player faults, the app lowers the player level by 1.00
* :white_check_mark: On player successes, the app raises the player level by 0.25
* :white_check_mark: Free text answers
* On a wrong answer the player is shown an explanation
  * As an NxM grid?
  * For 5x3, show `5x1=5`, `5x2=10`, `5x3=15`, iterate the lowest number?
  * For 5x3, show `5=5`, `5+5=10`, `5+5+5=15`?
  * Specific tips on certain challenges?
* On correct answer, flash screen in green + sound effect / speech
* After each round the user is presented with stats and praise,
  especially if they beat a problem they initially failed on
* :white_check_mark: The presented level never goes down. Internally the level might though.
* :white_check_mark: Challenges are rated by difficulty, not by which table they have
* :white_check_mark: The app keeps track of failed challenges:
  * One failure is compensated by two successes
  * Two or more failures are compensated by three successes
* Recently failed challenges should come up more often, it's better that the user
  learns one challenge properly than practices many
* When the user becomes good enough, start timing the rounds!
* Everything is cat themed

## TODO

* Fix app orientation to portrait
* Test what happens when skill level goes above difficulty of available
  assignments
* Add meow sound effect when starting a new level
* Add a music score. Auto-generated from the Internet?
* Localize into Swedish
* Show progress bar during the level
* Set up CI on pushes, running at least the JUnit tests
* If the player defeats a previously-failed question, say so in the
  level-finished screen
* Rename to "Meowplicator"
* Use Comic Sans as a font everywhere?
* Test cat pictures, text and speech bubbles on multiple resolutions
* Test cat pictures, text and speech bubbles on multiple aspect ratios
* Add skip button to Game Activity (counts as failing the question)

### DONE

* Create a Game Activity
* Add suitable layout to the Game Activity
* Set a good font size for Game Activity
* Fix Game Activity text alignment (for both `TextView` and `EditText`)
* Move text up when keyboard appears
* Start keyboard automatically in the Game Activity
* Add random challenges
* Show toast with correct or wrong
* Accept result when enough digits typed, no need for pressing Return
* Add a level-starting screen with a start button
* Finish the level after 10 questions
* Add a level summary screen
* Enable player to start a new level after the summary screen
* Maintain user state throughout the app
* Add failed challenges to user state
* Add `Timber` and start logging
* Create a challenge suggester that suggests challenges based on:
  * level context (what we've used in the current round), avoid repetition
  * player context (skill level, failures)
* On success, play encouraging sound effect
* On failure, open a dialog saying:
  "The answer is 5*4=20, type it here: 5x4=__". Close the dialog when the
  user has typed the correct answer
* Pause a bit after each completed answer to give the user time to
  understand whether they passed or failed
* Add applause sound to level-finished screen. One for 10/10, another
  one for 9/10 or less
* Put cat picture as background for level-launching screen
* Put cat picture as background for in-game screen
* Put cat picture as background for level-finished screen
