# photogps

Java program that will let you pick a jpg image and read the GPS location.
It will then make a call to Google api http://maps.googleapis.com/maps/api/geocode/json?latlng=
Then will parse the JSON object to extract the city and country.
Output for example: The photo /Users/mick/Downloads/FullSizeRender (1).jpg was taken in Cuzco,Peru
It will also open  google maps on your browser with the adress.

