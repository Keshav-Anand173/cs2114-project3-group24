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


## Next Steps


## Attribution
