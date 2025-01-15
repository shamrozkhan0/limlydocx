$(document).ready(function () {
    console.log("i am ready ")

    // Show password / hide password
    $(".fa-eye-slash").click(function() {
        let icon = $(this);
        let passwordField = $("#password");
    
        if (passwordField.prop("type") === "password") {
            passwordField.prop("type", "text"); 
            icon.removeClass("fa-eye-slash").addClass("fa-eye");
        } else {
            passwordField.prop("type", "password"); 
            icon.removeClass("fa-eye").addClass("fa-eye-slash");
        }
    });
    

    let input = document.querySelectorAll('input').forEach((text) => {
        text.addEventListener("keypress", (e) => {
            if (e.key === " ") {
                e.preventDefault(); 
            }
        });
    });

    
});