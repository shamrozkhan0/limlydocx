function getPath(event, path) {
    event.preventDefault();  // Prevent the default action of the link (GET request)

    console.log(path);  // Log the path to make sure the correct URL is passed

    fetch(path, {
        method: "DELETE",  // Send a DELETE request
    })
    .then((result) => {
        console.log("Document deleted successfully.");
        // Optionally, you can redirect after successful deletion
        window.location.href = "/dashboard";  // Redirect to the dashboard
    })
    .catch((err) => {
        console.error(err);
    });
}
