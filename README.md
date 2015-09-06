# TrackMeProject

updatelocation is a POST
http://146.148.86.166:8280/mapapi/1.0/location
call update location as {"location":{"lon":"80", "lat":"7"}}
result
<soap:Envelope>
<soap:Body>
<ns3:updateResponse>
<return>Updated lon 7 lat 80</return>
</ns3:updateResponse>
</soap:Body>
</soap:Envelope>

getlocation is a simple get
http://146.148.86.166:8280/mapapi/1.0/getlocation
result is like 
{
Envelope: {
Body: {
viewResponse: {
return: "(80,7)"
}
}
}
}
