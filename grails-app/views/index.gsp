<%--
 * @author alari
 * @since 2/5/13 10:25 AM
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
  <title></title>
</head>
<body>

<infra:build schema="${{


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


}}"/>

</body>
</html>
