var API = (function() {
    "use strict";

    var SERVER_URL = "http://localhost:8000/api";
    var LADDER_ENDPOINT = "/ladder";
    var STATUS_PARAM = "?newStatus=";
    var POSITION_PARAM = "?position=";
    var PENALTY_PARAM = "?penalty=";

    function API() {
        this.STATUS_PLAYING = "playing";
        this.STATUS_NOT_PLAYING = "not playing";
        this.PENALTY_MISS = "miss";
        this.PENALTY_LATE = "late";
        this.PENALTY_ACCIDENT = "accident";
    }

    API.prototype.getLadder = function(doneCallback, failCallback) {
        $.ajax({
            method: "GET",
            url: SERVER_URL + LADDER_ENDPOINT
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
                alert(responseBody.message);
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
                alert(responseBody.message);
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
                alert(responseBody.message);
            }
        });
    }

    API.prototype.prepareNewPlayer = function(firstName, lastName, phoneNumber) {
        return {
            "firstName": firstName,
            "lastName": lastName,
            "phoneNumber": phoneNumber
        };
    }

    API.prototype.prepareExistingPlayer = function(playerId) {
        return {
            "existingId": playerId
        };
    }

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
                alert(responseBody.message);
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
                alert(responseBody.message);
            }
        });
    };

    // Valid penalties are "late", "miss" or "accident"
    API.prototype.addPenalty = function (pairId, penalty, doneCallback, failCallback) {
        $.ajax({
            method: "POST",
            url: SERVER_URL + "/matches/" + pairId + PENALTY_PARAM + penalty
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
                alert(responseBody.message);
            }
        });
    };

    API.prototype.getMatches = function (doneCallback) {
        $.ajax({
            method: "GET",
            url: SERVER_URL + "/matches"
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
                alert(responseBody.message);
            }
        });
    };

    API.prototype.inputMatchResults = function (matchId, doneCallback, failCallback) {
        $.ajax({
            method: "PATCH",
            url: SERVER_URL + "/matches/" + matchId,
            data: {
                results: [
                    //TODO: determine how to send results to back-end (document currently says array of ints?)
                ]
            }
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
                alert(responseBody.message);
            }
        });
    };

    API.prototype.removePairFromMatch = function (pairId, doneCallback, failCallback) {
        $.ajax({
            method: "DELETE",
            url: SERVER_URL + "/matches/" + pairId
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
                alert(responseBody.message);
            }
        });
    };

    API.prototype.userLogin = function (email, password, doneCallback, failCallback) {
        $.ajax({
            method: "POST",
            url: SERVER_URL + "/login",
            data: {
                "Email": email,
                "Password": password
            }
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
                alert(responseBody.message);
            }
        });
    };

    API.prototype.userRegistration = function (email, password, doneCallback, failCallback) {
        $.ajax({
            method: "POST",
            url: SERVER_URL + "/login/new",
            data: {
                "Email": email,
                "Password": password
            }
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
                alert(responseBody.message);
            }
        });
    };

    return API;

})();
