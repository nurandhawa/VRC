(function () {
    "use strict";

    $.material.init();



    var ladderData = {
      "dateCreated": "Jun 16, 2016 9:02:31 PM",
      "pairs": [
        {
          "team": [
            {
              "firstName": "Bobby",
              "lastName": "Chan",
              "id": 51
            },
            {
              "firstName": "Wing",
              "lastName": "Man",
              "id": 52
            }
          ],
          "dateCreated": "Jun 16, 2016 9:02:31 PM",
          "position": 0,
          "oldPosition": 0,
          "penalty": 0,
          "isPlaying": false,
          "id": 29
        },
        {
          "team": [
            {
              "firstName": "Ken",
              "lastName": "Hazen",
              "id": 53
            },
            {
              "firstName": "Brian",
              "lastName": "Fraser",
              "id": 54
            }
          ],
          "dateCreated": "Jun 16, 2016 9:02:31 PM",
          "position": 0,
          "oldPosition": 0,
          "penalty": 0,
          "isPlaying": false,
          "id": 30
        },
        {
          "team": [
            {
              "firstName": "Simon",
              "lastName": "Fraser",
              "id": 55
            },
            {
              "firstName": "Dwight",
              "lastName": "Howard",
              "id": 56
            }
          ],
          "dateCreated": "Jun 16, 2016 9:02:31 PM",
          "position": 0,
          "oldPosition": 0,
          "penalty": 0,
          "isPlaying": false,
          "id": 31
        },
        {
          "team": [
            {
              "firstName": "Bobby",
              "lastName": "Chan",
              "id": 51
            },
            {
              "firstName": "Big",
              "lastName": "Head",
              "id": 57
            }
          ],
          "dateCreated": "Jun 16, 2016 9:02:31 PM",
          "position": 0,
          "oldPosition": 0,
          "penalty": 0,
          "isPlaying": false,
          "id": 32
        }
      ],
      "id": 6
    };

    ladderData.pairs.forEach(function(pair) {
      pair.teamName = pair.team[0].firstName + " and " + pair.team[1].firstName;
      pair.playingStatus = pair.isPlaying ? "playing" : "notplaying";
    });


    var ladder = new Ladder(ladderData.pairs);

    var editFunction = function() {
        ladder.changeMode.call(ladder.component);
    };

    var header = new Header("Ladder", "Edit Ladder", ladderData.dateCreated, editFunction);
})();
