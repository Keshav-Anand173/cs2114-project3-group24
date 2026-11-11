# Restaurant Waitlist

A command-line MVC program that manages a restaurant's first-come-first-served
waitlist, backed by a singly linked FIFO queue, exactly as specified in the
project's design document.

## Project layout

```
RestaurantWaitlist/
├── src/
│   ├── model/
│   │   ├── Party.java        # one waiting party (name, size)
│   │   ├── Table.java        # one restaurant table (id, available?)
│   │   └── Waitlist.java     # the singly linked queue + the tables
│   ├── controller/
│   │   └── Controller.java   # menu loop, input validation, main()
│   └── view/
│       └── View.java         # all console output
└── test/
    └── model/
        ├── PartyTest.java
        ├── TableTest.java
        └── WaitlistTest.java # one normal + one bad-input case per public method
```

This matches the class design and method signatures in the design doc:
the model (`Party`, `Table`, `Waitlist`) never prints anything and only
communicates failure by throwing `IllegalArgumentException`,
`IllegalStateException`, or `NoSuchElementException`; the `Controller`
catches those and tells the `View` what to display.

## Compiling and running the program

From the `RestaurantWaitlist` directory, with a JDK installed:

```bash
mkdir -p out
javac -d out $(find src -name "*.java")
java -cp out controller.Controller
```

You'll be prompted for the number of tables, then shown a menu to add a
party, seat the next party, free a table, or show the current status.

## Running the unit tests

The tests use JUnit 5 and only touch the `model` package directly — no
`View`, `Controller`, or `Scanner` is ever created, which is what shows the
model is testable independently of the view.

**In an IDE (IntelliJ / Eclipse / VS Code):** mark `src` and `test` as
source roots, add JUnit 5 to the project's dependencies, and run the tests
in `test/model` normally.

**From the command line**, download the JUnit 5 console launcher once
(`junit-platform-console-standalone`, from Maven Central) and run:

```bash
mkdir -p out
javac -d out $(find src -name "*.java")
javac -cp out:junit-platform-console-standalone.jar -d out $(find test -name "*.java")
java -jar junit-platform-console-standalone.jar -cp out --scan-classpath
```

## What's implemented vs. stretch goals

The MVP is fully implemented: enqueue/seat/free with FIFO ordering, a fixed
number of equally-sized tables, and all eight bad-input cases from the
design doc (zero/negative/oversized party, non-integer input, seating an
empty waitlist, zero/negative table count, blank party name, seating with
no open table, freeing an already-open table, and an out-of-range table ID).

Estimated wait time and differently-sized tables are the stretch goals and
are **not** implemented here, per the design doc's own scope split.

## Reflection

What changed from our specification?:
The overall design stayed the same: Party, Table, and Waitlist in the model, a Controller that handles input, and a View that handles output. The biggest change was adding methods we didn't plan for. countOpenTables() and hasOpenTable() were added so the controller could check if anyone can be seated before trying, and getTableStatuses() was added so the view could show which tables are open without being given the actual Table objects. It returns a copy so the view can't change the model by accident. seatNextParty() also returns the table ID now, so the controller can tell the view where the party was seated.

Did the model really stand alone?:
Yes. Nothing in the model package prints or imports anything from the view or controller, and the unit tests create Party, Table, and Waitlist objects without ever creating a View, Controller, or Scanner. The view does import Party so it can display names and sizes, but that only goes one way. One thing that crosses the line slightly is that the error messages are written inside the model's exceptions, and the view just prints whatever message it receives. We decided this was fine because the model is only describing what went wrong, not deciding how it looks to the user.

What happened to our unknowns?:
- Building a singly linked queue: figured out. The tricky part was resetting lastNode when the queue empties, and we wrote a test that drains the queue and enqueues again to check it.
- Handling bad input with Scanner: mostly figured out. Reading full lines and using Integer.parseInt avoids the skipped-prompt problem, and the program asks again if the user types a non-number. Ending input with Ctrl+D still crashes the program.
- Keeping the model and view separate: figured out. The model throws exceptions, the controller catches them, and the view prints the message.
- Estimating wait time: still open. We still need to decide how to track how long tables have been occupied and what to use as a default before there is any data.
- Different sized tables: still open. We have not started this and are focusing on wait time first.

What doesn't work yet?:
- There is no upper limit on the number of tables, so entering a huge number can crash the program by running out of memory.
- Ending input with Ctrl+D (or Ctrl+Z on Windows) crashes the program instead of exiting cleanly.
- Party names are not trimmed, so extra spaces are saved as part of the name.
- There are no tests for the exact party size limits (1 and 20 allowed, 21 rejected).
- The Controller and View have no automated tests; we tested them by running the program by hand.
- Neither stretch goal is implemented yet.

## Next Steps
We are pretty much completely done implementing the MVP. The core functionality of the waitlist is there and we can enqueue, dequeue, and seat parties of guests at available tables and remove them when they are done. One change feature that we did not initially plan to add is the function countOpenTables(). This allows us to get the number of tables currently open and using that we know how many are taken. This should affect the model class as in our stretch goals we want to estimate a wait time using number of empty tables + groups ahead in the queue. We are committing to this stretch goal to create a tracker for estimated wait time based on the time since a party has been seated and parties in the queue. We will need to test the estimateWaitTime() function with null, full and empty variants. Alex is responsible for implementing this stretch goal, Alper will oversee it's interaction with model, view and controller, and Keshav will be in charge of formatting the second prototype and identifying key changes between now and then.

## Attribution
Alex | Implemented the controller.java class and handled Next steps and attribution sections of the README.

Keshav | Implemented party, table and waitlist classes and baseline README summary.

Alper | Implemented the view.java class and handled the Reflection section of the README.

## GenAI
We used GenAI (Claude) to review our code and suggest improvements, such as adding an upper limit on the number of tables, adding boundary tests for party size, and planning how to test the estimated wait time. The Reflection section was written by Alper and then edited by GenAI for better layout. All code decisions were made by our team, and we checked every suggestion by reading the code and running our tests before accepting it.







