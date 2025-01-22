document.addEventListener('DOMContentLoaded', function () {

    const popupButton = document.getElementById('popupButton');
    let selectedText = ''; 

    document.addEventListener('mouseup', function () {
        selectedText = window.getSelection().toString();

        // Log selected text for debugging
        console.log(selectedText);

        // If some text is selected, show the button
        if (selectedText) {
            const selection = window.getSelection().getRangeAt(0).getBoundingClientRect();

            // Position the button near the selected text
            popupButton.style.top = `${selection.top + window.scrollY + 20}px`;
            popupButton.style.left = `${selection.left + window.scrollX + 20}px`;
            popupButton.style.display = 'block'; // Show the button
        } else {
            // Hide the button if no text is selected
            popupButton.style.display = 'none';
        }
    });

    popupButton.click = () => {CheckText();}

    /**
     * Check if the text is selected and then convert the text to speech
     */
    const CheckText = (){
        if (selectedText) {
            console.log("inside the click " + selectedText)
            textToSpeech(selectedText);
        } else {
            alert('No text selected!');
        }
    }


    /**
     * Converts text to speech using the SpeechSynthesis API
     * 
     * @param {*} text passed from the 
     */

    function textToSpeech(text) {
        // Check if the browser supports the SpeechSynthesis API
        if ('speechSynthesis' in window) {
            const synth = window.speechSynthesis;

            // Create a new utterance object
            const utterance = new SpeechSynthesisUtterance(text);

            // Optional settings
            utterance.lang = 'en-US'; 
            utterance.rate = 1;
            utterance.pitch = 1; 
            utterance.volume = 1; 

            // Speak the text
            synth.speak(utterance);
        } else {
            console.error('Text-to-Speech is not supported in this browser.');
        }
    }






});



