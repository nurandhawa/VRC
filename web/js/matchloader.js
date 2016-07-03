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
                            "id": 7
                        },
                        {
                            "firstName": "Test",
                            "lastName": "Player",
                            "phoneNumber": "",
                            "id": 8
                        }
                    ],
                    "position": 4,
                    "isPlaying": true,
                    "pairScore": 0,
                    "id": 4
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
                            "firstName": "Yu",
                            "lastName": "Billy",
                            "phoneNumber": "",
                            "id": 1
                        },
                        {
                            "firstName": "Leung",
                            "lastName": "Ben",
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
                            "firstName": "Woolfries",
                            "lastName": "Cameron",
                            "phoneNumber": "",
                            "id": 3
                        },
                        {
                            "firstName": "Troung",
                            "lastName": "Cindy",
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
                            "firstName": "Tang",
                            "lastName": "Bonnie",
                            "phoneNumber": "",
                            "id": 5
                        },
                        {
                            "firstName": "Yun",
                            "lastName": "Carol",
                            "phoneNumber": "",
                            "id": 6
                        }
                    ],
                    "position": 3,
                    "isPlaying": true,
                    "pairScore": 0,
                    "id": 3
                },
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
        var saveResultsButton = Vue.extend({
            template: '<a v-on:click="saveResults()" class="btn btn-raised btn-success header-button">Save Results</a>',
            methods: {
                saveResults: function() {
                    console.log("save results");
                }
            },
            parent: header.component
        });
        header.addButton(saveResultsButton);
   });
})();
