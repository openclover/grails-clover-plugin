var myYUI = {}
myYUI.get = YAHOO.util.Dom.get

myYUI.fade = function(elementId, delay, fadeTime) {
    delay = typeof(delay) != 'undefined' ? delay : 0
    fadeTime = typeof(fadeTime) != 'undefined' ? fadeTime : 1

    this.A = function() {
        var anim = new YAHOO.util.Anim(
            elementId,
            { opacity: {from: 1, to: 0 } },
            fadeTime
        )
        anim.onComplete.subscribe(function() {YAHOO.util.Dom.setStyle(elementId, "display", "none");});
        anim.animate();
    }
    setTimeout("myYUI.A()", delay * 1000);
}

myYUI.appear = function(elementId, delay, fadeTime) {
    delay = typeof(delay) != 'undefined' ? delay : 0
    fadeTime = typeof(fadeTime) != 'undefined' ? fadeTime : 1

    this.A = function() {
        YAHOO.util.Dom.setStyle(elementId, "opacity", 0);
        YAHOO.util.Dom.setStyle(elementId, "display", "block");

        new YAHOO.util.Anim(
            elementId,
            { opacity: {from: 0, to: 1 } },
            fadeTime
        ).animate();
    }
    setTimeout("myYUI.A()", delay * 1000);
}
