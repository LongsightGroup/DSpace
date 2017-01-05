if (document.querySelector('meta[content^="Knorr (Ship : 1970-)"]')) {
var element = document.querySelector('meta[content^="Knorr (Ship : 1970-)"]').attributes;
}
if (document.querySelector('meta[content^="Oceanus (Ship : 1975-)"]')) {
var element = document.querySelector('meta[content^="Oceanus (Ship : 1975-)"]').attributes;
}
if (document.querySelector('meta[content^="Atlantis (Ship : 1996-)"]')) {
var element = document.querySelector('meta[content^="Atlantis (Ship : 1996-)"]').attributes;
}



if (element) {



/*if there is a matching meta element, insert a geoLink div in right column.*/

  var topElement = document.getElementsByClassName("col-sm-8");
  var newElement = document.createElement("div");
  newElement.setAttribute("id","geoLink");
  newElement.setAttribute("class","geoLink");
  topElement[0].insertBefore(newElement,topElement[0].firstChild);
  
/*get the cruise from the element value and create gldeployment element, make contents of geoLink element*/
  var evalue = element.content.value;
  var cruise = evalue.split('Cruise ')[1];
  glConstruct = "<gldeployment-element params='{\"host\": \"GeoLink\", \"hosturl\":\"http://geolink.org\", \"label\": \"" + cruise + "\"}'/>"
  newElement.innerHTML = glConstruct;

}
