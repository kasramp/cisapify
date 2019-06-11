$(document).ready(function () {
    $("#dropbox-authentication").click(function (event) {
        var token = $('#_csrf').attr('content');
        var header = $('#_csrf_header').attr('content');
        event.preventDefault();
        $.ajax({
            type: 'POST',
            beforeSend: function(xhr) {
                xhr.setRequestHeader(header, token);
            },
            url: '/user/dropbox/authentication',
            crossDomain: true
        }).done(function (response) {
            if(response.redirect) {
                window.location.href = response.redirect;
            }
        }).fail(function (response, status, error) {
            console.log(error);
        });
    });
});