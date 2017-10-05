<?xml version="1.0"?>

<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:mets="http://www.loc.gov/METS/"
                xmlns:didl="urn:mpeg:mpeg21:2002:02-DIDL-NS"
                xmlns:xlink="http://www.w3.org/1999/xlink"
                xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
                exclude-result-prefixes="rdf didl"
>

    <xsl:template match="/">
        <mets:mets>
            <mets:fileSec USE="storage/preservation">
                <mets:fileGrp>
                    <mets:file ID="metadata" MIMETYPE="text/xml">
                        <mets:FLocat LOCTYPE="URL" xlink:href="file://./metadata.xml" />
                    </mets:file>
                    <xsl:apply-templates select="//didl:Resource[starts-with(@ref, 'http')]" />
                </mets:fileGrp>
            </mets:fileSec>
        </mets:mets>
    </xsl:template>

    <xsl:template match="//didl:Resource[starts-with(@ref, 'http')]">
        <xsl:variable name="count" select="position()" />
        <mets:file>
            <xsl:attribute name="ID">
                <xsl:value-of select="concat('FILE_', format-number($count, '0000'))" />
            </xsl:attribute>
            <xsl:attribute name="MIMETYPE">
                <xsl:value-of select="@mimeType" />
            </xsl:attribute>
            <mets:FLocat LOCTYPE="URL">
                <xsl:attribute name="xlink:href">
                    <xsl:value-of select="@ref" />
                </xsl:attribute>
            </mets:FLocat>
        </mets:file>
    </xsl:template>

</xsl:stylesheet>