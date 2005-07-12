<?xml version="1.0" encoding="UTF-8" ?>
<!--
                Sun Public License Notice

The contents of this file are subject to the Sun Public License
Version 1.0 (the "License"). You may not use this file except in
compliance with the License. A copy of the License is available at
http://www.sun.com/

The Original Code is NetBeans. The Initial Developer of the Original
Code is Sun Microsystems, Inc. Portions Copyright 1997-2003 Sun
Microsystems, Inc. All Rights Reserved.
-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:import href="../antsrc/org/netbeans/nbbuild/Arch.xsl" />
    <xsl:import href="export-apichanges.xsl" />
    <xsl:output method="html" />
    
    <xsl:param name="arch.stylesheet"/>
    <xsl:param name="arch.overviewlink"/>
    <xsl:param name="arch.footer"/>
    <xsl:param name="arch.target"/>

    <xsl:template match="/">
        <xsl:variable name="interfaces" select="//api[@type='export']" />
        
        <html>
         <head>
          <title>Overview</title><!-- note this is ignored -->
         </head>
         <body>
          <p>
            <xsl:apply-templates select="api-answers/answer[@id='arch-overall']/node()" mode="description"/>
          </p>

          <h3>What is New (see <a href="apichanges.html">all changes</a>)?</h3>

          <ul>
              <xsl:call-template name="api-changes" >
                  <xsl:with-param name="changes-since-url" select="'apichanges.html'" />
                  <xsl:with-param name="changes-since-amount" select="'5'" />
              </xsl:call-template>
          </ul>
          
          <h3>Use Cases</h3>
          
          <xsl:apply-templates select="//answer[@id='arch-usecases']" mode="description" />
          
          <h3>Exported Interfaces</h3>
          
                This table lists all of the module exported APIs 
                with 
                defined stability classifications. It is generated
                based on answers to questions about the architecture 
                of the module. <a href="architecture-summary.html">Read them all</a>...

                <!-- imported from Arch.xsl -->
                <xsl:call-template name="generate-api-table" >
                    <xsl:with-param name="target" select="'api-group'" />
                    <xsl:with-param name="generate-import" select="'false'" />
                </xsl:call-template>
        
            <h3>Dependencies</h3>

            <h5>What do other modules need to do to declare a dependency on this one?</h5>
            <xsl:apply-templates select="api-answers/answer[@id='deploy-dependencies']/node()" mode="description"/>

            <h5>Other dependencies</h5>

                PENDING: Need project.xml to list what projects this one depends on from project.xml &amp;
                arch.xml and
                other imported interfaces from arch.xml

         </body>
        </html>
         
         
        <!--
        <module name="{api-answers/@module}"
                target="{$arch.target}"
                stylesheet="{$arch.stylesheet}"
                overviewlink="{$arch.overviewlink}"
                footer="{$arch.footer}">
            
            <xsl:variable name="deploy-dependencies" select="api-answers/answer[@id='deploy-dependencies']"/>
            <xsl:if test="$deploy-dependencies">
                <deploy-dependencies>
                    <xsl:apply-templates select="$deploy-dependencies/node()"/>
                </deploy-dependencies>
            </xsl:if>
            
            <xsl:variable name="arch-usecases" select="api-answers/answer[@id='arch-usecases']"/>
            <xsl:if test="$arch-usecases">
                <arch-usecases>
                    <xsl:apply-templates select="$arch-usecases/node()"/>
                </arch-usecases>
            </xsl:if>            


        </module>
        -->
    </xsl:template>

    <xsl:template match="api" mode="description">
        <xsl:param name="group" />
        <xsl:param name="type" />
    
        <xsl:variable name="name" select="@name" />
        <xsl:variable name="category" select="@category" />
        <xsl:variable name="url" select="@url" />

        <xsl:choose> 
          <xsl:when test="string-length($url)>0">
            <a>
              <xsl:attribute name="href"><xsl:value-of select="$url" /></xsl:attribute>
              <xsl:value-of select="$name" />
            </a>
          </xsl:when>
          <xsl:otherwise>
            <xsl:value-of select="$name" />
          </xsl:otherwise>
        </xsl:choose>

        <xsl:apply-templates />
    </xsl:template>

    <xsl:template match="usecase" mode="description" >
        <h5><xsl:value-of select="@name" /></h5>
        <xsl:apply-templates select="./node()" />
    </xsl:template>
     
    <!-- Format random HTML elements as is: -->
    <xsl:template match="@*|node()">
       <xsl:copy  >
          <xsl:apply-templates select="@*|node()"/>
       </xsl:copy>
    </xsl:template>

  
    <!-- format the API table -->
    <xsl:template name="export-api">
        <xsl:param name="arch.target" />
        <xsl:variable name="name" select="@name" />
        <xsl:variable name="type" select="@type" />
        <xsl:variable name="group" select="@group" />
        <xsl:variable name="category" select="@category" />
        <xsl:variable name="url" select="@url" />
        <xsl:variable name="description" select="node()" />

        <tr class="tabler">
            <td>
                <a>
                    <xsl:attribute name="href" >
                        <xsl:value-of select="$arch.target" />
                        <xsl:text>#</xsl:text>
                        <xsl:value-of select="$group"/>
                        <xsl:text>-</xsl:text>
                        <xsl:value-of select="$name"/>
                    </xsl:attribute>
                    <xsl:value-of select="$name" />
                </a>
            </td>
            <!--
            <td>
                <xsl:choose>
                    <xsl:when test="$type='import'">Imported</xsl:when>
                    <xsl:when test="$type='export'">Exported</xsl:when>
                    <xsl:otherwise>WARNING: <xsl:value-of select="$type" /></xsl:otherwise>
                </xsl:choose>
            </td> -->
            <td> <!-- stability category -->
                <a>
                    <xsl:attribute name="href">
                        <xsl:text>http://openide.netbeans.org/tutorial/api-design.html#category-</xsl:text>
                        <xsl:value-of select="$category" />
                    </xsl:attribute>
                    <xsl:choose>
                        <xsl:when test="$category='official'">Official</xsl:when>
                        <xsl:when test="$category='stable'">Stable</xsl:when>
                        <xsl:when test="$category='devel'">Under Development</xsl:when>
                        <xsl:when test="$category='third'">Third party</xsl:when>
                        <xsl:when test="$category='standard'">Standard</xsl:when>
                        <xsl:when test="$category='friend'">Friend</xsl:when>
                        <xsl:when test="$category='private'">Private</xsl:when>
                        <xsl:when test="$category='deprecated'">Deprecated</xsl:when>
                        <xsl:otherwise>
                            <xsl:message>
                                WARNING: <xsl:value-of select="$category"/>
                            </xsl:message>
                        </xsl:otherwise>
                    </xsl:choose>
                </a>  
            </td>

            <td> <!-- url -->
                <a href="{$url}"><xsl:value-of select="$url"/></a>
                <xsl:if test="$description" >
                    <p>
                        <xsl:apply-templates select="$description" />
                    </p>
                </xsl:if>
            </td>
        </tr>

    </xsl:template>
    
    <xsl:template name="print-change" >
        <li>
            <xsl:choose>
                <xsl:when test="date/@month=1">Jan</xsl:when>
                <xsl:when test="date/@month=2">Feb</xsl:when>
                <xsl:when test="date/@month=3">Mar</xsl:when>
                <xsl:when test="date/@month=4">Apr</xsl:when>
                <xsl:when test="date/@month=5">May</xsl:when>
                <xsl:when test="date/@month=6">Jun</xsl:when>
                <xsl:when test="date/@month=7">Jul</xsl:when>
                <xsl:when test="date/@month=8">Aug</xsl:when>
                <xsl:when test="date/@month=9">Sep</xsl:when>
                <xsl:when test="date/@month=10">Oct</xsl:when>
                <xsl:when test="date/@month=11">Nov</xsl:when>
                <xsl:when test="date/@month=12">Dec</xsl:when>
            </xsl:choose><xsl:text> </xsl:text>
            <xsl:value-of select="date/@day"/> '<xsl:value-of select="substring(date/@year, 3, 2)"/>
            <xsl:text> </xsl:text>
            <a><xsl:attribute name="href">apichanges.html#<xsl:call-template name="change-id"/></xsl:attribute>
                <xsl:apply-templates select="summary/node()"/>
            </a>
            <p>
                <xsl:apply-templates select="description/node()" />
            </p>
        </li>
    </xsl:template>
    
</xsl:stylesheet> 
