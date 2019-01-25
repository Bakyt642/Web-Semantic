import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.jena.atlas.csv.CSVParser;
import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.rdf.model.*;
import org.apache.jena.vocabulary.RDF;

public class Calendar {

    public static final String _LANG_FR = "fr"; // The language of the station names
    public static final String _INPUT_FILE_NAME = "D:/study/semantic_web/semwebproject/ter_txt_files/calendar.txt"; // The input file name
    public static final String _OUTPUT_FILE_NAME = "D:/study/semantic_web/semwebproject/rdf_files/calendar.rdf"; // The output file name
    public static final String _OUTPUT_FORMAT = "Turtle"; // The output format
    public static final String _UTF_8 = "UTF-8"; // Character encoding

    // Namespaces of the vocabularies we use
    public static final String _EX_NS = "http://example.com/stations/";
    public static final String _GTFS_NS = "http://vocab.gtfs.org/terms#";
    public static final String _RDFS_NS = "http://www.w3.org/2000/01/rdf-schema#";
    public static final String _XSD_NS = "http://www.w3.org/2001/XMLSchema#";
    public static final String _DCT_NS = "http://purl.org/dc/terms/";
    public static final String _SCHEMA_NS = "http://schema.org/";

    // Prefixes
    public static final String _EX_PREFIX = "ex";
    public static final String _GTFS_PREFIX = "gtfs";
    public static final String _RDFS_PREFIX = "rdfs";
    public static final String _XSD_PREFIX = "xsd";
    public static final String _DCT_PREFIX = "dct";
    public static final String _SCHEMA_PREFIX = "schema";

    // THE IRIs of the geo: terms we are using
    public static final String _GTFS_CALENDARRULE = "http://vocab.gtfs.org/terms#CalendarRule";
    public static final String _GTFS_MONDAY = "http://vocab.gtfs.org/terms#monday";
    public static final String _GTFS_TUESDAY = "http://vocab.gtfs.org/terms#tuesday";
    public static final String _GTFS_WEDNESDAY = "http://vocab.gtfs.org/terms#wednesday";
    public static final String _GTFS_THURSDAY = "http://vocab.gtfs.org/terms#thursday";
    public static final String _GTFS_FRIDAY = "http://vocab.gtfs.org/terms#friday";
    public static final String _GTFS_SATURDAY = "http://vocab.gtfs.org/terms#saturday";
    public static final String _GTFS_SUNDAY = "http://vocab.gtfs.org/terms#sunday";
    public static final String _DCT_TEMPORAL = "http://purl.org/dc/terms/temporal";
    public static final String _DCT_PERIODOFTIME = "http://purl.org/dc/terms/PeriodOfTime";
    public static final String _SCHEMA_STARTDATE = "http://schema.org/startDate";
    public static final String _SCHEMA_ENDDATE = "http://schema.org/endDate";

    public static void main(String[] args) {
        org.apache.log4j.Logger.getRootLogger().setLevel(org.apache.log4j.Level.OFF);
        // Starts with an empty Jena model
        Model stationGraph = ModelFactory.createDefaultModel();

        // Define prefixes for pretty output
        stationGraph.setNsPrefix(_EX_PREFIX, _EX_NS);
        stationGraph.setNsPrefix(_GTFS_PREFIX, _GTFS_NS);
        stationGraph.setNsPrefix(_RDFS_PREFIX, _RDFS_NS);
        stationGraph.setNsPrefix(_XSD_PREFIX, _XSD_NS);
        stationGraph.setNsPrefix(_DCT_PREFIX, _DCT_NS);
        stationGraph.setNsPrefix(_SCHEMA_PREFIX, _SCHEMA_NS);

        // We initialise two properties and one resource from the geo: vocabulary
        Resource gtfs_CalendarRule = stationGraph.createResource(_GTFS_CALENDARRULE);
        Property gtfs_monday = stationGraph.createProperty(_GTFS_MONDAY);
        Property gtfs_tuesday = stationGraph.createProperty(_GTFS_TUESDAY);
        Property gtfs_wednesday = stationGraph.createProperty(_GTFS_WEDNESDAY);
        Property gtfs_thursday = stationGraph.createProperty(_GTFS_THURSDAY);
        Property gtfs_friday = stationGraph.createProperty(_GTFS_FRIDAY);
        Property gtfs_saturday = stationGraph.createProperty(_GTFS_SATURDAY);
        Property gtfs_sunday = stationGraph.createProperty(_GTFS_SUNDAY);
        Property dct_temporal = stationGraph.createProperty(_DCT_TEMPORAL);
        Property dct_PeriodOfTime = stationGraph.createProperty(_DCT_PERIODOFTIME);
        Property schema_startDate = stationGraph.createProperty(_SCHEMA_STARTDATE);
        Property schema_endDate = stationGraph.createProperty(_SCHEMA_ENDDATE);

        // Initialise a CSV parser
        CSVParser parser = CSVParser.create(_INPUT_FILE_NAME);

        // Skip the first line that contains headers
        parser.parse1();

        // For each line, we will generate some triples with the same subject
        for (List<String> line : parser) {

            //Store all what we need in Strings
            String service_id = line.get(0); // first column
            String monday = line.get(1);
            String tuesday = line.get(2);
            String wednesday = line.get(3);
            String thursday = line.get(4);
            String friday = line.get(5);
            String saturday = line.get(6);
            String sunday = line.get(7);
            String start_date = line.get(8);
            String end_date = line.get(9);

            // We have to catch exceptions
            try {
                // Make a new IRI for the station described in this line
                String service_id_iri = _EX_NS + URLEncoder.encode(service_id, _UTF_8);

                // Create the resource that corresponds to the station
                Resource service = stationGraph.createResource(service_id_iri);

                service.addProperty(RDF.type, gtfs_CalendarRule);
                service.addProperty(gtfs_monday, getBoolean(monday), XSDDatatype.XSDboolean);
                service.addProperty(gtfs_tuesday, getBoolean(tuesday), XSDDatatype.XSDboolean);
                service.addProperty(gtfs_wednesday, getBoolean(wednesday), XSDDatatype.XSDboolean);
                service.addProperty(gtfs_thursday, getBoolean(thursday), XSDDatatype.XSDboolean);
                service.addProperty(gtfs_friday, getBoolean(friday), XSDDatatype.XSDboolean);
                service.addProperty(gtfs_saturday, getBoolean(saturday), XSDDatatype.XSDboolean);
                service.addProperty(gtfs_sunday, getBoolean(sunday), XSDDatatype.XSDboolean);

                String defis = "-";

                String newStartDate = start_date.substring(0, 4) + defis + start_date.substring(4, 6) + defis + start_date.substring(6, 8);
                String newEndDate = end_date.substring(0, 4) + defis + end_date.substring(4, 6) + defis + end_date.substring(6, 8);

                service.addProperty(schema_startDate, newStartDate, XSDDatatype.XSDdate);
                service.addProperty(schema_endDate, newEndDate, XSDDatatype.XSDdate);

//                Resource node = stationGraph.createResource();
//
//                node.addProperty(RDF.type, dct_PeriodOfTime);
//                node.addProperty(schema_startDate, start_date, XSDDatatype.XSDdate);
//                node.addProperty(schema_endDate, end_date, XSDDatatype.XSDdate);
//
//                service.addProperty(dct_temporal, node);
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

    public static String getBoolean(String day){
        if (day.equals("1")) {
            return "true";
        } else {
            return "false";
        }
    }

}
