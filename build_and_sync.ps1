$javac = "C:\Program Files\Java\jdk-26.0.2\bin\javac.exe"
$m2 = "C:\Users\NEHA KUMARI\.m2\repository"

$cp = "target/classes;" +
      "$m2\org\openjfx\javafx-controls\21.0.2\javafx-controls-21.0.2-win.jar;" +
      "$m2\org\openjfx\javafx-controls\21.0.2\javafx-controls-21.0.2.jar;" +
      "$m2\org\openjfx\javafx-graphics\21.0.2\javafx-graphics-21.0.2-win.jar;" +
      "$m2\org\openjfx\javafx-graphics\21.0.2\javafx-graphics-21.0.2.jar;" +
      "$m2\org\openjfx\javafx-base\21.0.2\javafx-base-21.0.2-win.jar;" +
      "$m2\org\openjfx\javafx-base\21.0.2\javafx-base-21.0.2.jar;" +
      "$m2\org\openjfx\javafx-fxml\21.0.2\javafx-fxml-21.0.2-win.jar;" +
      "$m2\org\openjfx\javafx-fxml\21.0.2\javafx-fxml-21.0.2.jar;" +
      "$m2\com\mysql\mysql-connector-j\8.3.0\mysql-connector-j-8.3.0.jar"

Write-Host "Copying resources to target\classes..."
Copy-Item -Recurse -Force src/main/resources/* target/classes/

Write-Host "Compiling all Java sources..."
$javaFiles = Get-ChildItem -Recurse -Filter *.java src/main/java | Select-Object -ExpandProperty FullName
& $javac -cp $cp -d target/classes $javaFiles

if ($LASTEXITCODE -eq 0) {
    Write-Host "=== Java compilation successful! ==="
} else {
    Write-Host "Java compilation FAILED with code $LASTEXITCODE"
    exit $LASTEXITCODE
}
