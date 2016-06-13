"use strict";

var ladderData = [
  {
    position: 1,
    name: "Alex Land and David Li",
    playingStatus: {
      status: "Playing",
      'btn-success': true
    }
  },
  {
    position: 2,
    name: "Gordon Shieh and Samuel Kim",
    playingStatus: {
      status: "Playing",
      'btn-success': true
    }
  },
  {
    position: 3,
    name: "Jas Jassal and Noor Randhawa",
    playingStatus: {
      status: "Playing",
      'btn-success': true
    }
  },
  {
    position: 4,
    name: "Constantin Koval and Raymond Chan",
    playingStatus: {
      status: "Playing",
      'btn-success': true
    }
  }
];

var editFunction = function() {
    console.log("onCLick");
};

var header = new Header("Ladder", "Edit Ladder", editFunction);
var ladder = new Ladder(ladderData);
