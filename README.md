# spotify-scala
A Scala client for the Spotify desktop API.

## Usage
Clone the repository, publish to your local Ivy repository, and depend on it:
```
libraryDependencies += "me.shadaj" %% "spotify-scala" % "0.1.0-SNAPSHOT"
```

# Troubleshooting
If an exception is thrown about an untrusted SSL certificate, use this [tutorial](http://myshittycode.com/2014/06/05/java-https-unable-to-find-valid-certification-path-to-requested-target) to add the certificate for https://localhost:4370 to your Java installation.
