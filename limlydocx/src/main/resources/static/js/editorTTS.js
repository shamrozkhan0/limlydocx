



        // Message Handler (Auto-hide messages after 5 seconds)
        document.addEventListener("DOMContentLoaded", () => {

            // Initialize Quill Editor
            const quill = new Quill('#editor', {
                theme: 'snow', // Snow theme for toolbar
                modules: {
                    toolbar: { container: '#toolbar' }, // Fixed toolbar
                    // clipboard: true, // Use modified clipboard for inline styles
                    imageResize: {} // Enable image resizing
                }
            });



            // Apply heading levels
            window.setHeading = function (level) {
                quill.format('header', level);
                console.log(`Heading set to H${level}`);
            };



            // Register custom fonts
            const Font = Quill.import('formats/font');
            Font.whitelist = ["sans-serif", "serif", "monospace"];
            Quill.register(Font, true);
            quill.format("font", "serif");


            const spinnerEl = document.querySelector(".spinner-parent");
            const buttons = document.querySelectorAll("#getContent");
            const contentSender = document.querySelector("#contentSender");
            const ContentSenderDB = document.querySelector('#contentSenderDB')



            /**
             * Gets content from the editor and 
             */
            const getEditorContent = () => {
                ContentSenderDB.value = quill.root.innerHTML;
                applyInlineStyles();
                const editorContent = quill.root.innerHTML;
                contentSender.value = editorContent;
                spinnerEl.classList.replace("d-none", "d-block");
            };



            /**
             * This will change the quilljs class into inline styles winthin the editor
             */
            function applyInlineStyles() {
                const pre = document.querySelectorAll(".ql-editor pre");

                if (pre) {
                    pre.forEach(pre => {
                        pre.style.backgroundColor = "black";
                        pre.style.color = "white";
                        pre.style.padding = "10px";
                        pre.style.borderRadius = "5px";
                        pre.style.overflowX = "auto";
                        pre.style.fontFamily = "monospace";
                        element.style.whiteSpace = "pre-wrap";
                    });
                }

                document.querySelectorAll(".ql-editor *").forEach(element => {
                    const computedStyle = window.getComputedStyle(element);
                    element.style.fontFamily = computedStyle.fontFamily;
                    element.style.fontSize = computedStyle.fontSize;
                    element.style.textAlign = computedStyle.textAlign;
                });
            }



            // Attach event listeners to all content buttons
            buttons.forEach((btn) => {
                btn.addEventListener("click", () => {
                    const form = document.querySelector("#content-form");

                    if (btn.value === "PDF") {
                        console.log("Generating PDF...");
                        form.setAttribute("action", "/save-content/pdf");
                    } else if (btn.value === "DOCX") {
                        console.log("Generating DOCX...");
                        form.setAttribute("action", "/save-content/docx");
                    } else {
                        console.error("Invalid Request!");
                    }

                    getEditorContent(); // Process editor content before submitting
                });
            });



  


            // =========================== TTS ==============================

            const speechButton = document.querySelector("#ttsButton");
            const speechIcon = speechButton.querySelector("i");


            quill.on("selection-change", (range) => {
                speechButton.parentElement.classList.add("tts-shown")
                speechButton.classList.replace("d-none", "d-flex")
                getSelectionTextAndActivateButton(range);
            });



            function getSelectionTextAndActivateButton(range) {
                if (range && range.length > 0) {
                    const selectedText = quill.getText(range.index, range.length).trim();
                    // console.log("Selected text:", selectedText);
                    speechButton.classList.replace("text-muted", "text-primary")
                    speechButton.disabled = false;
                    speechButton.onclick = () => TTS(selectedText)
                } else {
                    speechButton.parentElement.classList.remove("tts-shown")
                    speechButton.disabled = true
                    speechButton.classList.replace("text-primary", "text-muted")

                    setTimeout(() => {
                        speechButton.classList.replace("d-flex", "d-none")
                    }, 500)

                    speechSynthesis.cancel()
                }
            }





            function TTS(text) {
                console.log("ewjooqd")
                const voice = speechSynthesis.getVoices();
                const naturalVoice = voice.find(vocie => voice.name === "Google US English") || voice[0];

                console.log("jweoqwe")
                speechIcon.classList.replace("fa-play", "fa-pause")

                const utterace = new SpeechSynthesisUtterance(text);
                utterace.voice = naturalVoice;
                speechSynthesis.speak(utterace)


                utterace.onend = () => {
                    if (speechIcon.classList.contains("fa-pause")) {
                        speechIcon.classList.replace("fa-pause", "fa-play");
                    }

                    speechSynthesis.cancel()
                }
            }




            // =========================== TTS ==============================



        });