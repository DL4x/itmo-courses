window.notify = function (message, messageType) {
    $.notify(message, {
        position: 'right bottom',
        className: messageType
    });
}

window.success = function (obj) {
    return function (response) {
        const $error = $(obj).find(".error");
        if (response['error']) {
            $error.text(response['error']);
        } else {
            location.href = response['redirect'];
        }
    }
}

window.ajax = function (data, success) {
    $.ajax({
        type: 'POST',
        url: '',
        dataType: 'json',
        data,
        success
    });
}

window.submitEnterOrRegistration = function (name) {

    $(function () {
        $(`.${name} form`).submit(function () {
            const login = $(this).find("input[name='login']").val();
            const password = $(this).find("input[name='password']").val();

            ajax({
                    action: name,
                    login,
                    password
                },
                success(this));

            return false;
        });
    })
}
