package infra.bem
/**
 * @author alari
 * @since 2/5/13 10:45 AM
 */
class BemBuilder {
    private BemTagLib bem

    private static final Closure EMPTY = {}
    private Map<String,BemTaglibWrapper> taglibsByPrefix = [:]

    static String getBlockTemplate(String blockName) {
        "/bem/${blockName.replace('-', '/')}"
    }

    BemBuilder(BemTagLib bem) {
        this.bem = bem
    }

    def methodMissing(String name, args) {
        String template = getBlockTemplate(name)
        Map model = [:]
        Map modifiers = [:]
        Map attrs = [:]
        Closure body = EMPTY

        if (args) {

            if (args?.first() instanceof Map) {
                Map argsMap = (Map)args.first()
                if (argsMap.containsKey("_")) {
                    model =  (Map)argsMap.remove("_")
                }
                if (argsMap.containsKey("_attrs")) {
                    attrs = (Map)argsMap.remove("_attrs")
                }
                modifiers = argsMap
            }

            if (args?.last() instanceof Closure) {
                body = (Closure)args.last()
                body.delegate = this
                body.resolveStrategy = Closure.DELEGATE_ONLY
            }
        }

        bem.buildBlock(name, template, body, modifiers, model, attrs)
    }

    def propertyMissing(String name) {
        if (!taglibsByPrefix.containsKey(name)) {
            taglibsByPrefix.put(name, new BemTaglibWrapper(bem.propertyMissing(name), bem.out))
        }
        taglibsByPrefix.get(name)
    }
}
