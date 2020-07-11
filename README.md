# Android-GPX-Parser
A GPX Parser for Android

# Install from Maven
```
implementation 'net.federicomatera.agpxp:agpxp:1.0.0'
```

# Instance
```
val gpx = Gpx(
    version = "1",
    creator = "John Doe",
    tracks = Track(
        name = "myGpx",
         trackPoints = listOf(
             WayPoint(
                 latitude = ...,
                 longitude = ...,
                 elevation = ...,
                 time = ...,
                 speed = ...
             ),
             ...
         )
    )
)
```

# Write GPX file
```
val file = File(context.filesDir, "myFile.gpx")
val out = file.outputStream()
gpxWriter.write(gpx, out)
out.close()
```

# Read GPX file
```
val file = File(context.filesDir, "myFile.gpx")
val uri = FileProvider.getUriForFile(context, "your.package.fileprovider.here", file)
val stream = context.contentResolver.openInputStream(uri)
val gpx = gpxParser.parse(stream)
stream.close()
```
