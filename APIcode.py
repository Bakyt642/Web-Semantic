# from SPARQLWrapper import SPARQLWrapper, JSON
#
# sparql = SPARQLWrapper("http://localhost:3030/11min/sparql")
#
# name = "Jones"
# query = """
#     PREFIX vcard: <http://www.w3.org/2006/vcard/ns#>
#     PREFIX  sn: <http://www.snee.com/hr/>
#     SELECT ?person
#     WHERE
# {
#    ?person vcard:family-name """+"\""+name+ "\""+ """  .
# }
# """
#
# sparql.setQuery(query)
# sparql.setReturnFormat(JSON)
# results = sparql.query().convert()
# print(results)
#
# for result in results["results"]["bindings"]:
#     print(result["person"]["value"])
import requests

from geopy.geocoders import Nominatim

geolocator = Nominatim()
location = geolocator.reverse("50.3614404, 3.188341")
city = location.raw['address']
print(city)

# api_address='http://api.openweathermap.org/data/2.5/weather?appid=4a6e23dea90f8f8171ad0c47732c7e1e&q='
api_address='http://api.openweathermap.org/data/2.5/weather?appid=0c42f7f6b53b244c78a418f4f181282a&q='
# city = input('City Name :')
url = api_address + city
print(url)
json_data = requests.get(url).json()
format_add = json_data['main']['temp']
print(format_add)

#

#
# from pygeocoder import Geocoder
#
# location = Geocoder.reverse_geocode(12.9716,77.5946)
# print(location.city)

#
# from geopy.geocoders import Nominatim
#
# geolocator = Nominatim(user_agent='my_app')
#
# with open('input.geojson') as f:
#     geojson = json.load(f)
#
# for i, track in enumerate(geojson['features']):
#     lat, lon = track['geometry']['coordinates'][0]
#     location = geolocator.reverse((lat, lon))
#     # full address as returned by Nominatim
#     address = location.address
#     # city or country
#     city = location.raw['address']['city']
#     country = location.raw['address']['country']
#     # update geojson
#     geojson['features'][i]['properties']['name'] = city
#
# with open('dump.geojson', 'w') as f:
#     json.dump(geojson, f, indent=True)














