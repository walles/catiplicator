# Multiplication Trainer

## Design

* Welcome screen when starting
* Each run requires the user to press a button saying: "Level N"
* Each level consists of 10 different tasks
* Tasks are picked by the app so that the player succeeds 80% of the time
* On player faults, the app lowers the player level by 1.25
* On player successes, the app raises the player level by 0.25
* Free text answers
* On a wrong answer the player is shown an explanation
  * As an NxM grid?
  * For 5x3, show `5x1=5`, `5x2=10`, `5x3=15`, iterate the lowest number?
  * For 5x3, show `5=5`, `5+5=10`, `5+5+5=15`?
  * Specific tips on certain tasks?
* On correct answer, flash screen in green + sound effect / speech
* After each round the user is presented with stats and praise, especially if they reached a new level
* The presented level never goes down. Internally the level might though.
* Tasks are rated by difficulty, not by which table they have
* The app keeps track of failed tasks:
  * One failure is compensated by two successes
  * Two or more failures are compensated by five successes
* Recently failed tasks should come up more often, it's better that the user
  learns one task properly than practices many
* When the user becomes good enough, start timing the rounds!

## TODO

* Accept result when enough digits typed, no need for pressing Return
* Add sound effects to all interactions
* Consider adding a music score, auto-generated from the Internet?
* Add skip button to Game Activity (counts as failing the question)

### DONE

* Create a Game Activity
* Add suitable layout to the Game Activity
* Set a good font size for Game Activity
* Fix Game Activity text alignment (for both `TextView` and `EditText`)
* Move text up when keyboard appears
* Start keyboard automatically in the Game Activity
