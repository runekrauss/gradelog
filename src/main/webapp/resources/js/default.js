/*
 * Navigation trotzdem anzeigen, auch wenn man runterscrollt.
 */
jQuery(document).ready(function($) {
    // Die Navigation soll mit einem Slide-Effekt angezeigt werden.
    if ($(window).width() > 1170) {
        var headerHeight = $('.navbar-custom').height();
        $(window).on('scroll', {
                previousTop: 0
            },
            function() {
                var currentTop = $(window).scrollTop();
                // Überprüfe, ob der Benutzer nach oben scrollt.
                if (currentTop < this.previousTop) {
                    // Benutzer scrollt nach oben.
                    if (currentTop > 0 && $('.navbar-custom').hasClass('is-fixed')) {
                        $('.navbar-custom').addClass('is-visible');
                    } else {
                        $('.navbar-custom').removeClass('is-visible is-fixed');
                    }
                } else if (currentTop > this.previousTop) {
                    // Benutzer scrollt nach unten.
                    $('.navbar-custom').removeClass('is-visible');
                    if (currentTop > headerHeight && !$('.navbar-custom').hasClass('is-fixed')) $('.navbar-custom').addClass('is-fixed');
                }
                this.previousTop = currentTop;
            });
    }
});
