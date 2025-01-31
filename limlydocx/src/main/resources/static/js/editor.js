document.addEventListener("DOMContentLoaded", () => {
    // Configure custom fonts
    const Font = Quill.import('formats/font');
    Font.whitelist = ['arial', 'times-new-roman', 'courier-new'];
    Quill.register(Font, true);

    // Configure custom sizes
    const Size = Quill.import('attributors/style/size');
    Size.whitelist = ['8px', '10px', '12px', '14px', '16px', '20px', '24px', '32px', '48px'];
    Quill.register(Size, true);

    // Initialize Quill editor
    const quill = new Quill('#editor', {
        theme: 'snow',
        modules: {
            toolbar: {
                container: '#toolbar',
                handlers: {
                    // Custom image handler
                    image: () => {
                        const input = document.createElement('input');
                        input.setAttribute('type', 'file');
                        input.setAttribute('accept', 'image/*');
                        input.click();

                        input.onchange = () => {
                            const file = input.files[0];
                            if (file) {
                                const reader = new FileReader();
                                reader.onload = (e) => {
                                    const imageUrl = e.target.result;
                                    const range = quill.getSelection();
                                    quill.insertEmbed(range.index, 'image', imageUrl, 'user');
                                };
                                reader.readAsDataURL(file);
                            }
                        };
                    }
                }
            },
            imageResize: {},
        },
    });

    // Set default font size and family
    quill.format('size', '12px');
    quill.format('font', 'arial');




    // Initialize Bootstrap tooltips
    var tooltipElements = document.querySelectorAll('[data-bs-toggle="tooltip"]');
    tooltipElements.forEach(function (element) {
        new bootstrap.Tooltip(element);
    });


    //======================= Getting Content =====================================


    const button = document.getElementById("getContent");



    /**
     * Function to get the content of the editor
     */
    const getEditorContent = () => {
        const editorContent = quill.root.innerHTML;
        console.log(`Editor Content: ${editorContent}`);
        console.log(`Content Length ${quill.getLength() -1}`)
        sendContentToSpringboot(editorContent);
    };




    /**
     * Function to send the content to the Spring Boot backend
     * @param {String} content - The filtered HTML content
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
    };

    // Attach the event listener to the button
    button.addEventListener("click", getEditorContent);



// ======================= Custom Features =====================================

const counterElement = document.querySelector('#counter');

// Words counter
quill.on('text-change', function () {
    counterElement.innerHTML = quill.getLength() - 1;
});






});


