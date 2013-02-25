import org.codehaus.groovy.grails.cli.CommandLineHelper

includeTargets << grailsScript("_GrailsInit")

includeTargets << grailsScript("_GrailsInit")
includeTargets << grailsScript("_GrailsBootstrap")
includeTargets << grailsScript("_GrailsArgParsing")

target(main: "Renames a block and moves appropriate resources") {
    depends(configureProxy, packageApp, classpath, loadApp, configureApp)

    def bemManagerService = appCtx.getBean('bemManagerService')

    // Getting block name
    String blockName = getBlockName(argsMap, "Please enter the block name to rename, e.g. 'block':")
    String moveToBlock = getBlockName(argsMap, "Please enter the block name to move to, e.g. 'block-new':", 1)

    bemManagerService.moveBlock(basedir, blockName, moveToBlock)

    println "Done"
}

setDefaultTarget(main)

String getBlockName(argsMap, String q, int i=0) {
    String blockName
    try {
        blockName = argsMap.params[i]
    } catch (NoSuchElementException e) {
        def inputHelper = new CommandLineHelper()
        while(!blockName)
            blockName = inputHelper.userInput( q)
    }
    if (blockName.startsWith("b-") || blockName.startsWith("e-")) {
        blockName = blockName.substring(2)
    }
    blockName
}