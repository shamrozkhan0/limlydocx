document.addEventListener("DOMContentLoaded", () => {


    /**
     * Auto hides the message after 5 seconds if present
     */
    function removesMessageIfPresent() {
        const messageElement = document.querySelector(".message");
        if (messageElement) {
            console.log("Message detected. Auto-hide in 5s...");
            setTimeout(() => {
                messageElement.classList.add("d-none");
                console.log("Message hidden.");
            }, 5000);
        }
    }

    removesMessageIfPresent();



    });