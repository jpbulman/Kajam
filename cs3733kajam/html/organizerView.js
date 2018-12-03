var url = window.location.href;
var setOfParams = ""

for(var i=0;i<url.length;i++){
    if(url.substring(i,i+1)==="?"){
        i++;
        for(var j=i;j<url.length;j++){
            setOfParams+=url.substring(j,j+1);
        }
    }
}

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

getUrl = "https://f1a5ytx922.execute-api.us-east-2.amazonaws.com/Beta/schedule"
xhr = new XMLHttpRequest();
xhr.open("GET",getUrl,true);
xhr.send(urlParameters["id"],urlParameters["secretCode"]);
console.log(urlParameters["id"].toString(),urlParameters["secretCode"].toString());

xhr.onloadend = function(){

    if(xhr.readyState == XMLHttpRequest.DONE){
        console.log(xhr.responseText);
    }
    else{
        console.log("Did not work");
    }

}