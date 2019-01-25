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
import org.apache.jena.sparql.vocabulary.FOAF;
import org.apache.jena.vocabulary.RDF;

public class Stops {

    public static final String _LANG_FR = "fr"; // The language of the station names
    public static final String _INPUT_FILE_NAME = "D:/study/semantic_web/semwebproject/ter_txt_files/stops.txt"; // The input file name
    public static final String _OUTPUT_FILE_NAME = "D:/study/semantic_web/semwebproject/rdf_files/stops.rdf"; // The output file name
    public static final String _OUTPUT_FORMAT = "Turtle"; // The output format
    public static final String _UTF_8 = "UTF-8"; // Character encoding

    // Namespaces of the vocabularies we use
    public static final String _EX_NS = "http://example.com/stations/";
    public static final String _GTFS_NS = "http://vocab.gtfs.org/terms#";
    public static final String _GEO_NS = "http://www.w3.org/2003/01/geo/wgs84_pos#";
    public static final String _FOAF_NS = "http://xmlns.com/foaf/0.1/";
    public static final String _XSD_NS = "http://www.w3.org/2001/XMLSchema#";

    // Prefixes
    public static final String _EX_PREFIX = "ex";
    public static final String _GTFS_PREFIX = "gtfs";
    public static final String _GEO_PREFIX = "geo";
    public static final String _FOAF_PREFIX = "foaf";
    public static final String _XSD_PREFIX = "xsd";

    // THE IRIs of the geo: terms we are using
    public static final String _GTFS_STOP = "http://vocab.gtfs.org/terms#Stop";
    public static final String _GTFS_STATION = "http://vocab.gtfs.org/terms#Station";
    public static final String _GTFS_PARENTSTATION = "http://vocab.gtfs.org/terms#parentStation";
    public static final String _GEO_LAT = "http://www.w3.org/2003/01/geo/wgs84_pos#lat";
    public static final String _GEO_LONG = "http://www.w3.org/2003/01/geo/wgs84_pos#long";

    public static void main(String[] args) {
        org.apache.log4j.Logger.getRootLogger().setLevel(org.apache.log4j.Level.OFF);
        // Starts with an empty Jena model
        Model stationGraph = ModelFactory.createDefaultModel();

        // Define prefixes for pretty output
        stationGraph.setNsPrefix(_EX_PREFIX, _EX_NS);
        stationGraph.setNsPrefix(_GTFS_PREFIX, _GTFS_NS);
        stationGraph.setNsPrefix(_GEO_PREFIX, _GEO_NS);
        stationGraph.setNsPrefix(_FOAF_PREFIX, _FOAF_NS);
        stationGraph.setNsPrefix(_XSD_PREFIX, _XSD_NS);

        // We initialise two properties and one resource from the geo: vocabulary
        Resource gtfs_Stop = stationGraph.createResource(_GTFS_STOP);
        Property geo_long = stationGraph.createProperty(_GEO_LONG);
        Property geo_lat = stationGraph.createProperty(_GEO_LAT);
        Property gtfs_parentStation = stationGraph.createProperty(_GTFS_PARENTSTATION);
        Property gtfs_Station = stationGraph.createProperty(_GTFS_STATION);

        // Initialise a CSV parser
        CSVParser parser = CSVParser.create(_INPUT_FILE_NAME);

        // Skip the first line that contains headers
        parser.parse1();

        // For each line, we will generate some triples with the same subject
        for (List<String> line : parser) {

            //Store all what we need in Strings
            String stop_id = line.get(0); // first column
            String stop_name = line.get(1);
            String stop_lat = line.get(3);
            String stop_long = line.get(4);
            String location_type = line.get(7);
            String parent_station = line.get(8);

            // We have to catch exceptions
            try {

                // Make a new IRI for the station described in this line

                String stop_iri = _EX_NS + URLEncoder.encode(stop_id, _UTF_8);

                // Create the resource that corresponds to the station
                Resource stop = stationGraph.createResource(stop_iri);

                if (location_type.equals("1")) {
                    stop.addProperty(RDF.type, gtfs_Station);
                    stop.addProperty(FOAF.name, stop_name, _LANG_FR);
                    stop.addProperty(geo_lat, stop_lat, XSDDatatype.XSDfloat);
                    stop.addProperty(geo_long, stop_long, XSDDatatype.XSDfloat);
                } else {
                    stop.addProperty(RDF.type, gtfs_Stop);
                    stop.addProperty(FOAF.name, stop_name, _LANG_FR);
                    stop.addProperty(geo_lat, stop_lat, XSDDatatype.XSDfloat);
                    stop.addProperty(geo_long, stop_long, XSDDatatype.XSDfloat);
                    stop.addProperty(gtfs_parentStation, parent_station, XSDDatatype.XSDstring);
                }

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
