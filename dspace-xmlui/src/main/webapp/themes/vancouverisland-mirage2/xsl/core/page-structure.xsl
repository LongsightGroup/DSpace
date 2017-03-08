
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

            <!-- VIU CSS -->
            <link type="text/css" rel="stylesheet" href="{$theme-path}/styles/css_VQMdKuzMH0e2OM_kBeHZiwFTlpBbiD0sOJ_3q9TgeOg.css" media="all"/>
            <link type="text/css" rel="stylesheet" href="{$theme-path}/styles/css_JFORYGrrCKN63qbcAkrQNwGWZ64d1VMkxdm47C9SJlY.css" media="all"/>
            <link type="text/css" rel="stylesheet" href="{$theme-path}/styles/css_Z6wtNAupfmSGOq3AcNtWGPbqBAwuNuE1Io48n0_2Ojc.css" media="all"/>

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



            <!--[if lte IE 8]>
            <link type="text/css" rel="stylesheet" href="https://www.viu.ca/profiles/viu/modules/panopoly/panopoly_core/css/panopoly-fonts-ie-open-sans.css?oknxd8" media="all" />
            <link type="text/css" rel="stylesheet" href="https://www.viu.ca/profiles/viu/modules/panopoly/panopoly_core/css/panopoly-fonts-ie-open-sans-bold.css?oknxd8" media="all" />
            <link type="text/css" rel="stylesheet" href="https://www.viu.ca/profiles/viu/modules/panopoly/panopoly_core/css/panopoly-fonts-ie-open-sans-italic.css?oknxd8" media="all" />
            <link type="text/css" rel="stylesheet" href="https://www.viu.ca/profiles/viu/modules/panopoly/panopoly_core/css/panopoly-fonts-ie-open-sans-bold-italic.css?oknxd8" media="all" />
            <link type="text/css" rel="stylesheet" href="https://www.viu.ca/sites/default/files/css/css_GSpEzCB3Ld0DrZ2ZZEeoeS90LFMHgy5EsOdl-IfLFnw.css" media="all" />
            <![endif]-->
            <link type="text/css" rel="stylesheet" href="{$theme-path}/styles/css_txQ27OAjDuZQlb1RsKW2zYfnT9dZqaWs-h3WS5UJbzU.css" media="all"/>


            <!-- bring in RRU fonts -->
            <link type="text/css" rel="stylesheet" href="{$theme-path}/../rru-mirage2/styles/css_xE-rWrJf-fncB6ztZfd2huxqgxu4WO-qwma6Xer30m4.css" media="all" />
            <link type="text/css" rel="stylesheet" href="{$theme-path}/../rru-mirage2/styles/css_WqV69UA1faoRQbJdtSwGwUJ8MK7FrjmmaCOY4qeUK-c.css" media="all" />
            <link type="text/css" rel="stylesheet" href="{$theme-path}/../rru-mirage2/styles/css_yqslcOxgUgr1HVfBvP6vQLPffleaaF28MnBJ5TW3u6U.css" media="all" />
            <link type="text/css" rel="stylesheet" href="{$theme-path}/../rru-mirage2/styles/css_5KE8MugL6r_gSgi3lAM_ZvwH-8XlPN3TKXvgJadnOwY.css" media="all" />
            <link type="text/css" rel="stylesheet" href="//fonts.googleapis.com/css?family=Roboto:400,100,100italic,300,300italic,400italic,500,500italic,700,700italic,900,900italic" media="all" />
            <link type="text/css" rel="stylesheet" href="{$theme-path}/../rru-mirage2/styles/css_IAYnIwzRnT3HFhdzm_N-GLTa8eZyMkdiD510VQwo5GY.css" media="all" />
            <link type="text/css" rel="stylesheet" href="{$theme-path}/../rru-mirage2/styles/css_bBNcNbMjlQ8H16-QuSljlNekXp_Pgr0BLZn6Uo0k7a0.css" media="print" />
            <link type="text/css" rel="stylesheet" href="{$theme-path}/../rru-mirage2/styles/css_S085sfdJmQr5rTeatD26T7XworCMX9s3lCqt1lTzcKM.css" media="all" />



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

        </head>
    </xsl:template>


    <!-- The header (distinct from the HTML head element) contains the title, subtitle, login box and various
        placeholders for header images -->
    <xsl:template name="buildHeader">
        <div id="container" role="main" class="l-homepage">
            <div id="page" class="l-page">
                <div class="l-region l-region--g-header">
                    <div class="panel-pane pane-pane-header">
                        <div class="pane-content">
                            <header>

                                <div class="utility-bar">
                                    <div class="row not-collapse">
                                        <div class="large-12 columns">

                                            <a href="https://www.viu.ca/" title="VIU Home" rel="home" class="logo" itemprop="image name">VIU</a>

                                            <nav class="utility-nav top-bar" role="navigation">

                                                <ul class="title-area">
                                                    <li class="name">
                                                    </li>
                                                    <li class="toggle-topbar menu-icon"><a href="https://www.viu.ca/#"><span>Links</span></a></li>
                                                </ul>



                                                <section class="top-bar-section">
                                                    <ul role="navigation" class="right">
                                                        <li><a href="https://www.viu.ca/emergency" style="color: #9cdbf1">Emergency Info</a></li>
                                                        <li><a href="https://www.viu.ca/library">Library</a></li>
                                                        <li class="has-dropdown not-click"><a href="https://www.viu.ca/#">Campuses</a>
                                                            <ul class="dropdown"><li class="title back js-generated"><h5><a href="#">« Back</a></h5></li><li><a class="parent-link js-generated" href="https://www.viu.ca/#">Campuses</a></li><li class="title back js-generated"><h5><a href="https://www.viu.ca/#">« Back</a></h5></li>
                                                                <li><a href="https://www.viu.ca/">Nanaimo</a></li>
                                                                <li><a href="https://www.viu.ca/parksville">Parksville-Qualicum</a></li>
                                                                <li><a href="http://www.cc.viu.ca/">Cowichan</a></li>
                                                                <li><a href="http://www.pr.viu.ca/">Powell River</a></li>
                                                            </ul>
                                                        </li>
                                                        <li><a href="https://www.viu.ca/calendar">Programs and Courses</a></li>
                                                        <li class="has-dropdown not-click"><a href="https://www.viu.ca/#">Directories</a>
                                                            <ul class="dropdown"><li class="title back js-generated"><h5><a href="#">« Back</a></h5></li><li><a class="parent-link js-generated" href="https://www.viu.ca/#">Directories</a></li><li class="title back js-generated"><h5><a href="https://www.viu.ca/#">« Back</a></h5></li>
                                                                <li><a href="https://www.viu.ca/directory">Employee Directory</a></li>
                                                                <li><a href="https://www.viu.ca/calendar/instructional.asp">Instructional Departments</a></li>
                                                                <li><a href="https://www.viu.ca/services/">Service Departments</a></li>
                                                            </ul>
                                                        </li>
                                                        <li><a href="https://www.viu.ca/contact">Contact Us</a></li>
                                                    </ul>
                                                </section></nav>

                                        </div>
                                    </div>
                                </div>

                                <nav class="top-nav" role="navigation">
                                    <header>
                                        <div class="navbar navbar-default navbar-static-top" role="navigation">
                                            <div class="">
                                                <div class="navbar-header">

                                                    <button type="button" class="navbar-toggle" data-toggle="offcanvas">
                                                        <span class="sr-only">Toggle navigation</span>
                                                        <span class="icon-bar"></span>
                                                        <span class="icon-bar"></span>
                                                        <span class="icon-bar"></span>
                                                    </button>


                                                    <div class="navbar-header pull-right visible-xs hidden-sm hidden-md hidden-lg">
                                                        <ul class="nav nav-pills pull-left ">

                                                            <xsl:if test="count(/dri:document/dri:meta/dri:pageMeta/dri:metadata[@element='page'][@qualifier='supportedLocale']) &gt; 1">
                                                                <li id="ds-language-selection-xs" class="dropdown">
                                                                    <xsl:variable name="active-locale" select="/dri:document/dri:meta/dri:pageMeta/dri:metadata[@element='page'][@qualifier='currentLocale']"/>
                                                                    <button id="language-dropdown-toggle-xs" href="#" role="button" class="dropdown-toggle navbar-toggle navbar-link" data-toggle="dropdown">
                                                                        <b class="visible-xs glyphicon glyphicon-globe" aria-hidden="true"/>
                                                                    </button>
                                                                    <ul class="dropdown-menu pull-right" role="menu" aria-labelledby="language-dropdown-toggle-xs" data-no-collapse="true">
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

                                                            <xsl:choose>
                                                                <xsl:when test="/dri:document/dri:meta/dri:userMeta/@authenticated = 'yes'">
                                                                    <li class="dropdown">
                                                                        <button class="dropdown-toggle navbar-toggle navbar-link" id="user-dropdown-toggle-xs" href="#" role="button"  data-toggle="dropdown">
                                                                            <b class="visible-xs glyphicon glyphicon-user" aria-hidden="true"/>
                                                                        </button>
                                                                        <ul class="dropdown-menu pull-right" role="menu"
                                                                            aria-labelledby="user-dropdown-toggle-xs" data-no-collapse="true">
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
                                                                        <form style="display: inline" action="{/dri:document/dri:meta/dri:userMeta/
                            dri:metadata[@element='identifier' and @qualifier='loginURL']}" method="get">
                                                                            <button class="navbar-toggle navbar-link">
                                                                                <b class="visible-xs glyphicon glyphicon-user" aria-hidden="true"/>
                                                                            </button>
                                                                        </form>
                                                                    </li>
                                                                </xsl:otherwise>
                                                            </xsl:choose>
                                                        </ul>
                                                    </div>
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
                                            </div>
                                        </div>

                                    </header>
                                </nav>

                            </header>
                        </div>
                    </div>
                </div>
            </div>
        </div>




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
        <div id="footer" class="footer">
            <div id="footer-toggle">
                <span class="arrow arrow-up"></span>
            </div>
            <footer>
                <div id="footer-menu-ext" style="display: none;">
                    <div class="l-region l-region--g-footer-ext">
                        <div class="panel-pane pane-panels-mini pane-global-footer-links-extra">
                            <div class="pane-content">

                                <div class="panel-display sixpack clearfix viu-sixpack" id="mini-panel-global_footer_links_extra">

                                    <div class="container">
                                        <div class="row">
                                            <div class="panel-panel large-2 small-4 columns">
                                                <div class="panel-panel-inner">
                                                    <div class="panel-pane pane-fieldable-panels-pane pane-fpid-162 pane-bundle-quick-links">
                                                        <div class="pane-content">
                                                            <div class="ds-1col fieldable-panels-pane view-mode-Full clearfix">


                                                                <h5>Social Media</h5><div class="item-list-links"><ul><li class="first"><a href="http://www.facebook.com/LoveWhereYouLearn">Facebook</a></li>
                                                                <li><a href="http://instagram.com/viuniversity">Instagram</a></li>
                                                                <li><a href="http://www.linkedin.com/companies/vancouver-island-university">LinkedIn</a></li>
                                                                <li><a href="http://www.viu.ca/rss/">RSS</a></li>
                                                                <li><a href="http://twitter.com/VIUniversity">Twitter</a></li>
                                                                <li class="last"><a href="http://www.youtube.com/user/viuchannel">YouTube</a></li>
                                                            </ul></div></div>

                                                        </div>
                                                    </div>
                                                </div>
                                            </div>
                                            <div class="panel-panel large-2 small-4 columns">
                                                <div class="panel-panel-inner">
                                                    <div class="panel-pane pane-fieldable-panels-pane pane-fpid-164 pane-bundle-quick-links">
                                                        <div class="pane-content">
                                                            <div class="ds-1col fieldable-panels-pane view-mode-Full clearfix">


                                                                <h5>Campus Services</h5><div class="item-list-links"><ul><li class="first"><a href="http://www.viubookstore.ca/">Bookstore</a></li>
                                                                <li><a href="http://www.viu.ca/foodservices/">Food Services</a></li>
                                                                <li><a href="http://www.viu.ca/discoveryroom/">Discovery Room Restaurant</a></li>
                                                                <li><a href="http://www.viu.ca/directory">Employee Directory</a></li>
                                                                <li><a href="http://www.viu.ca/directory?select=expertise">Find VIU Experts</a></li>
                                                                <li><a href="http://www.viu.ca/eventservices/">Event Services</a></li>
                                                                <li><a href="http://www.viu.ca/catering/">Campus Caterers</a></li>
                                                                <li><a href="http://www.viu.ca/parking/">Parking and Security</a></li>
                                                                <li class="last"><a href="http://www.viu.ca/about/services.asp">Service Departments</a></li>
                                                            </ul></div></div>

                                                        </div>
                                                    </div>
                                                </div>
                                            </div>
                                            <div class="panel-panel large-2 small-4 columns">
                                                <div class="panel-panel-inner">
                                                    <div class="panel-pane pane-fieldable-panels-pane pane-fpid-163 pane-bundle-quick-links">
                                                        <div class="pane-content">
                                                            <div class="ds-1col fieldable-panels-pane view-mode-Full clearfix">


                                                                <h5>Public Engagement</h5><div class="item-list-links"><ul><li class="first"><a href="http://www.viu.ca/giving/">Advancement and Alumni</a></li>
                                                                <li><a href="http://www.viu.ca/universityrelations/contact_comm.aspx">Communications and Public Engagement</a></li>
                                                                <li><a href="http://www.viu.ca/governmentrelations/">Government Relations</a></li>
                                                                <li class="last"><a href="http://www.viu.ca/universityrelations/">University Relations</a></li>
                                                            </ul></div></div>

                                                        </div>
                                                    </div>
                                                </div>
                                            </div>
                                            <div class="panel-panel large-2 small-4 columns">
                                                <div class="panel-panel-inner">
                                                    <div class="panel-pane pane-fieldable-panels-pane pane-fpid-132 pane-bundle-quick-links">
                                                        <div class="pane-content">
                                                            <div class="ds-1col fieldable-panels-pane view-mode-Full clearfix">


                                                                <h5>Faculties</h5><div class="item-list-links"><ul><li class="first"><a href="http://www.viu.ca/cap/">Academic &amp; Career Preparation</a></li>
                                                                <li><a href="http://www.viu.ca/artsandhumanities/">Arts &amp; Humanities</a></li>
                                                                <li><a href="http://www.viu.ca/education/">Education</a></li>
                                                                <li><a href="http://www.viu.ca/hhs/">Health &amp; Human Services</a></li>
                                                                <li><a href="http://www.viu.ca/international/">International Programs</a></li>
                                                                <li><a href="http://www.viu.ca/scienceandtechnology/">Science &amp; Technology</a></li>
                                                                <li><a href="http://www.viu.ca/socialsciences/">Social Sciences</a></li>
                                                                <li><a href="http://www.viu.ca/management/">Management</a></li>
                                                                <li class="last"><a href="http://www.viu.ca/tat/">Trades &amp; Applied Technology</a></li>
                                                            </ul></div></div>

                                                        </div>
                                                    </div>
                                                </div>
                                            </div>
                                            <div class="panel-panel large-2 small-4 columns">
                                                <div class="panel-panel-inner">
                                                    <div class="panel-pane pane-fieldable-panels-pane pane-fpid-128 pane-bundle-quick-links">
                                                        <div class="pane-content">
                                                            <div class="ds-1col fieldable-panels-pane view-mode-Full clearfix">


                                                                <h5>Organizational Structure</h5><div class="item-list-links"><ul><li class="first"><a href="http://www.viu.ca/gap/">Governance, Administration, &amp; Planning</a></li>
                                                                <li><a href="http://www.viu.ca/calendar/instructional.asp">Instructional Departments</a></li>
                                                                <li><a href="http://www.viu.ca/pvpa/">Provost, and VP Academic</a></li>
                                                                <li><a href="http://www.viu.ca/policies/">Policies &amp; Procedures</a></li>
                                                                <li><a href="http://www.viu.ca/president/">President's Office</a></li>
                                                                <li><a href="http://www.viu.ca/universityrelations/">University Relations</a></li>
                                                                <li><a href="http://www.viu.ca/vpadmin/">VP Admin and Finance</a></li>
                                                                <li class="last"><a href="http://www.viu.ca/president/docs/InstitutionalGovernanceJanuary2013.pdf">VIU Organization Chart </a></li>
                                                            </ul></div></div>

                                                        </div>
                                                    </div>
                                                </div>
                                            </div>
                                            <div class="panel-panel large-2 small-4 columns">
                                                <div class="panel-panel-inner">
                                                    <div class="panel-pane pane-fieldable-panels-pane pane-fpid-165 pane-bundle-quick-links">
                                                        <div class="pane-content">
                                                            <div class="ds-1col fieldable-panels-pane view-mode-Full clearfix">


                                                                <h5>Publications</h5><div class="item-list-links"><ul><li class="first"><a href="http://www.viu.ca/integratedplanning/RegionalStrategy.asp">Regional Strategy Plan</a></li>
                                                                <li><a href="http://www.viu.ca/impact/">VIU Impact Report</a></li>
                                                                <li><a href="http://www.viu.ca/docs/VIU-Report-to-the-Community.pdf">VIU Report to the Community </a></li>
                                                                <li class="last"><a href="http://www.viu.ca/docs/VIU-AddingValue.pdf">Adding Value to Your Community</a></li>
                                                            </ul></div></div>

                                                        </div>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                    </div>

                                </div><!-- /.sixpack -->
                            </div>
                        </div>
                    </div>
                </div>
                <div id="footer-menu">
                    <div class="l-region l-region--g-footer">

                        <nav class="footer-links">
                            <div class="panel-display sixpack clearfix viu-sixpack" id="mini-panel-global_footer_links">

                                <div class="container">
                                    <div class="row">
                                        <div class="panel-panel large-2 small-4 columns">
                                            <div class="panel-panel-inner">

                                                <h5>Contact Info</h5>

                                                <div class="field field-name-field-basic-text-text"><div class="specific-contact">
                                                    <p>Vancouver Island University<br/>
                                                        Nanaimo Campus<br/>900 Fifth Street<br/>
                                                        Nanaimo, BC<br/>Canada V9R 5S5<br/>
                                                        Toll-free 1.888.920.2221<br/>
                                                        Switchboard&#160;250.753.3245<br/>
                                                        Email&#160;<a href="mailto:info@viu.ca">info@viu.ca</a>
                                                    </p>

                                                    <a>
                                                        <xsl:attribute name="href">
                                                            <xsl:value-of
                                                                    select="/dri:document/dri:meta/dri:pageMeta/dri:metadata[@element='contextPath'][not(@qualifier)]"/>
                                                            <xsl:text>/contact</xsl:text>
                                                        </xsl:attribute>
                                                        <i18n:text>xmlui.dri2xhtml.structural.contact-link</i18n:text>
                                                    </a><br/>
                                                    <a>
                                                        <xsl:attribute name="href">
                                                            <xsl:value-of
                                                                    select="/dri:document/dri:meta/dri:pageMeta/dri:metadata[@element='contextPath'][not(@qualifier)]"/>
                                                            <xsl:text>/feedback</xsl:text>
                                                        </xsl:attribute>
                                                        <i18n:text>xmlui.dri2xhtml.structural.feedback-link</i18n:text>
                                                    </a>

                                                    <!--Invisible link to HTML sitemap (for search engines) -->
                                                    <a class="hidden">
                                                        <xsl:attribute name="href">
                                                            <xsl:value-of
                                                                    select="/dri:document/dri:meta/dri:pageMeta/dri:metadata[@element='contextPath'][not(@qualifier)]"/>
                                                            <xsl:text>/htmlmap</xsl:text>
                                                        </xsl:attribute>
                                                        <xsl:text>&#160;</xsl:text>
                                                    </a>
                                                </div>
                                                    <div class="general-contact">
                                                        Copyright ©<br/>Vancouver Island University
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                        <div class="panel-panel large-2 small-4 columns">
                                            <div class="panel-panel-inner">
                                                <div class="panel-pane pane-fieldable-panels-pane pane-fpid-159 pane-bundle-quick-links">
                                                    <div class="pane-content">
                                                        <div class="ds-1col fieldable-panels-pane view-mode-Full clearfix">


                                                            <h5>About VIU</h5><div class="item-list-links"><ul><li class="first"><a href="http://www.viu.ca/administration/">Administration</a></li>
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
                                                        </ul></div></div>

                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                        <div class="panel-panel large-2 small-4 columns">
                                            <div class="panel-panel-inner">
                                                <div class="panel-pane pane-fieldable-panels-pane pane-fpid-129 pane-bundle-quick-links">
                                                    <div class="pane-content">
                                                        <div class="ds-1col fieldable-panels-pane view-mode-Full clearfix">


                                                            <h5>ACADEMICS</h5><div class="item-list-links"><ul><li class="first"><a href="http://www.viu.ca/calendar/GeneralInformation/admissions.asp">Admissions</a></li>
                                                            <li><a href="http://www.viu.ca/calendar/GeneralInformation/registration.asp">Registration</a></li>
                                                            <li><a href="http://www.viu.ca/calendar/">Programs and Courses</a></li>
                                                            <li><a href="http://www.viu.ca/dualcredit/">Dual Credit</a></li>
                                                            <li><a href="http://www.viu.ca/financialaid/">Financial Aid and Awards</a></li>
                                                            <li><a href="http://www.viu.ca/calendar/credential/mastersdegrees.asp">Graduate Programs</a></li>
                                                            <li><a href="http://www.viu.ca/international/">International Education</a></li>
                                                            <li><a href="http://www.viu.ca/library/">Library</a></li>
                                                            <li class="last"><a href="http://www.viu.ca/ciel/">Online Education</a></li>
                                                        </ul></div></div>

                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                        <div class="panel-panel large-2 small-4 columns">
                                            <div class="panel-panel-inner">
                                                <div class="panel-pane pane-fieldable-panels-pane pane-fpid-131 pane-bundle-quick-links">
                                                    <div class="pane-content">
                                                        <div class="ds-1col fieldable-panels-pane view-mode-Full clearfix">


                                                            <h5>ATHLETICS</h5><div class="item-list-links"><ul><li class="first"><a href="http://mariners.viu.ca/">VIU Mariners</a></li>
                                                            <li><a href="http://mariners.viu.ca/teams/">Mariners Teams</a></li>
                                                            <li><a href="http://www.viu.ca/campusrec/">Campus Recreation</a></li>
                                                            <li class="last"><a href="http://mariners.viu.ca/community/summer-camps/">Summer Camps</a></li>
                                                        </ul></div></div>

                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                        <div class="panel-panel large-2 small-4 columns">
                                            <div class="panel-panel-inner">
                                                <div class="panel-pane pane-fieldable-panels-pane pane-fpid-133 pane-bundle-quick-links">
                                                    <div class="pane-content">
                                                        <div class="ds-1col fieldable-panels-pane view-mode-Full clearfix">


                                                            <h5>CAMPUS LIFE</h5><div class="item-list-links"><ul><li class="first"><a href="https://campus-store.viu.ca/">Campus Store</a></li>
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
                                                        </ul></div></div>

                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                        <div class="panel-panel large-2 small-4 columns">
                                            <div class="panel-panel-inner">
                                                <div class="panel-pane pane-fieldable-panels-pane pane-fpid-160 pane-bundle-quick-links">
                                                    <div class="pane-content">
                                                        <div class="ds-1col fieldable-panels-pane view-mode-Full clearfix">


                                                            <h5>Extension and Outreach</h5><div class="item-list-links"><ul><li class="first"><a href="http://www.viu.ca/pdt/customizedtraining.asp">Contract Training</a></li>
                                                            <li><a href="http://www.viu.ca/pdt/courses-and-programs/index.asp">Professional Development and Training Courses</a></li>
                                                            <li><a href="http://www.viu.ca/summersession/">Summer Session</a></li>
                                                            <li><a href="http://www.viu.ca/eldercollege/">ElderCollege</a></li>
                                                            <li><a href="http://www.viu.ca/grandkids/">GrandKids University</a></li>
                                                            <li class="last"><a href="http://www.viu.ca/summercamps/">Youth Summer Camps</a></li>
                                                        </ul></div></div>

                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </div>

                            </div><!-- /.sixpack -->
                        </nav>
                    </div>
                </div>
            </footer>
        </div>
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
