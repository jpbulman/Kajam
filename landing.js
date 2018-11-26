function goAction(){
    var scheduleVal = document.getElementById("scheduleID").value;
    var organizerVal = document.getElementById("organizerID").value;

    if((scheduleVal==""&&organizerVal=="")){
        alert("Please fill in the required fields");
    }

    if(scheduleVal!=""&&organizerVal!=""){
        alert("You can only enter in one field");
    }

}