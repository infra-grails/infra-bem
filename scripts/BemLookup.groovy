import org.codehaus.groovy.grails.cli.CommandLineHelper

includeTargets << grailsScript("_GrailsInit")
includeTargets << grailsScript("_GrailsBootstrap")
includeTargets << grailsScript("_GrailsArgParsing")

target(main: "Lookup the usages of bem block") {
    depends(configureProxy, packageApp, classpath, loadApp, configureApp)

    def bemManagerService = appCtx.getBean('bemManagerService')

    // Getting block name
    String blockName = null
    try {
        blockName = argsMap.params.first()
    } catch (NoSuchElementException e) {
        def inputHelper = new CommandLineHelper()
        while(!blockName)
            blockName = inputHelper.userInput( "Please enter the block name to lookup, e.g. 'block':" )
    }
    if (blockName.startsWith("b-") || blockName.startsWith("e-")) {
        blockName = blockName.substring(2)
    }

    bemManagerService.lookupBlock(basedir, blockName)

    println "Done"
}

setDefaultTarget(main)



