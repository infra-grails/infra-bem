package infra.bem

class BemTagLib {
    static namespace = "infra"

    def build = {attrs->

        BemBuilder builder = new BemBuilder(this)

        try {
            Closure schema = attrs.schema
            schema.delegate = builder
            schema.resolveStrategy = Closure.DELEGATE_FIRST
            schema.call()
        } catch (Exception e) {
            log.error("Error during parse of bem tree", e)
        }

    }

    def bemAttrs = {attrs->
        String blockName = attrs.for
        getBlockAttrs(blockName).entrySet().each {
            out << " ${it.key}=\"${it.value.encodeAsHTML()}\""
        }
    }

    def include = {attrs, body->
        String template = BemBuilder.getBlockTemplate(attrs.block)
        buildBlock((String)attrs.block, template, body, attrs.modifiers ?: [:], attrs.model ?: [:], attrs.attrs ?: [:])
    }

    void buildBlock(String name, String template, Closure body, Map modifiers = [:], Map model = [:], Map attrs = [:]) {
        r.require(module: "b-${name}")
        setBlockAttrs(name, attrs, modifiers)
        out << g.render(template: template, model: model, body)
    }


    Map<String,Map<String,String>> getAttrsStorage() {
        Map<String, Map<String,String>> storage = pageScope.bemAttrsStorage
        if (!storage) {
            storage = [:]
            pageScope.bemAttrsStorage = storage
        }
        storage
    }

    Map<String,String> getBlockAttrs(String blockName) {
        attrsStorage.containsKey(blockName) ? attrsStorage.remove(blockName) : [:]
    }

    void setBlockAttrs(String blockName, Map<String,String> attrs, Map<String,String> modifiers) {
        String cssClass = "b-${blockName}"
        modifiers.entrySet().each {
            cssClass += " m-${blockName}_${it.key}_${it.value}"
        }
        attrs.put('class', cssClass)
        attrsStorage.put(blockName, attrs)
    }
}
