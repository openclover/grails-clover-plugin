<div class="currentPlugin">
	<div class="header">
		<div class="ratings">
			<rateable:ratings id="rating${plugin.id}" bean="${plugin}" active="false"/>
		</div>						
    	<h4><g:link action="show" params="${[name:plugin.name]}">${plugin.title?.encodeAsHTML()}</g:link></h4>						
        <g:if test="${plugin.official}">
            <div class="supported">supported by SpringSource</div>
        </g:if>
        <div class="detail">
            <div><span class="label">Tags:</span> ${plugin.tags.join(', ')?.encodeAsHTML()}</div>								
			<!-- <g:if test="${plugin.tags}"> -->

			<!-- </g:if> -->

            <div><span class="label">Grails Version:</span> ${plugin.grailsVersion?.encodeAsHTML()}</div>
            <div><span class="label">Current Release:</span> ${plugin.currentRelease?.encodeAsHTML()}</div>
        </div>

	</div>

    <ul class="links">
        <tmpl:pluginLinks plugin="${plugin}" />
    </ul>

    <div class="bottom">
		<div class="moreInfo">
            <g:link action="show" params="${[name:plugin.name]}">
				<r:img alt="More Info" uri="/images/new/plugins/Buttons/more_info_btn.png" border="0" /> 
			</g:link>
		
		</div>
		<a href="${plugin.downloadUrl}">
			<div class="download">
	        	Download
			</div>
		</a>
    </div>
</div>
