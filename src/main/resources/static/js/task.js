$(document).ready(function () {
    $('.lnkConfirmDelete').on('click', function (e) {
        e.preventDefault();
        let id = $(this).data("task-id");
        console.log(id)
        $.get("/deleteModal?id=" + id, function (data) {
            $('#paraModal').html(data);
            $('#miModal').modal('show')
        })
    });
});





