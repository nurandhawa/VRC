/**
 * Created by David on 2016-06-15.
 */

var EDIT_BUTTON_HTML = '<a v-on:click="editMatch()" class="edit-button btn btn-info btn-fab btn-fab-mini"><i class="material-icons md-light">create</i></a>';

var Matches = (function () {
    function Matches(matchData, elementId) {
        Vue.component("matches-modal", {
            template: "#matchesModalTemplate",
            props: {
                active: "active",
                isActive: "isActive",
                matchlist: "matchlist",
                show: {
                    type: Boolean,
                    required: true,
                    twoWay: true
                }
            },
            methods: {
                modalActiveContent: function (i) {
                    return this.active === i;
                },
                closeModal: function() {
                    this.show = false;
                    this.active = false;
                },
                applyPenalty: function(gameSession, pair, penaltyType, event) {
                    if (penaltyType === "late") {
                        pair.latePenalty = {
                            "btn-raised": true
                        };
                    }
                    else if (penaltyType === "miss") {
                        pair.absentPenalty = {
                            "btn-raised": true
                        };
                    }
                    var api = new API();
                    api.addPenalty(gameSession, pair.id, penaltyType, function(response) {
                    });
                },
                saveChanges: function (gameSession, index) {
                    var match = this.matchlist[index];
                    var results = [];

                    for (var round = 0; round < match.pairs.length; round++) {
                        var roundResult = [];
                        for (var pair in match.pairs) {
                            roundResult.push(match.results[pair][round]);
                        }
                        results.push(roundResult);
                    }

                    var api = new API();
                    api.inputMatchResults(gameSession, match.id, results, function() {
                        this.refreshMatches(gameSession);
                    }.bind(this));
                    this.closeModal();
                },
                refreshMatches: function(gameSession) {
                    var api = new API();
                    api.getMatches(gameSession, function(matchData) {
                        this.$broadcast("updateMatches", matchData, gameSession);
                    }.bind(this));
                }
            }
        });

        this.component = new Vue({
          el: "#tabs",
          data: {
              activeGameSession: "latest",
              allDone: false
          },
          methods: {
              setActive: function (tabClicked) {
                  this.activeGameSession = tabClicked;
              }
          },
          watch: {
              "activeGameSession": function (newVal, oldVal) {
                  if (newVal === "previous") {
                      $("#reorderLadderButton").prop("disabled", false);
                  }
                  else {
                      $("#reorderLadderButton").prop("disabled", !this.allDone);
                  }
              }
          },
          events: {
              "matchesDone": function (newVal) {
                  if (newVal === true) {
                      this.allDone = true;
                      $("#reorderLadderButton").prop("disabled", false);
                  }
                  else {
                      this.allDone = false;
                      $("#reorderLadderButton").prop("disabled", true);
                  }
              }
          },
          components: {
              "matches": Vue.extend({
                  template: "#matchesComponentTemplate",
                  data: function() {
                      return {
                          active: 0,
                          showModal: false,
                          matches: matchData,
                          allDone: false,
                          mode: "read"
                      };
                  },
                  props: ["index", "gameSession"],
                  methods: {
                      openModal: function (index) {
                          this.showModal = true;
                          this.active = index;
                          return this.active;
                      },
                      validateResults: function (currentMatch, newVal, oldVal) {
                          var numRounds = newVal.length;
                          var CORRECT_ROUNDS_PLAYED = 2;
                          var CORRECT_ROUNDS_NOT_PLAYED = numRounds - CORRECT_ROUNDS_PLAYED;

                          var isValid = newVal.every(function (pairRecord) {
                              var roundsNotPlayed = pairRecord.filter(function (entry) {
                                  return entry === "-";
                              }).length;
                              return roundsNotPlayed === CORRECT_ROUNDS_NOT_PLAYED;
                          });

                          currentMatch.resultsValid = isValid;
                      }
                  },
                  events: {
                      "updateMatches": function (matchData, gameSession) {
                          if (gameSession === this.gameSession) {
                              this.matches = matchData;
                              this.matches.forEach(function(match, index) {
                                  this.$watch("matches[" + index + "].results", this.validateResults.bind(this, match));
                              }.bind(this));

                              var matchesDone = this.matches.every(function(match) {
                                  return match.isDone;
                              });

                              if (gameSession === "latest") {
                                  this.$dispatch("matchesDone", matchesDone);
                              }
                          }
                      },
                      "changeMode": function () {
                          if (this.mode === "read") {
                              this.mode = "edit";
                          }
                          else {
                              this.mode = "read";
                          }
                      }
                  },
                  components: {
                      edit: Vue.extend({
                          props: ["column","index"],
                          template: EDIT_BUTTON_HTML,
                          methods: {
                              editMatch: function () {
                                  this.$parent.openModal(this.index);
                              }
                           }
                      }),
                      read: Vue.extend({
                          template: "<a></a>"
                      })
                  }
              })
          }
        });
    }

    Matches.prototype.changeMode = function () {
        this.component.$broadcast("changeMode");
    };

    Matches.prototype.updateMatches = function (matchData, gameSession) {
        this.component.$broadcast("updateMatches", matchData, gameSession);
    };

    Matches.prototype.isAllDone = function() {
        if (this.component.activeGameSession === "latest") {
            return this.component.allDone;
        }
        else {
            return true;
        }
    };

    return Matches;
})();
