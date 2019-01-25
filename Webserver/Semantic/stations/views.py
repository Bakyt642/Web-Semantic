from django.shortcuts import render
from django.http import HttpResponse
from SPARQLWrapper import SPARQLWrapper, JSON
from django.template import loader
import requests

from geopy.geocoders import Nominatim


sparql = SPARQLWrapper("http://localhost:3030/11min/sparql")
sparql2 = SPARQLWrapper("http://localhost:3030/11min/sparql")


# Create your views here.

def show (request):
    return render(request, 'stations/home.html')
def Query (request):
    return render(request, 'stations/query2.html')
def query(request):
    # list =[0,232,45,123,4,553434,54,23,9]
    tester =[0,232,45]
    testerb =['wewew','wewew','ewewe']
    template=loader.get_template( 'stations/query.html')

    my_dictionary = {'Name': 'Zara', 'Age': 7, 'Class': 'First'}
    context ={
        "test" : "TEXT",
        # "list": list,
        "testerb":  ['wewew', 'wewew', 'ewewe'],
        "weather":  [5, 5, 6],
        "name":"Alex",
        "surname":"Jazun",
        "coords": {
            "x": "x coords",
            "y": "y coords",

        },
        # 'list': [1,2,3,4]
    }
    return HttpResponse (template.render(context,request))



def search_engine(request):
    if request.method =="GET":

        if 'q' in request.GET:
            return HttpResponse( '%s'% request.GET['q'])
        else:
            return HttpResponse("You send blank form %s ")

#
#
# def search(request):
#     sparql.setQuery("""
#         PREFIX vcard: <http://www.w3.org/2006/vcard/ns#>
#         PREFIX  sn: <http://www.snee.com/hr/>
#         SELECT ?person
#         WHERE
#     {
#        ?person vcard:family-name "Jones" .
#     }
#     """)
#     sparql.setReturnFormat(JSON)
#     results = sparql.query().convert()
#
#     for result in results["results"]["bindings"]:
#         print(result["person"]["value"])
def test_view (request):
    return HttpResponse('Welcom %s ' % request.path)





def queryA (request):


    template = loader.get_template('stations/route.html')
    departure = request.POST['departure']
    arrival = request.POST['arrival']
    arrivaldate = request.POST['arrivaldate']
    departuredate = request.POST['departuredate']
    some_file = open("some.txt", "w")
    some_file.write("departure" + departure + "\n")
    some_file.write("arrival" + arrival)
    some_file.close()


    query = """



PREFIX ex:    <http://example.com/stations/> 
PREFIX gtfs:  <http://vocab.gtfs.org/terms#> 
PREFIX foaf: <http://xmlns.com/foaf/0.1/> 
PREFIX dct: <http://purl.org/dc/terms/> 
PREFIX geo:   <http://www.w3.org/2003/01/geo/wgs84_pos#>
PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX xsd:   <http://www.w3.org/2001/XMLSchema#> 
PREFIX schema: <http://schema.org/>
PREFIX dbo: <http://dbpedia.org/ontology/>
PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
PREFIX bif: <http://www.openlinksw.com/schemas/bif#>

SELECT DISTINCT  ?longName ?train_number ?depTime ?arrTime ?long?lat

WHERE 

{
  ?stop_id	foaf:name"""      + "\"" + departure + "\"" + """  @fr.
  
  [ a 				gtfs:StopTime ;
    gtfs:departureTime	?depTime;
    gtfs:stop			?stop_id;
  gtfs:trip				?trip_id;
  gtfs:stopSequence		?stop_seq
    ] . 
  ?trip_id	gtfs:route	?route_id;
           gtfs:service	?service_id;
           gtfs:headsign	?train_number;
           gtfs:direction	?direction.
  
  ?route_id gtfs:longName	?longName.
  ?service_id	  a         gtfs:CalendarRule;
                gtfs:thursday     true ;
                schema:endDate    ?end_date ;
                 schema:startDate  ?start_date.

  {
  	[ a 					gtfs:StopTime ;
         gtfs:arrivalTime	?arrTime;
  	gtfs:trip				?trip_id;
  	gtfs:stopSequence		?stop_seq_end;
  	gtfs:stop				?stop_id_end;
    ] . 
  ?stop_id_end	foaf:name """   + "\"" + arrival + "\"" + """  @fr;
                geo:long ?long;
                geo:lat ?lat.
  }
  FILTER (?stop_seq_end > ?stop_seq)
  FILTER ( """+ "\"" + departuredate + "\"" + """^^xsd:date >= ?start_date && """+ "\"" + arrivaldate + "\"" + """^^xsd:date <= ?end_date)
}
  ORDER BY ASC (?depTime)
"""


    sparql.setQuery(query)
    sparql.setReturnFormat(JSON)
    results = sparql.query().convert()



    res =[]

    deptime=[]
    arrtime=[]
    long=''
    lat=[]

    print(results)
    for i in results["results"]["bindings"]:
          res.append((i["longName"]["value"]))
    for i in results["results"]["bindings"]:
         deptime.append((i["depTime"]["value"]))
    for i in results["results"]["bindings"]:
          arrtime.append((i["arrTime"]["value"]))
    # for i in results["results"]["bindings"]:
    #     long = str(i["long"]["value"])
    # for i in results["results"]["bindings"]:
    #     lat = str(i["lat"]["value"])
    print(deptime)
    print(arrtime)
    print(res)
    # print(long)
    # print(lat)
    # coor="\"" + long + ", " + lat + "\""
    # print(coor)
    #
    # geolocator = Nominatim()
    # location = geolocator.reverse(coor)
    # city = location.raw['address']['city']
    # print(city)
    # api_address = 'http://api.openweathermap.org/data/2.5/weather?appid=0c42f7f6b53b244c78a418f4f181282a&q='
    # url = api_address + city
    # print(url)
    # json_data = requests.get(url).json()
    # format_add = json_data['main']['temp']
    # print(format_add)




    context = {
        "tosend":res,
        "dept":deptime,
         "arrt":arrtime
    }



    return HttpResponse(template.render(context, request))


#


# Enter headsign train number and date
# Information about the train
def queryB (request):
    template = loader.get_template('stations/route2.html')
    headsign = request.POST['headsign']
    # arrival = request.POST['arrival']
    arrivaldate2 = request.POST['arrivaldate2']
    departuredate2= request.POST['departuredate2']

    query2 ="""
PREFIX
ex: < http: // example.com / stations / >
PREFIX
gtfs: < http: // vocab.gtfs.org / terms  # >
PREFIX
foaf: < http: // xmlns.com / foaf / 0.1 / >
PREFIX
dct: < http: // purl.org / dc / terms / >
PREFIX
geo: < http: // www.w3.org / 2003 / 01 / geo / wgs84_pos  # >
PREFIX
rdf: < http: // www.w3.org / 1999 / 02 / 22 - rdf - syntax - ns  # >
PREFIX
xsd: < http: // www.w3.org / 2001 / XMLSchema  # >
PREFIX
schema: < http: // schema.org / >
PREFIX
dbo: < http: // dbpedia.org / ontology / >
PREFIX
rdfs: < http: // www.w3.org / 2000 / 01 / rdf - schema  # >
PREFIX
bif: < http: // www.openlinksw.com / schemas / bif  # >

SELECT ?longName ?stop_name ?arrTime ?depTime

WHERE

{
?trip_id
gtfs: headsign """ + "\"" + headsign + "\"" + """;
gtfs: route    ?route_id;
gtfs: direction    ?direction;
gtfs: service    ?service_id.

[a
gtfs: StopTime;
gtfs: arrivalTime    ?arrTime;
gtfs: departureTime    ?depTime;
gtfs: stop            ?stop_id;
gtfs: trip                ?trip_id;
gtfs: stopSequence        ?stop_seq
].

?route_id
gtfs: longName    ?longName.

?service_id
a
gtfs: CalendarRule;
gtfs: wednesday
true;
schema: endDate    ?end_date;
schema: startDate  ?start_date.
?stop_id
foaf: name     ?stop_name.

    FILTER(""" + "\"" + departuredate2 + "\"" + """ ^ ^ xsd: date >= ?start_date & & """ + "\"" + arrivaldate2 + "\"" + """ ^ ^ xsd: date <= ?end_date )
}
ORDER
BY
ASC(?depTime)
"""
    sparql.setQuery(query2)
    sparql.setReturnFormat(JSON)
    results = sparql.query().convert()

    res = []

    deptime = []
    arrtime = []
    long = ''
    lat = []

    print(results)
    for i in results["results"]["bindings"]:
        res.append((i["longName"]["value"]))
    for i in results["results"]["bindings"]:
        deptime.append((i["depTime"]["value"]))
    for i in results["results"]["bindings"]:
        arrtime.append((i["arrTime"]["value"]))
    # for i in results["results"]["bindings"]:
    #     long = str(i["long"]["value"])
    # for i in results["results"]["bindings"]:
    #     lat = str(i["lat"]["value"])
    print(deptime)
    print(arrtime)
    print(res)
    # print(long)
    # print(lat)
    # coor="\"" + long + ", " + lat + "\""
    # print(coor)
    #
    # geolocator = Nominatim()
    # location = geolocator.reverse(coor)
    # city = location.raw['address']['city']
    # print(city)
    # api_address = 'http://api.openweathermap.org/data/2.5/weather?appid=0c42f7f6b53b244c78a418f4f181282a&q='
    # url = api_address + city
    # print(url)
    # json_data = requests.get(url).json()
    # format_add = json_data['main']['temp']
    # print(format_add)

    context = {
        "tosend": res,
        "dept": deptime,
        "arrt": arrtime
    }

    return HttpResponse(template.render(context, request))


def queryC(request):
    template = loader.get_template('stations/route3.html')
    # headsign = request.POST['headsign']
    # arrival = request.POST['arrival']
    # arrivaldate = request.POST['arrivaldate']
    # departuredate = request.POST['departuredate']

    query3 = """

PREFIX ex:    <http://example.com/stations/> 
PREFIX gtfs:  <http://vocab.gtfs.org/terms#> 
PREFIX foaf: <http://xmlns.com/foaf/0.1/> 
PREFIX dct: <http://purl.org/dc/terms/> 
PREFIX geo:   <http://www.w3.org/2003/01/geo/wgs84_pos#>
PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX xsd:   <http://www.w3.org/2001/XMLSchema#> 
PREFIX schema: <http://schema.org/>
PREFIX dbo: <http://dbpedia.org/ontology/>
PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
PREFIX bif: <http://www.openlinksw.com/schemas/bif#>

SELECT DISTINCT  ?longName ?stop_end_name ?arrTime ?depTime

WHERE 

{
  ?stop_id	foaf:name     "Gare de Wallers"@fr.
  
  [ a 				gtfs:StopTime ;
    gtfs:arrivalTime	?arrTime;
    gtfs:departureTime	?depTime;
    gtfs:stop			?stop_id;
  gtfs:trip				?trip_id;
  gtfs:stopSequence		?stop_seq
    ] . 
  ?trip_id	gtfs:route	?route_id;
           gtfs:service	?service_id;
           gtfs:direction	?direction.
  
  ?route_id gtfs:longName	?longName.
  ?service_id	  a         gtfs:CalendarRule;
                gtfs:wednesday     true ;
                schema:endDate    ?end_date ;
                 schema:startDate  ?start_date.

  {
  	[ a 					gtfs:StopTime ;
  	gtfs:trip				?trip_id;
  	gtfs:stopSequence		?stop_seq_end;
  	gtfs:stop				?stop_id_end;
    ] . 
  ?stop_id_end	foaf:name     ?stop_end_name.
  }
  FILTER (?stop_seq_end > ?stop_seq)
  FILTER ( "2019-01-23"^^xsd:date >= ?start_date && "2019-01-23"^^xsd:date <= ?end_date )
}
  ORDER BY ASC (?depTime)
"""
    sparql.setQuery(query3)
    sparql.setReturnFormat(JSON)
    results = sparql.query().convert()

    res = []

    deptime = []
    arrtime = []
    long = ''
    lat = []

    print(results)
    for i in results["results"]["bindings"]:
        res.append((i["longName"]["value"]))
    for i in results["results"]["bindings"]:
        deptime.append((i["depTime"]["value"]))
    for i in results["results"]["bindings"]:
        arrtime.append((i["arrTime"]["value"]))
    # for i in results["results"]["bindings"]:
    #     long = str(i["long"]["value"])
    # for i in results["results"]["bindings"]:
    #     lat = str(i["lat"]["value"])
    print(deptime)
    print(arrtime)
    print(res)
    # print(long)
    # print(lat)
    # coor="\"" + long + ", " + lat + "\""
    # print(coor)
    #
    # geolocator = Nominatim()
    # location = geolocator.reverse(coor)
    # city = location.raw['address']['city']
    # print(city)
    # api_address = 'http://api.openweathermap.org/data/2.5/weather?appid=0c42f7f6b53b244c78a418f4f181282a&q='
    # url = api_address + city
    # print(url)
    # json_data = requests.get(url).json()
    # format_add = json_data['main']['temp']
    # print(format_add)

    context = {
        "tosend": res,
        "dept": deptime,
        "arrt": arrtime
    }

    return HttpResponse(template.render(context, request))
