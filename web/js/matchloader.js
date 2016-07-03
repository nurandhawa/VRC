/**
 * Created by David on 2016-06-22.
 */
(function () {
    "use strict";

    $.material.init();

    var matchData =  [
        {
            "pairs": [
                {
                    "players": [
                        {
                            "firstName": "Bobby",
                            "lastName": "Chan",
                            "phoneNumber": "",
                            "id": 1
                        },
                        {
                            "firstName": "Wing",
                            "lastName": "Man",
                            "phoneNumber": "",
                            "id": 2
                        }
                    ],
                    "position": 1,
                    "isPlaying": true,
                    "pairScore": 0,
                    "id": 1
                },
                {
                    "players": [
                        {
                            "firstName": "Ken",
                            "lastName": "Hazen",
                            "phoneNumber": "",
                            "id": 3
                        },
                        {
                            "firstName": "Brian",
                            "lastName": "Fraser",
                            "phoneNumber": "",
                            "id": 4
                        }
                    ],
                    "position": 2,
                    "isPlaying": true,
                    "pairScore": 0,
                    "id": 2
                },
                {
                    "players": [
                        {
                            "firstName": "Simon",
                            "lastName": "Fraser",
                            "phoneNumber": "",
                            "id": 5
                        },
                        {
                            "firstName": "Dwight",
                            "lastName": "Howard",
                            "phoneNumber": "",
                            "id": 6
                        }
                    ],
                    "position": 3,
                    "isPlaying": true,
                    "pairScore": 0,
                    "id": 3
                },
                {
                    "players": [
                        {
                            "firstName": "Alex",
                            "lastName": "Land",
                            "phoneNumber": "",
                            "id": 9
                        },
                        {
                            "firstName": "Test",
                            "lastName": "Player",
                            "phoneNumber": "",
                            "id": 10
                        }
                    ],
                    "position": 5,
                    "isPlaying": true,
                    "pairScore": 0,
                    "id": 5
                }
            ],
            "scorecardIndex": 0,
            "id": 0
        },
        {
            "pairs": [
                {
                    "players": [
                        {
                            "firstName": "Bobby",
                            "lastName": "Chan",
                            "phoneNumber": "",
                            "id": 1
                        },
                        {
                            "firstName": "Wing",
                            "lastName": "Man",
                            "phoneNumber": "",
                            "id": 2
                        }
                    ],
                    "position": 1,
                    "isPlaying": true,
                    "pairScore": 0,
                    "id": 1
                },
                {
                    "players": [
                        {
                            "firstName": "Ken",
                            "lastName": "Hazen",
                            "phoneNumber": "",
                            "id": 3
                        },
                        {
                            "firstName": "Brian",
                            "lastName": "Fraser",
                            "phoneNumber": "",
                            "id": 4
                        }
                    ],
                    "position": 2,
                    "isPlaying": true,
                    "pairScore": 0,
                    "id": 2
                },
                {
                    "players": [
                        {
                            "firstName": "Simon",
                            "lastName": "Fraser",
                            "phoneNumber": "",
                            "id": 5
                        },
                        {
                            "firstName": "Dwight",
                            "lastName": "Howard",
                            "phoneNumber": "",
                            "id": 6
                        }
                    ],
                    "position": 3,
                    "isPlaying": true,
                    "pairScore": 0,
                    "id": 3
                },
                {
                    "players": [
                        {
                            "firstName": "Alex",
                            "lastName": "Land",
                            "phoneNumber": "",
                            "id": 9
                        },
                        {
                            "firstName": "Test",
                            "lastName": "Player",
                            "phoneNumber": "",
                            "id": 10
                        }
                    ],
                    "position": 5,
                    "isPlaying": true,
                    "pairScore": 0,
                    "id": 5
                }
            ],
            "scorecardIndex": 1,
            "id": 1
        },
        {
            "pairs": [
                {
                    "players": [
                        {
                            "firstName": "Bobby",
                            "lastName": "Chan",
                            "phoneNumber": "",
                            "id": 1
                        },
                        {
                            "firstName": "Wing",
                            "lastName": "Man",
                            "phoneNumber": "",
                            "id": 2
                        }
                    ],
                    "position": 1,
                    "isPlaying": true,
                    "pairScore": 0,
                    "id": 1
                },
                {
                    "players": [
                        {
                            "firstName": "Ken",
                            "lastName": "Hazen",
                            "phoneNumber": "",
                            "id": 3
                        },
                        {
                            "firstName": "Brian",
                            "lastName": "Fraser",
                            "phoneNumber": "",
                            "id": 4
                        }
                    ],
                    "position": 2,
                    "isPlaying": true,
                    "pairScore": 0,
                    "id": 2
                },
                {
                    "players": [
                        {
                            "firstName": "Simon",
                            "lastName": "Fraser",
                            "phoneNumber": "",
                            "id": 5
                        },
                        {
                            "firstName": "Dwight",
                            "lastName": "Howard",
                            "phoneNumber": "",
                            "id": 6
                        }
                    ],
                    "position": 3,
                    "isPlaying": true,
                    "pairScore": 0,
                    "id": 3
                },
                {
                    "players": [
                        {
                            "firstName": "Alex",
                            "lastName": "Land",
                            "phoneNumber": "",
                            "id": 9
                        },
                        {
                            "firstName": "Test",
                            "lastName": "Player",
                            "phoneNumber": "",
                            "id": 10
                        }
                    ],
                    "position": 5,
                    "isPlaying": true,
                    "pairScore": 0,
                    "id": 5
                }
            ],
            "scorecardIndex": 2,
            "id": 2
        }
    ];

    var api = new API();
    api.getMatches(function (matchData) {
        var matches = new Matches(matchData);

        var editFunction = function() {
            matches.changeMode.call(matches.component);
        };

        var header = new Header("Matches", "Edit Matches", "TBD", editFunction);

   });
})();
