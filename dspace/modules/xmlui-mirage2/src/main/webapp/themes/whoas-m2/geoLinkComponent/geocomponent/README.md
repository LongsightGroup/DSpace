# GeoComponents  

## Intro

This catalog contains components that either directly use or could use GeoLink graph resources and relations. The intent is to provide a simply method by which pages can include components which leverage GeoLink.

Through web components the amount of code necessary to implement these is reduced. Site developers can use simple HTML import directives to pull complex components in. These components expose a set of input parameters that can be used to generate relevant and useful content for the hosting site from GeoLink.

For example, to include a simple component into a web page as few as 2 additional header entries needs to be made. Placing the component itself into the page then can be as short of 1 or 2 more HTML lines. The approach works in both server and client side generated page structures.


#### Implementation
First a couple line are needed to include the element itself as well as a polyfill library to enable support in older browsers. Many modern browsers do not require a polyfill and this .js is ignored by the browser.

Next the element itself is referenced and used in the HTML of the page following the patterns of the component itself. Parameters and values can be passed to the component for use in obtaining remote data. Note remote services will need to allow cross origin scripting (CORS).

```
!-- GeoLink Web component include and polyfill -->
<script src="http://glcomponents.tech/components/glperson-element/bower_components/webcomponentsjs/webcomponents-lite.js">
<link rel="import" href="http://glcomponents.tech/components/glperson-element/glperson-element.html">

<!-- GeoLink component call -->
<glperson-element params='{"host": "GeoLink", "hosturl":"http://geolink.org", "url": "http://opencoredata.org/id/resource/people/v1/078d82ba-21df-11e6-8725-c8bcc89d1645"}'/>

```

#### Examples

Examples of remote pages implementing GeoLink web components.  You can do a view source on these pages to see how the components are called.  

* http://geocomponents.org/index.html 
* http://opencoredata.org/doc/resource/people/v1/078d82ba-21df-11e6-8725-c8bcc89d1645
* http://www.bco-dmo.org/deployment/616332?geolink=ecah2016

#### Further information
The following PDF provides a visual flow of how the GeoLink graph is created by a community and then implemented by web architecture approaches. These approaches enable multiple provides to place GeoLink into their web pages and applications with nothing more than simple HTML5 approaches.

http://geocomponents.org/GeoLinkWebComponents.pdf

