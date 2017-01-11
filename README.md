# photogps

Java program that will let you pick a jpg image and return the city and country where the photo was taken

1) It will read the GPS location from the exif.

2) It will then make a call to Google api http://maps.googleapis.com/maps/api/geocode/json?latlng=

3) Parse the JSON object to extract the city and country.

4) Display the location, example: The photo /Users/mick/Downloads/FullSizeRender (1).jpg was taken in Cuzco,Peru

5) It will also open  google maps on your browser with the adress.
