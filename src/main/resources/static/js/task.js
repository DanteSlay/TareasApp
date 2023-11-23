$(document).ready(function () {
    $('.lnkConfirmDelete').on('click', function (e) {
        e.preventDefault();
        let id = $(this).data("task-id");
        console.log(id)
        $.get("/home/deleteModal?id=" + id, function (data) {
            $('#paraModal').html(data);
        })
    });

    $('.lnkViewTask').on('click', function (e) {
        e.preventDefault();
        let id = $(this).data("task-id");
        console.log(id)
        $.get("/home/viewTask?id=" + id, function (data) {
            $('#paraModal').html(data);
        })
    });



});





