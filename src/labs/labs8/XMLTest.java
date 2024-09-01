package labs.labs8;

import java.util.*;

interface XMLComponent{
    void addAttribute(String a,String b);
    String print(int indent);
}
abstract class XMLElement implements XMLComponent{
    protected String type;
    protected Map<String,String> attributesMap;

    public XMLElement(String type) {
        this.type = type;
        attributesMap=new LinkedHashMap<>();
    }

    @Override
    public void addAttribute(String a, String b) {
        attributesMap.put(a,b);
    }
}
class XMLLeaf extends XMLElement{
    private final String value;

    public XMLLeaf(String type, String value) {
        super(type);
        this.value = value;
    }

    @Override
    public String print(int indent) {
        StringBuilder sb=new StringBuilder();
        sb.append("\t".repeat(Math.max(0, indent)));
        sb.append("<").append(type);
        for(Map.Entry<String,String>entry:attributesMap.entrySet()){
            sb.append(String.format(" %s=\"%s\"",entry.getKey(),entry.getValue()));
        }
        sb.append(">").append(value).append("</").append(type).append(">");
        return sb.toString();
    }
}
class XMLComposite extends XMLElement {
    private final List<XMLComponent> xmlChildrenElements;

    public XMLComposite(String type) {
        super(type);
        this.xmlChildrenElements = new ArrayList<>();
    }

    @Override
    public String print(int indent) {
        StringBuilder sb = new StringBuilder();
        String tabs = "\t".repeat(indent);

        sb.append(tabs).append("<").append(type);
        for (Map.Entry<String, String> entry : attributesMap.entrySet()) {
            sb.append(String.format(" %s=\"%s\"", entry.getKey(), entry.getValue()));
        }
        sb.append(">\n");

        for (XMLComponent child : xmlChildrenElements) {
            sb.append(child.print(indent + 1)).append("\n");
        }

        sb.append(tabs).append("</").append(type).append(">");

        return sb.toString();
    }

    public void addComponent(XMLComponent xmlComponent) {
        xmlChildrenElements.add(xmlComponent);
    }
}





public class XMLTest {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int testCase = sc.nextInt();
        XMLComponent component = new XMLLeaf("student", "Trajce Trajkovski");
        component.addAttribute("type", "redoven");
        component.addAttribute("program", "KNI");

        XMLComposite composite = new XMLComposite("name");
        composite.addComponent(new XMLLeaf("first-name", "trajce"));
        composite.addComponent(new XMLLeaf("last-name", "trajkovski"));
        composite.addAttribute("type", "redoven");
        component.addAttribute("program", "KNI");

        if (testCase==1) {
            //TODO Print the component object
            System.out.println(component.print(0));
        } else if(testCase==2) {
            //TODO print the composite object
            System.out.println(composite.print(0));
        } else if (testCase==3) {
            XMLComposite main = new XMLComposite("level1");
            main.addAttribute("level","1");
            XMLComposite lvl2 = new XMLComposite("level2");
            lvl2.addAttribute("level","2");
            XMLComposite lvl3 = new XMLComposite("level3");
            lvl3.addAttribute("level","3");
            lvl3.addComponent(component);
            lvl2.addComponent(lvl3);
            lvl2.addComponent(composite);
            lvl2.addComponent(new XMLLeaf("something", "blabla"));
            main.addComponent(lvl2);
            main.addComponent(new XMLLeaf("course", "napredno programiranje"));

            //TODO print the main object
            System.out.println(main.print(0));
        }
    }
}
