import org.codehaus.groovy.grails.cli.CommandLineHelper

includeTargets << grailsScript("_GrailsInit")
includeTargets << grailsScript("_GrailsArgParsing")

target(main: "BEM block scaffolding") {
    // Getting block name
    String blockName = null
    try {
        blockName = argsMap.params.first()
    } catch (NoSuchElementException e) {
        def inputHelper = new CommandLineHelper()
        while(!blockName)
            blockName = inputHelper.userInput( "Please enter the block name to generate, e.g. 'block':" )
    }
    if (blockName.startsWith("b-") || blockName.startsWith("e-")) {
        blockName = blockName.substring(2)
    }

    // Preparing path part
    String path = blockName.replace('-', '/')
    String name = path.substring(path.lastIndexOf("/")+1)
    path = path.substring(0, path.size()-name.size())

    if (!path.endsWith('/')) {
        path = "${path}/"
    }
    if (path == "/") path = ""

    String blockCss = "bem/$path${name}.css"

    println "Generating basic artifacts for ${blockName}..."

    [
            ("${infraBemPluginDir}/src/templates/_block.gsp") : "${basedir}/grails-app/views/bem/${path}_${name}.gsp",
            ("${infraBemPluginDir}/src/templates/block.css") : "${basedir}/web-app/$blockCss",
            ("${infraBemPluginDir}/src/templates/block.html") : "${basedir}/web-app/bem/${path}${name}.html"
    ].entrySet().each {
        ant.copy(file: it.key, tofile: it.value, overwrite: true)
        ant.replace(file: it.value) {
            ant.replacefilter(token: "@block.name@", value: blockName)
            ant.replacefilter(token: "@block.css@", value: blockCss)
        }
        println it.value
    }

    // register resource
    File resources = new File("grails-app/conf/BemResources.groovy")
    String resourcesText = resources.text
    int rightParenthesis = resourcesText.lastIndexOf('}')
    resourcesText = resourcesText.substring(0, rightParenthesis-1) + """
    'b-$blockName'{ defaultBundle "bem"; resource url: "$blockCss"; }
""" + resourcesText.substring(rightParenthesis)

    resources.text = resourcesText

    println "Resource registered as '${blockName}' module."

    println "Done! Enjoy please."

}

setDefaultTarget(main)
