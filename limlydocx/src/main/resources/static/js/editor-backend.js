 $(document).ready(function () {

document.getElementById("getData").addEventListener("click", () => {
    console.log("clicked") // for testing

    let rawData = document.getElementById("editor").innerHTML
    let tempDiv = document.createElement("div");

    tempDiv.innerHTML = rawData;
    let filteredHTML = "";
    
    tempDiv.querySelectorAll("p").forEach((element) => {
        filteredHTML += element.outerHTML; // Append the cleaned-up elements
    });

    console.log(filteredHTML);
    tempDiv.remove();

    sendDataToSpringBoot(filteredHTML)

})

/**
 * This function sends data to the backend Which then saved in the database
 * @param {*} data 
 */

const sendDataToSpringBoot = (data)=> {

    let confirmRequest = confirm("you do want to send an http request to save data")

    if(confirmRequest){

        $.ajax({
            type: "POST",
            url: "http://localhost:8080/savecontent",
            contentType:"application/json",
            data: JSON.stringify({content: data}) ,
            dataType: "json",
            success: function (response, status) {
                console.log("data has been saved" + status )
            },

            error: function(request, status, error){
                console.error(error)
            }
            
            
        });
       
    } 
    else{
        console.log("data send request is canceled by the user")
    }

}




 });










