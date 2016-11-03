
<!--
    This stylesheet contains helper templates for things like i18n and standard attributes.

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
	xmlns="http://www.w3.org/1999/xhtml"
    xmlns:confman="org.dspace.core.ConfigurationManager"
	exclude-result-prefixes="i18n dri mets xlink xsl dim xhtml mods dc confman">

    <xsl:output indent="yes"/>

    <!--added classes to differentiate between collections, communities and items-->
    <xsl:template match="dri:reference" mode="summaryList">
        <xsl:variable name="externalMetadataURL">
            <xsl:text>cocoon:/</xsl:text>
            <xsl:value-of select="@url"/>
            <!-- Since this is a summary only grab the descriptive metadata, and the thumbnails -->
            <xsl:text>?sections=dmdSec,fileSec&amp;fileGrpTypes=THUMBNAIL,ORIGINAL</xsl:text>
            <!-- An example of requesting a specific metadata standard (MODS and QDC crosswalks only work for items)->
            <xsl:if test="@type='DSpace Item'">
                <xsl:text>&amp;dmdTypes=DC</xsl:text>
            </xsl:if>-->
        </xsl:variable>
        <xsl:comment> External Metadata URL: <xsl:value-of select="$externalMetadataURL"/> </xsl:comment>
        <li>
            <xsl:attribute name="class">
                <xsl:text>ds-artifact-item </xsl:text>
                <xsl:choose>
                    <xsl:when test="contains(@type, 'Community')">
                        <xsl:text>community </xsl:text>
                    </xsl:when>
                    <xsl:when test="contains(@type, 'Collection')">
                        <xsl:text>collection </xsl:text>
                    </xsl:when>
                </xsl:choose>
                <xsl:variable name="emphasis" select="confman:getProperty('xmlui.theme.mirage.item-list.emphasis')"/>
                <xsl:choose>
                    <xsl:when test="'file' = $emphasis">
                        <xsl:text>emphasis-file </xsl:text>
                    </xsl:when>
                    <xsl:when test="'gallery' = $emphasis">
                        <xsl:text>emphasis-gallery </xsl:text>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:text>emphasis-other </xsl:text>
                    </xsl:otherwise>
                </xsl:choose>
                <xsl:choose>
                    <xsl:when test="position() mod 2 = 0">even</xsl:when>
                    <xsl:otherwise>odd</xsl:otherwise>
                </xsl:choose>
            </xsl:attribute>
            <xsl:apply-templates select="document($externalMetadataURL)" mode="summaryList"/>
            <xsl:apply-templates />
        </li>
    </xsl:template>

    <xsl:template name="standardAttributes">
        <xsl:param name="class"/>
        <xsl:param name="placeholder"/>
        <xsl:if test="@id">
            <xsl:attribute name="id"><xsl:value-of select="translate(@id,'.','_')"/></xsl:attribute>
        </xsl:if>
        <xsl:attribute name="class">
            <xsl:value-of select="normalize-space($class)"/>
            <xsl:if test="@rend">
                <xsl:text> </xsl:text>
                <xsl:value-of select="@rend"/>
            </xsl:if>
        </xsl:attribute>
        <xsl:if test="string-length($placeholder)>0">
            <xsl:attribute name="placeholder"><xsl:value-of select="$placeholder"/></xsl:attribute>
            <xsl:attribute name="i18n:attr">placeholder</xsl:attribute>
        </xsl:if>
    </xsl:template>

    <!-- Helper for showing mime-type-icon for item in lieu of thumbnail -->
    <xsl:template name="getFileFormatIcon">
        <xsl:param name="mimetype"/>

        <img class="mimeicon">
            <xsl:attribute name="width">128px</xsl:attribute>
            <xsl:attribute name="height">128px</xsl:attribute>
            <xsl:attribute name="alt"><xsl:value-of select="$mimetype"/></xsl:attribute>
            <xsl:attribute name="src">
                <xsl:value-of select="$theme-path"/>
                <xsl:text>../_precompiled-mirage2</xsl:text>

                <xsl:choose>
                    <xsl:when test="$mimetype='application/pdf'">
                        <xsl:text>/images/mimeicons/pdf.png</xsl:text>
                    </xsl:when>
                    <xsl:when test="$mimetype='application/html'">
                        <xsl:text>/images/mimeicons/html.png</xsl:text>
                    </xsl:when>
                    <xsl:when test="$mimetype='text/xml'">
                        <xsl:text>/images/mimeicons/xml.png</xsl:text>
                    </xsl:when>
                    <xsl:when test="$mimetype='text/plain'">
                        <xsl:text>/images/mimeicons/plain.png</xsl:text>
                    </xsl:when>
                    <xsl:when test="$mimetype='text/html'">
                        <xsl:text>/images/mimeicons/html.png</xsl:text>
                    </xsl:when>
                    <xsl:when test="$mimetype='application/msword' or $mimetype='application/vnd.openxmlformats-officedocument.wordprocessingml.document'">
                        <xsl:text>/images/mimeicons/msword.png</xsl:text>
                    </xsl:when>
                    <xsl:when test="$mimetype='application/vnd.ms-powerpoint' or $mimetype='application/vnd.openxmlformats-officedocument.presentationml.presentation'">
                        <xsl:text>/images/mimeicons/vnd.ms-powerpoint.png</xsl:text>
                    </xsl:when>
                    <xsl:when test="$mimetype='application/vnd.ms-excel' or $mimetype='application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'">
                        <xsl:text>/images/mimeicons/vnd.ms-excel.png</xsl:text>
                    </xsl:when>
                    <xsl:when test="$mimetype='image/jpeg'">
                        <xsl:text>/images/mimeicons/jpeg.png</xsl:text>
                    </xsl:when>
                    <xsl:when test="$mimetype='image/gif'">
                        <xsl:text>/images/mimeicons/gif.png</xsl:text>
                    </xsl:when>
                    <xsl:when test="$mimetype='image/png'">
                        <xsl:text>/images/mimeicons/png.png</xsl:text>
                    </xsl:when>
                    <xsl:when test="$mimetype='image/tiff'">
                        <xsl:text>/images/mimeicons/other_image.png</xsl:text>
                    </xsl:when>
                    <xsl:when test="$mimetype='audio/x-aiff'">
                        <xsl:text>/images/mimeicons/other_audio.png</xsl:text>
                    </xsl:when>
                    <xsl:when test="$mimetype='audio/basic'">
                        <xsl:text>/images/mimeicons/other_audio.png</xsl:text>
                    </xsl:when>
                    <xsl:when test="$mimetype='audio/x-wav'">
                        <xsl:text>/images/mimeicons/other_audio.png</xsl:text>
                    </xsl:when>
                    <xsl:when test="$mimetype='video/mpeg'">
                        <xsl:text>/images/mimeicons/mpeg.png</xsl:text>
                    </xsl:when>
                    <xsl:when test="$mimetype='text/richtext'">
                        <xsl:text>/images/mimeicons/richtext.png</xsl:text>
                    </xsl:when>
                    <xsl:when test="$mimetype='image/x-ms-bmp'">
                        <xsl:text>/images/mimeicons/x-ms-bmp.png</xsl:text>
                    </xsl:when>
                    <xsl:when test="$mimetype='application/postscript'">
                        <xsl:text>/images/mimeicons/plain.png</xsl:text>
                    </xsl:when>
                    <xsl:when test="$mimetype='video/quicktime'">
                        <xsl:text>/images/mimeicons/mov.png</xsl:text>
                    </xsl:when>
                    <xsl:when test="$mimetype='audio/mpeg'">
                        <xsl:text>/images/mimeicons/mp3.png</xsl:text>
                    </xsl:when>
                    <xsl:when test="$mimetype='audio/x-mpeg'">
                        <xsl:text>/images/mimeicons/mpeg.png</xsl:text>
                    </xsl:when>
                    <xsl:when test="$mimetype='application/x-dvi'">
                        <xsl:text>/images/mimeicons/other_movie.png</xsl:text>
                    </xsl:when>
                    <xsl:when test="$mimetype='audio/x-pn-realaudio'">
                        <xsl:text>/images/mimeicons/real.png</xsl:text>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:text>/images/mimeicons/mime.png</xsl:text>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:attribute>
        </img>
    </xsl:template>


    <!-- URL ENCODE-->
    <!-- Characters we'll support.
     We could add control chars 0-31 and 127-159, but we won't. -->
    <xsl:variable name="ascii"> !"#$%&amp;'()*+,-./0123456789:;&lt;=&gt;?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\]^_`abcdefghijklmnopqrstuvwxyz{|}~</xsl:variable>
    <xsl:variable name="latin1">&#160;&#161;&#162;&#163;&#164;&#165;&#166;&#167;&#168;&#169;&#170;&#171;&#172;&#173;&#174;&#175;&#176;&#177;&#178;&#179;&#180;&#181;&#182;&#183;&#184;&#185;&#186;&#187;&#188;&#189;&#190;&#191;&#192;&#193;&#194;&#195;&#196;&#197;&#198;&#199;&#200;&#201;&#202;&#203;&#204;&#205;&#206;&#207;&#208;&#209;&#210;&#211;&#212;&#213;&#214;&#215;&#216;&#217;&#218;&#219;&#220;&#221;&#222;&#223;&#224;&#225;&#226;&#227;&#228;&#229;&#230;&#231;&#232;&#233;&#234;&#235;&#236;&#237;&#238;&#239;&#240;&#241;&#242;&#243;&#244;&#245;&#246;&#247;&#248;&#249;&#250;&#251;&#252;&#253;&#254;&#255;</xsl:variable>

    <!-- Characters that usually don't need to be escaped -->
    <xsl:variable name="safe">!'()*-.0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ_abcdefghijklmnopqrstuvwxyz~</xsl:variable>

    <xsl:variable name="hex" >0123456789ABCDEF</xsl:variable>

    <xsl:template name="url-encode">
        <xsl:param name="str"/>
        <xsl:if test="$str">
            <xsl:variable name="first-char" select="substring($str,1,1)"/>
            <xsl:choose>
                <xsl:when test="contains($safe,$first-char)">
                    <xsl:value-of select="$first-char"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:variable name="codepoint">
                        <xsl:choose>
                            <xsl:when test="contains($ascii,$first-char)">
                                <xsl:value-of select="string-length(substring-before($ascii,$first-char)) + 32"/>
                            </xsl:when>
                            <xsl:when test="contains($latin1,$first-char)">
                                <xsl:value-of select="string-length(substring-before($latin1,$first-char)) + 160"/>
                            </xsl:when>
                            <xsl:otherwise>
                                <xsl:message terminate="no">Warning: string contains a character that is out of range! Substituting "?".</xsl:message>
                                <xsl:text>63</xsl:text>
                            </xsl:otherwise>
                        </xsl:choose>
                    </xsl:variable>
                    <xsl:variable name="hex-digit1" select="substring($hex,floor($codepoint div 16) + 1,1)"/>
                    <xsl:variable name="hex-digit2" select="substring($hex,$codepoint mod 16 + 1,1)"/>
                    <xsl:value-of select="concat('%',$hex-digit1,$hex-digit2)"/>
                </xsl:otherwise>
            </xsl:choose>
            <xsl:if test="string-length($str) &gt; 1">
                <xsl:call-template name="url-encode">
                    <xsl:with-param name="str" select="substring($str,2)"/>
                </xsl:call-template>
            </xsl:if>
        </xsl:if>
    </xsl:template>


</xsl:stylesheet>
