<%@ page import="org.grails.wiki.WikiPage" %>
<div id="editPane">
    <h1>Create Page: ${pageName} <g:render template='/common/wikiSyntaxLink'/></h1>

    <g:hasErrors bean="${wikiPage}">
        <div id="errors" class="errors">
            <g:renderErrors bean="${wikiPage}"></g:renderErrors>
        </div>
    </g:hasErrors>
    <g:form name="createWikiPage" url="[controller:'content', action:'saveWikiPage', id:pageName]">
        <g:render template="wikiFields" model="[wikiPage:new WikiPage(title:pageName)]"/>
        <g:submitButton name="save" value="Save"/>
    </g:form>
</div>
<script type="text/javascript">
    if (myYUI.get("errors") != null) {
        myYUI.fade("errors", {delay:3})
    }
</script>
