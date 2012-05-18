<%@page import="org.grails.auth.Role" %>
<html>
	<head>
		<meta name="layout" content="pluginDetails">
		<title>Edit Plugin</title>
		<style>
.pluginBoxBottom {
	top: -11px;
}
		</style>
	</head>
	<body id="editplugin">
		<div id="pluginDetailsBox" align="center">
			<div id="pluginDetailsTop"></div>
			<div id="pluginDetailsContainer">
				<h1 id="pluginBoxTitle">Edit Plugin</h1>
				<div class="pluginDetail">

					<g:if test="${flash.message}">
						<div class="message">${flash.message}</div>
					</g:if>
					<g:hasErrors bean="${plugin}">
						<div class="errors">
							<g:renderErrors bean="${plugin}" as="list"/>
						</div>
					</g:hasErrors>

					<g:form action='editPlugin' id="${plugin?.id}">
						<input type="hidden" name="id" value="${plugin?.id}"/>
						<input type="hidden" name="version" value="${plugin?.version}"/>
							<table>
								<tbody>

								<plugin:input
										name="Name"
										description="This is what you would type in the command line after 'grails install-plugin'.">
									<input type="text" id="name" name="name" value="${fieldValue(bean: plugin, field: 'name')}"/>
								</plugin:input>


								<plugin:input
										name="Title"
										description="The 'Human-Readable' title of your plugin">
									<input type="text" id="title" name="title" value="${fieldValue(bean: plugin, field: 'title')}"/>
								</plugin:input>

								<plugin:input
										name="Description"
										description="A description/summary of the plugin">
									<textarea id="summary" name="summary">${fieldValue(bean: plugin, field: 'summary')}</textarea>
								</plugin:input>

								<plugin:input
										name="Grails Version"
										description="The Grails version this plugin was developed under.">
									<input type="text" id="grailsVersion" name="grailsVersion" value="${fieldValue(bean: plugin, field: 'grailsVersion')}"/>
								</plugin:input>

								<plugin:input name="Default Dependency Scope" description="When users declare a dependency on this plugin, which scope should they use (compile, build,...)?">
									<input type="text" id="defaultDependencyScope" name="defaultDependencyScope" value="${fieldValue(bean: plugin, field: 'defaultDependencyScope')}"/>
								</plugin:input>

								<plugin:input
										name="Documentation URL"
										description="Where on the web are your docs? If this is it, then leave blank.">
									<input type="text" id="documentationUrl" name="documentationUrl" value="${fieldValue(bean: plugin, field: 'documentationUrl')}"/>
								</plugin:input>

								<plugin:input
										name="Download URL"
										description="Where someone would click to get your plugin">
									<input type="text" id="downloadUrl" name="downloadUrl" value="${fieldValue(bean: plugin, field: 'downloadUrl')}"/>
								</plugin:input>
												<plugin:input name="Source URL" description="Where is the source code for your plugin? Links to browseable code preferred.">
													<input type="text" id="scmUrl" name="scmUrl" value="${fieldValue(bean: plugin, field: 'scmUrl')}"/>
												</plugin:input>
								<plugin:input name="Issues URL" description="Where is the issue tracker for your plugin?">
									<input type="text" id="issuesUrl" name="issuesUrl" value="${fieldValue(bean: plugin, field: 'issuesUrl')}"/>
								</plugin:input>

								<plugin:input name="Custom Repository URLs" description="Some plugins require dependencies that come from other repositories than the standard Grails and Maven ones. This is a comma-separated list of those repositories.">
									<input type="text" id="mavenRepositoryUrls" name="mavenRepositoryUrls" value="${plugin.mavenRepositories?.join(', ') ?: ''}"/>
								</plugin:input>

								<shiro:hasRole name="${ Role.ADMINISTRATOR }">
								<plugin:input name="Featured?" description="Should this plugin appear in the featured list?">
									<g:checkBox name="featured" value="${plugin?.featured}"/>
								</plugin:input>
								<plugin:input name="Official?" description="Is this an official, supported plugin from SpringSource/VMware?">
									<g:checkBox name="official" value="${plugin?.official}"/>
								</plugin:input>
								<plugin:input name="Abandoned?" description="Has the plugin been abandoned?">
									<g:checkBox name="zombie" value="${plugin?.zombie}"/>
								</plugin:input>
								</shiro:hasRole>
								</tbody>
							</table>
						<ul class="actionBar">
							<li><g:submitButton name="save" value="Save"/></li>
							<li><g:link action="show" params="[name: plugin.name]">Cancel</g:link></li>
						</ul>
						%{--<div class="buttons">--}%
							%{--<span class="button"><g:actionSubmit class="save" value="Update"/></span>--}%
							%{--<span class="button"><g:actionSubmit class="delete" onclick="return confirm('Are you sure?');" value="Delete"/></span>--}%
						%{--</div>--}%
					</g:form>


				</div>
				
			</div>
		</div>
	</body>
</html>
