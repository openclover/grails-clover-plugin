<%--
  Created by IntelliJ IDEA.
  User: pal20
  Date: 27-May-2010
  Time: 16:09:08
  To change this template use File | Settings | File Templates.
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<head>
  <title>Grails Plugins Forum</title>
  <meta name="layout" content="pluginLayout">
</head>
<body>
  <div id="forumTop">
      <g:if test="${ request.getHeader('User-Agent') =~ /\bOpera\b/ }">
           <p style="position: absolute; top: -3em; left: 8em;"><b>Opera users</b>: if the page appears to reload continually, <a href="http://grails-plugins.847840.n3.nabble.com/">use this link</a> to access the forum.</p>
      </g:if>
      &nbsp;
  </div>
  <div id="forumWrapper">
    <a id="nabblelink" href="http://grails-plugins.847840.n3.nabble.com/">Grails Plugins</a>
    <script src="http://grails-plugins.847840.n3.nabble.com/embed/f847840"></script>
  </div>
  <div id="forumBottom">&nbsp;</div>
</body>
