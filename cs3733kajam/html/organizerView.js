var url = window.location.href;
var setOfParams = "";
var q = "";

function getWeekDay(day){
    return day.substring(0,3);
}

function fillInDate(dayOfWeek,actualDate){
    if(dayOfWeek=="Mon"){
        document.getElementById(dayOfWeek).innerHTML = '<button onclick="" type="button"><</button> '+actualDate.toDateString();
    }
    else if(dayOfWeek.toString()=="Fri"){
        document.getElementById(dayOfWeek).innerHTML = actualDate.toDateString()+' <button onclick="getNextWeek()" type="button">></button>'; 
    }
    else{
        document.getElementById(dayOfWeek).innerHTML = actualDate.toDateString()
    }
}

function fillInWeek(mon){
    while(getWeekDay(mon.toDateString())!="Sat"){
        fillInDate(getWeekDay(mon.toDateString()),mon);
        mon.setDate(mon.getDate()+1);
    }
}

function getMonday(currDate){
    while(getWeekDay(currDate.toDateString())!="Mon"){
        currDate.setDate(currDate.getDate()-1);
    }
    return currDate;
}

function getNextWeek(mondayOfThisWeek){
    mondayOfThisWeek.setDate(mondayOfThisWeek.getDate()+7);
    fillInWeek(mondayOfThisWeek);
}

function getPrevWeek(mondayOfThisWeek){
    mondayOfThisWeek.setDate(mondayOfThisWeek.getDate()-7);
    fillInWeek(mondayOfThisWeek);
}

function updateView(json){
    console.log(json);
    document.getElementById("schName").innerHTML = json["name"];

    var startMonth = parseInt(json["startDate"]["month"]);
    var startDay = parseInt(json["startDate"]["day"]);
    var startYear = parseInt(json["startDate"]["year"]);

    var duration = parseInt(json["meetingDuration"]);

    var startingDate = new Date(startMonth+" "+startDay+" "+startYear);

    var mondayOfWeek = getMonday(startingDate);
    fillInWeek(mondayOfWeek);

    //This section populates the table with cells
    var dayStartHour = parseInt(json["startTime"]["hour"]);
    var dayEndHour = parseInt(json["endTime"]["hour"]);

    var currHour,currMinute, i;
    for(currHour = dayStartHour, currMinute = 0, i=1;currHour<dayEndHour;i++){
        
        var row = document.getElementById("scheduleTable").insertRow(i);
        var head = row.insertCell(0);

        currMinuteStr = (currMinute==0)? "00":currMinute;

        enderHour = currHour;
        enderMinute = (currMinute+duration);

        if(enderMinute==60){
            enderMinute="00";
            enderHour++;
        }

        head.innerHTML = currHour+":"+currMinuteStr+" - "+enderHour+":"+enderMinute;

        // Populate rest of the row
        for(var a=1;a<=5;a++){
            row.insertCell(a);
        }

        if(currMinute+duration == 60){
            currMinute = 0;
            currHour++;
        }
        else{
            currMinute+=duration;
        }
    }

}

for(var i=0;i<url.length;i++){
    if(url.substring(i,i+1)==="?"){
        i++;
        for(var j=i;j<url.length;j++){
            setOfParams+=url.substring(j,j+1);
        }
    }
}

q=setOfParams;

urlParameters = {}

for(var k=0;k<setOfParams.length;k++){
    keyName = ""

    while(setOfParams.substring(k,k+1) != "="){
        keyName+=setOfParams.substring(k,k+1);
        k++;
    }
    k++;

    if(k==setOfParams.length){
        break;
    }

    value = ""

    while(setOfParams.substring(k,k+1)!="&"){
        value+=setOfParams.substring(k,k+1);
        k++;
        if(k==setOfParams.length){
            break;
        }
    }

    urlParameters[keyName]=value;
}

getBackParameters = {}

getUrl = "https://f1a5ytx922.execute-api.us-east-2.amazonaws.com/Beta/scheduleid"
xhr = new XMLHttpRequest();
xhr.open("POST",getUrl,true);

sender = {}

sender["arg1"] = urlParameters["id"];
sender["arg2"] = urlParameters["secretCode"];
sender = JSON.stringify(sender);

xhr.send(sender);

console.log(sender);

xhr.onloadend = function(){

    if(xhr.readyState == XMLHttpRequest.DONE){
        console.log(xhr.responseText);
        getBackParameters = JSON.parse(JSON.parse(xhr.responseText)["body"]);
        updateView(getBackParameters);
    }
    else{
        console.log("POST request failed, either a connectiivity issue or a problem with the server");
    }

};