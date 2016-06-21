var API = (function() {
    "use strict";

    var SERVER_URL = "http://localhost:8000/api";

    function API() {
    }

    API.prototype.getLadder = function(callback) {
        $.ajax({
            method: "GET",
            url: SERVER_URL + "/index"
        })
        .done(function(response) {
            callback(JSON.parse(response));
        });
    }

    // newStatus must be "playing" or "not playing"
    API.prototype.updatePairStatus = function(pairId, newStatus, callback) {
        $.ajax({
            method: "PATCH",
            url: SERVER_URL + "/" + pairId + "/" + newStatus;
        })
        .done(function(response) {
            if (callback) {
                callback(JSON.parse(response));
            }
        });
    }

    API.prototype.addPair = function(pairId, position, callback) {
        $.ajax({
            method: "POST",
            url: SERVER_URL + "/index/add",
            data: {
                "id": pairId,
                "Position": position
            }
        })
        .done(function(response) {
            if (callback) {
                callback(JSON.parse(response));
            }
        });
    }

    return API;

})();
