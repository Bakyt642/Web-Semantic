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

public class StopTimes {

    public static final String _LANG_FR = "fr"; // The language of the station names
    public static final String _INPUT_FILE_NAME = "D:/study/semantic_web/semwebproject/ter_txt_files/stop_times.txt"; // The input file name
    public static final String _OUTPUT_FILE_NAME = "D:/study/semantic_web/semwebproject/rdf_files/stop_times.rdf"; // The output file name
    public static final String _OUTPUT_FORMAT = "Turtle"; // The output format
    public static final String _UTF_8 = "UTF-8"; // Character encoding

    // Namespaces of the vocabularies we use
    public static final String _EX_NS = "http://example.com/stations/";
    public static final String _GTFS_NS = "http://vocab.gtfs.org/terms#";
    public static final String _RDFS_NS = "http://www.w3.org/2000/01/rdf-schema#";
    public static final String _XSD_NS = "http://www.w3.org/2001/XMLSchema#";

    // Prefixes
    public static final String _EX_PREFIX = "ex";
    public static final String _GTFS_PREFIX = "gtfs";
    public static final String _RDFS_PREFIX = "rdfs";
    public static final String _XSD_PREFIX = "xsd";

    // THE IRIs of the geo: terms we are using
    public static final String _GTFS_TRIP = "http://vocab.gtfs.org/terms#trip";
    public static final String _GTFS_STOPTIME = "http://vocab.gtfs.org/terms#StopTime";
    public static final String _GTFS_ARRIVALTIME = "http://vocab.gtfs.org/terms#arrivalTime";
    public static final String _GTFS_DEPARTURETIME = "http://vocab.gtfs.org/terms#departureTime";
    public static final String _GTFS_STOP = "http://vocab.gtfs.org/terms#stop";
    public static final String _GTFS_STOPSEQUENCE = "http://vocab.gtfs.org/terms#stopSequence";
    public static final String _GTFS_PICKUPTYPE = "http://vocab.gtfs.org/terms#pickupType";
    public static final String _GTFS_DROPOFFTYPE = "http://vocab.gtfs.org/terms#dropOffType";
    public static final String _GTFS_REGULAR = "http://vocab.gtfs.org/terms#Regular";

    public static void main(String[] args) {
        org.apache.log4j.Logger.getRootLogger().setLevel(org.apache.log4j.Level.OFF);
        // Starts with an empty Jena model
        Model stationGraph = ModelFactory.createDefaultModel();

        // Define prefixes for pretty output
        stationGraph.setNsPrefix(_EX_PREFIX, _EX_NS);
        stationGraph.setNsPrefix(_GTFS_PREFIX, _GTFS_NS);
        stationGraph.setNsPrefix(_RDFS_PREFIX,_RDFS_NS);
        stationGraph.setNsPrefix(_XSD_PREFIX,_XSD_NS);

        // We initialise two properties and one resource from the geo: vocabulary
        Resource gtfs_StopTime = stationGraph.createResource(_GTFS_STOPTIME);
        Property gtfs_trip = stationGraph.createProperty(_GTFS_TRIP);
        Property gtfs_arrivalTime = stationGraph.createProperty(_GTFS_ARRIVALTIME);
        Property gtfs_departureTime = stationGraph.createProperty(_GTFS_DEPARTURETIME);
        Property gtfs_stop = stationGraph.createProperty(_GTFS_STOP);
        Property gtfs_stopSequence = stationGraph.createProperty(_GTFS_STOPSEQUENCE);
        Property gtfs_pickupType = stationGraph.createProperty(_GTFS_PICKUPTYPE);
        Property gtfs_dropOffType = stationGraph.createProperty(_GTFS_DROPOFFTYPE);
        Property gtfs_Regular = stationGraph.createProperty(_GTFS_REGULAR);

        // Initialise a CSV parser
        CSVParser parser = CSVParser.create(_INPUT_FILE_NAME);

        // Skip the first line that contains headers
        parser.parse1();

        // For each line, we will generate some triples with the same subject
        for (List<String> line : parser) {

            //Store all what we need in Strings
            String trip_id = line.get(0); // first column
            String arrival_time = line.get(1);
            String departure_time = line.get(2);
            String stop_id = line.get(3);
            String stop_sequence = line.get(4);
            String pickup_type = line.get(6);
            String drop_off_type = line.get(7);

            // We have to catch exceptions
            try {

                // Make a new IRI for the station described in this line
                String stop_id_iri = _EX_NS + URLEncoder.encode(stop_id,_UTF_8);
                String trip_id_iri = _EX_NS + trip_id;

                // Create the resource that corresponds to the station
                Resource node = stationGraph.createResource();
                Resource stop_id_r = stationGraph.createResource(stop_id_iri);
                Resource trip_id_r = stationGraph.createResource(trip_id_iri);

                node.addProperty(RDF.type, gtfs_StopTime);
                node.addProperty(gtfs_stop, stop_id_r);
                node.addProperty(gtfs_trip, trip_id_r);
                node.addProperty(gtfs_arrivalTime, arrival_time, XSDDatatype.XSDtime);
                node.addProperty(gtfs_departureTime, departure_time, XSDDatatype.XSDtime);
                node.addProperty(gtfs_stopSequence, stop_sequence, XSDDatatype.XSDnonNegativeInteger);
                node.addProperty(gtfs_pickupType, gtfs_Regular);
                node.addProperty(gtfs_dropOffType, gtfs_Regular);

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
