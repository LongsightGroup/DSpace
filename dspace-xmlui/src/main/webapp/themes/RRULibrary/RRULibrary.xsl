<?xml version="1.0" encoding="UTF-8"?>

<!--
  RRULibrary.xsl

  Version: $Revision: 1.7 $
 
  Date: $Date: 2006/07/27 22:54:52 $
 
  Copyright (c) 2002-2005, Hewlett-Packard Company and Massachusetts
  Institute of Technology.  All rights reserved.
 
  Redistribution and use in source and binary forms, with or without
  modification, are permitted provided that the following conditions are
  met:
 
  - Redistributions of source code must retain the above copyright
  notice, this list of conditions and the following disclaimer.
 
  - Redistributions in binary form must reproduce the above copyright
  notice, this list of conditions and the following disclaimer in the
  documentation and/or other materials provided with the distribution.
 
  - Neither the name of the Hewlett-Packard Company nor the name of the
  Massachusetts Institute of Technology nor the names of their
  contributors may be used to endorse or promote products derived from
  this software without specific prior written permission.
 
  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
  ``AS IS'' AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
  LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
  A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
  HOLDERS OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
  INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
  BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS
  OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
  ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR
  TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE
  USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH
  DAMAGE.
-->

<xsl:stylesheet
	xmlns:i18n="http://apache.org/cocoon/i18n/2.1"
	xmlns:dri="http://di.tamu.edu/DRI/1.0/"
	xmlns:mets="http://www.loc.gov/METS/"
	xmlns:xlink="http://www.w3.org/TR/xlink/"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
	xmlns:dim="http://www.dspace.org/xmlns/dspace/dim"
	xmlns:xhtml="http://www.w3.org/1999/xhtml"
	xmlns:mods="http://www.loc.gov/mods/v3"
	xmlns:dc="http://purl.org/dc/elements/1.1/"
	xmlns="http://www.w3.org/1999/xhtml"
	exclude-result-prefixes="i18n dri mets xlink xsl dim xhtml mods dc">
    <xsl:import href="../dri2xhtml.xsl"/>
    <xsl:output indent="yes"/>
    <xsl:template match="dri:document">
        <html>
            <!-- First of all, build the HTML head element -->
            <xsl:call-template name="buildHead"/>
            <!-- Then proceed to the body -->
            <body>
                <!-- Here's where the specially classed div gets inserted -->
                <div id="page" class="container">
                    <!--
                        The header div, complete with title, subtitle, trail and other junk. The trail is
                        built by applying a template over pageMeta's trail children. -->
                    <xsl:call-template name="buildHeader"/>

                    <div class="divMainBody">
                    <!--
                        Goes over the document tag's children elements: body, options, meta. The body template
                        generates the ds-body div that contains all the content. The options template generates
                        the ds-options div that contains the navigation and action options available to the
                        user. The meta element is ignored since its contents are not processed directly, but
                        instead referenced from the different points in the document. -->
                    <xsl:apply-templates />
                    </div>
                    <!--
                        The footer div, dropping whatever extra information is needed on the page. It will
                        most likely be something similar in structure to the currently given example. -->
                    <xsl:call-template name="buildFooter"/>

                </div>
            </body>
        </html>
    </xsl:template>


    <xsl:template name="buildHeader">
        <div class="HomeHeader">
            <div id="ucHeader_myRRUNavLinks" class="myRRUNavLinks">
                <div id="navigation" class="menu">
                    <div id="secondary" class="clear-block">
                    <ul class="links-menu">
                    <a id="ucHeader_hypLink01" href="http://www.royalroads.ca/" target="_blank">Programs</a>
					<xsl:text>&#xA0; | &#xA0;</xsl:text>
                    <a id="ucHeader_hypLink02" href="http://www.royalroads.ca/research" target="_blank">Research</a>
                    <xsl:text>&#xA0; | &#xA0;</xsl:text>
                    <a id="ucHeader_hypLink03" href="http://www.royalroads.ca/about-rru/the-university/foundation-rru/" target="_blank">Giving</a>
                    <xsl:text>&#xA0; | &#xA0;</xsl:text>
                    <a id="ucHeader_hypLink04" href="http://www.royalroads.ca/news-events" target="_blank">News and Events</a>
                    <xsl:text>&#xA0; | &#xA0;</xsl:text>
                    <a id="ucHeader_hypLink05" href="http://www.royalroads.ca/about-rru/the-university/rru-alumni/" target="_blank">Alumni</a>
                    <xsl:text>&#xA0; | &#xA0;</xsl:text>
                    <a id="ucHeader_hypLink06" href="http://myrru.royalroads.ca/learners" target="_blank">Current Students</a>
                    </ul>					
                    </div>
                </div>
            </div>  
			<div id="logoWithinBanner">
				<a href="http://www.royalroads.ca/"><img src="/docs/themes/RRULibrary/images/basic_logo.png" border="0" /></a>
			</div>
        </div>
		
        <div class="divNavigation">
			<ul id="nav">
				<li>
					<a href="http://library.royalroads.ca/">Library Home</a>
				</li>

				<li>
					<a href="http://library.royalroads.ca/">Find</a>
					<ul>
						<li><a href="http://library.royalroads.ca/videos">Videos</a></li>
						<li><a href="http://libguides.royalroads.ca/ebooks">eBooks</a></li>
						<li><a href="http://library.royalroads.ca/theses-and-projects">Theses and Projects</a></li>
						<li><a href="http://libguides.royalroads.ca/content.php?pid=96875">Statistics and Data</a></li>
						<li><a href="http://dspace.royalroads.ca/docs/">DSpace @ RRU</a></li>
						<li><a href="http://library.royalroads.ca/archives">About the RRU Archives</a></li>
					</ul>
				</li>
				<li>
					<a href="http://library.royalroads.ca/">Help</a>
					<ul>
						<li><a href="http://library.royalroads.ca/contact-us">Contact Us</a></li>
						<li><a href="http://library.royalroads.ca/refworks">RefWorks</a></li>
						<li><a href="http://libguides.royalroads.ca/browse.php">Subject Guides</a></li>
						<li><a href="http://library.royalroads.ca/writing-centre">Writing Centre</a></li>
						<li><a href="http://library.royalroads.ca/assignment">Assignment Calculator</a></li>
						<li><a href="http://library.royalroads.ca/faq">Frequently Asked Questions</a></li>
					</ul>

				</li>
				<li>
					<a href="http://library.royalroads.ca/">Services</a>
					<ul>
						<li><a href="http://voyager.royalroads.ca/vwebv/login;jsessionid=BD4B407544469EC2E6340E560ACB4E26">My Account (Renew Books)</a></li>
						<li><a href="http://library.royalroads.ca/document-delivery">Document Delivery</a></li>
						<li><a href="http://library.royalroads.ca/interlibrary-loans">Interlibrary Loan</a></li>
						<li><a href="http://library.royalroads.ca/copyright">Copyright Information</a></li>
					</ul>
				</li>
				<li>
					<a href="http://library.royalroads.ca/">About the Library</a>
					<ul>
						<li><a href="http://library.royalroads.ca/hours-operation">Hours of Operation</a></li>
						<li><a href="http://library.royalroads.ca/library-cards">Library Cards</a></li>
						<li><a href="http://library.royalroads.ca/library-policies">Library Policies</a></li>
						<li><a href="http://library.royalroads.ca/about-library">About the Library</a></li>
						<li><a href="http://library.royalroads.ca/contact-us">Feedback Form</a></li>
					</ul>
				</li>
			</ul>
		</div>
		
		<div id="askLibrarianSection">
			<div id="askLibrarianLink">
				Need Help? <a href="http://library.royalroads.ca/ask-a-librarian">Ask a Librarian</a>
			</div>
		</div>
		
		<div class="breadCrumbNavigation">
			<ul id="ds-trail"> 
				<xsl:choose>
					<xsl:when test="count(/dri:document/dri:meta/dri:pageMeta/dri:trail) = 0">
                   
						<li class="ds-trail-link first-link"> - </li>
					</xsl:when>
					<xsl:otherwise>
					<xsl:apply-templates select="/dri:document/dri:meta/dri:pageMeta/dri:trail"/>
					</xsl:otherwise>
				</xsl:choose>
			</ul>
		</div>

    </xsl:template>

    <xsl:template name="buildFooter">
        <div class="clearnone"></div>
		<br />
		<br />
		<br />
		<br />
		<div style="vertical-align:middle; text-align:left;">
<a rel="license" href="http://creativecommons.org/licenses/by-nc-sa/2.5/ca/deed.en_CA"><img hspace="25" alt="Creative Commons Licence" style="border-width:0; vertical-align:middle;" src="http://i.creativecommons.org/l/by-nc-sa/2.5/ca/88x31.png" /></a>
This work is licensed under a <a rel="license" href="http://creativecommons.org/licenses/by-nc-sa/2.5/ca/deed.en_CA">Creative Commons Attribution-NonCommercial-ShareAlike 2.5 Canada License</a>.
                </div>
		<div id="upperFooterBox">
			<div id="textUpperFooter">
				<a href="http://library.royalroads.ca/">Royal Roads University Library</a>
			</div>
		</div>
        <div class="FooterShell">
            <div id="ds-footer" class="footer">
				<br />
				<a>
					<xsl:attribute name="href">
						<xsl:value-of select="/dri:document/dri:meta/dri:pageMeta/dri:metadata[@element='contextPath'][not(@qualifier)]"/>
						<xsl:text>/contact</xsl:text>
					</xsl:attribute>
					<i18n:text>xmlui.dri2xhtml.structural.contact-link</i18n:text>
				</a>
				<xsl:text> | </xsl:text>
				<a>
					<xsl:attribute name="href">
						<xsl:value-of select="/dri:document/dri:meta/dri:pageMeta/dri:metadata[@element='contextPath'][not(@qualifier)]"/>
						<xsl:text>/feedback</xsl:text>
					</xsl:attribute>
					<i18n:text>xmlui.dri2xhtml.structural.feedback-link</i18n:text>
				</a>
				<xsl:text> | </xsl:text>
				<a href="http://www.royalroads.ca/affiliations" target="_blank">Partners &amp; Affiliations</a>
				<xsl:text> | </xsl:text>
				<a href="http://www.royalroads.ca/freedom-information-and-protection-privacy" target="_blank">Privacy Statement</a>
				<xsl:text> | </xsl:text>
				<a href="http://myrru.royalroads.ca/learners/learner-services/policies-and-procedures" target="_blank">Academic Regulations &amp; Policies</a>
				<xsl:text> | </xsl:text>
				<a href="http://computerservices.royalroads.ca/" target="_blank">Computer Services</a>
					<br />
				&#xA9;1997-2012 Royal Roads University
					<br />
				2005 Sooke Road, Victoria, British Columbia, Canada V9B 5Y2
					<br />
				Phone: 250-391-2511, Toll-free 1-800-788-8028
					<br />
				E-mail:<a href="mailto:info@royalroads.ca">info@royalroads.ca</a>
					<br />
            </div>
        </div>
    </xsl:template>
</xsl:stylesheet>
