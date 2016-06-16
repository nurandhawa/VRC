var Header = (function() {
    "use strict";

    function Header(titleText, buttonText, editOnClick) {
        this.component = new Vue({
            el: '#header',
            data: {
                title: titleText,
                timestamp: this.getTime(),
                buttonTitle: buttonText
            },
            methods: {
                onClick: editOnClick
            }
        });
    }

    Header.prototype.getTime = function() {
        // TODO: Use timestamp of when ladder was last modified on the server
        var currentDate = new Date();
        return currentDate.toDateString() + " at " + currentDate.toTimeString();
    };

    return Header;
})();
