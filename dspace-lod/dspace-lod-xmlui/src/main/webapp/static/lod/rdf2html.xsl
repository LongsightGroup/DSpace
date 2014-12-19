<!--

    The contents of this file are subject to the license and copyright
    detailed in the LICENSE and NOTICE files at the root of the source
    tree and available online at

    http://www.dspace.org/license/

-->
<!--
    Main structure of the page, determines where
    header, footer, body, navigation are structurally rendered.
    Rendering of the header, footer, trail and alerts

    Author: minipillai at atmire.com
    Author: mark diggory at atmire.com


-->

<xsl:stylesheet	xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
                   xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
                   xmlns="http://www.w3.org/1999/xhtml">

    <xsl:output indent="yes"/>


    <xsl:template match="/rdf:RDF">
        <html>
            <head>
                <title>
                    <xsl:value-of select="*[0]/@rdf:about"/>
                </title>
                <link rel="stylesheet" type="text/css" href="/static/lod/style.css" />
                <link rel="stylesheet" type="text/css" href="/static/lod/metadata.css" />

                <link rel="alternate" type="application/rdf+xml" href="?Accept=application/rdf+xml" title="This page in RDF (XML)" />
                <link rel="alternate" type="text/turtle" href="?Accept=text/turtle" title="This page in RDF (Turtle)" />
            </head>
            <body class="browser">

                <div id="rdficon">
                    <a href="?Accept=text/n3" title="RDF data"><img src="/static/lod/rdf_flyer.24.gif" alt="[RDF data]" /></a>
                </div>

                <div id="header">
                    <h2 id="title" style="text-align:center">
                        Resource URI: <xsl:value-of select="*[1]/@rdf:about"/>
                    </h2>
                </div>

                <div class="section" style="line-height:30px">
                    <strong><a href="/">Home</a></strong>
                    <strong><a href="/data">Data</a></strong>
                    <strong><a href="/sesame/repositories/dspace">SPARQL End-Point</a></strong>
                </div>

                <div class="section">
                    <table class="queryresults">
                        <xsl:apply-templates select="rdf:Description"/>
                    </table>
                </div>
            </body>
        </html>


    </xsl:template>

    <xsl:template match="rdf:Description/*">

        <tr>

            <td><xsl:apply-templates select="../@rdf:about"/></td>
            <td><xsl:value-of select="name(.)"/></td>
            <td><xsl:apply-templates select="@*|text()"/></td>
        </tr>

    </xsl:template>

    <xsl:template match="@rdf:about|@rdf:resource">
        <a>
            <xsl:attribute name="href"><xsl:value-of select="concat('/data?subject=',.)"/></xsl:attribute>
            <xsl:value-of select="."/>
        </a>
        <a>
            <xsl:attribute name="href"><xsl:value-of select="."/></xsl:attribute>
            <img src="/static/lod/external.png"/>
        </a>
    </xsl:template>
</xsl:stylesheet>