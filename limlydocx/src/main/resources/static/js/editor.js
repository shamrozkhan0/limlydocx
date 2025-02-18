document.addEventListener("DOMContentLoaded", () => {

    const button = document.querySelector(".theme-btn");
    const body = document.querySelector("body");

    /**
     * Load Theme For Editor 
     */
    function loadEditorTheme(){
        const savedTheme = localStorage.getItem("theme");
        
        if (savedTheme) {
        body.classList.remove("editor-dark", "editor-light");
        body.classList.add(savedTheme);
        }
    }

    loadEditorTheme();



    /**
     * Chnage The editor Theme based on User Response
     */
    function changeEditorTheme() {
        if (body.classList.contains("editor-dark")) {
            body.classList.replace("editor-dark", "editor-light");
            
            localStorage.setItem("theme", "editor-light");
        } else {
            body.classList.replace("editor-light", "editor-dark");
            localStorage.setItem("theme", "editor-dark");
        }
    }

    button.addEventListener("click", changeEditorTheme);


});
