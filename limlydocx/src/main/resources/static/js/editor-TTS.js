document.addEventListener('DOMContentLoaded', function () {
    const popupButton = document.getElementById('popupButton');
    let selectedText = ''; // Variable to store selected text

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

    // Button action when clicked
    popupButton.addEventListener('click', function () {
        if (selectedText) {
            console.log("inside the click " + selectedText)
            textToSpeech(selectedText);
        } else {
            alert('No text selected!');
        }
    });
});

function textToSpeech(text) {
// Check if the browser supports the SpeechSynthesis API
if ('speechSynthesis' in window) {
    const synth = window.speechSynthesis;

    // Create a new utterance object
    const utterance = new SpeechSynthesisUtterance(text);

    // Optional settings
    utterance.lang = 'en-US'; // Set the language (default is 'en-US')
    utterance.rate = 1; // Set the speed of speech (1 is normal)
    utterance.pitch = 1; // Set the pitch (1 is normal)
    utterance.volume = 1; // Set the volume (1 is maximum)

    // Speak the text
    synth.speak(utterance);
} else {
    console.error('Text-to-Speech is not supported in this browser.');
}
}
