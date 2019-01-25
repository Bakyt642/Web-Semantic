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

public class CalendarDates {

    public static final String _LANG_FR = "fr"; // The language of the station names
    public static final String _INPUT_FILE_NAME = "D:/study/semantic_web/semwebproject/ter_txt_files/calendar_dates.txt"; // The input file name
    public static final String _OUTPUT_FILE_NAME = "D:/study/semantic_web/semwebproject/rdf_files/calendar_dates.rdf"; // The output file name
    public static final String _OUTPUT_FORMAT = "Turtle"; // The output format
    public static final String _UTF_8 = "UTF-8"; // Character encoding

    // Namespaces of the vocabularies we use
    public static final String _EX_NS = "http://example.com/stations/";
    public static final String _GTFS_NS = "http://vocab.gtfs.org/terms#";
    public static final String _RDFS_NS = "http://www.w3.org/2000/01/rdf-schema#";
    public static final String _XSD_NS = "http://www.w3.org/2001/XMLSchema#";
    public static final String _DCT_NS = "http://purl.org/dc/terms/";

    // Prefixes
    public static final String _EX_PREFIX = "ex";
    public static final String _GTFS_PREFIX = "gtfs";
    public static final String _RDFS_PREFIX = "rdfs";
    public static final String _XSD_PREFIX = "xsd";
    public static final String _DCT_PREFIX = "dct";

    // THE IRIs of the geo: terms we are using
    public static final String _GTFS_CALENDARDATERULE = "http://vocab.gtfs.org/terms#CalendarDateRule";
    public static final String _GTFS_SERVICE = "http://vocab.gtfs.org/terms#service";
    public static final String _DCT_DATE = "http://purl.org/dc/terms/date";
    public static final String _GTFS_DATEADDITION = "http://vocab.gtfs.org/terms#dateAddition";

    public static void main(String[] args) {
        org.apache.log4j.Logger.getRootLogger().setLevel(org.apache.log4j.Level.OFF);
        // Starts with an empty Jena model
        Model stationGraph = ModelFactory.createDefaultModel();

        // Define prefixes for pretty output
        stationGraph.setNsPrefix(_EX_PREFIX, _EX_NS);
        stationGraph.setNsPrefix(_GTFS_PREFIX, _GTFS_NS);
        stationGraph.setNsPrefix(_RDFS_PREFIX,_RDFS_NS);
        stationGraph.setNsPrefix(_XSD_PREFIX,_XSD_NS);
        stationGraph.setNsPrefix(_DCT_PREFIX,_DCT_NS);

        // We initialise two properties and one resource from the geo: vocabulary
        Resource gtfs_CalendarDateRule = stationGraph.createResource(_GTFS_CALENDARDATERULE);
        Property gtfs_service = stationGraph.createProperty(_GTFS_SERVICE);
        Property gtfs_dateAddition = stationGraph.createProperty(_GTFS_DATEADDITION);
        Property dct_date = stationGraph.createProperty(_DCT_DATE);

        // Initialise a CSV parser
        CSVParser parser = CSVParser.create(_INPUT_FILE_NAME);

        // Skip the first line that contains headers
        parser.parse1();

        // For each line, we will generate some triples with the same subject
        for (List<String> line : parser) {

            //Store all what we need in Strings
            String service_id = line.get(0); // first column
            String date = line.get(1);
            String exception_type = line.get(2);

            // We have to catch exceptions
//            try {
                String dateAddition = "";
                if (exception_type.equals("1")) {
                    dateAddition = "true";
                } else {
                    dateAddition = "false";
                }

                // Make a new IRI for the station described in this line
                String service_id_iri = _EX_NS + service_id;

                // Create the resource that corresponds to the station
                Resource node = stationGraph.createResource();
                Resource service_id_r = stationGraph.createResource(service_id_iri);

                node.addProperty(RDF.type, gtfs_CalendarDateRule);
                node.addProperty(gtfs_service, service_id_r);
                node.addProperty(dct_date, date, XSDDatatype.XSDdate);
                node.addProperty(gtfs_dateAddition, dateAddition, XSDDatatype.XSDboolean);

//            } catch (UnsupportedEncodingException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            }
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
