var query = window.location.search.substring(1);
var cruiseURI = getQueryVariable("uri");

function getQueryVariable(variable)
{
       var query = window.location.search.substring(1);
       var vars = query.split("&");
       for (var i=0;i<vars.length;i++) {
               var pair = vars[i].split("=");
               if(pair[0] == variable){return pair[1];}
       }
       return(false);
}

glConstruct = "<gldeployment-element params='{\"host\": \"GeoLink\", \"hosturl\":\"http://geolink.org\", \"url\": \"" + cruiseURI + "\"}'/>";
var gl = document.getElementById("geoLink");
gl.innerHTML = glConstruct;
