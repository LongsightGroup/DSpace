$('.responsive-icon').on('click', function() {
    var regionResponsive = $(this).next('.responsive-collapse')
    if( regionResponsive.is(':hidden') ) {
        regionResponsive.show();
        $(this).addClass('active');
    } else {
        regionResponsive.hide();
        $(this).removeClass('active');
    }
});