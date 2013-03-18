package infra.bem

import grails.test.mixin.TestFor
import spock.lang.Specification

@TestFor(BemTagLib)
class BemTagLibSpec extends Specification {

    void "root render ok"() {
        String out = tagLib.build(schema: {
            'root'(
                    modif: 'root',
                    _:[title:"root-core-root"],
                    _attrs:[onclick:"alert('rootт!')"]) {

                root (amodif: "top", _:[title:"top test"])
                for(i in 0..10) {
                    root(emodif: "bottom", _:[title:"middle ${i}"])
                }

                g.link(url:"/", "tet")

                root(emodif: "bottom", _:[title:"bottom test"])
            }


        })

        expect:
        out.replaceAll(~/\s+/, " ") == """<div onclick="alert(&#39;rootт!&#39;)" class="b-root m-root_modif_root">
    <h1>root-core-root</h1>
    <div class="b-root m-root_amodif_top">
    <h1>top test</h1>

</div>
<div class="b-root m-root_emodif_bottom">
    <h1>middle 0</h1>

</div>
<div class="b-root m-root_emodif_bottom">
    <h1>middle 1</h1>

</div>
<div class="b-root m-root_emodif_bottom">
    <h1>middle 2</h1>

</div>
<div class="b-root m-root_emodif_bottom">
    <h1>middle 3</h1>

</div>
<div class="b-root m-root_emodif_bottom">
    <h1>middle 4</h1>

</div>
<div class="b-root m-root_emodif_bottom">
    <h1>middle 5</h1>

</div>
<div class="b-root m-root_emodif_bottom">
    <h1>middle 6</h1>

</div>
<div class="b-root m-root_emodif_bottom">
    <h1>middle 7</h1>

</div>
<div class="b-root m-root_emodif_bottom">
    <h1>middle 8</h1>

</div>
<div class="b-root m-root_emodif_bottom">
    <h1>middle 9</h1>

</div>
<div class="b-root m-root_emodif_bottom">
    <h1>middle 10</h1>

</div>
<a href="/">tet</a><div class="b-root m-root_emodif_bottom">
    <h1>bottom test</h1>

</div>

</div>
""".replaceAll(~/\s+/, " ")
    }

    void "sub places render ok"() {
        String out = tagLib.build(schema: {
            'root' {
                'sub-test'(
                        _sub: {
                            head = {
                                "override head"
                            }
                            footer = 'root'(_: [title:"footer"])
                        }
                ) {
                    "just a text"
                }
                'sub-test' {
                    "empty"
                }
            }
        }).replaceAll(~/\s+/, " ")
        println out

        expect:
        out == """<div class="b-root">
    <h1></h1>
<div class="b-sub-test">
<span>override head</span>
    just a text
    <div><div class="b-root">
    <h1>footer</h1>

    </div></div>
</div>
<div class="b-sub-test">
<span>default head</span>
    empty
<div></div>
</div>
</div>
""".replaceAll(~/\s+/, " ")
    }
}
