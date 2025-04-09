import EditorJsHtml from "https://cdn.jsdelivr.net/npm/editorjs-html@3.4.0/+esm";

document.addEventListener("DOMContentLoaded", () => {

    const editor = new EditorJS({
        holder: 'editor',
        autofocus: true,
        placeholder: "Type / To Continue",
        tools: {
            header: {
                class: Header,
                inlineToolbar: true,
                config: {
                    placeholder: 'Enter a heading',
                    levels: [1, 2, 3, 4, 5, 6],  // Enable all heading levels
                    defaultLevel: 2  // Default to H2
                }
            },
            paragraph: {
                class: Paragraph, // Added class
                inlineToolbar: true
            },
            // list: {
            //     class: List, // Fix: Reference correctly
            //     inlineToolbar: true
            // },
            image: {
                class: ImageTool,
                config: { endpoints: { byFile: 'YOUR_UPLOAD_ENDPOINT' } }
            },
            embed: {
                class: Embed // Fix: Reference correctly
            },
            // table: {
            //     class: Table // Fix: Reference correctly
            // },
            code: {
                class: CodeTool, // Fix: Reference correctly
                inlineToolbar: true,
            },
            quote: {
                class: Quote // Fix: Reference correctly
            },
            checklist: {
                class: Checklist // Fix: Reference correctly
            },
            delimiter: {
                class: Delimiter // Fix: Reference correctly
            },
            marker: {
                class: Marker // Fix: Reference correctly
            },
            inlineCode: {
                class: InlineCode // Fix: Reference correctly
            }
        }
    });

    const editorJsHtml = EditorJsHtml();

    // getContent button event listener
    document.querySelector("#getContent").addEventListener("click", getContent);


    /**
     * Get the content from the editor and parse it to html
     */
    function getContent() {
        editor.save().then((outputData) => {
            const html = editorJsHtml.parse(outputData);
            console.log(html);
            filterHtmlContent(html);
        });
    }



    /**
     * Filter the html content to remove unwanted characters
     */
    function filterHtmlContent(content) {

        if (typeof content === "string") {}

        let stringContent = String(content).replaceAll(",","</br>")
        console.log(stringContent);
        transferContentToInput(stringContent)

    }



    function transferContentToInput(content) {
        const input = document.querySelector("#content");
        input.value = content;

        sendContentToBackend();
    }


    function sendContentToBackend(){
        const form = document.querySelector("#editorForm");
        form.setAttribute("action", "/editor/savecontent/pdf");   
    }






})