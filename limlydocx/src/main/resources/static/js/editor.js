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
                    // Add custom handlers here if needed
                }
            },
            imageResize: {},
        },
        placeholder: 'Start typing your document...',
    });

    // Set default font size and family
    quill.format('size', '12px');
    quill.format('font', 'arial');

    var tooltipElements = document.querySelectorAll('[data-bs-toggle="tooltip"]');
    tooltipElements.forEach(function (element) {
        new bootstrap.Tooltip(element);
    });






});