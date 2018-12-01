var x = window.location.href;
var id = "";

for(var i=0;i<x.length;i++){
    var curr = x.substring(i,i+1);

    if(curr=="?"){
        for(var j=i;j<x.length;j++){
            id+=x.substring(j,j+1);
        }
    }

}

id = id.substring(4,id.length);

alert(id);