package org.grails.test.pages

import geb.Page

class PluginHomePage extends Page {
    static url = "/plugins"
    
    static at = {
        assert title == "Grails - Plugins Portal"
        assert $("h1", 0).text() == "Plugins"

        def pluginSearch = $("#pluginSearch")
        assert pluginSearch.size() == 1
        assert pluginSearch.find("input", name: "q").size() == 1
        assert pluginSearch.find("input", value: "Search").size() == 1

        // Helpful Links
        assert $(text: "Helpful Links").size() == 1
        assert $("a", text: "Creating a plugin").@href == "/Creating+Plugins"
        assert $("a", text: "Plugins Fisheye").@href == "http://svn.grails-plugins.codehaus.org/"
        assert $("a", text: "JIRA Issue Tracker").@href == "http://jira.codehaus.org/browse/GRAILSPLUGINS"
        assert $("a", text: "Mailing List Help").@href == "/Mailing+lists"
        return true
    }
}
