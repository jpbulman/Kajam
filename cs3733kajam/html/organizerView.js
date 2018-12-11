var url = window.location.href;
var setOfParams = "";
// var q = "";

var theScheduleID = "";

var globalSchStartHour = 0;
var globalSchEndHour = 0;
var globalDuration = 0;
var globalSchID = "";

function getWeekDay(day){
    return day.toString().substring(0,3);
}

function fillInDate(dayOfWeek,actualDate){
    if(dayOfWeek=="Mon"){
        document.getElementById(dayOfWeek).innerHTML = '<button onclick="getPrevWeek()" type="button"><</button> <a id="MonDate">'+actualDate.toDateString()+'</a>';
    }
    else if(dayOfWeek.toString()=="Fri"){
        document.getElementById(dayOfWeek).innerHTML = '<a id="FriDate">'+actualDate.toDateString()+'</a>'+' <button onclick="getNextWeek()" type="button">></button>'; 
    }
    else{
        document.getElementById(dayOfWeek).innerHTML = '<a id="'+getWeekDay(actualDate)+'Date">'+actualDate.toDateString()
    }
}

function fillInWeek(mon){
    while(getWeekDay(mon.toDateString())!="Sat"){
        fillInDate(getWeekDay(mon.toDateString()),mon);
        mon.setDate(mon.getDate()+1);
    }
}

function getMonday(currDate){
    if(getWeekDay(currDate.toDateString())=="Sat" || getWeekDay(currDate.toDateString())=="Sun"){
        while(getWeekDay(currDate.toDateString())!="Mon"){
            currDate.setDate(currDate.getDate()+1);
        }
    }else{
        while(getWeekDay(currDate.toDateString())!="Mon"){
            currDate.setDate(currDate.getDate()-1);
        }
    }
    return currDate;
}

function getNextWeek(){
    var mon = document.getElementById("MonDate").innerHTML;
    var d = new Date(mon);
    d.setDate(d.getDate()+7);
    fillInWeek(d);
    refreshTable();
}

function getPrevWeek(){
    var mon = document.getElementById("MonDate").innerHTML;
    var d = new Date(mon);
    d.setDate(d.getDate()-7);
    fillInWeek(d);
    refreshTable()
}

function getTimeSlotView(schId,year,month,day,startHour,startMinute){
    // console.log(schId,year,month,day,startHour,startMinute)

    var tsvReq = new XMLHttpRequest();
    var posterUrl = "https://f1a5ytx922.execute-api.us-east-2.amazonaws.com/Beta/scheduleView/timeslotview";
    tsvReq.open("POST",posterUrl,true);

    sender = {}
    sender["arg1"] = schId.toString();
    sender["arg2"] = year.toString();
    sender["arg3"] = month.toString();
    sender["arg4"] = day.toString();
    sender["arg5"] = startHour.toString();
    sender["arg6"] = startMinute.toString();

    tsvReq.send(JSON.stringify(sender));
    // console.log(JSON.stringify(sender));

    var asdf = true;
    httpCoder = 400;

    tsvReq.onloadend = function(){

        if(tsvReq.readyState == XMLHttpRequest.DONE){
            // console.log("getTimeSlotView invoked. Returned:");
            var parsedInfo = JSON.parse(JSON.parse(tsvReq.responseText)["body"]);
            // console.log(parsedInfo["closed"])

            // console.log(parsedInfo)

            if(parsedInfo["httpCode"]==400){
                // // return false;
                // console.log(" no")
                console.log("INCORRECT:"+ schId,year,month,day,startHour,startMinute)
            }
            else{
                // console.log("CORRECT:"+ schId,year,month,day,startHour,startMinute)
                // console.log(asdf)
                httpCoder = parsedInfo["httpCode"];
                asdf = parsedInfo;
                console.log(asdf)
                // return parsedInfo;
            }
        }
        else{

        }

    };

    // while(httpCoder>300){
        
    // }

    return asdf;
}

function deleteSchedule(){
    console.log("Started delete request");
    var deleteRequest = new XMLHttpRequest();
    var deleteURL = "https://f1a5ytx922.execute-api.us-east-2.amazonaws.com/Beta/schedule";
    deleteRequest.open("DELETE",deleteURL,true);

    var sendMaterial = {};
    sendMaterial["arg1"] = theScheduleID;

    var json = JSON.stringify(sendMaterial);
    console.log(json);

    deleteRequest.send(json);

    deleteRequest.onloadend = function(){
        if(deleteRequest.readyState == XMLHttpRequest.DONE){
            alert("Schedule has been deleted, thank you for using scheduler!");
            window.location.href = "landing.html";
            console.log("Schedule has been deleted");
        }
        else{
            console.log("ERROR: Could not delete schedule properly");
        }
    }
}

function getCurrColDateInfo(column){
    var id = "";
    switch(column){
        case 1:id="Mon";break;
        case 2:id="Tue";break;
        case 3:id="Wed";break;
        case 4:id="Thu";break;
        case 5:id="Fri";break;
    }
    var x = document.getElementById(id+"Date").innerHTML;
    var d = new Date(x);
    
    var information = {};
    information["year"] = d.getFullYear();
    information["month"] = d.getMonth();
    information["day"] = d.getDate();

    return information;
}

function clearTable(){
    for(var row=1;row<document.getElementById("scheduleTable").rows.length;row++){
        for(var col=1;col<document.getElementById("scheduleTable").rows[row].cells.length;col++){
        var currentCell = document.getElementById("scheduleTable").rows[row].cells[col];
        currentCell.innerHTML = "";
        currentCell.classList.remove("openTS")
        currentCell.classList.remove("closedTS")   

        }
    }

}

function refreshTable(){
    clearTable()
    for(var a=1;a<=5;a++){
        // var cell = row.insertCell(a);
        if(a==1){

            var dateInformation = getCurrColDateInfo(a);
            console.log(dateInformation)

            var tsvReq = new XMLHttpRequest();
            var posterUrl = "https://f1a5ytx922.execute-api.us-east-2.amazonaws.com/Beta/scheduleView";
            tsvReq.open("POST",posterUrl,true);
        
            sender = {}
            sender["arg1"] = globalSchID.toString();
            sender["arg2"] = dateInformation["year"].toString();
            sender["arg3"] = (dateInformation["month"]+1).toString();
            sender["arg4"] = dateInformation["day"].toString();

            console.log(JSON.stringify(sender));

            tsvReq.send(JSON.stringify(sender));

            tsvReq.onloadend = function(){
                if(tsvReq.readyState==XMLHttpRequest.DONE){
                    var body = JSON.parse(JSON.parse(tsvReq.responseText)["body"]);
                    var ts = body["ts"]
                    console.log(ts)

                    var row=1;
                    var col=1;

                    var table = document.getElementById("scheduleTable")
                    console.log(ts)

                    for(var k=0;k<ts.length;k++){
                        // console.log(ts.length)
                        var currentCell = table.rows[row].cells[col];
                        currentCell.setAttribute("data-year",ts[k]["date"]["year"])
                        currentCell.setAttribute("data-month",ts[k]["date"]["month"])
                        currentCell.setAttribute("data-dayOfMonth",ts[k]["date"]["day"])
                        currentCell.setAttribute("data-hour",ts[k]["startTime"]["hour"])
                        currentCell.setAttribute("data-minute",ts[k]["startTime"]["minute"])
                        currentCell.setAttribute("data-isFree",ts[k]["isFree"])

                        if(ts[k]["isFree"]){
                            currentCell.innerHTML="Open";
                            currentCell.classList.add("openTS")
                        }
                        else if(ts[k]["meeting"]["secretCode"]==0){
                            currentCell.innerHTML="Closed"
                            currentCell.classList.add("closedTS");
                        }
                        else{
                            currentCell.innerHTML=ts[k]["meeting"]["name"]
                        }

                        currentCell.onclick = function (){
                            if(this.innerHTML=="Open"){
                                this.classList.remove("openTS");
                                this.classList.add("closedTS");
                                this.innerHTML = "Closed";

                                //id, year,m day,h,m,avail

                                var changeAvailReq = new XMLHttpRequest();
                                var availURL = "https://f1a5ytx922.execute-api.us-east-2.amazonaws.com/Beta/timeslotavailability"
                                changeAvailReq.open("POST",availURL,true);

                                sender = {}
                                sender["arg1"] = globalSchID;
                                sender["arg2"] = this.getAttribute("data-year")
                                sender["arg3"] = this.getAttribute("data-month")
                                sender["arg4"] = this.getAttribute("data-dayOfMonth")
                                sender["arg5"] = this.getAttribute("data-hour")
                                sender["arg6"] = this.getAttribute("data-minute")
                                sender["arg7"] = "unavailable";

                                changeAvailReq.send(JSON.stringify(sender));
                                console.log(JSON.stringify(sender));
                                changeAvailReq.onloadend = function(){

                                    if(changeAvailReq.readyState==XMLHttpRequest.DONE){
                                        console.log("Done")
                                        console.log(changeAvailReq.responseText)
                                    }

                                }

                            }
                            else if(this.innerHTML=="Closed"){
                                this.classList.remove("closedTS");
                                this.classList.add("openTS");
                                this.innerHTML = "Open";

                                var changeAvailReq = new XMLHttpRequest();
                                var availURL = "https://f1a5ytx922.execute-api.us-east-2.amazonaws.com/Beta/timeslotavailability"
                                changeAvailReq.open("POST",availURL,true);

                                sender = {}
                                sender["arg1"] = scheduleID;
                                sender["arg2"] = this.getAttribute("data-year")
                                sender["arg3"] = this.getAttribute("data-month")
                                sender["arg4"] = this.getAttribute("data-dayOfMonth")
                                sender["arg5"] = this.getAttribute("data-hour")
                                sender["arg6"] = this.getAttribute("data-minute")
                                sender["arg7"] = "available";

                                changeAvailReq.send(JSON.stringify(sender));
                                console.log(JSON.stringify(sender));
                                changeAvailReq.onloadend = function(){

                                    if(changeAvailReq.readyState==XMLHttpRequest.DONE){
                                        console.log("Done")
                                        console.log(changeAvailReq.responseText)
                                    }

                                }
                            }
                            else{
                                //Alert to confirm to cancel a meeting
                            }
                        }
                        // console.log(row,col)
                        if(row%(ts.length/5)==0){
                            row=0;
                            col+=1;
                        }
                        row++;
                    }

                }
            }
        }
    }
}

function popTable(dayStartHour,dayEndHour,duration,scheduleID){

    console.log(dayStartHour,dayEndHour,duration,scheduleID)

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

        // refreshTable();

        // Populate rest of the row
        for(var a=1;a<=5;a++){
            var cell = row.insertCell(a);
            if(a==1 && i==1){

                var dateInformation = getCurrColDateInfo(a);

                var tsvReq = new XMLHttpRequest();
                var posterUrl = "https://f1a5ytx922.execute-api.us-east-2.amazonaws.com/Beta/scheduleView";
                tsvReq.open("POST",posterUrl,true);
            
                sender = {}
                sender["arg1"] = scheduleID.toString();
                sender["arg2"] = dateInformation["year"].toString();
                sender["arg3"] = (dateInformation["month"]+1).toString();
                sender["arg4"] = dateInformation["day"].toString();

                console.log(JSON.stringify(sender));

                tsvReq.send(JSON.stringify(sender));

                tsvReq.onloadend = function(){
                    if(tsvReq.readyState==XMLHttpRequest.DONE){
                        var body = JSON.parse(JSON.parse(tsvReq.responseText)["body"]);
                        var ts = body["ts"]
                        console.log(ts)

                        var row=1;
                        var col=1;

                        var table = document.getElementById("scheduleTable")
                        console.log(ts)

                        for(var k=0;k<ts.length;k++){
                            // console.log(ts.length)
                            var currentCell = table.rows[row].cells[col];
                            currentCell.setAttribute("data-year",ts[k]["date"]["year"])
                            currentCell.setAttribute("data-month",ts[k]["date"]["month"])
                            currentCell.setAttribute("data-dayOfMonth",ts[k]["date"]["day"])
                            currentCell.setAttribute("data-hour",ts[k]["startTime"]["hour"])
                            currentCell.setAttribute("data-minute",ts[k]["startTime"]["minute"])
                            currentCell.setAttribute("data-isFree",ts[k]["isFree"])

                            console.log(ts[k]["meeting"]["secretCode"])

                            if(ts[k]["isFree"] && ts[k]["meeting"]["secretCode"]==0){
                                currentCell.innerHTML="Open";
                                currentCell.classList.add("openTS")
                            }
                            else if(ts[k]["meeting"]["secretCode"]==0){
                                currentCell.innerHTML="Closed"
                                currentCell.classList.add("closedTS");
                            }
                            else{
                                currentCell.innerHTML=ts[k]["meeting"]["name"]
                            }

                            currentCell.onclick = function (){
                                if(this.innerHTML=="Open"){
                                    this.classList.remove("openTS");
                                    this.classList.add("closedTS");
                                    this.innerHTML = "Closed";

                                    //id, year,m day,h,m,avail

                                    var changeAvailReq = new XMLHttpRequest();
                                    var availURL = "https://f1a5ytx922.execute-api.us-east-2.amazonaws.com/Beta/timeslotavailability"
                                    changeAvailReq.open("POST",availURL,true);

                                    sender = {}
                                    sender["arg1"] = scheduleID;
                                    sender["arg2"] = this.getAttribute("data-year")
                                    sender["arg3"] = this.getAttribute("data-month")
                                    sender["arg4"] = this.getAttribute("data-dayOfMonth")
                                    sender["arg5"] = this.getAttribute("data-hour")
                                    sender["arg6"] = this.getAttribute("data-minute")
                                    sender["arg7"] = "unavailable";

                                    changeAvailReq.send(JSON.stringify(sender));
                                    console.log(JSON.stringify(sender));
                                    changeAvailReq.onloadend = function(){

                                        if(changeAvailReq.readyState==XMLHttpRequest.DONE){
                                            console.log("Done")
                                            console.log(changeAvailReq.responseText)
                                        }

                                    }

                                }
                                else if(this.innerHTML=="Closed"){
                                    this.classList.remove("closedTS");
                                    this.classList.add("openTS");
                                    this.innerHTML = "Open";

                                    var changeAvailReq = new XMLHttpRequest();
                                    var availURL = "https://f1a5ytx922.execute-api.us-east-2.amazonaws.com/Beta/timeslotavailability"
                                    changeAvailReq.open("POST",availURL,true);

                                    sender = {}
                                    sender["arg1"] = scheduleID;
                                    sender["arg2"] = this.getAttribute("data-year")
                                    sender["arg3"] = this.getAttribute("data-month")
                                    sender["arg4"] = this.getAttribute("data-dayOfMonth")
                                    sender["arg5"] = this.getAttribute("data-hour")
                                    sender["arg6"] = this.getAttribute("data-minute")
                                    sender["arg7"] = "available";

                                    changeAvailReq.send(JSON.stringify(sender));
                                    console.log(JSON.stringify(sender));
                                    changeAvailReq.onloadend = function(){

                                        if(changeAvailReq.readyState==XMLHttpRequest.DONE){
                                            console.log("Done")
                                            console.log(changeAvailReq.responseText)
                                        }

                                    }
                                }
                                else{
                                    //Alert to confirm to cancel a meeting
                                }
                            }
                            // console.log(row,col)
                            if(row%(ts.length/5)==0){
                                row=0;
                                col+=1;
                            }
                            row++;
                        }

                    }
                }
            }
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

function updateView(json){
    document.getElementById("schName").innerHTML = json["name"];

    var scheduleID = json["id"];

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

    globalSchID = scheduleID;
    globalSchStartHour = dayStartHour;
    globalDuration = duration;
    globalSchEndHour = dayEndHour;

    popTable(dayStartHour,dayEndHour,duration,scheduleID)

    // console.log(document.getElementById("11").innerHTML)    
    // console.log(startYear)
    // getTimeSlotView(scheduleID,startYear,startMonth,startDay,dayStartHour,"00");
}

for(var i=0;i<url.length;i++){
    if(url.substring(i,i+1)==="?"){
        i++;
        for(var j=i;j<url.length;j++){
            setOfParams+=url.substring(j,j+1);
        }
    }
}

// q=setOfParams;

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
theScheduleID = urlParameters["id"];
sender["arg1"] = urlParameters["id"];
sender["arg2"] = urlParameters["secretCode"];
sender = JSON.stringify(sender);

xhr.send(sender);

xhr.onloadend = function(){

    if(xhr.readyState == XMLHttpRequest.DONE){
        console.log("Got back schedule information:"+xhr.responseText);
        getBackParameters = JSON.parse(JSON.parse(xhr.responseText)["body"]);


        if(getBackParameters["httpCode"]==400){
            window.location.href = "Error Pages/General400.html"
        }

        updateView(getBackParameters);
    }
    else{
        console.log("POST request failed, either a connectiivity issue or a problem with the server");
    }

};