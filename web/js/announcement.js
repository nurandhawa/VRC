(function () {
    "use strict";

    $.material.options.validate = false;
    $.material.init();

    var ANNOUNCEMENT_DIV_ID = "#announcements";

    var announcementComponent = new Vue({
        el: ANNOUNCEMENT_DIV_ID,
        data: {
            announcements: []
        }
    });

    var api = new API();
    api.getAnnouncements(function(newAnnouncements) {
        announcementComponent.announcements = newAnnouncements;
    });
})();
