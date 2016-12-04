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
                        if (pair.latePenalty.value) {
                            pair.latePenalty = {
                                "btn-raised": false,
                                "value": false
                            };
                            penaltyType = "zero";
                        }
                        else{
                            pair.latePenalty = {
                                "btn-raised": true,
                                "value": true
                            };
                        }
                    }
                    else if (penaltyType === "miss") {
                        if (pair.absentPenalty.value) {
                            pair.absentPenalty = {
                                "btn-raised": false,
                                "value": false
                            };
                            penaltyType = "zero";
                        }
                        else{
                            pair.absentPenalty = {
                                "btn-raised": true,
                                "value": true
                            };
                        }
                    }
                    var api = new API();
                    api.addPenalty(gameSession, pair.id, penaltyType, function(response) {
                    });
                },
                removeMatchPair: function(pair, gameSession){
                    var api = new API();
                    var answer = confirm("Are you sure you want to delete this pair?");

                    if(answer) {
                        api.removePairFromMatch(gameSession, pair.id, function() {
                            this.refreshMatches(gameSession);
                        }.bind(this));
                        this.closeModal();
                    }
                },
                saveChanges: function (gameSession, index) {
                    var match = this.matchlist[index];
                    var results = match.results;
                    this.mode = "loading";

                    var api = new API();
                    api.inputMatchResults(gameSession, match.id, results, function() {
                        this.refreshMatches(gameSession);
                    }.bind(this));
                    this.closeModal();
                },
                refreshMatches: function(gameSession) {
                    var api = new API();
                    api.getMatches(gameSession, function(matchData) {
                        this.$dispatch("updateMatches", matchData, gameSession);
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
                          var ROUNDS_TO_PLAY = currentMatch.pairs.length;
                          var results = currentMatch.results;

                          var numPlayed = 0;
                          var ranksTaken = [];
                          results.forEach(function(result){
                              var thisRanking = result.newRanking;
                              var isRankTaken = false;
                              for (var i = 0; i < ranksTaken.length; i++) {
                                  if (ranksTaken[i] === thisRanking){
                                      isRankTaken = true;
                                  }
                              }
                              if(!isRankTaken && result.newRanking){
                                  ranksTaken.push(thisRanking);
                                  numPlayed++;
                              }
                          });

                          var isValid = ROUNDS_TO_PLAY === numPlayed;
                          currentMatch.resultsValid = isValid;
                      }
                  },
                  events: {
                      "updateMatches": function (matchData, gameSession) {
                          if (gameSession === this.gameSession) {
                              this.matches = matchData;
                              this.matches.forEach(function (match, matchIndex) {
                                  var thisMatch = this.matches[matchIndex];
                                  var thisVue = this;
                                  thisMatch.pairs.forEach(function (pair, pairIndex) {
                                      thisVue.$watch(
                                          "matches[" + matchIndex + "].results[" + pairIndex + "]",
                                          thisVue.validateResults.bind(thisVue, match));
                                  });
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
                      }),
                      loading: VueSpinner.ClipLoader
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
