/**
 * Created by David on 2016-06-22.
 */
(function () {
    "use strict";

    $.material.init();

    var matchData = [
        {
            matchNum: 1,
            pairs: [
                {
                    matchRanking: 1,
                    name: "Alex Land and David Li",
                },
                {
                    matchRanking: 2,
                    name: "Floyd Mayweather and Manny Pacquiao",
                },
                {
                    matchRanking: 3,
                    name: "Clark Kent and Bruce Wayne",
                }
            ]
        },
        {
            matchNum: 2,
            pairs: [
                {
                    matchRanking: 1,
                    name: "Gordon Shieh and Jas Jassal",
                },
                {
                    matchRanking: 2,
                    name: "Tony Stark and Steve Rogers",
                },
                {
                    matchRanking: 3,
                    name: "Bruce Banner and Natascha Romanoff",
                }
            ]
        },
        {
            matchNum: 3,
            pairs: [
                {
                    matchRanking: 1,
                    name: "Noor Randhawa and Constantin Koval",
                },
                {
                    matchRanking: 2,
                    name: "Jason Statham and Liam Neeson",
                },
                {
                    matchRanking: 3,
                    name: "Scott Summers and Peter Parker",
                }
            ]
        },
        {
            matchNum: 4,
            pairs: [
                {
                    matchRanking: 1,
                    name: "Raymond Huang and Samuel Kim",
                },
                {
                    matchRanking: 2,
                    name: "Oda Nobunaga and Tokugawa Ieyasu",
                },
                {
                    matchRanking: 3,
                    name: "Enrico Dandolo and Suleiman III",
                }
            ]
        },
        {
            matchNum: 5,
            pairs: [
                {
                    matchRanking: 1,
                    name: "Matt Murdock and Luke Cage",
                },
                {
                    matchRanking: 2,
                    name: "James Bond and Jason Bourne",
                },
                {
                    matchRanking: 3,
                    name: "Lisa Simpson and Bart Simpson",
                },
                {
                    matchRanking: 4,
                    name: "Edward Elric and Alphonse Elric",
                }
            ]
        },
        {
            matchNum: 6,
            pairs: [
                {
                    matchRanking: 1,
                    name: "Harry Potter and Ron Weasley",
                },
                {
                    matchRanking: 2,
                    name: "Jon Snow and Ygritte Wildling",
                },
                {
                    matchRanking: 3,
                    name: "Khal Drogo and Daenerys Targaryen",
                },
                {
                    matchRanking: 4,
                    name: "Roy Mustang and Mayes Hughes",
                }
            ]
        }
    ];

    var matchData2 =  [
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

    var matches = new Matches(matchData2);

    var editFunction = function() {
        matches.changeMode.call(matches.component);
    };

    var header = new Header("Matches", "Edit Matches", "TBD", editFunction);

})();
