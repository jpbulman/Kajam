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
        document.getElementById(dayOfWeek).innerHTML = '<button onclick="getPrevWeek()" class="weekButton" type="button"><i class="fas fa-chevron-circle-left"></i></button> <a id="MonDate">'+actualDate.toDateString()+'</a>';
    }
    else if(dayOfWeek.toString()=="Fri"){
        document.getElementById(dayOfWeek).innerHTML = '<a id="FriDate">'+actualDate.toDateString()+'</a>'+' <button class="weekButton" onclick="getNextWeek()" type="button"><i class="fas fa-chevron-circle-right"></i></button>'; 
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
    }
    else{
        while(getWeekDay(currDate.toDateString())!="Mon"){
            currDate.setDate(currDate.getDate()-1);
        }
    }
    document.getElementById("scheduleTable").setAttribute("data-monday",currDate.toDateString())
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
        currentCell.classList.remove("bookedTS")    
        }
    }

}

function giveCellsAttr(){
    var table = document.getElementById("scheduleTable")

    var duration = parseInt(document.getElementById("scheduleTable").getAttribute("data-duration"))

    var currHour = parseInt(document.getElementById("scheduleTable").getAttribute("data-dailyStartHour"))
    var currMinute = 0

    var currEndHour = currHour;
    var currEndMinute = currMinute+duration

    console.log(document.getElementById("scheduleTable").getAttribute("data-schStartDate"))
    var currDate = new Date(document.getElementById("scheduleTable").getAttribute("data-monday"));

    if(currEndMinute==60){
        currEndHour+=1;
        currEndMinute=0
    }

    var currMonth = currDate.getMonth()+1
    var currDay = currDate.getDate()
    var currYear = currDate.getFullYear()

    for(var i=1;i<6;i++){
        for(var j=1;j<table.rows.length;j++){
            var currentCell = table.rows[j].cells[i];
            // console.log(currentCell)
            currentCell.setAttribute("data-year",currYear)
            currentCell.setAttribute("data-month",currMonth)
            currentCell.setAttribute("data-dayOfMonth",currDay)
            currentCell.setAttribute("data-hour",currHour)
            currentCell.setAttribute("data-minute",currMinute)
            currentCell.setAttribute("data-endHour",currEndHour)
            currentCell.setAttribute("data-endMinute",currEndMinute)

            currMinute+=duration
            currEndMinute+=duration
            if(currMinute==60){
                currMinute=0;
                currHour+=1;
            }
            if(currEndMinute==60){
                currEndMinute=0;
                currEndHour+=1;
            }
        }
        currDate.setDate(currDate.getDate()+1)
        currMonth = currDate.getMonth()+1
        currDay = currDate.getDate()
        currYear = currDate.getFullYear()

        currHour = parseInt(document.getElementById("scheduleTable").getAttribute("data-dailyStartHour"))
        currMinute = 0
    
        currEndHour = currHour;
        currEndMinute = currMinute+duration;

        if(currEndMinute==60){
            currEndHour+=1;
            currEndMinute=0
        }
    }
}

function refreshTable(){
    clearTable()
            var dateInformation = getCurrColDateInfo(1);
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

                    var row=1;
                    var col=1;

                    var table = document.getElementById("scheduleTable")
                    console.log("Refresh")
                    console.log(ts)

                    for(var k=0;k<ts.length;k++){
                        var currentCell = table.rows[row].cells[col];
    
                        giveCellsAttr()

                        for(var searchRow=1;searchRow<table.rows.length;searchRow++){
                            for(var searchColumn=1;searchColumn<table.rows[searchRow].cells.length;searchColumn++){
                                var cc = table.rows[searchRow].cells[searchColumn];
                                if(cc.getAttribute("data-year")==ts[k]["date"]["year"]&&
                                    cc.getAttribute("data-month")==ts[k]["date"]["month"]&&
                                    cc.getAttribute("data-dayOfMonth")==ts[k]["date"]["day"]&&
                                    cc.getAttribute("data-hour")==ts[k]["startTime"]["hour"]&&
                                    cc.getAttribute("data-minute")==ts[k]["startTime"]["minute"]&&
                                    cc.getAttribute("data-endHour")==ts[k]["endTime"]["hour"]&&
                                    cc.getAttribute("data-endMinute")==ts[k]["endTime"]["minute"]){
                                    currentCell = cc;
                                }
                            }
                        }

                        // currentCell.setAttribute("data-year",ts[k]["date"]["year"])
                        // currentCell.setAttribute("data-month",ts[k]["date"]["month"])
                        // currentCell.setAttribute("data-dayOfMonth",ts[k]["date"]["day"])
                        // currentCell.setAttribute("data-hour",ts[k]["startTime"]["hour"])
                        // currentCell.setAttribute("data-minute",ts[k]["startTime"]["minute"])
                        // currentCell.setAttribute("data-endHour",ts[k]["endTime"]["hour"])
                        // currentCell.setAttribute("data-endMinute",ts[k]["endTime"]["minute"])
                        currentCell.setAttribute("data-isFree",ts[k]["isFree"])
                        currentCell.setAttribute("data-tsid",ts[k]["id"])

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
                            currentCell.classList.add("bookedTS")
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
                                sender["arg7"] = this.getAttribute("data-endHour")
                                sender["arg8"] = this.getAttribute("data-endMinute")
                                sender["arg9"] = "unavailable";

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
                                sender["arg1"] = globalSchID;
                                sender["arg2"] = this.getAttribute("data-year")
                                sender["arg3"] = this.getAttribute("data-month")
                                sender["arg4"] = this.getAttribute("data-dayOfMonth")
                                sender["arg5"] = this.getAttribute("data-hour")
                                sender["arg6"] = this.getAttribute("data-minute")
                                sender["arg7"] = this.getAttribute("data-endHour")
                                sender["arg8"] = this.getAttribute("data-endMinute")
                                sender["arg9"] = "available";

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
                                var wantsToCancel = confirm("Are you sure you want to delete this meeting?")

                                if(wantsToCancel){
                                    var delReq = new XMLHttpRequest();
                                    var url = "https://f1a5ytx922.execute-api.us-east-2.amazonaws.com/Beta/meeting/organizer";
                                    delReq.open("DELETE",url,true);

                                    var sendInfo = {}
                                    sendInfo["timeSlotID"] = this.getAttribute("data-tsid");

                                    delReq.send(JSON.stringify(sendInfo))
                                    console.log(JSON.stringify(sendInfo))

                                    delReq.onloadend = function(){
                                        if(delReq.readyState==XMLHttpRequest.DONE){
                                            alert("Meeting deleted!")
                                            document.location.reload(true);
                                        }
                                        else{   
                                            console.log("ouch")
                                        }
                                    }

                                }
                                else{

                                }
                            }
                        }
                        
                        if(row%(table.rows.length-1)==0){
                            row=0;
                            col+=1;
                        }
                        row++;
                    }

                }
            }
}

function setAvailabilityGeneric(){
    var availablity = (document.getElementById("selectAvailability").value).toLowerCase();
    var fromHour = (document.getElementById("fromTimeField").value).substring(0,2)
    var fromMinute = (document.getElementById("fromTimeField").value).substring(3,5)
    var toHour = (document.getElementById("toTimeField").value).substring(0,2)
    var toMinute = (document.getElementById("toTimeField").value).substring(3,5);
    var currDayVal = document.getElementById("daysDropDown").value;

    var changeDay = "Not working";

    switch(currDayVal){
        case "All Mondays":changeDay=1;break;
        case "All Tuesdays":changeDay=2;break;
        case "All Wednesdays":changeDay=3;break;
        case "All Thursdays":changeDay=4;break;
        case "All Fridays":changeDay=5;break;
        default: changeDay=-1;
    }
    changeDay = changeDay.toString()

    var setRequest = new XMLHttpRequest();
    var url = "https://f1a5ytx922.execute-api.us-east-2.amazonaws.com/Beta/timeslotavailability/general"
    setRequest.open("POST",url,true)

    var sender = {}
    //schid, changeDay, starthour, startmin, eh, em, av
    sender["arg1"] = document.getElementById("scheduleTable").getAttribute("data-scheduleID")
    sender["arg2"] = changeDay;
    sender["arg3"] = fromHour;
    sender["arg4"] = fromMinute;
    sender["arg5"] = toHour;
    sender["arg6"] = toMinute;
    sender["arg7"] = availablity

    setRequest.send(JSON.stringify(sender))
    console.log(JSON.stringify(sender))

    setRequest.onloadend = function(){
        if(setRequest.readyState==XMLHttpRequest.DONE){
            console.log(setRequest.responseText)
        }
    }

}

function setAvailabilitySpecific(){
    var availablity = (document.getElementById("selectAvailability").value).toLowerCase();
    var fromHour = (document.getElementById("fromTimeField").value).substring(0,2)
    var fromMinute = (document.getElementById("fromTimeField").value).substring(3,5)
    var toHour = (document.getElementById("toTimeField").value).substring(0,2)
    var toMinute = (document.getElementById("toTimeField").value).substring(3,5);
    var dateVal = (document.getElementById("dateHolder").value);
    var startMonth = dateVal.substring(0,2)
    var startDay = dateVal.substring(3,5)
    var startYear = dateVal.substring(6,10)

    var setReq = new XMLHttpRequest();
    var url = "https://f1a5ytx922.execute-api.us-east-2.amazonaws.com/Beta/timeslotavailability"
    setReq.open("POST",url,true);

    var sender = {}
    sender["arg1"] = document.getElementById("scheduleTable").getAttribute("data-scheduleID")
    sender["arg2"] = startYear
    sender["arg3"] = startMonth
    sender["arg4"] = startDay;
    sender["arg5"] = fromHour;
    sender["arg6"] = fromMinute;
    sender["arg7"] = toHour
    sender["arg8"] = toMinute;
    sender["arg9"] = availablity

    setReq.send(JSON.stringify(sender))
    console.log(JSON.stringify(sender))

    setReq.onloadend = function(){
        if(setReq.readyState==XMLHttpRequest.DONE){
            console.log(setReq.responseText)
        }
    }

}

function setAvailability(){
    var date = document.getElementById("dateHolder").value
    if(date===""){
        setAvailabilityGeneric()
    }
    else{
        setAvailabilitySpecific()
    }
    document.location.reload(true);
    // refreshTable()
}

function dropDownChange(){
    var val = document.getElementById("daysDropDown").value

    if(val!="None"){
        document.getElementById("dateHolder").disabled = true;
        document.getElementById("dateHolder").value = "";
        document.getElementById("dateHolder").style.backgroundColor = "grey"
    }
    else{
        document.getElementById("dateHolder").disabled = false;
        document.getElementById("dateHolder").style.backgroundColor = "white"
    }

}

function dateHolderChange(){
    var val = document.getElementById("dateHolder").value

    if(val==""){
        document.getElementById("daysDropDown").disabled=false;
    }
    else{
        document.getElementById("daysDropDown").disabled=true;
    }

}

function popTable(dayStartHour,dayEndHour,duration,scheduleID){

    document.getElementById("loading").style.visibility = "visible"

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

                tsvReq.send(JSON.stringify(sender));

                tsvReq.onloadend = function(){
                    if(tsvReq.readyState==XMLHttpRequest.DONE){
                        // console.log(tsvReq.responseText)
                        document.getElementById("loading").style.visibility = "hidden"

                        var body = JSON.parse(JSON.parse(tsvReq.responseText)["body"]);
                        var ts = body["ts"]
                        console.log(ts)

                        var row=1;
                        var col=1;

                        var table = document.getElementById("scheduleTable")
                        console.log(ts)

                        for(var k=0;k<ts.length;k++){
                            var currentCell = table.rows[row].cells[col];

                            giveCellsAttr()

                            for(var searchRow=1;searchRow<table.rows.length;searchRow++){
                                for(var searchColumn=1;searchColumn<table.rows[searchRow].cells.length;searchColumn++){
                                    var cc = table.rows[searchRow].cells[searchColumn];

                                    if(cc.getAttribute("data-year")==ts[k]["date"]["year"]&&
                                        cc.getAttribute("data-month")==ts[k]["date"]["month"]&&
                                        cc.getAttribute("data-dayOfMonth")==ts[k]["date"]["day"]&&
                                        cc.getAttribute("data-hour")==ts[k]["startTime"]["hour"]&&
                                        cc.getAttribute("data-minute")==ts[k]["startTime"]["minute"]&&
                                        cc.getAttribute("data-endHour")==ts[k]["endTime"]["hour"]&&
                                        cc.getAttribute("data-endMinute")==ts[k]["endTime"]["minute"]){
                                        currentCell = cc;
                                        console.log(cc)
                                    }
                                }
                            }
    
                            // currentCell.setAttribute("data-year",ts[k]["date"]["year"])
                            // currentCell.setAttribute("data-month",ts[k]["date"]["month"])
                            // currentCell.setAttribute("data-dayOfMonth",ts[k]["date"]["day"])
                            // currentCell.setAttribute("data-hour",ts[k]["startTime"]["hour"])
                            // currentCell.setAttribute("data-minute",ts[k]["startTime"]["minute"])
                            // currentCell.setAttribute("data-endHour",ts[k]["endTime"]["hour"])
                            // currentCell.setAttribute("data-endMinute",ts[k]["endTime"]["minute"])
                            currentCell.setAttribute("data-isFree",ts[k]["isFree"])
                            currentCell.setAttribute("data-tsid",ts[k]["id"])

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
                                currentCell.classList.add("bookedTS")
                            }

                            currentCell.onclick = function (){
                                // console.log(this.getAttribute("data-endMinute"))
                                if(this.innerHTML=="Open"){
                                    this.classList.remove("openTS");
                                    this.classList.add("closedTS");
                                    this.innerHTML = "Closed";

                                    //id, year,m day,h,m,eh,em,avail

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
                                    sender["arg7"] = this.getAttribute("data-endHour")
                                    sender["arg8"] = this.getAttribute("data-endMinute")
                                    sender["arg9"] = "unavailable";

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
                                    sender["arg7"] = this.getAttribute("data-endHour")
                                    sender["arg8"] = this.getAttribute("data-endMinute")
                                    sender["arg9"] = "available";

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
                                    var wantsToCancel = confirm("Are you sure you want to delete this meeting?")

                                    if(wantsToCancel){
                                        var delReq = new XMLHttpRequest();
                                        var url = "https://f1a5ytx922.execute-api.us-east-2.amazonaws.com/Beta/meeting/organizer";
                                        delReq.open("DELETE",url,true);

                                        var sendInfo = {}
                                        sendInfo["timeSlotID"] = this.getAttribute("data-tsid");

                                        delReq.send(JSON.stringify(sendInfo))
                                        console.log(JSON.stringify(sendInfo))

                                        delReq.onloadend = function(){
                                            if(delReq.readyState==XMLHttpRequest.DONE){
                                                alert("Meeting deleted!")
                                                document.location.reload(true);
                                            }
                                            else{   
                                                console.log("ouch")
                                            }
                                        }

                                    }
                                    else{

                                    }

                                }
                            }
                            // console.log(row,col)
                            if(row%(table.rows.length-1)==0){
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

function editSchedule(){
    var editRequest = new XMLHttpRequest();
    var url = "https://f1a5ytx922.execute-api.us-east-2.amazonaws.com/Beta/schedule/settings"
    editRequest.open("POST",url,true)

    var nameVal = document.getElementById("editScheduleName").value;
    var startDateVals = document.getElementById("editScheduleSD").value;

    var startMonth="",startDay="",startYear="";
    var k=0;
    for(var i=0;i<startDateVals.length;i++){
        var currChar = startDateVals.substring(i,i+1)
        if(currChar==="/"){
            k++;
        }
        else{
            switch(k){
                case 0:startMonth+=currChar;break;
                case 1:startDay+=currChar;break;
                case 2:startYear+=currChar;break;
            }
        }
    }

    var endDateVals = document.getElementById("editScheduleED").value;

    var endMonth = "", endDay="",endYear="";
    var a=0;
    for(var j=0;j<endDateVals.length;j++){
        var currChar = endDateVals.substring(j,j+1)
        if(currChar==="/"){
            a++;
        }
        else{
            switch (a) {
                case 0:endMonth+=currChar; break;
                case 1:endDay+=currChar; break;
                case 2:endYear+=currChar; break;
                default:
                    console.log("not going to happen")
                    break;
            }
        }
    }

    console.log(nameVal)

    var sendInfo = {}
    sendInfo["arg1"] = document.getElementById("scheduleTable").getAttribute("data-scheduleID");
    sendInfo["arg2"] = (nameVal=="Enter schedule name")? "":nameVal;
    sendInfo["arg3"] = startYear
    sendInfo["arg4"] = startMonth
    sendInfo["arg5"] = startDay
    sendInfo["arg6"] = endYear
    sendInfo["arg7"] = endMonth
    sendInfo["arg8"] = endDay

    editRequest.send(JSON.stringify(sendInfo))
    console.log(JSON.stringify(sendInfo))

    editRequest.onloadend = function(){
        if(editRequest.readyState==XMLHttpRequest.DONE){
            console.log(editRequest.responseText)
            document.location.reload(true)
        }
    }

}

function updateView(json){
    document.getElementById("schName").innerHTML = json["name"]+' <i class="fas fa-calendar-alt"></i>';

    var scheduleID = json["id"];
    document.getElementById("scheduleTable").setAttribute("data-scheduleID",scheduleID)

    var startMonth = parseInt(json["startDate"]["month"]);
    var startDay = parseInt(json["startDate"]["day"]);
    var startYear = parseInt(json["startDate"]["year"]);

    console.log(startYear,startMonth,startDay)

    var schStartDate = new Date(startYear,startMonth-1,startDay)
    console.log(schStartDate)
    document.getElementById("scheduleTable").setAttribute("data-schStartDate",schStartDate)

    var duration = parseInt(json["meetingDuration"]);
    document.getElementById("scheduleTable").setAttribute("data-duration",duration)

    var startingDate = new Date(startMonth+" "+startDay+" "+startYear);

    var mondayOfWeek = getMonday(startingDate);
    fillInWeek(mondayOfWeek);

    //This section populates the table with cells
    var dayStartHour = parseInt(json["startTime"]["hour"]);
    document.getElementById("scheduleTable").setAttribute("data-dailyStartHour",dayStartHour)
    var dayEndHour = parseInt(json["endTime"]["hour"]);
    document.getElementById("scheduleTable").setAttribute("data-dailyEndHour",dayEndHour)

    globalSchID = scheduleID;
    globalSchStartHour = dayStartHour;
    globalDuration = duration;
    globalSchEndHour = dayEndHour;

    popTable(dayStartHour,dayEndHour,duration,scheduleID)

    // console.log(document.getElementById("11").innerHTML)    
    // console.log(startYear)
    // getTimeSlotView(scheduleID,startYear,startMonth,startDay,dayStartHour,"00");
}

function copyToClip(){
    document.getElementById("copied").style.visibility="visible"
    var currUrl = window.location.href
    currUrl = currUrl.replace("organizerView","participantView")

    for(var i=0;i<currUrl.length;i++){
        if(currUrl.substring(i,i+1)==="&"){
            currUrl = currUrl.substring(0,i)
        }
    }

    var area = document.createElement('textarea');
    area.value = currUrl;
    document.body.appendChild(area)
    area.select();
    document.execCommand('copy')
    document.body.removeChild(area)

    var timeOut = 750;
    setTimeout(function(){
        document.getElementById("copied").style.visibility="hidden"
    },timeOut);

}

function copyToClipSA(){
    document.getElementById("copiedSA").style.visibility="visible"
    var currUrl = window.location.href
    currUrl = currUrl.replace("organizerView","sysAdminView")

    for(var i=0;i<currUrl.length;i++){
        if(currUrl.substring(i,i+1)==="?"){
            currUrl = currUrl.substring(0,i)
        }
    }

    var area = document.createElement('textarea');
    area.value = currUrl;
    document.body.appendChild(area)
    area.select();
    document.execCommand('copy')
    document.body.removeChild(area)

    var timeOut = 750;
    setTimeout(function(){
        document.getElementById("copiedSA").style.visibility="hidden"
    },timeOut);
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