<?xml version="1.0"?>

<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:pom="http://maven.apache.org/POM/4.0.0">

    <xsl:output omit-xml-declaration="yes" indent="no"/>

    <xsl:template match="/">
        <xsl:apply-templates select="/pom:project/pom:version" />
    </xsl:template>

    <xsl:template match="/pom:project/pom:version">
        <xsl:value-of select="."/>
    </xsl:template>

</xsl:stylesheet>