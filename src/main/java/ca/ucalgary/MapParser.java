package ca.ucalgary;

import ca.ucalgary.mapgraph.Intersection;
import ca.ucalgary.mapgraph.MapGraph;
import ca.ucalgary.mapgraph.Way;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class MapParser extends DefaultHandler {

    private static final SAXParserFactory factory = SAXParserFactory.newInstance();
    private final SAXParser parser;
    private MapGraph mapGraph;
    private Way currentWay;

    private HashMap<String, Intersection> nodeTable = new HashMap<>(1024 * 4);


    public MapParser() throws ParserConfigurationException, SAXException {
        parser = factory.newSAXParser();
    }

    public synchronized MapGraph parse(File mapFile) throws IOException, SAXException {
        mapGraph = new MapGraph();
        currentWay = null;
        nodeTable.clear();
        parser.parse(mapFile, this);
        mapGraph.setIntersections(nodeTable.values());
        return mapGraph;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        switch (qName) {
            case "node":
                String id = attributes.getValue("id");
                double lon = Double.parseDouble(attributes.getValue("lon"));
                double lat = Double.parseDouble(attributes.getValue("lat"));
                nodeTable.put(id, new Intersection(lat, lon));
                break;

            case "way":
                currentWay = new Way();
                break;

            case "nd":
                String ref = attributes.getValue("ref");
                Intersection intersection = nodeTable.get(ref);
                currentWay.addIntersection(intersection);
                break;

            case "bounds":
                double x = Double.parseDouble(attributes.getValue("minlon"));
                double y = Double.parseDouble(attributes.getValue("minlat"));
                double w = Double.parseDouble(attributes.getValue("maxlon")) - x;
                double h = Double.parseDouble(attributes.getValue("maxlat")) - y;
                mapGraph.setBounds(new Rectangle2D.Double(x, y, w, h));
                break;

            case "tag":
                String k = attributes.getValue("k");
                if (currentWay!= null) {
                    String v = attributes.getValue("v");
                    if (k.equals("highway")) {
                        currentWay.setType(v);
                    } else if (k.equals("railway")) {
                        currentWay.setType(v.startsWith("rail") || v.endsWith("rail") ? v : "rail_" + v);
                    }
                }

                break;
        }

    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (qName.equals("way")) {
            mapGraph.addWay(currentWay);
        }
    }

    public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException {
        MapGraph parseResult = new MapParser().parse(new File("maps/calgary.xml"));
        System.out.println("Bound: " + parseResult.getBounds());
        System.out.println("Ways: " + parseResult.getWays().size());

        parseResult.edges().forEach(System.out::println);
    }
}
