<!--

    The contents of this file are subject to the license and copyright
    detailed in the LICENSE and NOTICE files at the root of the source
    tree and available online at

    http://www.dspace.org/license/

-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
   xmlns:ore="http://www.openarchives.org/ore/terms/"
   xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
   xmlns:dc="http://purl.org/dc/elements/1.1/"
   xmlns:dcterms="http://purl.org/dc/terms/" 
   xmlns="http://www.w3.org/2005/Atom"
   version="1.1"
   exclude-result-prefixes="ore xsl rdf dc dcterms">

   <xsl:output method="xml" indent="yes" />

   <xsl:template match="/rdf:RDF">
      <feed>
         <xsl:apply-templates select="rdf:Description[rdf:type/@rdf:resource='http://www.openarchives.org/ore/terms/ResourceMap']" />
         <xsl:apply-templates select="rdf:Description[rdf:type/@rdf:resource='http://www.openarchives.org/ore/terms/Aggregation']" />
      </feed>
   </xsl:template>

   <xsl:template match="rdf:Description[rdf:type/@rdf:resource='http://www.openarchives.org/ore/terms/ResourceMap']">
      <title>
         <xsl:value-of select="../rdf:Description[rdf:type/@rdf:resource='http://www.openarchives.org/ore/terms/Aggregation']/dc:title"/>
      </title>
      <xsl:if test="../rdf:Description[rdf:type/@rdf:resource='http://www.openarchives.org/ore/terms/Aggregation']/dcterms:alternative">
         <subtitle>
            <xsl:value-of select="dcterms:alternative"/>
         </subtitle>
      </xsl:if>
      <link href="{@rdf:about}" type="application/atom+xml" rel="self"/>
      <link href="{ore:describes/@rdf:resource}" rel="describes"/>
      <logo>http://www.openarchives.org/ore/logos/ore_logo.png</logo>
      <category 
         scheme="http://www.openarchives.org/ore/terms/" 
         term="http://www.openarchives.org/ore/terms/ResourceMap" 
         label="Resource Map" />
      <xsl:for-each select="
         ../rdf:Description[rdf:type/@rdf:resource='http://www.openarchives.org/ore/terms/Aggregation']/dc:creator
         |
         ../rdf:Description[rdf:type/@rdf:resource='http://www.openarchives.org/ore/terms/Aggregation']/dcterms:contributor
         ">
         <author>
            <name><xsl:value-of select="."/></name>
            <!-- <uri><xsl:value-of select="@rdf:about"/></uri> -->
         </author>
      </xsl:for-each>
      <updated><xsl:value-of select="dcterms:modified"/></updated>
      <!--  ID's will be available in 1.6/2.0 -->
      <id><xsl:value-of select="ore:describes/@rdf:resource"/></id>
      <summary>
      <xsl:value-of select="../rdf:Description[rdf:type/@rdf:resource='http://www.openarchives.org/ore/terms/Aggregation']/dcterms:abstract"/>
      </summary>
   </xsl:template>
   

   <xsl:template match="rdf:Description[rdf:type/@rdf:resource='http://www.openarchives.org/ore/terms/Aggregation']">
      <xsl:apply-templates select="ore:aggregates" />
   </xsl:template>

   <xsl:template match="ore:aggregates">
      <xsl:variable name="resource" select="@rdf:resource"/>
      
      <xsl:variable name="description" select="../../rdf:Description[@rdf:about=$resource]"/>

       <entry>
         <title><xsl:value-of select="$description/dc:title"/></title>
         <link href="{$description/@rdf:about}" />
         <!--  ID's will be available in 1.6/2.0 -->
         <id><xsl:value-of select="$resource"/></id>
         <!--  updated><xsl:value-of select="$description/dcterms:modified"/></updated>
         <summary><xsl:value-of select="$description/dcterms:abstract"/></summary>-->
      </entry>
   </xsl:template>

</xsl:stylesheet>