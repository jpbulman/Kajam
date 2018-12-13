function checkInputDaysOld(){
    var val = parseInt(document.getElementById("daysOldTF").value);

    if(isNaN(val)){
        alert("Please enter a valid N value for the number of days old field");
        return false;
    }

    return true;
}

function checkInputHoursOld(){
    var val = parseInt(document.getElementById("hoursOldTF").value);

    if(isNaN(val)){
        alert("Please enter a valid N value for the number of hours old field");
        return false;
    }

    return true;
}

function clearHoursOld(){
    var cont = document.getElementById("hoursOldContainer")
    while(cont.firstChild){
        cont.removeChild(cont.firstChild)
    }
}

function getHoursOld(){
    if(checkInputDaysOld){
        clearHoursOld()
        var val = parseInt(document.getElementById("hoursOldTF").value);
        var url = "https://f1a5ytx922.execute-api.us-east-2.amazonaws.com/Beta/oldschedules/hours"

        var hReq = new XMLHttpRequest();
        hReq.open("POST",url,true);
        
        var sendInformation = {}
        sendInformation["arg1"] = val;

        hReq.send(JSON.stringify(sendInformation))
        console.log(sendInformation)

        hReq.onloadend = function(){
            if(hReq.readyState==XMLHttpRequest.DONE){
                var response = JSON.parse(JSON.parse(hReq.responseText)["body"])["schedulesList"]
                console.log(response)
                
                for(i in response){
                    var currSch = response[i];
                    
                    var cont = document.getElementById("hoursOldContainer")
                    var button = document.createElement("button")
                    var text = document.createTextNode(currSch["name"])
                    button.classList.add("entry")
                    button.appendChild(text)

                    var div = document.createElement("div")
                    div.appendChild(button)
                    cont.appendChild(div)
                }

            }
        }
    }
}

function clearDaysOld(){
    var cont = document.getElementById("daysOldContainer")
    while(cont.firstChild){
        cont.removeChild(cont.firstChild)
    }
}

function getDaysOld(){
    if(checkInputDaysOld()){
        clearDaysOld()
        var val = parseInt(document.getElementById("daysOldTF").value);
        var url = "https://f1a5ytx922.execute-api.us-east-2.amazonaws.com/Beta/daysold"

        var dayReq = new XMLHttpRequest();
        dayReq.open("POST",url,true);

        var sendInfo = {}
        sendInfo["arg1"] = val;

        dayReq.send(JSON.stringify(sendInfo))
        console.log(JSON.stringify(sendInfo))

        dayReq.onloadend = function(){
            if(dayReq.readyState==XMLHttpRequest.DONE){
                console.log(dayReq.responseText)
            }
        }
    }
}

function deleteNDays(){
    
}

var url = window.location.href;

var schID = ""

for(var i=0;i<url.length;i++){
    var current = url.substring(i,i+1);

    if(current=="="){
        for(var j=i+1;j<url.length;j++){
            current = url.substring(j,j+1)
            schID+=current;
        }
    }
}

