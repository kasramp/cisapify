$(document).ready(function () {
    let token = $('#_csrf').attr('content');
    let header = $('#_csrf_header').attr('content');

    $.ajax({
        type: 'GET',
        beforeSend: function (xhr) {
            xhr.setRequestHeader(header, token);
        },
        url: '/user/songs/count',
        crossDomain: true
    }).done(function (response) {
        let playerLinks = $('.player-nav-link');
        if (response.numberOfSongs > 0) {
            playerLinks.css('display', 'block');
        } else {
            playerLinks.hide();
        }
    }).fail(function (response, status, error) {
        console.log(error);
    });

    $('#dropbox-authentication').click(function (event) {
        event.preventDefault();
        $.ajax({
            type: 'POST',
            beforeSend: function (xhr) {
                xhr.setRequestHeader(header, token);
            },
            url: '/user/dropbox/authentication',
            crossDomain: true
        }).done(function (response) {
            if (response.redirect) {
                window.location.href = response.redirect;
            }
        }).fail(function (response, status, error) {
            console.log(error);
        });
    });

    $('.custom-file-input').on('change', function () {
        let fileName = $(this).val().split('\\').pop();
        $(this).siblings('.custom-file-label').addClass('selected').html(fileName);
    });
});