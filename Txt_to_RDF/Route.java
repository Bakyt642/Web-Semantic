import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import org.apache.jena.atlas.csv.CSVParser;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;

public class Route {

    public static final String _LANG_FR = "fr"; // The language of the station names
    public static final String _INPUT_FILE_NAME = "D:/study/semantic_web/semwebproject/ter_txt_files/routes.txt"; // The input file name
    public static final String _OUTPUT_FILE_NAME = "D:/study/semantic_web/semwebproject/rdf_files/routes.rdf"; // The output file name
    public static final String _OUTPUT_FORMAT = "Turtle"; // The output format
    public static final String _UTF_8 = "UTF-8"; // Character encoding

    // Namespaces of the vocabularies we use
    public static final String _EX_NS = "http://example.com/stations/";
    public static final String _GTFS_NS = "http://vocab.gtfs.org/terms#";

    // Prefixes
    public static final String _EX_PREFIX = "ex";
    public static final String _GTFS_PREFIX = "gtfs";

    // THE IRIs of the geo: terms we are using
    public static final String _GTFS_ROUTE = "http://vocab.gtfs.org/terms#Route";
    public static final String _GTFS_ROUTETYPE = "http://vocab.gtfs.org/terms#routeType";
    public static final String _GTFS_AGENCY = "http://vocab.gtfs.org/terms#agency";
    public static final String _GTFS_LONGNAME = "http://vocab.gtfs.org/terms#longName";
    public static final String _GTFS_SHORTNAME = "http://vocab.gtfs.org/terms#shortName";


    public static void main(String[] args) {
        org.apache.log4j.Logger.getRootLogger().setLevel(org.apache.log4j.Level.OFF);
        // Starts with an empty Jena model
        Model stationGraph = ModelFactory.createDefaultModel();

        // Define prefixes for pretty output
        stationGraph.setNsPrefix(_EX_PREFIX, _EX_NS);
        stationGraph.setNsPrefix(_GTFS_PREFIX, _GTFS_NS);

        // We initialise two properties and one resource from the geo: vocabulary
        Resource gtfs_Route = stationGraph.createResource(_GTFS_ROUTE);
        Property gtfs_routeType = stationGraph.createProperty(_GTFS_ROUTETYPE);
        Property gtfs_agency = stationGraph.createProperty(_GTFS_AGENCY);
        Property gtfs_longName = stationGraph.createProperty(_GTFS_LONGNAME);
        Property gtfs_shortName = stationGraph.createProperty(_GTFS_SHORTNAME);

        // Initialise a CSV parser
        CSVParser parser = CSVParser.create(_INPUT_FILE_NAME);

        // Skip the first line that contains headers
        parser.parse1();

        // For each line, we will generate some triples with the same subject
        for (List<String> line : parser) {

            //Store all what we need in Strings
            String route_id = line.get(0); // first column
            String agency_id = line.get(1);
            String route_long_name = line.get(3);
            String route_type = line.get(5);

            // We have to catch exceptions
            try {
                String route_type_desc;
                switch (route_type) {
                    case "0":
                        route_type_desc = "LightRail";
                        break;
                    case "1":
                        route_type_desc = "Subway";
                        break;
                    case "2":
                        route_type_desc = "Rail";
                        break;
                    case "3":
                        route_type_desc = "Bus";
                        break;
                    case "4":
                        route_type_desc = "Ferry";
                        break;
                    case "5":
                        route_type_desc = "CableCar";
                        break;
                    case "6":
                        route_type_desc = "Gondola";
                        break;
                    case "7":
                        route_type_desc = "Funicular";
                        break;
                    default:
                        route_type_desc = "";
                        break;
                }

                // Make a new IRI for the station described in this line
                String route_iri = _EX_NS + URLEncoder.encode(route_id, _UTF_8);
                String ex_agency_id = _EX_NS + agency_id;
                String gtfs_route_type_desc = _GTFS_NS + route_type_desc;

                // Create the resource that corresponds to the station
                Resource route = stationGraph.createResource(route_iri);
                Resource agency = stationGraph.createResource(ex_agency_id);
                Resource route_type_r = stationGraph.createResource(gtfs_route_type_desc);

                // Add a type to the station
                route.addProperty(RDF.type, gtfs_Route);

                // Add latitude and longitude of the station
                route.addProperty(gtfs_agency, agency);
                route.addProperty(gtfs_longName, route_long_name);
                route.addProperty(gtfs_routeType, route_type_r);

            } catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        // We have to catch I/O exceptions in case the file is not writable
        try {

            // An output stream to save the generated RDF graph
            OutputStream output = new FileOutputStream(new File(_OUTPUT_FILE_NAME));

            // Save the RDF graph to a file
            stationGraph.write(output, _OUTPUT_FORMAT);

        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

}