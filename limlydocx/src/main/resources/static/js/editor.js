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
            },
            imageResize: {},
        },
    });

    // Set default font size and family
    quill.format('size', '12px'); // initial font size
    quill.format('font', 'arial');




    // Initialize Bootstrap tooltips
    var tooltipElements = document.querySelectorAll('[data-bs-toggle="tooltip"]');
    tooltipElements.forEach(function (element) {
        new bootstrap.Tooltip(element);
    });




    const counterElement = document.querySelector('#counter');



    // Editor words counter
    quill.on('text-change', function () {
        counterElement.innerHTML = quill.getLength() - 1;
    });


    //======================= Getting Content =====================================

    // trigger getEditorContent() function
    const button = document.getElementById("getContent");
    const spinnerEl = document.querySelector(".spinner-parent");


    /**
     * Function to get the content of the editor
     */
    const getEditorContent = () => {
        const editorContent = quill.root.innerHTML;

        console.log(`Editor Content: ${editorContent}`);
        console.log(`Content Length ${quill.getLength() - 1}`)

        spinnerEl.classList.replace("d-none", "d-block")
        sendContentToSpringboot(editorContent);
    };

    // Attach the event listener to the button
    button.addEventListener("click", getEditorContent);


    // ======================= Custom Features =====================================







});


