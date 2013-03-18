package infra.bem

import grails.util.Environment
import org.codehaus.groovy.grails.web.util.StreamCharBuffer

class BemTagLib {
    static namespace = "infra"

    def build = {attrs->

        BemBuilder builder = new BemBuilder(this, out)

        try {
            Closure schema = attrs.schema
            schema.delegate = builder
            schema.resolveStrategy = Closure.DELEGATE_FIRST
            schema.call()
        } catch (Exception e) {
            log.error("Error during parse of bem tree", e)
        }

    }

    def place = {attrs, body->
        String blockName = attrs.for
        String placeName = attrs.name
        out << (getSubBlock(blockName, placeName) ?: body())
    }

    def bemAttrs = {attrs->
        String blockName = attrs.for
        getBlockAttrs(blockName).entrySet().each {
            out << / ${it.key}="${it.value.encodeAsHTML()}"/
        }
    }

    def include = {attrs, body->
        BemBlock block = new BemBlock(
                name: attrs.block,
                body: body,
                modifiers: attrs.modifiers ?: [:],
                model: attrs.model ?: [:],
                attrs: attrs.attrs ?: [:]

        )
        out << buildBlock(block)
    }

    StreamCharBuffer buildBlock(BemBlock block) {
        if (Environment.TEST != Environment.getCurrent()) r.require(module: "b-${block.name}")
        setBlockAttrs(block)
        if (block.subs?.size()) {
            setBlockSubs(block)
        }
        g.render(template: block.template, model: block.model, block.body)
    }

    def unescape = {attrs ->
        def var = attrs.var
        if(var) out << var
    }

    Map<String,Map<String,String>> getAttrsStorage() {
        Map<String, Map<String,String>> storage = pageScope.bemAttrsStorage
        if (!storage) {
            storage = [:]
            pageScope.bemAttrsStorage = storage
        }
        storage
    }

    Map<String,Map<String,?>> getSubsStorage() {
        Map<String,Map<String,?>> storage = pageScope.bemSubsStorage
        if (!storage) {
            storage = [:]
            pageScope.bemSubsStorage = storage
        }
        storage
    }

    Map<String,String> getBlockAttrs(String blockName) {
        attrsStorage.containsKey(blockName) ? attrsStorage.remove(blockName) : [:]
    }

    private setBlockSubs(BemBlock block) {
        subsStorage.put(block.name, block.subs)
    }

    def getSubBlock(String blockName, String placeName) {
        subsStorage.get(blockName)?.remove(placeName)
    }

    private void setBlockAttrs(BemBlock block) {
        String cssClass = "b-${block.name}"
        block.modifiers.entrySet().each {
            cssClass += " m-${block.name}_${it.key}_${it.value}"
        }
        block.attrs.put('class', cssClass)
        attrsStorage.put(block.name, block.attrs)

    }
}
