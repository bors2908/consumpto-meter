# consumpto-meter
Demo project. An example of my understanding of how it should be "by-the-book". 

Allows to register fuel refills from multiple drivers and receive detailed stats about monthly fuel consumption.

Mostly finished. Requires some polish. 

Javadocs and some dao tests are absent due to time constraints.

I didn't really get the part of the task about "fuel consumption reports grouped by month": 
whether it should be monthly reports for each fuel type consumpton or monthly separated lists of anonymous refill, so I've done both.

## Usage

A Program is accessed via simple REST API on port 8099 (configurable) on path "/consumption"


### Add refill
To add new fuel refill data, send a POST request to an **/addRefills** endpoint with JSON-formatted body, that consists of array of fuel refills:

**POST**  *URL*:8099/consumption/addRefills

Request Body:
```
    [
      {
          "fuelType": "D",  // Fuel type: "D", "95" or "98"
          "pricePerLiter": 20.0,  // Price per liter: positive decimal with two digits after point
          "amount": 1.4,  // Fuel amount: positive decimal with two digits after point
          "date": "2022-04-17",  // Refill date: YYYY-MM-DD
          "driverId": 2  // Driver ID: Positive integer
      },
      {
          "fuelType": "98",  
          "pricePerLiter": 25.0,  
          "amount": 1.8,  
          "date": "2022-04-18",  
          "driverId": 3  
      },
    ]
```

If request was successful, app with respond with an array of refill IDs

Response Body:
```
    [1, 2]
```


### Monthly spendings

To get money spendings on fuel by month send a GET request to a **/monthlyAmount** endpoint with an empty body (overall) or with a DriverId (specific driver)

**GET**  *URL*:8099/consumption/monthlyAmount

Request Body:
```
    42  // DriverID: Integer or empty
```

If request was successful, app with respond with a map of monthly spendings.

Response Body:
```
{
    "January 2021": 36.00,
    "April 2022": 56.00
}
```


### Monthly refills grouped

To get a grouped list of anonymous fuel refills, grouped by month send a GET request to a **/monthlyRefills** endpoint with an empty body (overall) or with DriverId (specific driver)

**GET**  *URL*:8099/consumption/monthlyRefills

Request Body:
```
    42  // DriverID: Integer or empty
```

If request was successful, app with respond with a map of anonymous refills grouped by month.

Response Body:
```
{
    "January 2021": [
        {
            "fuelType": "95",
            "amount": 1.80,
            "pricePerLiter": 20.00,
            "totalPrice": 36.00
        }
    ],
    "April 2022": [
        {
            "fuelType": "95",
            "amount": 1.40,
            "pricePerLiter": 20.00,
            "totalPrice": 28.00
        },
        {
            "fuelType": "95",
            "amount": 1.40,
            "pricePerLiter": 20.00,
            "totalPrice": 28.00
        }
    ]
}
```


### Monthly refill statistics by fuel type

To get monthly reports of refills for each fuel type a GET request to **/monthlyStats** endpoint with an empty body (overall) or with DriverId (specific driver)

**GET**  *URL*:8099/consumption/monthlyStats

Request Body:
```
    42  // DriverID: Integer or empty
```

If request was successful, app with respond with a map of monthly reports of refills for each fuel type 

Response Body:
```
{
    "January 2021": [
        {
            "fuelType": "95",
            "amount": 1.80,
            "avgPricePerLiter": 20.00,
            "totalPrice": 36.00
        }
    ],
    "April 2022": [
        {
            "fuelType": "95",
            "amount": 2.80,
            "avgPricePerLiter": 20.00,
            "totalPrice": 56.00
        }
    ]
}
```
