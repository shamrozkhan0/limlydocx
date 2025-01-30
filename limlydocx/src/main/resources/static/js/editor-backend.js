document.addEventListener("DOMContentLoaded", () => {

    const button = document.getElementById("getContent");
    const editor = document.getElementById("editor");



    /**
     * Function to get the content of the editor
     */
    const getEditorContent = () => {

        const content = editor.innerHTML;
        let format = "pdf";

        filterContent(content, format);
    };



    /**
     * Filter the unwanted tags from the content
     * 
     * @param {*} content - The content (HTML) to filter
     * @param {*} format - Either "pdf" or "docx", specifying the output format
     */
    const filterContent = (content, format) => {

        let tempDiv = document.createElement("div");
        tempDiv.innerHTML = content;

        let filteredHTML = '';

        tempDiv.querySelectorAll('img, p, pre').forEach((element) => {
            filteredHTML += element.outerHTML;  
        });

        console.log("Filtered Content:", filteredHTML);

        tempDiv.remove();
        sendContentToSpringboot(filteredHTML);

    }



    /**
     * Function to send the content to the springboot
     * @param {String} content  
     */
    const sendContentToSpringboot = (content) => {
        $.ajax({
            type: "POST",
            url: "/savecontent",
            data: content,  // Send the raw HTML content
            contentType: "text/html",  // Specify that you're sending raw HTML
            success: function (response) {
                console.log("Data sent successfully");
            },
            error: function (error) {
                console.log("Data not sent successfully");
            }
        });
    }





    button.addEventListener("click", getEditorContent);


});



