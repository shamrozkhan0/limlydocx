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
    

    function inputFocus(bla){
        const input = document.querySelector(".input");

        if(focus){
            input.focus();
        }
    }


    const button = document.querySelector('.signupButton').addEventListener("click",(e)=>{
        e.preventDefault();
    })
});


