<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
    <xsl:output method="html" indent="yes" encoding="UTF-8"/>
    <xsl:include href="common.xsl"/>
    <xsl:include href="common-view.xsl"/>

    <xsl:template match="/">
        <xsl:call-template name="test"/>
    </xsl:template>

    <xsl:template name="main">
        <div class="menu">
            <div class="fill">
                <div class="container">
                    <ul class="nav">
                        <li class="current">
                            <a href="#">Популярные товары</a>
                        </li>
                        <li>
                            <a href="#">Частые тезисы</a>
                        </li>
                        <li>
                            <a href="#">Последние запросы</a>
                        </li>
                    </ul>
                </div>
            </div>
        </div>
        <div class="productInfo">
            <xsl:for-each select="/page/data[@id='product']/collection/detailed-product-for-view">
                <xsl:call-template name="detailed-product"/>
            </xsl:for-each>
        </div>
    </xsl:template>

    <xsl:template name="detailed-product">
        <div class="productName">
            <xsl:value-of select="name"/>
        </div>
        <div class="row">
            <div class="span5">
                <img src="images/lenovoTabletBig.jpg"/>
            </div>
            <div class="span3">
                <h3>Тезисы</h3>
                <ul>
                    <xsl:for-each select="//thesis-for-view">
                        <li>
                            <xsl:value-of select="content"/>
                        </li>
                    </xsl:for-each>
                </ul>
            </div>
            <div class="span5">
                <h3>Похожие товары</h3>
                <ul>
                    <li>
                        <a href="#">lenovo thinkpad x201</a>
                    </li>
                    <li>
                        <a href="#">lenovo thinkpad x220</a>
                    </li>
                </ul>
            </div>
        </div>
        <h3>Лучшие комментарии</h3>
        <xsl:for-each select="//review-for-view">
            <div class="comment">
                <xsl:value-of select="content"/>
                <a>
                    <xsl:attribute name="href">review.xml?id=<xsl:value-of select="@id"/>
                    </xsl:attribute>
                    >>
                </a>
            </div>
        </xsl:for-each>
    </xsl:template>
</xsl:stylesheet>

