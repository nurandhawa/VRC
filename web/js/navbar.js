var Navbar = (function() {
    "use strict";

    function Navbar(userRole) {
        this.component = new Vue({
            el: "#nav",
            data: {
                role: userRole
            }
        });
    }


    return Navbar;
})();
