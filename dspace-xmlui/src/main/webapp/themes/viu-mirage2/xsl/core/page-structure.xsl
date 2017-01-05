
<!--
    Main structure of the page, determines where
    header, footer, body, navigation are structurally rendered.
    Rendering of the header, footer, trail and alerts

    Author: art.lowel at atmire.com
    Author: lieven.droogmans at atmire.com
    Author: ben at atmire.com
    Author: Alexey Maslov

-->

<xsl:stylesheet xmlns:i18n="http://apache.org/cocoon/i18n/2.1"
                xmlns:dri="http://di.tamu.edu/DRI/1.0/"
                xmlns:mets="http://www.loc.gov/METS/"
                xmlns:xlink="http://www.w3.org/TR/xlink/"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
                xmlns:dim="http://www.dspace.org/xmlns/dspace/dim"
                xmlns:xhtml="http://www.w3.org/1999/xhtml"
                xmlns:mods="http://www.loc.gov/mods/v3"
                xmlns:dc="http://purl.org/dc/elements/1.1/"
                xmlns:confman="org.dspace.core.ConfigurationManager"
                exclude-result-prefixes="i18n dri mets xlink xsl dim xhtml mods dc confman">

    <xsl:output method="xml" encoding="UTF-8" indent="yes"/>

    <!--
        Requested Page URI. Some functions may alter behavior of processing depending if URI matches a pattern.
        Specifically, adding a static page will need to override the DRI, to directly add content.
    -->
    <xsl:variable name="request-uri" select="/dri:document/dri:meta/dri:pageMeta/dri:metadata[@element='request'][@qualifier='URI']"/>

    <!--
        The starting point of any XSL processing is matching the root element. In DRI the root element is document,
        which contains a version attribute and three top level elements: body, options, meta (in that order).

        This template creates the html document, giving it a head and body. A title and the CSS style reference
        are placed in the html head, while the body is further split into several divs. The top-level div
        directly under html body is called "ds-main". It is further subdivided into:
            "ds-header"  - the header div containing title, subtitle, trail and other front matter
            "ds-body"    - the div containing all the content of the page; built from the contents of dri:body
            "ds-options" - the div with all the navigation and actions; built from the contents of dri:options
            "ds-footer"  - optional footer div, containing misc information

        The order in which the top level divisions appear may have some impact on the design of CSS and the
        final appearance of the DSpace page. While the layout of the DRI schema does favor the above div
        arrangement, nothing is preventing the designer from changing them around or adding new ones by
        overriding the dri:document template.
    -->
    <xsl:template match="dri:document">

        <xsl:choose>
            <xsl:when test="not($isModal)">

                <!--THIS (LACK OF) INDENTATION IS ON PURPOSE, DON'T CHANGE IT ##START##-->
            <xsl:text disable-output-escaping='yes'>&lt;!DOCTYPE html&gt;
            </xsl:text>
            <xsl:text disable-output-escaping="yes">&lt;!--[if lt IE 7]&gt; &lt;html class=&quot;no-js lt-ie9 lt-ie8 lt-ie7&quot; lang=&quot;en&quot;&gt; &lt;![endif]--&gt;
            &lt;!--[if IE 7]&gt;    &lt;html class=&quot;no-js lt-ie9 lt-ie8&quot; lang=&quot;en&quot;&gt; &lt;![endif]--&gt;
            &lt;!--[if IE 8]&gt;    &lt;html class=&quot;no-js lt-ie9&quot; lang=&quot;en&quot;&gt; &lt;![endif]--&gt;
            &lt;!--[if gt IE 8]&gt;&lt;!--&gt; &lt;html class=&quot;no-js&quot; lang=&quot;en&quot;&gt; &lt;!--&lt;![endif]--&gt;
            </xsl:text>
                <!--THIS (LACK OF) INDENTATION IS ON PURPOSE, DON'T CHANGE IT ##END##-->

                <!-- First of all, build the HTML head element -->

                <xsl:call-template name="buildHead"/>

                <!-- Then proceed to the body -->
                <body>
                    <!-- Prompt IE 6 users to install Chrome Frame. Remove this if you support IE 6.
                   chromium.org/developers/how-tos/chrome-frame-getting-started -->
                    <!--[if lt IE 7]><p class=chromeframe>Your browser is <em>ancient!</em> <a href="http://browsehappy.com/">Upgrade to a different browser</a> or <a href="http://www.google.com/chromeframe/?redirect=true">install Google Chrome Frame</a> to experience this site.</p><![endif]-->
                    <xsl:choose>
                        <xsl:when
                                test="/dri:document/dri:meta/dri:pageMeta/dri:metadata[@element='framing'][@qualifier='popup']">
                            <xsl:apply-templates select="dri:body/*"/>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:call-template name="buildHeader"/>
                            <xsl:call-template name="buildTrail"/>
                            <!--javascript-disabled warning, will be invisible if javascript is enabled-->
                            <div id="no-js-warning-wrapper" class="hidden">
                                <div id="no-js-warning">
                                    <div class="notice failure">
                                        <xsl:text>JavaScript is disabled for your browser. Some features of this site
                                            may not work without it.
                                        </xsl:text>
                                    </div>
                                </div>
                            </div>

                            <div class="full-width-holder">
                            <div id="main-container" class="container">
                                <div class="row row-offcanvas row-offcanvas-right">
                                    <div class="horizontal-slider clearfix">
                                        <div class="col-xs-12 col-sm-12 col-md-9 main-content">
                                            <xsl:apply-templates select="*[not(self::dri:options)]"/>
                                        </div>
                                        <div class="col-xs-6 col-sm-3 sidebar-offcanvas" id="sidebar" role="navigation">
                                            <xsl:apply-templates select="dri:options"/>
                                        </div>
                                    </div>
                                </div>
                            </div>
                            </div>

                            <!--
                        The footer div, dropping whatever extra information is needed on the page. It will
                        most likely be something similar in structure to the currently given example. -->
                            <xsl:call-template name="buildFooter"/>

                        </xsl:otherwise>
                    </xsl:choose>
                    <!-- Javascript at the bottom for fast page loading -->
                    <xsl:call-template name="addJavascript"/>
                </body>
                <xsl:text disable-output-escaping="yes">&lt;/html&gt;</xsl:text>

            </xsl:when>
            <xsl:otherwise>
                <!-- This is only a starting point. If you want to use this feature you need to implement
                JavaScript code and a XSLT template by yourself. Currently this is used for the DSpace Value Lookup -->
                <xsl:apply-templates select="dri:body" mode="modal"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <!-- The HTML head element contains references to CSS as well as embedded JavaScript code. Most of this
    information is either user-provided bits of post-processing (as in the case of the JavaScript), or
    references to stylesheets pulled directly from the pageMeta element. -->
    <xsl:template name="buildHead">
        <head>
            <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>

            <!-- Use the .htaccess and remove these lines to avoid edge case issues.
             More info: h5bp.com/i/378 -->
            <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1"/>

            <!-- Mobile viewport optimized: h5bp.com/viewport -->
            <meta name="viewport" content="width=device-width,initial-scale=1"/>

            <link rel="shortcut icon">
                <xsl:attribute name="href">
                    <xsl:value-of select="$theme-path"/>
                    <xsl:text>images/favicon.ico</xsl:text>
                </xsl:attribute>
            </link>
            <link rel="apple-touch-icon">
                <xsl:attribute name="href">
                    <xsl:value-of select="$theme-path"/>
                    <xsl:text>images/apple-touch-icon.png</xsl:text>
                </xsl:attribute>
            </link>

            <meta name="Generator">
                <xsl:attribute name="content">
                    <xsl:text>DSpace</xsl:text>
                    <xsl:if test="/dri:document/dri:meta/dri:pageMeta/dri:metadata[@element='dspace'][@qualifier='version']">
                        <xsl:text> </xsl:text>
                        <xsl:value-of select="/dri:document/dri:meta/dri:pageMeta/dri:metadata[@element='dspace'][@qualifier='version']"/>
                    </xsl:if>
                </xsl:attribute>
            </meta>

            <!-- Add stylsheets -->

            <!--TODO figure out a way to include these in the concat & minify-->
            <xsl:for-each select="/dri:document/dri:meta/dri:pageMeta/dri:metadata[@element='stylesheet']">
                <link rel="stylesheet" type="text/css">
                    <xsl:attribute name="media">
                        <xsl:value-of select="@qualifier"/>
                    </xsl:attribute>
                    <xsl:attribute name="href">
                        <xsl:value-of select="$theme-path"/>
                        <xsl:value-of select="."/>
                    </xsl:attribute>
                </link>
            </xsl:for-each>

            <!--### CLASSIC MIRAGE COLOR SCHEME START ###-->
            <link rel="stylesheet" href="{concat($theme-path, '../_precompiled-mirage2/styles/bootstrap-classic-mirage-colors-min.css')}"/>
            <link rel="stylesheet" href="{concat($theme-path, '../_precompiled-mirage2/styles/classic-mirage-style.css')}"/>
            <!--### CLASSIC MIRAGE COLOR SCHEME END ###-->

            <!--### BOOTSTRAP COLOR SCHEME START ###-->
            <!--<link rel="stylesheet" href="{concat($theme-path, 'styles/bootstrap-min.css')}"/>-->
            <!--### BOOTSTRAP COLOR SCHEME END ###-->

            <link rel="stylesheet" href="{concat($theme-path, '../_precompiled-mirage2/styles/dspace-bootstrap-tweaks.css')}"/>
            <link rel="stylesheet" href="{concat($theme-path, '../_precompiled-mirage2/styles/jquery-ui-1.10.3.custom.css')}"/>

            <link rel="stylesheet" href="{concat($theme-path, '../_precompiled-mirage2/vendor/BookReader/BookReader.css')}"/>
            <link rel="stylesheet" href="{concat($theme-path, '../_precompiled-mirage2/styles/snazy.css')}"/>

            <!-- Local css -->
            <link rel="stylesheet" href="{concat($theme-path, 'styles/theme.css')}"/>

            <!-- Add syndication feeds -->
            <xsl:for-each select="/dri:document/dri:meta/dri:pageMeta/dri:metadata[@element='feed']">
                <link rel="alternate" type="application">
                    <xsl:attribute name="type">
                        <xsl:text>application/</xsl:text>
                        <xsl:value-of select="@qualifier"/>
                    </xsl:attribute>
                    <xsl:attribute name="href">
                        <xsl:value-of select="."/>
                    </xsl:attribute>
                </link>
            </xsl:for-each>

            <!--  Add OpenSearch auto-discovery link -->
            <xsl:if test="/dri:document/dri:meta/dri:pageMeta/dri:metadata[@element='opensearch'][@qualifier='shortName']">
                <link rel="search" type="application/opensearchdescription+xml">
                    <xsl:attribute name="href">
                        <xsl:value-of select="/dri:document/dri:meta/dri:pageMeta/dri:metadata[@element='request'][@qualifier='scheme']"/>
                        <xsl:text>://</xsl:text>
                        <xsl:value-of select="/dri:document/dri:meta/dri:pageMeta/dri:metadata[@element='request'][@qualifier='serverName']"/>
                        <xsl:text>:</xsl:text>
                        <xsl:value-of select="/dri:document/dri:meta/dri:pageMeta/dri:metadata[@element='request'][@qualifier='serverPort']"/>
                        <xsl:value-of select="$context-path"/>
                        <xsl:text>/</xsl:text>
                        <xsl:value-of select="/dri:document/dri:meta/dri:pageMeta/dri:metadata[@element='opensearch'][@qualifier='context']"/>
                        <xsl:text>description.xml</xsl:text>
                    </xsl:attribute>
                    <xsl:attribute name="title" >
                        <xsl:value-of select="/dri:document/dri:meta/dri:pageMeta/dri:metadata[@element='opensearch'][@qualifier='shortName']"/>
                    </xsl:attribute>
                </link>
            </xsl:if>

            <!-- The following javascript removes the default text of empty text areas when they are focused on or submitted -->
            <!-- There is also javascript to disable submitting a form when the 'enter' key is pressed. -->
            <script>
                //Clear default text of emty text areas on focus
                function tFocus(element)
                {
                if (element.value == '<i18n:text>xmlui.dri2xhtml.default.textarea.value</i18n:text>'){element.value='';}
                }
                //Clear default text of emty text areas on submit
                function tSubmit(form)
                {
                var defaultedElements = document.getElementsByTagName("textarea");
                for (var i=0; i != defaultedElements.length; i++){
                if (defaultedElements[i].value == '<i18n:text>xmlui.dri2xhtml.default.textarea.value</i18n:text>'){
                defaultedElements[i].value='';}}
                }
                //Disable pressing 'enter' key to submit a form (otherwise pressing 'enter' causes a submission to start over)
                function disableEnterKey(e)
                {
                var key;

                if(window.event)
                key = window.event.keyCode;     //Internet Explorer
                else
                key = e.which;     //Firefox and Netscape

                if(key == 13)  //if "Enter" pressed, then disable!
                return false;
                else
                return true;
                }
            </script>

            <xsl:text disable-output-escaping="yes">&lt;!--[if lt IE 9]&gt;
                &lt;script src="</xsl:text><xsl:value-of select="concat($theme-path, '../_precompiled-mirage2/vendor/html5shiv/dist/html5shiv.js')"/><xsl:text disable-output-escaping="yes">"&gt;&#160;&lt;/script&gt;
                &lt;script src="</xsl:text><xsl:value-of select="concat($theme-path, '../_precompiled-mirage2/vendor/respond/respond.min.js')"/><xsl:text disable-output-escaping="yes">"&gt;&#160;&lt;/script&gt;
                &lt;![endif]--&gt;</xsl:text>

            <!-- Modernizr enables HTML5 elements & feature detects -->
            <script src="{concat($theme-path, '../_precompiled-mirage2/vendor/modernizr/modernizr.js')}">&#160;</script>

            <!-- Add the title in -->
            <xsl:variable name="page_title" select="/dri:document/dri:meta/dri:pageMeta/dri:metadata[@element='title'][last()]" />
            <title>
                <xsl:choose>
                    <xsl:when test="starts-with($request-uri, 'page/about')">
                        <xsl:text>About This Repository</xsl:text>
                    </xsl:when>
                    <xsl:when test="not($page_title)">
                        <xsl:text>  </xsl:text>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:copy-of select="$page_title/node()" />
                    </xsl:otherwise>
                </xsl:choose>
            </title>

            <!-- Head metadata in item pages -->
            <xsl:if test="/dri:document/dri:meta/dri:pageMeta/dri:metadata[@element='xhtml_head_item']">
                <xsl:value-of select="/dri:document/dri:meta/dri:pageMeta/dri:metadata[@element='xhtml_head_item']"
                              disable-output-escaping="yes"/>
            </xsl:if>

            <!-- Add all Google Scholar Metadata values -->
            <xsl:for-each select="/dri:document/dri:meta/dri:pageMeta/dri:metadata[substring(@element, 1, 9) = 'citation_']">
                <meta name="{@element}" content="{.}"></meta>
            </xsl:for-each>

            <script src="//jwpsrv.com/library/tdG5srbdEeSqzQp+lcGdIw.js"></script>
	    <link rel="sitemap">
                <xsl:attribute name="href">
                    <xsl:value-of
                            select="/dri:document/dri:meta/dri:pageMeta/dri:metadata[@element='contextPath'][not(@qualifier)]"/>
                    <xsl:text>/sitemap</xsl:text>
                </xsl:attribute>
            </link>

            <!-- Add a google analytics script if the key is present -->
            <xsl:if test="/dri:document/dri:meta/dri:pageMeta/dri:metadata[@element='google'][@qualifier='analytics']">
                <script><xsl:text>
                    (function(i,s,o,g,r,a,m){i['GoogleAnalyticsObject']=r;i[r]=i[r]||function(){
                    (i[r].q=i[r].q||[]).push(arguments)},i[r].l=1*new Date();a=s.createElement(o),
                    m=s.getElementsByTagName(o)[0];a.async=1;a.src=g;m.parentNode.insertBefore(a,m)
                    })(window,document,'script','//www.google-analytics.com/analytics.js','ga');

                    ga('create', '</xsl:text><xsl:value-of select="/dri:document/dri:meta/dri:pageMeta/dri:metadata[@element='google'][@qualifier='analytics']"/><xsl:text>', 'auto');
                    ga('send', 'pageview');
                </xsl:text></script>
            </xsl:if>


            <!-- Custom CSS for VIU -->
            <link type="text/css" rel="stylesheet" href="https://library.viu.ca/sites/default/files/advagg_css/css__2nIbOAe8O4Xeh_SVyhkHf8dG5OkYBxsYB_3IgCBMLfE__XfE2iiC6lFZyL-r5hedBC_KFVr-hNEw_8cFGHbcgG2g__HuTMoItqqYuhtXWLd0ciCnl2e3iB8_XT3-_U1Dta3Hc.css" media="all" />
            <link type="text/css" rel="stylesheet" href="https://library.viu.ca/sites/default/files/advagg_css/css__QZBV4dHUrecX-NylMVSpBuwHSrmViuRbJGw_fnVGqxw__El20A5KOyNkNcFdChcb5hyCWy3uIRXpOF6jt4XIP5tU__HuTMoItqqYuhtXWLd0ciCnl2e3iB8_XT3-_U1Dta3Hc.css" media="screen" />
            <link type="text/css" rel="stylesheet" href="https://library.viu.ca/sites/default/files/advagg_css/css__nsO8_7d0KMc9rX_RquNh2MJ7W-W6ol3Omc8MIGNIU8A__8uOOjocNpMQH3IbszLwgOZAK6FZ2iAUGtmPtoUy1Ew0__HuTMoItqqYuhtXWLd0ciCnl2e3iB8_XT3-_U1Dta3Hc.css" media="all" />
            <link type="text/css" rel="stylesheet" href="https://library.viu.ca/sites/default/files/advagg_css/css__IYybZiYGE_E54YZWksg2gbpTofm9xW4cjZlgtsTvANI__JcYWWOyHMcquBEFfB7rZUAZ5A0ycfGzPWE4BXVpmjuU__HuTMoItqqYuhtXWLd0ciCnl2e3iB8_XT3-_U1Dta3Hc.css" media="all" />
            <link type="text/css" rel="stylesheet" href="https://library.viu.ca/sites/default/files/advagg_css/css__SFeT2YOHs2ALYwm1zvf5MJN0sErI_z_49sS8Lj2j3Kk__xs3IxVh4YVNvUY9z-iNy1o5CtzP-JqDujRbm24TchA0__HuTMoItqqYuhtXWLd0ciCnl2e3iB8_XT3-_U1Dta3Hc.css" media="all" />
            <link type="text/css" rel="stylesheet" href="https://library.viu.ca/sites/default/files/advagg_css/css__4voJ7WF5xVfT8dOJU0GnI0a5v8CvMzzDWUfP_vg6y0w__GFVFKLTaFdKfZcAD7rhxiKrVE0eU8chBMG5zByqkCMk__HuTMoItqqYuhtXWLd0ciCnl2e3iB8_XT3-_U1Dta3Hc.css" media="all" />
            <link type="text/css" rel="stylesheet" href="https://library.viu.ca/sites/default/files/advagg_css/css__ecrzeobboLpJbCelFX76FLJ-_ZML51GAyOL6P8SE8cI__7LCWEsNRAsnx4zL1TRdXbe2m-UOUbB5lYYVHyFe9lbQ__HuTMoItqqYuhtXWLd0ciCnl2e3iB8_XT3-_U1Dta3Hc.css" media="all" />
            <link type="text/css" rel="stylesheet" href="https://library.viu.ca/sites/default/files/advagg_css/css__BDUQRyquX2_9Cjk3-3bHS8DiPqG0XY-4A214JkTk9b4__BSRV206S_FWWpEMNfB6H0BYpe_txiAmJ10udIdRQJkw__HuTMoItqqYuhtXWLd0ciCnl2e3iB8_XT3-_U1Dta3Hc.css" media="all" />
            <link type="text/css" rel="stylesheet" href="https://library.viu.ca/sites/default/files/advagg_css/css__o8jy7sOgo9PrB4uZsa0XbB2IHUzomyxHeWqZDHOIrCk__XiCWHkbXwJ13KbnEZZRIRf6WizNiaNEiIztGTD0sXL0__HuTMoItqqYuhtXWLd0ciCnl2e3iB8_XT3-_U1Dta3Hc.css" media="all" />
            <link type="text/css" rel="stylesheet" href="https://library.viu.ca/sites/default/files/advagg_css/css__B6BlvSnv-wJigqKVrUrfgOEt822xR3rhKPoI1gwD58k__UWMZ6I42fq_zZJtPEL9y9mV17S9NHONKTAwAAPKIxoE__HuTMoItqqYuhtXWLd0ciCnl2e3iB8_XT3-_U1Dta3Hc.css" media="all" />
            <link type="text/css" rel="stylesheet" href="https://library.viu.ca/sites/default/files/advagg_css/css__cxWhIHlpMDUN0JEOigsknBUcBKHiyFA4nYZ0280vf68__PPqCiy3O0IyMTV9JanCE4bbmn9st-IdfztrG9E0RgAg__HuTMoItqqYuhtXWLd0ciCnl2e3iB8_XT3-_U1Dta3Hc.css" media="print" />
            <link type="text/css" rel="stylesheet" href="https://library.viu.ca/sites/default/files/colorizer/library_radix-653e8846.css" media="all" />

            <!--[if lte IE 9]>
            <link type="text/css" rel="stylesheet" href="https://library.viu.ca/sites/default/files/advagg_css/css__-I40o1fdHFASIRyqUR6cUTiv9KCNhODopDvH4sOxu9M__OsR1L4Yqnh2R5EYhsgDhEaxTLPl6IG1yOrU5jzKaWYw__HuTMoItqqYuhtXWLd0ciCnl2e3iB8_XT3-_U1Dta3Hc.css" media="all" />
            <![endif]-->

            <!--[if IE 7]>
            <link type="text/css" rel="stylesheet" href="https://library.viu.ca/sites/default/files/advagg_css/css__B8yO9tjioVgqvQsE0jdYTl93XkX1Zy4tFiqH-Lyz3uQ__Whubzdv9zyTyeqdpEpouWE1QVQ0tGlMpbn3eJpTuHog__HuTMoItqqYuhtXWLd0ciCnl2e3iB8_XT3-_U1Dta3Hc.css" media="all" />
            <![endif]-->

            <!--[if IE 8]>
            <link type="text/css" rel="stylesheet" href="https://library.viu.ca/sites/default/files/advagg_css/css__MPaGpOoPqPWA41yQmsCFaxR9mqlcJVLM070T1VeRaX0__Whubzdv9zyTyeqdpEpouWE1QVQ0tGlMpbn3eJpTuHog__HuTMoItqqYuhtXWLd0ciCnl2e3iB8_XT3-_U1Dta3Hc.css" media="all" />
            <![endif]-->

            <!--[if IE 7]>
            <link type="text/css" rel="stylesheet" href="https://library.viu.ca/sites/default/files/advagg_css/css__wPdx3O6N-tRTjDiAlGXHFVRQbzUXh8letNcZSxXL0FA__Whubzdv9zyTyeqdpEpouWE1QVQ0tGlMpbn3eJpTuHog__HuTMoItqqYuhtXWLd0ciCnl2e3iB8_XT3-_U1Dta3Hc.css" media="all" />
            <![endif]-->

            <!--[if IE 8]>
            <link type="text/css" rel="stylesheet" href="https://library.viu.ca/sites/default/files/advagg_css/css__S3wBh0qEnKJVLOeuBeUm4-KeTIRx8jBwVLKKGfH3dYE__Whubzdv9zyTyeqdpEpouWE1QVQ0tGlMpbn3eJpTuHog__HuTMoItqqYuhtXWLd0ciCnl2e3iB8_XT3-_U1Dta3Hc.css" media="all" />
            <![endif]-->
            <link type="text/css" rel="stylesheet" href="https://library.viu.ca/sites/default/files/advagg_css/css___ROcqMUFFQ2jd1RRkwodPG7agOC9dc0DjC24yybwKaE__aDjgJ07okmgIBqya3Fay6RwHnt02_a2Cy5BXvHWu4-A__HuTMoItqqYuhtXWLd0ciCnl2e3iB8_XT3-_U1Dta3Hc.css" media="all" />


        </head>
    </xsl:template>


    <!-- The header (distinct from the HTML head element) contains the title, subtitle, login box and various
        placeholders for header images -->
    <xsl:template name="buildHeader">
        <header>
            <!-- 1-->
            <div class="header-viu">

                <!-- Topbar -->
                <div class="topbar-v1">
                    <div class="container">
                        <div class="row">
                            <!--      <div class="col-md-2">-->
                            <!--        <ul class="list-inline top-list top-v1-contacts">-->
                            <!--          --><!--        </ul>-->
                            <!--      </div>-->
                            <button type="button" class="topbar-toggle" data-toggle="collapse" data-target=".topbar-responsive-collapse">
                                <span class="topbar-toggle-text">VIU</span>
                                <i class="fa fa-bars"></i>
                            </button>

                            <div class="col-md-12 top-v1-data-container collapse topbar-collapse topbar-responsive-collapse">
                                <ul class="top-list top-v1-data mega-menu">

                                    <li><a href="https://www.viu.ca/"><i class="fa fa-home"></i> <span class="mobile-extra">VIU.ca</span></a></li>

                                    <li><a href="https://www.viu.ca/healthandsafety/emergency-information/" class="top-emergency-link">Emergency Info</a></li>

                                    <li><a href="https://www.viu.ca/library/">Library</a></li>

                                    <li class="click-dropdown"><a href="#" data-toggle="dropdown">Campuses <i class="fa fa-angle-down"></i></a>
                                        <ul class="dropdown-menu">
                                            <li><a href="https://www.viu.ca">Nanaimo</a></li>
                                            <li><a href="https://www.viu.ca/parksville">Parksville-Qualicum</a></li>
                                            <li><a href="http://www.cc.viu.ca/">Cowichan</a></li>
                                            <li><a href="http://www.pr.viu.ca/">Powell River</a></li>
                                        </ul>
                                    </li>

                                    <li><a href="https://www.viu.ca/calendar/">Programs and Courses</a></li>

                                    <li class="click-dropdown"><a href="#" data-toggle="dropdown">Directories <i class="fa fa-angle-down"></i></a>
                                        <ul class="dropdown-menu">
                                            <li><a href="https://www.viu.ca/directory">Employee Directory</a></li>
                                            <li><a href="https://www.viu.ca/calendar/instructional.asp">Instructional Departments</a></li>
                                            <li><a href="https://www.viu.ca/services">Service Departments</a></li>
                                        </ul>
                                    </li>

                                    <li class="click-dropdown mega-menu-fullwidth"><a href="#" data-toggle="dropdown">Quick Links <i class="fa fa-angle-down"></i></a>
                                        <ul class="dropdown-menu">
                                            <li>
                                                <div class="mega-menu-content">
                                                    <div class="container">
                                                        <div class="row equal-height">
                                                            <div class="col-md-2 equal-height-in">
                                                                <ul class="list-unstyled equal-height-list">
                                                                    <li><h3>Contact Info</h3></li>

                                                                    <li class="first">
                                                                        <p>Vancouver Island University<br/>
                                                                            Nanaimo Campus<br/>900 Fifth Street<br/>
                                                                            Nanaimo, BC<br/>Canada V9R 5S5<br/>
                                                                            Toll-free 1.888.920.2221<br/>
                                                                            Switchboard 250.753.3245<br/>
                                                                            Email <a href="mailto:info@viu.ca">info@viu.ca</a>
                                                                            <br/><br/>
                                                                            Copyright ©<br/>Vancouver Island University
                                                                        </p>
                                                                    </li>

                                                                </ul>
                                                            </div>
                                                            <div class="col-md-2 equal-height-in">
                                                                <ul class="list-unstyled equal-height-list">
                                                                    <li><h3>About VIU</h3></li>

                                                                    <li class="first"><a href="http://www.viu.ca/administration/">Administration</a></li>
                                                                    <li><a href="http://www.viu.ca/giving/">Advancement</a></li>
                                                                    <li><a href="http://www.viu.ca/alumni/">Alumni Association</a></li>
                                                                    <li><a href="http://cc.viu.ca/">Cowichan Campus</a></li>
                                                                    <li><a href="http://www.viu.ca/parksville/">Parksville-Qualicum Centre</a></li>
                                                                    <li><a href="http://pr.viu.ca/">Powell River Campus</a></li>
                                                                    <li><a href="http://www.viu.ca/gap/">Governance</a></li>
                                                                    <li><a href="http://www.viu.ca/integratedplanning/">Integrated Planning</a></li>
                                                                    <li><a href="http://www.viu.ca/HumanResources/postings/">Employment</a></li>
                                                                    <li><a href="http://www.viu.ca/mission/">Mission</a></li>
                                                                    <li class="last"><a href="http://www.viu.ca/retrospective/">History</a></li>


                                                                </ul>
                                                            </div>
                                                            <div class="col-md-2 equal-height-in">
                                                                <ul class="list-unstyled equal-height-list">
                                                                    <li><h3>Academics</h3></li>

                                                                    <li class="first"><a href="http://www.viu.ca/calendar/GeneralInformation/admissions.asp">Admissions</a></li>
                                                                    <li><a href="http://www.viu.ca/calendar/GeneralInformation/registration.asp">Registration</a></li>
                                                                    <li><a href="http://www.viu.ca/calendar/">Programs and Courses</a></li>
                                                                    <li><a href="http://www.viu.ca/dualcredit/">Dual Credit</a></li>
                                                                    <li><a href="http://www.viu.ca/financialaid/">Financial Aid and Awards</a></li>
                                                                    <li><a href="http://www.viu.ca/calendar/credential/mastersdegrees.asp">Graduate Programs</a></li>
                                                                    <li><a href="http://www.viu.ca/international/">International Education</a></li>
                                                                    <li><a href="http://www.viu.ca/library/">Library</a></li>
                                                                    <li class="last"><a href="http://www.viu.ca/ciel/">Online Education</a></li>


                                                                </ul>
                                                            </div>
                                                            <div class="col-md-2 equal-height-in">
                                                                <ul class="list-unstyled equal-height-list">
                                                                    <li><h3>Athletics</h3></li>

                                                                    <li class="first"><a href="http://mariners.viu.ca/">VIU Mariners</a></li>
                                                                    <li><a href="http://mariners.viu.ca/teams/">Mariners Teams</a></li>
                                                                    <li><a href="http://www.viu.ca/campusrec/">Campus Recreation</a></li>
                                                                    <li class="last"><a href="http://mariners.viu.ca/community/summer-camps/">Summer Camps</a></li>

                                                                </ul>
                                                            </div>

                                                            <div class="col-md-2 equal-height-in">
                                                                <ul class="list-unstyled equal-height-list">
                                                                    <li><h3>Campus Life</h3></li>

                                                                    <li class="first"><a href="http://www.viubookstore.ca/">Bookstore</a></li>
                                                                    <li><a href="http://www.viu.ca/counselling/">Counseling</a></li>
                                                                    <li><a href="http://www.viu.ca/disabilityservices/">Disability Services</a></li>
                                                                    <li><a href="http://www.viu.ca/foodservices/">Food Services</a></li>
                                                                    <li><a href="http://www.viu.ca/health/">Health and Wellness</a></li>
                                                                    <li><a href="http://viuresidences.ca/">Housing</a></li>
                                                                    <li><a href="http://u.viu.ca/">Join One</a></li>
                                                                    <li><a href="http://www.viu.ca/sas/">Services for Aboriginal Students</a></li>
                                                                    <li><a href="http://www.viu.ca/sustainability/">Sustainability</a></li>
                                                                    <li><a href="http://www.viu.ca/parking/">Parking</a></li>
                                                                    <li class="last"><a href="http://www.viu.ca/events/">Events</a></li>

                                                                </ul>
                                                            </div>

                                                            <div class="col-md-2 equal-height-in">
                                                                <ul class="list-unstyled equal-height-list">
                                                                    <li><h3>Extension and Outreach</h3></li>

                                                                    <li class="first"><a href="http://www.viu.ca/pdt/customizedtraining.asp">Contract Training</a></li>
                                                                    <li><a href="http://www.viu.ca/pdt/courses-and-programs/index.asp">Professional Development and Training Courses</a></li>
                                                                    <li><a href="http://www.viu.ca/summersession/">Summer Session</a></li>
                                                                    <li><a href="http://www.viu.ca/eldercollege/">ElderCollege</a></li>
                                                                    <li><a href="http://www.viu.ca/grandkids/">GrandKids University</a></li>
                                                                    <li class="last"><a href="http://www.viu.ca/summercamps/">Youth Summer Camps</a></li>


                                                                </ul>
                                                            </div>
                                                        </div>
                                                        <div class="row equal-height">
                                                            <div class="col-md-2 equal-height-in">
                                                                <ul class="list-unstyled equal-height-list">
                                                                    <li><h3>Social Media</h3></li>

                                                                    <li class="first"><a href="http://www.facebook.com/LoveWhereYouLearn">Facebook</a></li>
                                                                    <li><a href="http://instagram.com/viuniversity">Instagram</a></li>
                                                                    <li><a href="http://www.linkedin.com/companies/vancouver-island-university">LinkedIn</a></li>
                                                                    <li><a href="http://www.viu.ca/rss/">RSS</a></li>
                                                                    <li><a href="http://twitter.com/VIUniversity">Twitter</a></li>
                                                                    <li class="last"><a href="http://www.youtube.com/user/viuchannel">YouTube</a></li>

                                                                </ul>
                                                            </div>
                                                            <div class="col-md-2 equal-height-in">
                                                                <ul class="list-unstyled equal-height-list">
                                                                    <li><h3>Campus Services</h3></li>

                                                                    <li class="first"><a href="http://www.viubookstore.ca/">Bookstore</a></li>
                                                                    <li><a href="http://www.viu.ca/foodservices/">Food Services</a></li>
                                                                    <li><a href="http://www.viu.ca/discoveryroom/">Discovery Room Restaurant</a></li>
                                                                    <li><a href="http://www.viu.ca/directory">Employee Directory</a></li>
                                                                    <li><a href="http://www.viu.ca/directory?select=expertise">Find VIU Experts</a></li>
                                                                    <li><a href="http://www.viu.ca/eventservices/">Event Services</a></li>
                                                                    <li><a href="http://www.viu.ca/catering/">Campus Caterers</a></li>
                                                                    <li><a href="http://www.viu.ca/parking/">Parking and Security</a></li>
                                                                    <li class="last"><a href="http://www.viu.ca/about/services.asp">Service Departments</a></li>

                                                                </ul>
                                                            </div>
                                                            <div class="col-md-2 equal-height-in">
                                                                <ul class="list-unstyled equal-height-list">
                                                                    <li><h3>Public Engagement</h3></li>

                                                                    <li class="first"><a href="http://www.viu.ca/giving/">Advancement and Alumni</a></li>
                                                                    <li><a href="http://www.viu.ca/universityrelations/contact_comm.aspx">Communications and Public Engagement</a></li>
                                                                    <li><a href="http://www.viu.ca/governmentrelations/">Government Relations</a></li>
                                                                    <li class="last"><a href="http://www.viu.ca/universityrelations/">University Relations</a></li>

                                                                </ul>
                                                            </div>
                                                            <div class="col-md-2 equal-height-in">
                                                                <ul class="list-unstyled equal-height-list">
                                                                    <li><h3>Faculties</h3></li>

                                                                    <li class="first"><a href="http://www.viu.ca/cap/">Academic &amp; Career Preparation</a></li>
                                                                    <li><a href="http://www.viu.ca/artsandhumanities/">Arts &amp; Humanities</a></li>
                                                                    <li><a href="http://www.viu.ca/education/">Education</a></li>
                                                                    <li><a href="http://www.viu.ca/hhs/">Health &amp; Human Services</a></li>
                                                                    <li><a href="http://www.viu.ca/international/">International Programs</a></li>
                                                                    <li><a href="http://www.viu.ca/scienceandtechnology/">Science &amp; Technology</a></li>
                                                                    <li><a href="http://www.viu.ca/socialsciences/">Social Sciences</a></li>
                                                                    <li><a href="http://www.viu.ca/management/">Management</a></li>
                                                                    <li class="last"><a href="http://www.viu.ca/tat/">Trades &amp; Applied Technology</a></li>

                                                                </ul>
                                                            </div>

                                                            <div class="col-md-2 equal-height-in">
                                                                <ul class="list-unstyled equal-height-list">
                                                                    <li><h3>Organizational Structure</h3></li>

                                                                    <li class="first"><a href="http://www.viu.ca/gap/">Governance, Administration, &amp; Planning</a></li>
                                                                    <li><a href="http://www.viu.ca/calendar/instructional.asp">Instructional Departments</a></li>
                                                                    <li><a href="http://www.viu.ca/pvpa/">Provost, and VP Academic</a></li>
                                                                    <li><a href="http://www.viu.ca/policies/">Policies &amp; Procedures</a></li>
                                                                    <li><a href="http://www.viu.ca/president/">President's Office</a></li>
                                                                    <li><a href="http://www.viu.ca/universityrelations/">University Relations</a></li>
                                                                    <li><a href="http://www.viu.ca/vpadmin/">VP Admin and Finance</a></li>
                                                                    <li class="last"><a href="http://www.viu.ca/president/docs/InstitutionalGovernanceJanuary2013.pdf">VIU Organization Chart </a></li>


                                                                </ul>
                                                            </div>

                                                            <div class="col-md-2 equal-height-in">
                                                                <ul class="list-unstyled equal-height-list">
                                                                    <li><h3>Publications</h3></li>

                                                                    <li class="first"><a href="http://www.viu.ca/integratedplanning/RegionalStrategy.asp">Regional Strategy Plan</a></li>
                                                                    <li><a href="http://www.viu.ca/impact/">VIU Impact Report</a></li>
                                                                    <li><a href="http://www.viu.ca/docs/VIU-Report-to-the-Community.pdf">VIU Report to the Community </a></li>
                                                                    <li class="last"><a href="http://www.viu.ca/docs/VIU-AddingValue.pdf">Adding Value to Your Community</a></li>

                                                                </ul>
                                                            </div>
                                                        </div>
                                                    </div>
                                                </div>
                                            </li>
                                        </ul>
                                    </li>

                                    <li><a href="https://www.viu.ca/contact/">Contact Us</a></li>


                                    <li class="click-dropdown"><a href="#" data-toggle="dropdown">Login</a>
                                        <ul class="dropdown-menu">
                                            <li><a href="https://learn.viu.ca/">VIULearn(D2L)</a></li>
                                            <li><a href="https://isweb.viu.ca/SRS/mystudentrecord.htm">Student Record</a></li>
                                        </ul>
                                    </li>


                                    <li>
                                        <a href="#" class="search">
                                            <i class="fa fa-search search-btn"></i>
                                        </a>

                                        <form action="https://www.viu.ca/search/" method="get" name="search" id="search">

                                            <div class="search-open">
                                                <div class="input-group animated fadeInDown">

                                                    <input type="text" name="_q" class="form-control" placeholder="Search" value=""/>
                                                    <input type="hidden" name="site" value=""/>

                                                    <span class="input-group-btn">
                                                        <button type="submit" class="btn-u">Go</button>
                                                    </span>

                                                </div>
                                            </div>
                                        </form>
                                    </li>        </ul>
                            </div>
                        </div>
                    </div>
                </div>
                <!-- End Topbar -->


            </div>

            <!-- 2 -->
            <div class="wrapper">
                <div class="header-viu">


                <!-- Navbar -->
                <div class="navbar navbar-default" role="navigation">


                    <!-- Brand and toggle get grouped for better mobile display -->
                    <div class="navbar-header">

                        <div class="header-banner-region container no-padding">
                            <a class="navbar-brand" href="https://www.viu.ca">
                                <img id="logo-header" src="{$theme-path}/images/viu-logo.png" alt="Logo"/>
                            </a>
                            <div class="header-banner">
                                <a class="banner-title-link" href="/"><h1 class="header-banner-title bigtext" id="bigtext-id0"><em class="bigtext-line0">Library</em></h1></a>
                            </div>
                        </div>

                    </div>

                    <a role="button" class="navbar-toggle" data-toggle="collapse" data-target=".navbar-responsive-collapse">
                        <span class="full-width-menu">Menu</span>
                        <span class="icon-toggle">
                            <span class="icon-bar"></span>
                            <span class="icon-bar"></span>
                            <span class="icon-bar"></span>
                        </span>
                    </a>
                    <!-- Collect the nav links, forms, and other content for toggling -->
                    <div class="collapse navbar-collapse navbar-responsive-collapse">
                        <div class="container no-padding">
                            <!--BEGIN DSPACE INSIDE NAV-->
                            <div class="navbar-header pull-right visible-xs hidden-sm hidden-md hidden-lg">

                            </div>


                            <div class="navbar-header pull-right hidden-xs">
                            <ul class="nav navbar-nav pull-left">
                                <xsl:call-template name="languageSelection"/>
                            </ul>
                            <ul class="nav navbar-nav pull-left">
                                <xsl:choose>
                                    <xsl:when test="/dri:document/dri:meta/dri:userMeta/@authenticated = 'yes'">
                                        <li class="dropdown">
                                            <a id="user-dropdown-toggle" href="#" role="button" class="dropdown-toggle"
                                               data-toggle="dropdown">
                                                <span class="hidden-xs">
                                                    <xsl:value-of select="/dri:document/dri:meta/dri:userMeta/
                            dri:metadata[@element='identifier' and @qualifier='firstName']"/>
                                                    <xsl:text> </xsl:text>
                                                    <xsl:value-of select="/dri:document/dri:meta/dri:userMeta/
                            dri:metadata[@element='identifier' and @qualifier='lastName']"/>
                                                    &#160;
                                                    <b class="caret"/>
                                                </span>
                                            </a>
                                            <ul class="dropdown-menu pull-right" role="menu"
                                                aria-labelledby="user-dropdown-toggle" data-no-collapse="true">
                                                <li>
                                                    <a href="{/dri:document/dri:meta/dri:userMeta/
                            dri:metadata[@element='identifier' and @qualifier='url']}">
                                                        <i18n:text>xmlui.EPerson.Navigation.profile</i18n:text>
                                                    </a>
                                                </li>
                                                <li>
                                                    <a href="{/dri:document/dri:meta/dri:userMeta/
                            dri:metadata[@element='identifier' and @qualifier='logoutURL']}">
                                                        <i18n:text>xmlui.dri2xhtml.structural.logout</i18n:text>
                                                    </a>
                                                </li>
                                            </ul>
                                        </li>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <li>
                                            <a href="{/dri:document/dri:meta/dri:userMeta/
                            dri:metadata[@element='identifier' and @qualifier='loginURL']}">
                                                <span class="hidden-xs">
                                                    <i18n:text>xmlui.dri2xhtml.structural.login</i18n:text>
                                                </span>
                                            </a>
                                        </li>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </ul>

                            <button data-toggle="offcanvas" class="navbar-toggle visible-sm" type="button">
                                <span class="sr-only">Toggle navigation</span>
                                <span class="icon-bar"></span>
                                <span class="icon-bar"></span>
                                <span class="icon-bar"></span>
                            </button>
                        </div>

                            <!--END DSPACE INSIDE NAV-->




                            <ul class="nav-viu main-nav-viu ready">
                                <!-- Home -->
                                <!--            <li class="dropdown">-->
                                <!--              <a href="javascript:void(0);" class="dropdown-toggle" data-toggle="dropdown">-->
                                <!--                Home-->
                                <!--              </a>-->
                                <!--            </li>-->
                                <li class="first leaf list-group-item menu-link-home"><a href="https://library.viu.ca" title="">Home</a></li>
                                <li class="expanded list-group-item menu-link-find"><span title="" class="nolink">Find</span><button data-target="#collapse-find" class="list-toggle menu-block-handle" data-parent="#sidebar-nav" data-toggle="collapse"><span class="action">Extend</span><span class="label">Find</span></button><div class="subnav collapse" id="collapse-find"><ul><li class="first leaf list-group-item menu-link-books"><a href="https://library.viu.ca/search-books">Books</a></li>
                                    <li class="leaf list-group-item menu-link-articles"><a href="https://library.viu.ca/search-articles">Articles</a></li>
                                    <li class="leaf list-group-item menu-link-databases"><a href="http://libguides.viu.ca/az.php" title="">Databases</a></li>
                                    <li class="leaf list-group-item menu-link-journals"><a href="http://dd6db2vc8s.search.serialssolutions.com/" title="">Journals</a></li>
                                    <li class="leaf list-group-item menu-link-course-reserves"><a href="https://marlin.viu.ca/malabin/door.pl/0/0/0/36/624/X" title="">Course Reserves</a></li>
                                    <li class="leaf list-group-item menu-link-cds"><a href="https://marlin.viu.ca/malabin/door.pl/0/0/0/60/794/X" title="">CDs</a></li>
                                    <li class="leaf list-group-item menu-link-video"><a href="https://library.viu.ca/search-video">Video</a></li>
                                    <li class="leaf list-group-item menu-link-newspapers"><a href="http://libguides.viu.ca/Newspapers" title="">Newspapers</a></li>
                                    <li class="leaf list-group-item menu-link-catalogue"><a href="https://marlin.viu.ca/malabin/door.pl/0/0/0/60/792/X" title="">Catalogue</a></li>
                                    <li class="leaf list-group-item menu-link-viuspace"><a href="http://viuspace.viu.ca/" title="">VIUspace</a></li>
                                    <li class="leaf list-group-item menu-link-special-collections"><a href="http://libguides.viu.ca/c.php?g=246946&amp;p=1645330" title="">Special Collections</a></li>
                                    <li class="last leaf list-group-item menu-link-google-scholar-viu-access"><a href="http://ezproxy.viu.ca/login?url=https://scholar.google.ca" title="">Google Scholar (VIU access)</a></li>
                                </ul></div></li>
                                <li class="expanded list-group-item menu-link-help"><span title="" class="nolink">Help</span><button data-target="#collapse-help" class="list-toggle menu-block-handle" data-parent="#sidebar-nav" data-toggle="collapse"><span class="action">Extend</span><span class="label">Help</span></button><div class="subnav collapse" id="collapse-help"><ul><li class="first leaf list-group-item menu-link-new-students"><a href="http://libguides.viu.ca/getstarted" title="">New Students</a></li>
                                    <li class="leaf list-group-item menu-link-forgot-your-pin"><a href="https://marlin.viu.ca/malabin/door.pl/0/0/0/1/769/X" title="">Forgot your PIN?</a></li>
                                    <li class="leaf list-group-item menu-link-guides--tutorials"><a href="http://libguides.viu.ca/" title="">Guides &amp; Tutorials</a></li>
                                    <li class="leaf list-group-item menu-link-research-assistance"><a href="https://library.viu.ca/research-assistance">Research Assistance</a></li>
                                    <li class="leaf list-group-item menu-link-contact-us"><a href="https://library.viu.ca/viu-library-contacts" title="">Contact Us</a></li>
                                    <li class="last leaf list-group-item menu-link-workshops"><a href="http://libguides.viu.ca/workshops" title="">Workshops</a></li>
                                </ul></div></li>
                                <li class="expanded list-group-item menu-link-request"><span title="" class="nolink">Request</span><button data-target="#collapse-request" class="list-toggle menu-block-handle" data-parent="#sidebar-nav" data-toggle="collapse"><span class="action">Extend</span><span class="label">Request</span></button><div class="subnav collapse" id="collapse-request"><ul><li class="first leaf list-group-item menu-link-library-accounts--pins"><a href="https://library.viu.ca/library-accounts-pins-cards" title="">Library accounts &amp; PINs</a></li>
                                    <li class="leaf list-group-item menu-link-interlibrary-loans"><a href="https://library.viu.ca/interlibrary-loan-services" title="">Interlibrary loans</a></li>
                                    <li class="leaf list-group-item menu-link-dvd-vhs"><a href="https://library.viu.ca/video-services">DVD/VHS</a></li>
                                    <li class="expanded list-group-item menu-link-equipment"><a href="https://library.viu.ca/equipment-loan-nanaimo-campus">Equipment</a><button data-target="#collapse-equipment" class="list-toggle menu-block-handle" data-parent="#sidebar-nav" data-toggle="collapse"><span class="action">Extend</span><span class="label">Equipment</span></button><div class="subnav collapse" id="collapse-equipment"><ul><li class="first leaf list-group-item menu-link-equipment-loan-request-form-for-employees"><a href="https://library.viu.ca/equipment-loan-request-form-employees">Equipment Loan Request Form for Employees</a></li>
                                        <li class="leaf list-group-item menu-link-equipment-loan-request-form-for-students"><a href="https://library.viu.ca/equipment-loan-request-form-students">Equipment Loan Request Form for Students</a></li>
                                        <li class="last leaf list-group-item menu-link-equipment-for-loan"><a href="https://library.viu.ca/equipment-loan">Equipment for loan</a></li>
                                    </ul></div></li>
                                    <li class="leaf list-group-item menu-link-library-instruction"><a href="https://library.viu.ca/library-instruction-request-form">Library Instruction</a></li>
                                    <li class="last leaf list-group-item menu-link-course-reserves"><a href="https://library.viu.ca/course-reserves">Course Reserves</a></li>
                                </ul></div></li>
                                <li class="expanded list-group-item menu-link-book-a"><span title="" class="nolink">Book a...</span><button data-target="#collapse-book-a" class="list-toggle menu-block-handle" data-parent="#sidebar-nav" data-toggle="collapse"><span class="action">Extend</span><span class="label">Book a...</span></button><div class="subnav collapse" id="collapse-book-a"><ul><li class="first leaf list-group-item menu-link-video-audio-editing-room-nanaimo"><a href="http://ezproxy.viu.ca/login?url=http://viu-ca.libcal.com/booking/editing_suites" title="">Video/audio editing room (Nanaimo)</a></li>
                                    <li class="leaf list-group-item menu-link-group-study-room-nanaimo"><a href="http://ezproxy.viu.ca/login?url=http://viu-ca.libcal.com/booking/groupstudy" title="">Group study room (Nanaimo)</a></li>
                                    <li class="leaf list-group-item menu-link-media-workstation"><a href="http://ezproxy.viu.ca/login?url=http://viu-ca.libcal.com/booking/media_stations" title="">Media workstation</a></li>
                                    <li class="leaf list-group-item menu-link-accessibility-workstation"><a href="http://ezproxy.viu.ca/login?url=http://viu-ca.libcal.com/booking/accessibility" title="">Accessibility workstation</a></li>
                                    <li class="leaf list-group-item menu-link-video-editing-room-cowichan"><a href="http://ezproxy.viu.ca/login?url=http://viu-ca.libcal.com/booking/Cowichan_Video" title="">Video editing room (Cowichan)</a></li>
                                    <li class="last leaf list-group-item menu-link-group-study-room-cowichan"><a href="http://ezproxy.viu.ca/login?url=http://viu-ca.libcal.com/booking/Cowichan_GroupStudy" title="">Group study room (Cowichan)</a></li>
                                </ul></div></li>
                                <li class="expanded list-group-item menu-link-tools"><span title="" class="nolink">Tools</span><button data-target="#collapse-tools" class="list-toggle menu-block-handle" data-parent="#sidebar-nav" data-toggle="collapse"><span class="action">Extend</span><span class="label">Tools</span></button><div class="subnav collapse" id="collapse-tools"><ul><li class="first leaf list-group-item menu-link-renew-loans"><a href="https://marlin.viu.ca/uhtbin/cgisirsi/0/0/0/1/762/X" title="">Renew Loans</a></li>
                                    <li class="leaf list-group-item menu-link-your-library-record"><a href="https://marlin.viu.ca/uhtbin/cgisirsi/0/0/0/1/762/X" title="">Your Library Record</a></li>
                                    <li class="leaf list-group-item menu-link-refworks"><a href="http://libguides.viu.ca/refworks" title="">RefWorks</a></li>
                                    <li class="leaf list-group-item menu-link-citation-guides"><a href="http://libguides.viu.ca/citing" title="">Citation Guides</a></li>
                                    <li class="leaf list-group-item menu-link-copyright"><a href="http://libguides.viu.ca/licenses" title="">Copyright</a></li>
                                    <li class="leaf list-group-item menu-link-mobile"><a href="http://libguides.viu.ca/m" title="">Mobile</a></li>
                                    <li class="last leaf list-group-item menu-link-stable-linking"><a href="http://libguides.viu.ca/stablelinks" title="">Stable Linking</a></li>
                                </ul></div></li>
                                <li class="last expanded list-group-item menu-link-about"><span title="" class="nolink">About</span><button data-target="#collapse-about" class="list-toggle menu-block-handle" data-parent="#sidebar-nav" data-toggle="collapse"><span class="action">Extend</span><span class="label">About</span></button><div class="subnav collapse" id="collapse-about"><ul><li class="first leaf list-group-item menu-link-hours"><a href="https://library.viu.ca/library-hours">Hours</a></li>
                                    <li class="leaf list-group-item menu-link-loans"><a href="https://library.viu.ca/loan-information">Loans</a></li>
                                    <li class="leaf list-group-item menu-link-photo-id"><a href="https://library.viu.ca/viu-student-id-card-smartcard">Photo ID</a></li>
                                    <li class="leaf list-group-item menu-link-library-staff"><a href="https://library.viu.ca/viu-library-contacts">Library Staff</a></li>
                                    <li class="last leaf list-group-item menu-link-send-feedback"><a href="https://library.viu.ca/send-feedback">Send feedback</a></li>
                                </ul></div></li>
                            </ul>
                        </div>
                    </div><!--/navbar-collapse-->


                    <div class="clearfix"></div>

                </div>
                <!-- End Navbar -->
            </div>
            </div>

        </header>

    </xsl:template>


    <!-- The header (distinct from the HTML head element) contains the title, subtitle, login box and various
        placeholders for header images -->
    <xsl:template name="buildTrail">
        <div class="trail-wrapper">
            <div class="container">
                <div class="row">
                    <!--TODO-->
                    <!--<div class="col-xs-9 col-sm-10 col-md-12">-->
                    <div class="col-xs-12">
                        <xsl:choose>
                            <xsl:when test="count(/dri:document/dri:meta/dri:pageMeta/dri:trail) > 1">
                                <div class="breadcrumb dropdown visible-xs">
                                    <a id="trail-dropdown-toggle" href="#" role="button" class="dropdown-toggle"
                                       data-toggle="dropdown">
                                        <xsl:variable name="last-node"
                                                      select="/dri:document/dri:meta/dri:pageMeta/dri:trail[last()]"/>
                                        <xsl:choose>
                                            <xsl:when test="$last-node/i18n:*">
                                                <xsl:apply-templates select="$last-node/*"/>
                                            </xsl:when>
                                            <xsl:otherwise>
                                                <xsl:apply-templates select="$last-node/text()"/>
                                            </xsl:otherwise>
                                        </xsl:choose>
                                        <xsl:text>&#160;</xsl:text>
                                        <b class="caret"/>
                                    </a>
                                    <ul class="dropdown-menu" role="menu" aria-labelledby="trail-dropdown-toggle">
                                        <xsl:apply-templates select="/dri:document/dri:meta/dri:pageMeta/dri:trail"
                                                             mode="dropdown"/>
                                    </ul>
                                </div>
                                <ul class="breadcrumb hidden-xs">
                                    <xsl:apply-templates select="/dri:document/dri:meta/dri:pageMeta/dri:trail"/>
                                </ul>
                            </xsl:when>
                            <xsl:otherwise>
                                <ul class="breadcrumb">
                                    <xsl:apply-templates select="/dri:document/dri:meta/dri:pageMeta/dri:trail"/>
                                </ul>
                            </xsl:otherwise>
                        </xsl:choose>
                    </div>
                </div>
            </div>
        </div>


    </xsl:template>

    <xsl:template match="dri:trail">
        <!--put an arrow between the parts of the trail-->
        <li>
            <xsl:if test="position()=1">
                <i class="glyphicon glyphicon-home" aria-hidden="true"/>&#160;
            </xsl:if>
            <!-- Determine whether we are dealing with a link or plain text trail link -->
            <xsl:choose>
                <xsl:when test="./@target">
                    <a>
                        <xsl:attribute name="href">
                            <xsl:value-of select="./@target"/>
                        </xsl:attribute>
                        <xsl:apply-templates />
                    </a>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:attribute name="class">active</xsl:attribute>
                    <xsl:apply-templates />
                </xsl:otherwise>
            </xsl:choose>
        </li>
    </xsl:template>

    <xsl:template match="dri:trail" mode="dropdown">
        <!--put an arrow between the parts of the trail-->
        <li role="presentation">
            <!-- Determine whether we are dealing with a link or plain text trail link -->
            <xsl:choose>
                <xsl:when test="./@target">
                    <a role="menuitem">
                        <xsl:attribute name="href">
                            <xsl:value-of select="./@target"/>
                        </xsl:attribute>
                        <xsl:if test="position()=1">
                            <i class="glyphicon glyphicon-home" aria-hidden="true"/>&#160;
                        </xsl:if>
                        <xsl:apply-templates />
                    </a>
                </xsl:when>
                <xsl:when test="position() > 1 and position() = last()">
                    <xsl:attribute name="class">disabled</xsl:attribute>
                    <a role="menuitem" href="#">
                        <xsl:apply-templates />
                    </a>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:attribute name="class">active</xsl:attribute>
                    <xsl:if test="position()=1">
                        <i class="glyphicon glyphicon-home" aria-hidden="true"/>&#160;
                    </xsl:if>
                    <xsl:apply-templates />
                </xsl:otherwise>
            </xsl:choose>
        </li>
    </xsl:template>

    <xsl:template name="cc-license">
        <xsl:param name="metadataURL"/>
        <xsl:variable name="externalMetadataURL">
            <xsl:text>cocoon:/</xsl:text>
            <xsl:value-of select="$metadataURL"/>
            <xsl:text>?sections=dmdSec,fileSec&amp;fileGrpTypes=THUMBNAIL</xsl:text>
        </xsl:variable>

        <xsl:variable name="ccLicenseName"
                      select="document($externalMetadataURL)//dim:field[@element='rights']"
                />
        <xsl:variable name="ccLicenseUri"
                      select="document($externalMetadataURL)//dim:field[@element='rights'][@qualifier='uri']"
                />
        <xsl:variable name="handleUri">
            <xsl:for-each select="document($externalMetadataURL)//dim:field[@element='identifier' and @qualifier='uri']">
                <a>
                    <xsl:attribute name="href">
                        <xsl:copy-of select="./node()"/>
                    </xsl:attribute>
                    <xsl:copy-of select="./node()"/>
                </a>
                <xsl:if test="count(following-sibling::dim:field[@element='identifier' and @qualifier='uri']) != 0">
                    <xsl:text>, </xsl:text>
                </xsl:if>
            </xsl:for-each>
        </xsl:variable>

        <xsl:if test="$ccLicenseName and $ccLicenseUri and contains($ccLicenseUri, 'creativecommons')">
            <div about="{$handleUri}" class="row">
                <!--<xsl:attribute name="style">-->
                    <!--<xsl:text>margin:0em 2em 0em 2em; padding-bottom:0em;</xsl:text>-->
                <!--</xsl:attribute>-->
            <div class="col-sm-3 col-xs-12">
                <a rel="license"
                   href="{$ccLicenseUri}"
                   alt="{$ccLicenseName}"
                   title="{$ccLicenseName}"
                        >
                    <img class="img-responsive">
                        <xsl:attribute name="src">
                            <xsl:value-of select="concat($theme-path,'../_precompiled-mirage2/images/cc-ship.gif')"/>
                        </xsl:attribute>
                        <xsl:attribute name="alt">
                            <xsl:value-of select="$ccLicenseName"/>
                        </xsl:attribute>
                    </img>
                </a>
            </div> <div class="col-sm-8">
                <span>
                    <i18n:text>xmlui.dri2xhtml.METS-1.0.cc-license-text</i18n:text>
                    <xsl:value-of select="$ccLicenseName"/>
                </span>
            </div>
            </div>
        </xsl:if>
    </xsl:template>

    <!-- Like the header, the footer contains various miscellaneous text, links, and image placeholders -->
    <xsl:template name="buildFooter">
        <footer>
            <div class="container-fluid">
                <div class="row">
                    <a href="http://www.dspace.org/" target="_blank">DSpace software</a> Copyright&#160;&#169;&#160;2015&#160; <a href="http://www.duraspace.org/" target="_blank">Duraspace</a>
                </div>
                <div class="row">
                    <a>
                        <xsl:attribute name="href">
                            <xsl:value-of
                                    select="/dri:document/dri:meta/dri:pageMeta/dri:metadata[@element='contextPath'][not(@qualifier)]"/>
                            <xsl:text>/contact</xsl:text>
                        </xsl:attribute>
                        <i18n:text>xmlui.dri2xhtml.structural.contact-link</i18n:text>
                    </a>
                    <xsl:text> | </xsl:text>
                    <a>
                        <xsl:attribute name="href">
                            <xsl:value-of
                                    select="/dri:document/dri:meta/dri:pageMeta/dri:metadata[@element='contextPath'][not(@qualifier)]"/>
                            <xsl:text>/feedback</xsl:text>
                        </xsl:attribute>
                        <i18n:text>xmlui.dri2xhtml.structural.feedback-link</i18n:text>
                    </a>
                </div>
            </div>
            <!--Invisible link to HTML sitemap (for search engines) -->
            <a class="hidden">
                <xsl:attribute name="href">
                    <xsl:value-of
                            select="/dri:document/dri:meta/dri:pageMeta/dri:metadata[@element='contextPath'][not(@qualifier)]"/>
                    <xsl:text>/htmlmap</xsl:text>
                </xsl:attribute>
                <xsl:text>&#160;</xsl:text>
            </a>
            <p>&#160;</p>
        </footer>
    </xsl:template>


    <!--
            The meta, body, options elements; the three top-level elements in the schema
    -->




    <!--
        The template to handle the dri:body element. It simply creates the ds-body div and applies
        templates of the body's child elements (which consists entirely of dri:div tags).
    -->
    <xsl:template match="dri:body">
        <div>
            <xsl:if test="/dri:document/dri:meta/dri:pageMeta/dri:metadata[@element='alert'][@qualifier='message']">
                <div class="alert">
                    <button type="button" class="close" data-dismiss="alert">&#215;</button>
                    <xsl:copy-of select="/dri:document/dri:meta/dri:pageMeta/dri:metadata[@element='alert'][@qualifier='message']/node()"/>
                </div>
            </xsl:if>

            <!-- Check for the custom pages -->
            <xsl:choose>
                <xsl:when test="starts-with($request-uri, 'page/about')">
                    <div class="hero-unit">
                        <h1>About This Repository</h1>
                        <p>To add your own content to this page, edit webapps/xmlui/themes/Mirage/lib/xsl/core/page-structure.xsl and
                            add your own content to the title, trail, and body. If you wish to add additional pages, you
                            will need to create an additional xsl:when block and match the request-uri to whatever page
                            you are adding. Currently, static pages created through altering XSL are only available
                            under the URI prefix of page/.</p>
                    </div>
                </xsl:when>
                <!-- Otherwise use default handling of body -->
                <xsl:otherwise>
                    <xsl:apply-templates />
                </xsl:otherwise>
            </xsl:choose>

        </div>
    </xsl:template>


    <!-- Currently the dri:meta element is not parsed directly. Instead, parts of it are referenced from inside
        other elements (like reference). The blank template below ends the execution of the meta branch -->
    <xsl:template match="dri:meta">
    </xsl:template>

    <!-- Meta's children: userMeta, pageMeta, objectMeta and repositoryMeta may or may not have templates of
        their own. This depends on the meta template implementation, which currently does not go this deep.
    <xsl:template match="dri:userMeta" />
    <xsl:template match="dri:pageMeta" />
    <xsl:template match="dri:objectMeta" />
    <xsl:template match="dri:repositoryMeta" />
    -->

    <xsl:template name="addJavascript">

        <!--TODO concat & minify!-->

        <script>
            <xsl:text>if(!window.DSpace){window.DSpace={};}window.DSpace.context_path='</xsl:text><xsl:value-of select="$context-path"/><xsl:text>';window.DSpace.theme_path='</xsl:text><xsl:value-of select="$theme-path"/><xsl:text>';</xsl:text>
        </script>

        <script src="{$theme-path}../_precompiled-mirage2/scripts/theme.js">&#160;</script>

	<script src="//code.jquery.com/jquery-migrate-1.2.1.js"></script>
        <script src="{$theme-path}../_precompiled-mirage2/scripts/holder.js">&#160;</script>

        <script src="{$theme-path}../_precompiled-mirage2/vendor/BookReader/jquery-ui-1.8.5.custom.min.js"></script>
        <script src="{$theme-path}../_precompiled-mirage2/vendor/BookReader/dragscrollable.js"></script>
        <script src="{$theme-path}../_precompiled-mirage2/vendor/BookReader/jquery.colorbox-min.js"></script>
        <script src="{$theme-path}../_precompiled-mirage2/vendor/BookReader/jquery.ui.ipad.js"></script>
        <script src="{$theme-path}../_precompiled-mirage2/vendor/BookReader/jquery.bt.min.js"></script>
        <script src="{$theme-path}../_precompiled-mirage2/vendor/BookReader/BookReader.js"></script>
        <script src="{$theme-path}../_precompiled-mirage2/vendor/BookReader/BookReaderJSSimple.js"></script>

        <!-- Snazy -->
        <script src="{$theme-path}../_precompiled-mirage2/scripts/jquery.lazyload.min.js"></script>
        <script src="{$theme-path}../_precompiled-mirage2/scripts/snazy.js"></script>

        <!-- add "shared" javascript from static, path is relative to webapp root -->
        <xsl:for-each select="/dri:document/dri:meta/dri:pageMeta/dri:metadata[@element='javascript'][@qualifier='url']">
            <script type="text/javascript">
                <xsl:attribute name="src">
                    <xsl:value-of select="."/>
                </xsl:attribute>&#160;</script>
        </xsl:for-each>

        <!-- Add javascipt specified in DRI -->
        <xsl:for-each select="/dri:document/dri:meta/dri:pageMeta/dri:metadata[@element='javascript'][not(@qualifier)]">
            <script>
                <xsl:attribute name="src">
                    <xsl:value-of select="$theme-path"/>
                    <xsl:value-of select="."/>
                </xsl:attribute>&#160;</script>
        </xsl:for-each>

        <!-- add "shared" javascript from static, path is relative to webapp root-->
        <xsl:for-each select="/dri:document/dri:meta/dri:pageMeta/dri:metadata[@element='javascript'][@qualifier='static']">
            <!--This is a dirty way of keeping the scriptaculous stuff from choice-support
            out of our theme without modifying the administrative and submission sitemaps.
            This is obviously not ideal, but adding those scripts in those sitemaps is far
            from ideal as well-->
            <xsl:choose>
                <xsl:when test="text() = 'static/js/choice-support.js'">
                    <script>
                        <xsl:attribute name="src">
                            <xsl:value-of select="$theme-path"/>
                            <xsl:text>../_precompiled-mirage2/js/choice-support.js</xsl:text>
                        </xsl:attribute>&#160;</script>
                </xsl:when>
                <xsl:when test="not(starts-with(text(), 'static/js/scriptaculous'))">
                    <script>
                        <xsl:attribute name="src">
                            <xsl:value-of
                                    select="/dri:document/dri:meta/dri:pageMeta/dri:metadata[@element='contextPath'][not(@qualifier)]"/>
                            <xsl:text>/</xsl:text>
                            <xsl:value-of select="."/>
                        </xsl:attribute>&#160;</script>
                </xsl:when>
            </xsl:choose>
        </xsl:for-each>

        <!-- add setup JS code if this is a choices lookup page -->
        <xsl:if test="dri:body/dri:div[@n='lookup']">
            <xsl:call-template name="choiceLookupPopUpSetup"/>
        </xsl:if>

        <!-- Add Custom VIU JS -->
        <script type="text/javascript" src="https://library.viu.ca/profiles/viu/themes/viu_radix/assets/vendor/bigtext.js?ocj1bz"></script>

        <script type="text/javascript">
            $(document).ready(function() {
                $('.header-banner-title').bigtext({
                    maxfontsize: 60,
                    minfontsize: 20
                });
            });
        </script>

    </xsl:template>

    <xsl:template name="languageSelection">
        <xsl:if test="count(/dri:document/dri:meta/dri:pageMeta/dri:metadata[@element='page'][@qualifier='supportedLocale']) &gt; 1">
            <li id="ds-language-selection" class="dropdown">
                <xsl:variable name="active-locale" select="/dri:document/dri:meta/dri:pageMeta/dri:metadata[@element='page'][@qualifier='currentLocale']"/>
                <a id="language-dropdown-toggle" href="#" role="button" class="dropdown-toggle" data-toggle="dropdown">
                    <span class="hidden-xs">
                        <xsl:value-of
                                select="/dri:document/dri:meta/dri:pageMeta/dri:metadata[@element='supportedLocale'][@qualifier=$active-locale]"/>
                        <xsl:text>&#160;</xsl:text>
                        <b class="caret"/>
                    </span>
                </a>
                <ul class="dropdown-menu pull-right" role="menu" aria-labelledby="language-dropdown-toggle" data-no-collapse="true">
                    <xsl:for-each
                            select="/dri:document/dri:meta/dri:pageMeta/dri:metadata[@element='page'][@qualifier='supportedLocale']">
                        <xsl:variable name="locale" select="."/>
                        <li role="presentation">
                            <xsl:if test="$locale = $active-locale">
                                <xsl:attribute name="class">
                                    <xsl:text>disabled</xsl:text>
                                </xsl:attribute>
                            </xsl:if>
                            <a>
                                <xsl:attribute name="href">
                                    <xsl:value-of select="$current-uri"/>
                                    <xsl:text>?locale-attribute=</xsl:text>
                                    <xsl:value-of select="$locale"/>
                                </xsl:attribute>
                                <xsl:value-of
                                        select="/dri:document/dri:meta/dri:pageMeta/dri:metadata[@element='supportedLocale'][@qualifier=$locale]"/>
                            </a>
                        </li>
                    </xsl:for-each>
                </ul>
            </li>
        </xsl:if>
    </xsl:template>

</xsl:stylesheet>
