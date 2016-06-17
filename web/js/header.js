var Header = (function() {
    "use strict";

    function Header(titleText, buttonText, lastModified, editOnClick) {
        this.component = new Vue({
            el: '#header',
            data: {
                title: titleText,
                timestamp: lastModified,
                buttonTitle: buttonText
            },
            methods: {
                onClick: editOnClick
            }
        });
    }

    return Header;
})();
