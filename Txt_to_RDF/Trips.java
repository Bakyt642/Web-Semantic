import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import org.apache.jena.atlas.csv.CSVParser;
import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.rdf.model.*;
import org.apache.jena.vocabulary.RDF;

public class Trips {

    public static final String _LANG_FR = "fr"; // The language of the station names
    public static final String _INPUT_FILE_NAME = "D:/study/semantic_web/semwebproject/ter_txt_files/trips.txt"; // The input file name
    public static final String _OUTPUT_FILE_NAME = "D:/study/semantic_web/semwebproject/rdf_files/trips.rdf"; // The output file name
    public static final String _OUTPUT_FORMAT = "Turtle"; // The output format
    public static final String _UTF_8 = "UTF-8"; // Character encoding

    // Namespaces of the vocabularies we use
    public static final String _EX_NS = "http://example.com/stations/";
    public static final String _GTFS_NS = "http://vocab.gtfs.org/terms#";

    // Prefixes
    public static final String _EX_PREFIX = "ex";
    public static final String _GTFS_PREFIX = "gtfs";

    // THE IRIs of the geo: terms we are using
    public static final String _GTFS_TRIP = "http://vocab.gtfs.org/terms#Trip";
    public static final String _GTFS_ROUTE = "http://vocab.gtfs.org/terms#route";
    public static final String _GTFS_SERVICE = "http://vocab.gtfs.org/terms#service";
    public static final String _GTFS_HEADSIGN = "http://vocab.gtfs.org/terms#headsign";
    public static final String _GTFS_DIRECTION = "http://vocab.gtfs.org/terms#direction";

    public static void main(String[] args) {
        org.apache.log4j.Logger.getRootLogger().setLevel(org.apache.log4j.Level.OFF);
        // Starts with an empty Jena model
        Model stationGraph = ModelFactory.createDefaultModel();

        // Define prefixes for pretty output
        stationGraph.setNsPrefix(_EX_PREFIX, _EX_NS);
        stationGraph.setNsPrefix(_GTFS_PREFIX, _GTFS_NS);

        // We initialise two properties and one resource from the geo: vocabulary
        Resource gtfs_Trip = stationGraph.createResource(_GTFS_TRIP);
        Property gtfs_route = stationGraph.createProperty(_GTFS_ROUTE);
        Property gtfs_service = stationGraph.createProperty(_GTFS_SERVICE);
        Property gtfs_headsign = stationGraph.createProperty(_GTFS_HEADSIGN);
        Property gtfs_direction = stationGraph.createProperty(_GTFS_DIRECTION);

        // Initialise a CSV parser
        CSVParser parser = CSVParser.create(_INPUT_FILE_NAME);

        // Skip the first line that contains headers
        parser.parse1();

        // For each line, we will generate some triples with the same subject
        for (List<String> line : parser) {

            //Store all what we need in Strings
            String route_id = line.get(0); // first column
            String service_id = line.get(1);
            String trip_id = line.get(2);
            String trip_headsign = line.get(3);
            String direction_id = line.get(4);

            // We have to catch exceptions
            try {

                // Make a new IRI for the station described in this line
                String trip_iri = _EX_NS + URLEncoder.encode(trip_id, _UTF_8);
                String ex_route_id = _EX_NS + route_id;
                String ex_service_id = _EX_NS + service_id;

                // Create the resource that corresponds to the station
                Resource trip = stationGraph.createResource(trip_iri);
                Resource routeid = stationGraph.createResource(ex_route_id);
                Resource serviceid = stationGraph.createResource(ex_service_id);

                String direction = "";
                if (direction_id.equals("1")) {
                    direction = "true";
                } else {
                    direction = "false";
                }

                trip.addProperty(RDF.type, gtfs_Trip);
                trip.addProperty(gtfs_route, routeid);
                trip.addProperty(gtfs_service, serviceid);
                trip.addProperty(gtfs_headsign, trip_headsign, XSDDatatype.XSDstring);
                trip.addProperty(gtfs_direction, direction, XSDDatatype.XSDboolean);

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
