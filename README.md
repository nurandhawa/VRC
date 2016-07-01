CMPT 373 Project Team Beta
================
Team Members:
        Alex Land,
        Constantin Koval,
        David Li,
        Gordon Shieh,
        Jasdeep Jassal,
        NoorUllah Randhawa,
        Raymond Huang,
        Samuel Kim,

PROJECT INTRO:

    This project is intended to be used by Vancouver Racquet Club to replace the present manual system.
    The program contains the following features:

        - LADDER:
                A ladder is a list of pair/teams that consists of two players each. They are ranked through their
                performances i.e. weekly matches, with the top of the ladder representing the highest ranked players.
                Every week, a pair can choose to play or not play in the upcoming round of matches. The players that
                choose to play are placed in groups of 3 or 4 in which they play with other pairs from the ladder. The
                players that choose to not play are penalised accordingly and so every week, after a round of matches,
                ladder is updated to show the new rankings.
                Some features included in the ladder are:
                    - Adding a pair to the ladder
                    - Removing a pair from the ladder
                    - Editing/Updating a pair


        - MATCHES:
                Every week, matches are automatically generated once the pairs from the ladder confirm their availability
                for that week. A match is a group of 3 or 4 pairs (depending on the total number of active players).
                They are generated in sequential order from top of the ladder to the end so the highest ranked pairs
                play with other pairs close to their ranking to avoid mismatch. The pairs that show up late or miss the
                match are penalised accordingly. The pairs are ranked from their performances in a match and then that
                ranking is used to update the ladder afterwards.
                Some features included in the matches are:
                    - Inputting results for a match
                    - Removing a player from a match
                    - Apply penalties to a player

    Each player would have their own accounts where they can login and see the ladder and upcoming matches for the
    week. This would need them to enter their email and password at the time of registration and then that can be
    used for logging in. Users would be able to change their playing status to specify if they would be playing
    for the upcoming week.



Directory Structure:

    There are three main directories in the project structure:
        - src:
            This is our source root and contains logic for the program. The 'core' directory, as the name suggests,
            contains a list of core classes that represent data on which the project is build upon such as Ladder,
            Pair, Game etc. The logic directory includes the classes that interact with core classes to perform some
            functionalities. ui folder contains the text user interface developed for the first iteration.

        - test:
            These include JUnit tests written for the above mentioned classes.

        - web:
            Includes html, css, js etc. that are being used to design the front end.


Build & Run Instructions:

    Clone the repo
    cd prj
    ./gradlew run
