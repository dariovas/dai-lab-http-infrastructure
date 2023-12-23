# Commands used to run CRUD operations on the API-Server
***

The tests have been done with curl command or directly through the browser.

A bar contains the following properties :
- name --> String
- city --> String
- capacity --> int
- cocktails list --> ArrayList of String

## Create operations
### POST /api/bars
To create a new bar, you need to specify the name, the city, the capacity and the cocktails list like the example below :
```
curl -X POST -H "Content-Type: application/json" -d '{"name": "Bar 1", "city": "Yverdon", "capacity": 430, "cocktails": ["Hugo", "Spritz"]}' http://localhost/api/bars
```
### POST /api/bars/{id}/cocktails/add
To add a cocktail to a bar, you need to specify its id and the cocktailName entry like the example below :
```
curl -X POST http://localhost/api/bars/1/cocktails/add -d "cocktailName=Spritz"
```

## Read operations
### GET /api/bars
To get all bars, you can access through the browser :
http://localhost/api/bars

Or, simply runs the following command :
```
curl -X GET http://localhost/api/bars
```

### GET /api/bars/{id}
To get a specific bar identified by its id, you can access through the browser :
http://localhost/api/bars/1

Or, simply runs the following command :
```
curl -X GET http://localhost/api/bars/1
```

### GET /api/bars/{id}/cocktails
To get the cocktails list of a specific bar identified by its id, you can access through the browser :
http://localhost/api/bars/1/cocktails/

Or, simply runs the following command :
```
curl -X GET http://localhost/api/bars/1/cocktails
```

## Update operation
### PUT /api/bars/{id}
To update the properties of a specific bar, you need to specify the property(ies) to update like the example below :
```
curl -X PUT -H "Content-Type: application/json" -d '{"name": "The Motel 2", "cocktails": ["Long Island", "Mojito"]}' http://localhost/api/bars/1 
```

It is also possible to update all properties :
```
curl -X PUT -H "Content-Type: application/json" -d '{"name": "Bar 1", "city": "Yverdon", "capacity": 430, "cocktails": ["Hugo", "Spritz"]}' http://localhost/api/bars/1
```

## Delete operations
### DELETE /api/bars/{id}
To delete a specific bar, you need to specify its id like the example below :
```
curl -X DELETE http://localhost/api/bars/1
```

### DELETE /api/bars/{id}/cocktails/del
To delete a cocktail from a bar, you need to specify its id and the cocktailName entry like the example below :
```
curl -X DELETE http://localhost/api/bars/1/cocktails/del -d "cocktailName=Spritz"
```
