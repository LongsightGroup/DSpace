<!--

    The contents of this file are subject to the license and copyright
    detailed in the LICENSE and NOTICE files at the root of the source
    tree and available online at

    http://www.dspace.org/license/

-->
<xsl:stylesheet
        xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
        xmlns:xalan="http://xml.apache.org/xslt"
        xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
        xmlns:owl="http://www.w3.org/2002/07/owl#"
        version="1.0">

    <xsl:output method="xml" indent="yes" encoding="UTF-8"  xalan:indent-amount="3"/>



    <xsl:template match="rdf:Description|owl:ObjectProperty|owl:Class">
        public static final URI <xsl:value-of select="substring-after(@rdf:about, '#')"/> = vf.createURI("<xsl:value-of select="substring-before(@rdf:about, '#')"/>#", "<xsl:value-of select="substring-after(@rdf:about, '#')"/>");
    </xsl:template>


    <xsl:template match="/rdf:RDF">
              <foo>
            <xsl:apply-templates select="rdf:Description|owl:ObjectProperty|owl:Class" />
              </foo>
    </xsl:template>

</xsl:stylesheet>