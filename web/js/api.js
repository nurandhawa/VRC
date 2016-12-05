var API = (function() {
    "use strict";

    var SERVER_URL = "api";
    var LADDER_ENDPOINT = "/ladder";
    var STATUS_PARAM = "?newStatus=";
    var POSITION_PARAM = "?position=";
    var PENALTY_PARAM = "&penalty=";
    var GAMESESSION_PARAM = "?gameSession=";
    var EMAIL_PARAM = "?email=";

    var ROLE_ADMINISTRATOR = "ADMINISTRATOR";
    var ROLE_COOKIE = "userRole";
    var ID_COOKIE = "playerId";

    var RESPONSE_STATUS_OK = "OK";
    var RESPONSE_STATUS_ERROR = "ERROR";
    var RESPONSE_STATUS_AUTH_ERROR = "AUTH_ERROR";

    var defaultFailCallback = function(response) {
        alert(response.message);

        if (response.status === RESPONSE_STATUS_AUTH_ERROR) {
            window.location.href = "/index.html";
        }
    };

    function API() {
        this.playingStatus = Object.freeze({
            PLAYING: "playing",
            NOT_PLAYING: "not playing"
        });
        this.STATUS_PLAYING = "playing";
        this.STATUS_NOT_PLAYING = "not playing";
        this.penalty = Object.freeze({
            MISS: "miss",
            LATE: "late",
            ACCIDENT: "accident"
        });
        this.PENALTY_MISS = "miss";
        this.PENALTY_LATE = "late";
        this.PENALTY_ACCIDENT = "accident";
        this.gameSession = Object.freeze({
            LATEST: "latest",
            PREVIOUS: "previous"
        });
    }

    API.prototype.getLadder = function(doneCallback, failCallback) {
        $.ajax({
            method: "GET",
            url: SERVER_URL + LADDER_ENDPOINT
        })
            .done(function(response) {
                if (doneCallback) {
                    var userRole = Cookies.get(ROLE_COOKIE);
                    var isAdministrator = userRole === ROLE_ADMINISTRATOR;
                    var playerId = parseInt(Cookies.get(ID_COOKIE));

                    var ladderData = {};
                    ladderData = JSON.parse(response);
                    ladderData.players = [];
                    ladderData.pairs.forEach(function(pair) {
                        var player1 = pair.players[0];
                        var player2 = pair.players[1];

                        var player1Name = player1.firstName + " " + player1.lastName;
                        var player2Name = player2.firstName + " " + player2.lastName;

                        pair.teamName = player1Name + " and " + player2Name;
                        pair.p1Name = player1Name;
                        pair.p2Name = player2Name;
                        pair.playingStatus = pair.isPlaying ? "playing" : "notplaying";
                        pair.showPlayingStatus = (isAdministrator ||
                                                player1.id === playerId ||
                                                player2.id === playerId) ? true : false;
                        ladderData.players.push({
                            label: player1Name,
                            id: player1.id
                        });
                        ladderData.players.push({
                            label: player2Name,
                            id: player2.id
                        });
                    });
                    doneCallback(ladderData);
                }
            })
            .fail(function(response) {
                if (failCallback) {
                    failCallback(response);
                }
                else {
                    var responseBody = JSON.parse(response.responseText);
                    defaultFailCallback(responseBody);
                }
            });
    };

    // newStatus must be "playing" or "not playing"
    API.prototype.updatePairStatus = function(pairId, newStatus, doneCallback, failCallback) {
        $.ajax({
            method: "PATCH",
            url: SERVER_URL + LADDER_ENDPOINT + "/" + pairId + STATUS_PARAM + newStatus
        })
            .done(function(response) {
                if (doneCallback) {
                    doneCallback(JSON.parse(response));
                }
            })
            .fail(function(response) {
                if (failCallback) {
                    failCallback(response);
                }
                else {
                    var responseBody = JSON.parse(response.responseText);
                    defaultFailCallback(responseBody);
                }
            });
    };

    API.prototype.updatePairPosition = function(pairId, newPosition, doneCallback, failCallback) {
        $.ajax({
            method: "PATCH",
            url: SERVER_URL + LADDER_ENDPOINT + "/" + pairId + POSITION_PARAM + newPosition
        })
            .done(function(response) {
                if (doneCallback) {
                    doneCallback(JSON.parse(response));
                }
            })
            .fail(function(response) {
                if (failCallback) {
                    failCallback(response);
                }
                else {
                    var responseBody = JSON.parse(response.responseText);
                    defaultFailCallback(responseBody);
                }
            });
    };

    API.prototype.prepareNewPlayer = function(firstName, lastName, phoneNumber) {
        var onlyDigitsRegex = /\d/g;
        var sanitizedPhoneNumber = "";
        if (phoneNumber !== "") {
            sanitizedPhoneNumber = phoneNumber.match(onlyDigitsRegex).join("");
        }
        return {
            "firstName": firstName,
            "lastName": lastName,
            "phoneNumber": sanitizedPhoneNumber,
            "existingId": -1
        };
    };

    API.prototype.prepareExistingPlayer = function(playerId) {
        return {
            "existingId": playerId
        };
    };

    // player1 and player2 must be created using the prepare methods above
    API.prototype.addPair = function(players, position, doneCallback, failCallback) {
        $.ajax({
            method: "POST",
            url: SERVER_URL + "/ladder",
            data: JSON.stringify({
                players: players,
                "position": position
            })
        })
            .done(function(response) {
                if (doneCallback) {
                    doneCallback(JSON.parse(response));
                }
            })
            .fail(function(response) {
                if (failCallback) {
                    failCallback(response);
                }
                else {
                    var responseBody = JSON.parse(response.responseText);
                    defaultFailCallback(responseBody);
                }
            });
    };

    API.prototype.removePair = function (pairId, doneCallback, failCallback) {
        $.ajax({
            method: "DELETE",
            url: SERVER_URL + "/ladder/" + pairId
        })
            .done(function (response) {
                if (doneCallback) {
                    doneCallback(JSON.parse(response));
                }
            })
            .fail(function(response) {
                if (failCallback) {
                    failCallback(response);
                }
                else {
                    var responseBody = JSON.parse(response.responseText);
                    defaultFailCallback(responseBody);
                }
            });
    };

    // Valid penalties are "late", "miss" or "accident"
    API.prototype.addPenalty = function (gameSession, pairId, penalty, doneCallback, failCallback) {
        $.ajax({
            method: "POST",
            url: SERVER_URL + "/matches/" + pairId + GAMESESSION_PARAM + gameSession +
            PENALTY_PARAM + penalty
        })
            .done(function (response) {
                if (doneCallback) {
                    doneCallback(JSON.parse(response));
                }
            })
            .fail(function(response) {
                if (failCallback) {
                    failCallback(response);
                }
                else {
                    var responseBody = JSON.parse(response.responseText);
                    defaultFailCallback(responseBody);
                }
            });
    };

    API.prototype.reorderLadder = function (gameSession, doneCallback, failCallback) {
        $.ajax({
            method: "POST",
            url: SERVER_URL + "/matches" + GAMESESSION_PARAM + gameSession
        })
        .done(function (response) {
            if (doneCallback) {
                doneCallback(response);
            }
        })
        .fail(function (response) {
            if (failCallback) {
                failCallback(response);
            }
            else {
                var responseBody = JSON.parse(response.responseText);
                defaultFailCallback(responseBody);
            }
        });
    };

    API.prototype.getMatches = function (gameSession, doneCallback, failCallback) {
        $.ajax({
            method: "GET",
            url: SERVER_URL + "/matches" + GAMESESSION_PARAM + gameSession
        })
            .done(function (response) {
                var matches = JSON.parse(response);
                matches.forEach(function(match, i) {
                    match.scorecardIndex = i;
                    match.gameSession = gameSession;
                    match.resultsValid = false;
                    match.results = [];
                    match.pairs.forEach(function(pair, index) {
                        var result;
                        if (match.isDone) {
                            result = {
                                pairId: pair.id,
                                newRanking: index + 1
                            };
                        } else {
                            result = {
                                pairId: pair.id,
                                newRanking: 0
                            };
                        }
                        match.results.push(result);

                        pair.absentPenalty = {
                            'btn-raised': false
                        };
                        pair.latePenalty = {
                            'btn-raised': false
                        };
                    });
                });
                if (doneCallback) {
                    doneCallback(matches);
                }
            })
            .fail(function(response) {
                if (failCallback) {
                    failCallback(response);
                }
                else {
                    var responseBody = JSON.parse(response.responseText);
                    defaultFailCallback(responseBody);
                }
            });
    };

    API.prototype.inputMatchResults = function (gameSession, matchId, results, doneCallback, failCallback) {
        $.ajax({
            method: "PATCH",
            url: SERVER_URL + "/matches/" + matchId + GAMESESSION_PARAM + gameSession,
            data: JSON.stringify({
                results: results
            })
        })
            .done(function (response) {
                if (doneCallback) {
                    doneCallback(JSON.parse(response));
                }
            })
            .fail(function(response) {
                if (failCallback) {
                    failCallback(response);
                }
                else {
                    var responseBody = JSON.parse(response.responseText);
                    defaultFailCallback(responseBody);
                }
            });
    };

    API.prototype.removePairFromMatch = function (gameSession, pairId, doneCallback, failCallback) {
        $.ajax({
            method: "DELETE",
            url: SERVER_URL + "/matches/" + pairId + GAMESESSION_PARAM + gameSession,
        })
            .done(function (response) {
                if (doneCallback) {
                    doneCallback(JSON.parse(response));
                }
            })
            .fail(function(response) {
                if (failCallback) {
                    failCallback(response);
                }
                else {
                    var responseBody = JSON.parse(response.responseText);
                    defaultFailCallback(responseBody);
                }
            });
    };

    API.prototype.userLogin = function (email, password, rememberMe, doneCallback, failCallback) {
        $.ajax({
            method: "POST",
            url: SERVER_URL + "/login",
            data: JSON.stringify({
                "email": email,
                "password": password,
                "rememberMe": rememberMe
            })
        })
            .done(function (response) {
                if (doneCallback) {
                    doneCallback(JSON.parse(response));
                }
            })
            .fail(function(response) {
                if (failCallback) {
                    failCallback(response);
                }
                else {
                    var responseBody = JSON.parse(response.responseText);
                    defaultFailCallback(responseBody);
                }
            });
    };

    API.prototype.userRegistration = function (email, password, securityInfo, playerId, doneCallback, failCallback) {
        $.ajax({
            method: "POST",
            url: SERVER_URL + "/register",
            data: JSON.stringify({
                "email": email,
                "password": password,
                "securityQuestion": securityInfo.question,
                "securityAnswer": securityInfo.answer,
                "playerId": playerId
            })
        })
            .done(function (response) {
                if (doneCallback) {
                    doneCallback(JSON.parse(response));
                }
            })
            .fail(function(response) {
                if (failCallback) {
                    failCallback(response);
                }
                else {
                    var responseBody = JSON.parse(response.responseText);
                    defaultFailCallback(responseBody);
                }
            });
    };

    API.prototype.editPlayer = function (email, password, firstName, lastName, playerId, doneCallback, failCallback) {
        $.ajax({
            method: "PATCH",
            url: SERVER_URL + "/register",
            data: JSON.stringify({
                "email": email,
                "password": password,
                "firstName": firstName,
                "lastName": lastName,
                "playerId": playerId
            })
        })
            .done(function (response) {
                if (doneCallback) {
                    doneCallback(JSON.parse(response));
                }
            })
            .fail(function(response) {
                if (failCallback) {
                    failCallback(response);
                }
                else {
                    var responseBody = JSON.parse(response.responseText);
                    defaultFailCallback(responseBody);
                }
            });
    };

    API.prototype.deleteUser = function (playerId, doneCallback, failCallback) {
        $.ajax({
            method: "DELETE",
            url: SERVER_URL + "/register",
            data: JSON.stringify({
                "playerId": playerId
            })
        })
            .done(function (response) {
                if (doneCallback) {
                    doneCallback(JSON.parse(response));
                }
            })
            .fail(function(response) {
                if (failCallback) {
                    failCallback(response);
                }
                else {
                    var responseBody = JSON.parse(response.responseText);
                    defaultFailCallback(responseBody);
                }
            });
    };

    API.prototype.setUserSecurityQuestion = function (email, securityQuestion, answer, doneCallback, failCallback) {
        $.ajax({
            method: "PATCH",
            url: SERVER_URL + "/login/security",
            data: JSON.stringify({
                "email": email,
                "securityQuestion": securityQuestion,
                "answer": answer
            })
        })
            .done(function (response) {
                if (doneCallback) {
                    doneCallback(JSON.parse(response));
                }
            })
            .fail(function(response) {
                if (failCallback) {
                    failCallback(response);
                }
                else {
                    var responseBody = JSON.parse(response.responseText);
                    defaultFailCallback(responseBody);
                }
            });
    };

    API.prototype.getUserSecurityQuestion = function (email, doneCallback, failCallback) {
        $.ajax({
            method: "GET",
            url: SERVER_URL + "/login/reset" + EMAIL_PARAM + email
        })
            .done(function (response) {
                if (doneCallback) {
                    doneCallback(JSON.parse(response));
                }
            })
            .fail(function(response) {
                if (failCallback) {
                    failCallback(response);
                }
                else {
                    var responseBody = JSON.parse(response.responseText);
                    defaultFailCallback(responseBody);
                }
            });
    };

    API.prototype.answerSecurityQuestion = function (email, answer, doneCallback, failCallback) {
        $.ajax({
            method: "POST",
            url: SERVER_URL + "/login/reset",
            data: JSON.stringify({
                "email": email,
                "answer": answer
            })
        })
            .done(function (response) {
                if (doneCallback) {
                    doneCallback(JSON.parse(response));
                }
            })
            .fail(function(response) {
                if (failCallback) {
                    failCallback(response);
                }
                else {
                    var responseBody = JSON.parse(response.responseText);
                    defaultFailCallback(responseBody);
                }
            });
    };

    API.prototype.changePassword = function (email, voucherCode, password, doneCallback, failCallback) {
        $.ajax({
            method: "POST",
            url: SERVER_URL + "/login/change",
            data: JSON.stringify({
                "email": email,
                "voucherCode": voucherCode,
                "password": password
            })
        })
            .done(function (response) {
                if (doneCallback) {
                    doneCallback(JSON.parse(response));
                }
            })
            .fail(function(response) {
                if (failCallback) {
                    failCallback(response);
                }
                else {
                    var responseBody = JSON.parse(response.responseText);
                    defaultFailCallback(responseBody);
                }
            });
    };

    API.prototype.userLogout = function (doneCallback, failCallback) {
        $.ajax({
            method: "POST",
            url: SERVER_URL + "/logout"
        })
            .done(function (response) {
                if (doneCallback) {
                    doneCallback(JSON.parse(response));
                }
            })
            .fail(function(response) {
                if (failCallback) {
                    failCallback(response);
                }
            });
    };

    API.prototype.setTime = function (time, pairId, doneCallback, failCallback) {
        $.ajax({
            method: "PATCH",
            url: SERVER_URL + "/ladder/time/" + pairId,
            data: JSON.stringify({
                "time": time,
            })
        })

            .done(function (response) {
                if (doneCallback) {
                    doneCallback(JSON.parse(response));
                }
            })
            .fail(function(response) {
                if (failCallback) {
                    failCallback(response);
                }
                else {
                    var responseBody = JSON.parse(response.responseText);
                    defaultFailCallback(responseBody);
                }
            });
    };

    API.prototype.getPlayers = function(doneCallback, failCallback) {
        $.ajax({
            method: "GET",
            url: SERVER_URL + "/players"
        })
            .done(function(response) {
                if (doneCallback) {
                    var playerData = JSON.parse(response);
                    playerData.danglingPlayers = [];
                    playerData.players.forEach(function(player){
                        playerData.danglingPlayers.push({
                            label: player.firstName + " " + player.lastName,
                            id: player.id,
                            email: player.email
                        });
                    });
                    playerData.playersWithAccounts = [];
                    playerData.users.forEach(function(user){
                        playerData.playersWithAccounts.push({
                            label: user.firstName + " " + user.lastName,
                            id: user.id,
                            email: user.email
                        });
                    });
                    playerData.allPlayers = [];
                    playerData.playersAndUsers.forEach(function(player){
                        playerData.allPlayers.push({
                            label: player.firstName + " " + player.lastName,
                            id: player.id,
                            email: player.email
                        });
                    });
                    doneCallback(playerData);
                }
            })
            .fail(function(response) {
                if (failCallback) {
                    failCallback(response);
                }
                else {
                    var responseBody = JSON.parse(response.responseText);
                    defaultFailCallback(responseBody);
                }
            });
    };

    API.prototype.getAnnouncementCount = function(doneCallback, failCallback) {
        $.ajax({
            method: "GET",
            url: SERVER_URL + "/announcements/count"
        })
        .done(function(response) {
            if (doneCallback) {
                doneCallback(JSON.parse(response));
            }
        })
        .fail(function(response) {
            if (failCallback) {
                failCallback(response);
            }
            else {
                var responseBody = JSON.parse(response.responseText);
                defaultFailCallback(responseBody);
            }
        });
    };

    API.prototype.getAnnouncements = function(doneCallback, failCallback) {
        $.ajax({
            method: "GET",
            url: SERVER_URL + "/announcements"
        })
        .done(function(response) {
            if (doneCallback) {
                doneCallback(JSON.parse(response));
            }
        })
        .fail(function(response) {
            if (failCallback) {
                failCallback(response);
            }
            else {
                var responseBody = JSON.parse(response.responseText);
                defaultFailCallback(responseBody);
            }
        });
    };

    API.prototype.addAnnouncement = function(announcementTitle, announcementMessage, doneCallback, failCallback) {
        $.ajax({
            method: "POST",
            url: SERVER_URL + "/announcements",
            data: JSON.stringify({
                title: announcementTitle,
                message: announcementMessage
            })
        })
        .done(function(response) {
            if (doneCallback) {
                doneCallback(JSON.parse(response));
            }
        })
        .fail(function(response) {
            if (failCallback) {
                failCallback(response);
            }
            else {
                var responseBody = JSON.parse(response.responseText);
                defaultFailCallback(responseBody);
            }
        });
    };

    API.prototype.editAnnouncement = function (id, announcementTitle, announcementMessage, doneCallback, failCallback) {
        $.ajax({
            method: "PATCH",
            url: SERVER_URL + "/announcements/" + id,
            data: JSON.stringify({
                title: announcementTitle,
                message: announcementMessage
            })
        })
            .done(function (response) {
                if (doneCallback) {
                    doneCallback(JSON.parse(response));
                }
            })
            .fail(function (response) {
                if (failCallback) {
                    failCallback(response);
                }
                else {
                    var responseBody = JSON.parse(response.responseText);
                    defaultFailCallback(responseBody);
                }
            });
    };

    API.prototype.deleteAnnouncement = function(id, doneCallback, failCallback) {
        $.ajax({
            method: "DELETE",
            url: SERVER_URL + "/announcements/" + id
        })
        .done(function(response) {
            if (doneCallback) {
                doneCallback(JSON.parse(response));
            }
        })
        .fail(function(response) {
            if (failCallback) {
                failCallback(response);
            }
            else {
                var responseBody = JSON.parse(response.responseText);
                defaultFailCallback(responseBody);
            }
        });
    };

    return API;

})();
