Grails Infra Bem Plugin
==========================
Plugin provides support for [BEM methodology](http://bem.info/) in Grails infrastructure. It lays over GSP's,
taglibs and Resources framework and helps to organize working with markup and pages structure
in middle to huge projects.

General usage
--------------------------

### 1. Create blocks

Blocks are independent and fully reuseable markup and interface logic pieces.

```
grails bem-block my-block
grails bem-block my-another
```

It will create `views/bem/my/_block.gsp`, `web-app/bem/my/block/block.css`, the same resources for `my-another`
block and register them all in `BemResources.groovy` (you shouldn't care about this unless you wish to alter
your block resources, e.g. add javascripts).


### 2. Change block markup and resources

According to _BEM_ methodology, alter the block files. To allow content insertion in blocks, use `${body()}` feature.
Don't care about passing the model to inner templates -- it's not about independent markup!

### 3. Write down the schema of your view

In any of your views, e.g. `views/index.gsp`, you may use `<infra:build/>` tag.

```groovy

<infra:build schema="${{

  'my-block'(modifierKey: 'modifierValue', _:[title:"it's a model map"], _attrs:[onclick:"alert('root!')"]) {
  
      'my-block'(position: 'top') // modifier is .m-my-block_position_top
      
      for(i in 0..10) { // you should insert all structure logic there
        'my-another'() // to insert a block without params, don't forget to use parenthes!
      }
      
        g.link(url:"/", "tet") // all grails tags works ok
        
        cache.cache(key: 'cache key') {
          'my-another'() // this should also work
        }

    }
    
}}"/>

```

### 4. Make blocks from another blocks

You may include any block into another with `<infra:include block="block-name"/>` tag.

### 5. Enjoy!

Now you have page structure separated from markup in such a way it's easy to manage both to markup engineers and developers.


Why BEM
--------------------------

BEM (acronym of Block-Element-Modifier) is an advanced markup management and UI development technique invented and
proved in Yandex. It offers several advantages:

- Independent markup blocks, fully reuseable and making no impact on each other (css inheritance is prohibited!)
- Structure of markup and layout is represented in simple and clean DSL
- Markup engineers may provide developers with a ready-to-use blocks API
- Developers have a single point of control of page data and logic
- When project is getting larger, you just write new blocks. No more messing markup with millions `<g:if>`s and so on.

Tags
--------------------------

### infra:bemAttrs

Generates all attributes for a BEM block for its name. Probably you will never type it -- it's generated in a block template with a script.

Example:
```
<div<infra:bemAttrs for='root'/>>
    <h1 class="b-root__title">${title}</h1>
    <infra:unescape var="${body()}"/>
</div>
```

### infra:include

Hard uses another block in a current block. Attributes are:

- _block_ -- a block name to include, e.g. 'my-block'
- _modifiers_ -- a map of modifiers given to a target block, e.g. `modifiers="[position: 'top', color: 'pink']"`
- _model_ -- a model given to a target template. Example: `model="[title:'my title']"`
- _attrs_ -- a map of custom block attributes -- for js etc.

### infra:build

Builds a page (or a part of a page) by DSL schema. The only attribute of `infra:build` is a `schema` closure with
DSL describing the page structure.

### infra:unescape

If a variable you render is displayed in html-encoded form for an incredible reason, use this tag to render it as is.

- _var_ -- a data piece to render.


Schema
--------------------------

The DSL describing page schema defines a number of quite simple rules.

- All method calls are mapped to BEM blocks
- Last argument is closure -- equivalent to `body` in GSP tags
- First argument is a map with modifiers, model and custom attributes
- Namespaced method calls are mapped to Grails tags

Map argument in block call in a schema by default is passed to modifiers:

`'my-block'(top:'left')` will be translated to something like `<div class="b-my-block m-my-block_top_left"></div>`

`_` key is mapped to a template model.

`_attrs` key provides a map of custom attributes. For example, `_attrs:['ng-click':'alert("this!")']` will be translated
into Angular.js `ng-click` directive attribute.

You may (and you should) use inheritance in page schemas. You should pass all your logic, iterations, caching, all ifs
and elses to schema and not to markup blocks themselves.

Full schema for a page may be huge. But it's controllable. It's easy to read and understand all the fuzzy choses you
make during the render process.

Scripts
--------------------------

### bem-block

It's an entry point to a plugin. Generates block resources for a block name. Notice: `-` is an encapsulation symbol,
and it's translated to path separator.

Usage example:

`grails bem-block news-footer-comment`

`grails bB`


Changelog
--------------------------

- 0.1 Initial release

Roadmap
--------------------------

- 0.2 Separate views DSL files
- 0.3 GSP tag adapters
- 0.4 Velocity adapter, all block resources on a single page
- 0.5 Resources preprocessing
- 1.0 Stable release / GA
